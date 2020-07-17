package uk.gov.gsi.justice.spg.test.system;

import org.junit.Test;
import uk.gov.gsi.justice.spg.audit.SPGLogFields;
import uk.gov.gsi.justice.spg.audit.SPGLogTypes;
import uk.gov.gsi.justice.spg.healthcheck.elk.ElkSearchItem;
import uk.gov.gsi.justice.spg.test.core.ConstantTestVar;
import uk.gov.gsi.justice.spg.test.core.RemoteSPGBaseTestPerformance;
import uk.gov.gsi.justice.spg.test.core.elk.ElkSearchClient;
import uk.gov.gsi.justice.spg.test.core.elk.helper.OtherHelper;
import uk.gov.gsi.justice.spg.test.util.helper.CamelRouteHelper;
import uk.gov.gsi.justice.spg.test.util.helper.JMSConnectionHelper;
import uk.gov.gsi.justice.spg.test.util.helper.MessagesFilesHelper;
import uk.gov.gsi.justice.spg.test.util.helper.SignatureHelper;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static uk.gov.gsi.justice.spg.test.core.ConstantTestVar.*;

public class Test_Performance_Using_Threads extends RemoteSPGBaseTestPerformance {

    /*

    Move to performance tests group

     */

    private final static int maximumSecondsToWait = 3;
    private final static int updatedSeconds = 20;
    private static long defaultTestTime=180l;

    List<String> monitoredQueueList = new ArrayList<>(
			Arrays.asList("fromNdelius", "outbound.crc.P01", "outbound.crc.P02", "crc-queue-awaiting-response-C01", "crc-queue-awaiting-response-C12"));

    
    @Test
    public void multiTimeBasedPerformanceTest() throws Exception
    {

        LocalDateTime startDateTime = LocalDateTime.now();
        long runMinutes = getTestTime() * 60;
        LinkedList<String> memoryList = new LinkedList<>();

        while(Duration.between(startDateTime, LocalDateTime.now()).getSeconds() < runMinutes)
        {
            sendMultipleMessagesAsJms();
            memoryList.addFirst(getPerformanceUpdate());
            System.out.println("Memory usage history: " + memoryList);
        }

    }



    public void sendMultipleMessagesAsJms() throws IOException, InterruptedException, ExecutionException {
        long startTime = System.currentTimeMillis();
        ThreadMessageCount threadMessageCount = new ThreadMessageCount(9);
        int threadCount = threadMessageCount.getThreadCount();
        int filesPerThread = threadMessageCount.getFilesPerThread();
        int modFiles = threadMessageCount.getModFiles();

        int soapReponseDelay = getConfigValueWithDefault(500, "camel.threads.test.soapReponseDelay");


        Thread.sleep(5000L);

        // SEND THE MESSAGES
        // Setup the threadpool and Start invoking the threads
        List<Future<List<String>>> futuresResultList = new ArrayList<>(threadCount); // Store the results of the multiple threads
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        for(int count = 0; count < threadCount; count++) {
            // For the final thread add in the remaining files
            if((threadCount - count) == 1) {
                filesPerThread += modFiles;
            }
            futuresResultList.add(executorService.submit(new SendJmsMessage(filesPerThread, soapReponseDelay)));
        }
        executorService.shutdown();

        // COLLECT THE MESSAGES
        // Now wait for the threads to complete - Update status periodically but set a maximum timeout
        List<String> sentMessagesList = new ArrayList<>();
        int secondsWaited = 0;
        while(secondsWaited < maximumSecondsToWait) {
            try {
                if (executorService.awaitTermination(updatedSeconds, TimeUnit.SECONDS)) {
                    for(Future<List<String>> fut : futuresResultList) {
                        sentMessagesList.addAll(fut.get());
                    }
                    break;
                } else {
                    secondsWaited += updatedSeconds;
                }
            } catch (InterruptedException e) {
                throw e;
            } catch(ExecutionException e) {
                throw e;
            }
        }
        try {
            result(startTime, secondsWaited, sentMessagesList);
        }
        catch(AssertionError a)
        {
        }
    }


    
  public void result(long startTime, int secondsWaited, List<String> sentMessagesList){
    	    
        Long stopTime = null;
        Long testDuration = null;
    	
        if(secondsWaited >= maximumSecondsToWait) {
            fail("Maximum timeout reached with threads still processing files");
        } else {
            if (sentMessagesList.isEmpty()) {
                fail("sentMessagesList is Empty");
            }else{
          
            	CamelRouteHelper.setLogToScreen(false);
                CamelRouteHelper.setMpxHost(getMpxEndpoint());


                try {
					CamelRouteHelper.waitUntilQueuesEmpty(monitoredQueueList, 60000*20L);
					 // At this point the inbound and outbound message activity has ceased and we can measure this point as the test end time

	                stopTime = System.currentTimeMillis();

	                testDuration = stopTime - startTime;
				} catch (Exception e) {
					log(e.getMessage());
				}
                
                // Now check the log to see if all messages have a successful response
                List<ElkSearchItem> result = getMessageResults(sentMessagesList);
                
                result.stream()
                        .map(i->i.getAsString(SPGLogFields.INTERCHANGE_TIMESTAMP.toString()) +" <"+ i.getAsString(SPGLogFields.SENDER_CONTROL_REF.toString())+">")
                        .sorted()
                        .forEach(ConstantTestVar::log);

                List<String> resultList = result.stream().map(i -> i.getAsString(SPGLogFields.SENDER_CONTROL_REF.toString())).distinct().sorted().collect(Collectors.toList());
                Collections.sort(sentMessagesList);


                sentMessagesList.removeAll(resultList);
                assertEquals("Items left", new ArrayList(), resultList);
                addInterchangeELKExpectation(OtherHelper.SKIP());
                 
            }
        }
    }

    
    //////////////////////////////////////////////////////////////////
    ////    ELK HELP CODE
    //////////////////////////////////////////////////////////////////
    public List<ElkSearchItem> getMessageResults(List<String> messageList) {
    	
        sleepInSec(5); // small pause to allow the log processing to complete before ELK search begins

        String elkAssertHost = configData.getProperty("elk.assert.host");
        int elkAssertPort = configData.getIntProperty("elk.assert.port");

        List<ElkSearchItem> messageSetDeliveryTimes = new ArrayList<>();
        for (String ref : messageList){
            List<ElkSearchItem> list = new ElkSearchClient(ref).query(configData,elkAssertHost,elkAssertPort,0)
                    .stream()
                    .filter(item -> item
                            .getAsString(SPGLogFields.SPG_LOG_TYPE.toString())
                            .equals(SPGLogTypes.PROXY_OUTBOUND_SUCCESS.toString())
                    )
                    .filter(item -> item
                            .getAsString(SPGLogFields.SENDER_CONTROL_REF.toString())
                            .equals(ref)
                    )
                    .collect(
                            Collectors.toList()
                    );

            if(list.isEmpty()){}
            messageSetDeliveryTimes.addAll(list);
        }
        return messageSetDeliveryTimes;
    }


    //////////////////////////////////////////////////////////////////
    ////    Thread Class
    //////////////////////////////////////////////////////////////////
    private class ThreadMessageCount{
        private int threadCount;
        private int filesPerThread;
        private int modFiles;

        public ThreadMessageCount(int maxThreads){

            // Extract and validate the numeric values from the configuration file
            int messageCount = getConfigValueWithDefault(10, "performance.threads.test.messageCount");
            threadCount = getConfigValueWithDefault(2, "performance.threads.test.threadCount");

            threadCount = (threadCount > maxThreads) ? maxThreads : threadCount;

            threadCount = (messageCount < threadCount) ? messageCount : threadCount;

            filesPerThread = messageCount / threadCount; // Handle any remaining messages later (using mod)
            modFiles = messageCount % threadCount;

            // Audit (and then validate) the configuration properties
        }

        public int getThreadCount() {
            return threadCount;
        }

        public int getFilesPerThread() {
            return filesPerThread;
        }

        public int getModFiles() {
            return modFiles;
        }
    }

    //////////////////////////////////////////////////////////////////
    ////    INJECTSTUB CLASS
    //////////////////////////////////////////////////////////////////
    private class SendJmsMessage implements Callable<List<String>> {

        private Integer maxMessagesToSend = 0;
        private Integer messagesSent = 0;
        private Integer soapDelay = 500;
        private String threadName;
        private final List<String> CRCTargets = Arrays.asList("C01", "C12"); // a list of crc targets to be used in testing

        public SendJmsMessage(Integer numMessages, Integer soapResponseDelay) {
            maxMessagesToSend = numMessages;
            soapDelay = soapResponseDelay;
        }

        @SuppressWarnings("static-access")
        @Override
        public List<String> call() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, SignatureException, InvalidKeyException, JMSException {
            List<String> sentMessagesList = new ArrayList<>();

            // TODO - PC Dec 12 2015: will this work across clustered machines??
            // Use the trailing numeric part of the default thread name to generate a prefix for the senderControlref to keep it unique between threads
            // Just convert the number (0-9) to its ascii character equivalent

            Connection connection = null;
            try {
                connection = JMSConnectionHelper.getCamelTestJMSConnection(configData);
                connection.start();

                threadName = Thread.currentThread().getName();
                char asciiValue = (char) (Integer.parseInt(threadName.substring( (threadName.lastIndexOf('-')) + 1))+48);
                while(messagesSent < maxMessagesToSend) {
                    long senderControlRef = generateSenderControlRef();

                    String crcTarget = CRCTargets.get(messagesSent % 2);

                    Map<String,String> messageHeaders = new HashMap<String,String>(){{
                        put("id", String.valueOf(senderControlRef));
                        put("senderControlRef", "123456789");
                    }};

                    // if crc target is C13 then add a delay value to the message header/properties.
                    // this value is picked up in the route 'CRCStubInbound' and shall delay for that amount of time (mS)
                    // set this value above 30000mS and you will see timeout exceptions in the logs
                    if (crcTarget.equals("C13")) {
                    	messageHeaders.put("SOLIRIUSCRCSoapResponseDelay", soapDelay.toString()); // this value is picked up in the route 'CRCStubInbound' and shall delay for that amount of time (mS)
                    }


                    // Take the template xml file and create a new temporary version with the placeholder updated with the runtime dynamic value

                    String message = MessagesFilesHelper.generateTestConnectionNotificationMessageFromDelius(senderControlRef,crcTarget,202);
                    //String message = MessagesFilesHelper.generateComlplexReferenceDataBroadcastLatest(senderControlRef, "N00", crcTarget, assertor, configData); Used to test SPG-17628

                    // Check value of "remote.delius.brokerURL"
                    // The remote connection is only made if the "local.delius.brokerURL" is closed (i.e. make sure local servicemix is not running!)

                    SignatureHelper.signMessageHeadersWithDeliusStubCredentials(configData, message, messageHeaders);
                    JMSConnectionHelper.sendMessageToQueueOnOpenConnection(connection, message, messageHeaders, FROM_DELIUS_INBOUND_QUEUE);
                    sentMessagesList.add(String.valueOf(senderControlRef));
                    messagesSent++;
                }

            } catch (JMSException ex) {
                logError("JMS Exception <" + ex + ">");
                return Collections.emptyList();
            }finally {
                if(connection != null){connection.close();}
            }
            return sentMessagesList;
        }

    }

    public synchronized long generateSenderControlRef() {
        return new Date().getTime();
    }

    public long getTestTime()
    {

        String testTime = System.getProperty("testTime");
        if(testTime==null)
            return defaultTestTime;
        else
            return Long.parseLong(testTime);
    }


    public String getPerformanceUpdate() throws Exception {

        MBeanServerConnection mBeanConnection = CamelRouteHelper.getServerConnection();
        System.out.println("Calling Garbage Collector");
        mBeanConnection.invoke(new ObjectName("java.lang:type=Memory"), "gc", null, null);
        System.out.println("Getting memory snapshots and CPU");
        Object memoryMbean = mBeanConnection.getAttribute(new ObjectName("java.lang:type=Memory"), "HeapMemoryUsage");
        CompositeData cd = (CompositeData) memoryMbean;
        String usedMemory = cd.get("used").toString();
        System.out.println("Used memory: " + " " + usedMemory); //print memory usage
        return usedMemory;
    }

}

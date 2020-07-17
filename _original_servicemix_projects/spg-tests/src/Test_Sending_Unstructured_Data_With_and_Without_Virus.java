package uk.gov.gsi.justice.spg.test.system;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Shell;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.PKCS8KeyFile;
import net.schmizz.sshj.userauth.keyprovider.PuTTYKeyFile;
import net.schmizz.sshj.userauth.method.AuthPublickey;
import net.sf.expectit.Expect;
import net.sf.expectit.ExpectBuilder;
import net.sf.expectit.Result;
import org.apache.camel.Exchange;
import org.junit.*;
import uk.gov.gsi.justice.spg.test.RemoteSPGBaseTestClassConfig;
import uk.gov.gsi.justice.spg.test.core.RemoteSPGBaseTest;
import uk.gov.gsi.justice.spg.test.core.RemoteSPGBaseTestNoConfig;

import static uk.gov.gsi.justice.spg.test.core.ConstantTestVar.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import static net.sf.expectit.filter.Filters.removeColors;
import static net.sf.expectit.filter.Filters.removeNonPrintable;
import static net.sf.expectit.matcher.Matchers.contains;
import static net.sf.expectit.matcher.Matchers.regexp;
import static uk.gov.gsi.justice.spg.test.core.ConstantTestVar.log;
import static uk.gov.gsi.justice.spg.test.core.elk.helper.OtherHelper.assertUDMessageDelivery;
import static uk.gov.gsi.justice.spg.test.core.elk.helper.OtherHelper.assertUDMessageResponse;
//IN SPREAD SHEET
public class Test_Sending_Unstructured_Data_With_and_Without_Virus extends RemoteSPGBaseTestClassConfig {

    /*

    Merge with TestUnstructuredData

     */
    private static SSHClient ssh;
    private static Session session;
    private static Expect expect;
    private static String jarName;
    private static boolean isDistTestEnvironment;
    private static String CRN;


    public Test_Sending_Unstructured_Data_With_and_Without_Virus()
    {

        try {

            changeServerConfigProperties(mergeLists(
                    getSPGCoreConfigChangeTasks(),
                    getDeliusStubConfigChangeTasks(),
                    getCRCStubConfigChangeTasks(),
                    getSPGProxyConfigChangeTasks(),
                    getSPGProxyUDConfigChangeTasks(new HashMap<String,String>(){{
                        put("spg.unstructured.proxy.clamav.scanForViruses","true");
                        put("display.payload","true");
                    }})
            ));
        }
        catch(Exception e)
        {
            log("Problem changing server settings: " + e.getLocalizedMessage());
            e.printStackTrace();

        }

    }

    @BeforeClass
    public static void checkEnvironmentBeforeTesting() throws IOException {

        String env = configData.getStringProperty("Environment.Type");
        boolean isDistTestEnvironment = env.equals("LOCAL") || env.equals("AWS");
        Assume.assumeTrue("Unstructured Data test will only be run on Solirius AWS environments", isDistTestEnvironment);

        CRN = "X030927" ;

        ssh = new SSHClient();
        ssh.addHostKeyVerifier(new PromiscuousVerifier());
        ssh.connect(configData.getStringProperty("Host.CRC.Server"));

        PKCS8KeyFile keyFile = new PKCS8KeyFile();
        keyFile.init(new File(configData.getStringProperty("ud.test.crc.key")));
        ssh.authPublickey(configData.getStringProperty("ud.test.crc.username"),
                keyFile);


        session = ssh.startSession();
        session.allocateDefaultPTY();
        Shell shell = session.startShell();
        expect = new ExpectBuilder()
                .withOutput(shell.getOutputStream())
                .withInputs(shell.getInputStream(), shell.getErrorStream()).withEchoInput(System.out)
                .withEchoOutput(System.err)
                .withInputFilters(removeColors(), removeNonPrintable())
                .withExceptionOnFailure()
                .build();

        expect.sendLine("cd /opt/spg/servicemix");
        expect.sendLine("ls");
        System.out.println();
        Result result = expect.expect(regexp("(crc-dr-test-).*(\\.jar)"));
        jarName = result.group(0);
    }

    @AfterClass
    public static void tearDownAfterClass() throws IOException {
        if(isDistTestEnvironment){
            expect.close();
            session.close();
            ssh.close();
        }
    }

//IN SPREAD SHEET (.*)
    @Test
    public void testUploadNewVirus() throws IOException {



        String fileName =  "/tmp/eicar.txt";//configData.getStringProperty("eicar.file.path");
        expect.sendLine("echo -n \"X5O\"'!'\"P%@AP[4\\PZX54(P^)7CC)7}\\$EICAR-STANDARD-ANTIVIRUS-TEST-FILE\"'!'\"\\$H+H*\" > " + fileName);
        String command = "java -jar " + jarName + " uploadnew X030927 "+ fileName+" author=test entityType=REFERRAL entityId=1 docType=DOCUMENT";
        expect.sendLine(command).sendLine();
		expect.sendLine(command).expect(contains("HTTP status code : 403"));
        expect.sendLine("rm " + fileName);
		log("Verify Audit...");
  
        String path = "/uploadnew";
        addInterchangeELKExpectation(assertUDMessageDelivery(C01, ALF, path));
        addInterchangeELKExpectation(assertUDMessageResponse(C01, CRN, "403"));
    }

//IN SPREAD SHEET (.*)
    @Test
    public void testUploadNoVirus() throws IOException {


        String fileName =  "/tmp/novirus.txt";
        expect.sendLine("echo -n \"No virus in this file.......\" > " + fileName);
        String command = "java -jar " + jarName + " uploadnew X030927 "+ fileName+" author=test entityType=REFERRAL entityId=1 docType=DOCUMENT";
        expect.sendLine(command).sendLine();
        expect.sendLine(command).expect(contains("HTTP status code : 200"));
        expect.sendLine("rm " + fileName);
        log("Verify Audit...");

        String path = "/uploadnew";
        addInterchangeELKExpectation(assertUDMessageDelivery(C01, ALF, path));
        addInterchangeELKExpectation(assertUDMessageResponse(ALF, CRN, "200"));
    }


    @AfterClass
    public static void extra_sleeps()
    {
        sleepRegular();
    }

}

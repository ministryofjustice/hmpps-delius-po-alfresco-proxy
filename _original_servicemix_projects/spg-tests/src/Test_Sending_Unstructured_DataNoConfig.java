package uk.gov.gsi.justice.spg.test.system;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Shell;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.PKCS8KeyFile;
import net.sf.expectit.Expect;
import net.sf.expectit.ExpectBuilder;
import net.sf.expectit.Result;
import org.junit.*;
import uk.gov.gsi.justice.spg.test.core.RemoteSPGBaseTestNoConfig;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import static net.sf.expectit.filter.Filters.removeColors;
import static net.sf.expectit.filter.Filters.removeNonPrintable;
import static net.sf.expectit.matcher.Matchers.contains;
import static net.sf.expectit.matcher.Matchers.regexp;
import static uk.gov.gsi.justice.spg.test.core.ConstantTestVar.*;
import static uk.gov.gsi.justice.spg.test.core.elk.helper.OtherHelper.assertUDMessageDelivery;
import static uk.gov.gsi.justice.spg.test.core.elk.helper.OtherHelper.assertUDMessageResponse;

//IN SPREADSHEET (TODO COULD BE EXPANDED IN SPREADSHEET and validated all calls are exercised)
//both our stub client and proxy need to be exercised in general this tests both stub and spg
public class Test_Sending_Unstructured_DataNoConfig extends RemoteSPGBaseTestNoConfig {

    /*
        Merge with TestUnstructuredData
     */

    private static SSHClient ssh;
    private static Session session;
    private static Expect expect;
    private static String jarName;
    private static boolean isDistTestEnvironment;
    private static String CRN;

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


    @Ignore
    @Test
    public void testSearch() throws IOException {

        String command = "java -jar " + jarName + " search " + CRN;
        expect.sendLine(command).expect(contains("HTTP response status code : 200"));
        log("Verify Audit...");
        String path = "/search";
        addInterchangeELKExpectation(assertUDMessageDelivery("C14", ALF, path));

    }

    @Ignore("Need to specify newly added file save location in test before re-enabling")
    @Test
    public void testFetch() throws IOException {
        String command = "java -jar " + jarName + " fetch " + CRN;
        expect.sendLine(command).expect(contains("HTTP response status code : 200"));
        log("Verify Audit...");
        String path = "/fetch";
        addInterchangeELKExpectation(assertUDMessageDelivery("C14", ALF, path));

    }

    @Ignore("Need to specify newly added file save location in test before re-enabling")
    @Test
    public void testFetchAndReserve() throws IOException{
        String docId = CRN;
        String command = "java -jar " + jarName + " fetchandreserve " + docId;
        expect.sendLine(command).expect(contains("INFO: HTTP response status code : 200"));
        log("Verify Audit...");
        String path = "/fetchandreserve";
        addInterchangeELKExpectation(assertUDMessageDelivery("C14", ALF, path));

    }

    @Test
    public void testUploadAndRelease() throws IOException{
        String fileName = "/tmp/test_file.txt";
        expect.sendLine("echo \"This is a test file\" > " + fileName);
        String command = "java -jar " + jarName + " uploadandrelease " + CRN + " " + fileName + " author=test";
        expect.sendLine(command).expect(contains("HTTP status code : 200"));
        expect.sendLine("rm " + fileName);
        log("Verify Audit...");
        String path = "/uploadandrelease/"+CRN;
        addInterchangeELKExpectation(assertUDMessageDelivery("C01", ALF, path));
    }

    @Test
    public void testRelease() throws IOException{
        String docId = CRN;
        String command = "java -jar " + jarName + " release " + docId;
        expect.sendLine(command).expect(contains("HTTP status code : 200"));
        log("Verify Audit...");
        String path = "/release/"+docId;
        addInterchangeELKExpectation(assertUDMessageDelivery("C01", ALF, path));
    }

    @Test
    public void testDelete() throws IOException{


        String docId = CRN;
        String command = "java -jar " + jarName + " delete " + docId;
        expect.sendLine(command).expect(contains("HTTP status code : 200"));
        log("Verify Audit...");
        String path = "/delete/"+CRN;
        addInterchangeELKExpectation(assertUDMessageDelivery("C01", ALF, path));
    }

    @Test
    public void testDeleteAll() throws IOException{

        String crnId = CRN;
        String command = "java -jar " + jarName + " deleteall " + crnId;
        expect.sendLine(command).expect(contains("HTTP status code : 200"));
        log("Verify Audit...");
        String path = "/deleteall/"+crnId;
        addInterchangeELKExpectation(assertUDMessageDelivery("C01", ALF, path));
    }

    @Test
    public void testLock() throws IOException{

        String command = "java -jar " + jarName + " lock " + CRN;
        expect.sendLine(command).expect(contains("HTTP status code : 200"));
        log("Verify Audit...");
        String path = "/lock/"+CRN;
        addInterchangeELKExpectation(assertUDMessageDelivery("C01", ALF, path));
    }


}

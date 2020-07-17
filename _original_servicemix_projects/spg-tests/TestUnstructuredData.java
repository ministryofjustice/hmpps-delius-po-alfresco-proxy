package uk.gov.gsi.justice.spg.test.system.unstructured;

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
import org.junit.*;
import uk.gov.gsi.justice.spg.test.core.RemoteSPGBaseTest;
import uk.gov.gsi.justice.spg.test.core.RemoteSPGBaseTestNoConfig;
import uk.gov.gsi.justice.spg.test.core.elk.helper.OtherHelper;

import java.io.File;
import java.io.IOException;

import static net.sf.expectit.filter.Filters.removeColors;
import static net.sf.expectit.filter.Filters.removeNonPrintable;
import static net.sf.expectit.matcher.Matchers.contains;
import static net.sf.expectit.matcher.Matchers.regexp;
import static uk.gov.gsi.justice.spg.test.core.ConstantTestVar.log;
import static uk.gov.gsi.justice.spg.test.core.elk.helper.OtherHelper.assertUDMessageDelivery;
import static uk.gov.gsi.justice.spg.test.util.helper.MessagesFilesHelper.generateSenderControlRef;


public class TestUnstructuredData extends RemoteSPGBaseTestNoConfig {

	/*


	Exercises DR client used by both automation tests, manual tests and PO dist stubs


	 */

	private static SSHClient ssh;
	private static Session session;
	private static Shell shell;
	private static Expect expect;
	private static String jarName;
	private static String senderControlRef;
	private static final String crn = "X030927";


	@BeforeClass
	public static void checkEnvironmentBeforeTesting() throws IOException {

		senderControlRef = String.valueOf(generateSenderControlRef());

		ssh = new SSHClient();
		ssh.addHostKeyVerifier(new PromiscuousVerifier());
		ssh.connect(configData.getStringProperty("Host.CRC.Server"));

		PKCS8KeyFile keyFile = new PKCS8KeyFile();
		keyFile.init(new File(configData.getStringProperty("ud.test.crc.key")));
		ssh.authPublickey(configData.getStringProperty("ud.test.crc.username"),
				keyFile);

		session = ssh.startSession();
		session.allocateDefaultPTY();
		shell = session.startShell();
		expect = new ExpectBuilder()
				.withOutput(shell.getOutputStream())
				.withInputs(shell.getInputStream(), shell.getErrorStream()).withEchoInput(System.out)
				.withEchoOutput(System.err)
				.withInputFilters(removeColors(), removeNonPrintable())
				.withExceptionOnFailure()
				.build();

		expect.sendLine("cd /opt/spg/servicemix");
		expect.sendLine("ls *.jar");
	    Result result = expect.expect(regexp("(crc-dr-test-).*(\\.jar)"));
	    jarName = result.group(0);
	}

	@AfterClass
	public static void tearDownAfterClass() throws IOException {
			expect.close();
			session.close();
			ssh.close();
	}

	@Test
	public void testSearch() throws IOException {
		String command = "java -jar " + jarName + " search " + crn;
		expect.sendLine(command).expect(contains("HTTP status code : 200"));
		addInterchangeELKExpectation(OtherHelper.SKIP());
	}

	@Ignore
	@Test
	public void testFetch() throws IOException {
		String command = "java -jar " + jarName + " fetch " + crn;
		expect.sendLine(command).expect(contains("HTTP status code : 200"));
		addInterchangeELKExpectation(OtherHelper.SKIP());
	}

	@Test
	public void testUploadNew() throws IOException {
		String fileName = "/tmp/test_file.txt";
		expect.sendLine("echo \"This is a test file\" > " + fileName);
		String author = "author=Test";
		String entityType = "entityType=REFERRAL";
		String entityId = "entityId=1";
		String docType = "docType=DOCUMENT";
		String command = "java -jar " + jarName + " uploadnew " + crn + " " + fileName + " " + author + " " + entityType + " " + entityId + " " + docType;
		expect.sendLine(command).expect(contains("HTTP status code : 200"));
		expect.sendLine("rm " + fileName);
		addInterchangeELKExpectation(OtherHelper.SKIP());
	}

	@Ignore("No output seen is the test correct?")
	@Test
	public void testFetchAndReserve() throws IOException{
		String docId = senderControlRef;
		String command = "java -jar " + jarName + " fetchandreserve " + docId;
		expect.sendLine(command).expect(contains("HTTP status code : 200"));
		addInterchangeELKExpectation(OtherHelper.SKIP());
	}

	@Test
	public void testUploadAndRelease() throws IOException{
		String fileName = "/tmp/test_file.txt";
		expect.sendLine("echo \"This is a test file\" > " + fileName);
		String docId = senderControlRef;
		String author = "author=Test";
		String command = "java -jar " + jarName + " uploadandrelease " + docId + " " + fileName + " " + author;
		expect.sendLine(command).expect(contains("HTTP status code : 200"));
		expect.sendLine("rm " + fileName);
		addInterchangeELKExpectation(OtherHelper.SKIP());
	}


	@Test
	public void testRelease() throws IOException{
		String docId = senderControlRef;
		String command = "java -jar " + jarName + " release " + docId;
		expect.sendLine(command).expect(contains("HTTP status code : 200"));
		addInterchangeELKExpectation(OtherHelper.SKIP());
	}

	@Test
	public void testDelete() throws IOException{
		String docId = senderControlRef;
		String command = "java -jar " + jarName + " delete " + docId;
		expect.sendLine(command).expect(contains("HTTP status code : 200"));
		addInterchangeELKExpectation(OtherHelper.SKIP());
	}

	@Test
	public void testDeleteAll() throws IOException{
		String crnId = senderControlRef;
		String command = "java -jar " + jarName + " deleteall " + crnId;
		expect.sendLine(command).expect(contains("HTTP status code : 200"));
		addInterchangeELKExpectation(OtherHelper.SKIP());
	}

	@Test
	public void testLock() throws IOException{
		String docId = senderControlRef;
		String command = "java -jar " + jarName + " lock " + docId;
		expect.sendLine(command).expect(contains("HTTP status code : 200"));
		addInterchangeELKExpectation(OtherHelper.SKIP());
	}

}

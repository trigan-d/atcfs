package atc.fs.client.test;

import java.io.File;

import atc.fs.client.AtcFsClient;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(AuthorizationTests.class);
		suite.addTestSuite(RecordCRUDTests.class);
		suite.addTestSuite(JsonRecordTests.class);
		suite.addTestSuite(FileCRUDTests.class);
		suite.addTestSuite(FileInformationTests.class);
		suite.addTestSuite(RecordFilesTests.class);
		suite.addTestSuite(FileTmpLinksTests.class);
		suite.addTestSuite(TimeoutTests.class);
		// $JUnit-END$
		return suite;
	}

	public static AtcFsClient createClient() {
		return createClient("test_vis", "client junit", "secret1");
	}

	public static AtcFsClient createClient(String visId, String visUser, String visPass) {
		// return new AtcFsClient("http://192.168.120.86:8088/atcfs", visId,
		return new AtcFsClient("http://localhost:8088/atcfs", visId,
				visUser, visPass);
	}

	public static File getFileForUpload() {
		return new File("C:/temp/1+2 gf ����.jpg");
	}

	public static File getBigFileForUpload() {
		return new File("C:/backup/jboss-as-7.1.0.Final.zip");
	}

	public static File getFileForDownload() {
		return new File("C:/temp/downloaded_file.jpg");
	}
}

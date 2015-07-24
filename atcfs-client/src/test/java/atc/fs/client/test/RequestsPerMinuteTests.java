package atc.fs.client.test;

import atc.fs.client.AtcFsClient;
import junit.framework.TestCase;

/**
 * Created by mgtriffid on 05.11.2014.
 */
public class RequestsPerMinuteTests extends TestCase {
	private AtcFsClient client = AllTests.createClient();
	String recordData = "test record data string";

	public void testOneHundredRequests() throws InterruptedException {

		//assuming maxRequestPerMinute is 100
		String recordId = client.createRecord(recordData);

		for (int i = 0; i < 50; i++) {
			client.readRecord(recordId, String.class);
		}
		Thread.sleep(30000);
		for (int i = 0; i < 49; i++) {
			client.readRecord(recordId, String.class);
		}
		Thread.sleep(30000);
		for (int i = 0; i < 50; i++) {
			client.readRecord(recordId, String.class);
		}
	}

	public void testMoreRequests() throws InterruptedException {
		String recordId = client.createRecord(recordData);

		try {
			client.readRecord(recordId, String.class);
			fail();
		} catch(RuntimeException e) {

		}
		Thread.sleep(30000);
		try {
			client.readRecord(recordId, String.class);
		} catch (RuntimeException e) {
			fail();
		}
	}
}

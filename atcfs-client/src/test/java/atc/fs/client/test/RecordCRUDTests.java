package atc.fs.client.test;

import atc.fs.client.AtcFsClient;
import atc.fs.client.AtcFsClientNotFoundException;
import junit.framework.TestCase;

public class RecordCRUDTests extends TestCase {
	private AtcFsClient client = AllTests.createClient();

	public void testCRUDStringRecord() {
		String recordData = "test record data string с кириллицей";

		String recordId = client.createRecord(recordData);

		assertNotNull(recordId);

		String savedData = client.readRecord(recordId, String.class);
		assertEquals(recordData, savedData);

		recordData += " edited";

		client.updateRecord(recordId, recordData);
		savedData = client.readRecord(recordId, String.class);
		assertEquals(recordData, savedData);

		client.deleteRecord(recordId);

		try {
			client.readRecord(recordId, String.class);
			fail("404 exception expected");
		} catch (Exception e) {
			assertTrue(e instanceof AtcFsClientNotFoundException);
		}
	}
}

package atc.fs.client.test;

import atc.fs.client.AtcFsClient;
import junit.framework.TestCase;

public class JsonRecordTests extends TestCase {
	private AtcFsClient client = AllTests.createClient();

	public void testCreateAndReadPojoRecord() {
		RecordSample record = new RecordSample(
				"json record (с блэкджеком и кириллицей)", 12345l, 3, 2, 1);

		String recordId = client.createRecord(record);

		assertNotNull(recordId);

		RecordSample savedRecord = client.readRecord(recordId,
				RecordSample.class);

		assertEquals(record.getName(), savedRecord.getName());
		assertEquals(record.getId(), savedRecord.getId());
		assertEquals(record.getNumbers().length,
				savedRecord.getNumbers().length);
		assertEquals(record.getNumbers()[1], savedRecord.getNumbers()[1]);
	}

	public static class RecordSample {
		private String name;
		private long id;
		private int[] numbers;

		public RecordSample() {
		}

		public RecordSample(String name, long id, int... numbers) {
			this.setName(name);
			this.setId(id);
			this.setNumbers(numbers);
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public int[] getNumbers() {
			return numbers;
		}

		public void setNumbers(int[] numbers) {
			this.numbers = numbers;
		}
	}

}

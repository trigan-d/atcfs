package atc.fs.client.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import atc.fs.client.AtcFsClient;
import atc.fs.common.AtcFileDto;
import junit.framework.TestCase;

public class RecordFilesTests extends TestCase {
	private AtcFsClient client = AllTests.createClient();

	public void testRecordWithFiles() throws FileNotFoundException {
		String recordData = "test record with files";

		String recordId = client.createRecord(recordData);

		assertNotNull(recordId);

		File file1 = AllTests.getFileForUpload();
		String fileId1 = client.createFile(file1.getName(),
				"image/jpeg", new FileInputStream(file1),
				recordId);
		assertNotNull(fileId1);

		ArrayList<AtcFileDto> recordFiles = client.getRecordFiles(recordId);

		assertEquals(1, recordFiles.size());
		assertEquals(fileId1, recordFiles.get(0).getId());
		assertEquals(file1.getName(), recordFiles.get(0).getFileName());
		assertEquals(file1.length(), recordFiles.get(0).getSize());

		File file2 = AllTests.getBigFileForUpload();
		String fileId2 = client.createFile(file2.getName(),
				"application/octet-stream", new FileInputStream(
						file2), recordId);
		assertNotNull(fileId2);

		recordFiles = client.getRecordFiles(recordId);
		assertEquals(2, recordFiles.size());
		if (recordFiles.get(0).getId().equals(fileId1)) {
			assertEquals(file1.length(), recordFiles.get(0).getSize());
			assertEquals(file2.length(), recordFiles.get(1).getSize());
		} else {
			assertEquals(file2.length(), recordFiles.get(0).getSize());
			assertEquals(file1.length(), recordFiles.get(1).getSize());
		}
	}
}

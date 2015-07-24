package atc.fs.client.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import atc.fs.client.AtcFileDtoWithStream;
import atc.fs.client.AtcFsClient;
import atc.fs.client.AtcFsClientNotFoundException;
import junit.framework.TestCase;

public class FileCRUDTests extends TestCase {
	private AtcFsClient client = AllTests.createClient();

	public void testFileCRUD() throws IOException {
		File file = AllTests.getFileForUpload();
		String fileId = client.createFile(file.getName(),
				"image/jpeg", new FileInputStream(file), null);
		assertNotNull(fileId);

		AtcFileDtoWithStream fileDtoWithStream = client
				.getFileDtoWithStream(fileId);
		assertEquals(file.getName(), fileDtoWithStream.getFileName());
		assertEquals(file.length(), fileDtoWithStream.getSize());
		assertEquals("image/jpeg",
				fileDtoWithStream.getContentType());

		// File tmpFile = File.createTempFile("downloaded", ".tmp");
		File tmpFile = AllTests.getFileForDownload();
		IOUtils.copy(fileDtoWithStream.getFileStream(),
				new FileOutputStream(tmpFile));

		assertEquals(file.length(), tmpFile.length());
		// tmpFile.delete();

		client.deleteFile(fileId);

		try {
			client.getFileDtoWithStream(fileId);
			fail("404 exception expected");
		} catch (Exception e) {
			assertTrue(e instanceof AtcFsClientNotFoundException);
		}
	}
}

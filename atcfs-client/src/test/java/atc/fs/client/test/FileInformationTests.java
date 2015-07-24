package atc.fs.client.test;

import atc.fs.client.AtcFsClient;
import atc.fs.client.AtcFsClientNotFoundException;
import atc.fs.client.AtcFsClientSecurityException;
import atc.fs.common.AtcFileDto;
import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by mgtriffid on 20.05.2014.
 */
public class FileInformationTests extends TestCase {
	private AtcFsClient client = AllTests.createClient();

	public void testFileInfo() throws IOException {
		File file = AllTests.getFileForUpload();
		String fileName = file.getName();
		String contentType = "image/jpeg";
		long length = file.length();
		String fileId = client.createFile(fileName,
				contentType, new FileInputStream(file), null);
		assertNotNull(fileId);
		AtcFileDto fileDto = client.getFileDto(fileId);
		assertEquals(fileName, fileDto.getFileName());
		assertEquals(contentType, fileDto.getContentType());
		assertEquals(length, fileDto.getSize());
		assertEquals(fileId, fileDto.getId());
	}

	public void testNotExistingFileInfo() {
		String fileId = UUID.randomUUID().toString();
		try {
			client.getFileDto(fileId);
		} catch (AtcFsClientNotFoundException e) {
			return;
		}
		assertTrue(false);
	}

	public void testGettingFileInfoNotAuthorized() {
		AtcFsClient wrongClient = AllTests.createClient("somewrongvis", "somename", "somesecretkey");
		String fileId = UUID.randomUUID().toString();
		try {
			wrongClient.getFileDto(fileId);
		} catch (AtcFsClientSecurityException e) {
			return;
		}
		assertTrue(false);
	}
}

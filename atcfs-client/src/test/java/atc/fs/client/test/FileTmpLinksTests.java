package atc.fs.client.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import atc.fs.client.AtcFsClient;
import atc.fs.client.AtcFsClientNotFoundException;
import junit.framework.TestCase;

public class FileTmpLinksTests extends TestCase {
	private AtcFsClient client = AllTests.createClient();

	public void testGetTmpLinkForUnexisitingFile() {
		try {
			client.getFileTmpLink("unexisting_file_id");
			fail("404 exception expected");
		} catch (Exception e) {
			assertTrue(e instanceof AtcFsClientNotFoundException);
		}
	}

	public void testFileTmpLink() throws IOException, InterruptedException {
		File file = AllTests.getFileForUpload();
		String fileId = client.createFile(file.getName(),
				"image/jpeg", new FileInputStream(file), null);
		assertNotNull(fileId);

		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		File tmpFile = AllTests.getFileForDownload();

		URI tmpLink = client.getFileTmpLink(fileId);
		assertNotNull(tmpLink);
		InputStream fileStream = httpClient.execute(new HttpGet(tmpLink))
				.getEntity().getContent();
		IOUtils.copy(fileStream, new FileOutputStream(tmpFile));
		assertEquals(file.length(), tmpFile.length());

		tmpFile.delete();
		Thread.sleep(1000);

		// one more access, with filename
		URI tmpLinkWithName = client.getFileTmpLinkWithFilename(fileId,
				file.getName());
		assertNotNull(tmpLinkWithName);
		InputStream fileStream2 = httpClient
				.execute(new HttpGet(tmpLinkWithName)).getEntity().getContent();
		IOUtils.copy(fileStream2, new FileOutputStream(tmpFile));
		assertEquals(file.length(), tmpFile.length());

		// assuming 5sec expiration period for temp file links at server
		Thread.sleep(7000);
		HttpResponse responseExpired = httpClient.execute(new HttpGet(tmpLink));
		assertEquals(404, responseExpired.getStatusLine().getStatusCode());
		
		httpClient.close();
	}
}

package atc.fs.client.test;

import atc.fs.client.AtcFsClient;
import atc.fs.client.AtcFsClientBuilder;
import atc.fs.client.AtcFsClientException;
import junit.framework.TestCase;

/**
 * Created by mgtriffid on 23.04.2014.
 */
public class TimeoutTests extends TestCase {

	public void testConnectTimeouts() {
		long beginMillis = System.currentTimeMillis();
		try {
			new AtcFsClientBuilder("http://8.8.8.8/atcfs", "test_vis", "client junit", "secret1")
					.setConnectionTimeoutSeconds(20).build()
					.createRecord("record for authorization tests");
			fail();
		} catch (AtcFsClientException afce) {
			long endMillis = System.currentTimeMillis();
			assertTrue((endMillis - beginMillis) > 19000);
		}
		beginMillis = System.currentTimeMillis();
		try {
			new AtcFsClient("http://8.8.8.8/atcfs", "test_vis", "client junit", "secret1")
					.createRecord("record for authorization tests");
			fail();
		} catch (AtcFsClientException afce) {
			long endMillis = System.currentTimeMillis();
			assertTrue((endMillis - beginMillis) < 2500);
		}

	}
}

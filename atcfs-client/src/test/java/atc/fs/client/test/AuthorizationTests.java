package atc.fs.client.test;

import atc.fs.client.AtcFsClientSecurityException;
import junit.framework.TestCase;

public class AuthorizationTests extends TestCase {
	public void testAuthorization(){
		assertNotNull(AllTests.createClient("test_vis", "client junit", "secret1").createRecord("record for authorization tests"));
		
		assertNotNull(AllTests.createClient("test_vis", "other client", "secret1").createRecord("record for authorization tests"));

		try {
			AllTests.createClient("test_vis", "client junit", "wrong pass").createRecord("record for authorization tests");
			fail();
		} catch(Throwable t) {
			assertTrue(t instanceof AtcFsClientSecurityException);
		}

		try {
			AllTests.createClient("wrong vis", "client junit", "secret1").createRecord("record for authorization tests");
			fail();
		} catch(Throwable t) {
			assertTrue(t instanceof AtcFsClientSecurityException);
		}
	}
}

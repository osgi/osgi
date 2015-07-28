package org.osgi.test.cases.http.whiteboard.secure.junit;

public class SecureTestCase extends BaseHttpWhiteboardTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		assertNotNull(System.getSecurityManager());
	}

	public void testReplaceMe() throws Exception {
		fail("replace me with real tests; this is just a skeleton project");
	}
}

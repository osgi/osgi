package org.osgi.test.cases.residentialmanagement;

import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class TopLevelTestCase extends DefaultTestBundleControl {

	DmtAdmin dmtAdmin;
	DmtSession session;
	

	protected void setUp() throws Exception {
		super.setUp();
		System.out.println("setting up");
		dmtAdmin = (DmtAdmin) getService(DmtAdmin.class);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		System.out.println("tearing down");
		if (session != null && session.getState() == DmtSession.STATE_OPEN)
			session.close();
		unregisterAllServices();
		ungetAllServices();
	}

	// ensure that a sys-prop for the top-level location is set
	public void testSystemProperty() {
		String parent = System.getProperty("org.osgi.dmt.residential"); 
		assertNotNull( parent );
		assertEquals("must be an absolute path", "./", parent.substring(0, 2));
	}
	
	// test that position of top-level nodes can be configured with framework-property org.osgi.dmt.residential
	
	// test existence of top-level nodes
	
	// test attributes of top-level nodes
	
}

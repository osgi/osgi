package org.osgi.test.cases.dmt.tc4.rfc141;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.test.support.sleep.Sleep;

/**
 * This testcase tests that there is no blocking dependency from
 * ConfigurationAdmin at startup of the ConfigurationAdmin.
 *
 * It corresponds to the bug report No. 1657 at Bugzilla
 * see: https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1657
 * @author steffen
 *
 */
public class TestBug1657_CMDependency extends OSGiTestCase{

	Bundle configAdminBundle;
	Bundle dmtAdminBundle;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System.out.println("setting up");

		// stopping the configuration admin service
		ServiceReference< ? > ref = getContext()
				.getServiceReference(ConfigurationAdmin.class);
		if (ref != null ) {
			configAdminBundle = ref.getBundle();
			configAdminBundle.stop();
			System.out.println( "stopped configuration-admin bundle" );
			Sleep.sleep(200);
		}
		ref = getContext().getServiceReference(DmtAdmin.class);
		if (ref != null ) {
			dmtAdminBundle = ref.getBundle();
			dmtAdminBundle.stop();
			System.out.println( "stopped dmt-admin bundle" );
			Sleep.sleep(200);
		}
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		System.out.println( "tearing down");
	}


	/**
	 * tests existence of specified constants for data formats in org.osgi.service.dmt.DmtData
	 */
	public void testDmtAdminStartWithoutCM() throws Exception {
		dmtAdminBundle.start();
		configAdminBundle.start();
	}

}

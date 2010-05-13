/*
 * $Id$
 * 
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.framework.secure.junit.classloading;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This class contains tests related with the framework class loading policies.
 * 
 * @author left
 * @version $Id$
 */
public class ClassLoadingTests extends DefaultTestBundleControl {
	private PackageAdmin	padmin;
	
	protected void tearDown() {
		if ( padmin == null ) {
			ServiceReference ref = getContext().getServiceReference( PackageAdmin.class.getName());
			if ( ref == null )
				return;
			
			padmin = (PackageAdmin) getContext().getService(ref);
		}
		padmin.refreshPackages(null);
	}

	// Service Registry --------------------------

	/**
	 * Since multiple modules may export permission classes with the same class
	 * name, the framework must make sure that permission checks are performed
	 * using the correct class. The net result is that the framework needs to
	 * look up permissions based on class rather than class name and when it
	 * needs to instantiate a permission it needs to use the class of the
	 * permission being checked to do the instantiation. This is a complication
	 * for framework implementers; bundle programmers are not affected.
	 * 
	 * @spec Bundle.start()
	 * @throws Exception if there is any problem or an assert fails
	 */
	public void testPermissionChecking001() throws Exception {
		Bundle tb1;
		Bundle tb6a;
		Bundle tb6b;
		Bundle tb6c;
		Bundle tb6d;

		tb1 = installBundle("classloading.tb1.jar");
		tb1.start();
		tb6a = installBundle("classloading.tb6a.jar");
		tb6a.start();
		tb6b = installBundle("classloading.tb6b.jar");
		tb6b.start();
		tb6c = installBundle("classloading.tb6c.jar");
		tb6c.start();

		tb6d = installBundle("classloading.tb6d.jar", false);
		try {
			trace("Changing a permission class to avoid authorization checking");
			tb6d.start();
			tb6d.stop();
		}
		catch (IllegalStateException ex) {
			fail(ex.getMessage());
		}
		finally {
			tb6d.uninstall();

			tb6c.stop();
			tb6c.uninstall();
			tb6b.stop();
			tb6b.uninstall();
			tb6a.stop();
			tb6a.uninstall();
			tb1.stop();
			tb1.uninstall();
		}
	}

	// Exporting System Classes --------------------

	/**
	 * Test that in order to be allowed to require a name bundle, the requiring
	 * bundle must have:
	 * 
	 * BundlePermission[ <required bundle symbolic name>, REQUIRE_BUNDLE]
	 * 
	 * Test that when the resolution directive has a value of "mandatory" the
	 * required bundle must be resolved if the requiring module is resolved.
	 * 
	 * @spec Bundle.start()
	 * @throws Exception if any failure occurs or any assert fails
	 */
	public void testRequiredBundle003() throws Exception {
		Bundle tb16b = getContext().installBundle(
				getWebServer() + "classloading.tb16b.jar");
		Bundle tb16d = getContext().installBundle(
				getWebServer() + "classloading.tb16d.jar");
		try {
			tb16d.start();
			tb16d.stop();
			fail("Expecting BundleException");
		}
		catch (BundleException e) {
			// expected
		}
		finally {
			uninstallBundle(tb16d);
			uninstallBundle(tb16b);
		}
	}

}
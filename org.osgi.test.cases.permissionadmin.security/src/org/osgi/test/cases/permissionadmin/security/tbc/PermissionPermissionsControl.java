/*
 * $Id$
 *
 * Copyright (c) OSGi Alliance (2000-2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.permissionadmin.security.tbc;

import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class PermissionPermissionsControl extends DefaultTestBundleControl {
	PermissionAdmin	permissionAdmin;

	public void setUp() throws Exception {
		/* Get the PermissionAdmin service */
		permissionAdmin = (PermissionAdmin) getService(PermissionAdmin.class);
		if ( permissionAdmin == null )
			fail("No Permission Admin");
	}

	public void testSecurity() throws Exception {
		try {
			System.out.println(System.getSecurityManager());
			assertNotNull(System.getSecurityManager());
			permissionAdmin.setDefaultPermissions(null);
			fail("Were able to set default permissions without "
					+ "admin permission");
		}
		catch (SecurityException e) {
			pass("Correctly got SecurityException when setting default "
					+ "permissions without admin permission");
		}
		try {
			permissionAdmin.setPermissions("fake.jar", null);
			fail("Were able to set permissions without " + "admin permission");
		}
		catch (SecurityException e) {
			pass("Correctly got SecurityException when setting "
					+ "permissions without admin permission");
		}
		/*
		 * trace("Will try to write to a file"); File f =
		 * getContext().getDataFile("nicke.txt"); FileOutputStream o = new
		 * FileOutputStream(f); o.write(65); o.write(66); o.write(67);
		 * o.close(); trace("Succeeded writing to the file!"); try {
		 * Thread.sleep(30000); } catch(InterruptedException e) {}
		 */
	}
}

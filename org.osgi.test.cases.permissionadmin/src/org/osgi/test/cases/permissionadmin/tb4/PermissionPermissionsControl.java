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
package org.osgi.test.cases.permissionadmin.tb4;

import junit.framework.Assert;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.permissionadmin.PermissionAdmin;

public class PermissionPermissionsControl extends Assert implements
		BundleActivator {

	public void start(BundleContext context) throws Exception {
		ServiceReference ref = context.getServiceReference(PermissionAdmin.class.getName());
		PermissionAdmin	permissionAdmin = (PermissionAdmin) (ref == null ? null : context.getService(ref));
		assertNotNull("No Permission Admin", permissionAdmin);
		try {
			System.out.println(System.getSecurityManager());
			assertNotNull(System.getSecurityManager());
			permissionAdmin.setDefaultPermissions(null);
			fail("Were able to set default permissions without "
					+ "admin permission");
		}
		catch (SecurityException e) {
			// Do nothing; PASS
		}
		try {
			permissionAdmin.setPermissions("fake.jar", null);
			fail("Were able to set permissions without " + "admin permission");
		}
		catch (SecurityException e) {
			// Do nothing; PASS
		}
		/*
		 * trace("Will try to write to a file"); File f =
		 * getContext().getDataFile("nicke.txt"); FileOutputStream o = new
		 * FileOutputStream(f); o.write(65); o.write(66); o.write(67);
		 * o.close(); trace("Succeeded writing to the file!"); try {
		 * Thread.sleep(30000); } catch(InterruptedException e) {}
		 */
	}

	public void stop(BundleContext context) throws Exception {
		// Do nothing.
	}
}

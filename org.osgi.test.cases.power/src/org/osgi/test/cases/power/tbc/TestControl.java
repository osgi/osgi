/*
 * $Header$
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
package org.osgi.test.cases.power.tbc;

import org.osgi.framework.*;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.power.*;
import org.osgi.test.cases.util.DefaultTestBundleControl;

/**
 * <remove>The TemplateControl controls is downloaded in the target and will control the
 * test run. The description of this test cases should contain the overall
 * execution of the run. This description is usuall quite minimal because the
 * main description is in the TemplateTestCase.</remove>
 * 
 * TODO Add Javadoc comment for this.
 * 
 * @version $Revision$
 */
public class TestControl extends DefaultTestBundleControl {
	ServiceReference ref;
	SystemPower sysPower;
	
	/**
	 * List of test methods used in this test case.
	 */
	static String[]	methods	= new String[] {"testSystemPowerChanges"};

	/**
	 * Returns the list of test methods contained in this test case.
	 * 
	 * @return list of test methods
	 */
	public String[] getMethods() {
		return methods;
	}

	public boolean checkPrerequisites() {
		ref = getContext().getServiceReference(SystemPower.class.getName());
		if (ref == null)
			return false;
		sysPower = (SystemPower) getContext().getService(ref);
		if (sysPower == null)
			return false;
		return true;
	}
		
	/**
	 */
	public void testSystemPowerChanges() {		
		log("Current system power state: " + sysPower.getPowerState());
		
		for (int i=0; i<6; i++) {
			log("Change to power state: " + i);
			try {
				sysPower.setPowerState(i, true);
			}
			catch (PowerException pe) {
				assertException("Exception while changing system power state",
								PowerException.class, pe);
				
			}
			log("Power state after change: " + sysPower.getPowerState());
		}
	}

	/**
	 * System bundle exports system services.
	 * 
	 * Verify that the System bundle exists and exports the
	 * system services: PackageAdmin, PermissionAdmin.
	 * @throws Exception
	 *
	 * @specification			org.osgi.framework
	 * @specificationSection    system.bundle
	 * @specificationVersion	3
	 */
	public void testExporter() throws Exception {
		Bundle tb = installBundle("tb1.jar");
		assertBundle(TBCService.class.getName(), tb );
		
		Bundle system = getContext().getBundle(0);
		assertBundle(PackageAdmin.class.getName(), system);
	}

	/**
	 * Verify that the service with name is exported by the bundle b.
	 * 
	 * @param name		fqn of the service, e.g. com.acme.foo.Foo
	 */
	private void assertBundle(String name, Bundle b) {
		ServiceReference	ref = getContext().getServiceReference(name);
		assertNotNull( name + "  service must be registered ", ref );
		assertEquals("Invalid exporter for " + name, b, ref.getBundle());
	}	
}
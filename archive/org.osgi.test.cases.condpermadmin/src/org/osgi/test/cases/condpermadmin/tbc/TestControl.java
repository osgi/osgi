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
package org.osgi.test.cases.condpermadmin.tbc;

import org.osgi.framework.*;
import org.osgi.service.packageadmin.PackageAdmin;
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
	
	/**
	 * <remove>Prepare for each run. It is important that a test run is properly
	 * initialized and that each case can run standalone. To save a lot
	 * of time in debugging, clean up all possible persistent remains
	 * before the test is run. Clean up is better don in the prepare
	 * because debugging sessions can easily cause the unprepare never
	 * to be called.</remove> 
	 */
	public void prepare() {
		log("#before each run");
	}

	/**
	 * <remove>Prepare for each method. It is important that each method can
	 * be executed independently of each other method. Do not keep
	 * state between methods, if possible. This method can be used
	 * to clean up any possible remaining state.</remove> 
	 */
	public void setState() {
		log("#before each method");
	}

	/**
	 * <remove>Test methods starts with "test" and are automatically
	 * picked up by the base class. The order is in the order of declaration.
	 * (It is possible to control this). Test methods should use the assert methods
	 * to test.</remove>
	 * <remove>The documentation of the test method is the test method
	 * specification. Normal java tile and html rules apply.</remove>
	 * 
	 * TODO Fill in tags
	 * @specification			<remove>Specification</remove>
	 * @interface				<remove>Related interface, e.g. org.osgi.util.measurement</remove>
	 * @specificationVersion	<remove>Version nr of the specification</remove>
	 * @methods					<remove>Related method(s)</remove>
	 */
	public void testA() {
		log("#test a");
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

	
	/**
	 * Clean up after each method. Notice that during debugging
	 * many times the unsetState is never reached.
	 */
	public void unsetState() {
		log("#after each method");
	}

	/**
	 * Clean up after a run. Notice that during debugging
	 * many times the unprepare is never reached.
	 */
	public void unprepare() {
		log("#after each run");
	}
}
/*
 * Copyright (c) OSGi Alliance (2000-2009).
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
package org.osgi.test.cases.permissions.filtered.tbc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;
import java.util.Properties;
import junit.framework.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.service.packageadmin.PackageAdmin;

import org.osgi.test.cases.permissions.filtered.util.*;

/**
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 * 
 *         This class provides TestCases for extended ServicePermission and
 *         extended PackagePermission.
 * 
 */
public class FilteredTestControl extends TestCase {

	private BundleContext context;

	/**
	 * Prior to each test, flag is set to false. If the target exception is
	 * caught, it will be set to true.
	 */
	private boolean exceptionFlag;
	private boolean flagRegisterEvent;
	private boolean flagModifyEvent;
	private boolean flagUnregisterEvent;

	private PermissionAdmin permAdmin;
	private Hashtable /* <String bundleSymbolicName, Bundle bundle> */bundlesTable = new Hashtable();

	private static final String REGISTER_BUNDLE_LOCATION = "bundles/register.jar";
	private static final String REGISTER_MODIFY_BUNDLE_LOCATION = "bundles/registerModify.jar";
	private static final String REGISTER_PLURAL_BUNDLE_LOCATION = "bundles/registerPlural.jar";
	private static final String GET_BUNDLE_LOCATION = "bundles/get.jar";
	private static final String RESET_PERMISSION_BUNDLE_LOCATION = "bundles/resetPermission.jar";
	private static final String EXPORT_BUNDLE_LOCATION = "bundles/exportBundle.jar";
	private static final String IMPORT_BUNDLE_LOCATION = "bundles/importBundle.jar";
	private static final String REGISTER_SERVICEREGISTRATION_BUNDLE_LOCATION = "bundles/registerForServiceRegistration.jar";

	private Bundle registerBundle = null;
	private Bundle registerPluralBundle = null;
	private Bundle getBundle = null;
	private Bundle registerModifyBundle = null;
	private Bundle exportBundle = null;
	private Bundle importBundle = null;
	private Bundle registerForServiceRegistrationTestBundle = null;

	private static final int SLEEP_PERIOD_IN_MSEC = 200;
	private static final String SETPROPERTIES_TEST_KEY = "vender";
	private static final String SETPROPERTIES_TEST_VALUE = "NTT";

	public void setBundleContext(BundleContext context) {
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp() throws Exception {
		this.exceptionFlag = false;
		this.flagModifyEvent = false;
		this.flagRegisterEvent = false;
		this.flagUnregisterEvent = false;
		this.resetBundles();
	}

	private void resetBundles() throws BundleException {
		if (this.registerBundle != null) {
			this.registerBundle.uninstall();
			this.registerBundle = null;
		}
		if (this.registerPluralBundle != null) {
			this.registerPluralBundle.uninstall();
			this.registerPluralBundle = null;
		}
		if (this.getBundle != null) {
			this.getBundle.uninstall();
			this.getBundle = null;
		}
		if (this.registerModifyBundle != null) {
			this.registerModifyBundle.uninstall();
			this.registerModifyBundle = null;
		}
		if (this.exportBundle != null) {
			this.exportBundle.uninstall();
			this.exportBundle = null;
		}
		if (this.importBundle != null) {
			this.importBundle.uninstall();
			this.importBundle = null;
		}
		if (this.registerForServiceRegistrationTestBundle != null) {
			this.registerForServiceRegistrationTestBundle.uninstall();
			this.registerForServiceRegistrationTestBundle = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	public void tearDown() throws Exception {
		this.resetBundles();

		Bundle resetPermBundle = this
				.installBundle(RESET_PERMISSION_BUNDLE_LOCATION);
		resetPermBundle.start();
		resetPermBundle.uninstall();
	}

	/*
	 * ----------------------------------------- Test methods.
	 */

	/**
	 * This test is to confirm behavior of Service Permission when bundleA which
	 * is permitted to register serviceA registers serviceA. The class name
	 * isn't described using "filter", this test is to confirm backward
	 * compatibility.
	 * 
	 * This registration MUST succeed.
	 */

	 public void testServiceRegistration1_1_1_1() throws Exception {
		final String testId = setTestId("1_1_1_1");
		installStartAndCheckSecurityException(testId, REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
	}

	/**
	 * This test is to confirm behavior of Service Permission when bundleA which
	 * is permitted to register serviceB registers serviceA. The class name
	 * isn't described using "filter", this test is to confirm backward
	 * compatibility.
	 * 
	 * This registration MUST fail.
	 */
	public void testServiceRegistration1_1_1_2() throws Exception {
		final String testId = setTestId("1_1_1_2");
		installStartAndCheckSecurityException(testId, REGISTER_BUNDLE_LOCATION);
		assertTrue("Succeed in registering service. It MUST fail.",
				exceptionFlag);
	}

	public void testServiceRegistration1_1_1_3() throws Exception {
		final String testId = setTestId("1_1_1_3");
		installStartAndCheckSecurityException(testId, REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceRegistration1_1_1_4() throws Exception {
		final String testId = setTestId("1_1_1_4");
		installStartAndCheckSecurityException(testId, REGISTER_BUNDLE_LOCATION);
		assertFalse("Succeed in registering service. It MUST fail.",
				exceptionFlag);
	}

	public void testServiceRegistration1_1_3_1() throws Exception {
		final String testId = setTestId("1_1_3_1");
		installStartAndCheckSecurityException(testId, REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceRegistration1_1_3_2() throws Exception {
		final String testId = setTestId("1_1_3_2");
		installStartAndCheckSecurityException(testId, REGISTER_BUNDLE_LOCATION);
		assertTrue("Succeed in registering service. It MUST fail.",
				exceptionFlag);
	}

	public void testServiceRegistration1_1_3_3() throws Exception {
		final String testId = setTestId("1_1_3_3");
		installStartAndCheckSecurityException(testId, REGISTER_BUNDLE_LOCATION);
		assertTrue("Fail to register service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceRegistration1_1_3_4() throws Exception {
		final String testId = setTestId("1_1_3_4");
		installStartAndCheckSecurityException(testId, REGISTER_BUNDLE_LOCATION);
		assertFalse("Succeed in registering service. It MUST fail.",
				exceptionFlag);
	}

	public void testServiceRegistration1_1_4_1() throws Exception {
		final String testId = setTestId("1_1_4_1");
		installStartAndCheckSecurityException(testId, REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceRegistration1_1_4_2() throws Exception {
		final String testId = setTestId("1_1_4_2");
		installStartAndCheckSecurityException(testId, REGISTER_BUNDLE_LOCATION);
		assertTrue("Succeed in registering service. It MUST fail.",
				exceptionFlag);
	}

	public void testServiceRegistration1_1_4_3() throws Exception {
		final String testId = setTestId("1_1_4_3");
		installStartAndCheckSecurityException(testId, REGISTER_BUNDLE_LOCATION);
		assertTrue("Fail to register service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceRegistration1_1_4_4() throws Exception {
		final String testId = setTestId("1_1_4_4");
		installStartAndCheckSecurityException(testId, REGISTER_BUNDLE_LOCATION);
		assertFalse("Succeed in registering service. It MUST fail.",
				exceptionFlag);
	}

	public void testServiceRegistration1_1_5_9() throws Exception {
		final String testId = setTestId("1_1_5_9");
		installStartAndCheckSecurityException(testId, REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceRegistration1_1_5_10() throws Exception {
		final String testId = setTestId("1_1_5_10");
		installStartAndCheckSecurityException(testId, REGISTER_BUNDLE_LOCATION);
		assertTrue("Succeed in registering service. It MUST fail.",
				exceptionFlag);
	}

	public void testServiceRegistration1_1_5_11() throws Exception {
		final String testId = setTestId("1_1_5_11");
		installStartAndCheckSecurityException(testId, REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceRegistration1_1_5_12() throws Exception {
		final String testId = setTestId("1_1_5_12");
		installStartAndCheckSecurityException(testId, REGISTER_BUNDLE_LOCATION);
		assertTrue("Succeed in registering service. It MUST fail.",
				exceptionFlag);
	}

	public void testPluralInterfaceRegister1_2_1_1() throws Exception {
		final String testId = setTestId("1_2_1_1");
		installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
	}

	public void testPluralInterfaceRegister1_2_1_2() throws Exception {
		final String testId = setTestId("1_2_1_2");
		installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		assertTrue("Succeed in registering service. It MUST fail.",
				exceptionFlag);
	}

	public void testPluralInterfaceRegister1_2_1_3() throws Exception {
		final String testId = setTestId("1_2_1_3");
		installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		assertTrue("Succeed in registering service. It MUST fail.",
				exceptionFlag);
	}

	public void testServiceRegisterModify1_3_1_1() throws Exception {
		final String testId = setTestId("1_3_1_1");
		installStartAndCheckSecurityException(testId,
				REGISTER_MODIFY_BUNDLE_LOCATION);
		assertFalse("Fail to register or modify service. It MUST succeed.",
				exceptionFlag);
	}

	public void testServiceRegisterModify1_3_1_2() throws Exception {
		final String testId = setTestId("1_3_1_2");
		installStartAndCheckSecurityException(testId,
				REGISTER_MODIFY_BUNDLE_LOCATION);
		assertTrue(
				"Succeed in registering or modifying service. It MUST fail.",
				exceptionFlag);
	}

	public void testServiceRegisterModify1_3_2_1() throws Exception {
		final String testId = setTestId("1_3_2_1");
		installStartAndCheckSecurityException(testId,
				REGISTER_MODIFY_BUNDLE_LOCATION);
		assertFalse("Fail to register or modify service. It MUST succeed.",
				exceptionFlag);
	}

	public void testServiceRegisterModify1_3_2_2() throws Exception {
		final String testId = setTestId("1_3_2_2");
		installStartAndCheckSecurityException(testId,
				REGISTER_MODIFY_BUNDLE_LOCATION);
		assertTrue(
				"Succeed in registering or modifying service. It MUST fail.",
				exceptionFlag);
	}

	public void testServiceGet2_1_1_1() throws Exception {
		final String testId = setTestId("2_1_1_1");
		registerBundle = installBundle(REGISTER_BUNDLE_LOCATION);
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.prepare(testId);
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	/**
	 * @throws Exception
	 */
	public void testServiceGet2_1_1_2() throws Exception {
		final String testId = setTestId("2_1_1_2");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_1_1_3() throws Exception {
		final String testId = setTestId("2_1_1_3");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_1_1_4() throws Exception {
		final String testId = setTestId("2_1_1_4");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_1_3_1() throws Exception {
		final String testId = setTestId("2_1_3_1");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_1_3_2() throws Exception {
		final String testId = setTestId("2_1_3_2");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_1_3_3() throws Exception {
		final String testId = setTestId("2_1_3_3");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_1_3_4() throws Exception {
		final String testId = setTestId("2_1_3_4");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_2_1_1() throws Exception {
		final String testId = setTestId("2_2_1_1");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_2_1_2() throws Exception {
		final String testId = setTestId("2_2_1_2");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_2_1_3() throws Exception {
		final String testId = setTestId("2_2_1_3");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_3_1_1() throws Exception {
		final String testId = setTestId("2_3_1_1");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_3_1_2() throws Exception {
		final String testId = setTestId("2_3_1_2");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_3_1_3() throws Exception {
		final String testId = setTestId("2_3_1_3");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_3_1_4() throws Exception {
		final String testId = setTestId("2_3_1_4");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_3_4_1() throws Exception {
		final String testId = setTestId("2_3_4_1");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_3_4_2() throws Exception {
		final String testId = setTestId("2_3_4_2");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_3_4_3() throws Exception {
		final String testId = setTestId("2_3_4_3");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_3_4_4() throws Exception {
		final String testId = setTestId("2_3_4_4");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_4_3_9() throws Exception {
		final String testId = setTestId("2_4_3_9");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_4_3_10() throws Exception {
		final String testId = setTestId("2_4_3_10");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_4_3_11() throws Exception {
		final String testId = setTestId("2_4_3_11");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_4_3_12() throws Exception {
		final String testId = setTestId("2_4_3_12");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceEvent3_1_1_1() throws Exception {
		final String testId = setTestId("3_1_1_1");
		ServiceListener sl = addServiceListener();
		this.installStartAndCheckSecurityException(testId,
				REGISTER_MODIFY_BUNDLE_LOCATION);
		assertFalse("Fail to register or modify service. It MUST succeed.",
				exceptionFlag);

		assertTrue("ServiceEvent.REGISTERED has not been delivered.",
				flagRegisterEvent);
		assertTrue("ServiceEvent.MODIFIED has not been delivered.",
				flagModifyEvent);
		this.registerModifyBundle.stop();
		assertTrue("ServiceEvent.UNREGISTERED has not been delivered.",
				flagUnregisterEvent);
		context.removeServiceListener(sl);
	}

	public void testServiceEvent3_1_1_2() throws Exception {
		final String testId = setTestId("3_1_1_2");
		ServiceListener sl = addServiceListener();
		this.installStartAndCheckSecurityException(testId,
				REGISTER_MODIFY_BUNDLE_LOCATION);
		assertFalse("Fail to register or modify service. It MUST succeed.",
				exceptionFlag);

		assertFalse("ServiceEvent.REGISTERED has not been delivered.",
				flagRegisterEvent);
		assertFalse("ServiceEvent.MODIFIED has not been delivered.",
				flagModifyEvent);
		this.registerModifyBundle.stop();
		assertFalse("ServiceEvent.UNREGISTERED has not been delivered.",
				flagUnregisterEvent);
		context.removeServiceListener(sl);
	}

	public void testServiceEvent3_1_1_3() throws Exception {
		final String testId = setTestId("3_1_1_3");
		ServiceListener sl = addServiceListener();
		this.installStartAndCheckSecurityException(testId,
				REGISTER_MODIFY_BUNDLE_LOCATION);
		assertFalse("Fail to register or modify service. It MUST succeed.",
				exceptionFlag);

		assertTrue("ServiceEvent.REGISTERED has not been delivered.",
				flagRegisterEvent);
		assertFalse("ServiceEvent.MODIFIED has not been delivered.",
				flagModifyEvent);
		this.registerModifyBundle.stop();
		assertFalse("ServiceEvent.UNREGISTERED has not been delivered.",
				flagUnregisterEvent);
		context.removeServiceListener(sl);
	}

	private ServiceListener addServiceListener() {
		ServiceListener sl = new ServiceListener() {
			public void serviceChanged(ServiceEvent se) {
				switch (se.getType()) {
				case ServiceEvent.REGISTERED:
					flagRegisterEvent = true;
					break;
				case ServiceEvent.MODIFIED:
					flagModifyEvent = true;
					break;
				case ServiceEvent.UNREGISTERING:
					flagUnregisterEvent = true;
					break;
				default:
					break;
				}
			}
		};
		context.addServiceListener(sl);
		return sl;
	}

	public void testRegisteredService4_1_1_1() throws Exception {
		final String testId = setTestId("4_1_1_1");
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);

		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			fail("Fail to register service under multiple name. It MUST succeed.");
	}

	public void testRegisteredService4_1_1_2() throws Exception {
		final String testId = setTestId("4_1_1_2");
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);

		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			return;
		fail("Succeed in registering service under multiple name. It MUST fail.");
	}

	public void testRegisteredService4_1_2_1() throws Exception {
		final String testId = setTestId("4_1_2_1");
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);

		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			fail("Fail to register service under multiple name. It MUST succeed.");
	}

	public void testRegisteredService4_1_2_2() throws Exception {
		final String testId = setTestId("4_1_2_2");
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);

		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			return;
		fail("Succeed in registering service under multiple name. It MUST fail.");
	}

	public void testRegisteredService4_1_3_1() throws Exception {
		final String testId = setTestId("4_1_3_1");
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);

		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			fail("Fail to register service under multiple name. It MUST succeed.");
	}

	public void testRegisteredService4_1_3_2() throws Exception {
		final String testId = setTestId("4_1_3_2");
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);

		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			return;
		fail("Succeed in registering service under multiple name. It MUST fail.");
	}

	public void testRegisteredService4_1_4_1() throws Exception {
		final String testId = setTestId("4_1_4_1");
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);

		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			fail("Fail to register service under multiple name. It MUST succeed.");
	}

	public void testRegisteredService4_1_4_2() throws Exception {
		final String testId = setTestId("4_1_4_2");
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);

		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			return;
		fail("Succeed in registering service under multiple name. It MUST fail.");
	}

	public void testRegisteredService4_1_5_1() throws Exception {
		final String testId = setTestId("4_1_5_1");
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);

		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			fail("Fail to register service under multiple name. It MUST succeed.");
	}

	public void testRegisteredService4_1_5_2() throws Exception {
		final String testId = setTestId("4_1_5_2");
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			return;
		fail("Succeed in registering service under multiple name. It MUST fail.");
	}

	public void testServiceInUse5_1_1_1() throws Exception {
		final String testId = setTestId("5_1_1_1");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);

		ServiceReference[] ref = getBundle.getServicesInUse();
		if (ref == null || ref.length != 1)
			fail("Fail to getServiceInUse(). It MUST succeed.");
	}

	public void testServiceInUse5_1_1_2() throws Exception {
		final String testId = setTestId("5_1_1_2");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);

		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);

		ServiceReference[] ref = getBundle.getServicesInUse();
		if (ref == null || ref.length == 1)
			return;

		fail("Succeed in getServiceInUse. It MUST fail.");
	}

	public void testServiceInUse5_1_2_1() throws Exception {
		final String testId = setTestId("5_1_2_1");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);

		ServiceReference[] ref = getBundle.getServicesInUse();
		if (ref == null || ref.length != 1)
			fail("Fail to getServiceInUse(). It MUST succeed.");
	}

	public void testServiceInUse5_1_2_2() throws Exception {
		final String testId = setTestId("5_1_2_2");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);

		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);

		ServiceReference[] ref = getBundle.getServicesInUse();
		if (ref == null || ref.length == 1)
			return;

		fail("Succeed in getServiceInUse. It MUST fail.");
	}

	public void testServiceInUse5_1_3_1() throws Exception {
		final String testId = setTestId("5_1_3_1");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);

		ServiceReference[] ref = getBundle.getServicesInUse();
		if (ref == null || ref.length != 1)
			fail("Fail to getServiceInUse(). It MUST succeed.");
	}

	public void testServiceInUse5_1_3_2() throws Exception {
		final String testId = setTestId("5_1_3_2");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);

		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);

		ServiceReference[] ref = getBundle.getServicesInUse();
		if (ref == null || ref.length == 1)
			return;

		fail("Succeed in getServiceInUse. It MUST fail.");
	}

	public void testServiceInUse5_1_4_1() throws Exception {
		final String testId = setTestId("5_1_4_1");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);

		ServiceReference[] ref = getBundle.getServicesInUse();
		if (ref == null || ref.length != 1)
			fail("Fail to getServiceInUse(). It MUST succeed.");
	}

	public void testServiceInUse5_1_4_2() throws Exception {
		final String testId = setTestId("5_1_4_2");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);

		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);

		ServiceReference[] ref = getBundle.getServicesInUse();
		if (ref == null || ref.length == 1)
			return;

		fail("Succeed in getServiceInUse. It MUST fail.");
	}

	public void testServiceInUse5_1_5_1() throws Exception {
		final String testId = setTestId("5_1_5_1");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);

		ServiceReference[] ref = getBundle.getServicesInUse();
		if (ref == null || ref.length != 1)
			fail("Fail to getServiceInUse(). It MUST succeed.");
	}

	public void testServiceInUse5_1_5_2() throws Exception {
		final String testId = setTestId("5_1_5_2");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);

		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);

		ServiceReference[] ref = getBundle.getServicesInUse();
		if (ref == null || ref.length == 1)
			return;

		fail("Succeed in getServiceInUse. It MUST fail.");
	}
	
	public void testSetProperties6_1_1_1() throws Exception {
		final String testId = setTestId("6_1_1_1");
		this.installStartAndCheckSecurityException(testId,
				REGISTER_SERVICEREGISTRATION_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		ServiceReference ref = context
				.getServiceReference(ServiceRegistrationSupplier.class
						.getName());
		if (ref == null)
			fail("Fail to getServiceReference. It MUST succeed.");
		ServiceRegistrationSupplier srObj = (ServiceRegistrationSupplier) context
				.getService(ref);
		try {
			srObj.getServiceRegistration().setProperties(this.propertiesSet());
		} catch (SecurityException se) {
			fail("Fail to setProperties(). It MUST succeed.");
		}
	}

	public void testSetProperties6_1_1_2() throws Exception {
		final String testId = setTestId("6_1_1_2");
		this.installStartAndCheckSecurityException(testId,
				REGISTER_SERVICEREGISTRATION_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		ServiceReference ref = context
				.getServiceReference(ServiceRegistrationSupplier.class
						.getName());
		if (ref == null)
			fail("Fail to getServiceReference. It MUST succeed.");
		ServiceRegistrationSupplier srObj = (ServiceRegistrationSupplier) context
				.getService(ref);
		try {
			srObj.getServiceRegistration().setProperties(this.propertiesSet());
		} catch (SecurityException se) {
			return;
		}
		fail("Succeed in setProperties(). It MUST fail.");
	}

	private Hashtable propertiesSet() {
		Hashtable setProp = new Hashtable();
		setProp.put(SETPROPERTIES_TEST_KEY, SETPROPERTIES_TEST_VALUE);
		return setProp;
	}

	 public void testExportPackage7_1_1_1() throws Exception {
		final String testId = setTestId("7_1_1_1");
		this.packagePermissionTestPrepare(testId);
		// TODO if the PackageAdmin impl is fixed, we can check whether the
		// package is exported or not by PackageAdmin.
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			return;
		fail("Fail to export package. It MUST succeed.");
	}

	public void testExportPackage7_1_1_2() throws Exception {
		final String testId = setTestId("7_1_1_2");
		this.packagePermissionTestPrepare(testId);
		// TODO if the PackageAdmin impl is fixed, we can check whether the
		// package is exported or not by PackageAdmin.
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			fail("Succeed in exporting package. It MUST fail.");
	}

	public void testExportPackage7_1_1_3() throws Exception {
		final String testId = setTestId("7_1_1_3");
		this.packagePermissionTestPrepare(testId);
		// TODO if the PackageAdmin impl is fixed, we can check whether the
		// package is exported or not by PackageAdmin.
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			return;
		fail("Fail to export package. It MUST succeed.");
	}

	public void testExportPackage7_1_1_4() throws Exception {
		final String testId = setTestId("7_1_1_4");
		this.packagePermissionTestPrepare(testId);
		// TODO if the PackageAdmin impl is fixed, we can check whether the
		// package is exported or not by PackageAdmin.
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			return;
		fail("Fail to export package. It MUST succeed.");
	}

	public void testExportPackage7_1_1_5() throws Exception {
		final String testId = setTestId("7_1_1_5");
		this.packagePermissionTestPrepare(testId);
		// TODO if the PackageAdmin impl is fixed, we can check whether the
		// package is exported or not by PackageAdmin.
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			return;
		fail("Fail to export package. It MUST succeed.");
	}

	public void testExportPackage7_1_1_6() throws Exception {
		final String testId = setTestId("7_1_1_6");
		this.packagePermissionTestPrepare(testId);
		// TODO if the PackageAdmin impl is fixed, we can check whether the
		// package is exported or not by PackageAdmin.
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			fail("Succeed in exporting package. It MUST fail.");
	}

	public void testExportPackage7_1_1_7() throws Exception {
		final String testId = setTestId("7_1_1_7");
		this.packagePermissionTestPrepare(testId);
		// TODO if the PackageAdmin impl is fixed, we can check whether the
		// package is exported or not by PackageAdmin.
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			return;
		fail("Fail to export package. It MUST succeed.");
	}

	public void testExportPackage7_1_1_8() throws Exception {
		final String testId = setTestId("7_1_1_8");
		this.packagePermissionTestPrepare(testId);
		// TODO if the PackageAdmin impl is fixed, we can check whether the
		// package is exported or not by PackageAdmin.
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			return;
		fail("Fail to export package. It MUST succeed.");
	}

	public void testExportPackage7_1_1_9() throws Exception {
		final String testId = setTestId("7_1_1_9");
		this.packagePermissionTestPrepare(testId);
		// TODO if the PackageAdmin impl is fixed, we can check whether the
		// package is exported or not by PackageAdmin.
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			return;
		fail("Fail to export package. It MUST succeed.");
	}

	public void testExportPackage7_1_1_10() throws Exception {
		final String testId = setTestId("7_1_1_10");
		this.packagePermissionTestPrepare(testId);
		// TODO if the PackageAdmin impl is fixed, we can check whether the
		// package is exported or not by PackageAdmin.
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			fail("Succeed in exporting package. It MUST fail.");
	}

	public void testExportPackage7_1_1_11() throws Exception {
		final String testId = setTestId("7_1_1_11");
		this.packagePermissionTestPrepare(testId);
		// TODO if the PackageAdmin impl is fixed, we can check whether the
		// package is exported or not by PackageAdmin.
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			return;
		fail("Fail to export package. It MUST succeed.");
	}

	public void testExportPackage7_1_1_12() throws Exception {
		final String testId = setTestId("7_1_1_12");
		this.packagePermissionTestPrepare(testId);
		// TODO if the PackageAdmin impl is fixed, we can check whether the
		// package is exported or not by PackageAdmin.
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			fail("Succeed in exporting package. It MUST fail.");
	}

	public void testExportPackage7_1_2_1() throws Exception {
		final String testId = setTestId("7_1_2_1");
		this.packagePermissionTestPrepare(testId);
		// TODO if the PackageAdmin impl is fixed, we can check whether the
		// package is exported or not by PackageAdmin.
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			return;
		fail("Fail to export package. It MUST succeed.");
	}

	public void testExportPackage7_1_2_2() throws Exception {
		final String testId = setTestId("7_1_2_2");
		this.packagePermissionTestPrepare(testId);
		// TODO if the PackageAdmin impl is fixed, we can check whether the
		// package is exported or not by PackageAdmin.
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			fail("Succeed in exporting package. It MUST fail.");
	}

	public void testImportPackage8_1_1_1() throws Exception {
		final String testId = setTestId("8_1_1_1");
		this.packagePermissionTestPrepare(testId);
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			return;
		fail("Fail to import package. It MUST succeed.");
	}

	public void testImportPackage8_1_1_2() throws Exception {
		final String testId = setTestId("8_1_1_2");
		this.packagePermissionTestPrepare(testId);
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			fail("Succeed in importing package. It MUST fail.");
	}

	public void testImportPackage8_1_1_3() throws Exception {
		final String testId = setTestId("8_1_1_3");
		this.packagePermissionTestPrepare(testId);
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			return;
		fail("Fail to import package. It MUST succeed.");
	}

	public void testImportPackage8_1_1_4() throws Exception {
		final String testId = setTestId("8_1_1_4");
		this.packagePermissionTestPrepare(testId);
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			return;
		fail("Fail to import package. It MUST succeed.");
	}

	public void testImportPackage8_1_1_5() throws Exception {
		final String testId = setTestId("8_1_1_5");
		this.packagePermissionTestPrepare(testId);
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			return;
		fail("Fail to import package. It MUST succeed.");
	}

	public void testImportPackage8_1_1_6() throws Exception {
		final String testId = setTestId("8_1_1_6");
		this.packagePermissionTestPrepare(testId);
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			fail("Succeed in importing package. It MUST fail.");
	}

	public void testImportPackage8_1_1_7() throws Exception {
		final String testId = setTestId("8_1_1_7");
		this.packagePermissionTestPrepare(testId);
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			return;
		fail("Fail to import package. It MUST succeed.");
	}

	public void testImportPackage8_1_1_8() throws Exception {
		final String testId = setTestId("8_1_1_8");
		this.packagePermissionTestPrepare(testId);
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			return;
		fail("Fail to import package. It MUST succeed.");
	}

	public void testImportPackage8_1_1_9() throws Exception {
		final String testId = setTestId("8_1_1_9");
		this.packagePermissionTestPrepare(testId);
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			return;
		fail("Fail to import package. It MUST succeed.");
	}

	public void testImportPackage8_1_1_10() throws Exception {
		final String testId = setTestId("8_1_1_10");
		this.packagePermissionTestPrepare(testId);
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			fail("Succeed in importing package. It MUST fail.");
	}

	public void testImportPackage8_1_1_11() throws Exception {
		final String testId = setTestId("8_1_1_11");
		this.packagePermissionTestPrepare(testId);
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			return;
		fail("Fail to import package. It MUST succeed.");
	}

	public void testImportPackage8_1_1_12() throws Exception {
		final String testId = setTestId("8_1_1_12");
		this.packagePermissionTestPrepare(testId);
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			fail("Succeed in importing package. It MUST fail.");
	}

	public void testImportPackage8_1_2_1() throws Exception {
		final String testId = setTestId("8_1_2_1");
		this.packagePermissionTestPrepare(testId);
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			return;
		fail("Fail to import package. It MUST succeed.");
	}

	public void testImportPackage8_1_2_2() throws Exception {
		final String testId = setTestId("8_1_2_2");
		this.packagePermissionTestPrepare(testId);
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			fail("Succeed in importing package. It MUST fail.");
	}

	public void testImportPackage8_1_2_3() throws Exception {
		final String testId = setTestId("8_1_2_3");
		this.packagePermissionTestPrepare(testId);
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			return;
		fail("Fail to import package. It MUST succeed.");
	}

	public void testImportPackage8_1_2_4() throws Exception {
		final String testId = setTestId("8_1_2_4");
		this.packagePermissionTestPrepare(testId);
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			fail("Succeed in importing package. It MUST fail.");
	}

	public void testImportPackage8_1_2_5() throws Exception {
		final String testId = setTestId("8_1_2_5");
		this.packagePermissionTestPrepare(testId);
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			return;
		fail("Fail to import package. It MUST succeed.");
	}

	public void testImportPackage8_1_2_6() throws Exception {
		final String testId = setTestId("8_1_2_6");
		this.packagePermissionTestPrepare(testId);
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			fail("Succeed in importing package. It MUST fail.");
	}

	public void testImportPackage8_1_3_1() throws Exception {
		final String testId = setTestId("8_1_3_1");
		this.packagePermissionTestPrepare(testId);
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			fail("Succeed in importing package. It MUST fail.");
	}

	public void testImportPackage8_1_3_2() throws Exception {
		final String testId = setTestId("8_1_3_2");
		this.packagePermissionTestPrepare(testId);
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			fail("Succeed in importing package. It MUST fail.");
	}

	public void testImportPackage8_1_3_3() throws Exception {
		final String testId = setTestId("8_1_3_3");
		this.packagePermissionTestPrepare(testId);
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			fail("Succeed in importing package. It MUST fail.");
	}

	public void testImportPackage8_1_3_4() throws Exception {
		final String testId = setTestId("8_1_3_4");
		this.packagePermissionTestPrepare(testId);
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			fail("Succeed in importing package. It MUST fail.");
	}

	public void testImportPackage8_1_4_1() throws Exception {
		final String testId = setTestId("8_1_4_1");
		this.packagePermissionTestPrepare(testId);
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			return;
		fail("Fail to import package. It MUST succeed.");
	}

	public void testImportPackage8_1_4_2() throws Exception {
		final String testId = setTestId("8_1_4_2");
		this.packagePermissionTestPrepare(testId);
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			return;
		fail("Fail to import package. It MUST succeed.");
	}

	public void testImportPackage8_1_4_3() throws Exception {
		final String testId = setTestId("8_1_4_3");
		this.packagePermissionTestPrepare(testId);
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			return;
		fail("Fail to import package. It MUST succeed.");
	}

	public void testImportPackage8_1_4_4() throws Exception {
		final String testId = setTestId("8_1_4_4");
		this.packagePermissionTestPrepare(testId);
		if (exportBundle.getState() == 4 && importBundle.getState() == 4)
			return;
		fail("Fail to import package. It MUST succeed.");
	}

	/*
	 * ----------------------------------------- Utility methods.
	 */

	private void prepare(final String testId) {
		manageBundles();
		setPermissions(testId);
		printPermissions();
	}

	private static void sleep() {
		try {
			Thread.sleep(SLEEP_PERIOD_IN_MSEC);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void setPermissions(String testId) {
		ServiceReference ref = context
				.getServiceReference(PermissionAdmin.class.getName());
		if (ref == null) {
			System.out.println("Fail to get ServiceReference of "
					+ PermissionAdmin.class.getName());
			return;
		}
		permAdmin = (PermissionAdmin) context.getService(ref);

		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("bnd/permission/Test" + testId
					+ ".properties"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		final int count = Integer.parseInt(prop.getProperty("count"));
		for (int i = 0; i < count; i++) {
			String si = Integer.toString(i);
			String sbcount = prop.getProperty(si + ".perm.count");
			int bcount = Integer.parseInt(sbcount);
			PermissionInfo[] pis = new PermissionInfo[bcount];
			String bundleSymbolicName = prop.getProperty(si + ".sname");
			for (int j = 0; j < bcount; j++) {
				String sj = Integer.toString(j);
				String perm = prop.getProperty(si + ".perm." + sj);
				pis[j] = new PermissionInfo(perm);
			}
			if (bundleSymbolicName.equals("default")) {
				permAdmin.setDefaultPermissions(pis);
			} else {
				Bundle bundle = getSpecifiedBundle(bundleSymbolicName);
				permAdmin.setPermissions(bundle.getLocation(), pis);
			}
		}
	}

	private Bundle getSpecifiedBundle(String bundleSymbolicName) {
		Bundle bundle = (Bundle) this.bundlesTable.get(bundleSymbolicName);
		if (bundle == null)
			throw new IllegalStateException("No bundle \"" + bundleSymbolicName
					+ "\" exists.");
		return bundle;
	}

	private void manageBundles() {
		Bundle[] bundles = context.getBundles();
		for (int i = 0; i < bundles.length; i++)
			this.bundlesTable.put(bundles[i].getSymbolicName(), bundles[i]);
	}

	private void printPermissions() {
		PermissionInfo[] pis = permAdmin.getDefaultPermissions();
		if (pis == null) {
			System.out.println("DefaultPermissions[] is not set");
		} else {
			for (int i = 0; i < pis.length; i++)
				System.out.println("DefaultPermissions[" + i + "]="
						+ pis[i].getEncoded());
		}
		String[] locations = permAdmin.getLocations();
		if (locations == null) {
			System.out.println("pa.getLocation() == null");
		} else {
			for (int j = 0; j < locations.length; j++) {
				System.out.println("Permissions of (" + locations[j] + "):");
				pis = permAdmin.getPermissions(locations[j]);
				if (pis == null) {
					System.out.println("Permissions of (" + locations[j]
							+ ") is not set");
				} else {
					for (int i = 0; i < pis.length; i++) {
						System.out.println("\tPermission[" + i + "]="
								+ pis[i].getEncoded());
					}
				}
			}
		}
	}

	/**
	 * Install and start a bundle from the specified location.
	 * <p>
	 * After installation, permissions for each bundles are set. If
	 * BundleException nesting SecurityException is thrown at its start,
	 * exceptionFlag is set to true. Otherwise, the nested exception will be
	 * thrown to the caller of this method.
	 * 
	 * @param testId
	 *            Test ID.
	 * @param location
	 *            location of a bundle to be installed.
	 * @throws IOException
	 * @throws BundleException
	 * @throws Exception
	 */
	private void installStartAndCheckSecurityException(final String testId,
			final String location) throws IOException, BundleException,
			Exception {
		if (location.equals(REGISTER_BUNDLE_LOCATION)) {
			this.registerBundle = installBundle(location);
			this.prepare(testId);
			this.startBundleAndCheckSecurityException(registerBundle);
		} else if (location.equals(REGISTER_PLURAL_BUNDLE_LOCATION)) {
			registerPluralBundle = this.installBundle(location);
			this.prepare(testId);
			this.startBundleAndCheckSecurityException(registerPluralBundle);
		} else if (location.equals(REGISTER_MODIFY_BUNDLE_LOCATION)) {
			this.registerModifyBundle = this.installBundle(location);
			this.prepare(testId);
			this
					.startBundleAndCheckSecurityException(this.registerModifyBundle);
		} else if (location
				.equals(REGISTER_SERVICEREGISTRATION_BUNDLE_LOCATION)) {
			this.registerForServiceRegistrationTestBundle = this
					.installBundle(location);
			prepare(testId);
			this
					.startBundleAndCheckSecurityException(registerForServiceRegistrationTestBundle);
		} else {
			throw new IllegalArgumentException("location is invalid. location="
					+ location);
		}
	}

	private Bundle installBundle(String location) throws IOException,
			BundleException {
		URL url = context.getBundle().getResource(location);
		InputStream is = url.openStream();
		Bundle bundle = context.installBundle(location, is);
		is.close();
		return bundle;
	}

	/**
	 * If specified BundleException has nested exception of SecurityException,
	 * exceptionFlag is set to true. Otherwise, it will be thrown to the caller
	 * of this method.
	 * 
	 * @param be
	 * @throws Exception
	 */
	private void checkIfExIsSecurityException(BundleException be)
			throws Exception {
		Throwable th = be.getNestedException();
		if (th instanceof SecurityException)
			exceptionFlag = true;
		else
			throw (Exception) th;
	}

	/**
	 * If specified BundleException has nested exception of
	 * PermissionsFilterException, exceptionFlag is set to true. Otherwise, it
	 * will be thrown to the caller of this method.
	 * 
	 * @param be
	 * @throws Exception
	 */
	private void checkIfExIsPermissionsFilterException(BundleException be)
			throws Exception {
		Throwable th = be.getNestedException();
		if (th instanceof PermissionsFilterException)
			exceptionFlag = true;
		else
			throw (Exception) th;
	}

	private void startBundleAndCheckSecurityException(Bundle bundle)
			throws Exception {
		try {
			bundle.start();
		} catch (BundleException be) {
			this.checkIfExIsSecurityException(be);
		}
	}

	/**
	 * Start specified bundle.
	 * 
	 * If
	 * 
	 * @param bundle
	 * @throws Exception
	 */
	private void startBundleAndCheckPermissionsFilterException(Bundle bundle)
			throws Exception {
		try {
			bundle.start();
		} catch (BundleException be) {
			this.checkIfExIsPermissionsFilterException(be);
		}
	}

	private String setTestId(String testId) {
		System.out.println("This test is ---> " + testId);
		return testId;
	}

	private void packagePermissionTestPrepare(String testId) throws Exception {
		exportBundle = this.installBundle(EXPORT_BUNDLE_LOCATION);
		importBundle = this.installBundle(IMPORT_BUNDLE_LOCATION);
		this.prepare(testId);
		Bundle[] bundles = new Bundle[] { exportBundle, importBundle };
		PackageAdmin packageAdmin = getPackageAdmin();
		packageAdmin.refreshPackages(bundles);
		sleep();
	}

	private PackageAdmin getPackageAdmin() {
		ServiceReference ref = context.getServiceReference(PackageAdmin.class
				.getName());
		if (ref == null)
			throw new IllegalStateException("Fail to get ServiceReference of "
					+ PackageAdmin.class.getName());

		PackageAdmin packageAdmin = (PackageAdmin) context.getService(ref);
		return packageAdmin;
	}

}

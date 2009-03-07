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

import java.io.IOException;
import java.io.InputStream;
import java.io.FilePermission;
import java.net.URL;
import java.security.AllPermission; //import java.util.Hashtable;
import java.util.Dictionary;
import java.util.Enumeration;

import junit.framework.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServicePermission;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.test.cases.permissions.filtered.util.*;

/**
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 * 
 *         This class provides TestCases for extended ServicePermission and
 *         extended PackagePermission.
 * 
 */
public class FilteredTestControl extends TestCase {

	private BundleContext		context;
	private PermissionAdmin		permAdmin;

	/**
	 * Prior to each test, flag is set to false. If the target exception is
	 * caught, it will be set to true.
	 */
	private boolean				exceptionFlag;
	private boolean				flagRegisterEvent;
	private boolean				flagModifyEvent;
	private boolean				flagUnregisterEvent;
	private boolean				flagSpecificSetting;

	private static final String	REGISTER_BUNDLE_LOCATION						= "bundles/register.jar";
	private static final String	REGISTER_MODIFY_BUNDLE_LOCATION					= "bundles/registerModify.jar";
	private static final String	REGISTER_PLURAL_BUNDLE_LOCATION					= "bundles/registerPlural.jar";
	private static final String	GET_BUNDLE_LOCATION								= "bundles/get.jar";
	private static final String	RESET_PERMISSION_BUNDLE_LOCATION				= "bundles/setPermission.jar";
	private static final String	EXPORT_BUNDLE1_LOCATION							= "bundles/exportBundle.jar";
	private static final String	EXPORT_BUNDLE_2_LOCATION						= "bundles/exportBundle2.jar";
	private static final String	EXPORT_BUNDLE_3_LOCATION						= "bundles/exportBundle3.jar";
	private static final String	EXPORT_BUNDLE_4_LOCATION						= "bundles/exportBundle4.jar";
	private static final String	IMPORT_BUNDLE_LOCATION							= "bundles/importBundle.jar";
	private static final String	REGISTER_SERVICEREGISTRATION_BUNDLE_LOCATION	= "bundles/registerForServiceRegistration.jar";

	private Bundle				registerBundle									= null;
	private Bundle				registerPluralBundle							= null;
	private Bundle				getBundle										= null;
	private Bundle				registerModifyBundle							= null;
	private Bundle				exportBundle1									= null;
	private Bundle				importBundle									= null;
	private Bundle				registerForServiceRegistrationTestBundle		= null;
	private Bundle				setPermBundle									= null;
	private Bundle				exportBundle2									= null;
	private Bundle				exportBundle3									= null;
	private Bundle				exportBundle4									= null;

	private static final int	SLEEP_PERIOD_IN_MSEC							= 200;

	private static final String	PACKAGE_NAME_EXPORTBUNDLE1						= "org.osgi.test.cases.permissions.filtered.exportBundle";
	private static final String	PACKAGE_NAME_EXPORTBUNDLE2						= "org.osgi.test.cases.permissions.filtered.exportBundle2";

	private static final String	PACKAGE_NAME_UTIL								= "org.osgi.test.cases.permissions.filtered.exportPackageUtil";

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
		this.flagSpecificSetting = false;
		this.resetBundles();
		this.setAllpermission(RESET_PERMISSION_BUNDLE_LOCATION);
		setPermBundle = this.installBundle(RESET_PERMISSION_BUNDLE_LOCATION);
		setPermBundle.start();
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
		if (this.exportBundle1 != null) {
			this.exportBundle1.uninstall();
			this.exportBundle1 = null;
		}
		if (this.importBundle != null) {
			this.importBundle.uninstall();
			this.importBundle = null;
		}
		if (this.registerForServiceRegistrationTestBundle != null) {
			this.registerForServiceRegistrationTestBundle.uninstall();
			this.registerForServiceRegistrationTestBundle = null;
		}
		if (this.exportBundle2 != null) {
			this.exportBundle2.uninstall();
			this.exportBundle2 = null;
		}
		if (this.exportBundle3 != null) {
			this.exportBundle3.uninstall();
			this.exportBundle3 = null;
		}
		if (this.exportBundle4 != null) {
			this.exportBundle4.uninstall();
			this.exportBundle4 = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	public void tearDown() throws Exception {
		this.resetBundles();
		setPermBundle.uninstall();
	}

	/*
	 * ----------------------------------------- Test methods.
	 */

	public void testServiceRegistration1_1_1_1() throws Exception {
		final String testId = setTestId("1_1_1_1");
		installStartAndCheckSecurityException(testId, REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
	}

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
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceRegistration1_1_1_5() throws Exception {
		final String testId = setTestId("1_1_1_5");
		installStartAndCheckSecurityException(testId, REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceRegistration1_1_2_1() throws Exception {
		final String testId = setTestId("1_1_2_1");
		installStartAndCheckSecurityException(testId, REGISTER_BUNDLE_LOCATION);
		assertTrue("Succeed in registering service. It MUST fail.",
				exceptionFlag);
	}

	public void testServiceRegistration1_1_2_2() throws Exception {
		final String testId = setTestId("1_1_2_1");
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

	public void testServiceGet2_3_2_1() throws Exception {
		flagSpecificSetting = true;
		final String testId = setTestId("2_3_2_1");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_3_2_3() throws Exception {
		flagSpecificSetting = true;
		final String testId = setTestId("2_3_2_3");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_3_3_1() throws Exception {
		flagSpecificSetting = true;
		final String testId = setTestId("2_3_3_1");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_3_3_3() throws Exception {
		flagSpecificSetting = true;
		final String testId = setTestId("2_3_3_3");
		getBundle = installBundle(GET_BUNDLE_LOCATION);
		this.installStartAndCheckSecurityException(testId,
				REGISTER_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		if (!exceptionFlag)
			return;
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Fail to get service. It MUST succeed.", exceptionFlag);
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
					case ServiceEvent.REGISTERED :
						flagRegisterEvent = true;
						break;
					case ServiceEvent.MODIFIED :
						flagModifyEvent = true;
						break;
					case ServiceEvent.UNREGISTERING :
						flagUnregisterEvent = true;
						break;
					default :
						break;
				}
			}
		};
		context.addServiceListener(sl);
		return sl;
	}

	public void testGetRegisteredService4_1_1_1() throws Exception {
		final String testId = setTestId("4_1_1_1");
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);

		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			fail("Fail to register service under multiple name. It MUST succeed.");
	}

	public void testGetRegisteredService4_1_1_2() throws Exception {
		final String testId = setTestId("4_1_1_2");
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);

		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			return;
		fail("Succeed in registering service under multiple name. It MUST fail.");
	}

	public void testGetRegisteredService4_1_2_1() throws Exception {
		final String testId = setTestId("4_1_2_1");
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);

		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			fail("Fail to register service under multiple name. It MUST succeed.");
	}

	public void testGetRegisteredService4_1_2_2() throws Exception {
		final String testId = setTestId("4_1_2_2");
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);

		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			return;
		fail("Succeed in registering service under multiple name. It MUST fail.");
	}

	public void testGetRegisteredService4_1_3_1() throws Exception {
		final String testId = setTestId("4_1_3_1");
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);

		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			fail("Fail to register service under multiple name. It MUST succeed.");
	}

	public void testGetRegisteredService4_1_3_2() throws Exception {
		final String testId = setTestId("4_1_3_2");
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);

		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			return;
		fail("Succeed in registering service under multiple name. It MUST fail.");
	}

	public void testGetRegisteredService4_1_4_1() throws Exception {
		final String testId = setTestId("4_1_4_1");
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);

		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			fail("Fail to register service under multiple name. It MUST succeed.");
	}

	public void testGetRegisteredService4_1_4_2() throws Exception {
		final String testId = setTestId("4_1_4_2");
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);

		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			return;
		fail("Succeed in registering service under multiple name. It MUST fail.");
	}

	public void testGetRegisteredService4_1_5_1() throws Exception {
		final String testId = setTestId("4_1_5_1");
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);

		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			fail("Fail to register service under multiple name. It MUST succeed.");
	}

	public void testGetRegisteredService4_1_5_2() throws Exception {
		final String testId = setTestId("4_1_5_2");
		this.installStartAndCheckSecurityException(testId,
				REGISTER_PLURAL_BUNDLE_LOCATION);
		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			return;
		fail("Succeed in registering service under multiple name. It MUST fail.");
	}

	public void testGetServicesInUse5_1_1_1() throws Exception {
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

	public void testGetServicesInUse5_1_1_2() throws Exception {
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

	public void testGetServicesInUse5_1_2_1() throws Exception {
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

	public void testGetServicesInUse5_1_2_2() throws Exception {
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

	public void testGetServicesInUse5_1_3_1() throws Exception {
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

	public void testGetServicesInUse5_1_3_2() throws Exception {
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

	public void testGetServicesInUse5_1_4_1() throws Exception {
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

	public void testGetServicesInUse5_1_4_2() throws Exception {
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

	public void testGetServicesInUse5_1_5_1() throws Exception {
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

	public void testGetServicesInUse5_1_5_2() throws Exception {
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

	public void testExportPackage7_1_1_1() throws Exception {
		final String testId = setTestId("7_1_1_1");
		ExportedPackage[] pkgs = this
				.packagePermissionTestPrepareForExportBundle1AndImportBundle(testId);
		if (pkgs != null && pkgs.length == 1
				&& pkgs[0].getName().equals(PACKAGE_NAME_EXPORTBUNDLE1))
			return;

		fail("Fail to export package. It MUST succeed.");
	}

	public void testExportPackage7_1_1_2() throws Exception {
		final String testId = setTestId("7_1_1_2");
		ExportedPackage[] pkgs = this
				.packagePermissionTestPrepareForExportBundle1AndImportBundle(testId);
		if (pkgs == null)
			return;

		fail("Succeed in exporting package. It MUST fail.");
	}

	public void testExportPackage7_1_1_3() throws Exception {
		final String testId = setTestId("7_1_1_3");
		ExportedPackage[] pkgs = this
				.packagePermissionTestPrepareForExportBundle1AndImportBundle(testId);
		if (pkgs != null && pkgs.length == 1
				&& pkgs[0].getName().equals(PACKAGE_NAME_EXPORTBUNDLE1))
			return;
		fail("Fail to export package. It MUST succeed.");
	}

	public void testExportPackage7_1_1_4() throws Exception {
		final String testId = setTestId("7_1_1_4");
		ExportedPackage[] pkgs = this
				.packagePermissionTestPrepareForExportBundle1AndImportBundle(testId);
		if (pkgs != null && pkgs.length == 1
				&& pkgs[0].getName().equals(PACKAGE_NAME_EXPORTBUNDLE1))
			return;
		fail("Fail to export package. It MUST succeed.");
	}

	public void testExportPackage7_1_1_5() throws Exception {
		final String testId = setTestId("7_1_1_5");
		ExportedPackage[] pkgs = this.ppTestPrepareForExportBundle2Only(testId);

		if (pkgs != null && pkgs.length == 1
				&& pkgs[0].getName().equals(PACKAGE_NAME_EXPORTBUNDLE2))
			return;
		fail("Fail to export package. It MUST succeed.");
	}

	public void testExportPackage7_1_1_6() throws Exception {
		final String testId = setTestId("7_1_1_6");
		packagePermissionTestPrepareForExportBundle3and4(testId);
		if (exportBundle3.getState() == Bundle.RESOLVED)
			return;
		fail("Fail to import package. It MUST succeed.");
	}

	public void testExportPackage7_1_2_1() throws Exception {
		final String testId = setTestId("7_1_2_1");
		ExportedPackage[] pkgs = this.ppTestPrepareForExportBundle1Only(testId);
		if (pkgs != null && pkgs.length == 1
				&& pkgs[0].getName().equals(PACKAGE_NAME_EXPORTBUNDLE1)) {
			Bundle[] importer = pkgs[0].getImportingBundles();
			if (importer == null || importer.length == 0)
				return;
		}

		fail("Fail to export package. It MUST succeed.");
	}

	public void testExportPackage7_1_2_2() throws Exception {
		final String testId = setTestId("7_1_2_2");
		ExportedPackage[] pkgs = this.ppTestPrepareForExportBundle1Only(testId);
		if (pkgs == null)
			return;

		fail("Succeed in exporting package. It MUST fail.");
	}

	public void testExportPackage7_1_2_3() throws Exception {
		final String testId = setTestId("7_1_2_3");
		ExportedPackage[] pkgs = this.ppTestPrepareForExportBundle2Only(testId);
		if (pkgs != null && pkgs.length == 1
				&& pkgs[0].getName().equals(PACKAGE_NAME_EXPORTBUNDLE2)) {
			Bundle[] importer = pkgs[0].getImportingBundles();
			if (importer.length == 1 && importer[0].equals(this.exportBundle2))
				return;
		}
		if (exportBundle2.getState() == Bundle.RESOLVED)
			return;
		fail("Fail to export package. It MUST succeed.");
	}

	public void testExportPackage7_1_2_4() throws Exception {
		final String testId = setTestId("7_1_2_4");
		this.ppTestPrepareForExportBundle2Only(testId);

		ExportedPackage[] pkgs = this.ppTestPrepareForExportBundle2Only(testId);
		if (pkgs != null && pkgs.length == 1
				&& pkgs[0].getName().equals(PACKAGE_NAME_EXPORTBUNDLE2)) {
			Bundle[] importers = pkgs[0].getImportingBundles();
			if (importers == null || importers.length == 0)
				return;
		}

		fail("Succeed in exporting package. It MUST fail.");
	}

	public void testExportPackage7_1_2_5() throws Exception {
		final String testId = setTestId("7_1_2_5");
		ExportedPackage[] packages = packagePermissionTestPrepareForExportBundle3and4(testId);
		if (exportBundle3.getState() != 4)
			fail("Fail to import package. It MUST succeed.");

		this.exportBundle4.start();

		printoutHeader(this.exportBundle3);
		printoutHeader(this.exportBundle4);

		boolean flag4 = false;
		for (int i = 0; i < packages.length; i++) {
			Bundle exporter = packages[i].getExportingBundle();
			Bundle[] importers = packages[i].getImportingBundles();
			if (exporter.equals(this.exportBundle3)) {
				if (importers != null && importers[0].equals(exportBundle3))
					fail("exportBundle3 imported a Package exported by itself. It MUST import a package exported by exportBundle4.");

			}
			else
				if (exporter.equals(this.exportBundle4)) {
					if (importers.length == 1)
						if (importers[0].equals(this.exportBundle3)) {
							flag4 = true;
						}
				}
		}
		if (flag4)
			return;
		fail("Fail to import package. It MUST succeed.");

	}

	private void printoutHeader(Bundle bundle) {
		Dictionary headers = bundle.getHeaders();
		System.out.println("bundle=" + bundle);

		for (Enumeration keys = headers.keys(); keys.hasMoreElements();) {
			Object key = keys.nextElement();
			if (key.equals(Constants.IMPORT_PACKAGE)
					|| key.equals(Constants.EXPORT_PACKAGE)
					|| key.equals(Constants.BUNDLE_SYMBOLICNAME))
				System.out.println("\t[" + key + "," + headers.get(key) + "]");
		}

	}

	public void testExportPackage7_1_2_6() throws Exception {
		final String testId = setTestId("7_1_2_6");
		ExportedPackage[] packages = packagePermissionTestPrepareForExportBundle3and4(testId);
		boolean flag3 = false;
		boolean flag4 = false;
		for (int i = 0; i < packages.length; i++) {
			Bundle exporter = packages[i].getExportingBundle();
			Bundle[] importingBundles = packages[i].getImportingBundles();
			if (exporter.equals(this.exportBundle3)) {
				if (importingBundles == null || importingBundles.length == 0)
					flag3 = true;
			}
			else
				if (exporter.equals(this.exportBundle4)) {
					if (importingBundles == null
							|| importingBundles.length == 0)
						flag4 = true;
				}
		}
		if (flag3 && flag4)
			return;

		fail("Succeed in importing package. It MUST fail.");
	}

	public void testExportPackage7_1_3_1() throws Exception {
		final String testId = setTestId("7_1_3_1");
		this.ppTestPrepareForExportBundle2Only(testId);

		ExportedPackage[] pkgs = this.ppTestPrepareForExportBundle2Only(testId);
		if (pkgs == null)
			return;

		fail("Succeed in exporting package. It MUST fail.");
	}

	public void testImportPackage8_1_1_1() throws Exception {
		final String testId = setTestId("8_1_1_1");
		this
				.packagePermissionTestPrepareForExportBundle1AndImportBundle(testId);
		if (exportBundle1.getState() == Bundle.RESOLVED
				&& importBundle.getState() == Bundle.RESOLVED)
			return;
		fail("Fail to import package. It MUST succeed.");
	}

	public void testImportPackage8_1_1_2() throws Exception {
		final String testId = setTestId("8_1_1_2");
		this
				.packagePermissionTestPrepareForExportBundle1AndImportBundle(testId);
		if (exportBundle1.getState() == Bundle.RESOLVED
				&& importBundle.getState() == Bundle.RESOLVED)
			fail("Succeed in importing package. It MUST fail.");
	}

	public void testImportPackage8_1_1_3() throws Exception {
		final String testId = setTestId("8_1_1_3");
		this
				.packagePermissionTestPrepareForExportBundle1AndImportBundle(testId);
		if (exportBundle1.getState() == Bundle.RESOLVED
				&& importBundle.getState() == Bundle.RESOLVED)
			return;
		fail("Fail to import package. It MUST succeed.");
	}

	public void testImportPackage8_1_1_4() throws Exception {
		final String testId = setTestId("8_1_1_4");
		this
				.packagePermissionTestPrepareForExportBundle1AndImportBundle(testId);
		if (exportBundle1.getState() == Bundle.RESOLVED
				&& importBundle.getState() == Bundle.RESOLVED)
			return;
		fail("Fail to import package. It MUST succeed.");
	}

	public void testImportPackage8_1_1_5() throws Exception {
		final String testId = setTestId("8_1_1_5");
		this
				.packagePermissionTestPrepareForExportBundle1AndImportBundle(testId);
		if (exportBundle1.getState() == Bundle.RESOLVED
				&& importBundle.getState() == Bundle.RESOLVED)
			return;
		fail("Fail to import package. It MUST succeed.");
	}

	public void testImportPackage8_1_1_6() throws Exception {
		final String testId = setTestId("8_1_1_6");
		this
				.packagePermissionTestPrepareForExportBundle1AndImportBundle(testId);
		if (exportBundle1.getState() == Bundle.RESOLVED
				&& importBundle.getState() == Bundle.RESOLVED)
			fail("Succeed in importing package. It MUST fail.");
	}

	public void testImportPackage8_1_1_7() throws Exception {
		final String testId = setTestId("8_1_1_7");
		this
				.packagePermissionTestPrepareForExportBundle1AndImportBundle(testId);
		if (exportBundle1.getState() == Bundle.RESOLVED
				&& importBundle.getState() == Bundle.RESOLVED)
			return;
		fail("Fail to import package. It MUST succeed.");
	}

	public void testImportPackage8_1_1_8() throws Exception {
		final String testId = setTestId("8_1_1_8");
		this
				.packagePermissionTestPrepareForExportBundle1AndImportBundle(testId);
		if (exportBundle1.getState() == Bundle.RESOLVED
				&& importBundle.getState() == Bundle.RESOLVED)
			return;
		fail("Fail to import package. It MUST succeed.");
	}

	public void testImportPackage8_1_1_9() throws Exception {
		final String testId = setTestId("8_1_1_9");
		this
				.packagePermissionTestPrepareForExportBundle1AndImportBundle(testId);
		if (exportBundle1.getState() == Bundle.RESOLVED
				&& importBundle.getState() == Bundle.RESOLVED)
			return;
		fail("Fail to import package. It MUST succeed.");
	}

	public void testImportPackage8_1_1_10() throws Exception {
		final String testId = setTestId("8_1_1_10");
		this
				.packagePermissionTestPrepareForExportBundle1AndImportBundle(testId);
		if (exportBundle1.getState() == Bundle.RESOLVED
				&& importBundle.getState() == Bundle.RESOLVED)
			fail("Succeed in importing package. It MUST fail.");
	}

	public void testImportPackage8_1_1_11() throws Exception {
		final String testId = setTestId("8_1_1_11");
		this
				.packagePermissionTestPrepareForExportBundle1AndImportBundle(testId);
		if (exportBundle1.getState() == Bundle.RESOLVED
				&& importBundle.getState() == Bundle.RESOLVED)
			return;
		fail("Fail to import package. It MUST succeed.");
	}

	public void testImportPackage8_1_1_12() throws Exception {
		final String testId = setTestId("8_1_1_12");
		this
				.packagePermissionTestPrepareForExportBundle1AndImportBundle(testId);
		if (exportBundle1.getState() == Bundle.RESOLVED
				&& importBundle.getState() == Bundle.RESOLVED)
			fail("Succeed in importing package. It MUST fail.");
	}

	/*
	 * ----------------------------------------- Utility methods.
	 */

	private void prepare(final String testId) {
		ServiceReference ref = context
				.getServiceReference(ISetPermissionService.class.getName());
		if (ref == null) {
			System.out.println("Fail to get ServiceReference of "
					+ ISetPermissionService.class.getName());
		}
		ISetPermissionService setPerm = (ISetPermissionService) context
				.getService(ref);
		if (flagSpecificSetting)
			this.setPermissionsForSpecificSettings(testId);
		else
			setPerm.setPermissions(testId);
		this.printPermissions();
	}

	private static void sleep() {
		try {
			Thread.sleep(SLEEP_PERIOD_IN_MSEC);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void setAllpermission(String bundleLocation) {
		ServiceReference ref = context
				.getServiceReference(PermissionAdmin.class.getName());
		if (ref == null) {
			System.out.println("Fail to get ServiceReference of "
					+ PermissionAdmin.class.getName());
			return;
		}
		permAdmin = (PermissionAdmin) context.getService(ref);
		PermissionInfo[] pisAllPerm = new PermissionInfo[1];
		pisAllPerm[0] = new PermissionInfo("(" + AllPermission.class.getName()
				+ ")");
		permAdmin.setPermissions(bundleLocation, pisAllPerm);
	}

	private void setPermissionsForSpecificSettings(String testId) {
		ServiceReference ref = context
				.getServiceReference(PermissionAdmin.class.getName());
		if (ref == null) {
			System.out.println("Fail to get ServiceReference of "
					+ PermissionAdmin.class.getName());
			return;
		}
		permAdmin = (PermissionAdmin) context.getService(ref);
		String bundleLoc = registerBundle.getLocation();
		long bundleId = registerBundle.getBundleId();
		PermissionInfo[] pisDefault = new PermissionInfo[1];
		pisDefault[0] = new PermissionInfo("(" + AllPermission.class.getName()
				+ ")");
		permAdmin.setDefaultPermissions(pisDefault);
		PermissionInfo[] pisTestCase = new PermissionInfo[1];
		pisTestCase[0] = new PermissionInfo("(" + AllPermission.class.getName()
				+ ")");
		permAdmin
				.setPermissions(context.getBundle().getLocation(), pisTestCase);
		PermissionInfo[] pisRegister = new PermissionInfo[1];
		pisRegister[0] = new PermissionInfo("(" + AllPermission.class.getName()
				+ ")");
		permAdmin.setPermissions(registerBundle.getLocation(), pisRegister);
		PermissionInfo[] pisGet = new PermissionInfo[4];
		pisGet[0] = new PermissionInfo("(" + PackagePermission.class.getName()
				+ " \"org.osgi.framework\" \"IMPORT\")");
		pisGet[1] = new PermissionInfo(
				"("
						+ PackagePermission.class.getName()
						+ " \"org.osgi.test.cases.permissions.filtered.util\" \"IMPORT\")");
		pisGet[2] = new PermissionInfo("(" + FilePermission.class.getName()
				+ " \"<<ALL FILES>>\" \"read\")");
		if (testId.equals("2_3_2_1"))
			pisGet[3] = new PermissionInfo(
					"("
							+ ServicePermission.class.getName()
							+ " \"(&(objectClass=org.osgi.test.cases.permissions.filtered.util.IService)(service.bundleLocation="
							+ bundleLoc + "))\" \"GET\")");
		else
			if (testId.equals("2_3_2_3"))
				pisGet[3] = new PermissionInfo(
						"("
								+ ServicePermission.class.getName()
								+ " \"(&(objectClass=org.osgi.test.cases.permissions.filtered.util.IService)(!(service.bundleLocation="
								+ bundleLoc + ")))\" \"GET\")");
			else
				if (testId.equals("2_3_3_1"))
					pisGet[3] = new PermissionInfo(
							"("
									+ ServicePermission.class.getName()
									+ " \"(&(objectClass=org.osgi.test.cases.permissions.filtered.util.IService)(service.bundleId="
									+ bundleId + "))\" \"GET\")");
				else
					if (testId.equals("2_3_3_3"))
						pisGet[3] = new PermissionInfo(
								"("
										+ ServicePermission.class.getName()
										+ " \"(&(objectClass=org.osgi.test.cases.permissions.filtered.util.IService)(!(service.bundleId="
										+ bundleId + ")))\" \"GET\")");
					else
						throw new IllegalArgumentException(
								"location is invalid. TestID=" + testId);
		permAdmin.setPermissions(getBundle.getLocation(), pisGet);
	}

	private void printPermissions() {
		PermissionInfo[] pis = permAdmin.getDefaultPermissions();
		if (pis == null) {
			System.out.println("DefaultPermissions[] is not set");
		}
		else {
			for (int i = 0; i < pis.length; i++)
				System.out.println("DefaultPermissions[" + i + "]="
						+ pis[i].getEncoded());
		}
		String[] locations = permAdmin.getLocations();
		if (locations == null) {
			System.out.println("pa.getLocation() == null");
		}
		else {
			for (int j = 0; j < locations.length; j++) {
				System.out.println("Permissions of (" + locations[j] + "):");
				pis = permAdmin.getPermissions(locations[j]);
				if (pis == null) {
					System.out.println("Permissions of (" + locations[j]
							+ ") is not set");
				}
				else {
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
	 * @param testId Test ID.
	 * @param location location of a bundle to be installed.
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
		}
		else
			if (location.equals(REGISTER_PLURAL_BUNDLE_LOCATION)) {
				registerPluralBundle = this.installBundle(location);
				this.prepare(testId);
				this.startBundleAndCheckSecurityException(registerPluralBundle);
			}
			else
				if (location.equals(REGISTER_MODIFY_BUNDLE_LOCATION)) {
					this.registerModifyBundle = this.installBundle(location);
					this.prepare(testId);
					this
							.startBundleAndCheckSecurityException(this.registerModifyBundle);
				}
				else
					if (location
							.equals(REGISTER_SERVICEREGISTRATION_BUNDLE_LOCATION)) {
						this.registerForServiceRegistrationTestBundle = this
								.installBundle(location);
						prepare(testId);
						this
								.startBundleAndCheckSecurityException(registerForServiceRegistrationTestBundle);
					}
					else {
						throw new IllegalArgumentException(
								"location is invalid. location=" + location);
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
		}
		catch (BundleException be) {
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
		}
		catch (BundleException be) {
			this.checkIfExIsPermissionsFilterException(be);
		}
	}

	private String setTestId(String testId) {
		System.out.println("This test is ---> " + testId);
		return testId;
	}

	/**
	 * 
	 * install both exportBundle1 and importBundle and return exported packages
	 * by exportBundle.
	 * 
	 * @param testId
	 * @return
	 * @throws Exception
	 */
	private ExportedPackage[] packagePermissionTestPrepareForExportBundle1AndImportBundle(
			String testId) throws Exception {
		exportBundle1 = this.installBundle(EXPORT_BUNDLE1_LOCATION);
		importBundle = this.installBundle(IMPORT_BUNDLE_LOCATION);
		this.prepare(testId);
		Bundle[] bundles = new Bundle[] {exportBundle1, importBundle};
		PackageAdmin packageAdmin = getPackageAdmin();
		packageAdmin.refreshPackages(bundles);
		sleep();
		return packageAdmin.getExportedPackages(exportBundle1);
	}

	/**
	 * 
	 * install both exportBundle1 Return exported packages by exportBundle1.
	 * 
	 * @param testId
	 * @return
	 * @throws Exception
	 */
	private ExportedPackage[] ppTestPrepareForExportBundle1Only(String testId)
			throws Exception {
		exportBundle1 = this.installBundle(EXPORT_BUNDLE1_LOCATION);
		this.prepare(testId);
		Bundle[] bundles = new Bundle[] {exportBundle1};
		PackageAdmin packageAdmin = getPackageAdmin();
		packageAdmin.refreshPackages(bundles);
		sleep();
		return packageAdmin.getExportedPackages(exportBundle1);
	}

	/**
	 * 
	 * install both exportBundle2. Return exported packages by exportBundle2.
	 * 
	 * @param testId
	 * @return
	 * @throws Exception
	 */
	private ExportedPackage[] ppTestPrepareForExportBundle2Only(String testId)
			throws Exception {
		exportBundle2 = this.installBundle(EXPORT_BUNDLE_2_LOCATION);
		this.prepare(testId);
		Bundle[] bundles = new Bundle[] {exportBundle2};
		PackageAdmin packageAdmin = getPackageAdmin();
		packageAdmin.refreshPackages(bundles);
		sleep();
		return packageAdmin.getExportedPackages(exportBundle2);
	}

	/**
	 * install both exportBundle3 and exportBundle4 and return exported packages
	 * under name of PACKGE_NAME_UTIL.
	 * 
	 * @param testId
	 * @return
	 * @throws Exception
	 */
	private ExportedPackage[] packagePermissionTestPrepareForExportBundle3and4(
			String testId) throws Exception {
		exportBundle3 = this.installBundle(EXPORT_BUNDLE_3_LOCATION);
		exportBundle4 = this.installBundle(EXPORT_BUNDLE_4_LOCATION);
		this.prepare(testId);
		Bundle[] bundles = new Bundle[] {exportBundle3, exportBundle4};
		PackageAdmin packageAdmin = getPackageAdmin();
		packageAdmin.refreshPackages(bundles);
		sleep();
		return packageAdmin.getExportedPackages(PACKAGE_NAME_UTIL);
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

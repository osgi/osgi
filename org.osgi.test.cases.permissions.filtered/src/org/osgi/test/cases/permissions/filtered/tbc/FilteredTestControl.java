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
import java.net.URL;
import java.security.AllPermission;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.permissions.filtered.util.PermissionsFilterException;

/**
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 * 
 *         This class provides TestCases for RFC131, extended ServicePermission
 *         and extended PackagePermission as whole framework.
 * 
 */
public class FilteredTestControl extends TestCase {

	private static final String	S_NAME_UTIL_ISERVICE1						= "org.osgi.test.cases.permissions.filtered.util.IService1";
	private static final String	S_NAME_UTIL_ISERVICE2						= "org.osgi.test.cases.permissions.filtered.util.IService2";
	private static final String	P_NAME_UTIL									= "org.osgi.test.cases.permissions.filtered.util";
	private static final String	P_NAME_SHARED								= "org.osgi.test.cases.permissions.filtered.sharedPkg";
	private static final String	SP											= "org.osgi.framework.ServicePermission";
	private static final String	PP											= "org.osgi.framework.PackagePermission";
	private BundleContext		context;
	private PermissionAdmin		permAdmin;
	private PackageAdmin		pkgAdmin;

	/**
	 * Prior to each test, flag is set to false. If the target exception is
	 * caught, it will be set to true.
	 */
	private boolean				exceptionFlag;
	private boolean				flagRegisterEvent;
	private boolean				flagModifyEvent;
	private boolean				flagUnregisterEvent;

	private static final String	REGISTER_BUNDLE_LOCATION					= "bundles/register.jar";
	private static final String	REGISTER_MODIFY_BUNDLE_LOCATION				= "bundles/registerModify.jar";
	private static final String	REGISTER_PLURAL_BUNDLE_LOCATION				= "bundles/registerPlural.jar";
	private static final String	GET_BUNDLE_LOCATION							= "bundles/get.jar";
	private static final String	RESET_PERMISSION_BUNDLE_LOCATION			= "bundles/setPermission.jar";
	private static final String	EXPORT_BUNDLE_1_LOCATION					= "bundles/exporter1.jar";
	private static final String	EXPORT_BUNDLE_2_LOCATION					= "bundles/exporter2.jar";
	private static final String	IMPORT_BUNDLE_1_LOCATION					= "bundles/importer1.jar";
	private static final String	IMPORT_BUNDLE_2_LOCATION					= "bundles/importer2.jar";

	private Bundle				registerBundle								= null;
	private Bundle				registerPluralBundle						= null;
	private Bundle				getBundle									= null;
	private Bundle				registerModifyBundle						= null;
	private Bundle				exportBundle1								= null;
	private Bundle				importBundle1								= null;
	private Bundle				importBundle2								= null;
	private Bundle				registerForServiceRegistrationTestBundle	= null;
	private Bundle				setPermBundle								= null;
	private Bundle				exportBundle2								= null;
	private LinkedList			list;

	private static final int	SLEEP_PERIOD_IN_MSEC						= 200;

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
		permAdmin = this.getPermissionAdmin();
		pkgAdmin = this.getPackageAdmin();
		this.resetBundles();
		this.setAllpermission(RESET_PERMISSION_BUNDLE_LOCATION);
		setPermBundle = this.installBundle(RESET_PERMISSION_BUNDLE_LOCATION);
		setPermBundle.start();
		list = new LinkedList();
		this.registerBundle = this.installBundle(REGISTER_BUNDLE_LOCATION);
		this.registerPluralBundle = this
				.installBundle(REGISTER_PLURAL_BUNDLE_LOCATION);
		this.getBundle = this.installBundle(GET_BUNDLE_LOCATION);
		this.registerModifyBundle = this
				.installBundle(REGISTER_MODIFY_BUNDLE_LOCATION);
		this.exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		this.exportBundle2 = this.installBundle(EXPORT_BUNDLE_2_LOCATION);
		this.importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		this.importBundle2 = this.installBundle(IMPORT_BUNDLE_2_LOCATION);
		this.setBasePermissions();
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
		if (this.importBundle1 != null) {
			this.importBundle1.uninstall();
			this.importBundle1 = null;
		}

		if (this.importBundle2 != null) {
			this.importBundle2.uninstall();
			this.importBundle2 = null;
		}
		if (this.registerForServiceRegistrationTestBundle != null) {
			this.registerForServiceRegistrationTestBundle.uninstall();
			this.registerForServiceRegistrationTestBundle = null;
		}
		if (this.exportBundle2 != null) {
			this.exportBundle2.uninstall();
			this.exportBundle2 = null;
		}

		this.refreshPackagesAndResolveBundles(null);
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

	public void testServiceRegistration1_1_1() throws Exception {
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, SP, S_NAME_UTIL_ISERVICE1, "REGISTER");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.registerBundle, list);
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceRegistration1_1_2() throws Exception {
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, SP, "org.osgi.test.cases.permissions.filtered.util.*",
				"REGISTER");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.registerBundle, list);
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceRegistration1_1_3() throws Exception {
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, SP, "org.osgi.test.cases.permissions.*", "REGISTER");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.registerBundle, list);
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceRegistration1_1_4() throws Exception {
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, SP, "*", "REGISTER");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.registerBundle, list);
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceRegistration1_2_1() throws Exception {
		add(list, PP, P_NAME_UTIL, "IMPORT");
		// add(list, SP, S_NAME_UTIL_ISERVICE, "REGISTER");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.registerBundle, list);
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertTrue("Succeed in registering service. It MUST fail.",
				exceptionFlag);
	}

	public void testServiceRegistration1_3_1() throws Exception {
		add(list, PP, P_NAME_UTIL, "IMPORT");
		// It must be ignored because filter is invalid for REGISTER action.
		add(list, SP, "(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")", "REGISTER");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.registerBundle, list);
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertTrue("Succeed in registering service. It MUST fail.",
				exceptionFlag);
	}

	public void testServiceRegistration1_3_2() throws Exception {
		add(list, PP, P_NAME_UTIL, "IMPORT");
		// It must be ignored because filter is invalid for REGISTER action.
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(segment=providerA))", "REGISTER");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.registerBundle, list);
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertTrue("Succeed in registering service. It MUST fail.",
				exceptionFlag);
	}

	public void testPluralInterfaceRegister1_4_1() throws Exception {
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, SP, S_NAME_UTIL_ISERVICE1, "REGISTER");
		add(list, SP, S_NAME_UTIL_ISERVICE2, "REGISTER");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.registerPluralBundle, list);
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
	}

	public void testPluralInterfaceRegister1_4_2() throws Exception {
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, SP, S_NAME_UTIL_ISERVICE1, "REGISTER");
		// add(list, SP, S_NAME_UTIL_ISERVICE2, "REGISTER");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.registerPluralBundle, list);
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertTrue("Succeed in registering service. It MUST fail.",
				exceptionFlag);
	}

	public void testPluralInterfaceRegister1_4_3() throws Exception {
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, SP, "org.osgi.test.cases.permissions.*", "REGISTER");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.registerPluralBundle, list);
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_01_1() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, S_NAME_UTIL_ISERVICE1, "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_01_2() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "org.osgi.test.cases.permissions.filtered.*", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_01_3() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);

		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "org.osgi.test.cases.permissions.filtered.util.*", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_01_4() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "*", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_02_1() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_02_2() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP,
				"(objectClass=org.osgi.test.cases.permissions.filtered.*)",
				"GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_02_3() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(objectClass=org.osgi.test.*)", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_02_4() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(objectClass=*)", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_03_1() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		// add(list, SP, S_NAME_UTIL_ISERVICE, "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_03_2() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "something.else", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_03_3() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "something.*", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_04_1() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(!(objectClass=" + S_NAME_UTIL_ISERVICE2 + "))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_05_1() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(objectClass=" + S_NAME_UTIL_ISERVICE2 + ")", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_05_2() throws Exception {
		this.startBundleAndCheckSecurityException(registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(!(objectClass=" + S_NAME_UTIL_ISERVICE1 + "))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	// plural interface
	public void testServiceGet2_06_1() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);

		this.getBundle.stop();
		list.clear();
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(objectClass=" + S_NAME_UTIL_ISERVICE2 + ")", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);

		this.getBundle.stop();
		list.clear();
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, S_NAME_UTIL_ISERVICE1, "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);

		this.getBundle.stop();
		list.clear();
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, S_NAME_UTIL_ISERVICE2, "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_06_2() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(!(objectClass=something.else))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_06_3() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(!(objectClass=" + S_NAME_UTIL_ISERVICE1 + "))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	// service properties
	public void testServiceGet2_07_1() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(segment=providerA))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_07_2() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(segment=providerFail))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_07_3() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(!(segment=providerFail)))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_07_4() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(!(segment=providerFail)))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	// bundle identifiers
	public void testServiceGet2_08_1() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(location="
				+ this.registerBundle.getLocation() + "))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_08_2() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(location=something.else))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_08_3() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(id="
				+ this.registerBundle.getBundleId() + "))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_08_4() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(id="
				+ this.context.getBundle().getBundleId() + "))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_08_5() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(name="
				+ this.registerBundle.getSymbolicName() + "))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_08_6() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(name=something.else))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_08_7() throws Exception {
		// TODO implements signer test.
	}

	public void testServiceGet2_08_8() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(signer=cn=something.else))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	// combination of service properties and bundle identifiers.
	public void testServiceGet2_09_1() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(name="
				+ this.context.getBundle().getSymbolicName()
				+ ")(segment=BBBB)", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_09_2() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(|(name="
				+ this.registerBundle.getSymbolicName() + ")(segment=BBBB)))",
				"GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	// only bundle identifiers no objectClass
	public void testServiceGet2_10_1() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(location=" + this.registerBundle.getLocation() + ")",
				"GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_10_2() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(location=something.else)", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_10_3() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(id=" + this.registerBundle.getBundleId() + ")", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_10_4() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(id=" + this.context.getBundle().getBundleId() + ")",
				"GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_10_5() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(name=" + this.registerBundle.getSymbolicName() + ")",
				"GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_10_6() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(name=something.else)", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_10_7() throws Exception {
		// TODO implements signer test.
	}

	public void testServiceGet2_10_8() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(signer=cn=something.else)", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertTrue("Succeed in getting service. It MUST fail.", exceptionFlag);
	}

	public void testServiceGet2_11_1() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(@id=id.NTT))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_11_2() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(@@location=location.NTT))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	public void testServiceGet2_11_3() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(@@@name=name.NTT))", "GET");
		this.setBundlePermission(this.getBundle, list);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
	}

	// /////////////////////////////////////////

	public void testServiceEvent3_1_1() throws Exception {
		ServiceListener sl = addServiceListener(S_NAME_UTIL_ISERVICE1);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(name="
				+ this.registerModifyBundle.getSymbolicName() + "))", "GET");
		this.setBundlePermission(this.context.getBundle(), list);
		this.startBundleAndCheckSecurityException(this.registerModifyBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
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

	public void testServiceEvent3_1_2() throws Exception {
		ServiceListener sl = addServiceListener(S_NAME_UTIL_ISERVICE1);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(name=something.else))", "GET");
		this.setBundlePermission(this.context.getBundle(), list);
		this.startBundleAndCheckSecurityException(this.registerModifyBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);

		assertFalse("ServiceEvent.REGISTERED has been delivered.",
				flagRegisterEvent);
		assertFalse("ServiceEvent.MODIFIED has been delivered.",
				flagModifyEvent);
		this.registerModifyBundle.stop();
		assertFalse("ServiceEvent.UNREGISTERED has been delivered.",
				flagUnregisterEvent);
		context.removeServiceListener(sl);
	}

	public void testServiceEvent3_1_3() throws Exception {
		ServiceListener sl = addServiceListener(S_NAME_UTIL_ISERVICE1);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(segment=providerA))", "GET");
		this.setBundlePermission(this.context.getBundle(), list);
		this.startBundleAndCheckSecurityException(this.registerModifyBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);

		assertTrue("ServiceEvent.REGISTERED has not been delivered.",
				flagRegisterEvent);
		assertFalse("ServiceEvent.MODIFIED has been delivered.",
				flagModifyEvent);
		this.registerModifyBundle.stop();
		assertFalse("ServiceEvent.UNREGISTERED has been delivered.",
				flagUnregisterEvent);
		context.removeServiceListener(sl);
	}

	public void testServiceEvent3_1_4() throws Exception {
		ServiceListener sl = addServiceListener(S_NAME_UTIL_ISERVICE1);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(vendor=ACME))", "GET");
		this.setBundlePermission(this.context.getBundle(), list);
		this.startBundleAndCheckSecurityException(this.registerModifyBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);

		assertFalse("ServiceEvent.REGISTERED has been delivered.",
				flagRegisterEvent);
		assertTrue("ServiceEvent.MODIFIED has not been delivered.",
				flagModifyEvent);
		this.registerModifyBundle.stop();
		assertTrue("ServiceEvent.UNREGISTERED has not been delivered.",
				flagUnregisterEvent);
		context.removeServiceListener(sl);
	}

	private ServiceListener addServiceListener(final String clazz) {
		ServiceListener sl = new ServiceListener() {
			public void serviceChanged(ServiceEvent se) {
				String[] clazzes = (String[]) se.getServiceReference()
						.getProperty(Constants.OBJECTCLASS);
				for (int i = 0; i < clazzes.length; i++) {
					if (clazzes[i].equals(clazz)) {
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
					break;
				}
			}
		};
		context.addServiceListener(sl);
		return sl;
	}

	// /////////////////////////////

	public void testGetRegisteredService4_1_1() throws Exception {
		this.startBundleAndCheckSecurityException(registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, SP, S_NAME_UTIL_ISERVICE1, "GET");
		this.setBundlePermission(this.context.getBundle(), list);

		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			fail("Fail to get registered service under multiple name. It MUST succeed.");
	}

	public void testGetRegisteredService4_1_2() throws Exception {
		this.startBundleAndCheckSecurityException(registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, SP, "something.else", "GET");
		this.setBundlePermission(this.context.getBundle(), list);

		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			return;
		fail("Succeed in getting registered service under multiple name. It MUST fail.");
	}

	public void testGetRegisteredService4_2_1() throws Exception {
		this.startBundleAndCheckSecurityException(registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(vendor=NTT))", "GET");
		this.setBundlePermission(this.context.getBundle(), list);

		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			fail("Fail to get registered service under multiple name. It MUST succeed.");
	}

	public void testGetRegisteredService4_2_2() throws Exception {
		this.startBundleAndCheckSecurityException(registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(vendor=something.else))", "GET");
		this.setBundlePermission(this.context.getBundle(), list);

		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			return;
		fail("Succeed in getting registered service under multiple name. It MUST fail.");
	}

	public void testGetRegisteredService4_2_3() throws Exception {
		this.startBundleAndCheckSecurityException(registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(name="
				+ this.registerPluralBundle.getSymbolicName() + "))", "GET");
		this.setBundlePermission(this.context.getBundle(), list);

		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			fail("Fail to get registered service under multiple name. It MUST succeed.");
	}

	public void testGetRegisteredService4_2_4() throws Exception {
		this.startBundleAndCheckSecurityException(registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(name=something.else))", "GET");
		this.setBundlePermission(this.context.getBundle(), list);

		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			return;
		fail("Succeed in getting registered service under multiple name. It MUST fail.");
	}

	public void testGetRegisteredService4_2_5() throws Exception {
		this.startBundleAndCheckSecurityException(registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, SP, "(name=" + this.registerPluralBundle.getSymbolicName()
				+ ")", "GET");
		this.setBundlePermission(this.context.getBundle(), list);

		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			fail("Fail to get registered service under multiple name. It MUST succeed.");
	}

	public void testGetRegisteredService4_2_6() throws Exception {
		this.startBundleAndCheckSecurityException(registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, SP, "(name=something.else)", "GET");
		this.setBundlePermission(this.context.getBundle(), list);

		ServiceReference[] ref = registerPluralBundle.getRegisteredServices();
		if (ref == null || ref.length != 1)
			return;
		fail("Succeed in getting registered service under multiple name. It MUST fail.");
	}

	public void testGetServicesInUse5_1_1() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, SP, S_NAME_UTIL_ISERVICE1, "GET");
		this.setBundlePermission(this.context.getBundle(), list);
		ServiceReference[] ref = getBundle.getServicesInUse();
		if (ref == null || ref.length != 1)
			fail("Fail to getServicesInUse(). It MUST succeed.");
	}

	public void testGetServicesInUse5_1_2() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, SP, "something.else", "GET");
		this.setBundlePermission(this.context.getBundle(), list);
		ServiceReference[] ref = getBundle.getServicesInUse();
		if (ref == null || ref.length == 1)
			return;

		fail("Succeed in getServicesInUse. It MUST fail.");
	}

	public void testGetServicesInUse5_2_1() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(vendor=NTT))", "GET");
		this.setBundlePermission(this.context.getBundle(), list);
		ServiceReference[] ref = getBundle.getServicesInUse();
		if (ref == null || ref.length != 1)
			fail("Fail to getServicesInUse(). It MUST succeed.");
	}

	public void testGetServicesInUse5_2_2() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1
				+ ")(vendor=something.else))", "GET");
		this.setBundlePermission(this.context.getBundle(), list);
		ServiceReference[] ref = getBundle.getServicesInUse();
		if (ref == null || ref.length == 1)
			return;

		fail("Succeed in getServicesInUse. It MUST fail.");
	}

	public void testGetServicesInUse5_2_3() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(name="
				+ this.registerPluralBundle.getSymbolicName() + "))", "GET");
		this.setBundlePermission(this.context.getBundle(), list);
		ServiceReference[] ref = getBundle.getServicesInUse();
		if (ref == null || ref.length != 1)
			fail("Fail to getServicesInUse(). It MUST succeed.");
	}

	public void testGetServicesInUse5_2_4() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, SP, "(&(objectClass=" + S_NAME_UTIL_ISERVICE1 + ")(name="
				+ "something.else" + "))", "GET");
		this.setBundlePermission(this.context.getBundle(), list);
		ServiceReference[] ref = getBundle.getServicesInUse();
		if (ref == null || ref.length == 1)
			return;

		fail("Succeed in getServicesInUse. It MUST fail.");
	}

	public void testGetServicesInUse5_2_5() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, SP, "(location=" + this.registerPluralBundle.getLocation()
				+ ")", "GET");
		this.setBundlePermission(this.context.getBundle(), list);
		ServiceReference[] ref = getBundle.getServicesInUse();
		if (ref == null || ref.length != 1)
			fail("Fail to getServicesInUse(). It MUST succeed.");
	}

	public void testGetServicesInUse5_2_6() throws Exception {
		this.startBundleAndCheckSecurityException(this.registerPluralBundle);
		assertFalse("Fail to register service. It MUST succeed.", exceptionFlag);
		this.startBundleAndCheckPermissionsFilterException(getBundle);
		assertFalse("Fail to get service. It MUST succeed.", exceptionFlag);
		add(list, PP, P_NAME_UTIL, "IMPORT, exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		add(list, AdminPermission.class.getName(), "*", "*");
		add(list, SP, "(location=" + "something.else" + ")", "GET");
		this.setBundlePermission(this.context.getBundle(), list);
		ServiceReference[] ref = getBundle.getServicesInUse();
		if (ref == null || ref.length == 1)
			return;

		fail("Succeed in getServicesInUse. It MUST fail.");
	}

	public void testExportPackage7_1_1() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		add(list, PP, P_NAME_SHARED, "export");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		checkExport1Succeed();
	}

	public void testExportPackage7_1_2() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		add(list, PP, "org.osgi.test.cases.permissions.*", "export");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		checkExport1Succeed();
	}

	public void testExportPackage7_1_3() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		add(list, PP, "org.osgi.*", "export");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		checkExport1Succeed();
	}

	public void testExportPackage7_1_4() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		add(list, PP, "*", "export");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		checkExport1Succeed();
	}

	private void checkExport1Succeed() {
		refreshPackagesAndResolveBundles(new Bundle[] {exportBundle1});
		if (exportBundle1.getState() != Bundle.RESOLVED)
			fail("Fail to export package. It MUST succeed.");

		printoutAllPkgs();
		ExportedPackage[] pkgs = pkgAdmin
				.getExportedPackages(this.exportBundle1);
		if (pkgs == null || pkgs.length != 1
				|| !pkgs[0].getName().equals(P_NAME_SHARED)) {
			fail("Fail to export package. It MUST succeed.");
		}

		Bundle[] importers = pkgs[0].getImportingBundles();
		if (importers == null || importers.length != 0)
			fail("There MUST be no importers.");
	}

	private void printoutAllPkgs() {
		Bundle b = null;
		ExportedPackage[] pkgsAll = pkgAdmin.getExportedPackages(b);
		for (int i = 0; i < pkgsAll.length; i++) {
			if (pkgsAll[i].getName().startsWith("org.osgi.test")) {
				System.out.println(pkgsAll[i]);
				System.out.println("Exporter="
						+ pkgsAll[i].getExportingBundle());
				Bundle[] importers = pkgsAll[0].getImportingBundles();
				for (int j = 0; j < importers.length; j++) {
					System.out.println("Importer[" + j + "]=" + importers[j]);
				}
			}
		}
	}

	public void testExportPackage7_2_1() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		add(list, PP, "something.else", "export");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		checkExport1Fail();
	}

	public void testExportPackage7_2_2() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		add(list, PP, "something.*", "export");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		checkExport1Fail();
	}

	private void checkExport1Fail() {
		refreshPackagesAndResolveBundles(new Bundle[] {exportBundle1});
		if (exportBundle1.getState() != Bundle.RESOLVED)
			fail("Fail to export package. It MUST succeed.");
		ExportedPackage[] pkgs = pkgAdmin
				.getExportedPackages(this.exportBundle1);
		if (pkgs != null)
			fail("Succeed in exporting package. It MUST fail.");
	}

	public void testExportPackage7_3_1() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		add(list, PP, P_NAME_SHARED, "exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		this.checkExport1Succeed();
	}

	public void testExportPackage7_3_2() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		add(list, PP, "org.osgi.test.cases.permissions.*", "exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		checkExport1Succeed();
	}

	public void testExportPackage7_3_3() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		add(list, PP, "org.osgi.*", "exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		checkExport1Succeed();
	}

	public void testExportPackage7_3_4() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		add(list, PP, "*", "exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		checkExport1Succeed();
	}

	public void testExportPackage7_4_1() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		add(list, PP, "something.else", "exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		checkExport1Fail();
	}

	public void testExportPackage7_4_2() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		add(list, PP, "something.*", "exportonly");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		checkExport1Fail();
	}

	public void testExportPackage7_5_1() throws Exception {
		this.resetBundles();
		// bundle=org.osgi.test.cases.permissions.filtered.exporter1_1.1.1 [10]
		// [Export-Package:
		// org.osgi.test.cases.permissions.filtered.sharedPkg;version="1.0.0"]
		// [Import-Package:
		// org.osgi.test.cases.permissions.filtered.sharedPkg;version="1.0"]
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);

		// bundle=org.osgi.test.cases.permissions.filtered.exporter2_2.2.2 [11]
		// [Export-Package:
		// org.osgi.test.cases.permissions.filtered.sharedPkg;version="2.0.0"]
		// [Import-Package:
		// org.osgi.test.cases.permissions.filtered.sharedPkg;version="2.0"]
		exportBundle2 = this.installBundle(EXPORT_BUNDLE_2_LOCATION);

		this.printoutHeader(exportBundle1);
		this.printoutHeader(exportBundle2);

		add(list, PP, "*", "exportonly");
		add(list, PP, "*", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		checkExport1Export2Succeed();
	}

	public void testExportPackage7_5_2() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		exportBundle2 = this.installBundle(EXPORT_BUNDLE_2_LOCATION);

		add(list, PP, P_NAME_SHARED, "exportonly");
		add(list, PP, "(&(package.name=" + P_NAME_SHARED + ")(name="
				+ this.exportBundle2.getSymbolicName() + "))", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		checkExport1Export2Succeed();
	}

	public void testExportPackage7_5_3() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		exportBundle2 = this.installBundle(EXPORT_BUNDLE_2_LOCATION);

		add(list, PP, P_NAME_SHARED, "exportonly");
		add(list, PP, "(&(package.name=" + P_NAME_SHARED + ")(name="
				+ this.exportBundle2.getSymbolicName() + "))", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);

		list.clear();
		add(list, PP, P_NAME_SHARED, "exportonly");
		add(list, PP, "(&(package.name=" + P_NAME_SHARED + ")(name="
				+ this.exportBundle1.getSymbolicName() + "))", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle2, list);

		checkExport1Export2Succeed();
	}

	public void testExportPackage7_5_4() throws Exception {
		this.resetBundles();

		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		exportBundle2 = this.installBundle(EXPORT_BUNDLE_2_LOCATION);

		add(list, PP, P_NAME_SHARED, "exportonly");
		add(list, PP, "(&(package.name=" + "something.else" + ")(name="
				+ this.exportBundle2.getSymbolicName() + "))", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		refreshPackagesAndResolveBundles(new Bundle[] {exportBundle1,
				exportBundle2});

		ExportedPackage[] pkgs3 = pkgAdmin
				.getExportedPackages(this.exportBundle1);
		ExportedPackage[] pkgs4 = pkgAdmin
				.getExportedPackages(this.exportBundle2);
		if (pkgs3 == null || pkgs3.length != 1
				|| !pkgs3[0].getName().equals(P_NAME_SHARED)) {
			fail("Fail to export package. It MUST succeed.");
		}
		if (pkgs3[0].getImportingBundles().length != 0)
			fail("It MUST not be imported.");

		if (pkgs4 == null || pkgs4.length != 1
				|| !pkgs4[0].getName().equals(P_NAME_SHARED)) {
			fail("Fail to export package. It MUST succeed.");
		}
		if (pkgs4[0].getImportingBundles().length != 0)
			fail("It MUST not be imported.");
		if (this.exportBundle1.getState() == Bundle.RESOLVED)
			return;
		fail("Fail to export package. It MUST succeed.");
	}

	public void testExportPackage7_5_5() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		exportBundle2 = this.installBundle(EXPORT_BUNDLE_2_LOCATION);

		add(list, PP, "something.else", "exportonly");
		add(list, PP, "(&(package.name=" + P_NAME_SHARED + ")(name="
				+ this.exportBundle2.getSymbolicName() + "))", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.exportBundle1, list);
		refreshPackagesAndResolveBundles(new Bundle[] {exportBundle1,
				exportBundle2});
		ExportedPackage[] pkgs3 = pkgAdmin
				.getExportedPackages(this.exportBundle1);
		ExportedPackage[] pkgs4 = pkgAdmin
				.getExportedPackages(this.exportBundle2);
		if (pkgs3 != null)
			fail("Succeed in exporting package. It MUST fail.");

		if (pkgs4 == null || pkgs4.length != 1
				|| !pkgs4[0].getName().equals(P_NAME_SHARED)) {
			fail("Fail to export package. It MUST succeed.");
		}
		Bundle[] importers4 = pkgs4[0].getImportingBundles();
		if (importers4 != null && importers4.length == 1
				&& importers4[0].equals(this.exportBundle1))
			return;
		fail("Fail to export package. It MUST succeed.");

	}

	private void checkExport1Export2Succeed() {
		refreshPackagesAndResolveBundles(new Bundle[] {exportBundle1,
				exportBundle2});

		if (this.exportBundle1.getState() != Bundle.RESOLVED)
			fail("Fail to resolve exportBundle1");
		if (this.exportBundle2.getState() != Bundle.RESOLVED)
			fail("Fail to resolve exportBundle2");

		ExportedPackage[] pkgs1 = pkgAdmin
				.getExportedPackages(this.exportBundle1);
		ExportedPackage[] pkgs2 = pkgAdmin
				.getExportedPackages(this.exportBundle2);
		if (pkgs1 == null || pkgs1.length != 1
				|| !pkgs1[0].getName().equals(P_NAME_SHARED)) {
			fail("Fail to export package. It MUST succeed.");
		}
		if (pkgs1[0].getImportingBundles().length != 0)
			fail("It MUST not be imported.");

		if (pkgs2 == null || pkgs2.length != 1
				|| !pkgs2[0].getName().equals(P_NAME_SHARED)) {
			fail("Fail to export package. It MUST succeed.");
		}
		Bundle[] importers2 = pkgs2[0].getImportingBundles();
		if (importers2 != null && importers2.length == 1
				&& importers2[0].equals(this.exportBundle1))
			return;
		fail("Fail to export package. It MUST succeed.");
	}

	private void refreshPackagesAndResolveBundles(Bundle[] bundles) {
		pkgAdmin.refreshPackages(bundles);
		pkgAdmin.resolveBundles(bundles);
		sleep();
	}

	private void printoutHeader(Bundle bundle) {
		Dictionary headers = bundle.getHeaders();
		System.out.println("bundle=" + bundle);

		for (Enumeration keys = headers.keys(); keys.hasMoreElements();) {
			Object key = keys.nextElement();
			if (key.equals(Constants.IMPORT_PACKAGE)
					|| key.equals(Constants.EXPORT_PACKAGE)
					|| key.equals(Constants.BUNDLE_SYMBOLICNAME))
				System.out.println("\t[" + key + ": " + headers.get(key) + "]");
		}

	}

	// ///////////////////////////////////////////////////////

	public void testImportPackage8_1_1() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, P_NAME_SHARED, "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Succeed();
	}

	public void testImportPackage8_1_2() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "org.osgi.test.cases.permissions.filtered.*", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Succeed();
	}

	public void testImportPackage8_1_3() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "org.osgi.test.cases.*", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Succeed();
	}

	public void testImportPackage8_1_4() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "*", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Succeed();
	}

	public void testImportPackage8_2_1() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "(package.name=" + P_NAME_SHARED + ")", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Succeed();
	}

	public void testImportPackage8_2_2() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "(package.name=org.osgi.test.cases.permissions.*)",
				"import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Succeed();
	}

	public void testImportPackage8_2_3() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "(package.name=org.osgi.*)", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Succeed();
	}

	public void testImportPackage8_2_4() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "(package.name=*)", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Succeed();
	}

	private void checkExport1SucceedImport1Succeed() throws BundleException,
			IOException {
		refreshPackagesAndResolveBundles(new Bundle[] {exportBundle1,
				importBundle1});
		this.printoutHeader(exportBundle1);
		this.printoutHeader(importBundle1);

		if (importBundle1.getState() != Bundle.RESOLVED)
			fail("Fail to import package. It MUST succeed.");
		ExportedPackage[] pkgs = pkgAdmin
				.getExportedPackages(this.exportBundle1);
		if (pkgs == null || pkgs.length != 1
				|| !pkgs[0].getName().equals(P_NAME_SHARED)) {
			fail("Fail to export package. It MUST succeed.");
		}

		Bundle[] importers = pkgs[0].getImportingBundles();
		boolean flag = false;
		if (importers != null) {
			for (int i = 0; i < importers.length; i++) {
				if (importers[0].equals(this.importBundle1)) {
					flag = true;
					break;
				}
			}
		}

		if (!flag)
			fail("Fail to import package. It MUST succeed.");
	}

	public void testImportPackage8_3_1() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "something.else", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Fail();
	}

	public void testImportPackage8_3_2() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "something.*", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Fail();
	}

	public void testImportPackage8_3_3() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "(package.name=something.else)", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Fail();
	}

	public void testImportPackage8_3_4() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "(package.name=something.*)", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Fail();
	}

	private void checkExport1SucceedImport1Fail() {
		refreshPackagesAndResolveBundles(new Bundle[] {exportBundle1,
				importBundle1});
		if (importBundle1.getState() != Bundle.INSTALLED)
			fail("Succeed in importing package. It MUST fail.");
		ExportedPackage[] pkgs = pkgAdmin
				.getExportedPackages(this.exportBundle1);
		if (pkgs == null || pkgs.length != 1
				|| !pkgs[0].getName().equals(P_NAME_SHARED)) {
			fail("Fail to export package. It MUST succeed.");
		}

		Bundle[] importers = pkgs[0].getImportingBundles();
		boolean flag = false;
		if (importers != null) {
			for (int i = 0; i < importers.length; i++) {
				if (importers[0].equals(this.importBundle1)) {
					flag = true;
					break;
				}
			}
		}
		if (flag)
			fail("Succeed in importing package. It MUST fail.");
	}

	public void testImportPackage8_4_1() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "(name=" + this.exportBundle1.getSymbolicName() + ")",
				"import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Succeed();
	}

	public void testImportPackage8_4_2() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "(id=" + this.exportBundle1.getBundleId() + ")", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Succeed();
	}

	public void testImportPackage8_4_3() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "(location=" + this.exportBundle1.getLocation() + ")",
				"import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Succeed();
	}

	public void testImportPackage8_4_4() throws Exception {
		// TODO implement signer filtering.
	}

	public void testImportPackage8_5_1() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "(name=" + "something.else" + ")", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);
		checkExport1SucceedImport1Fail();
	}

	public void testImportPackage8_5_2() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "(id=" + this.context.getBundle().getBundleId() + ")",
				"import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);

		checkExport1SucceedImport1Fail();
	}

	public void testImportPackage8_5_3() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle1 = this.installBundle(IMPORT_BUNDLE_1_LOCATION);
		add(list, PP, "(location=" + "something.else" + ")", "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle1, list);

		checkExport1SucceedImport1Fail();
	}

	public void testImportPackage8_5_4() throws Exception {
		// TODO implement signer filtering.
	}

	public void testImportPackage8_6_1() throws Exception {
		this.resetBundles();
		exportBundle1 = this.installBundle(EXPORT_BUNDLE_1_LOCATION);
		importBundle2 = this.installBundle(IMPORT_BUNDLE_2_LOCATION);
		add(list, PP, P_NAME_SHARED, "import");
		add(list, PP, "org.osgi.framework", "IMPORT");
		this.setBundlePermission(this.importBundle2, list);
		checkExport1SucceedImport2Fail();
	}

	private void checkExport1SucceedImport2Fail() throws BundleException,
			IOException {
		refreshPackagesAndResolveBundles(new Bundle[] {exportBundle1,
				importBundle2});

		if (importBundle2.getState() != Bundle.INSTALLED)
			fail("It must be INSTALLED(" + Bundle.INSTALLED + "). state="
					+ importBundle2.getState());
		ExportedPackage[] pkgs = pkgAdmin
				.getExportedPackages(this.exportBundle1);
		if (pkgs == null || pkgs.length != 1
				|| !pkgs[0].getName().equals(P_NAME_SHARED)) {
			fail("Fail to export package. It MUST succeed.");
		}

		Bundle[] importers = pkgs[0].getImportingBundles();
		if (importers.length != 0)
			fail("There MUST be no bundle who imports the package.");
	}

	/*
	 * -----------------------------------------
	 * 
	 * Utility methods.
	 */

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

	private void setBundlePermission(Bundle b, List list) {
		PermissionInfo[] pis = new PermissionInfo[list.size()];
		pis = (PermissionInfo[]) list.toArray(pis);
		permAdmin.setPermissions(b.getLocation(), pis);
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
		be.printStackTrace();
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

	private PackageAdmin getPackageAdmin() {
		ServiceReference ref = context.getServiceReference(PackageAdmin.class
				.getName());
		if (ref == null)
			throw new IllegalStateException("Fail to get ServiceReference of "
					+ PackageAdmin.class.getName());

		PackageAdmin packageAdmin = (PackageAdmin) context.getService(ref);
		return packageAdmin;
	}

	private PermissionAdmin getPermissionAdmin() {
		ServiceReference ref = context
				.getServiceReference(PermissionAdmin.class.getName());
		if (ref == null)
			throw new IllegalStateException("Fail to get ServiceReference of "
					+ PermissionAdmin.class.getName());

		PermissionAdmin permissionAdmin = (PermissionAdmin) context
				.getService(ref);
		return permissionAdmin;
	}

	private void add(List list, String clazz, String name, String actions) {
		list.add(new PermissionInfo(clazz, name, actions));
	}

	private void setBasePermissions() {
		Bundle[] bundles = context.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			this.setAllpermission(bundles[i].getLocation());
		}
		permAdmin
				.setDefaultPermissions(new PermissionInfo[] {new PermissionInfo(
						"java.util.PropertyPermission", "java.home", "read")});

	}

}

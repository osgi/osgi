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
package org.osgi.test.cases.permissionadmin.junit;

import java.io.InputStream;
import java.net.URL;
import java.util.Vector;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.service.startlevel.StartLevel;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.compatibility.MethodCall;

/**
 * Contains the test methods of the permission signature test case.
 * 
 * @author Petia Sotirova
 * @version 1.1
 */
public class PermissionSignatureTestControl extends DefaultTestBundleControl {

	private PermissionSignatureTBCService	tbc;
	private PermissionAdmin					permissionAdmin;

	private Bundle							testBundle;
	private String							testBundleLocation;

	private Bundle							testSignatureBundle;
	private String							signatureBundleLocation;

	private String							signatureBundleName;

	private PermissionSignatureUtility		utility;

	private String							resourceName;
	private String							resourcesName;
	private String							entryName;
	private String							entryPath;
	private String							className;
	private String							extensionEntryName;
	private String							extensionEntryPath;
	private String							extensionClassName;

	private int								startLevel				= 10;
	private int								initialBundleStartLevel	= 10;
	private boolean							checkStartLevel			= false;

	private String							extensionBundleName;

	protected void setUp() throws Exception {
		assertNotNull("Must have a security manager", System
				.getSecurityManager());
		permissionAdmin = (PermissionAdmin) getService(PermissionAdmin.class);
		assertNotNull(permissionAdmin);

		testBundle = installBundle("tb1.jar", false);
		testBundleLocation = testBundle.getLocation();
		permissionAdmin.setPermissions(testBundleLocation, null);
		testBundle.start();

		signatureBundleName = SignatureResource.getString("bundle.name");
		resourceName = SignatureResource.getString("resource.name");
		resourcesName = SignatureResource.getString("resources.name");
		entryName = SignatureResource.getString("entry.name");
		entryPath = SignatureResource.getString("entry.path");
		className = SignatureResource.getString("load.class");

		extensionEntryName = SignatureResource
				.getString("extension.entry.name");
		extensionEntryPath = SignatureResource
				.getString("extension.entry.path");
		extensionClassName = SignatureResource
				.getString("extension.load.class");

		extensionBundleName = SignatureResource
				.getString("extensionBundle.name");

		tbc = (PermissionSignatureTBCService) getService(PermissionSignatureTBCService.class);
		StartLevel startLevelService = (StartLevel) getService(StartLevel.class);
		if (startLevelService != null) {
			startLevel = startLevelService.getStartLevel();
			initialBundleStartLevel = startLevelService
					.getInitialBundleStartLevel();
			checkStartLevel = true;
		}

		utility = new PermissionSignatureUtility(this, tbc);

		testSignatureBundle = installBundle(signatureBundleName, false);
		signatureBundleLocation = testSignatureBundle.getLocation();
		permissionAdmin.setPermissions(signatureBundleLocation, null);
		testSignatureBundle.start();
	}

	protected void tearDown() throws Exception {
		log("#after each run");
		testSignatureBundle.uninstall();
		permissionAdmin.setPermissions(signatureBundleLocation, null);
		testBundle.uninstall();
		permissionAdmin.setPermissions(testBundleLocation, null);
		ungetAllServices();
	}

	/**
	 * Tests AdminPermission. Checks if a bundle with NoPermission can not
	 * execute anything that requires AdminPermission.
	 */
	public void testPermission() throws Throwable {
		permissionAdmin.setPermissions(testBundleLocation,
				new PermissionInfo[0]);
		printPermissions(testBundleLocation);
		String message = "";

		// Each bundle must be given AdminPermission(<bundle id>, "resource") so
		// that it can access it's own resources.
		utility.allowed_Bundle_getResource(message, testBundle, resourceName);
		utility.allowed_Bundle_getResources(message, testBundle, resourcesName);
		utility.allowed_Bundle_getEntry(message, testBundle, resourceName);
		utility.allowed_Bundle_getEntryPaths(message, testBundle, "/META-INF/");

		utility.not_allowed_Bundle_getHeaders(message, testSignatureBundle);
		utility.not_allowed_Bundle_getHeaders_byLocation(message,
				testSignatureBundle);
		utility.not_allowed_Bundle_getLocation(message, testSignatureBundle);
		utility.not_allowed_Bundle_getResource(message, testSignatureBundle,
				resourceName);
		utility.not_allowed_Bundle_getResources(message, testSignatureBundle,
				resourcesName);
		utility.not_allowed_Bundle_getEntry(message, testSignatureBundle,
				entryName);
		utility.not_allowed_Bundle_getEntryPaths(message, testSignatureBundle,
				entryPath);
		if (checkStartLevel) {
			utility.not_allowed_StartLevel_setBundleStartLevel(message,
					testSignatureBundle, startLevel);
			utility.not_allowed_StartLevel_setStartLevel(message, startLevel);
			utility.not_allowed_StartLevel_setInitialBundleStartLevel(message,
					initialBundleStartLevel);
		}
		utility.not_allowed_Bundle_loadClass(message, testSignatureBundle,
				className);
		utility.not_allowed_Bundle_stop(message, testSignatureBundle);
		utility.not_allowed_Bundle_start(message, testSignatureBundle);
		utility.not_allowed_Bundle_update(message, testSignatureBundle);
		utility.not_allowed_Bundle_update_by_InputStream(message,
				testSignatureBundle, getInputStream(signatureBundleName));
		utility.not_allowed_Bundle_uninstall(message, testSignatureBundle);
		utility.not_allowed_BundleContext_addBundleListener(message,
				testSignatureBundle);
		utility.not_allowed_BundleContext_removeBundleListener(message,
				testSignatureBundle);
		utility.not_allowed_PackageAdmin_refreshPackages(message,
				new Bundle[] {testSignatureBundle});
		utility.not_allowed_PackageAdmin_resolveBundles(message,
				new Bundle[] {testSignatureBundle});
		utility.not_allowed_PermissionAdmin_setPermissions(message,
				signatureBundleLocation,
				new PermissionInfo[] {new PermissionInfo(AdminPermission.class
						.getName(), "*", "*")});
		utility.not_allowed_PermissionAdmin_setDefaultPermissions(message,
				permissionAdmin.getDefaultPermissions());
	}

	/**
	 * Tests AdminPermission with all actions allowed on all. Checks if a bundle
	 * with AllPermission can execute all methods requiring AdminPermission.
	 */
	public void testAdminPermission() throws Throwable {
		PermissionInfo info = new PermissionInfo(AdminPermission.class
				.getName(), "*", "*");
		permissionAdmin.setPermissions(testBundleLocation,
				new PermissionInfo[] {info});
		printPermissions(testBundleLocation);
		String message = "";

		utility.allowed_Bundle_getHeaders(message, testSignatureBundle);
		utility.allowed_Bundle_getHeaders_byLocation(message,
				testSignatureBundle);
		utility.allowed_Bundle_getLocation(message, testSignatureBundle);
		utility.allowed_Bundle_getResource(message, testSignatureBundle,
				resourceName);
		utility.allowed_Bundle_getResources(message, testSignatureBundle,
				resourcesName);
		utility
				.allowed_Bundle_getEntry(message, testSignatureBundle,
						entryName);
		utility.allowed_Bundle_getEntryPaths(message, testSignatureBundle,
				entryPath);
		if (checkStartLevel) {
			utility.allowed_StartLevel_setStartLevel(message, startLevel);
			utility.allowed_StartLevel_setInitialBundleStartLevel(message,
					initialBundleStartLevel);
		}
		utility.allowed_Bundle_loadClass(message, testSignatureBundle,
				className);
		utility.allowed_Bundle_stop(message, testSignatureBundle);
		utility.allowed_Bundle_start(message, testSignatureBundle);
		utility.allowed_Bundle_update(message, testSignatureBundle);
		utility.allowed_Bundle_update_by_InputStream(message,
				testSignatureBundle, getInputStream(signatureBundleName));

		utility.allowed_BundleContext_addBundleListener(message,
				testSignatureBundle);
		utility.allowed_BundleContext_removeBundleListener(message,
				testSignatureBundle);
		utility.not_allowed_PermissionAdmin_setPermissions(message,
				signatureBundleLocation,
				new PermissionInfo[] {new PermissionInfo(AdminPermission.class
						.getName(), "*", "*")});
		utility.not_allowed_PermissionAdmin_setDefaultPermissions(message,
				permissionAdmin.getDefaultPermissions());
		utility.allowed_PackageAdmin_resolveBundles(message,
				new Bundle[] {testSignatureBundle});
		utility.allowed_PackageAdmin_refreshPackages(message,
				new Bundle[] {testSignatureBundle});

	}

	/**
	 * Tests AdminPermission with an action parameter - metadata. Checks if a
	 * bundle with AdminPrmission - metadata can execute: - Bundle.getHeaders -
	 * Bundle.getLocation and can not execute anything else that requires other
	 * AdminPermission.
	 * 
	 * The bundle is specified either by bundle id or by filter string.
	 */
	public void testAdminPermissionMetadata() throws Throwable {
		String message = "";
		Vector permissions = utility.getPInfosForAdminPermisssion(
				AdminPermission.METADATA, testSignatureBundle.getBundleId(),
				testSignatureBundle.getLocation(), testSignatureBundle
						.getSymbolicName());

		PermissionInfo info;
		for (int i = 0; i < permissions.size(); ++i) {
			info = (PermissionInfo) permissions.elementAt(i);
			permissionAdmin.setPermissions(testBundleLocation,
					new PermissionInfo[] {info});
			printPermissions(testBundleLocation);

			utility.allowed_Bundle_getHeaders(message, testSignatureBundle);
			utility.allowed_Bundle_getHeaders_byLocation(message,
					testSignatureBundle);
			utility.allowed_Bundle_getLocation(message, testSignatureBundle);

			utility.not_allowed_Bundle_getResource(message,
					testSignatureBundle, resourceName);
			utility.not_allowed_Bundle_getResources(message,
					testSignatureBundle, resourcesName);
			utility.not_allowed_Bundle_getEntry(message, testSignatureBundle,
					entryName);
			utility.not_allowed_Bundle_getEntryPaths(message,
					testSignatureBundle, entryPath);

			if (checkStartLevel) {
				utility.not_allowed_StartLevel_setBundleStartLevel(message,
						testSignatureBundle, startLevel);
				utility.not_allowed_StartLevel_setStartLevel(message,
						startLevel);
				utility.not_allowed_StartLevel_setInitialBundleStartLevel(
						message, initialBundleStartLevel);
			}
			utility.not_allowed_Bundle_loadClass(message, testSignatureBundle,
					className);
			utility.not_allowed_Bundle_stop(message, testSignatureBundle);
			utility.not_allowed_Bundle_start(message, testSignatureBundle);
			utility.not_allowed_Bundle_update(message, testSignatureBundle);
			utility.not_allowed_Bundle_update_by_InputStream(message,
					testSignatureBundle, getInputStream(signatureBundleName));
			utility.not_allowed_Bundle_uninstall(message, testSignatureBundle);
			utility.not_allowed_BundleContext_addBundleListener(message,
					testSignatureBundle);
			utility.not_allowed_BundleContext_removeBundleListener(message,
					testSignatureBundle);
			utility.not_allowed_PackageAdmin_refreshPackages(message,
					new Bundle[] {testSignatureBundle});
			utility.not_allowed_PackageAdmin_resolveBundles(message,
					new Bundle[] {testSignatureBundle});
		}
	}

	/**
	 * Tests AdminPermission with an action parameter - resource. Checks if a
	 * bundle with AdminPrmission - resource can execute: - Bundle.getResource -
	 * Bundle.getEntry - Bundle.getEntryPaths and can not execute anything else
	 * that requires other AdminPermission.
	 * 
	 * The bundle is specified either by bundle id or by filter string.
	 */
	public void testAdminPermissionResource() throws Throwable {
		String message = "";
		Vector permissions = utility.getPInfosForAdminPermisssion(
				AdminPermission.RESOURCE, testSignatureBundle.getBundleId(),
				testSignatureBundle.getLocation(), testSignatureBundle
						.getSymbolicName());

		PermissionInfo info;
		for (int i = permissions.size() - 1; i >= 0; i--) {
			info = (PermissionInfo) permissions.elementAt(i);

			permissionAdmin.setPermissions(testBundleLocation,
					new PermissionInfo[] {info});
			printPermissions(testBundleLocation);

			utility.allowed_Bundle_getResource(message, testSignatureBundle,
					resourceName);
			utility.allowed_Bundle_getResources(message, testSignatureBundle,
					resourcesName);
			utility.allowed_Bundle_getEntry(message, testSignatureBundle,
					entryName);
			utility.allowed_Bundle_getEntryPaths(message, testSignatureBundle,
					entryPath);


			utility.not_allowed_Bundle_getHeaders(message, testSignatureBundle);
			utility.not_allowed_Bundle_getHeaders_byLocation(message,
					testSignatureBundle);
			utility
					.not_allowed_Bundle_getLocation(message,
							testSignatureBundle);

			if (checkStartLevel) {
				utility.not_allowed_StartLevel_setBundleStartLevel(message,
						testSignatureBundle, startLevel);
				utility.not_allowed_StartLevel_setStartLevel(message,
						startLevel);
				utility.not_allowed_StartLevel_setInitialBundleStartLevel(
						message, initialBundleStartLevel);
			}
			utility.not_allowed_Bundle_loadClass(message, testSignatureBundle,
					className);
			utility.not_allowed_Bundle_stop(message, testSignatureBundle);
			utility.not_allowed_Bundle_start(message, testSignatureBundle);
			utility.not_allowed_Bundle_update(message, testSignatureBundle);
			utility.not_allowed_Bundle_update_by_InputStream(message,
					testSignatureBundle, getInputStream(signatureBundleName));
			utility.not_allowed_Bundle_uninstall(message, testSignatureBundle);
			utility.not_allowed_BundleContext_addBundleListener(message,
					testSignatureBundle);
			utility.not_allowed_BundleContext_removeBundleListener(message,
					testSignatureBundle);
			utility.not_allowed_PermissionAdmin_setPermissions(message,
					signatureBundleLocation,
					new PermissionInfo[] {new PermissionInfo(
							AdminPermission.class.getName(), "*", "*")});
			utility.not_allowed_PermissionAdmin_setDefaultPermissions(message,
					permissionAdmin.getDefaultPermissions());

			// note that in R4.2 a change was made to make resource imply
			// resolve
			if (i == 0) {
				utility.allowed_PackageAdmin_resolveBundles(message,
						new Bundle[] {testSignatureBundle});
				utility.allowed_PackageAdmin_refreshPackages(message,
						new Bundle[] {testSignatureBundle});
			}
			else {
				utility.not_allowed_PackageAdmin_resolveBundles(message,
						new Bundle[] {testSignatureBundle});
				utility.not_allowed_PackageAdmin_refreshPackages(message,
						new Bundle[] {testSignatureBundle});
			}
		}
	}

	/**
	 * Tests AdminPermission with an action parameter - Class. Checks if a
	 * bundle with AdminPrmission - Class can execute: - Bundle.loadClass and
	 * can not execute anything else that requires other AdminPermission.
	 * 
	 * The bundle is specified either by bundle id or by filter string.
	 */
	public void testAdminPermissionClass() throws Throwable {
		String message = "";
		Vector permissions = utility.getPInfosForAdminPermisssion(
				AdminPermission.CLASS, testSignatureBundle.getBundleId(),
				testSignatureBundle.getLocation(), testSignatureBundle
						.getSymbolicName());

		PermissionInfo info;
		for (int i = permissions.size() - 1; i >= 0; i--) {
			info = (PermissionInfo) permissions.elementAt(i);

			permissionAdmin.setPermissions(testBundleLocation,
					new PermissionInfo[] {info});
			printPermissions(testBundleLocation);

			utility.allowed_Bundle_loadClass(message, testSignatureBundle,
					className);

			utility.not_allowed_Bundle_getHeaders(message, testSignatureBundle);
			utility.not_allowed_Bundle_getHeaders_byLocation(message,
					testSignatureBundle);
			utility
					.not_allowed_Bundle_getLocation(message,
							testSignatureBundle);
			utility.not_allowed_Bundle_getResource(message,
					testSignatureBundle, resourceName);
			utility.not_allowed_Bundle_getResources(message,
					testSignatureBundle, resourcesName);
			utility.not_allowed_Bundle_getEntry(message, testSignatureBundle,
					entryName);
			utility.not_allowed_Bundle_getEntryPaths(message,
					testSignatureBundle, entryPath);
			if (checkStartLevel) {
				utility.not_allowed_StartLevel_setBundleStartLevel(message,
						testSignatureBundle, startLevel);
				utility.not_allowed_StartLevel_setStartLevel(message,
						startLevel);
				utility.not_allowed_StartLevel_setInitialBundleStartLevel(
						message, initialBundleStartLevel);
			}
			utility.not_allowed_Bundle_stop(message, testSignatureBundle);
			utility.not_allowed_Bundle_start(message, testSignatureBundle);
			utility.not_allowed_Bundle_update(message, testSignatureBundle);
			utility.not_allowed_Bundle_update_by_InputStream(message,
					testSignatureBundle, getInputStream(signatureBundleName));
			utility.not_allowed_Bundle_uninstall(message, testSignatureBundle);
			utility.not_allowed_BundleContext_addBundleListener(message,
					testSignatureBundle);
			utility.not_allowed_BundleContext_removeBundleListener(message,
					testSignatureBundle);
			utility.not_allowed_PermissionAdmin_setPermissions(message,
					signatureBundleLocation,
					new PermissionInfo[] {new PermissionInfo(
							AdminPermission.class.getName(), "*", "*")});
			utility.not_allowed_PermissionAdmin_setDefaultPermissions(message,
					permissionAdmin.getDefaultPermissions());

			// note that in R4.2 a change was made to make class imply
			// resolve
			if (i == 0) {
				utility.allowed_PackageAdmin_resolveBundles(message,
						new Bundle[] {testSignatureBundle});
				utility.allowed_PackageAdmin_refreshPackages(message,
						new Bundle[] {testSignatureBundle});
			}
			else {
				utility.not_allowed_PackageAdmin_resolveBundles(message,
						new Bundle[] {testSignatureBundle});
				utility.not_allowed_PackageAdmin_refreshPackages(message,
						new Bundle[] {testSignatureBundle});
			}
		}

	}

	/**
	 * Tests AdminPermission with an action parameter - lifecycle. Checks if a
	 * bundle with AdminPrmission - lifecycle can execute: -
	 * BundleContext.installBundle - Bundle.update - Bundle.uninstall and can
	 * not execute anything else that requires other AdminPermission.
	 * 
	 * The bundle is specified either by bundle id or by filter string.
	 */
	public void testAdminPermissionLifecycle() throws Throwable {
		String message = "";
		Vector permissions = utility.getPInfosForAdminPermisssion(
				AdminPermission.LIFECYCLE, testSignatureBundle.getBundleId(),
				testSignatureBundle.getLocation(), testSignatureBundle
						.getSymbolicName());

		PermissionInfo info;
		for (int i = 0; i < permissions.size(); ++i) {
			info = (PermissionInfo) permissions.elementAt(i);

			permissionAdmin.setPermissions(testBundleLocation,
					new PermissionInfo[] {info});
			printPermissions(testBundleLocation);

			utility.not_allowed_Bundle_getHeaders(message, testSignatureBundle);
			utility.not_allowed_Bundle_getHeaders_byLocation(message,
					testSignatureBundle);
			utility
					.not_allowed_Bundle_getLocation(message,
							testSignatureBundle);
			utility.not_allowed_Bundle_getResource(message,
					testSignatureBundle, resourceName);
			utility.not_allowed_Bundle_getResources(message,
					testSignatureBundle, resourcesName);
			utility.not_allowed_Bundle_getEntry(message, testSignatureBundle,
					entryName);
			utility.not_allowed_Bundle_getEntryPaths(message,
					testSignatureBundle, entryPath);
			if (checkStartLevel) {
				utility.not_allowed_StartLevel_setBundleStartLevel(message,
						testSignatureBundle, startLevel);
				utility.not_allowed_StartLevel_setStartLevel(message,
						startLevel);
				utility.not_allowed_StartLevel_setInitialBundleStartLevel(
						message, initialBundleStartLevel);
			}
			utility.not_allowed_Bundle_loadClass(message, testSignatureBundle,
					className);
			utility.not_allowed_Bundle_stop(message, testSignatureBundle);
			utility.not_allowed_Bundle_start(message, testSignatureBundle);
			utility.not_allowed_BundleContext_addBundleListener(message,
					testSignatureBundle);
			utility.not_allowed_BundleContext_removeBundleListener(message,
					testSignatureBundle);
			utility.not_allowed_PackageAdmin_refreshPackages(message,
					new Bundle[] {testSignatureBundle});
			utility.not_allowed_PackageAdmin_resolveBundles(message,
					new Bundle[] {testSignatureBundle});
			utility.not_allowed_PermissionAdmin_setPermissions(message,
					signatureBundleLocation,
					new PermissionInfo[] {new PermissionInfo(
							AdminPermission.class.getName(), "*", "*")});
			utility.not_allowed_PermissionAdmin_setDefaultPermissions(message,
					permissionAdmin.getDefaultPermissions());
		}
	}

	/**
	 * Tests AdminPermission with an action parameter - lifecycle. Checks if a
	 * bundle with AdminPrmission - lifecycle can execute: -
	 * BundleContext.installBundle - Bundle.update - Bundle.uninstall and can
	 * not execute anything else that requires other AdminPermission.
	 * 
	 * The bundle is specified either by bundle id or by filter string.
	 */
	public void testAdminPermissionExtensionLifecycle() throws Throwable {
		String message = "";
		String extensionBundleLocation = getInstallBundleLocation(extensionBundleName);

		Vector permissions = utility.getPInfosForAdminPermisssion(
				AdminPermission.EXTENSIONLIFECYCLE + ','
						+ AdminPermission.LIFECYCLE, -1, // extension bundle is
				// not yet installed
				extensionBundleLocation, // ?
				null);

		PermissionInfo info;
		for (int i = 0; i < permissions.size(); ++i) {
			info = (PermissionInfo) permissions.elementAt(i);

			permissionAdmin.setPermissions(testBundleLocation,
					new PermissionInfo[] {info});
			printPermissions(testBundleLocation);

			Bundle extensionBundle = (Bundle) utility
					.allowed_BundleContext_installBundle(message,
							testSignatureBundle,
							extensionBundleLocation);
			utility.allowed_Bundle_update(message, extensionBundle);
			utility.allowed_Bundle_update_by_InputStream(message,
					extensionBundle, getInputStream(extensionBundleName));

			utility.not_allowed_Bundle_getHeaders(message, extensionBundle);
			utility.not_allowed_Bundle_getHeaders_byLocation(message,
					extensionBundle);
			utility.not_allowed_Bundle_getLocation(message, extensionBundle);

			utility.not_allowed_Bundle_getResource(message, extensionBundle,
					resourceName);
			utility.not_allowed_Bundle_getResources(message, extensionBundle,
					resourcesName);
			utility.not_allowed_Bundle_getEntry(message, extensionBundle,
					extensionEntryName);
			utility.not_allowed_Bundle_getEntryPaths(message, extensionBundle,
					extensionEntryPath);

			if (checkStartLevel) {
				utility.not_allowed_StartLevel_setBundleStartLevel(message,
						extensionBundle, startLevel);
				utility.not_allowed_StartLevel_setStartLevel(message,
						startLevel);
				utility.not_allowed_StartLevel_setInitialBundleStartLevel(
						message, initialBundleStartLevel);
			}

			utility.not_allowed_Bundle_loadClass(message, extensionBundle,
					extensionClassName);

			utility.not_allowed_Bundle_stop(message, extensionBundle);
			utility.not_allowed_Bundle_start(message, extensionBundle);
			utility.not_allowed_BundleContext_addBundleListener(message,
					testSignatureBundle);
			utility.not_allowed_BundleContext_removeBundleListener(message,
					testSignatureBundle);
			utility.not_allowed_PackageAdmin_refreshPackages(message,
					new Bundle[] {testSignatureBundle});
			utility.not_allowed_PackageAdmin_resolveBundles(message,
					new Bundle[] {testSignatureBundle});
			utility.not_allowed_PermissionAdmin_setPermissions(message,
					extensionBundleLocation,
					new PermissionInfo[] {new PermissionInfo(
							AdminPermission.class.getName(), "*", "*")});
			utility.not_allowed_PermissionAdmin_setDefaultPermissions(message,
					permissionAdmin.getDefaultPermissions());

			utility.allowed_Bundle_uninstall(message, extensionBundle);
		}
	}

	/**
	 * Tests AdminPermission with an action parameter - execute. Checks if a
	 * bundle with AdminPrmission - execute can execute: - Bundle.start -
	 * Bundle.stop - StartLevel.setBundleStartLevel and can not execute anything
	 * else that requires other AdminPermission.
	 * 
	 * The bundle is specified either by bundle id or by filter string.
	 */
	public void testAdminPermissionExecute() throws Throwable {
		String message = "";
		Vector permissions = utility.getPInfosForAdminPermisssion(
				AdminPermission.EXECUTE, testSignatureBundle.getBundleId(),
				testSignatureBundle.getLocation(), testSignatureBundle
						.getSymbolicName());

		PermissionInfo info;
		for (int i = permissions.size() - 1; i >= 0; i--) {
			info = (PermissionInfo) permissions.elementAt(i);

			permissionAdmin.setPermissions(testBundleLocation,
					new PermissionInfo[] {info});
			printPermissions(testBundleLocation);

			utility.allowed_Bundle_stop(message, testSignatureBundle);
			utility.allowed_Bundle_start(message, testSignatureBundle);

			utility.not_allowed_Bundle_getHeaders(message, testSignatureBundle);
			utility.not_allowed_Bundle_getHeaders_byLocation(message,
					testSignatureBundle);
			utility
					.not_allowed_Bundle_getLocation(message,
							testSignatureBundle);
			utility.not_allowed_Bundle_getResource(message,
					testSignatureBundle, resourceName);
			utility.not_allowed_Bundle_getResources(message,
					testSignatureBundle, resourcesName);
			utility.not_allowed_Bundle_getEntry(message, testSignatureBundle,
					entryName);
			utility.not_allowed_Bundle_getEntryPaths(message,
					testSignatureBundle, entryPath);
			if (checkStartLevel) {
				utility.not_allowed_StartLevel_setStartLevel(message,
						startLevel);
				utility.not_allowed_StartLevel_setInitialBundleStartLevel(
						message, initialBundleStartLevel);
			}
			utility.not_allowed_Bundle_loadClass(message, testSignatureBundle,
					className);
			utility.not_allowed_Bundle_update(message, testSignatureBundle);
			utility.not_allowed_Bundle_update_by_InputStream(message,
					testSignatureBundle, getInputStream(signatureBundleName));
			utility.not_allowed_Bundle_uninstall(message, testSignatureBundle);
			utility.not_allowed_BundleContext_addBundleListener(message,
					testSignatureBundle);
			utility.not_allowed_BundleContext_removeBundleListener(message,
					testSignatureBundle);
			utility.not_allowed_PermissionAdmin_setPermissions(message,
					signatureBundleLocation,
					new PermissionInfo[] {new PermissionInfo(
							AdminPermission.class.getName(), "*", "*")});
			utility.not_allowed_PermissionAdmin_setDefaultPermissions(message,
					permissionAdmin.getDefaultPermissions());

			// note that in R4.2 a change was made to make execute imply
			// resolve
			if (i == 0) {
				utility.allowed_PackageAdmin_resolveBundles(message,
						new Bundle[] {testSignatureBundle});
				utility.allowed_PackageAdmin_refreshPackages(message,
						new Bundle[] {testSignatureBundle});
			}
			else {
				utility.not_allowed_PackageAdmin_resolveBundles(message,
						new Bundle[] {testSignatureBundle});
				utility.not_allowed_PackageAdmin_refreshPackages(message,
						new Bundle[] {testSignatureBundle});
			}
		}
	}

	/**
	 * Tests AdminPermission with an action parameter - listener. Checks if a
	 * bundle with AdminPrmission - listener can execute: -
	 * BundleContext.addBundleListener - BundleContext.removeBundleListener and
	 * can not execute anything else that requires other AdminPermission.
	 * 
	 * The bundle is specified either by bundle id or by filter string.
	 */
	public void testAdminPermissionListener() throws Throwable {
		String message = "";
		Vector permissions = utility.getPInfosForAdminPermisssion(
				AdminPermission.LISTENER, testSignatureBundle.getBundleId(),
				testSignatureBundle.getLocation(), testSignatureBundle
						.getSymbolicName());

		PermissionInfo info;
		for (int i = 0; i < permissions.size(); ++i) {
			info = (PermissionInfo) permissions.elementAt(i);

			permissionAdmin.setPermissions(testBundleLocation,
					new PermissionInfo[] {info});
			printPermissions(testBundleLocation);

			utility.allowed_BundleContext_addBundleListener(message,
					testSignatureBundle);
			utility.allowed_BundleContext_removeBundleListener(message,
					testSignatureBundle);

			utility.not_allowed_Bundle_getHeaders(message, testSignatureBundle);
			utility.not_allowed_Bundle_getHeaders_byLocation(message,
					testSignatureBundle);
			utility
					.not_allowed_Bundle_getLocation(message,
							testSignatureBundle);
			utility.not_allowed_Bundle_getResource(message,
					testSignatureBundle, resourceName);
			utility.not_allowed_Bundle_getResources(message,
					testSignatureBundle, resourcesName);
			utility.not_allowed_Bundle_getEntry(message, testSignatureBundle,
					entryName);
			utility.not_allowed_Bundle_getEntryPaths(message,
					testSignatureBundle, entryPath);
			if (checkStartLevel) {
				utility.not_allowed_StartLevel_setBundleStartLevel(message,
						testSignatureBundle, startLevel);
				utility.not_allowed_StartLevel_setStartLevel(message,
						startLevel);
				utility.not_allowed_StartLevel_setInitialBundleStartLevel(
						message, initialBundleStartLevel);
			}
			utility.not_allowed_Bundle_loadClass(message, testSignatureBundle,
					className);
			utility.not_allowed_Bundle_stop(message, testSignatureBundle);
			utility.not_allowed_Bundle_start(message, testSignatureBundle);
			utility.not_allowed_Bundle_update(message, testSignatureBundle);
			utility.not_allowed_Bundle_update_by_InputStream(message,
					testSignatureBundle, getInputStream(signatureBundleName));
			utility.not_allowed_Bundle_uninstall(message, testSignatureBundle);
			utility.not_allowed_PackageAdmin_refreshPackages(message,
					new Bundle[] {testSignatureBundle});
			utility.not_allowed_PackageAdmin_resolveBundles(message,
					new Bundle[] {testSignatureBundle});
			utility.not_allowed_PermissionAdmin_setPermissions(message,
					signatureBundleLocation,
					new PermissionInfo[] {new PermissionInfo(
							AdminPermission.class.getName(), "*", "*")});
			utility.not_allowed_PermissionAdmin_setDefaultPermissions(message,
					permissionAdmin.getDefaultPermissions());

		}

	}

	/**
	 * Tests AdminPermission with an action parameter - resolve. Checks if a
	 * bundle with AdminPrmission - resolve can execute: -
	 * PackageAdmin.refreshPackages - PackageAdmin.resolveBundles and can not
	 * execute anything else that requires other AdminPermission.
	 * 
	 * The bundle is specified either by bundle id or by filter string.
	 */
	public void testAdminPermissionResolve() throws Throwable {
		String message = "";
		Vector permissions = utility.getPInfosForAdminPermisssion(
				AdminPermission.RESOLVE, testSignatureBundle.getBundleId(),
				testSignatureBundle.getLocation(),
				testSignatureBundle.getSymbolicName());

		PermissionInfo info;
		for (int i = permissions.size() - 1; i >= 0; i--) {
			info = (PermissionInfo) permissions.elementAt(i);

			permissionAdmin.setPermissions(testBundleLocation,
					new PermissionInfo[] {info});
			printPermissions(testBundleLocation);

			utility.not_allowed_BundleContext_addBundleListener(message,
					testSignatureBundle);
			utility.not_allowed_BundleContext_removeBundleListener(message,
					testSignatureBundle);
			utility.not_allowed_Bundle_getHeaders(message, testSignatureBundle);
			utility.not_allowed_Bundle_getHeaders_byLocation(message,
					testSignatureBundle);
			utility
					.not_allowed_Bundle_getLocation(message,
							testSignatureBundle);
			utility.not_allowed_Bundle_getResource(message,
					testSignatureBundle, resourceName);
			utility.not_allowed_Bundle_getResources(message,
					testSignatureBundle, resourcesName);
			utility.not_allowed_Bundle_getEntry(message, testSignatureBundle,
					entryName);
			utility.not_allowed_Bundle_getEntryPaths(message,
					testSignatureBundle, entryPath);
			if (checkStartLevel) {
				utility.not_allowed_StartLevel_setBundleStartLevel(message,
						testSignatureBundle, startLevel);
				utility.not_allowed_StartLevel_setStartLevel(message,
						startLevel);
				utility.not_allowed_StartLevel_setInitialBundleStartLevel(
						message, initialBundleStartLevel);
			}
			utility.not_allowed_Bundle_loadClass(message, testSignatureBundle,
					className);
			utility.not_allowed_Bundle_stop(message, testSignatureBundle);
			utility.not_allowed_Bundle_start(message, testSignatureBundle);
			utility.not_allowed_Bundle_update(message, testSignatureBundle);
			utility.not_allowed_Bundle_update_by_InputStream(message,
					testSignatureBundle, getInputStream(signatureBundleName));
			utility.not_allowed_Bundle_uninstall(message, testSignatureBundle);
			utility.not_allowed_PermissionAdmin_setPermissions(message,
					signatureBundleLocation,
					new PermissionInfo[] {new PermissionInfo(
							AdminPermission.class.getName(), "*", "*")});
			utility.not_allowed_PermissionAdmin_setDefaultPermissions(message,
					permissionAdmin.getDefaultPermissions());

			if (i == 0) {
				utility.allowed_PackageAdmin_resolveBundles(message,
						new Bundle[] {testSignatureBundle});
				utility.allowed_PackageAdmin_refreshPackages(message,
						new Bundle[] {testSignatureBundle});
			}
			else {
				utility.not_allowed_PackageAdmin_resolveBundles(message,
						new Bundle[] {testSignatureBundle});
				utility.not_allowed_PackageAdmin_refreshPackages(message,
						new Bundle[] {testSignatureBundle});
			}

		}

	}

	/**
	 * Tests AdminPermission with an action parameter - startlevel. Checks if a
	 * bundle with AdminPrmission - startlevel can execute: -
	 * StartLevel.setStartLevel - StartLevel.setInitialBundleStartLevel and can
	 * not execute anything else that requires other AdminPermission.
	 * 
	 * The bundle is specified either by bundle id or by filter string.
	 */
	public void testAdminPermissionStartlevel() throws Throwable {
		if (!checkStartLevel) {
			return; // this method shall not be excecuted - no StartLevel
			// service detected
		}
		String message = "";
		Vector permissions = utility.getPInfosForAdminPermisssion(
				AdminPermission.STARTLEVEL, 0, testSignatureBundle
						.getLocation(), testSignatureBundle.getSymbolicName());

		PermissionInfo info;
		for (int i = 0; i < permissions.size(); ++i) {
			info = (PermissionInfo) permissions.elementAt(i);

			permissionAdmin.setPermissions(testBundleLocation,
					new PermissionInfo[] {info});
			printPermissions(testBundleLocation);

			if (i < 2) {
				utility.allowed_StartLevel_setStartLevel(message, startLevel);
				utility.allowed_StartLevel_setInitialBundleStartLevel(message,
						initialBundleStartLevel);
			}
			else {
				utility.not_allowed_StartLevel_setStartLevel(message,
						startLevel);
				utility.not_allowed_StartLevel_setInitialBundleStartLevel(
						message, initialBundleStartLevel);
			}

			utility.not_allowed_BundleContext_addBundleListener(message,
					testSignatureBundle);
			utility.not_allowed_BundleContext_removeBundleListener(message,
					testSignatureBundle);
			utility.not_allowed_Bundle_getHeaders(message, testSignatureBundle);
			utility.not_allowed_Bundle_getHeaders_byLocation(message,
					testSignatureBundle);
			utility
					.not_allowed_Bundle_getLocation(message,
							testSignatureBundle);
			utility.not_allowed_Bundle_getResource(message,
					testSignatureBundle, resourceName);
			utility.not_allowed_Bundle_getResources(message,
					testSignatureBundle, resourcesName);
			utility.not_allowed_Bundle_getEntry(message, testSignatureBundle,
					entryName);
			utility.not_allowed_Bundle_getEntryPaths(message,
					testSignatureBundle, entryPath);
			if (checkStartLevel) {
				utility.not_allowed_StartLevel_setBundleStartLevel(message,
						testSignatureBundle, startLevel);
			}
			utility.not_allowed_Bundle_loadClass(message, testSignatureBundle,
					className);
			utility.not_allowed_Bundle_stop(message, testSignatureBundle);
			utility.not_allowed_Bundle_start(message, testSignatureBundle);
			utility.not_allowed_Bundle_update(message, testSignatureBundle);
			utility.not_allowed_Bundle_update_by_InputStream(message,
					testSignatureBundle, getInputStream(signatureBundleName));
			utility.not_allowed_Bundle_uninstall(message, testSignatureBundle);
			utility.not_allowed_PackageAdmin_refreshPackages(message,
					new Bundle[] {testSignatureBundle});
			utility.not_allowed_PackageAdmin_resolveBundles(message,
					new Bundle[] {testSignatureBundle});
			utility.not_allowed_PermissionAdmin_setPermissions(message,
					signatureBundleLocation,
					new PermissionInfo[] {new PermissionInfo(
							AdminPermission.class.getName(), "*", "*")});
			utility.not_allowed_PermissionAdmin_setDefaultPermissions(message,
					permissionAdmin.getDefaultPermissions());

		}
	}

	// returns true if 'method' failed
	boolean not_allowed_call(String message, String methodName,
			Class[] paramClasses, Object[] paramObjects, Class wanted)
			throws Exception {
		try {
			MethodCall method = new MethodCall(methodName, paramClasses,
					paramObjects);
			method.invoke(tbc);
		}
		catch (Throwable e) {
			assertException(message, wanted, e);
			return true;
		}
		failException(message, wanted);
		return false;
	}

	boolean not_allowed_call_assertNull(String message, String methodName,
			Class[] paramClasses, Object[] paramObjects) throws Throwable {
		MethodCall method = new MethodCall(methodName, paramClasses,
				paramObjects);
		Object result = method.invoke(tbc);
		if (result == null) {
			pass(message + " and correctly returns null");
			return true;
		}
		else {
			fail(message + " but returns not null");
			return false;
		}
	}

	boolean allowed_call_assertNotNull(String message, String methodName,
			Class[] paramClasses, Object[] paramObjects) throws Throwable {
		MethodCall method = new MethodCall(methodName, paramClasses,
				paramObjects);
		Object result = method.invoke(tbc);
		if (result == null) {
			fail(message + " but returns null");
			return false;
		}
		else {
			pass(message + " and correctly returns not null");
			return true;
		}
	}

	Object allowed_call(String message, String methodName,
			Class[] paramClasses, Object[] paramObjects) {
		try {
			MethodCall method = new MethodCall(methodName, paramClasses,
					paramObjects);
			Object result = method.invoke(tbc);
			pass(message);
			return result;
		}
		catch (Throwable e) {
			fail(message + " but " + e.getClass().getName() + " was thrown", e);
			return null;
		}
	}

	private InputStream getInputStream(String bundleName) throws Exception {
		return (new URL(getInstallBundleLocation(bundleName))).openStream();
	}

	private void printPermissions(String bundleLocation) {
		PermissionInfo[] pi = permissionAdmin.getPermissions(bundleLocation);
		if ((pi == null) || (pi.length == 0)) {
			log("No permissions for " + bundleLocation);
			return;
		}
		log("Permissions for " + bundleLocation);
		for (int j = 0; j < pi.length; ++j) {
			log(pi[j].toString());
		}
	}

	private String getInstallBundleLocation(String bundleName) {
		return getWebServer() + bundleName;
	}
}

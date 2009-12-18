/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.test.cases.composite.secure.junit;

import java.io.FilePermission;
import java.net.SocketPermission;
import java.security.AllPermission;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.composite.CompositeAdmin;
import org.osgi.service.composite.CompositeBundle;
import org.osgi.service.composite.CompositeConstants;
import org.osgi.service.condpermadmin.BundleLocationCondition;
import org.osgi.service.condpermadmin.BundleSignerCondition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionUpdate;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.composite.AbstractCompositeTestCase;

public class CompositeSecurityTests extends AbstractCompositeTestCase {

	private static String TB1_DN_CHAIN = "CN=John Smith,O=ACME Inc,OU=ACME Cert Authority,L=Austin,ST=Texas,C=US";

	private static String RUNNABLE_AND_IMPORT_PERM = 
			"(org.osgi.framework.ServicePermission \"java.lang.Runnable\" \"get\")" +
			"(org.osgi.framework.PackagePermission \"*\" \"import\")";
	private static String ALL_PERM = "(" + AllPermission.class.getName() + ")";
	private static String ALL_FILE_PERM = "(" + FilePermission.class.getName() + " \"<<ALL FILES>>\" \"read\")";
	private static String ALL_SOCKET_PERM = "(" + SocketPermission.class.getName() + " \"*\" \"connect\")";

	private ServiceReference condAdminRef;
	private ConditionalPermissionAdmin condAdmin;
	private ServiceReference permAdminRef;
	private PermissionAdmin permAdmin;
	private ArrayList<ServiceRegistration<?>> registrations = new ArrayList<ServiceRegistration<?>>();

	private String getEncodedInfo(String decision, String condArg, String permissions, String name, boolean byLocation) {
		return decision + " { " +
				(condArg == null ? "" : "[" + (byLocation ? BundleLocationCondition.class.getName() : BundleSignerCondition.class.getName()) + 
						" \"" + condArg + "\"] ") +
						permissions + " } \"" + name + "\"";
	}

	private void setPermissions(Bundle bundle, ConditionalPermissionAdmin admin, String permissions, String name, boolean byLocation) {
		ConditionalPermissionInfo info = admin.getConditionalPermissionInfo(name);
		if (info != null)
			info.delete();
		String condArg = byLocation ? (bundle == null ? null : bundle.getLocation()) : TB1_DN_CHAIN;
		String encodedInfo = getEncodedInfo(ConditionalPermissionInfo.ALLOW, condArg, permissions, name, byLocation);
		ConditionalPermissionUpdate update = admin.newConditionalPermissionUpdate();
		List infos = update.getConditionalPermissionInfos();
		infos.add(0, admin.newConditionalPermissionInfo(encodedInfo));
		update.commit();
	}

	public void setUp() {
		super.setUp();
		condAdminRef = getContext().getServiceReference(ConditionalPermissionAdmin.class.getName());
		assertNotNull(condAdminRef);
		condAdmin = (ConditionalPermissionAdmin) getContext().getService(condAdminRef);
		assertNotNull(condAdmin);
		permAdminRef = getContext().getServiceReference(PermissionAdmin.class.getName());
		assertNotNull(permAdminRef);
		permAdmin = (PermissionAdmin) getContext().getService(permAdminRef);
		assertNotNull(permAdmin);

		// allow all bundles to access the Runnable service
		setPermissions(null, condAdmin, RUNNABLE_AND_IMPORT_PERM, "runnable.import.perm", true);
	}

	public void tearDown() {
		super.tearDown();

		for (ServiceRegistration<?> registration : registrations) {
			registration.unregister();
		}
		registrations.clear();

		ConditionalPermissionUpdate update = condAdmin.newConditionalPermissionUpdate();
		update.getConditionalPermissionInfos().clear();
		update.commit();

		if (condAdmin != null)
			getContext().ungetService(condAdminRef);
		condAdmin = null;
		condAdminRef = null;
		if (permAdmin != null)
			getContext().ungetService(permAdminRef);
		permAdmin = null;
		permAdminRef = null;
	}

	public void testSecurityCreate01() {
		// try to test create and uninstall
		Runnable testRunnable = new Runnable() {
			public void run() {
				CompositeBundle composite = createCompositeBundle(compAdmin, getName(), null, null);
				uninstallCompositeBundle(composite);
			}
		};
		Dictionary<String, Object> props = new Hashtable<String, Object>();
		props.put("security.test", true);
		// should fail; tb1 does not have all permissions
		props.put("security.test.fail", true);
		registrations.add(getContext().registerService(Runnable.class.getName(), testRunnable, props));
		Bundle tb1 = install("tb1.jar");
		try {
			tb1.start();
			assertEquals("Wrong bundle state", Bundle.ACTIVE, tb1.getState());
		} catch (BundleException e) {
			fail("Unexpected failure", e);
		}
	}

	public void testSecurityCreate02() {
		// try to test create and uninstall
		Runnable testRunnable = new Runnable() {
			public void run() {
				CompositeBundle composite = createCompositeBundle(compAdmin, getName(), null, null);
				uninstallCompositeBundle(composite);
			}
		};
		Dictionary<String, Object> props = new Hashtable<String, Object>();
		props.put("security.test", true);
		// should not fail; tb1 has all permissions
		props.put("security.test.fail", false);
		registrations.add(getContext().registerService(Runnable.class.getName(), testRunnable, props));
		Bundle tb1 = install("tb1.jar");
		setPermissions(tb1, condAdmin, ALL_PERM, getName(), true);
		try {
			tb1.start();
			assertEquals("Wrong bundle state", Bundle.ACTIVE, tb1.getState());
		} catch (BundleException e) {
			fail("Unexpected failure", e);
		}
	}

	public void testSecurityCreate03() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass="+ Runnable.class.getName() + ")");
		CompositeBundle composite = createCompositeBundle(compAdmin, getName(), manifest, null);
		startCompositeBundle(composite);
		final CompositeAdmin innerCompAdmin = (CompositeAdmin) getService(composite.getSystemBundleContext(), CompositeAdmin.class.getName());
		// try to test create and uninstall
		Runnable testRunnable = new Runnable() {
			public void run() {
				CompositeBundle innerComposite = createCompositeBundle(innerCompAdmin, getName()+1, null, null);
				uninstallCompositeBundle(innerComposite);
			}
		};
		Dictionary<String, Object> props = new Hashtable<String, Object>();
		props.put("security.test", true);
		// should not fail; tb1 has all permissions
		props.put("security.test.fail", false);
		registrations.add(getContext().registerService(Runnable.class.getName(), testRunnable, props));
		Bundle tb1 = installConstituent(composite, null, "tb1.jar");
		// Don't need to set all perms since bundles installed in the inner composite should have all perm by default
		try {
			tb1.start();
			assertEquals("Wrong bundle state", Bundle.ACTIVE, tb1.getState());
		} catch (BundleException e) {
			fail("Unexpected failure", e);
		}
	}

	public void testSecurityCreate04() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass="+ Runnable.class.getName() + ")");
		CompositeBundle composite = createCompositeBundle(compAdmin, getName(), manifest, null);
		startCompositeBundle(composite);
		final CompositeAdmin innerCompAdmin = (CompositeAdmin) getService(composite.getSystemBundleContext(), CompositeAdmin.class.getName());
		// try to test create and uninstall
		Runnable testRunnable = new Runnable() {
			public void run() {
				CompositeBundle innerComposite = createCompositeBundle(innerCompAdmin, getName()+1, null, null);
				uninstallCompositeBundle(innerComposite);
			}
		};
		Dictionary<String, Object> props = new Hashtable<String, Object>();
		props.put("security.test", true);
		// should fail; tb1 does not have all permissions
		props.put("security.test.fail", true);
		registrations.add(getContext().registerService(Runnable.class.getName(), testRunnable, props));
		Bundle tb1 = installConstituent(composite, null, "tb1.jar");
		ConditionalPermissionAdmin innerCondAdmin = (ConditionalPermissionAdmin) getService(composite.getSystemBundleContext(), ConditionalPermissionAdmin.class.getName());
		// Scope the permissions so the test bundle does not have all perms.
		setPermissions(null, innerCondAdmin, RUNNABLE_AND_IMPORT_PERM, "runnable.import.perm", true);
		try {
			tb1.start();
			assertEquals("Wrong bundle state", Bundle.ACTIVE, tb1.getState());
		} catch (BundleException e) {
			fail("Unexpected failure", e);
		}
	}

	public void testCompositePermissions01() {
		// Create two composites with different permission settings for the test bundle
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass="+ Runnable.class.getName() + ")");
		CompositeBundle composite1 = createCompositeBundle(compAdmin, getName()+1, manifest1, null);
		startCompositeBundle(composite1);
		Bundle tb1_1 = installConstituent(composite1, "tb1_1", "tb1.jar");
		ConditionalPermissionAdmin innerCondAdmin1 = (ConditionalPermissionAdmin) getService(composite1.getSystemBundleContext(), ConditionalPermissionAdmin.class.getName());
		// Scope the permissions so the tb1_1 does not have all perms.
		setPermissions(null, innerCondAdmin1, RUNNABLE_AND_IMPORT_PERM, "runnable.import.perm", true);
		setPermissions(tb1_1, innerCondAdmin1, ALL_FILE_PERM, "tb1.perms", false);

		Map manifest2 = new HashMap();
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest2.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass="+ Runnable.class.getName() + ")");
		CompositeBundle composite2 = createCompositeBundle(compAdmin, getName()+2, manifest2, null);
		startCompositeBundle(composite2);
		Bundle tb1_2 = installConstituent(composite2, "tb1_2", "tb1.jar");
		ConditionalPermissionAdmin innerCondAdmin2 = (ConditionalPermissionAdmin) getService(composite2.getSystemBundleContext(), ConditionalPermissionAdmin.class.getName());
		// Scope the permissions so the tb1_2 does not have all perms.
		setPermissions(null, innerCondAdmin2, RUNNABLE_AND_IMPORT_PERM, "runnable.import.perm", true);
		setPermissions(tb1_2, innerCondAdmin2, ALL_SOCKET_PERM, "tb1.perms", false);

		Runnable testRunnable1 = new Runnable() {
			public void run() {
				System.getSecurityManager().checkRead("test");
			}
		};
		Dictionary<String, Object> props1 = new Hashtable<String, Object>();
		props1.put("security.test", true);
		// should pass; tb1_1 has all file permissions
		props1.put("security.test.fail", false);
		ServiceRegistration<?> testRegistration1 = getContext().registerService(Runnable.class.getName(), testRunnable1, props1);
		registrations.add(testRegistration1);

		try {
			tb1_1.start();
			assertEquals("Wrong bundle state", Bundle.ACTIVE, tb1_1.getState());
			tb1_1.stop();
		} catch (BundleException e) {
			fail("Unexpected failure", e);
		}

		// should fail; tb1_2 does not have all file permissions
		props1.put("security.test.fail", true);
		testRegistration1.setProperties(props1);

		try {
			tb1_2.start();
			assertEquals("Wrong bundle state", Bundle.ACTIVE, tb1_2.getState());
			tb1_2.stop();
		} catch (BundleException e) {
			fail("Unexpected failure", e);
		}

		testRegistration1.unregister();
		registrations.remove(testRegistration1);

		Runnable testRunnable2 = new Runnable() {
			public void run() {
				System.getSecurityManager().checkConnect("test", 20);
			}
		};
		Dictionary<String, Object> props2 = new Hashtable<String, Object>();
		props2.put("security.test", true);
		// should pass; tb1_2 has all socket permissions
		props2.put("security.test.fail", false);
		ServiceRegistration<?> testRegistration2 = getContext().registerService(Runnable.class.getName(), testRunnable2, props2);
		registrations.add(testRegistration2);

		try {
			tb1_2.start();
			assertEquals("Wrong bundle state", Bundle.ACTIVE, tb1_2.getState());
			tb1_2.stop();
		} catch (BundleException e) {
			fail("Unexpected failure", e);
		}

		// should fail; tb1_1 does not have all socket permissions
		props2.put("security.test.fail", true);
		testRegistration2.setProperties(props2);

		try {
			tb1_1.start();
			assertEquals("Wrong bundle state", Bundle.ACTIVE, tb1_1.getState());
			tb1_1.stop();
		} catch (BundleException e) {
			fail("Unexpected failure", e);
		}
	}

}

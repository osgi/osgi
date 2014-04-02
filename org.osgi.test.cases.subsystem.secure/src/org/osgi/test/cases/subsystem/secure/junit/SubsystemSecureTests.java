/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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
package org.osgi.test.cases.subsystem.secure.junit;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.service.condpermadmin.BundleLocationCondition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionUpdate;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.service.subsystem.Subsystem;
import org.osgi.service.subsystem.SubsystemConstants;
import org.osgi.service.subsystem.SubsystemPermission;
import org.osgi.test.cases.subsystem.resource.SubsystemInfo;
import org.osgi.test.cases.subsystem.secure.runner.Runner;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.util.tracker.ServiceTracker;

public class SubsystemSecureTests extends OSGiTestCase {
	static SubsystemInfo subsystemInfo;
	public static String SUBSYSTEM_SECURE_TEST = "subsystem.secure.test@1.0.0.esa";
	public static String BUNDLE_NO_DEPS_A_V1 = "no.deps.a@1.0.0.jar";
	public static String BUNDLE_NO_DEPS_B_V1 = "no.deps.b@1.0.0.jar";
	public static String BUNDLE_NO_DEPS_C_V1 = "no.deps.c@1.0.0.jar";

	Runner runner;
	Bundle runnerBundle;
	ServiceReference<Runner> runnerRef;
	ServiceTracker<Subsystem, Subsystem> rootSubsystem;
	Subsystem testSubsystem = null;

	@Override
	protected void setUp() throws Exception {
		createSubsystem();
		runnerBundle = install("bundle.jar");
		runnerBundle.start();

		runnerRef = getContext().getServiceReference(Runner.class);
		assertNotNull("No runner found.", runnerRef);
		runner = getContext().getService(runnerRef);
		assertNotNull("No runner found.", runner);

		Filter rootFilter = getContext().createFilter(
				"(&(objectClass=" + Subsystem.class.getName() + ")("
						+ SubsystemConstants.SUBSYSTEM_ID_PROPERTY + "=0)("
						+ SubsystemConstants.SUBSYSTEM_STATE_PROPERTY + "="
						+ Subsystem.State.ACTIVE + "))");
		rootSubsystem = new ServiceTracker<Subsystem, Subsystem>(getContext(), rootFilter, null);
		rootSubsystem.open();

		// wait for the subsytems implementation to register the root subsystem
		rootSubsystem.waitForService(10000);
	}

	protected void setPermissions(String access) {
		ServiceReference<ConditionalPermissionAdmin> adminRef = getContext().getServiceReference(ConditionalPermissionAdmin.class);
		assertNotNull("Cannot test without conditional permission admin.", adminRef);
		ConditionalPermissionAdmin admin = getContext().getService(adminRef);
		assertNotNull("Cannot test without conditional permission admin.", admin);

		ConditionalPermissionUpdate update = admin.newConditionalPermissionUpdate();
		List<ConditionalPermissionInfo> infos = update.getConditionalPermissionInfos();
				infos.add(
				admin.newConditionalPermissionInfo(
						getName() + "_1", 
						new ConditionInfo[] {new ConditionInfo(BundleLocationCondition.class.getName(), new String[] {runnerBundle.getLocation()})}, 
						new PermissionInfo[] {new PermissionInfo(SubsystemPermission.class.getName(), "*", "execute,lifecycle,metadata,context")}, 
						access));
		infos.add(
				admin.newConditionalPermissionInfo(
						getName() + "_2", 
						new ConditionInfo[] {new ConditionInfo(BundleLocationCondition.class.getName(), new String[] {runnerBundle.getLocation()})}, 
						new PermissionInfo[] {
							new PermissionInfo(ServicePermission.class.getName(), "*", "get,register"),
							new PermissionInfo(PackagePermission.class.getName(), "*", "import")
						}, 
						ConditionalPermissionInfo.ALLOW));
		infos.add(
				admin.newConditionalPermissionInfo(
						getName() + "_3", 
						new ConditionInfo[] {new ConditionInfo(BundleLocationCondition.class.getName(), new String[] {runnerBundle.getLocation()})}, 
						new PermissionInfo[] {
							new PermissionInfo(AllPermission.class.getName(), null, null)
						}, 
						ConditionalPermissionInfo.DENY));
		infos.add(
				admin.newConditionalPermissionInfo(
						getName() + "_4", 
						new ConditionInfo[] {new ConditionInfo(BundleLocationCondition.class.getName(), new String[] {"*"})}, 
						new PermissionInfo[] {
							new PermissionInfo(AllPermission.class.getName(), null, null)
						}, 
						ConditionalPermissionInfo.ALLOW));
		update.commit();
	}

	private void createSubsystem() {
		if (subsystemInfo != null) {
			return;
		}
		File testSubsystemRoots = getContext().getDataFile("testSubsystems");
		testSubsystemRoots.mkdirs();
		String contentHeader = SubsystemInfo.getSymbolicName(BUNDLE_NO_DEPS_A_V1) + ", " + SubsystemInfo.getSymbolicName(BUNDLE_NO_DEPS_B_V1) + ", " + SubsystemInfo.getSymbolicName(BUNDLE_NO_DEPS_C_V1);
		Map<String, URL> contents = getBundleContents(null, BUNDLE_NO_DEPS_A_V1, BUNDLE_NO_DEPS_B_V1, BUNDLE_NO_DEPS_C_V1);
		subsystemInfo = new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_SECURE_TEST), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, contents, null);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (runnerBundle != null) {
			runnerBundle.uninstall();
		}
		if (runnerRef != null) {
			getContext().ungetService(runnerRef);
		}
		if (testSubsystem != null) {
			try {
				testSubsystem.uninstall();
			} catch (Throwable t) {
				// just trying to clean up
			}
		}
		unsetPermissions();
	}

	protected void unsetPermissions() {
		ServiceReference<ConditionalPermissionAdmin> adminRef = getContext().getServiceReference(ConditionalPermissionAdmin.class);
		assertNotNull("Cannot test without conditional permission admin.", adminRef);
		ConditionalPermissionAdmin admin = getContext().getService(adminRef);
		assertNotNull("Cannot test without conditional permission admin.", admin);

		ConditionalPermissionUpdate update = admin.newConditionalPermissionUpdate();
		List<ConditionalPermissionInfo> infos = update.getConditionalPermissionInfos();
		for (Iterator<ConditionalPermissionInfo> iInfos = infos.iterator(); iInfos.hasNext();) {
			ConditionalPermissionInfo info = iInfos.next();
			if (info.getName() != null && info.getName().startsWith(getName() + "_")) {
				iInfos.remove();
			}	
		}
		update.commit();
	}

	Map<String, URL> getBundleContents(Map<String, URL> result, String... bundles) {
		if (result == null)
			result = new HashMap<String, URL>();
		for (String bundleName : bundles) {
			URL url = getContext().getBundle().getEntry(bundleName);
			assertNotNull("Could not find bundle: " + bundleName, url);
			result.put(bundleName, url);
		}
		return result;
	}

	Subsystem getRootSubsystem() {
		Subsystem root = rootSubsystem.getService();
		assertNotNull("Can not locate the root subsystem.", root);
		return root;
	}

	Subsystem installTestSubsystem() {
		Subsystem root = getRootSubsystem();
		InputStream input = AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
			public InputStream run() {
				return subsystemInfo.getSubsystemResource().getContent();
			}
		});
		testSubsystem = root.install(getName(), input, null);
		return testSubsystem;
	}

	public void testAllowInstall() {
		setPermissions(ConditionalPermissionInfo.ALLOW);
		try {
			runner.run(new PrivilegedExceptionAction<Subsystem>() {
				public Subsystem run() {
					return installTestSubsystem();
				}
			});
		} catch (Exception e) {
			fail("Expected to be able to install the subsystem.", e);
		}
	}

	public void testDenyInstall() throws Exception {
		setPermissions(ConditionalPermissionInfo.DENY);
		try {
			runner.run(new PrivilegedExceptionAction<Subsystem>() {
				public Subsystem run() {
					return installTestSubsystem();
				}
			});
			fail("Expected to get security exception when installing");
		} catch (SecurityException e) {
			// expected
		}
	}

	public void testAllowGetBundleContext() {
		final Subsystem subsystem = installTestSubsystem();
		setPermissions(ConditionalPermissionInfo.ALLOW);
		try {
			BundleContext context = runner.run(new PrivilegedExceptionAction<BundleContext>() {
				public BundleContext run() {
					return subsystem.getBundleContext();
				}
			});
			assertNotNull("Context is null.", context);
		} catch (Exception e) {
			fail("Expected to be able to get the context.", e);
		}
	}

	public void testDenyGetBundleContext() throws Exception {
		final Subsystem subsystem = installTestSubsystem();
		setPermissions(ConditionalPermissionInfo.DENY);
		try {
			runner.run(new PrivilegedExceptionAction<BundleContext>() {
				public BundleContext run() {
					return subsystem.getBundleContext();
				}
			});
			fail("Expected to get security exception when getting the context.");
		} catch (SecurityException e) {
			// expected
		}
	}

	public void testAllowStart() {
		final Subsystem subsystem = installTestSubsystem();
		setPermissions(ConditionalPermissionInfo.ALLOW);
		try {
			Subsystem.State state = runner.run(new PrivilegedExceptionAction<Subsystem.State>() {
				public Subsystem.State run() {
					subsystem.start();
					return subsystem.getState();
				}
			});
			assertEquals("Wrong state.", Subsystem.State.ACTIVE, state);
		} catch (Exception e) {
			fail("Expected to be able to start the subsystem.", e);
		}
	}

	public void testDenyStart() throws Exception {
		final Subsystem subsystem = installTestSubsystem();
		setPermissions(ConditionalPermissionInfo.DENY);
		try {
			runner.run(new PrivilegedExceptionAction<Subsystem.State>() {
				public Subsystem.State run() {
					subsystem.start();
					return subsystem.getState();
				}
			});
			fail("Expected to get security exception when starting");
		} catch (SecurityException e) {
			// expected
		}
	}

	public void testAllowStop() {
		final Subsystem subsystem = installTestSubsystem();

		subsystem.start();
		assertEquals("Wrong state.", Subsystem.State.ACTIVE, subsystem.getState());

		setPermissions(ConditionalPermissionInfo.ALLOW);
		try {
			Subsystem.State state = runner.run(new PrivilegedExceptionAction<Subsystem.State>() {
				public Subsystem.State run() {
					subsystem.stop();
					return subsystem.getState();
				}
			});
			assertEquals("Wrong state.", Subsystem.State.RESOLVED, state);
		} catch (Exception e) {
			fail("Expected to be able to stop the subsystem.", e);
		}
	}

	public void testDenyStop() throws Exception {
		final Subsystem subsystem = installTestSubsystem();

		subsystem.start();
		assertEquals("Wrong state.", Subsystem.State.ACTIVE, subsystem.getState());

		setPermissions(ConditionalPermissionInfo.DENY);
		try {
			runner.run(new PrivilegedExceptionAction<Subsystem.State>() {
				public Subsystem.State run() {
					subsystem.stop();
					return subsystem.getState();
				}
			});
			fail("Expected to get security exception when stopping");
		} catch (SecurityException e) {
			// expected
		}
	}

	public void testAllowUninstall() {
		final Subsystem subsystem = installTestSubsystem();

		setPermissions(ConditionalPermissionInfo.ALLOW);
		try {
			Subsystem.State state = runner.run(new PrivilegedExceptionAction<Subsystem.State>() {
				public Subsystem.State run() {
					subsystem.uninstall();
					return subsystem.getState();
				}
			});
			assertEquals("Wrong state.", Subsystem.State.UNINSTALLED, state);
		} catch (Exception e) {
			fail("Expected to be able to uninstall the subsystem.", e);
		}
	}

	public void testDenyUninstall() throws Exception {
		final Subsystem subsystem = installTestSubsystem();

		setPermissions(ConditionalPermissionInfo.DENY);
		try {
			runner.run(new PrivilegedExceptionAction<Subsystem.State>() {
				public Subsystem.State run() {
					subsystem.uninstall();
					return subsystem.getState();
				}
			});
			fail("Expected to get security exception when uninstalling");
		} catch (SecurityException e) {
			// expected
		}
	}

	public void testAllowGetHeaders() {
		final Subsystem subsystem = installTestSubsystem();
		setPermissions(ConditionalPermissionInfo.ALLOW);
		try {
			Map<String, String> headers = runner.run(new PrivilegedExceptionAction<Map<String, String>>() {
				public Map<String, String> run() {
					return subsystem.getSubsystemHeaders(null);
				}
			});
			assertNotNull("Headers is null.", headers);
		} catch (Exception e) {
			fail("Expected to be able to get the headers.", e);
		}
	}

	public void testDenyGetHeaders() throws Exception {
		final Subsystem subsystem = installTestSubsystem();
		setPermissions(ConditionalPermissionInfo.DENY);
		try {
			runner.run(new PrivilegedExceptionAction<Map<String, String>>() {
				public Map<String, String> run() {
					return subsystem.getSubsystemHeaders(null);
				}
			});
			fail("Expected to get security exception when getting the headers.");
		} catch (SecurityException e) {
			// expected
		}
	}

	public void testAllowGetLocation() {
		final Subsystem subsystem = installTestSubsystem();
		setPermissions(ConditionalPermissionInfo.ALLOW);
		try {
			String location = runner.run(new PrivilegedExceptionAction<String>() {
				public String run() {
					return subsystem.getLocation();
				}
			});
			assertNotNull("Location is null.", location);
		} catch (Exception e) {
			fail("Expected to be able to get the location.", e);
		}
	}

	public void testDenyGetLocation() throws Exception {
		final Subsystem subsystem = installTestSubsystem();
		setPermissions(ConditionalPermissionInfo.DENY);
		try {
			runner.run(new PrivilegedExceptionAction<String>() {
				public String run() {
					return subsystem.getLocation();
				}
			});
			fail("Expected to get security exception when getting the location.");
		} catch (SecurityException e) {
			// expected
		}
	}
	
	public void testAllowGetDeploymentHeaders() {
		final Subsystem subsystem = installTestSubsystem();
		setPermissions(ConditionalPermissionInfo.ALLOW);
		try {
			Map<String, String> headers = runner.run(new PrivilegedExceptionAction<Map<String, String>>() {
				public Map<String, String> run() {
					return subsystem.getDeploymentHeaders();
				}
			});
			assertNotNull("Headers is null.", headers);
		} catch (Exception e) {
			fail("Expected to be able to get the headers.", e);
		}
	}

	public void testDenyGetDeploymentHeaders() throws Exception {
		final Subsystem subsystem = installTestSubsystem();
		setPermissions(ConditionalPermissionInfo.DENY);
		try {
			runner.run(new PrivilegedExceptionAction<Map<String, String>>() {
				public Map<String, String> run() {
					return subsystem.getDeploymentHeaders();
				}
			});
			fail("Expected to get security exception when getting the headers.");
		} catch (SecurityException e) {
			// expected
		}
	}
}

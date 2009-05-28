/*
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
package org.osgi.test.cases.framework.launch.junit;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.startlevel.StartLevel;
import org.osgi.test.support.OSGiTestCase;

public class FrameworkLaunchTests extends OSGiTestCase {
	private static final String STORAGEROOT = "org.osgi.framework.launch.test.storageroot";
	private static final String FRAMEWORK_FACTORY = "/META-INF/services/org.osgi.framework.launch.FrameworkFactory";

	private String frameworkFactoryClassName;
	private String rootStorageArea;
	private FrameworkFactory frameworkFactory;
	
	protected void setUp() throws Exception {
		super.setUp();
		frameworkFactoryClassName = getFrameworkFactoryClassName();
		assertNotNull("Could not find framework factory class", frameworkFactoryClassName);
		frameworkFactory = getFrameworkFactory();

		rootStorageArea = getStorageAreaRoot();
		assertNotNull("No storage area root found", rootStorageArea);
		File rootFile = new File(rootStorageArea);
		assertFalse("Root storage area is not a directory: " + rootFile.getPath(), rootFile.exists() && !rootFile.isDirectory());
		if (!rootFile.isDirectory())
			assertTrue("Could not create root directory: " + rootFile.getPath(), rootFile.mkdirs());
	}


	private String getFrameworkFactoryClassName() throws IOException {
		BundleContext context = getBundleContextWithoutFail();
		URL factoryService = context == null ? this.getClass().getResource(FRAMEWORK_FACTORY) : context.getBundle(0).getEntry(FRAMEWORK_FACTORY);
		assertNotNull("Could not locate: " + FRAMEWORK_FACTORY, factoryService);
		return getClassName(factoryService);

	}

	private String getClassName(URL factoryService) throws IOException {
		InputStream in = factoryService.openStream();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(in));
			for (String line = br.readLine(); line != null; line=br.readLine()) {
				int pound = line.indexOf('#');
				if (pound >= 0)
					line = line.substring(0, pound);
				line.trim();
				if (!"".equals(line))
					return line;
			}
		} finally {
			try {
				if (br != null)
					br.close();
			}
			catch (IOException e) {
				// did our best; just ignore
			}
		}
		return null;
	}

	private String getStorageAreaRoot() {
		BundleContext context = getBundleContextWithoutFail();
		if (context == null) {
			String storageroot = System.getProperty(STORAGEROOT);
			assertNotNull("Must set property: " + STORAGEROOT, storageroot);
			return storageroot;
		}
		return context.getDataFile("storageroot").getAbsolutePath();
	}

	private Class loadFrameworkClass(String className)
			throws ClassNotFoundException {
		BundleContext context = getBundleContextWithoutFail();
		return context == null ? Class.forName(className) : getContext().getBundle(0).loadClass(className);
	}

	private BundleContext getBundleContextWithoutFail() {
		try {
			return getContext();
		} catch (Throwable t) {
			return null; // don't fail
		}
	}

	private FrameworkFactory getFrameworkFactory() {
		try {
			Class clazz = loadFrameworkClass(frameworkFactoryClassName);
			return (FrameworkFactory) clazz.newInstance();
		} catch (Exception e) {
			fail("Failed to get the framework constructor", e);
		}
		return null;
	}

	private File getStorageArea(String testName, boolean delete) {
		File storageArea = new File(rootStorageArea, testName);
		if (delete) {
			assertTrue("Could not clean up storage area: " + storageArea.getPath(), delete(storageArea));
			assertTrue("Could not create storage area directory: " + storageArea.getPath(), storageArea.mkdirs());
		}
		return storageArea;
	}

	private boolean delete(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				String list[] = file.list();
				if (list != null) {
					int len = list.length;
					for (int i = 0; i < len; i++)
						if (!delete(new File(file, list[i])))
							return false;
				}
			}

			return file.delete();
		}
		return (true);
	}

	private Framework createFramework(Map configuration) {
		Framework framework = null;
		try {
			framework = frameworkFactory.newFramework(configuration);
		}
		catch (Exception e) {
			fail("Failed to construct the framework", e);
		}
		assertEquals("Wrong state for newly constructed framework", Bundle.INSTALLED, framework.getState());
		return framework;
	}
	
	private Map getConfiguration(String testName) {
		return getConfiguration(testName, true);
	}

	private Map getConfiguration(String testName, boolean delete) {
		Map configuration = new HashMap();
		if (testName != null)
			configuration.put(Constants.FRAMEWORK_STORAGE, getStorageArea(testName, delete).getAbsolutePath());
		return configuration;
	}

	private Bundle installBundle(Framework framework, String bundle) throws BundleException, IOException {
		BundleContext fwkContext = framework.getBundleContext();
		assertNotNull("Framework context is null", fwkContext);
		URL input = getBundleInput(bundle);
		assertNotNull("Cannot find resource: " + bundle, input);
		return fwkContext.installBundle(bundle, input.openStream());
	}

	private URL getBundleInput(String bundle) {
		BundleContext context = getBundleContextWithoutFail();
		return context == null ? this.getClass().getResource(bundle) : context.getBundle().getEntry(bundle);
	}


	private void initFramework(Framework framework) {
		try {
			framework.init();
			assertNotNull("BundleContext is null after init", framework.getBundleContext());
		}
		catch (BundleException e) {
			fail("Unexpected BundleException initializing", e);
		}
		assertEquals("Wrong framework state after init", Bundle.STARTING, framework.getState());
	}

	private void startFramework(Framework framework) {
		try {
			framework.start();
			assertNotNull("BundleContext is null after start", framework.getBundleContext());
		}
		catch (BundleException e) {
			fail("Unexpected BundleException initializing", e);
		}
		assertEquals("Wrong framework state after init", Bundle.ACTIVE, framework.getState());

	}

	private void stopFramework(Framework framework) {
		int previousState = framework.getState();
		try {
			framework.stop();
			FrameworkEvent event = framework.waitForStop(10000);
			assertNotNull("FrameworkEvent is null", event);
			assertEquals("Wrong event type", FrameworkEvent.STOPPED, event.getType());
			assertNull("BundleContext is not null after stop", framework.getBundleContext());
		}
		catch (BundleException e) {
			fail("Unexpected BundleException stopping", e);
		}
		catch (InterruptedException e) {
			fail("Unexpected InterruptedException waiting for stop", e);
		}
		// if the framework was not STARTING STOPPING or ACTIVE then we assume the waitForStop returned immediately with a FrameworkEvent.STOPPED 
		// and does not change the state of the framework
		int expectedState = (previousState & (Bundle.STARTING | Bundle.ACTIVE | Bundle.STOPPING)) != 0 ? Bundle.RESOLVED : previousState;
		assertEquals("Wrong framework state after init", expectedState, framework.getState());
	}

	private void updateFramework(Framework framework) {
		int previousState = framework.getState();
		FrameworkEvent[] result = new FrameworkEvent[1];
		Exception[] failureException = new Exception[1];
		Thread waitForStop = waitForStopThread(framework, 10000, result, failureException);
		waitForStop.start();
		try {
			Thread.sleep(500);
			framework.update();
		} catch (BundleException e) {
			fail("Failed to update the framework", e); //$NON-NLS-1$
		}
		catch (InterruptedException e) {
			fail("Unexpected interruption", e);
		}
		try {
			waitForStop.join();
		} catch (InterruptedException e) {
			fail("Unexpected interruption", e); //$NON-NLS-1$
		}
		if (failureException[0] != null)
			fail("Error occurred while waiting", failureException[0]); //$NON-NLS-1$
		assertNotNull("Wait for stop event is null", result[0]); //$NON-NLS-1$

		// if the framework was not STARTING or ACTIVE then we assume the waitForStop returned immediately with a FrameworkEvent.STOPPED
		int expectedFrameworkEvent = (previousState & (Bundle.STARTING | Bundle.ACTIVE)) != 0 ? FrameworkEvent.STOPPED_UPDATE : FrameworkEvent.STOPPED; 
		assertEquals("Wait for stop event type is wrong", expectedFrameworkEvent, result[0].getType()); //$NON-NLS-1$
			
		// hack; not sure how to listen for when a framework is done starting back up.
		for (int i = 0; i < 20; i++) {
			if (framework.getState() != previousState) {
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException e) {
					// nothing
				}
			} else {
				break;
			}
		}
		assertEquals("Not back at previous state after update", previousState, framework.getState());
	}

	private Object getService(Framework framework, Class serviceClass) {
		BundleContext context = framework.getBundleContext();
		ServiceReference ref = context.getServiceReference(serviceClass.getName());
		return ref == null ? null : context.getService(ref);
	}

	private Thread waitForStopThread(final Framework framework, final long timeout, final FrameworkEvent[] success, final Exception[] failureException) {
		return new Thread(new Runnable() {
			public void run() {
				try {
					success[0] = framework.waitForStop(10000);
				} catch (InterruptedException e) {
					failureException[0] = e;
				}
			}
		}, "test waitForStop thread"); //$NON-NLS-1$

	}

	private void checkImporter(ExportedPackage ep, Bundle testBundle) {
		if (ep == null)
			return;
		Bundle[] importers = ep.getImportingBundles();
		assertNotNull("null importers", importers);
		for (int i = 0; i < importers.length; i++) {
			if (importers[i] == testBundle)
				return;
		}
		fail("Bundle is not an importer of the package: " + ep);
	}

	public void testBasicCreate() {
		createFramework(getConfiguration(getName()));
	}

	public void testInitStop() {
		Framework framework = createFramework(getConfiguration(getName()));
		initFramework(framework);
		stopFramework(framework);
	}

	public void testStartStop() {
		Framework framework = createFramework(getConfiguration(getName()));
		startFramework(framework);
		stopFramework(framework);
	}

	public void testRestarts() {
		Framework framework = createFramework(getConfiguration(getName()));
		// first stop without init/start
		stopFramework(framework);

		// first start/stop
		startFramework(framework);
		stopFramework(framework);

		// now init/stop
		initFramework(framework);
		stopFramework(framework);

		// now init/start/stop
		initFramework(framework);
		startFramework(framework);
		stopFramework(framework);
	}

	public void testUpdate() {
		Framework framework = createFramework(getConfiguration(getName()));

		// update before first init (in INSTALLED state)
		updateFramework(framework);

		// update after init (in STARTING state)
		initFramework(framework);
		updateFramework(framework);
		stopFramework(framework);

		// update after start (in ACTIVE state)
		startFramework(framework);
		updateFramework(framework);
		stopFramework(framework);

		// update after stop (in RESOLVED state)
		updateFramework(framework);
	}

	public void testStartLevel() {
		Map configuration = getConfiguration(getName());
		configuration.put(Constants.FRAMEWORK_BEGINNING_STARTLEVEL, "25");
		Framework framework = createFramework(configuration);
		startFramework(framework);
		StartLevel sl = (StartLevel) getService(framework, StartLevel.class);
		if (sl == null) {
			stopFramework(framework);
			return; // cannot test without start level
		}
		assertEquals("Wrong start level after start", 25, sl.getStartLevel());
		stopFramework(framework);
	}

	public void testSystemServices() {
		Framework framework = createFramework(getConfiguration(getName()));
		startFramework(framework);
		boolean hasPackageAdmin = getService(framework, PackageAdmin.class) != null;
		boolean hasStartLevel = getService(framework, StartLevel.class) != null;
		boolean hasPermissionAdmin = getService(framework, PermissionAdmin.class) != null;
		boolean hasCondPermAdmin = getService(framework, ConditionalPermissionAdmin.class) != null;
		stopFramework(framework);
		if (!(hasPackageAdmin | hasStartLevel | hasPermissionAdmin | hasCondPermAdmin))
			return; // nothing to test; no system services available

		// check that the available system services are available when framework is initialized
		initFramework(framework);
		if (hasPackageAdmin)
			assertNotNull("PackageAdmin", getService(framework, PackageAdmin.class));
		if (hasStartLevel)
			assertNotNull("StartLevel", getService(framework, StartLevel.class));
		if (hasPackageAdmin)
			assertNotNull("PermissionAdmin", getService(framework, PermissionAdmin.class));
		if (hasPackageAdmin)
			assertNotNull("ConditionalPermissionAdmin", getService(framework, ConditionalPermissionAdmin.class));
		stopFramework(framework);
	}

	public void testStorageArea() throws BundleException, IOException {
		String testBundleLocation = "/launch.tb1.jar";
		// install a bundle into a framework
		Framework framework = createFramework(getConfiguration(getName()));
		initFramework(framework);
		long id = installBundle(framework, testBundleLocation).getBundleId();
		stopFramework(framework);

		// create another framework with same storage area; make sure bundle is still installed
		framework = createFramework(getConfiguration(getName(), false));
		initFramework(framework);
		Bundle testBundle = framework.getBundleContext().getBundle(id);
		assertNotNull("Missing installed bundle", testBundle);
		assertEquals("Wrong bundle", testBundleLocation, testBundle.getLocation());
		stopFramework(framework);

		// create another framework with same storage area using clean ONFIRSTINIT; make sure bundle is not there
		Map configuration = getConfiguration(getName(), false);
		configuration.put(Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);
		framework = createFramework(configuration);
		initFramework(framework);
		Bundle[] bundles = framework.getBundleContext().getBundles();
		assertNotNull("No bundles", bundles);
		assertEquals("Wrong number of bundles", 1, bundles.length);
		id = installBundle(framework, testBundleLocation).getBundleId();
		stopFramework(framework);
		
		// test that second init does not clean
		initFramework(framework);
		testBundle = framework.getBundleContext().getBundle(id);
		assertNotNull("Missing installed bundle", testBundle);
		assertEquals("Wrong bundle", testBundleLocation, testBundle.getLocation());
		stopFramework(framework);
	}

	public void testSystemPackagesExtra() throws BundleException, IOException {
		String pkg1 = "org.osgi.tests.pkg1";
		String pkg2 = "org.osgi.tests.pkg2";
		Map configuration = getConfiguration(getName());
		configuration.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, pkg1 + ',' + pkg2);
		Framework framework = createFramework(configuration);

		initFramework(framework);
		ExportedPackage ep1 = null, ep2 = null;
		PackageAdmin pa = (PackageAdmin) getService(framework, PackageAdmin.class);
		if (pa != null) {
			ep1 = pa.getExportedPackage(pkg1);
			assertNotNull("pkg1 is null", ep1);
			assertEquals("Wrong Exporter", 0, ep1.getExportingBundle().getBundleId());
			ep2 = pa.getExportedPackage(pkg2);
			assertNotNull("pkg2 is null", ep2);
			assertEquals("Wrong Exporter", 0, ep2.getExportingBundle().getBundleId());
		}
		Bundle testBundle = installBundle(framework, "/launch.tb1.jar");
		try {
			testBundle.start();
		} catch (BundleException e) {
			fail("Unexpected exception", e);
		}
		checkImporter(ep1, testBundle);
		checkImporter(ep2, testBundle);
		stopFramework(framework);
	}

	public void testLibraryExtensions() throws BundleException, IOException {
		Map configuration = getConfiguration(getName());
		configuration.put(Constants.FRAMEWORK_LIBRARY_EXTENSIONS, ".1, .test");
		configuration.put("nativecodetest", "1");
		Framework framework = createFramework(configuration);
		startFramework(framework);
		Bundle testBundle = installBundle(framework, "/launch.tb2.jar");
		testBundle.start();
		stopFramework(framework);

		configuration = getConfiguration(getName());
		configuration.put(Constants.FRAMEWORK_LIBRARY_EXTENSIONS, ".2, .test");
		configuration.put("nativecodetest", "2");
		framework = createFramework(configuration);
		startFramework(framework);
		testBundle = installBundle(framework, "/launch.tb2.jar");
		testBundle.start();
		stopFramework(framework);

		configuration = getConfiguration(getName());
		configuration.put(Constants.FRAMEWORK_LIBRARY_EXTENSIONS, ".3, .test");
		configuration.put("nativecodetest", "3");
		framework = createFramework(configuration);
		startFramework(framework);
		testBundle = installBundle(framework, "/launch.tb2.jar");
		testBundle.start();
		stopFramework(framework);
	}

	public void testWaitForStop() {
		Framework framework = createFramework(getConfiguration(getName()));
		startFramework(framework);
		FrameworkEvent event = null;
		try {
			event = framework.waitForStop(1000);
		}
		catch (InterruptedException e) {
			fail("Unexpected interuption", e);
		}
		assertNotNull("Wait for stop event is null", event);
		assertEquals("Wrong event type", FrameworkEvent.WAIT_TIMEDOUT, event
				.getType());
		stopFramework(framework);
	}

    static class AllPolicy extends Policy {
        static PermissionCollection all = new AllPermissionCollection();

        public PermissionCollection getPermissions(ProtectionDomain domain) {
        	// causes recursive permission check (StackOverflowError)
        	// System.out.println("Returning all permission for " + domain == null ? null : domain.getCodeSource());
        	return all;
        }
        public PermissionCollection getPermissions(CodeSource codesource) {
            System.out.println("Returning all permission for " + codesource);
            return all;
        }

        public boolean implies(ProtectionDomain domain, Permission permission) {
        	// causes recursive permission check (StackOverflowError)
        	// System.out.println("Granting permission for " + domain == null ? null : domain.getCodeSource());
        	return true;
        }

        public void refresh() {
        }
    }

    static class AllPermissionCollection extends PermissionCollection {
        private static final long serialVersionUID = 1L;
        private static Vector     list             = new Vector();

        {
            setReadOnly();
        }

        public void add(Permission permission) {
        }

        public Enumeration elements() {
            return list.elements();
        }

        public boolean implies(Permission permission) {
            return true;
        }
    }
}

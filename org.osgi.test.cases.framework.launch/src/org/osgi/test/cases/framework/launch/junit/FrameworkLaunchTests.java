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
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;
import org.osgi.test.support.OSGiTestCase;

public class FrameworkLaunchTests extends OSGiTestCase {
	private static final String STORAGEROOT = "org.osgi.test.cases.framework.launch.storageroot";
	private static final String FRAMEWORK_FACTORY = "/META-INF/services/org.osgi.framework.launch.FrameworkFactory";
	private static final String PACKAGE_ADMIN = "org.osgi.service.packageadmin.PackageAdmin";
	private static final String STARTLEVEL = "org.osgi.service.startlevel.StartLevel";
	private static final String PERMISSION_ADMIN = "org.osgi.service.permissionadmin.PermissionAdmin";
	private static final String CONDPERM_ADMIN = "org.osgi.service.condpermadmin.ConditionalPermissionAdmin";

	private static final String TEST_TRUST_REPO = "org.osgi.test.cases.framework.launch.trust.repository";

	private String frameworkFactoryClassName;
	private String rootStorageArea;
	private FrameworkFactory frameworkFactory;

	private static class BootClassLoader extends ClassLoader {
		protected BootClassLoader() {
			super(null);
		}
	}
	private static ClassLoader bootClassLoader = new BootClassLoader();

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

	private Object getService(Framework framework, String serviceClass) {
		BundleContext context = framework.getBundleContext();
		ServiceReference ref = context.getServiceReference(serviceClass);
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
		StartLevel sl = (StartLevel) getService(framework, STARTLEVEL);
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
		boolean hasPackageAdmin = getService(framework, PACKAGE_ADMIN) != null;
		boolean hasStartLevel = getService(framework, STARTLEVEL) != null;
		boolean hasPermissionAdmin = getService(framework, PERMISSION_ADMIN) != null;
		boolean hasCondPermAdmin = getService(framework, CONDPERM_ADMIN) != null;
		stopFramework(framework);
		if (!(hasPackageAdmin | hasStartLevel | hasPermissionAdmin | hasCondPermAdmin))
			return; // nothing to test; no system services available

		// check that the available system services are available when framework is initialized
		initFramework(framework);
		if (hasPackageAdmin)
			assertNotNull("PackageAdmin", getService(framework, PACKAGE_ADMIN));
		if (hasStartLevel)
			assertNotNull("StartLevel", getService(framework, STARTLEVEL));
		if (hasPackageAdmin)
			assertNotNull("PermissionAdmin", getService(framework, PERMISSION_ADMIN));
		if (hasPackageAdmin)
			assertNotNull("ConditionalPermissionAdmin", getService(framework, CONDPERM_ADMIN));
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
		PackageAdmin pa = (PackageAdmin) getService(framework, PACKAGE_ADMIN);
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

	public void testBootDelegation() throws BundleException, IOException {
		Class vmClass = null;
		try {
			vmClass = bootClassLoader.loadClass("javax.security.auth.x500.X500Principal");
		} catch (ClassNotFoundException e) {
			fail("Unexpected CNFE", e);
		}
		Map configuration = getConfiguration(getName());
		doTestBootDelegation(vmClass, configuration, "javax.security.auth.x500", true);
		doTestBootDelegation(vmClass, configuration, "javax.security.auth.*", true);
		doTestBootDelegation(vmClass, configuration, "javax.security.*", true);
		doTestBootDelegation(vmClass, configuration, "*", true);
		doTestBootDelegation(vmClass, configuration, null, false);
		doTestBootDelegation(vmClass, configuration, "junk.*", false);
	}

	private void doTestBootDelegation(Class vmClass, Map configuration, String bootDelegation, boolean fromBoot) throws BundleException, IOException {
		if (bootDelegation != null)
			configuration.put(Constants.FRAMEWORK_BOOTDELEGATION, bootDelegation);
		else
			configuration.remove(Constants.FRAMEWORK_BOOTDELEGATION);
		Framework framework = createFramework(configuration);
		startFramework(framework);
		Bundle testBundle = installBundle(framework, "/launch.tb3.jar");
		Class testClass = null;
		try {
			 testClass = testBundle.loadClass("javax.security.auth.x500.X500Principal");
		} catch (ClassNotFoundException e) {
			fail("Unexpected CNFE", e);
		}
		if (fromBoot)
			assertEquals("Unexpected class", vmClass, testClass);
		else
			assertFalse("Unexpected class", vmClass.equals(testClass));
		stopFramework(framework);
	}

	public void testParentClassLoader() throws BundleException, IOException {
		Map configuration = getConfiguration(getName());
		configuration.put(Constants.FRAMEWORK_BOOTDELEGATION, "*");
		doTestParentClassLoader(configuration, null, Bundle.class.getName(), true);
		doTestParentClassLoader(configuration, Constants.FRAMEWORK_BUNDLE_PARENT_BOOT, Bundle.class.getName(), true);
		doTestParentClassLoader(configuration, Constants.FRAMEWORK_BUNDLE_PARENT_EXT, Bundle.class.getName(), true);
		// here we assume the framework jar was placed on the app class loader.
		doTestParentClassLoader(configuration, Constants.FRAMEWORK_BUNDLE_PARENT_APP, Bundle.class.getName(), false);
		doTestParentClassLoader(configuration, Constants.FRAMEWORK_BUNDLE_PARENT_FRAMEWORK, Bundle.class.getName(), false);
	}

	private void doTestParentClassLoader(Map configuration, String parentType, String className, boolean fail) throws BundleException, IOException {
		if (parentType != null)
			configuration.put(Constants.FRAMEWORK_BUNDLE_PARENT, parentType);
		else
			configuration.remove(Constants.FRAMEWORK_BUNDLE_PARENT);
		Framework framework = createFramework(configuration);
		startFramework(framework);
		Bundle testBundle = installBundle(framework, "/launch.tb3.jar");
		try {
			 testBundle.loadClass(className);
			 if (fail)
				 fail("Should not be able to load class: " + className);
		} catch (ClassNotFoundException e) {
			if (!fail)
				fail("Unexpected CNFE", e);
		}
		stopFramework(framework);
	}

	public void testTrustRepositories() throws BundleException, IOException {
		BundleContext context = getBundleContextWithoutFail();
		String testRepo = context != null ? context.getProperty(TEST_TRUST_REPO) : System.getProperty(TEST_TRUST_REPO);
		if (testRepo == null)
			fail("Must set property to test: \"" + TEST_TRUST_REPO + "\"");
		Map configuration = getConfiguration(getName());
		doTestTrustRepository(configuration, null, false);
		doTestTrustRepository(configuration, testRepo, true);
	}


	private void doTestTrustRepository(Map configuration, String testRepo, boolean trusted) throws BundleException, IOException {
		if (testRepo != null)
			configuration.put(Constants.FRAMEWORK_TRUST_REPOSITORIES, testRepo);
		else
			configuration.remove(Constants.FRAMEWORK_TRUST_REPOSITORIES);
		Framework framework = createFramework(configuration);
		startFramework(framework);
		Bundle testBundle = installBundle(framework, "/launch.tb3.jar");
		Map signers = testBundle.getSignerCertificates(Bundle.SIGNERS_ALL);
		assertEquals("Expecting 1 signer", 1, signers.size());
		Map trustedSigners = testBundle.getSignerCertificates(Bundle.SIGNERS_TRUSTED);
		if (trusted)
			assertEquals("Expecting 1 signer", 1, trustedSigners.size());
		else
			assertEquals("Expecting 0 signers", 0, trustedSigners.size());
	}

	public void testExecPermission() throws BundleException, IOException {
		File testOutputFile = new File(rootStorageArea, getName() + File.separator + "execPermissions.out");
		Map configuration = getConfiguration(getName());
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().indexOf("windows") >= 0)
			configuration.put(Constants.FRAMEWORK_EXECPERMISSION, "copy ${abspath} " + testOutputFile.getAbsolutePath());
		else
			configuration.put(Constants.FRAMEWORK_EXECPERMISSION, "cp ${abspath} " + testOutputFile.getAbsolutePath());
		configuration.put(Constants.FRAMEWORK_LIBRARY_EXTENSIONS, ".1, .test");
		configuration.put("nativecodetest", "1");
		Framework framework = createFramework(configuration);
		startFramework(framework);
		Bundle testBundle = installBundle(framework, "/launch.tb2.jar");
		testBundle.start();
		stopFramework(framework);
		assertTrue("File does not exist: " + testOutputFile.getAbsolutePath(), testOutputFile.exists());
	}
}

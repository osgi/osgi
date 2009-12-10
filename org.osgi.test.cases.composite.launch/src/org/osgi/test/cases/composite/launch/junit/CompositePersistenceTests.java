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
package org.osgi.test.cases.composite.launch.junit;

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
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.service.composite.CompositeAdmin;
import org.osgi.service.composite.CompositeBundle;
import org.osgi.service.composite.CompositeConstants;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;
import org.osgi.test.support.OSGiTestCase;

public class CompositePersistenceTests extends OSGiTestCase {
	private static final String STORAGEROOT = "org.osgi.test.cases.composite.launch";
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
	
	private Object getService(BundleContext context, String serviceName) {
		assertNotNull("context is null!", context);
		ServiceReference ref = context.getServiceReference(serviceName);
		assertNotNull(serviceName + " reference is null!", ref);
		Object service = context.getService(ref);
		assertNotNull(serviceName + " is null!", service);
		return service;
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
			if ("true".equals(System.getProperty("noframework")))
				return null;
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

	private Bundle installBundle(Framework framework, String bundle) {
		BundleContext fwkContext = framework.getBundleContext();
		assertNotNull("Framework context is null", fwkContext);
		URL input = getBundleInput(bundle);
		assertNotNull("Cannot find resource: " + bundle, input);
		try {
			return fwkContext.installBundle(bundle, input.openStream());
		} catch (Exception e) {
			fail("Unexpected exception installing: " + bundle, e);
			return null;
		}
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

	private CompositeBundle createCompositeBundle(CompositeAdmin factory, String location, Map compositeManifest, Map configuration) {
		return createCompositeBundle(factory, location, compositeManifest, configuration, false);
	}

	private CompositeBundle createCompositeBundle(CompositeAdmin factory, String location, Map compositeManifest, Map configuration, boolean expectedFail) {
		if (configuration == null)
			configuration = new HashMap();

		if (compositeManifest == null)
			compositeManifest = new HashMap();

		if (compositeManifest.get(Constants.BUNDLE_SYMBOLICNAME) == null)
			compositeManifest.put(Constants.BUNDLE_SYMBOLICNAME, location + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=true");

		CompositeBundle composite = null;
		try {
			composite = factory.installCompositeBundle(location, compositeManifest, configuration);
			if (expectedFail)
				fail("Expected to fail composite installation: " + location);
		} catch (BundleException e) {
			if (!expectedFail)
				fail("Unexpected exception creating composite bundle", e); //$NON-NLS-1$
			return null;
		}
		assertNotNull("Composite is null", composite); //$NON-NLS-1$
		assertEquals("Wrong composite location", location, composite.getLocation()); //$NON-NLS-1$
		assertNotNull("Compoisite System Bundle context must not be null", composite.getSystemBundleContext());
		assertEquals("Wrong state for SystemBundle", Bundle.STARTING, composite.getSystemBundleContext().getBundle().getState()); //$NON-NLS-1$
		return composite;
	}

	protected Bundle installConstituent(CompositeBundle composite, String location, String name) {
		return installConstituent(composite, location, name, false);
	}

	protected Bundle installConstituent(CompositeBundle composite, String location, String name, boolean expectFail) {
		try {
			URL content = getBundleInput(name);
			String externalForm = content.toExternalForm();
			if (location == null)
				location = externalForm;
			BundleContext context = composite.getSystemBundleContext();
			Bundle result = (externalForm.equals(location)) ? context.installBundle(location) : context.installBundle(location, content.openStream());		
			if (expectFail)
				fail("Expected a failure to install test bundle: " + name);
			return result;
		} catch (BundleException e) {
			if (!expectFail)
				fail("failed to install test bundle", e); //$NON-NLS-1$
		} catch (IOException e) {
			fail("failed to install test bundle", e); //$NON-NLS-1$
		}
		return null;
	}

	private void startBundle(Bundle bundle, boolean expectFail) {
		try {
			bundle.start();
			if (expectFail)
				fail("Expected to fail starting bundle: " + bundle.getLocation());
		} catch (BundleException e) {
			if (!expectFail)
				fail("Unexpected failure to start bundle: " + bundle.getLocation());
		}
	}

	private void refreshPackages(BundleContext context, Bundle[] bundles) {
		ServiceReference ref = context.getServiceReference(PackageAdmin.class.getName());
		assertNotNull("PackageAdmin ref is null.", ref);
		PackageAdmin pa = (PackageAdmin) context.getService(ref);
		assertNotNull("PackageAdmin service is null.", pa);
		final boolean[] monitor = new boolean[] {false};
		FrameworkListener l = new FrameworkListener() {
			public void frameworkEvent(FrameworkEvent event) {
				if (event.getType() != FrameworkEvent.PACKAGES_REFRESHED)
					return;
				synchronized (monitor) {
					monitor[0] = true;
					monitor.notify();
				}
			}
		};
		context.addFrameworkListener(l);
		try {
			pa.refreshPackages(bundles);
			synchronized (monitor) {
				if (!monitor[0])
					monitor.wait(5000);
				if (!monitor[0])
					fail("Failed to finish refresh in a reasonable amount of time.");
			}
		} catch (InterruptedException e) {
			fail("Unexpected interruption", e);
		} finally {
			context.ungetService(ref);
			context.removeFrameworkListener(l);
		}
	}

	public void testBasicPersistence01() {
		// create a root framework
		Framework rootFramework = createFramework(getConfiguration(getName()));
		initFramework(rootFramework);
		CompositeAdmin compositeAdmin = (CompositeAdmin) getService(rootFramework.getBundleContext(), CompositeAdmin.class.getName());
		// create a composite to test persistence
		CompositeBundle composite = createCompositeBundle(compositeAdmin, getName(), null, null);
		long compID = composite.getBundleId(); // save the id for later
		try {
			composite.start();
		} catch (BundleException e) {
			fail("Failed to mark composite for start", e);
		}
		assertFalse("Composite is active", composite.getState() == Bundle.ACTIVE);
		startFramework(rootFramework);
		assertEquals("Composite is not active", Bundle.ACTIVE, composite.getState());
		stopFramework(rootFramework);
		assertFalse("Composite is active", composite.getState() == Bundle.ACTIVE);

		// reify the root framework from the previously used storage area
		rootFramework = createFramework(getConfiguration(getName(), false));
		initFramework(rootFramework);
		// find the persistent composite
		composite = (CompositeBundle) rootFramework.getBundleContext().getBundle(compID);
		assertFalse("Composite is active", composite.getState() == Bundle.ACTIVE);
		startFramework(rootFramework);
		assertEquals("Composite is not active", Bundle.ACTIVE, composite.getState());
		stopFramework(rootFramework);
		assertFalse("Composite is active", composite.getState() == Bundle.ACTIVE);
	}

	public void testBasicPersistence02() {
		// get and save a configuration for the root framework
		Map configuration = getConfiguration(getName());
		// create a root framework
		Framework rootFramework = createFramework(configuration);
		initFramework(rootFramework);
		CompositeAdmin compositeAdmin = (CompositeAdmin) getService(rootFramework.getBundleContext(), CompositeAdmin.class.getName());
		// create a composite to test persistence
		CompositeBundle composite = createCompositeBundle(compositeAdmin, getName(), null, null);
		long compID = composite.getBundleId(); // save the id for later
		try {
			composite.start();
		} catch (BundleException e) {
			fail("Failed to mark composite for start", e);
		}

		// install some bundles to test persistence
		Bundle tb1 = installConstituent(composite, null, "/launch.tb1.jar");
		Bundle tb2 = installBundle(rootFramework, "/launch.tb2.jar");
		// save the ids for later
		long tb1ID = tb1.getBundleId();
		long tb2ID = tb2.getBundleId();

		assertFalse("Composite is active", composite.getState() == Bundle.ACTIVE);
		startFramework(rootFramework);
		startBundle(tb1, false);
		startBundle(tb2, true); // expect the bundle to fail to start because it cannot resolve
		assertEquals("Composite is not active", Bundle.ACTIVE, composite.getState());
		stopFramework(rootFramework);
		assertFalse("Composite is active", composite.getState() == Bundle.ACTIVE);

		// reify the root framework from the previously used storage area
		rootFramework = createFramework(configuration);
		initFramework(rootFramework);
		composite = (CompositeBundle) rootFramework.getBundleContext().getBundle(compID);

		// test that the bundles are still installed
		tb1 = composite.getSystemBundleContext().getBundle(tb1ID);
		assertNotNull(tb1);
		tb2 = rootFramework.getBundleContext().getBundle(tb2ID);
		assertNotNull(tb2);

		assertFalse("Composite is active", composite.getState() == Bundle.ACTIVE);
		startFramework(rootFramework);
		assertEquals("Composite is not active", Bundle.ACTIVE, composite.getState());
		assertEquals("tb1 should be active", Bundle.ACTIVE, tb1.getState());
		assertEquals("tb2 should be installed", Bundle.INSTALLED, tb2.getState()); // still cannot resolve the bundle

		// update the composite to allow tb2 to resolve
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.launch.tb1");
		try {
			composite.update(manifest);
		} catch (BundleException e) {
			fail("Failed to update composite", e);
		}

		refreshPackages(rootFramework.getBundleContext(), new Bundle[] {composite});
		startBundle(tb2, false); // we should be able to start tb2 now.

		assertEquals("tb2 should be active", Bundle.ACTIVE, tb2.getState());
		stopFramework(rootFramework);
		assertFalse("Composite is active", composite.getState() == Bundle.ACTIVE);

		// reify the root framework from the previously used storage area
		rootFramework = createFramework(configuration);
		initFramework(rootFramework);
		composite = (CompositeBundle) rootFramework.getBundleContext().getBundle(compID);

		// test that the bundles are still installed
		tb1 = composite.getSystemBundleContext().getBundle(tb1ID);
		assertNotNull(tb1);
		tb2 = rootFramework.getBundleContext().getBundle(tb2ID);
		assertNotNull(tb2);

		startFramework(rootFramework);
		assertEquals("Composite is not active", Bundle.ACTIVE, composite.getState());
		assertEquals("tb1 should be active", Bundle.ACTIVE, tb1.getState());
		assertEquals("tb2 should be active", Bundle.ACTIVE, tb2.getState());
		try {
			tb1.stop(); // test persistently stopping a tb1
		} catch (BundleException e) {
			fail("Failed to stop bundle", e);
		}
		stopFramework(rootFramework);
		assertFalse("Composite is active", composite.getState() == Bundle.ACTIVE);

		// reify the root framework from the previously used storage area
		rootFramework = createFramework(configuration);
		initFramework(rootFramework);
		composite = (CompositeBundle) rootFramework.getBundleContext().getBundle(compID);

		// test that the bundles are still installed
		tb1 = composite.getSystemBundleContext().getBundle(tb1ID);
		assertNotNull(tb1);
		tb2 = rootFramework.getBundleContext().getBundle(tb2ID);
		assertNotNull(tb2);

		startFramework(rootFramework);
		assertEquals("Composite is not active", Bundle.ACTIVE, composite.getState());
		// should be resolved but not active
		assertEquals("tb1 should not be active", Bundle.RESOLVED, tb1.getState());
		assertEquals("tb2 should be active", Bundle.ACTIVE, tb2.getState());

		try {
			composite.stop(); // test persistent stop of composite
		} catch (BundleException e) {
			fail("Failed to stop bundle", e);
		}

		stopFramework(rootFramework);
		assertFalse("Composite is active", composite.getState() == Bundle.ACTIVE);

		// reify the root framework from the previously used storage area
		rootFramework = createFramework(configuration);
		initFramework(rootFramework);
		composite = (CompositeBundle) rootFramework.getBundleContext().getBundle(compID);

		tb1 = composite.getSystemBundleContext().getBundle(tb1ID);
		assertNotNull(tb1);
		tb2 = rootFramework.getBundleContext().getBundle(tb2ID);
		assertNotNull(tb2);

		startFramework(rootFramework);
		// Both the composite and constituent bundle tb1 should not be active
		assertEquals("Composite should not be active", Bundle.RESOLVED, composite.getState());
		assertEquals("tb1 should not be active", Bundle.RESOLVED, tb1.getState());
		assertEquals("tb2 should be active", Bundle.ACTIVE, tb2.getState());
		try {
			tb1.uninstall();
			tb2.uninstall();
		} catch (BundleException e) {
			fail("Failed to uninstall bundle", e);
		}
		stopFramework(rootFramework);

		// reify the root framework from the previously used storage area
		rootFramework = createFramework(configuration);
		initFramework(rootFramework);
		composite = (CompositeBundle) rootFramework.getBundleContext().getBundle(compID);
		assertNotNull(composite);
		tb1 = composite.getSystemBundleContext().getBundle(tb1ID);
		assertNull(tb1);
		tb2 = rootFramework.getBundleContext().getBundle(tb2ID);
		assertNull(tb2);
		// test perstent uninstall of composite
		try {
			composite.uninstall();
		} catch (BundleException e) {
			fail("Failed to uninstall bundle", e);
		}
		stopFramework(rootFramework);

		// reify the root framework from the previously used storage area
		rootFramework = createFramework(configuration);
		initFramework(rootFramework);
		composite = (CompositeBundle) rootFramework.getBundleContext().getBundle(compID);
		assertNull(composite);
		stopFramework(rootFramework);
	}

	private void setInitialStartLevel(BundleContext context, int level) {
		ServiceReference ref = context.getServiceReference(StartLevel.class.getName());
		assertNotNull(ref);
		StartLevel sl = (StartLevel) context.getService(ref);
		assertNotNull(sl);
		try {
			sl.setInitialBundleStartLevel(level);
		} finally {
			context.ungetService(ref);
		}
	}

	private void setBundleStartLevel(BundleContext context, Bundle bundle, int level) {
		ServiceReference ref = context.getServiceReference(StartLevel.class.getName());
		assertNotNull(ref);
		StartLevel sl = (StartLevel) context.getService(ref);
		assertNotNull(sl);
		try {
			sl.setBundleStartLevel(bundle, level);
		} finally {
			context.ungetService(ref);
		}
	}

	private void setStartLevel(BundleContext context, int level) {
		ServiceReference ref = context.getServiceReference(StartLevel.class.getName());
		assertNotNull(ref);
		StartLevel sl = (StartLevel) context.getService(ref);
		assertNotNull(sl);
		try {
			sl.setStartLevel(level);
		} finally {
			context.ungetService(ref);
		}
	}

	private int getInitialStartLevel(BundleContext context) {
		ServiceReference ref = context.getServiceReference(StartLevel.class.getName());
		assertNotNull(ref);
		StartLevel sl = (StartLevel) context.getService(ref);
		assertNotNull(sl);
		try {
			return sl.getInitialBundleStartLevel();
		} finally {
			context.ungetService(ref);
		}
	}

	private int getBundleStartLevel(BundleContext context, Bundle bundle) {
		ServiceReference ref = context.getServiceReference(StartLevel.class.getName());
		assertNotNull(ref);
		StartLevel sl = (StartLevel) context.getService(ref);
		assertNotNull(sl);
		try {
			return sl.getBundleStartLevel(bundle);
		} finally {
			context.ungetService(ref);
		}
	}

	private int getStartLevel(BundleContext context) {
		ServiceReference ref = context.getServiceReference(StartLevel.class.getName());
		assertNotNull(ref);
		StartLevel sl = (StartLevel) context.getService(ref);
		assertNotNull(sl);
		try {
			return sl.getStartLevel();
		} finally {
			context.ungetService(ref);
		}
	}

	public void testStartLevelPersistence01() {
		// get and save a configuration for the root framework
		Map configuration = getConfiguration(getName());
		// create a root framework
		Framework rootFramework = createFramework(configuration);
		initFramework(rootFramework);
		CompositeAdmin compositeAdmin = (CompositeAdmin) getService(rootFramework.getBundleContext(), CompositeAdmin.class.getName());
		// create a composite to test persistence
		Map compConfiguration = new HashMap();
		compConfiguration.put(Constants.FRAMEWORK_BEGINNING_STARTLEVEL, "10");
		CompositeBundle composite = createCompositeBundle(compositeAdmin, getName(), null, compConfiguration);
		long compID = composite.getBundleId(); // save the id for later
		try {
			composite.start();
		} catch (BundleException e) {
			fail("Failed to mark composite for start", e);
		}
		setInitialStartLevel(composite.getSystemBundleContext(), 5);
		stopFramework(rootFramework);
		assertFalse("Composite is active", composite.getState() == Bundle.ACTIVE);

		// reify the root framework from the previously used storage area
		rootFramework = createFramework(configuration);
		startFramework(rootFramework);
		assertEquals("Wrong framework start level", 1, getStartLevel(rootFramework.getBundleContext()));
		assertEquals("Wrong initial start level", 1, getInitialStartLevel(rootFramework.getBundleContext()));
		composite = (CompositeBundle) rootFramework.getBundleContext().getBundle(compID);
		assertNotNull(composite);
		assertEquals("Composite is not active", Bundle.ACTIVE, composite.getState());
		assertEquals("Wrong framework start level", 10, getStartLevel(composite.getSystemBundleContext()));
		assertEquals("Wrong intial start level", 5, getInitialStartLevel(composite.getSystemBundleContext()));
		// install some bundles to test persistence of start-level
		Bundle tb1 = installConstituent(composite, null, "/launch.tb1.jar");
		Bundle tb2 = installConstituent(composite, null, "/launch.tb2.jar");
		Bundle tb1InRoot = installBundle(rootFramework, "/launch.tb1.jar");
		// save the ids for later
		long tb1ID = tb1.getBundleId();
		long tb2ID = tb2.getBundleId();
		long tb1InRootID = tb1InRoot.getBundleId();

		// make sure their initial start-level is correct
		assertEquals("Wrong bundle startlevel", 5, getBundleStartLevel(composite.getSystemBundleContext(), tb1));
		assertEquals("Wrong bundle startlevel", 5, getBundleStartLevel(composite.getSystemBundleContext(), tb2));
		assertEquals("Wrong bundle startlevel", 1, getBundleStartLevel(rootFramework.getBundleContext(), tb1InRoot));

		setBundleStartLevel(composite.getSystemBundleContext(), tb1, 6);
		setBundleStartLevel(composite.getSystemBundleContext(), tb2, 6);
		setBundleStartLevel(rootFramework.getBundleContext(), tb1InRoot, 2);

		stopFramework(rootFramework);
		assertFalse("Composite is active", composite.getState() == Bundle.ACTIVE);

		// reify the root framework from the previously used storage area
		rootFramework = createFramework(configuration);
		startFramework(rootFramework);
		composite = (CompositeBundle) rootFramework.getBundleContext().getBundle(compID);
		assertNotNull(composite);
		assertEquals("Composite is not active", Bundle.ACTIVE, composite.getState());

		tb1 = composite.getSystemBundleContext().getBundle(tb1ID);
		assertNotNull(tb1);
		tb2 = composite.getSystemBundleContext().getBundle(tb2ID);
		assertNotNull(tb2);
		tb1InRoot = rootFramework.getBundleContext().getBundle(tb1InRootID);
		assertNotNull(tb1InRoot);

		// make sure their start-level is correct
		assertEquals("Wrong bundle startlevel", 6, getBundleStartLevel(composite.getSystemBundleContext(), tb1));
		assertEquals("Wrong bundle startlevel", 6, getBundleStartLevel(composite.getSystemBundleContext(), tb2));
		assertEquals("Wrong bundle startlevel", 2, getBundleStartLevel(rootFramework.getBundleContext(), tb1InRoot));

		stopFramework(rootFramework);
		assertFalse("Composite is active", composite.getState() == Bundle.ACTIVE);
	}
}

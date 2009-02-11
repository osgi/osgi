package org.osgi.test.cases.framework.junit.launch;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.launch.Framework;
import org.osgi.test.support.OSGiTestCase;

public abstract class AbstractFrameworkLaunchTests extends OSGiTestCase {
	protected static final String FRAMEWORK_IMPL = "org.osgi.framework.launch.test.class";

	protected String frameworkClass;
	protected String root;
	protected Constructor frameworkConstructor;
	
	protected void setUp() throws Exception {
		super.setUp();
		frameworkClass = getProperty(FRAMEWORK_IMPL);
		assertNotNull("Must set: " + FRAMEWORK_IMPL, frameworkClass);
		frameworkConstructor = getFrameworkConstructor();

		root = getStorageAreaRoot();
		assertNotNull("No storage area root found", root);
		File rootFile = new File(root);
		assertFalse("Root storage area is not a directory: " + rootFile.getPath(), rootFile.exists() && !rootFile.isDirectory());
		if (!rootFile.isDirectory())
			assertTrue("Could not create root directory: " + rootFile.getPath(), rootFile.mkdirs());
	}

	protected abstract String getProperty(String property);
	protected abstract String getStorageAreaRoot();
	protected abstract Class loadFrameworkClass(String className) throws ClassNotFoundException;

	private Constructor getFrameworkConstructor() {
		try {
			Class clazz = loadFrameworkClass(frameworkClass);
			return clazz.getConstructor(new Class[] {Map.class});
		} catch (Exception e) {
			fail("Failed to get the framework constructor", e);
		}
		return null;
	}

	private File getStorageArea(String testName) {
		File storageArea = new File(root, testName);
		assertTrue("Could not clean up storage area: " + storageArea.getPath(), delete(storageArea));
		assertTrue("Could not create storage area directory: " + storageArea.getPath(), storageArea.mkdirs());
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
			framework = (Framework) frameworkConstructor.newInstance(new Object[] {configuration});
		}
		catch (Exception e) {
			fail("Failed to construct the framework", e);
		}
		assertEquals("Wrong state for newly constructed framework", Bundle.INSTALLED, framework.getState());
		return framework;
	}

	private Map getConfiguration(String testName) {
		Map configuration = new HashMap();
		if (testName != null)
			configuration.put(Constants.FRAMEWORK_STORAGE, getStorageArea(testName).getAbsolutePath());
		return configuration;
	}

	private void initFramework(Framework framework) {
		try {
			framework.init();
		}
		catch (BundleException e) {
			fail("Unexpected BundleException initializing", e);
		}
		assertEquals("Wrong framework state after init", Bundle.STARTING, framework.getState());
	}

	private void startFramework(Framework framework) {
		
	}

	private void stopFramework(Framework framework) {
		try {
			framework.stop();
			FrameworkEvent event = framework.waitForStop(10000);
			assertNotNull("FrameworkEvent is null", event);
			assertEquals("Wrong event type", FrameworkEvent.STOPPED, event.getType());
		}
		catch (BundleException e) {
			fail("Unexpected BundleException initializing", e);
		}
		catch (InterruptedException e) {
			fail("Unexpected InterruptedException initializing", e);
		}
		assertEquals("Wrong framework state after init", Bundle.RESOLVED, framework.getState());
	}


	public void testBasicCreate() {
		if (frameworkClass == null)
			return;
		createFramework(getConfiguration(getName()));
	}

	public void testInitStop() {
		if (frameworkClass == null)
			return;
		Framework framework = createFramework(getConfiguration(getName()));
		initFramework(framework);
		stopFramework(framework);
	}
}

package org.osgi.test.cases.framework.junit.launch;

import org.osgi.framework.BundleContext;

public class EmbeddedFrameworkLaunchTests extends AbstractFrameworkLaunchTests {
	protected static final String STORAGEROOT = "org.osgi.framework.launch.test.storageroot";

	protected String getProperty(String property) {
		BundleContext context = getBundleContextWithoutFail();
		return context == null ? System.getProperty(property) : context.getProperty(property);
	}

	protected String getStorageAreaRoot() {
		BundleContext context = getBundleContextWithoutFail();
		if (context == null) {
			String storageroot = System.getProperty(STORAGEROOT);
			assertNotNull("Must set property: " + STORAGEROOT, storageroot);
			return storageroot;
		}
		return context.getDataFile("storageroot").getAbsolutePath();
	}

	protected Class loadFrameworkClass(String className)
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
}

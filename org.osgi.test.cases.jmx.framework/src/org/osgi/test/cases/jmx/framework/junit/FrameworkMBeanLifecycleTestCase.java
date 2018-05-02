package org.osgi.test.cases.jmx.framework.junit;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.jmx.framework.FrameworkMBean;
import org.osgi.test.support.OSGiTestCaseProperties;

public class FrameworkMBeanLifecycleTestCase extends MBeanGeneralTestCase {

	private static final String FRAMEWORK_FACTORY = "/META-INF/services/org.osgi.framework.launch.FrameworkFactory";
	private static final String	STORAGEROOT				= "org.osgi.test.cases.jmx.framework.storageroot";
	private static final String DEFAULT_STORAGEROOT = "generated/testframeworkstorage";
	private static final String	SYSTEM_PACKAGES_EXTRA	= "org.osgi.test.cases.jmx.framework.system.packages.extra";

	private Framework framework;
	private String frameworkFactoryClassName;
	private FrameworkFactory frameworkFactory;
	private String rootStorageArea;
	private ExecutorService executor;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		frameworkFactoryClassName = getFrameworkFactoryClassName();
		assertNotNull("Could not find framework factory class", frameworkFactoryClassName);
		frameworkFactory = getFrameworkFactory();

		rootStorageArea = getStorageAreaRoot();
		assertNotNull("No storage area root found", rootStorageArea);

		File rootFile = new File(rootStorageArea);
		delete(rootFile);
		assertFalse("Root storage area is not a directory: " + rootFile.getPath(), rootFile.exists() && !rootFile.isDirectory());
		if (!rootFile.isDirectory())
			assertTrue("Could not create root directory: " + rootFile.getPath(), rootFile.mkdirs());

		Map<String, String> configuration = getConfiguration();
		configuration.put(Constants.FRAMEWORK_STORAGE, rootFile.getAbsolutePath());

		framework = createFramework(configuration);
		initFramework();
		startFramework();

		installFramework();

		executor = Executors.newFixedThreadPool(1);
	}

	public void testShutdown() throws Exception {
		BundleContext context = framework.getBundleContext();

		MBeanServer newMBeanServer = MBeanServerFactory.newMBeanServer();//use new mbean server ManagementFactory.getPlatformMBeanServer();

		ServiceRegistration registration = context.registerService(MBeanServer.class.getCanonicalName(), newMBeanServer, null);
		String key = MBeanServer.class.getCanonicalName();
		ServiceReference reference = context.getServiceReference(key);
		assertNotNull(reference);
		MBeanServer mBeanServer = (MBeanServer) context.getService(reference);
		assertNotNull(mBeanServer);

		ObjectName objectName = waitForRegistering(newMBeanServer, new ObjectName(FrameworkMBean.OBJECTNAME));
        assertNotNull("Framework MBean is not registered", objectName);

		FrameworkMBean mbean = MBeanServerInvocationHandler
				.newProxyInstance(mBeanServer, objectName, FrameworkMBean.class, false);

		context.ungetService(reference);

		Future<FrameworkEvent> waitForStopFuture = waitForStop();
		mbean.shutdownFramework();

		FrameworkEvent event = waitForStopFuture.get();
		assertTrue("event indicated that framework was not moved to stopped state when shutdown was called",
					event.getType() == FrameworkEvent.STOPPED);
		assertTrue("framework was not moved to stopped state when was shutdown",
					framework.getState() == Bundle.RESOLVED);
	}

	public void testRestart() throws Exception {
		BundleContext context = framework.getBundleContext();

		MBeanServer newMBeanServer = MBeanServerFactory.newMBeanServer();

		ServiceRegistration registration = context.registerService(MBeanServer.class.getCanonicalName(), newMBeanServer, null);
		String key = MBeanServer.class.getCanonicalName();
		ServiceReference reference = context.getServiceReference(key);
		assertNotNull(reference);
		MBeanServer mBeanServer = (MBeanServer) context.getService(reference);
		assertNotNull(mBeanServer);

		ObjectName objectName = waitForRegistering(newMBeanServer, new ObjectName(FrameworkMBean.OBJECTNAME));
        assertNotNull("Framework MBean is not registered", objectName);

		FrameworkMBean mbean = MBeanServerInvocationHandler
				.newProxyInstance(mBeanServer, objectName, FrameworkMBean.class, false);

		context.ungetService(reference);

		Future<FrameworkEvent> waitForStopFuture = waitForStop();
		mbean.restartFramework();

		FrameworkEvent event = waitForStopFuture.get();
		assertTrue("event indicated that framework was not moved to stopped state when shutdown was called",
					event.getType() == FrameworkEvent.STOPPED_UPDATE);

		boolean started = false;
		int count = 100;
		while (count > 0 && !started) {
			started = framework.getState() == Bundle.ACTIVE;
			count--;
			Thread.sleep(100);
		}
		assertTrue("Framework was not started", started);

		stopFramework();
	}

	public void testUpdate() throws Exception {
		BundleContext context = framework.getBundleContext();

		MBeanServer newMBeanServer = MBeanServerFactory.newMBeanServer();

		ServiceRegistration registration = context.registerService(
				MBeanServer.class.getCanonicalName(), newMBeanServer, null);
		String key = MBeanServer.class.getCanonicalName();
		ServiceReference reference = context.getServiceReference(key);
		assertNotNull(reference);
		MBeanServer mBeanServer = (MBeanServer) context.getService(reference);
		assertNotNull(mBeanServer);

		ObjectName objectName = waitForRegistering(newMBeanServer, new ObjectName(FrameworkMBean.OBJECTNAME));
        assertNotNull("Framework MBean is not registered", objectName);

		FrameworkMBean mbean = MBeanServerInvocationHandler
				.newProxyInstance(mBeanServer, objectName, FrameworkMBean.class, false);

		context.ungetService(reference);

		Future<FrameworkEvent> waitForStopFuture = waitForStop();
		mbean.updateFramework();

		FrameworkEvent event = waitForStopFuture.get();
		assertTrue("event indicated that framework was not moved to stopped state when shutdown was called",
					event.getType() == FrameworkEvent.STOPPED_UPDATE);

		boolean started = false;
		int count = 100;
		while (count > 0 && !started) {
			started = framework.getState() == Bundle.ACTIVE;
			count--;
			Thread.sleep(100);
		}
		assertTrue("Framework was not started", started);

		stopFramework();
	}

	public void testAll() throws Exception {
		BundleContext context = framework.getBundleContext();

		MBeanServer newMBeanServer = MBeanServerFactory.newMBeanServer();

		ServiceRegistration registration = context.registerService(
				MBeanServer.class.getCanonicalName(), newMBeanServer, null);
		String key = MBeanServer.class.getCanonicalName();
		ServiceReference reference = context.getServiceReference(key);
		assertNotNull(reference);
		MBeanServer mBeanServer = (MBeanServer) context.getService(reference);
		assertNotNull(mBeanServer);

		ObjectName objectName = waitForRegistering(newMBeanServer, new ObjectName(FrameworkMBean.OBJECTNAME));
        assertNotNull("Framework MBean is not registered", objectName);

		FrameworkMBean mbean = MBeanServerInvocationHandler
				.newProxyInstance(mBeanServer, objectName, FrameworkMBean.class, false);

		context.ungetService(reference);

		Future<FrameworkEvent> waitForStopFuture = waitForStop();
		mbean.restartFramework();

		FrameworkEvent event = waitForStopFuture.get();
		assertTrue(
				"event indicated that framework was not moved to stopped state when shutdown was called",
				event.getType() == FrameworkEvent.STOPPED_UPDATE);

		boolean started = false;
		int count = 100;
		while (count > 0 && !started) {
			started = framework.getState() == Bundle.ACTIVE;
			count--;
			Thread.sleep(100);
		}
		assertTrue("Framework was not started", started);

		context = framework.getBundleContext();

		registration = context.registerService(MBeanServer.class
				.getCanonicalName(), newMBeanServer, null);
		reference = context.getServiceReference(key);
		assertNotNull(reference);
		mBeanServer = (MBeanServer) context.getService(reference);
		assertNotNull(mBeanServer);

        ObjectName objectName2 = waitForRegistering(newMBeanServer, new ObjectName(FrameworkMBean.OBJECTNAME));
		assertNotNull("Framework MBean is not registered", objectName2);

		mbean = MBeanServerInvocationHandler.newProxyInstance(
				mBeanServer, objectName2, FrameworkMBean.class, false);

		context.ungetService(reference);

		waitForStopFuture = waitForStop();
		mbean.updateFramework();

		event = waitForStopFuture.get();
		assertTrue(
				"event indicated that framework was not moved to stopped state when shutdown was called",
				event.getType() == FrameworkEvent.STOPPED_UPDATE);

		started = false;
		count = 100;
		while (count > 0 && !started) {
			started = framework.getState() == Bundle.ACTIVE;
			count--;
			Thread.sleep(100);
		}
		assertTrue("Framework was not started", started);

		context = framework.getBundleContext();

		registration = context.registerService(MBeanServer.class
				.getCanonicalName(), newMBeanServer, null);
		reference = context.getServiceReference(key);
		assertNotNull(reference);
		mBeanServer = (MBeanServer) context.getService(reference);
		assertNotNull(mBeanServer);

        ObjectName objectName3 = waitForRegistering(newMBeanServer, new ObjectName(FrameworkMBean.OBJECTNAME));
		assertNotNull("Framework MBean is not registered", objectName3);

		mbean = MBeanServerInvocationHandler.newProxyInstance(
				mBeanServer, objectName3, FrameworkMBean.class, false);

		context.ungetService(reference);

		waitForStopFuture = waitForStop();
		mbean.shutdownFramework();

		event = waitForStopFuture.get();
		assertTrue(
				"event indicated that framework was not moved to stopped state when shutdown was called",
				event.getType() == FrameworkEvent.STOPPED);
		assertTrue(
				"framework was not moved to stopped state when was shutdown",
				framework.getState() == Bundle.RESOLVED);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		executor.shutdown();
	}

	private String getFrameworkFactoryClassName() throws IOException {
		BundleContext context = getBundleContextWithoutFail();
        URL factoryService = context == null ? this.getClass().getResource(FRAMEWORK_FACTORY) : context.getBundle(0).getEntry(FRAMEWORK_FACTORY);
		assertNotNull("Could not locate: " + FRAMEWORK_FACTORY, factoryService);
		return getClassName(factoryService);
	}

	private BundleContext getBundleContextWithoutFail() {
		try {
			if ("true".equals(getProperty("noframework")))
				return null;
			return getContext();
		} catch (Throwable t) {
			return null; // don't fail
		}
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

	private FrameworkFactory getFrameworkFactory() {
		try {
			Class clazz = loadFrameworkClass(frameworkFactoryClassName);
			return (FrameworkFactory) clazz.getConstructor().newInstance();
		} catch (Exception e) {
			fail("Failed to get the framework constructor", e);
		}
		return null;
	}

	private Class loadFrameworkClass(String className)	throws ClassNotFoundException {
		BundleContext context = getBundleContextWithoutFail();
		return context == null ? Class.forName(className) : getContext().getBundle(0).loadClass(className);
	}

	private String getStorageAreaRoot() {
		BundleContext context = getBundleContextWithoutFail();
		if (context == null) {
			String storageroot = getProperty(STORAGEROOT, DEFAULT_STORAGEROOT);
			assertNotNull("Must set property: " + STORAGEROOT, storageroot);
			return storageroot;
		}
		return context.getDataFile("storageroot").getAbsolutePath();
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

	private void initFramework() {
		try {
			framework.init();
			assertNotNull("BundleContext is null after init", framework.getBundleContext());
		}
		catch (BundleException e) {
			fail("Unexpected BundleException initializing", e);
		}
		assertEquals("Wrong framework state after init", Bundle.STARTING, framework.getState());
	}

	private void startFramework() {
		try {
			framework.start();
			assertNotNull("BundleContext is null after start", framework.getBundleContext());
		}
		catch (BundleException e) {
			fail("Unexpected BundleException initializing", e);
		}
		assertEquals("Wrong framework state after init", Bundle.ACTIVE, framework.getState());

	}

	private void installFramework() throws Exception {
		System.out.println("Installing child framework");

		Framework f = getFramework();

		List bundles = new LinkedList();

		StringTokenizer st = new StringTokenizer(getProperty(
				"org.osgi.test.cases.jmx.framework.bundles", ""), ",");
		while (st.hasMoreTokens()) {
			String bundle = st.nextToken();

			Bundle b = f.getBundleContext().installBundle("file:" + bundle);
			assertNotNull(b);
			assertEquals("Bundle " + b.getSymbolicName() + " is not INSTALLED", Bundle.INSTALLED, b.getState());

			System.out.println("installed bundle " + b.getSymbolicName() + " " + b.getVersion());
			bundles.add(b);
		}

		for (Iterator it = bundles.iterator(); it.hasNext();) {
			Bundle b = (Bundle) it.next();

			if (b.getHeaders().get(Constants.FRAGMENT_HOST) == null) {
				b.start();
				assertEquals("Bundle " + b.getSymbolicName() + " is not ACTIVE", Bundle.ACTIVE, b.getState());

				System.out.println("started bundle " + b.getSymbolicName());
			}
		}
	}

	public Framework getFramework() {
		Framework f = framework;
		if (f == null || f.getState() != Bundle.ACTIVE) {
			fail("Framework is not started yet");
		}
		return f;
	}

	private void stopFramework() {
		int previousState = framework.getState();
		try {
			Future<FrameworkEvent> stopFuture = waitForStop();
            framework.stop();
			FrameworkEvent event = stopFuture.get();
			assertNotNull("FrameworkEvent is null", event);
			assertEquals("Wrong event type", FrameworkEvent.STOPPED, event.getType());
			assertNull("BundleContext is not null after stop", framework.getBundleContext());
		}
		catch (BundleException e) {
			fail("Unexpected BundleException stopping", e);
		}
		catch (InterruptedException e) {
			fail("Unexpected InterruptedException waiting for stop", e);
		} catch (ExecutionException e) {
			fail("Unexpected ExecutuionException waiting for stop", e);
		}
		// if the framework was not STARTING STOPPING or ACTIVE then we assume the waitForStop returned immediately with a FrameworkEvent.STOPPED
		// and does not change the state of the framework
		int expectedState = (previousState & (Bundle.STARTING | Bundle.ACTIVE | Bundle.STOPPING)) != 0 ? Bundle.RESOLVED : previousState;
		assertEquals("Wrong framework state after init", expectedState, framework.getState());
	}

	/**
	 * This method is implemented by subclasses, which contain the test cases
	 * @return Map with framework properties.
	 */
	public Map<String, String> getConfiguration() {
		Map<String, String> configuration = new HashMap<String, String>();
		configuration.put(Constants.FRAMEWORK_STORAGE_CLEAN, "true");

		//make sure that the server framework System Bundle exports the interfaces
		String systemPackagesXtra = getProperty(SYSTEM_PACKAGES_EXTRA);
		if (systemPackagesXtra != null) {
			configuration.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
					systemPackagesXtra);
		}
		if (consoleId != 0) {
			configuration.put("osgi.console", "" + consoleId++);
		}
        return configuration;
	}

	protected void verifyFramework() throws Exception {
		Framework f = getFramework();
		assertFalse(
				"child framework must have a different UUID",
				getContext().getProperty(Constants.FRAMEWORK_UUID).equals(
						f.getBundleContext().getProperty(
								Constants.FRAMEWORK_UUID)));

		BundleWiring wiring = f.getBundleContext().getBundle()
				.adapt(BundleWiring.class);
		assertNotNull(
				"Framework is not supplying a BundleWiring for the system bundle",
				wiring);
		List<BundleCapability> exportedPkgs = wiring
				.getCapabilities(BundleRevision.PACKAGE_NAMESPACE);

		String pkgXtras = f.getBundleContext().getProperty(
				Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA);
		if (pkgXtras == null) {
			System.out.println("pkgXtras is null");
			return;
		}
		List<String> pkgList = splitString(pkgXtras, ",");

		for (BundleCapability exportedPkg : exportedPkgs) {
			String name = (String) exportedPkg.getAttributes().get(
					BundleRevision.PACKAGE_NAMESPACE);
			pkgList.remove(name);
		}
		assertTrue("Framework does not export some packages " + pkgList,
				pkgList.isEmpty());
	}

	private List<String> splitString(String string, String delim) {
		List<String> result = new ArrayList<String>();
		for (StringTokenizer st = new StringTokenizer(string, delim); st
				.hasMoreTokens();) {
			String token = st.nextToken();
			int pos = token.indexOf(";");
			if (pos != -1) {
				token = token.substring(0, pos);
			}
			result.add(token);
		}
		return result;
	}

	static int	consoleId;
	{
		consoleId = OSGiTestCaseProperties
				.getIntegerProperty("osgi.console", 0);
	}

	private Future<FrameworkEvent> waitForStop() {
		final boolean[] inCall = new boolean[] { false };
		FutureTask<FrameworkEvent> future = new FutureTask<FrameworkEvent>(
				new Callable<FrameworkEvent>() {

					public FrameworkEvent call() throws Exception {
						synchronized (inCall) {
							inCall[0] = true;
							inCall.notifyAll();
						}
						return framework.waitForStop(10000);
					}
				});
		executor.execute(future);

		try {
			synchronized (inCall) {
				if (!inCall[0]) {
					inCall.wait(5000);
				}
			}
			// need to make extra sure waitForStop is called before
			// returning
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			fail("Unexpected interruption.", e);
		}

		return future;
	}
}

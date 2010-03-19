package org.osgi.test.cases.jmx.junit;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.management.InstanceNotFoundException;
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
import org.osgi.jmx.framework.BundleStateMBean;
import org.osgi.jmx.framework.FrameworkMBean;
import org.osgi.jmx.framework.PackageStateMBean;
import org.osgi.jmx.framework.ServiceStateMBean;
import org.osgi.jmx.service.cm.ConfigurationAdminMBean;
import org.osgi.jmx.service.permissionadmin.PermissionAdminMBean;
import org.osgi.jmx.service.provisioning.ProvisioningServiceMBean;
import org.osgi.jmx.service.useradmin.UserAdminMBean;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;

public class FrameworkMBeanLifecycleTestCase extends MBeanGeneralTestCase {

	private static final String FRAMEWORK_FACTORY = "/META-INF/services/org.osgi.framework.launch.FrameworkFactory";
	private static final String STORAGEROOT = "org.osgi.test.cases.jmx.storageroot";
	private static final String DEFAULT_STORAGEROOT = "generated/testframeworkstorage";
	private static final String	SYSTEM_PACKAGES_EXTRA	= "org.osgi.test.cases.jmx.system.packages.extra";	
	
	private Framework framework;
	private String frameworkFactoryClassName;
	private FrameworkFactory frameworkFactory;
	private String rootStorageArea;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		//fail("This test class either needs to be fixed or removed before we ship Enterprise 4.2 CT");
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

		assertTrue("Framework MBean is not registered", waitRegistrationMBean(newMBeanServer, new ObjectName(FrameworkMBean.OBJECTNAME)));
		
		FrameworkMBean mbean = (FrameworkMBean) MBeanServerInvocationHandler
				.newProxyInstance(mBeanServer, new ObjectName(
						FrameworkMBean.OBJECTNAME), FrameworkMBean.class, false);
		
		context.ungetService(reference);
		
		mbean.shutdownFramework();
		
		FrameworkEvent event = framework.waitForStop(10000);
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

		assertTrue("Framework MBean is not registered", waitRegistrationMBean(newMBeanServer, new ObjectName(FrameworkMBean.OBJECTNAME)));
		
		FrameworkMBean mbean = (FrameworkMBean) MBeanServerInvocationHandler
				.newProxyInstance(mBeanServer, new ObjectName(
						FrameworkMBean.OBJECTNAME), FrameworkMBean.class, false);
		
		context.ungetService(reference);
		
		mbean.restartFramework();
	
		FrameworkEvent event = framework.waitForStop(10000);
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

		assertTrue("Framework MBean is not registered", waitRegistrationMBean(newMBeanServer, new ObjectName(FrameworkMBean.OBJECTNAME)));
		
		FrameworkMBean mbean = (FrameworkMBean) MBeanServerInvocationHandler
				.newProxyInstance(mBeanServer, new ObjectName(
						FrameworkMBean.OBJECTNAME), FrameworkMBean.class, false);
		
		context.ungetService(reference);
		
		mbean.updateFramework();
		
		FrameworkEvent event = framework.waitForStop(10000);
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

		assertTrue("Framework MBean is not registered", waitRegistrationMBean(newMBeanServer, new ObjectName(FrameworkMBean.OBJECTNAME)));
		
		FrameworkMBean mbean = (FrameworkMBean) MBeanServerInvocationHandler
				.newProxyInstance(mBeanServer, new ObjectName(
						FrameworkMBean.OBJECTNAME), FrameworkMBean.class, false);

		context.ungetService(reference);
		
		mbean.restartFramework();
		
		FrameworkEvent event = framework.waitForStop(10000);
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

		assertTrue("Framework MBean is not registered", waitRegistrationMBean(newMBeanServer, new ObjectName(FrameworkMBean.OBJECTNAME)));
		
		mbean = (FrameworkMBean) MBeanServerInvocationHandler.newProxyInstance(
				mBeanServer, new ObjectName(FrameworkMBean.OBJECTNAME),
				FrameworkMBean.class, false);

		context.ungetService(reference);
		
		mbean.updateFramework();

		event = framework.waitForStop(10000);
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

		assertTrue("Framework MBean is not registered", waitRegistrationMBean(newMBeanServer, new ObjectName(FrameworkMBean.OBJECTNAME)));
		
		mbean = (FrameworkMBean) MBeanServerInvocationHandler.newProxyInstance(
				mBeanServer, new ObjectName(FrameworkMBean.OBJECTNAME),
				FrameworkMBean.class, false);

		context.ungetService(reference);
		
		mbean.shutdownFramework();
		
		event = framework.waitForStop(10000);
		assertTrue(
				"event indicated that framework was not moved to stopped state when shutdown was called",
				event.getType() == FrameworkEvent.STOPPED);
		assertTrue(
				"framework was not moved to stopped state when was shutdown",
				framework.getState() == Bundle.RESOLVED);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	private String getFrameworkFactoryClassName() throws IOException {
		BundleContext context = getBundleContextWithoutFail();
        URL factoryService = context == null ? this.getClass().getResource(FRAMEWORK_FACTORY) : context.getBundle(0).getEntry(FRAMEWORK_FACTORY);
		assertNotNull("Could not locate: " + FRAMEWORK_FACTORY, factoryService);
		return getClassName(factoryService);
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
			return (FrameworkFactory) clazz.newInstance();
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
			String storageroot = System.getProperty(STORAGEROOT, DEFAULT_STORAGEROOT);
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
		
		StringTokenizer st = new StringTokenizer(System.getProperty(
				"org.osgi.test.cases.jmx.bundles", ""), ",");
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
	
	/**
	 * This method is implemented by subclasses, which contain the test cases
	 * @return Map with framework properties.
	 */
	public Map<String, String> getConfiguration() {
		Map<String, String> configuration = new HashMap<String, String>();
		configuration.put(Constants.FRAMEWORK_STORAGE_CLEAN, "true");
		
		//make sure that the server framework System Bundle exports the interfaces
		String systemPackagesXtra = System.getProperty(SYSTEM_PACKAGES_EXTRA);
        configuration.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, systemPackagesXtra);
        configuration.put("osgi.console", "" + consoleId++);
        return configuration;
	}

	protected void verifyFramework() throws Exception {
		Framework f = getFramework();
//		assertFalse("child framework must have a different UUID",
//				getContext().getProperty("org.osgi.framework.uuid").equals(f.getBundleContext().getProperty("org.osgi.framework.uuid")));
		
		ServiceReference sr = f.getBundleContext().getServiceReference(PackageAdmin.class.getName());
		assertNotNull("Framework is not supplying PackageAdmin service", sr);
		
		PackageAdmin pkgAdmin = (PackageAdmin) f.getBundleContext().getService(sr);
		ExportedPackage[] exportedPkgs = pkgAdmin.getExportedPackages(f.getBundleContext().getBundle());
		assertNotNull(exportedPkgs);
		f.getBundleContext().ungetService(sr);
		
		String pkgXtras = f.getBundleContext().getProperty(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA);
		if (pkgXtras == null) {
			System.out.println("pkgXtras is null");
			return;
		}
		List<String> pkgList = splitString(pkgXtras, ",");
		
		for (int i=0;i<exportedPkgs.length;i++) {
			String name = exportedPkgs[i].getName();
			pkgList.remove(name);
		}
		assertTrue("Framework does not export some packages " + pkgList, pkgList.isEmpty());
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
	
	private void waitRegistrationAndUnregisterMBean(MBeanServer server, ObjectName mbean) {
		int count  = 100;
		boolean registered = server.isRegistered(mbean);
		while ((count > 0) && (!registered)) {
			synchronized (this) {
				try {					
					this.wait(100);
				} catch(InterruptedException iException) {}
			}
			registered = server.isRegistered(mbean);
			count--;
		}
		if (registered) {
			try {
				server.unregisterMBean(mbean);
			} catch(Exception exception){
				exception.printStackTrace();
			}
		}
	}

	private boolean waitRegistrationMBean(MBeanServer server, ObjectName mbean) {
		int count  = 100;
		boolean registered = server.isRegistered(mbean);
		while ((count > 0) && (!registered)) {
			synchronized (this) {
				try {					
					this.wait(100);
				} catch(InterruptedException iException) {}
			}
			registered = server.isRegistered(mbean);
			count--;
		}
		return registered;
	}
	
	static int consoleId = 1112;
	
}

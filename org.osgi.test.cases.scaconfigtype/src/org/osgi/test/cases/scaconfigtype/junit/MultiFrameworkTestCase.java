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
package org.osgi.test.cases.scaconfigtype.junit;

import static org.osgi.test.cases.scaconfigtype.common.TestConstants.DEFAULT_STORAGEROOT;
import static org.osgi.test.cases.scaconfigtype.common.TestConstants.ORG_OSGI_TEST_CASES_SCACONFIG_TYPE_BUNDLES;
import static org.osgi.test.cases.scaconfigtype.common.TestConstants.STORAGEROOT;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 * @author <a href="mailto:david.savage@paremus.com">David Savage</a>
 *
 */
public abstract class MultiFrameworkTestCase extends DefaultTestBundleControl {
	private static final String FRAMEWORK_FACTORY = "/META-INF/services/org.osgi.framework.launch.FrameworkFactory";
	
	private HashMap<String, Framework> frameworks = new HashMap<String, Framework>();
	
	private FrameworkFactory frameworkFactory;
	private Map<String, Object> configuration;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		frameworkFactory = getFrameworkFactory();

		String rootStorageArea = getStorageAreaRoot();
		assertNotNull("No storage area root found", rootStorageArea);
		
		File rootFile = new File(rootStorageArea);
		delete(rootFile);
		
		assertFalse("Root storage area is not a directory: " + rootFile.getPath(), rootFile.exists() && !rootFile.isDirectory());
		
		if (!rootFile.isDirectory())
			assertTrue("Could not create root directory: " + rootFile.getPath(), rootFile.mkdirs());
		
		configuration = getConfiguration();
		
		configuration.put(Constants.FRAMEWORK_STORAGE, rootFile.getAbsolutePath());		
	}


	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		try {
			for ( Iterator i = frameworks.values().iterator(); i.hasNext(); ) {
				Framework f = (Framework) i.next();
				stopFramework(f);
			}
		}
		finally {
			frameworks.clear();
		}

		super.tearDown();
	}

	/**
	 * @return started Framework instance
	 */
	public synchronized Framework getFramework(String name) {
		Framework f = frameworks.get(name);
		if ( f == null ) {
			f = createFramework(configuration);
			initFramework(f);
			startFramework(f);
			installFramework(f);
			frameworks.put(name, f);
		}
		if (f == null || f.getState() != Bundle.ACTIVE) {
			fail("Framework is not started yet");
		}
		return f;
	}
	
	/**
	 * This method is implemented by subclasses, which contain the test cases
	 * @return Map with framework properties.
	 */
	public abstract Map getConfiguration();

	/**
	 * Install a bundle into the framework.
	 * @param bundle Bundle location to install
	 * @return Bundle that was created by the framework and installed
	 * @throws BundleException
	 * @throws IOException
	 */
//	public Bundle installBundle(String bundle) throws BundleException, IOException {
//		BundleContext fwkContext = getFramework().getBundleContext();
//		assertNotNull("Framework context is null", fwkContext);
//		URL input = getBundleInput(bundle);
//		assertNotNull("Cannot find resource: " + bundle, input);
//		return fwkContext.installBundle(bundle, input.openStream());
//	}
//	
//	private URL getBundleInput(String bundle) {
//		BundleContext context = getBundleContextWithoutFail();
//	    return context == null ? this.getClass().getResource(bundle) : context.getBundle().getEntry(bundle);
//	}

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
			String storageroot = System.getProperty(STORAGEROOT, DEFAULT_STORAGEROOT);
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
			String frameworkFactoryClassName = getFrameworkFactoryClassName();
			assertNotNull("Could not find framework factory class", frameworkFactoryClassName);
			Class clazz = loadFrameworkClass(frameworkFactoryClassName);
			return (FrameworkFactory) clazz.newInstance();
		} catch (Exception e) {
			fail("Failed to get the framework constructor", e);
		}
		return null;
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

	private void installFramework(Framework f) {
		List bundles = new LinkedList();
		
		StringTokenizer st = new StringTokenizer(System.getProperty(
				ORG_OSGI_TEST_CASES_SCACONFIG_TYPE_BUNDLES, ""), "|");
		while (st.hasMoreTokens()) {
			String bundle = st.nextToken();
			
			try {
				Bundle b = f.getBundleContext().installBundle("file:" + bundle);
				assertNotNull(b);
				//assertEquals("Bundle " + b.getSymbolicName() + " is not INSTALLED", Bundle.INSTALLED, b.getState());
				
				System.out.println("installed bundle " + b.getSymbolicName());
				bundles.add(b);
			} catch (BundleException e) {
				fail("Failed to install bundle: " + bundle, e);
			}
			
		}
		
		for (Iterator it = bundles.iterator(); it.hasNext();) {
			Bundle b = (Bundle) it.next();
			
			if (b.getHeaders().get(Constants.FRAGMENT_HOST) == null) {
				try {
					b.start();
					assertEquals("Bundle " + b.getSymbolicName() + " is not ACTIVE", Bundle.ACTIVE, b.getState());
					
					System.out.println("started bundle " + b.getSymbolicName());
				} catch (BundleException e) {
					fail("Failed to start bundle: " + b.getSymbolicName(), e);
				}
			}
		}
	}
}

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
package org.osgi.test.cases.framework.launch.secure.junit;

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
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.test.support.OSGiTestCase;

public class FrameworkLaunchTests extends OSGiTestCase {
	private static final String STORAGEROOT = "org.osgi.test.cases.framework.launch.storageroot";
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

	public void testSecurity() {
		SecurityManager previousSM = System.getSecurityManager();
		if (previousSM != null) {
			// need to remove security manager to test this
			System.setSecurityManager(null);
		}
		Policy previous = Policy.getPolicy();
		Policy.setPolicy(new AllPolicy());
		try {
			Map configuration = getConfiguration(getName());
			configuration.put(Constants.FRAMEWORK_SECURITY, "osgi");
			Framework framework = createFramework(configuration);
			initFramework(framework);
			assertNotNull("Null SecurityManager", System.getSecurityManager());
			stopFramework(framework);
			assertNull("SecurityManager is not null", System
					.getSecurityManager());

			System.setSecurityManager(new SecurityManager());
			try {
				framework.start();
				fail("Expected an exception when starting with a SecurityManager already set");
			}
			catch (Exception e) {
				// expected
			}
			finally {
				System.setSecurityManager(null);
			}
		}
		finally {
			if (previousSM != null)
				System.setSecurityManager(previousSM);
			Policy.setPolicy(previous);
		}
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

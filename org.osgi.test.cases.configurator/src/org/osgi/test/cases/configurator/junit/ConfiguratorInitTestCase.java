/*
 * Copyright (c) OSGi Alliance (2017, 2018). All Rights Reserved.
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

package org.osgi.test.cases.configurator.junit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.util.tracker.ServiceTracker;
public class ConfiguratorInitTestCase extends OSGiTestCase {

	private static final String	STORAGEROOT			= "org.osgi.test.cases.configurator.storageroot";
	private static final String	DEFAULT_STORAGEROOT	= "generated/testframeworkstorage";
	private static final String	FRAMEWORK_FACTORY	= "/META-INF/services/org.osgi.framework.launch.FrameworkFactory";

	// in these tests we launch a new Framework each time and check whether the
	// correct intial configurations are set

	public void testInitialConfig() throws Exception {
		String pid = "org.osgi.test.init.pid";
		Map<String,String> launchConfig = new HashMap<>();
		launchConfig.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
				"org.osgi.service.cm");
		String config = "{\":configurator:resource-version\": 1,"
				+ "\":configurator:symbolicname\": \"org.osgi.test.config.init\","
				+ "\":configurator:version\": \"1.0.0\","
				+ "\"org.osgi.test.init.pid\":{\"foo\": \"bar\"}}";
		launchConfig.put("configurator.initial", config);

		Framework framework = startFramework(launchConfig);

		// get Configuration Admin
		ServiceTracker<ConfigurationAdmin,ConfigurationAdmin> configAdminTracker = new ServiceTracker<>(
				framework.getBundleContext(), ConfigurationAdmin.class, null);
		configAdminTracker.open();
		ConfigurationAdmin ca = configAdminTracker.waitForService(5000);

		// check configuration
		Configuration cfg = readConfig(ca, pid);
		assertNotNull("There should be a configuration with pid " + pid, cfg);
		Dictionary<String,Object> props = cfg.getProperties();
		assertEquals("bar", props.get("foo"));
		
		configAdminTracker.close();
		framework.stop();
		FrameworkEvent event = framework.waitForStop(10000);
	}

	public void testInitialConfigFile() throws Exception {
		String pid = "org.osgi.test.init.pid";

		// write init_config.json to file
		URL url = getContext().getBundle().getResource("init_config.json");
		InputStream is = url.openConnection().getInputStream();
		File f = getContext().getDataFile("init_config.json");
		try (FileOutputStream out = new FileOutputStream(f)) {
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = is.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
		}

		// provide file uri as configurator.initial
		Map<String,String> launchConfig = new HashMap<>();
		launchConfig.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
				"org.osgi.service.cm");
		String config = f.toURI().toString();
		launchConfig.put("configurator.initial", config);

		Framework framework = startFramework(launchConfig);

		// get Configuration Admin
		ServiceTracker<ConfigurationAdmin,ConfigurationAdmin> configAdminTracker = new ServiceTracker<>(
				framework.getBundleContext(), ConfigurationAdmin.class, null);
		configAdminTracker.open();
		ConfigurationAdmin ca = configAdminTracker.waitForService(5000);

		// check configuration
		Configuration cfg = readConfig(ca, pid);
		assertNotNull("There should be a configuration with pid " + pid, cfg);
		Dictionary<String,Object> props = cfg.getProperties();
		assertEquals("bar", props.get("foo"));

		configAdminTracker.close();
		framework.stop();
		FrameworkEvent event = framework.waitForStop(10000);
	}

	public void testInitialConfigRequiresVersion() throws Exception {
		String pid = "org.osgi.test.init.pid";
		Map<String,String> launchConfig = new HashMap<>();
		launchConfig.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
				"org.osgi.service.cm");
		String config = "{\":configurator:resource-version\": 1,"
				+ "\":configurator:symbolicname\": \"org.osgi.test.config.init\","
				+ "\"org.osgi.test.init.pid\":{\"foo\": \"bar\"}}";
		launchConfig.put("configurator.initial", config);

		Framework framework = startFramework(launchConfig);

		// get Configuration Admin
		ServiceTracker<ConfigurationAdmin,ConfigurationAdmin> configAdminTracker = new ServiceTracker<>(
				framework.getBundleContext(), ConfigurationAdmin.class, null);
		configAdminTracker.open();
		ConfigurationAdmin ca = configAdminTracker.waitForService(5000);

		// check configuration
		assertNull("This init config has a missing version",
				readConfig(ca, pid));

		configAdminTracker.close();
		framework.stop();
		FrameworkEvent event = framework.waitForStop(10000);
	}

	public void testInitialConfigRequiresSymbolicname() throws Exception {
		String pid = "org.osgi.test.init.pid";
		Map<String,String> launchConfig = new HashMap<>();
		launchConfig.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
				"org.osgi.service.cm");
		String config = "{\":configurator:resource-version\": 1,"
				+ "\":configurator:version\": \"1.0.0\","
				+ "\"org.osgi.test.init.pid\":{\"foo\": \"bar\"}}";
		launchConfig.put("configurator.initial", config);

		Framework framework = startFramework(launchConfig);

		// get Configuration Admin
		ServiceTracker<ConfigurationAdmin,ConfigurationAdmin> configAdminTracker = new ServiceTracker<>(
				framework.getBundleContext(), ConfigurationAdmin.class, null);
		configAdminTracker.open();
		ConfigurationAdmin ca = configAdminTracker.waitForService(5000);

		// check configuration
		assertNull("This init config has a missing symblicname",
				readConfig(ca, pid));

		configAdminTracker.close();
		framework.stop();
		FrameworkEvent event = framework.waitForStop(10000);
	}

	private Configuration readConfig(ConfigurationAdmin configAdmin, String pid)
			throws IOException, InvalidSyntaxException {
		Configuration[] configs = configAdmin.listConfigurations(
				"(" + Constants.SERVICE_PID + "=" + pid + ")");
		if (configs == null)
			return null;
		return configs[0];
	}

	private Framework startFramework(Map<String,String> configuration)
			throws Exception {
		// get factory
		String frameworkFactoryClassName = getFrameworkFactoryClassName();
		Class< ? > clazz = loadFrameworkClass(frameworkFactoryClassName);
		FrameworkFactory frameworkFactory = (FrameworkFactory) clazz
				.getConstructor()
				.newInstance();

		// set storage area
		String rootStorageArea = getStorageAreaRoot();
		File rootFile = new File(rootStorageArea);
		delete(rootFile);

		// create and start framework
		Framework framework = frameworkFactory.newFramework(configuration);
		framework.init();
		framework.start();

		// install bundles in framework
		installFramework(framework);
		return framework;
	}

	private String getFrameworkFactoryClassName() throws IOException {
		BundleContext context = getBundleContextWithoutFail();
		URL factoryService = context == null
				? this.getClass().getResource(FRAMEWORK_FACTORY)
				: context.getBundle(0).getEntry(FRAMEWORK_FACTORY);
		return getClassName(factoryService);

	}

	private Class< ? > loadFrameworkClass(String className)
			throws ClassNotFoundException {
		BundleContext context = getBundleContextWithoutFail();
		return context == null ? Class.forName(className)
				: getContext().getBundle(0).loadClass(className);
	}

	private String getClassName(URL factoryService) throws IOException {
		InputStream in = factoryService.openStream();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(in));
			for (String line = br.readLine(); line != null; line = br
					.readLine()) {
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
			} catch (IOException e) {
				// did our best; just ignore
			}
		}
		return null;
	}

	private String getStorageAreaRoot() {
		BundleContext context = getBundleContextWithoutFail();
		if (context == null) {
			String storageroot = getProperty(STORAGEROOT, DEFAULT_STORAGEROOT);
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

	private void installFramework(Framework f) throws Exception {
		List<Bundle> bundles = new LinkedList<>();
		StringTokenizer st = new StringTokenizer(getProperty(
				"org.osgi.test.cases.configurator.bundles", ""), ",");
		while (st.hasMoreTokens()) {
			String bundle = st.nextToken();
			Bundle b = f.getBundleContext().installBundle("file:" + bundle);
			bundles.add(b);
		}

		for (Iterator<Bundle> it = bundles.iterator(); it.hasNext();) {
			Bundle b = it.next();
			if (b.getHeaders().get(Constants.FRAGMENT_HOST) == null) {
				b.start();
			}
		}
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
}



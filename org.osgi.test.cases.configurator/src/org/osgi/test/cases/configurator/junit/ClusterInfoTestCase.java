/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
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

import java.io.IOException;
import java.util.Dictionary;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.util.tracker.ServiceTracker;

public class ClusterInfoTestCase extends OSGiTestCase {
	private ConfigurationAdmin configAdmin;
	private ServiceTracker<ConfigurationAdmin,ConfigurationAdmin> configAdminTracker;

	public void setUp() throws Exception {
		configAdminTracker = new ServiceTracker<>(
				getContext(), ConfigurationAdmin.class, null);
		configAdminTracker.open();
		configAdmin = configAdminTracker.waitForService(5000);
	}

	public void tearDown() {
		configAdminTracker.close();
	}

	public void testBasicConfigurationLifecycle() throws Exception {
		assertNull("Precondition, should not yet have the test config",
				readConfig("org.osgi.test.pid1"));

		Bundle tb1 = install("tb1.jar");
		System.out.println("*** Bundle state: " + tb1.getState());
		tb1.start();
		System.out.println("*** Bundle state: " + tb1.getState());
		sleep(500); // TODO use notification
		System.out.println("*** Slept for some time");
		Configuration cfg = readConfig("org.osgi.test.pid1");
		Dictionary<String,Object> props = cfg.getProperties();
		assertEquals("bar", props.get("foo"));
	}

	private Configuration readConfig(String pid)
			throws IOException, InvalidSyntaxException {
		Configuration[] configs = configAdmin.listConfigurations(
						"(" + Constants.SERVICE_PID + "=" + pid + ")");
		if (configs == null)
			return null;
		return configs[0];
	}
}



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
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.util.promise.Deferred;
import org.osgi.util.tracker.ServiceTracker;

public class ConfiguratorTestCase extends OSGiTestCase {
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
		String pid = "org.osgi.test.pid1";
		Deferred<Configuration> deleted = new Deferred<>();
		Deferred<Configuration> updated = new Deferred<>();

		ServiceRegistration<ConfigurationListener> reg = registerConfigListener(
				pid, updated, deleted);
		try {
			assertNull("Precondition, should not yet have the test config",
					readConfig(pid));

			Bundle tb1 = install("tb1.jar");
			assertFalse(updated.getPromise().isDone());
			tb1.start();

			Configuration cfg = updated.getPromise().getValue();
			Dictionary<String,Object> props = cfg.getProperties();
			assertEquals("bar", props.get("foo"));

			assertFalse(deleted.getPromise().isDone());
			tb1.uninstall();
			assertEquals(pid, deleted.getPromise().getValue().getPid());
		} finally {
			reg.unregister();
		}
	}

	public void testImplicitDatatypes() throws Exception {
		Deferred<Configuration> updated = new Deferred<>();

		ServiceRegistration<ConfigurationListener> reg = registerConfigListener(
				"org.osgi.test.pid2", updated, null);
		try {
			Bundle tb2 = install("tb2.jar");
			assertFalse("Precondition", updated.getPromise().isDone());
			tb2.start();

			Configuration cfg = updated.getPromise().getValue();
			Dictionary<String,Object> props = cfg.getProperties();
			assertEquals(6, props.size());
			assertEquals(true, props.get("bval"));
			assertEquals(1234L, props.get("ival"));
			assertEquals(-2.718, props.get("dval"));
			assertEquals("bar", props.get("sval"));
			assertEquals(
					"{\"a\":1,\"b\":\"2\",\"c\":{\"d\":true,\"e\":[999,1000]}}",
					((String) props.get("oval")).replaceAll("\\s", ""));

			assertEquals("org.osgi.test.pid2",
					props.get(Constants.SERVICE_PID));

			tb2.uninstall();
		} finally {
			reg.unregister();
		}
	}

	public void testExplicitDatatypes() throws Exception {
		String pid = "org.osgi.test.pid3a";
		Deferred<Configuration> updated = new Deferred<>();

		ServiceRegistration<ConfigurationListener> reg = registerConfigListener(
				pid, updated, null);
		try {
			Bundle tb2 = install("tb2.jar");
			assertFalse("Precondition", updated.getPromise().isDone());
			tb2.start();

			Configuration cfg = updated.getPromise().getValue();
			Dictionary<String,Object> props = cfg.getProperties();
			assertEquals(Integer.valueOf(1234), props.get("Ival"));
			assertEquals(Boolean.TRUE, props.get("Bval"));
			assertEquals('q', props.get("Cval"));
			assertEquals(Long.MAX_VALUE, props.get("Lval"));
			assertEquals("false", props.get("Sval"));
			assertEquals(-12.34f, props.get("Fval"));
			assertEquals(3.141592653589793, props.get("Dval"));
			assertEquals(Byte.valueOf("-128"), props.get("ByteVal"));
			assertEquals(Short.valueOf("16384"), props.get("ShortVal"));

			tb2.uninstall();
		} finally {
			reg.unregister();
		}
	}

	private ServiceRegistration<ConfigurationListener> registerConfigListener(
			String pid,
			Deferred<Configuration> updated, Deferred<Configuration> deleted) {
		if (updated == null)
			updated = new Deferred<>();
		if (deleted == null)
			deleted = new Deferred<>();

		Deferred<Configuration> fupdated = updated;
		Deferred<Configuration> fdeleted = deleted;
		ConfigurationListener cl = new ConfigurationListener() {
			@Override
			public void configurationEvent(ConfigurationEvent event) {
				if (!pid.equals(event.getPid()))
					return;

				try {
					ConfigurationAdmin cm = getContext()
							.getService(event.getReference());
					switch (event.getType()) {
						case ConfigurationEvent.CM_UPDATED :
							fupdated.resolve(
									cm.getConfiguration(event.getPid()));
							return;
						case ConfigurationEvent.CM_DELETED :
							fdeleted.resolve(
									cm.getConfiguration(event.getPid()));
							return;
					}
				} catch (IOException e) {
					fail("Unexpected test result", e);
				}
			}
		};
		return getContext().registerService(ConfigurationListener.class, cl,
				null);
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



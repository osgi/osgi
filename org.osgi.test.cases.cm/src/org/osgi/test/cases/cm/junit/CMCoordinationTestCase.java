/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.cm.junit;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.SynchronousConfigurationListener;
import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.Coordinator;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * @author Carsten Ziegeler, Adobe, Testing CM + Coordinator
 */
public class CMCoordinationTestCase extends DefaultTestBundleControl {

	private long				SIGNAL_WAITING_TIME;

	private ConfigurationAdmin cm;
	private PermissionAdmin permAdmin;
	private Bundle setAllPermissionBundle;

	private Set<String>								existingConfigs;

	protected void setUp() throws Exception {
		SIGNAL_WAITING_TIME = getLongProperty(
				"org.osgi.test.cases.cm.signal_waiting_time", 4000);
	    assignCm();

		if (System.getSecurityManager() != null) {
			permAdmin = getService(PermissionAdmin.class);
			setAllPermissionBundle = getContext().installBundle(
					getWebServer() + "setallpermission.jar");
		}

		// existing configurations
        Configuration[] configs = cm.listConfigurations(null);
		existingConfigs = new HashSet<>();
        if (configs != null) {
            for (int i = 0; i < configs.length; i++) {
                Configuration config = configs[i];
                existingConfigs.add(config.getPid());
            }
        }
	}

	protected void tearDown() throws Exception {
		resetPermissions();
		cleanCM(existingConfigs);
		if (this.setAllPermissionBundle != null) {
			this.setAllPermissionBundle.uninstall();
			this.setAllPermissionBundle = null;
		}
		if (permAdmin != null)
			ungetService(permAdmin);

        unregisterAllServices();
		ungetService(cm);
	}

	private void resetPermissions() throws BundleException {
		if (permAdmin == null)
			return;
		try {
			if (this.setAllPermissionBundle == null)
				this.setAllPermissionBundle = getContext().installBundle(
						getWebServer() + "setallpermission.jar");
			this.setAllPermissionBundle.start();
			this.setAllPermissionBundle.stop();
		} catch (BundleException e) {
			Exception ise = new IllegalStateException(
					"fail to install or start setallpermission bundle.");
			ise.initCause(e);
			throw e;
		}
		this.printoutPermissions();
	}

	private void printoutPermissions() {
		if (permAdmin == null)
			return;
		String[] locations = this.permAdmin.getLocations();
		if (locations != null)
			for (int i = 0; i < locations.length; i++) {
				System.out.println("locations[" + i + "]=" + locations[i]);
				PermissionInfo[] pInfos = this.permAdmin
						.getPermissions(locations[i]);
				for (int j = 0; j < pInfos.length; j++) {
					System.out.println("\t" + pInfos[j]);
				}
			}
		PermissionInfo[] pInfos = this.permAdmin.getDefaultPermissions();
		if (pInfos == null)
			System.out.println("default permission=null");
		else {
			System.out.println("default permission=");
			for (int j = 0; j < pInfos.length; j++) {
				System.out.println("\t" + pInfos[j]);
			}
		}

	}

	private void sleep() {
		try {
			Thread.sleep(SIGNAL_WAITING_TIME);
		} catch (InterruptedException e) {
			// ignore
		}
	}

    private void assignCm() {
        cm = getService(ConfigurationAdmin.class);
    }

	/**
	 * Removes any configurations made by this bundle.
	 */
	private void cleanCM(Set<String> existingConfigs) throws Exception {
        if (cm != null) {
            Configuration[] configs = cm.listConfigurations(null);
            if (configs != null) {
                for (int i = 0; i < configs.length; i++) {
                    Configuration config = configs[i];
                    if (!existingConfigs.contains(config.getPid())) {
                        config.delete();
                    }
                }
            }
        }
	}

	/**
	 * TEST CASES
	 */

	/**
	 * This test tests synchronous and plain configuration listeners. It
	 * <ol>
	 * <li>starts a coordination
	 * <li>adds a synchronous listener
	 * <li>adds a plain listener
	 * <li>creates/updates/deletes a configuration
	 * <li>ends the coordination
	 * <li>checks that synchronous listener is informed immediately
	 * <li>plain listener is informed once coordination ends
	 * </ol>
	 *
	 * @throws Exception
	 * @since 1.6
	 */
	public void testListeners() throws Exception {
		// start a coordination
		final Coordinator c = this.getService(Coordinator.class);
		final Coordination coord = c.begin("cm-test", 0);
		final List<ConfigurationEvent> syncList = new ArrayList<>();
		final List<ConfigurationEvent> plainList = new ArrayList<>();

		final String pid = this.getClass().getName() + ".testpid";

		try {
			// add a synchronous listener

			this.registerService(
					SynchronousConfigurationListener.class.getName(),
					new SynchronousConfigurationListener() {

						@Override
						public void configurationEvent(
								ConfigurationEvent event) {
							if (pid.equals(event.getPid())) {
								syncList.add(event);
							}
						}
					}, null);

			// add a plain listener

			this.registerService(
					ConfigurationListener.class.getName(),
					new ConfigurationListener() {

						@Override
						public void configurationEvent(
								ConfigurationEvent event) {
							if (pid.equals(event.getPid())) {
								plainList.add(event);
							}
						}
					}, null);

			// create the configuration
			final Dictionary<String,Object> props = new Hashtable<>();
			props.put("key", "value");

			final Configuration conf = this.cm.getConfiguration(pid);
			conf.update(props);

			assertEquals(1, syncList.size());
			assertEquals(ConfigurationEvent.CM_UPDATED,
					syncList.get(0).getType());

			assertEquals(0, plainList.size());

			sleep();
			assertEquals(0, plainList.size());

			// update configuration
			props.put("key2", "value2");
			conf.update(props);

			assertEquals(2, syncList.size());
			assertEquals(ConfigurationEvent.CM_UPDATED,
					syncList.get(1).getType());
			assertEquals(0, plainList.size());

			sleep();
			assertEquals(0, plainList.size());

			// delete configuration
			conf.delete();
			assertEquals(3, syncList.size());
			assertEquals(ConfigurationEvent.CM_DELETED,
					syncList.get(2).getType());
			assertEquals(0, plainList.size());

			sleep();
			assertEquals(0, plainList.size());
		} finally {
			coord.end();
		}

		// wait and verify listener
		sleep();
		assertEquals(3, plainList.size());
		assertEquals(ConfigurationEvent.CM_UPDATED, plainList.get(0).getType());
		assertEquals(ConfigurationEvent.CM_UPDATED, plainList.get(1).getType());
		assertEquals(ConfigurationEvent.CM_DELETED, plainList.get(2).getType());

	}

	// Test opposite of:
	// If a Managed Service is registered and no configuration information is
	// available or the configuration is not visible then the Configuration
	// Admin service must call updated(Dictionary) with a null parameter.
	//
	// Configuration exists before ManagedService is registered. No update(null)
	// use cfg-dictionary directly

	public void test_deliver_existing_Configuration_to_later_registered_ManagedService()
			throws Exception {
		// start a coordination
		final Coordinator c = this.getService(Coordinator.class);
		final Coordination coord = c.begin("cm-test1", 0);
		final List<String> events = new ArrayList<>();

		final String pid = this.getClass().getName() + ".mstestpid2";

		try {
			// create the configuration
			final Dictionary<String,Object> props = new Hashtable<>();
			props.put("key", "value");

			final Configuration conf = this.cm.getConfiguration(pid);
			conf.update(props);

			// add managed service
			final Dictionary<String,Object> msProps = new Hashtable<>();
			msProps.put(Constants.SERVICE_PID, pid);

			this.registerService(ManagedService.class.getName(),
					new ManagedService() {

						@Override
						public void updated(Dictionary<String, ? > properties)
								throws ConfigurationException {
							events.add((String) properties.get("key2"));
						}

					}, msProps);

			sleep();
			assertEquals(0, events.size());

			// update configuration
			props.put("key2", "value2");
			conf.update(props);

			sleep();
			assertEquals(0, events.size());
		} finally {
			coord.end();
		}

		// wait and verify listener
		sleep();

		assertEquals(1, events.size());
		// last update
		assertEquals("value2", events.get(0));
	}

	public void test_create_managedservice_delete() throws Exception {
		ConfigurationAdmin cm = getService(ConfigurationAdmin.class);
		Coordinator c = getService(Coordinator.class);
		Coordination coord = c.begin("cm-test1", 0);
		final List<Boolean> events = new ArrayList<>();

		String pid = getClass().getName() + ".mstestpid3";

		try {
			// create the configuration
			Dictionary<String,Object> props = new Hashtable<>();
			props.put("key", "value");

			Configuration conf = cm.getConfiguration(pid);
			conf.update(props);

			// add managed service
			Dictionary<String,Object> msProps = new Hashtable<>();
			msProps.put(Constants.SERVICE_PID, pid);

			this.registerService(ManagedService.class.getName(),
					new ManagedService() {

						@Override
						public void updated(Dictionary<String, ? > properties)
								throws ConfigurationException {
							events.add(properties != null);
						}

					}, msProps);

			// update configuration
			props.put("key2", "value2");
			conf.update(props);

			// delete configuration
			conf.delete();

			assertEquals(0, events.size());
		} finally {
			coord.end();
		}

		// wait and verify listener
		sleep();

		assertEquals(1, events.size());
		assertFalse(events.get(0));// no configuration, update with null
	}

	public void testCoordinatedConfigurationOnBeforeRegisteredManagedService()
			throws Exception {

		final List<Boolean> events = new ArrayList<>();
		final String pid = this.getClass().getName() + ".mstestpid3";
		// add managed service
		final Dictionary<String,Object> msProps = new Hashtable<>();
		msProps.put(Constants.SERVICE_PID, pid);

		this.registerService(ManagedService.class.getName(),
				new ManagedService() {

					@Override
					public void updated(Dictionary<String, ? > properties)
							throws ConfigurationException {
						events.add(properties != null);
					}

				}, msProps);

		// start a coordination
		final Coordinator c = this.getService(Coordinator.class);
		final Coordination coord = c.begin("cm-test2", 0);

		try {

			// create the configuration
			final Dictionary<String,Object> props = new Hashtable<>();
			props.put("key", "value");

			final Configuration conf = this.cm.getConfiguration(pid);
			conf.update(props);

			sleep();
			assertEquals(1, events.size());// update with null because
											// registered before coordination

			// update configuration
			props.put("key2", "value2");
			conf.update(props);

			sleep();
			assertEquals(1, events.size());

			// delete configuration
			conf.delete();

			sleep();
			assertEquals(1, events.size());
		} finally {
			coord.end();
		}

		// wait and verify listener
		sleep();
		assertEquals(4, events.size());
		assertFalse(events.get(0));
		assertTrue(events.get(1));
		assertTrue(events.get(2));
		assertFalse(events.get(3));

	}

	/**
	 * This test tests a managed service. It
	 * <ol>
	 * <li>starts a coordination
	 * <li>adds a synchronous listener
	 * <li>adds a plain listener
	 * <li>creates/updates/deletes a configuration
	 * <li>ends the coordination
	 * <li>checks that synchronous listener is informed immediately
	 * <li>plain listener is informed once coordination ends
	 * </ol>
	 *
	 * @throws Exception
	 * @since 1.6
	 */
	public void testManagedService() throws Exception {
		// start a coordination
		final Coordinator c = this.getService(Coordinator.class);
		final Coordination coord = c.begin("cm-test", 0);
		final List<Boolean> events = new ArrayList<>();

		final String pid = this.getClass().getName() + ".mstestpid";

		boolean checkpoint = false;
		try {
			// add managed service
			final Dictionary<String,Object> msProps = new Hashtable<>();
			msProps.put(Constants.SERVICE_PID, pid);

			this.registerService(ManagedService.class.getName(),
					new ManagedService() {

						@Override
						public void updated(Dictionary<String, ? > properties)
								throws ConfigurationException {
							events.add(properties != null);
						}

					}, msProps);


			// create the configuration
			final Dictionary<String,Object> props = new Hashtable<>();
			props.put("key", "value");

			final Configuration conf = this.cm.getConfiguration(pid);
			conf.update(props);

			sleep();
			assertEquals(0, events.size());

			// update configuration
			props.put("key2", "value2");
			conf.update(props);

			sleep();
			assertEquals(0, events.size());

			// delete configuration
			conf.delete();

			sleep();
			assertEquals(0, events.size());
			checkpoint = true;
		} catch (Exception e) {
			e.printStackTrace();
			c.fail(e);
		} finally {
			coord.end();
		}
		assertTrue(checkpoint);
		// wait and verify listener
		sleep();
		assertEquals(4, events.size());
		assertFalse(events.get(0));
		assertTrue(events.get(1));
		assertTrue(events.get(2));
		assertFalse(events.get(3));

	}
}

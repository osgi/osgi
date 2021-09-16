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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
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
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectBundleInstaller;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.install.BundleInstaller;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.context.InstalledBundleExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * @author Carsten Ziegeler, Adobe, Testing CM + Coordinator
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(InstalledBundleExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
public class CMCoordinationTestCase {

	private long				SIGNAL_WAITING_TIME;

	@InjectBundleContext
	BundleContext								bc;
	@InjectService(cardinality = 0)
	ServiceAware<ConfigurationAdmin>	cm;
	@InjectService(cardinality = 0)
	ServiceAware<PermissionAdmin>		permAdmin;
	@InjectBundleInstaller
	BundleInstaller								bi;
	Bundle								setAllPermissionBundle;

	private Set<String>								existingConfigs;

	@BeforeEach
	protected void setUp() throws Exception {
		SIGNAL_WAITING_TIME = getLongProperty(
				"org.osgi.test.cases.cm.signal_waiting_time", 4000);

		if (System.getSecurityManager() != null) {

			setAllPermissionBundle = bi.installBundle("setallpermission.jar",
					true);
		}

		// existing configurations
		Configuration[] configs = cm.waitForService(500)
				.listConfigurations(null);
		existingConfigs = new HashSet<>();
        if (configs != null) {
            for (int i = 0; i < configs.length; i++) {
                Configuration config = configs[i];
                existingConfigs.add(config.getPid());
            }
        }
	}

	@AfterEach
	protected void tearDown() throws Exception {
		resetPermissions();
		cleanCM(existingConfigs);
		if (this.setAllPermissionBundle != null) {
			this.setAllPermissionBundle.stop();
			this.setAllPermissionBundle.uninstall();
			this.setAllPermissionBundle = null;
		}
	}

	private void resetPermissions()
			throws BundleException, InterruptedException {
		if (permAdmin == null)
			return;
		try {
			if (this.setAllPermissionBundle == null)
				this.setAllPermissionBundle = bi
						.installBundle("setallpermission.jar", false);
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

	private void printoutPermissions() throws InterruptedException {
		if (permAdmin == null)
			return;
		String[] locations = this.permAdmin.waitForService(500).getLocations();
		if (locations != null)
			for (int i = 0; i < locations.length; i++) {
				System.out.println("locations[" + i + "]=" + locations[i]);
				PermissionInfo[] pInfos = this.permAdmin.waitForService(500)
						.getPermissions(locations[i]);
				for (int j = 0; j < pInfos.length; j++) {
					System.out.println("\t" + pInfos[j]);
				}
			}
		PermissionInfo[] pInfos = this.permAdmin.waitForService(500)
				.getDefaultPermissions();
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

	/**
	 * Removes any configurations made by this bundle.
	 */
	private void cleanCM(Set<String> existingConfigs) throws Exception {
        if (cm != null) {
			Configuration[] configs = cm.waitForService(500)
					.listConfigurations(null);
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
	@Test
	public void testListeners(@InjectService
	Coordinator c) throws Exception {
		// start a coordination
		final Coordination coord = c.begin("cm-test", 0);
		final List<ConfigurationEvent> syncList = new ArrayList<>();
		final List<ConfigurationEvent> plainList = new ArrayList<>();

		final String pid = this.getClass().getName() + ".testpid";

		try {
			// add a synchronous listener

			bc.registerService(
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

			bc.registerService(
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

			final Configuration conf = this.cm.waitForService(500)
					.getConfiguration(pid);
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
	@RepeatedTest(10)
	public void testManagedService(@InjectService
	Coordinator c) throws Exception {
		// start a coordination
		final Coordination coord = c.begin("cm-test", 0);
		final List<Boolean> events = new ArrayList<>();

		final String pid = this.getClass().getName() + ".mstestpid";
		ServiceRegistration regms = null;
		try {
			// add managed service
			final Dictionary<String,Object> msProps = new Hashtable<>();
			msProps.put(Constants.SERVICE_PID, pid);

			regms = bc.registerService(
					ManagedService.class.getName(), new ManagedService() {

						@Override
						public void updated(Dictionary<String, ? > properties)
								throws ConfigurationException {
							events.add(properties != null);
						}

					}, msProps);


			// create the configuration
			final Dictionary<String,Object> props = new Hashtable<>();
			props.put("key", "value");

			final Configuration conf = this.cm.waitForService(500)
					.getConfiguration(pid);
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
		if (regms != null) {
			regms.unregister();
		}
	}

	private long getLongProperty(String string, int i) {
		return i;
	}
}

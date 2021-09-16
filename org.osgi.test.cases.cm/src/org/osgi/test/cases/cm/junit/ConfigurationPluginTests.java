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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.osgi.test.support.compatibility.DefaultTestBundleControl.trace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.cm.ConfigurationPlugin;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.test.cases.cm.common.ConfigurationListenerImpl;
import org.osgi.test.cases.cm.common.SynchronizerImpl;
import org.osgi.test.cases.cm.shared.Synchronizer;
import org.osgi.test.cases.cm.shared.Util;
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
 * Tests related to {@link ConfigurationPlugin}
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(InstalledBundleExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
public class ConfigurationPluginTests {

	private static final String	PROP_MS_PREFIX	= "plugin.ms.";

	private static final String	PROP_MSF_PREFIX			= "plugin.msf.";

	private long				SIGNAL_WAITING_TIME;

	@InjectBundleContext
	BundleContext								bc;
	@InjectService(cardinality = 0)
	ServiceAware<ConfigurationAdmin>	cm;
	@InjectService(cardinality = 0)
	ServiceAware<PermissionAdmin>		permAdmin;
	@InjectBundleInstaller
	BundleInstaller								bi;
	private Bundle setAllPermissionBundle;

	/** PIDs of configurations existing before the test. */
	private Set<String>								existingConfigs;

	@BeforeEach
	protected void setUp() throws Exception {
		SIGNAL_WAITING_TIME = getLongProperty(
				"org.osgi.test.cases.cm.signal_waiting_time", 4000);

		if (System.getSecurityManager() != null) {
			setAllPermissionBundle = bi.installBundle("setallpermission.jar",
					false);
			setAllPermissionBundle.start();
			;
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

	private long getLongProperty(String string, int i) {
		// TODO Auto-generated method stub
		return i;
	}

	@AfterEach
	protected void tearDown() throws Exception {
		resetPermissions();
		cleanCM(existingConfigs);

	}

	private void resetPermissions() throws BundleException {
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
	}

	/**
	 * Tests if a configuration plugin is invoked when only a configuration
	 * listener is registered (no managed service). It should not be invoked.
	 *
	 * @spec ConfigurationAdmin.getConfiguration(String)
	 * @spec Configuration.update(Dictionary)
	 * @spec ConfigurationListener.configurationEvent(ConfigurationEvent)
	 * @spec ConfigurationPlugin.modifyConfiguration(ServiceReference,
	 *       Dictionary)
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 */

	@Test
	public void testConfigurationPluginService() throws Exception {
		// create configuration
		String pid = Util
				.createPid(ConfigurationListenerImpl.LISTENER_PID_SUFFIX);
		Configuration conf = cm.waitForService(500).getConfiguration(pid);

		SynchronizerImpl synchronizer = new SynchronizerImpl();
		trace("Create and register a new ConfigurationListener");
		createConfigurationListener(synchronizer);
		trace("Create and register a new ConfigurationPlugin");
		NotVisitablePlugin plugin = createConfigurationPlugin();

		// update configuration
		Dictionary<String,Object> props = Util.singletonDictionary("key",
				"value1");
		conf.update(props);
		trace("Wait until the ConfigurationListener has gotten the update");

		assertTrue("Update done",
					synchronizer.waitForSignal(SIGNAL_WAITING_TIME));
		assertTrue("ConfigurationPlugin not visited", plugin.notVisited());
	}

	/**
	 * Tests if a configuration plugin is invoked when only a configuration
	 * listener is registered (managed service factory). It should not be
	 * invoked.
	 *
	 * @spec ConfigurationAdmin.createFactoryConfiguration(String)
	 * @spec Configuration.update(Dictionary)
	 * @spec ConfigurationListener.configurationEvent(ConfigurationEvent)
	 * @spec
	 *       ConfigurationPlugin.modifyConfiguration(ServiceReference,Dictionary)
	 *
	 * @throws Exception
	 *             if an error occurs or an assertion fails in the test.
	 */
	@Test
	public void testConfigurationPluginServiceFactory() throws Exception {

		// create configuration
		String factorypid = Util
				.createPid(ConfigurationListenerImpl.LISTENER_PID_SUFFIX);
		Configuration conf = cm.waitForService(500)
				.createFactoryConfiguration(factorypid);

		SynchronizerImpl synchronizer = new SynchronizerImpl();
		trace("Create and register a new ConfigurationListener");
		createConfigurationListener(synchronizer);
		trace("Create and register a new ConfigurationPlugin");
		NotVisitablePlugin plugin = createConfigurationPlugin();

		Dictionary<String,Object> props = Util.singletonDictionary("key",
				"value1");
		conf.update(props);
		trace("Wait until the ConfigurationListener has gotten the update");

		assertTrue("Update done",
					synchronizer.waitForSignal(SIGNAL_WAITING_TIME));
		assertTrue("ConfigurationPlugin not visited", plugin.notVisited());
	}

	/**
	 * Tests the order of invoked plugins based on cmRanking for a managed
	 * service. This test checks several aspects at once:
	 * <ul>
	 * <li>ordering
	 * <li>handling of update(Dictionary), update(), and delete()
	 * <li>handling of auto properties
	 * <li>modifications wrt ranking
	 * </ul>
	 */
	@Test
	public void testRankingForManagedService() throws Exception {
		// create configuration
		final String pid = Util.createPid("mspid");
		cm.waitForService(500)
				.getConfiguration(pid)
				.update(Util.singletonDictionary("key", "val"));

		final SynchronizerImpl synchronizer = new SynchronizerImpl();

		final PluginContext context = new PluginContext();
		context.pid = pid;
		createConfigurationPlugin(context, 1, -1, null);
		createConfigurationPlugin(context, 2, null, null);
		createConfigurationPlugin(context, 3, 5, null);
		createConfigurationPlugin(context, 4, 1000, null);
		createConfigurationPlugin(context, 5, 1001, null);

		ServiceRegistration< ? > reg = registerManagedService(pid,
				synchronizer);

		trace("Wait until the ManagedService has gotten the update");

		assertTrue("Update done",
				synchronizer.waitForSignal(SIGNAL_WAITING_TIME));
		final Dictionary<String,Object> props = synchronizer.getProps();
		assertEquals("val", props.get("key"));
		verifyPlugins(props, false, Arrays.asList(1, 2, 3, 4, 5),
				Arrays.asList(2, 3, 4), context);

		// update configuration
		context.invocationOrder.clear();
		synchronizer.resetCount();
		cm.waitForService(500)
				.getConfiguration(pid)
				.update(Util.singletonDictionary("key1", "val1"));
		trace("Wait until the ManagedService has gotten the update");

		assertTrue("Update done",
				synchronizer.waitForSignal(SIGNAL_WAITING_TIME));
		final Dictionary<String,Object> props1 = synchronizer.getProps();
		assertEquals("val1", props1.get("key1"));
		verifyPlugins(props1, false, Arrays.asList(1, 2, 3, 4, 5),
				Arrays.asList(2, 3, 4), context);

		// force update
		context.invocationOrder.clear();
		synchronizer.resetCount();
		cm.waitForService(500)
				.getConfiguration(pid)
				.update(Util.singletonDictionary("key1", "val1"));
		trace("Wait until the ManagedService has gotten the update");

		assertTrue("Update done",
				synchronizer.waitForSignal(SIGNAL_WAITING_TIME));
		final Dictionary<String,Object> props2 = synchronizer.getProps();
		assertEquals("val1", props2.get("key1"));
		verifyPlugins(props2, false, Arrays.asList(1, 2, 3, 4, 5),
				Arrays.asList(2, 3, 4), context);

		// delete configuration
		context.invocationOrder.clear();
		synchronizer.resetCount();
		cm.waitForService(500).getConfiguration(pid).delete();
		trace("Wait until the ManagedService has gotten the delete");

		assertTrue("Update done",
				synchronizer.waitForSignal(SIGNAL_WAITING_TIME));
		assertNull(synchronizer.getProps());
		assertTrue(context.invocationOrder.isEmpty());
		assertTrue(context.errors.isEmpty());
		reg.unregister();
	}

	/**
	 * Tests the order of invoked plugins based on cmRanking for a managed
	 * service factory. This test checks several aspects at once:
	 * <ul>
	 * <li>ordering
	 * <li>handling of update(Dictionary), update(), and delete()
	 * <li>handling of auto properties
	 * <li>modifications wrt ranking
	 * </ul>
	 */
	@Test
	public void testRankingForManagedServiceFactory() throws Exception {
		// create configuration
		final String pid = Util.createPid("msfpid");
		final Configuration conf = cm.waitForService(500)
				.createFactoryConfiguration(pid);
		conf.update(Util.singletonDictionary("key", "val"));

		final SynchronizerImpl synchronizer = new SynchronizerImpl();

		final PluginContext context = new PluginContext();
		context.factoryPid = pid;
		createConfigurationPlugin(context, 1, -1, null);
		createConfigurationPlugin(context, 2, null, null);
		createConfigurationPlugin(context, 3, 5, null);
		createConfigurationPlugin(context, 4, 1000, null);
		createConfigurationPlugin(context, 5, 1001, null);

		ServiceRegistration<?>reg=registerManagedServiceFactory(pid, synchronizer);

		trace("Wait until the ManagedServiceFactory has gotten the update");

		assertTrue("Update done",
				synchronizer.waitForSignal(SIGNAL_WAITING_TIME));
		final Dictionary<String,Object> props = synchronizer.getProps();
		assertEquals("val", props.get("key"));

		verifyPlugins(props, true, Arrays.asList(1, 2, 3, 4, 5),
				Arrays.asList(2, 3, 4), context);

		// update configuration
		context.invocationOrder.clear();
		synchronizer.resetCount();
		conf.update(Util.singletonDictionary("key1", "val1"));
		trace("Wait until the ManagedServiceFactory has gotten the update");

		assertTrue("Update done",
				synchronizer.waitForSignal(SIGNAL_WAITING_TIME));
		final Dictionary<String,Object> props1 = synchronizer.getProps();
		assertEquals("val1", props1.get("key1"));
		verifyPlugins(props1, true, Arrays.asList(1, 2, 3, 4, 5),
				Arrays.asList(2, 3, 4), context);

		// force update
		context.invocationOrder.clear();
		synchronizer.resetCount();
		conf.update();
		trace("Wait until the ManagedServiceFactory has gotten the update");

		assertTrue("Update done",
				synchronizer.waitForSignal(SIGNAL_WAITING_TIME));
		final Dictionary<String,Object> props2 = synchronizer.getProps();
		assertEquals("val1", props2.get("key1"));
		verifyPlugins(props2, true, Arrays.asList(1, 2, 3, 4, 5),
				Arrays.asList(2, 3, 4), context);

		// delete configuration
		context.invocationOrder.clear();
		synchronizer.resetCount();
		conf.delete();
		trace("Wait until the ManagedServiceFactory has gotten the delete");

		assertTrue("Update done",
				synchronizer.waitForSignal(SIGNAL_WAITING_TIME));
		assertNull(synchronizer.getProps());
		assertTrue(context.invocationOrder.isEmpty());
		assertTrue(context.errors.isEmpty());
		reg.unregister();
	}

	/**
	 * Test targetting of configuration plugins with a managed service
	 */
	@Test
	public void testTargettingManagedService() throws Exception {
		// create configuration
		final String pid = Util.createPid("mspid");
		cm.waitForService(500)
				.getConfiguration(pid)
				.update(Util.singletonDictionary("key", "val"));

		final PluginContext context = new PluginContext();

		createConfigurationPlugin(context, 1, 1, null);
		createConfigurationPlugin(context, 2, 2, new String[] {pid});
		createConfigurationPlugin(context, 3, 3, new String[] {"foo"});

		final SynchronizerImpl synchronizer = new SynchronizerImpl();
		ServiceRegistration< ? > reg = registerManagedService(pid,
				synchronizer);

		trace("Wait until the ManagedService has gotten the update");

		assertTrue("Update done",
				synchronizer.waitForSignal(SIGNAL_WAITING_TIME));
		final Dictionary<String,Object> props = synchronizer.getProps();
		assertEquals("val", props.get("key"));
		verifyPlugins(props, false, Arrays.asList(1, 2), Arrays.asList(1, 2),
				context);
		reg.unregister();
	}

	/**
	 * Test targetting of configuration plugins with a managed service factory
	 */
	@Test
	public void testTargettingManagedServiceFactory() throws Exception {
		// create factory configuration
		final String factoryPid = Util.createPid("msfpid");
		final Configuration conf = cm.waitForService(500)
				.createFactoryConfiguration(factoryPid);
		conf.update(Util.singletonDictionary("key", "val"));

		final PluginContext context = new PluginContext();

		createConfigurationPlugin(context, 1, 1, null);
		createConfigurationPlugin(context, 2, 2, new String[] {factoryPid});
		createConfigurationPlugin(context, 3, 3, new String[] {"foo"});

		final SynchronizerImpl synchronizer = new SynchronizerImpl();
		ServiceRegistration< ? > reg = registerManagedServiceFactory(factoryPid,
				synchronizer);

		trace("Wait until the ManagedServiceFactory has gotten the update");

		assertTrue("Update done",
				synchronizer.waitForSignal(SIGNAL_WAITING_TIME));
		final Dictionary<String,Object> props = synchronizer.getProps();
		assertEquals("val", props.get("key"));
		verifyPlugins(props, true, Arrays.asList(1, 2), Arrays.asList(1, 2),
				context);
		reg.unregister();
	}

	/**
	 * Test calling plugins through Configuration object
	 */
	@Test
	public void testGetProcessedProperties() throws Exception {
		// create configuration
		final String pid = Util.createPid("mspid");
		final Configuration conf = cm.waitForService(500).getConfiguration(pid);
		conf.update(Util.singletonDictionary("key", "val"));

		final PluginContext context = new PluginContext();
		context.pid = pid;
		createConfigurationPlugin(context, 1, -1, null);
		createConfigurationPlugin(context, 2, null, null);
		createConfigurationPlugin(context, 3, 5, null);
		createConfigurationPlugin(context, 4, 1000, null);
		createConfigurationPlugin(context, 5, 1001, null);

		final ServiceRegistration<ManagedService> reg = bc
				.registerService(ManagedService.class, new ManagedService() {

					@Override
					public void updated(Dictionary<String, ? > properties)
							throws ConfigurationException {
						// nothing to do

					}
				}, null);

		try {
			final Dictionary<String,Object> props = conf
					.getProcessedProperties(reg.getReference());
			assertEquals("val", props.get("key"));

			verifyPlugins(props, false, Arrays.asList(1, 2, 3, 4, 5),
					Arrays.asList(2, 3, 4), context);
		} finally {
			reg.unregister();
		}
	}

	/**
	 * Verify that the plugins are called in the correct order and that only
	 * modifications from plugins in the modification set got accepted.
	 * 
	 * @param props The properties
	 * @param isFactory Whether to check for managed service or ms factory
	 * @param callOrder The expected call order of the plugins
	 * @param modifyOrder These plugins are allowed to modify
	 * @param context The context collecting the plugin info
	 */
	private void verifyPlugins(final Dictionary<String,Object> props,
			final boolean isFactory,
			final List<Integer> callOrder, final List<Integer> modifyOrder,
			final PluginContext context) {

		if (context.pid != null) {
			assertEquals(context.pid, props.get(Constants.SERVICE_PID));
		}
		if (context.factoryPid != null) {
			assertEquals(context.factoryPid,
					props.get(ConfigurationAdmin.SERVICE_FACTORYPID));
		}
		assertNull(props.get(ConfigurationAdmin.SERVICE_BUNDLELOCATION));
		Assert.assertEquals(
				"Order expected=" + callOrder + ", but was "
						+ context.invocationOrder,
				callOrder, context.invocationOrder);

		assertTrue("Errors: " + context.errors, context.errors.isEmpty());

		final Set<Integer> modifications = new HashSet<>();
		Enumeration<String> keyEnum = props.keys();
		while (keyEnum.hasMoreElements()) {
			final String key = keyEnum.nextElement();
			if (key.startsWith(PROP_MSF_PREFIX)) {
				if (!isFactory) {
					fail("no managed service factory modification expected");
				}
				final Integer id = Integer
						.valueOf(key.substring(PROP_MSF_PREFIX.length()));
				assertTrue("No modification from plugin " + id + " expected.",
						modifyOrder.contains(id));
				modifications.add(id);
			} else if (key.startsWith(PROP_MS_PREFIX)) {
				if (isFactory) {
					fail("no managed service modification expected");
				}
				final Integer id = Integer
						.valueOf(key.substring(PROP_MS_PREFIX.length()));
				assertTrue("No modification from plugin " + id + " expected.",
						modifyOrder.contains(id));
				modifications.add(id);
			}
		}
		assertTrue("Expected " + modifyOrder + ", got: " + modifications,
				modifications.equals(new HashSet<>(modifyOrder)));
	}

	/**
	 * Register a managed service
	 * 
	 * @param pid The pid for the service
	 * @param synchronizer The synchronizer
	 * @return
	 * @throws Exception if something goes wrong
	 */
	private ServiceRegistration< ? > registerManagedService(final String pid,
			final Synchronizer synchronizer) throws Exception {
		trace("Create and register a new ManagedService");

		return bc.registerService(ManagedService.class.getName(),
				new ManagedService() {

			@Override
			public void updated(Dictionary<String, ? > properties)
					throws ConfigurationException {
				synchronizer.signal(properties);

			}
		}, Util.singletonDictionary(Constants.SERVICE_PID, pid));
	}

	/**
	 * Register a managed service factory
	 * 
	 * @param factoryPid The factoryPid for the service
	 * @param synchronizer The synchronizer
	 * @return
	 * @throws Exception if something goes wrong
	 */
	private ServiceRegistration< ? > registerManagedServiceFactory(
			final String factoryPid,
			final Synchronizer synchronizer) throws Exception {
		trace("Create and register a new ManagedServiceFactory");

		return bc.registerService(ManagedServiceFactory.class.getName(),
				new ManagedServiceFactory() {

					@Override
					public String getName() {
						return null;
					}

					@Override
					public void updated(String pid,
							Dictionary<String, ? > properties)
							throws ConfigurationException {
						synchronizer
								.signal(properties);
					}

					@Override
					public void deleted(String pid) {
						synchronizer.signal(null);
					}

				}, Util.singletonDictionary(Constants.SERVICE_PID, factoryPid));
	}

	/**
	 * Creates and registers a configuration listener that expects just one
	 * event.
	 */
	private ConfigurationListenerImpl createConfigurationListener(
			SynchronizerImpl synchronizer) throws Exception {
		ConfigurationListenerImpl listener = new ConfigurationListenerImpl(
				synchronizer, 1);
		bc.registerService(ConfigurationListener.class.getName(), listener,
				null);
		return listener;
	}

	/**
	 * Creates and registers a non visible configuration plugin.
	 */
	private NotVisitablePlugin createConfigurationPlugin() throws Exception {
		NotVisitablePlugin plugin = new NotVisitablePlugin();
		bc.registerService(ConfigurationPlugin.class.getName(), plugin, null);
		return plugin;
	}

	/**
	 * Creates a configuration plugin with the id and ranking
	 * 
	 * @param context The context to collect invocation information
	 * @param id The id of the plugin
	 * @param ranking The ranking of the plugin
	 * @param pids The pids to register the plugin for
	 * @throws Exception If an error occurs
	 */
	private void createConfigurationPlugin(final PluginContext context, int id,
			Object ranking,
			String[] pids) throws Exception {
		trace("Create and register a new ConfigurationPlugin");
		Dictionary<String,Object> props = new Hashtable<>();
		if (ranking != null) {
			props.put(ConfigurationPlugin.CM_RANKING, ranking);
		}
		if (pids != null) {
			props.put(ConfigurationPlugin.CM_TARGET, pids);
		}

		Plugin plugin = new Plugin(context, ranking, id);
		bc.registerService(ConfigurationPlugin.class.getName(), plugin, props);
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
                    } else {
                    }
                }
            }
        }
	}

	private static final class PluginContext {
		PluginContext() {
			super();
		}
		public String				pid;
		public String				factoryPid;
		public final List<Integer>	invocationOrder	= new ArrayList<>();
		public final List<String>	errors			= new ArrayList<>();
	}

	/**
	 * Test Configuration Plugin gets id and ranking
	 */
	private static class Plugin implements ConfigurationPlugin {
		private final int			id;

		private final Object	ranking;

		private final PluginContext	context;

		Plugin(PluginContext ctx, Object ranking, int x) {
			this.ranking = ranking;
			this.id = x;
			this.context = ctx;
		}

		public void modifyConfiguration(ServiceReference< ? > ref,
				Dictionary<String,Object> props) {
			trace("Calling plugin with cmRanking=" + ranking);
			// add to invocation order
			this.context.invocationOrder.add(id);
			// set prop
			String[] types = (String[]) ref.getProperty("objectClass");
			for (int i = 0; i < types.length; i++) {
				if (ManagedService.class.getName().equals(types[i])) {
					props.put(PROP_MS_PREFIX + id, id);
					break;
				} else if (ManagedServiceFactory.class.getName()
						.equals(types[i])) {
					props.put(PROP_MSF_PREFIX + id, id);
					break;
				}
			}
			// check bundle location
			if (props.get(ConfigurationAdmin.SERVICE_BUNDLELOCATION) != null) {
				this.context.errors.add("Add plugin " + id
						+ " must not see service.bundleLocation property");
			}
			// check pid
			if (this.context.pid != null && !this.context.pid
					.equals(props.get(Constants.SERVICE_PID))) {
				this.context.errors.add("Plugin " + id + " expected pid "
						+ this.context.pid + " but got "
						+ props.get(Constants.SERVICE_PID));
			}
			// check factory pid
			if (this.context.factoryPid != null && !this.context.factoryPid
					.equals(props.get(ConfigurationAdmin.SERVICE_FACTORYPID))) {
				this.context.errors.add("Plugin " + id
						+ " expected factory pid " + this.context.pid
						+ " but got "
						+ props.get(ConfigurationAdmin.SERVICE_FACTORYPID));
			}
			// modify auto props
			props.put(Constants.SERVICE_PID, "a");
			props.put(ConfigurationAdmin.SERVICE_FACTORYPID, "a");
			props.put(ConfigurationAdmin.SERVICE_BUNDLELOCATION, "a");
		}
	}

	/**
	 * <code>ConfigurationPlugin</code> implementation to be used in the
	 * <code>ConfigurationListener</code> test. The plugin should NOT be invoked
	 * when there's no <code>ManagedService</code> or
	 * <code>ManagedServiceFactory</code> registered.
	 */
	private static class NotVisitablePlugin implements ConfigurationPlugin {
		private volatile boolean visited;

		/**
		 * Creates a <code>ConfigurationPlugin</code> instance that has not been
		 * invoked (visited) by a <code>Configuration</code> update event.
		 *
		 */
		public NotVisitablePlugin() {
			visited = false;
		}

		/**
		 * Set plugin to visited (<code>visited = true</code>) when this method
		 * is invoked. If this happens, the <code>ConfigurationListener</code>
		 * tests failed.
		 *
		 * @param ref the service that gets the update.
		 * @param props the properties
		 */
		public void modifyConfiguration(ServiceReference< ? > ref,
				Dictionary<String,Object> props) {
			visited = true;
		}

		/**
		 * Checks if the plugin has not been invoked by a <code>Configuration
		 * </code> update event.
		 *
		 * @return <code>true</code> if plugin has not been visited (invoked).
		 *         <code>false</code>, otherwise.
		 */
		public boolean notVisited() {
			return !visited;
		}
	}
}

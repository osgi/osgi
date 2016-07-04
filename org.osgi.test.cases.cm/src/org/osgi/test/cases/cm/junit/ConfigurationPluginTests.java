/*
 * Copyright (c) OSGi Alliance (2000, 2016). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.cm.junit;

import java.io.IOException;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.cm.ConfigurationPlugin;
import org.osgi.service.cm.SynchronousConfigurationListener;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.test.cases.cm.common.ConfigurationListenerImpl;
import org.osgi.test.cases.cm.common.SynchronizerImpl;
import org.osgi.test.cases.cm.shared.Synchronizer;
import org.osgi.test.cases.cm.shared.Util;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.sleep.Sleep;

/**
 * Tests related to {@link ConfigurationPlugin}
 */
public class ConfigurationPluginTests extends DefaultTestBundleControl {

	private long				SIGNAL_WAITING_TIME;

	private ConfigurationAdmin cm;

	private PermissionAdmin permAdmin;
	private Bundle setAllPermissionBundle;

	/** PIDs of configurations existing before the test. */
	private Set<String>								existingConfigs;

	protected void setUp() throws Exception {
		SIGNAL_WAITING_TIME = getLongProperty(
				"org.osgi.test.cases.cm.signal_waiting_time", 4000);

		cm = getService(ConfigurationAdmin.class);

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
	public void testConfigurationPluginService() throws Exception {
		ConfigurationListenerImpl cl = null;
		NotVisitablePlugin plugin = null;
		String pid = Util
				.createPid(ConfigurationListenerImpl.LISTENER_PID_SUFFIX);
		/* Set up the configuration */
		Configuration conf = cm.getConfiguration(pid);
		Hashtable<String,Object> props = new Hashtable<>();
		props.put("key", "value1");
		SynchronizerImpl synchronizer = new SynchronizerImpl();
		trace("Create and register a new ConfigurationListener");
		cl = createConfigurationListener(synchronizer);
		trace("Create and register a new ConfigurationPlugin");
		plugin = createConfigurationPlugin();
		conf.update(props);
		trace("Wait until the ConfigurationListener has gotten the update");
		try {
			assertTrue("Update done",
					synchronizer.waitForSignal(SIGNAL_WAITING_TIME));
			assertTrue("ConfigurationPlugin not visited", plugin.notVisited());
		} finally {
			removeConfigurationListener(cl);
			removeConfigurationPlugin(plugin);
		}
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
	public void testConfigurationPluginServiceFactory() throws Exception {
		ConfigurationListenerImpl cl = null;
		NotVisitablePlugin plugin = null;
		String factorypid = Util
				.createPid(ConfigurationListenerImpl.LISTENER_PID_SUFFIX);
		/* Set up the configuration */
		Configuration conf = cm.createFactoryConfiguration(factorypid);
		Hashtable<String,Object> props = new Hashtable<>();
		props.put("key", "value1");
		SynchronizerImpl synchronizer = new SynchronizerImpl();
		trace("Create and register a new ConfigurationListener");
		cl = createConfigurationListener(synchronizer);
		trace("Create and register a new ConfigurationPlugin");
		plugin = createConfigurationPlugin();
		conf.update(props);
		trace("Wait until the ConfigurationListener has gotten the update");
		try {
			assertTrue("Update done",
					synchronizer.waitForSignal(SIGNAL_WAITING_TIME));
			assertTrue("ConfigurationPlugin not visited", plugin.notVisited());
		} finally {
			removeConfigurationListener(cl);
			removeConfigurationPlugin(plugin);
		}
	}

	/**
	 * creates and registers a configuration listener that expects just one
	 * event.
	 */
	private ConfigurationListenerImpl createConfigurationListener(
			SynchronizerImpl synchronizer) throws Exception {
		return createConfigurationListener(synchronizer, 1);
	}

	/**
	 * creates and registers a configuration listener that expects eventCount
	 * events.
	 */
	private ConfigurationListenerImpl createConfigurationListener(
			SynchronizerImpl synchronizer, int eventCount) throws Exception {
		ConfigurationListenerImpl listener = new ConfigurationListenerImpl(
				synchronizer, eventCount);
		registerService(ConfigurationListener.class.getName(), listener, null);
		return listener;
	}

	/**
	 * creates and registers a configuration plugin.
	 */
	private NotVisitablePlugin createConfigurationPlugin() throws Exception {
		NotVisitablePlugin plugin = new NotVisitablePlugin();
		registerService(ConfigurationPlugin.class.getName(), plugin, null);
		return plugin;
	}

	/**
	 * unregisters a configuration listener.
	 */
	private void removeConfigurationListener(ConfigurationListener cl)
			throws Exception {
		unregisterService(cl);
	}

	/**
	 * unregisters a configuration plugin.
	 */
	private void removeConfigurationPlugin(ConfigurationPlugin plugin)
			throws Exception {
		unregisterService(plugin);
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
                    } else {
                    }
                }
            }
        }
	}

	class Plugin implements ConfigurationPlugin {
		private int index;

		Plugin(int x) {
			index = x;
		}

		public void modifyConfiguration(ServiceReference< ? > ref,
				Dictionary<String,Object> props) {
			trace("Calling plugin with cmRanking=" + (index * 10));
			String[] types = (String[]) ref.getProperty("objectClass");
			for (int i = 0; i < types.length; i++) {
				if ("org.osgi.service.cm.ManagedService".equals(types[i])) {
					props.put("plugin.ms." + index, "added by plugin#" + index);
					break;
				} else if ("org.osgi.service.cm.ManagedServiceFactory"
						.equals(types[i])) {
					props.put("plugin.factory." + index, "added by plugin#"
							+ index);
					break;
				}
			}
		}
	}

	/**
	 * <code>ConfigurationPlugin</code> implementation to be used in the
	 * <code>ConfigurationListener</code> test. The plugin should NOT be invoked
	 * when there's no <code>ManagedService</code> or
	 * <code>ManagedServiceFactory</code> registered.
	 */
	class NotVisitablePlugin implements ConfigurationPlugin {
		private boolean visited;

		/**
		 * Creates a <code>ConfigurationPlugin</code> instance that has not been
		 * invoked (visited) by a <code>Configuration</code> update event.
		 *
		 */
		public NotVisitablePlugin() {
			visited = false;
		}

		/**
		 * <p>
		 * Callback method when a <code>Configuration</code> update is being
		 * delivered to a registered <code>ManagedService</code> or
		 * <code>ManagedServiceFactory</code> instance.
		 * </p>
		 * <p>
		 * Set plugin to visited (<code>visited = true</code>) when this method
		 * is invoked. If this happens, the <code>ConfigurationListener</code>
		 * tests failed.
		 * </p>
		 *
		 * @param ref
		 *            the <code>ConfigurationAdmin</code> that generated the
		 *            update.
		 * @param props
		 *            the <code>Dictionary</code> containing the properties of
		 *            the <code>
		 * @see org.osgi.service.cm.ConfigurationPlugin#modifyConfiguration(org.osgi.framework.ServiceReference,
		 *      java.util.Dictionary)
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

	/**
	 * <code>ConfigurationPlugin</code> implementation to be used in the
	 * <code>ConfigurationListener</code> test. The plugin should NOT be invoked
	 * when there's no <code>ManagedService</code> or
	 * <code>ManagedServiceFactory</code> registered.
	 */
	class RunnableImpl implements Runnable {
		private final String pid;
		private String location;
		private Configuration conf;
		private Object lock = new Object();

		RunnableImpl(String pid, String location) {
			this.pid = pid;
			this.location = location;
		}

		Configuration getConfiguration() {
			return conf;
		}

		public void run() {
			try {
				Sleep.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			try {
				conf = cm.getConfiguration(pid, location);
			} catch (IOException e) {
				e.printStackTrace();
			}
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				Sleep.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			conf.setBundleLocation(location);

		}

		void unlock() {
			synchronized (lock) {
				lock.notifyAll();
			}
		}

		void unlock(String newLocation) {
			location = newLocation;
			synchronized (lock) {
				lock.notifyAll();
			}
		}
	}

    class SyncEventListener implements SynchronousConfigurationListener {

        private final Synchronizer sync;

        public SyncEventListener(final Synchronizer sync) {
            this.sync = sync;
        }

        public void configurationEvent(ConfigurationEvent event) {
            this.sync.signal();
        }

    }

}

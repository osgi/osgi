/*
 * $Id$
 *
 * Copyright (c) OSGi Alliance (2000, 2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi (OSGI)
 * Specification may be subject to third party intellectual property rights,
 * including without limitation, patent rights (such a third party may or may
 * not be a member of OSGi). OSGi is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL NOT
 * INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY LOSS OF
 * PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF BUSINESS,
 * OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL, PUNITIVE OR
 * CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS DOCUMENT OR THE
 * INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.impl.service.cm;

import java.io.IOException;
import java.util.*;

import org.osgi.framework.*;
import org.osgi.service.cm.*;

/**
 * Implementation class of Configuration Manager. Cares for creation of, access
 * to, and listing Configurations.
 * 
 * @author OSGi Alliance
 * @version $Revision$
 */
public class ConfigurationAdminImpl implements ConfigurationAdmin {
	/**
	 * Object for checking availability of Admin Permission.
	 */
	protected static ConfigurationPermission	configurationPermission	= new ConfigurationPermission("*",ConfigurationPermission.CONFIGURE);
	private static final String					BUNDLE_LOCATION			= "service.bundleLocation";
	private BundleContext						bc;
	private String								location;

	/**
	 * Constructor. Bundle Caller's location is necessary to check access rights
	 * to a Configuration object.
	 * 
	 * @param location Location of bundle, requested CM service.
	 */
	public ConfigurationAdminImpl(BundleContext bc, String location) {
		this.bc = bc;
		this.location = location;
	}

	/**
	 * Creates a new Configuration object with the factory pid passed, with
	 * location equal to bundle caller's and cm generated pid.
	 * 
	 * @param factoryPid pid of ManagedServiceFactory
	 * @return a new Configuration object or null if factory pid is null
	 * @exception IOException when new configuration can not be stored
	 * @exception SecurityException if a configuration with this factory pid
	 *            already exists and is bound to another non null location
	 *            (different from caller's)
	 */
	public Configuration createFactoryConfiguration(String factoryPid)
			throws IOException, SecurityException {
		if (factoryPid == null)
			return null;
		synchronized (ServiceAgent.storage) {
			Vector factoryConfigs = (Vector) ConfigurationStorage.configsF
					.get(factoryPid);
			if (factoryConfigs != null) {
				ConfigurationImpl fConfig;
				for (int i = 0; i < factoryConfigs.size(); i++) {
					fConfig = (ConfigurationImpl) factoryConfigs.elementAt(i);
					if (fConfig.location != null
							&& !location.equals(fConfig.location)) {
						checkPermission();
						break; /*
								 * if we have permission, there is no need to
								 * keep checking
								 */
					}
				}
			}
			ConfigurationImpl config = new ConfigurationImpl(factoryPid,
					ServiceAgent.storage.getNextPID(), location);
			ServiceAgent.storage.storeConfig(config, true);
			return config;
		}
	}

	/**
	 * Creates a new Configuration object with the factory pid and location
	 * passed.
	 * 
	 * @param factoryPid pid of ManagedServiceFactory
	 * @param location bundle location of configured bundle
	 * @return a new Configuration object or null - if factory pid is null
	 * @exception IOException when db is unable to store new configuration
	 * @throws SecurityException if caller does not have
	 *         <code>ConfigurationPermission[CONFIGURE]</code>.
	 */
	public Configuration createFactoryConfiguration(String factoryPid,
			String location) throws IOException, SecurityException {
		checkPermission();
		if (factoryPid == null)
			return null;
		synchronized (ServiceAgent.storage) {
			ConfigurationImpl config = new ConfigurationImpl(factoryPid,
					ServiceAgent.storage.getNextPID(), location);
			if (location == null) {
				ServiceAgent.searchForService(config, false);
			}
			ServiceAgent.storage.storeConfig(config, true);
			return config;
		}
	}

	/**
	 * Gets a new or existing configuration with the pid passed. If
	 * configuration with this pid exists - it is retrived ignoring the location
	 * parameter. Otherwise - if there is no configuration with this pid - a new
	 * Configuration is created, and is associated with the pid and location
	 * passed.
	 * 
	 * @param pid ManagedService's pid, for which the configuration will be
	 *        passed to
	 * @param location location of bundle, which must own the target
	 *        ManagedService
	 * @return a Configuration object; null only if passing null for pid
	 * @exception IOException If db fails to save new configuration
	 * @throws SecurityException if the caller does not have
	 *         <code>ConfigurationPermission[CONFIGURE]</code>.
	 */
	public Configuration getConfiguration(String pid, String location)
			throws IOException, SecurityException {
		checkPermission();
		if (pid == null)
			return null;
		synchronized (ServiceAgent.storage) {
			ConfigurationImpl config = (ConfigurationImpl) ConfigurationStorage.configs
					.get(pid);
			if (config == null) {
				config = new ConfigurationImpl(null, pid, location);
				if (location == null) {
					ServiceAgent.searchForService(config, false);
				}
				ServiceAgent.storage.storeConfig(config, true);
			}
			return config;
		}
	}

	/**
	 * Gets a new or existing configuration, associated with the pid passed. If
	 * configuration with this pid already exists - it is retrived only if the
	 * bundle caller has rights to get is, i.e. if it has admin permission, if
	 * the location of the configuration and the caller's are equal, or if the
	 * configuration's location is null (in this case - caller's location is set
	 * to configuration).
	 * 
	 * @param pid a ManagedService's pid, for which this configuration is
	 *        designed
	 * @return a Configuration object; null only if pid passed is null
	 * @exception IOException if db fails to write new configuration
	 * @throws SecurityException if caller does not have
	 *         <code>ConfigurationPermission[CONFIGURE]</code> and the location of
	 *         the <code>Configuration</code> does not match the location of
	 *         the calling bundle.
	 */
	public Configuration getConfiguration(String pid) throws IOException,
			SecurityException {
		if (pid == null)
			return null;
		synchronized (ServiceAgent.storage) {
			ConfigurationImpl config = (ConfigurationImpl) ConfigurationStorage.configs
					.get(pid);
			if (config == null) {
				config = new ConfigurationImpl(null, pid, location);
				ServiceAgent.storage.storeConfig(config, true);
			}
			else if (config.location == null) {
				config.setLocation(location, false);
				ServiceAgent.storage.storeConfig(config, false);
			}
			else if (!config.location.equals(location)) {
				checkPermission();
			}
			return config;
		}
	}

	/**
	 * List the current <code>Configuration</code> objects which match the
	 * filter.
	 * 
	 * <p>
	 * Only <code>Configuration</code> objects with non- <code>null</code>
	 * properties are considered current. That is,
	 * <code>Configuration.getProperties()</code> is guaranteed not to return
	 * <code>null</code> for each of the returned <code>Configuration</code>
	 * objects.
	 * 
	 * <p>
	 * Normally only <code>Configuration</code> objects that are bound to the
	 * location of the calling bundle are returned. Otherwise, all matching
	 * <code>Configuration</code> objects are returned for which the caller
	 * has <code>ConfigurationPermission[READ]</code> access.
	 * 
	 * <p>
	 * The returned array must only contain Configuration objects that for which
	 * the caller has <code>ConfigurationPermission[READ]</code> access.
	 * <p>
	 * The syntax of the filter string is as defined in the <code>Filter</code>
	 * class. The filter can test any configuration parameters including the
	 * following system properties:
	 * <ul>
	 * <li><code>service.pid</code>-<code>String</code>- the PID under
	 * which this is registered</li>
	 * <li><code>service.factoryPid</code>-<code>String</code>- the
	 * factory if applicable</li>
	 * <li><code>service.bundleLocation</code>-<code>String</code>- the
	 * bundle location</li>
	 * </ul>
	 * The filter can also be <code>null</code>, meaning that all
	 * <code>Configuration</code> objects should be returned.
	 * 
	 * @param filter a <code>Filter</code> object, or <code>null</code> to
	 *        retrieve all <code>Configuration</code> objects.
	 * @return all matching <code>Configuration</code> objects, or
	 *         <code>null</code> if there aren't any
	 * @throws IOException if access to persistent storage fails
	 * @throws InvalidSyntaxException if the filter string is invalid
	 */
	public Configuration[] listConfigurations(String filter)
			throws IOException, InvalidSyntaxException {
		synchronized (ServiceAgent.storage) {
			Enumeration keys = ConfigurationStorage.configs.keys();
			ConfigurationImpl config;
			Filter ldapSearch = null;
			if (filter != null) {
				ldapSearch = bc.createFilter(filter);
			}
			Vector filtered = new Vector();
			boolean filterContainsBundleLocation = (filter != null && filter
					.indexOf(BUNDLE_LOCATION) > -1);
			while (keys.hasMoreElements()) {
				config = (ConfigurationImpl) ConfigurationStorage.configs
						.get(keys.nextElement());
				if (config.props != null) {
					try {
						if (!location.equals(config.location)) {
							checkPermission();
						}
						// properties should not contain service.bundleLocaiton
						// key
						if (filterContainsBundleLocation
								&& config.location != null) {
							config.props.put(BUNDLE_LOCATION, config.location);
						}
						if (filter == null || ldapSearch.match(config.props)) {
							filtered.addElement(config);
						}
						if (filterContainsBundleLocation
								&& config.location != null) {
							config.props.remove(BUNDLE_LOCATION);
						}
					}
					catch (SecurityException e) {
						// the configuration objects which cause this security
						// exc
						// will not be returned; listConfigurations methods does
						// not throw security exc.
					}
				}
			}
			if (filtered.size() > 0) {
				Configuration[] toReturn = new Configuration[filtered.size()];
				filtered.copyInto(toReturn);
				return toReturn;
			}
			else {
				return null;
			}
		}
	}

	static void checkPermission() {
		SecurityManager security = System.getSecurityManager();
		if (security != null) {
			security.checkPermission(configurationPermission);
		}
	}
}
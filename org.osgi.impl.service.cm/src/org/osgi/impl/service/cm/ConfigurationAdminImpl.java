/*
 * $Header$
 *
 * Copyright (c) The OSGi Alliance (2000, 2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative (OSGI)
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

import java.util.Enumeration;
import java.util.Vector;
import java.io.IOException;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.Configuration;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;

/**
 * Implementation class of Configuration Manager. Cares for
 * creation of, access to, and listing Configurations.
 *
 * @author Open Services Gateway Initiative
 * @version $Revision$
 */
public class ConfigurationAdminImpl 
  implements ConfigurationAdmin {

  /**
   * Object for checking availability of Admin Permission.
   */
  protected static AdminPermission adminPermission = new AdminPermission();  
  private static final String BUNDLE_LOCATION = "service.bundleLocation";
  
  private BundleContext bc;
  private String location;



  /**
   * Constructor. Bundle Caller's location is necessary to check 
   * access rights to a Configuration object.
   *
   * @param   location  Location of bundle, requested CM service.
   */
  public ConfigurationAdminImpl(BundleContext bc, String location) {
    this.bc = bc;
    this.location = location;
  }



  /**
   * Creates a new Configuration object with the factory pid passed,
   * with location equal to bundle caller's and cm generated pid.
   *
   * @param   factoryPid  pid of ManagedServiceFactory
   * @return     a new Configuration object or null if factory pid is null
   * @exception   IOException  when new configuration can not be stored
   * @exception   SecurityException  if a configuration with this factory pid
   *                                 already exists and is bound to another
   *                                 non null location (different from caller's)
   */
  public Configuration createFactoryConfiguration(String factoryPid)
    throws IOException, SecurityException {

    if (factoryPid == null) return null;
    
    synchronized (ServiceAgent.storage) {
      Vector factoryConfigs = (Vector) ServiceAgent.storage.configsF.get(factoryPid);
      if (factoryConfigs != null) {
        ConfigurationImpl fConfig;
        for (int i = 0; i < factoryConfigs.size(); i++) {
          fConfig = (ConfigurationImpl) factoryConfigs.elementAt(i);
          if (fConfig.location != null &&
              !location.equals(fConfig.location)) {
              checkPermission();
              break;    /* if we have permission, there is no need to keep checking */
          }
        }
      }
      ConfigurationImpl config = new ConfigurationImpl(factoryPid, ServiceAgent.storage.getNextPID(), location);
      ServiceAgent.storage.storeConfig(config, true);
      return config;
    }
  }


  /**
   * Creates a new Configuration object with the factory pid and location
   * passed.
   *
   * @param   factoryPid  pid of ManagedServiceFactory
   * @param   location  bundle location of configured bundle
   * @return     a new Configuration object or null - if factory pid is null
   * @exception   IOException  when db is unable to store new configuration
   * @exception   SecurityException  if caller bundle has no admin permission
   */
  public Configuration createFactoryConfiguration(String factoryPid, String location)
    throws IOException, SecurityException {
      
    checkPermission();
    if (factoryPid == null) return null;
    synchronized (ServiceAgent.storage) {
      ConfigurationImpl config = 
        new ConfigurationImpl(factoryPid, ServiceAgent.storage.getNextPID(), location);
      if (location == null) {
        ServiceAgent.searchForService(config, false);
      }
      ServiceAgent.storage.storeConfig(config, true);
      return config;
    }
  }


  /**
   * Gets a new or existing configuration with the pid passed.
   * If configuration with this pid exists - it is retrived
   * ignoring the location parameter. Otherwise - if there is no
   * configuration with this pid - a new Configuration is created,
   * and is associated with the pid and location passed.
   *
   * @param   pid  ManagedService's pid, for which the configuration
   *               will be passed to
   * @param   location  location of bundle, which must own the target ManagedService
   * @return     a Configuration object; null only if passing null for pid
   * @exception   IOException  If db fails to save new configuration
   * @exception   SecurityException  if caller has no admin permission
   */
  public Configuration getConfiguration(String pid, String location)
    throws IOException, SecurityException {

    checkPermission();
    if (pid == null) return null;
    synchronized (ServiceAgent.storage) {
      ConfigurationImpl config = (ConfigurationImpl) ServiceAgent.storage.configs.get(pid);
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
   * Gets a new or existing configuration, associated with the pid passed.
   * If configuration with this pid already exists - it is retrived only if
   * the bundle caller has rights to get is, i.e. if it has admin permission,
   * if the location of the configuration and the caller's are equal, or if the
   * configuration's location is null (in this case - caller's location is set
   * to configuration).
   *
   * @param   pid  a ManagedService's pid, for which this configuration is designed
   * @return     a Configuration object; null only if pid passed is null
   * @exception   IOException  if db fails to write new configuration
   * @exception   SecurityException  if caller has no admin permission and is
   *                                 trying to access another bundle's configuration
   */
  public Configuration getConfiguration(String pid) 
    throws IOException, SecurityException {
      
    if (pid == null) return null;
    synchronized (ServiceAgent.storage) {
      ConfigurationImpl config = (ConfigurationImpl) ServiceAgent.storage.configs.get(pid);
      if (config == null) {
        config = new ConfigurationImpl(null, pid, location);
        ServiceAgent.storage.storeConfig(config, true);
      } else if (config.location == null) {
        config.setLocation(location, false);
        ServiceAgent.storage.storeConfig(config, false);
      } else if (!config.location.equals(location)) {
        checkPermission();
      }
      return config;
    }
  }

  /**
   * Lists the configurations, matching the filter. For non-admin bundle callers,
   * only configurations matching their location are scanned.
   *
   * @param   filter  LDAP valid filter or null.
   * @return     a list of configurations matching the filter;
   *             if none - null
   * @exception   IOException  here it is never thrown
   * @exception   InvalidSyntaxException  if filter string is
   *                                      incorrect LDAP filter
   */
  public Configuration [] listConfigurations(String filter)
    throws IOException, InvalidSyntaxException {

    synchronized (ServiceAgent.storage) {
      Enumeration keys = ServiceAgent.storage.configs.keys();
      ConfigurationImpl config;
      
      Filter ldapSearch = null;
      if (filter != null) {
        ldapSearch = bc.createFilter(filter);
      }
      Vector filtered = new Vector();
      boolean filterContainsBundleLocation = (filter != null && filter.indexOf(BUNDLE_LOCATION) > -1);
      while (keys.hasMoreElements()) {
        config = (ConfigurationImpl) ServiceAgent.storage.configs.get(keys.nextElement());
        if (config.props != null) {
          try {
            if (!location.equals(config.location)) {
              checkPermission();
            }
            //properties should not contain service.bundleLocaiton key
            if (filterContainsBundleLocation && config.location != null) {
              config.props.put(BUNDLE_LOCATION, config.location);
            }
            if (filter == null || ldapSearch.match(config.props)) {
              filtered.addElement(config);
            }
            if (filterContainsBundleLocation && config.location != null) {
              config.props.remove(BUNDLE_LOCATION);
            }            
          } catch (SecurityException e) {
            //the configuration objects which cause this security exc
            //will not be returned; listConfigurations methods does not throw security exc.
          }
        }
      }
      if (filtered.size() > 0) {
        Configuration [] toReturn = new Configuration[filtered.size()];
        filtered.copyInto(toReturn);
        return toReturn;
      } else {
        return null;
      }
    }
  }

  protected static void checkPermission() {
    SecurityManager security = System.getSecurityManager();
    if (security != null) {
      security.checkPermission(adminPermission);
    }
  }
}
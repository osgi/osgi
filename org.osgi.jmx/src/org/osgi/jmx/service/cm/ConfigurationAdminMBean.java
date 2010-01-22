/*
 * Copyright (c) OSGi Alliance (2009, 2010). All Rights Reserved.
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

package org.osgi.jmx.service.cm;

import java.io.IOException;

import javax.management.openmbean.TabularData;

import org.osgi.jmx.JmxConstants;

/**
 * This MBean provides the management interface to the OSGi Configuration
 * Administration Service.
 * 
 * @version $Revision$
 * @ThreadSafe
 */
public interface ConfigurationAdminMBean {
	/**
	 * The object name for this mbean.
	 */
	String OBJECTNAME = JmxConstants.OSGI_COMPENDIUM+":service=cm,version=1.3";

	/**
	 * Create a new configuration instance for the supplied persistent id of the
	 * factory, answering the PID of the created configuration
	 * 
	 * @param factoryPid the persistent id of the factory
	 * @return the PID of the created configuration
	 * @throws IOException if the operation failed
	 */
	String createFactoryConfiguration(String factoryPid) throws IOException;

	/**
	 * Create a factory configuration for the supplied persistent id of the
	 * factory and the bundle location bound to bind the created configuration
	 * to, answering the PID of the created configuration
	 * 
	 * @param factoryPid the persistent id of the factory
	 * @param location the bundle location
	 * @return the pid of the created configuation
	 * @throws IOException if the operation failed
	 */
	String createFactoryConfigurationForLocation(String factoryPid, String location)
			throws IOException;

	/**
	 * Delete the configuration
	 * 
	 * @param pid the persistent identifier of the configuration
	 * @throws IOException if the operation fails
	 */
	void delete(String pid) throws IOException;

	/**
	 * Delete the configuration
	 * 
	 * @param pid the persistent identifier of the configuration
	 * @param location the bundle location
	 * @throws IOException if the operation fails
	 */
	void deleteForLocation(String pid, String location) throws IOException;

	/**
	 * Delete the configurations matching the filter specification.
	 * 
	 * @param filter the string representation of the
	 *        <code>org.osgi.framework.Filter</code>
	 * @throws IOException if the operation failed
	 * @throws IllegalArgumentException if the filter is invalid
	 */
	void deleteConfigurations(String filter) throws IOException;

	/**
	 * Answer the bundle location the configuration is bound to
	 * 
	 * @param pid the persistent identifier of the configuration
	 * @return the bundle location
	 * @throws IOException if the operation fails
	 */
	String getBundleLocation(String pid) throws IOException;

	/**
	 * Answer the factory PID if the configuration is a factory configuration,
	 * null otherwise.
	 * 
	 * @param pid the persistent identifier of the configuration
	 * @return the factory PID
	 * @throws IOException if the operation fails
	 */
	String getFactoryPid(String pid) throws IOException;

	/**
	 * Answer the factory PID if the configuration is a factory configuration,
	 * null otherwise.
	 * 
	 * @param pid the persistent identifier of the configuration
	 * @param location the bundle location
	 * @return the factory PID
	 * @throws IOException if the operation fails
	 */
	String getFactoryPidForLocation(String pid, String location) throws IOException;

	/**
	 * Answer the contents of the configuration <p/>
	 * 
	 * @see JmxConstants#PROPERTIES_TYPE for the details of the TabularType
	 * 
	 * @param pid the persistent identifier of the configuration
	 * @return the table of contents
	 * @throws IOException if the operation fails
	 */

	TabularData getProperties(String pid) throws IOException;

	/**
	 * Answer the contents of the configuration <p/>
	 * 
	 * @see JmxConstants#PROPERTIES_TYPE for the details of the TabularType
	 * 
	 * @param pid the persistent identifier of the configuration
	 * @param location the bundle location
	 * @return the table of contents
	 * @throws IOException if the operation fails
	 */
	TabularData getPropertiesForLocation(String pid, String location) throws IOException;

	/**
	 * Answer the list of PID/Location pairs of the configurations managed by
	 * this service
	 * 
	 * @param filter the string representation of the
	 *        <code>org.osgi.framework.Filter</code>
	 * @return the list of configuration PID/Location pairs
	 * @throws IOException if the operation failed
	 * @throws IllegalArgumentException if the filter is invalid
	 */
	String[][] getConfigurations(String filter) throws IOException;

	/**
	 * Set the bundle location the configuration is bound to
	 * 
	 * @param pid the persistent identifier of the configuration
	 * @param location the bundle location
	 * @throws IOException if the operation fails
	 */
	void setBundleLocation(String pid, String location) throws IOException;

	/**
	 * Update the configuration with the supplied properties For each property
	 * entry, the following row is supplied <p/>
	 * 
	 * @see JmxConstants#PROPERTIES_TYPE for the details of the TabularType
	 * 
	 * @param pid the persistent identifier of the configuration
	 * @param properties the table of properties
	 * @throws IOException if the operation fails
	 */
	void update(String pid, TabularData properties) throws IOException;

	/**
	 * Update the configuration with the supplied properties For each property
	 * entry, the following row is supplied <p/>
	 * 
	 * @see JmxConstants#PROPERTIES_TYPE for the details of the TabularType
	 * 
	 * @param pid the persistent identifier of the configuration
	 * @param location the bundle location
	 * @param properties the table of properties
	 * @throws IOException if the operation fails
	 */
	void updateForLocation(String pid, String location, TabularData properties)
			throws IOException;
}
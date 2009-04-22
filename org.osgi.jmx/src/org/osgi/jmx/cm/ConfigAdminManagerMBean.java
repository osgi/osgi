/*
 * Copyright 2008 Oracle Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.osgi.jmx.cm;

import java.io.IOException;

import javax.management.openmbean.TabularData;

/**
 * This MBean provides the management interface to the OSGi Configuration
 * Administration Service.
 * 
 */
public interface ConfigAdminManagerMBean {
	/**
	 * Add or update the property for the configuration identified by the
	 * supplied pid
	 * 
	 * @param pid
	 *            the persistent id of the configuration
	 * @param name
	 *            - the property key to add or update
	 * @param value
	 *            - the string encoded property value to add or update
	 * @param type
	 *            - the type of the property
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the filter is invalid
	 */
	void addProperty(String pid, String name, String value, String type)
			throws IOException;

	/**
	 * Add or update the property for the configuration identified by the
	 * supplied pid and location
	 * 
	 * @param pid
	 *            the persistent id of the configuration
	 * @param location
	 *            - the bundle location
	 * @param name
	 *            - the property key to add or update
	 * @param value
	 *            - the string encoded property value to add or update
	 * @param type
	 *            - the type of the property
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the filter is invalid
	 */
	void addProperty(String pid, String location, String name, String value,
			String type) throws IOException;

	/**
	 * Add or update the property on all configurations matching the supplied
	 * filter
	 * 
	 * @param filter
	 *            the string representation of the
	 *            <code>org.osgi.framework.Filter</code>
	 * @param name
	 *            - the property key to add or update
	 * @param value
	 *            - the string encoded property value to add or update
	 * @param type
	 *            - the type of the property
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the filter is invalid
	 */
	void addPropertyToConfigurations(String filter, String name, String value,
			String type) throws IOException;

	/**
	 * Create a new configuration instance for the supplied persistent id of the
	 * factory, answering the pid of the created configuration
	 * 
	 * @param factoryPid
	 *            - the persistent id of the factory
	 * @return the pid of the created configuation
	 * @throws IOException
	 *             if the operation failed
	 */
	String createFactoryConfiguration(String factoryPid) throws IOException;

	/**
	 * Creae a factory configuration for the supplied persistent id of the
	 * factory and the bundle location bound to bind the created configuration
	 * to, answering the pid of the created configuration
	 * 
	 * @param factoryPid
	 *            - the persistent id of the factory
	 * @param location
	 *            - the bundle location
	 * @return the pid of the created configuation
	 * @throws IOException
	 *             if the operation failed
	 */
	String createFactoryConfiguration(String factoryPid, String location)
			throws IOException;

	/**
	 * Delete the configuration
	 * 
	 * @param pid
	 *            - the persistent identifier of the configuration
	 * @throws IOException
	 *             if the operation fails
	 */
	void delete(String pid) throws IOException;

	/**
	 * Delete the configuration
	 * 
	 * @param pid
	 *            - the persistent identifier of the configuration
	 * @param location
	 *            - the bundle location
	 * @throws IOException
	 *             if the operation fails
	 */
	void delete(String pid, String location) throws IOException;

	/**
	 * Delete the configurations matching the filter spec
	 * 
	 * @param filter
	 *            the string representation of the
	 *            <code>org.osgi.framework.Filter</code>
	 * @throws IOException
	 *             if the operation failed
	 * @throws IllegalArgumentException
	 *             if the filter is invalid
	 */
	void deleteConfigurations(String filter) throws IOException;

	/**
	 * Delete the property from the configuration
	 * 
	 * @param pid
	 *            - the persistent identifier of the configuration
	 * @param key
	 *            the property
	 * @throws IOException
	 *             if the operation fails
	 */
	void deleteProperty(String pid, String key) throws IOException;

	/**
	 * Delete the property from the configuration
	 * 
	 * @param pid
	 *            - the persistent identifier of the configuration
	 * @param location
	 *            - the bundle location
	 * @param key
	 *            the property
	 * @throws IOException
	 *             if the operation fails
	 */
	void deleteProperty(String pid, String location, String key)
			throws IOException;

	/**
	 * Remove the property from all configurations matching the supplied filter
	 * 
	 * @param filter
	 *            the string representation of the
	 *            <code>org.osgi.framework.Filter</code>
	 * @param key
	 *            the property key to be removed
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the filter is invalid
	 */
	void deletePropertyFromConfigurations(String filter, String key)
			throws IOException;

	/**
	 * Answer the bundle location the configuration is bound to
	 * 
	 * @param pid
	 *            - the persistent identifier of the configuration
	 * @return the bundle location
	 * @throws IOException
	 *             if the operation fails
	 */
	String getBundleLocation(String pid) throws IOException;

	/**
	 * Answer the factory pid if the configuration is a factory configuration,
	 * null otherwise.
	 * 
	 * @param pid
	 *            - the persistent identifier of the configuration
	 * @return the factory pid
	 * @throws IOException
	 *             if the operation fails
	 */
	String getFactoryPid(String pid) throws IOException;

	/**
	 * Answer the factory pid if the configuration is a factory configuration,
	 * null otherwise.
	 * 
	 * @param pid
	 *            - the persistent identifier of the configuration
	 * @param location
	 *            - the bundle location
	 * @return the factory pid
	 * @throws IOException
	 *             if the operation fails
	 */
	String getFactoryPid(String pid, String location) throws IOException;

	/**
	 * Answer the credentials of the configuration
	 * <p>
	 * 
	 * @see org.osgi.jmx.codec.OSGiProperties for the details of the TabularType
	 *      <p>
	 *      For each propery entry, the following row is returned
	 *      <ul>
	 *      <li>Property Key - the string key</li>
	 *      <li>Property Value - the stringified version of the property value</li>
	 *      <li>Property Value Type - the type of the property value</li>
	 *      </ul>
	 * 
	 * @param pid
	 *            - the persistent identifier of the configuration
	 * @return the table of credentials
	 * @throws IOException
	 *             if the operation fails
	 */
	TabularData getProperties(String pid) throws IOException;

	/**
	 * Answer the credentials of the configuration
	 * <p>
	 * 
	 * @see org.osgi.jmx.codec.OSGiProperties for the details of the TabularType
	 *      <p>
	 *      For each propery entry, the following row is returned
	 *      <ul>
	 *      <li>Property Key - the string key</li>
	 *      <li>Property Value - the stringified version of the property value</li>
	 *      <li>Property Value Type - the type of the property value</li>
	 *      </ul>
	 * 
	 * @param pid
	 *            - the persistent identifier of the configuration
	 * @param location
	 *            - the bundle location
	 * @return the table of credentials
	 * @throws IOException
	 *             if the operation fails
	 */
	TabularData getProperties(String pid, String location) throws IOException;

	/**
	 * Answer the list of PID/Location pairs of the configurations managed by
	 * this service
	 * 
	 * @param filter
	 *            the string representation of the
	 *            <code>org.osgi.framework.Filter</code>
	 * @return the list of configuration PID/Location pairs
	 * @throws IOException
	 *             if the operation failed
	 * @throws IllegalArgumentException
	 *             if the filter is invalid
	 */
	String[][] listConfigurations(String filter) throws IOException;

	/**
	 * Set the bundle location the configuration is bound to
	 * 
	 * @param pid
	 *            - the persistent identifier of the configuration
	 * @param location
	 *            - the bundle location
	 * @throws IOException
	 *             if the operation fails
	 */
	void setBundleLocation(String pid, String location) throws IOException;

	/**
	 * Update the configuration with the supplied properties For each propery
	 * entry, the following row is supplied
	 * <p>
	 * 
	 * @see org.osgi.jmx.codec.OSGiProperties for the details of the TabularType
	 *      <p>
	 *      <ul>
	 *      <li>Property Key - the string key</li>
	 *      <li>Property Value - the stringified version of the property value</li>
	 *      <li>Property Value Type - the type of the property value</li>
	 *      </ul>
	 * 
	 * @param pid
	 *            - the persistent identifier of the configuration
	 * @param properties
	 *            - the table of properties
	 * @throws IOException
	 *             if the operation fails
	 */
	void update(String pid, TabularData properties) throws IOException;

	/**
	 * Update the configuration with the supplied properties For each propery
	 * entry, the following row is supplied
	 * <p>
	 * 
	 * @see org.osgi.jmx.codec.OSGiProperties for the details of the TabularType
	 *      <p>
	 *      <ul>
	 *      <li>Property Key - the string key</li>
	 *      <li>Property Value - the stringified version of the property value</li>
	 *      <li>Property Value Type - the type of the property value</li>
	 *      </ul>
	 * 
	 * @param pid
	 *            - the persistent identifier of the configuration
	 * @param location
	 *            - the bundle location
	 * @param properties
	 *            - the table of properties
	 * @throws IOException
	 *             if the operation fails
	 */
	void update(String pid, String location, TabularData properties)
			throws IOException;
}
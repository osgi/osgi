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

package org.osgi.service.jdbc;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;

/**
 * A factory for JDBC connection factories. There are 3 preferred connection
 * factories for getting JDBC connections: <code>javax.sql.DataSource</code>,
 * <code>javax.sql.ConnectionPoolDataSource</code>, and
 * <code>javax.sql.XADataSource</code>.
 * 
 * DataSource providers should implement this interface and register it as an
 * OSGi service with the JDBC driver class name in the
 * {@link #OSGI_JDBC_DRIVER_CLASS} property.
 * 
 * @version $Id$
 * @ThreadSafe
 */
public interface DataSourceFactory {
	/**
	 * Service property used by a JDBC driver to declare the driver class when
	 * registering a JDBC DataSourceFactory service. Clients may filter or test
	 * this property to determine if the driver is suitable, or the desired one.
	 */
	public static final String	OSGI_JDBC_DRIVER_CLASS		= "osgi.jdbc.driver.class";

	/**
	 * Service property used by a JDBC driver to declare the driver name when
	 * registering a JDBC DataSourceFactory service. Clients may filter or test
	 * this property to determine if the driver is suitable, or the desired one.
	 */
	public static final String	OSGI_JDBC_DRIVER_NAME		= "osgi.jdbc.driver.name";

	/**
	 * Service property used by a JDBC driver to declare the driver version when
	 * registering a JDBC DataSourceFactory service. Clients may filter or test
	 * this property to determine if the driver is suitable, or the desired one.
	 */
	public static final String	OSGI_JDBC_DRIVER_VERSION	= "osgi.jdbc.driver.version";

	/**
	 * The "databaseName" property that DataSource clients should supply a value
	 * for when calling {@link #createDataSource(Properties)}.
	 */
	public static final String	JDBC_DATABASE_NAME			= "databaseName";

	/**
	 * The "dataSourceName" property that DataSource clients should supply a
	 * value for when calling {@link #createDataSource(Properties)}.
	 */
	public static final String	JDBC_DATASOURCE_NAME		= "dataSourceName";

	/**
	 * The "description" property that DataSource clients should supply a value
	 * for when calling {@link #createDataSource(Properties)}.
	 */
	public static final String	JDBC_DESCRIPTION			= "description";

	/**
	 * The "networkProtocol" property that DataSource clients should supply a
	 * value for when calling {@link #createDataSource(Properties)}.
	 */
	public static final String	JDBC_NETWORK_PROTOCOL		= "networkProtocol";

	/**
	 * The "password" property that DataSource clients should supply a value for
	 * when calling {@link #createDataSource(Properties)}.
	 */
	public static final String	JDBC_PASSWORD				= "password";

	/**
	 * The "portNumber" property that DataSource clients should supply a value
	 * for when calling {@link #createDataSource(Properties)}.
	 */
	public static final String	JDBC_PORT_NUMBER			= "portNumber";

	/**
	 * The "roleName" property that DataSource clients should supply a value for
	 * when calling {@link #createDataSource(Properties)}.
	 */
	public static final String	JDBC_ROLE_NAME				= "roleName";

	/**
	 * The "serverName" property that DataSource clients should supply a value
	 * for when calling {@link #createDataSource(Properties)}.
	 */
	public static final String	JDBC_SERVER_NAME			= "serverName";

	/**
	 * The "user" property that DataSource clients should supply a value for
	 * when calling {@link #createDataSource(Properties)}.
	 */
	public static final String	JDBC_USER					= "user";

	/**
	 * The "url" property that DataSource clients should supply a value for when
	 * calling {@link #createDataSource(Properties)}.
	 */
	public static final String	JDBC_URL					= "url";

	/**
	 * The "initialPoolSize" property that ConnectionPoolDataSource and
	 * XADataSource clients may supply a value for when calling
	 * {@link #createConnectionPoolDataSource(Properties)} or
	 * {@link #createXADataSource(Properties)} on drivers that support
	 * this property.
	 */
	public static final String	JDBC_INITIAL_POOL_SIZE		= "initialPoolSize";

	/**
	 * The "maxIdleTime" property that ConnectionPoolDataSource and 
	 * XADataSource clients may supply a value for when calling
	 * {@link #createConnectionPoolDataSource(Properties)} or
	 * {@link #createXADataSource(Properties)} on drivers that support
	 * this property.
	 */
	public static final String	JDBC_MAX_IDLE_TIME			= "maxIdleTime";

	/**
	 * The "maxPoolSize" property that ConnectionPoolDataSource and 
	 * XADataSource clients may supply a value for when calling
	 * {@link #createConnectionPoolDataSource(Properties)} or
	 * {@link #createXADataSource(Properties)} on drivers that support
	 * this property.
	 */
	public static final String	JDBC_MAX_POOL_SIZE			= "maxPoolSize";

	/**
	 * The "maxStatements" property that ConnectionPoolDataSource and
	 * XADataSource clients may supply a value for when calling
	 * {@link #createConnectionPoolDataSource(Properties)} or
	 * {@link #createXADataSource(Properties)} on drivers that support
	 * this property.
	 */
	public static final String	JDBC_MAX_STATEMENTS			= "maxStatements";

	/**
	 * The "minPoolSize" property that ConnectionPoolDataSource and 
	 * XADataSource clients may supply a value for when calling
	 * {@link #createConnectionPoolDataSource(Properties)} or
	 * {@link #createXADataSource(Properties)} on drivers that support
	 * this property.
	 */
	public static final String	JDBC_MIN_POOL_SIZE			= "minPoolSize";

	/**
	 * The "propertyCycle" property that ConnectionPoolDataSource and
	 * XADataSource clients may supply a value for when calling
	 * {@link #createConnectionPoolDataSource(Properties)} or
	 * {@link #createXADataSource(Properties)} on drivers that support
	 * this property.
	 */
	public static final String	JDBC_PROPERTY_CYCLE			= "propertyCycle";

	/**
	 * Create a new <code>DataSource</code> using the given properties.
	 * 
	 * @param props The properties used to configure the <code>DataSource</code>
	 *        . <code>null</code> indicates no properties. If the property
	 *        cannot be set on the <code>DataSource</code> being created then a
	 *        <code>SQLException</code> must be thrown.
	 * @return A configured <code>DataSource</code>.
	 * @throws SQLException If the <code>DataSource</code> cannot be created.
	 */
	public DataSource createDataSource(Properties props) throws SQLException;

	/**
	 * Create a new <code>ConnectionPoolDataSource</code> using the given
	 * properties.
	 * 
	 * @param props The properties used to configure the
	 *        <code>ConnectionPoolDataSource</code>. <code>null</code> indicates
	 *        no properties. If the property cannot be set on the
	 *        <code>ConnectionPoolDataSource</code> being created then a
	 *        <code>SQLException</code> must be thrown.
	 * @return A configured <code>ConnectionPoolDataSource</code>.
	 * @throws SQLException If the <code>ConnectionPoolDataSource</code> cannot
	 *         be created.
	 */
	public ConnectionPoolDataSource createConnectionPoolDataSource(
			Properties props) throws SQLException;

	/**
	 * Create a new <code>XADataSource</code> using the given properties.
	 * 
	 * @param props The properties used to configure the
	 *        <code>XADataSource</code>. <code>null</code> indicates no
	 *        properties. If the property cannot be set on the
	 *        <code>XADataSource</code> being created then a
	 *        <code>SQLException</code> must be thrown.
	 * @return A configured <code>XADataSource</code>.
	 * @throws SQLException If the <code>XADataSource</code> cannot be created.
	 */
	public XADataSource createXADataSource(Properties props)
			throws SQLException;

	/**
	 * Create a new <code>Driver</code> using the given properties.
	 * 
	 * @param props The properties used to configure the <code>Driver</code>.
	 *        <code>null</code> indicates no properties. If the property cannot
	 *        be set on the <code>Driver</code> being created then a
	 *        <code>SQLException</code> must be thrown.
	 * @return A configured <code>Driver</code>.
	 * @throws SQLException If the <code>Driver</code> cannot be created.
	 */
	public Driver createDriver(Properties props) throws SQLException;
}

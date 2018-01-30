/*
 * Copyright (c) OSGi Alliance (2016, 2018). All Rights Reserved.
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
package org.osgi.service.transaction.control.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.transaction.control.TransactionException;

/**
 * A factory for creating JDBCConnectionProvider instances
 * <p>
 * This factory can be used if the {@link JDBCConnectionProvider} should not be
 * a public service, for example to protect a username/password.
 */
@ProviderType
public interface JDBCConnectionProviderFactory {

	/**
	 * The property used to determine whether XA enlistment is enabled for this
	 * resource provider
	 */
	public static final String	XA_ENLISTMENT_ENABLED		= "osgi.xa.enabled";

	/**
	 * The property used to determine whether local enlistment is enabled for
	 * this resource provider
	 */
	public static final String	LOCAL_ENLISTMENT_ENABLED	= "osgi.local.enabled";

	/**
	 * The property used to determine whether XA recovery is enabled for this
	 * resource provider
	 */
	public static String		XA_RECOVERY_ENABLED			= "osgi.recovery.enabled";

	/**
	 * The property used to determine whether connection pooling is enabled for
	 * this resource provider
	 */
	public static final String	CONNECTION_POOLING_ENABLED	= "osgi.connection.pooling.enabled";

	/**
	 * The property used to set the maximum amount of time that the pool should
	 * wait for a connection
	 */
	public static final String	CONNECTION_TIMEOUT			= "osgi.connection.timeout";

	/**
	 * The property used to set the maximum amount of time that connections in
	 * the pool should remain idle before being closed
	 */
	public static final String	IDLE_TIMEOUT				= "osgi.idle.timeout";

	/**
	 * The property used to set the maximum amount of time that connections in
	 * the pool should remain open
	 */
	public static final String	CONNECTION_LIFETIME			= "osgi.connection.lifetime";

	/**
	 * The property used to set the minimum number of connections that should be
	 * held in the pool
	 */
	public static final String	MIN_CONNECTIONS				= "osgi.connection.min";

	/**
	 * The property used to set the maximum number of connections that should be
	 * held in the pool
	 */
	public static final String	MAX_CONNECTIONS				= "osgi.connection.max";

	/**
	 * The property used to set the maximum number of connections that should be
	 * held in the pool
	 */
	public static final String	USE_DRIVER					= "osgi.use.driver";

	/**
	 * The property used to set the recovery identifier that should be used by
	 * this resource
	 */
	public static String		OSGI_RECOVERY_IDENTIFIER	= "osgi.recovery.identifier";

	/**
	 * Create a private {@link JDBCConnectionProvider} using a
	 * DataSourceFactory. This call may fail with a {@link TransactionException}
	 * if the supplied configuration is invalid. Examples of invalid
	 * configuration include:
	 * <ul>
	 * <li>The properties request XA enlistment, but the provider implementation
	 * only supports local enlistment</li>
	 * <li>The properties attempt to set a recovery alias, but the provider does
	 * not support recovery.</li>
	 * </ul>
	 * 
	 * @param dsf
	 * @param jdbcProperties The properties to pass to the
	 *            {@link DataSourceFactory} in order to create the underlying
	 *            {@link DataSource}
	 * @param resourceProviderProperties Configuration properties to pass to the
	 *            JDBC Resource Provider runtime
	 * @return A {@link JDBCConnectionProvider} that can be used in transactions
	 */
	JDBCConnectionProvider getProviderFor(DataSourceFactory dsf,
			Properties jdbcProperties,
			Map<String,Object> resourceProviderProperties);

	/**
	 * Create a private {@link JDBCConnectionProvider} using an existing
	 * {@link DataSource}. This call may fail with a
	 * {@link TransactionException} if the supplied configuration is invalid.
	 * Examples of invalid configuration include:
	 * <ul>
	 * <li>The properties request XA enlistment, but the provider implementation
	 * only supports local enlistment</li>
	 * <li>The properties attempt to set a recovery alias, but the provider does
	 * not support recovery.</li>
	 * </ul>
	 * 
	 * @param ds
	 * @param resourceProviderProperties Configuration properties to pass to the
	 *            JDBC Resource Provider runtime
	 * @return A {@link JDBCConnectionProvider} that can be used in transactions
	 */
	JDBCConnectionProvider getProviderFor(DataSource ds,
			Map<String,Object> resourceProviderProperties);

	/**
	 * Create a private {@link JDBCConnectionProvider} using an existing
	 * {@link Driver}. This call may fail with a {@link TransactionException} if
	 * the supplied configuration is invalid. Examples of invalid configuration
	 * include:
	 * <ul>
	 * <li>The properties request XA enlistment, but the provider implementation
	 * only supports local enlistment</li>
	 * <li>The properties attempt to set a recovery alias, but the provider does
	 * not support recovery.</li>
	 * </ul>
	 * 
	 * @param driver
	 * @param jdbcProperties The properties to pass to the {@link Driver} in
	 *            order to create a {@link Connection}
	 * @param resourceProviderProperties Configuration properties to pass to the
	 *            JDBC Resource Provider runtime
	 * @return A {@link JDBCConnectionProvider} that can be used in transactions
	 */
	JDBCConnectionProvider getProviderFor(Driver driver,
			Properties jdbcProperties,
			Map<String,Object> resourceProviderProperties);

	/**
	 * Create a private {@link JDBCConnectionProvider} using an existing
	 * {@link XADataSource}. This call may fail with a
	 * {@link TransactionException} if the supplied configuration is invalid.
	 * Examples of invalid configuration include:
	 * <ul>
	 * <li>The properties request XA enlistment, but the provider implementation
	 * only supports local enlistment</li>
	 * <li>The properties attempt to set a recovery alias, but the provider does
	 * not support recovery.</li>
	 * </ul>
	 * 
	 * @param ds
	 * @param resourceProviderProperties Configuration properties to pass to the
	 *            JDBC Resource Provider runtime
	 * @return A {@link JDBCConnectionProvider} that can be used in transactions
	 */
	JDBCConnectionProvider getProviderFor(XADataSource ds,
			Map<String,Object> resourceProviderProperties);

	/**
	 * Release a {@link JDBCConnectionProvider} instance that has been created
	 * by this factory. Released instances are eligible to be shut down and have
	 * any remaining open connections closed.
	 * <p>
	 * Note that all {@link JDBCConnectionProvider} instances created by this
	 * factory service are implicitly released when the factory service is
	 * released by this bundle.
	 * 
	 * @param provider
	 * @throws IllegalArgumentException if the supplied resource was not created
	 *             by this factory service instance.
	 */
	void releaseProvider(JDBCConnectionProvider provider);

}

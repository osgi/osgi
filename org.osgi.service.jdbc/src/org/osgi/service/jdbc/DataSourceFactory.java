/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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
 * A factory for JDBC connection factories.  There are 3 preferred connection
 * factories for getting JDBC connections: {@link javax.sql.DataSource},
 * {@link javax.sql.ConnectionPoolDataSource}, and {@link javax.sql.XADataSource}.
 * 
 * DataSource providers should implement this interface and register it as an
 * OSGi service with the JDBC driver class name in the "osgi.jdbc.driver.class" property.
 */
public interface DataSourceFactory {
    /**
     * Property used by a JDBC driver to declare the driver class when registering 
     * a JDBC DataSourceFactory service. Clients may filter or test this
     * property to determine if the driver is suitable, or the desired one.
     */
    public static final String OSGI_JDBC_DRIVER_CLASS = "osgi.jdbc.driver.class";
    
    /**
     * Property used by a JDBC driver to declare the driver name when registering 
     * a JDBC DataSourceFactory service. Clients may filter or test this
     * property to determine if the driver is suitable, or the desired one.
     */
    public static final String OSGI_JDBC_DRIVER_NAME = "osgi.jdbc.driver.name";
    
    /**
     * Property used by a JDBC driver to declare the driver version when registering 
     * a JDBC DataSourceFactory service. Clients may filter or test this
     * property to determine if the driver is suitable, or the desired one.
     */
    public static final String OSGI_JDBC_DRIVER_VERSION = "osgi.jdbc.driver.version";
    
    
    /**
     * The "databaseName" property that DataSource clients should supply a value for
     * when calling {@link #createDataSource(Properties)}.
     */
    public static final String JDBC_DATABASE_NAME = "databaseName";

    /**
     * The "dataSourceName" property that DataSource clients should supply a value for
     * when calling {@link #createDataSource(Properties)}.
     */
    public static final String JDBC_DATASOURCE_NAME = "dataSourceName";

    /**
     * The "description" property that DataSource clients should supply a value for
     * when calling {@link #createDataSource(Properties)}.
     */
    public static final String JDBC_DESCRIPTION = "description";

    /**
     * The "networkProtocol" property that DataSource clients should supply a value for
     * when calling {@link #createDataSource(Properties)}.
     */
    public static final String JDBC_NETWORK_PROTOCOL = "networkProtocol";

    /**
     * The "password" property that DataSource clients should supply a value for
     * when calling {@link #createDataSource(Properties)}.
     */
    public static final String JDBC_PASSWORD = "password";

    /**
     * The "portNumber" property that DataSource clients should supply a value for
     * when calling {@link #createDataSource(Properties)}.
     */
    public static final String JDBC_PORT_NUMBER = "portNumber";

    /**
     * The "roleName" property that DataSource clients should supply a value for
     * when calling {@link #createDataSource(Properties)}.
     */
    public static final String JDBC_ROLE_NAME = "roleName";

    /**
     * The "serverName" property that DataSource clients should supply a value for
     * when calling {@link #createDataSource(Properties)}.
     */
    public static final String JDBC_SERVER_NAME = "serverName";

    /**
     * The "user" property that DataSource clients should supply a value for
     * when calling {@link #createDataSource(Properties)}.
     */
    public static final String JDBC_USER = "user";

    /**
     * The "url" property that DataSource clients should supply a value for
     * when calling {@link #createDataSource(Properties)}.
     */
    public static final String JDBC_URL = "url";

    /**
     * The "initialPoolSize" property that ConnectionPoolDataSource and
     * XADataSource clients should supply a value for
     * when calling {@link #createConnectionPoolDataSource(Properties)} or 
     * {@link #createXAPoolDataSource(Properties)}.
     */
    public static final String JDBC_INITIAL_POOL_SIZE = "initialPoolSize";

    /**
     * The "maxIdleTime" property that ConnectionPoolDataSource and
     * XADataSource clients should supply a value for
     * when calling {@link #createConnectionPoolDataSource(Properties)} or 
     * {@link #createXAPoolDataSource(Properties)}.
     */
    public static final String JDBC_MAX_IDLE_TIME = "maxIdleTime";

    /**
     * The "maxPoolSize" property that ConnectionPoolDataSource and
     * XADataSource clients should supply a value for
     * when calling {@link #createConnectionPoolDataSource(Properties)} or 
     * {@link #createXAPoolDataSource(Properties)}.
     */
    public static final String JDBC_MAX_POOL_SIZE = "maxPoolSize";

    /**
     * The "maxStatements" property that ConnectionPoolDataSource and
     * XADataSource clients should supply a value for
     * when calling {@link #createConnectionPoolDataSource(Properties)} or 
     * {@link #createXAPoolDataSource(Properties)}.
     */
    public static final String JDBC_MAX_STATEMENTS = "maxStatements";

    /**
     * The "minPoolSize" property that ConnectionPoolDataSource and
     * XADataSource clients should supply a value for
     * when calling {@link #createConnectionPoolDataSource(Properties)} or 
     * {@link #createXAPoolDataSource(Properties)}.
     */
    public static final String JDBC_MIN_POOL_SIZE = "minPoolSize";

    /**
     * The "propertyCycle" property that ConnectionPoolDataSource and
     * XADataSource clients should supply a value for
     * when calling {@link #createConnectionPoolDataSource(Properties)} or 
     * {@link #createXAPoolDataSource(Properties)}.
     */
    public static final String JDBC_PROPERTY_CYCLE = "propertyCycle";

    /**
     * Create a new {@link DataSource} using the given properties.
     * 
     * @param props The properties used to configure the DataSource.  Null 
     *              indicates no properties.
     *              If the property cannot be set on the DataSource being 
     *              created then a SQLException must be thrown.
     * @return A configured DataSource.
     * @throws SQLException If the DataSource cannot be created.
     */
    public DataSource createDataSource(Properties props) throws SQLException;

    /**
     * Create a new {@link ConnectionPoolDataSource} using the given properties.
     * 
     * @param props The properties used to configure the ConnectionPoolDataSource.
     *              Null indicates no properties. If the property cannot be set on 
     *              the ConnectionPoolDataSource being created then a SQLException
     *              must be thrown.
     * @return A configured ConnectionPoolDataSource.
     * @throws SQLException If the ConnectionPoolDataSource cannot be created.
     */
    public ConnectionPoolDataSource createConnectionPoolDataSource(Properties props)
    		throws SQLException;

    /**
     * Create a new {@link XADataSource} using the given properties.
     * 
     * @param props The properties used to configure the XADataSource.  Null 
     *              indicates no properties. If the property cannot be set on 
     *              the XADataSource being created then a SQLException must be
     *              thrown.
     * @return A configured XADataSource.
     * @throws SQLException If the XADataSource cannot be created.
     */
    public XADataSource createXADataSource(Properties props) throws SQLException;

    /**
     * Create a new {@link Driver} using the given properties.
     * 
     * @param props The properties used to configure the Driver.  Null 
     *              indicates no properties.
     *              If the property cannot be set on the Driver being 
     *              created then a SQLException must be thrown.
     * @return A configured Driver.
     * @throws SQLException If the Driver cannot be created.
     */
    public Driver createDriver( Properties props ) throws SQLException;
}

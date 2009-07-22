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


package org.osgi.test.cases.jdbc.junit;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.jdbc.DerbyEmbeddedDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.test.support.OSGiTestCase;

public class JDBCTestCase extends OSGiTestCase {
	private String databaseName = "dbName";
	private String dataSourceName = "dsName";
	private String description = "desc";
	private String password = "pswd";
	private String user = "usr";
	
	private ServiceReference	ref;
	private DataSourceFactory	factory;

	protected void setUp() {
		ref = getContext()
				.getServiceReference(DataSourceFactory.class.getName());
		assertNotNull("No DataSourceFactory service available", ref);
		factory = (DataSourceFactory) getContext().getService(ref);
		assertNotNull(factory);
		assertEquals( DerbyEmbeddedDataSourceFactory.JDBC_DRIVER_CLASS_PROPERTY_VALUE,
				ref.getProperty( DataSourceFactory.JDBC_DRIVER_CLASS ) );
		assertEquals( DerbyEmbeddedDataSourceFactory.JDBC_DRIVER_NAME_PROPERTY_VALUE,
				ref.getProperty( DataSourceFactory.JDBC_DRIVER_NAME ) );
		assertEquals( DerbyEmbeddedDataSourceFactory.JDBC_DRIVER_VERSION_PROPERTY_VALUE,
				ref.getProperty( DataSourceFactory.JDBC_DRIVER_VERSION ) );
	}
	
	protected void tearDown() {
		getContext().ungetService(ref);
	}

	private void assertDSProperty( Object dataSource, String methodName, Object value ) throws Exception {
		Method method = dataSource.getClass().getMethod( methodName, new Class[] {} );
		Object res = method.invoke( dataSource, new Object[] {} );
		assertEquals( res, value );
	}

	public void testCreateDataSource() throws Exception {
		Properties props = new Properties();
		props.put( DataSourceFactory.JDBC_DATABASE_NAME, databaseName );
		props.put( DataSourceFactory.JDBC_DATASOURCE_NAME, dataSourceName );
		props.put( DataSourceFactory.JDBC_DESCRIPTION, description );
		props.put( DataSourceFactory.JDBC_PASSWORD, password );
		props.put( DataSourceFactory.JDBC_USER, user );

		DataSource ds = factory.createDataSource( props );
		assertDSProperty( ds, "getDatabaseName", databaseName );
		assertDSProperty( ds, "getDataSourceName", dataSourceName );
		assertDSProperty( ds, "getDescription", description );
		assertDSProperty( ds, "getPassword", password );
		assertDSProperty( ds, "getUser", user );

		// make sure we get an exception if we use an unknown property
		props = new Properties();
		props.put( "junk", "junk" );
		try {
			factory.createDataSource( props );
			fail( "Should have gotten a SQLException." );
		} catch ( SQLException ignore ) { }
		
		// make sure we get an exception if we use an invalid property
		props = new Properties();
		props.put( "loginTimeout", "junk" );
		try {
			factory.createDataSource( props );
			fail( "Should have gotten a SQLException." );
		} catch ( SQLException ignore ) { }
	}

	public void testCreateConnectionPoolDataSource() throws Exception {
		Properties props = new Properties();
		props.put( DataSourceFactory.JDBC_DATABASE_NAME, databaseName );
		props.put( DataSourceFactory.JDBC_DATASOURCE_NAME, dataSourceName );
		props.put( DataSourceFactory.JDBC_DESCRIPTION, description );
		props.put( DataSourceFactory.JDBC_PASSWORD, password );
		props.put( DataSourceFactory.JDBC_USER, user );

		ConnectionPoolDataSource ds = factory.createConnectionPoolDataSource( props );
		assertDSProperty( ds, "getDatabaseName", databaseName );
		assertDSProperty( ds, "getDataSourceName", dataSourceName );
		assertDSProperty( ds, "getDescription", description );
		assertDSProperty( ds, "getPassword", password );
		assertDSProperty( ds, "getUser", user );
		
		// make sure we get an exception if we use an unknown property
		props = new Properties();
		props.put( "junk", "junk" );
		try {
			factory.createConnectionPoolDataSource( props );
			fail( "Should have gotten a SQLException." );
		} catch ( SQLException ignore ) { }
	}

	public void testCreateXADataSource() throws Exception {
		Properties props = new Properties();
		props.put( DataSourceFactory.JDBC_DATABASE_NAME, databaseName );
		props.put( DataSourceFactory.JDBC_DATASOURCE_NAME, dataSourceName );
		props.put( DataSourceFactory.JDBC_DESCRIPTION, description );
		props.put( DataSourceFactory.JDBC_PASSWORD, password );
		props.put( DataSourceFactory.JDBC_USER, user );

		XADataSource ds = factory.createXADataSource( props );
		assertDSProperty( ds, "getDatabaseName", databaseName );
		assertDSProperty( ds, "getDataSourceName", dataSourceName );
		assertDSProperty( ds, "getDescription", description );
		assertDSProperty( ds, "getPassword", password );
		assertDSProperty( ds, "getUser", user );
		
		// make sure we get an exception if we use an unknown property
		props = new Properties();
		props.put( "junk", "junk" );
		try {
			factory.createXADataSource( props );
			fail( "Should have gotten a SQLException." );
		} catch ( SQLException ignore ) { }
	}

}

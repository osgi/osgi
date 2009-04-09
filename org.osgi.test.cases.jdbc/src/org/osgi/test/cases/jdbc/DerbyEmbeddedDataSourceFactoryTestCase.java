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


package org.osgi.test.cases.jdbc;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource;
import org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource40;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.derby.jdbc.EmbeddedDataSource40;
import org.apache.derby.jdbc.EmbeddedSimpleDataSource;
import org.apache.derby.jdbc.EmbeddedXADataSource;
import org.apache.derby.jdbc.EmbeddedXADataSource40;
import org.osgi.impl.service.jdbc.DerbyEmbeddedDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import junit.framework.TestCase;

public class DerbyEmbeddedDataSourceFactoryTestCase extends TestCase {
	private String databaseName = "C:\\Software\\db-derby-10.4.2.0-bin\\testdbs\\firstdb";
	private String dataSourceName = "dsName";
	private String description = "desc";
	private String password = "pswd";
	private String user = "usr";
	private int loginTimeout = 5;
	

	public void testCreateDataSource() throws Exception {
		DerbyEmbeddedDataSourceFactory factory = new DerbyEmbeddedDataSourceFactory();
		
		// default no properties
		DataSource ds = factory.createDataSource( null );
		assertTrue( ds instanceof EmbeddedDataSource40 );
		EmbeddedDataSource40 e40ds = ( EmbeddedDataSource40 ) ds;
		assertNull( e40ds.getDatabaseName() );
		assertNull( e40ds.getDataSourceName() );
		assertNull( e40ds.getDescription() );
		assertNull( e40ds.getPassword() );
		assertNull( e40ds.getUser() );
		assertEquals( 0, e40ds.getLoginTimeout() );

		// default with properties
		Properties props = new Properties();
		props.put( DataSourceFactory.JDBC_DATABASE_NAME, databaseName );
		props.put( DataSourceFactory.JDBC_DATASOURCE_NAME, dataSourceName );
		props.put( DataSourceFactory.JDBC_DESCRIPTION, description );
		props.put( DataSourceFactory.JDBC_PASSWORD, password );
		props.put( DataSourceFactory.JDBC_USER, user );
		props.put( "loginTimeout", String.valueOf( loginTimeout ) );

		ds = factory.createDataSource( props );
		assertTrue( ds instanceof EmbeddedDataSource40 );
		e40ds = ( EmbeddedDataSource40 ) ds;
		assertEquals( databaseName, e40ds.getDatabaseName() );
		assertEquals( dataSourceName, e40ds.getDataSourceName() );
		assertEquals( description, e40ds.getDescription() );
		assertEquals( password, e40ds.getPassword() );
		assertEquals( user, e40ds.getUser() );
		assertEquals( loginTimeout, e40ds.getLoginTimeout() );
		
		// make sure we get an exception if we use an unknown property
		props = new Properties();
		props.put( "junk", "junk" );
		try {
			factory.createDataSource( props );
			fail( "Should have gotten a SQLException." );
		} catch ( SQLException ignore ) { }
		
		// make sure we get an exception if we use an unknown property
		props = new Properties();
		props.put( "loginTimeout", "junk" );
		try {
			factory.createDataSource( props );
			fail( "Should have gotten a SQLException." );
		} catch ( SQLException ignore ) { }
		
		// get the specified data source: EmbeddedSimpleDataSource
		props = new Properties();
		props.put( DerbyEmbeddedDataSourceFactory.DERBY_JDBC_PROPERTY_NAME_VERSION,
				   DerbyEmbeddedDataSourceFactory.DERBY_JDBC_PROPERTY_VALUE_VERSION_30 );
		props.put( DerbyEmbeddedDataSourceFactory.DERBY_PROPERTY_NAME_ENVIRONMENT, 
				   DerbyEmbeddedDataSourceFactory.DERBY_PROPERTY_VALUE_ENVIRONMENT_EMBEDDED_SIMPLE );
		ds = factory.createDataSource( props );
		assertTrue( ds instanceof EmbeddedSimpleDataSource );
		
		// get the specified data source: EmbeddedDataSource40
		props = new Properties();
		props.put( DerbyEmbeddedDataSourceFactory.DERBY_JDBC_PROPERTY_NAME_VERSION,
				   DerbyEmbeddedDataSourceFactory.DERBY_JDBC_PROPERTY_VALUE_VERSION_40 );
		ds = factory.createDataSource( props );
		assertTrue( ds instanceof EmbeddedDataSource40 );
		
		
		// get the specified data source: EmbeddedDataSource
		props = new Properties();
		props.put( DerbyEmbeddedDataSourceFactory.DERBY_JDBC_PROPERTY_NAME_VERSION,
				   DerbyEmbeddedDataSourceFactory.DERBY_JDBC_PROPERTY_VALUE_VERSION_30 );
		ds = factory.createDataSource( props );
		assertTrue( ds instanceof EmbeddedDataSource );
	}

	public void testCreateConnectionPoolDataSource() throws Exception {
		DerbyEmbeddedDataSourceFactory factory = new DerbyEmbeddedDataSourceFactory();
		
		// default no properties
		ConnectionPoolDataSource ds = factory.createConnectionPoolDataSource( null );
		assertTrue( ds instanceof EmbeddedConnectionPoolDataSource40 );
		EmbeddedConnectionPoolDataSource40 e40ds = ( EmbeddedConnectionPoolDataSource40 ) ds;
		assertNull( e40ds.getDatabaseName() );
		assertNull( e40ds.getDataSourceName() );
		assertNull( e40ds.getDescription() );
		assertNull( e40ds.getPassword() );
		assertNull( e40ds.getUser() );
		assertEquals( 0, e40ds.getLoginTimeout() );

		// default with properties
		Properties props = new Properties();
		props.put( DataSourceFactory.JDBC_DATABASE_NAME, databaseName );
		props.put( DataSourceFactory.JDBC_DATASOURCE_NAME, dataSourceName );
		props.put( DataSourceFactory.JDBC_DESCRIPTION, description );
		props.put( DataSourceFactory.JDBC_PASSWORD, password );
		props.put( DataSourceFactory.JDBC_USER, user );
		props.put( "loginTimeout", String.valueOf( loginTimeout ) );

		ds = factory.createConnectionPoolDataSource( props );
		assertTrue( ds instanceof EmbeddedConnectionPoolDataSource40 );
		e40ds = ( EmbeddedConnectionPoolDataSource40 ) ds;
		assertEquals( databaseName, e40ds.getDatabaseName() );
		assertEquals( dataSourceName, e40ds.getDataSourceName() );
		assertEquals( description, e40ds.getDescription() );
		assertEquals( password, e40ds.getPassword() );
		assertEquals( user, e40ds.getUser() );
		assertEquals( loginTimeout, e40ds.getLoginTimeout() );
		
		// make sure we get an exception if we use an unknown property
		props = new Properties();
		props.put( "junk", "junk" );
		try {
			factory.createConnectionPoolDataSource( props );
			fail( "Should have gotten a SQLException." );
		} catch ( SQLException ignore ) { }
		
		// get the specified data source: EmbeddedConnectionPoolDataSource40
		props = new Properties();
		props.put( DerbyEmbeddedDataSourceFactory.DERBY_JDBC_PROPERTY_NAME_VERSION,
				   DerbyEmbeddedDataSourceFactory.DERBY_JDBC_PROPERTY_VALUE_VERSION_40 );
		ds = factory.createConnectionPoolDataSource( props );
		assertTrue( ds instanceof EmbeddedConnectionPoolDataSource40 );
		
		
		// get the specified data source: EmbeddedConnectionPoolDataSource
		props = new Properties();
		props.put( DerbyEmbeddedDataSourceFactory.DERBY_JDBC_PROPERTY_NAME_VERSION,
				   DerbyEmbeddedDataSourceFactory.DERBY_JDBC_PROPERTY_VALUE_VERSION_30 );
		ds = factory.createConnectionPoolDataSource( props );
		assertTrue( ds instanceof EmbeddedConnectionPoolDataSource );
	}

	public void testCreateXADataSource() throws Exception {
		DerbyEmbeddedDataSourceFactory factory = new DerbyEmbeddedDataSourceFactory();
		
		// default no properties
		XADataSource ds = factory.createXADataSource( null );
		assertTrue( ds instanceof EmbeddedXADataSource40 );
		EmbeddedXADataSource40 e40ds = ( EmbeddedXADataSource40 ) ds;
		assertNull( e40ds.getDatabaseName() );
		assertNull( e40ds.getDataSourceName() );
		assertNull( e40ds.getDescription() );
		assertNull( e40ds.getPassword() );
		assertNull( e40ds.getUser() );
		assertEquals( 0, e40ds.getLoginTimeout() );

		// default with properties
		Properties props = new Properties();
		props.put( DataSourceFactory.JDBC_DATABASE_NAME, databaseName );
		props.put( DataSourceFactory.JDBC_DATASOURCE_NAME, dataSourceName );
		props.put( DataSourceFactory.JDBC_DESCRIPTION, description );
		props.put( DataSourceFactory.JDBC_PASSWORD, password );
		props.put( DataSourceFactory.JDBC_USER, user );
		props.put( "loginTimeout", String.valueOf( loginTimeout ) );

		ds = factory.createXADataSource( props );
		assertTrue( ds instanceof EmbeddedXADataSource40 );
		e40ds = ( EmbeddedXADataSource40 ) ds;
		assertEquals( databaseName, e40ds.getDatabaseName() );
		assertEquals( dataSourceName, e40ds.getDataSourceName() );
		assertEquals( description, e40ds.getDescription() );
		assertEquals( password, e40ds.getPassword() );
		assertEquals( user, e40ds.getUser() );
		assertEquals( loginTimeout, e40ds.getLoginTimeout() );
		
		// make sure we get an exception if we use an unknown property
		props = new Properties();
		props.put( "junk", "junk" );
		try {
			factory.createXADataSource( props );
			fail( "Should have gotten a SQLException." );
		} catch ( SQLException ignore ) { }
		
		// get the specified data source: EmbeddedXADataSource40
		props = new Properties();
		props.put( DerbyEmbeddedDataSourceFactory.DERBY_JDBC_PROPERTY_NAME_VERSION,
				   DerbyEmbeddedDataSourceFactory.DERBY_JDBC_PROPERTY_VALUE_VERSION_40 );
		ds = factory.createXADataSource( props );
		assertTrue( ds instanceof EmbeddedXADataSource40 );
		
		
		// get the specified data source: EmbeddedXADataSource
		props = new Properties();
		props.put( DerbyEmbeddedDataSourceFactory.DERBY_JDBC_PROPERTY_NAME_VERSION,
				   DerbyEmbeddedDataSourceFactory.DERBY_JDBC_PROPERTY_VALUE_VERSION_30 );
		ds = factory.createXADataSource( props );
		assertTrue( ds instanceof EmbeddedXADataSource );
	}

}

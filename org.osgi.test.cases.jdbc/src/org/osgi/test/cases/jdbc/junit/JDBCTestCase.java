/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/


package org.osgi.test.cases.jdbc.junit;

import java.sql.SQLException;
import java.util.Properties;

import org.osgi.framework.ServiceReference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.test.support.OSGiTestCase;

public class JDBCTestCase extends OSGiTestCase {
	private String databaseName = "dbName";
	private String dataSourceName = "dsName";
	private String description = "desc";
	private String password = "pswd";
	private String user = "usr";
	
	private ServiceReference<DataSourceFactory>	ref;
	private DataSourceFactory	factory;

	protected void setUp() {
		ref = getContext()
				.getServiceReference(DataSourceFactory.class);
		assertNotNull("No DataSourceFactory service available", ref);
		factory = getContext().getService(ref);
		assertNotNull(factory);
	}
	
	protected void tearDown() {
		getContext().ungetService(ref);
	}

	public void testRegisteredProperties() {

		Object className = ref
				.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_CLASS);

		assertNotNull(
				"The DataSourceFactory is missing the required osgi.jdbc.driver.class property",
				className);
		assertTrue("The DataSourceFactory driver class is not a String",
				className instanceof String);

		Object driverName = ref
				.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_NAME);
		if (driverName != null) {
			assertTrue("The DataSourceFactory driver name is not a String",
					driverName instanceof String);
		}

		Object driverVersion = ref
				.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_VERSION);
		if (driverVersion != null) {
			assertTrue("The DataSourceFactory driver version is not a String",
					driverName instanceof String);
		}
	}

	public void testCreateDataSource() throws Exception {
		Properties props = new Properties();
		props.put( DataSourceFactory.JDBC_DATABASE_NAME, databaseName );
		props.put( DataSourceFactory.JDBC_DATASOURCE_NAME, dataSourceName );
		props.put( DataSourceFactory.JDBC_DESCRIPTION, description );
		props.put( DataSourceFactory.JDBC_PASSWORD, password );
		props.put( DataSourceFactory.JDBC_USER, user );

		try {
			factory.createDataSource(props);
		} catch (SQLException sqle) {
			// This is allowed as it may not be supported (Driver only
			// DataSourceFactory implementations are perfectly valid).
			// No point in further testing
			return;
		}

	}

	public void testCreateConnectionPoolDataSource() throws Exception {
		Properties props = new Properties();
		props.put( DataSourceFactory.JDBC_DATABASE_NAME, databaseName );
		props.put( DataSourceFactory.JDBC_DATASOURCE_NAME, dataSourceName );
		props.put( DataSourceFactory.JDBC_DESCRIPTION, description );
		props.put( DataSourceFactory.JDBC_PASSWORD, password );
		props.put( DataSourceFactory.JDBC_USER, user );

		try {
			factory.createConnectionPoolDataSource(props);
		} catch (SQLException sqle) {
			// This is allowed as it may not be supported (Driver only
			// DataSourceFactory implementations are perfectly valid).
			// No point in further testing
			return;
		}
	}

	public void testCreateXADataSource() throws Exception {
		Properties props = new Properties();
		props.put( DataSourceFactory.JDBC_DATABASE_NAME, databaseName );
		props.put( DataSourceFactory.JDBC_DATASOURCE_NAME, dataSourceName );
		props.put( DataSourceFactory.JDBC_DESCRIPTION, description );
		props.put( DataSourceFactory.JDBC_PASSWORD, password );
		props.put( DataSourceFactory.JDBC_USER, user );

		try {
			factory.createXADataSource(props);
		} catch (SQLException sqle) {
			// This is allowed as it may not be supported (Driver only
			// DataSourceFactory implementations are perfectly valid).
			// No point in further testing
			return;
		}
	}

	public void testCreateDriver() throws Exception {
		Properties props = new Properties();

		try {
			factory.createDriver(props);
		} catch (SQLException sqle) {
			// This is allowed as it may not be supported (DataSource only
			// DataSourceFactory implementations are perfectly valid).
			// No point in further testing
			return;
		}

	}

	public void testAtLeastOneMethodWorks() {

		Properties props = new Properties();

		try {
			factory.createDriver(props);

			// At least one test passed!
			return;
		} catch (SQLException sqle) {
			// This is allowed as it may not be supported (DataSource only
			// DataSourceFactory implementations are perfectly valid).
		}

		props.put(DataSourceFactory.JDBC_DATABASE_NAME, databaseName);
		props.put(DataSourceFactory.JDBC_DATASOURCE_NAME, dataSourceName);
		props.put(DataSourceFactory.JDBC_DESCRIPTION, description);
		props.put(DataSourceFactory.JDBC_PASSWORD, password);
		props.put(DataSourceFactory.JDBC_USER, user);

		try {
			factory.createXADataSource(props);

			// At least one test passed!
			return;
		} catch (SQLException sqle) {
			// This is allowed as it may not be supported (Driver only
			// DataSourceFactory implementations are perfectly valid).
		}

		try {
			factory.createConnectionPoolDataSource(props);

			// At least one test passed!
			return;
		} catch (SQLException sqle) {
			// This is allowed as it may not be supported (Driver only
			// DataSourceFactory implementations are perfectly valid).
		}

		try {
			factory.createDataSource(props);

			// At least one test passed!
			return;
		} catch (SQLException sqle) {
			// This is allowed as it may not be supported (Driver only
			// DataSourceFactory implementations are perfectly valid).
		}

		fail("None of the DataSourceFactory methods successfully created a DataSource or Driver");
	}

}

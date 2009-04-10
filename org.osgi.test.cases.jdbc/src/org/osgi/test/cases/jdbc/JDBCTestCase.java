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

import java.util.Properties;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import junit.framework.TestCase;

import org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource;
import org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource40;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.derby.jdbc.EmbeddedDataSource40;
import org.apache.derby.jdbc.EmbeddedSimpleDataSource;
import org.apache.derby.jdbc.EmbeddedXADataSource;
import org.apache.derby.jdbc.EmbeddedXADataSource40;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.jdbc.DerbyEmbeddedDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

public class JDBCTestCase extends TestCase {

	private BundleContext	context;

	public void setBundleContext(BundleContext context) {
		this.context = context;
	}

	public void testDataSourceFactoryService() throws Exception {
		String filter = "(" + DataSourceFactory.JDBC_DRIVER +
				"=" +
				DerbyEmbeddedDataSourceFactory.JDBC_DRIVER_PROPERTY_VALUE +
				")";
		ServiceReference[] serviceReferences = context.getServiceReferences(
				DataSourceFactory.class.getName(), filter);
		assertEquals(1, serviceReferences.length);
		DataSourceFactory dsf = (DataSourceFactory) context.getService(serviceReferences[0]);
		assertTrue(dsf instanceof DerbyEmbeddedDataSourceFactory);

		// get the default objects
		DataSource ds = dsf.createDataSource(null);
		ConnectionPoolDataSource cpds = dsf.createConnectionPoolDataSource(null);
		XADataSource xads = dsf.createXADataSource(null);
		assertTrue( ds instanceof EmbeddedDataSource40 );
		assertTrue( cpds instanceof EmbeddedConnectionPoolDataSource40 );
		assertTrue( xads instanceof EmbeddedXADataSource40 );

		// get the simple embedded data source
		Properties props = new Properties();
		props.put(DerbyEmbeddedDataSourceFactory.DERBY_JDBC_PROPERTY_NAME_VERSION,
				DerbyEmbeddedDataSourceFactory.DERBY_JDBC_PROPERTY_VALUE_VERSION_30);
		props.put(DerbyEmbeddedDataSourceFactory.DERBY_PROPERTY_NAME_ENVIRONMENT,
				DerbyEmbeddedDataSourceFactory.DERBY_PROPERTY_VALUE_ENVIRONMENT_EMBEDDED_SIMPLE);
		ds = dsf.createDataSource(props);
		cpds = dsf.createConnectionPoolDataSource(props);
		xads = dsf.createXADataSource(props);
		assertTrue( ds instanceof EmbeddedSimpleDataSource );
		assertTrue( cpds instanceof EmbeddedConnectionPoolDataSource );
		assertTrue( xads instanceof EmbeddedXADataSource );

		// get the 3.0 objects
		props = new Properties();
		props.put(DerbyEmbeddedDataSourceFactory.DERBY_JDBC_PROPERTY_NAME_VERSION,
				DerbyEmbeddedDataSourceFactory.DERBY_JDBC_PROPERTY_VALUE_VERSION_30);
		ds = dsf.createDataSource(props);
		cpds = dsf.createConnectionPoolDataSource(props);
		xads = dsf.createXADataSource(props);
		assertTrue( ds instanceof EmbeddedDataSource );
		assertTrue( cpds instanceof EmbeddedConnectionPoolDataSource );
		assertTrue( xads instanceof EmbeddedXADataSource );

		props = new Properties();
		props.put(DerbyEmbeddedDataSourceFactory.DERBY_JDBC_PROPERTY_NAME_VERSION,
				DerbyEmbeddedDataSourceFactory.DERBY_JDBC_PROPERTY_VALUE_VERSION_30);
		props.put(DerbyEmbeddedDataSourceFactory.DERBY_PROPERTY_NAME_ENVIRONMENT,
				DerbyEmbeddedDataSourceFactory.DERBY_PROPERTY_VALUE_ENVIRONMENT_EMBEDDED);
		ds = dsf.createDataSource(props);
		cpds = dsf.createConnectionPoolDataSource(props);
		xads = dsf.createXADataSource(props);
		assertTrue( ds instanceof EmbeddedDataSource );
		assertTrue( cpds instanceof EmbeddedConnectionPoolDataSource );
		assertTrue( xads instanceof EmbeddedXADataSource );
	}
}


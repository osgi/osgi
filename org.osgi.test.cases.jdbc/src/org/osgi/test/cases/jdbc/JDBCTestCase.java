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

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.derby.jdbc.EmbeddedXADataSource;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.test.support.OSGiTestCase;

public class JDBCTestCase extends OSGiTestCase {

	public void testDataSourceFactoryService() throws Exception {
		ServiceReference[] serviceReferences = getContext()
				.getServiceReferences(
				DataSourceFactory.class.getName(), null);
		assertEquals(1, serviceReferences.length);
		DataSourceFactory dsf = (DataSourceFactory) getContext().getService(
				serviceReferences[0]);

		// get the default objects
		DataSource ds = dsf.createDataSource(null);
		ConnectionPoolDataSource cpds = dsf.createConnectionPoolDataSource(null);
		XADataSource xads = dsf.createXADataSource(null);
		assertTrue( ds instanceof EmbeddedDataSource );
		assertTrue( cpds instanceof EmbeddedConnectionPoolDataSource );
		assertTrue( xads instanceof EmbeddedXADataSource );
	}
}


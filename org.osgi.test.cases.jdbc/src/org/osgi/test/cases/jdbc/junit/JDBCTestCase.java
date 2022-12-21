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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.osgi.service.jdbc.DataSourceFactory.OSGI_JDBC_CAPABILITY;
import static org.osgi.service.jdbc.DataSourceFactory.OSGI_JDBC_CAPABILITY_CONNECTIONPOOLDATASOURCE;
import static org.osgi.service.jdbc.DataSourceFactory.OSGI_JDBC_CAPABILITY_DATASOURCE;
import static org.osgi.service.jdbc.DataSourceFactory.OSGI_JDBC_CAPABILITY_DRIVER;
import static org.osgi.service.jdbc.DataSourceFactory.OSGI_JDBC_CAPABILITY_XADATASOURCE;
import static org.osgi.service.jdbc.DataSourceFactory.OSGI_JDBC_DRIVER_CLASS;
import static org.osgi.service.jdbc.DataSourceFactory.OSGI_JDBC_DRIVER_VERSION;

import java.sql.SQLException;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.test.assertj.servicereference.ServiceReferenceAssert;
import org.osgi.test.junit5.service.ServiceSource;

public class JDBCTestCase {
	private static class DfltProps extends Properties {

		private static final long serialVersionUID = 1L;

		DfltProps() {
			super();
			put(DataSourceFactory.JDBC_DATABASE_NAME, databaseName);
			put(DataSourceFactory.JDBC_DATASOURCE_NAME, dataSourceName);
			put(DataSourceFactory.JDBC_DESCRIPTION, description);
			put(DataSourceFactory.JDBC_PASSWORD, password);
			put(DataSourceFactory.JDBC_USER, user);
			// Drivers can support additional custom configuration properties.
		}
	}

	private static final String	FILTER_CAPABILITY_CONNECTIONPOOLDATASOURCE	= "("
			+ OSGI_JDBC_CAPABILITY + "="
			+ OSGI_JDBC_CAPABILITY_CONNECTIONPOOLDATASOURCE + ")";

	private static final String	FILTER_CAPABILITY_DATASOURCE					= "("
			+ OSGI_JDBC_CAPABILITY + "=" + OSGI_JDBC_CAPABILITY_DATASOURCE
			+ ")";

	private static final String	FILTER_CAPABILITY_DRIVER						= "("
			+ OSGI_JDBC_CAPABILITY + "=" + OSGI_JDBC_CAPABILITY_DRIVER + ")";

	private static final String	FILTER_CAPABILITY_XADATASOURCE					= "("
			+ OSGI_JDBC_CAPABILITY + "=" + OSGI_JDBC_CAPABILITY_XADATASOURCE
			+ ")";

	static String				databaseName									= "dbName";
	static String				dataSourceName									= "dsName";
	static String				description										= "desc";
	static String				password										= "pswd";
	static String				user											= "usr";
	
	/**
	 * Test that the JDBS service defines at least one capability
	 * 
	 * @param sr the service reference parameter
	 */
	@ParameterizedTest
	@ServiceSource(serviceType = DataSourceFactory.class)
	public void testMinimumOneDataSourceFactory(
			ServiceReference<DataSourceFactory> sr) {
		ServiceReferenceAssert.assertThat(sr)
				.hasServicePropertiesThat()
				.containsKey(OSGI_JDBC_CAPABILITY)
				.extractingByKey(OSGI_JDBC_CAPABILITY)
				.isNotNull();
	}

	@ParameterizedTest
	@ServiceSource(serviceType = DataSourceFactory.class)
	public void testRegisteredProperties(
			ServiceReference<DataSourceFactory> sr) {

		ServiceReferenceAssert<DataSourceFactory> sRefAssert = ServiceReferenceAssert
				.assertThat(sr);

		sRefAssert.hasServicePropertiesThat()
				.as("The DataSourceFactory is missing the required osgi.jdbc.driver.class property")
				.containsKey(OSGI_JDBC_DRIVER_CLASS)
				.extractingByKey(OSGI_JDBC_DRIVER_CLASS)
				.isNotNull()
				.as("The DataSourceFactory driver class is not a String")
				.isInstanceOf(String.class);

		Object driverVersion = sr.getProperty(OSGI_JDBC_DRIVER_VERSION);

		if (driverVersion != null) {
			assertTrue(driverVersion instanceof String,
					"The DataSourceFactory driver version is not a String");
		}
	}

	@ParameterizedTest
	@ServiceSource(serviceType = DataSourceFactory.class, filter = FILTER_CAPABILITY_CONNECTIONPOOLDATASOURCE)
	public void testCapabilityConnectionPoolDataSource(DataSourceFactory dsf)
			throws SQLException {

		assertThatNoException()
				.isThrownBy(() -> dsf.createConnectionPoolDataSource(null));

		assertThat(dsf.createConnectionPoolDataSource(null)).isNotNull();

		assertThatNoException().isThrownBy(
				() -> dsf.createConnectionPoolDataSource(new DfltProps()));

		assertThat(dsf.createConnectionPoolDataSource(new DfltProps()))
				.isNotNull();
	}

	@ParameterizedTest
	@ServiceSource(serviceType = DataSourceFactory.class, filter = FILTER_CAPABILITY_DATASOURCE)
	public void testCapabilityDataSource(DataSourceFactory dsf)
			throws SQLException {

		assertThatNoException().isThrownBy(() -> dsf.createDataSource(null));

		assertThat(dsf.createDataSource(null)).isNotNull();

		assertThatNoException()
				.isThrownBy(() -> dsf.createDataSource(new DfltProps()));

		assertThat(dsf.createDataSource(new DfltProps())).isNotNull();
	}

	@ParameterizedTest
	@ServiceSource(serviceType = DataSourceFactory.class, filter = FILTER_CAPABILITY_DRIVER)
	public void testCapabilityDriver(DataSourceFactory dsf)
			throws SQLException {

		assertThatNoException().isThrownBy(() -> dsf.createDriver(null));

		assertThat(dsf.createDriver(null)).isNotNull();

		assertThatNoException()
				.isThrownBy(() -> dsf.createDriver(new Properties()));

		assertThat(dsf.createDriver(new Properties())).isNotNull();

	}

	@ParameterizedTest
	@ServiceSource(serviceType = DataSourceFactory.class, filter = FILTER_CAPABILITY_XADATASOURCE)
	public void testCapabilityXADataSource(DataSourceFactory dsf)
			throws SQLException {

		assertThatNoException().isThrownBy(() -> dsf.createXADataSource(null));

		assertThat(dsf.createXADataSource(null)).isNotNull();

		assertThatNoException()
				.isThrownBy(() -> dsf.createXADataSource(new DfltProps()));

		assertThat(dsf.createXADataSource(new DfltProps())).isNotNull();
	}

}

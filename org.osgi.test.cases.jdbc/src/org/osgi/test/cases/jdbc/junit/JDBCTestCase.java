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
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.osgi.service.jdbc.DataSourceFactory.OSGI_JDBC_DRIVER_CLASS;
import static org.osgi.service.jdbc.DataSourceFactory.OSGI_JDBC_DRIVER_NAME;
import static org.osgi.service.jdbc.DataSourceFactory.OSGI_JDBC_DRIVER_VERSION;

import java.util.Properties;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.params.ParameterizedTest;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.test.assertj.servicereference.ServiceReferenceAssert;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.service.ServiceSource;

public class JDBCTestCase {
	private static String	databaseName	= "dbName";
	private static String	dataSourceName	= "dsName";
	private static String	description		= "desc";
	private static String	password		= "pswd";
	private static String	user			= "usr";

	private static Properties props() {
		Properties props = new Properties();
		props.put(DataSourceFactory.JDBC_DATABASE_NAME, databaseName);
		props.put(DataSourceFactory.JDBC_DATASOURCE_NAME, dataSourceName);
		props.put(DataSourceFactory.JDBC_DESCRIPTION, description);
		props.put(DataSourceFactory.JDBC_PASSWORD, password);
		props.put(DataSourceFactory.JDBC_USER, user);
		// Drivers can support additional custom configuration properties.

		return props;
	}

	@Test
	@Tag("MUST")
	public void testRegisteredMandantoryProperties(@InjectService
	ServiceAware<DataSourceFactory> sa) {

		ServiceReferenceAssert<DataSourceFactory> sRefAssert = ServiceReferenceAssert
				.assertThat(sa.getServiceReference());

		sRefAssert.hasServicePropertiesThat()
				.as("The DataSourceFactory is missing the required osgi.jdbc.driver.class property")
				.containsKey(OSGI_JDBC_DRIVER_CLASS)
				.extractingByKey(OSGI_JDBC_DRIVER_CLASS)
				.isNotNull()
				.as("The DataSourceFactory driver class is not a String")
				.isInstanceOf(String.class);

		Object driverName = sa.getServiceReference()
				.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_NAME);
		if (driverName != null) {
			Assertions.assertThat(driverName).isInstanceOf(String.class);
		}

		Object driverVersion = sa.getServiceReference()
				.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_VERSION);
		if (driverVersion != null) {
			Assertions.assertThat(driverVersion).isInstanceOf(String.class);
		}

	}

	@Test
	@Tag("SHOULD")
	@EnabledIfSystemProperty(named = "org.osgi.tck.cardinality", matches = ".*SHOULD.*")
	public void testRegistereOptionalPropertyName(@InjectService
	ServiceAware<DataSourceFactory> sa) {

		ServiceReferenceAssert.assertThat(sa.getServiceReference())
				.hasServicePropertiesThat()
				.as("The DataSourceFactory is missing the required osgi.jdbc.driver.name property")
				.containsKey(OSGI_JDBC_DRIVER_NAME)
				.extractingByKey(OSGI_JDBC_DRIVER_NAME)
				.isNotNull()
				.as("The DataSourceFactory driver name is not a String")
				.isInstanceOf(String.class);


	}

	@Test
	@Tag("SHOULD")
	@EnabledIfSystemProperty(named = "org.osgi.tck.cardinality", matches = ".*SHOULD.*")
	public void testRegistereOptionalPropertyVersion(@InjectService
	ServiceAware<DataSourceFactory> sa) {

		ServiceReferenceAssert.assertThat(sa.getServiceReference())
				.hasServicePropertiesThat()
				.as("The DataSourceFactory is missing the required osgi.jdbc.driver.version property")
				.containsKey(OSGI_JDBC_DRIVER_VERSION)
				.extractingByKey(OSGI_JDBC_DRIVER_VERSION)
				.isNotNull()
				.as("The DataSourceFactory driver version is not a String")
				.isInstanceOf(String.class);

	}

	@Tag("MUST")
	@ParameterizedTest
	@ServiceSource(serviceType = DataSourceFactory.class)
	public void testAtLeastOneMethodWorks(DataSourceFactory dsfFactory) {

		// One of the DataSourceFactory methods must successfully created a
		// DataSource or Driver"

		assertThat(dsfFactory).satisfiesAnyOf(

				// DataSource
				dsf -> assertThat(dsf).satisfies(

						// no Exception
						f -> assertThatCode(() -> f.createDataSource(props()))
								.doesNotThrowAnyException(),

						// null indicates no properties
						f -> assertThatCode(() -> f.createDataSource(null))
								.doesNotThrowAnyException(),

						// AND return not null
						f -> assertThat(f.createDataSource(props()))
								.isNotNull()),

				// OR ConnectionPoolDataSource
				dsf -> assertThat(dsf).satisfies(

						// no Exception
						f -> assertThatCode(
								() -> f.createConnectionPoolDataSource(props()))
										.doesNotThrowAnyException(),

						// null indicates no properties
						f -> assertThatCode(
								() -> f.createConnectionPoolDataSource(null))
										.doesNotThrowAnyException(),

						// AND return not null
						f -> assertThat(
								f.createConnectionPoolDataSource(props()))
										.isNotNull()),

				// OR XADataSource
				dsf -> assertThat(dsf).satisfies(

						// no Exception
						f -> assertThatCode(() -> f.createXADataSource(props()))
								.doesNotThrowAnyException(),

						// null indicates no properties
						f -> assertThatCode(() -> f.createXADataSource(null))
								.doesNotThrowAnyException(),

						// AND return not null
						f -> assertThat(f.createXADataSource(props()))
								.isNotNull()),

				// OR Driver
				dsf -> assertThat(dsf).satisfies(

						// no Exception
						f -> assertThatCode(
								() -> f.createDriver(new Properties()))
										.doesNotThrowAnyException(),

						// null indicates no properties
						f -> assertThatCode(() -> f.createDriver(null))
								.doesNotThrowAnyException(),

						// AND return not null
						f -> assertThat(f.createDriver(new Properties()))
								.isNotNull()));

	}

}

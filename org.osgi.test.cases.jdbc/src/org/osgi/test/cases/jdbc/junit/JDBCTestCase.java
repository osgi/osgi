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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.osgi.service.jdbc.DataSourceFactory.OSGI_JDBC_CAPABILITY;
import static org.osgi.service.jdbc.DataSourceFactory.OSGI_JDBC_CAPABILITY_CONNECTIONPOOLDATASOURCE;
import static org.osgi.service.jdbc.DataSourceFactory.OSGI_JDBC_CAPABILITY_DATASOURCE;
import static org.osgi.service.jdbc.DataSourceFactory.OSGI_JDBC_CAPABILITY_DRIVER;
import static org.osgi.service.jdbc.DataSourceFactory.OSGI_JDBC_CAPABILITY_XADATASOURCE;
import static org.osgi.service.jdbc.DataSourceFactory.OSGI_JDBC_DRIVER_CLASS;
import static org.osgi.service.jdbc.DataSourceFactory.OSGI_JDBC_DRIVER_NAME;
import static org.osgi.service.jdbc.DataSourceFactory.OSGI_JDBC_DRIVER_VERSION;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.junit.jupiter.params.ParameterizedTest;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.test.assertj.servicereference.ServiceReferenceAssert;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.junit5.service.ServiceSource;

public class JDBCTestCase {
	
	static String				databaseName									= "dbName";
	static String				dataSourceName									= "dsName";
	static String				description										= "desc";
	static String				password										= "pswd";
	static String				user											= "usr";
	
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

	private static final List<JdbcCapability<?>> ALL_CAPABILITIES = Arrays.asList(
			new JdbcCapability<Driver>(OSGI_JDBC_CAPABILITY_DRIVER, DataSourceFactory::createDriver, 
					"org.osgi.service.jdbc.DataSourceFactory.createDriver(Properties)", Properties::new), 
			new JdbcCapability<DataSource>(OSGI_JDBC_CAPABILITY_DATASOURCE, DataSourceFactory::createDataSource, 
					"org.osgi.service.jdbc.DataSourceFactory.createDataSource(Properties)", DfltProps::new), 
			new JdbcCapability<ConnectionPoolDataSource>(OSGI_JDBC_CAPABILITY_CONNECTIONPOOLDATASOURCE, DataSourceFactory::createConnectionPoolDataSource, 
					"org.osgi.service.jdbc.DataSourceFactory.createConnectionPoolDataSource(Properties)", DfltProps::new), 
			new JdbcCapability<XADataSource>(OSGI_JDBC_CAPABILITY_XADATASOURCE, DataSourceFactory::createXADataSource, 
					"org.osgi.service.jdbc.DataSourceFactory.createXADataSource(Properties)", DfltProps::new)
		);

	@InjectBundleContext
	BundleContext bundleContext;
	
	/**
	 * This test that 
	 * <ul>
	 * <li>all required properties are declared and of the required type</li>
	 * <li>all optional properties are of the required type</li>
	 * </ul>
	 * @param sr the service reference of the {@link DataSourceFactory}
	 */
	@ParameterizedTest
	@ServiceSource(serviceType = DataSourceFactory.class)
	public void testRegisteredProperties(ServiceReference<DataSourceFactory> sr) {

		ServiceReferenceAssert<DataSourceFactory> sRefAssert = ServiceReferenceAssert
				.assertThat(sr);

		//first check all mandatory properties
		sRefAssert.hasServicePropertiesThat()
				.as("According to '125.2 Database Driver' the DataSourceFactory must specify the '%s' with its registration.",
						OSGI_JDBC_DRIVER_CLASS)
				.containsKey(OSGI_JDBC_DRIVER_CLASS)
				.extractingByKey(OSGI_JDBC_DRIVER_CLASS)
				.isNotNull()
				.as("According to '125.2 Database Driver' the property '%s' must be a String", OSGI_JDBC_DRIVER_CLASS)
				.isInstanceOf(String.class);
		
		sRefAssert.hasServicePropertiesThat()
				.as(String.format("According to '125.2 Database Driver' the DataSourceFactory must specify the '%s' with its registration.",
						OSGI_JDBC_CAPABILITY))
				.containsKey(OSGI_JDBC_CAPABILITY)
				.extractingByKey(OSGI_JDBC_CAPABILITY)
				.isNotNull()
				.as("According to '125.2 Database Driver' the property '%s' must be a String+", OSGI_JDBC_CAPABILITY)
				.isInstanceOfAny(String.class, String[].class, Collection.class);

		//then check optional ones
		Object driverVersion = sr.getProperty(OSGI_JDBC_DRIVER_VERSION);
		if (driverVersion != null) {
			assertThat(driverVersion)
				.as("According to '125.2 Database Driver' the property '%s' must be a String if specified", OSGI_JDBC_DRIVER_VERSION)
				.isInstanceOf(String.class);
		}
		Object driverName = sr.getProperty(OSGI_JDBC_DRIVER_NAME);
		if (driverName != null) {
			assertThat(driverName)
				.as("According to '125.2 Database Driver' the property '%s' must be a String if specified", OSGI_JDBC_DRIVER_NAME)
				.isInstanceOf(String.class);
		}
		
	}

	/**
	 * This test that 
	 * <ul>
	 * <li>all declared capabilities behave according to their contract</li>
	 * <li>at least one capability is provided</li>
	 * </ul>
	 * @param sr the service reference of the {@link DataSourceFactory}
	 */
	@ParameterizedTest
	@ServiceSource(serviceType = DataSourceFactory.class)
	public void testDeclaredCapabilities(ServiceReference<DataSourceFactory> sr)
			throws SQLException, InvalidSyntaxException {
		boolean providesAny = false;
		for (JdbcCapability<?> capability : ALL_CAPABILITIES) {
			providesAny |= testCapability(sr, capability);
		}
		assertThat(providesAny)
			.as("According to '125.2 Database Driver' the DataSourceFactory must at laest provide one of the follwoing capabilities '%s'.", ALL_CAPABILITIES)
			.isTrue();
	}

	private <T> boolean testCapability(ServiceReference<DataSourceFactory> reference, JdbcCapability<T> capability) throws SQLException, InvalidSyntaxException {
		DataSourceFactory dsf = bundleContext.getService(reference);
		try {
			if (bundleContext.createFilter(String.format("(%s=%s)", OSGI_JDBC_CAPABILITY, capability.capability)).match(reference)) {
				//if the source declares the support, it should work to create items without an exception
				assertThatNoException()
					.as("According to '125.2 Database Driver' a DataSourceFactory declaring the capability %s must not throw any exception when invoking %s with a null argument",
							capability.capability, capability.functionName)
					.isThrownBy(() -> capability.apply(dsf, null));
				assertThat(capability.apply(dsf, null))
					.as("According to '125.2 Database Driver' a DataSourceFactory declaring the capability %s must not return null when invoking %s with a null argument",
						capability.capability, capability.functionName)
					.isNotNull();
				assertThatNoException()
					.as("According to '125.2 Database Driver' a DataSourceFactory declaring the capability %s must not throw any exception when invoking %s with a properties object containing the properties %s",
						capability.capability, capability.functionName, capability.standardPropertiesProvider.get())
					.isThrownBy(() -> capability.apply(dsf, capability.standardPropertiesProvider.get()));
				assertThat(capability.apply(dsf, capability.standardPropertiesProvider.get()))
					.as("According to '125.2 Database Driver' a DataSourceFactory declaring the capability %s must not return null when invoking %s with a properties object containing the properties %s",
						capability.capability, capability.functionName, capability.standardPropertiesProvider.get())
					.isNotNull();
				return true;
			} else {
				//it is required to throw an exception here
				assertThatExceptionOfType(SQLException.class)
					.as("According to '125.2 Database Driver' a DataSourceFactory not declaring the capability %s must throw SQLException when invoking %s with a null argument",
							capability.capability, capability.functionName)
					.isThrownBy(() -> capability.apply(dsf, null));
				assertThatExceptionOfType(SQLException.class)
					.as("According to '125.2 Database Driver' a DataSourceFactory not declaring the capability %s must throw SQLException when invoking %s a properties object containing the properties %s",
						capability.capability, capability.functionName, capability.standardPropertiesProvider.get())
					.isThrownBy(() -> capability.apply(dsf, capability.standardPropertiesProvider.get()));
				return false;
			}
		} finally {
			if (dsf != null) {
				bundleContext.ungetService(reference);
			}
		}
	}
	
	private static interface DsfCallback<T, U, R> {
		R invoke(T t, U u) throws SQLException;
	}

	private static final class JdbcCapability<T> {

		private String capability;
		private DsfCallback<DataSourceFactory, Properties, T> accessorFunction;
		private String functionName;
		private Supplier<Properties> standardPropertiesProvider;

		public JdbcCapability(String capability, DsfCallback<DataSourceFactory, Properties, T> accessorFunction, String functionName, Supplier<Properties> standardPropertiesProvider) {
			this.capability = capability;
			this.accessorFunction = accessorFunction;
			this.functionName = functionName;
			this.standardPropertiesProvider = standardPropertiesProvider;
		}
		
		@Override
		public String toString() {
			return capability;
		}
		
		T apply(DataSourceFactory factory, Properties properties) throws SQLException {
			return accessorFunction.invoke(factory, properties);
		}
		
	}
}

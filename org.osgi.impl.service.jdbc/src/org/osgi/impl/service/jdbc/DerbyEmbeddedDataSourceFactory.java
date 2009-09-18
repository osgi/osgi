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

package org.osgi.impl.service.jdbc;

import java.lang.reflect.Method;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.apache.derby.jdbc.EmbeddedXADataSource;
import org.osgi.service.jdbc.DataSourceFactory;

/**
 * A factory for creating Derby embedded data sources. The properties specified
 * in the create methods determine how the created object is configured.
 * 
 * The following code shows how to get a EmbeddedDataSource.
 * 
 * ServiceTracker factoryServiceTracker = new ServiceTracker( context,
 * DerbyDataSourceFactory.class.getName(), null ); factoryServiceTracker.open();
 * DataSourceFactory dsf = ( DataSourceFactory )
 * factoryServiceTracker.getService(); Properties props = new Properties();
 * props.put( DataSourceFactory.JDBC_DATABASE_NAME,
 * "C:\\Software\\db-derby-10.4.2.0-bin\\testdbs\\firstdb" );
 * 
 * DataSource ds = dsf.createDataSource( props );
 * 
 */
public class DerbyEmbeddedDataSourceFactory implements DataSourceFactory {
	public static final String JDBC_DRIVER_CLASS_PROPERTY_VALUE = "org.apache.derby.jdbc.Driver40";
	public static final String JDBC_DRIVER_NAME_PROPERTY_VALUE = "Derby Embedded Data Source";
	public static final String JDBC_DRIVER_VERSION_PROPERTY_VALUE = "40";

	/**
	 * Create a Derby DataSource object.
	 * 
	 * @param props The properties that define the DataSource implementation to
	 *        create and how the DataSource is configured.
	 * @return The configured DataSource.
	 * @throws SQLException
	 * @see org.osgi.service.jdbc.DataSourceFactory#createDataSource(java.util.Properties)
	 */
	public DataSource createDataSource(Properties props) throws SQLException {
		if (props == null) {
			props = new Properties();
		}
		DataSource dataSource = new EmbeddedDataSource();
		setDataSourceProperties(dataSource, props);
		return dataSource;
	}

	/**
	 * Create a Derby ConnectionPoolDataSource object.
	 * 
	 * @param props The properties that define the ConnectionPoolDataSource
	 *        implementation to create and how the ConnectionPoolDataSource is
	 *        configured.
	 * @return The configured ConnectionPoolDataSource.
	 * @throws SQLException
	 * @see org.osgi.service.jdbc.DataSourceFactory#createConnectionPoolDataSource(java.util.Properties)
	 */
	public ConnectionPoolDataSource createConnectionPoolDataSource(
			Properties props) throws SQLException {
		if (props == null) {
			props = new Properties();
		}
		ConnectionPoolDataSource dataSource = new EmbeddedConnectionPoolDataSource();
		setDataSourceProperties(dataSource, props);
		return dataSource;
	}

	/**
	 * Create a Derby XADataSource object.
	 * 
	 * @param props The properties that define the XADataSource implementation
	 *        to create and how the XADataSource is configured.
	 * @return The configured XADataSource.
	 * @throws SQLException
	 * @see org.osgi.service.jdbc.DataSourceFactory#createXADataSource(java.util.Properties)
	 */
	public XADataSource createXADataSource(Properties props)
			throws SQLException {
		if (props == null) {
			props = new Properties();
		}
		XADataSource dataSource = new EmbeddedXADataSource();
		setDataSourceProperties(dataSource, props);
		return dataSource;
	}

	private void setDataSourceProperties(Object object, Properties props)
			throws SQLException {
		Enumeration enumeration = props.keys();
		while (enumeration.hasMoreElements()) {
			String name = (String) enumeration.nextElement();
			setProperty(object, name, props.getProperty(name));
		}
	}

	private void throwSQLException(Exception cause, String theType, String value)
			throws SQLException {
		SQLException sqlException = new SQLException("Invalid " + theType
				+ " value: " + value);
		sqlException.initCause(cause);
		throw sqlException;
	}

	private Object toBasicType(String value, String type) throws SQLException {
		if (value == null) {
			return null;
		}
		else
			if (type == null || type.equals(String.class.getName())) {
				return value;
			}
			else
				if (type.equals(Integer.class.getName())
						|| type.equals(int.class.getName())) {
					try {
						return Integer.valueOf(value);
					}
					catch (NumberFormatException e) {
						throwSQLException(e, "Integer", value);
					}
				}
				else
					if (type.equals(Float.class.getName())
							|| type.equals(float.class.getName())) {
						try {
							return Float.valueOf(value);
						}
						catch (NumberFormatException e) {
							throwSQLException(e, "Float", value);
						}
					}
					else
						if (type.equals(Long.class.getName())
								|| type.equals(long.class.getName())) {
							try {
								return Long.valueOf(value);
							}
							catch (NumberFormatException e) {
								throwSQLException(e, "Long", value);
							}
						}
						else
							if (type.equals(Double.class.getName())
									|| type.equals(double.class.getName())) {
								try {
									return Double.valueOf(value);
								}
								catch (NumberFormatException e) {
									throwSQLException(e, "Double", value);
								}
							}
							else
								if (type.equals(Character.class.getName())
										|| type.equals(char.class.getName())) {
									if (value.length() != 1) {
										throw new SQLException(
												"Invalid Character value: "
														+ value);
									}

									return new Character(value.charAt(0));
								}
								else
									if (type.equals(Byte.class.getName())
											|| type
													.equals(byte.class
															.getName())) {
										try {
											return Byte.valueOf(value);
										}
										catch (NumberFormatException e) {
											throwSQLException(e, "Byte", value);
										}
									}
									else
										if (type.equals(Short.class.getName())
												|| type.equals(short.class
														.getName())) {
											try {
												return Short.valueOf(value);
											}
											catch (NumberFormatException e) {
												throwSQLException(e, "Short",
														value);
											}
										}
										else
											if (type.equals(Boolean.class
													.getName())
													|| type
															.equals(boolean.class
																	.getName())) {
												try {
													return Boolean
															.valueOf(value);
												}
												catch (NumberFormatException e) {
													throwSQLException(e,
															"Boolean", value);
												}
											}
											else {
												throw new SQLException(
														"Invalid property type: "
																+ type);
											}
		return null;
	}

	private void setProperty(Object object, String name, String value)
			throws SQLException {
		Class type = object.getClass();

		java.beans.PropertyDescriptor[] descriptors;
		try {
			descriptors = java.beans.Introspector.getBeanInfo(type)
					.getPropertyDescriptors();
		}
		catch (Exception exc) {
			SQLException sqlException = new SQLException();
			sqlException.initCause(exc);
			throw sqlException;
		}
		List names = new ArrayList();

		for (int i = 0; i < descriptors.length; i++) {
			if (descriptors[i].getWriteMethod() == null) {
				continue;
			}

			if (descriptors[i].getName().equals(name)) {
				Method method = descriptors[i].getWriteMethod();
				Class paramType = method.getParameterTypes()[0];
				Object param = toBasicType(value, paramType.getName());

				try {
					method.invoke(object, new Object[] {param});
				}
				catch (Exception exc) {
					SQLException sqlException = new SQLException();
					sqlException.initCause(exc);
					throw sqlException;
				}
				return;
			}

			names.add(descriptors[i].getName());
		}
		throw new SQLException("No such property: " + name
				+ ", exists.  Witable properties are: " + names);
	}

    /**
     * Create a new org.apache.derby.jdbc.EmbeddedDriver.
     * 
     * @param props The properties used to configure the Driver.  Null 
     *              indicates no properties.
     *              If the property cannot be set on the Driver being 
     *              created then a SQLException must be thrown.
     * @return A configured org.apache.derby.jdbc.EmbeddedDriver.
     * @throws SQLException If the org.apache.derby.jdbc.EmbeddedDriver cannot be created.
     */
	public Driver createDriver(Properties props) throws SQLException {
		if (props == null) {
			props = new Properties();
		}
		EmbeddedDriver driver = new EmbeddedDriver();
		setDataSourceProperties(driver, props);
		return driver;
	}

}

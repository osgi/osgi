/*
 * Copyright (c) OSGi Alliance (2017, 2018). All Rights Reserved.
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
package org.osgi.test.cases.transaction.control.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.DataSource;
import javax.sql.XAConnection;
import javax.sql.XADataSource;

import org.osgi.service.transaction.control.jdbc.JDBCConnectionProvider;

public class UnwrappableXADataSourceJDBCProviderFactoryTestCase
		extends CommonJDBCProviderFactoryTestCase {

	@Override
	protected JDBCConnectionProvider getProvider(Properties props,
			Map<String,Object> providerConfig) throws SQLException {
		return jdbcResourceProviderFactory.getProviderFor(
				new UnwrappableXADataSource(
						dataSourceFactory.createXADataSource(props)),
				providerConfig);
	}
	
	public static class UnwrappableXADataSource implements DataSource {

		private final XADataSource delegate;

		public UnwrappableXADataSource(XADataSource delegate) {
			this.delegate = delegate;
		}

		@Override
		public PrintWriter getLogWriter() throws SQLException {
			return delegate.getLogWriter();
		}

		@Override
		public void setLogWriter(PrintWriter out) throws SQLException {
			delegate.setLogWriter(out);
		}

		@Override
		public void setLoginTimeout(int seconds) throws SQLException {
			delegate.setLoginTimeout(seconds);
		}

		@Override
		public int getLoginTimeout() throws SQLException {
			return delegate.getLoginTimeout();
		}

		@Override
		public Logger getParentLogger() throws SQLFeatureNotSupportedException {
			return delegate.getParentLogger();
		}

		@Override
		public <T> T unwrap(Class<T> iface) throws SQLException {
			return iface.cast(delegate);
		}

		@Override
		public boolean isWrapperFor(Class< ? > iface) throws SQLException {
			return iface.isInstance(delegate);
		}

		@Override
		public Connection getConnection() throws SQLException {

			XAConnection xaConnection = delegate.getXAConnection();
			xaConnection
					.addConnectionEventListener(new ConnectionEventListener() {

						@Override
						public void connectionErrorOccurred(
								ConnectionEvent event) {}

						@Override
						public void connectionClosed(ConnectionEvent event) {
							try {
								xaConnection.close();
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
			return xaConnection.getConnection();
		}

		@Override
		public Connection getConnection(String username, String password)
				throws SQLException {
			return delegate.getXAConnection(username, password).getConnection();
		}
	}
}

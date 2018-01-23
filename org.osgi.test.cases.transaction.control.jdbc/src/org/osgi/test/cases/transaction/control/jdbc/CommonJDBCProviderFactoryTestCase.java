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

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.osgi.service.transaction.control.jdbc.JDBCConnectionProviderFactory.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;

import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.TransactionException;
import org.osgi.service.transaction.control.jdbc.JDBCConnectionProvider;
import org.osgi.test.cases.transaction.control.jdbc.control.NoTransactionTxControl;
import org.osgi.test.cases.transaction.control.jdbc.control.TestTransactionControl;
import org.osgi.test.cases.transaction.control.jdbc.control.TransactionTxControl;
import org.osgi.test.cases.transaction.control.jdbc.control.UnscopedTxControl;

/**
 * This test covers common ResourceProvider requirements as defined in 147.5.1
 * and 147.5.2 - it is extended to cover the various ways of making a provider.
 */
public abstract class CommonJDBCProviderFactoryTestCase
		extends JDBCResourceTestCase {
	
	protected static final String	H2_URL	= "jdbc:h2:mem:test";

	private Connection				unmanaged;

	private JDBCConnectionProvider	provider;

	public void setUp() throws Exception {
		super.setUp();

		provider = createProvider(0);

		unmanaged = dataSourceFactory.createDriver(null).connect(H2_URL, null);
		unmanaged.setAutoCommit(false);
	}

	private JDBCConnectionProvider createProvider(int poolSize)
			throws SQLException {
		Properties props = new Properties();
		props.setProperty("url", H2_URL);

		Map<String,Object> providerConfig = new HashMap<>();
		if (poolSize == 0) {
			providerConfig.put(CONNECTION_POOLING_ENABLED, false);
		} else {
			providerConfig.put(MIN_CONNECTIONS, poolSize);
			providerConfig.put(MAX_CONNECTIONS, poolSize);
		}

		return getProvider(props, providerConfig);
	}

	protected abstract JDBCConnectionProvider getProvider(Properties props,
			Map<String,Object> providerConfig) throws SQLException;

	public void tearDown() {

		try (Connection c = unmanaged) {
			try (Statement s = unmanaged.createStatement()) {
				s.execute("DROP TABLE TEST");
				unmanaged.commit();
			}
		} catch (Exception e) {}

		closeProvider();

		super.tearDown();
	}

	private void closeProvider() {
		try {
			jdbcResourceProviderFactory.releaseProvider(provider);
		} catch (Exception e) {}
	}

	/**
	 * 147.5.1.1 Connection must be enlisted in the ongoing transaction
	 * 
	 * @throws Exception
	 */
	public void testEnlistmentLocal() throws Exception {

		if (!localEnabled) {
			return;
		}

		try (Statement s = unmanaged.createStatement()) {
			s.execute("CREATE TABLE TEST (MESSAGE varchar(255) NOT NULL)");
			s.execute("INSERT INTO TEST (MESSAGE) VALUES ('TEST')");
			unmanaged.commit();
		}

		TestTransactionControl tran = new TransactionTxControl(true, false);

		Connection scoped = provider.getResource(tran);

		ResultSet rs = scoped.createStatement()
				.executeQuery("SELECT MESSAGE FROM TEST");
		rs.next();
		assertEquals("TEST", rs.getString(1));

		assertEquals(1, tran.getEnlistedLocalResources().size());

		tran.finish(false);
	}

	/**
	 * 147.5.1.1 Connection must be enlisted in the ongoing transaction
	 * 
	 * @throws Exception
	 */
	public void testEnlistmentXA() throws Exception {

		if (!xaEnabled) {
			return;
		}

		try (Statement s = unmanaged.createStatement()) {
			s.execute("CREATE TABLE TEST (MESSAGE varchar(255) NOT NULL)");
			s.execute("INSERT INTO TEST (MESSAGE) VALUES ('TEST')");
			unmanaged.commit();
		}

		TestTransactionControl tran = new TransactionTxControl(false, true);

		Connection scoped = provider.getResource(tran);

		ResultSet rs = scoped.createStatement()
				.executeQuery("SELECT MESSAGE FROM TEST");
		rs.next();
		assertEquals("TEST", rs.getString(1));

		assertEquals(1, tran.getEnlistedXAResources().size());

		tran.finish(false);
	}

	/**
	 * 147.5.1.1 Connection must be released at the end of the scope even if not
	 * explicitly closed
	 * 
	 * @throws Exception
	 */
	public void testNoNeedToClose() throws Exception {

		try (Statement s = unmanaged.createStatement()) {
			s.execute("CREATE TABLE TEST (MESSAGE varchar(255) NOT NULL)");
			s.execute("INSERT INTO TEST (MESSAGE) VALUES ('TEST')");
			unmanaged.commit();
		}

		TestTransactionControl noTran = new NoTransactionTxControl();

		Connection scoped = provider.getResource(noTran);

		for (int i = 0; i < 20; i++) {
			ResultSet rs = scoped.createStatement()
					.executeQuery("SELECT MESSAGE FROM TEST");
			rs.next();
			assertEquals("TEST", rs.getString(1));
			noTran.finish(false);
		}

		try (Statement s = unmanaged.createStatement()) {
			try (ResultSet rs = s.executeQuery(
					"Select COUNT(ID) from INFORMATION_SCHEMA.SESSIONS")) {
				rs.next();
				assertEquals(1, rs.getInt(1));
			}
		}
	}

	/**
	 * 147.5.1.2 Unscoped usage must throw an exception
	 * 
	 * @throws Exception
	 */
	public void testUnscopedUsage() throws Exception {

		TransactionControl unscoped = new UnscopedTxControl();

		Connection scoped = provider.getResource(unscoped);

		try {
			scoped.createStatement();
			fail("Should not be able to use a scoped resource outside of a scope");
		} catch (TransactionException te) {
			// Expected
		}
	}

	/**
	 * 147.5.1.3 Calls to close must be ignored
	 * 
	 * @throws Exception
	 */
	public void testCloseIgnored() throws Exception {

		try (Statement s = unmanaged.createStatement()) {
			s.execute("CREATE TABLE TEST (MESSAGE varchar(255) NOT NULL)");
			s.execute("INSERT INTO TEST (MESSAGE) VALUES ('TEST')");
			unmanaged.commit();
		}

		TestTransactionControl noTran = new NoTransactionTxControl();

		Connection scoped = provider.getResource(noTran);

		ResultSet rs = scoped.createStatement()
				.executeQuery("SELECT MESSAGE FROM TEST");
		rs.next();
		assertEquals("TEST", rs.getString(1));

		// This should be ignored
		scoped.close();

		rs = scoped.createStatement().executeQuery("SELECT MESSAGE FROM TEST");
		rs.next();
		assertEquals("TEST", rs.getString(1));

		noTran.finish(false);
	}

	/**
	 * 147.5.1.3 & 147.5.2.1 Calls to:
	 * <ul>
	 * <li>{@link Connection#commit()}
	 * <li>{@link Connection#setAutoCommit(boolean)}
	 * <li>{@link Connection#setSavepoint()}
	 * <li>{@link Connection#setSavepoint(String)}
	 * <li>{@link Connection#releaseSavepoint()}
	 * <li>{@link Connection#rollback()}
	 * <li>{@link Connection#rollback(Savepoint)}
	 * </ul>
	 * are errors in a Transactional scope
	 * 
	 * @throws Exception
	 */
	public void testCommitAndRollbackIgnored() throws Exception {

		try (Statement s = unmanaged.createStatement()) {
			s.execute("CREATE TABLE TEST (MESSAGE varchar(255) NOT NULL)");
			s.execute("INSERT INTO TEST (MESSAGE) VALUES ('TEST')");
			unmanaged.commit();
		}

		TestTransactionControl tran = new TransactionTxControl(localEnabled,
				xaEnabled);

		Connection scoped = provider.getResource(tran);

		try {
			scoped.commit();
			fail("commit Should not be callable");
		} catch (TransactionException te) {}
		try {
			scoped.setAutoCommit(true);
			fail("setAutoCommit Should not be callable");
		} catch (TransactionException te) {}
		try {
			scoped.setSavepoint();
			fail("setSavepoint Should not be callable");
		} catch (TransactionException te) {}
		try {
			scoped.setSavepoint("test");
			fail("setSavepoint Should not be callable");
		} catch (TransactionException te) {}
		try {
			scoped.releaseSavepoint(null);
			fail("releaseSavepoint Should not be callable");
		} catch (TransactionException te) {}
		try {
			scoped.rollback();
			fail("rollback Should not be callable");
		} catch (TransactionException te) {}
		try {
			scoped.rollback(null);
			fail("rollback Should not be callable");
		} catch (TransactionException te) {}

		tran.finish(false);
	}

	/**
	 * 147.5.1.4 Releasing a resource provider must close its resources
	 * 
	 * @throws Exception
	 */
	public void testProviderRelease() throws Exception {

		try (Statement s = unmanaged.createStatement()) {
			s.execute("CREATE TABLE TEST (MESSAGE varchar(255) NOT NULL)");
			s.execute("INSERT INTO TEST (MESSAGE) VALUES ('TEST')");
			unmanaged.commit();
		}

		TestTransactionControl noTran = new NoTransactionTxControl();

		Connection scoped = provider.getResource(noTran);

		jdbcResourceProviderFactory.releaseProvider(provider);

		try {
			scoped.createStatement();
			fail("This resource should be invalid after the provider is released");
		} catch (TransactionException te) {
			// expected
		}

		noTran.finish(false);

		// Create a pooled provider

		provider = createProvider(5);

		scoped = provider.getResource(noTran);
		ResultSet rs = scoped.createStatement()
				.executeQuery("SELECT MESSAGE FROM TEST");
		rs.next();
		assertEquals("TEST", rs.getString(1));
		noTran.finish(false);

		// Give the pool time to settle
		Thread.sleep(1000);

		try (Statement s = unmanaged.createStatement()) {
			try (ResultSet rs2 = s.executeQuery(
					"Select COUNT(*) from INFORMATION_SCHEMA.SESSIONS")) {
				rs2.next();
				// 5 in the pool and one unmanaged
				assertEquals(6, rs2.getInt(1));
			}
		}

		jdbcResourceProviderFactory.releaseProvider(provider);

		try (Statement s = unmanaged.createStatement()) {
			try (ResultSet rs2 = s.executeQuery(
					"Select COUNT(*) from INFORMATION_SCHEMA.SESSIONS")) {
				rs2.next();
				// The pool is closed and one unmanaged
				assertEquals(1, rs2.getInt(1));
			}
		}

	}

	/**
	 * 147.5.2.1 transaction commit isolation
	 * 
	 * @throws Exception
	 */
	public void testTransactionIsolation() throws Exception {

		try (Statement s = unmanaged.createStatement()) {
			s.execute("CREATE TABLE TEST (MESSAGE varchar(255) NOT NULL)");
			unmanaged.commit();
		}

		TestTransactionControl tran = new TransactionTxControl(localEnabled, xaEnabled);

		Connection scoped = provider.getResource(tran);

		Statement scopedStatement = scoped.createStatement();
		scopedStatement.execute("INSERT INTO TEST (MESSAGE) VALUES ('TEST')");

		assertEquals(emptyList(), getMessages());

		tran.finish(false);

		assertEquals(singletonList("TEST"), getMessages());

		scopedStatement = scoped.createStatement();
		scopedStatement.execute("INSERT INTO TEST (MESSAGE) VALUES ('TEST2')");

		assertEquals(singletonList("TEST"), getMessages());
		tran.setRollbackOnly();

		tran.finish(false);

		assertEquals(asList("TEST"), getMessages());
	}

	private List<String> getMessages() throws SQLException {
		List<String> messages = new ArrayList<>();
		try (Statement s = unmanaged.createStatement()) {
			try (ResultSet rs = s.executeQuery("Select MESSAGE from TEST")) {
				while (rs.next()) {
					messages.add(rs.getString(1));
				}
			}
		}
		return messages;
	}

	/**
	 * 147.5.2.2 no transaction commit isolation
	 * 
	 * @throws Exception
	 */
	public void testNoTransactionIsolation() throws Exception {

		try (Statement s = unmanaged.createStatement()) {
			s.execute("CREATE TABLE TEST (MESSAGE varchar(255) NOT NULL)");
			unmanaged.commit();
		}

		TestTransactionControl noTran = new NoTransactionTxControl();

		Connection scoped = provider.getResource(noTran);

		scoped.setAutoCommit(false);
		Statement scopedStatement = scoped.createStatement();
		scopedStatement.execute("INSERT INTO TEST (MESSAGE) VALUES ('TEST')");

		assertEquals(emptyList(), getMessages());

		scoped.commit();

		assertEquals(singletonList("TEST"), getMessages());

		scopedStatement.execute("INSERT INTO TEST (MESSAGE) VALUES ('TEST2')");

		assertEquals(singletonList("TEST"), getMessages());

		Savepoint sp = scoped.setSavepoint("testing");

		scopedStatement.execute("INSERT INTO TEST (MESSAGE) VALUES ('TEST3')");

		assertEquals(singletonList("TEST"), getMessages());

		scoped.rollback(sp);
		scoped.commit();

		assertEquals(asList("TEST", "TEST2"), getMessages());

		noTran.finish(false);
	}

	/**
	 * 147.5.2.3 Calls to abort must be ignored
	 * 
	 * @throws Exception
	 */
	public void testAbortIgnored() throws Exception {

		try (Statement s = unmanaged.createStatement()) {
			s.execute("CREATE TABLE TEST (MESSAGE varchar(255) NOT NULL)");
			s.execute("INSERT INTO TEST (MESSAGE) VALUES ('TEST')");
			unmanaged.commit();
		}

		TestTransactionControl noTran = new NoTransactionTxControl();

		Connection scoped = provider.getResource(noTran);

		ResultSet rs = scoped.createStatement()
				.executeQuery("SELECT MESSAGE FROM TEST");
		rs.next();
		assertEquals("TEST", rs.getString(1));

		// This should be ignored
		scoped.abort(Executors.newSingleThreadExecutor());

		rs = scoped.createStatement().executeQuery("SELECT MESSAGE FROM TEST");
		rs.next();
		assertEquals("TEST", rs.getString(1));

		noTran.finish(false);
	}
}

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
package org.osgi.test.cases.transaction.control.jpa;

import static java.util.Arrays.asList;
import static java.util.Collections.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

import org.osgi.service.transaction.control.TransactionException;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;
import org.osgi.test.cases.transaction.control.jpa.control.TestTransactionControl;
import org.osgi.test.cases.transaction.control.jpa.control.TestTxControlImpl;
import org.osgi.test.cases.transaction.control.jpa.entity.TestEntity;

/**
 * This test covers common ResourceProvider requirements as defined in 147.5.1
 * and 147.5.2 - it is extended to cover the various ways of making a provider.
 */
public abstract class CommonJPAProviderFactoryTestCase
		extends JPAResourceTestCase {
	
	protected static final String	H2_URL	= "jdbc:h2:mem:test";

	protected Connection				unmanaged;

	protected JPAEntityManagerProvider	provider;

	public void setUp() throws Exception {
		super.setUp();

		provider = createProvider();

		unmanaged = dataSourceFactory.createDriver(null).connect(H2_URL, null);
		unmanaged.setAutoCommit(false);
	}

	protected abstract JPAEntityManagerProvider createProvider(int poolSize)
			throws Exception;

	protected JPAEntityManagerProvider createProvider() throws Exception {
		return createProvider(0);
	}

	public void tearDown() {

		try (Connection c = unmanaged) {
			try (Statement s = unmanaged.createStatement()) {
				s.execute("DROP TABLE TEST");
				unmanaged.commit();
			}
		} catch (Exception e) {}

		safeCloseProvider();

		super.tearDown();
	}

	private void safeCloseProvider() {
		try {
			releaseProvider();
		} catch (Exception e) {}
	}

	protected void releaseProvider() {
		jpaResourceProviderFactory.releaseProvider(provider);
	}

	/**
	 * 147.5.1.1 EntityManager must be enlisted in the ongoing transaction
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

		TestTransactionControl tran = new TestTxControlImpl(true, false, true);

		EntityManager scoped = provider.getResource(tran);

		CriteriaQuery<TestEntity> query = scoped.getCriteriaBuilder()
				.createQuery(TestEntity.class);
		query.from(TestEntity.class);
		assertEquals("TEST",
				scoped.createQuery(query).getSingleResult().message);

		assertEquals(1, tran.getEnlistedLocalResources().size());

		tran.finish(false);
	}

	/**
	 * 147.5.1.1 EntityManager must be enlisted in the ongoing transaction
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

		TestTransactionControl tran = new TestTxControlImpl(false, true, true);

		EntityManager scoped = provider.getResource(tran);

		CriteriaQuery<TestEntity> query = scoped.getCriteriaBuilder()
				.createQuery(TestEntity.class);
		query.from(TestEntity.class);
		assertEquals("TEST",
				scoped.createQuery(query).getSingleResult().message);

		assertEquals(1, tran.getEnlistedXAResources().size());

		tran.finish(false);
	}

	/**
	 * 147.5.1.1 EntityManager must be released at the end of the scope even if
	 * not explicitly closed
	 * 
	 * @throws Exception
	 */
	public void testNoNeedToClose() throws Exception {

		try (Statement s = unmanaged.createStatement()) {
			s.execute("CREATE TABLE TEST (MESSAGE varchar(255) NOT NULL)");
			s.execute("INSERT INTO TEST (MESSAGE) VALUES ('TEST')");
			unmanaged.commit();
		}

		TestTransactionControl noTran = new TestTxControlImpl(true, true,
				false);

		EntityManager scoped = provider.getResource(noTran);

		for (int i = 0; i < 20; i++) {

			CriteriaQuery<TestEntity> query = scoped.getCriteriaBuilder()
					.createQuery(TestEntity.class);
			query.from(TestEntity.class);
			assertEquals("TEST",
					scoped.createQuery(query).getSingleResult().message);
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

		TestTransactionControl noTran = new TestTxControlImpl(true, true,
				false);

		EntityManager scoped = provider.getResource(noTran);

		CriteriaQuery<TestEntity> query = scoped.getCriteriaBuilder()
				.createQuery(TestEntity.class);
		query.from(TestEntity.class);
		assertEquals("TEST",
				scoped.createQuery(query).getSingleResult().message);

		// This should be ignored
		scoped.close();

		query = scoped.getCriteriaBuilder().createQuery(TestEntity.class);
		query.from(TestEntity.class);
		assertEquals("TEST",
				scoped.createQuery(query).getSingleResult().message);

		noTran.finish(false);
	}

	/**
	 * 147.5.1.3 & 147.5.3.1 Calls to {@link EntityManager#getTransaction()} are
	 * errors in a Transactional scope, and calls to
	 * {@link EntityManager#joinTransaction()} are no-ops
	 * 
	 * @throws Exception
	 */
	public void testGetTransactionForbidden() throws Exception {

		try (Statement s = unmanaged.createStatement()) {
			s.execute("CREATE TABLE TEST (MESSAGE varchar(255) NOT NULL)");
			s.execute("INSERT INTO TEST (MESSAGE) VALUES ('TEST')");
			unmanaged.commit();
		}

		TestTransactionControl tran = new TestTxControlImpl(localEnabled,
				xaEnabled, true);

		EntityManager scoped = provider.getResource(tran);

		try {
			scoped.getTransaction();
			fail("commit Should not be callable");
		} catch (TransactionException te) {}

		scoped.joinTransaction();

		assertTrue(scoped.isJoinedToTransaction());

		tran.finish(false);
	}

	/**
	 * 147.5.1.3 & 147.5.3.2 Calls to {@link EntityManager#joinTransaction()}
	 * are errors in no Transaction scope, and calls to
	 * 
	 * @throws Exception
	 */
	public void testJoinForbidden() throws Exception {

		try (Statement s = unmanaged.createStatement()) {
			s.execute("CREATE TABLE TEST (MESSAGE varchar(255) NOT NULL)");
			s.execute("INSERT INTO TEST (MESSAGE) VALUES ('TEST')");
			unmanaged.commit();
		}

		TestTransactionControl noTran = new TestTxControlImpl(localEnabled,
				xaEnabled,
				false);

		EntityManager scoped = provider.getResource(noTran);

		try {
			scoped.joinTransaction();
			fail("joinTransaction Should not be callable");
		} catch (TransactionException te) {}

		assertFalse(scoped.isJoinedToTransaction());

		noTran.finish(false);
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

		TestTransactionControl noTran = new TestTxControlImpl(localEnabled,
				xaEnabled,
				false);

		EntityManager scoped = provider.getResource(noTran);

		releaseProvider();

		try {
			scoped.getCriteriaBuilder();
			fail("This resource should be invalid after the provider is released");
		} catch (TransactionException te) {
			// expected
		}

		noTran.finish(false);

		// Create a pooled provider

		provider = createProvider(5);

		scoped = provider.getResource(noTran);
		CriteriaQuery<TestEntity> query = scoped.getCriteriaBuilder()
				.createQuery(TestEntity.class);
		query.from(TestEntity.class);
		assertEquals("TEST",
				scoped.createQuery(query).getSingleResult().message);
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

		releaseProvider();

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
	 * 147.5.3.1 transaction commit isolation
	 * 
	 * @throws Exception
	 */
	public void testTransactionIsolation() throws Exception {

		try (Statement s = unmanaged.createStatement()) {
			s.execute("CREATE TABLE TEST (MESSAGE varchar(255) NOT NULL)");
			unmanaged.commit();
		}

		TestTransactionControl tran = new TestTxControlImpl(localEnabled,
				xaEnabled, true);

		EntityManager scoped = provider.getResource(tran);

		TestEntity te = new TestEntity();
		te.message = "TEST";

		scoped.persist(te);

		assertEquals(emptyList(), getMessages());

		tran.finish(false);

		assertEquals(singletonList("TEST"), getMessages());

		te = new TestEntity();
		te.message = "TEST2";
		scoped.persist(te);

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
	 * 147.5.3.2 no transaction commit isolation
	 * 
	 * @throws Exception
	 */
	public void testNoTransactionIsolation() throws Exception {

		if (!localEnabled) {
			return;
		}

		try (Statement s = unmanaged.createStatement()) {
			s.execute("CREATE TABLE TEST (MESSAGE varchar(255) NOT NULL)");
			unmanaged.commit();
		}

		TestTransactionControl noTran = new TestTxControlImpl(localEnabled,
				xaEnabled, false);

		EntityManager scoped = provider.getResource(noTran);

		scoped.getTransaction().begin();

		TestEntity te = new TestEntity();
		te.message = "TEST";

		scoped.persist(te);

		assertEquals(emptyList(), getMessages());

		scoped.getTransaction().commit();

		assertEquals(singletonList("TEST"), getMessages());

		scoped.getTransaction().begin();

		te = new TestEntity();
		te.message = "TEST2";

		scoped.persist(te);

		assertEquals(singletonList("TEST"), getMessages());

		scoped.getTransaction().setRollbackOnly();

		scoped.getTransaction().rollback();

		assertEquals(singletonList("TEST"), getMessages());

		noTran.finish(false);
	}
}

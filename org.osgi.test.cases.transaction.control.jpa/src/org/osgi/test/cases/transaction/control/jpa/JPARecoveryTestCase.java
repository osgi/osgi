/*
 * Copyright (c) OSGi Alliance (2017, 2020). All Rights Reserved.
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

import static java.util.Collections.singleton;
import static org.osgi.service.transaction.control.jpa.JPAEntityManagerProviderFactory.OSGI_RECOVERY_IDENTIFIER;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.xa.XAResource;

import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;
import org.osgi.service.transaction.control.recovery.RecoverableXAResource;
import org.osgi.test.cases.transaction.control.jpa.control.TestTransactionControl;
import org.osgi.test.cases.transaction.control.jpa.control.TestTxControlImpl;
import org.osgi.test.cases.transaction.control.jpa.entity.TestEntity;
import org.osgi.util.tracker.ServiceTracker;

public class JPARecoveryTestCase extends JPAResourceTestCase {
	
	private static final String		RECOVERY_ID	= "recover";
	private JPAEntityManagerProvider	provider;

	private Connection				unmanaged;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		provider = getProvider();

		unmanaged = dataSourceFactory.createDriver(null)
				.connect("jdbc:h2:mem:test", null);
	}

	@Override
	protected void tearDown() {
		try {
			jpaResourceProviderFactory.releaseProvider(provider);
		} catch (Exception e) {

		}

		try {
			unmanaged.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.tearDown();
	}

	private JPAEntityManagerProvider getProvider() throws SQLException {

		if (!xaEnabled) {
			return null;
		}

		Properties props = new Properties();
		props.setProperty("url", "jdbc:h2:mem:test");

		Map<String,Object> emfConfig = new HashMap<>();
		emfConfig.put("javax.persistence.dataSource",
				dataSourceFactory.createXADataSource(props));

		Map<String,Object> providerConfig = new HashMap<>();
		providerConfig.put(OSGI_RECOVERY_IDENTIFIER, RECOVERY_ID);

		return jpaResourceProviderFactory.getProviderFor(emfBuilder, emfConfig,
				providerConfig);
	}
	
	/**
	 * 147.6.1 - We should get an alias when we register a
	 * {@link RecoverableXAResource}
	 * 
	 * @throws Exception
	 */
	public void testRecoveryIdIsRegisteredWithXAResource() throws Exception {

		if (!recoveryEnabled) {
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

		Map<XAResource,String> enlistedXAResources = tran
				.getEnlistedXAResources();
		assertEquals(1, enlistedXAResources.size());
		assertEquals(singleton(RECOVERY_ID),
				new HashSet<>(enlistedXAResources.values()));

		tran.finish(false);
	}

	/**
	 * 147.6.2 - We should have a {@link RecoverableXAResource}
	 * 
	 * @throws Exception
	 */
	public void testRecoveryServiceIsRegistered() throws Exception {

		if (!recoveryEnabled) {
			return;
		}

		TestTransactionControl tran = new TestTxControlImpl(false, true, true);

		@SuppressWarnings("unused")
		EntityManager em = provider.getResource(tran);

		ServiceTracker<RecoverableXAResource,RecoverableXAResource> tracker = new ServiceTracker<>(
				getContext(), RecoverableXAResource.class, null);
		tracker.open();

		RecoverableXAResource recoveryService = tracker.waitForService(5000);
		assertNotNull(recoveryService);

		assertEquals(RECOVERY_ID, recoveryService.getId());
	}
}

/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
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

import static java.util.Collections.singleton;
import static org.osgi.service.transaction.control.jdbc.JDBCConnectionProviderFactory.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

import javax.transaction.xa.XAResource;

import org.osgi.service.transaction.control.jdbc.JDBCConnectionProvider;
import org.osgi.service.transaction.control.recovery.RecoverableXAResource;
import org.osgi.test.cases.transaction.control.jdbc.control.TestTransactionControl;
import org.osgi.test.cases.transaction.control.jdbc.control.TransactionTxControl;
import org.osgi.util.tracker.ServiceTracker;

public class JDBCRecoveryTestCase extends JDBCResourceTestCase {
	
	private static final String		RECOVERY_ID	= "recover";
	private JDBCConnectionProvider	provider;

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
			jdbcResourceProviderFactory.releaseProvider(provider);
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

	private JDBCConnectionProvider getProvider() throws SQLException {

		if (!xaEnabled) {
			return null;
		}

		Properties props = new Properties();
		props.setProperty("url", "jdbc:h2:mem:test");

		Map<String,Object> providerConfig = new HashMap<String,Object>();
		providerConfig.put(XA_ENLISTMENT_ENABLED, true);
		providerConfig.put(LOCAL_ENLISTMENT_ENABLED, false);
		providerConfig.put(OSGI_RECOVERY_IDENTIFIER, RECOVERY_ID);

		return jdbcResourceProviderFactory.getProviderFor(dataSourceFactory,
				props, providerConfig);
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

		TestTransactionControl tran = new TransactionTxControl(false, true);

		Connection scoped = provider.getResource(tran);

		ResultSet rs = scoped.createStatement()
				.executeQuery("SELECT MESSAGE FROM TEST");
		rs.next();
		assertEquals("TEST", rs.getString(1));

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

		ServiceTracker<RecoverableXAResource,RecoverableXAResource> tracker = new ServiceTracker<>(
				getContext(), RecoverableXAResource.class, null);
		tracker.open();

		RecoverableXAResource recoveryService = tracker.waitForService(5000);
		assertNotNull(recoveryService);

		assertEquals(RECOVERY_ID, recoveryService.getId());
	}
}

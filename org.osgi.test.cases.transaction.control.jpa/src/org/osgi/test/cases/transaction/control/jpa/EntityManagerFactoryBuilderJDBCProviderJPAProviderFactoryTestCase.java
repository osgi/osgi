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

import static org.osgi.service.transaction.control.jpa.JPAEntityManagerProviderFactory.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.osgi.service.transaction.control.jdbc.JDBCConnectionProvider;
import org.osgi.service.transaction.control.jdbc.JDBCConnectionProviderFactory;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;
import org.osgi.util.tracker.ServiceTracker;

public class EntityManagerFactoryBuilderJDBCProviderJPAProviderFactoryTestCase
		extends CommonJPAProviderFactoryTestCase {

	private JDBCConnectionProviderFactory	factory;
	private JDBCConnectionProvider			jdbcProvider;

	@Override
	protected JPAEntityManagerProvider createProvider(int poolSize)
			throws Exception {

		Properties props = new Properties();
		props.setProperty("url", H2_URL);

		ServiceTracker<JDBCConnectionProviderFactory,JDBCConnectionProviderFactory> tracker = new ServiceTracker<>(
				getContext(), JDBCConnectionProviderFactory.class, null);
		tracker.open();

		factory = tracker.waitForService(5000);
		assertNotNull(factory);

		Map<String,Object> providerConfig = new HashMap<>();
		providerConfig.put(LOCAL_ENLISTMENT_ENABLED, localEnabled);
		providerConfig.put(XA_ENLISTMENT_ENABLED, xaEnabled);

		if (poolSize == 0) {
			providerConfig.put(CONNECTION_POOLING_ENABLED, false);
		} else {
			providerConfig.put(MIN_CONNECTIONS, poolSize);
			providerConfig.put(MAX_CONNECTIONS, poolSize);
		}

		jdbcProvider = factory.getProviderFor(dataSourceFactory,
				props, providerConfig);

		providerConfig = new HashMap<>();
		providerConfig.put(TRANSACTIONAL_DB_CONNECTION, jdbcProvider);

		return jpaResourceProviderFactory.getProviderFor(emfBuilder, null,
				providerConfig);
	}

	@Override
	protected void releaseProvider() {
		super.releaseProvider();
		factory.releaseProvider(jdbcProvider);
	}

}

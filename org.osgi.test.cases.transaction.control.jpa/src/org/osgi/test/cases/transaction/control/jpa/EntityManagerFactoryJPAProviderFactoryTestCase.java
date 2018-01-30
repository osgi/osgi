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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.h2.jdbcx.JdbcConnectionPool;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

public class EntityManagerFactoryJPAProviderFactoryTestCase
		extends CommonJPAProviderFactoryTestCase {

	private JdbcConnectionPool pool;

	@Override
	protected JPAEntityManagerProvider createProvider(int poolSize)
			throws Exception {

		Properties props = new Properties();
		props.setProperty("url", H2_URL);

		Map<String,Object> providerConfig = new HashMap<>();

		if (localEnabled) {
			Map<String,Object> emfConfig = new HashMap<>();

			if (poolSize == 0) {
				emfConfig.put("javax.persistence.dataSource",
						dataSourceFactory.createDataSource(props));
			} else {
				pool = JdbcConnectionPool.create(dataSourceFactory
						.createConnectionPoolDataSource(props));
				pool.setMaxConnections(poolSize);

				List<Connection> conns = new ArrayList<>(poolSize);
				for (int i = 0; i < poolSize; i++) {
					conns.add(pool.getConnection());
				}

				for (Connection c : conns) {
					c.close();
				}

				emfConfig.put("javax.persistence.dataSource", pool);
			}

			EntityManagerFactory emf = emfBuilder
					.createEntityManagerFactory(emfConfig);

			providerConfig.put(LOCAL_ENLISTMENT_ENABLED, true);

			return jpaResourceProviderFactory.getProviderFor(emf,
					providerConfig);
		} else {
			Map<String,Object> emfConfig = new HashMap<>();
			emfConfig.put("javax.persistence.jtaDataSource",
					dataSourceFactory.createXADataSource(props));

			providerConfig.put(XA_ENLISTMENT_ENABLED, true);

			if (poolSize == 0) {
				providerConfig.put(CONNECTION_POOLING_ENABLED, false);
			} else {
				providerConfig.put(MIN_CONNECTIONS, poolSize);
				providerConfig.put(MAX_CONNECTIONS, poolSize);
			}

			return jpaResourceProviderFactory.getProviderFor(emfBuilder,
					emfConfig, providerConfig);
		}
	}

	@Override
	protected void releaseProvider() {
		super.releaseProvider();
		if (pool != null) {
			pool.dispose();
			pool = null;
		}
	}

}

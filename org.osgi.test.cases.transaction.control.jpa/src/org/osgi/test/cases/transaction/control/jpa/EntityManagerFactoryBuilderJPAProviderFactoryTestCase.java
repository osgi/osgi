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

import javax.sql.CommonDataSource;

import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

public class EntityManagerFactoryBuilderJPAProviderFactoryTestCase
		extends CommonJPAProviderFactoryTestCase {

	@Override
	protected JPAEntityManagerProvider createProvider(int poolSize)
			throws Exception {

		Properties props = new Properties();
		props.setProperty("url", H2_URL);

		CommonDataSource ds;

		if (xaEnabled) {
			ds = dataSourceFactory.createXADataSource(props);
		} else {
			ds = dataSourceFactory.createDataSource(props);
		}

		Map<String,Object> emfConfig = new HashMap<>();
		emfConfig.put("javax.persistence.dataSource", ds);

		Map<String,Object> providerConfig = new HashMap<>();
		if (poolSize == 0) {
			providerConfig.put(CONNECTION_POOLING_ENABLED, false);
		} else {
			providerConfig.put(MIN_CONNECTIONS, poolSize);
			providerConfig.put(MAX_CONNECTIONS, poolSize);
		}

		return jpaResourceProviderFactory.getProviderFor(emfBuilder, emfConfig,
				providerConfig);
	}


}

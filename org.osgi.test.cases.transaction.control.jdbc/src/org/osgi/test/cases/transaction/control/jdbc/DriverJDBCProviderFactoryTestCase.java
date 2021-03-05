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
package org.osgi.test.cases.transaction.control.jdbc;

import static org.osgi.service.transaction.control.jdbc.JDBCConnectionProviderFactory.*;

import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import org.osgi.service.transaction.control.jdbc.JDBCConnectionProvider;

public class DriverJDBCProviderFactoryTestCase
		extends CommonJDBCProviderFactoryTestCase {

	@Override
	protected JDBCConnectionProvider getProvider(Properties props,
			Map<String,Object> providerConfig) throws SQLException {

		if (!localEnabled) {
			throw new UnsupportedOperationException("Not supported when local");
		}

		// Drivers can never be XA
		xaEnabled = false;

		providerConfig.put(XA_ENLISTMENT_ENABLED, false);
		providerConfig.put(LOCAL_ENLISTMENT_ENABLED, true);

		return jdbcResourceProviderFactory.getProviderFor(
				dataSourceFactory.createDriver(null), props, providerConfig);
	}
	
	
}

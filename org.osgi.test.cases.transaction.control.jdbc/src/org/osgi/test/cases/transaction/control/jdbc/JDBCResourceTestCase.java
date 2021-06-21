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

import static org.osgi.service.transaction.control.jdbc.JDBCConnectionProviderFactory.LOCAL_ENLISTMENT_ENABLED;
import static org.osgi.service.transaction.control.jdbc.JDBCConnectionProviderFactory.XA_ENLISTMENT_ENABLED;
import static org.osgi.service.transaction.control.jdbc.JDBCConnectionProviderFactory.XA_RECOVERY_ENABLED;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.transaction.control.jdbc.JDBCConnectionProviderFactory;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.util.tracker.ServiceTracker;

public abstract class JDBCResourceTestCase extends OSGiTestCase {
	
	private ServiceTracker<JDBCConnectionProviderFactory,JDBCConnectionProviderFactory>	tracker;

	private ServiceTracker<DataSourceFactory,DataSourceFactory>							dsfTracker;

	protected JDBCConnectionProviderFactory												jdbcResourceProviderFactory;

	protected DataSourceFactory															dataSourceFactory;

	protected Bundle																	jdbcResourceProviderBundle;

	protected boolean												localEnabled;

	protected boolean												xaEnabled;

	protected boolean																	recoveryEnabled;

	protected void setUp() throws Exception {
		tracker = new ServiceTracker<JDBCConnectionProviderFactory,JDBCConnectionProviderFactory>(
				getContext(), JDBCConnectionProviderFactory.class, null);
		tracker.open();

		dsfTracker = new ServiceTracker<DataSourceFactory,DataSourceFactory>(
				getContext(), DataSourceFactory.class, null);
		dsfTracker.open();

		jdbcResourceProviderFactory = tracker.waitForService(5000);
		dataSourceFactory = dsfTracker.waitForService(5000);

		assertNotNull("No Tx Control service available within 5 seconds",
				jdbcResourceProviderFactory);

		ServiceReference<JDBCConnectionProviderFactory> ref = tracker
				.getServiceReference();

		jdbcResourceProviderBundle = ref.getBundle();

		localEnabled = toBoolean(ref.getProperty(LOCAL_ENLISTMENT_ENABLED));
		xaEnabled = toBoolean(ref.getProperty(XA_ENLISTMENT_ENABLED));

		assertTrue(
				"This transaction control service does not support local or xa transactions",
				localEnabled || xaEnabled);

		recoveryEnabled = toBoolean(ref.getProperty(XA_RECOVERY_ENABLED));
	}
	
	protected void tearDown() {
		tracker.close();
		dsfTracker.close();
	}
	
	protected boolean toBoolean(Object o) {
		return o == null ? false : Boolean.parseBoolean(o.toString());
	}
}

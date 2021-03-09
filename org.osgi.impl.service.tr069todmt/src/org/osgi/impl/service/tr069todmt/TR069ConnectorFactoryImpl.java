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

package org.osgi.impl.service.tr069todmt;

import java.util.ArrayList;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.log.LogService;
import org.osgi.service.tr069todmt.TR069Connector;
import org.osgi.service.tr069todmt.TR069ConnectorFactory;
import org.osgi.util.tracker.ServiceTracker;

/**
 * 
 *
 */
public class TR069ConnectorFactoryImpl implements BundleActivator, TR069ConnectorFactory {

	BundleContext						context;
	PersistenceManager					persistenceManager;

	private ServiceRegistration<TR069ConnectorFactory>	reg;
	private ServiceTracker<LogService,LogService>		logTracker;
	private ArrayList<TR069Connector>	connectors;

	@Override
	public void start(BundleContext bc) throws Exception {
		context = bc;
		logTracker = new ServiceTracker<>(bc, LogService.class, null);
		logTracker.open();
		persistenceManager = new PersistenceManager(this);
		connectors = new ArrayList<TR069Connector>();
		reg = bc.registerService(TR069ConnectorFactory.class, this, null);
	}

	@Override
	public void stop(BundleContext bc) throws Exception {
		if (persistenceManager != null) {
			persistenceManager.close();
			persistenceManager = null;
		}
		if (reg != null) {
			reg.unregister();
			reg = null;
		}
		for (int i = 0; i < connectors.size(); i++) {
			connectors.get(i).close();
		}
		connectors = null;
		if (logTracker != null) {
			logTracker.close();
			logTracker = null;
		}
	}

	@Override
	public TR069Connector create(DmtSession session) {
		if (session == null) {
			throw new NullPointerException("DMT Session is null");
		}
		TR069Connector connector = new TR069ConnectorImpl(session, this);
		connectors.add(connector);
		return connector;
	}

	void log(int level, String message, Throwable exception) {
		LogService log = logTracker == null ? null : (LogService) logTracker.getService();
		if (log != null) {
			log.log(level, message, exception);
		}
	}

}

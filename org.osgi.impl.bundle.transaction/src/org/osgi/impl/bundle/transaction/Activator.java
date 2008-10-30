/*
 * Copyright (C) 2008 ProSyst Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.impl.bundle.transaction;

import java.util.Properties;

import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;
import javax.transaction.xa.XAException;

import org.apache.geronimo.transaction.GeronimoUserTransaction;
import org.apache.geronimo.transaction.manager.GeronimoTransactionManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static final String SERVICE_DESCRIPTION = "Transactions in OSGi";
	private static final int TRANSACTION_TIMEOUT_SECONDS = 30;

	private GeronimoTransactionManager manager;
	private BundleContext context;

	/**
	 * Start
	 */
	public void start(BundleContext context) throws Exception {
		this.manager = new GeronimoTransactionManager(TRANSACTION_TIMEOUT_SECONDS);
		this.context = context;

		registerTransactionManager();
		registerUserTransaction();
	}

	/**
	 * Stop
	 */
	public void stop(BundleContext context) throws Exception {
	}
	
	private void registerTransactionManager() throws XAException {
		Properties p = new Properties();
		p.put("service.description", SERVICE_DESCRIPTION);
		String[] ifaces = { TransactionManager.class.getName(),
				TransactionSynchronizationRegistry.class.getName() };
		context.registerService(ifaces, manager, p);
	}

	private void registerUserTransaction()
			throws XAException {
		Properties p = new Properties();
		p.put("service.description", SERVICE_DESCRIPTION);
		GeronimoUserTransaction userTx = new GeronimoUserTransaction(manager);
		String iface = UserTransaction.class.getName();
		context.registerService(iface, userTx, p);
	}


}

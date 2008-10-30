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
package test;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import junit.framework.TestCase;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class TransactionTest extends TestCase {

	private BundleContext context;

	public void setBundleContext(BundleContext context) {
		this.context = context;
	}
	
	private TransactionManager getTransactionManager() {
		ServiceReference ref = context.getServiceReference(TransactionManager.class.getName());
		assertNotNull(ref);
		TransactionManager manager = (TransactionManager) context.getService(ref);
		assertNotNull(manager);
		return manager;
	}
	
	private UserTransaction getUserTransaction() {
		ServiceReference refUserTransaction = context.getServiceReference(UserTransaction.class.getName());
		assertNotNull(refUserTransaction);
		UserTransaction userTransaction = (UserTransaction) context.getService(refUserTransaction);
		assertNotNull(userTransaction);
		return userTransaction;
	}

	public void testTransactionManager() throws Exception {
		TransactionManager manager = getTransactionManager();
		manager.begin();
		
		TestResource res1 = new TestResource();
		TestResource res2 = new TestResource();
		manager.getTransaction().enlistResource(res1);
		manager.getTransaction().enlistResource(res2);
		assertFalse(res1.isCommitted());
		assertFalse(res2.isCommitted());
		
		manager.commit();
		assertTrue(res1.isCommitted());
		assertTrue(res2.isCommitted());
	}
	
	public void testUserTransaction() throws Exception {
		TransactionManager transactionManager = getTransactionManager();
		UserTransaction userTransaction = getUserTransaction();
		userTransaction.begin();
		
		TestResource res1 = new TestResource();
		TestResource res2 = new TestResource();
		transactionManager.getTransaction().enlistResource(res1);
		transactionManager.getTransaction().enlistResource(res2);
		assertFalse(res1.isCommitted());
		assertFalse(res2.isCommitted());
		
		userTransaction.commit();
		assertTrue(res2.isCommitted());
		assertTrue(res1.isCommitted());
	}
}

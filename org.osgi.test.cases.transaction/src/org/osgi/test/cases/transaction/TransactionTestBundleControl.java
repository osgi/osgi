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
package org.osgi.test.cases.transaction;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.osgi.test.cases.transaction.util.TransactionManagerFactory;
import org.osgi.test.cases.transaction.util.TransactionSynchronizationRegistryFactory;
import org.osgi.test.cases.transaction.util.TransactionUtil;
import org.osgi.test.cases.transaction.util.UserTransactionFactory;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * @version $Rev$ $Date$
 *
 * abstract class TransactionTestBundleControl, provides a waitSomeTime method to wait for a
 * default time 30 seconds or user specified time at the beginning of the test to allow the transaction 
 * services to have enough time to be registered.
 * 
 *
 */
public abstract class TransactionTestBundleControl extends DefaultTestBundleControl {

    private final int DEFAULTTIME = 30;
    static TransactionManager tm;
    static UserTransaction ut;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TransactionManagerFactory.setBundleContext(getContext());
		UserTransactionFactory.setBundleContext(getContext());
		TransactionSynchronizationRegistryFactory
				.setBundleContext(getContext());
    }

    /*
     * get the wait time in seconds, check the user preference first
     */
    protected int getWaitTime() {
        // check if wait time is specified in system property
            String p;
            int waitTime = DEFAULTTIME;
            // First check if the user has a preference.
		p = getProperty("org.osgi.test.cases.transaction.waittime");
            if (p != null) {
                if (Integer.parseInt(p) > 0) {
                    waitTime = Integer.parseInt(p);
                }
            } 
            return waitTime;
    }
    
    public void setUpTransactionManager() throws Exception {
        if (tm == null) {
            tm = TransactionManagerFactory.getTransactionManager(getWaitTime());
        }
        assertNotNull(tm);
        TransactionUtil.startWithCleanTM(tm); 
    }
    

    
    public void setUpUserTransaction() throws Exception {
        if (ut == null) {
            ut = UserTransactionFactory.getUserTransaction(getWaitTime());
        }
        assertNotNull(ut);
        TransactionUtil.startWithCleanUT(ut); 
    }

}

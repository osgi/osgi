/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
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

package org.osgi.test.cases.transaction;

import javax.transaction.RollbackException;
import javax.transaction.TransactionManager;

import org.osgi.framework.BundleContext;
import org.osgi.test.cases.transaction.util.TransactionManagerFactory;
import org.osgi.test.cases.transaction.util.TransactionUtil;

/**
 * @version $Rev$ $Date$
 *
 * Test transaction manager timeout
 *
 *
 */

public class LengthyTransactionManagerTest extends TransactionTestBundleControl {
    private static final int TOTAL_TRANSACTION_LIFETIME_TIMEOUT = 10;
    private static final int SUITABLE_DELAY = 5;

    BundleContext context;
    TransactionManager tm;

    public void setBundleContext(BundleContext context) {
        super.setBundleContext(context);
        TransactionManagerFactory.setBundleContext(context);
    }
    
    public void setUp() throws Exception {
    	tm = TransactionManagerFactory.getTransactionManager();
    	if (tm == null) {
            super.waitSomeTime();
            // let's try get tm again after the waiting
            tm = TransactionManagerFactory.getTransactionManager();
    	}
    	TransactionUtil.startWithCleanTM(tm); 
    }
    
    public void testTM035() throws Exception {
        try
        {
            tm.setTransactionTimeout(TOTAL_TRANSACTION_LIFETIME_TIMEOUT);
            tm.begin();
            Thread.sleep(1000 * (TOTAL_TRANSACTION_LIFETIME_TIMEOUT - SUITABLE_DELAY));
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }

        try
        {
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }
    }

    public void testTM036() throws Exception {
        try
        {
            tm.setTransactionTimeout(TOTAL_TRANSACTION_LIFETIME_TIMEOUT);
            tm.begin();
            Thread.sleep(1000 * (TOTAL_TRANSACTION_LIFETIME_TIMEOUT + SUITABLE_DELAY));
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }

        try
        {
            tm.commit();
            fail();
        }
        catch (RollbackException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }
    }

}
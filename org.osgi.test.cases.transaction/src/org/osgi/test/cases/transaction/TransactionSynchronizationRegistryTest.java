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
import javax.transaction.Status;
import javax.transaction.TransactionSynchronizationRegistry;

import org.osgi.test.cases.transaction.util.SynchronizationImpl;
import org.osgi.test.cases.transaction.util.TransactionSynchronizationRegistryFactory;

/**
 * @version $Rev$ $Date$
 *
 * transaction SynchronizationRegistry test, test transactionKey, transaction status, etc
 * 
 *
 */
public class TransactionSynchronizationRegistryTest extends TransactionTestBundleControl {

    public void setUp() throws Exception {
        super.setUpTransactionManager();
    }
    
    public void testTSR001() throws Exception {
        final TransactionSynchronizationRegistry tsr = TransactionSynchronizationRegistryFactory.getTransactionSynchronizationRegistry();

        assertNull(tsr.getTransactionKey());
    }

    public void testTSR002() throws Exception {
        final TransactionSynchronizationRegistry tsr = TransactionSynchronizationRegistryFactory.getTransactionSynchronizationRegistry();

        assertEquals(Status.STATUS_NO_TRANSACTION, tsr.getTransactionStatus());
    }

    public void testTSR003() throws Exception {
        final TransactionSynchronizationRegistry tsr = TransactionSynchronizationRegistryFactory.getTransactionSynchronizationRegistry();

        try
        {
            tm.begin();
            
            final String key = "resourceKey";
            final Object value = new Object();
            
            tsr.putResource(key, value);
            
            final Object retrievedResource = tsr.getResource(key);
            
            assertTrue(value.equals(retrievedResource));

            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }
    }

    public void testTSR004() throws Exception {
        final TransactionSynchronizationRegistry tsr = TransactionSynchronizationRegistryFactory.getTransactionSynchronizationRegistry();

        try
        {
            tm.begin();

            assertNotNull(tsr.getTransactionKey());

            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }
    }

    public void testTSR005() throws Exception {
        final TransactionSynchronizationRegistry tsr = TransactionSynchronizationRegistryFactory.getTransactionSynchronizationRegistry();
    
        try
        {
            tm.begin();
    
            assertEquals(Status.STATUS_ACTIVE, tsr.getTransactionStatus());

            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }
    }

    public void testTSR006() throws Exception {
        final TransactionSynchronizationRegistry tsr = TransactionSynchronizationRegistryFactory.getTransactionSynchronizationRegistry();
    
        try
        {
            tm.begin();
    
            final SynchronizationImpl s = new SynchronizationImpl();

            tsr.registerInterposedSynchronization(s);

            tm.commit();

            assertFalse(!s.beforeCompletionCalled() || s.afterCompletionStatus() != Status.STATUS_COMMITTED);
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }
    }

    public void testTSR007() throws Exception {
        final TransactionSynchronizationRegistry tsr = TransactionSynchronizationRegistryFactory.getTransactionSynchronizationRegistry();
    
        try
        {
            tm.begin();

            assertFalse(tsr.getRollbackOnly());

            tsr.setRollbackOnly();

            assertFalse(!tsr.getRollbackOnly() || tsr.getTransactionStatus() != Status.STATUS_MARKED_ROLLBACK);
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
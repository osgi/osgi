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
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAException;

import org.osgi.framework.BundleContext;
import org.osgi.test.cases.transaction.util.SynchronizationImpl;
import org.osgi.test.cases.transaction.util.TransactionManagerFactory;
import org.osgi.test.cases.transaction.util.TransactionUtil;
import org.osgi.test.cases.transaction.util.XAResourceImpl;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * @version $Rev$ $Date$
 *
 * transaction test, test transaction status, commit/rollback and register synchronization
 * 
 *
 */
public class TransactionTest extends DefaultTestBundleControl {
    BundleContext context;
    TransactionManager tm;

    public void setBundleContext(BundleContext context) {
        this.context = context;
        TransactionManagerFactory.setBundleContext(context);
    }

    public void setUp() throws Exception {
        tm = TransactionManagerFactory.getTransactionManager();
        TransactionUtil.startWithCleanTM(tm); 
    }
    
    public void testTI001() throws Exception {
        try
        {
            tm.begin();
            tm.commit();

            assertEquals(tm.getStatus(), Status.STATUS_NO_TRANSACTION);
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }
    }

    public void testTI002() throws Exception {       
        try
        {
            tm.begin();
            tm.rollback();

            assertEquals(tm.getStatus(), Status.STATUS_NO_TRANSACTION);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTI003() throws Exception {
        try
        {
            tm.begin();

            assertEquals(tm.getStatus(), Status.STATUS_ACTIVE);

            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTI004() throws Exception {
        try
        {
            tm.begin();
            final SynchronizationImpl s = new SynchronizationImpl();
            tm.getTransaction().registerSynchronization(s);
            tm.commit();

            assertFalse(!s.beforeCompletionCalled() || s.afterCompletionStatus() != Status.STATUS_COMMITTED);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTI005() throws Exception {
        Transaction tx = null;
        try
        {
            tm.begin();
            tx = tm.getTransaction();
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }

        try
        {
            tx.registerSynchronization(new SynchronizationImpl());
            fail();
        }
        catch(IllegalStateException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTI007() throws Exception {
        try
        {
            tm.begin();
            final SynchronizationImpl s = new SynchronizationImpl();
            tm.getTransaction().registerSynchronization(s);
            tm.rollback();

            assertFalse(s.beforeCompletionCalled() || s.afterCompletionStatus() != Status.STATUS_ROLLEDBACK);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTI008() throws Exception {
        final SynchronizationImpl s = new SynchronizationImpl();
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();
            tx.registerSynchronization(s);
            tx.enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_RBROLLBACK));
            tm.commit();
            fail();
        }
        catch (RollbackException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }

        assertFalse(!s.beforeCompletionCalled() || s.afterCompletionStatus() != Status.STATUS_ROLLEDBACK);
    }

    public void failingtestTI009() throws Exception {
        final SynchronizationImpl s = new SynchronizationImpl();
        try
        {
            tm.begin();
            tm.setRollbackOnly();
            tm.getTransaction().registerSynchronization(s);
            fail();
        }
        catch (RollbackException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }

        try
        {
            tm.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTI010() throws Exception {
        try
        {
            tm.begin();
            tm.setRollbackOnly();

            assertEquals(tm.getStatus(), Status.STATUS_MARKED_ROLLBACK);

            tm.commit();
            fail();
        }
        catch (RollbackException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTI011() throws Exception {
        try
        {
            tm.begin();
            tm.setRollbackOnly();

            assertEquals(tm.getStatus(), Status.STATUS_MARKED_ROLLBACK);

            tm.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTI012() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();
            tm.commit();
            tx.setRollbackOnly();
            fail();
        }
        catch (IllegalStateException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTI013() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl();
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
        
        assertEquals(xares.getStatusDuringRollback(), Status.STATUS_ROLLING_BACK);
    }

    public void testTI014() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl();
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(xares);
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
        
        assertEquals(xares.getStatusDuringPrepare(), Status.STATUS_PREPARING);
    }

    public void testTI015() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl();
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();
            tx.enlistResource(xares);
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
        
        assertEquals(xares.getStatusDuringCommit(), Status.STATUS_COMMITTING);
    }

    public void testTI017() throws Exception {
        try
        {
            tm.begin();
            tm.commit();
            tm.commit();
            fail();
        }
        catch (IllegalStateException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTI018() throws Exception {
        try
        {
            tm.begin();
            tm.commit();
            tm.rollback();
            fail();
        }
        catch (IllegalStateException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTI019() throws Exception {
        try
        {
            tm.begin();
            tm.rollback();
            tm.rollback();
            fail();
        }
        catch (IllegalStateException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTI020() throws Exception {
        try
        {
            tm.begin();
            tm.rollback();
            tm.commit();
            fail();
        }
        catch (IllegalStateException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTI021() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.suspend();

            assertEquals(tx.getStatus(), Status.STATUS_ACTIVE);

            tm.resume(tx);
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTI022() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().registerSynchronization(null);
            fail();
        }
        catch (NullPointerException e)
        {
            // As expected
        }
        catch (IllegalArgumentException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }

        try
        {
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTI023() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().registerSynchronization(new SynchronizationImpl().setBeforeCompletionException(new RuntimeException()));
            tm.commit();
            fail();
        }
        catch (RollbackException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTI024() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().registerSynchronization(new SynchronizationImpl().setAfterCompletionException(new RuntimeException()));
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }
}
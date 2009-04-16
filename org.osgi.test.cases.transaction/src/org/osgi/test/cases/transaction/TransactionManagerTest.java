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

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAException;

import org.osgi.framework.BundleContext;
import org.osgi.test.cases.transaction.util.TransactionManagerFactory;
import org.osgi.test.cases.transaction.util.TransactionUtil;
import org.osgi.test.cases.transaction.util.XAResourceImpl;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * @version $Rev$ $Date$
 *
 * transaction manager test, test basic commit, rollback, suspend/resume, with exceptions arisen from xa resources
 *
 *
 */

public class TransactionManagerTest extends DefaultTestBundleControl {

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
    
    public void testTransactionManagerFactory() {
        assertNotNull(TransactionManagerFactory.getTransactionManager());
    }

    public void testTM001() throws Exception {
        try
        {
            tm.begin();
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTM003() throws Exception { 
        try
        {
            tm.begin();         
            tm.getTransaction().enlistResource(new XAResourceImpl());           
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTM004() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());           
            tm.getTransaction().enlistResource(new XAResourceImpl());           
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTM005() throws Exception {
        try
        {
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

    public void testTM006() throws Exception {
        try
        {
            tm.begin(); 
            tm.setRollbackOnly();
            tm.commit();
            fail();
        }               
        catch (RollbackException re)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTM007() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();

            tx.enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_RBROLLBACK));

            try
            {
                tm.commit();
                fail();
            }
            catch (RollbackException e)
            {
                // As expected
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTM008() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));           
            tm.getTransaction().enlistResource(new XAResourceImpl());           
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }

        try
        {
            tm.commit();
            fail();
        }
        catch(RollbackException e)
        {
            // As expected
        }
        catch(Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTM009() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURRB));           
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }

        try
        {
            tm.commit();
            fail();
        }
        catch(HeuristicRollbackException e)
        {
            // As expected
        }
        catch (RollbackException e)
        {
            // As expected
        }
        catch(Exception e)
        {
            e.printStackTrace();
            fail();
        }

        try
        {
            assertEquals(tm.getStatus(), Status.STATUS_NO_TRANSACTION);
        }
        catch(SystemException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTM010() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURMIX));           
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }

        try
        {
            tm.commit();
            fail();
        }
        catch(HeuristicMixedException e)
        {
            // As expected
        }
        catch (RollbackException e)
        {
            // As expected
        }
        catch(Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTM011() throws Exception {
        try
        {
            tm.begin();
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }

        try
        {
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

    public void testTM012() throws Exception {
        try
        {
            tm.begin();
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }

        try
        {
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

    public void testTM013() throws Exception {
        try
        {
            assertEquals(tm.getStatus(), Status.STATUS_NO_TRANSACTION);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTM014() throws Exception {
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

    public void testTM015() throws Exception {
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

    public void testTM016() throws Exception {
        try
        {
            tm.begin();
            tm.commit();

            assertEquals(tm.getStatus(), Status.STATUS_NO_TRANSACTION);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTM017() throws Exception {
        try
        {
            tm.begin();

            assertNotNull(tm.getTransaction());

            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTM018() throws Exception {
        try
        {
            assertNull(tm.getTransaction());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTM019() throws Exception {
        try
        {
            tm.resume(null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTM020() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();
            tm.suspend();
            tm.resume(tx);
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTM021() throws Exception {
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
            tm.resume(tx);
            fail();
        }
        catch(InvalidTransactionException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTM022() throws Exception {
        try
        {
            tm.begin();
            tm.resume(null);
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

    public void testTM023() throws Exception {
        Transaction tx = null;
        try
        {
            tm.begin();
            tx = tm.suspend();

            tm.begin();
            tm.resume(tx);
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

        try
        {
            tm.rollback();
            tm.resume(tx);
            tm.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTM024() throws Exception {
        Transaction tx = null;
        try
        {
            tm.begin();
            tx = tm.getTransaction();
            tm.rollback();

            tm.begin();
            tm.resume(tx);
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

    public void testTM025() throws Exception {
        Transaction tx = null;
        try
        {
            tm.begin();
            tx = tm.suspend();
            assertNull(tm.getTransaction());
            tm.resume(tx);
            tm.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTM026() throws Exception {  
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

    public void testTM027() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());           
            tm.rollback();

            assertEquals(tm.getStatus(), Status.STATUS_NO_TRANSACTION);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTM028() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());           
            tm.getTransaction().enlistResource(new XAResourceImpl());           
            tm.rollback();

            assertEquals(tm.getStatus(), Status.STATUS_NO_TRANSACTION);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTM029() throws Exception {
        try
        {
            tm.begin();
            tm.setRollbackOnly();           
            tm.rollback();

            assertEquals(tm.getStatus(), Status.STATUS_NO_TRANSACTION);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTM030() throws Exception {
        try
        {
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

    public void testTM031() throws Exception {
        try
        {
            tm.begin();
            tm.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }

        try
        {
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

    public void testTM032() throws Exception {
        try
        {
            tm.begin();
            tm.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }

        try
        {
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

    public void testTM033() throws Exception {
        try
        {
            tm.setRollbackOnly();
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

    public void testTM034() throws Exception {
        try
        {
            tm.begin();
            tm.setRollbackOnly();
            tm.setRollbackOnly();
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
            e.printStackTrace();
            fail();
        }
    }

    public void testTM040() throws Exception {
        try
        {
            tm.setTransactionTimeout(-1);
            fail();
        }
        catch (SystemException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTM041() throws Exception {
        try
        {
            assertNull(tm.suspend());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testTM042() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.suspend();
            assertNotNull(tx);
            
            assertEquals(tm.getStatus(), Status.STATUS_NO_TRANSACTION);

            tm.resume(tx);
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }
}

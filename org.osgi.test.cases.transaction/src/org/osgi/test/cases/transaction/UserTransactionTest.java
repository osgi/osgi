/*
 * $Id$
 *
 * Copyright (c) The OSGi Alliance (2009). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.transaction;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import javax.transaction.xa.XAException;

import org.osgi.framework.BundleContext;
import org.osgi.test.cases.transaction.util.TransactionManagerFactory;
import org.osgi.test.cases.transaction.util.TransactionUtil;
import org.osgi.test.cases.transaction.util.UserTransactionFactory;
import org.osgi.test.cases.transaction.util.XAResourceImpl;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * user transaction test, test transaction status, xa resource state, various exceptions arisen from commit
 * 
 *
 */
public class UserTransactionTest extends DefaultTestBundleControl {
    BundleContext context;
    UserTransaction ut;
    TransactionManager tm;

    public void setBundleContext(BundleContext context) {
        this.context = context;
        UserTransactionFactory.setBundleContext(context);
        TransactionManagerFactory.setBundleContext(context);
    }
    
    public void setUp() throws Exception {
        tm = TransactionManagerFactory.getTransactionManager();
        ut = UserTransactionFactory.getUserTransaction();
        TransactionUtil.startWithClean(tm, ut); 
    }
    
    public void testUT001() throws Exception {
        assertNotNull(ut);
    }

    public void testUT003() throws Exception {
        try
        {
            ut.begin();
            ut.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testUT004() throws Exception {
        try
        {
            ut.begin();
            ut.begin();
            fail();
        }
        catch (NotSupportedException e)
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
            ut.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testUT005() throws Exception {
        try
        {
            ut.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            ut.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }

        assertTrue(XAResourceImpl.allInState(XAResourceImpl.COMMITTED));
    }

    public void testUT006() throws Exception {
        try
        {
            ut.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(new XAResourceImpl());
            ut.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }

        assertTrue(XAResourceImpl.allInState(XAResourceImpl.COMMITTED));
    }

    public void testUT007() throws Exception {
        try
        {
            ut.commit();
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

    public void testUT008() throws Exception {
        try
        {
            ut.begin();
            ut.setRollbackOnly();
            ut.commit();
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

    public void testUT009() throws Exception {
        try
        {
            ut.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_RBROLLBACK));
            ut.commit();
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

    public void testUT010() throws Exception {
        try
        {
            ut.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
            tm.getTransaction().enlistResource(new XAResourceImpl());
            ut.commit();
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
            assertEquals(ut.getStatus(), Status.STATUS_NO_TRANSACTION);
        }
        catch(SystemException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testUT011() throws Exception {
        try
        {
            ut.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURRB));
            ut.commit();
            fail();
        }
        catch (HeuristicRollbackException e)
        {
            // As expected
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
            assertEquals(ut.getStatus(), Status.STATUS_NO_TRANSACTION);
        }
        catch(SystemException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testUT012() throws Exception {
        try
        {
            ut.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURMIX));
            ut.commit();
            fail();
        }
        catch (HeuristicMixedException e)
        {
            // As expected
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
            assertEquals(ut.getStatus(), Status.STATUS_NO_TRANSACTION);
        }
        catch(SystemException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testUT013() throws Exception {
        try
        {
            ut.begin();
            ut.commit();
            ut.commit();
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
            assertEquals(ut.getStatus(), Status.STATUS_NO_TRANSACTION);
        }
        catch(SystemException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testUT014() throws Exception {
        try
        {
            ut.begin();
            ut.commit();
            ut.rollback();
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
            assertEquals(ut.getStatus(), Status.STATUS_NO_TRANSACTION);
        }
        catch(SystemException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testUT015() throws Exception {
        try
        {
            assertEquals(ut.getStatus(), Status.STATUS_NO_TRANSACTION);
        }
        catch(SystemException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testUT016() throws Exception {
        try
        {
            ut.begin();

            assertEquals(ut.getStatus(), Status.STATUS_ACTIVE);

            ut.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testUT017() throws Exception {
        try
        {
            ut.begin();
            ut.setRollbackOnly();

            assertEquals(ut.getStatus(), Status.STATUS_MARKED_ROLLBACK);

            ut.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testUT018() throws Exception {
        try
        {
            ut.begin();
            ut.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }

        try
        {
            assertEquals(ut.getStatus(), Status.STATUS_NO_TRANSACTION);
        }
        catch(SystemException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testUT019() throws Exception {
        try
        {
            ut.begin();
            ut.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }

        try
        {
            assertEquals(ut.getStatus(), Status.STATUS_NO_TRANSACTION);
        }
        catch(SystemException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testUT020() throws Exception {
        try
        {
            ut.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            ut.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }

        try
        {
            assertEquals(ut.getStatus(), Status.STATUS_NO_TRANSACTION);
        }
        catch(SystemException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testUT021() throws Exception {
        try
        {
            ut.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(new XAResourceImpl());
            ut.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }

        try
        {
            assertEquals(ut.getStatus(), Status.STATUS_NO_TRANSACTION);
        }
        catch(SystemException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testUT022() throws Exception {
        try
        {
            ut.begin();
            ut.setRollbackOnly();
            ut.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }

        try
        {
            assertEquals(ut.getStatus(), Status.STATUS_NO_TRANSACTION);
        }
        catch(SystemException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testUT023() throws Exception {
        try
        {
            ut.rollback();
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

    public void testUT024() throws Exception {
        try
        {
            ut.begin();
            ut.rollback();
            ut.rollback();
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

    public void testUT025() throws Exception {
        try
        {
            ut.begin();
            ut.rollback();
            ut.commit();
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

    public void testUT026() throws Exception {
        try
        {
            ut.setRollbackOnly();
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

    public void testUT027() throws Exception {
        try
        {
            ut.begin();
            ut.setRollbackOnly();
            ut.setRollbackOnly();
            ut.commit();
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

    public void testUT033() throws Exception {
        try
        {
            // Defect 447459 allows timeout of -1
            ut.setTransactionTimeout(-1);
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
}

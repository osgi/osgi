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
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAException;

import org.osgi.framework.BundleContext;
import org.osgi.test.cases.transaction.util.TransactionManagerFactory;
import org.osgi.test.cases.transaction.util.TransactionUtil;
import org.osgi.test.cases.transaction.util.XAResourceImpl;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * XAException test, test various XAException arisen from XA Resources during tm rollback or commit.
 * 
 *
 */

public class XAExceptionTest extends DefaultTestBundleControl {
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

    public void testEX001() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_RBROLLBACK));
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

    public void testEX002() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XAER_RMERR));
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

    public void testEX008() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XAER_NOTA));
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

    public void testEX013() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XAER_INVAL));
            tm.commit();
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

    public void testEX014() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XAER_INVAL));
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.commit();
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

    public void testEX015() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XAER_INVAL));
            tm.commit();
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

    public void testEX016() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XAER_PROTO));
            tm.commit();
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

    public void testEX017() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XAER_PROTO));
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.commit();
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

    public void testEX018() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XAER_PROTO));
            tm.commit();
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

    public void testEX019() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setEndAction(XAException.XAER_RMERR));
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

    public void testEX020() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setEndAction(XAException.XAER_RMERR));
            tm.getTransaction().enlistResource(new XAResourceImpl());
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

    public void testEX021() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(new XAResourceImpl().setEndAction(XAException.XAER_RMERR));
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

    public void testEX022() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setEndAction(XAException.XAER_RMFAIL));
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

    public void testEX023() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setEndAction(XAException.XAER_RMFAIL));
            tm.getTransaction().enlistResource(new XAResourceImpl());
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

    public void testEX024() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(new XAResourceImpl().setEndAction(XAException.XAER_RMFAIL));
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

    public void testEX025() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setEndAction(XAException.XAER_NOTA));
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

    public void testEX026() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setEndAction(XAException.XAER_NOTA));
            tm.getTransaction().enlistResource(new XAResourceImpl());
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

    public void testEX027() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(new XAResourceImpl().setEndAction(XAException.XAER_NOTA));
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

    public void testEX028() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setEndAction(XAException.XA_RBROLLBACK));
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

    public void testEX029() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setEndAction(XAException.XA_RBROLLBACK));
            tm.getTransaction().enlistResource(new XAResourceImpl());
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

    public void testEX030() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(new XAResourceImpl().setEndAction(XAException.XA_RBROLLBACK));
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

    public void testEX031() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setEndAction(XAException.XAER_PROTO));
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

    public void testEX032() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setEndAction(XAException.XAER_PROTO));
            tm.getTransaction().enlistResource(new XAResourceImpl());
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

    public void testEX033() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(new XAResourceImpl().setEndAction(XAException.XAER_PROTO));
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

    public void testEX034() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setEndAction(XAException.XAER_INVAL));
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

    public void testEX035() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setEndAction(XAException.XAER_INVAL));
            tm.getTransaction().enlistResource(new XAResourceImpl());
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

    public void testEX036() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(new XAResourceImpl().setEndAction(XAException.XAER_INVAL));
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

    public void testEX040() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setCommitAction(XAException.XA_HEURMIX).setForgetAction(XAException.XAER_NOTA);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.commit();
            fail();
        }
        catch (HeuristicMixedException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }

        assertFalse(xares.inState(XAResourceImpl.FORGOTTEN));
    }

    public void testEX041() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setCommitAction(XAException.XA_HEURMIX).setForgetAction(XAException.XAER_NOTA);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.commit();
            fail();
        }
        catch (HeuristicMixedException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    
        assertFalse(xares.inState(XAResourceImpl.FORGOTTEN));
    }

    public void testEX042() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setCommitAction(XAException.XA_HEURMIX).setForgetAction(XAException.XAER_NOTA);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(xares);
            tm.commit();
            fail();
        }
        catch (HeuristicMixedException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    
        assertFalse(xares.inState(XAResourceImpl.FORGOTTEN));
    }

    public void testEX043() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setCommitAction(XAException.XA_HEURMIX).setForgetAction(XAException.XAER_INVAL);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.commit();
            fail();
        }
        catch (SystemException e)
        {
            // As expected
        }
        catch (HeuristicMixedException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }

        assertFalse(xares.inState(XAResourceImpl.FORGOTTEN));
    }

    public void testEX044() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setCommitAction(XAException.XA_HEURMIX).setForgetAction(XAException.XAER_INVAL);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.commit();
            fail();
        }
        catch (SystemException e)
        {
            // As expected
        }
        catch (HeuristicMixedException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    
        assertFalse(xares.inState(XAResourceImpl.FORGOTTEN));
    }

    public void testEX045() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setCommitAction(XAException.XA_HEURMIX).setForgetAction(XAException.XAER_INVAL);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(xares);
            tm.commit();
            fail();
        }
        catch (SystemException e)
        {
            // As expected
        }
        catch (HeuristicMixedException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    
        assertFalse(xares.inState(XAResourceImpl.FORGOTTEN));
    }

    public void testEX046() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setCommitAction(XAException.XA_HEURMIX).setForgetAction(XAException.XAER_PROTO);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.commit();
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

        assertFalse(xares.inState(XAResourceImpl.FORGOTTEN));
    }

    public void testEX047() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setCommitAction(XAException.XA_HEURMIX).setForgetAction(XAException.XAER_PROTO);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.commit();
            fail();
        }
        catch (SystemException e)
        {
            // As expected
        }
        catch (HeuristicMixedException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    
        assertFalse(xares.inState(XAResourceImpl.FORGOTTEN));
    }

    public void testEX048() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setCommitAction(XAException.XA_HEURMIX).setForgetAction(XAException.XAER_PROTO);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(xares);
            tm.commit();
            fail();
        }
        catch (SystemException e)
        {
            // As expected
        }
        catch (HeuristicMixedException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    
        assertFalse(xares.inState(XAResourceImpl.FORGOTTEN));
    }

    public void testEX049() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
            tm.getTransaction().enlistResource(new XAResourceImpl());
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

    public void testEX050() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
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

    public void testEX051() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XAER_RMERR));
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.commit();
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

        assertTrue(XAResourceImpl.allInState(XAResourceImpl.ROLLEDBACK));
    }

    public void testEX052() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XAER_RMERR));
            tm.commit();
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
    
        assertTrue(XAResourceImpl.allInState(XAResourceImpl.ROLLEDBACK));
    }

    public void testEX053() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XAER_RMFAIL));
            tm.getTransaction().enlistResource(new XAResourceImpl());
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
    
        assertTrue(XAResourceImpl.allInState(XAResourceImpl.ROLLEDBACK));
    }

    public void testEX054() throws Exception {
        final XAResourceImpl xares1 = new XAResourceImpl();
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares1);
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XAER_RMFAIL));
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
    
        assertTrue(xares1.inState(XAResourceImpl.ROLLEDBACK));
    }

    public void testEX055() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl();
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XAER_NOTA));
            tm.getTransaction().enlistResource(xares);
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX056() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl();
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XAER_NOTA));
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX057() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XAER_PROTO));
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.commit();
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

    public void testEX058() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XAER_PROTO));
            tm.commit();
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

    public void testEX059() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XAER_INVAL));
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.commit();
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

    public void testEX060() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XAER_INVAL));
            tm.commit();
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

    public void testEX061() throws Exception { 
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setRollbackAction(XAException.XA_RBROLLBACK));
            tm.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testEX062() throws Exception {  
    
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XA_RBROLLBACK);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.setRollbackOnly();
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

        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX063() throws Exception {  
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XA_RBROLLBACK);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX064() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XA_RBROLLBACK);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(xares);
            tm.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX065() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XA_RBROLLBACK);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
            tm.commit();
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX066() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XA_RBROLLBACK);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
            tm.getTransaction().enlistResource(xares);
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX067() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XA_RBROLLBACK);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.setRollbackOnly();
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX068() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XA_RBROLLBACK);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(xares);
            tm.setRollbackOnly();
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX069() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_RMERR);
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX070() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_RMERR);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.setRollbackOnly();
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX071() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_RMERR);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX072() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_RMERR);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(xares);
            tm.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX073() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_RMERR);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX074() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_RMERR);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
            tm.getTransaction().enlistResource(xares);
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX075() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_RMERR);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.setRollbackOnly();
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX076() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_RMERR);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(xares);
            tm.setRollbackOnly();
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX077() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_NOTA);
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX078() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_NOTA);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.setRollbackOnly();
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX079() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_NOTA);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX080() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_NOTA);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(xares);
            tm.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX081() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_NOTA);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX082() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_NOTA);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
            tm.getTransaction().enlistResource(xares);
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX083() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_NOTA);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.setRollbackOnly();
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX084() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_NOTA);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(xares);
            tm.setRollbackOnly();
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX085() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_INVAL);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.rollback();
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX086() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_INVAL);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.setRollbackOnly();
            tm.commit();
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX087() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_INVAL);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.rollback();
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX088() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_INVAL);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(xares);
            tm.rollback();
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX089() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_INVAL);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
            tm.commit();
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX090() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_INVAL);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
            tm.getTransaction().enlistResource(xares);
            tm.commit();
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX091() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_INVAL);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.setRollbackOnly();
            tm.commit();
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX092() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_INVAL);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(xares);
            tm.setRollbackOnly();
            tm.commit();
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX093() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_PROTO);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.rollback();
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX094() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_PROTO);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.setRollbackOnly();
            tm.commit();
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX095() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_PROTO);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.rollback();
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX096() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_PROTO);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(xares);
            tm.rollback();
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX097() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_PROTO);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
            tm.commit();
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX098() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_PROTO);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
            tm.getTransaction().enlistResource(xares);
            tm.commit();
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX099() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_PROTO);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.setRollbackOnly();
            tm.commit();
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX100() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_PROTO);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(xares);
            tm.setRollbackOnly();
            tm.commit();
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
    
        assertFalse(xares.getRollbackCount() == 0);
    }

    public void testEX101() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_RMFAIL);
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
    
        assertTrue(xares.getRollbackCount() == 1);
    }

    public void testEX102() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_RMFAIL);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.setRollbackOnly();
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
    
        assertTrue(xares.getRollbackCount() == 1);
    }

    public void testEX103() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_RMFAIL);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    
        assertTrue(xares.getRollbackCount() == 1);
    }

    public void testEX104() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_RMFAIL);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(xares);
            tm.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    
        assertTrue(xares.getRollbackCount() == 1);
    }

    public void testEX105() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_RMFAIL);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
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
    
        assertTrue(xares.getRollbackCount() == 1);
    }

    public void testEX107() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_RMFAIL);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(xares);
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.setRollbackOnly();
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
    
        assertTrue(xares.getRollbackCount() == 1);
    }

    public void testEX108() throws Exception {
        final XAResourceImpl xares = new XAResourceImpl().setRollbackAction(XAException.XAER_RMFAIL);
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(xares);
            tm.setRollbackOnly();
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
    
        assertEquals(xares.getRollbackCount(), 1);
    }

    public void testEX109() throws Exception {
        try
        {
            tm.begin();
            boolean result = tm.getTransaction().enlistResource(new XAResourceImpl().setStartAction(XAException.XAER_OUTSIDE));
            assertFalse(result);
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

    public void testEX110() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            boolean result = tm.getTransaction().enlistResource(new XAResourceImpl().setStartAction(XAException.XAER_OUTSIDE));
            assertFalse(result);
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

    public void testEX111() throws Exception {
        try
        {
            tm.begin();
            boolean result = tm.getTransaction().enlistResource(new XAResourceImpl().setStartAction(XAException.XA_RBROLLBACK));
            assertFalse(result);
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

    public void testEX112() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            boolean result = tm.getTransaction().enlistResource(new XAResourceImpl().setStartAction(XAException.XA_RBROLLBACK));
            assertFalse(result);
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

    public void testEX113() throws Exception {
        try
        {
            tm.begin();
            boolean result = tm.getTransaction().enlistResource(new XAResourceImpl().setStartAction(XAException.XAER_RMFAIL));
            assertFalse(result);
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

    public void testEX114() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            boolean result = tm.getTransaction().enlistResource(new XAResourceImpl().setStartAction(XAException.XAER_RMFAIL));
            assertFalse(result);
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

    public void testEX115() throws Exception {
        try
        {
            tm.begin();
            boolean result = tm.getTransaction().enlistResource(new XAResourceImpl().setStartAction(XAException.XAER_RMERR));
            assertFalse(result);
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

    public void testEX116() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            boolean result = tm.getTransaction().enlistResource(new XAResourceImpl().setStartAction(XAException.XAER_RMERR));
            assertFalse(result);
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

    public void testEX117() throws Exception {
        try
        {
            tm.begin();
            boolean result = tm.getTransaction().enlistResource(new XAResourceImpl().setStartAction(XAException.XAER_NOTA));
            assertFalse(result);
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

    public void testEX118() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            boolean result = tm.getTransaction().enlistResource(new XAResourceImpl().setStartAction(XAException.XAER_NOTA));
            assertFalse(result);
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

    public void testEX119() throws Exception {
        try
        {
            tm.begin();
            boolean result = tm.getTransaction().enlistResource(new XAResourceImpl().setStartAction(XAException.XAER_INVAL));
            assertFalse(result);
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

    public void testEX120() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            boolean result = tm.getTransaction().enlistResource(new XAResourceImpl().setStartAction(XAException.XAER_INVAL));
            assertFalse(result);
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

    public void testEX121() throws Exception {
        try
        {
            tm.begin();
            boolean result = tm.getTransaction().enlistResource(new XAResourceImpl().setStartAction(XAException.XAER_PROTO));
            assertFalse(result);
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

    public void testEX122() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            boolean result = tm.getTransaction().enlistResource(new XAResourceImpl().setStartAction(XAException.XAER_PROTO));
            assertFalse(result);
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

    public void testEX123() throws Exception {
        try
        {
            tm.begin();
            boolean result = tm.getTransaction().enlistResource(new XAResourceImpl().setStartAction(XAException.XAER_DUPID));
            assertFalse(result);
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

    public void testEX124() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            boolean result = tm.getTransaction().enlistResource(new XAResourceImpl().setStartAction(XAException.XAER_DUPID));
            assertFalse(result);
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
}
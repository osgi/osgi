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
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

import org.osgi.framework.BundleContext;
import org.osgi.test.cases.transaction.util.TransactionManagerFactory;
import org.osgi.test.cases.transaction.util.TransactionUtil;
import org.osgi.test.cases.transaction.util.XAResourceImpl;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * XA test, test various XAExceptions arisen from multiple XAResources
 * 
 *
 */
public class XATest extends DefaultTestBundleControl {
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

    public void testXA001() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();

            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());

            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }
    }

    public void testXA002() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();

            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());

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

    public void testXA003() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();

            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());

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

    public void testXA004() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();

            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));

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

    public void testXA005() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();

            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());

            tx.setRollbackOnly();

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

    public void testXA006() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();

            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());

            tm.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testXA007() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();

            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());

            tx.setRollbackOnly();
            tm.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testXA008() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();

            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAResource.XA_RDONLY));
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAResource.XA_RDONLY));
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAResource.XA_RDONLY));
            tx.enlistResource(new XAResourceImpl());

            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testXA009() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();

            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAResource.XA_RDONLY));
            tx.enlistResource(new XAResourceImpl().setRollbackAction(XAException.XA_HEURRB));
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAResource.XA_RDONLY));
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAResource.XA_RDONLY));
            tx.enlistResource(new XAResourceImpl());

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

    public void testXA010() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();

            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAResource.XA_RDONLY));
            tx.enlistResource(new XAResourceImpl().setRollbackAction(XAException.XA_HEURCOM));
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAResource.XA_RDONLY));
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAResource.XA_RDONLY));
            tx.enlistResource(new XAResourceImpl());

            try
            {
                tm.commit();
                fail();
            }
            catch (HeuristicMixedException e)
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

    public void testXA011() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();

            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAResource.XA_RDONLY));
            tx.enlistResource(new XAResourceImpl().setRollbackAction(XAException.XA_HEURMIX));
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAResource.XA_RDONLY));
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl().setPrepareAction(XAResource.XA_RDONLY));
            tx.enlistResource(new XAResourceImpl());

            try
            {
                tm.commit();
                fail();
            }
            catch (HeuristicMixedException e)
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

    public void testXA013() throws Exception {
        try
        {
            tm.begin();
            final Transaction tx = tm.getTransaction();

            tx.enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURRB));
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURMIX));
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl());
            tx.enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURCOM));

            try
            {
                tm.commit();
                fail();
            }
            catch (HeuristicMixedException e)
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
}
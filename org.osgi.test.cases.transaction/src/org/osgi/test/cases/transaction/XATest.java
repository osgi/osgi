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
import javax.transaction.RollbackException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

import org.osgi.framework.BundleContext;
import org.osgi.test.cases.transaction.util.TransactionManagerFactory;
import org.osgi.test.cases.transaction.util.TransactionUtil;
import org.osgi.test.cases.transaction.util.XAResourceImpl;

/**
 * @version $Rev$ $Date$
 *
 * XA test, test various XAExceptions arisen from multiple XAResources
 * 
 *
 */
public class XATest extends TransactionTestBundleControl {
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
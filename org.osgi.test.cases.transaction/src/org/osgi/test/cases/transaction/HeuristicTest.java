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
import javax.transaction.RollbackException;
import javax.transaction.xa.XAException;

import org.osgi.test.cases.transaction.util.XAResourceImpl;

/**
 * @version $Rev$ $Date$
 *
 * Test the various Heuristic Exceptions, arisen from xa_commit, xa_rollback or xa_prepare.
 *
 *
 */
public class HeuristicTest extends TransactionTestBundleControl {

    public void setUp() throws Exception {
        super.setUpTransactionManager();
    }

    public void testHE001() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURRB));
            tm.commit();
            fail();
        }
        catch (HeuristicRollbackException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }

        assertTrue(XAResourceImpl.checkForgotten());
    }

    public void testHE002() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURMIX));
            tm.commit();
            fail();
        }
        catch (HeuristicMixedException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }

        assertTrue(XAResourceImpl.checkForgotten());
    }

    public void testHE003() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURCOM));
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }

        assertTrue(XAResourceImpl.checkForgotten());
    }

    public void testHE005() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURRB));
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURRB));
            tm.commit();
            fail();
        }
        catch (HeuristicRollbackException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }

        assertTrue(XAResourceImpl.checkForgotten());
    }

    public void testHE006() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURRB));
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURMIX));
            tm.commit();
            fail();
        }
        catch (HeuristicMixedException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }

        assertTrue(XAResourceImpl.checkForgotten());
    }

    public void testHE007() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURRB));
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURCOM));
            tm.commit();
            fail();
        }
        catch (HeuristicMixedException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }

        assertTrue(XAResourceImpl.checkForgotten());
    }

    public void testHE009() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURRB));
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
            e.printStackTrace(System.out);
            fail();
        }

        assertTrue(XAResourceImpl.checkForgotten());
    }

    public void testHE010() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURMIX));
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURMIX));
            tm.commit();
            fail();
        }
        catch (HeuristicMixedException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }

        assertTrue(XAResourceImpl.checkForgotten());
    }

    public void testHE011() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURMIX));
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURCOM));
            tm.commit();
            fail();
        }
        catch (HeuristicMixedException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }

        assertTrue(XAResourceImpl.checkForgotten());
    }

    public void testHE013() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURMIX));
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
            e.printStackTrace(System.out);
            fail();
        }

        assertTrue(XAResourceImpl.checkForgotten());
    }

    public void testHE014() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURCOM));
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURCOM));
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }

        assertTrue(XAResourceImpl.checkForgotten());
    }

    public void testHE016() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAException.XA_HEURCOM));
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }

        assertTrue(XAResourceImpl.checkForgotten());
    }

    public void testHE019() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setRollbackAction(XAException.XA_HEURRB));
            tm.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }

        assertTrue(XAResourceImpl.checkForgotten());
    }

    public void testHE023() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setRollbackAction(XAException.XA_HEURRB));
            tm.getTransaction().enlistResource(new XAResourceImpl().setRollbackAction(XAException.XA_HEURRB));
            tm.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }

        assertTrue(XAResourceImpl.checkForgotten());
    }

    public void testHE027() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setRollbackAction(XAException.XA_HEURRB));
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }

        assertTrue(XAResourceImpl.checkForgotten());
    }

    public void testHE046() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
            tm.getTransaction().enlistResource(new XAResourceImpl().setRollbackAction(XAException.XA_HEURRB));
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

        assertTrue(XAResourceImpl.checkForgotten());
    }

    public void testHE062() throws Exception {
        try
        {
            tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
            tm.getTransaction().enlistResource(new XAResourceImpl().setRollbackAction(XAException.XA_HEURRB));
            tm.getTransaction().enlistResource(new XAResourceImpl().setRollbackAction(XAException.XA_HEURRB));
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

        assertTrue(XAResourceImpl.checkForgotten());
    }   
}
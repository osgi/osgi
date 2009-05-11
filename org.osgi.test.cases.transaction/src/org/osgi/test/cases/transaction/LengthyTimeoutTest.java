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
import javax.transaction.Transaction;
import javax.transaction.xa.XAException;

import org.osgi.test.cases.transaction.util.XAResourceImpl;

/**
 * @version $Rev$ $Date$
 *
 * Test Transaction timeout
 *
 *
 */
public class LengthyTimeoutTest extends TransactionTestBundleControl {
    private static final int DEFAULT_TRANSACTION_TIMEOUT = 20;
    private static final int TEST_TRANSACTION_TIMEOUT = 10;
    private static final int SUITABLE_DELAY = 5;

    public void setUp() throws Exception {
        super.setUpTransactionManager();
    }
    
    public void testTO001() throws Exception {
        try
        {
            tm.setTransactionTimeout(DEFAULT_TRANSACTION_TIMEOUT);
            tm.begin();
            final Transaction tx = tm.suspend();

            Thread.sleep(1000 * (DEFAULT_TRANSACTION_TIMEOUT - SUITABLE_DELAY));

            tm.resume(tx);
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }
    }

    public void testTO002() throws Exception {
        try
        {
            tm.setTransactionTimeout(DEFAULT_TRANSACTION_TIMEOUT);
            tm.begin();
            final Transaction tx = tm.suspend();

            Thread.sleep(1000 * (DEFAULT_TRANSACTION_TIMEOUT + SUITABLE_DELAY));

            tm.resume(tx);
            tm.commit();
            fail();
        }
        catch(RollbackException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }
    }

    public void testTO003() throws Exception {    
        try
        {
            tm.setTransactionTimeout(TEST_TRANSACTION_TIMEOUT);
            tm.begin();
            final Transaction tx = tm.suspend();
    
            Thread.sleep(1000 * (TEST_TRANSACTION_TIMEOUT - SUITABLE_DELAY));
    
            tm.resume(tx);
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }
    }

    public void testTO004() throws Exception {
        try
        {
            tm.setTransactionTimeout(TEST_TRANSACTION_TIMEOUT);
            tm.begin();
            final Transaction tx = tm.suspend();
    
            Thread.sleep(1000 * (TEST_TRANSACTION_TIMEOUT + SUITABLE_DELAY));
    
            tm.resume(tx);
            tm.commit();
        }
        catch(RollbackException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }
    }

    public void testTO005() throws Exception {
        try
        {
        	tm.setTransactionTimeout(DEFAULT_TRANSACTION_TIMEOUT);
        	tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAResourceImpl.SLEEP_COMMIT).setSleepTime(1000 * (DEFAULT_TRANSACTION_TIMEOUT * 2 + SUITABLE_DELAY)));
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }
    }

    public void testTO006() throws Exception {       
        try
        {
        	tm.setTransactionTimeout(DEFAULT_TRANSACTION_TIMEOUT);
        	tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAResourceImpl.SLEEP_ROLLBACK).setSleepTime(1000 * (DEFAULT_TRANSACTION_TIMEOUT * 2 + SUITABLE_DELAY)));
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

    public void testTO007() throws Exception {
        try
        {
        	tm.setTransactionTimeout(DEFAULT_TRANSACTION_TIMEOUT);
        	tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl());
            tm.getTransaction().enlistResource(new XAResourceImpl().setCommitAction(XAResourceImpl.SLEEP_COMMIT).setSleepTime(1000 * (DEFAULT_TRANSACTION_TIMEOUT * 2 + SUITABLE_DELAY)));
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }
    }

    public void testTO008() throws Exception {    
        try
        {
        	tm.setTransactionTimeout(DEFAULT_TRANSACTION_TIMEOUT);
        	tm.begin();
            tm.getTransaction().enlistResource(new XAResourceImpl().setPrepareAction(XAException.XA_RBROLLBACK));
            tm.getTransaction().enlistResource(new XAResourceImpl().setRollbackAction(XAResourceImpl.SLEEP_ROLLBACK).setSleepTime(1000 * (DEFAULT_TRANSACTION_TIMEOUT * 2 + SUITABLE_DELAY)));
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
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

import javax.transaction.RollbackException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAException;

import org.osgi.framework.BundleContext;
import org.osgi.test.cases.transaction.util.TransactionManagerFactory;
import org.osgi.test.cases.transaction.util.TransactionUtil;
import org.osgi.test.cases.transaction.util.XAResourceImpl;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Test Transaction timeout
 *
 *
 */
public class LengthyTimeoutTest extends DefaultTestBundleControl {
    private static final int DEFAULT_TRANSACTION_TIMEOUT = 20;
    private static final int TEST_TRANSACTION_TIMEOUT = 10;
    private static final int SUITABLE_DELAY = 5;


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
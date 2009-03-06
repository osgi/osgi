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

import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

import org.osgi.framework.BundleContext;
import org.osgi.test.cases.transaction.util.SimpleTestResource;
import org.osgi.test.cases.transaction.util.TransactionManagerFactory;
import org.osgi.test.cases.transaction.util.TransactionUtil;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * simple transaction manager test, test basic commit, rollback, suspend/resume
 *
 *
 */

public class SimpleTransactionManagerTest extends DefaultTestBundleControl {

    TransactionManager tm;
    BundleContext context;

    public void setBundleContext(BundleContext context) {
        this.context = context;
        TransactionManagerFactory.setBundleContext(context);
    }
    
    public void setUp() throws Exception {
        tm = TransactionManagerFactory.getTransactionManager();
        TransactionUtil.startWithCleanTM(tm);
    }

    // 4.5.3 4.5.5 simple test transaction manager commit without resource
    public void testTransactionManager1() throws Exception {
        startWithClean();
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
        
        tm.begin();       
        assertEquals(Status.STATUS_ACTIVE, tm.getStatus());
        assertNotNull(tm.getTransaction());
        
        tm.commit();
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
        assertNull(tm.getTransaction());
        
        tm.begin();
        assertEquals(Status.STATUS_ACTIVE, tm.getStatus());
        assertNotNull(tm.getTransaction());
        
        tm.commit();
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
        assertNull(tm.getTransaction());
    }
    
    // 4.5.3 4.5.5 simple test transaction manager commit with resource
    public void testTransactionManager2() throws Exception {
        startWithClean();
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
        
        tm.begin();        
        SimpleTestResource res1 = new SimpleTestResource("res1");
        SimpleTestResource res2 = new SimpleTestResource("res2");
        tm.getTransaction().enlistResource(res1);
        tm.getTransaction().enlistResource(res2);
        assertFalse(res1.isCommitted());
        assertFalse(res2.isCommitted());
        assertFalse(res1.isRolledback());
        assertFalse(res2.isRolledback());
        assertEquals(Status.STATUS_ACTIVE, tm.getStatus());
        assertNotNull(tm.getTransaction());
        
        tm.getTransaction().delistResource(res1, XAResource.TMSUCCESS);
        tm.getTransaction().delistResource(res2, XAResource.TMSUCCESS);
        tm.commit();
        assertTrue(res1.isCommitted());
        assertTrue(res2.isCommitted());
        assertFalse(res1.isRolledback());
        assertFalse(res2.isRolledback());
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
        assertNull(tm.getTransaction());
        
        tm.begin();        
        res1 = new SimpleTestResource("res1");
        res2 = new SimpleTestResource("res2");
        tm.getTransaction().enlistResource(res1);
        tm.getTransaction().enlistResource(res2);
        assertFalse(res1.isCommitted());
        assertFalse(res2.isCommitted());
        assertFalse(res1.isRolledback());
        assertFalse(res2.isRolledback());
        assertEquals(Status.STATUS_ACTIVE, tm.getStatus());
        assertNotNull(tm.getTransaction());
        
        tm.getTransaction().delistResource(res1, XAResource.TMSUCCESS);
        tm.getTransaction().delistResource(res2, XAResource.TMSUCCESS);
        tm.commit();
        assertTrue(res1.isCommitted());
        assertTrue(res2.isCommitted());
        assertFalse(res1.isRolledback());
        assertFalse(res2.isRolledback());
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
        assertNull(tm.getTransaction());
    }
    // 4.5.1, 4.5.6 test transaction manager status, rollback without resource
    public void testTransactionManager3() throws Exception {
        startWithClean();
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
        assertNull(tm.getTransaction());
        
        tm.begin();
        assertEquals(Status.STATUS_ACTIVE, tm.getStatus());
        Transaction tx = tm.getTransaction();
        assertNotNull(tx);
        assertEquals(Status.STATUS_ACTIVE, tx.getStatus());
         
        tm.rollback();
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
        assertNull(tm.getTransaction());     
         
        tm.begin();
        assertEquals(Status.STATUS_ACTIVE, tm.getStatus());
        tx = tm.getTransaction();
        assertNotNull(tx);
        assertEquals(Status.STATUS_ACTIVE, tx.getStatus());
         
        tm.rollback();
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
        assertNull(tm.getTransaction()); 
    }
    
    // 4.5.1, 4.5.6 test transaction manager status, rollback with resource
    public void testTransactionManager4() throws Exception {
        startWithClean();
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
        assertNull(tm.getTransaction());
        SimpleTestResource res1 = new SimpleTestResource("res1");
        SimpleTestResource res2 = new SimpleTestResource("res2");
        
        tm.begin();
        assertEquals(Status.STATUS_ACTIVE, tm.getStatus());
        Transaction tx = tm.getTransaction();
        assertNotNull(tx);
        assertEquals(Status.STATUS_ACTIVE, tx.getStatus());
        
        tm.getTransaction().enlistResource(res1);
        tm.getTransaction().enlistResource(res2);
        assertFalse(res1.isCommitted());
        assertFalse(res2.isCommitted());
        assertFalse(res1.isRolledback());
        assertFalse(res2.isRolledback());
         
        tm.getTransaction().delistResource(res1, XAResource.TMSUCCESS);
        tm.getTransaction().delistResource(res2, XAResource.TMSUCCESS);
        tm.rollback();
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
        assertNull(tm.getTransaction());     
        assertFalse(res1.isCommitted());
        assertFalse(res2.isCommitted());
        assertTrue(res1.isRolledback());
        assertTrue(res2.isRolledback());
    }

    // 4.5.3 test transaction manager status, commit fail, without resource
    public void testTransactionManager5() throws Exception {
        startWithClean();
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
        assertNull(tm.getTransaction());
        
        tm.begin();
        assertEquals(Status.STATUS_ACTIVE, tm.getStatus());
        Transaction tx = tm.getTransaction();
        assertNotNull(tx);
        assertEquals(Status.STATUS_ACTIVE, tx.getStatus());
         
        // set roll back only.   commit should fail
        tm.setRollbackOnly();
        assertEquals(Status.STATUS_MARKED_ROLLBACK, tm.getStatus());
        try {
            tm.commit();
            fail("expected a RollbackException : the transaction has been rolled back.");
        } catch (RollbackException e) {
            // expected
        }
        
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
        assertNull(tm.getTransaction()); 
    }
    
    // 4.5.3 test transaction manager status, commit fail, with resource
    public void testTransactionManager6() throws Exception {
        startWithClean();
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
        assertNull(tm.getTransaction());
        SimpleTestResource res1 = new SimpleTestResource("res1");
        SimpleTestResource res2 = new SimpleTestResource("res2");
        
        tm.begin();
        assertEquals(Status.STATUS_ACTIVE, tm.getStatus());
        Transaction tx = tm.getTransaction();
        assertNotNull(tx);
        assertEquals(Status.STATUS_ACTIVE, tx.getStatus());
        
        tm.getTransaction().enlistResource(res1);
        tm.getTransaction().enlistResource(res2);
        assertFalse(res1.isCommitted());
        assertFalse(res2.isCommitted());
        assertFalse(res1.isRolledback());
        assertFalse(res2.isRolledback());
         
        // set roll back only.   commit should fail
        tm.setRollbackOnly();
        assertEquals(Status.STATUS_MARKED_ROLLBACK, tm.getStatus());
        tm.getTransaction().delistResource(res1, XAResource.TMSUCCESS);
        tm.getTransaction().delistResource(res2, XAResource.TMSUCCESS);
        try {
            tm.commit();
            fail("expected a RollbackException : the transaction has been rolled back.");
        } catch (RollbackException e) {
            // expected
        }
        
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
        assertNull(tm.getTransaction()); 
        assertFalse(res1.isCommitted());
        assertFalse(res2.isCommitted());
        assertTrue(res1.isRolledback());
        assertTrue(res2.isRolledback());
    }
    // test 4.3.1 Nested transactions are not supported. 
    public void testTransactionManagerNested() throws Exception {
        startWithClean();
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
        assertNull(tm.getTransaction());
        
        tm.begin();
        assertEquals(Status.STATUS_ACTIVE, tm.getStatus());
        Transaction tx = tm.getTransaction();
        assertNotNull(tx);
        assertEquals(Status.STATUS_ACTIVE, tx.getStatus());
        
        tm.rollback();
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
        assertNull(tm.getTransaction());     
        
        tm.begin();
        assertEquals(Status.STATUS_ACTIVE, tm.getStatus());
        
        try {
            tm.begin();
            fail("expected a NotSupportedException : Nested Transactions are not supported.");
        } catch (NotSupportedException e) {
            // expected
        }
        
        tm.commit();
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
        assertNull(tm.getTransaction());
    }
    
    // 4.5.8 test transaction suspend/resume
    // test transaction manager status without resource
    public void testTransactionManager10() throws Exception {
        startWithClean();
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
        assertNull(tm.getTransaction());
        
        tm.begin();
        assertEquals(Status.STATUS_ACTIVE, tm.getStatus());
        Transaction tx = tm.getTransaction();
        assertNotNull(tx);
        assertEquals(Status.STATUS_ACTIVE, tx.getStatus());
        
        tx = tm.suspend();
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
        assertNull(tm.getTransaction()); 
        
        tm.resume(tx);

        assertEquals(Status.STATUS_ACTIVE, tm.getStatus());
        assertNotNull(tm.getTransaction());
        assertEquals(tx, tm.getTransaction());  
     
        tm.rollback();
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
        assertNull(tm.getTransaction()); 
    }
    
    private void startWithClean()
    {
        try
        {
            // let's always start with no transaction when starting each test.
            if (tm.getStatus() != Status.STATUS_NO_TRANSACTION)
            {
                tm.rollback();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();     
        }
    }  

}

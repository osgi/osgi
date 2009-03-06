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
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import javax.transaction.xa.XAResource;

import org.osgi.framework.BundleContext;
import org.osgi.test.cases.transaction.util.SimpleTestResource;
import org.osgi.test.cases.transaction.util.TransactionManagerFactory;
import org.osgi.test.cases.transaction.util.TransactionUtil;
import org.osgi.test.cases.transaction.util.UserTransactionFactory;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * simple user transaction test, test basic commit, rollback, suspend/resume
 *
 *
 */

public class SimpleUserTransactionTest extends DefaultTestBundleControl {

    UserTransaction ut;
    TransactionManager tm;
    BundleContext context;

    public void setBundleContext(BundleContext context) {
        this.context = context;
        TransactionManagerFactory.setBundleContext(context);
        UserTransactionFactory.setBundleContext(context);
    }
    
    public void setUp() throws Exception {
        tm = TransactionManagerFactory.getTransactionManager();
        ut = UserTransactionFactory.getUserTransaction();
        TransactionUtil.startWithClean(tm, ut); 
    }
    
    // 4.5.3 4.5.5 simple test user transaction commit without resource 
    public void testUserTransaction1() throws Exception {
        assertEquals(Status.STATUS_NO_TRANSACTION, ut.getStatus());
        assertNull(tm.getTransaction());

        // test commit
        ut.begin();
        assertEquals(Status.STATUS_ACTIVE, ut.getStatus());
        assertNotNull(tm.getTransaction());
        
        ut.commit();
        assertEquals(Status.STATUS_NO_TRANSACTION, ut.getStatus());
        assertNull(tm.getTransaction()); 
    }
    
    // 4.5.3 4.5.5 simple test user transaction commit with resource 
    public void testUserTransaction2() throws Exception {
        assertEquals(Status.STATUS_NO_TRANSACTION, ut.getStatus());
        assertNull(tm.getTransaction());
        SimpleTestResource res1 = new SimpleTestResource("res1");
        SimpleTestResource res2 = new SimpleTestResource("res2");
        
        // test commit
        ut.begin();
        assertEquals(Status.STATUS_ACTIVE, ut.getStatus());
        assertNotNull(tm.getTransaction());
        tm.getTransaction().enlistResource(res1);
        tm.getTransaction().enlistResource(res2);
        assertFalse(res1.isCommitted());
        assertFalse(res2.isCommitted());
        tm.getTransaction().delistResource(res1, XAResource.TMSUCCESS);
        tm.getTransaction().delistResource(res2, XAResource.TMSUCCESS);
        
        ut.commit();
        assertTrue(res2.isCommitted());
        assertTrue(res1.isCommitted());
        assertTrue(!res2.isRolledback());
        assertTrue(!res1.isRolledback());
        assertEquals(Status.STATUS_NO_TRANSACTION, ut.getStatus());
        assertNull(tm.getTransaction()); 
    }
    
    // 4.5.6, 4.5.7 simple test user transaction rollbackonly without resource 
    public void testUserTransaction3() throws Exception {
        assertEquals(Status.STATUS_NO_TRANSACTION, ut.getStatus());
        assertNull(tm.getTransaction());
        
        // test commit
        ut.begin();
        assertEquals(Status.STATUS_ACTIVE, ut.getStatus());
        assertNotNull(tm.getTransaction());

        // set roll back only.   commit should fail
        ut.setRollbackOnly();
        assertEquals(Status.STATUS_MARKED_ROLLBACK, tm.getStatus());
        
        try {
            ut.commit();
            fail("expected a RollbackException : the transaction has been rolled back.");
        } catch (RollbackException e) {
            // expected
        }

        assertEquals(Status.STATUS_NO_TRANSACTION, ut.getStatus());
        assertNull(tm.getTransaction()); 
    }   
    
    // 4.5.6, 4.5.7 simple test user transaction rollbackonly with resource 
    public void testUserTransaction4() throws Exception {
        assertEquals(Status.STATUS_NO_TRANSACTION, ut.getStatus());
        assertNull(tm.getTransaction());
        SimpleTestResource res1 = new SimpleTestResource("res1");
        SimpleTestResource res2 = new SimpleTestResource("res2");
        
        // test commit
        ut.begin();
        assertEquals(Status.STATUS_ACTIVE, ut.getStatus());
        assertNotNull(tm.getTransaction());
        tm.getTransaction().enlistResource(res1);
        tm.getTransaction().enlistResource(res2);
        assertFalse(res1.isCommitted());
        assertFalse(res2.isCommitted());

        // set roll back only.   commit should fail
        ut.setRollbackOnly();
        assertEquals(Status.STATUS_MARKED_ROLLBACK, tm.getStatus());
        tm.getTransaction().delistResource(res1, XAResource.TMSUCCESS);
        tm.getTransaction().delistResource(res2, XAResource.TMSUCCESS);
        
        try {
            ut.commit();
            fail("expected a RollbackException : the transaction has been rolled back.");
        } catch (RollbackException e) {
            // expected
        }

        assertTrue(res2.isRolledback());
        assertTrue(res1.isRolledback());
        assertTrue(!res2.isCommitted());
        assertTrue(!res1.isCommitted());
        assertEquals(Status.STATUS_NO_TRANSACTION, ut.getStatus());
        assertNull(tm.getTransaction()); 
    }   

    // 4.5.6, 4.5.7 simple test user transaction rollback 2 without resource 
    public void testUserTransaction5() throws Exception {
        assertEquals(Status.STATUS_NO_TRANSACTION, ut.getStatus());
        assertNull(tm.getTransaction());
        
        // test commit
        ut.begin();
        assertEquals(Status.STATUS_ACTIVE, ut.getStatus());
        assertNotNull(tm.getTransaction());
        
        ut.rollback();
        assertEquals(Status.STATUS_NO_TRANSACTION, ut.getStatus());
        assertNull(tm.getTransaction()); 
    }  
    
    // 4.5.6, 4.5.7 simple test user transaction rollback 2 with resource 
    public void testUserTransaction6() throws Exception {
        assertEquals(Status.STATUS_NO_TRANSACTION, ut.getStatus());
        assertNull(tm.getTransaction());
        SimpleTestResource res1 = new SimpleTestResource("res1");
        SimpleTestResource res2 = new SimpleTestResource("res2");
        
        // test commit
        ut.begin();
        assertEquals(Status.STATUS_ACTIVE, ut.getStatus());
        assertNotNull(tm.getTransaction());
        tm.getTransaction().enlistResource(res1);
        tm.getTransaction().enlistResource(res2);
        assertFalse(res1.isCommitted());
        assertFalse(res2.isCommitted());
        tm.getTransaction().delistResource(res1, XAResource.TMSUCCESS);
        tm.getTransaction().delistResource(res2, XAResource.TMSUCCESS);
        
        ut.rollback();
        assertTrue(res2.isRolledback());
        assertTrue(res1.isRolledback());
        assertTrue(!res2.isCommitted());
        assertTrue(!res1.isCommitted());
        assertEquals(Status.STATUS_NO_TRANSACTION, ut.getStatus());
        assertNull(tm.getTransaction()); 
    }   
    
    // 4.5.1 simple test user transaction with error 
    public void testUserTransactionNested() throws Exception {
        assertEquals(Status.STATUS_NO_TRANSACTION, ut.getStatus());
        assertNull(tm.getTransaction());
        
        ut.begin();
        assertEquals(Status.STATUS_ACTIVE, ut.getStatus());
        assertNotNull(tm.getTransaction());
        
        SimpleTestResource res1 = new SimpleTestResource("res1");
        SimpleTestResource res2 = new SimpleTestResource("res2");
        tm.getTransaction().enlistResource(res1);
        tm.getTransaction().enlistResource(res2);
        assertFalse(res1.isCommitted());
        assertFalse(res2.isCommitted());
        
        try {
            ut.begin();
            fail("expected a NotSupportedException : Nested Transactions are not supported.");
        } catch (NotSupportedException e) {
            // expected
        }
        
        tm.getTransaction().delistResource(res1, XAResource.TMSUCCESS);
        tm.getTransaction().delistResource(res2, XAResource.TMSUCCESS);
        ut.commit();
        assertTrue(res2.isCommitted());
        assertTrue(res1.isCommitted());
        assertTrue(!res2.isRolledback());
        assertTrue(!res1.isRolledback());
        assertEquals(Status.STATUS_NO_TRANSACTION, ut.getStatus());
        assertNull(tm.getTransaction());
    }
}

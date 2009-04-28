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

/**
 * @version $Rev$ $Date$
 *
 * simple transaction manager test, test basic commit, rollback, suspend/resume
 *
 *
 */

public class SimpleTransactionManagerTest extends TransactionTestBundleControl {

    TransactionManager tm;
    BundleContext context;

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

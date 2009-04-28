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
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import javax.transaction.xa.XAResource;

import org.osgi.framework.BundleContext;
import org.osgi.test.cases.transaction.util.SimpleTestResource;
import org.osgi.test.cases.transaction.util.TransactionManagerFactory;
import org.osgi.test.cases.transaction.util.TransactionUtil;
import org.osgi.test.cases.transaction.util.UserTransactionFactory;

/**
 * @version $Rev$ $Date$
 *
 * simple user transaction test, test basic commit, rollback, suspend/resume
 *
 *
 */

public class SimpleUserTransactionTest extends TransactionTestBundleControl {

    UserTransaction ut;
    TransactionManager tm;
    BundleContext context;

    public void setBundleContext(BundleContext context) {
        super.setBundleContext(context);
        TransactionManagerFactory.setBundleContext(context);
        UserTransactionFactory.setBundleContext(context);
    }
    
    public void setUp() throws Exception {
        tm = TransactionManagerFactory.getTransactionManager();
        if (tm == null) {
            super.waitSomeTime();
            // let's try get tm again after the waiting
            tm = TransactionManagerFactory.getTransactionManager();
        }
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

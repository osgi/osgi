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

import javax.transaction.Status;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.xa.XAResource;

import org.osgi.framework.BundleContext;
import org.osgi.test.cases.transaction.util.SimpleSynchronization;
import org.osgi.test.cases.transaction.util.SimpleTestResource;
import org.osgi.test.cases.transaction.util.TransactionManagerFactory;
import org.osgi.test.cases.transaction.util.TransactionSynchronizationRegistryFactory;
import org.osgi.test.cases.transaction.util.TransactionUtil;

/**
 * @version $Rev$ $Date$
 *
 * simple transaction SynchronizationRegistry test, test basic commit, rollback
 * 
 *
 */
public class SimpleTransactionSynchronizationRegistryTest extends TransactionTestBundleControl {

    
    TransactionManager tm;
    BundleContext context;

    public void setBundleContext(BundleContext context) {
        super.setBundleContext(context);
        TransactionManagerFactory.setBundleContext(context);
        TransactionSynchronizationRegistryFactory.setBundleContext(context);
    }
    
    // 4.4 locating TransactionSynchronizationRegistry OSGi service
    public void setUp() throws Exception {
        tm = TransactionManagerFactory.getTransactionManager();
        if (tm == null) {
            super.waitSomeTime();
            // let's try get tm again after the waiting
            tm = TransactionManagerFactory.getTransactionManager();
        }
        TransactionUtil.startWithCleanTM(tm);
    }
 
    // 4.3.3 4.4 simple test TransactionSynchronizationRegistry rollback without resource
    public void testTransactionSynchronizationRegistry1() throws Exception {
        final TransactionSynchronizationRegistry tsr = TransactionSynchronizationRegistryFactory.getTransactionSynchronizationRegistry();
        
        SimpleSynchronization sync = new SimpleSynchronization();
      
        assertNull(tsr.getTransactionKey());
        
        tm.begin();
        assertNotNull(tsr.getTransactionKey());
        
        tsr.registerInterposedSynchronization(sync);  
        
        tsr.setRollbackOnly();
        assertTrue(tsr.getRollbackOnly());
        assertEquals(Status.STATUS_MARKED_ROLLBACK, tm.getStatus());
        assertFalse(sync.getBeforeValue().endsWith("-before"));
        assertFalse(sync.getAfterValue().endsWith("-after"));
        
        tm.rollback();

        assertNull(tsr.getTransactionKey());
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
        assertNull(tm.getTransaction());       
        assertFalse(sync.getBeforeValue().endsWith("-before"));
        assertTrue(sync.getAfterValue().endsWith("-after"));
    }
    
    // 4.3.3 4.4 simple test TransactionSynchronizationRegistry rollback with resource
    public void testTransactionSynchronizationRegistry2() throws Exception {
        final TransactionSynchronizationRegistry tsr = TransactionSynchronizationRegistryFactory.getTransactionSynchronizationRegistry();
        SimpleSynchronization sync = new SimpleSynchronization();
        SimpleTestResource res1 = new SimpleTestResource("res1");
        SimpleTestResource res2 = new SimpleTestResource("res2");
      
        assertNull(tsr.getTransactionKey());
        
        tm.begin();
        assertNotNull(tsr.getTransactionKey());
        
        tsr.registerInterposedSynchronization(sync);  
        tm.getTransaction().enlistResource(res1);
        tm.getTransaction().enlistResource(res2);
        
        tsr.setRollbackOnly();
        assertTrue(tsr.getRollbackOnly());
        assertEquals(Status.STATUS_MARKED_ROLLBACK, tm.getStatus());
        assertFalse(sync.getBeforeValue().endsWith("-before"));
        assertFalse(sync.getAfterValue().endsWith("-after"));
        
        tm.getTransaction().delistResource(res1, XAResource.TMSUCCESS);
        tm.getTransaction().delistResource(res2, XAResource.TMSUCCESS);
        tm.rollback();

        assertNull(tsr.getTransactionKey());
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
        assertNull(tm.getTransaction());       
        assertFalse(sync.getBeforeValue().endsWith("-before"));
        assertTrue(sync.getAfterValue().endsWith("-after"));
    }
    
    // 4.3.3 4.4 simple test TransactionSynchronizationRegistry commit without resource
    public void testTransactionSynchronizationRegistry3() throws Exception {
        final TransactionSynchronizationRegistry tsr = TransactionSynchronizationRegistryFactory.getTransactionSynchronizationRegistry();
        SimpleSynchronization sync = new SimpleSynchronization();
        
        assertNull(tsr.getTransactionKey());
        
        tm.begin();
        assertNotNull(tsr.getTransactionKey());
        
        tsr.registerInterposedSynchronization(sync);  
        assertFalse(sync.getBeforeValue().endsWith("-before"));
        assertFalse(sync.getAfterValue().endsWith("-after"));

        tm.commit();

        assertNull(tsr.getTransactionKey());
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
        assertNull(tm.getTransaction());
        assertTrue(sync.getBeforeValue().endsWith("-before"));
        assertTrue(sync.getAfterValue().endsWith("-after"));
    }
    
    
    // 4.3.3 4.4 simple test TransactionSynchronizationRegistry commit with resource
    public void testTransactionSynchronizationRegistry4() throws Exception {
        final TransactionSynchronizationRegistry tsr = TransactionSynchronizationRegistryFactory.getTransactionSynchronizationRegistry();
        SimpleTestResource res1 = new SimpleTestResource("res1");
        SimpleTestResource res2 = new SimpleTestResource("res2");
        SimpleSynchronization sync = new SimpleSynchronization();
        

        assertNull(tsr.getTransactionKey());
        
        tm.begin();
        assertNotNull(tsr.getTransactionKey());
        tm.getTransaction().enlistResource(res1);
        tm.getTransaction().enlistResource(res2);
        
        tsr.registerInterposedSynchronization(sync);  
        assertFalse(sync.getBeforeValue().endsWith("-before"));
        assertFalse(sync.getAfterValue().endsWith("-after"));

        tm.getTransaction().delistResource(res1, XAResource.TMSUCCESS);
        tm.getTransaction().delistResource(res2, XAResource.TMSUCCESS);
        tm.commit();

        assertNull(tsr.getTransactionKey());
        assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
        assertNull(tm.getTransaction());
        assertTrue(sync.getBeforeValue().endsWith("-before"));
        assertTrue(sync.getAfterValue().endsWith("-after"));
    }
}

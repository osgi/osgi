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
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * simple transaction SynchronizationRegistry test, test basic commit, rollback
 * 
 *
 */
public class SimpleTransactionSynchronizationRegistryTest extends DefaultTestBundleControl {

    
    TransactionManager tm;
    BundleContext context;

    public void setBundleContext(BundleContext context) {
        this.context = context;
        TransactionManagerFactory.setBundleContext(context);
        TransactionSynchronizationRegistryFactory.setBundleContext(context);
    }
    
    // 4.4 locating TransactionSynchronizationRegistry OSGi service
    public void setUp() throws Exception {
        tm = TransactionManagerFactory.getTransactionManager();
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

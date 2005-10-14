/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.deploymentadmin;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.osgi.framework.BundleException;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentSession;

public class Transaction {
    
    public static final int INSTALLBUNDLE   = 0;
    public static final int UPDATEBUNDLE    = 1;
    public static final int UNINSTALLBUNDLE = 2;
    public static final int STARTBUNDLE     = 3;
    public static final int STOPBUNDLE      = 4;
    public static final int PROCESSOR       = 5;
    
    static final String[] transactionCodes = {
        "INSTALLBUNDLE",
        "UPDATEBUNDLE", 
        "UNINSTALLBUNDLE", 
        "STARTBUNDLE", 
        "STOPBUNDLE", 
        "PROCESSOR"
    };
    
    private List                  steps;
    private HashSet               processors;
    private Logger                logger;
    private DeploymentSession	  session;
    private boolean 			  cancelled;
    
    // Transaction is singleton
    private static Transaction instance;
    private Transaction() {
    }
    public static Transaction createTransaction(DeploymentSession session, Logger logger) {
        if (null == instance)
            instance = new Transaction();
        instance.logger = logger;
        instance.session = session;
        return instance;
    }
    
    public synchronized void start() {
        steps = new Vector();
        processors = new HashSet();
        cancelled = false;
        logger.log(Logger.LOG_INFO, "Transaction started");
    }

    public synchronized boolean addRecord(TransactionRecord record) {
        if (cancelled)
            throw new CancelException();
        
        if (PROCESSOR == record.code) {
            if (processors.contains(record.rp))
                return true;
            record.rp.begin(session);
            processors.add(record.rp);
        }
        steps.add(record);
        logger.log(Logger.LOG_INFO, "Transaction record added:\n" + record);
        return true;
    }
    
    public synchronized void commit() throws DeploymentException {
        // prepare !
        try {
	        for (ListIterator iter = steps.listIterator(steps.size()); iter.hasPrevious();) {
	            TransactionRecord element = (TransactionRecord) iter.previous();
	            if (element.code == PROCESSOR) {
	                element.rp.prepare();
	                logger.log(Logger.LOG_INFO, "Prepare " + element);
	            }
	        }
        } catch (DeploymentException e) {
            rollback();
            throw e;
        }
        
        // commit !
        try {
	        for (ListIterator iter = steps.listIterator(steps.size()); iter.hasPrevious();) {
	            final TransactionRecord element = (TransactionRecord) iter.previous();
	            logger.log(Logger.LOG_INFO, "Commit\n" + element);
	            switch (element.code) {
	                case INSTALLBUNDLE :
	                    break;
	                case UPDATEBUNDLE :
	                    break;
	                case UNINSTALLBUNDLE :
	                	try {
	                        AccessController.doPrivileged(new PrivilegedExceptionAction() {
	                            public Object run() throws BundleException {
	                                element.bundle.uninstall();
	                                return null;
	                            }});
	                    }
	                    catch (PrivilegedActionException e) {
	                        throw (BundleException) e.getException();
	                    }
	                    element.dp.remove(
	                            element.be);
	                    break;
	                case STARTBUNDLE :
	                    break;
	                case STOPBUNDLE :
	                    break;
	                case PROCESSOR:
	                    element.rp.commit();
	                    break;
	                default :
	                    break;
	            }
	        }
        } catch (Exception e) {
            logger.log(Logger.LOG_ERROR, "Error occured during transaction commit.");
            logger.log(e);
        }
        logger.log(Logger.LOG_INFO, "Transaction committed");
    }

    public synchronized void rollback() {
        try {
            if (steps.size() <= 0) {
                logger.log(Logger.LOG_INFO, "Transaction rolled back");
                return;
            }
            
            for (ListIterator iter = steps.listIterator(steps.size()); iter.hasPrevious();) {
                final TransactionRecord element = (TransactionRecord) iter.previous();
	            logger.log(Logger.LOG_INFO, "Rollback\n" + element);
	            switch (element.code) {
	                case INSTALLBUNDLE : {
	                	try {
	                        AccessController.doPrivileged(new PrivilegedExceptionAction() {
	                            public Object run() throws BundleException {
	                                element.bundle.uninstall();
	                                return null;
	                            }});
	                    }
	                    catch (PrivilegedActionException e) {
	                        throw (BundleException) e.getException();
	                    }
	                    break;
	                } 
	                case UPDATEBUNDLE :
                        // TODO use framework backdoor for transactionality
	                    break;
	                case UNINSTALLBUNDLE : {
	                    break;
	                } case STARTBUNDLE :
	                	try {
	                        AccessController.doPrivileged(new PrivilegedExceptionAction() {
	                            public Object run() throws BundleException {
	                                element.bundle.stop();
	                                return null;
	                            }});
	                    }
	                    catch (PrivilegedActionException e) {
	                        throw (BundleException) e.getException();
	                    }
	                    break;
	                case STOPBUNDLE :
	                	try {
	                        AccessController.doPrivileged(new PrivilegedExceptionAction() {
	                            public Object run() throws BundleException {
	                                element.bundle.start();
	                                return null;
	                            }});
	                    }
	                    catch (PrivilegedActionException e) {
	                        throw (BundleException) e.getException();
	                    }
	                    break;
	                case PROCESSOR:
	                    // TODO security !
	                    element.rp.rollback();
	                    break;
	                default :
	                    break;
	            }
	        }
        } catch (Exception e) {
            logger.log(Logger.LOG_ERROR, "Error occured during transaction rollback.");
            logger.log(e);
        }
        logger.log(Logger.LOG_INFO, "Transaction rolled back");
    }

    public synchronized void cancel() {
        cancelled = true;
    }
    
}

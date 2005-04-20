package org.osgi.impl.service.deploymentadmin;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.osgi.framework.Bundle;
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
    
    public void start() {
        steps = new Vector();
        processors = new HashSet();
        cancelled = false;
        logger.log(Logger.LOG_INFO, "Transaction started");
    }

    public synchronized boolean addRecord(TransactionRecord record) {
        if (cancelled)
            throw new CancelException();
        
        if (PROCESSOR == record.code) {
            if (processors.contains(record.rp)) {
                return true;
            }
            else {
                record.rp.begin(session);
                processors.add(record.rp);
            }
        }
        steps.add(record);
        logger.log(Logger.LOG_INFO, "Transaction record added:\n" + record);
        return true;
    }
    
    public synchronized void commit() throws DeploymentException {
        // prepare !
        try {
	        for (Iterator iter = steps.iterator(); iter.hasNext();) {
	            TransactionRecord element = (TransactionRecord) iter.next();
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
	        for (Iterator iter = steps.iterator(); iter.hasNext();) {
	            TransactionRecord element = (TransactionRecord) iter.next();
	            logger.log(Logger.LOG_INFO, "Commit\n" + element);
	            switch (element.code) {
	                case INSTALLBUNDLE :
	                    break;
	                case UPDATEBUNDLE :
	                    break;
	                case UNINSTALLBUNDLE :
	                    element.bundle.uninstall();
	                    element.dp.getBundleEntries().remove(
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
            
	        for (int i = steps.size() - 1; i >= 0; --i) {
	            TransactionRecord element = (TransactionRecord) steps.get(i);
	            logger.log(Logger.LOG_INFO, "Rollback\n" + element);
	            switch (element.code) {
	                case INSTALLBUNDLE : {
	                    uninstallBundle(element.bundle);
	                    break;
	                } 
	                case UPDATEBUNDLE :
	                    break;
	                case UNINSTALLBUNDLE : {
	                    break;
	                } case STARTBUNDLE :
	                    element.bundle.stop();
	                    break;
	                case STOPBUNDLE :
	                    element.bundle.start();
	                    break;
	                case PROCESSOR:
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

    private void uninstallBundle(final Bundle bundle) throws BundleException {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws BundleException {
                    bundle.uninstall();
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw (BundleException) e.getException();
        }
    }

    public synchronized void cancel() {
        cancelled = true;
    }

}

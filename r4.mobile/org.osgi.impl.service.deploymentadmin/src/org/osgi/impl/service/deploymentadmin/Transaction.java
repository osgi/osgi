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

import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.deploymentadmin.spi.DeploymentSession;

public class Transaction implements Serializable {
    
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
    
    private List                        steps;
    private HashSet                     processors;
    private String                      name;
    private transient Logger            logger;
    private transient DeploymentSession session;
    private transient BundleContext     bundleCtx;
    
    // Transaction is singleton
    private static Transaction instance;
    private Transaction() {
    }
    public static Transaction createTransaction(String name, BundleContext bundleCtx,  
    		DeploymentSession session, Logger logger) 
    {
        if (null == instance)
            instance = new Transaction();
        instance.name = name;
        instance.bundleCtx = bundleCtx;
        instance.logger = logger;
        instance.session = session;
        instance.steps = null;
        instance.processors = null;
        return instance;
    }
    
    public synchronized void start() {
        steps = new Vector();
        processors = new HashSet();
        logger.log(Logger.LOG_INFO, "Transaction started (name=" + name + ")");
    }

    public synchronized void addRecord(TransactionRecord record) {
        if (PROCESSOR == record.code && !processors.contains(record.wrProc.getPid())) {
            record.wrProc.begin(session);
            processors.add(record.wrProc.getPid());
        }
        
        steps.add(record);
        if (DAConstants.DEBUG)
            logger.log(Logger.LOG_INFO, "Transaction record added (name=" + name + "):\n" + record);
    }
    
    public synchronized void rollback() {
        try {
            if (steps.size() <= 0) {
                logger.log(Logger.LOG_INFO, "Transaction rolled back (name=" + name + ")");
                return;
            }
            
            for (ListIterator iter = steps.listIterator(steps.size()); iter.hasPrevious();) {
                final TransactionRecord element = (TransactionRecord) iter.previous();
                if (DAConstants.DEBUG)
                    logger.log(Logger.LOG_INFO, "Rollback (name=" + name + "):\n" + element);
	            switch (element.code) {
	                case INSTALLBUNDLE : {
	                	try {
	                        AccessController.doPrivileged(new PrivilegedExceptionAction() {
	                            public Object run() throws BundleException {
	                                getBundle(element.bundleId).uninstall();
	                                return null;
	                            }});
	                    }
	                    catch (PrivilegedActionException e) {
	                        throw (BundleException) e.getException();
	                    }
	                    break;
	                } 
	                case UPDATEBUNDLE :
	                	try {
	                        AccessController.doPrivileged(new PrivilegedExceptionAction() {
	                            public Object run() throws BundleException {
	                            	getBundle(element.bundleId).update(element.bis);
	                                return null;
	                            }});
	                    }
	                    catch (PrivilegedActionException e) {
	                        throw (BundleException) e.getException();
	                    }
	                    element.dp.add(element.be);
	                    break;
	                case UNINSTALLBUNDLE : {
	                	try {
	                		final BundleContext ctx = bundleCtx;
	                        AccessController.doPrivileged(new PrivilegedExceptionAction() {
	                            public Object run() throws BundleException {
	                               ctx.installBundle(
	                                    element.be.getLocation(), element.bis);
	                                return null;
	                            }});
	                    }
	                    catch (PrivilegedActionException e) {
	                        throw (BundleException) e.getException();
	                    }
	                    element.dp.add(element.be);
	                    break;
	                } case STARTBUNDLE :
	                	try {
	                        AccessController.doPrivileged(new PrivilegedExceptionAction() {
	                            public Object run() throws BundleException {
	                                getBundle(element.bundleId).stop();
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
	                                getBundle(element.bundleId).start();
	                                return null;
	                            }});
	                    }
	                    catch (PrivilegedActionException e) {
	                        throw (BundleException) e.getException();
	                    }
	                    break;
	                case PROCESSOR:
	                    element.wrProc.rollback();
	                    break;
	                default :
	                    break;
	            }
	        }
        } catch (Exception e) {
            logger.log(Logger.LOG_ERROR, "Error occured during transaction rollback  (name=" + 
                    name + "):\n");
            logger.log(e);
        }
        logger.log(Logger.LOG_INFO, "Transaction rolled back (name=" + name + ")");
    }
    
    private Bundle getBundle(long bundleId) {
    	return bundleCtx.getBundle(bundleId);
    }

}

package org.osgi.impl.service.deploymentadmin;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.ResourceProcessor;

public class Transaction {
    
    public static final int INSTALLBUNDLE   = 0;
    public static final int UPDATEBUNDLE    = 1;
    public static final int UNINSTALLBUNDLE = 2;
    public static final int STARTBUNDLE     = 3;
    public static final int STOPBUNDLE      = 4;
    public static final int PROCESSOR       = 5;
    public static final int FILTERS         = 6;
    public static final int RESOURCES       = 7;
    
    static final String[] transactionCodes = {
        "INSTALLBUNDLE",
        "UPDATEBUNDLE", 
        "UNINSTALLBUNDLE", 
        "STARTBUNDLE", 
        "STOPBUNDLE", 
        "PROCESSOR", 
        "FILTERS",
        "RESOURCES"
    };
    
    private List    steps      = new Vector();
    private HashSet processors = new HashSet();
    private Logger                logger;
    private DeploymentPackageImpl dp;
    
    public Transaction(DeploymentPackageImpl dp, Logger logger) {
        this.dp = dp;
        this.logger = logger;
        logger.log(Logger.LOG_INFO, "Transaction started");
    }

    public void addRecord(TransactionRecord record) {
        if (PROCESSOR == record.code) {
            ResourceProcessor proc = (ResourceProcessor) record.objs[0];
            if (processors.contains(proc)) {
                return;
            }
            else {
                DeploymentPackage dp = (DeploymentPackage) record.objs[1];
                int lce = ((Integer) record.objs[2]).intValue();
                proc.begin(dp, lce);
                processors.add(proc);
            }
        }
        steps.add(record);
        logger.log(Logger.LOG_INFO, "Transaction record added:\n" + record);
    }
    
    public void commit() {
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
	                    ((Bundle) element.objs[0]).uninstall();
	                    dp.stopCustomizerBundle();
	                    break;
	                case STARTBUNDLE :
	                    break;
	                case STOPBUNDLE :
	                    break;
	                case PROCESSOR:
	                    ResourceProcessor proc = (ResourceProcessor) element.objs[0];
	                    proc.complete(true);
	                    break;
	                case FILTERS:
	                    break;
	                case RESOURCES:
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

    public void rollback() {
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
	                    ((Bundle) element.objs[0]).uninstall();
	                    Set bundles = (Set) element.objs[1];
	                    BundleEntry be = (BundleEntry) element.objs[2];
	                    bundles.remove(be);
	                    break;
	                } case UPDATEBUNDLE :
	                    break;
	                case UNINSTALLBUNDLE : {
	                    Set bundles = (Set) element.objs[1];
	                    BundleEntry be = (BundleEntry) element.objs[2];
	                    bundles.add(be);
	                    break;
	                } case STARTBUNDLE :
	                    ((Bundle) element.objs[0]).stop();
	                    break;
	                case STOPBUNDLE :
	                    ((Bundle) element.objs[0]).start();
	                    break;
	                case PROCESSOR:
	                    ResourceProcessor proc = (ResourceProcessor) element.objs[0];
	                    proc.complete(false);
	                    break;
	                case FILTERS:
	                    Hashtable filters = (Hashtable) element.objs[0];
	                    Hashtable filtersBefore = (Hashtable) element.objs[1];
	                    filters = filtersBefore;
	                    break;
	                case RESOURCES:
	                    Set resources = (Set) element.objs[0];
	                    Set resourcesBefore = (Set) element.objs[1];
	                    resources = resourcesBefore;
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

}

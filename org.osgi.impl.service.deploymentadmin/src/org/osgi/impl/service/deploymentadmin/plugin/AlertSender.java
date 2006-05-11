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
package org.osgi.impl.service.deploymentadmin.plugin;

import info.dmtree.DmtData;
import info.dmtree.notification.AlertItem;
import info.dmtree.notification.NotificationService;

import java.security.AccessControlException;

import org.osgi.impl.service.deploymentadmin.Logger;
import org.osgi.impl.service.deploymentadmin.perm.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentException;

public class AlertSender {
	
	private static Logger logger;
	
	public static final void setLogger(Logger logger) {
		AlertSender.logger = logger; 
	}

    static void sendDeployAlert(boolean bundlesNs, Exception exception, String principal, 
            String correlator, String nodeUri, String type, NotificationService nfs) 
    {
        if (null == principal)
            return;

        try {
            if (null == exception)
                nfs.sendNotification(principal, 1226, correlator, new AlertItem[] {
                      new AlertItem(nodeUri, type,
                      null, new DmtData(bundlesNs ? 
                    		  PluginConstants.RESULT_SUCCESSFUL_BUNDLE_START_WARNING : 
                    		  PluginConstants.RESULT_SUCCESSFUL))});
            else {
                if (exception instanceof DeploymentException) {
                    DeploymentException de = (DeploymentException) exception;
                    nfs.sendNotification(principal, 1226, correlator, new AlertItem[] {
                        new AlertItem(nodeUri, type,
                        null, new DmtData(de.getCode()))});
                } else if (exception instanceof AccessControlException) {
                    AccessControlException ae = (AccessControlException) exception;
                    if (ae.getPermission() instanceof DeploymentAdminPermission)
                        nfs.sendNotification(principal, 1226, correlator, new AlertItem[] {
                            new AlertItem(nodeUri, type,
                            null, new DmtData(PluginConstants.RESULT_AUTHORIZATION_FAILURE))});
                }
            }
        } catch (Exception e) {
            logger.log(e);
        }
    }

    static void sendDeploymentRemoveAlert(Exception exception, String principal, 
    		String correlator, String nodeUri, NotificationService nfs) 
    {
    	if (null == principal)
    		return;
    	
    	try {
    		if (null == exception)
    			nfs.sendNotification(principal, 1226, correlator, new AlertItem[] {
    					new AlertItem(nodeUri, "org.osgi.deployment.deployed.remove",
    							null, new DmtData(PluginConstants.RESULT_SUCCESSFUL))});
    		else {
    			if (exception instanceof DeploymentException) {
    				DeploymentException de = (DeploymentException) exception;
    				nfs.sendNotification(principal, 1226, correlator, new AlertItem[] {
    						new AlertItem(nodeUri, "org.osgi.deployment.deployed.remove",
    								null, new DmtData(de.getCode()))});
    			} else if (exception instanceof AccessControlException) {
    				AccessControlException ae = (AccessControlException) exception;
    				if (ae.getPermission() instanceof DeploymentAdminPermission)
    					nfs.sendNotification(principal, 1226, correlator, new AlertItem[] {
    							new AlertItem(nodeUri, "org.osgi.deployment.deployed.remove",
    									null, new DmtData(PluginConstants.RESULT_AUTHORIZATION_FAILURE))});
    			}
    		}
    	} catch (Exception e) {
    		logger.log(e);
    	}
    }
    
    static void sendDeliveredRemoveAlert(boolean success, String principal, String correlator, String nodeUri, 
    		NotificationService nfs) {
    	if (null == principal)
    		return;
    	
    	try {
    		if (success) {
				nfs.sendNotification(principal, 1226, correlator, new AlertItem[] {
						new AlertItem(nodeUri, "org.osgi.deployment.delivered.remove",
								null, new DmtData(PluginConstants.RESULT_SUCCESSFUL))});
    		} else {
    			nfs.sendNotification(principal, 1226, correlator, new AlertItem[] {
    					new AlertItem(nodeUri, "org.osgi.deployment.delivered.remove",
    							null, new DmtData(PluginConstants.RESULT_UNDEFINED_ERROR))});
    		}
    	} catch (Exception e) {
    		logger.log(e);
    	}
    }

	public static void sendDownloadAlert(int status, String principal, String correlator, 
			String nodeUri, NotificationService nfs) {
		if (null == principal)
			return;
		
		try {
			nfs.sendNotification(principal, 1226, correlator, new AlertItem[] {
					new AlertItem(nodeUri, "org.osgi.deployment.downloadandinstallandactivate",
							null, new DmtData(status))});
		} catch (Exception e) {
			logger.log(e);
		}
	}
    
}

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
package org.osgi.impl.service.dmt;

import org.osgi.impl.service.dmt.api.DmtPrincipalPermissionAdmin;
import org.osgi.impl.service.dmt.api.RemoteAlertSender;
import org.osgi.service.dmt.*;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.util.tracker.ServiceTracker;

public class DmtAdminImpl implements DmtAdmin {
    private DmtPrincipalPermissionAdmin dmtPermissionAdmin;
	private DmtPluginDispatcher	dispatcher;
	private EventAdmin eventChannel;
	private ServiceTracker remoteAdapterTracker;

    // OPTIMIZE maybe make some context object to store these references
    public DmtAdminImpl(DmtPrincipalPermissionAdmin dmtPermissionAdmin,
            DmtPluginDispatcher dispatcher, EventAdmin eventChannel,
            ServiceTracker remoteAdapterTracker) {
        this.dmtPermissionAdmin = dmtPermissionAdmin;
        this.dispatcher = dispatcher;
        this.eventChannel = eventChannel;
        this.remoteAdapterTracker = remoteAdapterTracker;
    }

	public DmtSession getSession(String subtreeUri) throws DmtException {
		return getSession(null, subtreeUri, DmtSession.LOCK_TYPE_EXCLUSIVE);
	}

	public DmtSession getSession(String subtreeUri, int lockMode)
			throws DmtException {
		return getSession(null, subtreeUri, lockMode);
	}

	public DmtSession getSession(String principal, String subtreeUri, int lockMode)
			throws DmtException {
        PermissionInfo[] permissions = null;
        if(principal != null)
            permissions = (PermissionInfo[]) 
                dmtPermissionAdmin.getPrincipalPermissions().get(principal);
        
		return new DmtSessionImpl(principal, subtreeUri, lockMode, 
                                  permissions, eventChannel, dispatcher);
	}

    public void sendAlert(String principal, int code, DmtAlertItem[] items)
            throws DmtException {
        RemoteAlertSender alertSender = getAlertSender(principal);
        if (alertSender == null) {
            if (principal == null)
                throw new DmtException(null, DmtException.ALERT_NOT_ROUTED,
                        "Remote adapter not found or is not "
                                + "unique, cannot route alert without "
                                + "principal name.");
            throw new DmtException(null, DmtException.ALERT_NOT_ROUTED,
                    "Cannot find remote adapter that can send "
                            + "the alert to server '" + principal + "'.");
        }
        
        try {
            alertSender.sendAlert(principal, code, items);
        }
        catch (Exception e) {
            String message = "Error sending remote alert";
            if (principal != null)
                message = message + " to server '" + principal + "'";
            throw new DmtException(null, DmtException.REMOTE_ERROR, message
                    + ".", e);
        }
    }
    
    private RemoteAlertSender getAlertSender(String principal) {
        Object[] alertSenders = remoteAdapterTracker.getServices();
        
        if (principal == null) { // return adapter if unique and accepts anything
            if(alertSenders.length != 1)
                return null;
            RemoteAlertSender alertSender = (RemoteAlertSender) alertSenders[0];
            return alertSender.acceptServerId(null) ? alertSender : null;
        }
            
        // find adapter that accepts alerts for the given principal
        for(int i = 0; i < alertSenders.length; i++) {
            RemoteAlertSender alertSender = (RemoteAlertSender) alertSenders[i];
            if (alertSender.acceptServerId(principal))
                return alertSender;
        }
        
        return null;
    }
}

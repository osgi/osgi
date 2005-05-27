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

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.osgi.impl.service.dmt.api.DmtPrincipalPermissionAdmin;
import org.osgi.impl.service.dmt.api.RemoteAlertSender;
import org.osgi.service.dmt.*;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.util.tracker.ServiceTracker;

public class DmtAdminImpl implements DmtAdmin {
    // timeout after 10 seconds, to make testing easier 
    private static final long TIMEOUT = 10000; 
    
    private DmtPrincipalPermissionAdmin dmtPermissionAdmin;
	private DmtPluginDispatcher	dispatcher;
	private ServiceTracker eventTracker;
	private ServiceTracker remoteAdapterTracker;
    
    private List openSessions; // a list of DmtSession refs to open sessions

    // OPTIMIZE maybe make some context object to store these references
    public DmtAdminImpl(DmtPrincipalPermissionAdmin dmtPermissionAdmin,
            DmtPluginDispatcher dispatcher, ServiceTracker eventTracker,
            ServiceTracker remoteAdapterTracker) {
        this.dmtPermissionAdmin = dmtPermissionAdmin;
        this.dispatcher = dispatcher;
        this.eventTracker = eventTracker;
        this.remoteAdapterTracker = remoteAdapterTracker;
        
        openSessions = new Vector();
    }

	public DmtSession getSession(String subtreeUri) throws DmtException {
		return getSession(null, subtreeUri, DmtSession.LOCK_TYPE_EXCLUSIVE);
	}

	public DmtSession getSession(String subtreeUri, int lockMode)
			throws DmtException {
		return getSession(null, subtreeUri, lockMode);
	}

	public synchronized DmtSession getSession(String principal,
            String subtreeUri, int lockMode) throws DmtException {
        
        PermissionInfo[] permissions = null;
        if(principal != null)
            permissions = (PermissionInfo[]) 
                dmtPermissionAdmin.getPrincipalPermissions().get(principal);
        
        if(subtreeUri == null)
            subtreeUri = ".";
        
		DmtSessionImpl session = new DmtSessionImpl(principal, subtreeUri,
                lockMode, permissions, eventTracker, dispatcher, this);
                
        // passing the normalized variant of the subtreeUri parameter
		waitUntilNoConflictingSessions(session.getRootUri(), lockMode);
		session.open();
        openSessions.add(session); 
        
        // it must be ensured that releaseSession is called for each session
        // that is added to the list, otherwise threads might get stuck
        return session;
	}

    // Note, that this does not provide fair scheduling of waiting sessions,
    // the order of sessions depends on the order of thread activation when
    // notifyAll is called.  Some threads may be "starved", i.e. timed out.
    private void waitUntilNoConflictingSessions(String subtreeUri, int lockMode) 
            throws DmtException {
        // TODO also check for (non-shared) sessions within the same plugin
        final long timeLimit = System.currentTimeMillis() + TIMEOUT;
        
        while(conflictsWithOpenSessions(subtreeUri, lockMode)) {
            long timeLeft = timeLimit - System.currentTimeMillis();
            
            // throw exception if this session cannot run and time runs out
            if(timeLeft <= 0) 
                throw new DmtException(subtreeUri, DmtException.TIMEOUT,
                        "Session creation timed out because of concurrent " +
                        "sessions blocking access to Device Management Tree.");
            
            try {
                wait(timeLeft);
            } catch(InterruptedException e) {}
            
            // if wait() terminated because of timeout, then there must still
            // be a conflict (if the code works as it should)
        }
    }
    
    private boolean conflictsWithOpenSessions(String subtreeUri, int lockMode) {
        boolean shared = (lockMode == DmtSession.LOCK_TYPE_SHARED);
        
        Iterator i = openSessions.iterator();
        while (i.hasNext()) {
            DmtSession openSession = (DmtSession) i.next();
            if(Utils.isOnSameBranch(subtreeUri, openSession.getRootUri()) && 
                    (lockMode != DmtSession.LOCK_TYPE_SHARED || 
                     openSession.getLockType() != DmtSession.LOCK_TYPE_SHARED))
                return true; // only two shared sessions can be on one branch
        }
        return false;
    }

    synchronized void releaseSession(DmtSession session) {
        if(!openSessions.remove(session)) // TODO write log message instead
            System.out.println("Session release notification from unknown session!");
        
        notifyAll(); // wake all waiting sessions, and reevaluate conflicts
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

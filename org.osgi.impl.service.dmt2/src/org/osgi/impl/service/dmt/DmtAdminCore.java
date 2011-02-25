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

import info.dmtree.DmtException;
import info.dmtree.DmtSession;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.osgi.impl.service.dmt.export.DmtPrincipalPermissionAdmin;
import org.osgi.service.log.LogService;
import org.osgi.service.permissionadmin.PermissionInfo;

public class DmtAdminCore {
    // session initiation timeout after 10 seconds, to make testing easier 
    public static final long OPEN_TIMEOUT = 10000;
    
    // session idle timeout: session is invalidated after 5 minutes inactivity
    //public static final long IDLE_TIMEOUT = 300000;
    // half-minute idle timeout for demonstration purposes 
    public static final long IDLE_TIMEOUT = 30000;
    
    private Context context;
    private DmtPrincipalPermissionAdmin dmtPermissionAdmin;
    
    private List openSessions; // a list of DmtSession refs to open sessions


	public DmtAdminCore(DmtPrincipalPermissionAdmin dmtPermissionAdmin,
            Context context) {

        this.context = context;
		this.dmtPermissionAdmin = dmtPermissionAdmin;
		
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
        checkLockMode(lockMode);
        
        PermissionInfo[] permissions = null;
        if(principal != null)
            permissions = (PermissionInfo[]) 
                dmtPermissionAdmin.getPrincipalPermissions().get(principal);
        
        if(subtreeUri == null)
            subtreeUri = ".";
        
		SessionWrapper session = new SessionWrapper(principal, subtreeUri,
                lockMode, permissions, context, this);
                
        // passing the normalized variant of the subtreeUri parameter
		waitUntilNoConflictingSessions(session.getRootNode(), lockMode);
		session.open();
        openSessions.add(session); 
        
        // it must be ensured that releaseSession is called for each session
        // that is added to the list, otherwise threads might get stuck
        return session;
	}

    private void checkLockMode(int lockMode) throws DmtException {
        if (lockMode != DmtSession.LOCK_TYPE_SHARED
                && lockMode != DmtSession.LOCK_TYPE_EXCLUSIVE
                && lockMode != DmtSession.LOCK_TYPE_ATOMIC)
            throw new DmtException((String) null, DmtException.COMMAND_FAILED, 
                    "Unknown lockMode '" + lockMode + "' specified.");
    }
    
    // Note, that this does not provide fair scheduling of waiting sessions,
    // the order of sessions depends on the order of thread activation when
    // notifyAll is called.  Some threads may be "starved", i.e. timed out.
    private void waitUntilNoConflictingSessions(Node subtreeNode, int lockMode) 
            throws DmtException {
        final long timeLimit = System.currentTimeMillis() + OPEN_TIMEOUT;
        
        while(conflictsWithOpenSessions(subtreeNode, lockMode)) {
            long timeLeft = timeLimit - System.currentTimeMillis();
            
            // throw exception if this session cannot run and time runs out
            if(timeLeft <= 0) 
                throw new DmtException(subtreeNode.getUri(), 
                        DmtException.SESSION_CREATION_TIMEOUT,
                        "Session creation timed out because of concurrent " +
                        "sessions blocking access to Device Management Tree.");
            
            try {
                wait(timeLeft);
            } catch(InterruptedException e) {}
            
            // if wait() terminated because of timeout, then there must still
            // be a conflict (if the code works as it should)
        }
    }
    
    private boolean conflictsWithOpenSessions(Node subtreeNode, int lockMode) {
        Iterator i = openSessions.iterator();
        while (i.hasNext()) {
            DmtSessionImpl openSession = (DmtSessionImpl) i.next();
            if(subtreeNode.isOnSameBranch(openSession.getRootNode()) && 
                    (lockMode != DmtSession.LOCK_TYPE_SHARED || 
                     openSession.getLockType() != DmtSession.LOCK_TYPE_SHARED))
                return true; // only two shared sessions can be on one branch
        }
        return false;
    }

    synchronized void releaseSession(DmtSession session) {
        if(!openSessions.remove(session))
            context.log(LogService.LOG_INFO, "Session release notification " +
                    "from unknown session!", null);
        
        notifyAll(); // wake all waiting sessions, and reevaluate conflicts
    }
}

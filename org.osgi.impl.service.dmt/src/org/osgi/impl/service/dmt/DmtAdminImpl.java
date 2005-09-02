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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.dmt.api.DmtPrincipalPermissionAdmin;
import org.osgi.service.dmt.*;
import org.osgi.service.log.LogService;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.util.tracker.ServiceTracker;

public class DmtAdminImpl implements DmtAdmin {
    // the name of the system property containing the URI segment length limit 
    public static final String SEGMENT_LENGTH_LIMIT_PROPERTY = 
        "osgi.ri.uri.limits.segmentlength";
    
    // the smallest valid value for the URI segment length limit
    public static final int MINIMAL_SEGMENT_LENGTH_LIMIT = 32;

    // session initiation timeout after 10 seconds, to make testing easier 
    public static final long OPEN_TIMEOUT = 10000;
    
    // session idle timeout: session is invalidated after 5 minutes inactivity
    public static final long IDLE_TIMEOUT = 300000;
    // half-minute idle timeout for demonstration purposes 
    //public static final long IDLE_TIMEOUT = 30000;
    
    // contains the maximum length of node names or 0 if there is no limit
    static final int segmentLengthLimit;

    static {
        String limitString = System.getProperty(SEGMENT_LENGTH_LIMIT_PROPERTY);
        int limit = MINIMAL_SEGMENT_LENGTH_LIMIT; // min. used as default
        
        try {
            int limitInt = Integer.parseInt(limitString);
            if(limitInt == 0 || limitInt >= MINIMAL_SEGMENT_LENGTH_LIMIT)
                limit = limitInt;
        } catch(NumberFormatException e) {}
        
        segmentLengthLimit = limit;
    }
    
    private static final char base64table[] = {
        'A','B','C','D','E','F','G','H',
        'I','J','K','L','M','N','O','P',
        'Q','R','S','T','U','V','W','X',
        'Y','Z','a','b','c','d','e','f',
        'g','h','i','j','k','l','m','n',
        'o','p','q','r','s','t','u','v',
        'w','x','y','z','0','1','2','3',
        '4','5','6','7','8','9','+','_', // !!! this differs from base64
    };
    
    private DmtPrincipalPermissionAdmin dmtPermissionAdmin;
    private Context context;
    
    private List openSessions; // a list of DmtSession refs to open sessions

	private MessageDigest md;

    
	public DmtAdminImpl(DmtPrincipalPermissionAdmin dmtPermissionAdmin,
			Context context) throws NoSuchAlgorithmException
	{
		this.dmtPermissionAdmin = dmtPermissionAdmin;
		this.context = context;
		
		openSessions = new Vector();
		
		md = MessageDigest.getInstance("SHA");
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
    
    public String mangle(String nodeName) {
        if(nodeName == null)
            throw new NullPointerException(
                    "The 'nodeName' parameter must not be null.");
            
        if(nodeName.equals(""))
            throw new IllegalArgumentException(
                    "The 'nodeName' parameter must not be empty.");        

        if(segmentLengthLimit > 0 && nodeName.length() > segmentLengthLimit)
            // create node name hash
			return getHash(nodeName);

		// escape any '/' and '\' characters in the node name
		StringBuffer nameBuffer = new StringBuffer(nodeName);
		for(int i = 0; i < nameBuffer.length(); i++) // 'i' can increase in loop
		    if(nameBuffer.charAt(i) == '\\' || nameBuffer.charAt(i) == '/')
		        nameBuffer.insert(i++, '\\');
        
        return nameBuffer.toString();
    }
    
	private String getHash(String from) {
		byte[] byteStream;
		try {
			byteStream = from.getBytes("UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			// There's no way UTF-8 encoding is not implemented...
			throw new IllegalStateException("there's no UTF-8 encoder here!");
		}
		byte[] digest = md.digest(byteStream);
		
		// very dumb base64 encoder code. There is no need for multiple lines
		// or trailing '='-s....
		// also, we hardcoded the fact that sha-1 digests are 20 bytes long
		StringBuffer sb = new StringBuffer(digest.length*2);
		for(int i=0;i<6;i++) {
			int d0 = digest[i*3]&0xff;
			int d1 = digest[i*3+1]&0xff;
			int d2 = digest[i*3+2]&0xff;
			sb.append(base64table[d0>>2]);
			sb.append(base64table[(d0<<4|d1>>4)&63]);
			sb.append(base64table[(d1<<2|d2>>6)&63]);
			sb.append(base64table[d2&63]);
		}
		int d0 = digest[18]&0xff;
		int d1 = digest[19]&0xff;
		sb.append(base64table[d0>>2]);
		sb.append(base64table[(d0<<4|d1>>4)&63]);
		sb.append(base64table[(d1<<2)&63]);
		
		return sb.toString();
	}

    public void sendAlert(String principal, int code, String correlator,
            AlertItem[] items) throws DmtException {
        RemoteAlertSender alertSender = getAlertSender(principal);
        if (alertSender == null) {
            if (principal == null)
                throw new DmtException((String) null,
                        DmtException.ALERT_NOT_ROUTED,
                        "Remote adapter not found or is not unique, cannot " +
                        "route alert without principal name.");
            throw new DmtException((String) null, DmtException.ALERT_NOT_ROUTED,
                    "Cannot find remote adapter that can send the alert to " +
                    "server '" + principal + "'.");
        }
        
        try {
            alertSender.sendAlert(principal, code, correlator, items);
        }
        catch (Exception e) {
            String message = "Error sending remote alert";
            if (principal != null)
                message = message + " to server '" + principal + "'";
            throw new DmtException((String) null, DmtException.REMOTE_ERROR, 
                    message + ".", e);
        }
    }
    
    private RemoteAlertSender getAlertSender(String principal) {
        ServiceTracker remoteAdapterTracker = 
            context.getTracker(RemoteAlertSender.class);
        if (principal == null) { // return adapter if it is unique 
            Object[] alertSenders = remoteAdapterTracker.getServices();
            if(alertSenders == null || alertSenders.length != 1)
                return null;
            
            return (RemoteAlertSender) alertSenders[0];
        }
       
        ServiceReference[] alertSenderRefs = 
            remoteAdapterTracker.getServiceReferences(); 
        
        if(alertSenderRefs == null)
            return null;
        
        ServiceReference bestRef = null;
        
        // find best adapter that accepts alerts for the given principal
        for(int i = 0; i < alertSenderRefs.length; i++)
            if(acceptsServerId(alertSenderRefs[i], principal))
                bestRef = betterRef(alertSenderRefs[i], bestRef);

        if(bestRef == null)
            return null;
        
        // return service object for the best reference
        // can still be null if service was unregistered in the meantime
        return (RemoteAlertSender) remoteAdapterTracker.getService(bestRef);
    }
    
    // precondition: parameters are not null
    private boolean acceptsServerId(ServiceReference ref, String principal) {
        Object param = ref.getProperty("servers");
        if(param == null || !(param instanceof String[]))
            return false;

        String[] servers = (String[]) param;
        for (int i = 0; i < servers.length; i++)
            if(principal.equals(servers[i]))
                return true;
        
        return false;
    }
    
    private ServiceReference betterRef(ServiceReference ref, 
                                       ServiceReference best) {
        if(best == null)
            return ref;
       
        int refRank = getRanking(ref);
        int bestRank = getRanking(best);
        
        if(refRank != bestRank)
            return refRank > bestRank ? ref : best;
        
        return getId(ref) < getId(best) ? ref : best;
    }
    
    private int getRanking(ServiceReference ref) {
        Object property = ref.getProperty(Constants.SERVICE_RANKING);
        // a ranking of 0 must be assumed if property is missing or invalid
        if(property == null || !(property instanceof Integer))
            return 0;
        return ((Integer) property).intValue();
    }
    
    private long getId(ServiceReference ref) {
        // this property must be guaranteed to be set by the framework
        return ((Long) ref.getProperty(Constants.SERVICE_ID)).longValue();
    }
}

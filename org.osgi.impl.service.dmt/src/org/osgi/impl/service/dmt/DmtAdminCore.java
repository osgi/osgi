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



import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.dmt.dispatcher.MappingListener;
import org.osgi.impl.service.dmt.dispatcher.Util;
import org.osgi.impl.service.dmt.export.DmtPrincipalPermissionAdmin;
import org.osgi.service.dmt.DmtEvent;
import org.osgi.service.dmt.DmtEventListener;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.Uri;
import org.osgi.service.dmt.security.DmtPermission;
import org.osgi.service.log.LogService;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This core class tracks and checks registrations for DmtEventListeners now.
 * @author steffen
 *
 */
public class DmtAdminCore
		extends ServiceTracker<DmtEventListener,DmtEventListener>
		implements MappingListener {

    private static final int ALL_EVENT_TYPES =
        DmtEvent.ADDED | DmtEvent.COPIED | DmtEvent.DELETED | DmtEvent.RENAMED |
        DmtEvent.REPLACED | DmtEvent.SESSION_CLOSED | DmtEvent.SESSION_OPENED;
	private static final long MINIMUM_OPEN_TIMEOUT = 10000;
	private static final long MINIMUM_IDLE_TIMEOUT = 30000;
	private static final String FILTER_DMT_EVENT_LISTENER = 
		"(&" + 
			"(" + Constants.OBJECTCLASS + "=" + DmtEventListener.class.getName()+")" +
			"(" + DmtEventListener.FILTER_EVENT + "=*)" +
			"(" + DmtEventListener.FILTER_SUBTREE + "=*)" +
		")";
	
	protected static final String SESSION_INACTIVE_TIMEOUT = "60000";
	protected static final String SESSION_CREATION_TIMEOUT = "10000";
	private static long sessionOpenTimeout = -1;
	private static long sessionIdleTimeout = -1;
    
    // all currently registered DmtEventListener refs matching the filter above
	protected Set<ServiceReference<DmtEventListener>>	dmtEventListenerRefs;

	@SuppressWarnings("hiding")
	private Context										context;
    private DmtPrincipalPermissionAdmin dmtPermissionAdmin;
    
    private List<SessionWrapper> openSessions; // a list of DmtSession refs to open sessions

	public DmtAdminCore(DmtPrincipalPermissionAdmin dmtPermissionAdmin,
            Context context) throws InvalidSyntaxException {
		super(context.getBundleContext(), context.getBundleContext().createFilter(FILTER_DMT_EVENT_LISTENER), null);
		super.open();
        this.context = context;
		this.dmtPermissionAdmin = dmtPermissionAdmin;
		
		openSessions = new Vector<>();
	}

	public DmtSession getSession(String subtreeUri, Bundle initiatingBundle) throws DmtException {
		return getSession(null, subtreeUri, DmtSession.LOCK_TYPE_EXCLUSIVE, initiatingBundle);
	}

	public DmtSession getSession(String subtreeUri, int lockMode, Bundle initiatingBundle)
			throws DmtException {
		return getSession(null, subtreeUri, lockMode, initiatingBundle);
	}

	public synchronized DmtSession getSession(String principal,
            String subtreeUri, int lockMode, Bundle initiatingBundle) throws DmtException {
		
        checkLockMode(lockMode);
        
        PermissionInfo[] permissions = null;
        if(principal != null)
            permissions = dmtPermissionAdmin.getPrincipalPermissions().get(principal);
        
        if(subtreeUri == null)
            subtreeUri = ".";
        
		SessionWrapper session = new SessionWrapper(principal, subtreeUri,
                lockMode, permissions, context, this, initiatingBundle);
                
        // passing the normalized variant of the subtreeUri parameter
		waitUntilNoConflictingSessions(session.getRootNode(), lockMode);
		session.open();
        openSessions.add(session); 
        
        // it must be ensured that releaseSession is called for each session
        // that is added to the list, otherwise threads might get stuck
        return session;
	}

	/**
	 * a DmtEventListener was added
	 */
	@Override
	public DmtEventListener addingService(
			ServiceReference<DmtEventListener> ref) {
		if ( isValidDmtEventListener(ref) )
			getDmtEventListenerRefs().add(ref);
		return context.getBundleContext().getService(ref);
	}

	/**
	 * a DmtEventListener was modified
	 */
	@Override
	public void modifiedService(ServiceReference<DmtEventListener> ref,
			DmtEventListener service) {
		if ( isValidDmtEventListener(ref) )
			getDmtEventListenerRefs().add(ref);
		else
			getDmtEventListenerRefs().remove(ref);
	}

	/**
	 * a DmtEventListener was removed
	 */
	@Override
	public void removedService(ServiceReference<DmtEventListener> ref,
			DmtEventListener service) {
		getDmtEventListenerRefs().remove(ref);
		context.getBundleContext().ungetService(ref);
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
        final long timeLimit = System.currentTimeMillis() + getSessionCreationTimeout();
        
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
			} catch (InterruptedException e) {
				// ignore
			}
            
            // if wait() terminated because of timeout, then there must still
            // be a conflict (if the code works as it should)
        }
    }
    
    private boolean conflictsWithOpenSessions(Node subtreeNode, int lockMode) {
		Iterator<SessionWrapper> i = openSessions.iterator();
        while (i.hasNext()) {
            DmtSessionImpl openSession = i.next();
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
    

	long getSessionCreationTimeout() {
		if ( sessionOpenTimeout == -1) {
			sessionOpenTimeout = AccessController
					.doPrivileged(new PrivilegedAction<Long>() {
						@Override
						public Long run() {
							String limitString = SESSION_CREATION_TIMEOUT;
							long limit = MINIMUM_OPEN_TIMEOUT; // min.
																			// used
																			// as
																			// default

							try {
								long limitLong = Long.parseLong(limitString);
								if (limitLong >= MINIMUM_OPEN_TIMEOUT)
									limit = limitLong;
							}
							catch (NumberFormatException e) {
								// ignore
							}

							return Long.valueOf(limit);
						}
					})
					.longValue();
		}
		return sessionOpenTimeout;
	}

	long getSessionInactivityTimeout() {
		if ( sessionIdleTimeout == -1) {
			sessionIdleTimeout = AccessController
					.doPrivileged(new PrivilegedAction<Long>() {
						@Override
						public Long run() {
							String limitString = System
									.getProperty(SESSION_INACTIVE_TIMEOUT);
							long limit = MINIMUM_IDLE_TIMEOUT; // min.
																			// used
																			// as
																			// default

							try {
								long limitLong = Long.parseLong(limitString);
								if (limitLong >= MINIMUM_IDLE_TIMEOUT)
									limit = limitLong;
							}
							catch (NumberFormatException e) {
								// ignore
							}

							return Long.valueOf(limit);
						}
					})
					.longValue();
		}
		return sessionIdleTimeout;
	}
	
    void dispatchEvent(final DmtEventCore event) {

		Set<ServiceReference<DmtEventListener>> refs = getDmtEventListenerRefs();
		for (ServiceReference<DmtEventListener> ref : refs) {

    		// there are only valid refs at this point, so we don't need type-checks anymore
    		int type = ((Integer) ref.getProperty(DmtEventListener.FILTER_EVENT)).intValue();
    		
    		// type check
    		if ( (event.getType() & type) == 0 )
    			continue;

    		// subtree check
    		Collection<String> subtrees = Util.toCollection(ref.getProperty(DmtEventListener.FILTER_SUBTREE));
    		Collection<String> principals = Util.toCollection(ref.getProperty(DmtEventListener.FILTER_PRINCIPAL));
    		// Acl filtering is performed during initialization of the DmtEventImpl
    		final DmtEventImpl dmtEvent = new DmtEventImpl(event, principals);

    		if ( event.getType() == DmtEvent.SESSION_OPENED || event.getType() == DmtEvent.SESSION_CLOSED ) {
    			// these events don't have nodes or newNodes
    			// ensure that session.rooturi is part of at least one subtree
	    		boolean subtreeMatch = false;
    			Node sessionRoot = new Node( Uri.toPath((String) event.getProperty("session.rooturi")));
    			for (String subtree : subtrees) {
					if (sessionRoot
							.isAncestorOf(new Node(Uri.toPath(subtree)))) {
    					subtreeMatch = true;
    					break;
    				}
    			}
    			if ( ! subtreeMatch )
    				continue;
    			
    			// check permission against session.rooturi
	    		if ( ! hasGetPermission(ref, new String[] {sessionRoot.getUri()}) )
	    			continue;
    			
    		}
    		else {
    		
	    		boolean subtreeMatch = false;
	    		for (String subtree : subtrees ) {
	    			if ( event.containsNodeUnderRoot(new Node(Uri.toPath(subtree))) ) {
	    				// one match is sufficient
	    				subtreeMatch = true;
	    				break;
	    			}
	    		}
	    		if ( ! subtreeMatch )
	    			continue;
	
	    		// according to spec 2.0 empty nodes are allowed now
//	    		// are there still nodes left after Acl filtering ?
//	    		if (dmtEvent.getNodes().length == 0 )
//	    			continue;
	    		
	    		// permission checks (receiving bundle must have GET permission for EACH node/newnode)
	    		if ( ! hasGetPermission(ref, dmtEvent.getNodes()) )
	    			continue;
	    		if ( ! hasGetPermission(ref, dmtEvent.getNewNodes()) )
	    			continue;
    		}

    		// checks are OK, we can deliver the event
    		final DmtEventListener listener = context.getBundleContext().getService(ref);
            try {
				AccessController.doPrivileged(new PrivilegedAction<Void>() {
                    @Override
					public Void run() {
                        listener.changeOccurred(dmtEvent);
                        return null;
                    }
                });
            } catch(RuntimeException e) {
                context.log(LogService.LOG_WARNING, "DmtEventListener " +
                        "threw a " +
                        "RuntimeException while in a callback.", e);
            }
            context.getBundleContext().ungetService(ref);
    	}
    }

    /**
     * checks DmtPermission for given nodes and the bundle that belongs to the ref
     * @param ref ... the ServiceReference to get the Bundle from
     * @param nodes ... the node uris to check
     * @return ... true, only if the bundle has GET permission for ALL nodes
     */
	private boolean hasGetPermission(ServiceReference< ? > ref,
			String[] nodes) {
		boolean hasPermission = true;
		if ( nodes != null ) {
			for (String nodeUri : nodes) {
				if ( ! ref.getBundle().hasPermission(new DmtPermission(nodeUri, DmtPermission.GET))) {
					// one uri without permission is enough to stop 
					hasPermission = false;
					break;
				}
			}
		}
		return hasPermission;
	}
    

	// lazy getter
	private Set<ServiceReference<DmtEventListener>> getDmtEventListenerRefs() {
		if (dmtEventListenerRefs == null)
			dmtEventListenerRefs = new HashSet<>();
		return dmtEventListenerRefs;
	}
	
	private boolean isValidDmtEventListener(
			ServiceReference<DmtEventListener> ref) {
    	int type = -1;
    	try {
    		type = ((Integer) ref.getProperty(DmtEventListener.FILTER_EVENT)).intValue();
    		if((type & ~ALL_EVENT_TYPES) != 0) {
    			context.log(LogService.LOG_ERROR, "Type parameter contains bits " +
    	                "that do not correspond to any event type.", null);
                return false;
    		}
    		
    		// rootUri's are mandatory
    		Collection<String> subtrees = Util.toCollection( ref.getProperty(DmtEventListener.FILTER_SUBTREE));
    		if ( subtrees == null ) {
    			context.log(LogService.LOG_ERROR, "The subtree property of the registered DmtEventListener is not of type String+", null);
    			return false;
    		}

    		// check validity of the root-uris
    		for (String subtree : subtrees) {
    			Node node = Node.validateAndNormalizeUri(subtree);
    			if ( !node.isAbsolute() ) {
        			context.log(LogService.LOG_ERROR, "The subtree property of the registered DmtEventListener holds invalid uris", null);
    				return false;
    			}
    		}
    		
    	}
    	catch (Exception x ) {
    		return false;
    	}

    	// registration seems valid, security checks happen before delivery
        return true;
    }

    /**
     * Gets a callback from the Dispatcher whenever some mapping has changed.
     * Checks if any session is tainted by this change and invalidates such sessions immediately.
     */
	@Override
	public synchronized void pluginMappingChanged(String pluginRoot,
			ServiceReference< ? > ref) {
		if ( pluginRoot == null || pluginRoot.length() == 0 )
			return;
		SessionWrapper[] sessions = openSessions.toArray(new SessionWrapper[openSessions.size()]);
		for (SessionWrapper session : sessions) {
			if ( pluginRoot.startsWith(session.getRootUri() ))
				session.invalidateSession(true, false, 
						new DmtException(pluginRoot, DmtException.CONCURRENT_ACCESS, "Changed plugin (un-)mapping affects this session --> session is invalidated!"));
					
		}
	}

}

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
import java.lang.reflect.Constructor;
import java.security.*;
import java.util.*;
import org.osgi.service.dmt.*;
import org.osgi.service.dmt.security.*;
import org.osgi.service.dmt.spi.*;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.util.tracker.ServiceTracker;

// Needed for meta-data name and value pattern matching
//import java.util.regex.Pattern;

// OPTIMIZE node handling (e.g. retrieve plugin from dispatcher only once per API call), maybe with new URI class
// OPTIMIZE only retrieve meta-data once per API call
// OPTIMIZE only call commit/rollback for plugins that were actually modified since the last transaction boundary
public class DmtSessionImpl implements DmtSession {
    
    private static final int SHOULD_NOT_EXIST   = 0;
	private static final int SHOULD_EXIST       = 1;
	private static final int SHOULD_BE_LEAF     = 2; // implies SHOULD_EXIST
	private static final int SHOULD_BE_INTERIOR = 3; // implies SHOULD_EXIST
    
	private static final Class[] PERMISSION_CONSTRUCTOR_SIG = 
        new Class[] { String.class, String.class };

	private static Hashtable acls;
    
    // Stores the ACL table at the start of each transaction in an atomic
    // session.  Can be static because atomic session cannot run in parallel.
    private static Hashtable savedAcls;
    
	static {
		acls = new Hashtable();
		acls.put(".", new Acl("Add=*&Get=*&Replace=*"));
	}
	
    private final AccessControlContext securityContext;
    private final PluginDispatcher     dispatcher;
    private final ServiceTracker       eventTracker;
    private final DmtAdminImpl         dmtAdmin;

    private final String principal;
    private final String subtreeUri;
    private final int    lockMode;
    private final int    sessionId;
    
    private EventList eventList;
	private Map       dataPlugins;
    private int       state;
    
    // Session creation is done in two phases: 
    // - DmtAdmin creates a new DmtSessionImpl instance (this should indicate
    //   as many errors as possible, but must not call any plugins)
    // - when all conflicting sessions have been closed, DmtAdmin calls "open()"
    //   to actually open the session for external use
	DmtSessionImpl(String principal, String subtreeUri, int lockMode,
	               PermissionInfo[] permissions, ServiceTracker eventTracker, 
                   PluginDispatcher dispatcher, DmtAdminImpl dmtAdmin) 
            throws DmtException {
        
        subtreeUri = Utils.validateAndNormalizeAbsoluteUri(subtreeUri);
		
        this.principal = principal;
		this.subtreeUri = subtreeUri;
        this.lockMode = lockMode;
		this.dispatcher = dispatcher;
        this.eventTracker = eventTracker;
        this.dmtAdmin = dmtAdmin;
        
        if(principal != null) { // remote session
            SecurityManager sm = System.getSecurityManager();
            if(sm != null)
                sm.checkPermission(new DmtPrincipalPermission(principal));
            
            try {
                securityContext = getSecurityContext(permissions);
            } catch(Exception e) {
                throw new DmtException(subtreeUri, DmtException.COMMAND_FAILED, 
                        "Unable to create Protection Domain for remote server.", 
                        e);
            }
        } else
            securityContext = null;
        
        if(lockMode == LOCK_TYPE_ATOMIC)
        	eventList = new EventList();
        
        sessionId = (new Long(System.currentTimeMillis())).hashCode();
        dataPlugins = Collections.synchronizedMap(new HashMap());
        state = STATE_CLOSED;
	}
    
    // called directly before returning the session object in getSession()
    // throws NODE_NOT_FOUND if the previously specified root does not exist
    void open() throws DmtException {
        if(lockMode == LOCK_TYPE_ATOMIC)
            // shallow copy is enough, URIs and Acls are immutable 
            savedAcls = (Hashtable) acls.clone();
        
        state = STATE_OPEN;
        
		// after everything is initialized, check with the plugins whether the
		// given node really exists
		checkNode(subtreeUri, SHOULD_EXIST);
    }

    // called by the Session Wrapper, rollback parameter is:
    // - true if a fatal exception has been thrown in a DMT access method
    // - false if any exception has been thrown in the commit/rollback methods
    protected void invalidateSession(boolean rollback) {
        state = STATE_INVALID;
        
        if(lockMode == LOCK_TYPE_ATOMIC && rollback) {
            try {
                rollbackPlugins();
            } catch(DmtException e) {
                // TODO
            }
        }
        
        try {
            closeAndRelease(false);
        } catch(DmtException e) {
            // TODO
        }
    }
    
    /* These methods can be called even before the session has been opened, and 
     * also after the session has been closed. */
    
    public synchronized int getState() {
        return state;
    }
    
	public String getPrincipal() {
		return principal;
	}

	public int getSessionId() {
		return sessionId;
	}

	public String getRootUri() {
		return subtreeUri;
	}
    
    public int getLockType() {
        return lockMode;
    }
    
    public String mangle(String nodeName) {
        return dmtAdmin.mangle(nodeName);
    }
    
    /* These methods are only meaningful in the context of an open session. */

    // no other API methods can be called while this method is executed 
	public synchronized void close() throws DmtException {
        checkSession();
        
        // changed to CLOSED if this method finishes without error
        state = STATE_INVALID; 

        closeAndRelease(lockMode == LOCK_TYPE_ATOMIC);
        
        state = STATE_CLOSED;
	}
    
    private void closeAndRelease(boolean commit) throws DmtException {
        try {
            if(commit)
                commitPlugins();
            closePlugins();
        } finally {
            // DmtAdmin must be notified that this session has ended, otherwise
            // other sessions might never be allowed to run
            dmtAdmin.releaseSession(this);
        }
    }
    
    private void closePlugins() throws DmtException {
        Vector closeExceptions = new Vector();
        // this block requires synchronization
        Iterator i = dataPlugins.values().iterator();
        while (i.hasNext()) {
            try {
                ((PluginSessionWrapper) i.next()).close();
            } catch(Exception e) {
                closeExceptions.add(e);
            }
        }
        dataPlugins.clear();

        if (closeExceptions.size() != 0)
			throw new DmtException((String) null, DmtException.COMMAND_FAILED,
					"Some plugins failed to close.", closeExceptions, false);
    }
    
    // TODO close all plugins in case of error (if current op. is not close anyway)
    // no other API methods can be called while this method is executed 
    public synchronized void commit() throws DmtException {
        checkSession();

        if (lockMode != LOCK_TYPE_ATOMIC)
            throw new IllegalStateException("Commit can only be requested " +
                    "for atomic sessions.");
        
        // changed back to OPEN if this method finishes without error
        state = STATE_INVALID; 

        commitPlugins();

        savedAcls = (Hashtable) acls.clone();

        state = STATE_OPEN;
    }
    
    // precondition: lockMode == LOCK_TYPE_ATOMIC
    private void commitPlugins() throws DmtException {
        Vector commitExceptions = new Vector();
        Iterator i = dataPlugins.values().iterator();
        // this block requires synchronization
        while (i.hasNext()) {
            PluginSessionWrapper wrappedPlugin = (PluginSessionWrapper) i.next();
            try {
                // checks transaction support before calling commit on the plugin
                wrappedPlugin.commit();
            } catch(Exception e) {
                purgeEvents(wrappedPlugin.getSessionRoot());
                commitExceptions.add(e);
            }
        }
        
        sendEvent(EventList.ADD);
        sendEvent(EventList.DELETE);
        sendEvent(EventList.REPLACE);
        sendEvent(EventList.RENAME);
        sendEvent(EventList.COPY);
        eventList.clear();
        
        if (commitExceptions.size() != 0)
            throw new DmtException((String) null, 
                    DmtException.TRANSACTION_ERROR, 
                    "Some plugins failed to commit.", 
                    commitExceptions, false);

    }
    
    // TODO close all plugins in case of error 
    // no other API methods can be called while this method is executed 
    public synchronized void rollback() throws DmtException {
		checkSession();

		if (lockMode != LOCK_TYPE_ATOMIC)
			throw new IllegalStateException("Rollback can only be requested " +
                    "for atomic sessions.");
        
        // changed back to OPEN if this method finishes without error
        state = STATE_INVALID; 

        acls = (Hashtable) savedAcls.clone();
		
        rollbackPlugins();
        
		state = STATE_OPEN;
    }
    
    private void rollbackPlugins() throws DmtException {
		eventList.clear();
        
        Vector rollbackExceptions = new Vector();
        // this block requires synchronization
        Iterator i = dataPlugins.values().iterator();
        while (i.hasNext()) {
            try {
                // checks transaction support before calling rollback on the plugin
                ((PluginSessionWrapper) i.next()).rollback();
            } catch(Exception e) {
                rollbackExceptions.add(e);
            }
        }

        if (rollbackExceptions.size() != 0)
			throw new DmtException((String) null, DmtException.ROLLBACK_FAILED,
					"Some plugins failed to roll back or close.",
					rollbackExceptions, false);
    }

    public synchronized void execute(String nodeUri, String data) 
            throws DmtException {
        execute(nodeUri, null, data);
    } 
   
	public synchronized void execute(String nodeUri, final String correlator,
            final String data) throws DmtException {
        checkSession();
        // not allowing to execute non-existent nodes, all Management Objects
        // defined in the spec have data plugins backing them
		final String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
        checkOperation(uri, Acl.EXEC, MetaNode.CMD_EXECUTE);
        
        final ExecPlugin plugin = dispatcher.getExecPlugin(uri);
        final DmtSession session = this;
		
        if (plugin == null)
			throw new DmtException(uri, DmtException.COMMAND_FAILED,
					"No exec plugin registered for given node.");
        
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws DmtException {
                    plugin.execute(session, Utils.tempAbsoluteUriToPath(uri), correlator, data);
                    return null;
                }
            }, securityContext);
        } catch(PrivilegedActionException e) {
            throw (DmtException) e.getException();
        }
	}

    // requires DmtPermission with GET action, no ACL check done because there
    // are no ACLs stored for non-existing nodes (in theory)
	public synchronized boolean isNodeUri(String nodeUri) {
        checkSession();
        
        try {
            String uri = makeAbsoluteUri(nodeUri);
            checkLocalPermission(uri, writeAclCommands(Acl.GET));
            checkNode(uri, SHOULD_EXIST);
            // not checking meta-data for the GET capability, the plugin must be 
            // prepared to answer isNodeUri() even if the node is not "gettable" 
		} catch (DmtException e) {
			return false; // invalid node URI or error opening plugin
		}
        
		return true;
	}

	public synchronized boolean isLeafNode(String nodeUri) throws DmtException {
        checkSession();
		String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
        checkOperation(uri, Acl.GET, MetaNode.CMD_GET);
		return isLeafNodeNoCheck(uri);
	}

	// GET property op
	public synchronized Acl getNodeAcl(String nodeUri) throws DmtException {
        checkSession();
        String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
		checkOperation(uri, Acl.GET, MetaNode.CMD_GET);
        Acl acl = (Acl) acls.get(uri);
		return acl == null ? null : acl;
	}
    
    // GET property op
    public synchronized Acl getEffectiveNodeAcl(String nodeUri)
            throws DmtException {
        checkSession();
        String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
        checkOperation(uri, Acl.GET, MetaNode.CMD_GET);
        return getEffectiveNodeAclNoCheck(uri);
    }

	// REPLACE property op
	public synchronized void setNodeAcl(String nodeUri, Acl acl) 
            throws DmtException {
		checkWriteSession();
		String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
        
		// check for REPLACE permission:
		if (isLeafNodeNoCheck(uri)) // on the parent node for leaf nodes
		    checkNodePermission(Utils.parentUri(uri), Acl.REPLACE);
		else // on the node itself or the parent for interior nodes (parent will
            // be ignored in case of the root node)
		    checkNodeOrParentPermission(uri, Acl.REPLACE);
        
        // Not checking REPLACE capability, node does not have to be modifiable
        // to have an ACL associated with it.  It should be possible to set 
        // ACLs everywhere, and the "Replace" Access Type seems to be given
        // only for modifiable nodes.
        
        if (acl == null || isEmptyAcl(acl))
            acls.remove(uri);
        else {
            // check that the new ACL is valid
            if (uri.equals(".") && !acl.isPermitted("*", Acl.ADD)) 
                // should be 405 "Forbidden" according to DMTND 7.7.1.2
                throw new DmtException(uri, DmtException.COMMAND_NOT_ALLOWED,
                        "Root ACL must allow the Add operation for all principals.");

            acls.put(uri, acl);
        }
        getReadableDataSession(uri).nodeChanged(Utils.tempAbsoluteUriToPath(uri));
        
        enqueueEvent(EventList.REPLACE, uri);
	}

	public synchronized MetaNode getMetaNode(String nodeUri)
            throws DmtException {
        checkSession();
        String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
        checkNodePermission(uri, Acl.GET);
        // not checking meta-data for the GET capability, meta-data should
        // always be publicly available
        return getMetaNodeNoCheck(uri);
    }

	public synchronized DmtData getNodeValue(String nodeUri)
            throws DmtException {
        checkSession();
        String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_BE_LEAF);
        checkOperation(uri, Acl.GET, MetaNode.CMD_GET);
        return getReadableDataSession(uri).getNodeValue(Utils.tempAbsoluteUriToPath(uri));
    }

	public synchronized String[] getChildNodeNames(String nodeUri) 
            throws DmtException {
        checkSession();
        String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_BE_INTERIOR);
        checkOperation(uri, Acl.GET, MetaNode.CMD_GET);
        String[] pluginChildNodes = getReadableDataSession(uri).getChildNodeNames(Utils.tempAbsoluteUriToPath(uri));
        
        List processedChildNodes = normalizeChildNodeNames(pluginChildNodes);
        
        return (String[]) processedChildNodes
                .toArray(new String[processedChildNodes.size()]);
    }
    
	// GET property op
    public synchronized String getNodeTitle(String nodeUri) throws DmtException {
        checkSession();
        String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
        checkOperation(uri, Acl.GET, MetaNode.CMD_GET);
        return getReadableDataSession(uri).getNodeTitle(Utils.tempAbsoluteUriToPath(uri));
    }

	// GET property op
    public synchronized int getNodeVersion(String nodeUri) throws DmtException {
        checkSession();
        String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
        checkOperation(uri, Acl.GET, MetaNode.CMD_GET);
        return getReadableDataSession(uri).getNodeVersion(Utils.tempAbsoluteUriToPath(uri));
    }

	// GET property op
    public synchronized Date getNodeTimestamp(String nodeUri)
            throws DmtException {
        checkSession();
        String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
        checkOperation(uri, Acl.GET, MetaNode.CMD_GET);
        return getReadableDataSession(uri).getNodeTimestamp(Utils.tempAbsoluteUriToPath(uri));
    }

	// GET property op
	public synchronized int getNodeSize(String nodeUri) throws DmtException {
		checkSession();
        String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_BE_LEAF);
        checkOperation(uri, Acl.GET, MetaNode.CMD_GET);
		return getReadableDataSession(uri).getNodeSize(Utils.tempAbsoluteUriToPath(uri));
	}

	// GET property op
    public synchronized String getNodeType(String nodeUri) throws DmtException {
        checkSession();
        String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
        checkOperation(uri, Acl.GET, MetaNode.CMD_GET);
        return getReadableDataSession(uri).getNodeType(Utils.tempAbsoluteUriToPath(uri));
    }

	// REPLACE property op
    public synchronized void setNodeTitle(String nodeUri, String title)
            throws DmtException {
        checkWriteSession();
        internalSetNodeTitle(nodeUri, title, true); // send event if successful
    }
    
    // also used by copy() to set the node title without triggering an event
    private void internalSetNodeTitle(String nodeUri, String title, 
            boolean sendEvent) throws DmtException {
        
        String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
        checkOperation(uri, Acl.REPLACE, MetaNode.CMD_REPLACE);
        
        try {
            if (title != null && title.getBytes("UTF-8").length > 255)
                throw new DmtException(uri, DmtException.COMMAND_FAILED,
                        "Length of Title property exceeds 255 bytes (UTF-8).");
        } catch (UnsupportedEncodingException e) {
            // never happens
        }
        
        getReadWriteDataSession(uri).setNodeTitle(Utils.tempAbsoluteUriToPath(uri), title);
        if(sendEvent)
            enqueueEvent(EventList.REPLACE, uri);
    }

	public synchronized void setNodeValue(String nodeUri, DmtData data)
            throws DmtException {
        commonSetNodeValue(nodeUri, data);
    }
    
    public synchronized void setDefaultNodeValue(String nodeUri)
            throws DmtException {
        commonSetNodeValue(nodeUri, null);
    }
    
    private void commonSetNodeValue(String nodeUri, DmtData data)
            throws DmtException {
        checkWriteSession();
        String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_BE_LEAF);
        checkOperation(uri, Acl.REPLACE, MetaNode.CMD_REPLACE);
        checkValue(uri, data);

        MetaNode metaNode = getMetaNodeNoCheck(uri);
        if (metaNode != null && metaNode.getScope() == MetaNode.PERMANENT)
            throw new DmtException(uri, DmtException.METADATA_MISMATCH,
                    "Cannot set the value of a permanent node.");
        
        getReadWriteDataSession(uri).setNodeValue(Utils.tempAbsoluteUriToPath(uri), data);
        enqueueEvent(EventList.REPLACE, uri);        
    }
    
    // SyncML DMTND 7.5 (p16) Type: only the Get command is applicable!
    public synchronized void setNodeType(String nodeUri, String type)
            throws DmtException {
        checkWriteSession();
        String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
        checkOperation(uri, Acl.REPLACE, MetaNode.CMD_REPLACE);
        
        MetaNode metaNode = getMetaNodeNoCheck(uri);
        if (metaNode != null && metaNode.getScope() == MetaNode.PERMANENT)
            throw new DmtException(uri, DmtException.METADATA_MISMATCH,
                    "Cannot set type property of permanent node.");

        if(isLeafNodeNoCheck(uri))
            checkMimeType(uri, type);
        
        // could check type string for interior nodes, but this impl. does not 
        // handle it anyway, so we leave it to the plugins if they need it
        // (same in createInteriorNode/2)

        getReadWriteDataSession(uri).setNodeType(Utils.tempAbsoluteUriToPath(uri), type);
        enqueueEvent(EventList.REPLACE, uri);
    }

	public synchronized void deleteNode(String nodeUri) throws DmtException {
		checkWriteSession();
        String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
		checkOperation(uri, Acl.DELETE, MetaNode.CMD_DELETE);
        
        if(Utils.parentUri(uri) == null)
            throw new DmtException(uri, DmtException.COMMAND_NOT_ALLOWED,
                    "Cannot delete root node.");

        MetaNode metaNode = getMetaNodeNoCheck(uri);
		if (metaNode != null) {
		    if(metaNode.getScope() == MetaNode.PERMANENT)
		        throw new DmtException(uri, DmtException.METADATA_MISMATCH,
		            "Cannot delete permanent node.");
            
            if(!metaNode.isZeroOccurrenceAllowed()) {
                // maxOccurrence == 1 means that there cannot be other instances
                // of this node, so it cannot be deleted.  If maxOccurrence > 1 
                // then we have to check whether this is the last one.
                if(metaNode.getMaxOccurrence() == 1 
                        || getNodeCardinality(uri) == 1)
                    throw new DmtException(uri, DmtException.METADATA_MISMATCH,
                            "Metadata does not allow deleting last instance of this node.");
            }
        }
        
		getReadWriteDataSession(uri).deleteNode(Utils.tempAbsoluteUriToPath(uri));
		moveAclEntries(uri, null);
        enqueueEvent(EventList.DELETE, uri);
	}
    
	public synchronized void createInteriorNode(String nodeUri)
            throws DmtException {
    	checkWriteSession();
        commonCreateInteriorNode(nodeUri, null, true);
    }

	public synchronized void createInteriorNode(String nodeUri, String type)
			throws DmtException {
    	checkWriteSession();
        commonCreateInteriorNode(nodeUri, type, true);
	}
    
    // also used by copy() to create interior nodes without triggering an event
    private void commonCreateInteriorNode(String nodeUri, String type,
            boolean sendEvent) throws DmtException {
        String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_NOT_EXIST);
        String parent = Utils.parentUri(uri);
        if(parent == null)
            throw new DmtException(uri, DmtException.COMMAND_NOT_ALLOWED,
                    "Cannot create root node.");
        // TODO automatically create missing parent node 
        checkNode(parent, SHOULD_BE_INTERIOR);
        checkNodePermission(parent, Acl.ADD);
        checkNodeCapability(uri, MetaNode.CMD_ADD);

        MetaNode metaNode = getMetaNodeNoCheck(uri);
        if(metaNode != null && metaNode.isLeaf())
                throw new DmtException(uri, DmtException.METADATA_MISMATCH,
                        "Cannot create the specified interior node, " +
                        "meta-data defines it as a leaf node.");

        // could check type string, but this impl. does not handle it anyway
        // so we leave it to the plugins if they need it (same in setNodeType)

        checkNewNode(uri);
        checkMaxOccurrence(uri);
        
        // it is not really useful to allow creating automatic nodes, but this
        // is not a hard requirement, and should be enforced by the (lack of 
        // the) ADD access type instead
        
        getReadWriteDataSession(uri).createInteriorNode(Utils.tempAbsoluteUriToPath(uri), type);
        assignNewNodePermissions(uri, parent);
        if(sendEvent)
            enqueueEvent(EventList.ADD, uri);
    }

    public synchronized void createLeafNode(String nodeUri) throws DmtException {
        checkWriteSession();
        commonCreateLeafNode(nodeUri, null, null, true);
    }
    
    public synchronized void createLeafNode(String nodeUri, DmtData value)
			throws DmtException {
        checkWriteSession();
        commonCreateLeafNode(nodeUri, value, null, true);
	}
    
    public synchronized void createLeafNode(String nodeUri, DmtData value, 
            String mimeType) throws DmtException {
        checkWriteSession();
        commonCreateLeafNode(nodeUri, value, mimeType, true);
    }
    
    // also used by copy() to create leaf nodes without triggering an event
    private void commonCreateLeafNode(String nodeUri, DmtData value,
            String mimeType, boolean sendEvent) throws DmtException {
        String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_NOT_EXIST);
        String parent = Utils.parentUri(uri);
        if(parent == null)
            throw new DmtException(uri, DmtException.COMMAND_NOT_ALLOWED,
                    "Cannot create root node.");
        // TODO automatically create missing parent node 
        checkNode(parent, SHOULD_BE_INTERIOR);
        checkNodePermission(parent, Acl.ADD);
        checkNodeCapability(uri, MetaNode.CMD_ADD);

        MetaNode metaNode = getMetaNodeNoCheck(uri);
        if(metaNode != null && !metaNode.isLeaf())
                throw new DmtException(uri, DmtException.METADATA_MISMATCH,
                        "Cannot create the specified leaf node, meta-data " +
                        "defines it as an interior node.");
        checkNewNode(uri);
        checkValue(uri, value);
        checkMimeType(uri, mimeType);
        checkMaxOccurrence(uri);

        // it is not really useful to allow creating automatic nodes, but this
        // is not a hard requirement, and should be enforced by the (lack of 
        // the) ADD access type instead
        
        getReadWriteDataSession(uri).createLeafNode(Utils.tempAbsoluteUriToPath(uri), value, mimeType);
        
        if(sendEvent)
            enqueueEvent(EventList.ADD, uri);        
    }

	// Tree may be left in an inconsistent state if there is an error when only
	// part of the tree has been copied.
	public synchronized void copy(String nodeUri, String newNodeUri, 
            boolean recursive) throws DmtException {
		checkWriteSession();
        
		String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
		String newUri = makeAbsoluteUriAndCheck(newNodeUri, SHOULD_NOT_EXIST);
		if (Utils.isAncestor(uri, newUri))
			throw new DmtException(uri, DmtException.COMMAND_NOT_ALLOWED,
					"Cannot copy node to its descendant, '" + newUri + "'.");
		String newParentUri = Utils.parentUri(newUri);
        // newParentUri cannot be null, because newUri is a valid absolute URI
        // that points to a nonexisting node, and as such it must contain a '/'
		checkNode(newParentUri, SHOULD_BE_INTERIOR);

        // DMTND 7.7.1.5: "needs correct access rights for the equivalent Add,
		// Delete, Get, and Replace commands"
		if (dispatcher.handledBySameDataPlugin(uri, newUri)) {
            checkNodePermissionRecursive(uri, Acl.GET);
			checkNodeCapability(uri, MetaNode.CMD_GET);

            // ACL not copied, so parent does not need REPLACE permission even
            // if the copied node is a leaf
            checkNodePermission(newParentUri, Acl.ADD);
            checkNodeCapability(newUri, MetaNode.CMD_ADD);

			checkNewNode(newUri);
            checkMaxOccurrence(newUri);

            // for leaf nodes: since we are not passing a data object to the 
            // plugin, checking the value and mime-type against the new 
            // meta-data is the responsibility of the plugin itself
			
            getReadWriteDataSession(newUri).copy(Utils.tempAbsoluteUriToPath(uri), Utils.tempAbsoluteUriToPath(newUri), recursive);
		}
		else
			copyNoCheck(uri, newUri, recursive); // does not trigger any events  

        enqueueEvent(EventList.COPY, uri, newUri);
	}

	public synchronized void renameNode(String nodeUri, String newNodeName) 
            throws DmtException {
		checkWriteSession();
		String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
        checkOperation(uri, Acl.REPLACE, MetaNode.CMD_REPLACE);

        String parent = Utils.parentUri(uri);
		if (parent == null)
			throw new DmtException(uri, DmtException.COMMAND_NOT_ALLOWED,
					"Cannot rename root node.");
        
        String newName = Utils.validateAndNormalizeNodeName(newNodeName);
		String newUri = Utils.createAbsoluteUri(parent, newName);
		checkNode(newUri, SHOULD_NOT_EXIST);
		checkNewNode(newUri);
        
		MetaNode metaNode = getMetaNodeNoCheck(uri);
        MetaNode newMetaNode = getMetaNodeNoCheck(newUri);
        
		if (metaNode != null) {
		    if(metaNode.getScope() == MetaNode.PERMANENT)
		        throw new DmtException(uri, DmtException.METADATA_MISMATCH,
		                "Cannot rename permanent node.");
		    
		    int maxOcc = metaNode.getMaxOccurrence();
        
            // sanity check: all siblings of a node must either have a  
            // cardinality of 1, or they must be part of the same multi-node 
    		if(newMetaNode != null && maxOcc != newMetaNode.getMaxOccurrence())
                throw new DmtException(uri, DmtException.COMMAND_FAILED,
                        "Cannot rename node, illegal meta-data found (a " + 
                        "member of a multi-node has a sibling with different " +
                        "meta-data).");
        
            // if this is a multi-node (maxOcc > 1), renaming does not affect 
            // the cardinality
            if(maxOcc == 1 && !metaNode.isZeroOccurrenceAllowed())
                throw new DmtException(uri, DmtException.METADATA_MISMATCH,
                        "Metadata does not allow deleting last instance of " +
                        "this node.");
        }
        
		// the new node must be the same (leaf/interior) as the original
		if(newMetaNode != null && newMetaNode.isLeaf() != isLeafNodeNoCheck(uri))
		    throw new DmtException(newUri, DmtException.METADATA_MISMATCH,
		            "The destination of the rename operation is " +
                    (newMetaNode.isLeaf() ? "a leaf" : "an interior") + 
                    " node according to the meta-data, which does not match " +
                    "the source node.");
        
        // for leaf nodes: since we are not passing a data object to the 
        // plugin, checking the value and mime-type against the new 
        // meta-data is the responsibility of the plugin itself
        
		getReadWriteDataSession(uri).renameNode(Utils.tempAbsoluteUriToPath(uri), newName);
        moveAclEntries(uri, newUri);
        enqueueEvent(EventList.RENAME, uri, newName);
	}

    /**
     * Create an Access Control Context based on the given permissions. The
     * Permission objects are first created from the PermissionInfo objects,
     * then added to a permission collection, which is added to a protection
     * domain with no code source, which is used to create the access control
     * context. If the <code>null</code> argument is given, an empty access
     * control context is created.
     * 
     * @throws Exception if there is an error creating one of the permission
     *         objects (can be one of ClassNotFoundException, SecurityException,
     *         NoSuchMethodException, ClassCastException,
     *         IllegalArgumentException, InstantiationException,
     *         IllegalAccessException or InvocationTargetException)
     */
    private AccessControlContext getSecurityContext(PermissionInfo[] permissions)
            throws Exception {

        PermissionCollection permissionCollection = new Permissions();
        
        if(permissions != null)
            for (int i = 0; i < permissions.length; i++) {
                PermissionInfo info = permissions[i];
                
                Class permissionClass = Class.forName(info.getType());
                Constructor constructor = permissionClass
                        .getConstructor(PERMISSION_CONSTRUCTOR_SIG);
                Permission permission = (Permission) constructor.newInstance(
                        new Object[] {info.getName(), info.getActions()});
                permissionCollection.add(permission);
            }
        
        return new AccessControlContext(new ProtectionDomain[] {
                new ProtectionDomain(null, permissionCollection)});
    }

    private void checkSession() {
        if(state != STATE_OPEN)
            throw new IllegalStateException(
                    "Session is not open, cannot perform DMT operations.");
    }
    
    private void checkWriteSession() {
        checkSession();
        if(lockMode == LOCK_TYPE_SHARED)
            throw new IllegalStateException(
                    "Session is not open for writing, cannot perform " +
                    "requested write operation.");
    }
    
    private void purgeEvents(String[] rootPath) {
        eventList.excludeRoot(rootPath);
    }
    
	private void sendEvent(int type) {
		String[] nodes    = eventList.getNodes(type);
        String[] newNodes = eventList.getNewNodes(type);
        if(nodes.length != 0)
            sendEvent(type, nodes, newNodes);
    }

    private void enqueueEvent(int type, String node) {
        if(lockMode == LOCK_TYPE_ATOMIC)
            eventList.add(type, node);
        else
        	sendEvent(type, new String[] { node }, null);
    }
    
    private void enqueueEvent(int type, String node, String newNode) {
        if(lockMode == LOCK_TYPE_ATOMIC)
            eventList.add(type, node, newNode);
        else
            sendEvent(type, new String[] { node }, new String[] { newNode });
    }
    
    private void sendEvent(int type, String[] nodes, String[] newNodes) {
        String topic = EventList.getTopic(type);
        Hashtable properties = new Hashtable();
        properties.put("session.id", new Integer(sessionId));
        properties.put("nodes", nodes);
        if(newNodes != null)
            properties.put("newnodes", newNodes);
        Event event = new Event(topic, properties);
        
        // it's an error if Event Admin is missing, but it could also be ignored
        EventAdmin eventChannel = (EventAdmin) eventTracker.getService();
        if(eventChannel == null)
            throw new MissingResourceException("Event Admin not found.",
                    EventAdmin.class.getName(), null);
        
        eventChannel.postEvent(event);
    }
    
    private boolean isLeafNodeNoCheck(String uri) throws DmtException {
        return getReadableDataSession(uri).isLeafNode(Utils.tempAbsoluteUriToPath(uri));
    }

	private MetaNode getMetaNodeNoCheck(String uri) throws DmtException {
        return getReadableDataSession(uri).getMetaNode(Utils.tempAbsoluteUriToPath(uri));
	}

    // precondition: 'uri' must point be valid (checked with isNodeUri or
    // returned by getChildNodeNames)
	private void copyNoCheck(String uri, String newUri, boolean recursive) 
            throws DmtException {
		boolean isLeaf = isLeafNodeNoCheck(uri);
        String type = getNodeType(uri);
        
		// create new node (without sending a separate event about it)
		if (isLeaf)
		    // if getNodeValue() returns null, we attempt to set the default
            commonCreateLeafNode(newUri, getNodeValue(uri), type, false);
		else
			commonCreateInteriorNode(newUri, type, false);
        
		// copy Title property (without sending event) if it is supported by 
        // both source and target plugins
		try {
			internalSetNodeTitle(newUri, getNodeTitle(uri), false);
		} catch (DmtException e) {
		    if (e.getCode() != DmtException.FEATURE_NOT_SUPPORTED)
		        throw new DmtException(uri, DmtException.COMMAND_FAILED,
		                "Error copying node to '" + newUri
		                    + "', cannot copy title.", e);
		}
        
		// Format, Name, Size, TStamp and VerNo properties do not need to be
        // expicitly copied
        
		// copy children if mode is recursive and node is interior
		if (recursive && !isLeaf) {
            // 'children' is [] if there are no child nodes
			String[] children = getChildNodeNames(uri);
			for (int i = 0; i < children.length; i++)
				copyNoCheck(Utils.createAbsoluteUri(uri, children[i]), Utils
						.createAbsoluteUri(newUri, children[i]), true);
		}
	}

    // precondition: makeAbsoluteUri() must have been called for the uri
    private int getNodeCardinality(String uri) throws DmtException {
        String parent = Utils.parentUri(uri);
        String[] neighbours = getReadableDataSession(parent).getChildNodeNames(Utils.tempAbsoluteUriToPath(parent));
        return normalizeChildNodeNames(neighbours).size();
    }

	private void assignNewNodePermissions(String uri, String parent)
			throws DmtException {
		// DMTND 7.7.1.3: if parent does not have Replace permissions, give Add, 
        // Delete and Replace permissions to child.  (This rule cannot be 
        // applied to Java permissions, only to ACLs.)
		try {
			checkNodePermission(parent, Acl.REPLACE);
		}
		catch (DmtException e) {
			if (e.getCode() != DmtException.PERMISSION_DENIED)
				throw e; // should not happen
			if (principal != null) {
                Acl parentAcl = getEffectiveNodeAclNoCheck(parent);
                Acl newAcl = parentAcl.addPermission(principal, Acl.ADD
                        | Acl.DELETE | Acl.REPLACE);
				acls.put(uri, newAcl);
			}
		}
	}

    private void checkOperation(String uri, int actions, int capability)
        throws DmtException {
        checkNodePermission(uri, actions);
        checkNodeCapability(uri, capability);
    }
    
	// throws SecurityException if principal is local user, and sufficient
	// privileges are missing
	private void checkNodePermission(String uri, int actions)
			throws DmtException {
		checkNodeOrParentPermission(principal, uri, actions, false);
	}

	// throws SecurityException if principal is local user, and sufficient
	// privileges are missing
	private void checkNodeOrParentPermission(String uri, int actions)
			throws DmtException {
		checkNodeOrParentPermission(principal, uri, actions, true);
	}

    // precondition: 'uri' must point be valid (checked with isNodeUri or
    // returned by getChildNodeNames)
	private void checkNodePermissionRecursive(String uri, int actions) 
	        throws DmtException {
	    checkNodePermission(uri, actions);
	    
	    if (!isLeafNodeNoCheck(uri)) {
	        // 'children' is [] if there are no child nodes
	        String[] children = getChildNodeNames(uri);
	        for (int i = 0; i < children.length; i++)
	            checkNodePermissionRecursive(
	                    Utils.createAbsoluteUri(uri, children[i]), actions);
	    }
	}
	
	private String makeAbsoluteUriAndCheck(String nodeUri, int check)
			throws DmtException {
		String uri = makeAbsoluteUri(nodeUri);
		checkNode(uri, check);
		return uri;
	}

    // returns a plugin for read-only use
	private ReadableDataSession getReadableDataSession(String uri) 
            throws DmtException {
	    return getPluginSession(uri, false);
    }

    // returns a plugin for writing
	private ReadWriteDataSession getReadWriteDataSession(String uri) 
            throws DmtException {
        return getPluginSession(uri, true);
	}
    
    /*
    // 'synchronized' is just indication, all entry points are synch'd anyway
    private synchronized PluginWrapper getPlugin(String uri, boolean writable) 
            throws DmtException {
        Plugin plugin = dispatcher.getDataPlugin(uri);
        
        if(writable && !plugin.isWritableDataPlugin())
            throw new DmtException(uri, DmtException.COMMAND_NOT_ALLOWED,
                    "Cannot perform write operation on Read-Only plugin.");
        
        PluginWrapper wrappedPlugin = 
            new PluginWrapper(plugin, lockMode, securityContext);

        // this block requires synchronized access
        if (!dataPlugins.contains(wrappedPlugin)) {
            wrappedPlugin.open(subtreeUri, lockMode, this);
            dataPlugins.add(wrappedPlugin);
        }
        return wrappedPlugin;
        
    }
    */

    // precondition: if 'writable' is true, session lock type must not be shared
    // 'synchronized' is just indication, all entry points are synch'd anyway
    private synchronized PluginSessionWrapper getPluginSession(String uri, 
            boolean writeOperation) throws DmtException {
        
        PluginRegistration pluginRegistration = dispatcher.getDataPlugin(uri);
        String[] pluginSessionRoot = getRootForPlugin(pluginRegistration, uri);
        
        PluginSessionWrapper wrappedPlugin = (PluginSessionWrapper) 
            dataPlugins.get(pluginSessionRoot);
        
        if(wrappedPlugin != null) {
            if(writeOperation && 
                    wrappedPlugin.getSessionType() == LOCK_TYPE_SHARED)
                throw new DmtException(uri, DmtException.COMMAND_FAILED,
                        "Write operation attempted on a plugin that is " +
                        "read-only or does not support " +
                        (lockMode == LOCK_TYPE_EXCLUSIVE ? "non-" : "") +
                        "atomic writing.");
        } else {
            DataPluginFactory plugin = pluginRegistration.getDataPlugin();

            ReadableDataSession pluginSession = null;
            int pluginSessionType = lockMode;
            
            switch(lockMode) {
            case LOCK_TYPE_EXCLUSIVE:
                pluginSession = 
                    plugin.openReadWriteSession(pluginSessionRoot, this);
                if(pluginSession == null && writeOperation)
                    // TODO exception code does not match javadoc, we need a way to decide whether the plugin is writable
                    throw new DmtException(uri, DmtException.COMMAND_FAILED,
                            "The plugin handling the requested node does not " +
                            "support non-atomic writing.");
                break;
            case LOCK_TYPE_ATOMIC:
                pluginSession = 
                    plugin.openAtomicSession(pluginSessionRoot, this);
                if(pluginSession == null && writeOperation)
                    // TODO exception code does not match javadoc, we need a way to decide whether the plugin is writable
                    throw new DmtException(uri, DmtException.TRANSACTION_ERROR,
                            "The plugin handling the requested node does not " +
                            "support atomic writing.");
                break;
            }
            
            // read-only session if lockMode is LOCK_TYPE_SHARED, or if the 
            // plugin did not support the writing lock mode, and the current 
            // operation is for reading
            if(pluginSession == null) {
                pluginSessionType = LOCK_TYPE_SHARED;
                pluginSession =
                    plugin.openReadOnlySession(pluginSessionRoot, this);
            }

            wrappedPlugin = new PluginSessionWrapper(pluginSession, 
                    pluginSessionType, pluginSessionRoot, securityContext);
            
            // this requires synchronized access
            dataPlugins.put(pluginSessionRoot, wrappedPlugin);
        }
        
        return wrappedPlugin;
    }
    
    private String[] getRootForPlugin(PluginRegistration plugin, String uri) {
        String[] roots = plugin.getDataRoots();
        for(int i = 0; i < roots.length; i++)
            if(Utils.isAncestor(roots[i], uri))
                return Utils.tempAbsoluteUriToPath(
                        Utils.isAncestor(roots[i], subtreeUri) ? subtreeUri : roots[i]);
        
        throw new IllegalStateException("Internal error, plugin root not " +
                "found for a URI handled by the plugin.");
    }
    
    // precondition: 'uri' must be an absolute URI
    private void checkNode(String uri, int check) throws DmtException {
		boolean shouldExist = (check != SHOULD_NOT_EXIST);
		if (getReadableDataSession(uri).isNodeUri(Utils.tempAbsoluteUriToPath(uri)) != shouldExist)
			throw new DmtException(uri,
					shouldExist ? DmtException.NODE_NOT_FOUND
							: DmtException.NODE_ALREADY_EXISTS,
					"The specified URI should point to "
							+ (shouldExist ? "an existing" : "a non-existent")
							+ " node to perform the requested operation.");
		boolean shouldBeLeaf = (check == SHOULD_BE_LEAF);
		boolean shouldBeInterior = (check == SHOULD_BE_INTERIOR);
		if ((shouldBeLeaf || shouldBeInterior)
				&& isLeafNodeNoCheck(uri) != shouldBeLeaf)
			throw new DmtException(uri, DmtException.COMMAND_NOT_ALLOWED,
					"The specified URI should point to "
							+ (shouldBeLeaf ? "a leaf" : "an internal")
							+ " node to perform the requested operation.");
	}
    
    // precondition: checkNode() must have been called for the given uri
    private void checkNodeCapability(String uri, int capability)
            throws DmtException {
        MetaNode metaNode = getMetaNodeNoCheck(uri);
        if(metaNode != null && !metaNode.can(capability))
            throw new DmtException(uri, DmtException.METADATA_MISMATCH,
                    "Node meta-data does not allow the " + 
                    capabilityName(capability) + " operation for this node.");
        // default for all capabilities is 'true', if no meta-data is provided
    }

    private void checkValue(String uri, DmtData data) throws DmtException {
        MetaNode metaNode = getMetaNodeNoCheck(uri);
        
        if(metaNode == null)
            return;
        
        // if default data was requested, only check that there is a default 
        if(data == null) {
            if(metaNode.getDefault() == null)
                throw new DmtException(uri, DmtException.METADATA_MISMATCH,
                        "This node has no default value in the meta-data.");
            return;
        }

        if(!metaNode.isValidValue(data))
            throw new DmtException(uri, DmtException.METADATA_MISMATCH,
                    "The specified node value is not valid according to " +
                    "the meta-data.");
        
        // not checking value meta-data constraints individually, but leaving
        // this to the isValidValue method of the meta-node
        /*
        if((metaNode.getFormat() & data.getFormat()) == 0)
            throw new DmtException(uri, DmtException.METADATA_MISMATCH,
                    "The format of the specified value is not in the list of " +
                    "valid formats given in the node meta-data.");
        if(data.getFormat() == DmtData.FORMAT_INTEGER) {
            if(metaNode.getMax() < data.getInt())
                throw new DmtException(uri, DmtException.METADATA_MISMATCH,
                        "Attempting to set too large integer, meta-data " +
                        "specifies the maximum value of " + metaNode.getMax());
            if(metaNode.getMin() > data.getInt())
                throw new DmtException(uri, DmtException.METADATA_MISMATCH,
                        "Attempting to set too small integer, meta-data " +
                        "specifies the minimum value of " + metaNode.getMin());
        }
        
        DmtData[] validValues = metaNode.getValidValues();
        if(validValues != null && !Arrays.asList(validValues).contains(data))
            throw new DmtException(uri, DmtException.METADATA_MISMATCH,
                    "Specified value is not in the list of valid values " +
                    "given in the node meta-data.");
        */
    }
    
    private void checkNewNode(String uri) throws DmtException {
        MetaNode metaNode = getMetaNodeNoCheck(uri);

        if(metaNode == null)
            return;
        
        if(metaNode.getScope() == MetaNode.PERMANENT)
            throw new DmtException(uri, DmtException.METADATA_MISMATCH,
                    "Cannot create permanent node.");

        String name = Utils.getUriPart(uri, false, true);
        if(!metaNode.isValidName(name))
            throw new DmtException(uri, DmtException.METADATA_MISMATCH,
                    "The specified node name is not valid according to " +
                    "the meta-data.");

        // not checking valid name list from meta-data, but leaving this to the
        // isValidName method of the meta-node
        /*
        String[] validNames = metaNode.getValidNames();
        if(validNames != null && !Arrays.asList(validNames).contains(name))
            throw new DmtException(uri, DmtException.METADATA_MISMATCH,
                    "The specified node name is not in the list of valid " +
                    "names specified in the node meta-data.");
        */
    }
    
    private void checkMimeType(String uri, String type) throws DmtException {
        MetaNode metaNode = getMetaNodeNoCheck(uri);
        
        if(metaNode == null)
            return;
        
        if(type == null) // default MIME type was requested
            // TODO maybe check that meta-data defines at least one MIME type
            return;
        
        // TODO check that 'type' is a proper MIME type string, COMMAND_FAILED if not
        
        String[] validMimeTypes = metaNode.getMimeTypes();
        if(validMimeTypes != null && !Arrays.asList(validMimeTypes).contains(type))
            throw new DmtException(uri, DmtException.METADATA_MISMATCH,
                    "The specified MIME type is not in the list of valid " +
                    "types in the node meta-data.");
    }
    
    private void checkMaxOccurrence(String uri) throws DmtException {
        MetaNode metaNode = getMetaNodeNoCheck(uri);
        
        if(metaNode == null)
            return;

        // If maxOccurrence == 1 then it is not a multi-node, so it can be 
        // created if it did not exist before.  If maxOccurrence > 1, it can
        // only be created if the number of existing nodes does not reach it. 
        int maxOccurrence = metaNode.getMaxOccurrence();
        if(maxOccurrence != Integer.MAX_VALUE && maxOccurrence > 1 
                && getNodeCardinality(uri) >= maxOccurrence)
            throw new DmtException(uri, DmtException.METADATA_MISMATCH,
                    "Cannot create the specified node, meta-data maximizes " +
                    "the number of instances of this node to " + maxOccurrence + ".");
    }

    private String makeAbsoluteUri(String nodeUri) throws DmtException {
        String normNodeUri = Utils.validateAndNormalizeUri(nodeUri);
		if (Utils.isAbsoluteUri(normNodeUri)) {
			if (!Utils.isAncestor(subtreeUri, normNodeUri))
				throw new DmtException(nodeUri, DmtException.COMMAND_FAILED,
						"Specified URI points outside the subtree of this session.");
			return normNodeUri;
		}
		return Utils.createAbsoluteUri(subtreeUri, normNodeUri);
	}

    // remove null entries from the returned array (if it is non-null)
    private static List normalizeChildNodeNames(String[] pluginChildNodes) {
        List processedChildNodes = new Vector();
        
        if (pluginChildNodes != null) 
            for (int i = 0; i < pluginChildNodes.length; i++)
                if (pluginChildNodes[i] != null)
                    processedChildNodes.add(pluginChildNodes[i]);
        
        return processedChildNodes;
    }

	// Move ACL entries from 'uri' to 'newUri'.
	// If 'newUri' is 'null', the ACL entries are removed (moved to nowhere).
	private static void moveAclEntries(String uri, String newUri) {
		synchronized (acls) {
			Hashtable newEntries = null;
			if (newUri != null)
				newEntries = new Hashtable();
			Iterator i = acls.entrySet().iterator();
			while (i.hasNext()) {
				Map.Entry entry = (Map.Entry) i.next();
				String relUri = Utils.relativeUri(uri, (String) entry.getKey());
				if (relUri != null) {
					if (newUri != null)
						newEntries.put(Utils.createAbsoluteUri(newUri, relUri),
								entry.getValue());
					i.remove();
				}
			}
			if (newUri != null)
				acls.putAll(newEntries);
		}
	}

    private static Acl getEffectiveNodeAclNoCheck(String uri) {
        Acl acl;
        synchronized (acls) {
            acl = (Acl) acls.get(uri);
            // must finish whithout NullPointerException, because root ACL must
            // not be empty
            while (acl == null || isEmptyAcl(acl)) {
                uri = Utils.parentUri(uri);
                acl = (Acl) acls.get(uri);
            }
        }
        return acl;
    }
    
    // precondition: uri parameter must be an absolute URI
	// throws SecurityException if principal is local user, and sufficient
	// privileges are missing
	private static void checkNodeOrParentPermission(String name, String uri,
			int actions, boolean checkParent) throws DmtException {

        if(uri.equals("."))
            checkParent = false;
        
        String parentUri = null;
        if(checkParent)         // not null, as the uri is absolute but not "."
            parentUri = Utils.parentUri(uri); 
        
		if (name != null) {
            // succeed if the principal has the required permissions on the
            // given uri, OR if the checkParent parameter is true and the
            // principal has the required permissions for the parent uri
			if (!(
                    hasAclPermission(uri, name, actions) || 
                    checkParent && hasAclPermission(parentUri, name, actions)))
				throw new DmtException(uri, DmtException.PERMISSION_DENIED,
						"Principal '" + name
								+ "' does not have the required permissions ("
								+ writeAclCommands(actions) + ") on the node "
								+ (checkParent ? "or its parent " : "")
								+ "to perform this operation.");
		}
		else { // not doing local permission check if ACL check was done
		    String actionString = writeAclCommands(actions);
            checkLocalPermission(uri, actionString);
            if(checkParent)
                checkLocalPermission(parentUri, actionString);
		}
	}
    
    private static boolean hasAclPermission(String uri, String name, int actions) {
        return getEffectiveNodeAclNoCheck(uri).isPermitted(name, actions);
    }

    private static void checkLocalPermission(String uri, String actions) {
        SecurityManager sm = System.getSecurityManager();
        if(sm != null)
            sm.checkPermission(new DmtPermission(uri, actions));
    }

    private static String capabilityName(int capability) {
        switch(capability) {
        case MetaNode.CMD_ADD:     return "Add";
        case MetaNode.CMD_DELETE:  return "Delete";
        case MetaNode.CMD_EXECUTE: return "Execute";
        case MetaNode.CMD_GET:     return "Get";
        case MetaNode.CMD_REPLACE: return "Replace";
        }
        // never reached
        throw new IllegalArgumentException(
                "Unknown meta-data capability constant " + capability + ".");
    }
    
	// ENHANCE define constants for the action names should in the Acl class
	private static String writeAclCommands(int actions) {
		String commands = null;
		commands = writeCommand(commands, actions, Acl.ADD,     "Add");
		commands = writeCommand(commands, actions, Acl.DELETE,  "Delete");
		commands = writeCommand(commands, actions, Acl.EXEC,    "Exec");
		commands = writeCommand(commands, actions, Acl.GET,     "Get");
		commands = writeCommand(commands, actions, Acl.REPLACE, "Replace");
		return (commands != null) ? commands : "";
	}

	private static String writeCommand(String base, int actions, int action,
			String entry) {
		if ((actions & action) != 0)
			return (base != null) ? base + ',' + entry : entry;
		return base;
	}

	private static boolean isEmptyAcl(Acl acl) {
		return acl.getPermissions("*") == 0 && acl.getPrincipals().length == 0;
	}
}

// Sets of node URIs for the different types of changes. 
// Only used in atomic transactions.
class EventList {
    // two-parameter event types
    static final int RENAME  = 0;
    static final int COPY    = 1;
    
    // single-parameter event types
    static final int ADD     = 2;
    static final int DELETE  = 3;
    static final int REPLACE = 4;
    
    private static final int TWO_PARAM_EVENT_TYPE_NUM = 2;
    private static final int EVENT_TYPE_NUM = 5;
    
    private List[] nodeLists    = new List[EVENT_TYPE_NUM];
    private List[] newNodeLists = new List[TWO_PARAM_EVENT_TYPE_NUM];
    
    EventList() {
        for(int i = 0; i < EVENT_TYPE_NUM; i++)
            nodeLists[i] = new Vector();
        for(int i = 0; i < TWO_PARAM_EVENT_TYPE_NUM; i++)
            newNodeLists[i] = new Vector();
    }
    
    synchronized void clear() {
        for(int i = 0; i < EVENT_TYPE_NUM; i++)
            nodeLists[i].clear();
        for(int i = 0; i < TWO_PARAM_EVENT_TYPE_NUM; i++)
            newNodeLists[i].clear();
	}
    
    // TODO modify to accept a single root path (as a String[]) to exclude  
    synchronized void excludeRoot(String[] root) {
        int i = 0;
        for(; i < TWO_PARAM_EVENT_TYPE_NUM; i++)
            // cannot use iterator here because if there is any match,
            // items have to be removed from both lists
            for(int k = 0; k < nodeLists[i].size(); k++)
                if(Utils.tempIsAncestorPath(root, Utils.tempAbsoluteUriToPath((String) nodeLists[i].get(k))) ||
                        Utils.tempIsAncestorPath(root, Utils.tempAbsoluteUriToPath((String) newNodeLists[i].get(k)))) {
                    nodeLists[i].remove(k);
                    newNodeLists[i].remove(k);
                }
        
        for(; i < EVENT_TYPE_NUM; i++) {
            Iterator iterator = nodeLists[i].iterator();
            while(iterator.hasNext())
                if(Utils.tempIsAncestorPath(root, Utils.tempAbsoluteUriToPath((String) iterator.next())))
                    iterator.remove();
        }
    }

	synchronized void add(int type, String node) {
        if(type < TWO_PARAM_EVENT_TYPE_NUM)
            throw new IllegalArgumentException("Missing parameter for event.");
        
        nodeLists[type].add(node);
    }
    
	synchronized void add(int type, String node, String newNode) {
        if(type >= TWO_PARAM_EVENT_TYPE_NUM)
            throw new IllegalArgumentException("Too many parameters for event.");

        nodeLists[type].add(node);
        newNodeLists[type].add(newNode);
    }
    
    synchronized String[] getNodes(int type) {
        return (String[]) nodeLists[type].toArray(new String[0]);
    }

    synchronized String[] getNewNodes(int type) {
        if(type >= TWO_PARAM_EVENT_TYPE_NUM)
            return null;
        
        return (String[]) newNodeLists[type].toArray(new String[0]);
    }

    static String getTopic(int type) {
        switch(type) {
        case ADD:     return "org/osgi/service/dmt/ADDED";
        case DELETE:  return "org/osgi/service/dmt/DELETED";
        case REPLACE: return "org/osgi/service/dmt/REPLACED";
        case RENAME:  return "org/osgi/service/dmt/RENAMED";
        case COPY:    return "org/osgi/service/dmt/COPIED";
        }
        throw new IllegalArgumentException("Unknown event type.");
    }
}


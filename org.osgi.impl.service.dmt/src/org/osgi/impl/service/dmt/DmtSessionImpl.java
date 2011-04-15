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

import info.dmtree.Acl;
import info.dmtree.DmtData;
import info.dmtree.DmtEvent;
import info.dmtree.DmtException;
import info.dmtree.DmtIllegalStateException;
import info.dmtree.DmtSession;
import info.dmtree.MetaNode;
import info.dmtree.Uri;
import info.dmtree.security.DmtPermission;
import info.dmtree.security.DmtPrincipalPermission;
import info.dmtree.spi.DataPlugin;
import info.dmtree.spi.DmtConstants;
import info.dmtree.spi.ExecPlugin;
import info.dmtree.spi.ReadWriteDataSession;
import info.dmtree.spi.ReadableDataSession;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.osgi.impl.service.dmt.dispatcher.Plugin;
import org.osgi.impl.service.dmt.dispatcher.Segment;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.service.permissionadmin.PermissionInfo;

// OPTIMIZE node handling (e.g. retrieve plugin from dispatcher only once per API call)
// OPTIMIZE only retrieve meta-data once per API call
// OPTIMIZE only call commit/rollback for plugins that were actually modified since the last transaction boundary
public class DmtSessionImpl implements DmtSession {
	private static final String INTERIOR_NODE_VALUE_SUPPORT_PROPERTY = "org.osgi.impl.service.dmt.interior-node-value-support";

	private static final int SHOULD_NOT_EXIST = 0;
	private static final int SHOULD_EXIST = 1;
	private static final int SHOULD_BE_LEAF = 2; // implies SHOULD_EXIST
	private static final int SHOULD_BE_INTERIOR = 3; // implies SHOULD_EXIST

	private static final Class[] PERMISSION_CONSTRUCTOR_SIG = new Class[] {
			String.class, String.class };

	private static Hashtable acls;

	// Stores the ACL table at the start of each transaction in an atomic
	// session. Can be static because atomic session cannot run in parallel.
	private static Hashtable savedAcls;

	static {
		init_acls();
	}

	private final AccessControlContext securityContext;
	private final Context context;
	final DmtAdminCore dmtAdmin;

	private final String principal;
	private final Node subtreeNode;
	private final int lockMode;
	private final int sessionId;

	private EventStore eventStore;
	private Vector dataPlugins;
	private int state;

	// keeps a table of original state for every node that is modified 
	// in an atomic session via:
	// - setNodeValue(),
	// - setNodeTitle() or
	// - setNodeAcl()
	// This allows to determine effective changes in atomic sessions on-the-fly 
	// in order to generate the correct event sequence
	// 
	private Hashtable initialNodeStates;
	private Hashtable actualNodeStates;

	public Hashtable getInitialNodeStates() {
		if (initialNodeStates == null) {
			initialNodeStates = new Hashtable();
		}
		return initialNodeStates;
	}
	
	public Hashtable getActualNodeStates() {
		if (actualNodeStates == null) {
			actualNodeStates = new Hashtable();
		}
		return actualNodeStates;
	}
	
	/**
	 * If this is the first time that a node was touched in an atomic session,
	 * the nodes title, value, type and acl is requested and the nodeState - 
	 * encoded as hash codes - is stored in a hashtable. 
	 * @param node
	 * @param dmtSession
	 * @throws DmtException
	 */
	private void grabInitialNodeState(Node node, ReadableDataSession dmtSession) {
		if ( this.lockMode != DmtSession.LOCK_TYPE_ATOMIC )
			return;
		try {
			NodeState initialState = (NodeState) getInitialNodeStates().get(node); 
			if ( initialState == null ) {
				String oldTitle = dmtSession.getNodeTitle(node.getPath());
				String oldType = dmtSession.getNodeType(node.getPath());
				Acl oldAcl = (Acl) acls.get(node);
				DmtData oldData = dmtSession.getNodeValue(node.getPath());
				initialState = new NodeState();
				initialState.typeHash = oldType != null ? oldType.hashCode() : 0;
				initialState.titleHash = oldTitle != null ? oldTitle.hashCode() : 0;
				initialState.aclHash = oldAcl != null ? oldAcl.hashCode() : 0;
				initialState.valueHash = oldData != null ? oldData.hashCode() : 0;
				getInitialNodeStates().put( node, initialState );
			}
		} catch (DmtException e) {
		}
	}

	/**
	 * creates a new NodeState instance for a leaf node
	 * Will be called, if a new leaf node is created in an atomic session.
	 * The nodes state will be empty except the given DmtData value.
	 * Furthermore this NodeState is added to the list of actual node states.
	 * @param node
	 * @param value
	 */
	private void createNodeState( Node node, DmtData value ) {
		NodeState nodeState = new NodeState();
		nodeState.valueHash = value != null ? value.hashCode() : 0;
		getActualNodeStates().put(node, nodeState);
	}
	
	/**
	 * creates a new NodeState instance for an interior node
	 * Will be called, if a new interior node is created in an atomic session.
	 * The nodes state will be empty except the given type.
	 * Furthermore this NodeState is added to the list of actual node states.
	 * @param node
	 * @param value
	 */
	private void createNodeState( Node node, String type) {
		NodeState nodeState = new NodeState();
		nodeState.valueHash = type != null ? type.hashCode() : 0;
		getActualNodeStates().put(node, nodeState);
	}

	/**
	 * Searches for an existing NodeState instance in the list of actual node states.
	 * If found, it will be returned.
	 * If not found, then a new Instance will be created as copy of the one from the 
	 * initial node state, i.e. the state that the node had at beginning of the transaction.
	 * Then this NodeState is added to the list of actual node states.
	 * This method will be invoked, if just individual parameters of a node are changed in
	 * an atomic session. It allows to compare the nodes actual state during the transaction
	 * with its initial state at beginning of the transaction. 
	 * @param node
	 * @return
	 */
	// looks for existing node-state
	// if no such state exists, a new one will be created and initialized from initialNodeStates
	private NodeState lookupNodeState( Node node ) {
		NodeState actualState = (NodeState) getActualNodeStates().get( node );
		if ( actualState == null ) {
			NodeState initialState = (NodeState) getInitialNodeStates().get(node);
			actualState = initialState != null ? initialState.clone() : new NodeState();
			getActualNodeStates().put(node, actualState);
		}
		return actualState;
	}
	private void updateTitleState( Node node, String title ) {
		((NodeState) lookupNodeState(node)).titleHash = title != null ? title.hashCode() : 0;
	}
	private void updateValueState( Node node, DmtData value ) {
		((NodeState) lookupNodeState(node)).valueHash = value != null ? value.hashCode() : 0;
	}
	private void updateTypeState( Node node, String type ) {
		((NodeState) lookupNodeState(node)).typeHash = type != null ? type.hashCode() : 0;
	}
	private void updateAclState( Node node, Acl acl ) {
		((NodeState) lookupNodeState(node)).aclHash = acl != null ? acl.hashCode() : 0;
	}

	// Session creation is done in two phases:
	// - DmtAdmin creates a new DmtSessionImpl instance (this should indicate
	// as many errors as possible, but must not call any plugins)
	// - when all conflicting sessions have been closed, DmtAdmin calls "open()"
	// to actually open the session for external use
	DmtSessionImpl(String principal, String subtreeUri, int lockMode,
			PermissionInfo[] permissions, Context context, DmtAdminCore dmtAdmin)
			throws DmtException {

		subtreeNode = Node.validateAndNormalizeUri(subtreeUri);

		if (!subtreeNode.isAbsolute())
			throw new DmtException(subtreeUri, DmtException.COMMAND_FAILED,
					"Relative subtree root URI passed to getSession().");

		this.principal = principal;
		this.lockMode = lockMode;
		this.dmtAdmin = dmtAdmin;
		this.context = context;

		if (principal != null) { // remote session
			SecurityManager sm = System.getSecurityManager();
			if (sm != null)
				sm.checkPermission(new DmtPrincipalPermission(principal));

			try {
				securityContext = getSecurityContext(permissions);
			} catch (Exception e) {
				throw new DmtException(
						subtreeNode.getUri(),
						DmtException.COMMAND_FAILED,
						"Unable to create Protection Domain for remote server.",
						e);
			}
		} else
			securityContext = null;

		sessionId = (new Long(System.currentTimeMillis())).hashCode()
				^ hashCode();

		eventStore = new EventStore(context, sessionId, this);

		dataPlugins = new Vector();
		state = STATE_CLOSED;
	}

	// called directly before returning the session object in getSession()
	// throws NODE_NOT_FOUND if the previously specified root does not exist
	// throws SecurityException or DmtException.PERMISSION_DENIED if the caller
	// doesn't have GET permissions for the session root node
	void open() throws DmtException {
		checkNodePermission(subtreeNode, Acl.GET);

		if (lockMode == LOCK_TYPE_ATOMIC)
			// shallow copy is enough, Nodes and Acls are immutable
			savedAcls = (Hashtable) acls.clone();

		state = STATE_OPEN;

		// after everything is initialized, check with the plugins whether the
		// given node really exists

		// // session might be opened on scaffold node
		// if ( ! isPureStructuralNode(subtreeNode))
		checkNode(subtreeNode, SHOULD_EXIST);
		eventStore.dispatchSessionLifecycleEvent(DmtEvent.SESSION_OPENED);
	}

	// called by Dmt Admin when checking session conflicts
	Node getRootNode() {
		return subtreeNode;
	}

	// called by the Session Wrapper, rollback parameter is:
	// - true if a fatal exception has been thrown in a DMT access method
	// - false if any exception has been thrown in the commit/rollback methods
	protected void invalidateSession(boolean rollback, boolean timeout) {
		state = STATE_INVALID;
		context.log(LogService.LOG_WARNING, "Invalidating session '"
				+ sessionId + "' because of "
				+ (timeout ? "timeout." : "error."), null);

		if (lockMode == LOCK_TYPE_ATOMIC && rollback) {
			try {
				rollbackPlugins();
			} catch (DmtException e) {
				context.log(LogService.LOG_WARNING, "Error rolling back "
						+ "plugin while invalidating session.", e);
			}
		}

		try {
			closeAndRelease(false);
		} catch (DmtException e) {
			context.log(LogService.LOG_WARNING, "Error closing plugin while "
					+ "invalidating session.", e);
		}

		eventStore.dispatchSessionLifecycleEvent(DmtEvent.SESSION_CLOSED);
	}

	/*
	 * These methods can be called even before the session has been opened, and
	 * also after the session has been closed.
	 */

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
		return subtreeNode.getUri();
	}

	public int getLockType() {
		return lockMode;
	}

	/* These methods are only meaningful in the context of an open session. */

	// no other API methods can be called while this method is executed
	public synchronized void close() throws DmtException {
		checkSession();

		// changed to CLOSED if this method finishes without error
		state = STATE_INVALID;

		try {
			closeAndRelease(lockMode == LOCK_TYPE_ATOMIC);
		} finally {
			eventStore.dispatchSessionLifecycleEvent(DmtEvent.SESSION_CLOSED);
		}

		state = STATE_CLOSED;
	}

	private void closeAndRelease(boolean commit) throws DmtException {
		try {
			if (commit)
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
		ListIterator i = dataPlugins.listIterator(dataPlugins.size());
		while (i.hasPrevious()) {
			try {
				((PluginSessionWrapper) i.previous()).close();
			} catch (Exception e) {
				closeExceptions.add(e);
			}
		}
		dataPlugins.clear();

		if (closeExceptions.size() != 0)
			throw new DmtException((String) null, DmtException.COMMAND_FAILED,
					"Some plugins failed to close.", closeExceptions, false);
	}

	// no other API methods can be called while this method is executed
	public synchronized void commit() throws DmtException {
		checkSession();

		if (lockMode != LOCK_TYPE_ATOMIC)
			throw new DmtIllegalStateException("Commit can only be requested "
					+ "for atomic sessions.");

		// changed back to OPEN if this method finishes without error
		state = STATE_INVALID;

		commitPlugins();

		savedAcls = (Hashtable) acls.clone();

		state = STATE_OPEN;
	}

	// precondition: lockMode == LOCK_TYPE_ATOMIC
	private void commitPlugins() throws DmtException {
		Vector commitExceptions = new Vector();
		ListIterator i = dataPlugins.listIterator(dataPlugins.size());
		// this block requires synchronization
		while (i.hasPrevious()) {
			PluginSessionWrapper wrappedPlugin = (PluginSessionWrapper) i
					.previous();
			try {
				// checks transaction support before calling commit on the
				// plugin
				wrappedPlugin.commit();
			} catch (Exception e) {
				eventStore.excludeRoot(wrappedPlugin.getSessionRoot());
				commitExceptions.add(e);
			}
		}

		eventStore.dispatchEvents();

		if (commitExceptions.size() != 0)
			throw new DmtException((String) null,
					DmtException.TRANSACTION_ERROR,
					"Some plugins failed to commit.", commitExceptions, false);

	}

	// no other API methods can be called while this method is executed
	public synchronized void rollback() throws DmtException {
		checkSession();

		if (lockMode != LOCK_TYPE_ATOMIC)
			throw new DmtIllegalStateException(
					"Rollback can only be requested " + "for atomic sessions.");

		// changed back to OPEN if this method finishes without error
		state = STATE_INVALID;

		acls = (Hashtable) savedAcls.clone();

		rollbackPlugins();

		state = STATE_OPEN;
	}

	// precondition: lockMode == LOCK_TYPE_ATOMIC
	private void rollbackPlugins() throws DmtException {
		eventStore.clear();

		Vector rollbackExceptions = new Vector();
		// this block requires synchronization
		ListIterator i = dataPlugins.listIterator(dataPlugins.size());
		while (i.hasPrevious()) {
			try {
				// checks transaction support before calling rollback on the
				// plugin
				((PluginSessionWrapper) i.previous()).rollback();
			} catch (Exception e) {
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
		internalExecute(nodeUri, null, data);
	}

	public synchronized void execute(String nodeUri, String correlator,
			String data) throws DmtException {
		internalExecute(nodeUri, correlator, data);
	}

	// same as execute/3 but can be called internally, because it is not wrapped
	private void internalExecute(String nodeUri, final String correlator,
			final String data) throws DmtException {

		// session must be writable
		checkWriteSession("execute");

		final Node node = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);

		checkOperation(node, Acl.EXEC, MetaNode.CMD_EXECUTE);

		Plugin dispatcherPlugin = context.getPluginDispatcher()
				.getExecPluginFor(node.getPath());
		if (dispatcherPlugin == null)
			throw new DmtException(node.getUri(), DmtException.COMMAND_FAILED,
					"No exec plugin registered for given node.");

		final ExecPlugin plugin = (ExecPlugin) context.getBundleContext()
				.getService(dispatcherPlugin.getReference());
		final DmtSession session = this;

		try {
			AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws DmtException {
					plugin.execute(session, node.getPath(), correlator, data);
					return null;
				}
			}, securityContext);
		} catch (PrivilegedActionException e) {
			throw (DmtException) e.getException();
		}
	}

	private boolean isPureStructuralNode(String uri) throws DmtException {
		return isPureStructuralNode(makeAbsoluteUri(uri));
	}

	/**
	 * checks if the given path is
	 * 
	 * checks if the given path points to a node that is just implicitely
	 * defined by the mountpoint of a DataPlugin
	 * 
	 * @param path
	 * @return
	 */
	private boolean isPureStructuralNode(Node nodePath) {

		// can't be a structural node if the path does not exist at all (as a
		// Segment)
		if (context.getPluginDispatcher().findSegment(nodePath.getPath()) == null)
			return false;

		// if it is directly matched in the responsible plugins dataRootURI,
		// then it can't be a structural node
		Plugin plugin = context.getPluginDispatcher().getDataPluginFor(
				nodePath.getPath());
		for (Segment segment : (List<Segment>) plugin.getOwns()) {
			if (nodePath.getUri().equals(segment.getUri().toString()))
				return false;
		}

		// // direct match in the mountPoints, but no directly matching plugin
		// found before, i.e. a structural node
		// if ( plugin.getMountPoints().contains(nodePath.getUri()) )
		// return true;
		//
		// // nodePath is a scaffold of the responsible plugins mountPoint(s)
		// for( String mountPoint : (Set<String>) plugin.getMountPoints() ) {
		// if ( mountPoint.startsWith( nodePath.getUri() ))
		// return true;
		// }

		// if root plugin is responsible and nodepath is below root, then
		// nodePath must be a scaffold node
		if (plugin.isRoot() && !nodePath.isRoot())
			return true;

		// if all other checks failed, then it's not a structural node
		return false;
	}

	// requires DmtPermission with GET action, no ACL check done because there
	// are no ACLs stored for non-existing nodes (in theory)
	public synchronized boolean isNodeUri(String nodeUri) {
		checkSession();
		try {
			Node node = makeAbsoluteUri(nodeUri);
			if (isPureStructuralNode(node))
				return true;
			checkLocalPermission(node, writeAclCommands(Acl.GET));
			checkNode(node, SHOULD_EXIST);
			// not checking meta-data for the GET capability, the plugin must be
			// prepared to answer isNodeUri() even if the node is not "gettable"
		} catch (DmtException e) {
			return false; // invalid node URI or error opening plugin
		}
		return true;
	}

	public synchronized boolean isLeafNode(String nodeUri) throws DmtException {
		checkSession();
		if (isPureStructuralNode(nodeUri))
			return false;
		Node node = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
		checkOperation(node, Acl.GET, MetaNode.CMD_GET);
		return isLeafNodeNoCheck(node);
	}

	// GET property op
	public synchronized Acl getNodeAcl(String nodeUri) throws DmtException {
		checkSession();
		Node node = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
		checkOperation(node, Acl.GET, MetaNode.CMD_GET);
		Acl acl = (Acl) acls.get(node);
		return acl == null ? null : acl;
	}

	// GET property op
	public synchronized Acl getEffectiveNodeAcl(String nodeUri)
			throws DmtException {
		checkSession();
		Node node = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
		checkOperation(node, Acl.GET, MetaNode.CMD_GET);
		return getEffectiveNodeAclNoCheck(node);
	}

	// REPLACE property op
	public synchronized void setNodeAcl(String nodeUri, Acl acl)
			throws DmtException {
		checkWriteSession();
		Node node = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);

		// check for REPLACE permission:
		if (isLeafNodeNoCheck(node)) // on the parent node for leaf nodes
			checkNodePermission(node.getParent(), Acl.REPLACE);
		else
			// on the node itself or the parent for interior nodes (parent will
			// be ignored in case of the root node)
			checkNodeOrParentPermission(node, Acl.REPLACE);

		// Not checking REPLACE capability, node does not have to be modifiable
		// to have an ACL associated with it. It should be possible to set
		// ACLs everywhere, and the "Replace" Access Type seems to be given
		// only for modifiable nodes.

		// check that the new ACL is valid
		if (node.isRoot() && (acl == null || !acl.isPermitted("*", Acl.ADD)))
			// should be 405 "Forbidden" according to DMTND 7.7.1.2
			throw new DmtException(node.getUri(),
					DmtException.COMMAND_NOT_ALLOWED, "Root ACL must allow "
							+ "the Add operation for all principals.");

		ReadableDataSession dmtSession = getReadableDataSession(node);
		if ( this.lockMode == LOCK_TYPE_ATOMIC ) {
			// if this is the first change of the node state in this transaction
			// then remember the old state --> initial state at transaction start
			grabInitialNodeState(node, dmtSession);
			updateAclState(node, acl);
		}

		if (acl == null || isEmptyAcl(acl))
			acls.remove(node);
		else
			acls.put(node, acl);


		dmtSession.nodeChanged(node.getPath());

		enqueueEventWithCurrentAcl(DmtEvent.REPLACED, node, null);
	}

	public synchronized MetaNode getMetaNode(String nodeUri)
			throws DmtException {
		checkSession();
		Node node = makeAbsoluteUri(nodeUri);
		checkNodePermission(node, Acl.GET);
		// not checking meta-data for the GET capability, meta-data should
		// always be publicly available
		if (isPureStructuralNode(nodeUri))
			return new ScaffoldMetaNode();
		else
			return getMetaNodeNoCheck(node);
	}

	public synchronized DmtData getNodeValue(String nodeUri)
			throws DmtException {
		checkSession();
		Node node = makeAbsoluteUri(nodeUri);
		if (isPureStructuralNode(nodeUri))
			return null;
		else
			return internalGetNodeValue(node);
	}

	// also used by copy() to pass an already validated Node instead of a URI
	private DmtData internalGetNodeValue(Node node) throws DmtException {
		checkNode(node, SHOULD_EXIST);

		checkOperation(node, Acl.GET, MetaNode.CMD_GET);

		if (!isLeafNodeNoCheck(node)) {
			checkDescendantGetPermissions(node);
			// SD: removed check against ExtensionProperty according to discussion on Bug-Report 1948
//			checkInteriorNodeValueSupport(node);
		}

		ReadableDataSession pluginSession = getReadableDataSession(node);
		DmtData data = pluginSession.getNodeValue(node.getPath());

		boolean isLeafNode = pluginSession.isLeafNode(node.getPath());
		boolean isLeafData = data.getFormat() != DmtData.FORMAT_NODE;
		if (isLeafNode != isLeafData)
			throw new DmtException(
					node.getUri(),
					DmtException.COMMAND_FAILED,
					"Error retrieving node value, the type of the data "
							+ "returned by the plugin does not match the node type.");

		return data;
	}

	private void checkDescendantGetPermissions(Node node) throws DmtException {
		checkNodePermission(node, Acl.GET);
		if (!isLeafNodeNoCheck(node)) {
			String[] children = internalGetChildNodeNames(node);
			// 'children' is [] if there are no child nodes
			for (int i = 0; i < children.length; i++)
				checkDescendantGetPermissions(node.appendSegment(children[i]));
		}
	}

	public synchronized String[] getChildNodeNames(String nodeUri)
			throws DmtException {
		checkSession();
		Node node = makeAbsoluteUri(nodeUri);
		return internalGetChildNodeNames(node);
	}

	// public String[] getChildNodeNames(String[] nodePath) throws DmtException
	// {
	//
	// String[] directNodeNames = null;
	//
	// // do we have a responsible vendorPlugin ?
	// ServiceReference vendorPluginRef = getResponsibleVendorPlugin( nodePath
	// );
	// if ( vendorPluginRef != null ) {
	// VendorPluginInfo pluginInfo = (VendorPluginInfo)
	// vendorPluginRef.getProperty( _PLUGIN_INFO );
	// DmtSession session = getSession(pluginInfo);
	// // and pass the request through
	// directNodeNames = session.getChildNodeNames(toVendorPath(nodePath,
	// pluginInfo));
	// }
	//
	// // we also have to get all structure nodes created for configured
	// root-nodes and
	// // the mapped configurationPathes of other registered VendorPlugins
	//
	// // find all registered instances of MappedPath with this path prefix
	// String myPath = Uri.toUri( nodePath );
	// String filter = "(" + _MAPPED_NODE_PATH + "=" + Uri.toUri( nodePath ) +
	// "/*)";
	// ServiceReference[] refs;
	// try {
	// refs = activator.context.getServiceReferences(
	// MappedPath.class.getName(), filter );
	// } catch (InvalidSyntaxException e) {
	// e.printStackTrace();
	// throw new DmtException( myPath, DmtException.COMMAND_FAILED,
	// "Error in Filter syntax while looking up child names in the registry" );
	// }
	// // TreeSet used to filter out duplicates
	// TreeSet children = new TreeSet();
	// if ( refs != null ) {
	// for (int i = 0; i < refs.length; i++) {
	// String mappedPath = (String) refs[i].getProperty( _MAPPED_NODE_PATH );
	// // cut out the next segment of this path
	// int nextSegment = mappedPath.indexOf( '/', myPath.length() + 1 );
	// if ( nextSegment == -1 )
	// nextSegment = mappedPath.length();
	// String child = mappedPath.substring( myPath.length() + 1, nextSegment );
	// children.add( child );
	// }
	// }
	//
	// Collection direct = directNodeNames != null ? Arrays.asList(
	// directNodeNames ) : new Vector();
	// children.addAll( direct );
	//
	// return (String[]) children.toArray( new String[children.size()]);
	// }

	// also used by copy() to pass an already validated Node instead of a URI
	private String[] internalGetChildNodeNames(Node node) throws DmtException {

		String[] pluginChildNodes = new String[0];
		if (!isPureStructuralNode(node)) {
			checkNode(node, SHOULD_BE_INTERIOR);
			checkOperation(node, Acl.GET, MetaNode.CMD_GET);
			pluginChildNodes = getReadableDataSession(node).getChildNodeNames(
					node.getPath());
		}

		// Segment segment =
		// context.getPluginDispatcher().root.getSegmentFor(node.getPath(), 1);
		Vector<String> v = new Vector<String>();
		Segment segment = context.getPluginDispatcher().findSegment(
				node.getPath());
		if (segment != null) {
			List<Segment> childSegments = segment.getChildren();
			for (Segment child : childSegments)
				v.add(child.getName());
		}
		String[] structuralChildNodes = (String[]) v.toArray(new String[v
				.size()]);

		// // we have to merge the node-names from the plugin with the defined
		// mountpoints
		// Plugin plugin =
		// context.getPluginDispatcher().root.getPluginFor(node.getPath(), 1);
		// Vector v = new Vector();
		// Set<String> mountPoints = plugin.mountPoints;
		// String nodeUri = node.getUri();
		// for ( String mountPoint : mountPoints ) {
		// if ( mountPoint.startsWith(nodeUri) && mountPoint.length() >
		// nodeUri.length()) {
		// // get the next segment name from the mountpoint
		// String[] mp = Uri.toPath( mountPoint );
		// v.add(mp[node.getPath().length]);
		// }
		// }
		// String[] structuralChildNodes = (String[]) v.toArray( new
		// String[v.size()] );

		TreeSet<String> children = new TreeSet<String>();
		children.addAll(normalizeChildNodeNames(pluginChildNodes));
		children.addAll(normalizeChildNodeNames(structuralChildNodes));

		String[] processedChildNodeArray = (String[]) children
				.toArray(new String[children.size()]);
		// ordering is not a requirement, but allows easier testing of plugins
		Arrays.sort(processedChildNodeArray);
		return processedChildNodeArray;
	}

	// GET property op
	public synchronized String getNodeTitle(String nodeUri) throws DmtException {
		checkSession();
		Node node = makeAbsoluteUri(nodeUri);
		if (isPureStructuralNode(nodeUri))
			return null;
		else
			return internalGetNodeTitle(node);
	}

	// also used by copy() to pass an already validated Node instead of a URI
	private String internalGetNodeTitle(Node node) throws DmtException {
		checkNode(node, SHOULD_EXIST);
		checkOperation(node, Acl.GET, MetaNode.CMD_GET);
		return getReadableDataSession(node).getNodeTitle(node.getPath());
	}

	// GET property op
	public synchronized int getNodeVersion(String nodeUri) throws DmtException {
		checkSession();
		Node node = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
		checkOperation(node, Acl.GET, MetaNode.CMD_GET);
		if (isPureStructuralNode(nodeUri))
			return 0;
		else
			return getReadableDataSession(node).getNodeVersion(node.getPath());
	}

	// GET property op
	public synchronized Date getNodeTimestamp(String nodeUri)
			throws DmtException {
		checkSession();
		Node node = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
		checkOperation(node, Acl.GET, MetaNode.CMD_GET);
		if (isPureStructuralNode(node))
			throw new DmtException(node.getPath(),
					DmtException.FEATURE_NOT_SUPPORTED,
					"Timestamp is not available for this node from the DMTSubtree.");

		return getReadableDataSession(node).getNodeTimestamp(node.getPath());
	}

	// GET property op
	public synchronized int getNodeSize(String nodeUri) throws DmtException {
		checkSession();
		Node node = makeAbsoluteUriAndCheck(nodeUri, SHOULD_BE_LEAF);
		checkOperation(node, Acl.GET, MetaNode.CMD_GET);
		if (isPureStructuralNode(nodeUri))
			return 0;
		else
			return getReadableDataSession(node).getNodeSize(node.getPath());
	}

	// GET property op
	public synchronized String getNodeType(String nodeUri) throws DmtException {
		checkSession();
		Node node = makeAbsoluteUri(nodeUri);
		if (isPureStructuralNode(nodeUri))
			return DmtConstants.DDF_SCAFFOLD;
		else
			return internalGetNodeType(node);
	}

	// also used by copy() to pass an already validated Node instead of a URI
	private String internalGetNodeType(Node node) throws DmtException {
		checkNode(node, SHOULD_EXIST);
		checkOperation(node, Acl.GET, MetaNode.CMD_GET);
		return getReadableDataSession(node).getNodeType(node.getPath());
	}

	// REPLACE property op
	public synchronized void setNodeTitle(String nodeUri, String title)
			throws DmtException {
		checkWriteSession();
		Node node = makeAbsoluteUri(nodeUri);

		if (isPureStructuralNode(nodeUri))
			throw new DmtException(node.getPath(),
					DmtException.PERMISSION_DENIED,
					"setting of the node title is not allowed for node: "
							+ node.getPath());

		internalSetNodeTitle(node, title, true); // send event if successful
	}

	// also used by copy() to pass an already validated Node instead of a URI
	// and to set the node title without triggering an event
	private void internalSetNodeTitle(Node node, String title, boolean sendEvent)
			throws DmtException {

		checkNode(node, SHOULD_EXIST);
		checkOperation(node, Acl.REPLACE, MetaNode.CMD_REPLACE);

		try {
			if (title != null && title.getBytes("UTF-8").length > 255)
				throw new DmtException(node.getUri(),
						DmtException.COMMAND_FAILED,
						"Length of Title property exceeds 255 bytes (UTF-8).");
		} catch (UnsupportedEncodingException e) {
			// never happens
		}

		// if this is an atomic session then we keep the hashCode of the old title, 
		// in order to determine effective changes to the tree at commit time
		ReadWriteDataSession dmtSession = getReadWriteDataSession(node);
		if ( this.lockMode == LOCK_TYPE_ATOMIC ) {
			// if this is the first change of the node state in this transaction
			// then remember the old state --> initial state at transaction start
			grabInitialNodeState(node, dmtSession);
			updateTitleState(node, title);
		}

		dmtSession.setNodeTitle(node.getPath(), title);
		if (sendEvent)
			enqueueEventWithCurrentAcl(DmtEvent.REPLACED, node, null);
	}



	public synchronized void setNodeValue(String nodeUri, DmtData data)
			throws DmtException {
		if (isPureStructuralNode(nodeUri)) {
			Node node = makeAbsoluteUri(nodeUri);
			throw new DmtException(node.getPath(),
					DmtException.PERMISSION_DENIED,
					"setting of the node value is not allowed for node: "
							+ node.getPath());
		}
		commonSetNodeValue(nodeUri, data);
	}

	public synchronized void setDefaultNodeValue(String nodeUri)
			throws DmtException {
		commonSetNodeValue(nodeUri, null);
	}

	private void commonSetNodeValue(String nodeUri, DmtData data)
			throws DmtException {
		checkWriteSession();

		int nodeConstraint = data == null ? SHOULD_EXIST
				: data.getFormat() == DmtData.FORMAT_NODE ? SHOULD_BE_INTERIOR
						: SHOULD_BE_LEAF;

		Node node = makeAbsoluteUriAndCheck(nodeUri, nodeConstraint);
		checkOperation(node, Acl.REPLACE, MetaNode.CMD_REPLACE);

		// SD: removed check against ExtensionProperty according to discussion on Bug-Report 1948
//		if (!isLeafNodeNoCheck(node))
//			checkInteriorNodeValueSupport(node);

		// check data against meta-data
		checkValue(node, data);

		// SD: 22.10.2010: according to the discussions on BUG 1658,
		// setting a value on PERMANENT nodes must be permitted,
		// if the operation checks (above) where successful
		
		// MetaNode metaNode = getMetaNodeNoCheck(node);
		// if (metaNode != null && metaNode.getScope() == MetaNode.PERMANENT)
		// throw new DmtException(node.getUri(), DmtException.METADATA_MISMATCH,
		// "Cannot set the value of a permanent node.");

		ReadWriteDataSession dmtSession = getReadWriteDataSession(node);
		if ( this.lockMode == LOCK_TYPE_ATOMIC ) {
			// if this is the first change of the node state in this transaction
			// then remember the old state --> initial state at transaction start
			grabInitialNodeState(node, dmtSession);
			updateValueState(node, data);
		}
		
		dmtSession.setNodeValue(node.getPath(), data);

		traverseEvents(DmtEvent.REPLACED, node);
	}

	// SD 30.03.2011: changed according to spec. 117.13.9.41:
	//	An Event of type REPLACE is sent out for a leaf node. A replaced interior
	//	node sends out events for each of its children in depth first order and node
	//	names sorted with Arrays.sort(String[]). When setting a value on an interior
	//	node, the values of the leaf nodes under it can change, but the structure of
	//	the subtree is not modified by the operation.
	// --> only add events for leaf nodes in "depth first order", not whole subtree
	private void traverseEvents(int mode, Node node) throws DmtException {
		if (isLeafNodeNoCheck(node))
			enqueueEventWithCurrentAcl(mode, node, null);
		else {
			String children[] = internalGetChildNodeNames(node);
			Arrays.sort(children);
			for (int i = 0; i < children.length; i++) {
//				traverseEvents(mode, node.appendSegment(children[i]));
				Node child = node.appendSegment(children[i]);
				if ( isLeafNodeNoCheck(child) )
					enqueueEventWithCurrentAcl(mode, child, null);
			}
		}
	}

	// SyncML DMTND 7.5 (p16) Type: only the Get command is applicable!
	public synchronized void setNodeType(String nodeUri, String type)
			throws DmtException {
		checkWriteSession();
		if (isPureStructuralNode(nodeUri)) {
			Node node = makeAbsoluteUri(nodeUri);
			throw new DmtException(node.getPath(),
					DmtException.PERMISSION_DENIED,
					"setting of the node type is not allowed for node: "
							+ node.getPath());
		}

		Node node = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
		checkOperation(node, Acl.REPLACE, MetaNode.CMD_REPLACE);

		MetaNode metaNode = getMetaNodeNoCheck(node);
		if (metaNode != null && metaNode.getScope() == MetaNode.PERMANENT)
			throw new DmtException(node.getUri(),
					DmtException.METADATA_MISMATCH,
					"Cannot set type property of permanent node.");

		if (isLeafNodeNoCheck(node))
			checkMimeType(node, type);

		ReadWriteDataSession dmtSession = getReadWriteDataSession(node);
		if ( this.lockMode == LOCK_TYPE_ATOMIC ) {
			// if this is the first change of the node state in this transaction
			// then remember the old state --> initial state at transaction start
			grabInitialNodeState(node, dmtSession);
			updateTypeState(node, type);
		}

		// could check type string for interior nodes, but this impl. does not
		// handle it anyway, so we leave it to the plugins if they need it
		// (same in createInteriorNode/2)

		dmtSession.setNodeType(node.getPath(), type);
		enqueueEventWithCurrentAcl(DmtEvent.REPLACED, node, null);
	}

	public synchronized void deleteNode(String nodeUri) throws DmtException {
		checkWriteSession();
		if (isPureStructuralNode(nodeUri)) {
			Node node = makeAbsoluteUri(nodeUri);
			throw new DmtException(node.getPath(),
					DmtException.PERMISSION_DENIED,
					"deleting is not allowed for node: " + node.getPath());
		}

		Node node = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);

		// sub-case of the next check, but gives a more specific error
		if (node.isRoot())
			throw new DmtException(node.getUri(),
					DmtException.COMMAND_NOT_ALLOWED,
					"Cannot delete root node.");

		if (node.equals(subtreeNode))
			throw new DmtException(node.getUri(),
					DmtException.COMMAND_NOT_ALLOWED,
					"Cannot delete root node of the session.");

		checkOperation(node, Acl.DELETE, MetaNode.CMD_DELETE);

		MetaNode metaNode = getMetaNodeNoCheck(node);
		if (metaNode != null) {
			if (metaNode.getScope() == MetaNode.PERMANENT)
				throw new DmtException(node.getUri(),
						DmtException.METADATA_MISMATCH,
						"Cannot delete permanent node.");

			if (!metaNode.isZeroOccurrenceAllowed()) {
				// maxOccurrence == 1 means that there cannot be other instances
				// of this node, so it cannot be deleted. If maxOccurrence > 1
				// then we have to check whether this is the last one.
				if (metaNode.getMaxOccurrence() == 1)
					throw new DmtException(node.getUri(),
							DmtException.METADATA_MISMATCH,
							"Metadata does not allow deleting the only "
									+ "instance of this node.");
				checkNodeIsInSession(node.getParent(), "(needed to determine"
						+ "the number of siblings of the given node) ");
				if (getNodeCardinality(node) == 1)
					throw new DmtException(node.getUri(),
							DmtException.METADATA_MISMATCH,
							"Metadata does not allow deleting the last "
									+ "instance of this node.");
			}
		}

		ReadWriteDataSession dmtSession = getReadWriteDataSession(node);
		List subtreeEvents = new ArrayList();
		// In case of an atomic session, we have to remember the full subtree of deleted nodes.
		// If e.g. a full subtree ./A/B/C is deleted and then the just top of the deleted subtree ./A/B/C
		// is re-created in the same transaction, we still have to send a DELETED event 
		// for the rest of the deleted subtree (i.e. all children of ./A/B/C).
		if ( this.lockMode == LOCK_TYPE_ATOMIC ) {
			// if a node is deleted, that was create by previous COPIED operation (the newNodeUri)
			// then we don't add all subtree-events
			if ( ! eventStore.isNewEventNode(node, DmtEvent.COPIED )) {
				try {
					fillSubtreeEventList(subtreeEvents, dmtSession, node, true);
				} catch (Exception e) {}
			}
			getActualNodeStates().remove(node);
		}

		// get the initial state of the current node before it is deleted
		grabInitialNodeState(node, dmtSession);
		
		dmtSession.deleteNode(node.getPath());
		Acl acl = getEffectiveNodeAclNoCheck(node);
		moveAclEntries(node, null);
		enqueueEvent(DmtEvent.DELETED, node, null, acl);
		// if delete was successful, then enqueue events for whole subtree
		Iterator i = subtreeEvents.iterator();
		while (i.hasNext()) {
			Node subNode = (Node) i.next();
			acl = getEffectiveNodeAclNoCheck(subNode);
			moveAclEntries(subNode, null);
			enqueueEvent(DmtEvent.DELETED, subNode, null, acl);
		}
	}

	// TODO: do we need to check and store the ACL also before deletion?
	private void fillSubtreeEventList( List eventList, ReadWriteDataSession session, Node node, boolean first ) throws Exception {
		if ( ! session.isLeafNode(node.getPath()) ) {
			String children[] = internalGetChildNodeNames(node);
			for (int i = 0; i < children.length; i++)
				fillSubtreeEventList(eventList, session, node.appendSegment(children[i]), false);
		}
		if ( ! first ) {
			grabInitialNodeState(node, session);
			eventList.add( node );
			getActualNodeStates().remove(node);
		}
	}

	public synchronized void createInteriorNode(String nodeUri)
			throws DmtException {
		checkWriteSession();
		Node node = makeAbsoluteUri(nodeUri);

		commonCreateInteriorNode(node, null, true, false);
	}

	public synchronized void createInteriorNode(String nodeUri, String type)
			throws DmtException {
		checkWriteSession();
		Node node = makeAbsoluteUri(nodeUri);
		commonCreateInteriorNode(node, type, true, false);
	}

	// - used by the other createInteriorNode variants
	// - also used by copy() to pass an already validated Node instead of a URI
	// and to create interior nodes without triggering an event
	// - also used by ensureInteriorAncestors, to create missing nodes while
	// skipping automatically created nodes
	private void commonCreateInteriorNode(Node node, String type,
			boolean sendEvent, boolean skipAutomatic) throws DmtException {
		if (isPureStructuralNode(node)) {
			throw new DmtException(node.getPath(),
					DmtException.PERMISSION_DENIED,
					"creation of interior nodes is not allowed for node: "
							+ node.getPath());
		}

		checkNode(node, SHOULD_NOT_EXIST);

		Node parent = node.getParent();
		if (parent == null) // this should never happen, root must always exist
			throw new DmtException(node.getUri(), DmtException.COMMAND_FAILED,
					"Cannot create root node.");

		// Return silently if all of the following conditions are met:
		// - the parent node has been created while ensuring that the ancestor
		// nodes all exist
		// - this call is part of creating the ancestors for some sub-node (as
		// indicated by 'skipAutomatic')
		// - this current node was created automatically, triggered by the
		// creation of the parent (i.e. it has AUTOMATIC scope)
		if (ensureInteriorAncestors(parent, sendEvent) && skipAutomatic
				&& getReadableDataSession(node).isNodeUri(node.getPath()))
			return;

		checkNodePermission(parent, Acl.ADD);
		checkNodeCapability(node, MetaNode.CMD_ADD);

		MetaNode metaNode = getMetaNodeNoCheck(node);
		if (metaNode != null && metaNode.isLeaf())
			throw new DmtException(node.getUri(),
					DmtException.METADATA_MISMATCH,
					"Cannot create the specified interior node, "
							+ "meta-data defines it as a leaf node.");

		// could check type string, but this impl. does not handle it anyway
		// so we leave it to the plugins if they need it (same in setNodeType)

		checkNewNode(node);
		checkMaxOccurrence(node);

		// it is not really useful to allow creating automatic nodes, but this
		// is not a hard requirement, and should be enforced by the (lack of
		// the) ADD access type instead

		getReadWriteDataSession(node).createInteriorNode(node.getPath(), type);
		if ( this.lockMode == LOCK_TYPE_ATOMIC )
			// update node state (creates a new node state if necessary)
			createNodeState(node, type);

		assignNewNodePermissions(node, parent);
		if (sendEvent)
			enqueueEventWithCurrentAcl(DmtEvent.ADDED, node, null);
	}

	public synchronized void createLeafNode(String nodeUri) throws DmtException {
		// not calling createLeafNode/3, because it is wrapped
		checkWriteSession();
		Node node = makeAbsoluteUri(nodeUri);
		commonCreateLeafNode(node, null, null, true);
	}

	public synchronized void createLeafNode(String nodeUri, DmtData value)
			throws DmtException {
		// not calling createLeafNode/3, because it is wrapped
		checkWriteSession();
		Node node = makeAbsoluteUri(nodeUri);
		commonCreateLeafNode(node, value, null, true);
	}

	public synchronized void createLeafNode(String nodeUri, DmtData value,
			String mimeType) throws DmtException {
		checkWriteSession();
		Node node = makeAbsoluteUri(nodeUri);
		commonCreateLeafNode(node, value, mimeType, true);
	}

	// - used by the other createLeafNode variants
	// - also used by copy() to pass an already validated Node instead of a URI
	// and to create leaf nodes without triggering an event
	private void commonCreateLeafNode(Node node, DmtData value,
			String mimeType, boolean sendEvent) throws DmtException {
		if (isPureStructuralNode(node)) {
			throw new DmtException(node.getPath(),
					DmtException.PERMISSION_DENIED,
					"creation of leaf nodes is not allowed for node: "
							+ node.getPath());
		}

		checkNode(node, SHOULD_NOT_EXIST);

		Node parent = node.getParent();
		if (parent == null) // this should never happen, root must always exist
			throw new DmtException(node.getUri(), DmtException.COMMAND_FAILED,
					"Cannot create root node.");

		ensureInteriorAncestors(parent, sendEvent);

		checkNodePermission(parent, Acl.ADD);
		checkNodeCapability(node, MetaNode.CMD_ADD);

		MetaNode metaNode = getMetaNodeNoCheck(node);
		if (metaNode != null && !metaNode.isLeaf())
			throw new DmtException(node.getUri(),
					DmtException.METADATA_MISMATCH,
					"Cannot create the specified leaf node, meta-data "
							+ "defines it as an interior node.");
		checkNewNode(node);
		checkValue(node, value);
		checkMimeType(node, mimeType);
		checkMaxOccurrence(node);

		// it is not really useful to allow creating automatic nodes, but this
		// is not a hard requirement, and should be enforced by the (lack of
		// the) ADD access type instead

		getReadWriteDataSession(node).createLeafNode(node.getPath(), value,
				mimeType);

		if (sendEvent) {
			// if this is an atomic session then we keep the hashCode of the old value, 
			// in order to determine the correct event sequence afterwards
			if ( this.lockMode == LOCK_TYPE_ATOMIC )
				// update node state (creates a new node state if necessary)
				createNodeState(node, value);
			enqueueEventWithCurrentAcl(DmtEvent.ADDED, node, null);
		}
	}

	// Tree may be left in an inconsistent state if there is an error when only
	// part of the tree has been copied.
	public synchronized void copy(String nodeUri, String newNodeUri,
			boolean recursive) throws DmtException {
		checkWriteSession();
		if (isPureStructuralNode(nodeUri)) {
			Node node = makeAbsoluteUri(nodeUri);
			throw new DmtException(node.getPath(),
					DmtException.COMMAND_NOT_ALLOWED,
					"copy actions are not allowed for node: " + node.getPath());
		}

		// TODO: Bundlefest simplification

		Node node = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
		Node newNode = makeAbsoluteUriAndCheck(newNodeUri, SHOULD_NOT_EXIST);
		if (node.isAncestorOf(newNode))
			throw new DmtException(node.getUri(),
					DmtException.COMMAND_NOT_ALLOWED,
					"Cannot copy node to its descendant, '" + newNode + "'.");

		// SD: This check is not valid anymore in the new implementation,
		// because different parts of a nodes subtree can be handled by
		// different plugins
		// rather check for existing mountpoints below this node

		// if (context.getPluginDispatcher()
		// .handledBySameDataPlugin(node, newNode)) {
		Plugin plugin = context.getPluginDispatcher().getDataPluginFor(
				Uri.toPath(nodeUri));
		if ((plugin.getMountPoints() == null || plugin.getMountPoints().size() == 0)) {

			Node newParentNode = newNode.getParent();
			// newParentNode cannot be null, because newNode is a valid absolute
			// nonexisting node, so it cannot be the root

			ensureInteriorAncestors(newParentNode, false);

			// DMTND 7.7.1.5: "needs correct access rights for the equivalent
			// Add, Delete, Get, and Replace commands"
			copyPermissionCheck(node, newParentNode, newNode, recursive);

			checkNodeCapability(node, MetaNode.CMD_GET);
			checkNodeCapability(newNode, MetaNode.CMD_ADD);

			checkNewNode(newNode);
			checkMaxOccurrence(newNode);

			// for leaf nodes: since we are not passing a data object to the
			// plugin, checking the value and mime-type against the new
			// meta-data is the responsibility of the plugin itself

			try {
				getReadWriteDataSession(newNode).copy(node.getPath(),
						newNode.getPath(), recursive);
				assignNewNodePermissions(newNode, newParentNode);
			} catch (DmtException e) {
				// fall back to generic algorithm if plugin doesn't support copy
				if (e.getCode() != DmtException.FEATURE_NOT_SUPPORTED)
					throw e;

				// the above checks will be performed again, but we cannot even
				// attempt to call the plugin without them
				copyNoCheck(node, newNode, recursive);
			}
		} else
			copyNoCheck(node, newNode, recursive); // does not trigger events

		Acl acl = getEffectiveNodeAclNoCheck(node);
		Acl newAcl = getEffectiveNodeAclNoCheck(newNode);
		Acl mergedAcl = mergeAcls(acl, newAcl);
//		if ( lockMode == DmtSession.LOCK_TYPE_ATOMIC ) {
//			// if a node is copied that did not exist, when the session was opened,
//			// then this node must be signalled as ADDED when the session is committed 
//			if ( getInitialNodeStates().contains(node) )
//				enqueueEvent(DmtEvent.COPIED, node, newNode, mergedAcl);
//			else
//				enqueueEvent(DmtEvent.ADDED, newNode, null, mergedAcl);
//		}
//		else 
		if ( lockMode == DmtSession.LOCK_TYPE_ATOMIC && 
				! (eventStore.isEventNode(node, DmtEvent.ADDED ) || eventStore.isNewEventNode(node, DmtEvent.COPIED)))
			enqueueEvent(DmtEvent.COPIED, node, newNode, mergedAcl);
	}

	public synchronized void renameNode(String nodeUri, String newNodeName)
			throws DmtException {
		checkWriteSession();
		if (isPureStructuralNode(nodeUri)) {
			Node node = makeAbsoluteUri(nodeUri);
			throw new DmtException(node.getPath(),
					DmtException.PERMISSION_DENIED,
					"renaming actions are not allowed for node: "
							+ node.getPath());
		}

		Node node = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
		Node parent = node.getParent();

		// sub-case of the next check, but gives a more specific error
		if (parent == null)
			throw new DmtException(node.getUri(),
					DmtException.COMMAND_NOT_ALLOWED,
					"Cannot rename root node.");

		// SD: reordered a bit
		checkOperation(node, Acl.REPLACE, MetaNode.CMD_REPLACE);

		if (node.equals(subtreeNode))
			throw new DmtException(node.getUri(),
					DmtException.COMMAND_NOT_ALLOWED,
					"Cannot rename root node of the session.");

		String newName = Node.validateAndNormalizeNodeName(newNodeName);
		Node newNode = parent.appendSegment(newName);
		checkNode(newNode, SHOULD_NOT_EXIST);
		checkNewNode(newNode);

		MetaNode metaNode = getMetaNodeNoCheck(node);
		MetaNode newMetaNode = getMetaNodeNoCheck(newNode);

		if (metaNode != null) {
			if (metaNode.getScope() == MetaNode.PERMANENT)
				throw new DmtException(node.getUri(),
						DmtException.METADATA_MISMATCH,
						"Cannot rename permanent node.");

			int maxOcc = metaNode.getMaxOccurrence();

			// sanity check: all siblings of a node must either have a
			// cardinality of 1, or they must be part of the same multi-node
			if (newMetaNode != null && maxOcc != newMetaNode.getMaxOccurrence())
				throw new DmtException(
						node.getUri(),
						DmtException.COMMAND_FAILED,
						"Cannot rename node, illegal meta-data found (a "
								+ "member of a multi-node has a sibling with different "
								+ "meta-data).");

			// if this is a multi-node (maxOcc > 1), renaming does not affect
			// the cardinality
			if (maxOcc == 1 && !metaNode.isZeroOccurrenceAllowed())
				throw new DmtException(node.getUri(),
						DmtException.METADATA_MISMATCH,
						"Metadata does not allow deleting last instance of "
								+ "this node.");
		}

		// the new node must be the same (leaf/interior) as the original
		if (newMetaNode != null
				&& newMetaNode.isLeaf() != isLeafNodeNoCheck(node))
			throw new DmtException(
					newNode.getUri(),
					DmtException.METADATA_MISMATCH,
					"The destination of the rename operation is "
							+ (newMetaNode.isLeaf() ? "a leaf" : "an interior")
							+ " node according to the meta-data, which does not match "
							+ "the source node.");

		// for leaf nodes: since we are not passing a data object to the
		// plugin, checking the value and mime-type against the new
		// meta-data is the responsibility of the plugin itself
		ReadWriteDataSession dmtSession = getReadWriteDataSession(node);
		if ( this.lockMode == DmtSession.LOCK_TYPE_ATOMIC )
			grabInitialNodeState(node, dmtSession);

		dmtSession.renameNode(node.getPath(), newName);
		Acl acl = getEffectiveNodeAclNoCheck(node);
		moveAclEntries(node, newNode);
		enqueueEvent(DmtEvent.RENAMED, node, newNode, acl);
	}

	/**
	 * Create an Access Control Context based on the given permissions. The
	 * Permission objects are first created from the PermissionInfo objects,
	 * then added to a permission collection, which is added to a protection
	 * domain with no code source, which is used to create the access control
	 * context. If the <code>null</code> argument is given, an empty access
	 * control context is created.
	 * 
	 * @throws Exception
	 *             if there is an error creating one of the permission objects
	 *             (can be one of ClassNotFoundException, SecurityException,
	 *             NoSuchMethodException, ClassCastException,
	 *             IllegalArgumentException, InstantiationException,
	 *             IllegalAccessException or InvocationTargetException)
	 */
	private AccessControlContext getSecurityContext(PermissionInfo[] permissions)
			throws Exception {

		PermissionCollection permissionCollection = new Permissions();

		if (permissions != null)
			for (int i = 0; i < permissions.length; i++) {
				PermissionInfo info = permissions[i];

				Class permissionClass = Class.forName(info.getType());
				Constructor constructor = permissionClass
						.getConstructor(PERMISSION_CONSTRUCTOR_SIG);
				Permission permission = (Permission) constructor
						.newInstance(new Object[] { info.getName(),
								info.getActions() });
				permissionCollection.add(permission);
			}

		return new AccessControlContext(
				new ProtectionDomain[] { new ProtectionDomain(null,
						permissionCollection) });
	}

	private void checkSession() {
		if (state != STATE_OPEN)
			throw new DmtIllegalStateException(
					"Session is not open, cannot perform DMT operations.");
	}

	private void checkWriteSession() {
		checkWriteSession("write");
	}

	private void checkWriteSession(String op) {
		checkSession();
		if (lockMode == LOCK_TYPE_SHARED)
			throw new DmtIllegalStateException(
					"Session is not open for writing, cannot perform "
							+ "requested " + op + " operation.");
	}

	private Acl mergeAcls(Acl acl1, Acl acl2) {
		Acl mergedAcl = acl1;

		String[] principals = acl2.getPrincipals();
		for (int i = 0; i < principals.length; i++)
			mergedAcl = mergedAcl.addPermission(principals[i],
					acl2.getPermissions(principals[i]));
		mergedAcl.addPermission("*", acl2.getPermissions("*"));

		return mergedAcl;
	}

	private void enqueueEventWithCurrentAcl(int type, Node node, Node newNode) {
		Acl acl = null;
		if (node != null)
			acl = getEffectiveNodeAclNoCheck(node);

		enqueueEvent(type, node, newNode, acl);
	}

	private void enqueueEvent(int type, Node node, Node newNode, Acl acl) {
		boolean isAtomic = lockMode == LOCK_TYPE_ATOMIC;
		eventStore.add(type, node, newNode, acl, isAtomic);
	}

	private boolean isLeafNodeNoCheck(Node node) throws DmtException {
		return getReadableDataSession(node).isLeafNode(node.getPath());
	}

	private MetaNode getMetaNodeNoCheck(Node node) throws DmtException {
		return getReadableDataSession(node).getMetaNode(node.getPath());
	}

	// precondition: 'uri' must point be valid (checked with isNodeUri or
	// returned by getChildNodeNames)
	private void copyNoCheck(Node node, Node newNode, boolean recursive)
			throws DmtException {
		boolean isLeaf = isLeafNodeNoCheck(node);
		String type = internalGetNodeType(node);

		DmtData value = null;
		// create new node (without sending a separate event about it)
		if (isLeaf) {
			// if getNodeValue() returns null, we attempt to set the default
			value = internalGetNodeValue(node);
			commonCreateLeafNode(newNode, value, type, false);
		}
		else
			commonCreateInteriorNode(newNode, type, false, false);

		// if this is an atomic session then we keep the hashCode of the old value, 
		// in order to determine the correct event sequence afterwards
		if ( this.lockMode == LOCK_TYPE_ATOMIC ) {
			// if a node is copied, that was not existing at the beginning of the session,
			// (i.e. is the result of a previous ADDED or COPIED operation)
			// then the copied/new node must be notified as ADDED in the events at commit-time
			if ( eventStore.isEventNode(node, DmtEvent.ADDED ) || eventStore.isNewEventNode(node, DmtEvent.COPIED)) {
				createNodeState(newNode, type);
				if ( value != null )
					updateValueState(newNode, value);
				enqueueEventWithCurrentAcl(DmtEvent.ADDED, newNode, null);
			}
		}

		// copy Title property (without sending event) if it is supported by
		// both source and target plugins
		try {
			String title = internalGetNodeTitle(node);
			// It could be valid to copy "null" Titles as well, if the
			// implementation has default values for the Title property.
			if (title != null)
				internalSetNodeTitle(newNode, title, false);
		} catch (DmtException e) {
			if (e.getCode() != DmtException.FEATURE_NOT_SUPPORTED)
				throw new DmtException(node.getUri(),
						DmtException.COMMAND_FAILED, "Error copying node to '"
								+ newNode + "', cannot copy title.", e);
		}

		// Format, Name, Size, TStamp and VerNo properties do not need to be
		// expicitly copied

		// copy children if mode is recursive and node is interior
		if (recursive && !isLeaf) {
			// 'children' is [] if there are no child nodes
			String[] children = internalGetChildNodeNames(node);
			for (int i = 0; i < children.length; i++)
				copyNoCheck(node.appendSegment(children[i]),
						newNode.appendSegment(children[i]), true);
		}
	}

	// precondition: path must be absolute, and the parent of the given node
	// must be within the subtree of the session
	private int getNodeCardinality(Node node) throws DmtException {
		Node parent = node.getParent();
		String[] neighbours = getReadableDataSession(parent).getChildNodeNames(
				parent.getPath());
		return normalizeChildNodeNames(neighbours).size();
	}

	private void assignNewNodePermissions(Node node, Node parent)
			throws DmtException {
		// DMTND 7.7.1.3: if parent does not have Replace permissions, give Add,
		// Delete and Replace permissions to child. (This rule cannot be
		// applied to Java permissions, only to ACLs.)
		if (principal != null) {
			try {
				checkNodePermission(parent, Acl.REPLACE);
			} catch (DmtException e) {
				if (e.getCode() != DmtException.PERMISSION_DENIED)
					throw e; // should not happen
				Acl parentAcl = getEffectiveNodeAclNoCheck(parent);
				Acl newAcl = parentAcl.addPermission(principal, Acl.ADD
						| Acl.DELETE | Acl.REPLACE);
				acls.put(node, newAcl);
			}
		}
	}

	private void checkOperation(Node node, int actions, int capability)
			throws DmtException {
		checkNodePermission(node, actions);
		checkNodeCapability(node, capability);
	}

	// throws SecurityException if principal is local user, and sufficient
	// privileges are missing
	private void checkNodePermission(Node node, int actions)
			throws DmtException {
		checkNodeOrParentPermission(principal, node, actions, false);
	}

	// throws SecurityException if principal is local user, and sufficient
	// privileges are missing
	private void checkNodeOrParentPermission(Node node, int actions)
			throws DmtException {
		checkNodeOrParentPermission(principal, node, actions, true);
	}

	// Performs the necessary permission checks for a copy operation:
	// - checks that the caller has GET rights (ACL or Java permission) for all
	// source nodes
	// - in case of local sessions, checks that the caller has REPLACE Java
	// permissions on all nodes where a title needs to be set, and ADD Java
	// permissions for the parents of all added nodes
	// - in case of remote sessions, only the ACL of the parent of the target
	// node needs to be checked, because ACLs cannot be set for nonexitent
	// nodes; in this case the ADD ACL is always required, while REPLACE is
	// checked only if any of the copied nodes has a non-null Title string
	//
	// Precondition: 'node' must point be valid (checked with isNodeUri or
	// returned by getChildNodeNames)
	private void copyPermissionCheck(Node node, Node newParentNode,
			Node newNode, boolean recursive) throws DmtException {
		boolean hasTitle = copyPermissionCheckRecursive(node, newParentNode,
				newNode, recursive);

		// ACL not copied, so the parent of the target node only needs
		// REPLACE permission if the copied node (or any node in the copied
		// subtree) has a title

		// remote access permissions for the target only need to be checked once
		if (principal != null) {
			checkNodePermission(newParentNode, Acl.ADD);
			if (hasTitle)
				checkNodePermission(newNode, Acl.REPLACE);
		}
	}

	private boolean copyPermissionCheckRecursive(Node node, Node newParentNode,
			Node newNode, boolean recursive) throws DmtException {

		// check that the caller has GET rights for the current node
		checkNodePermission(node, Acl.GET);

		// check whether the node has a non-null title
		boolean hasTitle = nodeHasTitle(node);

		// local access permissions need to be checked for each target node
		if (principal == null) {
			checkLocalPermission(newParentNode, writeAclCommands(Acl.ADD));
			if (hasTitle)
				checkLocalPermission(newNode, writeAclCommands(Acl.REPLACE));
		}

		// perform the checks recursively for the subtree if requested
		if (recursive && !isLeafNodeNoCheck(node)) {
			// 'children' is [] if there are no child nodes
			String[] children = internalGetChildNodeNames(node);
			for (int i = 0; i < children.length; i++)
				if (copyPermissionCheckRecursive(
						node.appendSegment(children[i]), newNode,
						newNode.appendSegment(children[i]), true))
					hasTitle = true;
		}

		return hasTitle;
	}

	// Returns true if the plugin handling the given node supports the Title
	// property and value of the property is non-null. This is used for
	// determining whether the caller needs to have REPLACE rights for the
	// target node of the enclosing copy operation.
	private boolean nodeHasTitle(Node node) throws DmtException {
		try {
			return internalGetNodeTitle(node) != null;
		} catch (DmtException e) {
			// FEATURE_NOT_SUPPORTED means that Title is not supported
			if (e.getCode() != DmtException.FEATURE_NOT_SUPPORTED)
				throw e;
		}

		return false;
	}

	private Node makeAbsoluteUriAndCheck(String nodeUri, int check)
			throws DmtException {
		Node node = makeAbsoluteUri(nodeUri);
		checkNode(node, check);
		return node;
	}

	// returns a plugin for read-only use
	private ReadableDataSession getReadableDataSession(Node node)
			throws DmtException {
		return getPluginSession(node, false);
	}

	// returns a plugin for writing
	private ReadWriteDataSession getReadWriteDataSession(Node node)
			throws DmtException {
		return getPluginSession(node, true);
	}

	// precondition: if 'writable' is true, session lock type must not be shared
	// 'synchronized' is just indication, all entry points are synch'd anyway
	private synchronized PluginSessionWrapper getPluginSession(Node node,
			boolean writeOperation) throws DmtException {

		PluginSessionWrapper wrappedPlugin = null;
		Node wrappedPluginRoot = null;

		// Look through the open plugin sessions, and find the session with the
		// lowest root that handles the given node.
		Iterator i = dataPlugins.iterator();
		while (i.hasNext()) {
			PluginSessionWrapper plugin = (PluginSessionWrapper) i.next();
			Node pluginRoot = plugin.getSessionRoot();
			if (pluginRoot.isAncestorOf(node)
					&& (wrappedPluginRoot == null || wrappedPluginRoot
							.isAncestorOf(pluginRoot))) {
				wrappedPlugin = plugin;
				wrappedPluginRoot = pluginRoot;
			}
		}

		// // Find the plugin that would/will handle the given node, and the
		// root
		// // of the (potential) session opened on it.
		// PluginRegistration pluginRegistration =
		// context.getPluginDispatcher().getDataPlugin(node);

		// SD: path must be absolute
		// get the reference of the responsible plugin from the new dispatcher
		Plugin dispatcherPlugin = context.getPluginDispatcher()
				.getDataPluginFor(node.getPath());
		Node root = getLongestRootForPlugin(dispatcherPlugin, node);

		// If we found a plugin session handling the node, and the potential
		// new plugin session root (defined by 'root') is not in its subtree,
		// then use the open session. If there is no session yet, or if a new
		// session could be opened with a deeper root, then a new session is
		// opened. (This guarantees that the proper plugin is used instead of
		// the root plugin for nodes below the "root tree".)
		if (wrappedPlugin != null
				&& !wrappedPluginRoot.isAncestorOf(root, true)) {
			if (writeOperation
					&& wrappedPlugin.getSessionType() == LOCK_TYPE_SHARED)
				throw getWriteException(lockMode, node);
			return wrappedPlugin;
		}

		// No previously opened session found or another plugin that matches
		// with a longer path, attempting to open session with
		// correct lock type.

		DataPlugin plugin = (DataPlugin) context.getBundleContext().getService(
				dispatcherPlugin.getReference());
		ReadableDataSession pluginSession = null;
		int pluginSessionType = lockMode;

		if (lockMode != LOCK_TYPE_SHARED) {
			pluginSession = openPluginSession(plugin, root, pluginSessionType);
			if (pluginSession == null && writeOperation)
				throw getWriteException(lockMode, node);
		}

		// read-only session if lockMode is LOCK_TYPE_SHARED, or if the
		// plugin did not support the writing lock mode, and the current
		// operation is for reading
		if (pluginSession == null) {
			pluginSessionType = LOCK_TYPE_SHARED;
			pluginSession = openPluginSession(plugin, root, pluginSessionType);
		}

		wrappedPlugin = new PluginSessionWrapper(
				dispatcherPlugin.getReference(), pluginSession,
				pluginSessionType, root, securityContext);

		// this requires synchronized access
		dataPlugins.add(wrappedPlugin);

		// TODO: is this OK, while having the plugin "open" and "wrapped"
		// context.getBundleContext().ungetService(dispatcherPlugin.getReference());
		return wrappedPlugin;
	}

	// SD: changed to return the longest root, instead of the first one found
	private Node getLongestRootForPlugin(Plugin plugin, Node node) {
		// Node[] roots = plugin.getDataRoots();
		List<Segment> segments = plugin.getOwns();

		Node longestRoot = null;
		for (Segment segment : segments) {
			Node root = new Node(Uri.toPath(segment.getUri().toString()));
			if (root.isAncestorOf(node)) {
				Node n = root.isAncestorOf(subtreeNode) ? subtreeNode : root;
				if (n.getUri().length() > ((longestRoot != null) ? longestRoot
						.getUri().length() : 0))
					longestRoot = n;
			}
		}
		if (longestRoot != null)
			return longestRoot;
		throw new IllegalStateException("Internal error, plugin root not "
				+ "found for a URI handled by the plugin.");
	}

	private ReadableDataSession openPluginSession(final DataPlugin plugin,
			Node root, final int pluginSessionType) throws DmtException {

		final DmtSession session = this;
		final String[] rootPath = root.getPath();

		ReadableDataSession pluginSession;
		try {
			pluginSession = (ReadableDataSession) AccessController
					.doPrivileged(new PrivilegedExceptionAction() {
						public Object run() throws DmtException {
							switch (pluginSessionType) {
							case LOCK_TYPE_EXCLUSIVE:
								return plugin.openReadWriteSession(rootPath,
										session);
							case LOCK_TYPE_ATOMIC:
								return plugin.openAtomicSession(rootPath,
										session);
							default: // LOCK_TYPE_SHARED
								return plugin.openReadOnlySession(rootPath,
										session);
							}
						}
					}, securityContext);
		} catch (PrivilegedActionException e) {
			throw (DmtException) e.getException();
		}

		return pluginSession;
	}

	// precondition: path must be absolute
	private void checkNode(Node node, int check) throws DmtException {
		// 25.10.2010: added for Bug 1746
		node.checkUriAndSegmentLength();

		boolean shouldExist = (check != SHOULD_NOT_EXIST);
		if (getReadableDataSession(node).isNodeUri(node.getPath()) != shouldExist)
			throw new DmtException(node.getUri(),
					shouldExist ? DmtException.NODE_NOT_FOUND
							: DmtException.NODE_ALREADY_EXISTS,
					"The specified URI should point to "
							+ (shouldExist ? "an existing" : "a non-existent")
							+ " node to perform the requested operation.");
		boolean shouldBeLeaf = (check == SHOULD_BE_LEAF);
		boolean shouldBeInterior = (check == SHOULD_BE_INTERIOR);
		if ((shouldBeLeaf || shouldBeInterior)
				&& isLeafNodeNoCheck(node) != shouldBeLeaf)
			throw new DmtException(node.getUri(),
					DmtException.COMMAND_NOT_ALLOWED,
					"The specified URI should point to "
							+ (shouldBeLeaf ? "a leaf" : "an internal")
							+ " node to perform the requested operation.");
	}

	// precondition: checkNode() must have been called for the given uri
	private void checkNodeCapability(Node node, int capability)
			throws DmtException {
		MetaNode metaNode = getMetaNodeNoCheck(node);
		if (metaNode != null && !metaNode.can(capability))
			throw new DmtException(node.getUri(),
					DmtException.METADATA_MISMATCH,
					"Node meta-data does not allow the "
							+ capabilityName(capability)
							+ " operation for this node.");
		// default for all capabilities is 'true', if no meta-data is provided
	}

	private void checkValue(Node node, DmtData data) throws DmtException {
		MetaNode metaNode = getMetaNodeNoCheck(node);

		if (metaNode == null)
			return;

		// if default data was requested, only check that there is a default
		if (data == null) {
			if (metaNode.getDefault() == null)
				throw new DmtException(node.getUri(),
						DmtException.METADATA_MISMATCH,
						"This node has no default value in the meta-data.");
			return;
		}

		if (!metaNode.isValidValue(data))
			throw new DmtException(node.getUri(),
					DmtException.METADATA_MISMATCH,
					"The specified node value is not valid according to "
							+ "the meta-data.");

		// not checking value meta-data constraints individually, but leaving
		// this to the isValidValue method of the meta-node
		/*
		 * if((metaNode.getFormat() & data.getFormat()) == 0) throw new
		 * DmtException(uri, DmtException.METADATA_MISMATCH,
		 * "The format of the specified value is not in the list of " +
		 * "valid formats given in the node meta-data."); if(data.getFormat() ==
		 * DmtData.FORMAT_INTEGER) { if(metaNode.getMax() < data.getInt()) throw
		 * new DmtException(uri, DmtException.METADATA_MISMATCH,
		 * "Attempting to set too large integer, meta-data " +
		 * "specifies the maximum value of " + metaNode.getMax());
		 * if(metaNode.getMin() > data.getInt()) throw new DmtException(uri,
		 * DmtException.METADATA_MISMATCH,
		 * "Attempting to set too small integer, meta-data " +
		 * "specifies the minimum value of " + metaNode.getMin()); }
		 * 
		 * DmtData[] validValues = metaNode.getValidValues(); if(validValues !=
		 * null && !Arrays.asList(validValues).contains(data)) throw new
		 * DmtException(uri, DmtException.METADATA_MISMATCH,
		 * "Specified value is not in the list of valid values " +
		 * "given in the node meta-data.");
		 */
	}

	// precondition: path must be absolute and must specify an interior node
	private void checkInteriorNodeValueSupport(Node node) throws DmtException {
		MetaNode metaNode = getMetaNodeNoCheck(node);

		if (metaNode == null)
			return;

		boolean interiorNodeValueSupported = true;
		try {
			interiorNodeValueSupported = ((Boolean) metaNode
					.getExtensionProperty(INTERIOR_NODE_VALUE_SUPPORT_PROPERTY))
					.booleanValue();
		} catch (IllegalArgumentException e) {
		} catch (ClassCastException e) {
		}

		if (!interiorNodeValueSupported)
			throw new DmtException(node.getUri(),
					DmtException.FEATURE_NOT_SUPPORTED, "The given interior "
							+ "node does not support complex java values.");
	}

	private void checkNewNode(Node node) throws DmtException {
		MetaNode metaNode = getMetaNodeNoCheck(node);

		if (metaNode == null)
			return;

		if (metaNode.getScope() == MetaNode.PERMANENT)
			throw new DmtException(node.getUri(),
					DmtException.METADATA_MISMATCH,
					"Cannot create permanent node.");

		if (!metaNode.isValidName(node.getLastSegment()))
			throw new DmtException(node.getUri(),
					DmtException.METADATA_MISMATCH,
					"The specified node name is not valid according to "
							+ "the meta-data.");

		// not checking valid name list from meta-data, but leaving this to the
		// isValidName method of the meta-node
		/*
		 * String[] validNames = metaNode.getValidNames(); if(validNames != null
		 * && !Arrays.asList(validNames).contains(name)) throw new
		 * DmtException(uri, DmtException.METADATA_MISMATCH,
		 * "The specified node name is not in the list of valid " +
		 * "names specified in the node meta-data.");
		 */
	}

	private void checkMimeType(Node node, String type) throws DmtException {
		MetaNode metaNode = getMetaNodeNoCheck(node);

		if (metaNode == null)
			return;

		if (type == null) // default MIME type was requested
			return;

		int sep = type.indexOf('/');
		if (sep == -1 || sep == 0 || sep == type.length() - 1)
			throw new DmtException(node.getUri(), DmtException.COMMAND_FAILED,
					"The given type string does not contain a MIME type.");

		String[] validMimeTypes = metaNode.getMimeTypes();
		if (validMimeTypes != null
				&& !Arrays.asList(validMimeTypes).contains(type))
			throw new DmtException(node.getUri(),
					DmtException.METADATA_MISMATCH,
					"The specified MIME type is not in the list of valid "
							+ "types in the node meta-data.");
	}

	private void checkMaxOccurrence(Node node) throws DmtException {
		MetaNode metaNode = getMetaNodeNoCheck(node);

		if (metaNode == null)
			return;

		// If maxOccurrence == 1 then it is not a multi-node, so it can be
		// created if it did not exist before. If maxOccurrence > 1, it can
		// only be created if the number of existing nodes does not reach it.
		int maxOccurrence = metaNode.getMaxOccurrence();
		if (maxOccurrence != Integer.MAX_VALUE && maxOccurrence > 1
				&& getNodeCardinality(node) >= maxOccurrence)
			throw new DmtException(node.getUri(),
					DmtException.METADATA_MISMATCH,
					"Cannot create the specified node, meta-data maximizes "
							+ "the number of instances of this node to "
							+ maxOccurrence + ".");
	}

	private Node makeAbsoluteUri(String nodeUri) throws DmtException {
		Node node = Node.validateAndNormalizeUri(nodeUri);
		if (node.isAbsolute()) {
			checkNodeIsInSession(node, "");
			return node;
		}
		return subtreeNode.appendRelativeNode(node);
	}

	private void checkNodeIsInSession(Node node, String uriExplanation)
			throws DmtException {
		if (!subtreeNode.isAncestorOf(node))
			throw new DmtException(node.getUri(), DmtException.COMMAND_FAILED,
					"Specified URI " + uriExplanation + "points outside the "
							+ "subtree of this session.");
	}

	private boolean ensureInteriorAncestors(Node node, boolean sendEvent)
			throws DmtException {
		checkNodeIsInSession(node, "(needed to ensure "
				+ "a proper creation point for the new node) ");
		if (!getReadableDataSession(node).isNodeUri(node.getPath())) {
			commonCreateInteriorNode(node, null, sendEvent, true);
			return true;
		}

		checkNode(node, SHOULD_BE_INTERIOR);
		return false;
	}

	private static DmtException getWriteException(int lockMode, Node node) {
		boolean atomic = (lockMode == LOCK_TYPE_ATOMIC);
		return new DmtException(node.getUri(),
				atomic ? DmtException.TRANSACTION_ERROR
						: DmtException.COMMAND_NOT_ALLOWED,
				"The plugin handling the requested node does not support "
						+ (atomic ? "" : "non-") + "atomic writing.");
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

	// Move ACL entries from 'node' to 'newNode'.
	// If 'newNode' is 'null', the ACL entries are removed (moved to nowhere).
	private static void moveAclEntries(Node node, Node newNode) {
		synchronized (acls) {
			Hashtable newEntries = null;
			if (newNode != null)
				newEntries = new Hashtable();
			Iterator i = acls.entrySet().iterator();
			while (i.hasNext()) {
				Map.Entry entry = (Map.Entry) i.next();
				Node relativeNode = node.getRelativeNode((Node) entry.getKey());
				if (relativeNode != null) {
					if (newNode != null)
						newEntries.put(
								newNode.appendRelativeNode(relativeNode),
								entry.getValue());
					i.remove();
				}
			}
			if (newNode != null)
				acls.putAll(newEntries);
		}
	}

	private static Acl getEffectiveNodeAclNoCheck(Node node) {
		Acl acl;
		synchronized (acls) {
			acl = (Acl) acls.get(node);
			// must finish whithout NullPointerException, because root ACL must
			// not be empty
			while (acl == null || isEmptyAcl(acl)) {
				node = node.getParent();
				acl = (Acl) acls.get(node);
			}
		}
		return acl;
	}

	// precondition: node parameter must be an absolute node
	// throws SecurityException if principal is local user, and sufficient
	// privileges are missing
	private static void checkNodeOrParentPermission(String name, Node node,
			int actions, boolean checkParent) throws DmtException {

		if (node.isRoot())
			checkParent = false;

		Node parent = null;
		if (checkParent) // not null, as the uri is absolute but not "."
			parent = node.getParent();

		if (name != null) {
			// succeed if the principal has the required permissions on the
			// given uri, OR if the checkParent parameter is true and the
			// principal has the required permissions for the parent uri
			if (!(hasAclPermission(node, name, actions) || checkParent
					&& hasAclPermission(parent, name, actions)))
				throw new DmtException(node.getUri(),
						DmtException.PERMISSION_DENIED, "Principal '" + name
								+ "' does not have the required permissions ("
								+ writeAclCommands(actions) + ") on the node "
								+ (checkParent ? "or its parent " : "")
								+ "to perform this operation.");
		} else { // not doing local permission check if ACL check was done
			String actionString = writeAclCommands(actions);
			checkLocalPermission(node, actionString);
			if (checkParent)
				checkLocalPermission(parent, actionString);
		}
	}

	private static boolean hasAclPermission(Node node, String name, int actions) {
		return getEffectiveNodeAclNoCheck(node).isPermitted(name, actions);
	}

	private static void checkLocalPermission(Node node, String actions) {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null)
			sm.checkPermission(new DmtPermission(node.getUri(), actions));
	}

	private static String capabilityName(int capability) {
		switch (capability) {
		case MetaNode.CMD_ADD:
			return "Add";
		case MetaNode.CMD_DELETE:
			return "Delete";
		case MetaNode.CMD_EXECUTE:
			return "Execute";
		case MetaNode.CMD_GET:
			return "Get";
		case MetaNode.CMD_REPLACE:
			return "Replace";
		}
		// never reached
		throw new IllegalArgumentException(
				"Unknown meta-data capability constant " + capability + ".");
	}

	private static String writeAclCommands(int actions) {
		String cmds = null;
		cmds = writeCommand(cmds, actions, Acl.ADD, DmtPermission.ADD);
		cmds = writeCommand(cmds, actions, Acl.DELETE, DmtPermission.DELETE);
		cmds = writeCommand(cmds, actions, Acl.EXEC, DmtPermission.EXEC);
		cmds = writeCommand(cmds, actions, Acl.GET, DmtPermission.GET);
		cmds = writeCommand(cmds, actions, Acl.REPLACE, DmtPermission.REPLACE);
		return (cmds != null) ? cmds : "";
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

	static void init_acls() {
		acls = new Hashtable();
		acls.put(Node.ROOT_NODE, new Acl("Add=*&Get=*&Replace=*"));
	}

	public String toString() {
		StringBuffer info = new StringBuffer();
		info.append("DmtSessionImpl(");
		info.append(principal).append(", ");
		info.append(subtreeNode).append(", ");

		if (lockMode == LOCK_TYPE_ATOMIC)
			info.append("atomic");
		else if (lockMode == LOCK_TYPE_EXCLUSIVE)
			info.append("exclusive");
		else
			info.append("shared");

		info.append(", ");

		if (state == STATE_CLOSED)
			info.append("closed");
		else if (state == STATE_OPEN)
			info.append("open");
		else
			info.append("invalid");

		return info.append(')').toString();
	}
}

/**
 * 
 * @author steffen
 *
 */
class NodeState {
	int typeHash = 0;
	int valueHash = 0;
	int aclHash = 0;
	int titleHash = 0;
	
	public NodeState() {
		super();
	}
	
	public NodeState( int typeHash, int valueHash, int aclHash, int titleHash ) {
		this.typeHash = typeHash;
		this.valueHash = valueHash;
		this.aclHash = aclHash;
		this.titleHash = titleHash;
	}

	public boolean equals( Object obj ) {
		if ( obj == null || ! (obj instanceof NodeState) )
			return false;
		NodeState other = (NodeState) obj;
		return valueHash == other.valueHash && typeHash == other.typeHash && titleHash == other.titleHash && aclHash == other.aclHash;
	}
	
	protected NodeState clone() {
		return new NodeState( typeHash, valueHash, aclHash, titleHash );
	}
}


// Multi-purpose event store class:
// - stores sets of node URIs for the different types of changes within an
// atomic session
// - contains a static event queue for the asynchronous local event delivery;
// this queue is emptied by DmtAdminFactory, which forwards the events to all
// locally registered DmtEventListeners
class EventStore {
	private static LinkedList localEventQueue = new LinkedList();

	private static void postLocalEvent(DmtEventCore event) {
		synchronized (localEventQueue) {
			localEventQueue.addLast(event);
			localEventQueue.notifyAll();
		}
	}

	// Retrieve the next event from the queue. If there are no events, block
	// until one is added, or until the given timeout time (in milliseconds) has
	// elapsed. A timeout of zero blocks indefinitely. In case of timeout, or
	// if the wait has been interrupted, the method returns "null".
	static DmtEventCore getNextLocalEvent(int timeout) {
		synchronized (localEventQueue) {
			if (localEventQueue.size() == 0) {
				try {
					localEventQueue.wait(timeout);
				} catch (InterruptedException e) {
					// do nothing
				}

				if (localEventQueue.size() == 0)
					return null;
			}

			return (DmtEventCore) localEventQueue.removeFirst();
		}
	}

	private final int sessionId;
	private final Context context;
	private final DmtSessionImpl dmtSessionImpl;
	
	private Hashtable events;
	

	EventStore(Context context, int sessionId, DmtSessionImpl dmtSessionImpl) {
		this.sessionId = sessionId;
		this.context = context;
		this.dmtSessionImpl = dmtSessionImpl;

		events = new Hashtable();
	}

	synchronized void clear() {
		events.clear();
		dmtSessionImpl.getInitialNodeStates().clear();
		dmtSessionImpl.getActualNodeStates().clear();
	}

	synchronized void excludeRoot(Node root) {
		Enumeration e = events.elements();
		while (e.hasMoreElements())
			((DmtEventCore) e.nextElement()).excludeRoot(root);
	}

	synchronized void add(int type, Node node, Node newNode, Acl acl,
			boolean isAtomic) {
		if (isAtomic) { // add event to event store, for delivery at commit
			mergeWithTransactionLog(getDmtEventCore(new Integer(type)), type, node, newNode, acl);
		} else
			// dispatch to local and OSGi event listeners immediately
			dispatchEvent(new DmtEventCore(type, sessionId, node, newNode, acl));
	}
	
	void mergeWithTransactionLog( DmtEventCore eventType, Integer typeInteger, Node node, Node newNode, Acl acl ) {
		boolean modified = false;
		switch (typeInteger.intValue()) {
			case DmtEvent.ADDED :
				// has this node been deleted in the same transaction?
				if (  isEventNode(node, DmtEvent.DELETED )) {
					// delete from DELETED and don't add to ADDED
					deleteNodeFromEvent(node, DmtEvent.DELETED );

					// has the value changed against initial session state?
					if ( nodeChanged( node ))
						// add REPLACED event instead
						getDmtEventCore(DmtEvent.REPLACED).addNode(node, newNode, acl);
					break;
				}
				eventType.addNode(node, newNode, acl);
				break;
			
			case DmtEvent.REPLACED :
				// don't add to REPLACE, if node was added in this transaction
				if ( isEventNode(node, DmtEvent.ADDED ))
					break;

				// is this an effective change compared to initial state? 
				if ( nodeChanged(node) ) {
					if ( ! isEventNode(node, DmtEvent.REPLACED ))
						// add REPLACED event, if not done before already
						eventType.addNode(node, newNode, acl);
				}
				else 
					deleteNodeFromEvent(node, DmtEvent.REPLACED );
				break;
	
			case DmtEvent.DELETED :
				// has this node been added in the same transaction?
				if ( isEventNode(node, DmtEvent.ADDED ) ) {
					// delete from ADDED and don't add to DELETED
					deleteNodeFromEvent(node, DmtEvent.ADDED );
					break;
				}

				// has this node been replaced in the same transaction?
				if ( isEventNode(node, DmtEvent.REPLACED ) ) {
					// delete from REPLACED
					deleteNodeFromEvent(node, DmtEvent.REPLACED );
				}

				// is this node result of a RENAME operation in the same transaction?
				if ( isNewEventNode( node, DmtEvent.RENAMED ) ) {
					// add original node to DELETED
					Node originalNode = getNodeForNewNode(node, DmtEvent.RENAMED );
					if ( originalNode != null ) {
						eventType.addNode(originalNode, null, acl);
						// has the original node been replaced in the same transaction?
						if ( isEventNode(originalNode, DmtEvent.REPLACED ) )
							// delete from REPLACED
							deleteNodeFromEvent(originalNode, DmtEvent.REPLACED );
					}
					// and delete from RENAMED 
					deleteNewNodeFromEvent(node, DmtEvent.RENAMED );
					break;
				}

				// is this node result of a COPY operation in the same transaction?
				if ( isNewEventNode( node, DmtEvent.COPIED ) ) {
					// delete from COPIED
					deleteNewNodeFromEvent(node, DmtEvent.COPIED );
					break;
				}
				
				eventType.addNode(node, newNode, acl);
				break;

			case DmtEvent.RENAMED :
				// check and handling of multiple rename operations on same 
				// node is performed in DmtEventCore.addNode(..) method
				eventType.addNode(node, newNode, acl);
				break;

			case DmtEvent.COPIED :
				eventType.addNode(node, newNode, acl);
				break;
				
			default:
				eventType.addNode(node, newNode, acl);
				break;
		}
	}

	/**
	 * determines whether a node has changed during the current transaction 
	 * change is defined as: changed Value OR Title OR Acl OR Type 
	 * @param node
	 * @return
	 */
	private boolean nodeChanged( Node node ) {
		// has the value changed against initial session state?
		NodeState initialState = (NodeState) dmtSessionImpl.getInitialNodeStates().get(node);
		NodeState actualState = (NodeState) dmtSessionImpl.getActualNodeStates().get(node);
		// actualState can be null, if an interior node has been updated with "complex data" as specified in
		// 117.13.9.41
		// In this case, the plugin is responsible to distribute the new values taken from "complex data" to its leaf nodes.
		// Therefore they are not available as actualState. We assume the node as changed in this case.
		if ( actualState == null )
			return true;
		return ! actualState.equals(initialState);
	}
	
	/**
	 * checks whether there is already an event created for the given node and event-type
	 * @param node
	 * @param type
	 * @return
	 */
	boolean isEventNode( Node node, int type ) {
		DmtEventCore event = getDmtEventCore( type);
		if ( event != null ) 
			return event.isNode( node );
		return false;
	}

	boolean isNewEventNode( Node node, int type ) {
		DmtEventCore event = getDmtEventCore( type);
		if ( event != null ) 
			return event.isNewNode( node );
		return false;
	}

	private void deleteNodeFromEvent( Node node, int type ) {
		DmtEventCore event = getDmtEventCore(type);
		if ( event != null ) 
			event.removeNode( node );
	}
	private void deleteNewNodeFromEvent( Node newNode, int type ) {
		DmtEventCore event = getDmtEventCore(type);
		if ( event != null ) 
			event.removeNewNode( newNode );
	}
	
	/**
	 * looks for a node that is assigned to the given newnode for a certain event-type
	 * This check is interesting to figure out the original node name after rename operations.
	 * @param newNode
	 * @param type
	 * @return
	 */
	private Node getNodeForNewNode( Node newNode, int type ) {
		DmtEventCore event = getDmtEventCore(type);
		Node node = null;
		if ( event != null )
			node = event.getNodeByNewNode(newNode);
		return node;
	}
	
	private void removeSubtreeNodes( int type ) {
		DmtEventCore event = getDmtEventCore(type);
		
		// sorted set ensures that shortest path comes first
		SortedSet sortedSet = new TreeSet();
		sortedSet.addAll(event.getNodes());
		
		Vector toRemove = new Vector();
		Iterator i = sortedSet.iterator();
		String topNode = "";
		while (i.hasNext()) {
			Node node = (Node) i.next();
			if ( topNode.length() == 0 )
				topNode = node.getUri() + "/";	
			if ( node.getUri().startsWith(topNode) )
				toRemove.add(node);
			else 
				topNode = node.getUri() + "/";
		}
		event.removeNodes(toRemove);
	}
	
	/**
	 * removes nodes from DELETED and COPIED that exist in both lists
	 */
	private void removeMatchingCopiedAndDeleted() {
		DmtEventCore eventCopied = getDmtEventCore(DmtEvent.COPIED);
		DmtEventCore eventDeleted = getDmtEventCore(DmtEvent.DELETED);

		List copiedNodes = eventCopied.getNewNodes();
		List deletedNodes = eventDeleted.getNodes();
		if ( copiedNodes == null || deletedNodes == null )
			return;
		
		Vector toRemove = new Vector();
		Iterator i = copiedNodes.iterator();
		while (i.hasNext()) {
			Node node = (Node) i.next();
			// does node also exist in list of deleted nodes
			if ( deletedNodes.contains(node)) {
				toRemove.add( node );
			}
		}
		eventDeleted.removeNodes(toRemove);
		eventCopied.removeNewNodes(toRemove);
	}
	
	private DmtEventCore getDmtEventCore( int type ) {
		Integer typeInteger = new Integer( type );
		DmtEventCore event = (DmtEventCore) events.get(typeInteger);
		if (event == null) {
			event = new DmtEventCore(type, sessionId);
			events.put(typeInteger, event);
		}
		return event;
	}

	synchronized void dispatchEvents() {
		// if this is an atomic session, we have to do some cleanup work in the DELETED and COPIED Events
		// because only the top-level node must be notified, but during the session all subtree nodes have been added
		removeSubtreeNodes(DmtEvent.DELETED);
		
//		removeMatchingCopiedAndDeleted();
		
		dispatchEventsByType(DmtEvent.ADDED);
		dispatchEventsByType(DmtEvent.DELETED);
		dispatchEventsByType(DmtEvent.REPLACED);
		dispatchEventsByType(DmtEvent.RENAMED);
		dispatchEventsByType(DmtEvent.COPIED);

		clear();
	}

	synchronized void dispatchEventsByType(int type) {
		DmtEventCore event = (DmtEventCore) events.get(new Integer(type));
		// don't dispatch events with empty node-list
		if (event != null && event.getNodes() != null && event.getNodes().size() > 0 )
			dispatchEvent(event);
	}

	synchronized void dispatchSessionLifecycleEvent(int type) {
		if (type != DmtEvent.SESSION_OPENED && type != DmtEvent.SESSION_CLOSED)
			throw new IllegalArgumentException("Invalid event type, only "
					+ "session lifecycle events can be dispatched directly.");

		dispatchEvent(new DmtEventCore(type, sessionId));
	}

	private void dispatchEvent(DmtEventCore dmtEvent) {
		// send event to listeners directly registered with DmtAdmin
		postLocalEvent(dmtEvent);

		// send event to listeners registered through EventAdmin
		postOSGiEvent(dmtEvent);

	}

	private void postOSGiEvent(DmtEventCore dmtEvent) {
		final EventAdmin eventChannel = (EventAdmin) context.getTracker(
				EventAdmin.class).getService();

		if (eventChannel == null) {// logging a warning if Event Admin is
									// missing
			context.log(
					LogService.LOG_WARNING,
					"Event Admin not found, only "
							+ "delivering events to listeners directly registered with "
							+ "DmtAdmin.", null);
			return;
		}

		Hashtable properties = new Hashtable();
		properties.put("session.id", new Integer(dmtEvent.getSessionId()));

		List nodes = dmtEvent.getNodes();
		if (nodes != null)
			properties.put("nodes",
					Node.getUriArray((Node[]) nodes.toArray(new Node[0])));

		List newNodes = dmtEvent.getNewNodes();
		if (newNodes != null)
			properties.put("newnodes",
					Node.getUriArray((Node[]) newNodes.toArray(new Node[0])));

		final Event event = new Event(dmtEvent.getTopic(), properties);

		AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				eventChannel.postEvent(event);
				return null;
			}
		});
	}

}

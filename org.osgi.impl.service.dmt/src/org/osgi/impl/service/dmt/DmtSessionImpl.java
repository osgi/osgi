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
import java.util.*;
import org.osgi.service.dmt.*;

// TODO permissions: check java permissions, set permissions based on policy
// plugin, etc.
// TODO optimize node handling (e.g. retrieve plugin from dispatcher only once
// per API call), maybe with new URI class
// TODO lock mode handling: (1) no other session can run parallelly with a
// writer session (2) rollback functionality
// TODO send appropriate events (maybe only when session is closed)
// TODO check scope (permanent/dynamic) meta-data for each operation
// TODO check access type meta-data for each operation
// TODO decide what to assume for each operation when the required meta-data is
// missing
public class DmtSessionImpl implements DmtSession {
	private static Hashtable	acls;
	static {
		acls = new Hashtable();
		acls.put(".", new DmtAcl("Add=*&Get=*&Replace=*"));
	}
	private static int			SHOULD_NOT_EXIST	= 0;
	private static int			SHOULD_EXIST		= 1;
	private static int			SHOULD_BE_LEAF		= 2;	// implies
																// SHOULD_EXIST
	private static int			SHOULD_BE_INTERIOR	= 3;	// implies
																// SHOULD_EXIST
	private int					lockMode;
	private String				principal;
	private String				subtreeUri;
	private Set					dataPlugins;
	private DmtPlugInDispatcher	dispatcher;

	public DmtSessionImpl(String principal, String subtreeUri, int lockMode,
			DmtPlugInDispatcher dispatcher) throws DmtException {
		checkNodeUri(subtreeUri);
		subtreeUri = Utils.normalizeAbsoluteUri(subtreeUri);
		this.lockMode = lockMode;
		this.principal = principal;
		this.subtreeUri = subtreeUri;
		this.dispatcher = dispatcher;
		dataPlugins = Collections.synchronizedSet(new HashSet());
		// after everything is initialized, check with the plugins whether the
		// given node really exists
		checkNode(subtreeUri, SHOULD_EXIST);
	}

	public int getLockType() {
		return lockMode;
	}

	public String getPrincipal() {
		return principal;
	}

	public int getSessionId() {
		return (new Long(System.currentTimeMillis())).hashCode();
	}

	public String getRootUri() {
		return subtreeUri;
	}

	public void close() throws DmtException {
		Vector closeExceptions = new Vector();
		synchronized (dataPlugins) {
			Iterator i = dataPlugins.iterator();
			while (i.hasNext()) {
				try {
					((DmtReadOnly) i.next()).close();
				}
				catch (Exception e) {
					closeExceptions.add(e);
				}
			}
			dataPlugins.clear();
		}
		if (closeExceptions.size() != 0)
			// TODO try to include some information to identify the plugins that
			// threw an exception
			throw new DmtException(null, DmtException.COMMAND_FAILED,
					"Some plugins failed to close.", closeExceptions);
	}

	public void rollback() throws DmtException {
		// TODO support new transaction JSR?
		// TODO check that lock mode is 'atomic'
		// TODO revert to copy of ACLs at the start of the session
		Vector rollbackExceptions = new Vector();
		synchronized (dataPlugins) {
			Iterator i = dataPlugins.iterator();
			while (i.hasNext()) {
				Object plugin = i.next();
				try {
					if (plugin instanceof DmtDataPlugIn)
						((DmtDataPlugIn) plugin).rollback();
					else
						((DmtReadOnly) plugin).close();
				}
				catch (Exception e) {
					rollbackExceptions.add(e);
				}
			}
			dataPlugins.clear();
		}
		if (rollbackExceptions.size() != 0)
			// TODO try to include some information to identify the plugins that
			// threw an exception
			throw new DmtException(null, DmtException.ROLLBACK_FAILED,
					"Some plugins failed to roll back or close.");
	}

	public void execute(String nodeUri, String data) throws DmtException {
		// allow executing non-existent nodes so that an exec plugin is not
		// forced to be a data plugin as well
		String uri = makeAbsoluteUri(nodeUri);
		checkNodePermission(uri, DmtAcl.EXEC);
		DmtExecPlugIn plugin = dispatcher.getExecPlugin(uri);
		if (plugin == null)
			throw new DmtException(uri, DmtException.OTHER_ERROR,
					"No exec plugin registered for given node.");
		plugin.execute(this, uri, data);
	}

	public boolean isNodeUri(String nodeUri) {
		// TODO check permissions (only runtime SecurityException has to be
		// thrown)
		// requires "get" DmtPermission for local clients, and is not allowed
		// remotely (no SyncML command anyway)
		try {
			makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
		}
		catch (DmtException e) {
			return false; // invalid node URI or error opening plugin
		}
		return true;
	}

	public boolean isLeafNode(String nodeUri) throws DmtException {
		String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
		checkNodePermission(uri, DmtAcl.GET);
		return isLeafNodeNoCheck(uri);
	}

	// GET property op
	public DmtAcl getNodeAcl(String nodeUri) throws DmtException {
		String uri = makeAbsoluteUri(nodeUri);
		//String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
		checkNodePermission(uri, DmtAcl.GET);
		DmtAcl acl = (DmtAcl) acls.get(uri);
		return acl == null ? null : new DmtAcl(acl);
	}

	// REPLACE property op
	public void setNodeAcl(String nodeUri, DmtAcl acl) throws DmtException {
		if (acl == null)
			throw new IllegalArgumentException(
					"DmtAcl argument must be non-null.");
		String uri = makeAbsoluteUri(nodeUri);
		//String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
		boolean isRoot = uri.equals(".");
		// check for REPLACE permission:
		if (isRoot) // on the root itself for the root node
			checkNodePermission(uri, DmtAcl.REPLACE);
		else
			if (isLeafNodeNoCheck(uri)) // on the parent node for leaf nodes
				checkNodePermission(Utils.parentUri(uri), DmtAcl.REPLACE);
			else
				// on the node itself or the parent for non-root interior nodes
				checkNodeOrParentPermission(uri, DmtAcl.REPLACE);
		// check that the new ACL is valid
		if (isRoot && !acl.isPermitted("*", DmtAcl.ADD)) // should be 405
														 // "Forbidden"
														 // according to DMTND
														 // 7.7.1.2
			throw new DmtException(nodeUri, DmtException.COMMAND_NOT_ALLOWED,
					"Root ACL must allow the Add operation for all principals.");
		acls.put(uri, acl);
	}

	public DmtMetaNode getMetaNode(String nodeUri) throws DmtException {
		String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
		checkNodePermission(uri, DmtAcl.GET);
		return getMetaNodeNoCheck(uri);
	}

	public DmtData getNodeValue(String nodeUri) throws DmtException {
		String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_BE_LEAF);
		checkNodePermission(uri, DmtAcl.GET);
		return getDataPlugin(uri).getNodeValue(uri);
	}

	public String[] getChildNodeNames(String nodeUri) throws DmtException {
		String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_BE_INTERIOR);
		checkNodePermission(uri, DmtAcl.GET);
		return getDataPlugin(uri).getChildNodeNames(uri);
	}

	// GET property op
	public String getNodeTitle(String nodeUri) throws DmtException {
		String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
		checkNodePermission(uri, DmtAcl.GET);
		return getDataPlugin(uri).getNodeTitle(uri);
	}

	// GET property op
	public int getNodeVersion(String nodeUri) throws DmtException {
		String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
		checkNodePermission(uri, DmtAcl.GET);
		return getDataPlugin(uri).getNodeVersion(uri);
	}

	// GET property op
	public Date getNodeTimestamp(String nodeUri) throws DmtException {
		String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
		checkNodePermission(uri, DmtAcl.GET);
		return getDataPlugin(uri).getNodeTimestamp(uri);
	}

	// GET property op
	public int getNodeSize(String nodeUri) throws DmtException {
		String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_BE_LEAF);
		checkNodePermission(uri, DmtAcl.GET);
		return getDataPlugin(uri).getNodeSize(uri);
	}

	// GET property op
	public String getNodeType(String nodeUri) throws DmtException {
		String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_BE_INTERIOR);
		checkNodePermission(uri, DmtAcl.GET);
		return getDataPlugin(uri).getNodeType(uri);
	}

	// REPLACE property op
	public void setNodeTitle(String nodeUri, String title) throws DmtException {
		String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
		checkNodePermission(uri, DmtAcl.REPLACE);
		try {
			if (title.getBytes("UTF-8").length > 255)
				throw new DmtException(nodeUri, DmtException.COMMAND_FAILED,
						"Length of Title property exceeds 255 bytes (UTF-8).");
		}
		catch (UnsupportedEncodingException e) {
			// never happens
		}
		getWritableDataPlugin(uri).setNodeTitle(uri, title);
	}

	public void setNodeValue(String nodeUri, DmtData data) throws DmtException {
		if (data == null)
			throw new IllegalArgumentException(
					"DmtData argument must be non-null.");
		String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_BE_LEAF);
		checkNodePermission(uri, DmtAcl.REPLACE);
		getWritableDataPlugin(uri).setNodeValue(uri, data);
	}

	// SyncML DMTND 7.5 (p16) Type: only the Get command is applicable!
	public void setNodeType(String nodeUri, String type) throws DmtException {
		String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_BE_INTERIOR);
		checkNodePermission(uri, DmtAcl.REPLACE);
		getWritableDataPlugin(uri).setNodeType(uri, type);
	}

	public void deleteNode(String nodeUri) throws DmtException {
		String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
		checkNodePermission(uri, DmtAcl.DELETE);
		// TODO if there is no meta information, should we assume that the node
		// can be deleted or not?
		DmtMetaNode metaNode = getMetaNodeNoCheck(uri);
		if (metaNode != null) {
			if (metaNode.isPermanent())
				throw new DmtException(uri, DmtException.COMMAND_NOT_ALLOWED,
						"Cannot delete permanent node.");
			if (!metaNode.canDelete())
				throw new DmtException(uri, DmtException.COMMAND_NOT_ALLOWED,
						"Node meta-data does not allow direct deletion of this node.");
		}
		getWritableDataPlugin(uri).deleteNode(uri);
		copyAclEntries(uri, null, true);
	}

	public void createInteriorNode(String nodeUri) throws DmtException {
		String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_NOT_EXIST);
		String parent = Utils.parentUri(uri);
		checkNode(parent, SHOULD_BE_INTERIOR);
		checkNodePermission(parent, DmtAcl.ADD);
		getWritableDataPlugin(uri).createInteriorNode(uri);
		assignNewNodePermissions(uri, parent);
	}

	public void createInteriorNode(String nodeUri, String type)
			throws DmtException {
		String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_NOT_EXIST);
		String parent = Utils.parentUri(uri);
		checkNode(parent, SHOULD_BE_INTERIOR);
		checkNodePermission(parent, DmtAcl.ADD);
		getWritableDataPlugin(uri).createInteriorNode(uri, type);
		assignNewNodePermissions(uri, parent);
	}

	public void createLeafNode(String nodeUri, DmtData value)
			throws DmtException {
		if (value == null)
			throw new IllegalArgumentException(
					"DmtData argument must be non-null.");
		String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_NOT_EXIST);
		String parent = Utils.parentUri(uri);
		checkNode(parent, SHOULD_BE_INTERIOR);
		checkNodePermission(parent, DmtAcl.ADD);
		getWritableDataPlugin(uri).createLeafNode(uri, value);
	}

	// TODO support non-recursive clone mode, if semantics are clarified
	// Tree may be left in an inconsistent state if there is an error when only
	// part of the tree has been cloned (?)
	public void clone(String nodeUri, String newNodeUri, boolean recursive)
			throws DmtException {
		if (!recursive)
			throw new DmtException(nodeUri, DmtException.COMMAND_FAILED,
					"Non-recursive cloning currently not supported.");
		String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
		String newUri = makeAbsoluteUriAndCheck(newNodeUri, SHOULD_NOT_EXIST);
		if (Utils.isAncestor(uri, newUri))
			throw new DmtException(uri, DmtException.COMMAND_NOT_ALLOWED,
					"Cannot copy node to its descendant, '" + newUri + "'.");
		String newParentUri = Utils.parentUri(newUri);
		checkNode(newParentUri, SHOULD_BE_INTERIOR);
		// DMTND 7.7.1.5: "needs correct access rights for the equivalent Add,
		// Delete, Get, and Replace commands"
		if (dispatcher.handledBySameDataPlugin(uri, newUri)) {
			// TODO check for GET permission on the whole subtree
			checkNodePermission(uri, DmtAcl.GET);
			// new parent needs REPLACE permissions if the cloned node is a
			// leaf, in order to copy the ACL
			int reqParentPerm = DmtAcl.ADD
					| (isLeafNodeNoCheck(uri) ? DmtAcl.REPLACE : 0);
			checkNodePermission(newParentUri, reqParentPerm);
			getWritableDataPlugin(newUri).clone(uri, newUri, true);
			copyAclEntries(uri, newUri, false);
		}
		else {
			cloneNoCheck(uri, newUri);
		}
	}

	public void renameNode(String nodeUri, String newName) throws DmtException {
		if (!Utils.isValidNodeName(newName))
			throw new DmtException(newName, DmtException.OTHER_ERROR,
					"Syntactically invalid node name.");
		String uri = makeAbsoluteUriAndCheck(nodeUri, SHOULD_EXIST);
		String parent = Utils.parentUri(uri);
		if (parent == null)
			throw new DmtException(uri, DmtException.COMMAND_NOT_ALLOWED,
					"Cannot rename root node.");
		// TODO if there is no meta information, should we assume that the node
		// is permanent or dynamic?
		DmtMetaNode metaNode = getMetaNodeNoCheck(uri);
		if (metaNode != null && metaNode.isPermanent())
			throw new DmtException(uri, DmtException.COMMAND_NOT_ALLOWED,
					"Cannot rename permanent node.");
		String newUri = Utils.createAbsoluteUri(parent, newName);
		checkNode(newUri, SHOULD_NOT_EXIST);
		checkNodePermission(uri, DmtAcl.REPLACE);
		getWritableDataPlugin(uri).renameNode(uri, newName);
		copyAclEntries(uri, newUri, true);
	}

	private boolean isLeafNodeNoCheck(String uri) throws DmtException {
		// TODO what should be done if there is no metaNode information
		DmtMetaNode metaNode = getMetaNodeNoCheck(uri);
		if (metaNode != null)
			return metaNode.isLeaf();
		// TODO hack: try to find out the type of node by calling getNodeValue
		// and getChildNodeNames
		// TODO specify in the standard the exception the plugin has to throw if
		// the node type is invalid for the op.
		return false;
	}

	private DmtMetaNode getMetaNodeNoCheck(String uri) throws DmtException {
		// TODO is there any generic (engine-level) metadata that should be
		// given to the plugins?
		Object plugin = getDataPlugin(uri);
		if (plugin instanceof DmtDataPlugIn)
			return ((DmtDataPlugIn) plugin).getMetaNode(uri, null);
		else
			// (plugin instanceof DmtReadOnlyDataPlugIn) as guaranteed by
			// getDataPlugin()
			return ((DmtReadOnlyDataPlugIn) plugin).getMetaNode(uri, null);
	}

	private void cloneNoCheck(String uri, String newUri) throws DmtException {
		boolean isLeaf = isLeafNodeNoCheck(uri);
		// create new node
		if (isLeaf)
			createLeafNode(newUri, getNodeValue(uri));
		else
			createInteriorNode(newUri, getNodeType(uri));
		// copy ACL
		setNodeAcl(newUri, getNodeAcl(uri));
		// copy Title property if it is supported by both source and target
		// plugins
		try {
			setNodeTitle(newUri, getNodeTitle(uri));
		}
		catch (DmtException e) {
			if (e.getCode() != DmtException.FEATURE_NOT_SUPPORTED)
				throw new DmtException(uri, DmtException.COMMAND_FAILED,
						"Error copying node to '" + newUri
								+ "', cannot copy title.", e);
		}
		// Format, Name, Size, TStamp and VerNo properties do not need to be
		// expicitly copied
		// clone children recursively in case of interior node
		if (!isLeaf) {
			String[] children = getChildNodeNames(uri);
			for (int i = 0; i < children.length; i++)
				cloneNoCheck(Utils.createAbsoluteUri(uri, children[i]), Utils
						.createAbsoluteUri(newUri, children[i]));
		}
	}

	private void assignNewNodePermissions(String uri, String parent)
			throws DmtException {
		// DMTND 7.7.1.3: if parent does not have Replace permissions, give Add,
		// Delete and Replace permissions to child
		// TODO spec doesn't say that Get/Exec permission should be given, but
		// this would be logical if parent has them
		try {
			checkNodePermission(parent, DmtAcl.REPLACE);
		}
		catch (DmtException e) {
			if (e.getCode() != DmtException.PERMISSION_DENIED)
				throw e;
			if (principal != null) {
				DmtAcl acl = new DmtAcl();
				/*
				 * // TODO if this is needed, get parent permissions only once
				 * if(hasAclPermission(parent, principal, DmtAcl.GET)) actions |=
				 * DmtAcl.GET; if(hasAclPermission(parent, principal,
				 * DmtAcl.EXEC)) actions |= DmtAcl.EXEC;
				 */
				acl.setPermission(principal, DmtAcl.ADD | DmtAcl.DELETE
						| DmtAcl.REPLACE);
				acls.put(uri, acl);
			}
			// TODO how can this be applied to the Java permissions?
		}
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

	private String makeAbsoluteUriAndCheck(String nodeUri, int check)
			throws DmtException {
		String uri = makeAbsoluteUri(nodeUri);
		checkNode(uri, check);
		return uri;
	}

	// TODO try to factor out some common code from the get*DataPlugin methods
	private DmtReadOnly getDataPlugin(String uri) throws DmtException {
		DmtReadOnly plugin = (DmtReadOnly) dispatcher.getDataPlugin(uri);
		// ensure that the plugin is not returned for use in another thread
		// before its open() has successfully finished
		synchronized (plugin) {
			if (dataPlugins.add(plugin)) {
				if (plugin instanceof DmtDataPlugIn)
					((DmtDataPlugIn) plugin).open(subtreeUri, lockMode, this);
				else
					if (plugin instanceof DmtReadOnlyDataPlugIn)
						((DmtReadOnlyDataPlugIn) plugin).open(subtreeUri, this);
					else
						// never happens
						throw new IllegalStateException(
								"Invalid data plugin class given by dispatcher.");
			}
		}
		return plugin;
	}

	private Dmt getWritableDataPlugin(String uri) throws DmtException {
		Object pluginObj = dispatcher.getDataPlugin(uri);
		if (!(pluginObj instanceof DmtDataPlugIn))
			throw new DmtException(uri, DmtException.COMMAND_NOT_ALLOWED,
					"Cannot perform operation on Read-Only plugin.");
		DmtDataPlugIn plugin = (DmtDataPlugIn) pluginObj;
		// ensure that the plugin is not returned for use in another thread
		// before its open() has successfully finished
		synchronized (plugin) {
			if (dataPlugins.add(plugin))
				plugin.open(subtreeUri, lockMode, this);
		}
		return plugin;
	}

	private void checkNode(String uri, int check) throws DmtException {
		boolean shouldExist = (check != SHOULD_NOT_EXIST);
		if (getDataPlugin(uri).isNodeUri(uri) != shouldExist)
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

	private String makeAbsoluteUri(String nodeUri) throws DmtException {
		checkNodeUri(nodeUri);
		String normNodeUri = Utils.normalizeUri(nodeUri);
		if (Utils.isAbsoluteUri(normNodeUri)) {
			if (!Utils.isAncestor(subtreeUri, normNodeUri))
				throw new DmtException(nodeUri, DmtException.OTHER_ERROR,
						"Specified URI points outside the subtree of this session.");
			return normNodeUri;
		}
		return Utils.createAbsoluteUri(subtreeUri, normNodeUri);
	}

	// Copy ACL entries from 'uri' to 'newUri'.
	// If 'remove' is true, the original ACL entries are removed.
	// If 'newUri' is 'null', the new ACL entries are not written.
	private static void copyAclEntries(String uri, String newUri, boolean remove) {
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
					if (remove)
						i.remove();
				}
			}
			if (newUri != null)
				acls.putAll(newEntries);
		}
	}

	// throws SecurityException if principal is local user, and sufficient
	// privileges are missing
	private static void checkNodeOrParentPermission(String name, String uri,
			int actions, boolean checkParent) throws DmtException {
		// TODO add information about current operation in the error string
		if (name != null) { // TODO synchronize on 'acls'?
			if (!(hasAclPermission(uri, name, actions) || checkParent
					&& hasAclPermission(Utils.parentUri(uri), name, actions)))
				throw new DmtException(uri, DmtException.PERMISSION_DENIED,
						"Principal '" + name
								+ "' does not have the required permissions ("
								+ writeAclCommands(actions) + ") on the node "
								+ (checkParent ? "or its parent " : "")
								+ "to perform this operation.");
		}
		else { // TODO maybe do this even if ACL check was done?
			// TODO check DmtPermissions
		}
	}

	// TODO the action names should be retrieved from the DmtAcl class somehow
	private static String writeAclCommands(int actions) {
		String commands = null;
		commands = writeCommand(commands, actions, DmtAcl.ADD, "Add");
		commands = writeCommand(commands, actions, DmtAcl.DELETE, "Delete");
		commands = writeCommand(commands, actions, DmtAcl.EXEC, "Exec");
		commands = writeCommand(commands, actions, DmtAcl.GET, "Get");
		commands = writeCommand(commands, actions, DmtAcl.REPLACE, "Replace");
		return (commands != null) ? commands : "";
	}

	private static String writeCommand(String base, int actions, int action,
			String entry) {
		if ((actions & action) != 0)
			return (base != null) ? base + ',' + entry : entry;
		return base;
	}

	private static boolean hasAclPermission(String uri, String name, int actions)
			throws DmtException {
		DmtAcl acl;
		synchronized (acls) {
			acl = (DmtAcl) acls.get(uri);
			// must finish whithout NullPointerException, because root ACL must
			// not be empty
			while (acl == null || isEmptyAcl(acl)) {
				uri = Utils.parentUri(uri);
				acl = (DmtAcl) acls.get(uri);
			}
		}
		return acl.isPermitted(name, actions);
	}

	private static boolean isEmptyAcl(DmtAcl acl) {
		return acl.getPermissions("*") == 0 && acl.getPrincipals().size() == 0;
	}

	private static void checkNodeUri(String nodeUri) throws DmtException {
		if (!Utils.isValidUri(nodeUri))
			throw new DmtException(nodeUri, DmtException.OTHER_ERROR,
					"Syntactically invalid or 'null' URI string.");
	}
}

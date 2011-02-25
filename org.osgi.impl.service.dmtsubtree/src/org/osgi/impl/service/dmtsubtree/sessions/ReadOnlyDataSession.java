/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.impl.service.dmtsubtree.sessions;

import info.dmtree.DmtAdmin;
import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.DmtIllegalStateException;
import info.dmtree.DmtSession;
import info.dmtree.MetaNode;
import info.dmtree.Uri;
import info.dmtree.spi.ReadableDataSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.TreeSet;
import java.util.Vector;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.dmtsubtree.Activator;
import org.osgi.impl.service.dmtsubtree.Constants;
import org.osgi.impl.service.dmtsubtree.Util;
import org.osgi.impl.service.dmtsubtree.mapping.VendorPluginInfo;
import org.osgi.impl.service.dmtsubtree.mapping.flags.MappedPath;
import org.osgi.impl.service.dmtsubtree.mapping.flags.VendorDataPlugin;

public class ReadOnlyDataSession implements ReadableDataSession, Constants {

	static final boolean EXCLUDE_DIRECT_MATCH = false;
	static final boolean INCLUDE_DIRECT_MATCH = true;
	
	
	Activator		activator;
	String[]		sessionRoot;

	LinkedHashMap	vendorPluginSessions;
	private Util 	util;
	
	public ReadOnlyDataSession(Activator activator, String[] sessionRoot) throws DmtException {
		this.activator = activator;
		this.sessionRoot = sessionRoot;
		util = new Util( activator );
	}
	

	/**
	 * checks whether the given request can be passed to a registered VendorDataPlugin
	 * The plugin with the longest matching path will be returned or null, if no such mapping was found.
	 * If the given Path points to a purely structural node, then this method will always return null.  
	 * @return ServiceReference of the VendorDataPlugin flag registration
	 */
	ServiceReference getResponsibleVendorPlugin(String[] nodePath) {
		
		// per spec. the minimum length of a configurationPath is 3
		if ( nodePath.length < 3 )
			return null;

		String uri = Uri.toUri( nodePath );
		// if it is a pure structure node (i.e. not directly mapped) then there is definitely no responsible plugin
		if ( isStructureNode( nodePath, EXCLUDE_DIRECT_MATCH ) && ! isDirectMatch( uri ) )
				return null;
		
		String topLevel = nodePath[0] + "/" + nodePath[1] + "/" + nodePath[2];
		String filter1 = "(" + _MAPPED_NODE_PATH + "=" + topLevel + ")";
		String filter2 = "(" + _MAPPED_NODE_PATH + "=" + topLevel + "/*)";
		String filter = "(|" + filter1 + filter2 + ")";
		ServiceReference ref = null;
		int max = 0;
		try {
			// get all refs matching the top-level config-path and find the one with the longest match
			ServiceReference[] refs = activator.context.getServiceReferences( VendorDataPlugin.class.getName(), filter );
			if ( refs != null ) {
				for (int i = 0; i < refs.length; i++) {
					String mappedPath = (String) refs[i].getProperty( _MAPPED_NODE_PATH );
					// mappedPath must be fully included in nodePath
					if ( uri.startsWith( mappedPath )) {
						// check and remember longest match
						int d = util.countSlashes( mappedPath );
						if ( d > max ) {
							ref = refs[i];
							max = d;
						}
					}
				}
			}
		} catch (InvalidSyntaxException e) {
			activator.logError( "error while looking up VendorDataPlugins in the registry", e );
		}
		return ref;
	}

	public void close() throws DmtException {
		// we have to close all involved vendorPluginSessions
		// System.out.println(
		// "**************** close() called on read-only session ...");
		// Enumeration sessions = getVendorPluginSessions().elements();
		// while (sessions.hasMoreElements()) {
		SessionInfo[] sessionInfos = (SessionInfo[]) getVendorPluginSessions()
				.values().toArray(
						new SessionInfo[getVendorPluginSessions().size()]);
		DmtSession[] sessions = getOrderedSessions( sessionInfos );
		for (int i = 0; i < sessions.length; i++) {
			try {
				DmtSession session = (DmtSession) sessions[i];
				session.close();

			}
			catch (DmtException e) {
				activator
						.logError(
								"error while closing an involved vendor plugin session",
								e);
			}
		}
	}

	/**
	 * 
	 */
	public String[] getChildNodeNames(String[] nodePath) throws DmtException {
		
		String[] directNodeNames = null;

		// do we have a responsible vendorPlugin ?
		ServiceReference vendorPluginRef = getResponsibleVendorPlugin( nodePath );
		if ( vendorPluginRef != null ) {
			VendorPluginInfo pluginInfo = (VendorPluginInfo) vendorPluginRef.getProperty( _PLUGIN_INFO );
			DmtSession session = getSession(pluginInfo);
			// and pass the request through
			directNodeNames = session.getChildNodeNames(toVendorPath(nodePath,
					pluginInfo));
		}
		
		// we also have to get all structure nodes created for configured root-nodes and 
		// the mapped configurationPathes of other registered VendorPlugins
		
		// find all registered instances of MappedPath with this path prefix
		String myPath = Uri.toUri( nodePath );
		String filter = "(" + _MAPPED_NODE_PATH + "=" + Uri.toUri( nodePath ) + "/*)";
		ServiceReference[] refs;
		try {
			refs = activator.context.getServiceReferences( MappedPath.class.getName(), filter );
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			throw new DmtException( myPath, DmtException.COMMAND_FAILED, "Error in Filter syntax while looking up child names in the registry" );
		}
		// TreeSet used to filter out duplicates
		TreeSet children = new TreeSet();
		if ( refs != null ) {
			for (int i = 0; i < refs.length; i++) {
				String mappedPath = (String) refs[i].getProperty( _MAPPED_NODE_PATH );
				// cut out the next segment of this path
				int nextSegment = mappedPath.indexOf( '/', myPath.length() + 1 );
				if ( nextSegment == -1 )
					nextSegment = mappedPath.length();
				String child = mappedPath.substring( myPath.length() + 1, nextSegment );
				children.add( child );
			}
		}

		Collection direct = directNodeNames != null ? Arrays.asList( directNodeNames ) : new Vector();
		children.addAll( direct );
		
		return (String[]) children.toArray( new String[children.size()]);
	}

	/**
	 * requests either the MetaNode from a real Vendor plugin or creates a MetaNode for an emulated interior node
	 */
	public MetaNode getMetaNode(String[] nodePath) throws DmtException {
		MetaNode value = null;
		if ( ! isStructureNode( nodePath, INCLUDE_DIRECT_MATCH ) ) {
			ServiceReference vendorPluginRef = getResponsibleVendorPlugin( nodePath );
			if ( vendorPluginRef != null ) {
				VendorPluginInfo pluginInfo = (VendorPluginInfo) vendorPluginRef.getProperty( _PLUGIN_INFO );
				// if yes, get a suitable vendorPlugin session
				DmtSession session = getSession(pluginInfo);
				// and pass the request through
				value = session.getMetaNode(toVendorPath(nodePath, pluginInfo));
			}
		}
		else {
			value = new SubtreeMetaNode(Uri.toUri( nodePath ) );
		}
		return value;
	}

	public int getNodeSize(String[] nodePath) throws DmtException {
		int value = 0;
		if ( ! isStructureNode( nodePath, INCLUDE_DIRECT_MATCH ) ) {
			ServiceReference vendorPluginRef = getResponsibleVendorPlugin( nodePath );
			if ( vendorPluginRef != null ) {
				VendorPluginInfo pluginInfo = (VendorPluginInfo) vendorPluginRef.getProperty( _PLUGIN_INFO );
				DmtSession session = getSession(pluginInfo);
				// and pass the request through
				value = session.getNodeSize(toVendorPath(nodePath, pluginInfo));
			}
		}
		return value;
	}

	public Date getNodeTimestamp(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath,
				DmtException.FEATURE_NOT_SUPPORTED,
				"Timestamp is not available for this node from the DMTSubtree.");
	}

	public String getNodeTitle(String[] nodePath) throws DmtException {
		String value = null;
		if ( ! isStructureNode( nodePath, INCLUDE_DIRECT_MATCH ) ) {
			ServiceReference vendorPluginRef = getResponsibleVendorPlugin( nodePath );
			if ( vendorPluginRef != null ) {
				VendorPluginInfo pluginInfo = (VendorPluginInfo) vendorPluginRef.getProperty( _PLUGIN_INFO );
				DmtSession session = getSession(pluginInfo);
				// and pass the request through
				value = session.getNodeTitle(toVendorPath(nodePath, pluginInfo));
			}
		}
		return value;
	}

	public String getNodeType(String[] nodePath) throws DmtException {
		String value = null;
		if ( ! isStructureNode( nodePath, INCLUDE_DIRECT_MATCH ) ) {
			ServiceReference vendorPluginRef = getResponsibleVendorPlugin( nodePath );
			if ( vendorPluginRef != null ) {
				VendorPluginInfo pluginInfo = (VendorPluginInfo) vendorPluginRef.getProperty( _PLUGIN_INFO );
				DmtSession session = getSession(pluginInfo);
				// and pass the request through
				value = session.getNodeType(toVendorPath(nodePath, pluginInfo));
			}
		}
		return value;
	}

	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		DmtData value = null;
		if ( ! isStructureNode( nodePath, INCLUDE_DIRECT_MATCH ) ) {
			ServiceReference vendorPluginRef = getResponsibleVendorPlugin( nodePath );
			if ( vendorPluginRef != null ) {
				VendorPluginInfo pluginInfo = (VendorPluginInfo) vendorPluginRef.getProperty( _PLUGIN_INFO );
				DmtSession session = getSession(pluginInfo);
				// and pass the request through
				value = session.getNodeValue(toVendorPath(nodePath, pluginInfo));
			}
		}
		return value;
	}

	public int getNodeVersion(String[] nodePath) throws DmtException {
		int value = 1;
		if ( ! isStructureNode( nodePath, INCLUDE_DIRECT_MATCH ) ) {
			ServiceReference vendorPluginRef = getResponsibleVendorPlugin( nodePath );
			if ( vendorPluginRef != null ) {
				VendorPluginInfo pluginInfo = (VendorPluginInfo) vendorPluginRef.getProperty( _PLUGIN_INFO );
				DmtSession session = getSession(pluginInfo);
				// and pass the request through
				value = session.getNodeVersion(toVendorPath(nodePath, pluginInfo));
			}
		}
		return value;
	}

	public boolean isLeafNode(String[] nodePath) throws DmtException {
		boolean value = false;
		if ( ! isStructureNode( nodePath, INCLUDE_DIRECT_MATCH ) ) {
			ServiceReference vendorPluginRef = getResponsibleVendorPlugin( nodePath );
			if ( vendorPluginRef != null ) {
				VendorPluginInfo pluginInfo = (VendorPluginInfo) vendorPluginRef.getProperty( _PLUGIN_INFO );
				DmtSession session = getSession(pluginInfo);
				// and pass the request through
				value = session.isLeafNode(toVendorPath(nodePath, pluginInfo));
			}
		}
		return value;
	}

	
	
	public boolean isNodeUri(String[] nodePath) {
		boolean value = true;
		if ( ! isStructureNode( nodePath, INCLUDE_DIRECT_MATCH ) ) {
			ServiceReference vendorPluginRef = getResponsibleVendorPlugin( nodePath );
			if ( vendorPluginRef != null ) {
				VendorPluginInfo pluginInfo = (VendorPluginInfo) vendorPluginRef.getProperty( _PLUGIN_INFO );
				DmtSession session = null;
				try {
					session = getSession(pluginInfo);
					// and pass the request through
					value = session.isNodeUri(toVendorPath(nodePath, pluginInfo));
				} catch (DmtException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else 
				value = false;
		}
		return value;
	}

	public void nodeChanged(String[] nodePath) throws DmtException {
		// TODO: implement the nodeChanged callback
	}

	/**
	 * local facade for the retrieval (including potential creation) of a session with lock type SHARED 
	 * @param pluginInfo
	 * @return
	 * @throws DmtException
	 */
	DmtSession getSession(VendorPluginInfo pluginInfo) throws DmtException {
		return getSession(pluginInfo, DmtSession.LOCK_TYPE_SHARED);
	}

	/**
	 * looks-up a session in the local mapping and returns an already existing
	 * session for re-use if no existing session is found it will create a new
	 * one for the given lock-type, store it for later re-use and return it
	 * directly
	 * 
	 * @param pluginInfo
	 * @param lockType
	 * @return
	 * @throws DmtException
	 */
	DmtSession getSession(VendorPluginInfo pluginInfo, int lockType)
			throws DmtException {
		DmtSession session = null;
		SessionInfo sessionInfo = (SessionInfo) getVendorPluginSessions().get(pluginInfo.getDataRootURI());
		session = sessionInfo != null ? sessionInfo.session : null;
		if (session == null) {
			// request a DmtSession from the DmtAdmin for the rootDataURI of the
			// VendorPlugin
			DmtAdmin dmtAdmin = (DmtAdmin) activator.dmtTracker.getService();
			if (dmtAdmin == null) {
				throw new DmtIllegalStateException("no DmtAdmin available");
			}
			session = dmtAdmin
					.getSession(pluginInfo.getDataRootURI(), lockType);
			activator
					.logDebug("created session for uri '"
							+ pluginInfo.getDataRootURI()
							+ "' with  lock type: " + lockTypeName(lockType));

			// wrap the session in sessioninfo object to keep the relation to the configurationPath for later use (e.g. ordered commits)
			getVendorPluginSessions().put(pluginInfo.getDataRootURI(), new SessionInfo( session, pluginInfo.getConfigurationPath() ));
		}
		return session;

	}

	private String lockTypeName(int lockType) {
		String name = null;
		switch (lockType) {
			case DmtSession.LOCK_TYPE_SHARED :
				name = "SHARED";
				break;
			case DmtSession.LOCK_TYPE_EXCLUSIVE :
				name = "EXCLUSIVE";
				break;
			case DmtSession.LOCK_TYPE_ATOMIC :
				name = "ATOMIC";
				break;

			default :
				name = "unknown";
		}
		return name;
	}

	/**
	 * Rewrites the URI from the igd-root to the vendors dataRooot. Therefore it
	 * takes the suffix (everything thats longer than igd-root +
	 * hgConfigurationPath) and add it to the vendors dataRootURI
	 * 
	 * @param nodePath
	 * @return
	 */
	String toVendorPath(String[] nodePath, VendorPluginInfo pluginInfo) {
		if (nodePath == null || pluginInfo == null)
			return null;
		String path = Uri.toUri( nodePath );
		String prefix = pluginInfo.getConfigurationPath();

		String suffix = path.substring(prefix.length()); 
		// if the plugin is multiple, we have to remove the leading number from the resulting dataRootUri
		if ( pluginInfo.isMultiple() ) {
			int pos = suffix.indexOf('/', 1);
			if ( pos > -1 )
				suffix = suffix.substring(pos);
			else
				suffix = "";
		}
		return pluginInfo.getDataRootURI() + suffix;
	}

	/**
	 * @return the vendorPluginSessions
	 */
	LinkedHashMap getVendorPluginSessions() {
		if (vendorPluginSessions == null) {
			vendorPluginSessions = new LinkedHashMap();
		}
		return vendorPluginSessions;
	}
	
	
	/**
	 * orders the sessions according to their depth of the configurationPath and their age
	 * - deepest sessions will be first
	 * - if level is equal then the older session will be first
	 * @param sessionInfos ... contains the SessionInfo objects in chronological order of their creation
	 * @return
	 */
	DmtSession[] getOrderedSessions( SessionInfo[] sessionInfos ) {
		ArrayList orderedSessions = new ArrayList();
		for (int i = 0; i < sessionInfos.length; i++) {
			int index = 0;
			int depth = sessionInfos[i].depth;
			// kind of bubble sort
			while ( index < orderedSessions.size() && 
					((SessionInfo) orderedSessions.get( index )).depth >= depth ) {
				index++;
			}
			orderedSessions.add( index, sessionInfos[i] );
		}
		DmtSession[] sessions = new DmtSession[orderedSessions.size()];
		for (int i = 0; i < sessions.length; i++) {
			sessions[i] = ((SessionInfo) orderedSessions.get( i )).session;
		}
		return sessions;
	}
	
	/**
	 * Checks if the given node is generated ancestor of a mapped plugin
	 * 
	 * @param pluginInfo
	 * @return
	 */
	boolean isStructureNode( String[] path, boolean includeDirectMatch ) {
		String filter1 = "(" + _MAPPED_NODE_PATH + "=" + Uri.toUri( path ) + "/*)";
		String filter2 = includeDirectMatch ? "(" + _MAPPED_NODE_PATH + "=" + Uri.toUri( path ) + ")" : "";
		String filter = "(|" + filter1 + filter2 + ")";
//		System.out.println( "++++++++++++ filter: " + filter );
		
		ServiceReference[] refs;
		try {
			refs = activator.context.getServiceReferences( MappedPath.class.getName(), filter );
//			System.out.println( "++++++++++++ refs: " + refs );
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			activator.logError( "Error in filter syntax while looking up direct matches in the service registry.", e );
			return false;
		}
		return refs != null && refs.length > 0;
	}

	/**
	 * Checks for any registered plugin (rootNode or vendor plugin) with the same mappedNodePath 
	 * 
	 * @param pluginInfo
	 * @return
	 */
	private boolean isDirectMatch( String uri ) {
		String filter = "(" + _MAPPED_NODE_PATH + "=" + uri + ")";
		ServiceReference[] refs;
		try {
			refs = activator.context.getServiceReferences( MappedPath.class.getName(), filter );
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			activator.logError( "Error in filter syntax while looking up direct matches in the service registry.", e );
			return false;
		}
		return refs != null && refs.length > 0;
	}
	
	/*
	 * helper class for ordering of sessions. It keeps the configurationPath attached to the session in 
	 * order to allow for later re-ordering according to the tree-level.
	 */
	class SessionInfo {
		DmtSession session;
		String configurationPath;
		int depth = 0;
		public SessionInfo( DmtSession session, String configurationPath ) {
			this.session = session;
			this.configurationPath = configurationPath;
			this.depth = countChar( configurationPath, '/' ); 
		}
		private int countChar( String s, char c )  {
			int count = 0;
			int p = -1;
			while ( (p = s.indexOf( c, p+1 )) != -1) 
				count++;
			return count;
		}
	}

}
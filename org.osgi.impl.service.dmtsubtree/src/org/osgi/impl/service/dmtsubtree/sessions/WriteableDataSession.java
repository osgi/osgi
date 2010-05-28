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

import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.spi.ReadWriteDataSession;

import java.util.LinkedHashMap;

import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.dmtsubtree.Activator;
import org.osgi.impl.service.dmtsubtree.mapping.VendorPluginInfo;

public class WriteableDataSession extends ReadOnlyDataSession implements
		ReadWriteDataSession {

	// Writeable sessions are stored in it's own (overwritten) map
	LinkedHashMap	vendorPluginSessions;

	public WriteableDataSession(Activator activator, String[] sessionRoot) throws DmtException {
		super(activator, sessionRoot);
	}

	public void copy(String[] nodePath, String[] newNodePath, boolean recursive)
			throws DmtException {
		if ( ! isStructureNode( nodePath, INCLUDE_DIRECT_MATCH ) ) {
			ServiceReference vendorPluginRef = getResponsibleVendorPlugin( nodePath );
			if ( vendorPluginRef != null ) {
				VendorPluginInfo pluginInfo = (VendorPluginInfo) vendorPluginRef.getProperty( _PLUGIN_INFO );
				DmtSession session = getSession(pluginInfo);
				// and pass the request through
				session.copy(toVendorPath(nodePath, pluginInfo), toVendorPath(
						newNodePath, pluginInfo), recursive);
			}
		}
		else {
			throw new DmtException(nodePath, DmtException.PERMISSION_DENIED,
					"copy actions are not allowed for node: " + nodePath);
		}
	}

	public void createInteriorNode(String[] nodePath, String type)
			throws DmtException {
		if ( ! isStructureNode( nodePath, INCLUDE_DIRECT_MATCH ) ) {
			ServiceReference vendorPluginRef = getResponsibleVendorPlugin( nodePath );
			if ( vendorPluginRef != null ) {
				VendorPluginInfo pluginInfo = (VendorPluginInfo) vendorPluginRef.getProperty( _PLUGIN_INFO );
				DmtSession session = getSession(pluginInfo);
				// and pass the request through
				session
						.createInteriorNode(toVendorPath(nodePath, pluginInfo),
								type);
			}
		}
		else {
			throw new DmtException(nodePath, DmtException.PERMISSION_DENIED,
					"creation of interior nodes is not allowed for node: "
							+ nodePath);
		}
	}

	public void createLeafNode(String[] nodePath, DmtData value, String mimeType)
			throws DmtException {
		if ( ! isStructureNode( nodePath, INCLUDE_DIRECT_MATCH ) ) {
			ServiceReference vendorPluginRef = getResponsibleVendorPlugin( nodePath );
			if ( vendorPluginRef != null ) {
				VendorPluginInfo pluginInfo = (VendorPluginInfo) vendorPluginRef.getProperty( _PLUGIN_INFO );
				DmtSession session = getSession(pluginInfo);
				// and pass the request through
				session.createLeafNode(toVendorPath(nodePath, pluginInfo), value,
						mimeType);
			}
		}
		else {
			throw new DmtException(nodePath, DmtException.PERMISSION_DENIED,
					"creation of leaf nodes is not allowed for node: "
							+ nodePath);
		}
	}

	public void deleteNode(String[] nodePath) throws DmtException {
		if ( ! isStructureNode( nodePath, INCLUDE_DIRECT_MATCH ) ) {
			ServiceReference vendorPluginRef = getResponsibleVendorPlugin( nodePath );
			if ( vendorPluginRef != null ) {
				VendorPluginInfo pluginInfo = (VendorPluginInfo) vendorPluginRef.getProperty( _PLUGIN_INFO );
				DmtSession session = getSession(pluginInfo);
				// and pass the request through
				session.deleteNode(toVendorPath(nodePath, pluginInfo));
			}
		}
		else {
			throw new DmtException(nodePath, DmtException.PERMISSION_DENIED,
					"deleting is not allowed for node: " + nodePath);
		}
	}

	public void renameNode(String[] nodePath, String newName)
			throws DmtException {
		if ( ! isStructureNode( nodePath, INCLUDE_DIRECT_MATCH ) ) {
			ServiceReference vendorPluginRef = getResponsibleVendorPlugin( nodePath );
			if ( vendorPluginRef != null ) {
				VendorPluginInfo pluginInfo = (VendorPluginInfo) vendorPluginRef.getProperty( _PLUGIN_INFO );
				DmtSession session = getSession(pluginInfo);
				// and pass the request through
				session.renameNode(toVendorPath(nodePath, pluginInfo), newName);
			}
		}
		else {
			throw new DmtException(nodePath, DmtException.PERMISSION_DENIED,
					"renaming is not allowed for node: " + nodePath);
		}
	}

	public void setNodeTitle(String[] nodePath, String title)
			throws DmtException {
		if ( ! isStructureNode( nodePath, INCLUDE_DIRECT_MATCH ) ) {
			ServiceReference vendorPluginRef = getResponsibleVendorPlugin( nodePath );
			if ( vendorPluginRef != null ) {
				VendorPluginInfo pluginInfo = (VendorPluginInfo) vendorPluginRef.getProperty( _PLUGIN_INFO );
				DmtSession session = getSession(pluginInfo);
				// and pass the request through
				session.setNodeTitle(toVendorPath(nodePath, pluginInfo), title);
			}
		}
		else {
			throw new DmtException(nodePath, DmtException.PERMISSION_DENIED,
					"setting of the node title is not allowed for node: "
							+ nodePath);
		}
	}

	public void setNodeType(String[] nodePath, String type) throws DmtException {
		if ( ! isStructureNode( nodePath, INCLUDE_DIRECT_MATCH ) ) {
			ServiceReference vendorPluginRef = getResponsibleVendorPlugin( nodePath );
			if ( vendorPluginRef != null ) {
				VendorPluginInfo pluginInfo = (VendorPluginInfo) vendorPluginRef.getProperty( _PLUGIN_INFO );
				DmtSession session = getSession(pluginInfo);
				// and pass the request through
				session.setNodeType(toVendorPath(nodePath, pluginInfo), type);
			}
		}
		else {
			throw new DmtException(nodePath, DmtException.PERMISSION_DENIED,
					"setting of the node type is not allowed for node: "
							+ nodePath);
		}
	}

	public void setNodeValue(String[] nodePath, DmtData data)
			throws DmtException {
		if ( ! isStructureNode( nodePath, INCLUDE_DIRECT_MATCH ) ) {
			ServiceReference vendorPluginRef = getResponsibleVendorPlugin( nodePath );
			if ( vendorPluginRef != null ) {
				VendorPluginInfo pluginInfo = (VendorPluginInfo) vendorPluginRef.getProperty( _PLUGIN_INFO );
				DmtSession session = getSession(pluginInfo);
				// and pass the request through
				session.setNodeValue(toVendorPath(nodePath, pluginInfo), data);
			}
		}
		else {
			throw new DmtException(nodePath, DmtException.PERMISSION_DENIED,
					"setting of the node value is not allowed for node: "
							+ nodePath);
		}
	}

	
	
	public void nodeChanged(String[] nodePath) throws DmtException {
		// TODO Auto-generated method stub
	}

	/**
	 * local facade for the retrieval (including potential creation) of a session with lock type EXCLUSIVE 
	 * @param pluginInfo
	 * @return
	 * @throws DmtException
	 */
	DmtSession getSession(VendorPluginInfo pluginInfo) throws DmtException {
		return getSession(pluginInfo, DmtSession.LOCK_TYPE_EXCLUSIVE);
	}

}

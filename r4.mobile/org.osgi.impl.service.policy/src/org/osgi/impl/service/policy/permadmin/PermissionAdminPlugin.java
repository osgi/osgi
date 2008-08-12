/*
 * ============================================================================
 * (c) Copyright 2005 Nokia
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
package org.osgi.impl.service.policy.permadmin;

import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.MetaNode;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.osgi.impl.service.policy.AbstractPolicyPlugin;
import org.osgi.impl.service.policy.PermissionInfoMetaNode;
import org.osgi.impl.service.policy.RootMetaNode;
import org.osgi.impl.service.policy.util.PermissionInfoComparator;
import org.osgi.impl.service.policy.util.Splitter;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;

/**
 *
 * implements the ./OSGi/Policies/Java/Bundle subtree.
 * 
 * @version $Revision$
 */
public class PermissionAdminPlugin extends AbstractPolicyPlugin {
	private PermissionAdmin	permissionAdmin;
	
	/*
	 * ------------------------------------- 
	 */
	private final static MetaNode	rootMetaNode = new RootMetaNode("PermissionAdmin tree");
	private final static MetaNode	locationsDirMetaNode = new RootMetaNode("permissions associated with bundle location");
	private final static MetaNode	permissionInfoMetaNode = new PermissionInfoMetaNode();
	private final static MetaNode 	defaultMetaNode = new DefaultMetaNode();
	private final static MetaNode	locationMetaNode = new LocationMetaNode();
	private final static MetaNode	locationEntryMetaNode = new LocationEntryMetaNode();
	private final static Comparator permissionInfoComparator = new PermissionInfoComparator();

	public static final String PERMISSIONINFO = PermissionInfoMetaNode.PERMISSIONINFO;
	public static final String LOCATION = "Location";
	public static final String LOCATIONS = "Locations";
	public static final String	DEFAULT	= "Default";

	/** internal representation of the tree */
	HashMap	locationEntries;
	
	/** default permissions */
	PermissionInfo[] defaultPermissions;

	private final PermissionInfo[] stringToPermissionInfos(String str) {
		String[] ss;
		ss = Splitter.split(str,'\n',0);
		PermissionInfo[] pis = new PermissionInfo[ss.length]; 
		for(int i=0;i<ss.length;i++) {
			pis[i] = new PermissionInfo(ss[i]);
		}
		
		// we do the sorting on this, instead of the original strings, since these
		// are canonical
		Arrays.sort(pis,permissionInfoComparator);
		return pis;
	}

	private final String permissionInfosToString(PermissionInfo[] permissionInfo) {
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<permissionInfo.length;i++) {
			sb.append(permissionInfo[i].getEncoded());
			sb.append("\n");
		}
		return sb.toString();
	}
	
	private final class Entry {
		String location;
		PermissionInfo[] permissionInfo; // sorted!

		public Entry(String location,PermissionInfo[] permissionInfo) {
			this.location = location;
			
			this.permissionInfo = (PermissionInfo[]) permissionInfo.clone();
			Arrays.sort(this.permissionInfo,permissionInfoComparator);
		}

		/**
		 * @param nodename the name of the node inside the entry to be checked 
		 * @return true, if it exists
		 */
		public boolean isNodeUri(String nodename) {
			if (PERMISSIONINFO.equals(nodename)) return true;
			if (LOCATION.equals(nodename)) return true;
			return false;
		}

		public DmtData getNodeValue(String nodename) {
			if (PERMISSIONINFO.equals(nodename))
				return new DmtData(permissionInfosToString(permissionInfo));
			if (LOCATION.equals(nodename))
				return new DmtData(location);

			// isNodeUri should prevent this
			throw new IllegalStateException();
		}

		public void setNodeValue(String nodename, DmtData data) throws IllegalArgumentException,DmtException {
			if (nodename.equals(PERMISSIONINFO)) {
				this.permissionInfo = stringToPermissionInfos(data.getString());
				return;
			}
			if (nodename.equals(LOCATION)) {
				this.location = data.getString();
				return;
			}

			// isNodeUri should prevent this
			throw new IllegalStateException(nodename);
		}
	};

	public PermissionAdminPlugin(ComponentContext context) {
		super(context);
		permissionAdmin = (PermissionAdmin) context.locateService("permissionAdmin");
		loadFromPermissionAdmin();
	}

	/**
	 * loads all settings from the permission admin
	 */
	private void loadFromPermissionAdmin() {
		String[] locations = permissionAdmin.getLocations();
		PermissionInfo[] permissionInfo;
		String location;
		if (locations==null) locations=new String[0];
		locationEntries = new HashMap();
		for(int i=0;i<locations.length;i++) {
			location = locations[i];
			permissionInfo = permissionAdmin.getPermissions(location);
			Entry e = new Entry(location,permissionInfo);
			locationEntries.put(mangle(e.location),e);
		}
		defaultPermissions = permissionAdmin.getDefaultPermissions();
	}

	public MetaNode getMetaNode(String fullPath[])
			throws DmtException {
		String[] path = chopPath(fullPath);
		if (path.length==0) {
			return rootMetaNode;
		}
		if (path.length>=1) {
			if (!path[0].equals(LOCATIONS)&&!path[0].equals(DEFAULT)) 
				throw new DmtException(fullPath,DmtException.NODE_NOT_FOUND,"");
		}
		if (path.length==1) {
			if (path[0].equals(LOCATIONS)) {
				return locationsDirMetaNode;
			} else {
				return defaultMetaNode;
			}
		}
		if (path.length>=2) {
			if (!path[0].equals(LOCATIONS))
				throw new DmtException(fullPath,DmtException.NODE_NOT_FOUND,"");
		}
		if (path.length==2) {
			return locationEntryMetaNode;
		}
		if (path.length>=3) {
			if (!path[2].equals(PERMISSIONINFO)&&!path[2].equals(LOCATION)) {
				throw new DmtException(fullPath,DmtException.NODE_NOT_FOUND,"");
			}
		}
		if (path.length==3) {
			if (path[2].equals(PERMISSIONINFO)) return permissionInfoMetaNode;
			if (path[2].equals(LOCATION)) return locationMetaNode;
		}

		throw new DmtException(fullPath,DmtException.NODE_NOT_FOUND,"");
	}

	public void rollback() throws DmtException {
		// simply don't write data back to the permission admin
		// but do some cleanup, anyway
		defaultPermissions = null;
		locationEntries = null;
	}

	public void setNodeValue(String fullPath[], DmtData data) throws DmtException {
		if (!isLeafNode(fullPath)) throw new DmtException(fullPath,DmtException.FEATURE_NOT_SUPPORTED,"cannot set value for this interior node");
		String[] path = chopPath(fullPath);
		switchToWriteMode();
		if (path.length==1) {
			// this must be the Default node
			this.defaultPermissions = stringToPermissionInfos(data.getString());
			return;
		}
		if (path.length==3) {
			// this must be Location/<location>/*
			Entry e = (Entry) locationEntries.get(path[1]);
			try {
				e.setNodeValue(path[2],data);
			} catch (IllegalArgumentException iae) {
				throw new DmtException(fullPath,DmtException.METADATA_MISMATCH,"cannot parse permission",iae);
			}
			return;
		}
		// this cannot happen, isNodeUri and MetaData info should prevent this
		throw new IllegalStateException();
	}

	public void deleteNode(String path[]) throws DmtException {
		switchToWriteMode();
		path = chopPath(path);
		if (path.length==1) {
			// this must be the Default node
			this.defaultPermissions = null;
			return;
		}
		if (path.length==2) {
			// this must be Location/<location>
			locationEntries.remove(path[1]);
			return;
		}
		// should not get here, metanode info should prevent this
		throw new IllegalStateException();
	}

	public void createInteriorNode(String path[],String type) throws DmtException {
		path = chopPath(path);;
		switchToWriteMode();
		if (path[0].equals(DEFAULT)) {
			defaultPermissions = new PermissionInfo[0];
			return;
		}

		locationEntries.put(path[1],new Entry(null,new PermissionInfo[0]));
	}

	public void commit() throws DmtException {
		if (!isDirty()) return;

		// check if current tree is consistent
		Collection coll = locationEntries.values();
		for (Iterator iter = coll.iterator(); iter.hasNext();) {
			Entry e = (Entry) iter.next();
			if ((e.location==null)||(e.location.equals(""))) {
				throw new DmtException(ROOT,DmtException.METADATA_MISMATCH,"location is empty");
			}
		}
		
		
		// the default
		permissionAdmin.setDefaultPermissions(this.defaultPermissions);
		
		// the location -> permissionInfo data
		// FIRST, put everything into the permissionadmin,
		// SECOND delete those locations from the permissionadmin, that are not in our
		// table.
		Set locationSet = new TreeSet();
		for (Iterator iter = coll.iterator(); iter.hasNext();) {
			Entry entry = (Entry) iter.next();
			locationSet.add(entry.location);
			permissionAdmin.setPermissions(entry.location,entry.permissionInfo);
		}
		String[] locations = permissionAdmin.getLocations();
		for(int i=0;i<locations.length;i++) {
			if (!locationSet.contains(locations[i])) {
				permissionAdmin.setPermissions(locations[i],null);
			}
		}

		// some cleanup
		locationEntries = null;
		defaultPermissions = null;
	}

	public void close() {}
	
	public boolean isNodeUri(String fullPath[]) {
		Entry e = null;
		String[] path = chopPath(fullPath);
		if (path.length==0) { return true; }
		if (path.length>=1) {
			if (!path[0].equals(DEFAULT)&&!path[0].equals(LOCATIONS)) return false;
			if (path[0].equals(DEFAULT)&&defaultPermissions==null) return false;
		}
		if (path.length==1) return true;
		if (path.length>=2) {
			if (!path[0].equals(LOCATIONS)) return false;
			e = (Entry) locationEntries.get(path[1]);
			if (e==null) return false;
		}
		if (path.length==2) return true;
		if (path.length>3) return false;
		
		return e.isNodeUri(path[2]);
	}

	public DmtData getNodeValue(String path[]) throws DmtException {
		if (!isLeafNode(path)) throw new DmtException(path,DmtException.FEATURE_NOT_SUPPORTED,"cannot get value for this interior node");
		path = chopPath(path);

		if (path.length==1) {
			return new DmtData(permissionInfosToString(defaultPermissions));
		}
		if (path.length==3) {
			Entry e = (Entry) locationEntries.get(path[1]);
			return e.getNodeValue(path[2]);
		}
		throw new IllegalStateException();
	}

	public boolean isLeafNode(String path[]) throws DmtException {
		path = chopPath(path);
		if (path.length==0) return false;
		if (path.length==1) {
			return (path[0].equals(DEFAULT));
		}
		if (path.length==2) {
			return false;
		}
		if (path.length==3) {
			return true;
		}
		return false;
	}

	public String[] getChildNodeNames(String path[]) throws DmtException {
		path = chopPath(path);
		if (path.length==0) {
			if (defaultPermissions==null) {
				return new String[] { LOCATIONS };
			} else {
				return new String[] { DEFAULT, LOCATIONS };
			}
		}
		if (path.length==1) {
			String keys[] = new String[0];
			keys = (String[]) locationEntries.keySet().toArray(keys);
			return keys;
		}
		if (path.length==2) {
			return new String[] { LOCATION, PERMISSIONINFO };
		}
		throw new IllegalStateException();
	}

	public void createLeafNode(String path[], DmtData value, String mimeType) throws DmtException {
		switchToWriteMode();
		// only one leaf node can be created: Default
		if (value==null) {
			defaultPermissions=new PermissionInfo[0];
		} else {
			defaultPermissions = stringToPermissionInfos(value.getString());
		}
	}
	
}

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

import java.security.NoSuchAlgorithmException;
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
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.dmt.DmtSession;
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
	private final static DmtMetaNode	rootMetaNode = new RootMetaNode("PermissionAdmin tree");
	private final static DmtMetaNode	locationDirMetaNode = new RootMetaNode("permissions associated with bundle location");
	private final static DmtMetaNode	permissionInfoMetaNode = new PermissionInfoMetaNode();
	private final static DmtMetaNode 	defaultMetaNode = new DefaultMetaNode();
	private final static DmtMetaNode	locationMetaNode = new LocationMetaNode();
	private final static DmtMetaNode	locationEntryMetaNode = new LocationEntryMetaNode();
	private final static Comparator permissionInfoComparator = new PermissionInfoComparator();

	public static final String PERMISSIONINFO = PermissionInfoMetaNode.PERMISSIONINFO;
	public static final String LOCATION = "Location";
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
				try {
					this.location = data.getString();
				}
				catch (DmtException e) {
					throw new IllegalArgumentException(e.getMessage());
				}
				return;
			}

			// isNodeUri should prevent this
			throw new IllegalStateException(nodename);
		}
	};

	public PermissionAdminPlugin() throws NoSuchAlgorithmException {}
	
	protected void activate(ComponentContext context) {
		super.activate(context);
		permissionAdmin = (PermissionAdmin) context.locateService("permissionAdmin");
	}

	public void open(String subtreeUri, int lockMode, DmtSession session)
			throws DmtException {
		super.open(subtreeUri,lockMode,session);
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

	public DmtMetaNode getMetaNode(String nodeUri)
			throws DmtException {
		String[] path = getPath(nodeUri);
		if (path.length==0) {
			return rootMetaNode;
		}
		if (path.length>=1) {
			if (!path[0].equals(LOCATION)&&!path[0].equals(DEFAULT)) 
				throw new DmtException(nodeUri,DmtException.NODE_NOT_FOUND,"");
		}
		if (path.length==1) {
			if (path[0].equals(LOCATION)) {
				return locationDirMetaNode;
			} else {
				return defaultMetaNode;
			}
		}
		if (path.length>=2) {
			if (!path[0].equals(LOCATION))
				throw new DmtException(nodeUri,DmtException.NODE_NOT_FOUND,"");
		}
		if (path.length==2) {
			return locationEntryMetaNode;
		}
		if (path.length>=3) {
			if (!path[2].equals(PERMISSIONINFO)&&!path[2].equals(LOCATION)) {
				throw new DmtException(nodeUri,DmtException.NODE_NOT_FOUND,"");
			}
		}
		if (path.length==3) {
			if (path[2].equals(PERMISSIONINFO)) return permissionInfoMetaNode;
			if (path[2].equals(LOCATION)) return locationMetaNode;
		}

		throw new DmtException(nodeUri,DmtException.NODE_NOT_FOUND,"");
	}

	public void rollback() throws DmtException {
		// simply don't write data back to the permission admin
		// but do some cleanup, anyway
		defaultPermissions = null;
		locationEntries = null;
	}

	public void setNodeValue(String nodeUri, DmtData data) throws DmtException {
		switchToWriteMode(nodeUri);
		String[] path = getPath(nodeUri);
		if (path.length==1) {
			// this must be the Default node
			this.defaultPermissions = stringToPermissionInfos(data.getString());
			return;
		}
		if (path.length==3) {
			// this must be Location/<location>/*
			Entry e = (Entry) locationEntries.get(path[1]);
			try {
				switchToWriteMode(nodeUri);
				e.setNodeValue(path[2],data);
			} catch (IllegalArgumentException iae) {
				throw new DmtException(nodeUri,DmtException.METADATA_MISMATCH,"cannot parse permission",iae);
			}
			return;
		}
		// this cannot happen, isNodeUri and MetaData info should prevent this
		throw new IllegalStateException();
	}

	public void deleteNode(String nodeUri) throws DmtException {
		switchToWriteMode(nodeUri);
		String[] path = getPath(nodeUri);
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

	public void createInteriorNode(String nodeUri) throws DmtException {
		String path[] = getPath(nodeUri);
		
		switchToWriteMode(nodeUri);
		
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
	
	public boolean isNodeUri(String nodeUri) {
		Entry e = null;
		String[] path = getPath(nodeUri);
		if (path.length==0) { return true; }
		if (path.length>=1) {
			if (!path[0].equals(DEFAULT)&&!path[0].equals(LOCATION)) return false;
			if (path[0].equals(DEFAULT)&&defaultPermissions==null) return false;
		}
		if (path.length==1) return true;
		if (path.length>=2) {
			if (!path[0].equals(LOCATION)) return false;
			e = (Entry) locationEntries.get(path[1]);
			if (e==null) return false;
		}
		if (path.length==2) return true;
		if (path.length>3) return false;
		
		return e.isNodeUri(path[2]);
	}

	public DmtData getNodeValue(String nodeUri) throws DmtException {
		String path[] = getPath(nodeUri);
		
		if (path.length==1) {
			return new DmtData(permissionInfosToString(defaultPermissions));
		}
		if (path.length==3) {
			Entry e = (Entry) locationEntries.get(path[1]);
			return e.getNodeValue(path[2]);
		}
		throw new IllegalStateException(nodeUri);
	}

	public boolean isLeafNode(String nodeUri) throws DmtException {
		String[] path = getPath(nodeUri);
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

	public void setDefaultNodeValue(String nodeUri) throws DmtException {
		String path[] = getPath(nodeUri);
		// TODO
	}

	public String[] getChildNodeNames(String nodeUri) throws DmtException {
		String[] path = getPath(nodeUri);
		if (path.length==0) {
			if (defaultPermissions==null) {
				return new String[] { LOCATION };
			} else {
				return new String[] { DEFAULT, LOCATION };
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
		throw new IllegalStateException(nodeUri);
	}

	public void createLeafNode(String nodeUri, DmtData value, String mimeType) throws DmtException {
		// only one leaf node can be created: Default
		switchToWriteMode(nodeUri);
		defaultPermissions = stringToPermissionInfos(value.getString());
	}

	public void createLeafNode(String nodeUri) throws DmtException {
		// only one leaf node can be created: Default
		switchToWriteMode(nodeUri);
		defaultPermissions = new PermissionInfo[0];
	}

	
}

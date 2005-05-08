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
	private final PermissionAdmin	permissionAdmin;
	
	/*
	 * ------------------------------------- 
	 */
	private final static DmtMetaNode	rootMetaNode = new RootMetaNode("permissions associated with bundle locations");
	private final static DmtMetaNode	permissionInfoMetaNode = new PermissionInfoMetaNode();
	private final static DmtMetaNode 	defaultMetaNode = new DefaultMetaNode();
	private final static DmtMetaNode	locationMetaNode = new LocationMetaNode();
	private final static Comparator permissionInfoComparator = new PermissionInfoComparator();

	public static final String dataRootURI = "./OSGi/Policies/Java/Bundle";
	public static final String PERMISSIONINFO = PermissionInfoMetaNode.PERMISSIONINFO;
	public static final String LOCATION = "Location";
	public static final String	DEFAULT	= "Default";

	/** internal representation of the tree */
	HashMap	entries;

	private final class Entry {
		final boolean isDefault;
		String location;
		PermissionInfo[] permissionInfo; // sorted!

		public Entry(boolean isDefault,String location,PermissionInfo[] permissionInfo) {
			this.isDefault = isDefault;
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
			if (LOCATION.equals(nodename) && !isDefault) return true;
			return false;
		}

		public DmtData getNodeValue(String nodename) {
			if (PERMISSIONINFO.equals(nodename)) {
				StringBuffer sb = new StringBuffer();
				for(int i=0;i<permissionInfo.length;i++) {
					sb.append(permissionInfo[i].getEncoded());
					sb.append("\n");
				}
				return new DmtData(sb.toString());
			}
			if (LOCATION.equals(nodename)) return new DmtData(location);

			// isNodeUri should prevent this
			throw new IllegalStateException();
		}

		public void setNodeValue(String nodename, DmtData data) throws IllegalArgumentException {
			if (nodename.equals(PERMISSIONINFO)) {
				String[] ss;
				try {
					ss = Splitter.split(data.getString(),'\n',0);
				}
				catch (DmtException e) {
					// this cannot happen, since metadata info should block non-string data
					throw new IllegalArgumentException(e.getMessage());
				}
				PermissionInfo[] pis = new PermissionInfo[ss.length]; 
				for(int i=0;i<ss.length;i++) {
					pis[i] = new PermissionInfo(ss[i]);
				}
				
				// we do the sorting on this, instead of the original strings, since these
				// are canonical
				Arrays.sort(pis,permissionInfoComparator);
				this.permissionInfo = pis;
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
			throw new IllegalStateException();
		}
	};

	
	/**
	 * create a new PermissionAdmin plugin for the DMT admin. It is the responsibility
	 * of the caller to register in the service registry.
	 * @param permissionAdmin
	 * @throws NoSuchAlgorithmException
	 */
	public PermissionAdminPlugin(PermissionAdmin permissionAdmin) throws NoSuchAlgorithmException {
		this.permissionAdmin = permissionAdmin;
		ROOT = dataRootURI;
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
		entries = new HashMap();
		for(int i=0;i<locations.length;i++) {
			location = locations[i];
			permissionInfo = permissionAdmin.getPermissions(location);
			Entry e = new Entry(false,location,permissionInfo);
			entries.put(hashCalculator.getHash(e.location),e);
		}
		permissionInfo = permissionAdmin.getDefaultPermissions();
		if (permissionInfo!=null) entries.put(DEFAULT,new Entry(true,null,permissionInfo));
	}

	public DmtMetaNode getMetaNode(String nodeUri)
			throws DmtException {
		String[] path = getPath(nodeUri);
		if (path.length==0) {
			return rootMetaNode;
		}
		if (path.length==1) {
			return defaultMetaNode;
		}
		if (path.length==2) {
			if (path[1].equals(PERMISSIONINFO)) return permissionInfoMetaNode;
			if (path[1].equals(LOCATION)) return locationMetaNode;
		}
		throw new IllegalStateException("not implemented");
	}

	public void rollback() throws DmtException {
		// simply don't write data back to the permission admin
		entries = null;
	}

	public void setNodeValue(String nodeUri, DmtData data) throws DmtException {
		String[] path = getPath(nodeUri);
		if (path.length!=2) {
			// this cannot happen, isNodeUri and MetaData info should prevent this
			throw new IllegalStateException();
		}
		Entry e = (Entry) entries.get(path[0]);
		try {
			switchToWriteMode(nodeUri);
			e.setNodeValue(path[1],data);
		} catch (IllegalArgumentException iae) {
			throw new DmtException(nodeUri,DmtException.METADATA_MISMATCH,"cannot parse permission",iae);
		}
	}

	public void deleteNode(String nodeUri) throws DmtException {
		switchToWriteMode(nodeUri);
		String[] path = getPath(nodeUri);
		if (path.length!=1) {
			// should not get here, metanode info should prevent this
			throw new IllegalStateException();
		}
		entries.remove(path[0]); // isNodeUri already checked this
	}

	public void createInteriorNode(String nodeUri) throws DmtException {
		String path[] = getPath(nodeUri);
		
		switchToWriteMode(nodeUri);
		
		if (path[0].equals(DEFAULT)) {
			entries.put(DEFAULT,new Entry(true,null,new PermissionInfo[]{}));
			return;
		}

		entries.put(path[0],new Entry(false,null,new PermissionInfo[0]));
	}

	public void commit() throws DmtException {
		if (!isDirty()) return;

		// check if current tree is consistent
		Collection coll = entries.values();
		for (Iterator iter = coll.iterator(); iter.hasNext();) {
			Entry e = (Entry) iter.next();
			if (e.isDefault) continue;
			if ((e.location==null)||(e.location.equals(""))) {
				throw new DmtException(dataRootURI,DmtException.METADATA_MISMATCH,"location is empty");
			}
		}
		
		
		// the default
		Entry defaulte = (Entry) entries.get(DEFAULT);
		if (defaulte == null) {
			permissionAdmin.setDefaultPermissions(null);
		} else {
			permissionAdmin.setDefaultPermissions(defaulte.permissionInfo);
		}
		
		// the location -> permissionInfo data
		// FIRST, put everything into the permissionadmin,
		// SECOND delete those locations from the permissionadmin, that are not in our
		// table.
		Set locationSet = new TreeSet();
		for (Iterator iter = coll.iterator(); iter.hasNext();) {
			Entry entry = (Entry) iter.next();
			if (entry.isDefault) continue;
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
		entries = null;
	}

	public void close() {}
	
	public boolean isNodeUri(String nodeUri) {
		Entry e = null;
		String[] path = getPath(nodeUri);
		if (path.length==0) { return true; }
		if (path.length>=1) {
			e = (Entry) entries.get(path[0]);
			if (e==null) return false;
		}
		if (path.length==2) {
			return e.isNodeUri(path[1]);
		}
		if (path.length>2) {
			return false;
		}
		return true;
	}

	public DmtData getNodeValue(String nodeUri) throws DmtException {
		String path[] = getPath(nodeUri);
		if (path.length!=2) {
			// metanodes should prevent this
			throw new IllegalStateException();
		}
		Entry e = (Entry) entries.get(path[0]);
		return e.getNodeValue(path[1]);
	}

	public boolean isLeafNode(String nodeUri) throws DmtException {
		String[] path = getPath(nodeUri);
		return path.length==2;
	}

	public void setDefaultNodeValue(String nodeUri) throws DmtException {
		String path[] = getPath(nodeUri);
	}

	public String[] getChildNodeNames(String nodeUri) throws DmtException {
		String[] path = getPath(nodeUri);
		if (path.length==0) {
			String keys[] = new String[0];
			keys = (String[]) entries.keySet().toArray(keys);
			return keys;
		}
		if (path.length==1) {
			if (DEFAULT.equals(path[0])) {
				return new String[] { PERMISSIONINFO };
			} else {
				return new String[] { LOCATION, PERMISSIONINFO };
			}
		}
		throw new IllegalStateException("not implemented");
	}


}

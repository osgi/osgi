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

package org.osgi.impl.service.policy.permadmin;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import org.osgi.impl.service.policy.util.HashCalculator;
import org.osgi.impl.service.policy.util.Splitter;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtDataPlugIn;
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
public class PermissionAdminPlugin implements DmtDataPlugIn {
	private final PermissionAdmin	permissionAdmin;
	private final HashCalculator	hashCalculator;
	
	/*
	 * ------------------------------------- 
	 */
	private final static DmtMetaNode	rootMetaNode = new RootMetaNode();
	private final static DmtMetaNode	permissionInfoMetaNode = new PermissionInfoMetaNode();
	private final static DmtMetaNode defaultMetaNode = new DefaultMetaNode();
	private final static Comparator permissionInfoComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			PermissionInfo p1 = (PermissionInfo) o1;
			PermissionInfo p2 = (PermissionInfo) o2;
			return p1.toString().compareTo(p2.toString());
		}};

	public static final String dataRootURI = "./OSGi/Policies/Java/Bundle";
	public static final String PERMISSIONINFO = "PermissionInfo";
	public static final String LOCATION = "Location";
	public static final String	DEFAULT	= "Default";

	/** internal representation of the tree */
	HashMap	entries;
	boolean dirty;


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
			dirty = true;
			if (nodename.equals(PERMISSIONINFO)) {
				String[] ss = Splitter.split(data.getString(),'\n',0);
				PermissionInfo[] pis = new PermissionInfo[ss.length]; 
				for(int i=0;i<ss.length;i++) {
					pis[i] = new PermissionInfo(ss[i]);
				}
				
				// we do the sorting on this, instead of the original strings, since these
				// are canonical
				Arrays.sort(pis,permissionInfoComparator);
				this.permissionInfo = pis;
			}
			
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
		hashCalculator = new HashCalculator();
	}
	
	public void open(String subtreeUri, int lockMode, DmtSession session)
			throws DmtException {
		String[] path = getPath(subtreeUri); // this is also a check if it starts with the right subpath
		loadFromPermissionAdmin();
		dirty = false;
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

	public DmtMetaNode getMetaNode(String nodeUri, DmtMetaNode generic)
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
		}
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @return
	 * @see org.osgi.service.dmt.DmtDataPlugIn#supportsAtomic()
	 */
	public boolean supportsAtomic() {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.Dmt#rollback()
	 */
	public void rollback() throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @param nodeUri
	 * @param title
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.Dmt#setNodeTitle(java.lang.String, java.lang.String)
	 */
	public void setNodeTitle(String nodeUri, String title) throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	public void setNodeValue(String nodeUri, DmtData data) throws DmtException {
		String[] path = getPath(nodeUri);
		if (path.length!=2) {
			// this cannot happen, isNodeUri and MetaData info should prevent this
			throw new IllegalStateException();
		}
		Entry e = (Entry) entries.get(path[0]);
		try {
			e.setNodeValue(path[1],data);
		} catch (IllegalArgumentException iae) {
			throw new DmtException(nodeUri,DmtException.INVALID_DATA,"cannot parse permission",iae);
		}
	}

	/**
	 * @param nodeUri
	 * @param type
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.Dmt#setNodeType(java.lang.String, java.lang.String)
	 */
	public void setNodeType(String nodeUri, String type) throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	public void deleteNode(String nodeUri) throws DmtException {
		String[] path = getPath(nodeUri);
		if (path.length!=1) {
			// should not get here, metanode info should prevent this
			throw new IllegalStateException();
		}
		entries.remove(path[0]); // isNodeUri already checked this
		dirty = true;
	}

	public void createInteriorNode(String nodeUri) throws DmtException {
		String path[] = getPath(nodeUri);
		if (path.length!=1) throw new IllegalStateException();
		if (path[0].equals(DEFAULT)) {
			entries.put(DEFAULT,new Entry(true,null,new PermissionInfo[]{}));
			dirty = true;
			return;
		}
		throw new IllegalStateException("not implemented");
	}

	/**
	 * @param nodeUri
	 * @param type
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.Dmt#createInteriorNode(java.lang.String, java.lang.String)
	 */
	public void createInteriorNode(String nodeUri, String type)
			throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @param nodeUri
	 * @param value
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.Dmt#createLeafNode(java.lang.String, org.osgi.service.dmt.DmtData)
	 */
	public void createLeafNode(String nodeUri, DmtData value)
			throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @param nodeUri
	 * @param newNodeUri
	 * @param recursive
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.Dmt#clone(java.lang.String, java.lang.String, boolean)
	 */
	public void clone(String nodeUri, String newNodeUri, boolean recursive)
			throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @param nodeUri
	 * @param newName
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.Dmt#renameNode(java.lang.String, java.lang.String)
	 */
	public void renameNode(String nodeUri, String newName) throws DmtException {
		throw new DmtException(nodeUri,DmtException.COMMAND_NOT_ALLOWED,"no renaming is allowed");
	}

	public void close() throws DmtException {
		if (!dirty) return;
		// first, the default
		Entry defaulte = (Entry) entries.get(DEFAULT);
		if (defaulte == null) {
			permissionAdmin.setDefaultPermissions(null);
		} else {
			permissionAdmin.setDefaultPermissions(defaulte.permissionInfo);
		}
		
		// TODO the others
	}

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

	/**
	 * @param nodeUri
	 * @return
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.DmtReadOnly#getNodeTitle(java.lang.String)
	 */
	public String getNodeTitle(String nodeUri) throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @param nodeUri
	 * @return
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.DmtReadOnly#getNodeType(java.lang.String)
	 */
	public String getNodeType(String nodeUri) throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @param nodeUri
	 * @return
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.DmtReadOnly#getNodeVersion(java.lang.String)
	 */
	public int getNodeVersion(String nodeUri) throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @param nodeUri
	 * @return
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.DmtReadOnly#getNodeTimestamp(java.lang.String)
	 */
	public Date getNodeTimestamp(String nodeUri) throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	/**
	 * @param nodeUri
	 * @return
	 * @throws org.osgi.service.dmt.DmtException
	 * @see org.osgi.service.dmt.DmtReadOnly#getNodeSize(java.lang.String)
	 */
	public int getNodeSize(String nodeUri) throws DmtException {
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}

	public String[] getChildNodeNames(String nodeUri) throws DmtException {
		String[] path = getPath(nodeUri);
		if (path.length==0) {
			String keys[] = new String[0];
			keys = (String[]) entries.keySet().toArray(keys);
			return keys;
		}
		throw new IllegalStateException("not implemented");
		// TODO Auto-generated method stub
	}


	/**
	 * return the path elements, from our base
	 * @param nodeUri
	 * @return
	 */
	private String[] getPath(String nodeUri) {
		if (!nodeUri.startsWith(dataRootURI)) 
			throw new IllegalStateException("Dmt should not give me URIs that are not mine");
		if (nodeUri.length()==dataRootURI.length()) return new String[] {};
		return Splitter.split(nodeUri.substring(dataRootURI.length()+1),'/',0);
	}
}

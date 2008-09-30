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
package org.osgi.impl.service.policy.unittests;

import java.util.HashMap;
import java.util.Set;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;


public class DummyPermissionAdmin implements PermissionAdmin {
	PermissionInfo[] defaultPermissions;
	HashMap permissionTable = new HashMap();
	public PermissionInfo[] getPermissions(String location) {
		return (PermissionInfo[]) permissionTable.get(location);
	}
	public void setPermissions(String location, PermissionInfo[] permissions) {
		permissionTable.put(location,permissions);
	}
	public String[] getLocations() {
		Set keys = permissionTable.keySet();
		String[] locations = new String[keys.size()];
		keys.toArray(locations);
		return locations;
	}
	
	public PermissionInfo[] getDefaultPermissions() {
		return defaultPermissions;
	}
	public void setDefaultPermissions(PermissionInfo[] permissions) {
		defaultPermissions = permissions;
	}
}
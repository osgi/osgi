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
package org.osgi.impl.service.policy.util;

import java.util.Comparator;
import org.osgi.service.permissionadmin.PermissionInfo;

/**
 *
 * compares two PermissionInfos by their string representation
 * 
 * @version $Revision$
 */
public final class PermissionInfoComparator implements Comparator {
	public int compare(Object o1, Object o2) {
		PermissionInfo p1 = (PermissionInfo) o1;
		PermissionInfo p2 = (PermissionInfo) o2;
		return p1.getEncoded().compareTo(p2.getEncoded());
	}
}

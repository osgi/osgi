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
package org.osgi.impl.service.dmt.export;

import java.io.IOException;
import java.util.Map;

public interface DmtPrincipalPermissionAdmin {
    /**
     * Returns the mapping of principal names to Java permissions. The
     * permissions of a principal are represented as an array of PermissionInfo
     * objects. 
     * <p>
     * The returned map may be modified without affecting the stored
     * permissions.
     * 
     * @return a <code>Map</code> containing principal names and the
     *         permissions associated with them
     */
    Map  getPrincipalPermissions();
    
    /**
     * Replaces the current permission table with the argument. The given map
     * must contain <code>String</code> keys and <code>PermissionInfo[]</code>
     * values, otherwise an exception is thrown.
     * 
     * @param permissions the new set of principals and their permissions
     * @throws IllegalArgumentException if a key in the given <code>Map</code>
     *         is not a <code>String</code> or a value is not an array of
     *         <code>PermissionInfo</code> objects
     * @throws IOException if there is an error updating the persistent
     *         permission store
     */
    void setPrincipalPermissions(Map permissions) throws IOException;
}

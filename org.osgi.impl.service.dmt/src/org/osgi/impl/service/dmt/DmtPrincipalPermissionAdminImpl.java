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

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import org.osgi.framework.AdminPermission;
import org.osgi.impl.service.dmt.api.DmtPrincipalPermissionAdmin;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.permissionadmin.PermissionInfo;

// known problem: if a principal is called "service.pid" it will be ignored
public class DmtPrincipalPermissionAdminImpl 
        implements DmtPrincipalPermissionAdmin, ManagedService {
    
    private Hashtable permissions;
    private ConfigurationAdmin configAdmin;
    
    public DmtPrincipalPermissionAdminImpl(ConfigurationAdmin configAdmin) {
        this.configAdmin = configAdmin;
        
        // persisted permission table will be set by the Configuration Admin
        permissions = new Hashtable();
    }

    public synchronized Map getPrincipalPermissions() {
        System.getSecurityManager().checkPermission(new AdminPermission());
        return (Map) permissions.clone();
    }

    public synchronized void setPrincipalPermissions(Map permissions)
        throws IOException, IllegalArgumentException
    {
        System.getSecurityManager().checkPermission(new AdminPermission());

        // store permissions immediately, this will be overwritten by itself
        // when the (asynchronous) update arrives from the config. admin
        this.permissions = new Hashtable(permissions);
        
        Configuration config = 
            configAdmin.getConfiguration(DmtAdminActivator.PERMISSION_ADMIN_SERVICE_PID);
        
        Hashtable properties = new Hashtable();
        Iterator i = permissions.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            
            String principal;
            PermissionInfo[] permInfos;
            try {
                principal = (String) entry.getKey();
                permInfos = (PermissionInfo[]) entry.getValue();
            } catch(ClassCastException e) {
                throw new IllegalArgumentException("Invalid data type in permission map.");
            }
            
            String[] permStrings = new String[permInfos.length];
            for (int j = 0; j < permInfos.length; j++)
                permStrings[j] = permInfos[j].getEncoded();

            properties.put(principal, permStrings);
        }
        config.update(properties);
    }

    public synchronized void updated(Dictionary properties)
            throws ConfigurationException {
        if (properties == null) {
            permissions = new Hashtable();
            return;
        }
        Hashtable newPermissions = new Hashtable();
        Enumeration keys = properties.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();

            // TODO what about other mandatory fields?  Maybe ignore everything that is not a String[]?
            if(key == "service.pid")
                continue;
            
            String[] permStrings;
            try {
                permStrings = (String[]) properties.get(key);
            } catch (ClassCastException e) {
                throw new ConfigurationException(key,
                        "Invalid permission specification, value must be an array of Strings.");
            }
            
            PermissionInfo[] permInfos = new PermissionInfo[permStrings.length];
            for (int i = 0; i < permStrings.length; i++) {
                try {
                    permInfos[i] = new PermissionInfo(permStrings[i]);
                } catch (IllegalArgumentException e) {
                    throw new ConfigurationException(key,
                            "Invalid permission string: " + e.getMessage());
                }
            }
            newPermissions.put(key, permInfos);
        }
        permissions = newPermissions;
    }
}

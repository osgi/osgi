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
import java.security.Permission;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.impl.service.dmt.export.DmtPrincipalPermissionAdmin;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.permissionadmin.PermissionInfo;

// known problem: if a principal is called "service.pid" it will be ignored
public class DmtPrincipalPermissionAdminImpl 
        implements DmtPrincipalPermissionAdmin, ManagedService, ServiceListener {

    // The prefix used to distinguish the configuration entries containing
    // permission information from other entries (e.g. service.pid).
    private static final String CONFIG_KEY_PREFIX = "principal.";
    private static final int PREFIX_LENGTH = CONFIG_KEY_PREFIX.length();
    
	private Map<String,PermissionInfo[]>	permissions;
    private Context context;
    
    // the field "stored" indicates whether the last provided permission table has already be stored to the CM
    // It is used by the CM-ServiceListener to determine if the a table must be stored, when the CM becomes available. 
    private boolean stored;
    
    public DmtPrincipalPermissionAdminImpl(Context context ) {
        this.context = context;
        
        // persisted permission table will be set by the Configuration Admin
		permissions = new Hashtable<>();
        
        String caFilter = "(" + org.osgi.framework.Constants.OBJECTCLASS + "=" + ConfigurationAdmin.class.getName() + ")";
        try {
			context.getBundleContext().addServiceListener(this, caFilter);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
    }
    
    

	@Override
	public synchronized Map<String,PermissionInfo[]> getPrincipalPermissions() {
        checkPermission(new AdminPermission());
		return new Hashtable<>(permissions);
    }

    @Override
	public synchronized void setPrincipalPermissions(
			Map<String,PermissionInfo[]> permissions)
        throws IOException, IllegalArgumentException
    {
        checkPermission(new AdminPermission());

        // store permissions immediately, this will be overwritten by itself
        // when the (asynchronous) update arrives from the config. admin
		this.permissions = new Hashtable<>(permissions);
        
        ConfigurationAdmin configAdmin = context.getTracker(ConfigurationAdmin.class).getService();
        if(configAdmin == null) {
//          throw new MissingResourceException("Configuration Admin not found.",
//          ConfigurationAdmin.class.getName(), null);
        	stored = false;
        	System.out.println("ConfigurationAdmin not available --> delaying storage of PrincipalPermissions until CM gets available");
        	return;
        }

        storePermissions(configAdmin, permissions);
        stored = true;
    }

	private synchronized void storePermissions(ConfigurationAdmin configAdmin,
			@SuppressWarnings("hiding") Map<String,PermissionInfo[]> permissions)
    		throws IOException, IllegalArgumentException {
        Configuration config = configAdmin.getConfiguration(
                DmtAdminActivator.DMT_PERMISSION_ADMIN_SERVICE_PID);
        
		Dictionary<String,Object> properties = new Hashtable<>();
		Iterator<Map.Entry<String,PermissionInfo[]>> i = permissions.entrySet()
				.iterator();
        while (i.hasNext()) {
			Entry<String,PermissionInfo[]> entry = i.next();
            
            String principal;
            PermissionInfo[] permInfos;
            try {
                principal = entry.getKey();
                permInfos = entry.getValue();
            } catch(ClassCastException e) {
                throw new IllegalArgumentException("Invalid data type in permission map.");
            }
            
            String[] permStrings = new String[permInfos.length];
            for (int j = 0; j < permInfos.length; j++)
                permStrings[j] = permInfos[j].getEncoded();

            properties.put(CONFIG_KEY_PREFIX + principal, permStrings);
        }
        config.update(properties);
    }
    
    @Override
	public synchronized void updated(Dictionary<String, ? > properties)
            throws ConfigurationException {
        if (properties == null) {
			permissions = new Hashtable<>();
            return;
        }
		Map<String,PermissionInfo[]> newPermissions = new Hashtable<>();
		Enumeration<String> keys = properties.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();

            if(!key.startsWith(CONFIG_KEY_PREFIX))
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
            newPermissions.put(key.substring(PREFIX_LENGTH), permInfos);
        }
        permissions = newPermissions;
    }
    
    private void checkPermission(Permission p) {
        SecurityManager sm = System.getSecurityManager();
        if(sm  != null)
            sm.checkPermission(p);
    }


	@Override
	public void serviceChanged(ServiceEvent event) {
		if ( event.getType() == ServiceEvent.REGISTERED ) {
			if ( permissions != null && permissions.size() > 0 && ! stored ) {
				ConfigurationAdmin configAdmin = (ConfigurationAdmin) context.getBundleContext().getService(event.getServiceReference());
				try {
					System.out.println("ConfigurationAdmin became available --> storing pending PrincipalPermissions");
					storePermissions(configAdmin, permissions);
					this.stored = true;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				context.getBundleContext().ungetService(event.getServiceReference());
			}
			
		}
		else if ( event.getType() == ServiceEvent.UNREGISTERING ) {
			// ??
		}
		
	}
}

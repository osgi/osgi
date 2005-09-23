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

import java.security.AllPermission;
import java.util.Hashtable;
import java.util.Map;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.dmt.api.DmtPrincipalPermissionAdmin;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;

public class DmtAdminActivator implements BundleActivator {
    // do not change this, it is not handled dynamically in the plugins
    static final String DMT_ROOT = "./OSGi";
    
    static final String DMT_ROOT_PROPERTY = "org.osgi.service.dmt.root";
    
    static final String DMT_PERMISSION_ADMIN_SERVICE_PID = 
        "org.osgi.impl.service.dmt.perms";
    
    private ServiceRegistration	adminReg;
    private ServiceRegistration permissionReg;
    
    private Context context;

	public void start(BundleContext bc) throws BundleException {
		try {
            System.setProperty(DMT_ROOT_PROPERTY, DMT_ROOT);
            
            context = new Context(bc);
            
            DmtSessionImpl.init_acls();
            
			// creating the services
            DmtPrincipalPermissionAdmin dmtPermissionAdmin =
                new DmtPrincipalPermissionAdminImpl(context);
			DmtAdminImpl dmtAdmin = 
                new DmtAdminImpl(dmtPermissionAdmin, context);
			
            // registering the services
			adminReg = bc.registerService(DmtAdmin.class.getName(),
					dmtAdmin, null);
            
            String[] services = new String[] {
                    DmtPrincipalPermissionAdmin.class.getName(),
                    ManagedService.class.getName()
            };
            Hashtable properties = new Hashtable();
            properties.put("service.pid", DMT_PERMISSION_ADMIN_SERVICE_PID);
            permissionReg = bc.registerService(services, 
                    dmtPermissionAdmin, properties);
            
            // adding default (all) permissions for remote principal "admin"
            Map permissions = dmtPermissionAdmin.getPrincipalPermissions();
            permissions.put("admin", new PermissionInfo[] {
                    new PermissionInfo(AllPermission.class.getName(), "", "")
            });
            dmtPermissionAdmin.setPrincipalPermissions(permissions);
		} catch (Exception e) {
			System.out.println("Exception:" + e.getMessage());
			throw new BundleException("Failure in start() method.", e);
		}
	}

	public void stop(BundleContext bc) throws BundleException {
		// stopping everything in the context (e.g. service trackers)
        context.close();
		// unregistering the services
		adminReg.unregister();
        permissionReg.unregister();
	}
}

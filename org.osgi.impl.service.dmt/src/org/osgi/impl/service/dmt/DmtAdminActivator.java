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

import org.osgi.framework.*;
import org.osgi.impl.service.dmt.api.DmtPrincipalPermissionAdmin;
import org.osgi.impl.service.dmt.api.RemoteAlertSender;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.util.tracker.ServiceTracker;

public class DmtAdminActivator implements BundleActivator {
    static final String DMT_PERMISSION_ADMIN_SERVICE_PID = 
        "org.osgi.impl.service.dmt.permissions";

    private ServiceRegistration	adminReg;
	private ServiceRegistration	alertReg;
    private ServiceRegistration permissionReg;
	private ServiceTracker      eventTracker;
    private ServiceTracker      configTracker;
	private ServiceTracker      pluginTracker;
    private ServiceTracker      remoteAdapterTracker;


	public void start(BundleContext bc) throws BundleException {
		try {
            eventTracker = new ServiceTracker(bc, 
                    EventAdmin.class.getName(), null);
            eventTracker.open();
            
            configTracker = new ServiceTracker(bc, 
                    ConfigurationAdmin.class.getName(), null);
            configTracker.open();
            
			DmtPluginDispatcher dispatcher = new DmtPluginDispatcher(bc);
			String filter = "(|(objectClass=org.osgi.service.dmt.DmtDataPlugin)" +
					          "(objectClass=org.osgi.service.dmt.DmtExecPlugin)" +
                              "(objectClass=org.osgi.service.dmt.DmtReadOnlyDataPlugin))";
			pluginTracker = new ServiceTracker(bc, bc.createFilter(filter),
					dispatcher);
			pluginTracker.open();
            
            remoteAdapterTracker = new ServiceTracker(bc,
                    RemoteAlertSender.class.getName(), null);
            remoteAdapterTracker.open();
            
			// creating the services
            DmtPrincipalPermissionAdmin dmtPermissionAdmin =
                new DmtPrincipalPermissionAdminImpl(configTracker);
			DmtAdminImpl dmtAdmin = 
                new DmtAdminImpl(dmtPermissionAdmin,
                    dispatcher, eventTracker, remoteAdapterTracker);
			
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
		// stopping service trackers
		remoteAdapterTracker.close();
		pluginTracker.close();
        configTracker.close();
        eventTracker.close();
		// unregistering the service
		adminReg.unregister();
		alertReg.unregister();
        permissionReg.unregister();
	}
}

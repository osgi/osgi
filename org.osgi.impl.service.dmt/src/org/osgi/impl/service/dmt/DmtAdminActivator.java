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

// TODO cleanup service registrations properly in case of error
// TODO check that the service is not registered already?
// TODO when stop() is called, notify the impl. to release all refs to other services
// (these should be done in all activators!)
public class DmtAdminActivator implements BundleActivator {
    static final String PERMISSION_ADMIN_SERVICE_PID = 
        "org.osgi.impl.service.dmt.permissions";

    private ServiceRegistration	adminReg;
	private ServiceRegistration	alertReg;
    private ServiceRegistration permissionReg;
    private ServiceReference    eventChannelRef;
    private ServiceReference    configRef;
    private ServiceTracker      remoteAdapterTracker;
	private ServiceTracker      pluginTracker;

	public void start(BundleContext bc) throws BundleException {
		try {
            eventChannelRef = 
                bc.getServiceReference(EventAdmin.class.getName());
            if(eventChannelRef == null)
                throw new BundleException("Cannot find Event Channel service.");
            EventAdmin eventChannel = 
                (EventAdmin) bc.getService(eventChannelRef);
            if(eventChannel == null)
                throw new BundleException("Event Channel service no longer registered.");
            
            configRef = bc.getServiceReference(ConfigurationAdmin.class.getName());
            if(configRef == null)
                throw new Exception("Cannot find ConfigurationAdmin service.");
            ConfigurationAdmin ca = (ConfigurationAdmin) bc.getService(configRef);
            if(ca == null)
                throw new Exception("ConfigurationAdmin service no longer registered.");
            
			DmtPluginDispatcher dispatcher = new DmtPluginDispatcher(bc);
			//tracker = new ServiceTracker(bc, DmtDataPlugin.class.getName(), dispatcher);
			String filter = "(|(objectClass=org.osgi.service.dmt.DmtDataPlugin)"
					+ "(objectClass=org.osgi.service.dmt.DmtExecPlugin))";
			pluginTracker = new ServiceTracker(bc, bc.createFilter(filter),
					dispatcher);
			pluginTracker.open();
            
            remoteAdapterTracker = new ServiceTracker(bc,
                    RemoteAlertSender.class.getName(), null);
            remoteAdapterTracker.open();
            
			// creating the services
            DmtPrincipalPermissionAdmin dmtPermissionAdmin =
                new DmtPrincipalPermissionAdminImpl(ca);
			DmtAdminImpl dmtAdmin = 
                new DmtAdminImpl(dmtPermissionAdmin,
                    dispatcher, eventChannel, remoteAdapterTracker);
			
            // registering the services
			adminReg = bc.registerService(DmtAdmin.class.getName(),
					dmtAdmin, null);
            String[] services = new String[] {
                    DmtPrincipalPermissionAdmin.class.getName(),
                    ManagedService.class.getName()
            };
            Hashtable properties = new Hashtable();
            properties.put("service.pid", PERMISSION_ADMIN_SERVICE_PID);
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
        // releasing referenced services
        bc.ungetService(eventChannelRef);
        bc.ungetService(configRef);
		// stopping service trackers
		pluginTracker.close();
		remoteAdapterTracker.close();
		// unregistering the service
		adminReg.unregister();
		alertReg.unregister();
        permissionReg.unregister();
	}
}

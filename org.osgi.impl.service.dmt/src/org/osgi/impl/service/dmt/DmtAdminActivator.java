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

import java.lang.reflect.Field;
import java.security.AllPermission;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.dmt.export.DmtPrincipalPermissionAdmin;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.dmt.DmtAdmin;

// TODO removed the factory reference, not sure what happens

import org.osgi.service.dmt.notification.NotificationService;
import org.osgi.service.permissionadmin.PermissionInfo;

public class DmtAdminActivator implements BundleActivator {
    // do not change this, it is not handled dynamically in the plugins
    static final String DMT_ROOT = "./OSGi";
    
    static final String DMT_ROOT_PROPERTY = "org.osgi.service.dmt.osgi.root";
    
    static final String DMT_PERMISSION_ADMIN_SERVICE_PID = 
        "org.osgi.impl.service.dmt.perms";
    private static Context context;
    
	private ServiceRegistration<NotificationService>	notificationReg;
	private ServiceRegistration< ? >					adminFactoryReg;
	private ServiceRegistration< ? >					permissionReg;
    
    private Field factoryContext;
    private DmtAdminFactory dmtAdminFactory;
    private DmtAdminCore dmtAdmin;
    

	@Override
	public void start(BundleContext bc) throws BundleException {
        System.out.println("Dmt Admin activation started.");

		try {
            System.setProperty(DMT_ROOT_PROPERTY, DMT_ROOT);
            
//            // DmtAdmin service must be a singleton (as mandated by the spec),
//            // otherwise the context field settings/removals could get mixed up 
//            factoryContext = DmtServiceFactory.class.getDeclaredField("context");
//            factoryContext.setAccessible(true);
//            factoryContext.set(null, bc);
//            referenceDmtServiceFactory();

            context = new Context(bc);
            
            DmtSessionImpl.init_acls();
            
			// creating the services
            DmtPrincipalPermissionAdmin dmtPermissionAdmin =
                new DmtPrincipalPermissionAdminImpl(context);
            dmtAdmin = new DmtAdminCore(dmtPermissionAdmin, context);

            // add a listener for mapping changes - allows to immediately invalidate a session, 
    		// if relevant plugins have been mapped/unmapped
    		context.getPluginDispatcher().addMappingListener(dmtAdmin);

            dmtAdminFactory = 
                new DmtAdminFactory(context, dmtAdmin, dmtPermissionAdmin);
            NotificationService notificationService =
                new NotificationServiceImpl(context);

            // registering the services
			notificationReg = bc.registerService(NotificationService.class,
					notificationService, null);
            
			adminFactoryReg = bc.registerService(DmtAdmin.class.getName(),
					dmtAdminFactory, null);
                
            String[] services = new String[] {
                    DmtPrincipalPermissionAdmin.class.getName(),
                    ManagedService.class.getName()
            };
			Dictionary<String,Object> properties = new Hashtable<>();
            properties.put("service.pid", DMT_PERMISSION_ADMIN_SERVICE_PID);
            permissionReg = bc.registerService(services, 
                    dmtPermissionAdmin, properties);
            
            // adding default (all) permissions for remote principal "admin"
			Map<String,PermissionInfo[]> permissions = dmtPermissionAdmin
					.getPrincipalPermissions();
            permissions.put("admin", new PermissionInfo[] {
                    new PermissionInfo(AllPermission.class.getName(), "", "")
            });
            dmtPermissionAdmin.setPrincipalPermissions(permissions);
		} catch (Exception e) {
			System.out.println("Exception:" + e.getMessage());
			throw new BundleException("Failure in start() method.", e);
		}
        
        System.out.println("Dmt Admin activation finished successfully.");
	}

	@Override
	public void stop(BundleContext bc) throws BundleException {
        // stopping event delivery to listeners directly registered in DmtAdmin
        dmtAdminFactory.stop();
        
	    // unsetting our context from the private variable in DmtServiceFactory
        try {
        	if ( factoryContext != null )
        		factoryContext.set(null, null);
        } catch(IllegalAccessException e) {
            System.out.println("Exception:" + e.getMessage());
            throw new BundleException("Failure in stop() method.", e);
        }
		// unregistering the services
		notificationReg.unregister();
        adminFactoryReg.unregister();
        permissionReg.unregister();
        
		context.getPluginDispatcher().removeMappingListener(dmtAdmin);
        // stopping everything in the context (e.g. service trackers)
        context.close();
	}
    
	public static Context getContext() {
		return context;
	}
}

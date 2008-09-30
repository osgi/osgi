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
package com.nokia.test.plugin;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import info.dmtree.DmtAdmin;
//import info.dmtree.DmtSession;
//import org.osgi.service.permissionadmin.PermissionAdmin;
//import org.osgi.service.permissionadmin.PermissionInfo;


public class DoIt implements BundleActivator {
    
    private DmtAdmin admin;
    private TestDesktop desktop;

    public void start(BundleContext context) throws Exception {
        ServiceReference sref = context.getServiceReference(DmtAdmin.class.getName());
        admin = (DmtAdmin) context.getService(sref);
        
        desktop = new TestDesktop(admin);

//        ServiceReference paRef = context.getServiceReference(PermissionAdmin.class.getName());
//        if (null == paRef)
//            return;
//        PermissionAdmin pa = (PermissionAdmin) context.getService(paRef);
//        String loc = context.getBundle().getLocation();
//        pa.setPermissions(loc, new PermissionInfo[] {});
    }

    public void stop(BundleContext context) throws Exception {
        desktop.destroy();
    }

}

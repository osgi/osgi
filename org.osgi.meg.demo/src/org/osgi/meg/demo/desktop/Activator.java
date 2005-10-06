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
package org.osgi.meg.demo.desktop;

import java.io.File;
import java.security.Principal;

import org.osgi.framework.*;

/**
 * Controller part of the MVC pattern.
 */
public class Activator implements BundleActivator {
    
    private Model         model;
    private SimpleDesktop desktop;

    public Activator() {
        super();
    }

    public void start(BundleContext context) throws Exception {
        try {
            desktop = new SimpleDesktop(this);
            model = new Model(context, desktop);
            desktop.setVisible(true);
            
//            setPermissions(context);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

//    private void setPermissions(BundleContext context) {
//        ServiceReference sref = 
//            context.getServiceReference(ConditionalPermissionAdmin.class.getName());
//        ConditionalPermissionAdmin cpa = (ConditionalPermissionAdmin) context.getService(sref);
//        
//        ConditionalPermissionInfo info = cpa.addConditionalPermissionInfo(
//                new ConditionInfo[] {
//                        new ConditionInfo(BundleSignerCondition.class.getName(), new String[] {
//                            "CN=Sarah Bar, OU=Informatical Infrastructure Management, O=ConstructionOy, C=HU;CN=People, OU=Informatical Infrastructure Maintenance, O=ConstructionOy, L=Budapest, C=HU;CN=Root1, OU=FAKEDONTUSE, O=CASoft, L=Budapest, C=HU"
//                        })
//                }, new PermissionInfo[] {
//                        new PermissionInfo(AllPermission.class.getName(), "*", "*")
//                });
//        
//        context.ungetService(sref);
//    }

    public void stop(BundleContext context) throws Exception {
        model.destroy();
        desktop.setVisible(false);
    }

    public String installDp(String url) throws Exception {
        return model.installDp(url);
    }

    public String installDp(File f) throws Exception {
        return model.installDp(f);
    }

    public void uninstallDp(String dpName) throws Exception {
        model.uninstallDp(dpName);
    }

    public String installBundle(String url) throws Exception {
        return model.installBundle(url);
    }

    public String installBundle(File f) throws Exception {
        return model.installBundle(f);
    }

    public boolean existsDp(String dpName) {
        return model.existsDp(dpName);
    }

    public void uninstallBundle(String s) throws Exception {
        model.uninstallBundle(s);
    }

    public void launchApp(String pid) throws Exception {
        model.launchApp(pid);
    }

    public void stopApp(String pid) throws Exception {
        model.stopApp(pid);
    }

    public String[] getCondPerms() {
        return model.getCondPerms();
    }

    public Object[] getInfo(String cpiName) {
        return model.getInfo(cpiName);
    }

    public void delPermission(String cpiName) {
        model.delPermission(cpiName);
    }

    public void addPermission(String subjectDN) {
        model.addPermission(subjectDN);
    }
    
}

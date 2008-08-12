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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.application.ApplicationDescriptor;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.service.condpermadmin.BundleSignerCondition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Model part of the MVC pattern.
 */
public class Model implements BundleListener, ServiceListener,
        FrameworkListener, LogListener, EventHandler {

    private SimpleDesktop desktop;
	private BundleContext context;
	private Policy        policy;
    private Map appDescrs = new HashMap();  
    private Map appInstances = new HashMap();

    private ServiceTracker dAdminTracker;
    private ServiceTracker cpAdminTracker;
    
    private ListenToAppDescriptor listenToAppDescriptor = 
        new ListenToAppDescriptor();
    private ListenToAppHandle     listenToAppHandle = 
        new ListenToAppHandle();
    
    private class ListenToAppDescriptor implements ServiceListener {

        public void serviceChanged(ServiceEvent event) {
            ServiceReference sref = event.getServiceReference();
            String pid = (String) sref.getProperty(ApplicationDescriptor.APPLICATION_PID);
            switch (event.getType()) {
                case ServiceEvent.REGISTERED :
                    appDescrs.put(pid, sref);
                    desktop.onAppInstalled(pid);
                    break;
                case ServiceEvent.UNREGISTERING :
                    appDescrs.remove(pid);
                    desktop.onAppUninstalled(pid);
                    break;
                default :
                    break;
            }
        }
        
    }

    private class ListenToAppHandle implements ServiceListener {

        public void serviceChanged(ServiceEvent event) {
            ServiceReference sref = event.getServiceReference();
            String pid = (String) sref.getProperty(ApplicationHandle.APPLICATION_PID);
            switch (event.getType()) {
                case ServiceEvent.REGISTERED :
                    appInstances.put(pid, sref);
                    desktop.onAppLaunched(pid);
                    break;
                case ServiceEvent.UNREGISTERING :
                    appInstances.remove(pid);
                    desktop.onAppStopped(pid);
                    break;
                default :
                    break;
            }
        }
        
    }

	Model(BundleContext context, SimpleDesktop desktop) throws Exception {
		this.context = context;
		this.desktop = desktop;

        dAdminTracker = 
            new ServiceTracker(context, DeploymentAdmin.class.getName(), null);
        dAdminTracker.open();
        
        cpAdminTracker = 
            new ServiceTracker(context, ConditionalPermissionAdmin.class.getName(), null);
        cpAdminTracker.open();
        
        policy = new Policy(cpAdminTracker);
        policy.load();
        
        context.addServiceListener(listenToAppDescriptor, "(" + Constants.OBJECTCLASS +
                "=" + ApplicationDescriptor.class.getName() + ")");
        context.addServiceListener(listenToAppHandle, "(" + Constants.OBJECTCLASS +
                "=" + ApplicationHandle.class.getName() + ")");
        
        context.addBundleListener(this);
        context.addFrameworkListener(this);
        context.addServiceListener(this);
        
        ServiceReference logReference = context.getServiceReference(LogReaderService.class.getName());
        LogReaderService log =  (LogReaderService) context.getService(logReference);
        log.addLogListener(this);
        
        String[] topics = new String[] {EventConstants.EVENT_TOPIC, 
                "org/osgi/service/deployment/COMPLETE"};
        Hashtable ht = new Hashtable();
        ht.put(EventConstants.EVENT_TOPIC, topics);
        context.registerService(EventHandler.class.getName(), this, ht);
	}

    // closes the resources. called by the DesktopActivator
    public void destroy() {
        dAdminTracker.close();
        cpAdminTracker.close();
    }

	public String installDp(String aUrl) throws Exception {
        URL url = new URL(aUrl);
        InputStream is = url.openStream();
	    DeploymentAdmin da = (DeploymentAdmin) dAdminTracker.getService();
        
        try {
            DeploymentPackage dp = da.installDeploymentPackage(is);
            return dp.getName();
        } finally {
            if (null != is)
                is.close();
        }
	}

    public String installDp(File f) throws Exception {
        FileInputStream is = new FileInputStream(f);
        DeploymentAdmin da = (DeploymentAdmin) dAdminTracker.getService();
        
        try {
            DeploymentPackage dp = da.installDeploymentPackage(is);
            return dp.getName();
        } finally {
            if (null != is)
                is.close();
        }
    }

	public void uninstallDp(String dpName) throws Exception {
        DeploymentAdmin da = (DeploymentAdmin) dAdminTracker.getService();
        DeploymentPackage dp = da.getDeploymentPackage(dpName);
        dp.uninstallForced();
    }

    public String installBundle(String aUrl) throws Exception {
        URL url = new URL(aUrl);
        InputStream is = url.openStream();
        Bundle b = context.installBundle(aUrl, is);
        b.start();
        return b.getLocation();
    }

    public String installBundle(File f) throws Exception {
        FileInputStream is = new FileInputStream(f);
        Bundle b = context.installBundle("file:///" + f.getAbsolutePath(), is);
        b.start();
        return b.getLocation();
    }

    public boolean existsDp(String dpName) {
        DeploymentAdmin da = (DeploymentAdmin) dAdminTracker.getService();
        DeploymentPackage dp = da.getDeploymentPackage(dpName);
        return dp != null;
    }

    public void uninstallBundle(String s) throws Exception {
        Bundle[] bundles = context.getBundles();
        Bundle b = null;
        for (int i = 0; i < bundles.length; i++) {
            if (bundles[i].getLocation().equals(s)) {
                b = bundles[i];
                break;
            }
        }
        if (null != b) {
            if (b.getState() == Bundle.ACTIVE)
                b.stop();
            b.uninstall();
        }
    }

    public void launchApp(String pid) throws Exception {
        ServiceReference sref = (ServiceReference) appDescrs.get(pid);
        ApplicationDescriptor ad = (ApplicationDescriptor) context.getService(sref);
        
        // TODO app parameters
        ad.launch(new HashMap());

        context.ungetService(sref);
    }

    public void stopApp(String pid) throws Exception {
        ServiceReference sref = (ServiceReference) appInstances.get(pid);
        if (null == sref)
            return;
        ApplicationHandle handle = (ApplicationHandle) context.getService(sref);
        if (null == handle)
            return;
        handle.destroy();
    }

    public void bundleChanged(BundleEvent be) {
        if (be.getType() == BundleEvent.INSTALLED) {
            String location = be.getBundle().getLocation();
            if (!location.startsWith("osgi-dp:"))
                desktop.onBundleInstalled(location);
        }
        desktop.onEvent("BUNDLE EVENT: "+be.toString());
    }

    public void serviceChanged(ServiceEvent arg0) {
        desktop.onEvent("SERVICE EVENT: "+arg0.toString());
    }

    public void frameworkEvent(FrameworkEvent event) {
        Throwable t = event.getThrowable();
        String tail = "\n";
        if( null != t ) {
            tail = " Exception: " + t.toString();
        }
        desktop.onEvent("FRAMEWORK EVENT: " + event.toString() + tail);
    }

    public void handleEvent(Event event) {
        String symbName = (String) event.getProperty("deploymentpackage.name");
        Boolean succ = (Boolean) event.getProperty("successful");
        if (succ.booleanValue())
            desktop.onDpInstalled(symbName);
    }

    public void logged(LogEntry event) {        
        desktop.onEvent("LOG EVENT: ["+event.getBundle().getLocation()+ "] " + event.getMessage());
    }

    public String[] getCondPerms() {
        ConditionalPermissionAdmin cpa = (ConditionalPermissionAdmin) cpAdminTracker.getService();
        if (null == cpa)
            return new String[] {};
        ArrayList arr = new ArrayList(10);
        for (Enumeration en = cpa.getConditionalPermissionInfos(); en.hasMoreElements();) {
            ConditionalPermissionInfo cpi = (ConditionalPermissionInfo) en.nextElement();
            arr.add(cpi.getName());
        }
        String[] ret = new String[arr.size()];
        return (String[]) arr.toArray(ret);
    }

    public Object[] getInfo(String cpiName) {
        ConditionalPermissionAdmin cpa = (ConditionalPermissionAdmin) cpAdminTracker.getService();
        if (null == cpa)
            return null;
        ConditionalPermissionInfo cpi = cpa.getConditionalPermissionInfo(cpiName);
        if (null == cpi)
            return null;
        ConditionInfo[] condInfos = cpi.getConditionInfos();
        PermissionInfo[] permInfos = cpi.getPermissionInfos();
        String[] sCondInfos = new String[0];
        String[] sPermInfos = new String[0];
        if (null != condInfos)
            sCondInfos = new String[condInfos.length];
        if (null != permInfos)
            sPermInfos= new String[permInfos.length];
        for (int i = 0; i < condInfos.length; i++) {
            StringBuffer args = new StringBuffer();
            for (int j = 0; j < condInfos[i].getArgs().length; j++) {
                args.append(condInfos[i].getArgs()[j]);
            }
            sCondInfos[i] = condInfos[i].getType() + ": " + args; 
        }
        for (int i = 0; i < permInfos.length; i++) {
            sPermInfos[i] = permInfos[i].getType() + ": " + permInfos[i].getName() + ": " +
                    permInfos[i].getActions(); 
        }
        return new Object[] {sCondInfos, sPermInfos};
    }

	public void reloadPolicy() throws IOException {
		policy.clear();
		policy.load();
	}

}
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
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
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
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Model part of the MVC pattern.
 */
public class Model implements BundleListener, ServiceListener,
        FrameworkListener, LogListener {

    private SimpleDesktop desktop;
	private BundleContext context;
    private Map appDescrs = new HashMap();  
    private Map appInstances = new HashMap();

    private ServiceTracker dadminTracker;
    
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

        dadminTracker = 
            new ServiceTracker(context, DeploymentAdmin.class.getName(), null);
        dadminTracker.open();
        
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
	}

    // closes the resources. called by the DesktopActivator
    public void destroy() {
        dadminTracker.close();
    }

	public String installDp(String aUrl) throws Exception {
        URL url = new URL(aUrl);
        InputStream is = url.openStream();
	    DeploymentAdmin da = (DeploymentAdmin) dadminTracker.getService();
        
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
        DeploymentAdmin da = (DeploymentAdmin) dadminTracker.getService();
        
        try {
            DeploymentPackage dp = da.installDeploymentPackage(is);
            return dp.getName();
        } finally {
            if (null != is)
                is.close();
        }
    }

	public void uninstallDp(String dpName) throws Exception {
        DeploymentAdmin da = (DeploymentAdmin) dadminTracker.getService();
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
        DeploymentAdmin da = (DeploymentAdmin) dadminTracker.getService();
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
        ApplicationHandle handle = ad.launch(new HashMap());

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

    public void logged(LogEntry event) {        
        desktop.onEvent("LOG EVENT: ["+event.getBundle().getLocation()+ "] " + event.getMessage());
    }
   
}
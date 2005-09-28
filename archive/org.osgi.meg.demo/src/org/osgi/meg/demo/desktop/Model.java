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

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.application.ApplicationDescriptor;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * Model part of the MVC pattern.
 */
public class Model {

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
	}

    // closes the resources. called by the DesktopActivator
    public void destroy() {
        dadminTracker.close();
    }

	/*private static byte[] createImageData(ServiceReference reference,
			BundleContext context) {
		InputStream iconStream = null;
		// TODO not only 8x8
		String iconPath = (String) reference
				.getProperty("localized_icon_8x8_path");
		Bundle bundle = null;
		URL url = null;
		byte[] imageData = null;
		if (iconPath.startsWith("bundle://")) {
			String[] sarr = Splitter.split(iconPath, '/', 0);
			String fileName = sarr[sarr.length - 1];
			sarr = Splitter.split(sarr[2], ':', 0);
			bundle = context.getBundle(Long.parseLong(sarr[0]));
			url = bundle.getResource(fileName);
		}
		else {
			bundle = reference.getBundle();
			url = bundle.getResource(iconPath);
		}
		if (null != url) {
			try {
				iconStream = url.openConnection().getInputStream();
				imageData = createImageArray(iconStream);
				;
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				if (null != iconStream) {
					try {
						iconStream.close();
					}
					catch (IOException e) {
					}
				}
			}
		}
		return imageData;
	}*/

	/*private static byte[] createImageArray(InputStream iconStream) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			int d = iconStream.read();
			while (d != -1) {
				bos.write(d);
				d = iconStream.read();
			}
			return bos.toByteArray();
		}
		catch (IOException e) {
			return null;
		}
		finally {
			if (null != bos) {
				try {
					bos.close();
				}
				catch (IOException e) {
				}
			}
		}
	}*/

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

//	public String getLogs() {
//		ServiceReference sref = context
//				.getServiceReference(LogReaderService.class.getName());
//		LogReaderService lr = (LogReaderService) context.getService(sref);
//		StringBuffer sb = new StringBuffer();
//		String[] level = new String[] {"ERROR", "WARNING", "INFO", "DEBUG"};
//		for (Enumeration en = lr.getLog(); en.hasMoreElements();) {
//			LogEntry le = (LogEntry) en.nextElement();
//			Date d = new Date(le.getTime());
//			sb.append(d + "    " + level[le.getLevel() - 1] + " \t"
//					+ le.getMessage() + "\n");
//		}
//		context.ungetService(sref);
//		return sb.toString();
//	}

//	public void handleEvent(Event event) {
//		// TODO
//		if (!"Bundle Event".equals(event.getTopic()))
//			return;
//		Object[] pair = (Object[]) events.get(event.getTopic());
//		if (null == pair)
//			return;
//		Hashtable props = (Hashtable) pair[0];
//		ApplicationDescriptor descr = (ApplicationDescriptor) pair[1];
//		long ide = ((Long) event.getProperty("bundle.id")).longValue();
//		long idp = ((Long) props.get("bundle.id")).longValue();
//		if (ide == idp) {
//			try {
//				((ApplicationManager) getService()).launchApplication(
//						(ApplicationDescriptor) pair[1], null);
//			}
//			catch (SingletonException e) {
//				// TODO
//				e.printStackTrace();
//			}
//			catch (Exception e) {
//				// TODO
//				e.printStackTrace();
//			}
//			events.remove(event.getTopic());
//		}
//	}

//	public void scheduleOnEvent(String topic, Hashtable ht,
//			ApplicationDescriptor descr) {
//		events.put(topic, new Object[] {ht, descr});
//	}

//	public void scheduleOnDate(Date date, ApplicationDescriptor descr) {
//		((ApplicationManager) getService()).addScheduledApplication(descr,
//				new Hashtable(), date);
//	}
    
}
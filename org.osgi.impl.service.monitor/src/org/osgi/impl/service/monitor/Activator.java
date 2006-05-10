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
package org.osgi.impl.service.monitor;

import java.util.Hashtable;
import org.osgi.framework.*;
import info.dmtree.notification.NotificationService;
import info.dmtree.spi.DataPlugin;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.monitor.MonitorAdmin;
import org.osgi.service.monitor.MonitorListener;
import org.osgi.service.monitor.Monitorable;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Activator class for the bundle containing the Monitor Admin and
 * Monitor Plugin.  Retrieves required services (EventAdmin and
 * NotificationSender), starts tracking of Monitorable services,
 * and registers provided services (MonitorAdmin, UpdateListener
 * and DataPlugin).
 */
public class Activator implements BundleActivator
{
    static final String[] PLUGIN_ROOT_PATH = new String[] { 
        ".", "OSGi", "Monitor"
    };
    static final String PLUGIN_ROOT = "./OSGi/Monitor"; // generate from array?

    ServiceTracker monitorableTracker;
    ServiceRegistration monitorAdminReg;
    ServiceRegistration monitorPluginReg;
    ServiceReference eventChannelRef;
    ServiceReference alertSenderRef;

    public void start(BundleContext bc) throws BundleException {
        System.out.println("Monitor Admin and Plugin activation...");

        eventChannelRef = bc.getServiceReference(EventAdmin.class.getName());
        if(eventChannelRef == null)
            throw new BundleException("Cannot find Event Channel service.");
        EventAdmin eventChannel = (EventAdmin) bc.getService(eventChannelRef);
        if(eventChannel == null)
            throw new BundleException("Event Channel service no longer registered.");

        alertSenderRef = bc.getServiceReference(NotificationService.class.getName());
        if(alertSenderRef == null)
            throw new BundleException("Cannot find Notification Service.");
        NotificationService alertSender = (NotificationService) 
                bc.getService(alertSenderRef);
        if(alertSender == null)
            throw new BundleException("Notification Service no longer registered.");

        // create a tracker for Monitorable services that notifies the plugin if a monitorable is unregistered
        monitorableTracker = 
            new ServiceTracker(bc, Monitorable.class.getName(), null) {
                public void removedService(ServiceReference reference, Object object) {
                    String pid = (String) reference.getProperty("service.pid");

                    // Ignore pid == null case, no data to remove in plugin
                    if(pid != null)
                        StatusVarWrapper.removeMonitorable(pid);

                    super.removedService(reference, object);
                }
            };
        monitorableTracker.open();

        MonitorAdminImpl monitorAdmin = new MonitorAdminImpl(bc, monitorableTracker, eventChannel, alertSender);
        String[] services = new String[] {
            MonitorAdmin.class.getName(),
            MonitorListener.class.getName()
        };
        monitorAdminReg = bc.registerService(services, monitorAdmin, null);

        MonitorPlugin monitorPlugin = new MonitorPlugin(bc, monitorAdmin);
        Hashtable properties = new Hashtable();
        properties.put("dataRootURIs", new String[] { PLUGIN_ROOT });
        monitorPluginReg = bc.registerService(DataPlugin.class.getName(),
                monitorPlugin, properties);

        System.out.println("Monitor Admin and Plugin activation successful.");
    }

    public void stop(BundleContext bc) throws BundleException {
        monitorableTracker.close();
        monitorPluginReg.unregister();
        monitorAdminReg.unregister();
        bc.ungetService(alertSenderRef);
        bc.ungetService(eventChannelRef);
    }
}

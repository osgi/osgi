package org.osgi.impl.service.monitor;

import java.util.Hashtable;
import org.osgi.framework.*;
import org.osgi.service.dmt.DmtDataPlugIn;
import org.osgi.service.event.EventChannel;
import org.osgi.service.monitor.*;
import org.osgi.util.tracker.ServiceTracker;

public class MonitorAdminActivator implements BundleActivator
{
    static final String PLUGIN_ROOT = "./OSGi/mon";

    ServiceTracker monitorableTracker;
    ServiceRegistration monitorAdminReg;
    ServiceRegistration monitorPluginReg;
    ServiceReference eventChannelRef;

    public void start(BundleContext bc) throws BundleException {
        System.out.println("Monitor Admin and Plugin activation started.");

        eventChannelRef = bc.getServiceReference(EventChannel.class.getName());
        if(eventChannelRef == null)
            throw new BundleException("Cannot find Event Channel service.");
        EventChannel eventChannel = (EventChannel) bc.getService(eventChannelRef);

        monitorableTracker = new ServiceTracker(bc, Monitorable.class.getName(), null);
        monitorableTracker.open();

        MonitorAdminImpl monitorAdmin = new MonitorAdminImpl(monitorableTracker, eventChannel);
        monitorAdminReg = bc.registerService(MonitorAdmin.class.getName(), monitorAdmin, null);


        MonitorPlugin monitorPlugin = new MonitorPlugin(bc, monitorableTracker, monitorAdmin);
        Hashtable properties = new Hashtable();
        properties.put("dataRootURIs", new String[] { PLUGIN_ROOT });
        monitorPluginReg = bc.registerService(DmtDataPlugIn.class.getName(), monitorPlugin, properties);

        // TODO remove this line (or change texts)
        System.out.println("Monitor Admin and Plugin activation finished successfully.");
    }

    public void stop(BundleContext bc) throws BundleException {
        monitorableTracker.close();
        monitorPluginReg.unregister();
        monitorAdminReg.unregister();
        bc.ungetService(eventChannelRef);
    }
}

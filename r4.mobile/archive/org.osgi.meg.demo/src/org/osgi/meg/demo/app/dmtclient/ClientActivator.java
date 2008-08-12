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
package org.osgi.meg.demo.app.dmtclient;

import java.util.Hashtable;
import org.osgi.framework.*;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.ManagedServiceFactory;
import info.dmtree.DmtAdmin;
import info.dmtree.spi.DataPlugin;

import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.monitor.MonitorListener;
import org.osgi.service.monitor.Monitorable;

public class ClientActivator implements BundleActivator 
{
    static final String SERVICE_PID = "org.osgi.meg.demo.app.dmtclient";
    
    static final String PLUGIN_ROOT = 
        System.getProperty("info.dmtree.osgi.root") + "/" + 
        ReadWriteDataPlugin.ROOT;

    private BundleContext bc;
    private ServiceReference factoryRef;
    private ServiceReference monListenerRef;
    private ServiceRegistration serviceReg;
    private ServiceRegistration pluginReg;

    public void start(BundleContext bc) throws BundleException
    {
        System.out.println("Simple client activated.");

        this.bc = bc;

        try {
            factoryRef = getServiceReference(DmtAdmin.class);
            DmtAdmin factory = (DmtAdmin) getService(factoryRef, DmtAdmin.class);

            monListenerRef = getServiceReference(MonitorListener.class);
            MonitorListener monitorListener = (MonitorListener) getService(monListenerRef, MonitorListener.class);

            if(factory == null || monitorListener == null)
                throw new Exception("Error retrieving services.");

            SimpleClient client = new SimpleClient(factory, monitorListener, bc);

            // register configuration client
            Hashtable config = new Hashtable();
            config.put("service.pid", SERVICE_PID);
            config.put("my.Integer", new Integer(42));

            config.put(EventConstants.EVENT_TOPIC, new String[] {
                    "org/osgi/service/monitor/MonitorEvent",
                    "info/dmtree/DmtEvent/*"
            });
            
            // ProtectedUri doesn't work with wildcard filters, Comparable IF
            // is not adequate for checking matches 
            //config.put(EventConstants.EVENT_FILTER, "(proba=./guarded*)");
            

            String[] services = new String[] {
                ManagedService.class.getName(),
                ManagedServiceFactory.class.getName(),
                Monitorable.class.getName(),
                EventHandler.class.getName()
            };

            serviceReg = bc.registerService(services, client, config);
            
            config = new Hashtable();
            config.put("dataRootURIs",  PLUGIN_ROOT);
            pluginReg = bc.registerService(DataPlugin.class.getName(), 
                    new ReadWriteDataPlugin(), config);

            client.run();

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            throw new BundleException("Failure in start() method.", e);
        } catch (Throwable t) {
            System.out.println("Throwable: " + t);
            t.printStackTrace();
        }
    }

    public void stop(BundleContext bc) throws BundleException 
    {
        pluginReg.unregister();
        serviceReg.unregister();
        bc.ungetService(monListenerRef);
        bc.ungetService(factoryRef);
    }

    private ServiceReference getServiceReference(Class serviceClass) throws Exception {
        String fullName = serviceClass.getName();
        String shortName = fullName.substring(fullName.lastIndexOf('.') + 1);
        
        ServiceReference ref = bc.getServiceReference(fullName);
        if(ref == null)
            throw new Exception("Cannot find " + shortName + " service.");
        return ref;
    }

    private Object getService(ServiceReference ref, Class serviceClass) throws Exception {
        String fullName = serviceClass.getName();
        String shortName = fullName.substring(fullName.lastIndexOf('.') + 1);

        Object service = bc.getService(ref);
        if(service == null)
            throw new Exception(shortName + " service no longer registered.");

        return service;
    }
}

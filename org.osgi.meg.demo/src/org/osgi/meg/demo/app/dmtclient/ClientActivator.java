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

import org.osgi.service.dmt.*;

import org.osgi.service.event.*;

import org.osgi.service.monitor.Monitorable;
import org.osgi.service.monitor.UpdateListener;

public class ClientActivator implements BundleActivator 
{
    static final String SERVICE_PID = "org.osgi.meg.demo.app.dmtclient";

    private BundleContext bc;
    private ServiceReference factoryRef;
    private ServiceReference updateRef;
    private ServiceRegistration serviceReg;

    public void start(BundleContext bc) throws BundleException
    {
        System.out.println("Simple client activated.");

        this.bc = bc;

        try {
            factoryRef = getServiceReference(DmtAdmin.class);
            DmtAdmin factory = (DmtAdmin) getService(factoryRef, DmtAdmin.class);

            updateRef = getServiceReference(UpdateListener.class);
            UpdateListener updateListener = (UpdateListener) getService(updateRef, UpdateListener.class);

            if(factory == null || updateListener == null)
                throw new Exception("Error retrieving services.");

            SimpleClient client = new SimpleClient(factory, updateListener, bc);

            // register configuration client
            Hashtable config = new Hashtable();
            config.put("service.pid", SERVICE_PID);
            config.put("my.Integer", new Integer(42));

            config.put(EventConstants.EVENT_TOPIC, new String[] {
                    "org/osgi/service/monitor/MonitorEvent",
                    "org/osgi/service/dmt/DmtEvent/*"
            });
            
            
            // TODO try this property instead of topic when event manager works properly
            /*
            config.put(EventConstants.EVENT_FILTER, 
                    "(|(topic=org/osgi/service/monitor/MonitorEvent)" +
                    "(topic=org/osgi/service/dmt/DmtEvent/*))");
            */

            String[] services = new String[] {
                ManagedService.class.getName(),
                Monitorable.class.getName(),
                EventHandler.class.getName()
            };

            serviceReg = bc.registerService(services, client, config);

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
        serviceReg.unregister();
        bc.ungetService(updateRef);
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

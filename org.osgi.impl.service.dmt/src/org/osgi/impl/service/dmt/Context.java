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

import java.util.Hashtable;
import java.util.Iterator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class Context {
    private BundleContext bc;
    private Hashtable trackers;
    
    private PluginDispatcher pluginDispatcher = null;
    private ServiceTracker pluginTracker = null;

    Context(BundleContext bc) {
        this.bc = bc;
        trackers = new Hashtable();
    }
    
    BundleContext getBundleContext() {
        return bc;
    }
    
    // cannot be used for tracking multiple classes or using a customizer
    ServiceTracker getTracker(Class trackedClass) {
        ServiceTracker tracker = (ServiceTracker) trackers.get(trackedClass);
        if(tracker == null) // create tracker if it does not exist yet
            tracker = openTracker(trackedClass, null);
        return tracker;
    }
    
    synchronized PluginDispatcher getPluginDispatcher() {
        if(pluginDispatcher == null) { // create plugin tracker if it DNE yet
            pluginDispatcher = new PluginDispatcher(this);
            String filter = "(|(objectClass=info.dmtree.spi.DataPlugin)" +
                              "(objectClass=info.dmtree.spi.ExecPlugin))";
            try {
                pluginTracker = new ServiceTracker(bc, bc.createFilter(filter),
                        pluginDispatcher);
            } catch (InvalidSyntaxException e) {
                // cannot happen
                System.err.println("Internal error, invalid filter string. ");
                e.printStackTrace();
            }
            pluginTracker.open();
        }
            
        return pluginDispatcher;
    }
    
    void close() {
        Iterator i = trackers.values().iterator();
        while (i.hasNext())
            ((ServiceTracker) i.next()).close();
        
        if(pluginTracker != null) // pluginTracker is special, not in trackers 
            pluginTracker.close();
    }

    // Find a better place for this method...
    void log(int severity, String message, Throwable throwable) {
        System.out.print("Log entry | Serverity: " + severity + 
                " Message: " + message + " Throwable: ");
        if(throwable == null)
            System.out.println("null");
        else
            throwable.printStackTrace(System.out);

        LogService logService = 
            (LogService) getTracker(LogService.class).getService();
        
        if (logService != null)
            logService.log(severity, message, throwable);
    }
    
    private ServiceTracker openTracker(Class trackedClass,
            ServiceTrackerCustomizer customizer) {
        
        ServiceTracker tracker = new ServiceTracker(bc, trackedClass.getName(),
                customizer);
        tracker.open();
        trackers.put(trackedClass, tracker);
        
        return tracker;
    }
}

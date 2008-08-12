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

import java.util.Iterator;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.dmt.export.DmtPrincipalPermissionAdmin;

/*
 * Service factory class for the Dmt Admin service. Stores each Dmt Admin
 * delegate that is in use, and forwards DMT events to each one, for delivery to
 * the registered event listeners.
 */
public class DmtAdminFactory implements ServiceFactory {
    private DmtAdminCore dmtAdmin;
    private Vector delegates;
    private Context context;
    private LocalEventProxy localEventProxy;

    DmtAdminFactory(Context context, DmtAdminCore dmtAdmin, 
            DmtPrincipalPermissionAdmin dmtPermissionAdmin) {
        this.context = context;
        
        this.dmtAdmin = dmtAdmin;
        localEventProxy = new LocalEventProxy();
        
        new Thread(localEventProxy).start();
        
        delegates = new Vector();
    }

    public Object getService(Bundle bundle, ServiceRegistration registration) {
        DmtAdminDelegate delegate = new DmtAdminDelegate(dmtAdmin, context);
        delegates.add(delegate);
        return delegate;
    }

    public void ungetService(Bundle bundle, ServiceRegistration registration,
            Object service) {
        DmtAdminDelegate delegate = (DmtAdminDelegate) service;
        delegates.remove(delegate);
        delegate.close();
    }
    
    void stop() {
        localEventProxy.stop();
    }
    
    class LocalEventProxy implements Runnable {
        private boolean running = false;
        
        public void run() {
            running = true;
            
            while(running) {
                DmtEventCore event = EventStore.getNextLocalEvent(10000);
                if(event != null) {
                    Iterator i = delegates.iterator();
                    while (i.hasNext()) {
                        DmtAdminDelegate delegate = (DmtAdminDelegate) i.next();
                        delegate.dispatchEvent(event);
                    }
                }
            }
        }
        
        void stop() {
            running = false;
        }
    }
}

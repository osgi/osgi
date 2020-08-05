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

import java.util.List;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.dmt.export.DmtPrincipalPermissionAdmin;
import org.osgi.service.dmt.DmtAdmin;

/*
 * Service factory class for the Dmt Admin service. Stores each Dmt Admin
 * delegate that is in use, and forwards DMT events to each one, for delivery to
 * the registered event listeners.
 */
public class DmtAdminFactory implements ServiceFactory<DmtAdmin> {
	DmtAdminCore					dmtAdmin;
	private List<DmtAdminDelegate>	delegates;
    private Context context;
    private LocalEventProxy localEventProxy;

    DmtAdminFactory(Context context, DmtAdminCore dmtAdmin, 
            DmtPrincipalPermissionAdmin dmtPermissionAdmin) {
        this.context = context;
        
        this.dmtAdmin = dmtAdmin;
        localEventProxy = new LocalEventProxy();
        
        new Thread(localEventProxy).start();
        
		delegates = new Vector<>();
    }

    @Override
	public DmtAdmin getService(Bundle bundle,
			ServiceRegistration<DmtAdmin> registration) {
        DmtAdminDelegate delegate = new DmtAdminDelegate(dmtAdmin, context, bundle);
        delegates.add(delegate);
        return delegate;
    }

    @Override
	public void ungetService(Bundle bundle,
			ServiceRegistration<DmtAdmin> registration, DmtAdmin service) {
        DmtAdminDelegate delegate = (DmtAdminDelegate) service;
        delegates.remove(delegate);
        delegate.close();
    }
    
    void stop() {
        localEventProxy.stop();
    }
    
    class LocalEventProxy implements Runnable {
        private boolean running = false;
        
        @Override
		public void run() {
            running = true;
            
            while(running) {
                DmtEventCore event = EventDispatcher.getNextLocalEvent(10000);
                if(event != null)
                	// events are now dispatched by the dmtAdmin centrally
                	dmtAdmin.dispatchEvent(event);
            }
        }
        
        void stop() {
            running = false;
        }
    }
}

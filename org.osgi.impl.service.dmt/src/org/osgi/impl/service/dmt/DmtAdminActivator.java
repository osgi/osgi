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

import org.osgi.framework.*;
import org.osgi.impl.service.dmt.api.RemoteAlertSender;
import org.osgi.impl.service.dmt.api.DmtPrincipalPermissionAdmin;
import org.osgi.service.dmt.*;
import org.osgi.service.event.EventChannel;
import org.osgi.util.tracker.ServiceTracker;

// TODO cleanup service registrations properly in case of error
// TODO check that the service is not registered already?
// TODO when stop() is called, notify the impl. to release all refs to other services
// (these should be done in all activators!)
public class DmtAdminActivator implements BundleActivator {
	private ServiceRegistration	sessionReg;
	private ServiceRegistration	alertReg;
    private ServiceRegistration permissionReg;
    private ServiceReference    eventChannelRef;
    private ServiceTracker		remoteAdapterTracker;
	private ServiceTracker		pluginTracker;

	public void start(BundleContext bc) throws BundleException {
		try {
            eventChannelRef = 
                bc.getServiceReference(EventChannel.class.getName());
            if(eventChannelRef == null)
                throw new BundleException("Cannot find Event Channel service.");
            EventChannel eventChannel = 
                (EventChannel) bc.getService(eventChannelRef);
            if(eventChannel == null)
                throw new BundleException("Event Channel service no longer registered.");
            
			DmtPluginDispatcher dispatcher = new DmtPluginDispatcher(bc);
			//tracker = new ServiceTracker(bc, DmtDataPlugin.class.getName(), dispatcher);
			String filter = "(|(objectClass=org.osgi.service.dmt.DmtDataPlugin)"
					+ "(objectClass=org.osgi.service.dmt.DmtExecPlugin))";
			pluginTracker = new ServiceTracker(bc, bc.createFilter(filter),
					dispatcher);
			pluginTracker.open();
			// creating the services
			DmtAdminImpl dmtAdmin = new DmtAdminImpl(dispatcher, eventChannel);
			DmtAlertSenderImpl dmtAlertSender = new DmtAlertSenderImpl(bc);
			remoteAdapterTracker = new ServiceTracker(bc,
					RemoteAlertSender.class.getName(), dmtAlertSender);
			remoteAdapterTracker.open();
			// registering the services
			sessionReg = bc.registerService(DmtAdmin.class.getName(),
					dmtAdmin, null);
			alertReg = bc.registerService(DmtAlertSender.class.getName(),
					dmtAlertSender, null);
            permissionReg = 
                bc.registerService(DmtPrincipalPermissionAdmin.class.getName(),
                    new DmtPrincipalPermissionAdminImpl(), null);
		} catch (Exception e) {
			System.out.println("Exception:" + e.getMessage());
			throw new BundleException("Failure in start() method.", e);
		}
	}

	public void stop(BundleContext bc) throws BundleException {
		// stopping service trackers
		pluginTracker.close();
		remoteAdapterTracker.close();
		// unregistering the service
		sessionReg.unregister();
		alertReg.unregister();
        permissionReg.unregister();
	}
}

package org.osgi.impl.service.dmt;

import org.osgi.framework.*;
import org.osgi.impl.service.dmt.api.RemoteAlertSender;
import org.osgi.service.dmt.*;
import org.osgi.util.tracker.ServiceTracker;

// TODO cleanup service registrations properly in case of error
// TODO check that the service is not registered already?
// TODO when stop() is called, notify the impl. to release all refs to other services
// (these should be done in all activators!)
public class DmtAdminActivator implements BundleActivator {
	private ServiceRegistration	sessionReg;
	private ServiceRegistration	alertReg;
	private ServiceTracker		remoteAdapterTracker;
	private ServiceTracker		pluginTracker;

	public void start(BundleContext bc) throws BundleException {
		try {
			DmtPlugInDispatcher dispatcher = new DmtPlugInDispatcher(bc);
			//tracker = new ServiceTracker(bc, DmtDataPlugIn.class.getName(),
			// dispatcher);
			String filter = "(|(objectClass=org.osgi.service.dmt.DmtDataPlugIn)"
					+ "(objectClass=org.osgi.service.dmt.DmtExecPlugIn))";
			pluginTracker = new ServiceTracker(bc, bc.createFilter(filter),
					dispatcher);
			pluginTracker.open();
			// creating the services
			DmtFactoryImpl dmtFactory = new DmtFactoryImpl(dispatcher);
			DmtAlertSenderImpl dmtAlertSender = new DmtAlertSenderImpl(bc);
			remoteAdapterTracker = new ServiceTracker(bc,
					RemoteAlertSender.class.getName(), dmtAlertSender);
			remoteAdapterTracker.open();
			// registering the services
			sessionReg = bc.registerService(DmtFactory.class.getName(),
					dmtFactory, null);
			alertReg = bc.registerService(DmtAlertSender.class.getName(),
					dmtAlertSender, null);
		}
		catch (Exception e) {
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
	}
}

package org.osgi.impl.service.dmt;

import java.util.Hashtable;
import org.osgi.framework.*;
import org.osgi.service.dmt.*;
import org.osgi.service.log.*;

public class LogPluginActivator implements BundleActivator {
	private ServiceRegistration	servReg;
	private ServiceReference	logRef;
	private ServiceReference	logReaderRef;
	private ServiceReference	alertRef;
	private LogPlugin			logPlugin;
	static final String			PLUGIN_ROOT	= "./OSGi/log";

	public void start(BundleContext bc) throws BundleException {
		System.out.println("Log plugin activated.");
		//looking up the services needed
		logRef = bc.getServiceReference(LogService.class.getName());
		if (logRef == null)
			throw new BundleException("Cannot find Log Service.");
		LogService ls = (LogService) bc.getService(logRef);
		if (ls == null)
			throw new BundleException("Log Service no longer registered.");
		logReaderRef = bc.getServiceReference(LogReaderService.class.getName());
		if (logReaderRef == null)
			throw new BundleException("Cannot find Log Reader Service.");
		LogReaderService lrs = (LogReaderService) bc.getService(logReaderRef);
		if (lrs == null)
			throw new BundleException("Log Service no longer registered.");
		alertRef = bc.getServiceReference(DmtAlertSender.class.getName());
		if (alertRef == null)
			throw new BundleException("Cannot find Alert Service.");
		DmtAlertSender das = (DmtAlertSender) bc.getService(alertRef);
		if (das == null)
			throw new BundleException("Alert Service no longer registered.");
		//creating the service
		//TODO make the plugin and activator the same so that we don't need
		// to pass all these
		logPlugin = new LogPlugin(bc, ls, lrs, das);
		Hashtable props = new Hashtable();
		props.put("dataRootURIs", new String[] {PLUGIN_ROOT});
		props.put("execRootURIs", new String[] {PLUGIN_ROOT});
		String[] ifs = new String[] {DmtDataPlugIn.class.getName(),
				DmtExecPlugIn.class.getName()};
		servReg = bc.registerService(ifs, logPlugin, props);
	}

	public void stop(BundleContext bc) throws BundleException {
		//unregistering the service
		servReg.unregister();
		//releasing the used services
		bc.ungetService(logRef);
		bc.ungetService(logReaderRef);
		bc.ungetService(alertRef);
	}
}

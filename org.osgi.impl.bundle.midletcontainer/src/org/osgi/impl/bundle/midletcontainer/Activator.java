package org.osgi.impl.bundle.midletcontainer;

import org.osgi.framework.*;
import org.osgi.service.log.LogService;

public class Activator implements BundleActivator {

	private static BundleContext		  bc;
	private MidletContainer		        midletContainer;
	private OAT                       oat;
	
	public Activator() {}

	public void start(BundleContext bc) throws Exception {
		Activator.bc = bc;
		
		oat = new OAT();
		oat.start( bc );
		
		midletContainer = new MidletContainer(bc);
		
		System.out.println("Midlet container started successfully!");
	}

	public void stop(BundleContext bc) throws Exception {
		midletContainer.stop();
		Activator.bc = null;
		
		oat.stop( bc );
		oat = null;
		
		System.out.println("Midlet container stopped successfully!");
	}
	
	static boolean log( int severity, String message,	Throwable throwable) {
		System.out.println("Serverity:" + severity + " Message:" + message
				+ " Throwable:" + throwable);

		ServiceReference serviceRef = bc
				.getServiceReference("org.osgi.service.log.LogService");
		if (serviceRef != null) {
			LogService logService = (LogService) bc.getService(serviceRef);
			if (logService != null) {
				try {
					logService.log(severity, message, throwable);
					return true;
				}
				finally {
					bc.ungetService(serviceRef);
				}
			}
		}
		return false;
	}
}

package org.osgi.impl.service.midletcontainer;

import java.util.*;
import org.osgi.framework.*;

public class Activator implements BundleActivator {

	public Activator() {}

	public void start(BundleContext bc) throws Exception {
		this.bc = bc;
		midletContainer = new MidletContainer(bc);
		Hashtable serviceListenerProps = new Hashtable();
		serviceListenerProps.put("topic", new String[] {"*"});
		serviceListener = bc.registerService(
				"org.osgi.service.event.EventHandler", midletContainer,
				serviceListenerProps);
		System.out.println("Midlet container started successfully!");
	}

	public void stop(BundleContext bc) throws Exception {
		midletContainer.unregisterAllApplications();
		serviceListener.unregister();
		this.bc = null;
		System.out.println("Midlet container stopped successfully!");
	}

	private BundleContext		bc;
	private ServiceRegistration	serviceListener;
	private MidletContainer		midletContainer;
}

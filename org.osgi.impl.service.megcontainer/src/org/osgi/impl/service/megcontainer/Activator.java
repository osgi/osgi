package org.osgi.impl.service.megcontainer;

import java.util.*;
import org.osgi.framework.*;

public class Activator extends Object implements BundleActivator {
	private BundleContext		bc;
	private ServiceRegistration	serviceReg;
	private ServiceRegistration	serviceListener;
	private MEGContainerImpl	megContainerImpl;

	public Activator() {
		super();
	}

	public void start(BundleContext bc) throws Exception {
		this.bc = bc;
		megContainerImpl = new MEGContainerImpl(bc, "MEG");
		Dictionary properties = new Hashtable();
		properties.put("application_type", "MEG");
		properties.put("bundle_id", new Long(bc.getBundle().getBundleId())
				.toString());
		//registering the services
		serviceReg = bc.registerService(
				"org.osgi.service.application.ApplicationContainer",
				megContainerImpl, properties);
		Hashtable serviceListenerProps = new Hashtable();
		serviceListenerProps.put("topic", "*");
		serviceListener = bc.registerService(
				"org.osgi.service.event.ChannelListener", megContainerImpl,
				serviceListenerProps);
		System.out.println("MEG container started successfully!");
	}

	public void stop(BundleContext bc) throws Exception {
		//unregistering the service
		megContainerImpl.unregisterAllApplications();
		serviceListener.unregister();
		serviceReg.unregister();
		this.bc = null;
		System.out.println("MEG container stopped successfully!");
	}
}

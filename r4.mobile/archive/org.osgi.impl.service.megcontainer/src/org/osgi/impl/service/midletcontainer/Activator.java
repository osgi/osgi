package org.osgi.impl.service.midletcontainer;

import java.util.*;
import org.osgi.framework.*;

public class Activator implements BundleActivator {

	private BundleContext		  bc;
	private MidletContainer		midletContainer;
	
	public Activator() {}

	public void start(BundleContext bc) throws Exception {
		this.bc = bc;
		midletContainer = new MidletContainer(bc);
		Hashtable serviceListenerProps = new Hashtable();
		System.out.println("Midlet container started successfully!");
	}

	public void stop(BundleContext bc) throws Exception {
		midletContainer.stop();
		this.bc = null;
		System.out.println("Midlet container stopped successfully!");
	}
}

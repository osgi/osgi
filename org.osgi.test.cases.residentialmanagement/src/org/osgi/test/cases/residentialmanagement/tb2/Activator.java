package org.osgi.test.cases.residentialmanagement.tb2;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public void start(BundleContext arg0) throws Exception {
		System.out.println("TestBundle2 has been started.");

	}

	public void stop(BundleContext arg0) throws Exception {
		System.out.println("TestBundle2 is stopped.");

	}

}

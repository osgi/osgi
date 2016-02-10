package org.osgi.test.cases.framework.launch.dependency.tb2;

import java.util.Collections;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.test.cases.framework.launch.dependency.tb1.TB1;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		TB1 tb1 = new TB1();
		System.out.println(
				context.getBundle().toString() + " - Constructed TB1: " + tb1);
		context.registerService(TB1.class, tb1,
				new Hashtable<>(Collections.singletonMap("test", "tb2")));
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// nothing
	}

}

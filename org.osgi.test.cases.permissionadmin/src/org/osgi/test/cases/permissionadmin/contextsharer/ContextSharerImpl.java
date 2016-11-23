package org.osgi.test.cases.permissionadmin.contextsharer;

import java.lang.reflect.Method;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.permissionadmin.service.ContextSharer;

public class ContextSharerImpl implements ContextSharer, BundleActivator {
	BundleContext		bc;
	ServiceRegistration<ContextSharer>	serviceRegistration;

	public void start(BundleContext bc) {
		this.bc = bc;
		String location = bc.getBundle().getLocation();
		Hashtable<String,Object> props = new Hashtable<>();
		props.put("Bundle-Location", location);
		serviceRegistration = bc.registerService(ContextSharer.class,
				this, props);
		System.out.println("Hello world from the mighty CONTEXTSHARER!");
		// System.out.println("location: " + bc.getBundle().getLocation());
	}

	public void stop(BundleContext bc) {
		serviceRegistration.unregister();
		serviceRegistration = null;
		this.bc = null;
	}

	public BundleContext getContext() {
		return bc;
	}

	public Object createObject(String clazz) throws Exception {
		return Class.forName(clazz).newInstance();
	}

	public void invoke(Object o, Method m) throws Throwable {
		System.out.println("CONTEXTSHARER will call method!");
		m.invoke(o, new Object[0]);
		System.out.println("CONTEXTSHARER has called method!");
	}
}

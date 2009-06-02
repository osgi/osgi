package org.osgi.test.cases.permissionadmin.main.contextsharer;

import java.lang.reflect.*;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.test.cases.permissionadmin.main.tbc.ContextSharer;

public class ContextSharerImpl implements ContextSharer, BundleActivator {
	BundleContext		bc;
	ServiceRegistration	serviceRegistration;
	Vector				objectsInUse	= new Vector();

	public void start(BundleContext bc) {
		this.bc = bc;
		String location = bc.getBundle().getLocation();
		Hashtable props = new Hashtable();
		props.put("Bundle-Location", location);
		serviceRegistration = bc.registerService(ContextSharer.class.getName(),
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

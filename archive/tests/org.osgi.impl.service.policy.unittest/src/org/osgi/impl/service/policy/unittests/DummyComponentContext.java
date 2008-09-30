/*
 * ============================================================================
 * (c) Copyright 2005 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.policy.unittests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentInstance;

public class DummyComponentContext implements ComponentContext {
	public Hashtable properties = new Hashtable();
	public HashMap services = new HashMap();
	
	/**
	 * Calls a protected method void activate(ComponentContext) on a given object.
	 * @param o
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public void doActivate(Object component) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method activate = component.getClass().getDeclaredMethod("activate",new Class[] {ComponentContext.class});
		activate.setAccessible(true);
		activate.invoke(component,new Object[] {this});
	}

	public Dictionary getProperties() {
		return properties;
	}
	public Object locateService(String name) { 
		return services.get(name);
	}

	public Object[] locateServices(String name) { throw new IllegalStateException(); }
	public BundleContext getBundleContext() { throw new IllegalStateException(); }
	public Bundle getUsingBundle() { throw new IllegalStateException(); }
	public ComponentInstance getComponentInstance() { throw new IllegalStateException(); }
	public void enableComponent(String name) { throw new IllegalStateException(); }
	public void disableComponent(String name) { throw new IllegalStateException(); }
	public ServiceReference getServiceReference() { throw new IllegalStateException(); }
	public Object locateService(String name, ServiceReference reference) { throw new IllegalStateException(); }
}

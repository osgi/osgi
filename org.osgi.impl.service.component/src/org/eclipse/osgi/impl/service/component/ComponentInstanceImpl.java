/*
 * Copyright (c) IBM Corporation (2005)
 *
 * These materials have been contributed  to the OSGi Alliance as 
 * "MEMBER LICENSED MATERIALS" as defined in, and subject to the terms of, 
 * the OSGi Member Agreement, specifically including but not limited to, 
 * the license rights and warranty disclaimers as set forth in Sections 3.2 
 * and 12.1 thereof, and the applicable Statement of Work. 
 *
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.
 */

package org.eclipse.osgi.impl.service.component;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.eclipse.osgi.component.instance.BuildDispose;
import org.eclipse.osgi.component.model.ComponentDescriptionProp;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentInstance;

/**
 * A ComponentInstance encapsulates an instance of a component.
 * ComponentInstances are created whenever an instance of a component is
 * created.
 * 
 * @version $Revision$
 */
public class ComponentInstanceImpl implements ComponentInstance {

	Object instance;
	Dictionary properties = null;
	BuildDispose buildDispose;
	public ComponentDescriptionProp component;
	ServiceRegistration serviceRegistration = null;

	//ServiceReference:ServiceObject that binded to this reference
	private Hashtable serviceReferenceToServiceObject = new Hashtable();

	/** ComponentInstanceImpl
	 * 
	 * @param Object instance
	 * 
	 */
	public ComponentInstanceImpl(BuildDispose buildDispose, ComponentDescriptionProp component, Object instance, Dictionary properties) {
		this.buildDispose = buildDispose;
		this.instance = instance;
		this.properties = properties;
		this.component = component;

	}

	/**
	 * Dispose of this component instance. The instance will be deactivated. If
	 * the instance has already been deactivated, this method does nothing.
	 */
	public void dispose() {
		//deactivate, unregister if a service
		if(serviceRegistration != null)
			serviceRegistration.unregister();
		buildDispose.disposeComponentInstance(component, this);
		component.removeInstance(this);
		instance = null;
		properties = null;
	}

	/**
	 * Returns the component instance. The instance has been activated.
	 * 
	 * @return The component instance or <code>null</code> if the instance has
	 *         been deactivated.
	 */
	public Object getInstance() {
		return instance;
	}

	/*
	 * returns the instance properties
	 */
	protected Dictionary getProperties() {
		return properties;
	}
	
	public void addServiceReference(ServiceReference serviceReference, Object serviceObject)
	{
		serviceReferenceToServiceObject.put(serviceReference,serviceObject);
	}
	
	public Enumeration getServiceReferences() {
		return serviceReferenceToServiceObject.keys();
	}
	
	public void removeServiceReference(ServiceReference serviceReference)
	{
		serviceReferenceToServiceObject.remove(serviceReference);
	}
	
	public Object getServiceObject(ServiceReference serviceReference)
	{
		return serviceReferenceToServiceObject.get(serviceReference);
	}
		
	public void setServiceRegistration(ServiceRegistration serviceRegistration){
		this.serviceRegistration =  serviceRegistration;
	}
	
}

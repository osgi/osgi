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

package org.eclipse.osgi.component.instance;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.osgi.component.Main;
import org.eclipse.osgi.component.model.ComponentDescription;
import org.eclipse.osgi.component.model.ComponentDescriptionProp;
import org.eclipse.osgi.component.model.ReferenceDescription;
import org.eclipse.osgi.component.resolver.Reference;
import org.eclipse.osgi.impl.service.component.ComponentContextImpl;
import org.eclipse.osgi.impl.service.component.ComponentInstanceImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentInstance;

/**
 * This class provides the following function
 * 
 * Build of a component instance - includes activate, bind, instantiate.
 * Dispose of a component instance - includes deactivate, unbind, unreference.
 * 
 */
public class BuildDispose {

	/* set this to true to compile in debug messages */
	static final boolean DEBUG = false;

	protected InvokeMethod invoke;

	protected BundleContext bundleContext;

	/** Map CD+P:instances (1:n) */
	protected Hashtable instanceMap;

	/** next free component id. */ 
	protected long componentid;

	/** Main SCR class */
	protected Main main;

	/**
	 * BuildDispose - Build or Dispose of the Instance
	 * 
	 * @param main Main SCR class
	 * @param registrations - map of ComponentDescriptionProps to service registrations
	 */

	public BuildDispose(Main main, Hashtable registrations) {
		instanceMap = new Hashtable();
		invoke = new InvokeMethod();
		this.main = main;
	}

	/**
	 * dispose cleanup the SCR is shutting down
	 */
	public void dispose() {
		instanceMap = null;
		invoke = null;
		main = null;
	}

	/**
	 * build the ComponentInstance
	 * 
	 * @param bundleContext - bundle context of the bundle containg the Service Component
	 * @param component - Component Description with Properties Object
	 * @param instance - instance object
	 * @param properties - properties for this instance
	 * 
	 * @return ComponentInstance
	 */
	public ComponentInstance build(BundleContext bundleContext, Bundle usingBundle, ComponentDescriptionProp component, Object instance, Dictionary properties) throws Exception {

		List instances = new ArrayList();
		ComponentDescription componentDescription = component.getComponentDescription();
		this.bundleContext = bundleContext;

		if (instance == null)
			instance = main.resolver.instanceProcess.getInstance(componentDescription);

		ComponentInstanceImpl componentInstance = instantiate(instance, properties);

		bind(component, componentInstance);

		activate(component, usingBundle, componentInstance);

		instances.add(componentInstance);
		instanceMap.put(component, instances);

		return componentInstance;
	}

	/** dispose of the Component Instance
	 * 
	 * @param component Component Description plus properties
	 */
	public void disposeComponent(ComponentDescriptionProp component) {

		ComponentInstanceImpl componentInstance;
		// get all instances for this component
		List instances = (List)instanceMap.get(component);
		if (instances != null) {
			Iterator it = instances.iterator();
			while (it.hasNext()) {
				componentInstance = (ComponentInstanceImpl) it.next();
				deactivate(component, componentInstance.getInstance());
				unbind(component, componentInstance);
				componentInstance = null;
			}
		}
		instanceMap.remove(component);
	}

	/**
	 * Create the new Instance
	 * 
	 * @param componentDescription
	 * @return Object instance
	 */
	public Object createInstance(ComponentDescription componentDescription) throws Exception {
		Object instance = null;
		String classname = componentDescription.getImplementation().getClassname();
		try {
			instance = ((componentDescription.getBundle()).loadClass(classname)).newInstance();
		} catch (Exception e) {
			main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, componentDescription.getBundle(), e);
			throw e; //rethrow so we can eventually tell the resolver that this CD is bad
		}
		return instance;
	}

	/**
	 * instantiate - create the ComponentInstance
	 *
	 * @param instance 
	 * @param properties
	 */
	public ComponentInstanceImpl instantiate(Object instance, Dictionary properties) {
		return new ComponentInstanceImpl(instance, properties);
	}

	/**
	 * Call the bind method for each of the Referenced Services in this Service Component
	 *
	 * @param componentDescription
	 * @param instance 
	 */
	public void bind(ComponentDescriptionProp componentDescriptionProp, ComponentInstanceImpl componentInstance) throws Exception {
		//Get all the required service Reference Descriptions for this Service Component
		List references = componentDescriptionProp.getReferences();
		//call the Bind method if the Reference Description includes one
		Iterator itr = references.iterator();
		while(itr.hasNext()) {
			Reference reference = (Reference)itr.next();
			if (reference.getReferenceDescription().getBind() != null) {
					bindReference(reference,componentInstance);
			}
		}
	}

	/** 
	 * Call the bind method for this referenceDescription
	 * 
	 * @param referenceDescription
	 * @param instance 
	 * 
	 */
	public void bindReference(Reference reference, ComponentInstanceImpl componentInstance) throws Exception {
		ServiceReference[] serviceReferences = null;
		//if there is a published service, then get the ServiceObject and call bind
		try {
			//get All Registered services using this target filter
			serviceReferences = bundleContext.getServiceReferences(reference.getReferenceDescription().getInterfacename(), reference.getReferenceDescription().getTarget());
		} catch (Exception e) {
			main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, reference.getReferenceDescription().getComponentDescription().getBundle(), e);
			throw e;//rethrow exception so resolver is eventually notified that this CDP is bad
		}

		if (serviceReferences != null) {
			//we only want to bind one service
			String cardinality = reference.getReferenceDescription().getCardinality();
			if ((cardinality.equals("1..1")) || (cardinality.equals("0..1"))) {
				bindServiceToReference(reference, serviceReferences[0], componentInstance);
				//here we can bind more than one service, if availible
			} else if ((cardinality.equals("1..n")) || (cardinality.equals("0..n"))) {
				for (int j = 0; j < serviceReferences.length; j++) {
					bindServiceToReference(reference, serviceReferences[j], componentInstance);
				}
			}
		} else {
			bindServiceToReference(reference, null, componentInstance);
		}

	}

	//helper method for bindReference
	private void bindServiceToReference(Reference reference, ServiceReference serviceReference, ComponentInstanceImpl componentInstance) throws Exception {
		//	make sure we have not already binded this object
		if (!reference.bindedToServiceReference(serviceReference)) {
			Object serviceObject = null;
			if (serviceReference != null) {
				serviceObject = bundleContext.getService(serviceReference);
			} else if (reference.getReferenceDescription().getComponentDescription().isEligible()) {
				serviceObject = main.resolver.instanceProcess.getInstanceWithInterface(reference.getReferenceDescription().getInterfacename());
			}
			if (serviceObject != null) 
			{
				if (reference.getReferenceDescription().getBind() != null) {
					try {
						invoke.bindComponent(reference.getReferenceDescription().getBind(), componentInstance.getInstance(), serviceObject);
						//if this suceeds, save the servicereference and service object so we can call unbind later
						reference.addServiceReference(serviceReference);
						componentInstance.addServiceReference(serviceReference,serviceObject);
					} catch (Exception e) {
						main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, reference.getReferenceDescription().getComponentDescription().getBundle(), e);
						throw e;//rethrow exception so resolver is eventually notified that this CDP is bad
					}
				}
			}
		}
	}

	/**
	 * Call the unbind method for each of the Referenced Services in this Serivce Component
	 *
	 * @param componentDescription
	 * @param instance 
	 */
	public void unbind(ComponentDescriptionProp component, ComponentInstanceImpl componentInstance) {
		//Get all the required service reference descriptions for this Service Component
		List references = component.getReferences();
		//call the unBind method if the Reference Description includes one
		Iterator itr = references.iterator();
		while(itr.hasNext()) {
			Reference reference = (Reference)itr.next();
			if (reference.getReferenceDescription().getUnbind() != null) {
				unbindReference(reference, componentInstance);
			}
		}
	}

	/** 
	 * Call the unbind method for this Reference Description
	 * 
	 * @param referenceDescription
	 * @param instance 
	 */
	public void unbindReference(Reference reference, ComponentInstanceImpl componentInstance) {
		List serviceReferences = reference.getServiceReferences();
		Iterator itr = serviceReferences.iterator();
		while(itr.hasNext()) {
			ServiceReference serviceReference = (ServiceReference)itr.next();
			unbindServiceFromReference(reference, componentInstance, serviceReference);
			componentInstance.removeServiceReference(serviceReference);
			//TODO when do we remove servicereferences from reference?
		}
	}

	/** 
	 * Call the unbind method for this Reference Description
	 * 
	 * @param referenceDescription
	 * @param instance 
	 * @param seviceObject
	 */
	public void unbindDynamicReference(ComponentDescriptionProp component, Reference reference, ComponentInstanceImpl componentInstance, ServiceReference serviceReference) throws Exception {

		unbindServiceFromReference(reference, componentInstance, serviceReference);
		
		componentInstance.removeServiceReference(serviceReference);
		
		//TODO - when do we remove the servicereference from the reference?

		//check if we need to rebind
		if ((reference.getReferenceDescription().getCardinality().equals("1..1")) || (reference.getReferenceDescription().getCardinality().equals("0..n")) || (reference.getReferenceDescription().getCardinality().equals("1..n"))) {
			bind(component, componentInstance);
		}
	}

	//unbindReference helper method
	private void unbindServiceFromReference(Reference reference, ComponentInstanceImpl componentInstance, ServiceReference serviceReference) {
		if (reference.getReferenceDescription().getUnbind() != null)
			try {
				invoke.unbindComponent(reference.getReferenceDescription().getUnbind(), componentInstance.getInstance(), componentInstance.getServiceObject(serviceReference));
			} catch (Exception e) {
				main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, reference.getReferenceDescription().getComponentDescription().getBundle(), e);
			}
	}

	/**
	 * Invoke the Serivce Component activate method
	 *
	 * @param component - ComponentDescriptionProperty object
	 * @param usingBundle - 
	 * @param componentInstance - 
	 */
	public void activate(ComponentDescriptionProp component, Bundle usingBundle, ComponentInstance componentInstance) throws Exception {

		ComponentContext componentContext = createComponentContext(component, usingBundle, componentInstance);
		//call the activate method on the Service Component
		try {
			invoke.activateComponent(componentInstance.getInstance(), componentContext);
		} catch (Exception e) {
			main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, component.getComponentDescription().getBundle(), e);
			throw e;//rethrow exception so resolver is eventually notified that this CDP is bad
		}
	}

	/**
	 * Invoke the Service Component deactivate method
	 *
	 * @param component - ComponentDescriptionProperty object
	 * @param instance 
	 */
	public void deactivate(ComponentDescriptionProp component, Object instance) {

		try {
			invoke.deactivateComponent(instance, getComponentContext(component));
		} catch (Exception e) {
			main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, component.getComponentDescription().getBundle(), e);
		}
	}

	/**
	 * Create and return the ComponentContext 
	 * 
	 * @param component
	 * @param usingBundle
	 * @param componentInstance
	 * @return
	 */
	private ComponentContext createComponentContext(ComponentDescriptionProp component, Bundle usingBundle, ComponentInstance componentInstance) {
		ComponentContext context = new ComponentContextImpl(main, bundleContext, usingBundle, component, componentInstance);
		component.setComponentContext(context);
		return context;
	}

	private ComponentContext getComponentContext(ComponentDescriptionProp component) {
		return component.getComponentContext();
	}

	/**
	 * Method to return the next available component id. 
	 * 
	 * @return next component id.
	 */
	protected long getNextComponentId() {
		long id = componentid;
		componentid++;
		return (id);
	}

}

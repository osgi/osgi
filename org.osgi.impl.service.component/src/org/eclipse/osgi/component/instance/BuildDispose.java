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

import java.util.*;
import org.eclipse.osgi.component.Main;
import org.eclipse.osgi.component.model.*;
import org.eclipse.osgi.impl.service.component.ComponentContextImpl;
import org.eclipse.osgi.impl.service.component.ComponentInstanceImpl;
import org.osgi.framework.*;
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

	/** Map CD+P:componentContext(1:1) */
	protected Hashtable contextMap;

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
		contextMap = new Hashtable();
		invoke = new InvokeMethod();
		this.main = main;
	}

	/**
	 * dispose cleanup the SCR is shutting down
	 */
	public void dispose() {
		instanceMap = null;
		contextMap = null;
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

		ArrayList instances = new ArrayList();
		ComponentDescription componentDescription = component.getComponentDescription();
		this.bundleContext = bundleContext;

		if (instance == null)
			instance = main.resolver.instanceProcess.getInstance(componentDescription);

		ComponentInstance componentInstance = instantiate(instance, properties);

		bind(componentDescription, instance);

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

		ComponentDescription componentDescription = component.getComponentDescription();
		ComponentInstance componentInstance;
		Object instance;
		// get all instances for this component
		ArrayList instances = (ArrayList) instanceMap.get(component);
		if (instances != null) {
			Iterator it = instances.iterator();
			while (it.hasNext()) {
				componentInstance = (ComponentInstance) it.next();
				instance = componentInstance.getInstance();
				deactivate(component, instance);
				unbind(componentDescription, instance);
				instance = null;
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
	public ComponentInstance instantiate(Object instance, Dictionary properties) {
		return new ComponentInstanceImpl(instance, properties);
	}

	/**
	 * Call the bind method for each of the Referenced Services in this Service Component
	 *
	 * @param componentDescription
	 * @param instance 
	 */
	public void bind(ComponentDescription componentDescription, Object instance) throws Exception {
		//Get all the required service Reference Descriptions for this Service Component
		ReferenceDescription[] referenceDescriptions = componentDescription.getReferences();
		//call the Bind method if the Reference Description includes one
		if (referenceDescriptions.length > 0) {
			for (int i = 0; i < referenceDescriptions.length; i++) {
				if (referenceDescriptions[i].getBind() != null) {
					bindReference(referenceDescriptions[i], instance);
				}
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
	public void bindReference(ReferenceDescription referenceDescription, Object instance) throws Exception {
		ServiceReference[] serviceReferences = null;
		//if there is a published service, then get the ServiceObject and call bind
		try {
			//get All Registered services using this target filter
			serviceReferences = bundleContext.getServiceReferences(referenceDescription.getInterfacename(), referenceDescription.getTarget());
		} catch (Exception e) {
			main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, referenceDescription.getComponentDescription().getBundle(), e);
			throw e;//rethrow exception so resolver is eventually notified that this CDP is bad
		}

		if (serviceReferences != null) {
			//we only want to bind one service
			if ((referenceDescription.getCardinality().equals("1..1")) || (referenceDescription.getCardinality().equals("0..1"))) {
				bindServiceToReference(referenceDescription, serviceReferences[0], instance);
				//here we can bind more than one service, if availible
			} else if ((referenceDescription.getCardinality().equals("1..n")) || (referenceDescription.getCardinality().equals("0..n"))) {
				for (int j = 0; j < serviceReferences.length; j++) {
					bindServiceToReference(referenceDescription, serviceReferences[j], instance);
				}
			}
		} else {
			bindServiceToReference(referenceDescription, null, instance);
		}

	}

	//helper method for bindReference
	private void bindServiceToReference(ReferenceDescription referenceDescription, ServiceReference serviceReference, Object instance) throws Exception {
		Object serviceObject = null;
		if (serviceReference != null) {
			serviceObject = bundleContext.getService(serviceReference);
		} else if (referenceDescription.getComponentDescription().isEligible()) {
			serviceObject = main.resolver.instanceProcess.getInstanceWithInterface(referenceDescription.getInterfacename());
		}
		if ((serviceObject != null) && (!referenceDescription.containsServiceObject(serviceObject))) //make sure we have not already binded this object
		{
			if (referenceDescription.getBind() != null) {
				try {
					invoke.bindComponent(referenceDescription.getBind(), instance, serviceObject);
					//if this suceeds, add the service object to the reference description
					referenceDescription.addServiceObject(serviceObject);
				} catch (Exception e) {
					main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, referenceDescription.getComponentDescription().getBundle(), e);
					throw e;//rethrow exception so resolver is eventually notified that this CDP is bad
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
	public void unbind(ComponentDescription componentDescription, Object instance) {
		//Get all the required service reference descriptions for this Service Component
		ReferenceDescription[] referenceDescriptions = componentDescription.getReferences();
		//call the unBind method if the Reference Description includes one
		if (referenceDescriptions.length > 0) {
			for (int i = 0; i < referenceDescriptions.length; i++) {
				if (referenceDescriptions[i].getUnbind() != null) {
					unbindReference(referenceDescriptions[i], instance);
				}
			}
		}
	}

	/** 
	 * Call the unbind method for this Reference Description
	 * 
	 * @param referenceDescription
	 * @param instance 
	 */
	public void unbindReference(ReferenceDescription referenceDescription, Object instance) {
		Object[] serviceObjects = referenceDescription.getServiceObjects();
		if (serviceObjects != null) {
			for (int j = 0; j < serviceObjects.length; j++) {
				unbindServiceFromReference(referenceDescription, instance, serviceObjects[j]);
			}
		}
	}

	/** 
	 * Call the unbind method for this Reference Description
	 * 
	 * @param referenceDescription
	 * @param instance 
	 * @param seviceObject
	 */
	public void unbindDynamicReference(ReferenceDescription referenceDescription, Object instance, Object serviceObject) throws Exception {

		unbindServiceFromReference(referenceDescription, instance, serviceObject);

		//check if we need to rebind
		if ((referenceDescription.getCardinality().equals("1..1")) || (referenceDescription.getCardinality().equals("0..n")) || (referenceDescription.getCardinality().equals("1..n"))) {
			bind(referenceDescription.getComponentDescription(), instance);
		}
	}

	//unbindReference helper method
	private void unbindServiceFromReference(ReferenceDescription referenceDescription, Object instance, Object serviceObject) {
		if (referenceDescription.getUnbind() != null)
			try {
				invoke.unbindComponent(referenceDescription.getUnbind(), instance, serviceObject);
			} catch (Exception e) {
				main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, referenceDescription.getComponentDescription().getBundle(), e);
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
		contextMap.put(component, context);
		return context;
	}

	private ComponentContext getComponentContext(ComponentDescriptionProp component) {
		return (ComponentContext) contextMap.get(component);
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

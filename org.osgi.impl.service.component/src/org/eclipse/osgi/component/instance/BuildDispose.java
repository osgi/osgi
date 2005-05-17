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
import org.eclipse.osgi.component.model.ComponentDescription;
import org.eclipse.osgi.component.model.ComponentDescriptionProp;
import org.eclipse.osgi.component.resolver.Reference;
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

	public BuildDispose(Main main) {
		invoke = new InvokeMethod();
		this.main = main;
	}

	/**
	 * dispose cleanup the SCR is shutting down
	 */
	void dispose() {
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
	public ComponentInstance build(BundleContext bundleContext, Bundle usingBundle, ComponentDescriptionProp component, Dictionary properties) throws Exception {
		ComponentDescription componentDescription = component.getComponentDescription();

		Object instance = createInstance(componentDescription);

		ComponentInstanceImpl componentInstance = instantiate(component, instance, properties);

		bind(component, componentInstance, bundleContext);

		activate(component, usingBundle, componentInstance);

		component.addInstance(componentInstance);

		return componentInstance;
	}

	/** dispose of the Component Instances
	 * 
	 * @param component Component Description plus properties
	 */
	void disposeComponent(ComponentDescriptionProp component) {

		ComponentInstanceImpl componentInstance;
		// get all instances for this component
		List instances = component.getInstances();
		if (instances != null) {
			Iterator it = instances.iterator();
			while (it.hasNext()) {
				componentInstance = (ComponentInstanceImpl) it.next();
				deactivate(component, componentInstance.getInstance());
				unbind(component, componentInstance);
				componentInstance = null;
			}
		}
		component.removeAllInstances();
	}

	/** dispose of the Component Instance
	 * 
	 * @param component Component Description plus properties
	 * @param ci ComponentInstance
	 */
	public void disposeComponentInstance(ComponentDescriptionProp component, ComponentInstance ci) {

		ComponentInstanceImpl componentInstance = (ComponentInstanceImpl) ci;
		deactivate(component, componentInstance.getInstance());
		unbind(component, componentInstance);
		componentInstance = null;
		component.removeInstance(componentInstance);
	}

	/**
	 * Create the new Instance
	 * 
	 * @param componentDescription
	 * @return Object instance
	 */
	Object createInstance(ComponentDescription componentDescription) throws Exception {
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
	private ComponentInstanceImpl instantiate(ComponentDescriptionProp component, Object instance, Dictionary properties) {
		return new ComponentInstanceImpl(this, component, instance, properties);
	}

	/**
	 * Call the bind method for each of the Referenced Services in this Service Component
	 *
	 * @param componentDescription
	 * @param instance 
	 */
	private void bind(ComponentDescriptionProp componentDescriptionProp, ComponentInstanceImpl componentInstance, BundleContext context) throws Exception {
		//Get all the required service Reference Descriptions for this Service Component
		List references = componentDescriptionProp.getReferences();
		//call the Bind method if the Reference Description includes one
		Iterator itr = references.iterator();
		while (itr.hasNext()) {
			Reference reference = (Reference) itr.next();
			if (reference.getReferenceDescription().getBind() != null) {
				bindReference(reference, componentInstance, context);
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
	void bindReference(Reference reference, ComponentInstanceImpl componentInstance, BundleContext bundleContext) throws Exception {
		ServiceReference[] serviceReferences = null;
		//if there is a published service, then get the ServiceObject and call bind
		try {
			//get All Registered services using this target filter
			serviceReferences = bundleContext.getServiceReferences(reference.getReferenceDescription().getInterfacename(), reference.getReferenceDescription().getTarget());
		} catch (Exception e) {
			main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, reference.getReferenceDescription().getComponentDescription().getBundle(), e);
			throw e;//rethrow exception so resolver is eventually notified that this CDP is bad
		}

		String cardinality = reference.getReferenceDescription().getCardinality();

		//if cardinality is 0..1 or 0..n, it is OK if there is nothing to bind with
		if ((cardinality.equals("0..1") || cardinality.equals("0..n")) && serviceReferences == null) {
			//that's OK
			return;
		}

		//we only want to bind one service
		if ((cardinality.equals("1..1")) || (cardinality.equals("0..1"))) {
			bindServiceToReference(reference, serviceReferences[0], componentInstance, bundleContext);
			//here we can bind more than one service, if availible
		} else if ((cardinality.equals("1..n")) || (cardinality.equals("0..n"))) {
			for (int j = 0; j < serviceReferences.length; j++) {
				bindServiceToReference(reference, serviceReferences[j], componentInstance, bundleContext);
			}
		}

	}

	//helper method for bindReference
	private void bindServiceToReference(Reference reference, ServiceReference serviceReference, ComponentInstanceImpl componentInstance, BundleContext bundleContext) throws Exception {
		//	make sure we have not already bound this object
		if (!reference.bindedToServiceReference(serviceReference)) {
			Object serviceObject = bundleContext.getService(serviceReference);
			if (reference.getReferenceDescription().getBind() != null) {
				try {
					invoke.bindComponent(reference.getReferenceDescription().getBind(), componentInstance.getInstance(), serviceObject);
					//if this succeeds, save the servicereference and service object so we can call unbind later
					reference.addServiceReference(serviceReference);
					componentInstance.addServiceReference(serviceReference, serviceObject);
				} catch (Exception e) {
					main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, reference.getReferenceDescription().getComponentDescription().getBundle(), e);
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
	private void unbind(ComponentDescriptionProp component, ComponentInstanceImpl componentInstance) {
		//Get all the required service reference descriptions for this Service Component
		List references = component.getReferences();
		//call the unBind method if the Reference Description includes one
		Iterator itr = references.iterator();
		while (itr.hasNext()) {
			Reference reference = (Reference) itr.next();
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
	private void unbindReference(Reference reference, ComponentInstanceImpl componentInstance) {
		List serviceReferences = reference.getServiceReferences();
		Iterator itr = serviceReferences.iterator();
		while (itr.hasNext()) {
			ServiceReference serviceReference = (ServiceReference) itr.next();
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
	void unbindDynamicReference(ComponentDescriptionProp component, Reference reference, ComponentInstanceImpl componentInstance, ServiceReference serviceReference) throws Exception {

		//rebind if we can
		bindReference(reference, componentInstance, main.framework.getBundleContext(component.getComponentDescription().getBundle()));

		unbindServiceFromReference(reference, componentInstance, serviceReference);

		componentInstance.removeServiceReference(serviceReference);

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
	private void activate(ComponentDescriptionProp component, Bundle usingBundle, ComponentInstance componentInstance) throws Exception {

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
	private void deactivate(ComponentDescriptionProp component, Object instance) {

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
		ComponentContext context = new ComponentContextImpl(main, usingBundle, component, componentInstance);
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
	long getNextComponentId() {
		long id = componentid;
		componentid++;
		return (id);
	}

}

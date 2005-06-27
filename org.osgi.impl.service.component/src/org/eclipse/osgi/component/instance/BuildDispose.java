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

import org.eclipse.osgi.component.Main;
import org.eclipse.osgi.component.model.ComponentDescription;
import org.eclipse.osgi.component.model.ComponentDescriptionProp;
import org.eclipse.osgi.component.resolver.Reference;
import org.eclipse.osgi.component.resolver.Resolver;
import org.eclipse.osgi.component.workqueue.WorkDispatcher;
import org.eclipse.osgi.impl.service.component.ComponentContextImpl;
import org.eclipse.osgi.impl.service.component.ComponentInstanceImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentInstance;

/**
 * This class provides the following function
 * 
 * Build of a component instance - includes activate, bind, instantiate.
 * Dispose of a component instance - includes deactivate, unbind, unreference.
 * 
 */
public class BuildDispose implements WorkDispatcher {

	/* set this to true to compile in debug messages */
	static final boolean DEBUG = false;

	protected InvokeMethod invoke;

	/** next free component id. */
	protected long componentid;

	private int stackCount;
	private List delayedActivateCDPs;
	private Hashtable delayedBindTable;
	private static final int BUILD = 1;

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
		stackCount = 0;
	}

	/**
	 * dispose cleanup the SCR is shutting down
	 */
	public void dispose() {
		synchronized (this) {
			invoke = null;
			main = null;
		}
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

		synchronized (this) {

			//keep track of how many times we have re-entered this method
			stackCount++;

			ComponentInstanceImpl componentInstance = null;

			try {
				ComponentDescription componentDescription = component.getComponentDescription();

				Object instance = createInstance(componentDescription);

				componentInstance = instantiate(component, instance, properties);

				bind(component, componentInstance, bundleContext);

				activate(component, usingBundle, componentInstance);

				component.addInstance(componentInstance);
			} finally {

				stackCount--;

			}

			if (stackCount == 0 && delayedBindTable != null) {
				//put delayed activates and dynamic binds on the queue.
				//(this is used to handle circularity)
				Iterator cdps = delayedActivateCDPs.iterator();
				while (cdps.hasNext()) {
					ComponentDescriptionProp cdp = (ComponentDescriptionProp) cdps.next();
					if (cdp.getInstances().isEmpty()) {
						main.resolver.instanceProcess.workQueue.enqueueWork(this, BUILD, cdp);
					}
				}
				main.resolver.instanceProcess.workQueue.enqueueWork(main.resolver, Resolver.DYNAMICBIND, delayedBindTable);
				delayedActivateCDPs = null;
				delayedBindTable = null;
			}

			return componentInstance;
		}
	}

	/** dispose of the Component Instances
	 * 
	 * @param component Component Description plus properties
	 */
	void disposeComponent(ComponentDescriptionProp component) {

		synchronized (this) {
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
			component.clearReferenceCDPs();
			component.clearDelayActivateCDPNames();
		}
	}

	/** dispose of the Component Instance
	 * 
	 * @param component Component Description plus properties
	 * @param ci ComponentInstance
	 */
	public void disposeComponentInstance(ComponentDescriptionProp component, ComponentInstance ci) {

		synchronized (this) {
			ComponentInstanceImpl componentInstance = (ComponentInstanceImpl) ci;
			deactivate(component, componentInstance.getInstance());
			unbind(component, componentInstance);
			componentInstance = null;
			component.removeInstance(componentInstance);
		}
	}

	/**
	 * Create the new Instance
	 * 
	 * @param componentDescription
	 * @return Object instance
	 */
	private Object createInstance(ComponentDescription componentDescription) throws Exception {
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
				bindReference(componentDescriptionProp, reference, componentInstance, context);
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
	public void bindReference(ComponentDescriptionProp cdp, Reference reference, ComponentInstanceImpl componentInstance, BundleContext bundleContext) throws Exception {
		synchronized (this) {
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
				bindServiceToReference(cdp, reference, serviceReferences[0], componentInstance, bundleContext);
				//here we can bind more than one service, if availible
			} else if ((cardinality.equals("1..n")) || (cardinality.equals("0..n"))) {
				for (int j = 0; j < serviceReferences.length; j++) {
					bindServiceToReference(cdp, reference, serviceReferences[j], componentInstance, bundleContext);
				}
			}
		} //end synchronized(this)

	}

	//helper method for bindReference
	private void bindServiceToReference(ComponentDescriptionProp cdp, Reference reference, ServiceReference serviceReference, ComponentInstanceImpl componentInstance, BundleContext bundleContext) throws Exception {
		//	make sure we have not already bound this object
		if (!reference.bindedToServiceReference(serviceReference)) {

			Object serviceObject = getService(cdp, reference, bundleContext, serviceReference);

			if (serviceObject != null && reference.getReferenceDescription().getBind() != null) {
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

	public Object getService(ComponentDescriptionProp consumerCDP, Reference reference, BundleContext bundleContext, ServiceReference serviceReference) {

		//if we are building a component and 
		//if service is provided by a Service Component that is not active yet,
		//check to see if we would be causing a circularity
		String producerComponentName = (String) serviceReference.getProperty(ComponentConstants.COMPONENT_NAME);
		if (stackCount != 0 && producerComponentName != null && consumerCDP.getDelayActivateCDPNames().contains(producerComponentName)) {

			//find producer cdp
			ComponentDescriptionProp producerCDP = null;
			Iterator enabledCDPs = main.resolver.componentDescriptionPropsEnabled.iterator();
			while (enabledCDPs.hasNext()) {
				ComponentDescriptionProp cdp = (ComponentDescriptionProp) enabledCDPs.next();
				if (producerComponentName.equals(cdp.getComponentDescription().getName())) {
					//found the producer cdp
					producerCDP = cdp;
					break;
				}
			}

			//check if producerCDP has not been activated already
			if (producerCDP != null && producerCDP.getInstances().isEmpty()) {

				//producer cdp is not active - do not activate it because that would cause circularity

				//if reference has bind method and policy=dynamic, activate later and bind
				if (reference.getReferenceDescription().getBind() != null && reference.getPolicy().equalsIgnoreCase("dynamic")) {
					//delay bind by putting on the queue later
					if (delayedBindTable == null) {
						delayedActivateCDPs = new ArrayList();
						delayedBindTable = new Hashtable();
					}
					if (!delayedActivateCDPs.contains(producerCDP)) {
						delayedActivateCDPs.add(producerCDP);
					}
					delayedBindTable.put(reference, consumerCDP);
				}

				//can't get service now because of circularity - we will bind later
				//(dynamically) if the reference had a bind method and was dynamic
				return null;

			} //end if producerCDP was not already active

		}

		//getting this service will not cause a circularity
		return bundleContext.getService(serviceReference);

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

		synchronized (this) {
			//rebind if we can
			bindReference(component, reference, componentInstance, main.framework.getBundleContext(component.getComponentDescription().getBundle()));

			unbindServiceFromReference(reference, componentInstance, serviceReference);

			componentInstance.removeServiceReference(serviceReference);
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
		synchronized (this) {
			long id = componentid;
			componentid++;
			return (id);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osgi.component.workqueue.WorkDispatcher#dispatchWork(int, java.lang.Object)
	 */
	public void dispatchWork(int workAction, Object workObject) {
		switch (workAction) {
			case BUILD :
				ComponentDescriptionProp cdp = (ComponentDescriptionProp) workObject;
				BundleContext bundleContext = main.framework.getBundleContext(cdp.getComponentDescription().getBundle());
				try {
					build(bundleContext, null, cdp, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}

	}

}

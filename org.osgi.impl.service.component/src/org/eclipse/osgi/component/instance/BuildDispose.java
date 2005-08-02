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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.osgi.component.Log;
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
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentException;
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
	 * @param cdp - Component Description with Properties Object
	 * @param instance - instance object
	 * @param properties - properties for this instance
	 * 
	 * @return ComponentInstance
	 */

	public ComponentInstance build(BundleContext bundleContext, Bundle usingBundle, ComponentDescriptionProp cdp, Dictionary properties) throws ComponentException {

		synchronized (this) {

			//keep track of how many times we have re-entered this method
			stackCount++;

			ComponentInstanceImpl componentInstance = null;

			try {
				ComponentDescription componentDescription = cdp.getComponentDescription();

				Object instance = createInstance(componentDescription);

				componentInstance = instantiate(cdp, instance, properties);

				bind(cdp, componentInstance, bundleContext);

				activate(cdp, usingBundle, componentInstance);

				cdp.addInstance(componentInstance);

			} catch (ComponentException e) {
				Log.log(1, "[SCR] Error attempting to build component ", e);
				throw e;

			} finally {

				stackCount--;

			}

			if (stackCount == 0 && delayedBindTable != null) {
				//put delayed activates and dynamic binds on the queue.
				//(this is used to handle circularity)
				Iterator cdps = delayedActivateCDPs.iterator();
				while (cdps.hasNext()) {
					ComponentDescriptionProp cdpnext = (ComponentDescriptionProp) cdps.next();
					if (cdpnext.getInstances().isEmpty()) {
						main.resolver.instanceProcess.workQueue.enqueueWork(this, BUILD, cdpnext);
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
	 * @param cdp Component Description plus properties
	 */
	void disposeComponent(ComponentDescriptionProp cdp) {

		synchronized (this) {
			// get all instances for this component
			List instances = cdp.getInstances();
			if (instances != null) {
				Iterator it = instances.iterator();
				while (it.hasNext()) {
					disposeComponentInstance(cdp, (ComponentInstanceImpl) it.next());
				}
			}
			cdp.removeAllInstances();
			cdp.clearReferenceCDPs();
			cdp.clearDelayActivateCDPNames();
		}
	}

	/** dispose of the Component Instance
	 * 
	 * @param cdp - Component Description plus properties
	 * @param ci ComponentInstance
	 */
	public void disposeComponentInstance(ComponentDescriptionProp cdp, ComponentInstance ci) {

		synchronized (this) {
			ComponentInstanceImpl componentInstance = (ComponentInstanceImpl) ci;
			deactivate(cdp, componentInstance.getInstance());
			unbind(cdp, componentInstance);

			//unget any remaining service references
			Enumeration serviceReferences = componentInstance.getServiceReferences();
			while (serviceReferences.hasMoreElements()) {
				cdp.getComponentContext().getBundleContext().ungetService((ServiceReference) serviceReferences.nextElement());

			}
			componentInstance = null;
		}
	}

	/**
	 * Create the new Instance
	 * 
	 * @param componentDescription
	 * @return Object instance
	 */

	private Object createInstance(ComponentDescription componentDescription) throws ComponentException {
		Object instance = null;
		String classname = componentDescription.getImplementation().getClassname();
		try {
			instance = ((componentDescription.getBundle()).loadClass(classname)).newInstance();
		} catch (IllegalAccessException e) {
			Log.log(1, "[SCR] IllegalAccessException attempting to create instance ", e);
			throw new ComponentException(e.getMessage());
		} catch (ClassNotFoundException e) {
			Log.log(1, "[SCR] ClassNotFoundException attempting to create instance. ", e);
			throw new ComponentException(e.getMessage());
		} catch (InstantiationException e) {
			Log.log(1, "[SCR] InstantiationException attempting to create instance. ", e);
			throw new ComponentException(e.getMessage());
		}

		return instance;
	}

	/**
	 * instantiate - create the ComponentInstance
	 *
	 * @param cdp
	 * @param instance 
	 * @param properties
	 */
	private ComponentInstanceImpl instantiate(ComponentDescriptionProp cdp, Object instance, Dictionary properties) {
		return new ComponentInstanceImpl(this, cdp, instance, properties);
	}

	/**
	 * Call the bind method for each of the Referenced Services in this Service Component
	 *
	 * @param cdp
	 * @param componentInstance 
	 * @param context - BundleContext 
	 */
	private void bind(ComponentDescriptionProp cdp, ComponentInstanceImpl componentInstance, BundleContext context) {
		//Get all the required service Reference Descriptions for this Service Component
		//bind them in order
		List references = cdp.getReferences();
		//call the Bind method if the Reference Description includes one
		Iterator itr = references.iterator();
		while (itr.hasNext()) {
			Reference reference = (Reference) itr.next();
			if (reference.getReferenceDescription().getBind() != null) {
				bindReference(cdp, reference, componentInstance, context);
			}
		}
	}

	/** 
	 * Call the bind method for this referenceDescription
	 * 
	 * @param cdp
	 * @param reference
	 * @param componentInstance 
	 * @param bundleContext
	 * 
	 */
	public void bindReference(ComponentDescriptionProp cdp, Reference reference, ComponentInstanceImpl componentInstance, BundleContext bundleContext) {
		synchronized (this) {
			ServiceReference[] serviceReferences = null;
			//if there is a published service, then get the ServiceObject and call bind
			try {
				//get All Registered services using this target filter
				serviceReferences = bundleContext.getServiceReferences(reference.getReferenceDescription().getInterfacename(), reference.getReferenceDescription().getTarget());

				//If a bind method throws an exception, SCR must log an error message containing the exception with the Log Service but 
				//the activation of the component configuration does not fail.
			} catch (InvalidSyntaxException e) {
				Log.log(1, "[SCR] InvalidSyntaxException attempting to bindReference ", e);
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
	private void bindServiceToReference(ComponentDescriptionProp cdp, Reference reference, ServiceReference serviceReference, ComponentInstanceImpl componentInstance, BundleContext bundleContext) {
		//	make sure we have not already bound this object
		if (!reference.bindedToServiceReference(serviceReference)) {

			try {

				Method method = invoke.findMethod(reference.getReferenceDescription().getBind(), componentInstance.getInstance());
				Object param = null;
				if (method.getParameterTypes()[0].equals(ServiceReference.class)) {
					param = serviceReference;
				} else {
					param = getService(cdp, reference, bundleContext, serviceReference);
				}

				if (param != null && reference.getReferenceDescription().getBind() != null) {
					invoke.bindComponent(method, componentInstance.getInstance(), param);
					//if this succeeds, save the servicereference and service object so we can call unbind later
					reference.addServiceReference(serviceReference);
					if (serviceReference != param) {
						//we created a service object, (got the service) so save it for disposal later
						componentInstance.addServiceReference(serviceReference, param);
					}

				}
				//If a bind method throws an exception, SCR must log an error message containing 
				//the exception with the Log Service but the activation of the component configuration does not fail.
			} catch (IllegalAccessException e) {
				Log.log(1, "[SCR] IllegalAccessException attempting to bind Service to Reference ", e);
			} catch (InvocationTargetException e) {
				Log.log(1, "[SCR] InvocationTargetException attempting to bind Service to Reference ", e);

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
	 * @param cdp
	 * @param componentInstance 
	 */
	private void unbind(ComponentDescriptionProp cdp, ComponentInstanceImpl componentInstance) {
		//Get all the required service reference descriptions for this Service Component
		List references = cdp.getReferences();
		//call the unBind method if the Reference Description includes one
		//unbind in reverse order
		ListIterator itr = references.listIterator(references.size());
		while (itr.hasPrevious()) {
			Reference reference = (Reference) itr.previous();
			if (reference.getReferenceDescription().getUnbind() != null) {
				unbindReference(reference, componentInstance);
			}
		}
	}

	/** 
	 * Call the unbind method for this Reference Description
	 * 
	 * @param reference
	 * @param componentInstance 
	 */
	private void unbindReference(Reference reference, ComponentInstanceImpl componentInstance) {
		List serviceReferences = reference.getServiceReferences();
		Iterator itr = serviceReferences.iterator();
		while (itr.hasNext()) {
			ServiceReference serviceReference = (ServiceReference) itr.next();
			unbindServiceFromReference(reference, componentInstance, serviceReference);
		}
	}

	/** 
	 * Call the unbind method for this Reference Description
	 * 
	 * @param cdp
	 * @param reference
	 * @param componentInstance 
	 * @param serviceReference
	 */

	void unbindDynamicReference(ComponentDescriptionProp cdp, Reference reference, ComponentInstanceImpl componentInstance, ServiceReference serviceReference) {
		synchronized (this) {
			//rebind if we can
			bindReference(cdp, reference, componentInstance, main.framework.getBundleContext(cdp.getComponentDescription().getBundle()));

			unbindServiceFromReference(reference, componentInstance, serviceReference);

		}

	}

	//unbindReference helper method
	private void unbindServiceFromReference(Reference reference, ComponentInstanceImpl componentInstance, ServiceReference serviceReference) {
		String unbind = reference.getReferenceDescription().getUnbind();
		Object serviceObject = componentInstance.getServiceObject(serviceReference);
		Object param = null;
		if (unbind != null) {
			Method method = invoke.findMethod(unbind, componentInstance.getInstance());

			if (method.getParameterTypes()[0].equals(ServiceReference.class)) {
				param = serviceReference;
			} else {
				param = serviceObject;
			}
			// If an unbind method throws an exception, SCR must log an error message 
			// containing the exception with the Log Service and the deactivation of the component configuration will continue.
			try {
				invoke.unbindComponent(method, componentInstance.getInstance(), param);
			} catch (InvocationTargetException e) {
				Log.log(1, "[SCR] InvocationTargetException attempting to unbind reference.", e);
			} catch (IllegalAccessException e) {
				Log.log(1, "[SCR] IllegalAccessException attempting to unbind reference.", e);
			}
		}
		//release service object
		if (serviceObject != null) {
			componentInstance.component.getComponentContext().getBundleContext().ungetService(serviceReference);
		}
		componentInstance.removeServiceReference(serviceReference);
	}

	/**
	 * Invoke the Serivce Component activate method
	 *
	 * @param cdp - ComponentDescriptionProperty object
	 * @param usingBundle - 
	 * @param componentInstance - 
	 */
	private void activate(ComponentDescriptionProp cdp, Bundle usingBundle, ComponentInstanceImpl componentInstance) throws ComponentException {

		ComponentContext componentContext = createComponentContext(cdp, usingBundle, componentInstance);

		//If the activate method throws an exception, SCR must log an error message 
		// containing the exception with the Log Service and the component configuration is not activated.
		try {
			invoke.activateComponent(componentInstance.getInstance(), componentContext);
		} catch (IllegalAccessException e) {
			Log.log(1, "[SCR] IllegalAccessException attempting to activate component ", e);
			throw new ComponentException(e.getMessage());
		} catch (InvocationTargetException e) {
			Log.log(1, "[SCR] InvocationTargetException attempting to activate component ", e);
			throw new ComponentException(e.getMessage());
		}
	}

	/**
	 * Invoke the Service Component deactivate method
	 *
	 * @param cdp - ComponentDescriptionProperty object
	 * @param instance 
	 */
	private void deactivate(ComponentDescriptionProp cdp, Object instance) {

		// If the deactivate method throws an exception, SCR must log an error message 
		// containing the exception with the Log Service and the component configuration will continue.
		try {
			invoke.deactivateComponent(instance, getComponentContext(cdp));
		} catch (InvocationTargetException e) {
			Log.log(1, "[SCR] InvocationTargetException attempting to deactivate component.", e);
		} catch (IllegalAccessException e) {
			Log.log(1, "[SCR] IllegalAccessException attempting to deactivate component. ", e);
		}

	}

	/**
	 * Create and return the ComponentContext 
	 * 
	 * @param cdp
	 * @param usingBundle
	 * @param componentInstance
	 * @return
	 */
	private ComponentContext createComponentContext(ComponentDescriptionProp cdp, Bundle usingBundle, ComponentInstanceImpl componentInstance) {
		ComponentContext context = new ComponentContextImpl(main, usingBundle, cdp, componentInstance);
		cdp.setComponentContext(context);
		return context;
	}

	private ComponentContext getComponentContext(ComponentDescriptionProp cdp) {
		return cdp.getComponentContext();
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
				build(bundleContext, null, cdp, null);
		}

	}

}

/*
 * $Header$
 * 
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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.osgi.component.Log;
import org.eclipse.osgi.component.Main;
import org.eclipse.osgi.component.model.ComponentDescription;
import org.eclipse.osgi.component.model.ComponentDescriptionProp;
import org.eclipse.osgi.component.model.ReferenceDescription;
import org.eclipse.osgi.component.resolver.Reference;
import org.eclipse.osgi.component.resolver.Resolver;
import org.eclipse.osgi.component.workqueue.WorkDispatcher;
import org.eclipse.osgi.impl.service.component.ComponentContextImpl;
import org.eclipse.osgi.impl.service.component.ComponentInstanceImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentException;
import org.osgi.service.component.ComponentInstance;

/**
 * This class provides the following function
 * 
 * Build of a component instance - includes activate, bind, instantiate. Dispose
 * of a component instance - includes deactivate, unbind, unreference.
 * 
 */
public class BuildDispose implements WorkDispatcher {

	/* set this to true to compile in debug messages */
	static final boolean		DEBUG	= false;

	protected InvokeMethod		invoke;

	private int					stackCount;
	private Hashtable			delayedBindTable;
	private static final int	BUILD	= 1;

	/** Main SCR class */
	protected Main				main;

	/**
	 * BuildDispose - Build or Dispose of the Instance
	 * 
	 * @param main Main SCR class
	 * @param registrations - map of ComponentDescriptionProps to service
	 *        registrations
	 */

	public BuildDispose(Main main) {
		invoke = new InvokeMethod(this);
		this.main = main;
		stackCount = 0;
		delayedBindTable = new Hashtable();
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
	 * @param bundleContext - bundle context of the bundle containg the Service
	 *        Component
	 * @param cdp - Component Description with Properties Object
	 * @param instance - instance object
	 * @param properties - properties for this instance
	 * 
	 * @return ComponentInstance
	 */

	public ComponentInstance build(Bundle usingBundle,
			ComponentDescriptionProp cdp) throws ComponentException {

		synchronized (this) {

			// keep track of how many times we have re-entered this method
			stackCount++;

			ComponentInstanceImpl componentInstance = null;

			try {
				ComponentDescription componentDescription = cdp
						.getComponentDescription();

				Object instance = createInstance(componentDescription);

				componentInstance = instantiate(cdp, instance);

				createComponentContext(cdp, usingBundle, componentInstance);

				bind(cdp, componentInstance);

				activate(componentInstance);

				cdp.addInstance(componentInstance);

			}
			catch (ComponentException e) {
				Log.log(1, "[SCR] Error attempting to build component ", e);
				throw e;

			}
			finally {

				stackCount--;

			}

			if (stackCount == 0 && !delayedBindTable.isEmpty()) {
				// put delayed dynamic binds on the queue.
				// (this is used to handle circularity)
				main.resolver.instanceProcess.workQueue.enqueueWork(
						main.resolver, Resolver.DYNAMICBIND, delayedBindTable
								.clone());
				delayedBindTable.clear();
			}

			return componentInstance;
		} // end synchronized(this)
	}

	/**
	 * dispose of the Component Instances
	 * 
	 * @param cdp Component Description plus properties
	 */
	void disposeComponent(ComponentDescriptionProp cdp) {

		synchronized (this) {
			// unregister this cdp's service
			ServiceRegistration serviceRegistration = cdp
					.getServiceRegistration();
			if (serviceRegistration != null) {
				try {
					serviceRegistration.unregister();
				}
				catch (IllegalStateException e) {
					// service has already been unregistered, no problem
				}
			}

			// get all instances for this component
			List instances = cdp.getInstances();
			if (instances != null) {
				Iterator it = instances.iterator();
				while (it.hasNext()) {
					disposeComponentInstance(cdp, (ComponentInstanceImpl) it
							.next());
				}
			}
			cdp.removeAllInstances();
			Iterator it = cdp.getReferences().iterator();
			while (it.hasNext()) {
				Reference reference = (Reference) it.next();
				reference.clearServiceReferences();
			}
			cdp.clearDelayActivateCDPNames();
		}
	}

	/**
	 * dispose of the Component Instance
	 * 
	 * @param cdp - Component Description plus properties
	 * @param ci ComponentInstance
	 */
	public void disposeComponentInstance(ComponentDescriptionProp cdp,
			ComponentInstance ci) {

		synchronized (this) {
			ComponentInstanceImpl componentInstance = (ComponentInstanceImpl) ci;
			deactivate(componentInstance);
			unbind(cdp, componentInstance);

			// unget any remaining service references
			Enumeration serviceReferences = componentInstance
					.getServiceReferences();
			while (serviceReferences.hasMoreElements()) {
				componentInstance.getComponentContext().getBundleContext()
						.ungetService(
								(ServiceReference) serviceReferences
										.nextElement());

			}
			componentInstance = null;
		}
	}

	/**
	 * Create the new Instance
	 * 
	 * @param cd
	 * @return Object instance
	 */

	private Object createInstance(ComponentDescription cd)
			throws ComponentException {
		Object instance = null;
		String classname = cd.getImplementation().getClassname();
		try {
			instance = cd.getBundleContext().getBundle().loadClass(classname)
					.newInstance();
		}
		catch (IllegalAccessException e) {
			Log
					.log(
							1,
							"[SCR] IllegalAccessException attempting to create instance ",
							e);
			throw new ComponentException(e.getMessage());
		}
		catch (ClassNotFoundException e) {
			Log
					.log(
							1,
							"[SCR] ClassNotFoundException attempting to create instance. ",
							e);
			throw new ComponentException(e.getMessage());
		}
		catch (InstantiationException e) {
			Log
					.log(
							1,
							"[SCR] InstantiationException attempting to create instance. ",
							e);
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
	private ComponentInstanceImpl instantiate(ComponentDescriptionProp cdp,
			Object instance) {
		return new ComponentInstanceImpl(main, cdp, instance);
	}

	/**
	 * Call the bind method for each of the Referenced Services in this Service
	 * Component
	 * 
	 * @param cdp
	 * @param componentInstance
	 * @param context - BundleContext
	 */
	private void bind(ComponentDescriptionProp cdp,
			ComponentInstanceImpl componentInstance) {
		// Get all the required service Reference Descriptions for this Service
		// Component
		// bind them in order
		List references = cdp.getReferences();
		// call the Bind method if the Reference Description includes one
		Iterator itr = references.iterator();
		while (itr.hasNext()) {
			Reference reference = (Reference) itr.next();
			if (reference.getReferenceDescription().getBind() != null) {
				bindReference(reference, componentInstance);
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
	public void bindReference(Reference reference,
			ComponentInstanceImpl componentInstance) {
		synchronized (this) {
			ServiceReference[] serviceReferences = null;
			// if there is a published service, then get the ServiceObject and
			// call bind
			try {
				// get All Registered services using this target filter
				serviceReferences = componentInstance.getComponentContext()
						.getBundleContext().getServiceReferences(
								reference.getReferenceDescription()
										.getInterfacename(),
								reference.getTarget());

				// If a bind method throws an exception, SCR must log an error
				// message containing the exception with the Log Service but
				// the activation of the component configuration does not fail.
			}
			catch (InvalidSyntaxException e) {
				Log
						.log(
								1,
								"[SCR] InvalidSyntaxException attempting to bindReference ",
								e);
			}

			// if cardinality is 0..1 or 0..n, it is OK if there is nothing to
			// bind with
			if (!reference.getReferenceDescription().isRequired()
					&& serviceReferences == null) {
				// that's OK
				return;
			}

			// sort by service ranking and service id
			Arrays.sort(serviceReferences);

			// we only want to bind one service
			if (reference.getReferenceDescription().getCardinalityHigh() == 1) {
				bindServiceToReference(reference, serviceReferences[0],
						componentInstance);
				// here we can bind more than one service, if availible
			}
			else {
				for (int j = 0; j < serviceReferences.length; j++) {
					bindServiceToReference(reference, serviceReferences[j],
							componentInstance);
				}
			}
		} // end synchronized(this)

	}

	// helper method for bindReference
	private void bindServiceToReference(Reference reference,
			ServiceReference serviceReference,
			ComponentInstanceImpl componentInstance) {
		ReferenceDescription rd = reference.getReferenceDescription();
		// make sure we have not already bound this object
		if (!reference.bindedToServiceReference(serviceReference)
				&& rd.getBind() != null) {

			try {
				Method method = invoke.findBindOrUnbindMethod(
						componentInstance, reference, serviceReference, rd
								.getBind());
				if (method == null) {
					// could be circularity break
					return;
				}
				Object param = null;
				if (method.getParameterTypes()[0]
						.equals(ServiceReference.class)) {
					param = serviceReference;
				}
				else {
					// componentInstance.getServiceObject(...) is populated by
					// the findBindOrUnbindBeMethod function
					param = componentInstance
							.getServiceObject(serviceReference);
				}

				invoke.bindComponent(method, componentInstance.getInstance(),
						param);
				// If a bind method throws an exception, SCR must log an error
				// message containing
				// the exception with the Log Service but the activation of the
				// component configuration does not fail.
			}
			catch (IllegalAccessException e) {
				Log
						.log(
								1,
								"[SCR] IllegalAccessException attempting to bind Service to Reference ",
								e);
			}
			catch (InvocationTargetException e) {
				Log
						.log(
								1,
								"[SCR] InvocationTargetException attempting to bind Service to Reference ",
								e);

			}
			reference.addServiceReference(serviceReference);
		}
	}

	public Object getService(ComponentDescriptionProp consumerCDP,
			Reference reference, BundleContext bundleContext,
			ServiceReference serviceReference) {
		// check if getting this service would cause a circularity
		if (couldCauseCycle(consumerCDP, reference, bundleContext,
				serviceReference)) {
			return null;
		}

		// getting this service will not cause a circularity
		return bundleContext.getService(serviceReference);

	}

	private boolean couldCauseCycle(ComponentDescriptionProp consumerCDP,
			Reference reference, BundleContext bundleContext,
			ServiceReference serviceReference) {
		// if we are not building a component, no cycles possible
		if (stackCount == 0) {
			return false;
		}

		String producerComponentName = (String) serviceReference
				.getProperty(ComponentConstants.COMPONENT_NAME);

		// if producer is not a service component, no cycles possible
		if (producerComponentName == null) {
			return false;
		}

		// check if producer is on our "do not activate" list
		if (!consumerCDP.getDelayActivateCDPNames().contains(
				producerComponentName)) {
			return false;
		}

		// find producer cdp
		ComponentDescriptionProp producerCDP = null;
		Iterator it = main.resolver.satisfiedCDPs.iterator();
		while (it.hasNext()) {
			ComponentDescriptionProp cdp = (ComponentDescriptionProp) it.next();
			if (producerComponentName.equals(cdp.getComponentDescription()
					.getName())) {
				// found the producer cdp
				producerCDP = cdp;
				break;
			}
		}

		if (producerCDP.getComponentDescription().getService()
				.isServicefactory()) {
			// producer is a service factory - there is a new instance for every
			// bundle, so see if one of the instances is used by this bundle
			it = producerCDP.getInstances().iterator();
			Bundle bundle = bundleContext.getBundle();
			while (it.hasNext()) {
				ComponentInstanceImpl producerComponentInstance = (ComponentInstanceImpl) it
						.next();
				if (producerComponentInstance.getComponentContext()
						.getUsingBundle().equals(bundle)) {
					// a producer already exists, so no cycle possible
					return false;
				}
			}
		}
		else {
			// producer is not a service factory - there will only ever be one
			// instance - if it exists then no cycle possible
			if (!producerCDP.getInstances().isEmpty()) {
				return false;
			}
		}

		// producer cdp is not active - do not activate it because that could
		// cause circularity

		// if reference has bind method and policy=dynamic, activate later and
		// bind
		if (reference.getReferenceDescription().getBind() != null
				&& reference.getReferenceDescription().getPolicy()
						.equalsIgnoreCase("dynamic")) {
			// delay bind by putting on the queue later
			delayedBindTable.put(reference, consumerCDP);
		}

		// can't get service now because of circularity - we will bind later
		// (dynamically) if the reference had a bind method and was dynamic
		return true;
	}

	/**
	 * Call the unbind method for each of the Referenced Services in this
	 * Serivce Component
	 * 
	 * @param cdp
	 * @param componentInstance
	 */
	private void unbind(ComponentDescriptionProp cdp,
			ComponentInstanceImpl componentInstance) {
		// Get all the required service reference descriptions for this Service
		// Component
		List references = cdp.getReferences();
		// call the unBind method if the Reference Description includes one
		// unbind in reverse order
		ListIterator itr = references.listIterator(references.size());
		while (itr.hasPrevious()) {
			unbindReference((Reference) itr.previous(), componentInstance);
		}
	}

	/**
	 * Call the unbind method for this Reference Description
	 * 
	 * @param reference
	 * @param componentInstance
	 */
	private void unbindReference(Reference reference,
			ComponentInstanceImpl componentInstance) {
		List serviceReferences = reference.getServiceReferences();
		Iterator itr = serviceReferences.iterator();
		while (itr.hasNext()) {
			ServiceReference serviceReference = (ServiceReference) itr.next();
			unbindServiceFromReference(reference, componentInstance,
					serviceReference);
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

	void unbindDynamicReference(Reference reference,
			ComponentInstanceImpl componentInstance,
			ServiceReference serviceReference) {
		synchronized (this) {
			// rebind if we can
			bindReference(reference, componentInstance);

			unbindServiceFromReference(reference, componentInstance,
					serviceReference);

		}

	}

	// unbindReference helper method
	private void unbindServiceFromReference(Reference reference,
			ComponentInstanceImpl componentInstance,
			ServiceReference serviceReference) {
		ReferenceDescription rd = reference.getReferenceDescription();
		String unbind = rd.getUnbind();
		Object serviceObject = null;
		Method method = null;
		if (unbind != null) {
			method = invoke.findBindOrUnbindMethod(componentInstance,
					reference, serviceReference, unbind);
		}
		if (method != null) {
			Object param = null;
			if (method.getParameterTypes()[0].equals(ServiceReference.class)) {
				param = serviceReference;
			}
			else {
				// if we don't have a service object, create one
				serviceObject = componentInstance
						.getServiceObject(serviceReference);
				param = serviceObject;
			}
			// If an unbind method throws an exception, SCR must log an error
			// message
			// containing the exception with the Log Service and the
			// deactivation of the component configuration will continue.
			try {
				invoke.unbindComponent(method, componentInstance.getInstance(),
						param);
			}
			catch (InvocationTargetException e) {
				Log
						.log(
								1,
								"[SCR] InvocationTargetException attempting to unbind reference.",
								e);
			}
			catch (IllegalAccessException e) {
				Log
						.log(
								1,
								"[SCR] IllegalAccessException attempting to unbind reference.",
								e);
			}
		} // end if (method != null)

		// release service object
		if (serviceObject != null) {
			componentInstance.getComponentContext().getBundleContext()
					.ungetService(serviceReference);
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
	private void activate(ComponentInstanceImpl componentInstance)
			throws ComponentException {

		// If the activate method throws an exception, SCR must log an error
		// message
		// containing the exception with the Log Service and the component
		// configuration is not activated.
		try {
			invoke.activateComponent(componentInstance.getInstance(),
					componentInstance.getComponentContext());
		}
		catch (IllegalAccessException e) {
			Log
					.log(
							1,
							"[SCR] IllegalAccessException attempting to activate component ",
							e);
			throw new ComponentException(e.getMessage());
		}
		catch (InvocationTargetException e) {
			Log
					.log(
							1,
							"[SCR] InvocationTargetException attempting to activate component ",
							e);
			throw new ComponentException(e.getMessage());
		}
	}

	/**
	 * Invoke the Service Component deactivate method
	 * 
	 * @param cdp - ComponentDescriptionProperty object
	 * @param instance
	 */
	private void deactivate(ComponentInstanceImpl componentInstance) {

		// If the deactivate method throws an exception, SCR must log an error
		// message
		// containing the exception with the Log Service and the component
		// configuration will continue.
		try {
			invoke.deactivateComponent(componentInstance.getInstance(),
					componentInstance.getComponentContext());
		}
		catch (InvocationTargetException e) {
			Log
					.log(
							1,
							"[SCR] InvocationTargetException attempting to deactivate component.",
							e);
		}
		catch (IllegalAccessException e) {
			Log
					.log(
							1,
							"[SCR] IllegalAccessException attempting to deactivate component. ",
							e);
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
	private void createComponentContext(ComponentDescriptionProp cdp,
			Bundle usingBundle, ComponentInstanceImpl componentInstance) {
		ComponentContext context = new ComponentContextImpl(main, usingBundle,
				cdp, componentInstance);
		componentInstance.setComponentContext(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.osgi.component.workqueue.WorkDispatcher#dispatchWork(int,
	 *      java.lang.Object)
	 */
	public void dispatchWork(int workAction, Object workObject) {
		switch (workAction) {
			case BUILD :
				// only build if cdp is still resolved
				ComponentDescriptionProp cdp = (ComponentDescriptionProp) workObject;
				if (main.resolver.satisfiedCDPs.contains(cdp)) {
					build(null, cdp);
				}
		}

	}

}

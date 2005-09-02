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
package org.eclipse.osgi.impl.service.component;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.List;

import org.eclipse.osgi.component.Main;
import org.eclipse.osgi.component.model.ComponentDescription;
import org.eclipse.osgi.component.model.ComponentDescriptionProp;
import org.eclipse.osgi.component.resolver.Reference;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentException;
import org.osgi.service.component.ComponentInstance;

/**
 * A ComponentContext object is used by a Service Component to interact with it
 * execution context including locating services by reference name.
 * 
 * <p>
 * A component's implementation class may optionally implement an activate
 * method:
 * 
 * <pre>
 * protected void activate(ComponentContext context);
 * </pre>
 * 
 * If a component implements this method, this method will be called when the
 * component is activated to provide the component's ComponentContext object.
 * 
 * <p>
 * A component's implementation class may optionally implement a deactivate
 * method:
 * 
 * <pre>
 * protected void deactivate(ComponentContext context);
 * </pre>
 * 
 * If a component implements this method, this method will be called when the
 * component is deactivated.
 * 
 * <p>
 * The activate and deactivate methods will be called using reflection and must
 * be at least protected accessible. These methods do not need to be public
 * methods so that they do not appear as public methods on the component's
 * provided service object. The methods will be located by looking through the
 * component's implementation class hierarchy for the first declaration of the
 * method. If the method is declared protected or public, the method will
 * called.
 * 
 * @version $Revision$
 */
public class ComponentContextImpl implements ComponentContext {

	/** The BundleContext this component is associated with */
	protected BundleContext		bundleContext;

	/* ComponentInstance instance */
	ComponentInstanceImpl		componentInstance;

	/* ComponentDescription plus Properties */
	protected ComponentDescriptionProp	cdp;

	protected Main						main;

	protected Bundle						usingBundle;

	/**
	 * Construct a ComponentContext object
	 * 
	 * @param bundle The ComponentDescriptionProp we are wrapping.
	 */
	public ComponentContextImpl(Main main, Bundle usingBundle,
			ComponentInstanceImpl componentInstance) {
		this.cdp = componentInstance.getComponentDescriptionProp();
		this.componentInstance = componentInstance;
		this.bundleContext = cdp.getComponentDescription().getBundleContext();
		this.usingBundle = usingBundle;
		this.main = main;
	}

	/**
	 * Returns the component properties for this ComponentContext.
	 * 
	 * @return The properties for this ComponentContext. The properties are read
	 *         only and cannot be modified.
	 */
	public Dictionary getProperties() {
		return (Dictionary) cdp.getProperties().clone();
	}

	/**
	 * Returns the service object for the specified service reference name.
	 * 
	 * @param name The name of a service reference as specified in a
	 *        <code>reference</code> element in this component's description.
	 * @return A service object for the referenced service or <code>null</code>
	 *         if the reference cardinality is <code>0..1</code> or
	 *         <code>0..n</code> and no matching service is available.
	 * @throws ComponentException If the Service Component Runtime catches an
	 *         exception while activating the target service.
	 */
	public Object locateService(String name) throws ComponentException {

		try {
			// find the Reference Description with the specified name
			Iterator references = cdp.getReferences().iterator();
			Reference thisReference = null;
			while (references.hasNext()) {
				Reference reference = (Reference) references.next();
				if (reference.getReferenceDescription().getName().equals(name)) {
					thisReference = reference;
					break;
				}
			}

			if (thisReference != null) {
				ServiceReference serviceReference = null;
				// check to see if this reference is already bound
				if (!thisReference.getServiceReferences().isEmpty()) {
					// if possible, return reference we are already bound to
					serviceReference = (ServiceReference) thisReference
							.getServiceReferences().get(0);
				}
				else {
					ServiceReference[] serviceReferences = bundleContext
							.getServiceReferences(thisReference
									.getReferenceDescription()
									.getInterfacename(), thisReference
									.getTarget());
					if (serviceReferences != null
							&& serviceReferences.length > 0) {

						// sort by service ranking and service id
						Arrays.sort(serviceReferences);

						serviceReference = serviceReferences[0];
					}

				}
				if (serviceReference != null) {
					Object serviceObject = main.resolver.instanceProcess.buildDispose
							.getService(cdp, thisReference, serviceReference);
					componentInstance.addServiceReference(serviceReference,
							serviceObject);
					return serviceObject;
				}
			}

			return null;

		}
		catch (Exception e) {
			throw new ComponentException(e);
		}

	}

	/**
	 * Returns the service object for the specified reference name and
	 * <code>ServiceReference</code>.
	 * 
	 * @param name The name of a reference as specified in a
	 *        <code>reference</code> element in this component's description.
	 * @param reference The <code>ServiceReference</code> to a specific bound
	 *        service. This must be a <code>ServiceReference</code> provided
	 *        to the component via the bind or unbind method for the specified
	 *        reference name.
	 * @return A service object for the referenced service or <code>null</code>
	 *         if the specified <code>ServiceReference</code> is not a bound
	 *         service for the specified reference name.
	 * @throws ComponentException If the Service Component Runtime catches an
	 *         exception while activating the bound service.
	 */
	public Object locateService(String name, ServiceReference serviceReference)
			throws ComponentException {
		try {
			// find the Reference Description with the specified name
			Iterator references = cdp.getReferences().iterator();
			Reference thisReference = null;
			while (references.hasNext()) {
				Reference reference = (Reference) references.next();
				if (reference.getReferenceDescription().getName().equals(name)) {
					thisReference = reference;
					break;
				}
			}

			if (thisReference != null) {
				Object serviceObject = main.resolver.instanceProcess.buildDispose
						.getService(cdp, thisReference, serviceReference);
				componentInstance.addServiceReference(serviceReference,
						serviceObject);
				return serviceObject;
			}

			return null;

		}
		catch (ComponentException e) {
			throw e;
		}
	}

	/**
	 * Returns the service objects for the specified service reference name.
	 * 
	 * @param name The name of a service reference as specified in a
	 *        <code>reference</code> element in this component's description.
	 * @return An array of service objects for the referenced service or
	 *         <code>null</code> if the reference cardinality is
	 *         <code>0..1</code> or <code>0..n</code> and no matching
	 *         service is available.
	 * @throws ComponentException If the Service Component Runtime catches an
	 *         exception while activating a target service.
	 */
	public Object[] locateServices(String name) throws ComponentException {
		try {
			// find the Reference Description with the specified name
			Iterator references = cdp.getReferences().iterator();
			Reference thisReference = null;
			while (references.hasNext()) {
				Reference reference = (Reference) references.next();
				if (reference.getReferenceDescription().getName().equals(name)) {
					thisReference = reference;
					break;
				}
			}

			if (thisReference != null) {
				ServiceReference[] serviceReferences = bundleContext
						.getServiceReferences(thisReference
								.getReferenceDescription().getInterfacename(),
								thisReference.getTarget());

				// sort by service ranking and service id
				Arrays.sort(serviceReferences);

				List serviceObjects = new ArrayList(serviceReferences.length);
				for (int counter = 0; counter < serviceReferences.length; counter++) {
					Object serviceObject = main.resolver.instanceProcess.buildDispose
							.getService(cdp, thisReference,
									serviceReferences[counter]);
					if (serviceObject != null) {
						serviceObjects.add(serviceObject);
						componentInstance.addServiceReference(
								serviceReferences[counter], serviceObject);
					}
				} // end for serviceReferences
				if (!serviceObjects.isEmpty()) {
					return serviceObjects.toArray();
				}
			}
			return null;

		}
		catch (InvalidSyntaxException e) {
			throw new ComponentException(e.getMessage());
		}
	}

	/**
	 * Returns the BundleContext of the bundle which contains this component.
	 * 
	 * @return The BundleContext of the bundle containing this component.
	 */
	public BundleContext getBundleContext() {
		return bundleContext;
	}

	/**
	 * If the component is registered as a service using the
	 * <code>servicefactory=&quot;true&quot;</code> attribute, then this
	 * method returns the bundle using the service provided by this component.
	 * <p>
	 * This method will return <code>null</code> if the component is either:
	 * <ul>
	 * <li>Not a service, then no bundle can be using it as a service.
	 * <li>Is a service but did not specify the
	 * <code>servicefactory=&quot;true&quot;</code> attribute, then all
	 * bundles will use this component.
	 * </ul>
	 * 
	 * @return The bundle using this component as a service or <code>null</code>.
	 */

	public Bundle getUsingBundle() {
		ComponentDescription cd = cdp.getComponentDescription();
		if ((cd.getService() == null) || (!cd.getService().isServicefactory())) {
			return null;
		}
		return usingBundle;
	}

	/**
	 * Returns the ComponentInstance object for this component.
	 * 
	 * @return The ComponentInstance object for this component.
	 */
	public ComponentInstance getComponentInstance() {
		return componentInstance;
	}

	/**
	 * Enables the specified component name. The specified component name must
	 * be in the same bundle as this component.
	 * 
	 * @param name The name of a component or <code>null</code> to indicate
	 *        all components in the bundle.
	 */
	public void enableComponent(String name) {
		final String componentName = name;

		AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				// privileged code goes here, for example:
				main.enableComponent(componentName, bundleContext.getBundle());
				return null; // nothing to return
			}
		});

	}

	/**
	 * Disables the specified component name. The specified component name must
	 * be in the same bundle as this component.
	 * 
	 * @param name The name of a component.
	 */
	public void disableComponent(String name) {

		final String componentName = name;

		AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				// privileged code goes here, for example:
				main.disableComponent(componentName, bundleContext.getBundle());
				return null; // nothing to return
			}
		});
	}

	/**
	 * If this component is registered as a service using the
	 * <code>service</code> element, then this method returns the service
	 * reference of the service provided by this component.
	 * <p>
	 * This method will return <code>null</code> if this component is not
	 * registered as a service.
	 * 
	 * @return The <code>ServiceReference</code> object for this component or
	 *         <code>null</code> if this component is not registered as a
	 *         service.
	 */
	public ServiceReference getServiceReference() {
		ServiceReference serviceReference = null;
		if (cdp.getComponentDescription().getService() != null) {
			ServiceRegistration serviceRegistration = cdp
					.getServiceRegistration();
			if (serviceRegistration != null) {
				serviceReference = serviceRegistration.getReference();
			}
		}
		return serviceReference;
	}

}
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

package org.eclipse.osgi.component.resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.osgi.component.model.ComponentDescriptionProp;
import org.eclipse.osgi.component.model.ReferenceDescription;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;

/**
 * 
 * Wrapper for a {@link ReferenceDescription that may have
 * a different target filter set by ConfigAdmin or ComponentFactory.newInstance()
 * 
 * @see org.eclipse.osgi.component.model.ReferenceDescription
 * @version $Revision$
 */
public class Reference {

	/* set this to true to compile in debug messages */
	static final boolean			DEBUG				= false;
	protected static final String	TARGET				= ".target";
	protected ReferenceDescription	referenceDescription;
	protected String				target;

	/**
	 * A List of {@link ServiceReference}s bound to this Reference
	 */
	protected List					serviceReferences	= new ArrayList();

	/**
	 * Create a new Reference object.
	 * 
	 * If properties include a (reference name).target property, that overrides
	 * the target in the {@link ReferenceDescription}.
	 * 
	 * @param referenceDescription
	 * @param properties Properties for this Component Configuration
	 */
	public Reference(ReferenceDescription referenceDescription,
			Hashtable properties) {
		this.referenceDescription = referenceDescription;

		//properties can override the Service Component XML
		this.target = (String) properties.get(referenceDescription.getName()
				+ TARGET);
		
		this.target = this.target != null ? this.target : referenceDescription
				.getTarget();

		// RFC 80 section 5.3.1.3:
		// If [target] is not specified and there is no <reference-name>.target
		// component
		// property, then the selection filter used to select the desired
		// service is
		// “(objectClass=”+<interface-name>+”)”.
		this.target = this.target != null ? this.target : "(objectClass="
				+ referenceDescription.getInterfacename() + ")";

	}

	/**
	 * Check if there is at least one service registered that satisfies this
	 * reference.
	 * 
	 * Checks ServicePermission.GET.
	 * 
	 * @param context Bundle context used to call 
	 *        {@link BundleContext#getServiceReferences(java.lang.String, java.lang.String)}
	 * @return whether this Reference can be satisfied by the currently registered services
	 */
	public boolean hasProvider(BundleContext context) {

		// check if this bundle has the permission to GET the Service it
		// Requires
		//TODO this may be redundant - is this checked in getServiceReferences below?
		Bundle bundle = context.getBundle();
		if (System.getSecurityManager() != null
				&& !bundle.hasPermission(new ServicePermission(
						referenceDescription.getInterfacename(),
						ServicePermission.GET))) {

			return false;
		}

		// Get all service references for this target filter
		try {
			ServiceReference[] providers = null;
			providers = context.getServiceReferences(
					referenceDescription.getInterfacename(), target);
			// if there is no service published that this Service
			// ComponentReferences
			if (providers != null) {
				return true;
			}
			return false;
		}
		catch (InvalidSyntaxException e) {
			//TODO log something?
			return false;
		}
	}

	public ReferenceDescription getReferenceDescription() {
		return referenceDescription;
	}

	public String getTarget() {
		return target;
	}

	/**
	 * Check if a {@link ServiceReference} should be dynamically bound to this 
	 * Reference.
	 * 
	 * @param serviceReference
	 */
	public boolean dynamicBindReference(ServiceReference serviceReference) {
		
		//check policy
		if ("static".equals(referenceDescription.getPolicy())) {
			return false;
		}
		
		//check interface
		List provideList = Arrays.asList((String[]) (serviceReference
				.getProperty("objectClass")));
		if (!provideList.contains(this.getReferenceDescription()
				.getInterfacename())) {
			return false;
		}
		
		//check target filter
		Filter filter;
		try {
			filter = FrameworkUtil.createFilter(target);
		}
		catch (InvalidSyntaxException e) {
			//TODO log something?
			return false;
		}
		if (!filter.match(serviceReference)) {
			return false;
		}

		//check cardinality
		int currentRefCount = serviceReferences.size();
		if (currentRefCount < referenceDescription.getCardinalityHigh()) {
			return true;
		}
		return false;

	}

	/**
	 * Check if we need to be dynamically unbound from a {@link ServiceReference}
	 * 
	 * @param serviceReference
	 */
	public boolean dynamicUnbindReference(ServiceReference serviceReference) {

		// nothing dynamic to do if static
		if ("static".equals(referenceDescription.getPolicy())) {
			return false;
		}

		// now check if the ServiceReference is found in the list of saved
		// ServiceReferences for this reference
		if (!serviceReferences.contains(serviceReference)) {
			return false;
		}

		return true;

	}

	public void addServiceReference(ServiceReference serviceReference) {
		serviceReferences.add(serviceReference);
	}

	public void removeServiceReference(ServiceReference serviceReference) {
		serviceReferences.remove(serviceReference);
	}

	public void clearServiceReferences() {
		serviceReferences.clear();
	}

	public List getServiceReferences() {
		return serviceReferences;
	}

	public boolean bindedToServiceReference(ServiceReference serviceReference) {
		return serviceReferences.contains(serviceReference);
	}

	/**
	 * Check if this reference can be satisfied by the service provided by one
	 * of a list of Component Configurations
	 * 
	 * @param cdps a List of {@link ComponentDescriptionProp}s to search for providers
	 * for this reference
	 * @return the providing CDP or null if none
	 */
	public ComponentDescriptionProp findProviderCDP(List cdps) {

		Filter filter;
		try {
			filter = FrameworkUtil.createFilter(target);
		}
		catch (InvalidSyntaxException e) {
			//TODO log something?
			return null;
		}

		
		// loop thru cdps to search for provider of service
		Iterator it = cdps.iterator();
		while (it.hasNext()) {
			ComponentDescriptionProp cdp = (ComponentDescriptionProp) it.next();
			List provideList = cdp.getComponentDescription()
					.getServicesProvided();

			if (provideList.contains(this.getReferenceDescription()
					.getInterfacename())) {
				// check the target field
				if(filter.match(cdp.getProperties())) {
					return cdp;
				}
			}
		}

		return null;
	}

}

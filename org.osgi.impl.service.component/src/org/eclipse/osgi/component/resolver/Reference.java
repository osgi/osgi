/*
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

import java.util.*;

import org.eclipse.osgi.component.model.*;
import org.osgi.framework.*;

/**
 * 
 * Wrapper for a References Description includes eligible state
 * 
 * @version $Revision$
 */
public class Reference {

	/* set this to true to compile in debug messages */
	static final boolean DEBUG = false;
	static final String TARGET = ".target";
	static final String CARDINALITY_DEFAULT = "1..1";
	static final String POLICY_DEFAULT = "static";
	protected ReferenceDescription referenceDescription;
	protected String target;
	protected String cardinality;
	protected String policy;
	protected int cardinalityHigh;
	protected int cardinalityLow;

	protected List serviceReferences = new ArrayList();

	/**
	 * Reference object
	 * 
	 * @param referenceDescription
	 */
	public Reference(ReferenceDescription referenceDescription, Dictionary properties) {
		this.referenceDescription = referenceDescription;
		this.target = referenceDescription.getTarget();

		// RFC 80 section 5.3.1.3:
		// If [target] is not specified and there is no <reference-name>.target
		// component
		// property, then the selection filter used to select the desired
		// service is
		// “(objectClass=”+<interface-name>+”)”.
		if (properties != null) {
			String newTarget = (String) properties.get(referenceDescription.getName() + TARGET);
			if (newTarget != null) {
				this.target = newTarget;
			}
		}
		if (target == null) {
			target = "(objectClass=" + referenceDescription.getInterfacename() + ")";
		}

		// If it is not specified, then a policy of “static” is used.
		policy = referenceDescription.getPolicy();
		if (policy == null) {
			policy = POLICY_DEFAULT;
		}

		// Cardinality indicates the number of services, matching this
		// reference,
		// which will bind to this Service Component. Possible values are:
		// 0..1, 0..n, 1..1 (i.e. exactly one), 1..n (i.e. at least one).
		// This attribute is optional. If it is not specified, then a
		// cardinality
		// of “1..1” is used.
		cardinality = referenceDescription.getCardinality();
		if (cardinality == null) {
			cardinality = CARDINALITY_DEFAULT;
		}
		if (cardinality.charAt(0) == '0')
			cardinalityLow = 0;
		else
			cardinalityLow = 1;
		if (cardinality.charAt(3) == '1')
			cardinalityHigh = 1;
		else
			cardinalityHigh = 999999999; //infinite
	}

	/**
	 * hasLegacyProviders
	 * 
	 * @param scrBundleContext
	 */
	public boolean hasLegacyProviders(BundleContext scrBundleContext) {

		// Get all service references for this target filter
		try {
			ServiceReference[] serviceReferences = null;
			serviceReferences = scrBundleContext.getServiceReferences(referenceDescription.getInterfacename(), target);
			// if there is no service published that this Service ComponentReferences
			if (serviceReferences != null) {
				return true;
			}
			return false;
		} catch (InvalidSyntaxException e) {
			System.out.print("Filter " + target + " is invalid.  Error:");
			System.out.println(e.getMessage());
			return false;
		}
	}

	public ReferenceDescription getReferenceDescription() {
		return referenceDescription;
	}

	public String getTarget() {
		return target;
	}

	public String getPolicy() {
		return policy;
	}

	//	if the cardinality is "0..1" or "0..n" then this refernce is not required
	public boolean isRequiredFor(ComponentDescription cd) {
		//we want to re-resolve if the component is static and already eligible
		if (policy.equals("static") && cd.isEligible())
			return true;
		return (cardinality.charAt(0) == '1');
	}

	public boolean bindNewReference(ServiceReference reference) {
		if ("static".equals(policy)) {
			return false;
		}
		String[] serviceName = (String[]) (reference.getProperty("objectClass"));
		if (!serviceName[0].equals(referenceDescription.getInterfacename())) {
			return false;
		}
		int currentRefCount = serviceReferences.size();
		if (currentRefCount < cardinalityHigh) {
			return true;
		}
		return false;

	}

	public boolean unBindReference(ServiceReference serviceReference) {

		// nothing dynamic to do if static
		if ("static".equals(policy)) {
			return false;
		}

		//first compare the service name and the interface name
		String[] serviceName = (String[]) (serviceReference.getProperty("objectClass"));
		if (!serviceName[0].equals(referenceDescription.getInterfacename())) {
			return false;
		}

		//now check if the ServiceReference is found in the list of saved ServiceReferences for this reference 
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

	public List getServiceReferences() {
		return serviceReferences;
	}

	public boolean bindedToServiceReference(ServiceReference serviceReference) {
		return serviceReferences.contains(serviceReference);
	}

	public boolean matchProperties(ComponentDescriptionProp cdp, BundleContext context) {
		if (target == null) {
			return true;
		}
		Dictionary properties = cdp.getProperties();
		Filter filter;
		try {
			filter = context.createFilter(target);
			return filter.match(properties);
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false; //TODO - is this correct?
		}
	}

	/**
	 * Check if this reference can be satisfied for a particular CDP and set of enabled CDPs
	 * 
	 * @param cdp
	 * @param bc the bundle context of the cdp
	 * @param enabledCDPs the list of enabled cdps
	 * @return true if the reference can be resolved
	 */
	public boolean resolve(ComponentDescriptionProp cdp, BundleContext bc, List enabledCDPs) {
		//if the cardinality is "0..1" or "0..n" then this refernce is not required
		//TODO - how do we place this reference in the ordering?
		if (!this.isRequiredFor(cdp.getComponentDescription()))
			return true;

		if (this.hasLegacyProviders(bc))
			return true; //we found an availible provider in the legacy services

		// loop thru all components to match providers of services
		Iterator it = enabledCDPs.iterator();
		while (it.hasNext()) {
			ComponentDescriptionProp cdpRefLookup = (ComponentDescriptionProp) it.next();
			List provideList = cdpRefLookup.getServicesProvided();

			if (provideList.contains(this.getReferenceDescription().getInterfacename())) {
				//check the target field
				if (this.matchProperties(cdpRefLookup, bc)) {
					cdp.setReferenceCDP(cdpRefLookup);
					return true;
				}
			}
		}

		return false;
	}

}

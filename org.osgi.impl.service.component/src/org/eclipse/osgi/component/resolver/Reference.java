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

package org.eclipse.osgi.component.resolver;

import java.util.Dictionary;
import org.eclipse.osgi.component.model.ComponentDescription;
import org.eclipse.osgi.component.model.ReferenceDescription;
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
	protected String interfaceName;
	protected String target;
	protected String cardinality;
	protected String policy;
	protected String name;
	protected int cardinalityHigh;
	protected int cardinalityLow;

	/**
	 * Reference object
	 * 
	 * @param referenceDescription
	 */
	public Reference(ReferenceDescription referenceDescription, Dictionary properties) {
		this.referenceDescription = referenceDescription;
		this.interfaceName = referenceDescription.getInterfacename();
		this.target = referenceDescription.getTarget();
		this.name = referenceDescription.getName();

		// RFC 80 section 5.3.1.3:
		// If [target] is not specified and there is no <reference-name>.target
		// component
		// property, then the selection filter used to select the desired
		// service is
		// “(objectClass=”+<interface-name>+”)”.
		if (properties != null)
			target = (String) properties.get(name + TARGET);
		if (target == null) {
			target = "(objectClass=" + interfaceName + ")";
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
			serviceReferences = scrBundleContext.getServiceReferences(interfaceName, target);
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

	/**
	 * Return the interface name
	 * 
	 * @return
	 */
	public String getInterfaceName() {
		return interfaceName;
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
		if (!serviceName[0].equals(interfaceName)) {
			return false;
		}
		int currentRefCount = referenceDescription.getServiceObjects().length;
		if (currentRefCount < cardinalityHigh) {
			return true;
		}
		return false;

	}

	public boolean unBindReference(BundleContext bundleContext, ServiceReference reference) {

		// nothing dynamic to do if static
		if ("static".equals(policy)) {
			return false;
		}

		//first compare the service name and the interface name
		String[] serviceName = (String[]) (reference.getProperty("objectClass"));
		if (!serviceName[0].equals(interfaceName)) {
			return false;
		}

		//now check if the Service Object is found in the list of saved ServiceObjects for this reference 
		Object serviceObject = bundleContext.getService(reference);
		if (!referenceDescription.containsServiceObject(serviceObject)) {
			return false;
		}

		return true;

	}
}

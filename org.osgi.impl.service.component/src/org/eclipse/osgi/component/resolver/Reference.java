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

import java.util.ArrayList;
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
 * Wrapper for a References Description includes satisfied state
 * 
 * @version $Revision$
 */
public class Reference {

	/* set this to true to compile in debug messages */
	static final boolean DEBUG = false;
	static final String TARGET = ".target";
	protected ReferenceDescription referenceDescription;
	protected String target;
	
	protected List serviceReferences = new ArrayList();

	/**
	 * Reference object
	 * 
	 * @param referenceDescription
	 */
	public Reference(ReferenceDescription referenceDescription, Hashtable properties) {
		this.referenceDescription = referenceDescription;
		
		// RFC 80 section 5.3.1.3:
		// If [target] is not specified and there is no <reference-name>.target
		// component
		// property, then the selection filter used to select the desired
		// service is
		// “(objectClass=”+<interface-name>+”)”.
		this.target = (String) properties.get(referenceDescription.getName() + TARGET);
		this.target = this.target != null ? this.target : referenceDescription.getTarget();
		this.target = this.target != null ? this.target : "(objectClass=" + referenceDescription.getInterfacename() + ")";
		
	}

	/**
	 * hasProviders
	 * 
	 * @param scrBundleContext
	 */
	public boolean hasProvider(BundleContext context) {

		//check if this bundle has the permission to GET the Service it Requires
		Bundle bundle = context.getBundle();
		if (System.getSecurityManager() != null &&
			!bundle.hasPermission(new ServicePermission(referenceDescription.getInterfacename(), ServicePermission.GET))) {

			return false;
		}

		// Get all service references for this target filter
		try {
			ServiceReference[] serviceReferences = null;
			serviceReferences = context.getServiceReferences(referenceDescription.getInterfacename(),target);
			// if there is no service published that this Service ComponentReferences
			if (serviceReferences != null) {
				return true;
			}
			return false;
		} catch (InvalidSyntaxException e) {
			//won't ever happen because filter is null;
			return false;
		}
	}

	public ReferenceDescription getReferenceDescription() {
		return referenceDescription;
	}

	public String getTarget() {
		return target;
	}

	public boolean dynamicBindReference(ServiceReference serviceReference) {
		if ("static".equals(referenceDescription.getPolicy())) {
			return false;
		}
		String[] serviceName = (String[]) (serviceReference.getProperty("objectClass"));
		if (!serviceName[0].equals(referenceDescription.getInterfacename())) {
			return false;
		}
		int currentRefCount = serviceReferences.size();
		if (currentRefCount < referenceDescription.getCardinalityHigh()) {
			return true;
		}
		return false;

	}

	public boolean dynamicUnbindReference(ServiceReference serviceReference) {

		// nothing dynamic to do if static
		if ("static".equals(referenceDescription.getPolicy())) {
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
	
	public void clearServiceReferences() {
		serviceReferences.clear();
	}

	public List getServiceReferences() {
		return serviceReferences;
	}

	public boolean bindedToServiceReference(ServiceReference serviceReference) {
		return serviceReferences.contains(serviceReference);
	}

	public boolean matchProperties(ComponentDescriptionProp cdp) {
		Dictionary properties = cdp.getProperties();
		Filter filter;
		try {
			filter = FrameworkUtil.createFilter(target);
			return filter.match(properties);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Check if this reference can be satisfied by a CDP in a set of 
	 * enabled CDPs
	 * 
	 * @param enabledCDPs the list of enabled cdps
	 * @return the providing CDP or null if none
	 */
	public ComponentDescriptionProp findProviderCDP(List enabledCDPs) {

		// loop thru all enabled cdps to match providers of services
		Iterator it = enabledCDPs.iterator();
		while (it.hasNext()) {
			ComponentDescriptionProp cdpRefLookup = (ComponentDescriptionProp) it.next();
			List provideList = cdpRefLookup.getComponentDescription().getServicesProvided();

			if (provideList.contains(this.getReferenceDescription().getInterfacename())) {
				//check the target field
				if (matchProperties(cdpRefLookup)) {
					return cdpRefLookup;
				}
			}
		}

		return null;
	}

}

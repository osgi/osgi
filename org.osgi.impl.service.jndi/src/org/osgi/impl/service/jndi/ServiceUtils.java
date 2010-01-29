/*
 * Copyright 2009 Oracle Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


package org.osgi.impl.service.jndi;

import java.util.Arrays;
import java.util.Comparator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jndi.JNDIConstants;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This class holds utility methods for handling OSGi services/service references
 *
 * 
 * 
 * @version $Revision$
 */
class ServiceUtils {
	/* private constructor, static utility class */
	private ServiceUtils() {}

	/**
	 * Utility method to sort an array of ServiceReferences using the service
	 * ranking (if specified).
	 * 
	 * This utility should follow any service ranking rules already defined in
	 * the OSGi specification.
	 * 
	 * @param serviceTracker tracker to use to provide the initial array to sort
	 * @return sorted array of ServiceReferences, or a zero-length array if no
	 *         matching services were found
	 */
	static ServiceReference[] sortServiceReferences(ServiceTracker serviceTracker) {
		final ServiceReference[] serviceReferences = serviceTracker
				.getServiceReferences();
		if (serviceReferences == null) {
			return new ServiceReference[0];
		}
	
		return sortServiceReferences(serviceReferences);
	}

	
	/**
	 * Utility method to sort an array of ServiceReferences using the OSGi
	 * service ranking.  
	 * 
	 * This utility should follow any service ranking rules already defined in
	 * the OSGi specification.
	 * 
	 * 
	 * @param serviceReferences an array of ServiceReferences to sort
	 * @return the array of ServiceReferences passed into this method, but sorted 
	 *         according to OSGi service ranking.  
	 */
	static ServiceReference[] sortServiceReferences(
			final ServiceReference[] serviceReferences) {
		Arrays.sort(serviceReferences, new Comparator() {
			public int compare(Object objectOne, Object objectTwo) {
				ServiceReference serviceReferenceOne = (ServiceReference) objectOne;
				ServiceReference serviceReferenceTwo = (ServiceReference) objectTwo;
				return serviceReferenceTwo.compareTo(serviceReferenceOne);
			}
		});

		return serviceReferences;
	}

	static ServiceReference[] getServiceReferencesByServiceName(BundleContext bundleContext, OSGiURLParser urlParser)
			throws InvalidSyntaxException {
		final String serviceNameFilter = "("
				+ JNDIConstants.JNDI_SERVICENAME + "="
				+ urlParser.getServiceInterface() + ")";
		ServiceReference[] serviceReferencesByName = 
			bundleContext.getServiceReferences(null, serviceNameFilter);
		return serviceReferencesByName;
	}
	
}

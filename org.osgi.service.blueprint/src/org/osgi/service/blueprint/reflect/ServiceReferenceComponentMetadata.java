/*
 * Copyright (c) OSGi Alliance (2000, 2008). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.service.blueprint.reflect;

import java.util.Collection;
import java.util.Set;


/**
 * Metadata describing a reference to a service that is to be imported into the module
 * context from the OSGi service registry.
 */
public interface ServiceReferenceComponentMetadata extends ComponentMetadata {

	/**
	 * A matching service is required at all times.
	 */
	public static final int MANDATORY_AVAILABILITY = 1;
	
	/**
	 * A matching service is not required to be present.
	 */
	public static final int OPTIONAL_AVAILABILITY = 2;
	
	/**
	 * Whether or not a matching service is required at all times.
	 * 
	 * @return one of MANDATORY_AVAILABILITY or OPTIONAL_AVAILABILITY
	 */
	int getServiceAvailabilitySpecification();
	
	/**
	 * The interface types that the matching service must support
	 * 
	 * @return an immutable set of type names
	 */
	Set getInterfaceNames();
	
	/**
	 * The value of the component name attribute, if specified.
	 *
	 * @return the component name attribute value, or null if the attribute was not specified
	 */
	String getComponentName();
	
	/**
	 * The filter expression that a matching service must pass
	 * 
	 * @return filter expression
	 */
	String getFilter();

	/**
	 * The set of listeners registered to receive bind and unbind events for
	 * backing services.
	 * 
	 * @return an immutable collection of registered BindingListenerMetadata
	 */
	Collection /*<BindingListenerMetadata>*/ getBindingListeners();
	
}

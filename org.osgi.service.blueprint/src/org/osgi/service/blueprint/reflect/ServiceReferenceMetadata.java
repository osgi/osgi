/*
 * Copyright (c) OSGi Alliance (2008, 2009). All Rights Reserved.
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
import java.util.List;


/**
 * Metadata describing a reference to a service that is to be imported into the module
 * context from the OSGi service registry.
 *
 */
public interface ServiceReferenceMetadata extends ComponentMetadata {

	/**
	 * A matching service is required at all times.
	 */
	public static final int AVAILABILITY_MANDATORY = 1;

	/**
	 * A matching service is not required to be present.
	 */
	public static final int AVAILABILITY_OPTIONAL = 2;

	/**
	 * Whether or not a matching service is required at all times.
	 *
	 * Defined in the <code>availibility</code> attribute.
	 *
	 * @return one of AVAILABILITY_MANDATORY or AVAILABILITY_OPTIONAL
	 */
	int getAvailability();

	/**
	 * The interface types that the matching service must support
	 *
	 * Defined in the <code>interface</code> attribute or <code>interfaces</code> element.
	 *
	 * @return an immutable list of type names
	 */
	List/*<String>*/ getInterfaceNames();

	/**
	 * The value of the component-name attribute, if specified. This specifies
	 * the name of a component that is registered in the service registry. This will
	 * create an automatic filter (appended wit the filter if set) to select this
	 * component based on its automatic ###name attribute.
	 *
	 * Defined in the <code>component-name</code> attribute.
	 *
	 * @return the component name attribute value, or null if the attribute was not specified
	 */
	String getComponentName();

	/**
	 * The filter expression that a matching service must pass
	 *
	 * Defined in the <code>filter</code> attribute.
	 *
	 * @return filter expression
	 */
	String getFilter();

	/**
	 * The set of listeners registered to receive bind and unbind events for
	 * backing services.
	 *
	 * Defined in the <code>listener</code> elements.
	 *
	 * @return an immutable collection of registered ListenerMetadata
	 */
	Collection /*<Listener>*/ getServiceListeners();

}

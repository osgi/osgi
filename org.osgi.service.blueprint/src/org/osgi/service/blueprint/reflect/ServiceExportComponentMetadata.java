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
import java.util.Map;
import java.util.Set;


/**
 * Metadata representing a service to be exported by a module context.
 *
 */
public interface ServiceExportComponentMetadata extends ComponentMetadata {

	/**
	 * Do not auto-detect types for advertised service intefaces
	 */
	public static final int EXPORT_MODE_DISABLED = 1;
	
	/**
	 * Advertise all Java interfaces implemented by the exported component as
	 * service interfaces.
	 */
	public static final int EXPORT_MODE_INTERFACES= 2;
	
	/**
	 * Advertise all Java classes in the hierarchy of the exported component's type
	 * as service interfaces.
	 */
	public static final int EXPORT_MODE_CLASS_HIERARCHY = 3;
	
	/**
	 * Advertise all Java classes and interfaces in the exported component's type as 
	 * service interfaces.
	 */
	public static final int EXPORT_MODE_ALL = 4;
	
	/**
	 * The component that is to be exported as a service. Value must refer to a component and
	 * therefore be either a ComponentValue or ReferenceValue.
	 * 
	 * @return the component to be exported as a service.
	 */
	Value getExportedComponent();
	
	/**
	 * The type names of the set of interface types that the service should be advertised
	 * as supporting, as specified in the component declaration.
	 * 
	 * @return an immutable set of (String) type names, or an empty set if using auto-export
	 */
	Set getInterfaceNames();
	
	/**
	 * Return the auto-export mode specified.
	 * 
	 * @return One of EXPORT_MODE_DISABLED, EXPORT_MODE_INTERFACES, EXPORT_MODE_CLASS_HIERARCHY, EXPORT_MODE_ALL
	 */
	int getAutoExportMode();
	
	/**
	 * The user declared properties to be advertised with the service.
	 * 
	 * @return Map containing the set of user declared service properties (may be
	 * empty if no properties were specified).
	 */
	Map getServiceProperties();

	/**
	 * The ranking value to use when advertising the service
	 *  
	 * @return service ranking
	 */
	int getRanking();
	
	/**
	 * The listeners that have registered to be notified when the exported service
	 * is registered and unregistered with the framework.
	 * 
	 * @return an immutable collection of RegistrationListenerMetadata
	 */
	Collection /*<RegistrationListenerMetadata>*/ getRegistrationListeners();
	
}

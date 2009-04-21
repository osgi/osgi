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
 * Metadata representing a service to be exported by a module context.
 *
 */
public interface ServiceMetadata extends ComponentMetadata, Target {

	/**
	 * Do not auto-detect types for advertised service intefaces
	 */
	public static final int AUTO_EXPORT_DISABLED = 1;

	/**
	 * Advertise all Java interfaces implemented by the exported component as
	 * service interfaces.
	 */
	public static final int AUTO_EXPORT_INTERFACES= 2;

	/**
	 * Advertise all Java classes in the hierarchy of the exported component's type
	 * as service interfaces.
	 */
	public static final int AUTO_EXPORT_CLASS_HIERARCHY = 3;

	/**
	 * Advertise all Java classes and interfaces in the exported component's type as
	 * service interfaces.
	 */
	public static final int AUTO_EXPORT_ALL_CLASSES = 4;

	/**
	 * The component that is to be exported as a service. Value must refer to a component and
	 * therefore be either a RefMetadata or BeanMetadata.
	 *
	 * Defined in the <code>registration-method</code> attribute.
	 *
	 * @return the component to be exported as a service.
	 */
	Target getServiceComponent();

	/**
	 * The type names of the set of interface types that the service should be advertised
	 * as supporting, as specified in the component declaration.
	 *
	 * Defined in the <code>interface</code> attribute or <code>interfaces</code> element.
	 *
	 * @return an immutable set of (String) type names, or an empty set if using auto-export
	 */
	List/*<String>*/ getInterfaceNames();

	/**
	 * Return the auto-export mode specified.
	 *
	 * Defined in the <code>auto-export</code> attribute.
	 *
	 * @return One of AUTO_EXPORT_DISABLED, AUTO_EXPORT_INTERFACES, AUTO_EXPORT_CLASS_HIERARCHY, AUTO_EXPORT_ALL_CLASSES
	 */
	int getAutoExportMode();

	/**
	 * The user declared properties to be advertised with the service.
	 *
	 * Defined in the <code>service-properties</code> element.
	 *
	 * @return Map containing the set of user declared service properties (may be
	 * empty if no properties were specified).
	 */
	List/*<MapEntry>*/ getServiceProperties();

	/**
	 * The ranking value to use when advertising the service
	 *
	 * Defined in the <code>ranking</code> attribute.
	 *
	 * @return service ranking
	 */
	int getRanking();

	/**
	 * The listeners that have registered to be notified when the exported service
	 * is registered and unregistered with the framework.
	 *
	 * Defined in the <code>registration-listener</code> elements.
	 *
	 * @return an immutable collection of RegistrationListenerMetadata
	 */
	Collection /*<RegistrationListener>*/ getRegistrationListeners();

    /**
     * The names of any components listed in a "depends-on" attribute for this
     * component.
     *
     * @return an immutable List of component names for components that we have explicitly
     * declared a dependency on, or an empty set if none.
     */
    List/*<String>*/ getExplicitDependencies();
}

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
	 * Do not auto-detect types for advertised service interfaces
	 * 
	 * @see #getAutoExport()
	 */
	public static final int AUTO_EXPORT_DISABLED = 1;

	/**
	 * Advertise all Java interfaces implemented by the exported component as
	 * service interfaces.
	 * 
	 * @see #getAutoExport()
	 */
	public static final int AUTO_EXPORT_INTERFACES = 2;

	/**
	 * Advertise all Java classes in the hierarchy of the exported component's
	 * type as service interfaces.
	 * 
	 * @see #getAutoExport()
	 */
	public static final int AUTO_EXPORT_CLASS_HIERARCHY = 3;

	/**
	 * Advertise all Java classes and interfaces in the exported component's
	 * type as service interfaces.
	 * 
	 * @see #getAutoExport()
	 */
	public static final int AUTO_EXPORT_ALL_CLASSES = 4;

	/**
	 * The component that is to be exported as a service. Value must refer to a
	 * component and therefore be either a {@link RefMetadata},
	 * {@link BeanMetadata}, or {@link ReferenceMetadata}.
	 * 
	 * Defined in the <code>registration-method</code> attribute.
	 * 
	 * @return the component to be exported as a service.
	 */
	Target getServiceComponent();

	/**
	 * The type names of the set of interface types that the service should be
	 * advertised as supporting, as specified in the component declaration.
	 * 
	 * Defined in the <code>interface</code> attribute or
	 * <code>interfaces</code> element.
	 * 
	 * @return an immutable set of (<code>String</code>) type names, or an
	 *         empty set if using auto-export
	 */
	List/* <String> */getInterfaces();

	/**
	 * Return the auto-export mode specified.
	 * 
	 * Defined in the <code>auto-export</code> attribute.
	 * 
	 * @return One of {@link #AUTO_EXPORT_DISABLED},
	 *         {@link #AUTO_EXPORT_INTERFACES},
	 *         {@link #AUTO_EXPORT_CLASS_HIERARCHY},
	 *         {@link #AUTO_EXPORT_ALL_CLASSES}
	 */
	int getAutoExport();

	/**
	 * The user declared properties to be advertised with the service.
	 * 
	 * Defined in the <code>service-properties</code> element.
	 * 
	 * @return <code>Map</code> containing the set of user declared service
	 *         properties (may be empty if no properties were specified).
	 */
	List/* <MapEntry> */getServiceProperties();

	/**
	 * The ranking value to use when advertising the service. If this value is
	 * zero, no ranking service property must be registered.
	 * 
	 * Defined in the <code>ranking</code> attribute.
	 * 
	 * @return service ranking
	 */
	int getRanking();

	/**
	 * The listeners that have registered to be notified when the exported
	 * service is registered and unregistered with the framework.
	 * 
	 * Defined in the <code>registration-listener</code> elements.
	 * 
	 * @return an immutable collection of RegistrationListenerMetadata
	 */
	Collection /* <RegistrationListener> */getRegistrationListeners();
}

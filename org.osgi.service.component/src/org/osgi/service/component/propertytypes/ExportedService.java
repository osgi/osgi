/*
 * Copyright (c) OSGi Alliance (2016, 2017). All Rights Reserved.
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

package org.osgi.service.component.propertytypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ComponentPropertyType;

/**
 * Component Property Type for the remote service properties for an exported
 * service.
 * <p>
 * This annotation can be used on a {@link Component} to declare the values of
 * the remote service properties for an exported service.
 * 
 * @see "Component Property Types"
 * @see "Remote Services Specification"
 * @author $Id$
 * @since 1.4
 */
@ComponentPropertyType
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface ExportedService {
	/**
	 * Service property marking the service for export. It defines the
	 * interfaces under which the service can be exported.
	 * <p>
	 * If an empty array is specified, the property is not added to the
	 * component description.
	 * 
	 * @return The exported service interfaces.
	 * @see Constants#SERVICE_EXPORTED_INTERFACES
	 */
	Class< ? >[] service_exported_interfaces();

	/**
	 * Service property identifying the configuration types that should be used
	 * to export the service.
	 * <p>
	 * If an empty array is specified, the default value, the property is not
	 * added to the component description.
	 * 
	 * @return The configuration types.
	 * @see Constants#SERVICE_EXPORTED_CONFIGS
	 */
	String[] service_exported_configs() default {};

	/**
	 * Service property identifying the intents that the distribution provider
	 * must implement to distribute the service.
	 * <p>
	 * If an empty array is specified, the default value, the property is not
	 * added to the component description.
	 * 
	 * @return The intents that the distribution provider must implement to
	 *         distribute the service.
	 * @see Constants#SERVICE_EXPORTED_INTENTS
	 */
	String[] service_exported_intents() default {};

	/**
	 * Service property identifying the extra intents that the distribution
	 * provider must implement to distribute the service.
	 * <p>
	 * If an empty array is specified, the default value, the property is not
	 * added to the component description.
	 * 
	 * @return The extra intents that the distribution provider must implement
	 *         to distribute the service.
	 * @see Constants#SERVICE_EXPORTED_INTENTS_EXTRA
	 */
	String[] service_exported_intents_extra() default {};

	/**
	 * Service property identifying the intents that the distribution provider
	 * must implement to distribute the service.
	 * <p>
	 * If an empty array is specified, the default value, the property is not
	 * added to the component description.
	 * 
	 * @return The intents that the service implements.
	 * @see Constants#SERVICE_INTENTS
	 */
	String[] service_intents() default {};
}

/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.service.jakartars.whiteboard.propertytypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.osgi.service.component.annotations.ComponentPropertyType;

/**
 * Component Property Type for the {@code osgi.jakartars.application.select}
 * service property.
 * <p>
 * This annotation can be used on a Jakarta RESTful Web Services whiteboard
 * resource or extension service to declare the value of the
 * {@link org.osgi.service.jakartars.whiteboard.JakartarsWhiteboardConstants#JAKARTA_RS_APPLICATION_SELECT}
 * service property.
 * 
 * @see "Component Property Types"
 * @author $Id$
 */
@ComponentPropertyType
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface JakartarsApplicationSelect {
	/**
	 * Prefix for the property name. This value is prepended to each property
	 * name.
	 */
	String PREFIX_ = "osgi.";

	/**
	 * Service property providing a OSGi filter(s) identifying the
	 * application(s) to which this service should be bound.
	 * 
	 * @return The filter(s) for selecting the application(s) to bind to.
	 * @see org.osgi.service.jakartars.whiteboard.JakartarsWhiteboardConstants#JAKARTA_RS_APPLICATION_SELECT
	 */
	String[] value();
}

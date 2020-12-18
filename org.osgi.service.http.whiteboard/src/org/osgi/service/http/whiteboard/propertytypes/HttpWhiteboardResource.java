/*
 * Copyright (c) OSGi Alliance (2017, 2020). All Rights Reserved.
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

package org.osgi.service.http.whiteboard.propertytypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.osgi.service.component.annotations.ComponentPropertyType;
import org.osgi.service.http.whiteboard.annotations.RequireHttpWhiteboard;

/**
 * Component Property Type for the {@code osgi.http.whiteboard.resource.pattern}
 * and {@code osgi.http.whiteboard.resource.prefix} service properties.
 * <p>
 * This annotation can be used on any service to declare the values of the
 * {@link org.osgi.service.http.whiteboard.HttpWhiteboardConstants#HTTP_WHITEBOARD_RESOURCE_PATTERN
 * HTTP_WHITEBOARD_RESOURCE_PATTERN} and
 * {@link org.osgi.service.http.whiteboard.HttpWhiteboardConstants#HTTP_WHITEBOARD_RESOURCE_PREFIX
 * HTTP_WHITEBOARD_RESOURCE_PREFIX} service properties.
 * 
 * @see "Component Property Types"
 * @author $Id$
 * @since 1.1
 */
@ComponentPropertyType
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@RequireHttpWhiteboard
public @interface HttpWhiteboardResource {
	/**
	 * Prefix for the property name. This value is prepended to each property
	 * name.
	 */
	String PREFIX_ = "osgi.http.whiteboard.resource.";

	/**
 	 * Service property specifying the request mappings for resources.
         * The specified patterns are used to determine whether a request should be mapped 
	 * to resources.
	 * 
	 * @return The resource patterns.
	 * @see org.osgi.service.http.whiteboard.HttpWhiteboardConstants#HTTP_WHITEBOARD_RESOURCE_PATTERN
	 *      HTTP_WHITEBOARD_RESOURCE_PATTERN
	 */
	String[] pattern();

	/**
	 * Service property specifying the resource entry prefix for a resource service. This prefix is
	 * used to map a requested resource to the bundle's entries. The value must not end with slash 
	 * ("/") with the exception that a name of the form "/" is used to denote the root of the bundle. 
	 * 
	 * @return The resource prefix.
	 * @see org.osgi.service.http.whiteboard.HttpWhiteboardConstants#HTTP_WHITEBOARD_RESOURCE_PREFIX
	 *      HTTP_WHITEBOARD_RESOURCE_PREFIX
	 */
	String prefix();
}

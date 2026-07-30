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

package org.osgi.service.jdkhttp.whiteboard.propertytypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.osgi.service.component.annotations.ComponentPropertyType;
import org.osgi.service.jdkhttp.whiteboard.JdkHttpWhiteboardConstants;
import org.osgi.service.jdkhttp.whiteboard.annotations.RequireJdkHttpWhiteboard;

/**
 * Component Property Type for the {@code osgi.http.jdk.resource.pattern} and
 * {@code osgi.http.jdk.resource.prefix} service properties.
 * <p>
 * This annotation can be used on any component to declare the values of the
 * {@link JdkHttpWhiteboardConstants#JDK_HTTP_RESOURCE_PATTERN
 * JDK_HTTP_RESOURCE_PATTERN} and
 * {@link JdkHttpWhiteboardConstants#JDK_HTTP_RESOURCE_PREFIX
 * JDK_HTTP_RESOURCE_PREFIX} service properties.
 *
 * @see "Component Property Types"
 * @author $Id$
 */
@ComponentPropertyType
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@RequireJdkHttpWhiteboard
public @interface HttpJdkResource {
	/**
	 * Prefix for the property name. This value is prepended to each property
	 * name.
	 */
	String PREFIX_ = "osgi.http.jdk.resource.";

	/**
	 * Service property specifying the context paths at which the resources
	 * are served.
	 *
	 * @return The resource patterns. Each must start with {@code /}.
	 * @see JdkHttpWhiteboardConstants#JDK_HTTP_RESOURCE_PATTERN
	 */
	String[] pattern();

	/**
	 * Service property specifying the bundle entry prefix from which the
	 * resources are served.
	 *
	 * @return The resource prefix. Must not end with {@code /}, except for
	 *         {@code "/"} denoting the root of the bundle.
	 * @see JdkHttpWhiteboardConstants#JDK_HTTP_RESOURCE_PREFIX
	 */
	String prefix();
}

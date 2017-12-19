/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
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
 * Component Property Type for the {@code osgi.http.whiteboard.context.name} and
 * {@code osgi.http.whiteboard.context.path} service properties.
 * <p>
 * This annotation can be used on a
 * {@link org.osgi.service.http.context.ServletContextHelper
 * ServletContextHelper} to declare the values of the
 * {@link org.osgi.service.http.whiteboard.HttpWhiteboardConstants#HTTP_WHITEBOARD_CONTEXT_NAME
 * HTTP_WHITEBOARD_CONTEXT_NAME} and
 * {@link org.osgi.service.http.whiteboard.HttpWhiteboardConstants#HTTP_WHITEBOARD_CONTEXT_PATH
 * HTTP_WHITEBOARD_CONTEXT_PATH} service properties.
 * 
 * @see "Component Property Types"
 * @author $Id$
 * @since 1.1
 */
@ComponentPropertyType
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@RequireHttpWhiteboard
public @interface HttpWhiteboardContext {
	/**
	 * Prefix for the property name. This value is prepended to each property
	 * name.
	 */
	String PREFIX_ = "osgi.http.whiteboard.context.";

	/**
	 * Service property identifying a servlet context helper name.
	 * 
	 * @return The context name.
	 * @see org.osgi.service.http.whiteboard.HttpWhiteboardConstants#HTTP_WHITEBOARD_CONTEXT_NAME
	 *      HTTP_WHITEBOARD_CONTEXT_NAME
	 */
	String name();

	/**
	 * Service property identifying a servlet context helper path.
	 * 
	 * @return The context path.
	 * @see org.osgi.service.http.whiteboard.HttpWhiteboardConstants#HTTP_WHITEBOARD_CONTEXT_PATH
	 *      HTTP_WHITEBOARD_CONTEXT_PATH
	 */
	String path();
}

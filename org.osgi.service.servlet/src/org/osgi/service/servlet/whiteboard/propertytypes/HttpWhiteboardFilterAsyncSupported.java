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

package org.osgi.service.servlet.whiteboard.propertytypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.osgi.service.component.annotations.ComponentPropertyType;
import org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants;

/**
 * Component Property Type for the
 * {@code osgi.http.whiteboard.filter.asyncSupported} service property.
 * <p>
 * This annotation can be used on a {@link jakarta.servlet.Filter} to declare
 * the value of the
 * {@link HttpWhiteboardConstants#HTTP_WHITEBOARD_FILTER_ASYNC_SUPPORTED
 * HTTP_WHITEBOARD_FILTER_ASYNC_SUPPORTED} service property.
 * 
 * @see "Component Property Types"
 * @author $Id$
 */
@ComponentPropertyType
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface HttpWhiteboardFilterAsyncSupported {
	/**
	 * Prefix for the property name. This value is prepended to each property
	 * name.
	 */
	String PREFIX_ = "osgi.http.whiteboard.filter.";

	/**
	 * Service property identifying the asynchronous support of a filter.
	 * 
	 * @return Whether the filter supports asynchronous processing.
	 * @see org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants#HTTP_WHITEBOARD_FILTER_ASYNC_SUPPORTED
	 *      HTTP_WHITEBOARD_FILTER_ASYNC_SUPPORTED
	 */
	boolean asyncSupported() default true;
}

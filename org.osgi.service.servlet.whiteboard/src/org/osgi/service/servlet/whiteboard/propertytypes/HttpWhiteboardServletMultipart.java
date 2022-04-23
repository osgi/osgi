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

/**
 * Component Property Type for the
 * {@code osgi.http.whiteboard.servlet.multipart.enabled},
 * {@code osgi.http.whiteboard.servlet.multipart.fileSizeThreshold},
 * {@code osgi.http.whiteboard.servlet.multipart.location},
 * {@code osgi.http.whiteboard.servlet.multipart.maxFileSize}, and
 * {@code osgi.http.whiteboard.servlet.multipart.maxRequestSize} service
 * properties.
 * <p>
 * This annotation can be used on a {@link jakarta.servlet.Servlet} to declare the
 * values of the
 * {@link org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants#HTTP_WHITEBOARD_SERVLET_MULTIPART_ENABLED
 * HTTP_WHITEBOARD_SERVLET_MULTIPART_ENABLED},
 * {@link org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants#HTTP_WHITEBOARD_SERVLET_MULTIPART_FILESIZETHRESHOLD
 * HTTP_WHITEBOARD_SERVLET_MULTIPART_FILESIZETHRESHOLD},
 * {@link org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants#HTTP_WHITEBOARD_SERVLET_MULTIPART_LOCATION
 * HTTP_WHITEBOARD_SERVLET_MULTIPART_LOCATION},
 * {@link org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants#HTTP_WHITEBOARD_SERVLET_MULTIPART_MAXFILESIZE
 * HTTP_WHITEBOARD_SERVLET_MULTIPART_MAXFILESIZE}, and
 * {@link org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants#HTTP_WHITEBOARD_SERVLET_MULTIPART_MAXREQUESTSIZE
 * HTTP_WHITEBOARD_SERVLET_MULTIPART_MAXREQUESTSIZE} service properties.
 * 
 * @see "Component Property Types"
 * @author $Id$
 */
@ComponentPropertyType
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface HttpWhiteboardServletMultipart {
	/**
	 * Prefix for the property name. This value is prepended to each property
	 * name.
	 */
	String PREFIX_ = "osgi.http.whiteboard.servlet.multipart.";

	/**
	 * Service property identifying the multipart handling of a servlet.
	 * 
	 * @return Whether the servlet supports multipart handling.
	 * @see org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants#HTTP_WHITEBOARD_SERVLET_MULTIPART_ENABLED
	 *      HTTP_WHITEBOARD_SERVLET_MULTIPART_ENABLED
	 */
	boolean enabled() default true;

	/**
	 * Service property identifying the file size threshold for a multipart
	 * request handled by a servlet.
	 * 
	 * @return The file size threshold for a multipart request..
	 * @see org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants#HTTP_WHITEBOARD_SERVLET_MULTIPART_FILESIZETHRESHOLD
	 *      HTTP_WHITEBOARD_SERVLET_MULTIPART_FILESIZETHRESHOLD
	 */
	int fileSizeThreshold() default 0;

	/**
	 * Service property identifying the location for a multipart request handled
	 * by a servlet.
	 * 
	 * @return The location for a multipart request..
	 * @see org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants#HTTP_WHITEBOARD_SERVLET_MULTIPART_LOCATION
	 *      HTTP_WHITEBOARD_SERVLET_MULTIPART_LOCATION
	 */
	String location() default "";

	/**
	 * Service property identifying the max file size for a multipart request
	 * handled by a servlet.
	 * 
	 * @return The max file size for a multipart request..
	 * @see org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants#HTTP_WHITEBOARD_SERVLET_MULTIPART_MAXFILESIZE
	 *      HTTP_WHITEBOARD_SERVLET_MULTIPART_MAXFILESIZE
	 */
	long maxFileSize() default -1;

	/**
	 * Service property identifying the max request size for a multipart request
	 * handled by a servlet.
	 * 
	 * @return The max request size for a multipart request..
	 * @see org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants#HTTP_WHITEBOARD_SERVLET_MULTIPART_MAXREQUESTSIZE
	 *      HTTP_WHITEBOARD_SERVLET_MULTIPART_MAXREQUESTSIZE
	 */
	long maxRequestSize() default -1;
}

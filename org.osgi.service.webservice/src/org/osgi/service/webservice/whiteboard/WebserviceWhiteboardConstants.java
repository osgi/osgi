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
package org.osgi.service.webservice.whiteboard;

/**
 * Defines standard constants for the Jakarta Web Services Whiteboard services.
 * 
 * @author $Id$
 */
public class WebserviceWhiteboardConstants {

    private WebserviceWhiteboardConstants() {
        // non-instantiable
    }

	/**
	 * Base namespace for the Webservice Whiteboard specification
	 */
	public static final String	WEBSERVICE							= "osgi.service.webservice";
	/**
	 * Base prefix used in component property types
	 */
    public static final String WEBSERVICE_PREFIX = WEBSERVICE+".";
	/**
	 * Prefix used for properties of an endpoint implementor
	 */
	public static final String	WEBSERVICE_ENDPOINT_PREFIX		= WEBSERVICE_PREFIX
			+ "endpoint.";

	/**
	 * Prefix used for properties of an message handler implementor
	 */
	public static final String	WEBSERVICE_HANDLER_PREFIX			= WEBSERVICE_PREFIX
			+ "handler.";

	/**
	 * prefix used for properties related to the http transport
	 */
	public static final String	WEBSERVICE_HTTP_ENDPOINT_PREFIX	= WEBSERVICE_ENDPOINT_PREFIX
			+ "http.";
	/**
	 * property used to mark a service as an endpoint implementor
	 */
	public static final String	WEBSERVICE_ENDPOINT_IMPLEMENTOR	= WEBSERVICE_ENDPOINT_PREFIX
			+ "implementor";

	/**
	 * property that defines the address to use where endpoints should be
	 * published
	 */
	public static final String	WEBSERVICE_ENDPOINT_ADDRESS		= WEBSERVICE_ENDPOINT_PREFIX
			+ "address";

	/**
	 * property to define a filter that allows handlers to select a given
	 * endpoint as their target
	 */
	public static final String	WEBSERVICE_HANDLER_FILTER			= WEBSERVICE_HANDLER_PREFIX
			+ "filter";

	/**
	 * property to define that a handler registered as a service must be
	 * considered by the whiteboard
	 */
	public static final String	WEBSERVICE_HANDLER_EXTENSION		= WEBSERVICE_HANDLER_PREFIX
			+ "extension";
	/**
	 * Specification version
	 */
	public static final String	WEBSERVICE_SPECIFICATION_VERSION	= "1.0";

	/**
	 * Specification namesüace
	 */
	public static final String	WEBSERVICE_IMPLEMENTATION			= "osgi.webservice";

}

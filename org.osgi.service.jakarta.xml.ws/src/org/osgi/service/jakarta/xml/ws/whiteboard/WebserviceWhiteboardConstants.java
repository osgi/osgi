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
package org.osgi.service.jakarta.xml.ws.whiteboard;

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
	public static final String	WEBSERVICE							= "osgi.jakarta.xml.ws";
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
	public static final String	WEBSERVICE_ENDPOINT_SELECT		= WEBSERVICE_ENDPOINT_PREFIX
			+ "select";
	/**
	 * Specification version
	 */
	public static final String	WEBSERVICE_SPECIFICATION_VERSION	= "1.0";

}

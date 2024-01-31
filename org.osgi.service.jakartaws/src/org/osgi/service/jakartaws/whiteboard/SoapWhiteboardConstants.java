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
package org.osgi.service.jakartaws.whiteboard;

/**
 * Defines standard constants for the Jakarta Web Services Whiteboard services.
 * 
 * @author $Id$
 */
public class SoapWhiteboardConstants {

    private SoapWhiteboardConstants() {
        // non-instantiable
    }

	/**
	 * Base namespace for the SOAP Whiteboard specification
	 */
    public static final String SOAP = "osgi.soap";
	/**
	 * Base prefix used in component property types
	 */
    public static final String SOAP_PREFIX = SOAP+".";
	/**
	 * Prefix used for properties of an endpoint implementor
	 */
	public static final String	SOAP_ENDPOINT_PREFIX		= SOAP_PREFIX
			+ "endpoint.";

	/**
	 * TODO
	 */
	public static final String	SOAP_HTTP_ENDPOINT_PREFIX	= SOAP_ENDPOINT_PREFIX
			+ "http.";
	// /**
	// * TODO
	// */
	// public static final String SOAP_ENDPOINT_IMPLEMENTOR =
	// SOAP_ENDPOINT_PREFIX + "implementor";
	// /**
	// * TODO
	// */
	// public static final String SOAP_ENDPOINT_PATH = SOAP_ENDPOINT_PREFIX +
	// "contextpath";
	// /**
	// * TODO
	// */
	// public static final String SOAP_ENDPOINT_SELECT = SOAP_ENDPOINT_PREFIX +
	// "selector";
	/**
	 * TODO
	 */
	public static final String	SOAP_SPECIFICATION_VERSION	= "1.0";

}

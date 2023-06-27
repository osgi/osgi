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

package org.osgi.service.jakarta.websocket.whiteboard;

/**
 * Defines standard constants for the JakartaWebsocket Services Whiteboard
 * services.
 * 
 * @author $Id$
 */
public final class JakartaWebsocketWhiteboardConstants {
	private JakartaWebsocketWhiteboardConstants() {
		// non-instantiable
	}

	/**
	 * The version of the implementation capability for the Whiteboard
	 * Specification for Jakarta RESTful Web Services.
	 */
	public static final String JAKARTA_WEBSOCKET_WHITEBOARD_SPECIFICATION_VERSION = "1.0";

	/**
	 * Base namespace for the Webservice Whiteboard specification
	 */
	public static final String	WEBSOCKET											= "osgi.jakarta.websocket";
	/**
	 * Base prefix used in component property types
	 */
	public static final String	WEBSOCKET_PREFIX									= WEBSOCKET
			+ ".";
	/**
	 * Prefix used for properties of an endpoint implementor
	 */
	public static final String	WEBSOCKET_ENDPOINT_PREFIX							= WEBSOCKET_PREFIX
			+ "endpoint.";

	/**
	 * property used to mark a service as an endpoint implementor
	 */
	public static final String	WEBSOCKET_ENDPOINT_IMPLEMENTOR						= WEBSOCKET_ENDPOINT_PREFIX
			+ "implementor";

}

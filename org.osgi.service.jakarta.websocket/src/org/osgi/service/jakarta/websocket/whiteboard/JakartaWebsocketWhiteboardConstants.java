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
 * Defines standard constants for the Jakarta WebSocket Services Whiteboard
 * specification.
 * 
 * @author $Id$
 */
public final class JakartaWebsocketWhiteboardConstants {
	private JakartaWebsocketWhiteboardConstants() {
		// non-instantiable
	}

	/**
	 * The version of the Jakarta WebSocket Whiteboard Specification.
	 * <p>
	 * The value of this constant is {@value}.
	 */
	public static final String JAKARTA_WEBSOCKET_WHITEBOARD_SPECIFICATION_VERSION = "1.0";

	/**
	 * The name of the implementation capability for the Jakarta WebSocket
	 * Whiteboard Specification.
	 * <p>
	 * The value of this constant is {@value}.
	 */
	public static final String	WEBSOCKET											= "osgi.jakarta.websocket";
	
	/**
	 * The base prefix for property names in the Jakarta WebSocket Whiteboard
	 * Specification.
	 * <p>
	 * The value of this constant is {@value}.
	 */
	public static final String	WEBSOCKET_PREFIX									= WEBSOCKET
			+ ".";
	
	/**
	 * The prefix for property names specific to WebSocket endpoint services.
	 * <p>
	 * The value of this constant is {@value}.
	 */
	public static final String	WEBSOCKET_ENDPOINT_PREFIX							= WEBSOCKET_PREFIX
			+ "endpoint.";

	/**
	 * Service property specifying that a service should be processed as a
	 * WebSocket endpoint implementor by the whiteboard.
	 * <p>
	 * A service providing this property must be a valid Jakarta WebSocket
	 * endpoint (annotated with {@code @ServerEndpoint} or implementing
	 * {@code jakarta.websocket.Endpoint}).
	 * <p>
	 * The value of this service property must be of type {@code String} or
	 * {@link Boolean} and set to &quot;true&quot; or {@code true}.
	 * <p>
	 * The value of this constant is {@value}.
	 */
	public static final String	WEBSOCKET_ENDPOINT_IMPLEMENTOR						= WEBSOCKET_ENDPOINT_PREFIX
			+ "implementor";

	/**
	 * Service property specifying the URI path or URI template at which the
	 * endpoint should be published.
	 * <p>
	 * When this property is present and not empty, it overrides the value
	 * specified in the {@code @ServerEndpoint} annotation's {@code value()}
	 * attribute.
	 * <p>
	 * Changing this property will result in immediate termination of all
	 * currently running sessions for this endpoint, and the endpoint will be
	 * made available under the new path.
	 * <p>
	 * The value of this service property must be of type {@code String}.
	 * <p>
	 * The value of this constant is {@value}.
	 */
	public static final String	WEBSOCKET_ENDPOINT_PATH								= WEBSOCKET_ENDPOINT_PREFIX
			+ "path";

	/**
	 * The prefix for property names specific to WebSocket configurator
	 * services.
	 * <p>
	 * The value of this constant is {@value}.
	 */
	public static final String	WEBSOCKET_CONFIGURATOR_PREFIX						= WEBSOCKET_PREFIX
			+ "configurator.";

	/**
	 * Service property specifying that a service should be processed as a
	 * ServerEndpointConfig.Configurator implementor by the whiteboard.
	 * <p>
	 * A service providing this property must be a valid
	 * {@code ServerEndpointConfig.Configurator}.
	 * <p>
	 * The value of this service property must be of type {@code String} or
	 * {@link Boolean} and set to &quot;true&quot; or {@code true}.
	 * <p>
	 * The value of this constant is {@value}.
	 */
	public static final String	WEBSOCKET_CONFIGURATOR_IMPLEMENTOR					= WEBSOCKET_CONFIGURATOR_PREFIX
			+ "implementor";

	/**
	 * Service property specifying the endpoint path that a configurator should
	 * match.
	 * <p>
	 * When this property is present and not empty, the configurator will be
	 * used for endpoints whose path matches this value. If multiple
	 * configurators match the same endpoint path, the one with the highest
	 * service ranking (or lowest service ID if rankings are equal) will be
	 * selected.
	 * <p>
	 * A configurator matching an endpoint path takes precedence over any
	 * configurator specified in the {@code @ServerEndpoint} annotation's
	 * {@code configurator()} attribute.
	 * <p>
	 * The value of this service property must be of type {@code String}.
	 * <p>
	 * The value of this constant is {@value}.
	 */
	public static final String	WEBSOCKET_CONFIGURATOR_ENDPOINT_PATH				= WEBSOCKET_CONFIGURATOR_PREFIX
			+ "endpointPath";

}

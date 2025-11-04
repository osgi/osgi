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
package org.osgi.service.jakarta.websocket.whiteboard.propertytypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.osgi.service.component.annotations.ComponentPropertyType;
import org.osgi.service.jakarta.websocket.whiteboard.JakartaWebsocketWhiteboardConstants;
import org.osgi.service.jakarta.websocket.whiteboard.annotations.RequireWebsocketWhiteboard;

/**
 * Component Property Type for the
 * {@link JakartaWebsocketWhiteboardConstants#WEBSOCKET_ENDPOINT_IMPLEMENTOR
 * osgi.jakarta.websocket.endpoint.implementor} service property.
 * <p>
 * This annotation can be used on a Jakarta WebSocket endpoint component to
 * declare that it should be processed by the WebSocket Whiteboard
 * implementation.
 * <p>
 * A service annotated with this annotation must be a valid Jakarta WebSocket
 * endpoint, either by being annotated with
 * {@code @jakarta.websocket.server.ServerEndpoint} or by implementing
 * {@code jakarta.websocket.Endpoint}.
 * 
 * @see JakartaWebsocketWhiteboardConstants#WEBSOCKET_ENDPOINT_IMPLEMENTOR
 * @author $Id$
 */
@ComponentPropertyType
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@RequireWebsocketWhiteboard
public @interface WhiteboardEndpoint {

	/**
	 * Prefix for the property name. This value is prepended to each property
	 * name.
	 */
	String PREFIX_ = JakartaWebsocketWhiteboardConstants.WEBSOCKET_ENDPOINT_PREFIX;
	
	/**
	 * Service property identifying the WebSocket endpoint implementor.
	 * <p>
	 * When {@code true}, this service will be processed as a WebSocket endpoint
	 * by the whiteboard. This can be used to enable or disable an endpoint
	 * implementation.
	 * 
	 * @return {@code true} if this is an implementor for the WebSocket
	 *         whiteboard, {@code false} otherwise.
	 */
	boolean implementor() default true;
    
}
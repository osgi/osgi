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
package org.osgi.service.jakarta.websocket.whiteboard.annotations;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.osgi.annotation.bundle.Requirement;
import org.osgi.namespace.implementation.ImplementationNamespace;
import org.osgi.service.jakarta.websocket.whiteboard.JakartaWebsocketWhiteboardConstants;


/**
 * This annotation can be used to require the Jakarta WebSocket Whiteboard
 * implementation. It can be used on a bundle or service to ensure that the
 * WebSocket Whiteboard extender is present before the annotated element is
 * activated.
 * <p>
 * This annotation results in a requirement on the
 * {@code osgi.implementation} namespace with a filter requiring an
 * implementation of {@code osgi.jakarta.websocket} at version
 * {@code 1.0} or higher.
 * 
 * @see JakartaWebsocketWhiteboardConstants#WEBSOCKET
 * @see JakartaWebsocketWhiteboardConstants#JAKARTA_WEBSOCKET_WHITEBOARD_SPECIFICATION_VERSION
 * @author $Id$
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({
		ElementType.TYPE, ElementType.PACKAGE
})
@Requirement(namespace = ImplementationNamespace.IMPLEMENTATION_NAMESPACE, //
		name = JakartaWebsocketWhiteboardConstants.WEBSOCKET, //
		version = JakartaWebsocketWhiteboardConstants.JAKARTA_WEBSOCKET_WHITEBOARD_SPECIFICATION_VERSION)
public @interface RequireWebsocketWhiteboard {
	// This is a marker annotation.
}
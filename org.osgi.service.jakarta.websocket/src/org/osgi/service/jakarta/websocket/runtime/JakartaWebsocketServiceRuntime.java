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

package org.osgi.service.jakarta.websocket.runtime;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.jakarta.websocket.runtime.dto.RuntimeDTO;

/**
 * The JakartaWebsocketServiceRuntime service represents the runtime information
 * of a Jakarta WebSocket Whiteboard implementation.
 * <p>
 * It provides access to DTOs representing the current state of the service.
 * <p>
 * The JakartaWebsocketServiceRuntime service must be registered with the
 * {@code osgi.jakarta.websocket.endpoint} service property.
 * 
 * @ThreadSafe
 * @author $Id$
 */
@ProviderType
public interface JakartaWebsocketServiceRuntime {
	/**
	 * Return the runtime DTO representing the current state of the Jakarta
	 * WebSocket Whiteboard implementation.
	 * 
	 * @return The runtime DTO. The returned DTO is a snapshot of the runtime
	 *         state and will not be updated if the state changes.
	 */
	RuntimeDTO getRuntimeDTO();
}

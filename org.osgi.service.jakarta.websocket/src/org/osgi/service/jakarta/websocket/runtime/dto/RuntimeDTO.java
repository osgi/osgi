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

package org.osgi.service.jakarta.websocket.runtime.dto;

import org.osgi.dto.DTO;
import org.osgi.framework.dto.ServiceReferenceDTO;

/**
 * Represents the state of a Jakarta WebSocket Whiteboard Service Runtime.
 * <p>
 * This DTO provides a snapshot of the runtime state, including all registered
 * endpoints and any endpoints that failed to register.
 * 
 * @NotThreadSafe
 * @author $Id$
 */

public class RuntimeDTO extends DTO {
	/**
	 * The DTO for the corresponding {@code JakartaWebsocketServiceRuntime}.
	 * <p>
	 * This value is never {@code null}.
	 */
	public ServiceReferenceDTO	serviceReference;

	/**
	 * The representations of the WebSocket endpoints currently registered with
	 * the runtime.
	 * <p>
	 * The returned array may be empty if no endpoints are currently registered.
	 */
	public EndpointDTO[]		endpoints;

	/**
	 * The representations of the WebSocket endpoints that are known to the
	 * runtime but failed to register.
	 * <p>
	 * The returned array may be empty if all endpoints registered successfully.
	 */
	public FailedEndpointDTO[]	failedEndpoints;
}

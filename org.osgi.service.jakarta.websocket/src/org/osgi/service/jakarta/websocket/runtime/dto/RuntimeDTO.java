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
 * Represents the state of a Jakarta Websocket Runtime.
 * 
 * @NotThreadSafe
 * @author $Id$
 */

public class RuntimeDTO extends DTO {
	/**
	 * Returns the current service reference under that the runtime is
	 * registered
	 */
	public ServiceReferenceDTO	serviceReference;

	/**
	 * Returns the representations of the Web Services endpoints currently
	 * registered, The returned array may be empty.
	 */
	public EndpointDTO[]		endpoints;

	/**
	 * Returns the representations of the Web Services endpoints currently known
	 * but failed to register, The returned array may be empty.
	 */
	public FailedEndpointDTO[]	failedEndpoints;
}

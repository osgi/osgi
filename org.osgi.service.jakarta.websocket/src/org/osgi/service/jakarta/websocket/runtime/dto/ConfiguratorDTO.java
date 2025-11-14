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
 * Represents a ServerEndpointConfig.Configurator that is successfully
 * registered with the runtime and matched to an endpoint.
 * <p>
 * This DTO describes the current state of a configurator implementor known to
 * the service runtime.
 * 
 * @NotThreadSafe
 * @author $Id$
 */
public class ConfiguratorDTO extends DTO {
	/**
	 * The DTO for the corresponding service that implements this configurator.
	 * <p>
	 * This value is never {@code null}.
	 */
	public ServiceReferenceDTO	implementor;

	/**
	 * The endpoint path that this configurator matches.
	 * <p>
	 * This value is never {@code null}.
	 */
	public String				endpointPath;

}

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

import org.osgi.framework.dto.ServiceReferenceDTO;

/**
 * Represents a ServerEndpointConfig.Configurator that failed to be registered
 * with the runtime.
 * <p>
 * This DTO describes a configurator implementor that is known to the service
 * runtime but could not be successfully registered due to an error.
 * 
 * @NotThreadSafe
 * @author $Id$
 */
public class FailedConfiguratorDTO extends FailedDTO {

	/**
	 * The configurator has no matching endpoint. This may occur when the
	 * configurator's endpoint path does not match any registered endpoint.
	 * <p>
	 * The value of this constant is {@value}.
	 **/
	public static final int		FAILURE_REASON_NO_MATCHING_ENDPOINT	= 200;

	/**
	 * The configurator was not selected because a higher ranked configurator
	 * exists for the same endpoint path. The configurator with the highest
	 * service ranking (or lowest service ID if rankings are equal) is selected.
	 * <p>
	 * The value of this constant is {@value}.
	 **/
	public static final int		FAILURE_REASON_NOT_SELECTED			= 201;

	/**
	 * The DTO for the corresponding service that implements this configurator.
	 * <p>
	 * This value is never {@code null}.
	 */
	public ServiceReferenceDTO	implementor;
	
	/**
	 * The endpoint path that this configurator attempted to match.
	 * <p>
	 * This value may be {@code null} if the endpoint path could not be
	 * determined.
	 */
	public String				endpointPath;
}

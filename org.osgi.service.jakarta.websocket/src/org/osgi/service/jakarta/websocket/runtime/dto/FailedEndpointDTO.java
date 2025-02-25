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
 * The EndpointDTO describes the current state of an endoint implementor known
 * to the service runtime
 */
public class FailedEndpointDTO extends FailedDTO {

	/**
	 * The endpoint is invalid, for example does not contain required
	 * annotations
	 **/
	public static final int		FAILURE_REASON_INVALID	= 100;

	/**
	 * The DTO for the corresponding implementor that created this endpoint.
	 * This value is never {@code null}.
	 */
	public ServiceReferenceDTO	implementor;
}
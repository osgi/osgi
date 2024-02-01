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

package org.osgi.service.jakartaws.runtime.dto;

import org.osgi.dto.DTO;
import org.osgi.framework.dto.ServiceReferenceDTO;

/**
 * Represents the state of a Jakarta RESTful Web Services Service Runtime.
 * 
 * @NotThreadSafe
 * @author $Id$
 */
public class RuntimeDTO extends DTO {

	/**
	 * The DTO for the corresponding {@code JakartarsServiceRuntime}. This value
	 * is never {@code null}.
	 */
	public ServiceReferenceDTO	serviceReference;

	/**
	 * Returns the representations of the Web Services endpoints currently
	 * known, The returned array may be empty.
	 */
	public EnpointDTO[]			endpoints;

	/**
	 * Returns all handlers that are known and possible bound to an endpoint,
	 * the returned array may be empty.
	 */
	public HandlerDTO[]			handlers;

}

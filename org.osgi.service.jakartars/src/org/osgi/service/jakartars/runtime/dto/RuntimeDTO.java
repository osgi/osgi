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

package org.osgi.service.jakartars.runtime.dto;

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
	public ServiceReferenceDTO		serviceDTO;

	/**
	 * Returns the current state of the default application for this Runtime.
	 */
	public ApplicationDTO			defaultApplication;

	/**
	 * Returns the representations of the Jakarta RESTful Web Services
	 * Application services associated with this Runtime. The returned array may
	 * be empty if this whiteboard is currently not associated with any Jakarta
	 * RESTful Web Services application services.
	 */
	public ApplicationDTO[]			applicationDTOs;

	/**
	 * Returns the representations of the Jakarta RESTful Web Services resource
	 * services targeted to this runtime but currently not used due to some
	 * problem. The returned array may be empty.
	 */
	public FailedResourceDTO[]		failedResourceDTOs;

	/**
	 * Returns the representations of the Jakarta RESTful Web Services extension
	 * services targeted to this runtime but currently not used due to some
	 * problem. The returned array may be empty.
	 */
	public FailedExtensionDTO[]		failedExtensionDTOs;

	/**
	 * Returns the representations of the Jakarta RESTful Web Services extension
	 * services targeted to this runtime but currently not used due to some
	 * problem. The returned array may be empty.
	 */
	public FailedApplicationDTO[]	failedApplicationDTOs;

}

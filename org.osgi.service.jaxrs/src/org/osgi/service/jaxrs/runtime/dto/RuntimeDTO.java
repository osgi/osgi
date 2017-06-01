/*
 * Copyright (c) OSGi Alliance (2012, 2016). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.service.jaxrs.runtime.dto;

import org.osgi.dto.DTO;
import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.resource.dto.ResourceDTO;

/**
 * Represents the state of a Http Service Runtime.
 * 
 * @NotThreadSafe
 * @author $Id$
 */
public class RuntimeDTO extends DTO {

	/**
	 * The DTO for the corresponding {@code JaxRSServiceRuntime}. This value is
	 * never {@code null}.
	 */
	public ServiceReferenceDTO		serviceDTO;

	/**
	 * Returns the representations of the JAX-RS resource services associated
	 * with this Runtime. The returned array may be empty if this whiteboard is
	 * currently not associated with any JAX-RS Resource services.
	 */
	public ResourceDTO[]			resourceDTOs;

	/**
	 * Returns the representations of the JAX-RS extension services associated
	 * with this Runtime. The returned array may be empty if this whiteboard is
	 * currently not associated with any JAX-RS extension services.
	 */
	public ExtensionDTO[]			extensionDTOs;

	/**
	 * Returns the representations of the JAX-RS Application services associated
	 * with this Runtime. The returned array may be empty if this whiteboard is
	 * currently not associated with any JAX-RS application services.
	 */
	public ApplicationDTO[]			applicationDTOs;

	/**
	 * Returns the representations of the JAX-RS resource services associated
	 * with this runtime but currently not used due to some problem. The
	 * returned array may be empty.
	 */
	public FailedResourceDTO[]		failedResourceDTOs;

	/**
	 * Returns the representations of the JAX-RS extension services associated
	 * with this runtime but currently not used due to some problem. The
	 * returned array may be empty.
	 */
	public FailedExtensionDTO[]		failedExtensionDTOs;

	/**
	 * Returns the representations of the JAX-RS extension services associated
	 * with this runtime but currently not used due to some problem. The
	 * returned array may be empty.
	 */
	public FailedApplicationDTO[]	failedApplicationDTOs;

}

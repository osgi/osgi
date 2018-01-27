/*
 * Copyright (c) OSGi Alliance (2012, 2018). All Rights Reserved.
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

package org.osgi.service.jaxrs.runtime;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.jaxrs.runtime.dto.RuntimeDTO;

/**
 * The JaxrsServiceRuntime service represents the runtime information of a
 * JAX-RS Whiteboard implementation.
 * <p>
 * It provides access to DTOs representing the current state of the service.
 * <p>
 * The JaxrsServiceRuntime service must be registered with the
 * {@link JaxrsServiceRuntimeConstants#JAX_RS_SERVICE_ENDPOINT} service
 * property.
 *
 * @ThreadSafe
 * @author $Id$
 */
@ProviderType
public interface JaxrsServiceRuntime {

	/**
	 * Return the runtime DTO representing the current state.
	 * 
	 * @return The runtime DTO.
	 */
	public RuntimeDTO getRuntimeDTO();
}

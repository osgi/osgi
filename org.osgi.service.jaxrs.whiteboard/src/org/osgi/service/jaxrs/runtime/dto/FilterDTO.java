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
import org.osgi.service.jaxrs.runtime.JaxRSServiceRuntime;
import org.osgi.service.jaxrs.whiteboard.JaxRSWhiteboardConstants;

/**
 * Represents a JAX-RS Filter service currently being hosted by the
 * {@link JaxRSServiceRuntime}
 * 
 * @NotThreadSafe
 * @author $Id$
 */
public class FilterDTO extends DTO {

	/**
	 * The request mappings for the filter, as declared in
	 * {@link JaxRSWhiteboardConstants#JAX_RS_FILTER_BASE}
	 * 
	 * <p>
	 * The specified patterns are used to determine whether a request is mapped
	 * to the JAX-RS filter. This array might be empty.
	 */
	public String[]			baseUris;

	/**
	 * The resourceDTOs that are mapped to this Filter using a named annotation
	 */
	public ResourceDTO[]	filteredByName;

	/**
	 * Service property identifying the JAX-RS filter service.
	 */
	public long				serviceId;
}

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

import javax.ws.rs.Consumes;
import javax.ws.rs.NameBinding;
import javax.ws.rs.Produces;

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
public class ExtensionDTO extends DTO {

	/**
	 * The {@link JaxRSWhiteboardConstants#JAX_RS_EXTENSION_NAME} for this
	 * extension
	 */
	public String			name;

	/**
	 * The extension types recognized for this service.
	 */
	public String[]			extensionTypes;

	/**
	 * The media types produced by this service, if provided in an
	 * {@link Produces} annotation
	 */
	public String[]			produces;

	/**
	 * The media types consumed by this service, if provided in an
	 * {@link Consumes} annotation
	 */
	public String[]			consumes;

	/**
	 * The resourceDTOs that are mapped to this extension using a
	 * {@link NameBinding} annotation
	 */
	public ResourceDTO[]	filteredByName;

	/**
	 * Service property identifying the JAX-RS extension service.
	 */
	public long				serviceId;
}

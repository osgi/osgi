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
import org.osgi.service.jaxrs.whiteboard.JaxRSWhiteboardConstants;

/**
 * Represents common information about a JAX-RS resource service.
 *
 * @NotThreadSafe
 * @author $Id$
 */
public abstract class ResourceDTO extends DTO {
	/**
	 * The name of the resource if it set one using
	 * {@link JaxRSWhiteboardConstants#JAX_RS_NAME}, {@code null}
	 * otherwise.
	 */
	public String					name;

	/**
	 * The base URI of the resource defined by
	 * {@link JaxRSWhiteboardConstants#JAX_RS_RESOURCE}.
	 */
	public String					base;

	/**
	 * The RequestPaths handled by this resource
	 */
	public ResourceMethodInfoDTO[]	resourceMethods;

	/**
	 * Service property identifying the JAX-RS resource service
	 */
	public long						serviceId;
}

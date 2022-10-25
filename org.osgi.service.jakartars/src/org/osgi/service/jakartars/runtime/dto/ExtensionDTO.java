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

import org.osgi.service.jakartars.runtime.JakartarsServiceRuntime;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.NameBinding;
import jakarta.ws.rs.Produces;

/**
 * Represents a Jakarta RESTful Web Services Extension service currently being
 * hosted by the {@link JakartarsServiceRuntime}
 * 
 * @NotThreadSafe
 * @author $Id$
 */
public class ExtensionDTO extends BaseExtensionDTO {

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
	 * The full names of the {@link NameBinding} annotations applied to this
	 * extension, if any
	 */
	public String[]			nameBindings;

	/**
	 * The resourceDTOs that are mapped to this extension using a
	 * {@link NameBinding} annotation
	 */
	public ResourceDTO[]	filteredByName;
}

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

import org.osgi.service.jakartars.whiteboard.JakartarsWhiteboardConstants;

/**
 * Represents common information about a Jakarta RESTful Web Services
 * application service.
 *
 * @NotThreadSafe
 * @author $Id$
 */
public abstract class BaseApplicationDTO extends BaseDTO {

	/**
	 * The base URI of the resource defined by
	 * {@link JakartarsWhiteboardConstants#JAKARTA_RS_APPLICATION_BASE}.
	 */
	public String			base;

	/**
	 * Returns the representations of the dynamic Jakarta RESTful Web Services
	 * resource services associated with this Application. The returned array
	 * may be empty if this application is currently not associated with any
	 * Jakarta RESTful Web Services Resource services.
	 */
	public ResourceDTO[]	resourceDTOs;

	/**
	 * Returns the representations of the dynamic Jakarta RESTful Web Services
	 * extension services associated with this Application. The returned array
	 * may be empty if this application is currently not associated with any
	 * Jakarta RESTful Web Services extension services.
	 */
	public ExtensionDTO[]	extensionDTOs;
}

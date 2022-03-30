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
import org.osgi.service.jakartars.whiteboard.JakartarsWhiteboardConstants;

/**
 * Represents common information about a Jakarta RESTful Web Services service.
 *
 * @NotThreadSafe
 * @author $Id$
 */
public abstract class BaseDTO extends DTO {
	/**
	 * The name of the service if it set one using
	 * {@link JakartarsWhiteboardConstants#JAKARTA_RS_NAME}, otherwise this
	 * value will contain the generated name for this service
	 */
	public String					name;

	/**
	 * Service property identifying the Jakarta RESTful Web Services service
	 */
	public long						serviceId;
}

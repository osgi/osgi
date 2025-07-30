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
package org.osgi.service.webservice.runtime.dto;

import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.service.webservice.whiteboard.WebserviceWhiteboardConstants;

/**
 * Represents a handler currently known to the webservice runtime but can't be
 * used to a failure
 */
public class FailedHandlerDTO extends FailedDTO {


	/**
	 * No matching endpoint.
	 **/
	public static final int		FAILURE_REASON_NO_MATCHING_ENDPOINT	= 100;

	/**
	 * The registered service specifies an invalid filter
	 * {@value WebserviceWhiteboardConstants#WEBSERVICE_HANDLER_FILTER}
	 * property, this includes:
	 * <ul>
	 * <li>The property is not of type String</li>
	 * <li>The property can not be parsed as a valid OSGi Filter</li>
	 * </ul>
	 */
	public static final int		FAILURE_REASON_INVALID_FILTER		= 101;

	/**
	 * The service reference of the handler, is never {@code null}.
	 */
	public ServiceReferenceDTO serviceReference;

}

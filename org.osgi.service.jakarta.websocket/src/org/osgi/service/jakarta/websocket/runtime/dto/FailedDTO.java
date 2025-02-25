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
package org.osgi.service.jakarta.websocket.runtime.dto;

import org.osgi.dto.DTO;

/**
 * Base class representing a failure with a code and optional message
 */
public class FailedDTO extends DTO {

	/**
	 * Failure reason is unknown.
	 */
	public static final int	FAILURE_REASON_UNKNOWN				= 1;

	/**
	 * The service is registered in the service registry but getting the service
	 * fails as it returns {@code null}.
	 */
	public static final int	FAILURE_REASON_SERVICE_NOT_GETTABLE	= 2;

	/**
	 * Contains a code to indicate why the handler failed
	 */
	public int		failureCode;

	/**
	 * Contains a message that describes the failure further, might be
	 * <code>null</code> in case there is no such message available
	 */
	public String	failureMessage;
}
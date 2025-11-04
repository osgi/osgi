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
 * Base class for DTOs representing a failure, containing a failure code and an
 * optional failure message.
 * <p>
 * This is used to communicate why a particular WebSocket endpoint or other
 * service could not be successfully processed by the runtime.
 * 
 * @NotThreadSafe
 * @author $Id$
 */
public class FailedDTO extends DTO {

	/**
	 * Failure reason is unknown.
	 * <p>
	 * The value of this constant is {@value}.
	 */
	public static final int	FAILURE_REASON_UNKNOWN				= 1;

	/**
	 * The service is registered in the service registry but getting the service
	 * fails as it returns {@code null}.
	 * <p>
	 * This can happen if the service has already been unregistered or if the
	 * service factory returns {@code null}.
	 * <p>
	 * The value of this constant is {@value}.
	 */
	public static final int	FAILURE_REASON_SERVICE_NOT_GETTABLE	= 2;

	/**
	 * The failure code indicating why the service failed to be processed.
	 * <p>
	 * This will be one of the {@code FAILURE_REASON_*} constants defined in
	 * this class or its subclasses.
	 */
	public int		failureCode;

	/**
	 * A human-readable message that describes the failure in more detail.
	 * <p>
	 * This value may be {@code null} if no additional information is available.
	 */
	public String	failureMessage;
}
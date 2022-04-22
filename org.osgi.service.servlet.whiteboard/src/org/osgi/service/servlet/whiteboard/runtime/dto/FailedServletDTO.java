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

package org.osgi.service.servlet.whiteboard.runtime.dto;

/**
 * Represents a {@code jakarta.servlet.Servlet} service which is currently not
 * being used by a servlet context due to a problem.
 * <p>
 * As the servlet represented by this DTO is not used due to a failure, the
 * field {@link FailedServletDTO#servletContextId} always returns {@code 0} and
 * does not point to an existing servlet context.
 * 
 * @NotThreadSafe
 * @author $Id$
 */
public class FailedServletDTO extends ServletDTO {

	/**
	 * The reason why the servlet represented by this DTO is not used.
	 * 
	 * @see DTOConstants#FAILURE_REASON_UNKNOWN
	 * @see DTOConstants#FAILURE_REASON_EXCEPTION_ON_INIT
	 * @see DTOConstants#FAILURE_REASON_NO_SERVLET_CONTEXT_MATCHING
	 * @see DTOConstants#FAILURE_REASON_SERVICE_NOT_GETTABLE
	 * @see DTOConstants#FAILURE_REASON_SERVLET_CONTEXT_FAILURE
	 * @see DTOConstants#FAILURE_REASON_SHADOWED_BY_OTHER_SERVICE
	 * @see DTOConstants#FAILURE_REASON_SERVLET_WRITE_TO_LOCATION_DENIED
	 * @see DTOConstants#FAILURE_REASON_WHITEBOARD_WRITE_TO_DEFAULT_DENIED
	 * @see DTOConstants#FAILURE_REASON_SERVLET_READ_FROM_DEFAULT_DENIED
	 */
	public int	failureReason;
}

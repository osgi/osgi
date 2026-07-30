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

package org.osgi.service.jdkhttp.runtime.dto;

/**
 * Defines standard constants for the DTOs.
 * <p>
 * The failure reason constants reuse the numeric values of the identically
 * named constants defined by the OSGi Servlet Whiteboard Specification's
 * {@code org.osgi.service.servlet.runtime.dto.DTOConstants}, so that the same
 * failure reason has the same meaning across whiteboard specifications.
 *
 * @author $Id$
 */
public final class DTOConstants {
	private DTOConstants() {
		// non-instantiable
	}

	/**
	 * Failure reason is unknown.
	 */
	public static final int	FAILURE_REASON_UNKNOWN						= 0;

	/**
	 * Service is shadowed by another service.
	 * <p>
	 * For example, another service with the same context path or pattern but
	 * having a higher service ranking. See
	 * {@link org.osgi.framework.ServiceReference#compareTo(Object)}.
	 */
	public static final int	FAILURE_REASON_SHADOWED_BY_OTHER_SERVICE	= 3;

	/**
	 * An exception occurred while initializing the handler, filter,
	 * authenticator, or resource context.
	 */
	public static final int	FAILURE_REASON_EXCEPTION_ON_INIT			= 4;

	/**
	 * The service is registered in the service registry but getting the
	 * service fails as it returns {@code null}.
	 */
	public static final int	FAILURE_REASON_SERVICE_NOT_GETTABLE			= 5;

	/**
	 * The service is registered in the service registry but the service
	 * properties are invalid, for example a missing or malformed context
	 * path or pattern property.
	 */
	public static final int	FAILURE_REASON_VALIDATION_FAILED			= 6;
}

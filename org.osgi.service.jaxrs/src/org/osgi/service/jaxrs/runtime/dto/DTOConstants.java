/*
 * Copyright (c) OSGi Alliance (2012, 2018). All Rights Reserved.
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

import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;

/**
 * Defines standard constants for the DTOs. The error codes are defined to take
 * the same values as used by the Http Service Whiteboard
 */
public final class DTOConstants {
	private DTOConstants() {
		// non-instantiable
	}

	/**
	 * Failure reason is unknown.
	 */
	public static final int	FAILURE_REASON_UNKNOWN							= 0;

	/**
	 * Service is shadowed by another service.
	 * <p>
	 * For example, a service with the same service properties but a higher
	 * service ranking.
	 */
	public static final int	FAILURE_REASON_SHADOWED_BY_OTHER_SERVICE		= 1;

	/**
	 * The service is registered in the service registry but getting the service
	 * fails as it returns {@code null}.
	 */
	public static final int	FAILURE_REASON_SERVICE_NOT_GETTABLE				= 2;

	/**
	 * The service is registered in the service registry but the service
	 * properties are invalid.
	 */
	public static final int	FAILURE_REASON_VALIDATION_FAILED				= 3;

	/**
	 * The extension service is registered in the service registry but the
	 * service is not registered using a recognized extension type
	 */
	public static final int	FAILURE_REASON_NOT_AN_EXTENSION_TYPE			= 4;

	/**
	 * The service is registered in the service registry with the
	 * {@link JaxrsWhiteboardConstants#JAX_RS_EXTENSION_SELECT} property and one
	 * or more of the filters is not matched.
	 */
	public static final int	FAILURE_REASON_REQUIRED_EXTENSIONS_UNAVAILABLE	= 5;

	/**
	 * The service is registered in the service registry with the
	 * {@link JaxrsWhiteboardConstants#JAX_RS_NAME} property and a service with
	 * that name already exists in the runtime
	 */
	public static final int	FAILURE_REASON_DUPLICATE_NAME					= 6;

	/**
	 * The service is registered in the service registry with the
	 * {@link JaxrsWhiteboardConstants#JAX_RS_APPLICATION_SELECT} property and
	 * the filters is not matched by any running application.
	 */
	public static final int	FAILURE_REASON_REQUIRED_APPLICATION_UNAVAILABLE	= 7;

}

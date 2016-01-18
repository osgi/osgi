/*
 * Copyright (c) OSGi Alliance (2012, 2015). All Rights Reserved.
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

/**
 * Defines standard constants for the DTOs.
 * 
 * The error codes are defined to take the same values as used by the Http
 * Service Whiteboard
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
	 * For example, a service with the same service properties but a higher
	 * service ranking.
	 */
	public static final int	FAILURE_REASON_SHADOWED_BY_OTHER_SERVICE	= 3;

	/**
	 * The service is registered in the service registry but getting the service
	 * fails as it returns {@code null}.
	 */
	public static final int	FAILURE_REASON_SERVICE_NOT_GETTABLE			= 5;

	/**
	 * The service is registered in the service registry but the service
	 * properties are invalid.
	 */
	public static final int	FAILURE_REASON_VALIDATION_FAILED			= 6;

}

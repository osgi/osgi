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

package org.osgi.service.jaxrs.runtime;

import org.osgi.annotation.versioning.ProviderType;

/**
 * A JaxrsEndpoint service represents a registered JAX-RS whiteboard resource or
 * application.
 * <p>
 * It provides access to service properties representing the service, and the
 * URI at which it is available.
 * 
 * @author $Id$
 */
@ProviderType
public interface JaxrsEndpoint {

	/**
	 * A service property representing the URI(s) at which this resource or
	 * application is available.
	 */
	public static final String	JAX_RS_URI					= "osgi.jaxrs.uri";

	/**
	 * A service property providing the symbolic name of the bundle which
	 * registered the whiteboard service.
	 */
	public static final String	JAX_RS_BUNDLE_SYMBOLICNAME	= "osgi.jaxrs.bundle.symbolicname";

	/**
	 * A service property providing the bundle id of the bundle which registered
	 * the whiteboard service.
	 */
	public static final String	JAX_RS_BUNDLE_ID			= "osgi.jaxrs.bundle.id";

	/**
	 * A service property providing the bundle version of the bundle which
	 * registered the whiteboard service.
	 */
	public static final String	JAX_RS_BUNDLE_VERSION		= "osgi.jaxrs.bundle.version";

	/**
	 * A service property providing the service id of the whiteboard service.
	 */
	public static final String	JAX_RS_SERVICE_ID			= "osgi.jaxrs.service.id";
}

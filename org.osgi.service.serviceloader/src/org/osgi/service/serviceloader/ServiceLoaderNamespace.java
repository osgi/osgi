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

package org.osgi.service.serviceloader;

import org.osgi.resource.Namespace;

/**
 * Service Loader Capability and Requirement Namespace.
 * 
 * <p>
 * This class defines the names for the attributes and directives for this
 * namespace.
 * 
 * <p>
 * All unspecified capability attributes are of one of the following types:
 * <ul>
 * <li>{@code String}</li>
 * <li>{@code Version}</li>
 * <li>{@code Long}</li>
 * <li>{@code Double}</li>
 * <li>{@code List<String>}</li>
 * <li>{@code List<Version>}</li>
 * <li>{@code List<Long>}</li>
 * <li>{@code List<Double>}</li>
 * </ul>
 * and are used as arbitrary matching attributes for the capability. The values
 * associated with the specified directive and attribute keys are of type
 * {@code String}, unless otherwise indicated.
 * 
 * <p>
 * All unspecified capability attributes, unless the attribute name starts with
 * full stop ({@code '.'} &#92;u002E), are also used as service properties when
 * registering a Service Provider as a service.
 * 
 * @Immutable
 * @author $Id$
 */
public final class ServiceLoaderNamespace extends Namespace {

	/**
	 * Namespace name for service loader capabilities and requirements.
	 * 
	 * <p>
	 * Also, the capability attribute used to specify the fully qualified name
	 * of the service type.
	 */
	public static final String	SERVICELOADER_NAMESPACE			= "osgi.serviceloader";

	/**
	 * The capability directive used to specify the implementation class of the
	 * service. The value of this directive must be of type {@code String}.
	 * <p>
	 * If this directive is not specified, then all advertised Service Providers
	 * that match the service type name must be registered. If this directive is
	 * specified, then only Service Providers that match the service type name
	 * whose implementation class matches the value of this attribute must be
	 * registered. To not register a service for this capability use an empty
	 * string.
	 */
	public static final String	CAPABILITY_REGISTER_DIRECTIVE	= "register";

	private ServiceLoaderNamespace() {
		// empty
	}
}

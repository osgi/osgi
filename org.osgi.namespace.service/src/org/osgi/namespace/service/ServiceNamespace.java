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

package org.osgi.namespace.service;

import org.osgi.resource.Namespace;

/**
 * Service Capability and Requirement Namespace.
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
 * @Immutable
 * @author $Id$
 */
public final class ServiceNamespace extends Namespace {

	/**
	 * Namespace name for service capabilities and requirements.
	 */
	public static final String	SERVICE_NAMESPACE					= "osgi.service";

	/**
	 * The capability attribute used to specify the types of the service. The
	 * value of this attribute must be of type {@code List<String>}.
	 * 
	 * <p>
	 * A {@code ServiceNamespace} capability should express a
	 * {@link Namespace#CAPABILITY_USES_DIRECTIVE uses constraint} for all the
	 * packages mentioned in the value of this attribute.
	 */
	public static final String	CAPABILITY_OBJECTCLASS_ATTRIBUTE	= "objectClass";

	private ServiceNamespace() {
		// empty
	}
}

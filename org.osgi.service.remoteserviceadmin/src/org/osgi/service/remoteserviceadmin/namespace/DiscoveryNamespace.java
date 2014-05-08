/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
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

package org.osgi.service.remoteserviceadmin.namespace;

import org.osgi.resource.Namespace;

/**
 * Remote Services Discovery Provider Capability and Requirement Namespace.
 * 
 * <p>
 * This class defines the names for the attributes and directives for this
 * namespace.
 * 
 * @Immutable
 * @author $Id$
 */
public final class DiscoveryNamespace extends Namespace {

	/**
	 * Namespace name for Remote Services discovery provider capabilities and
	 * requirements.
	 */
	public static final String	DISCOVERY_NAMESPACE				= "osgi.remoteserviceadmin.discovery";

	/**
	 * The capability attribute used to specify the discovery protocols
	 * supported by this discovery provider. The value of this attribute must be
	 * of type {@code String} or {@code List<String>}.
	 */
	public static final String	CAPABILITY_PROTOCOLS_ATTRIBUTE	= "protocols";

	private DiscoveryNamespace() {
		// empty
	}
}

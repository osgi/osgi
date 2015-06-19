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
 * Remote Services Topology Manager Capability and Requirement Namespace.
 * 
 * <p>
 * This class defines the names for the attributes and directives for this
 * namespace.
 * 
 * @Immutable
 * @author $Id$
 */
public final class TopologyNamespace extends Namespace {

	/**
	 * Namespace name for Remote Services topology manager capabilities and
	 * requirements.
	 */
	public static final String	TOPOLOGY_NAMESPACE			= "osgi.remoteserviceadmin.topology";

	/**
	 * The capability attribute used to specify the policy or policies supported
	 * by this topology manager. The value of this attribute must be of type
	 * {@code String} or {@code List<String>}. Policy names are typically
	 * implementation specific, however the Remote Services Specification
	 * defines the <em>promiscuous</em> and <em>fail-over</em> policies for
	 * common use cases.
	 */
	public static final String	CAPABILITY_POLICY_ATTRIBUTE	= "policy";

	/**
	 * The attribute value for Topology managers with a promiscuous policy
	 * 
	 * @see TopologyNamespace#CAPABILITY_POLICY_ATTRIBUTE
	 */
	public static final String	PROMISCUOUS_POLICY			= "promiscuous";

	/**
	 * The attribute value for Topology managers with a fail-over policy
	 * 
	 * @see TopologyNamespace#CAPABILITY_POLICY_ATTRIBUTE
	 */
	public static final String	FAIL_OVER_POLICY			= "fail-over";

	private TopologyNamespace() {
		// empty
	}
}

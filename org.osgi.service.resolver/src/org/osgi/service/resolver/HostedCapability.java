/*
 * Copyright (c) OSGi Alliance (2012). All Rights Reserved.
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

package org.osgi.service.resolver;

import org.osgi.resource.Capability;
import org.osgi.resource.Resource;

/**
 * A HostedCapability is a tuple of a Resource with a Capability to model the
 * cases where the declared Resource of a Capability does not match the runtime
 * state. This is the case for fragments, when a fragment attaches a host, most
 * of its Capabilities and Requirements become hosted by the attached host.
 * Since a fragment can attach multiple hosts, a single Capability can actually
 * be hosted multiple times.
 * 
 * @Threadsafe
 * @version $Id$
 */
public interface HostedCapability {

	/**
	 * Return the Resource of this HostedCapability.
	 * 
	 * @return the Resource
	 */
	Resource getResource();

	/**
	 * Return the Capability of this HostedCapability.
	 * 
	 * @return the Capability
	 */
	Capability getCapability();
}

/*
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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

package org.osgi.service.discovery;

/**
 * Interface of the Discovery service. This service allows to publish services
 * exposed for remote access as well as to search for remote services. Register
 * a {@link ServicePublication} service in order to publish service metadata and
 * or a {@link DiscoveredServiceTracker} service in order to search for remote
 * services.<BR>
 * Discovery service implementations usually rely on some discovery protocols or
 * other information distribution means.
 * 
 * @version $Revision$
 */
public interface Discovery {

	/**
	 * ServiceRegistration property identifying Discovery's default strategy for
	 * distribution of published service information. It's up to the Discovery
	 * service to provide and support this property. Value of this property is
	 * of type String.
	 */
	public static final String PROP_KEY_PUBLISH_STRATEGY = "osgi.discovery.strategy.publication";

	/**
	 * Constant for a "push" publication strategy: published service information
	 * is actively pushed to the network for discovery.
	 */
	public static final String PROP_VAL_PUBLISH_STRATEGY_PUSH = "push";

	/**
	 * Constant for a "pull" publication strategy: published service information
	 * is available just upon lookup requests.
	 */
	public static final String PROP_VAL_PUBLISH_STRATEGY_PULL = "pull";
}

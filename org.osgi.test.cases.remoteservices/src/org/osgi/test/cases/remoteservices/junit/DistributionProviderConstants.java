/*
 * Copyright (c) OSGi Alliance (2008, 2009). All Rights Reserved.
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
package org.osgi.test.cases.remoteservices.junit;

/**
 * The constants described here are set on a service that identifies the
 * Distribution Provider. Every Distribution Provider registers exactly one
 * service in the local Service Registry with these properties. The service
 * provides information about the Distribution Provider through properties set
 * on the service registration.
 * <p>
 * A Distribution Provider does not have to use this service interface but must
 * register some service with the {@link #REMOTE_INTENTS_SUPPORTED} and
 * {@link #REMOTE_CONFIGS_SUPPORTED} service properties to denote the intents
 * and configuration types supported by the distribution provider.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public interface DistributionProviderConstants {
	/**
	 * The Configuration Types supported by this Distribution Provider.
	 * <p>
	 * Services that are suitable for remoting list the configuration types that
	 * describe the distribution metadata for that service in the
	 * {@link RemoteServiceConstants.SERVICE_EXPORTED_CONFIGS} property. Not
	 * every Distribution Provider will support all configuration types. The
	 * value of this property on the Distribution Provider service list the
	 * configuration types supported by this one.
	 * <p>
	 * The value of this property is of type String, String[] or Collection of
	 * String.
	 * 
	 * @see RemoteServiceConstants#SERVICE_EXPORTED_CONFIGS
	 * @see RemoteServiceConstants#SERVICE_IMPORTED_CONFIGS
	 */
	static final String REMOTE_CONFIGS_SUPPORTED = "remote.configs.supported";

	/**
	 * Service property that lists the intents supported by the distribution
	 * provider.
	 * <p>
	 * The value of this property is of type String, String[] or Collection of
	 * String.
	 * 
	 * @see RemoteServiceConstants#SERVICE_INTENTS
	 * @see RemoteServiceConstants#SERVICE_EXPORTED_INTENTS
	 * @see RemoteServiceConstants#SERVICE_EXPORTED_INTENTS_EXTRA
	 */
	static final String REMOTE_INTENTS_SUPPORTED = "remote.intents.supported";
}

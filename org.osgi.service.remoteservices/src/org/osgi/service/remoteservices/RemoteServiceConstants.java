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
package org.osgi.service.remoteservices;

import java.util.Collection;

import org.osgi.framework.ServiceReference;

/**
 * The Remote Service Admin service. This service may be registered by a
 * distribution provider to provide an interface to the distribution provider.
 * 
 * <p>
 * A distribution provider does not have to use this service interface but must
 * register some service with the {@link #REMOTE_INTENTS_SUPPORTED} and
 * {@link #REMOTE_CONFIGS_SUPPORTED} service properties to denote the intents
 * and configuration types supported by the distribution provider.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public interface RemoteServiceConstants {
	/**
	 * Service property that lists the intents supported by a distribution
	 * provider.
	 * <p>
	 * The value of this property is of type String, String[] or Collection of
	 * String.
	 */
	static final String	REMOTE_INTENTS_SUPPORTED	= "remote.intents.supported";
	/**
	 * Service property that lists the configuration types supported by a
	 * distribution provider.
	 * <p>
	 * The value of this property is of type String, String[] or Collection of
	 * String.
	 */
	static final String	REMOTE_CONFIGS_SUPPORTED	= "remote.configs.supported";

	/**
	 * The service.id of the distribution provide. ExportedEndpointDescriptions
	 * and imported service must be registered with this service property to
	 * identify the distribution provide which is managing the exported and
	 * imported services.
	 */
	static final String	DISTRIBUTION_PROVIDER_ID	= "distribution.provider.id";
}

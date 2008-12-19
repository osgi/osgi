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
 * Discovery registers a service implementing this interface. This service is
 * registered with extra properties identified at the beginning of this
 * interface to denote the name of the product providing Discovery
 * functionality, its version, vendor, used protocols etc.. 
 * 
 * Discovery allows to publish services exposed for remote access as well as to
 * search for remote services. Register a {@link ServicePublication} service in
 * order to publish service metadata and or a {@link DiscoveredServiceTracker}
 * service in order to search for remote services.<BR>
 * Discovery service implementations usually rely on some discovery protocols or
 * other information distribution means.
 * 
 * @version $Revision$
 */
public interface Discovery {

	/**
	 * Service Registration property for the name of the Discovery product.
	 */
	static final String PROP_KEY_PRODUCT_NAME = "osgi.remote.discovery.product";

	/**
	 * Service Registration property for the version of the Discovery product.
	 */
	static final String PROP_KEY_PRODUCT_VERSION = "osgi.remote.discovery.product.version";

	/**
	 * Service Registration property for the Discovery product vendor name.
	 */
	static final String PROP_KEY_VENDOR_NAME = "osgi.remote.discovery.vendor";

	/**
	 * Service Registration property that lists the discovery protocols used by
	 * this Discovery service. Value of this property is of type Collection (<?
	 * extends String>).
	 */
	static final String PROP_KEY_SUPPORTED_PROTOCOLS = "osgi.remote.discovery.supported_protocols";
}

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
package org.osgi.test.cases.scaconfigtype.common;

/**
 * Remote Service Properties. These properties are set on services that are
 * suitable for exporting remotely and/or on imported services.
 * 
 * @ThreadSafe
 * @version $Id$
 */
public interface RemoteServiceConstants {
	/**
	 * Defines the interfaces under which this service can be exported. This
	 * list must be a subset of the types listed in the objectClass service
	 * property. The single value of an asterisk (*, \u002A) indicates all
	 * interfaces in the registration's objectClass property (not classes). It
	 * is strongly recommended to only export interfaces and not concrete
	 * classes due to the complexity of creating proxies for some type of
	 * classes.
	 * <p>
	 * The value of this property is of type String, String[] or Collection of
	 * String.
	 */
	static final String SERVICE_EXPORTED_INTERFACES = "service.exported.interfaces";

	/**
	 * A list of intents that the distribution provider must implement to
	 * distribute the service. Intents listed in this property are reserved for
	 * intents that are critical for the code to function correctly, for
	 * example, ordering of messages. These intents should not be configurable.
	 * <p>
	 * The value of this property is of type String, String[] or Collection of
	 * String.
	 */
	static final String SERVICE_EXPORTED_INTENTS = "service.exported.intents";

	/**
	 * Extra intents configured in addition to the the intents specified in
	 * {@link #SERVICE_EXPORTED_INTENTS} .These intents are merged with the
	 * service.exported.intents and therefore have the same semantics. They are
	 * extra, so that the service.exported.intents can be set by the bundle
	 * developer and this property is then set by the administrator. Bundles
	 * should make this property configurable, for example through the
	 * Configuration Admin service.
	 * <p>
	 * The value of this property is of type String, String[] or Collection of
	 * String.
	 */
	static final String SERVICE_EXPORTED_INTENTS_EXTRA = "service.exported.intents.extra";

	/**
	 * A list of configuration types that should be used to export the service.
	 * Each of the configuration types should be synonymous, that is, describing
	 * the an endpoint for the same service using different technologies. A
	 * distribution provider should distribute such a service with one of the
	 * given types it recognizes. Each type has an associated specification that
	 * describes how the configuration data for the exported service is
	 * represented in an OSGi framework.
	 * <p>
	 * The value of this property is of type String, String[] or Collection of
	 * String.
	 */
	static final String SERVICE_EXPORTED_CONFIGS = "service.exported.configs";

	/**
	 * Must be set by a distribution provider to true when it registers the
	 * end-point proxy as an imported service. Can be used by a bundle to
	 * control not getting a service that is imported.
	 * <p>
	 * The value of this property is of type String or Boolean.
	 */
	static final String SERVICE_IMPORTED = "service.imported";

	/**
	 * The configuration information used to import this services, as described
	 * in {@link #SERVICE_EXPORTED_CONFIGS}. Any associated properties for this
	 * configuration types must be properly mapped to the importing system. For
	 * example, a URL in these properties must point to a valid resource when
	 * used in the importing framework.
	 * <p>
	 * The value of this property is of type String, String[] or Collection of
	 * String.
	 */
	static final String SERVICE_IMPORTED_CONFIGS = "service.imported.configs";

	/**
	 * A list of intents that this service implements. This property has dual
	 * purpose. A bundle can use this service property to notify the
	 * distribution provider that these intents are already implemented by the
	 * exported service object. For an imported service, a distribution provider
	 * must use this property to convey the combined intents of the exporting
	 * service and the intents that the distribution provider adds. To export a
	 * service, a distribution provider must recognize all these intents and
	 * expand any qualified intents.
	 * <p>
	 * The value of this property is of type String, String[] or Collection of
	 * String.
	 */
	static final String SERVICE_INTENTS = "service.intents";

	/**
	 * The <code>service.id</code> of the Distribution Provider.
	 * {@link ExportedEndpointDescription} services and imported services must be
	 * registered with this service property to identify the Distribution
	 * Provider which is managing the exported and imported services. The value
	 * of the property must be the <code>service.id</code> of the service
	 * identifying the Distribution Provider.
	 * <p>
	 * The value of this property is of type <code>Long</code>.
	 */
	static final String DISTRIBUTION_PROVIDER_ID = "distribution.provider.id";

}

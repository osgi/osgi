/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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

package org.osgi.service.remoteserviceadmin;

/**
 * Provide the definition of the constants used in the Remote Services API.
 * 
 * @Immutable
 * @version $Revision$
 */
public class RemoteConstants {
	private RemoteConstants() {
	}

	/**
	 * Service property identifying the configuration types supported by a
	 * distribution provider. Registered by the distribution provider on one of
	 * its services to indicate the supported configuration types.
	 * 
	 * <p>
	 * The value of this property must be of type <code>String</code>,
	 * <code>String[]</code>, or <code>Collection</code> of <code>String</code>.
	 */
	public static final String	REMOTE_CONFIGS_SUPPORTED		= "remote.configs.supported";

	/**
	 * Service property identifying the intents supported by a distribution
	 * provider. Registered by the distribution provider on one of its services
	 * to indicate the vocabulary of implemented intents.
	 * 
	 * <p>
	 * The value of this property must be of type <code>String</code>,
	 * <code>String[]</code>, or <code>Collection</code> of <code>String</code>.
	 */
	public static final String	REMOTE_INTENTS_SUPPORTED		= "remote.intents.supported";

	/**
	 * Service property identifying the configuration types that should be used
	 * to export the service. Each configuration type represents the
	 * configuration parameters for an endpoint. A distribution provider should
	 * create an endpoint for each configuration type that it supports.
	 * 
	 * <p>
	 * This property may be supplied in the <code>properties</code>
	 * <code>Dictionary</code> object passed to the
	 * <code>BundleContext.registerService</code> method. The value of this
	 * property must be of type <code>String</code>, <code>String[]</code>, or
	 * <code>Collection</code> of <code>String</code>.
	 */
	public static final String	SERVICE_EXPORTED_CONFIGS		= "service.exported.configs";

	/**
	 * Service property identifying the intents that the distribution provider
	 * must implement to distribute the service. Intents listed in this property
	 * are reserved for intents that are critical for the code to function
	 * correctly, for example, ordering of messages. These intents should not be
	 * configurable.
	 * 
	 * <p>
	 * This property may be supplied in the <code>properties</code>
	 * <code>Dictionary</code> object passed to the
	 * <code>BundleContext.registerService</code> method. The value of this
	 * property must be of type <code>String</code>, <code>String[]</code>, or
	 * <code>Collection</code> of <code>String</code>.
	 */
	public static final String	SERVICE_EXPORTED_INTENTS		= "service.exported.intents";

	/**
	 * Service property identifying the extra intents that the distribution
	 * provider must implement to distribute the service. This property is
	 * merged with the <code>service.exported.intents</code> property before the
	 * distribution provider interprets the listed intents; it has therefore the
	 * same semantics but the property should be configurable so the
	 * administrator can choose the intents based on the topology. Bundles
	 * should therefore make this property configurable, for example through the
	 * Configuration Admin service.
	 * 
	 * <p>
	 * This property may be supplied in the <code>properties</code>
	 * <code>Dictionary</code> object passed to the
	 * <code>BundleContext.registerService</code> method. The value of this
	 * property must be of type <code>String</code>, <code>String[]</code>, or
	 * <code>Collection</code> of <code>String</code>.
	 */
	public static final String	SERVICE_EXPORTED_INTENTS_EXTRA	= "service.exported.intents.extra";

	/**
	 * Service property marking the service for export. It defines the
	 * interfaces under which this service can be exported. This list must be a
	 * subset of the types under which the service was registered. The single
	 * value of an asterisk (&quot;*&quot;, &#92;u002A) indicates all the
	 * interface types under which the service was registered excluding the
	 * non-interface types. It is strongly recommended to only export interface
	 * types and not concrete classes due to the complexity of creating proxies
	 * for some type of concrete classes.
	 * 
	 * <p>
	 * This property may be supplied in the <code>properties</code>
	 * <code>Dictionary</code> object passed to the
	 * <code>BundleContext.registerService</code> method. The value of this
	 * property must be of type <code>String</code>, <code>String[]</code>, or
	 * <code>Collection</code> of <code>String</code>.
	 */
	public static final String	SERVICE_EXPORTED_INTERFACES		= "service.exported.interfaces";

	/**
	 * Service property identifying the service as imported. This service
	 * property must be set by a distribution provider to any value when it
	 * registers the endpoint proxy as an imported service. A bundle can use
	 * this property to filter out imported services.
	 * 
	 * <p>
	 * The value of this property may be of any type.
	 */
	public static final String	SERVICE_IMPORTED				= "service.imported";

	/**
	 * Service property identifying the configuration types used to import the
	 * service. Any associated properties for this configuration types must be
	 * properly mapped to the importing system. For example, a URL in these
	 * properties must point to a valid resource when used in the importing
	 * framework. If multiple configuration types are listed in this property,
	 * then they must be synonyms for exactly the same remote endpoint that is
	 * used to export this service.
	 * 
	 * <p>
	 * The value of this property must be of type <code>String</code>,
	 * <code>String[]</code>, or <code>Collection</code> of <code>String</code>.
	 * 
	 * @see #SERVICE_EXPORTED_CONFIGS
	 */
	public static final String	SERVICE_IMPORTED_CONFIGS		= "service.imported.configs";

	/**
	 * Service property identifying the intents that this service implement.
	 * This property has a dual purpose:
	 * <ul>
	 * <li>A bundle can use this service property to notify the distribution
	 * provider that these intents are already implemented by the exported
	 * service object.</li>
	 * <li>A distribution provider must use this property to convey the combined
	 * intents of:</li>
	 * <ul>
	 * <li>The exporting service, and</li>
	 * <li>the intents that the exporting distribution provider adds, and</li>
	 * <li>the intents that the importing distribution provider adds.</li>
	 * </ul>
	 * <i></i>
	 * 
	 * </ul> To export a service, a distribution provider must expand any
	 * qualified intents. Both the exporting and importing distribution
	 * providers must recognize all intents before a service can be distributed.
	 * 
	 * <p>
	 * The value of this property must be of type <code>String</code>,
	 * <code>String[]</code>, or <code>Collection</code> of <code>String</code>.
	 */
	public static final String	SERVICE_INTENTS					= "service.intents";

	/* above are from Ch 13 Remote Service spec. */

	/**
	 * Endpoint property identifying the URI for this endpoint. This service
	 * property must always be set.
	 * 
	 * <p>
	 * The value of this property must be of type <code>String</code>.
	 */
	public final static String	ENDPOINT_URI					= "endpoint.uri";

	/**
	 * Endpoint property identifying the service id of the exported service. Can
	 * be absent or 0 if the corresponding endpoint is not for an OSGi service.
	 * 
	 * <p>
	 * The value of this property must be of type <code>Long</code>.
	 */
	public final static String	ENDPOINT_ID						= "endpoint.id";

	/**
	 * Endpoint property identifying the universally unique id of the exporting
	 * framework. Can be absent if the corresponding endpoint is not for an OSGi
	 * service.
	 * 
	 * <p>
	 * The value of this property must be of type <code>String</code>.
	 */
	public final static String	ENDPOINT_FRAMEWORK_UUID			= "endpoint.framework.uuid";

	/**
	 * Prefix for an endpoint property identifying the interface Java package
	 * version for an interface. For example, the property
	 * endpoint.package.version.com.acme=1.3 describes the version of the
	 * package for the com.acme.Foo interface. This endpoint property for an
	 * interface package does not have to be set. If not set, the value must be
	 * assumed to be 0.
	 * 
	 * <p>
	 * Since endpoint properties are stored in a case insensitive map, case
	 * variants of a package name are folded together.
	 * 
	 * <p>
	 * The value of this property must be of type <code>String</code>.
	 */
	public final static String	ENDPOINT_PACKAGE_VERSION_		= "endpoint.package.version.";

}

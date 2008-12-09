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
 * Register a service implementing the <code>ServicePublication</code> interface
 * in order to publish metadata of a particular service (endpoint) via
 * Discovery. Metadata which has to be published is given in form of properties
 * at registration. <br>
 * In order to update published service metadata, update the properties
 * registered with the <code>ServicePublication</code> service. Depending on
 * Discovery's implementation and underlying protocol it may result in an update
 * or new re-publication of the service. <br>
 * In order to unpublish the previously published service metadata, unregister
 * the <code>ServicePublication</code> service.<br>
 * 
 * Please note that providing the {@link #PROP_KEY_SERVICE_INTERFACE_NAME}
 * property is mandatory when a <code>ServicePublication</code> service is
 * registered.<br>
 * In case a particular publication strategy should be applied when publishing
 * this service, then provide
 * {@link org.osgi.service.discovery.Discovery#PROP_KEY_PUBLISH_STRATEGY} as
 * part of registration.<br>
 * 
 * Also important is that it's not guaranteed that after registering a
 * <code>ServicePublication</code> object its service metadata is actually
 * published. Beside the fact that at least one Discovery service has to be
 * present, the provided properties have to be valid, e.g. shouldn't contain
 * case variants of the same key name, a supported publication strategy used and
 * the actual publication via Discovery mechanisms has to succeed.
 * 
 * @version $Revision: 5970 $
 */
public interface ServicePublication {

	/**
	 * Mandatory ServiceRegistration property which contains the list of
	 * interface names offered by the advertised service endpoint. Value of this
	 * property is of type String (whitespace-separated list).
	 */
	public static final String PROP_KEY_SERVICE_INTERFACE_NAME = "service.interface";

	/**
	 * Optional ServiceRegistration property which contains a list of interface
	 * names with their associated version attributes separated by a colon e.g.
	 * 'my.company.foo:1.3.5 my.company.zoo:2.3.5'. In case no version has been
	 * provided for an interface, Discovery may use the String-value of
	 * <code>org.osgi.framework.Version.emptyVersion</code> constant. <br>
	 * Value of this property is of type String (whitespace-separated list).
	 */
	public static final String PROP_KEY_SERVICE_INTERFACE_VERSION = "service.interface.version";

	/**
	 * Optional ServiceRegistration property which contains a list of interface
	 * names with their associated (non-Java) endpoint interface names separated
	 * by a colon e.g.:<br>
	 * 'my.company.foo:MyWebService my.company.zoo:MyWebService'.<br>
	 * Value of this property is of type String (whitespace-separated list).
	 */
	public static final String PROP_KEY_ENDPOINT_INTERFACE_NAME = "osgi.remote.endpoint.interface";

	/**
	 * Optional ServiceRegistration property which contains a map of properties
	 * of the published service. <br>
	 * Property keys are handled in a case insensitive manner (as OSGi Framework
	 * does). Note that Discovery might make use of certain standard properties
	 * e.g. defined by {@link ServiceEndpointDescription} for the publication
	 * process if they are provided.<br>
	 * Value of this property is of type <code>java.util.Map<code>.
	 */
	public static final String PROP_KEY_SERVICE_PROPERTIES = "service.properties";

	/**
	 * Optional property of the published service identifying its location.
	 * Value of this property is of type <code>java.net.URL<code>.
	 */
	public static final String PROP_KEY_ENDPOINT_LOCATION = "osgi.remote.endpoint.location";

	/**
	 * Optional property of the published service uniquely identifying its
	 * endpoint. Value of this property is of type <code>String<code>.
	 */
	public static final String PROP_KEY_ENDPOINT_ID = "osgi.remote.endpoint.id";
}

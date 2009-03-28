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

import org.osgi.framework.ServiceReference;

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
 * registered. Note also that a Discovery implementation may require provision
 * of additional properties, e.g. some of the standard properties defined below,
 * or may make special use of them in case they are provided. For example an
 * SLP-based Discovery might use the value provided with the
 * {@link #PROP_KEY_ENDPOINT_LOCATION} property for construction of a SLP-URL
 * used to publish the service.<br>
 * 
 * Also important is that it's not guaranteed that after registering a
 * <code>ServicePublication</code> object its service metadata is actually
 * published. Beside the fact that at least one Discovery service has to be
 * present, the provided properties have to be valid, e.g. shouldn't contain
 * case variants of the same key name, and the actual publication via Discovery
 * mechanisms has to succeed.
 * 
 * @version $Revision$
 */
public interface ServicePublication {

	/**
	 * Mandatory ServiceRegistration property which contains a collection of
	 * full qualified interface names offered by the advertised service
	 * endpoint. Value of this property is of type Collection (<? extends
	 * String>).
	 */
	public static final String	PROP_KEY_SERVICE_INTERFACE_NAME		= "service.interface";

	/**
	 * Optional ServiceRegistration property which contains a collection of
	 * interface names with their associated version attributes separated by
	 * {@link #SEPARATOR} e.g. 'my.company.foo|1.3.5 my.company.zoo|2.3.5'. In
	 * case no version has been provided for an interface, Discovery may use the
	 * String-value of <code>org.osgi.framework.Version.emptyVersion</code>
	 * constant. <br>
	 * Value of this property is of type Collection (<? extends String>).
	 */
	public static final String	PROP_KEY_SERVICE_INTERFACE_VERSION	= "service.interface.version";

	/**
	 * Optional ServiceRegistration property which contains a collection of
	 * interface names with their associated (non-Java) endpoint interface names
	 * separated by {@link #SEPARATOR} e.g.:<br>
	 * 'my.company.foo|MyWebService my.company.zoo|MyWebService'.<br>
	 * This (non-Java) endpoint interface name is usually a communication
	 * protocol specific interface, for instance a web service interface name.
	 * Though this information is usually contained in accompanying properties
	 * e.g. a wsdl file, Discovery usually doesn't read and interprets such
	 * service meta-data. Providing this information explicitly, might allow
	 * external non-Java applications find services based on this endpoint
	 * interface.
	 * 
	 * Value of this property is of type Collection (<? extends String>).
	 */
	public static final String	PROP_KEY_ENDPOINT_INTERFACE_NAME	= "osgi.remote.endpoint.interface";

	/**
	 * Optional ServiceRegistration property which contains a map of properties
	 * of the published service. <br>
	 * Property keys are handled in a case insensitive manner (as OSGi Framework
	 * does). <br>
	 * Value of this property is of type <code>java.util.Map<code>.
	 */
	public static final String	PROP_KEY_SERVICE_PROPERTIES			= "service.properties";

	/**
	 * Optional property of the published service identifying its location.
	 * Value of this property is of type <code>java.net.URL<code>.
	 */
	public static final String	PROP_KEY_ENDPOINT_LOCATION			= "osgi.remote.endpoint.location";

	/**
	 * Optional property of the published service uniquely identifying its
	 * endpoint. Value of this property is of type <code>String<code>.
	 */
	public static final String	PROP_KEY_ENDPOINT_ID				= "osgi.remote.endpoint.id";

	/**
	 * Separator constant for association of interface-specific values with the
	 * particular interface name. See also
	 * {@link #PROP_KEY_SERVICE_INTERFACE_VERSION} and
	 * {@link #PROP_KEY_ENDPOINT_INTERFACE_NAME} properties which describe such
	 * interface-specific values.
	 */
	public static final String	SEPARATOR							= "|";
	
	/**
	 * Returns the ServiceReference this publication metadata is associated with. 
	 * 
	 * @return the ServiceReference being published. Is never null.
	 */
	ServiceReference getReference();
}

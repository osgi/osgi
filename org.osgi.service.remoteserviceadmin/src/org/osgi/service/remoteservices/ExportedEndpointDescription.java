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

import org.osgi.framework.ServiceReference;

/**
 * Register a service implementing the <code>ServicePublication</code> interface
 * in order to publish metadata of a particular service (endpoint) via
 * Discovery. Metadata which has to be published is given in form of properties
 * at registration.
 * <p>
 * In order to update published service metadata, update the properties
 * registered with the <code>ServicePublication</code> service. Depending on
 * Discovery's implementation and underlying protocol it may result in an update
 * or new re-publication of the service.
 * <p>
 * In order to unpublish the previously published service metadata, unregister
 * the <code>ServicePublication</code> service.
 * <p>
 * Please note that providing the {@link #PROVIDED_INTERFACES} property is
 * mandatory when a <code>ServicePublication</code> service is registered. Note
 * also that a Discovery implementation may require provision of additional
 * properties, e.g. some of the standard properties defined below, or may make
 * special use of them in case they are provided. For example an SLP-based
 * Discovery might use the value provided with the {@link #ENDPOINT_URI}
 * property for construction of a SLP-URL used to publish the service.
 * <p>
 * Also important is that it's not guaranteed that after registering a
 * <code>ServicePublication</code> object its service metadata is actually
 * published. Beside the fact that at least one Discovery service has to be
 * present, the provided properties have to be valid, e.g. shouldn't contain
 * case variants of the same key name, and the actual publication via Discovery
 * mechanisms has to succeed.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public interface ExportedEndpointDescription extends EndpointDescription {

	/**
	 * Mandatory ServiceRegistration property which contains a collection of
	 * full qualified interface names offered by the advertised service
	 * endpoint.
	 * <p>
	 * Value of this property is of type
	 * <code>Collection (&lt;String&gt;)</code>.
	 * 
	 * @see EndpointDescription#getInterfaceDescriptions()
	 */
	static final String	PROVIDED_INTERFACES	= "provided.interfaces";

	/**
	 * Optional property of the published service identifying its location.
	 * <p>
	 * Value of this property is of type <code>String</code> and is the toString
	 * of the URI.
	 * 
	 * Property is not set if there is no URI.
	 * 
	 * @see EndpointDescription#getURI()
	 */
	static final String	ENDPOINT_URI		= "endpoint.uri";

	/**
	 * Returns the <code>ServiceReference</code> this publication metadata is
	 * associated with.
	 * 
	 * @return the <code>ServiceReference</code> being published. Is never
	 *         <code>null</code>.
	 */
	ServiceReference getLocalServiceReference();

}

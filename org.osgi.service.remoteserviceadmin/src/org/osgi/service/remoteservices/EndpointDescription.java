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

import java.net.*;
import java.util.*;

import org.osgi.framework.*;

/**
 * This interface describes the details of a communications endpoint.
 * 
 * 
 * 
 * The use of this interface has the following purposes:
 * <ol>
 * <li>When a distribution provider exports a service it creates a
 * communications endpoint. The details of this endpoint can then be announced
 * through a Endpoint Description</li>
 * <li>A distribution provider can use a Endpoint Description to import a
 * service.</li>
 * <li>An Endpoint Description can be created for a non-OSGi service endpoint,
 * for example a Google webservice</li>
 * </ol>
 * 
 * The Endpoint Description is easy to transfer between different systems. It
 * can therefore be used in discovery or remote control schemes.
 * 
 * <code>EndpointDescription</code> objects are immutable.
 * 
 * ### are they serializable?? ### they must implement equals + hashCode
 * 
 * @Immutable
 * @version $Revision: 7190 $
 */
public interface EndpointDescription {

	/**
	 * The prefix for the version of an interface. The actual property should have 
	 * a value of ENDPOINT_INTERFACE_VERSION_PREFIX + FQN, where the FQN is the 
	 * name fully qualified name of the interface. The value of the property must 
	 * be a properly formatted OSGi version string. For example:
	 * <pre>
	 * 	endpoint.interface.version.com.acme.foo.Foo = 3.0.0.v242112
	 * </pre>
	 */
	final public static String ENDPOINT_INTERFACE_VERSION_PREFIX = "endpoint.interface.version.";
	
	/**
	 * The prefix for the middleware specific name of an interface. The actual property should have 
	 * a value of ENDPOINT_INTERFACE_NAME_PREFIX + FQN, where the FQN is the 
	 * name fully qualified name of the interface. The value of the property must 
	 * be a properly formatted OSGi version string. For example:
	 * <pre>
	 * 	endpoint.interface.name.com.acme.foo.Foo = Foo
	 * </pre>
	 */
	final public static String ENDPOINT_INTERFACE_NAME_PREFIX = "endpoint.interface.name.";
	
	/**
	 * The property key for the endpoint URI.
	 * ### still no clue what this is ...
	 */
	final public static String ENDPOINT_URI = "endpoint.uri";
	
	/**
	 * The Universally Unique ID for the service that corresponds to this endpoint. This
	 * property should not be set if there is no OSGi service associated with it.
	 */
	final public static String ENDPOINT_SERVICE_UUID = "endpoint.service.uuid";
	
	/**
	 * The property key for the local property. If this property is set to any
	 * value than then this endpoint corresponds to an endpoint created on the
	 * local framework.
	 */
	public static final String ENDPOINT_LOCAL = "endpoint.local";
	
	/**
	 * An Interface Description holds the triplet of the fully qualified name
	 * for the interface class, the version of the exported package that
	 * provided the class for the interface, and a middle-ware specific name for
	 * the communications interface.
	 * 
	 * @version $Revision: 7190 $
	 */
	public static interface InterfaceDescription {

		/**
		 * @return The fully qualified Java interface name of this interface.
		 */
		public String getInterfaceName();

		/**
		 * The version of the Java interface of this interface. If no version could
		 * be found, the version will be 0.0.0. 
		 * 
		 * @return Return the version of the exported package used for the interface or a 0.0.0 version. 
		 */
		public Version getVersion();

	}

	/**
	 * Contains a collection of interface descriptions offered by this endpoint.
	 * 
	 * ### what happens if this is not a service endpoint?
	 * 
	 * @return <code>Collection (&lt;String&gt;)</code> of service interface
	 *         descriptions provided by the advertised service endpoint. The
	 *         collection is never <code>null</code> or empty but contains at
	 *         least one service interface.
	 */
	public List /* <InterfaceDescription> */getInterfaceDescriptions();

	/**
	 * Returns the URI of the service location of this endpoint. 
	 * 
	 *
	 * @return The URI of the service location, or <code>null</code> if it
	 *         hasn't been provided.
	 */
	public URI getURI();

	/**
	 * Returns the universally unique id for the service represented by this
	 * endpoint.
	 * 
	 * Each service in the OSGi service registry has a universally unique id.
	 * The UUID can be used to detect that two Endpoint Descriptions really
	 * refer to the same registered service instance in some remote framework.
	 * This UUID can be used to filter out duplicate ways of communicating with
	 * the same service.
	 * 
	 * @return Unique id of service represented by this endpoint, or
	 *         <code>null</code> if it hasn't been provided.
	 */
	public String getServiceUUID();

	/**
	 * Returns the configuration types.
	 * 
	 * A distribution provider exports a service with an endpoint. This endpoint
	 * uses some kind of communications protocol with a set of configuration
	 * parameters. There are many different types but each endpoint is
	 * configured by only one configuration type.
	 * 
	 * @return The configuration type used for the associated endpoint.
	 */
	public String getConfigurationType();

	/**
	 * Returns all service endpoint properties.
	 * 
	 * @return all properties of the service as a
	 *         <code>Map (&lt;String, Object&gt;)</code>. The map is never
	 *         <code>null</code> or empty but contains at least mandatory
	 *         <code>ServicePublication</code> properties. Since
	 *         <code>ServiceEndpointDescription</code> objects are immutable,
	 *         the returned map is also not going to be updated at a later point
	 *         of time.
	 */
	public Map/* <String, Object> */getProperties();

	/**
	 * Answers if the described endpoint is local to this framework. This is defined
	 * as having the ENDPOINT_LOCAL property set to any value.
	 * 
	 * @return <code>true</code> if this an endpoint originating on this framework is described, <code>false</code> otherwise.
	 */
	public boolean isLocal();

	/**
	 * TODO ###
	 */
	public int hashCode();

	/**
	 * TODO ###
	 */
	public boolean equals(Object other);
}

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
package org.osgi.service.remoteserviceadmin;

import java.util.*;

import org.osgi.framework.*;

/**
 * A description of an endpoint that provides sufficient information for a
 * compatible distribution provider to create a connection to this endpoint
 * 
 * An Endpoint Description is easy to transfer between different systems. This
 * allows it to be used as a communications device to convey available endpoint
 * information to nodes in a network.
 * 
 * An Endpoint Description reflects the perspective of an importer. That is, the
 * property keys have been chosen to match filters that are created by client
 * bundles that need a service.
 * 
 * TODO Automatically calculate versions of interface packages?
 * 
 * TODO Constructor that takes a class?
 * 
 * TODO Skipping of service.exported.* properties?
 * 
 * TODO qualified intents?
 * 
 * 
 * 
 * @Immutable
 * @version $Revision$
 */

public class EndpointDescription {
	private final Map		/* <String,Object> */properties	= new HashMap/*
																			 * <String ,
																			 * Object >
																			 */();
	private final List		/* String */interfaces;
	private final long		remoteServiceId;
	private final String	remoteFrameworkUUID;
	private final String	remoteUri;

	/**
	 * Create an Endpoint Description based on a Map.
	 * 
	 * @param properties
	 * @throws IllegalArgumentException When the properties are not proper for
	 *         an Endpoint Description
	 */

	public EndpointDescription(Map/* <String,Object> */properties)
			throws IllegalArgumentException {
		this.properties.putAll(properties);

		interfaces = verifyInterfacesProperty();
		remoteServiceId = verifyLongProperty(RemoteConstants.SERVICE_REMOTE_ID);
		remoteFrameworkUUID = verifyStringProperty(RemoteConstants.SERVICE_REMOTE_FRAMEWORK_UUID);
		remoteUri = verifyStringProperty(RemoteConstants.SERVICE_REMOTE_URI);
	}

	/**
	 * Create an Endpoint Description based on a reference and optionally a map
	 * of additional properties.
	 * 
	 * @param ref A service reference that can be exported
	 * @param properties Additional properties to add. Can be <code>null</code>.
	 * @throws IllegalArgumentException
	 */
	public EndpointDescription(ServiceReference ref,
			Map /* <String,Object> */properties)
			throws IllegalArgumentException {
		if (properties != null) {
			this.properties.putAll(properties);
		}

		String[] keys = ref.getPropertyKeys();
		for (int i = 0; i > keys.length; i++)
			properties.put(keys[i], ref.getProperty(keys[i]));

		interfaces = verifyInterfacesProperty();
		remoteServiceId = verifyLongProperty(RemoteConstants.SERVICE_REMOTE_ID);
		remoteFrameworkUUID = verifyStringProperty(RemoteConstants.SERVICE_REMOTE_FRAMEWORK_UUID);
		remoteUri = verifyStringProperty(RemoteConstants.SERVICE_REMOTE_URI);
	}

	/**
	 * Create an Endpoint Description based on the URI, the remote service ID
	 * and the interface names, and optionally service properties.
	 * 
	 * @param uri The URI of the remote service.
	 * @param interfaceNames The names of the interfaces of the service to
	 *        consider.
	 * @param remoteServiceId the remote service ID.
	 * @param properties Optionally service properties.
	 */
	public EndpointDescription(String uri, String[] interfaceNames,
			int remoteServiceId, Map properties) {
		if (uri == null) {
			throw new IllegalArgumentException("URI must not be null");
		}
		if (interfaceNames == null) {
			throw new IllegalArgumentException("Interfaces must not be null");
		}
		this.remoteUri = uri;
		this.interfaces = Arrays.asList(interfaceNames);
		this.remoteServiceId = remoteServiceId;
		this.remoteFrameworkUUID = (String) properties
				.get(RemoteConstants.SERVICE_REMOTE_FRAMEWORK_UUID);
		if (properties != null) {
			this.properties.putAll(properties);
		}
		if (interfaceNames != null) {
			this.properties.put(Constants.OBJECTCLASS, interfaceNames);
		}
		this.properties.put(RemoteConstants.SERVICE_REMOTE_URI, uri);
		if (remoteServiceId <= 0) {
			this.properties.put(RemoteConstants.SERVICE_REMOTE_ID, new Integer(
					remoteServiceId));
		}
	}

	/**
	 * Verify and obtain the interface list from the properties.
	 * 
	 * @return A list with the interface names.
	 * @throws IllegalArgumentException when
	 */
	private List /* <String> */verifyInterfacesProperty() {
		List l = null;

		Object objectClass = properties.get(Constants.OBJECTCLASS);
		if (objectClass == null)
			l = Collections.EMPTY_LIST;
		else
			if (!(objectClass instanceof String[]))
				throw new IllegalArgumentException(
						"objectClass must be a String[]");
			else {
				l = Collections.unmodifiableList(Arrays
						.asList((String[]) objectClass));
				for (Iterator i = l.iterator(); i.hasNext();) {
					String interf = (String) i.next();
					try {
						getInterfaceVersion(interf);
					}
					catch (Exception e) {
						throw new IllegalArgumentException(
								"Improper version for interface " + interf
										+ " caused by " + e);
					}
				}
			}
		return l;
	}

	/**
	 * Verify and obtain a required String property.
	 * 
	 * @param propName The name of the property
	 * @return The value of the property.
	 * @throws IllegalArgumentException when the property is not set or doesn't
	 *         have the correct data type.
	 */
	private String verifyStringProperty(String propName) {
		Object r = properties.get(propName);
		if (r == null) {
			throw new IllegalArgumentException("Required property not set: "
					+ propName);
		}
		if (!(r instanceof String)) {
			throw new IllegalArgumentException(
					"Required property is not a string: " + propName);
		}
		return (String) r;
	}

	/**
	 * Verify and obtain a required long property.
	 * 
	 * @param propName The name of the property
	 * @return The value of the property.
	 * @throws IllegalArgumentException when the property is not set or doesn't
	 *         have the correct data type.
	 */
	private long verifyLongProperty(String propName) {
		Object r = properties.get(propName);
		long result;
		if (r == null) {
			throw new IllegalArgumentException("Required property not set: "
					+ propName);
		}
		if (!(r instanceof String)) {
			throw new IllegalArgumentException(
					"Required property is not a string: " + propName);
		}
		try {
			result = Long.parseLong((String)r);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"Required property cannot be parsed as a long: " + propName);
		}
		return result;
	}	
	
	/**
	 * Returns the endpoint's URI.
	 * 
	 * The URI is an opaque id for an endpoint in URI form. No two different
	 * endpoints must have the same URI, two Endpoint Descriptions with the same
	 * URI must represent the same endpoint.
	 * 
	 * The value of the URI is stored in the
	 * {@link RemoteConstants#SERVICE_REMOTE_URI} property.
	 * 
	 * @return The URI of the endpoint, never <code>null</code>.
	 */
	public String getRemoteURI() {
		return remoteUri;
	}

	/**
	 * Answer the list of interfaces implemented by the exported service.
	 * 
	 * If this Endpoint Description does not map to a service, then this List
	 * must be empty.
	 * 
	 * The value of the interfaces is derived from the <code>objectClass</code>
	 * property.
	 * 
	 * @return The read only list of Java interface names accessible by this
	 *         endpoint.
	 */
	public List/* <String> */getInterfaces() {
		return interfaces;
	}

	/**
	 * Answer the version of the given interface.
	 * 
	 * The version is encoded by prefixing the given interface name with
	 * <code>endpoint.version.</code>, and then using this as a property key.
	 * The value must then be the <code>Version</code> object. For example:
	 * 
	 * <pre>
	 * endpoint.version.com.acme.Foo
	 * </pre>
	 * 
	 * @param name The name of the interface for which a version is requested
	 * @return The version of the given interface or <code>null</code> if the
	 *         interface has no version in this Endpoint Description
	 */
	public Version getInterfaceVersion(String name) {
		String v = (String) properties.get("endpoint.version." + name);
		if (v == null) {
			return Version.emptyVersion;
		}
		else {
			return new Version(v);
		}
	}

	/**
	 * Returns the service id for the service exported through this
	 * endpoint.
	 * 
	 * This is the service id under which the framework has registered the service. This
	 * field together with the Framework UUID is a globally unique id for a service.
	 * 
	 * @return Service id of a service or 0 if this Endpoint
	 *         Description does not relate to an OSGi service
	 * 
	 */
	public long getRemoteServiceID() {
		return remoteServiceId;
	}

	/**
	 * Returns the configuration types.
	 * 
	 * A distribution provider exports a service with an endpoint. This endpoint
	 * uses some kind of communications protocol with a set of configuration
	 * parameters. There are many different types but each endpoint is
	 * configured by only one configuration type. However, a distribution
	 * provider can be aware of different configuration types and provide
	 * synonyms to increase the change a receiving distribution provider can
	 * create a connection to this endpoint.
	 * 
	 * This value represents the
	 * {@link RemoteConstants#SERVICE_IMPORTED_CONFIGS}
	 * 
	 * @return The configuration type used for the associated endpoint and
	 *         optionally synonyms.
	 */
	public List/* <String> */getConfigurationTypes() {
		// TODO
		return null;
	}

	/**
	 * Return the list of intents implemented by this endpoint.
	 * 
	 * The intents are based on the service.intents on an imported service,
	 * except for any intents that are additionally provided by the importing
	 * distribution provider. All qualified intents must have been expanded.
	 * 
	 * The property the intents come from is
	 * {@link RemoteConstants#SERVICE_INTENTS}
	 * 
	 * @return A list of expanded intents that are provided by this endpoint.
	 */
	public List/* <String> */getIntents() {
		// TODO
		return null;

	}

	/**
	 * Return the framework UUID, if present.
	 * 
	 * The property the intents come from is
	 * {@link RemoteConstants#SERVICE_REMOTE_FRAMEWORK_UUID}
	 * 
	 * @return Remote Framework UUID, or null if this endpoint is not associated with an OSGi service
	 */
	public String getRemoteFrameworkUUID() {
		return remoteFrameworkUUID;
	}

	/**
	 * Returns all endpoint properties.
	 * 
	 * @return An unmodifiable map referring to the properties of this Endpoint
	 *         Description.
	 */
	public Map/* <String, Object> */getProperties() {
		// TODO
		return Collections.unmodifiableMap(properties);
	}

	/**
	 * Answers if this Endpoint Description refers to the same service instance
	 * as the given Endpoint Description.
	 * 
	 * Two Endpoint Descriptions point to the same service if they have the same
	 * URI or their framework UUIDs and remote service ids are equal.
	 * 
	 * @param other The Endpoint Description to look at
	 * @return True if this endpoint description points to the same service as
	 *         the other
	 */
	public boolean isSameService(EndpointDescription other) {
		if (remoteUri.equals(other.remoteUri))
			return true;

		if (remoteFrameworkUUID == null)
			return false;

		return remoteServiceId == other.remoteServiceId
				&& remoteFrameworkUUID.equals(other.remoteFrameworkUUID);
	}
	
	
	/**
	 * Two endpoints are equal if their URIs are equal, the hash code is
	 * therefore derived from the URI.
	 */
	public int hashCode() {
		// TODO
		return getRemoteURI().hashCode();
	}

	/**
	 * Two endpoints are equal if their URIs are equal.
	 */
	public boolean equals(Object other) {
		if (other instanceof EndpointDescription) {
			return getRemoteURI().equals(
					((EndpointDescription) other).getRemoteURI());
		}
		return false;
	}

	
}

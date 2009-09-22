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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

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
 * @Immutable
 * @version $Revision$
 */
/*
 * TODO Why is this serializable? That makes the serializable form public API!
 * There is no serializable code in here. You really want the default
 * serialization of HashMap for the properties and whatever concrete list that
 * interfaces holds? Also, since this class is immutable, you can also be
 * serializable without mucho effort and care.
 */
public class EndpointDescription implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Map			/* <String,Object> */properties	= new HashMap/*
																			 * <String
																			 * ,
																			 * Object
																			 * >
																			 */();
	private final List			/* String */interfaces;
	private final String		remoteServiceId;
	private final String		uri;

	/**
	 * Create an Endpoint Description based on a Map.
	 * 
	 * @param properties
	 * @throws IllegalArgumentException
	 *             When the properties are not proper for an Endpoint
	 *             Description
	 */

	public EndpointDescription(Map/* <String,Object> */properties)
			throws IllegalArgumentException {
		this.properties.putAll(properties);

		interfaces = verifyInterfacesProperty();
		remoteServiceId = verifyStringProperty(RemoteConstants.ENDPOINT_REMOTE_SERVICE_ID);
		uri = verifyStringProperty(RemoteConstants.ENDPOINT_URI);
	}

	/**
	 * Create an Endpoint Description based on a reference and optionally a map
	 * of additional properties.
	 * 
	 * @param ref
	 *            A service reference that is exportable
	 * @param properties
	 *            Additional properties to add. Can be <code>null</code>.
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
		remoteServiceId = verifyStringProperty(RemoteConstants.ENDPOINT_REMOTE_SERVICE_ID);
		uri = verifyStringProperty(RemoteConstants.ENDPOINT_URI);
	}

	/**
	 * Create an Endpoint Description based on the uri, the remote service ID
	 * and the interface names, and optionally service properties.
	 * 
	 * @param uri
	 *            The uri of the remote service.
	 * @param interfaceNames
	 *            The names of the interfaces of the service to consider.
	 * @param remoteServiceId
	 *            the remote service ID.
	 * @param properties
	 *            Optionally service properties.
	 */
	public EndpointDescription(String uri,
			String[] interfaceNames,  String remoteServiceId, Map properties) {
		if (uri == null) {
			throw new IllegalArgumentException("URI must not be null");
		}
		if (interfaceNames == null) {
			throw new IllegalArgumentException("Interfaces must not be null");
		}
		this.uri = uri;
		this.interfaces = Arrays.asList(interfaceNames);
		this.remoteServiceId = remoteServiceId;
		if (properties != null) {
			this.properties.putAll(properties);
		}
		if (interfaceNames != null) {
			this.properties.put(Constants.OBJECTCLASS, interfaceNames);
		}
		this.properties.put(RemoteConstants.ENDPOINT_URI, uri);
		if (remoteServiceId != null) {
		this.properties.put(RemoteConstants.ENDPOINT_REMOTE_SERVICE_ID,
				remoteServiceId);
		}
	}

	/**
	 * Verify and obtain the interface list from the properties.
	 * 
	 * @return A list with the interface names.
	 * @throws IllegalArgumentException
	 *             when
	 */
	private List /* <String> */verifyInterfacesProperty() {
		List l = null;

		Object objectClass = properties.get(Constants.OBJECTCLASS);
		if (objectClass == null)
			l = Collections.EMPTY_LIST;
		else if (!(objectClass instanceof String[]))
			throw new IllegalArgumentException("objectClass must be a String[]");
		else {
			l = Collections.unmodifiableList(Arrays
					.asList((String[]) objectClass));
			for (Iterator i = l.iterator(); i.hasNext();) {
				String interf = (String) i.next();
				try {
					getInterfaceVersion(interf);
				} catch (Exception e) {
					throw new IllegalArgumentException(
							"Improper version for interface " + interf
									+ " caused by " + e);
				}
			}
		}
		return l;
	}

	/**
	 * Verify and obtain the a required String property.
	 * 
	 * @param propName
	 *            The name of the
	 * @return The value of the property.
	 * @throws IllegalArgumentException
	 *             when the property is not set or doesn't have the correct data
	 *             type.
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
	 * Returns the endpoint's URI.
	 * 
	 * The URI is an opaque id for an endpoint in URI form. No two different
	 * endpoints must have the same URI, two Endpoint Descriptions with the same
	 * URI must represent the same endpoint.
	 * 
	 * The value of the URI is stored in the
	 * {@link RemoteConstants#ENDPOINT_URI} property.
	 * 
	 * @return The URI of the endpoint, never null.
	 */
	public String getURI() {
		return uri;
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
	 * @param name
	 *            The name of the interface for which a version is requested
	 * @return The version of the given interface or <code>null</code> if the
	 *         interface has no version in this Endpoint Description
	 */
	public Version getInterfaceVersion(String name) {
		String v = (String) properties.get("endpoint.version." + name);
		if (v == null) {
			return Version.emptyVersion;
		} else {
			return new Version(v);
		}
	}

	/**
	 * Returns the universally unique id for the service exported through this
	 * endpoint.
	 * 
	 * Each service in the OSGi service registry has a universally unique id.
	 * The UUID can be used to detect that two Endpoint Descriptions really
	 * refer to the same registered service instance in some remote framework.
	 * This UUID can be used to filter out duplicate ways of communicating with
	 * the same service.
	 * 
	 * The service UUID is constructed from two properties. It is first the
	 * <code>org.osgi.framework.uuid</code> System property set by the framework
	 * or through configuration. This property must uniquely represents the UUID
	 * of a framework instance. This UUID must not contain any dots ('.'
	 * \u002E). This is suffixed with a dot and then the <code>service.id</code>
	 * service property of the service.
	 * 
	 * For example:
	 * 
	 * <pre>
	 *   72dc5fd9-5f8f-4f8f-9821-9ebb433a5b72.121
	 * </pre>
	 * 
	 * If this Endpoint Description does not map to a remote OSGi service, for
	 * example some web service, then the Endpoint Description must not have a
	 * service UUID. If two endpoints have the same URI, then they must refer to
	 * the same OSGi service.
	 * 
	 * Starting . is not an OSGi service.
	 * 
	 * @return Unique id of a service or <code>null</code> if this Endpoint
	 *         Description does not relate to an OSGi service
	 * 
	 */
	public String getRemoteServiceID() {
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
	 * Returns all endpoint properties.
	 * 
	 * @return An immutable map referring to the properties of this Endpoint
	 *         Description.
	 */
	public Map/* <String, Object> */getProperties() {
		// TODO
		return Collections.unmodifiableMap(properties);
	}

	/**
	 * Two endpoints are equal if their URIs are equal, the hash code is
	 * therefore derived from the URI.
	 */
	public int hashCode() {
		// TODO
		return getURI().hashCode();
	}

	/**
	 * Two endpoints are equal if their URIs are equal.
	 */
	public boolean equals(Object other) {
		if (other instanceof EndpointDescription) {
			return getURI().equals(((EndpointDescription) other).getURI());
		}
		return false;
	}

}

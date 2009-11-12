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

import static org.osgi.service.remoteserviceadmin.RemoteConstants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
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
 * bundles that need a service. Therefore the map must not contain any
 * service.exported.* property and must contain the service.imported.* ones.
 * 
 * The service.intents property contains the intents provided by the service
 * itself combined with the intents added by the exporting distribution
 * provider. Qualified intents appear expanded on this property.
 * 
 * @Immutable
 * @version $Revision$
 */

public class EndpointDescription {
	private final Map<String, Object>	properties;
	private final List<String>			interfaces;
	private final long					remoteServiceId;
	private final String				remoteFrameworkUUID;
	private final String				remoteUri;

	/**
	 * Create an Endpoint Description based on a Map.
	 * 
	 * @param properties The map to create the Endpoint Description from.
	 * @throws IllegalArgumentException When the properties are not proper for
	 *         an Endpoint Description
	 */

	public EndpointDescription(Map<String, Object> properties) {
		this(properties, null);
		if (properties == null) {
			throw new NullPointerException("properties must not be null");
		}
	}

	/**
	 * Create an Endpoint Description based on a reference and optionally a map
	 * of additional properties. The properties on the original service take
	 * precedence over the ones in the map.
	 * 
	 * @param reference A service reference that can be exported
	 * @param properties Additional properties to add. Can be <code>null</code>.
	 * @throws IllegalArgumentException When the properties are not proper for
	 *         an Endpoint Description
	 */
	public EndpointDescription(ServiceReference reference,
			Map<String, Object> properties) {
		this(properties, reference);
		if (reference == null) {
			throw new NullPointerException("reference must not be null");
		}
	}

	private EndpointDescription(Map<String, Object> map,
			ServiceReference reference) {
		Map<String, Object> props = new TreeMap<String, Object>(
				String.CASE_INSENSITIVE_ORDER);

		if (map != null) {
			try {
				props.putAll(map);
			}
			catch (ClassCastException e) {
				IllegalArgumentException iae = new IllegalArgumentException(
						"non-String key in properties");
				iae.initCause(e);
				throw iae;
			}
			if (props.size() < map.size()) {
				throw new IllegalArgumentException(
						"duplicate keys with different cases in properties");
			}
		}

		if (reference != null) {
			for (String key : reference.getPropertyKeys()) {
				if (!props.containsKey(key)) {
					props.put(key, reference.getProperty(key));
				}
			}
		}

		properties = Collections.unmodifiableMap(props);
		/* properties must be initialized before calling the following methods */
		interfaces = verifyObjectClassProperty();
		remoteServiceId = verifyLongProperty(ENDPOINT_ID);
		remoteFrameworkUUID = verifyStringProperty(ENDPOINT_FRAMEWORK_UUID);
		remoteUri = verifyStringProperty(ENDPOINT_URI);
	}

	/**
	 * Verify and obtain the interface list from the properties.
	 * 
	 * @return A list with the interface names.
	 * @throws IllegalArgumentException When the properties do not contain the
	 *         right values for and interface list.
	 * 
	 */
	private List<String> verifyObjectClassProperty() {
		Object o = properties.get(Constants.OBJECTCLASS);
		if (o == null) {
			return Collections.EMPTY_LIST;
		}
		if (!(o instanceof String[])) {
			throw new IllegalArgumentException(
					"objectClass must be of type String[]");
		}
		String[] objectClass = (String[]) o;
		for (String interf : objectClass) {
			try {
				getInterfaceVersion(interf);
			}
			catch (IllegalArgumentException e) {
				IllegalArgumentException iae = new IllegalArgumentException(
						"Improper version for interface " + interf);
				iae.initCause(e);
				throw iae;
			}
		}
		return Collections.unmodifiableList(Arrays.asList(objectClass));
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
					"Required property is not a String: " + propName);
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
		if (r == null) {
			throw new IllegalArgumentException("Required property not set: "
					+ propName);
		}
		if (!(r instanceof String)) {
			throw new IllegalArgumentException(
					"Required property is not a string: " + propName);
		}
		try {
			return Long.parseLong((String) r);
		}
		catch (NumberFormatException e) {
			IllegalArgumentException iae = new IllegalArgumentException(
					"Required property cannot be parsed as a long: " + propName);
			iae.initCause(e);
			throw iae;
		}
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
	 * @return The URI of the endpoint, never <code>null</code>.
	 */
	public String getRemoteURI() {
		return remoteUri;
	}

	/**
	 * Provide the list of interfaces implemented by the exported service.
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
	public List<String> getInterfaces() {
		return interfaces;
	}

	/**
	 * Provide the version of the given interface.
	 * 
	 * The version is encoded by prefixing the given interface name with
	 * <code>endpoint.interface.version.</code>, and then using this as an
	 * endpoint property key. For example:
	 * 
	 * <pre>
	 * endpoint.interface.version.com.acme.Foo
	 * </pre>
	 * 
	 * The value of this property is in String format and will be converted to a
	 * <code>Version</code> object by this method.
	 * 
	 * @param name The name of the interface for which a version is requested.
	 * @return The version of the given interface or <code>null</code> if the
	 *         interface has no version in this Endpoint Description.
	 * @throws IllegalArgumentException If the version property value is not
	 *         String.
	 */
	public Version getInterfaceVersion(String name) {
		String key = ENDPOINT_INTERACE_VERSION_ + name;
		Object version = properties.get(key);
		if (!(version instanceof String)) {
			throw new IllegalArgumentException(key
					+ " property is not a String");
		}
		return Version.parseVersion((String) version);
	}

	/**
	 * Returns the service id for the service exported through this endpoint.
	 * 
	 * This is the service id under which the framework has registered the
	 * service. This field together with the Framework UUID is a globally unique
	 * id for a service.
	 * 
	 * The value of the remote service id is stored in the
	 * {@link RemoteConstants#ENDPOINT_ID} endpoint property.
	 * 
	 * @return Service id of a service or 0 if this Endpoint Description does
	 *         not relate to an OSGi service
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
	 * This value of the configuration types is stored in the
	 * {@link RemoteConstants#SERVICE_IMPORTED_CONFIGS} service property.
	 * 
	 * @return An unmodifiable list of the configuration types used for the
	 *         associated endpoint and optionally synonyms.
	 */
	public List<String> getConfigurationTypes() {
		return getStringPlusProperty(SERVICE_IMPORTED_CONFIGS);
	}

	/**
	 * Return the list of intents implemented by this endpoint.
	 * 
	 * The intents are based on the service.intents on an imported service,
	 * except for any intents that are additionally provided by the importing
	 * distribution provider. All qualified intents must have been expanded.
	 * 
	 * This value of the intents is stored in the
	 * {@link RemoteConstants#SERVICE_INTENTS} service property.
	 * 
	 * @return An unmodifiable list of expanded intents that are provided by
	 *         this endpoint.
	 */
	public List<String> getIntents() {
		return getStringPlusProperty(SERVICE_INTENTS);
	}

	/**
	 * Reads a 'String+' property from the properties map, which may be of type
	 * String, String[] or Collection<String> and returns it as an unmodifiable
	 * List.
	 * 
	 * @param key The property
	 * @return An unmodifiable list
	 */
	private List<String> getStringPlusProperty(String key) {
		Object value = properties.get(key);
		if (value == null) {
			return Collections.EMPTY_LIST;
		}

		if (value instanceof String) {
			return Collections.singletonList((String) value);
		}

		if (value instanceof String[]) {
			String[] values = (String[]) value;
			List<String> result = new ArrayList<String>(values.length);
			for (String v : values) {
				if (v != null) {
					result.add(v);
				}
			}
			return Collections.unmodifiableList(result);
		}

		if (value instanceof Collection< ? >) {
			Collection< ? > values = (Collection< ? >) value;
			List<String> result = new ArrayList<String>(values.size());
			for (Iterator< ? > iter = values.iterator(); iter.hasNext();) {
				Object v = iter.next();
				if ((v != null) && (v instanceof String)) {
					result.add((String) v);
				}
			}
			return Collections.unmodifiableList(result);
		}

		return Collections.EMPTY_LIST;
	}

	/**
	 * Return the framework UUID for the remote service, if present.
	 * 
	 * The value of the remote framework uuid is stored in the
	 * {@link RemoteConstants#ENDPOINT_FRAMEWORK_UUID} endpoint property.
	 * 
	 * @return Remote Framework UUID, or null if this endpoint is not associated
	 *         with an OSGi service
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
	public Map<String, Object> getProperties() {
		return properties;
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
		if (this.equals(other)) {
			return true;
		}

		if (getRemoteFrameworkUUID() == null) {
			return false;
		}

		return (this.getRemoteServiceID() == other.getRemoteServiceID())
				&& this.getRemoteFrameworkUUID().equals(
						other.getRemoteFrameworkUUID());
	}

	/**
	 * Returns a hash code value for the object.
	 * 
	 * @return An integer which is a hash code value for this object.
	 */
	public int hashCode() {
		return getRemoteURI().hashCode();
	}

	/**
	 * Compares this <code>EndpointDescription</code> object to another object.
	 * 
	 * <p>
	 * An Endpoint Description is considered to be <b>equal to</b> another
	 * Endpoint Description if their URIs are equal.
	 * 
	 * @param other The <code>EndpointDescription</code> object to be compared.
	 * @return <code>true</code> if <code>object</code> is a
	 *         <code>EndpointDescription</code> and is equal to this object;
	 *         <code>false</code> otherwise.
	 */
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof EndpointDescription)) {
			return false;
		}
		return getRemoteURI().equals(
				((EndpointDescription) other).getRemoteURI());
	}

	/**
	 * Tests the properties of this <code>EndpointDescription</code> against the
	 * given filter using a case insensitive match.
	 * 
	 * @param filter The filter to test.
	 * @return <code>true</code> If the properties of this
	 *         <code>EndpointDescription</code> match the filter,
	 *         <code>false</code> otherwise.
	 * @throws IllegalArgumentException If <code>filter</code> contains an
	 *         invalid filter string that cannot be parsed.
	 */
	public boolean matches(String filter) {
		Filter f;
		try {
			f = FrameworkUtil.createFilter(filter);
		}
		catch (InvalidSyntaxException e) {
			IllegalArgumentException iae = new IllegalArgumentException(e
					.getMessage());
			iae.initCause(e);
			throw iae;
		}
		Dictionary<String, Object> d = new UnmodifiableDictionary<String, Object>(
				properties);
		/*
		 * we can use matchCase here since properties already supports case
		 * insensitive key lookup.
		 */
		return f.matchCase(d);
	}

	/**
	 * Unmodifiable wrapper for Dictionary.
	 */
	private static class UnmodifiableDictionary<K, V> extends Dictionary<K, V> {
		private final Map<K, V>	wrapped;

		UnmodifiableDictionary(Map<K, V> wrapped) {
			this.wrapped = wrapped;
		}

		public Enumeration<V> elements() {
			return Collections.enumeration(wrapped.values());
		}

		public V get(Object key) {
			return wrapped.get(key);
		}

		public boolean isEmpty() {
			return wrapped.isEmpty();
		}

		public Enumeration<K> keys() {
			return Collections.enumeration(wrapped.keySet());
		}

		public V put(K key, V value) {
			throw new UnsupportedOperationException();
		}

		public V remove(Object key) {
			throw new UnsupportedOperationException();
		}

		public int size() {
			return wrapped.size();
		}
	}
}

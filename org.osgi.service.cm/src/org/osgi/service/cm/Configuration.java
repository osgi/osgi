/*
 * Copyright (c) OSGi Alliance (2001, 2008). All Rights Reserved.
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
package org.osgi.service.cm;

import java.io.IOException;
import java.util.Dictionary;

/**
 * The configuration information for a {@code ManagedService} or
 * {@code ManagedServiceFactory} object.
 * 
 * The Configuration Admin service uses this interface to represent the
 * configuration information for a {@code ManagedService} or for a
 * service instance of a {@code ManagedServiceFactory}.
 * 
 * <p>
 * A {@code Configuration} object contains a configuration dictionary and
 * allows the properties to be updated via this object. Bundles wishing to
 * receive configuration dictionaries do not need to use this class - they
 * register a {@code ManagedService} or
 * {@code ManagedServiceFactory}. Only administrative bundles, and
 * bundles wishing to update their own configurations need to use this class.
 * 
 * <p>
 * The properties handled in this configuration have case insensitive
 * {@code String} objects as keys. However, case is preserved from the
 * last set key/value.
 * <p>
 * A configuration can be <i>bound </i> to a bundle location (
 * {@code Bundle.getLocation()}). The purpose of binding a
 * {@code Configuration} object to a location is to make it impossible
 * for another bundle to forge a PID that would match this configuration. When a
 * configuration is bound to a specific location, and a bundle with a different
 * location registers a corresponding {@code ManagedService} object or
 * {@code ManagedServiceFactory} object, then the configuration is not
 * passed to the updated method of that object.
 * 
 * <p>
 * If a configuration's location is {@code null}, it is not yet bound to
 * a location. It will become bound to the location of the first bundle that
 * registers a {@code ManagedService} or
 * {@code ManagedServiceFactory} object with the corresponding PID.
 * <p>
 * The same {@code Configuration} object is used for configuring both a
 * Managed Service Factory and a Managed Service. When it is important to
 * differentiate between these two the term "factory configuration" is used.
 * 
 * @version $Id$
 */
public interface Configuration {
	/**
	 * Get the PID for this {@code Configuration} object.
	 * 
	 * @return the PID for this {@code Configuration} object.
	 * @throws IllegalStateException if this configuration has been deleted
	 */
	public String getPid();

	/**
	 * Return the properties of this {@code Configuration} object.
	 * 
	 * The {@code Dictionary} object returned is a private copy for the
	 * caller and may be changed without influencing the stored configuration.
	 * The keys in the returned dictionary are case insensitive and are always
	 * of type {@code String}.
	 * 
	 * <p>
	 * If called just after the configuration is created and before update has
	 * been called, this method returns {@code null}.
	 * 
	 * @return A private copy of the properties for the caller or
	 *         {@code null}. These properties must not contain the
	 *         "service.bundleLocation" property. The value of this property may
	 *         be obtained from the {@code getBundleLocation} method.
	 * @throws IllegalStateException if this configuration has been deleted
	 */
	public Dictionary getProperties();

	/**
	 * Update the properties of this {@code Configuration} object.
	 * 
	 * Stores the properties in persistent storage after adding or overwriting
	 * the following properties:
	 * <ul>
	 * <li>"service.pid" : is set to be the PID of this configuration.</li>
	 * <li>"service.factoryPid" : if this is a factory configuration it is set
	 * to the factory PID else it is not set.</li>
	 * </ul>
	 * These system properties are all of type {@code String}.
	 * 
	 * <p>
	 * If the corresponding Managed Service/Managed Service Factory is
	 * registered, its updated method must be called asynchronously. Else, this
	 * callback is delayed until aforementioned registration occurs.
	 * 
	 * <p>
	 * Also initiates an asynchronous call to all
	 * {@code ConfigurationListener}s with a
	 * {@code ConfigurationEvent.CM_UPDATED} event.
	 * 
	 * @param properties the new set of properties for this configuration
	 * @throws IOException if update cannot be made persistent
	 * @throws IllegalArgumentException if the {@code Dictionary} object
	 *         contains invalid configuration types or contains case variants of
	 *         the same key name.
	 * @throws IllegalStateException if this configuration has been deleted
	 */
	public void update(Dictionary properties) throws IOException;

	/**
	 * Delete this {@code Configuration} object.
	 * 
	 * Removes this configuration object from the persistent store. Notify
	 * asynchronously the corresponding Managed Service or Managed Service
	 * Factory. A {@code ManagedService} object is notified by a call to
	 * its {@code updated} method with a {@code null} properties
	 * argument. A {@code ManagedServiceFactory} object is notified by a
	 * call to its {@code deleted} method.
	 * 
	 * <p>
	 * Also initiates an asynchronous call to all
	 * {@code ConfigurationListener}s with a
	 * {@code ConfigurationEvent.CM_DELETED} event.
	 * 
	 * @throws IOException If delete fails
	 * @throws IllegalStateException if this configuration has been deleted
	 */
	public void delete() throws IOException;

	/**
	 * For a factory configuration return the PID of the corresponding Managed
	 * Service Factory, else return {@code null}.
	 * 
	 * @return factory PID or {@code null}
	 * @throws IllegalStateException if this configuration has been deleted
	 */
	public String getFactoryPid();

	/**
	 * Update the {@code Configuration} object with the current
	 * properties.
	 * 
	 * Initiate the {@code updated} callback to the Managed Service or
	 * Managed Service Factory with the current properties asynchronously.
	 * 
	 * <p>
	 * This is the only way for a bundle that uses a Configuration Plugin
	 * service to initiate a callback. For example, when that bundle detects a
	 * change that requires an update of the Managed Service or Managed Service
	 * Factory via its {@code ConfigurationPlugin} object.
	 * 
	 * @see ConfigurationPlugin
	 * @throws IOException if update cannot access the properties in persistent
	 *         storage
	 * @throws IllegalStateException if this configuration has been deleted
	 */
	public void update() throws IOException;

	/**
	 * Bind this {@code Configuration} object to the specified bundle
	 * location.
	 * 
	 * If the bundleLocation parameter is {@code null} then the
	 * {@code Configuration} object will not be bound to a location. It
	 * will be set to the bundle's location before the first time a Managed
	 * Service/Managed Service Factory receives this {@code Configuration}
	 * object via the updated method and before any plugins are called. The
	 * bundle location will be set persistently.
	 * 
	 * @param bundleLocation a bundle location or {@code null}
	 * @throws IllegalStateException If this configuration has been deleted.
	 * @throws SecurityException If the caller does not have
	 *         {@code ConfigurationPermission[*,CONFIGURE]}.
	 */
	public void setBundleLocation(String bundleLocation);

	/**
	 * Get the bundle location.
	 * 
	 * Returns the bundle location to which this configuration is bound, or
	 * {@code null} if it is not yet bound to a bundle location.
	 * 
	 * @return location to which this configuration is bound, or
	 *         {@code null}.
	 * @throws IllegalStateException If this {@code Configuration} object
	 *         has been deleted.
	 * @throws SecurityException If the caller does not have
	 *         {@code ConfigurationPermission[*,CONFIGURE]}.
	 */
	public String getBundleLocation();

	/**
	 * Equality is defined to have equal PIDs
	 * 
	 * Two Configuration objects are equal when their PIDs are equal.
	 * 
	 * @param other {@code Configuration} object to compare against
	 * @return {@code true} if equal, {@code false} if not a
	 *         {@code Configuration} object or one with a different PID.
	 */
	public boolean equals(Object other);

	/**
	 * Hash code is based on PID.
	 * 
	 * The hashcode for two Configuration objects must be the same when the
	 * Configuration PID's are the same.
	 * 
	 * @return hash code for this Configuration object
	 */
	public int hashCode();
}

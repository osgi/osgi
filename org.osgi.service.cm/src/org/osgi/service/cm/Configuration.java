/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2001, 2002).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.service.cm;

import java.io.*;
import java.util.*;

/**
 * The configuration information for a <code>ManagedService</code> or
 * <code>ManagedServiceFactory</code> object.
 * 
 * The Configuration Admin service uses this interface to represent the
 * configuration information for a <code>ManagedService</code> or for a service
 * instance of a <code>ManagedServiceFactory</code>.
 * 
 * <p>
 * A <code>Configuration</code> object contains a configuration dictionary and
 * allows the properties to be updated via this object. Bundles wishing to
 * receive configuration dictionaries do not need to use this class - they
 * register a <code>ManagedService</code> or <code>ManagedServiceFactory</code>.
 * Only administrative bundles, and bundles wishing to update their own
 * configurations need to use this class.
 * 
 * <p>
 * The properties handled in this configuration have case insensitive
 * <code>String</code> objects as keys. However, case is preserved from the last
 * set key/value.
 * <p>
 * A configuration can be <i>bound </i> to a bundle location (
 * <code>Bundle.getLocation()</code>). The purpose of binding a
 * <code>Configuration</code> object to a location is to make it impossible for
 * another bundle to forge a PID that would match this configuration. When a
 * configuration is bound to a specific location, and a bundle with a different
 * location registers a corresponding <code>ManagedService</code> object or
 * <code>ManagedServiceFactory</code> object, then the configuration is not passed
 * to the updated method of that object.
 * 
 * <p>
 * If a configuration's location is <code>null</code>, it is not yet bound to a
 * location. It will become bound to the location of the first bundle that
 * registers a <code>ManagedService</code> or <code>ManagedServiceFactory</code>
 * object with the corresponding PID.
 * <p>
 * The same <code>Configuration</code> object is used for configuring both a
 * Managed Service Factory and a Managed Service. When it is important to
 * differentiate between these two the term "factory configuration" is used.
 * 
 * @version $Revision$
 */
public interface Configuration {
	/**
	 * Get the PID for this <code>Configuration</code> object.
	 * 
	 * @return the PID for this <code>Configuration</code> object.
	 * @throws IllegalStateException if this configuration has been deleted
	 */
	String getPid();

	/**
	 * Return the properties of this <code>Configuration</code> object.
	 * 
	 * The <code>Dictionary</code> object returned is a private copy for the
	 * caller and may be changed without influencing the stored configuration.
	 * The keys in the returned dictionary are case insensitive and are always
	 * of type <code>String</code>.
	 * 
	 * <p>
	 * If called just after the configuration is created and before update has
	 * been called, this method returns <code>null</code>.
	 * 
	 * @return A private copy of the properties for the caller or <code>null</code>.
	 *         These properties must not contain the "service.bundleLocation"
	 *         property. The value of this property may be obtained from the
	 *         <code>getBundleLocation</code> method.
	 * @throws IllegalStateException if this configuration has been deleted
	 */
	Dictionary getProperties();

	/**
	 * Update the properties of this <code>Configuration</code> object.
	 * 
	 * Stores the properties in persistent storage after adding or overwriting
	 * the following properties:
	 * <ul>
	 * <li>"service.pid" : is set to be the PID of this configuration.</li>
	 * <li>"service.factoryPid" : if this is a factory configuration it is set
	 * to the factory PID else it is not set.</li>
	 * </ul>
	 * These system properties are all of type <code>String</code>.
	 * 
	 * <p>
	 * If the corresponding Managed Service/Managed Service Factory is
	 * registered, its updated method must be called asynchronously. Else, this
	 * callback is delayed until aforementioned registration occurs.
	 * 
	 * Also intiates a call to any <code>ConfigurationListener</code>s asynchronously.
	 * 
	 * @param properties the new set of properties for this configuration
	 * @throws IOException if update cannot be made persistent
	 * @throws IllegalArgumentException if the <code>Dictionary</code> object
	 *         contains invalid configuration types or contains case variants of
	 *         the same key name.
	 * @throws IllegalStateException if this configuration has been deleted
	 */
	void update(Dictionary properties) throws IOException;

	/**
	 * Delete this <code>Configuration</code> object.
	 * 
	 * Removes this configuration object from the persistent store. Notify
	 * asynchronously the corresponding Managed Service or Managed Service
	 * Factory. A <code>ManagedService</code> object is notified by a call to its
	 * <code>updated</code> method with a <code>null</code> properties argument. A
	 * <code>ManagedServiceFactory</code> object is notified by a call to its
	 * <code>deleted</code> method.
	 * 
	 * Also intiates a call to any <code>ConfigurationListener</code>s asynchronously.
	 * 
	 * @throws IOException If delete fails
	 * @throws IllegalStateException if this configuration has been deleted
	 */
	void delete() throws IOException;

	/**
	 * For a factory configuration return the PID of the corresponding Managed
	 * Service Factory, else return <code>null</code>.
	 * 
	 * @return factory PID or <code>null</code>
	 * @throws IllegalStateException if this configuration has been deleted
	 */
	String getFactoryPid();

	/**
	 * Update the <code>Configuration</code> object with the current properties.
	 * 
	 * Initiate the <code>updated</code> callback to the Managed Service or
	 * Managed Service Factory with the current properties asynchronously.
	 * 
	 * Also intiates a call to any <code>ConfigurationListener</code>s asynchronously.
	 * 
	 * <p>
	 * This is the only way for a bundle that uses a Configuration Plugin
	 * service to initate a callback. For example, when that bundle detects a
	 * change that requires an update of the Managed Service or Managed Service
	 * Factory via its <code>ConfigurationPlugin</code> object.
	 * 
	 * @see ConfigurationPlugin
	 * @throws IOException if update cannot access the properties in persistent
	 *         storage
	 * @throws IllegalStateException if this configuration has been deleted
	 */
	void update() throws IOException;

	/**
	 * Bind this <code>Configuration</code> object to the specified bundle
	 * location.
	 * 
	 * If the bundleLocation parameter is <code>null</code> then the
	 * <code>Configuration</code> object will not be bound to a location. It will
	 * be set to the bundle's location before the first time a Managed
	 * Service/Managed Service Factory receives this <code>Configuration</code>
	 * object via the updated method and before any plugins are called. The
	 * bundle location will be set persistently.
	 * 
	 * <p>
	 * This method requires <code>AdminPermission</code>.
	 * 
	 * @param bundleLocation a bundle location or <code>null</code>
	 * @throws SecurityException if the caller does not have
	 *         <code>AdminPermission</code>
	 * @throws IllegalStateException if this configuration has been deleted
	 */
	void setBundleLocation(String bundleLocation);

	/**
	 * Get the bundle location.
	 * 
	 * Returns the bundle location to which this configuration is bound, or
	 * <code>null</code> if it is not yet bound to a bundle location.
	 * <p>
	 * This call requires <code>AdminPermission</code>.
	 * 
	 * @return location to which this configuration is bound, or <code>null</code>.
	 * @throws SecurityException if the caller does not have
	 *         <code>AdminPermission</code>.
	 * @throws IllegalStateException if this <code>Configuration</code> object has
	 *         been deleted.
	 */
	String getBundleLocation();

	/**
	 * Equality is defined to have equal PIDs
	 * 
	 * Two Configuration objects are equal when their PIDs are equal.
	 * 
	 * @param other <code>Configuration</code> object to compare against
	 * @return <code>true</code> if equal, <code>false</code> if not a
	 *         <code>Configuration</code> object or one with a different PID.
	 */
	boolean equals(Object other);

	/**
	 * Hash code is based on PID.
	 * 
	 * The hashcode for two Configuration objects must be the same when the
	 * Configuration PID's are the same.
	 * 
	 * @return hash code for this Configuration object
	 */
	int hashCode();
}

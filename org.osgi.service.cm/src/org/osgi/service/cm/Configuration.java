/*
 * $Header$
 *
 * Copyright (c) The OSGi Alliance (2001, 2002).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
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
 * The configuration information for a <tt>ManagedService</tt> or <tt>ManagedServiceFactory</tt> object.
 *
 * The Configuration Admin service uses this interface to represent the
 * configuration information for a <tt>ManagedService</tt> or for a service instance
 * of a <tt>ManagedServiceFactory</tt>.
 *
 * <p>A <tt>Configuration</tt> object contains a configuration dictionary and
 * allows the properties to be updated via this object. Bundles wishing
 * to receive configuration dictionaries do not need to use this class
 * - they register a <tt>ManagedService</tt> or <tt>ManagedServiceFactory</tt>.
 * Only administrative bundles, and bundles
 * wishing to update their own configurations need to use this class.
 *
 * <p>The properties handled in this configuration have case
 * insensitive <tt>String</tt> objects as keys. However, case is preserved from the last set
 * key/value.
 * <p>A configuration can be <i>bound</i> to a bundle location
 * (<tt>Bundle.getLocation()</tt>). The purpose of binding a <tt>Configuration</tt> object
 * to a location is to make it impossible for another bundle to
 * forge a PID that would match this configuration. When a
 * configuration is bound to a specific location, and a bundle with a
 * different location registers a corresponding <tt>ManagedService</tt> object or
 * <tt>ManagedServiceFactory</tt> object, then the configuration is not passed to the
 * updated method of that object.
 *
 * <p>If a configuration's location is <tt>null</tt>, it is not yet bound to a
 * location. It will become bound to the location of the first bundle that registers a
 * <tt>ManagedService</tt> or <tt>ManagedServiceFactory</tt> object with the corresponding PID.
 * <p>The same <tt>Configuration</tt> object is used for configuring
 * both a Managed Service Factory and a Managed Service. When it is important
 * to differentiate between these two the term "factory configuration" is used.
 *
 * @version $Revision$
 */

public interface Configuration {

    /**
     * Get the PID for this <tt>Configuration</tt> object.
     *
     * @return the PID for this <tt>Configuration</tt> object.
     * @throws IllegalStateException if this configuration has been deleted
     */

    String getPid();

    /**
     * Return the properties of this <tt>Configuration</tt> object.
     *
     * The <tt>Dictionary</tt> object returned is a private copy for the caller and may
     * be changed without influencing the stored configuration.
     * The keys in the returned dictionary are case insensitive and are always of type <tt>String</tt>.
     *
     * <p>If called just after the configuration is created and before
     * update has been called, this method returns <tt>null</tt>.
     *
     * @return A private copy of the properties for the caller or <tt>null</tt>.
     * These properties must not contain the
     * "service.bundleLocation" property. The value of this property
     * may be obtained from the <tt>getBundleLocation</tt> method.
     * @throws IllegalStateException if this configuration has been deleted
     */
    Dictionary getProperties();

    /**
     * Update the properties of this <tt>Configuration</tt> object.
     *
     * Stores the properties in persistent storage after adding or overwriting
     * the following properties:
     * <ul>
     * <li>"service.pid" : is set to be the PID of this configuration.</li>
     * <li>"service.factoryPid" : if this is a factory configuration it is set to the factory PID else it is not set.</li>
     * </ul>
     * These system properties are all of type <tt>String</tt>.
     *
     * <p>If the corresponding Managed Service/Managed Service Factory is
     * registered, its updated method must be called asynchronously. Else, this callback is delayed until aforementioned
     * registration occurs.
     *
     * @param properties the new set of properties for this configuration
     * @throws IOException if update cannot be made persistent
     * @throws IllegalArgumentException if the <tt>Dictionary</tt> object contains invalid configuration types
     * or contains case variants of the same key name.
     * @throws IllegalStateException if this configuration has been deleted
     */
    void update(Dictionary properties) throws IOException;

    /**
     * Delete this <tt>Configuration</tt> object.
     *
     * Removes this configuration object from the persistent store. Notify
     * asynchronously the corresponding Managed Service
     * or Managed Service Factory.  A <tt>ManagedService</tt> object is notified by a call to its
     * <tt>updated</tt> method with a <tt>null</tt> properties argument.  A
     * <tt>ManagedServiceFactory</tt> object is notified by a call to its <tt>deleted</tt>
     * method.
     *
     * @throws IOException If delete fails
     * @throws IllegalStateException if this configuration has been deleted
    */
    void delete() throws IOException;

    /**
     * For a factory configuration return the PID of the
     * corresponding Managed Service Factory, else return <tt>null</tt>.
     *
     * @return factory PID or <tt>null</tt>
     * @throws IllegalStateException if this configuration has been deleted
     */
    String getFactoryPid();

    /**
     * Update the <tt>Configuration</tt> object with the current properties.
     *
     * Initiate the <tt>updated</tt> callback to the Managed Service or
     * Managed Service Factory with the current properties asynchronously.
     *
     * <p>This is the only way for a bundle that uses a Configuration Plugin service
     * to initate a callback. For example, when that bundle detects a change that
     * requires an update of the Managed Service or Managed Service Factory
     * via its <tt>ConfigurationPlugin</tt> object.
     *
     * @see ConfigurationPlugin
     * @throws IOException if update cannot access the properties in persistent storage
     * @throws IllegalStateException if this configuration has been deleted
     */
    void update() throws IOException;


    /**
     * Bind this <tt>Configuration</tt> object to the specified bundle location.
     *
     * If the bundleLocation parameter is <tt>null</tt> then the <tt>Configuration</tt>
     * object will not be bound to a location. It will be set to the bundle's
     * location before the first time a Managed Service/Managed Service Factory
     * receives this <tt>Configuration</tt> object via the updated method and before
     * any plugins are called. The bundle location will be set persistently.
     *
     * <p>This method requires <tt>AdminPermission</tt>.
     *
     * @param bundleLocation a bundle location or <tt>null</tt>
     * @throws SecurityException if the caller does not have <tt>AdminPermission</tt>
     * @throws IllegalStateException if this configuration has been deleted
     */
    void setBundleLocation( String bundleLocation );

    /**
     * Get the bundle location.
     *
     * Returns the bundle location to which this configuration is bound, or
     * <tt>null</tt> if it is not yet bound to a bundle location.
     * <p>This call requires <tt>AdminPermission</tt>.
     *
     * @return location to which this configuration is bound, or <tt>null</tt>.
     * @throws SecurityException if the caller does not have <tt>AdminPermission</tt>.
     * @throws IllegalStateException if this <tt>Configuration</tt> object has been deleted.
     */
    String getBundleLocation();

    /**
     * Equality is defined to have equal PIDs
     *
     * Two Configuration objects are equal when their PIDs are equal.
     * @param other <tt>Configuration</tt> object to compare against
     * @return <tt>true</tt> if equal, <tt>false</tt> if not a <tt>Configuration</tt> object or one with a
     *         different PID.
     */
    boolean equals( Object other );

    /**
     * Hash code is based on PID.
     *
     * The hashcode for two Configuration objects must be the same when the
     * Configuration PID's are the same.
     * @return hash code for this Configuration object
     */
    int hashCode();
}


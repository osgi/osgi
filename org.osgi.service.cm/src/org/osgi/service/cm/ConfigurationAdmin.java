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
import org.osgi.framework.*;
/**
 * Service for administering configuration data.
 *
 * <p>The main purpose of this interface is to store bundle configuration
 * data persistently. This information is
 * represented in <tt>Configuration</tt> objects. The actual configuration
 * data is a <tt>Dictionary</tt> of properties inside a <tt>Configuration</tt>
 * object.
 *
 * <p>There are two principally different ways to manage
 * configurations. First there is the concept of a Managed Service,
 * where configuration data is uniquely associated with an object
 * registered with the service registry.
 *
 * <p>Next, there is the concept of a factory where the Configuration Admin
 * service will maintain 0 or more <tt>Configuration</tt> objects for a
 * Managed Service Factory that is registered with the Framework.
 *
 * <p>The first concept is intended for configuration data
 * about "things/services" whose existence is defined
 * externally, e.g. a specific printer. Factories are
 * intended for "things/services" that can be created any number of
 * times, e.g. a configuration for a DHCP server for different
 * networks.
 *
 * <p>Bundles that require configuration should register a
 * Managed Service or a Managed Service Factory in the service
 * registry. A registration property named <tt>service.pid</tt> (persistent
 * identifier or PID) must be used to identify this Managed Service or
 * Managed Service Factory to the Configuration Admin service.
 *
 * <p>When the ConfigurationAdmin detects the registration of a
 * Managed Service, it checks its persistent storage for a configuration
 * object whose PID matches the PID registration property (<tt>service.pid</tt>) of the
 * Managed Service. If found, it calls {@link ManagedService#updated} method
 * with the new properties. The implementation of a Configuration Admin service
 * must run these call-backs asynchronously to allow proper synchronization.
 *
 * <p>When the Configuration Admin service detects a Managed Service Factory
 * registration, it checks its storage for configuration objects whose
 * <tt>factoryPid</tt> matches the PID of the
 * Managed Service Factory.  For each such <tt>Configuration</tt> objects, it
 * calls the <tt>ManagedServiceFactory.updated</tt> method asynchronously
 * with the new properties. The calls to the <tt>updated</tt> method
 * of a <tt>ManagedServiceFactory</tt> must be executed sequentially and not
 * overlap in time.
 *
 * <p>In general, bundles having permission to use the
 * Configuration Admin service can only access and modify their own
 * configuration information.  Accessing or modifying the
 * configuration of another bundle requires <tt>AdminPermission</tt>.
 *
 * <p><tt>Configuration</tt> objects can be <i>bound</i> to a specified bundle
 * location. In this case, if a matching Managed Service or
 * Managed Service Factory is registered by a bundle with a different
 * location, then the Configuration Admin service must not do the normal
 * callback, and it should log an error. In the case where a
 * <tt>Configuration</tt> object is not bound, its location field is <tt>null</tt>, the
 * Configuration Admin service will bind it to the location of the bundle that
 * registers the first Managed Service or Managed Service Factory that
 * has a corresponding PID property.
 * When a <tt>Configuration</tt> object is bound to a bundle location in this
 * manner, the Confguration Admin service must detect if the bundle corresponding
 * to the location is uninstalled. If this occurs, the <tt>Configuration</tt>
 * object is unbound, that is its location field is set back to <tt>null</tt>.
 *
 * <p>The method descriptions of this class refer to a concept of
 * "the calling bundle".  This is a loose way of referring to the
 * bundle which obtained the Configuration Admin service from the
 * service registry. Implementations of <tt>ConfigurationAdmin</tt>
 * must use a {@link org.osgi.framework.ServiceFactory} to support this concept.
 *
 * @version $Revision$
 */

public interface ConfigurationAdmin {
	/**
	 * Service property naming the Factory PID in the configuration dictionary.
	 * The property's value is of type <tt>String</tt>.
	 *
	 * @since 1.1
	 */
	public final static String SERVICE_FACTORYPID = "service.factoryPid";
	
	/**
	 * Service property naming the location of the bundle that is associated
	 * with a a <tt>Configuration</tt> object. This property can be searched for
	 * but must not appear in the configuration dictionary for security reason.
	 * The property's value is of type <tt>String</tt>.
	 *
	 * @since 1.1
	 */
	public final static String SERVICE_BUNDLELOCATION = "service.bundleLocation";
	
	/**
	 * Create a new factory <tt>Configuration</tt> object with a new PID.
	 *
	 * The properties of the new <tt>Configuration</tt> object are <tt>null</tt> until the first
	 * time that its {@link Configuration#update} method is called.
	 *
	 * <p>It is not required that the <tt>factoryPid</tt> maps to a registered
	 * Managed Service Factory.
	 * <p>The <tt>Configuration</tt> object is bound to the location of the calling
	 * bundle.
	 *
	 * @param factoryPid    PID of factory (not <tt>null</tt>).
	 * @return              a new <tt>Configuration</tt> object.
	 * @throws IOException  if access to persistent storage fails.
	 * @throws SecurityException if caller does not have <tt>AdminPermission</tt> and <tt>factoryPid</tt> is bound to another bundle.
	 */
    Configuration createFactoryConfiguration( String factoryPid ) throws IOException;

    /**
	 * Create a new factory <tt>Configuration</tt> object with a new PID.
	 *
	 * The properties of the new <tt>Configuration</tt> object are <tt>null</tt> until the first
	 * time that its {@link Configuration#update} method is called.
	 *
	 * <p>It is not required that the <tt>factoryPid</tt> maps to a registered
	 * Managed Service Factory.
	 *
	 * <p>The <tt>Configuration</tt> is bound to the location specified.
	 * If this location is <tt>null</tt> it will be bound to the location
	 * of the first bundle that registers a Managed Service Factory with
	 * a corresponding PID.
	 *
	 * <p>This method requires <tt>AdminPermission</tt>.
	 *
	 * @param factoryPid    PID of factory (not <tt>null</tt>).
	 * @param location      a bundle location string, or <tt>null</tt>.
	 * @return              a new <tt>Configuration</tt> object.
	 * @throws IOException  if access to persistent storage fails.
	 * @throws SecurityException if caller does not have <tt>AdminPermission</tt>.
	 */
    Configuration createFactoryConfiguration( String factoryPid, String location ) throws IOException;

    /**
	 * Get an existing <tt>Configuration</tt> object from the persistent store,
	 * or create a new <tt>Configuration</tt> object.
	 *
	 * <p>If a <tt>Configuration</tt> with this PID already exists in
	 * Configuration Admin service return it. The location parameter is
	 * ignored in this case.
	 *
	 * <p>Else, return a new  <tt>Configuration</tt> object. This new object is
	 * bound to the location and the properties are set to <tt>null</tt>. If the
	 * location parameter is <tt>null</tt>, it will be set when a Managed Service with
	 * the corresponding PID is registered for the first time.
	 *
	 * <p>This method requires <tt>AdminPermission</tt>.
	 * @param pid persistent identifier.
	 * @param location the bundle location string, or <tt>null</tt>.
	 * @return an existing or new <tt>Configuration</tt> object.
	 * @throws IOException if access to persistent storage fails.
	 * @throws SecurityException if the caller does not have <tt>AdminPermission</tt>.
	 */
    Configuration getConfiguration( String pid, String location ) throws IOException;

    /**
	 * Get an existing or new <tt>Configuration</tt> object from the persistent store.
	 *
	 * If the <tt>Configuration</tt> object for this PID does not exist, create a new
	 * <tt>Configuration</tt> object for that PID, where properties are <tt>null</tt>. Bind
	 * its location to the calling bundle's location.
	 *
	 * <p>Else, if the location of the existing <tt>Configuration</tt> object is <tt>null</tt>,
	 * set it to the calling bundle's location.
	 * <p>If the location of the <tt>Configuration</tt> object does not match the calling bundle,
	 * throw a <tt>SecurityException</tt>.
	 *
	 * @param pid persistent identifier.
	 * @return an existing or new <tt>Configuration</tt> matching the PID.
	 * @throws IOException if access to persistent storage fails.
	 * @throws SecurityException if the <tt>Configuration</tt> object is bound to a location different from that of the calling bundle and it has no <tt>AdminPermission</tt>.
	 */
    Configuration getConfiguration( String pid ) throws IOException;

    /**
	 * List the current <tt>Configuration</tt> objects which match the filter.
	 *
	 * <p>Only <tt>Configuration</tt> objects with non-<tt>null</tt> properties are considered current.  That is,
	 * <tt>Configuration.getProperties()</tt> is guaranteed not to return <tt>null</tt> for each of the
	 * returned <tt>Configuration</tt> objects.
	 *
	 * <p>Normally only <tt>Configuration</tt> objects that are bound to the location
	 * of the calling bundle are returned. If the caller has <tt>AdminPermission</tt>,
	 * then all matching <tt>Configuration</tt> objects are returned.
	 *
	 * <p>The syntax of the filter string is as defined in the <tt>Filter</tt> class. The
	 * filter can test any configuration parameters including
	 * the following system properties:
	 * <ul>
	 * <li><tt>service.pid</tt> - <tt>String</tt> - the PID under which this is registered</li>
	 * <li><tt>service.factoryPid</tt> - <tt>String</tt> - the factory if applicable</li>
	 * <li><tt>service.bundleLocation</tt> - <tt>String</tt> - the bundle location</li>
	 * </ul>
	 * The filter can also be <tt>null</tt>, meaning that all <tt>Configuration</tt>
	 * objects should be returned.
	 *
	 * @param filter     a <tt>Filter</tt> object, or <tt>null</tt> to retrieve all <tt>Configuration</tt> objects.
	 * @return       all matching <tt>Configuration</tt> objects, or <tt>null</tt> if there aren't any
	 * @throws IOException  if access to persistent storage fails
	 * @throws InvalidSyntaxException if the filter string is invalid
	 */
    Configuration [] listConfigurations( String filter ) throws IOException, InvalidSyntaxException;
}


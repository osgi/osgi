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

import java.io.IOException;
import java.util.Dictionary;
import org.osgi.framework.InvalidSyntaxException;

/**
 * Service for administering configuration data.
 * 
 * <p>
 * The main purpose of this interface is to store bundle configuration data
 * persistently. This information is represented in <code>Configuration</code>
 * objects. The actual configuration data is a <code>Dictionary</code> of
 * properties inside a <code>Configuration</code> object.
 * 
 * <p>
 * There are two principally different ways to manage configurations. First
 * there is the concept of a Managed Service, where configuration data is
 * uniquely associated with an object registered with the service registry.
 * 
 * <p>
 * Next, there is the concept of a factory where the Configuration Admin service
 * will maintain 0 or more <code>Configuration</code> objects for a Managed
 * Service Factory that is registered with the Framework.
 * 
 * <p>
 * The first concept is intended for configuration data about "things/services"
 * whose existence is defined externally, e.g. a specific printer. Factories are
 * intended for "things/services" that can be created any number of times, e.g.
 * a configuration for a DHCP server for different networks.
 * 
 * <p>
 * Bundles that require configuration should register a Managed Service or a
 * Managed Service Factory in the service registry. A registration property
 * named <code>service.pid</code> (persistent identifier or PID) must be used to
 * identify this Managed Service or Managed Service Factory to the Configuration
 * Admin service.
 * 
 * <p>
 * When the ConfigurationAdmin detects the registration of a Managed Service, it
 * checks its persistent storage for a configuration object whose PID matches
 * the PID registration property (<code>service.pid</code>) of the Managed
 * Service. If found, it calls {@link ManagedService#updated}method with the
 * new properties. The implementation of a Configuration Admin service must run
 * these call-backs asynchronously to allow proper synchronization.
 * 
 * <p>
 * When the Configuration Admin service detects a Managed Service Factory
 * registration, it checks its storage for configuration objects whose
 * <code>factoryPid</code> matches the PID of the Managed Service Factory. For
 * each such <code>Configuration</code> objects, it calls the
 * <code>ManagedServiceFactory.updated</code> method asynchronously with the new
 * properties. The calls to the <code>updated</code> method of a
 * <code>ManagedServiceFactory</code> must be executed sequentially and not
 * overlap in time.
 * 
 * <p>
 * In general, bundles having permission to use the Configuration Admin service
 * can only access and modify their own configuration information. Accessing or
 * modifying the configuration of another bundle requires
 * <code>AdminPermission</code>.
 * 
 * <p>
 * <code>Configuration</code> objects can be <i>bound </i> to a specified bundle
 * location. In this case, if a matching Managed Service or Managed Service
 * Factory is registered by a bundle with a different location, then the
 * Configuration Admin service must not do the normal callback, and it should
 * log an error. In the case where a <code>Configuration</code> object is not
 * bound, its location field is <code>null</code>, the Configuration Admin
 * service will bind it to the location of the bundle that registers the first
 * Managed Service or Managed Service Factory that has a corresponding PID
 * property. When a <code>Configuration</code> object is bound to a bundle
 * location in this manner, the Confguration Admin service must detect if the
 * bundle corresponding to the location is uninstalled. If this occurs, the
 * <code>Configuration</code> object is unbound, that is its location field is set
 * back to <code>null</code>.
 * 
 * <p>
 * The method descriptions of this class refer to a concept of "the calling
 * bundle". This is a loose way of referring to the bundle which obtained the
 * Configuration Admin service from the service registry. Implementations of
 * <code>ConfigurationAdmin</code> must use a
 * {@link org.osgi.framework.ServiceFactory}to support this concept.
 * 
 * @version $Revision$
 */
public interface ConfigurationAdmin {
	/**
	 * Service property naming the Factory PID in the configuration dictionary.
	 * The property's value is of type <code>String</code>.
	 * 
	 * @since 1.1
	 */
	public final static String	SERVICE_FACTORYPID		= "service.factoryPid";
	/**
	 * Service property naming the location of the bundle that is associated
	 * with a a <code>Configuration</code> object. This property can be searched
	 * for but must not appear in the configuration dictionary for security
	 * reason. The property's value is of type <code>String</code>.
	 * 
	 * @since 1.1
	 */
	public final static String	SERVICE_BUNDLELOCATION	= "service.bundleLocation";

	/**
	 * Create a new factory <code>Configuration</code> object with a new PID.
	 * 
	 * The properties of the new <code>Configuration</code> object are
	 * <code>null</code> until the first time that its
	 * {@link Configuration#update(Dictionary)}method is called.
	 * 
	 * <p>
	 * It is not required that the <code>factoryPid</code> maps to a registered
	 * Managed Service Factory.
	 * <p>
	 * The <code>Configuration</code> object is bound to the location of the
	 * calling bundle.
	 * 
	 * @param factoryPid PID of factory (not <code>null</code>).
	 * @return a new <code>Configuration</code> object.
	 * @throws IOException if access to persistent storage fails.
	 * @throws SecurityException if caller does not have
	 *         <code>AdminPermission</code> and <code>factoryPid</code> is bound to
	 *         another bundle.
	 */
	Configuration createFactoryConfiguration(String factoryPid)
			throws IOException;

	/**
	 * Create a new factory <code>Configuration</code> object with a new PID.
	 * 
	 * The properties of the new <code>Configuration</code> object are
	 * <code>null</code> until the first time that its
	 * {@link Configuration#update(Dictionary)}method is called.
	 * 
	 * <p>
	 * It is not required that the <code>factoryPid</code> maps to a registered
	 * Managed Service Factory.
	 * 
	 * <p>
	 * The <code>Configuration</code> is bound to the location specified. If this
	 * location is <code>null</code> it will be bound to the location of the first
	 * bundle that registers a Managed Service Factory with a corresponding PID.
	 * 
	 * <p>
	 * This method requires <code>AdminPermission</code>.
	 * 
	 * @param factoryPid PID of factory (not <code>null</code>).
	 * @param location a bundle location string, or <code>null</code>.
	 * @return a new <code>Configuration</code> object.
	 * @throws IOException if access to persistent storage fails.
	 * @throws SecurityException if caller does not have
	 *         <code>AdminPermission</code>.
	 */
	Configuration createFactoryConfiguration(String factoryPid, String location)
			throws IOException;

	/**
	 * Get an existing <code>Configuration</code> object from the persistent
	 * store, or create a new <code>Configuration</code> object.
	 * 
	 * <p>
	 * If a <code>Configuration</code> with this PID already exists in
	 * Configuration Admin service return it. The location parameter is ignored
	 * in this case.
	 * 
	 * <p>
	 * Else, return a new <code>Configuration</code> object. This new object is
	 * bound to the location and the properties are set to <code>null</code>. If
	 * the location parameter is <code>null</code>, it will be set when a Managed
	 * Service with the corresponding PID is registered for the first time.
	 * 
	 * <p>
	 * This method requires <code>AdminPermission</code>.
	 * 
	 * @param pid persistent identifier.
	 * @param location the bundle location string, or <code>null</code>.
	 * @return an existing or new <code>Configuration</code> object.
	 * @throws IOException if access to persistent storage fails.
	 * @throws SecurityException if the caller does not have
	 *         <code>AdminPermission</code>.
	 */
	Configuration getConfiguration(String pid, String location)
			throws IOException;

	/**
	 * Get an existing or new <code>Configuration</code> object from the
	 * persistent store.
	 * 
	 * If the <code>Configuration</code> object for this PID does not exist,
	 * create a new <code>Configuration</code> object for that PID, where
	 * properties are <code>null</code>. Bind its location to the calling
	 * bundle's location.
	 * 
	 * <p>
	 * Else, if the location of the existing <code>Configuration</code> object is
	 * <code>null</code>, set it to the calling bundle's location.
	 * <p>
	 * If the location of the <code>Configuration</code> object does not match the
	 * calling bundle, throw a <code>SecurityException</code>.
	 * 
	 * @param pid persistent identifier.
	 * @return an existing or new <code>Configuration</code> matching the PID.
	 * @throws IOException if access to persistent storage fails.
	 * @throws SecurityException if the <code>Configuration</code> object is bound
	 *         to a location different from that of the calling bundle and it
	 *         has no <code>AdminPermission</code>.
	 */
	Configuration getConfiguration(String pid) throws IOException;

	/**
	 * List the current <code>Configuration</code> objects which match the filter.
	 * 
	 * <p>
	 * Only <code>Configuration</code> objects with non- <code>null</code>
	 * properties are considered current. That is,
	 * <code>Configuration.getProperties()</code> is guaranteed not to return
	 * <code>null</code> for each of the returned <code>Configuration</code>
	 * objects.
	 * 
	 * <p>
	 * Normally only <code>Configuration</code> objects that are bound to the
	 * location of the calling bundle are returned. If the caller has
	 * <code>AdminPermission</code>, then all matching <code>Configuration</code>
	 * objects are returned.
	 * 
	 * <p>
	 * The syntax of the filter string is as defined in the <code>Filter</code>
	 * class. The filter can test any configuration parameters including the
	 * following system properties:
	 * <ul>
	 * <li><code>service.pid</code>-<code>String</code>- the PID under which
	 * this is registered</li>
	 * <li><code>service.factoryPid</code>-<code>String</code>- the factory if
	 * applicable</li>
	 * <li><code>service.bundleLocation</code>-<code>String</code>- the bundle
	 * location</li>
	 * </ul>
	 * The filter can also be <code>null</code>, meaning that all
	 * <code>Configuration</code> objects should be returned.
	 * 
	 * @param filter a <code>Filter</code> object, or <code>null</code> to retrieve
	 *        all <code>Configuration</code> objects.
	 * @return all matching <code>Configuration</code> objects, or <code>null</code>
	 *         if there aren't any
	 * @throws IOException if access to persistent storage fails
	 * @throws InvalidSyntaxException if the filter string is invalid
	 */
	Configuration[] listConfigurations(String filter) throws IOException,
			InvalidSyntaxException;
}

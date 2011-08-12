/*
 * Copyright (c) OSGi Alliance (2011). All Rights Reserved.
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
package org.osgi.service.subsystem;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import org.osgi.framework.Version;
import org.osgi.framework.wiring.Resource;

/*
 * TODO
 * (1) How will the root subsystem handle install, start, stop, update, and
 * uninstall?
 * (2) ResourceProcesser will accept Coordination again. No chaining. Processor
 * is responsible for failing the coordination or throwing exception from ended
 * or failed.
 * (3) Remove coordination from Resource.
 * (4) Do we need to return Resources and Bundles? Just Resources? What?
 * (5) Add description of root subsystem. List unsupported operations.
 */

/**
 * A representation of a subsystem in the framework. A subsystem is a 
 * collection of bundles and/or other resource. A subsystem has isolation 
 * semantics. Subsystem types are defined that have different default isolation 
 * semantics. For example, an Application subsystem does not export any of the 
 * packages or services provided by its content bundles, and imports any 
 * packages or services that are required to satisfy unresolved package or 
 * service dependencies of the content bundles. A subsystem is defined using a 
 * manifest format. 
 * 
 * @ThreadSafe
 * @noimplement
 */
public interface Subsystem {
	/**
	 * The states of a subsystem in the framework. These states match those of 
	 * a Bundle and are derived using the same rules as CompositeBundles. As 
	 * such, they are more a reflection of what content bundles are permitted 
	 * to do rather than an aggregation of the content bundle states. 
	 */
	public static enum State {
		/**
		 * A subsystem is in the INSTALLING state when it is initially created.
		 */
		INSTALLING,
		/**
		 * A subsystem is in the INSTALLED state when all resources are
		 * successfully installed.
		 */
		INSTALLED,
		/**
		 *  A subsystem in the RESOLVING is allowed to have its content bundles 
		 * resolved.
		 */
		RESOLVING,
		/**
		 *  A subsystem is in the RESOLVED state when all resources are 
		 * resolved.
		 */
		RESOLVED,
		/**
		 * A subsystem is in the STARTING state when all its content bundles 
		 * are enabled for activation.
		 */
		STARTING,
		/**
		 * A subsystem is in the ACTIVE state when it has reached the beginning 
		 * start-level (for starting it's contents), and all its persistently 
		 * started content bundles that are resolved and have had their 
		 * start-levels met have completed, or failed, their activator start 
		 * method.
		 */
		ACTIVE,
		/**
		 *  A subsystem in the STOPPING state is in the process of taking its 
		 * its active start level to zero, stopping all the content bundles.
		 */
		STOPPING,
		UPDATING,
		UNINSTALLING,
		/**
		 * A subsystem is in the UNINSTALLED state when all its content bundles 
		 * and uninstalled and its system bundle context is invalidated.
		 */
		UNINSTALLED
	}
	
	/**
	 * Cancels the currently executing asynchronous life-cycle operation, if
	 * any.
	 * @throws SubsystemException - If this subsystem is not in one of the
	 *         transitional states or the currently executing operation cannot
	 *         be cancelled for any reason.
	 */
	public void cancel() throws SubsystemException;
	
	/**
	 * Gets the subsystems managed by this service. This only includes the 
	 * top-level Subsystems installed in the Framework, CoompositeBundle or 
	 * Subsystem from which this service has been retrieved.
	 * @return The Subsystems managed by this service.
	 */
	public Collection<Subsystem> getChildren();
	
	/**
	 * Returns a snapshot of all {@code Resources} currently constituting this 
	 * {@link Subsystem}. If this {@code Subsystem} has no {@code Resources}, 
	 * the {@link Collection} will be empty.
	 * 
	 * @return A snapshot of all {@code Resources} currently constituting this
	 *         {@code Subsystem}.
	 */
	public Collection<Resource> getConstituents();
	
	/**
	 * Gets the headers used to define this subsystem. The headers will be 
	 * localized using the locale returned by java.util.Locale.getDefault. This 
	 * is equivalent to calling getHeaders(null). 
	 * @return The headers used to define this subsystem.
	 * @throws SecurityException If the caller does not have the appropriate 
	 *         AdminPermission[this,METADATA] and the runtime supports 
	 *         permissions.
	 */
	public Map<String, String> getHeaders();
	
	/**
	 * Gets the headers used to define this subsystem.
	 * @param locale The locale name to be used to localize the headers. If the 
	 *        locale is null then the locale returned by 
	 *        java.util.Locale.getDefault is used. If the value is the empty 
	 *        string then the returned headers are returned unlocalized. 
	 * @return the headers used to define this subsystem, localized to the 
	 *         specified locale. 
	 */
	public Map<String, String> getHeaders(String locale);
	
	/**
	 * The location identifier used to install this subsystem through 
	 * Subsystem.install. This identifier does not change while this subsystem 
	 * remains installed, even after Subsystem.update. This location identifier 
	 * is used in Subsystem.update if no other update source is specified. 
	 * @return The string representation of the subsystem's location identifier.
	 */
	public String getLocation();
	
	/**
	 * Gets the parent Subsystem that scopes this subsystem instance.
	 * @return The Subsystem that scopes this subsystem or null if there is no 
	 *         parent subsystem (e.g. if the outer scope is the framework).
	 */
	public Subsystem getParent();
	
	/**
	 * Gets the state of the subsystem.
	 * @return The state of the subsystem.
	 */
	public State getState();
	
	/**
	 * Gets the identifier of the subsystem. Subsystem identifiers are assigned 
	 * when the subsystem is installed and are unique within the framework. 
	 * @return The identifier of the subsystem.
	 */
	public long getSubsystemId();
	
	/**
	 * Gets the symbolic name of this subsystem.
	 * @return The symbolic name of this subsystem.
	 */
	public String getSymbolicName();
	
	/**
	 * Gets the version of this subsystem.
	 * @return The version of this subsystem.
	 */
	public Version getVersion();
	
	/**
	 * Install a new subsystem from the specified location identifier.
	 * <p>
	 * This method performs the same function as calling install(String, 
	 * InputStream) with the specified location identifier and a null 
	 * InputStream.
	 * @param location The location identifier of the subsystem to be installed.
	 * @return The installed subsystem.
	 * @throws SubsystemException If the subsystem could not be installed for
	 *         any reason.
	 * @throws SecurityException If the caller does not have the appropriate 
	 *         AdminPermission[installed subsystem,LIFECYCLE], and the Java 
	 *         Runtime Environment supports permissions.
	 */
	public Subsystem install(String location) throws SubsystemException;
	
	/**
	 * Install a new subsystem from the specified InputStream object.
	 * <p/>
	 * If the specified InputStream is null, the InputStream must be created 
	 * from the specified location.
	 * <p/>
	 * The specified location identifier will be used as the identity of the 
	 * subsystem. Every installed subsystem is uniquely identified by its 
	 * location identifier which is typically in the form of a URL.
	 * <p/>
	 * TODO: Understand whether this all change when we can install the same 
	 * bundle multiple times.
	 * <p/>
	 * A subsystem and its contents must remain installed across Framework and 
	 * VM restarts. The subsystem itself is installed atomically, however its 
	 * contents are not.
	 * <p/>
	 * The following steps are required to install a subsystem:
	 * <ol>
	 * 		<li>If there is an existing subsystem containing the same location 
	 *          identifier as the Subsystem to be installed, then a Future is 
	 *          returned that has the existing subsystem immediately available 
	 *          as its result.</li>
	 * 		<li>If there is already an install in progress for a subsystem with 
	 *          the same location identifier, then the Future returned is the 
	 *          same as the Future returned for the first install and a new 
	 *          install is not started.</li>
	 * 		<li>If this is a new install, then a new Future is returned with 
	 *          the installation process following the remaining step.</li>
	 * 		<li>The subsystem content is read from the input stream.</li>
	 * 		<li>Isolation is set up while the install is in progress, such that 
	 *          none of the content can be resolved with bundles outside the 
	 *          subsystem.</li>
	 * 		<li>The resources are into the framework through the use of 
	 *          ResourceProcessors.</li>
	 * 		<li>Isolation is configured appropriate for the subsystem such that 
	 *          the content can be resolved with bundles outside the subsystem.</li>
	 * 		<li>The subsystem's state is set to INSTALLED.</li>
	 * 		<li>The subsystem event of type INSTALLED is fired.</li>
	 * 		<li>The subsystem content is started.</li>
	 * 		<li>The subsystem object for the newly installed subsystem is made 
	 *          available from the Future.</li>
	 * </ol>
	 * @param location The location identifier of the subsystem to be installed.
	 * @param content The InputStream from where the subsystem is to be 
	 *        installed or null if the location is to be used to create the 
	 *        InputStream.
	 * @return The installed subsystem.
	 * @throws SubsystemException If the subsystem could not be installed for
	 *         any reason.
	 * @throws SecurityException If the caller does not have the appropriate 
	 *         AdminPermission[installed subsystem,LIFECYCLE], and the Java 
	 *         Runtime Environment supports permissions.
	 */
	public Subsystem install(String location, InputStream content) throws SubsystemException;
	
	/**
	 * Starts the subsystem. The subsystem is started according to the rules 
	 * defined for Bundles and the content bundles are enabled for activation. 
	 * @throws SubsystemException If this subsystem could not be started. 
	 * @throws IllegalStateException If this subsystem has been uninstalled. 
	 * @throws SecurityException If the caller does not have the appropriate 
	 *         AdminPermission[this,EXECUTE] and the runtime supports 
	 *         permissions.
	 */
	public void start() throws SubsystemException;
	
	/**
	 * Stops the subsystem. The subsystem is stopped according to the rules 
	 * defined for Bundles and the content bundles are disabled for activation 
	 * and stopped.
	 * @throws SubsystemException If an internal exception is thrown while 
	 *         stopping the subsystem (e.g. a BundleException from Bundle.stop). 
	 * @throws IllegalStateException - If this subsystem has been uninstalled. 
	 * @throws SecurityException - If the caller does not have the appropriate 
	 *         AdminPermission[this,EXECUTE] and the runtime supports 
	 *         permissions.
	 */
	public void stop() throws SubsystemException;
	
	/**
	 * Uninstall the given subsystem.
	 * <p/>
	 * This method causes the Framework to notify other bundles and subsystems 
	 * that this subsystem is being uninstalled, and then puts this subsystem 
	 * into the UNINSTALLED state. The Framework must remove any resources 
	 * related to this subsystem that it is able to remove. It does so using the 
	 * appropriate ResourceProcessor.uninstall(Subsystem, Resource, 
	 * Coordination) for the resource namespace. If this subsystem has exported 
	 * any packages, the Framework must continue to make these packages 
	 * available to their importing bundles or subsystems until the 
	 * org.osgi.service.packageadmin.PackageAdmin.refreshPackages(
	 * org.osgi.framework.Bundle[]) method has been called or the Framework is 
	 * relaunched. The following steps are required to uninstall a subsystem:
	 * <ol>
	 * 		<li>If this subsystem's state is UNINSTALLED then an 
	 *          IllegalStateException is thrown.</li>
	 * 		<li>If this subsystem's state is ACTIVE, STARTING or STOPPING, this 
	 *          subsystem is stopped as described in the Subsystem.stop() 
	 *          method. If Subsystem.stop() throws an exception, a Framework 
	 *          event of type FrameworkEvent.ERROR is fired containing the 
	 *          exception.</li>
	 * 		<li>This subsystem's state is set to UNINSTALLED.</li>
	 * 		<li>A subsystem event of type SubsystemEvent.UNINSTALLED is fired.</li>
	 * 		<li>This subsystem and any persistent storage area provided for this 
	 *          subsystem by the Framework are removed.</li>
	 * </ol>
	 * @throws SubsystemException If the uninstall failed.
	 * @throws IllegalStateException If the subsystem is already in the 
	 *         UNISTALLED state.
	 * @throws SecurityException If the caller does not have the appropriate 
	 *         AdminPermission[this,LIFECYCLE] and the Java Runtime Environment 
	 *         supports permissions.
	 */
	public void uninstall() throws SubsystemException;
}

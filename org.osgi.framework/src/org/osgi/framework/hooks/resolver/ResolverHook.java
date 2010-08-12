/*
 * Copyright (c) OSGi Alliance (2010). All Rights Reserved.
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

package org.osgi.framework.hooks.resolver;

import java.util.Collection;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.Capability;
import org.osgi.framework.wiring.FrameworkWiring;

/**
 * OSGi Framework Resolver Hook Service.
 * 
 * <p>
 * Services registered with this service interface will be called by the framework during a resolve
 * process.  The framework must at most have one resolve process running at any given point 
 * in time.  A resolver hook may influence the outcome of a resolve process by removing entries
 * from shrinkable collections that are passed to the hook during a resolve process.  A shrinkable 
 * collection is a {@code Collection} that supports all remove operations.  Any other attempts to modify
 * the a shrinkable collection will result in an {@code UnsupportedOperationException} being thrown.
 * <p>
 * The following steps outline the rules a framework must follow during a bundle resolve
 * process and the order in which the hooks are called.
 * <ol>
 *  <li> Collect a snapshot of registered resolver hooks that will be called during the
 *       current resolve process.  Any hooks registered after the snapshot is taken must not be called
 *       during the current resolve process.  A resolver hook contained in the snapshot may become
 *       unregistered during the resolve process.  The framework should handle this and stop calling
 *       the unregistered hook for the remainder of the resolve process.</li>
 *  <li> For each registered hook call the {@link #begin()} method to inform the hooks about a resolve 
 *       process beginning.</li>
 *  <li> Determine the collection of unresolved bundle revisions that may be considered for resolution during
 *       the current resolution process and place each of the bundle revisions in a shrinkable collection
 *       <b>{@code R}</b>.
 *       <ol type="a">
 *         <li> For each registered hook call the {@link #filterResolvable(Collection)} method with the 
 *              shrinkable collection <b>{@code R}</b>.</li>
 *       </ol>
 *  </li>
 *  <li> The shrinkable collection <b>{@code R}</b> now contains all the unresolved bundle revisions that may end up
 *       as resolved at the end of the current resolve process.  Any other bundle revisions that 
 *       got removed from the shrinkable collection <b>{@code R}</b> must not end up as resolved at the end
 *       of the current resolve process.</li>
 *  <li> For each bundle revision {@code B} left in the shrinkable collection <b>{@code R}</b> that represents a 
 *       singleton bundle do the following:
 *       <ol type="a">
 *         <li> Determine the collection of available capabilities that have a name space of 
 *              {@link Capability#BUNDLE_CAPABILITY osgi.bundle}, are singletons, and have the same
 *              symbolic name as the singleton bundle revision <b>{@code B}</b> and place each of the matching
 *              capabilities into a shrinkable collection <b>{@code S}</b>.</li>
 *         <li> Remove the {@link Capability#BUNDLE_CAPABILITY osgi.bundle} capability provided by
 *              bundle revision <b>{@code B}</b> from shrinkable collection <b>{@code S}</b>.  A singleton bundle 
 *              cannot collide with itself.</li>
 *         <li> For each registered hook call the {@link #filterSingletonCollisions(Capability, Collection)}
 *              with the {@link Capability#BUNDLE_CAPABILITY osgi.bundle} capability provided by bundle revision 
 *              <b>{@code B}</b> and the shrinkable collection <b>{@code S}</b></li>
 *         <li> The shrinkable collection <b>{@code S}</b> now contains all singleton {@link Capability#BUNDLE_CAPABILITY 
 *              osgi.bundle} capabilities that can influence the the ability of bundle revision <b>{@code B}</b> to resolve.</li>
 *       </ol>
 *  </li>
 *  <li> During a resolve process a framework is free to attempt to resolve any or all bundles contained in
 *       shrinkable collection <b>{@code R}</b>.  For each bundle revision <b>{@code B}</b> left in the shrinkable collection 
 *       <b>{@code R}</b> which the framework attempts to resolve the following steps must be followed:
 *       <ol type="a">
 *         <li> For each requirement <b>{@code R}</b> specified by bundle revision <b>{@code B}</b> determine the 
 *              collection of capabilities that satisfy (or match) the constraint and place each matching capability into
 *              a shrinkable collection <b>{@code C}</b>.</li>
 *         <li> For each registered hook call the {@link #filterMatches(BundleRevision, Collection)} with the
 *              bundle revision <b>{@code B}</b> and the shrinkable collection <b>{@code C}</b>.</li>
 *         <li> The shrinkable collection <b>{@code C}</b> now contains all the capabilities that may be used to 
 *              satisfy the requirement <b>{@code T}</b>.  Any other capabilities that got removed from the 
 *              shrinkable collection <b>{@code C}</b> must not be used to satisfy requirement <b>{@code T}</b>.</li>
 *       </ol>
 *  </li>
 *  <li> For each registered hook call the {@link #end()} method to inform the hooks about a resolve 
 *       process ending.</li>
 * </ol>
 * In all cases, the order in which the resolver hooks are called is the reverse compareTo ordering of 
 * their Service References.  That is, the service with the highest ranking number must be called first.
 * In cases where a shrinkable collection becomes empty the framework is required to call the
 * remaining registered hooks.
 * <p>
 * Resolver hooks are low level.  Implementations of the resolver hook must be careful not to create an unresolvable state which
 * is very hard for a developer or a provisioner to diagnose.  Resolver hooks also must not be allowed to start another synchronous 
 * resolve process (e.g. by calling {@link Bundle#start()} or {@link FrameworkWiring#resolveBundles(Collection)}).  
 * The framework must detect this and throw an {@link IllegalStateException}.
 * 
 * @ThreadSafe
 * @version $Id$
 */
public interface ResolverHook {

	/**
	 * This method is called once at the beginning of the resolve process
	 * before any other methods are called on this hook.
	 */
	void begin();

	/**
	 * Filter resolvable candidates hook method.  This method may be called
	 * multiple times during a single resolve process.
	 * This method can filter the collection of candidates by removing 
	 * potential candidates.  Removing a candidate will prevent the candidate
	 * from resolving during the current resolve process. 
	 * 
	 * @param candidates the collection of resolvable candidates available during
	 * a resolve process. 
	 */
	void filterResolvable(Collection<BundleRevision> candidates);

	/**
	 * Filter singleton collisions hook method. This method is called during the resolve process
	 * for the specified singleton.  The specified singleton represents a singleton capability
	 * and the specified collection represent a collection of singleton capabilities which are 
	 * considered collision candidates.  The singleton capability and the collection of collision 
	 * candidates must all use the same name space.
	 * <p>
	 * Currently only capabilities with the name space of {@link Capability#BUNDLE_CAPABILITY 
	 * osgi.bundle} can be singletons.  In that case all the collision candidates
	 * have the name space of {@link Capability#BUNDLE_CAPABILITY osgi.bundle}, are singletons, 
	 * and have the same symbolic name as the specified singleton capability.
	 * <p>
	 * In the future, capabilities in other name spaces may support the singleton concept.
	 * Hook implementations should be prepared to receive calls to this method for 
	 * capabilities in name spaces other than {@link Capability#BUNDLE_CAPABILITY 
	 * osgi.bundle}.  
	 * <p>
	 * This method can filter the list of collision candidates by removing potential collisions.
	 * Removing a collision candidate will allow the specified singleton to resolve regardless of 
	 * the resolution state of the removed collision candidate.
	 * 
	 * @param singleton the singleton involved in a resolve process
	 * @param collisionCandidates a collection of singleton collision candidates
	 */
	void filterSingletonCollisions(Capability singleton, Collection<Capability> collisionCandidates);

	/**
	 * Filter matches hook method. This method is called during the resolve process for the 
	 * specified requirer.  The collection of candidates match a requirement for the requirer.
	 * This method can filter the collection of matching candidates by removing candidates from 
	 * the collection.  Removing a candidate will prevent the resolve process from choosing the 
	 * removed candidate to satisfy a requirement for the requirer.
	 * <p>
	 * All of the candidates will have the same name space and will 
	 * match a requirement of the requirer.
	 * @param requirer the bundle revision which contains a requirement
	 * @param candidates a collection of candidates that match a requirement of the requirer
	 */
	void filterMatches(BundleRevision requirer, Collection<Capability> candidates);

	/**
	 * This method is called once at the end of the resolve process.
	 * After the end method is called the resolve process has ended.
	 * No methods will be called on this hook except after the
	 * {@link #begin() begin} method is called again to indicate
	 * a new resolve process is beginning.
	 */
	void end();
}

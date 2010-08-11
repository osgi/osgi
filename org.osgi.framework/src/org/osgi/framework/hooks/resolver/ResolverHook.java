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

import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.Capability;

/**
 * OSGi Framework Resolver Hook Service.
 * 
 * <p>
 * Services registered with this service interface will be called by the framework during bundle resolve
 * process.  The framework must at most have one resolve process running at any given point 
 * in time.    The resolver hook may influence the outcome of a resolve operation by removing entries
 * from shrinkable collections that are passed to the hook during a resolve process.  A shrinkable 
 * collection is a {@code Collection} that supports all remove operations.  Any other attempts to modify
 * the a shrinkable collection will result in an {@code UnsupportedOperationException} being thrown.
 * <p>
 * The following steps outline the rules a framework must follow during a bundle resolve
 * operation and the order in which the hooks are called.
 * <ol>
 *  <li> For each registered hook call the {@link #begin()} method to inform the hooks about a resolve 
 *       process beginning.</li>
 *  <li> Determine the collection of unresolved bundle revisions that may be considered for resolution during
 *       the current resolution process and place each of the bundle revisions in a shrinkable collection
 *       {@code R}.
 *       <ol type="a">
 *         <li> For each registered hook call the {@link #filterResolvable(Collection)} method with the 
 *              shrinkable collection {@code R}.</li>
 *       </ol>
 *  </li>
 *  <li> The shrinkable collection {@code R} now contains all the unresolved bundle revisions that may end up
 *       as resolved at the end of the current resolve process.  Any other bundle revisions that 
 *       got removed from the shrinkable collection {@code R} must not end up as resolved at the end
 *       of the current resolve operation.</li>
 *  <li> For each bundle revision {@code B} left in the shrinkable collection {@code R} that represents a 
 *       singleton bundle do the following:
 *       <ol type="a">
 *         <li> Determine the collection of available capabilities that have a name space of 
 *              {@link Capability#BUNDLE_CAPABILITY osgi.bundle}, are singletons, and have the same
 *              symbolic name as the singleton bundle revision {@code B} and place each of the matching
 *              capabilities into a shrinkable collection {@code S}.</li>
 *         <li> Remove the {@link Capability#BUNDLE_CAPABILITY osgi.bundle} capability provided by
 *              bundle revision {@code B}.  A singleton bundle cannot collide with itself.</li>
 *         <li> For each registered hook call the {@link #filterSingletonCollisions(Capability, Collection)}
 *              with the singleton bundle revision {@code B} and the shrinkable collection {@code S}</li>
 *         <li> The shrinkable collection {@code S} now contains all singleton {@link Capability#BUNDLE_CAPABILITY 
 *              osgi.bundle} capabilities that can influence the the ability of bundle revision {@code B} to resolve.</li>
 *       </ol>
 *  </li>
 *  <li> During a resolve process a framework is free to attempt to resolve any or all bundles contained in
 *       shrinkable collection {@code R}.  For each bundle revision {@code B} left in the shrinkable collection 
 *       {@code R} which the framework attempts to resolve the following steps must be followed:
 *       <ol type="a">
 *         <li> For each requirement {@code T} specified by bundle revision {@code B} determine the collection of 
 *              capabilities that satisfy (or match) the constraint and place each matching capability into
 *              a shrinkable collection {@code C}.</li>
 *         <li> For each registered hook call the {@link #filterMatchingCapabilities(BundleRevision, Collection)} with the
 *              bundle revision {@code B} and the shrinkable collection {@code C}.</li>
 *         <li> The shrinkable collection {@code C} now contains all the capabilities that may be used to 
 *              satisfy the requirement {@code T}.  Any other capabilities that got removed from the 
 *              shrinkable collection {@code C} must not be used to satisfy requirement {@code T}.</li>
 *       </ol>
 *  </li>
 *  <li> For each registered hook call the {@link #end()} method to inform the hooks about a resolve 
 *       process ending.</li>
 * </ol>
 * In all cases, the order in which the resolver hooks are called is the reverse compareTo ordering of 
 * their Service References.  That is, the service with the highest ranking number must be called first.
 * In cases where a shrinkable collection is empty the framework is free to skip calls the the registered hooks.
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
	 * multiple times during a single resolve operation.
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
	 * and the collection of collision candidates which are also singletons.  The 
	 * singleton capability and the collection of collision candidates must all use the
	 * same name space.
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
	 * @param singleton the singleton involved in a resolve operation
	 * @param collisionCandidates a collection of singleton collision candidates
	 */
	void filterSingletonCollisions(Capability singleton, Collection<Capability> collisionCandidates);

	/**
	 * Filter matching capabilities hook method. This method is called during the resolve process
	 * for the specified requirer.  The collection of candidates match a single
	 * requirement for the requirer.  This method can filter the collection of 
	 * matching candidates by removing candidates from the collection.  Removing a candidate 
	 * will prevent the resolve operation from choosing the removed candidate to satisfy
	 * a requirement for the requirer.
	 * <p>
	 * All of the candidates will have the same name space and will 
	 * match a single requirement of the requirer.
	 * @param requirer the bundle revision which contains a requirement
	 * @param candidates a collection of candidates that match a requirement of the requirer
	 */
	void filterMatchingCapabilities(BundleRevision requirer, Collection<Capability> candidates);

	/**
	 * This method is called once at the end of the resolve process.
	 * After the end method is called the resolve process has ended.
	 * No methods will be called on this hook except after the
	 * {@link #begin() begin} method is called to indicate
	 * a new resolve process is beginning.
	 */
	void end();
}

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
 * operations.  The resolver hook may influence the outcome of a resolve operation by removing entries
 * from shrinkable collections that are passed to the hook during a resolve operation.  A shrinkable 
 * collection is a {@code Collection} that supports all remove operations.  Any other attempts to modify
 * the a shrinkable collection will result in an {@code UnsupportedOperationException} being thrown.
 * <p>
 * The following steps outline the rules a framework must follow during a bundle resolve
 * operation and the order in which the hooks are called.
 * <ol>
 *  <li> Determine the collection of unresolved bundle revisions that may be considered for resolution during
 *       the current resolution process and place each of the bundle revisions in a shrinkable collection {@code R}.
 *       <ol>
 *         <li> For each registered hook call the {@link #filterResolvable(Collection)} method with the 
 *              shrinkable collection {@code R}.
 *       </ol>
 *  <li> The shrinkable collection {@code R} now contains all the unresolved bundle revisions that may end up
 *       as resolved at the end of the current resolve process.  Any other bundle revisions that 
 *       got removed from the shrinkable collection {@code R} must not end up as resolved at the end
 *       of the current resolve operation.
 *  <li> For each bundle revision {@code B} left in the shrinkable collection {@code R} that represents a 
 *       singleton bundle do the following:
 *       <ol>
 *         <li> Determine the collection of available capabilities that have a name space of 
 *              {@link Capability#BUNDLE_CAPABILITY osgi.bundle}, are singletons, and have the same
 *              symbolic name as the singleton bundle revision {@code B} and place each of the matching
 *              capabilities into a shrinkable collection {@code S}.
 *         <li> Remove the {@link Capability#BUNDLE_CAPABILITY osgi.bundle} capability provided by
 *              bundle revision {@code B}.  A singleton bundle cannot collide with itself.
 *         <li> For each registered hook call the {@link #filterSingletonCollisions(BundleRevision, Collection)}
 *              with the singleton bundle revision {@code B} and the shrinkable collection {@code S}
 *         <li> The shrinkable collection {@code S} now contains all singleton {@link Capability#BUNDLE_CAPABILITY 
 *              osgi.bundle} capabilities that can influence the the ability of bundle revision {@code B} to resolve. 
 *       </ol>
 *  <li> During a resolve process a framework is free to attempt to resolve any or all bundles contained in
 *       shrinkable collection {@code R}.  For each bundle revision {@code B} left in the shrinkable collection 
 *       {@code R} which the framework attempts to resolve the following steps must be followed:
 *       <ol>
 *         <li> For each requirement {@code T} specified by bundle revision {@code B} determine the collection of 
 *              capabilities that satisfy (or match) the constraint and place each matching capability into
 *              a shrinkable collection {@code C}.
 *         <li> For each registered hook call the {@link #filterCandidates(BundleRevision, Collection)} with the
 *              bundle revision {@code B} and the shrinkable collection {@code C}.
 *         <li> The shrinkable collection {@code C} now contains all the capabilities that may be used to 
 *              satisfy the requirement {@code T}.  Any other capabilities that got removed from the 
 *              shrinkable collection {@code C} must not be used to satisfy requirement {@code T}.
 *       </ol>
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
	 * Filter resolvable candidates hook method.  This method is called once during
	 * a single resolve operation.
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
	 * for the specified bundle revision.  The specified bundle revision represents a singleton bundle
	 * and the collection of collision candidates will have a name space of 
	 * {@link Capability#BUNDLE_CAPABILITY osgi.bundle}, are singletons, and have the same
	 * symbolic name as the specified bundle revision.
	 * <p>
	 * This method can filter the list of collision candidates by removing potential collisions.
	 * Removing a collision candidate will allow the specified singleton to resolve regardless of 
	 * the resolution state of the removed collision candidate.
	 * 
	 * @param singleton the singleton involved in a resolve operation
	 * @param collisionCandidates a collection of singleton collision candidates
	 */
	void filterSingletonCollisions(BundleRevision singleton, Collection<Capability> collisionCandidates);

	/**
	 * Filter candidates hook method. This method is called during the resolve process
	 * for the specified requirer.  The collection of candidates match a single
	 * requirement for the requirer.  This method can filter the collection of 
	 * candidates by removing potential candidates.  Removing a candidate will
	 * prevent the resolve operation from choosing the candidate to satisfy
	 * a requirement for the requirer.
	 * <p>
	 * All of the candidates will have the same name space and will 
	 * match a single requirement of the requirer.
	 * @param requirer the bundle revision which contains a requirement
	 * @param candidates a collection of candidates that match a requirement of the requirer
	 */
	void filterCandidates(BundleRevision requirer, Collection<Capability> candidates);
}

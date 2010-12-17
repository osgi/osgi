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
 * OSGi Framework Resolver Hook instances are obtained from the OSGi {@link ResolverHookFactory
 * Framework Resolver Hook Factory} service.  
 * 
 * <p>
 * A Resolver Hook instance is called by the framework during a resolve
 * process.  A resolver hook may influence the outcome of a resolve process by removing entries
 * from shrinkable collections that are passed to the hook during a resolve process.  A shrinkable 
 * collection is a {@code Collection} that supports all remove operations.  Any other attempts to modify
 * a shrinkable collection will result in an {@code UnsupportedOperationException} being thrown.
 * 
 * ### removed the nested LIs, will be describedin the spec
 * <p>
 * The order in which the resolver hooks are called is the reverse compareTo ordering of 
 * their Service References.  That is, the service with the highest ranking number must be called first.
 * In cases where a shrinkable collection becomes empty the framework is required to call the
 * remaining registered hooks.
 * <p>
 * Resolver hooks are low level.  Implementations of the resolver hook must be careful not to create an unresolvable state which
 * is very hard for a developer or a provisioner to diagnose.  Resolver hooks also must not be allowed to start another synchronous 
 * resolve process (e.g. by calling {@link Bundle#start()} or {@link FrameworkWiring#resolveBundles(Collection)}).  
 * The framework must detect this and throw an {@link IllegalStateException}.
 * 
 * @see ResolverHookFactory
 * @ThreadSafe
 * @version $Id$
 */
public interface ResolverHook {
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
	 * <p>
	 * If the Java Runtime Environment supports permissions then the collection of 
	 * candidates will only contain candidates for which the requirer has permission to
	 * access.
	 * @param requirer the bundle revision which contains a requirement
	 * @param candidates a collection of candidates that match a requirement of the requirer
	 */
	void filterMatches(BundleRevision requirer, Collection<Capability> candidates);

	/**
	 * This method is called once at the end of the resolve process.
	 * After the end method is called the resolve process has ended.
	 * The framework must not hold onto this resolver hook instance
	 * after end has been called.
	 */
	void end();
}

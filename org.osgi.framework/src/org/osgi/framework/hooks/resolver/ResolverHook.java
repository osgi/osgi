package org.osgi.framework.hooks.resolver;

import java.util.Collection;

import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.Capability;

/**
 * OSGi Framework Resolver Hook Service.
 * 
 * <p>
 * Services registered with this service interface will be called during bundle resolution
 * operations.
 * 
 * @ThreadSafe
 * @version $Id$
 */
public interface ResolverHook {

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

	/**
	 * Filter singleton collisions hook method. This method is called during the resolve process
	 * for the specified singleton.  The specified singleton and the collection of collision candidates
	 * will have a name space of {@link Capability#BUNDLE_CAPABILITY osgi.bundle}, with the same
	 * symbolic name and are singletons.
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
	 * Filter resolvable candidates hook method.  This method is called during
	 * a resolve process.
	 * This method can filter the collection of candidates by removing 
	 * potential candidates.  Removing a candidate will prevent the candidate
	 * from resolving during the current resolve process. 
	 * 
	 * @param candidates the collection of resolvable candidates available during
	 * a resolve process. 
	 */
	void filterResolvable(Collection<BundleRevision> candidates);
}

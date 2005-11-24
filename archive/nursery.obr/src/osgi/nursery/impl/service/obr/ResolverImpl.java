/*
 * $Header$
 * 
 * Copyright (c) The OSGi Alliance (2005). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package osgi.nursery.impl.service.obr;

import java.util.*;

import osgi.nursery.resource.*;
import osgi.nursery.service.obr.*;

/**
 * The Resolver resolves the dependencies of a set of resources.
 * 
 * @version $Revision$
 */
public class ResolverImpl implements Resolver {
	RepositoryAdminImpl					parent;
	Set<ResourceImpl>					installSet				= new HashSet<ResourceImpl>();
	Set<ResourceImpl>					requiredResources		= new HashSet<ResourceImpl>();
	Set<ResourceImpl>					optionalResources		= new HashSet<ResourceImpl>();
	Set<RequirementImpl>				unsatisfiedRequirements	= new HashSet<RequirementImpl>();
	Map<ResourceImpl, Set<Requirement>>	causes					= new HashMap<ResourceImpl, Set<Requirement>>();

	ResolverImpl(RepositoryAdminImpl parent) {
		this.parent = parent;
	}

	public boolean resolve() {
		requiredResources.clear();
		optionalResources.clear();
		unsatisfiedRequirements.clear();
		causes.clear();

		// Set the capabilities of the environment
		// as the first required set.
		// ### must also add existing bundles.
		requiredResources.add(parent.environment);

		// First match the required resources. We use a recursive
		// algorithm to fill the required list. Resources
		// can be required or are optional. This is kept in the
		// corresponding lists

		for (ResourceImpl installing : installSet)
			addDependent(installing);

		// Now find the extensions. Extensions are requirements
		// that select a resource. If that resource is in the
		// installSet, we add it to the options. The calculation
		// is only done on the installset. If an option is selected,
		// it must be added to the installSet and the resolve must be
		// rerun.

		outer: for (ResourceImpl extension : parent.getExtenders()) {
			// For each of the extends in the extender
			for (RequirementImpl requirementOnHost : extension.getExtendList()) {
				// Extends are an OR, any one that matches selects
				// the host
				if (canBeSatisfiedBy(installSet, requirementOnHost)) {
					optionalResources.add(extension);
					addCause(extension, requirementOnHost);
					continue outer;
				}
			}
		}

		// Remove duplicates in lists
		optionalResources.removeAll(requiredResources);
		requiredResources.remove(parent.environment);
		requiredResources.removeAll(installSet);

		return unsatisfiedRequirements.isEmpty();
	}

	/**
	 * Check if the set of resources matches the given requirement.
	 * 
	 * @param result
	 * @param rq
	 * @return
	 */
	boolean canBeSatisfiedBy(Collection<ResourceImpl> result, RequirementImpl rq) {
		for (ResourceImpl resource : result) {
			if (resource.isSatisfiedBy(rq))
				return true;
		}
		return false;
	}

	/**
	 * Dependency routine, recursive. This function will find resources that
	 * match the requirements of the resource. It keeps a list of requirements
	 * that are not met.
	 * 
	 * @param requiredResources
	 * @param unsatisfiedRequirements
	 * @param installing
	 */
	void addDependent(ResourceImpl installing) {
		// Did we already have it (or is being worked upon).
		if (requiredResources.contains(installing))
			return;

		requiredResources.add(installing);

		// For each requirement of this resource

		for (RequirementImpl requirement : installing.getRequirementList()) {
			// If the requirements can satisfy the requirements, do not bother.
			// ### should I also check the resources?

			if (canBeSatisfiedBy(requiredResources, requirement)) {
				//System.out.println("Can be satisfied locally" );
			} else {
				//
				// Must find a resource that has the required
				// capabilities.
				//
				Set<ResourceImpl> select = select(requirement);
				if (select.isEmpty()) {
					// No resource found
					unsatisfiedRequirements.add(requirement);
					//System.out.println("Missing in repositories" );
				}
				else {
					switch (requirement.getCardinality()) {
						case Requirement.OPTIONAL :
						case Requirement.MULTIPLE :
							optionalResources.addAll(select);
							//System.out.println("Optional" );
							for (ResourceImpl dependent : select) {
								addCause(dependent, requirement);
							}
							break;

						case Requirement.UNARY :
							// Hmm, could optimize with a smarter
							// strategy here ... Just get the first one.
							// ### invent an optimizing strategy
							ResourceImpl dependent = select.iterator().next();
							addCause(dependent, requirement);
							addDependent(dependent);
							break;
					}
				}
			}
		}

	}

	/**
	 * Return the list of resources that provide the given requirement.
	 * 
	 * @param requirement
	 * @return
	 */
	Set<ResourceImpl> select(RequirementImpl requirement) {
		Set<ResourceImpl> result = new HashSet<ResourceImpl>();
		for (RepositoryImpl repository : parent.repositories.values()) {
			nextResource: for (ResourceImpl resource : repository
					.getResourceList()) {
				if (resource.isSatisfiedBy(requirement))
					result.add(resource);
			}
		}
		return result;
	}

	/**
	 * Add the requirement that causes a resource to be included in the required
	 * or extensions set.
	 * 
	 * @param r
	 * @param rq
	 */
	void addCause(ResourceImpl r, Requirement rq) {
		Set<Requirement> set = causes.get(r);
		if (set == null) {
			set = new HashSet<Requirement>();
			causes.put(r, set);
		}
		set.add(rq);
	}

	public void add(Resource resource) {
		installSet.add((ResourceImpl) resource);
	}

	public Requirement[] getUnsatisfiedRequirements() {
		Requirement[] result = new Requirement[unsatisfiedRequirements.size()];
		unsatisfiedRequirements.toArray(result);
		return result;
	}

	public Resource[] getOptionalResources() {
		Resource[] result = new Resource[optionalResources.size()];
		optionalResources.toArray(result);
		return result;
	}

	public Resource[] getRequiredResources() {
		Resource[] result = new Resource[requiredResources.size()];
		requiredResources.toArray(result);
		return result;
	}

	public void deploy() {
		// TODO Auto-generated method stub
	}

	public Requirement[] getReason(Resource resource) {
		Set<Requirement> reasons = causes.get(resource);
		if (reasons != null) {
			Requirement[] result = new Requirement[reasons.size()];
			reasons.toArray(result);
			return result;
		}
		return new Requirement[0];
	}

	public Resource[] getAddedResources() {
		Resource[] result = new Resource[installSet.size()];
		installSet.toArray(result);
		return result;
	}

}

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

package nursery.obr.admin;

import java.util.*;

import nursery.obr.resource.*;

import org.osgi.service.obr.*;

/**
 * The Resolver resolves the dependencies of a set of resources.
 * 
 * @version $Revision$
 */
public class ResolverImpl implements Resolver {
	RepositoryAdminImpl	parent;
	Set					installSet				= new HashSet();
	Set					requiredResources		= new HashSet();
	Set					optionalResources		= new HashSet();
	Set					unsatisfiedRequirements	= new HashSet();
	Map					causes					= new HashMap();

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

		for (Iterator i = installSet.iterator(); i.hasNext();) {
			ResourceImpl installing = (ResourceImpl) i.next();
			addDependent(installing);
		}

		// Now find the extensions. Extensions are requirements
		// that select a resource. If that resource is in the
		// installSet, we add it to the options. The calculation
		// is only done on the installset. If an option is selected,
		// it must be added to the installSet and the resolve must be
		// rerun.

		outer: for (Iterator i = parent.getExtenders().iterator(); i.hasNext();) {
			ResourceImpl extension = (ResourceImpl) i.next();
			// For each of the extends in the extender
			for (Iterator rq = extension.getExtendList().iterator(); rq
					.hasNext();) {
				RequirementImpl requirementOnHost = (RequirementImpl) rq.next();
				// Extends are an OR, any one that matches selects
				// the host
				Resource resource = canBeSatisfiedBy(installSet, requirementOnHost); 
				if (resource!=null) {
					optionalResources.add(extension);
					// TODO not sure if this is resource or extension?
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
	ResourceImpl canBeSatisfiedBy(Collection result, RequirementImpl rq) {
		for (Iterator r = result.iterator(); r.hasNext();) {
			ResourceImpl resource = (ResourceImpl) r.next();
			if (resource.isSatisfiedBy(rq))
				return resource;
		}
		return null;
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

		for (Iterator r = installing.getRequirementList().iterator(); r
				.hasNext();) {
			RequirementImpl requirement = (RequirementImpl) r.next();
			// If the requirements can satisfy the requirements, do not bother.
			// ### should I also check the resources?

			ResourceImpl satisficant = canBeSatisfiedBy(requiredResources, requirement);
			if (satisficant!=null) {
				// System.out.println("Can be satisfied locally" );
				addCause(satisficant, requirement);
			}
			else {
				//
				// Must find a resource that has the required
				// capabilities.
				//
				Set select = select(requirement);
				if (select.isEmpty() && requirement.getCardinality() == Requirement.UNARY) {
					// No resource found
					unsatisfiedRequirements.add(requirement);
					// System.out.println("Missing in repositories" );
				}
				else {
					switch (requirement.getCardinality()) {
						case Requirement.OPTIONAL :
						case Requirement.MULTIPLE :
							optionalResources.addAll(select);
							// System.out.println("Optional" );
							for (Iterator i = select.iterator(); i.hasNext();) {
								ResourceImpl dependent = (ResourceImpl) i
										.next();
								addCause(dependent, requirement);
							}
							break;

						case Requirement.UNARY :
							// Hmm, could optimize with a smarter
							// strategy here ... Just get the first one.
							// ### invent an optimizing strategy
							// Lets try the one with the least nr of
							// required resources, or if equal, with 
							// the max nr of capabilities.
							ResourceImpl selected = null;
							for (Iterator i = select.iterator(); i.hasNext();) {
								ResourceImpl dependent = (ResourceImpl) i
										.next();
								if (selected == null )
									selected = dependent;
								
								if ( selected.getRequirementList().size() 
										== dependent.getRequirementList().size()) {
									if (selected.getCapabilityList().size() 
											< dependent.getCapabilityList().size() )
										selected = dependent;
								} else {
									if ( selected.getRequirementList().size() > dependent.getRequirementList().size())
										selected = dependent;
								}

							}

							addCause(selected, requirement);
							addDependent(selected);
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
	Set select(RequirementImpl requirement) {
		Set result = new HashSet();
		for (Iterator i = parent.repositories.values().iterator(); i.hasNext();) {
			RepositoryImpl repository = (RepositoryImpl) i.next();
			for (Iterator rs = repository.getResourceList().iterator(); rs
					.hasNext();) {
				ResourceImpl resource = (ResourceImpl) rs.next();
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
		Set set = (Set) causes.get(r);
		if (set == null) {
			set = new HashSet();
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
		Set reasons = (Set) causes.get(resource);
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

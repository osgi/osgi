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

import java.net.URL;
import java.util.*;

import org.osgi.framework.Version;

import osgi.nursery.resource.*;
import osgi.nursery.service.obr.*;


/**
 * Prototype implementatation of the Repository Admin service.
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public class RepositoryAdminImpl implements RepositoryAdmin {
	Map<String, RepositoryImpl>	repositories		= new HashMap<String, RepositoryImpl>();
	Resource[]					EMPTY_RESOURCE		= new Resource[0];
	String[]					EMPTY_STRING		= new String[0];
	RepositoryImpl[]			EMPTY_REPOSITORY	= new RepositoryImpl[0];
	ResourceImpl				environment			= new ResourceImpl(
															"environment",
															new Version("0"));

	/**
	 * Setup the environment resource.
	 *
	 */
	public RepositoryAdminImpl() {
		CapabilityImpl ee = new CapabilityImpl("ee");
		ee.addProperty("ee", "CDC-1.0/Foundation-1.0");
		ee.addProperty("ee", "OSGi/Minimum-1.1");
		ee.addProperty("ee", "JRE-1.1");
		ee.addProperty("ee", "J2SE-1.2");
		ee.addProperty("ee", "J2SE-1.3");
		ee.addProperty("ee", "J2SE-1.4");
		ee.addProperty("ee", "J2SE-5.0");
		environment.addCapability(ee);

		CapabilityImpl fw = new CapabilityImpl("package");
		fw.addProperty("package", "org.osgi.framework");
		fw.addProperty("version", new Version("1.3"));
		environment.addCapability(fw);
	}

	/**
	 * Select all resources that fullfil the requirements, though there is no
	 * guarantee that their requirements are fullfilled.
	 * 
	 * @param requirements
	 * @return
	 * @see osgi.nursery.service.obr.RepositoryAdmin#discoverResources(java.util.List)
	 */
	public Resource[] discoverResources(String filterExpr) {
		Filter f = new Filter(filterExpr);
		List<ResourceImpl> result = new ArrayList<ResourceImpl>();
		for (RepositoryImpl repository : repositories.values()) {
			for (ResourceImpl resource : repository.getResourceList()) {
				if (f.match(resource.asMap()))
					result.add(resource);
			}
		}
		return result.toArray(EMPTY_RESOURCE);
	}

	/**
	 * Get the resources that provide all the given requirements.
	 * 
	 * @param requirements
	 * @return
	 */
	List<ResourceImpl> select(List<RequirementImpl> requirements) {
		List<ResourceImpl> result = new ArrayList<ResourceImpl>();
		for (RepositoryImpl repository : repositories.values()) {
			nextResource: for (ResourceImpl resource : repository
					.getResourceList()) {
				for (RequirementImpl requirement : requirements) {
					if (!resource.isSatisfiedBy(requirement))
						continue nextResource;
				}
				result.add(resource);
			}
		}
		return result;
	}

	/**
	 * Return the list of resources that provide the given requirement.
	 * 
	 * @param requirement
	 * @return
	 */
	List<ResourceImpl> select(RequirementImpl requirement) {
		List<ResourceImpl> result = new ArrayList<ResourceImpl>();
		for (RepositoryImpl repository : repositories.values()) {
			nextResource: for (ResourceImpl resource : repository
					.getResourceList()) {
				if (resource.isSatisfiedBy(requirement))
					result.add(resource);
			}
		}
		return result;
	}

	public ResourceImpl[] deployedResources() {
		// TODO
		return null;
	}

	/**
	 * Find the list of resources that are needed to make the resources in the resolver
	 * resolve.
	 * 
	 * @param resolver
	 * @return
	 */
	boolean resolve(ResolverImpl resolver) {
		Set<ResourceImpl> result = new HashSet<ResourceImpl>();
		Set<RequirementImpl> unsatisfied = new HashSet<RequirementImpl>();
		result.add(environment);
		for (Iterator r = resolver.resources.iterator(); r.hasNext();) {
			ResourceImpl resource = (ResourceImpl) r.next();
			addDependent(result, unsatisfied, resource);
		}
		result.remove(environment);
		result.removeAll(resolver.resources);
		resolver.missing = unsatisfied;
		resolver.required = result;
		return unsatisfied.isEmpty();
	}

	/**
	 * Dependency routine, recursive. This function will find 
	 * resources that match the requirements of the resource. It
	 * keeps a list of requirements that are not met.
	 * 
	 * @param result
	 * @param unsatisfied
	 * @param resource
	 */
	void addDependent(Set<ResourceImpl> result,
			Set<RequirementImpl> unsatisfied, ResourceImpl resource) {
		
		//
		// We already had it.
		//
		
		if (result.contains(resource))
			return;

		result.add(resource);
		
		// For each requirement
		
		Set<RequirementImpl> requirements = new HashSet<RequirementImpl>(
				resource.getRequirements());
		for (Iterator<RequirementImpl> i = requirements.iterator(); i.hasNext();) {
			RequirementImpl rq = i.next();
			
			//
			// If the result can satisfy the requirements, do not bother.
			//
			if (canBeSatisfiedBy(result, rq)) {
				i.remove();
			}
			else {
				//
				// Must find a resource that has the required
				// capabilities.
				//
				List<ResourceImpl> select = select(rq);
				if (select.isEmpty())
					// No resource found
					unsatisfied.add(rq);
				else {
					//  Very Primitive strategy to select one of many
					ResourceImpl dependent = select.get(0);
					addDependent(result, unsatisfied, dependent);
				}
			}
		}

	}

	/**
	 * Check if the set of resources matches the given requirement.
	 * 
	 * @param result
	 * @param rq
	 * @return
	 */
	boolean canBeSatisfiedBy(Set<ResourceImpl> result, RequirementImpl rq) {
		for (ResourceImpl resource : result) {
			if (resource.isSatisfiedBy(rq))
				return true;
		}
		return false;
	}

	/**
	 * Add a new repository.
	 * 
	 * @param repository
	 * @return
	 * @throws Exception
	 * @see osgi.nursery.service.obr.RepositoryAdmin#addRepository(java.net.URL)
	 */
	public Repository addRepository(URL repository) throws Exception {
		RepositoryImpl r = repositories.get(repository);
		if (r != null)
			return r;
		r = new RepositoryImpl(repository);
		r.refresh();
		repositories.put(repository.toString(), r);
		return r;
	}

	/**
	 * Remove the repository.
	 * 
	 * @param repository
	 * @return
	 */
	public boolean removeRepository(String repository) {
		return repositories.remove(repository) != null;
	}

	public Repository[] listRepositories() {
		return repositories.values().toArray(EMPTY_REPOSITORY);
	}

	/**
	 * Create a new resolver for the given resource.
	 * 
	 * @param resource
	 * @return
	 * @see osgi.nursery.service.obr.RepositoryAdmin#resolver(osgi.nursery.service.obr.Resource)
	 */
	public Resolver resolver(Resource resource) {
		Resolver resolver = new ResolverImpl(this);
		resolver.add(resource);
		return resolver;
	}
}

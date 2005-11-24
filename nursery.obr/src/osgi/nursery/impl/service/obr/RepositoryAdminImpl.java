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


	public ResourceImpl[] deployedResources() {
		// TODO
		return null;
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

	public Set<ResourceImpl> getExtenders() {
		Set<ResourceImpl>	extenders = new HashSet<ResourceImpl>();
		for ( RepositoryImpl repository : repositories.values() ) {
			for ( ResourceImpl resource : repository.getResourceList() ) {
				if ( !resource.getExtendList().isEmpty())
					extenders.add(resource);
			}
		}
		return extenders;
	}
}

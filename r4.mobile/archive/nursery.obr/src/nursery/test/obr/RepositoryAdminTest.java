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

package nursery.test.obr;

import nursery.obr.admin.RepositoryAdminImpl;
import nursery.obr.resource.ResourceImpl;

import org.osgi.service.obr.*;

import junit.framework.TestCase;

public class RepositoryAdminTest extends TestCase {
	RepositoryAdmin	admin;
	RepositoryAdmin	adminSingle;

	protected void setUp() throws Exception {
		super.setUp();
		admin = new RepositoryAdminImpl();
		admin.addRepository(getClass().getResource("repository.xml"));
		adminSingle = new RepositoryAdminImpl();
		adminSingle.addRepository(getClass().getResource("single.xml"));
	}

	/*
	 * Test method for
	 * 'aQute.impl.service.obr.RepositoryAdminImpl.discoverResources(List)'
	 */
	public void testDiscoverResources() {
		Resource resources[] = adminSingle
				.discoverResources("(name=org.osgi.impl.service.http)");
		print(resources);
	}

	private void print(Resource[] resources) {
		if (resources == null)
			System.out.println("No Resources");
		for (int i = 0; i < resources.length; i++) {
			Resource resource = resources[i];
			System.out.println(resource);
			System.out.println(resource.getDescription());
			System.out.println();
		}
	}

	/*
	 * Test method for
	 * 'aQute.impl.service.obr.RepositoryAdminImpl.listRequiredResources(Resource)'
	 */
	public void testListRequiredResources() throws Exception {
		Resource resources[] = admin
				.discoverResources("(name=org.osgi.impl.service.http)");
		assertTrue("Must get at least one", resources != null
				&& resources.length > 0);
		Resource http = resources[0];
		Resolver resolver = admin.resolver();
		resolver.add(http);
		assertTrue("Must resolve, all is in there", resolver.resolve());
		System.out.println("All Must be Resolved");
		print(resolver);

		resources = adminSingle
				.discoverResources("(name=org.osgi.impl.service.http)");
		assertTrue("Must get at least one", resources != null
				&& resources.length > 0);
		http = resources[0];
		resolver = adminSingle.resolver();
		resolver.add(http);
		assertFalse("Must not resolve, only http is in there", resolver
				.resolve());
		System.out.println("Missing");
		print(resolver);

	}

	private void print(Resolver resolver) {
		Resource[] required = resolver.getRequiredResources();
		String del = "Needs: ";
		for (int i = 0; i < required.length; i++) {
			System.out.print(del);
			System.out.println(required[i]);
			printCauses(resolver, required[i]);
		}
		if (required.length > 0)
			System.out.println();

		Resource[] optional = resolver.getOptionalResources();
		del = "Optional: ";
		for (int i = 0; i < optional.length; i++) {
			System.out.print(del);
			System.out.print(optional[i]);
			printCauses(resolver, optional[i]);
			del = ", ";
		}
		System.out.println();

		Requirement missing[] = resolver.getUnsatisfiedRequirements();
		del = "Missing: ";
		for (int i = 0; i < missing.length; i++) {
			System.out.print(del);
			System.out.print(missing[i]);
			del = ", ";
		}
		System.out.println();
	}

	private void printCauses(Resolver resolver, Resource required) {
		Requirement causes[] = resolver.getReason(required);
		String del = "        ";
		for (int i = 0; i < causes.length; i++) {
			Requirement cause = causes[i];
			System.out.print(del);
			System.out.print(cause);
		}
		if (causes.length > 0)
			System.out.println();
	}

	/*
	 * Test method for
	 * 'aQute.impl.service.obr.RepositoryAdminImpl.addDependent(Set<Resource>,
	 * Set<Requirement>, Resource)'
	 */
	public void testAddDependent() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for
	 * 'aQute.impl.service.obr.RepositoryAdminImpl.canBeSatisfiedBy(Set<Resource>,
	 * Requirement)'
	 */
	public void testCanBeSatisfiedBy() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for
	 * 'aQute.impl.service.obr.RepositoryAdminImpl.listRequestedResources(Resource)'
	 */
	public void testListRequestedResources() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for
	 * 'aQute.impl.service.obr.RepositoryAdminImpl.addRepository(String)'
	 */
	public void testAddRepository() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for
	 * 'aQute.impl.service.obr.RepositoryAdminImpl.removeRepository(String)'
	 */
	public void testRemoveRepository() {
		// TODO Auto-generated method stub

	}

	/*
	 * Test method for
	 * 'aQute.impl.service.obr.RepositoryAdminImpl.listRepositories()'
	 */
	public void testListRepositories() {
		// TODO Auto-generated method stub

	}

}

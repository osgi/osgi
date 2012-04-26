/*
 * Copyright (c) OSGi Alliance (2012). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.resolver.junit;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.resource.Wiring;
import org.osgi.service.resolver.HostedCapability;
import org.osgi.service.resolver.ResolveContext;
import org.osgi.service.resolver.Resolver;

public class ResolverTestCase extends AbstractResolverTestCase {

	public void testNull() throws Exception {
		final Resolver resolver = getResolverService();

		try {
			resolver.resolve(null);
		} catch (final NullPointerException npe) {
			// implementation did not check the context to be null
			fail();
		} catch (final Exception e) {
			// expected
			return;
		}
		// nothing has happened? That's wrong.
		fail();
	}

	public void testEmpty() throws Exception {
		final Resolver resolver = getResolverService();

		resolver.resolve(new ResolveContext() {

			@Override
			public List<Capability> findProviders(Requirement requirement) {
				fail();
				return null;
			}

			@Override
			public int insertHostedCapability(List<Capability> capabilities,
					HostedCapability hostedCapability) {
				fail();
				return -1;
			}

			@Override
			public boolean isEffective(Requirement requirement) {
				fail();
				return false;
			}

			@Override
			public Map<Resource, Wiring> getWirings() {
				fail();
				return null;
			}

		});
	}

	/*
	 * mandatory resource with no requirements
	 */
	public void testMandatoryResources0() throws Exception {
		final TestResource r1 = new TestResource();
		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1), null, null);
		context.checkMandatoryResources(shouldResolve(context));
	}

	/*
	 * mandatory resource with unresolved requirement
	 */
	public void testMandatoryResources1() throws Exception {
		final TestResource r1 = new TestResource();
		r1.addRequirement(null, "(foo=bar)");
		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1), null, null);
		shouldNotResolve(context);
	}

	/*
	 * mandatory resource with resolvable requirement
	 */
	public void testMandatoryResources2() throws Exception {
		final TestResource r1 = new TestResource();
		final Requirement req1 = r1.addRequirement(null, "(foo=bar)");
		final TestResource r2 = new TestResource();
		r2.addCapability(null, "foo=bar");
		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1, r2), null, null);
		context.checkMandatoryResources(shouldResolve(context));
		context.checkCallback(req1);
	}

	/*
	 * mandatory resource with unresolvable requirement
	 */
	public void testMandatoryResources3() throws Exception {
		final TestResource r1 = new TestResource();
		r1.addRequirement(null, "(foo=bar)");
		final TestResource r2 = new TestResource();
		r2.addCapability(null, "foo=else");
		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1, r2), null, null);
		shouldNotResolve(context);
	}

	/*
	 * mandatory resource but capability is in wrong namespace
	 */
	public void testMandatoryResources4() throws Exception {
		final TestResource r1 = new TestResource();
		r1.addRequirement(null, "(foo=bar)");
		final TestResource r2 = new TestResource();
		r2.addCapability("wrong", "foo=bar");
		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1, r2), null, null);
		shouldNotResolve(context);
	}

}

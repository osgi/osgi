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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.resolver.ResolverHook;
import org.osgi.framework.hooks.resolver.ResolverHookFactory;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.resource.Wiring;
import org.osgi.service.resolver.HostedCapability;
import org.osgi.service.resolver.ResolveContext;
import org.osgi.service.resolver.Resolver;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This is the Resolver test case
 */
public class ResolverTestCase extends DefaultTestBundleControl {

	private Bundle fwtestbundle;

	private ServiceRegistration resolverHookRegistration;

	private String webserver;

	protected void setUp() throws Exception {
		resolverHookRegistration = getContext().registerService(
				ResolverHookFactory.class, new Factory(), null);
	}

	protected void tearDown() throws Exception {
		resolverHookRegistration.unregister();
		resolverHookRegistration = null;
	}

	public void testZero() throws Exception {
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
	
	@Override
	public String getWebServer() {
		String w = webserver;
		if (w == null) {
			URL base = getFrameworkBundle().getEntry("/");
			webserver = w = (base == null) ? "/" : base.toExternalForm();
		}
		return w;
	}

	public void testFromFW() throws Exception {
		final Bundle bundle =installBundle("fragments.tb1a.jar", false);

		final BundleRevision revision = bundle.adapt(BundleRevision.class);
		final List<Capability> caps = revision.getCapabilities(null);
		assertNotNull(caps);
		System.out.println("CAPABILITIES " + caps);
		System.out.println("REQUIREMENTS " + revision.getRequirements(null));

	}

	private Resolver getResolverService() {
		final BundleContext context = getContext();
		final ServiceReference<Resolver> sref = context
				.getServiceReference(Resolver.class);
		assertNotNull(sref);
		return context.getService(sref);
	}

	private Bundle getFrameworkBundle() {
		if (fwtestbundle == null) {
			final BundleContext context = getContext();
			final Bundle[] bundles = context.getBundles();
			for (int i = 0; i < bundles.length; i++) {
				if ("org.osgi.test.cases.framework".equals(bundles[i]
						.getSymbolicName())) {
					fwtestbundle = bundles[i];
					return fwtestbundle;
				}
			}
		}

		fail("cannot find the framework test bundle");
		return null;
	}

	private class Factory implements ResolverHookFactory, ResolverHook {

		public ResolverHook begin(Collection<BundleRevision> triggers) {
			return this;
		}

		public void filterResolvable(Collection<BundleRevision> candidates) {
			for (final BundleRevision br : candidates) {
				candidates.remove(br);
			}
		}

		public void filterSingletonCollisions(BundleCapability singleton,
				Collection<BundleCapability> collisionCandidates) {
			// nop
		}

		public void filterMatches(BundleRequirement requirement,
				Collection<BundleCapability> candidates) {
			// nop
		}

		public void end() {
			// nop
		}

	}

}

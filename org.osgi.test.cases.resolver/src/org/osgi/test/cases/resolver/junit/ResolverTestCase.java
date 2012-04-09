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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.resolver.ResolverHook;
import org.osgi.framework.hooks.resolver.ResolverHookFactory;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.resource.Capability;
import org.osgi.resource.Namespace;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.resource.Wire;
import org.osgi.resource.Wiring;
import org.osgi.service.resolver.HostedCapability;
import org.osgi.service.resolver.ResolutionException;
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

	public void testDynamicImport0() throws Exception {
		final FrameworkTestResolveContext fwtrc = new FrameworkTestResolveContext(
				"dynpkgimport.tlx.jar", "dynpkgimport.tb0.jar");

		final Map<Resource, List<Wire>> result = shouldResolve(fwtrc);

		shouldBeWiredTo("dynpkgimport.tb0.jar", "dynpkgimport.tlx.jar", result);
	}

	
	public void testDynamicImport1() throws Exception {
		final FrameworkTestResolveContext fwtrc = new FrameworkTestResolveContext(
				"dynpkgimport.tlx.jar", "dynpkgimport.tb1.jar");

		final Map<Resource, List<Wire>> result = shouldResolve(fwtrc);

		shouldBeWiredTo("dynpkgimport.tb1.jar", "dynpkgimport.tlx.jar", result);
	}

	public void testFragment1() throws Exception {
		final FrameworkTestResolveContext fwtrc = new FrameworkTestResolveContext(
				"fragments.tb1a.jar");

		shouldResolve(fwtrc);
	}

	public void testFragment2() throws Exception {
		final FrameworkTestResolveContext fwtrc = new FrameworkTestResolveContext(
				"fragments.tb1b.jar");

		shouldNotResolve(fwtrc);
	}

	public void testFragment3() throws Exception {
		final FrameworkTestResolveContext fwtrc = new FrameworkTestResolveContext(
				"fragments.tb1a.jar", "fragments.tb1b.jar");

		// TODO: implement the fragment attachment in the resolve context

		shouldResolve(fwtrc);
	}

	private Map<Resource, List<Wire>> shouldResolve(
			final FrameworkTestResolveContext context) {
		final Resolver resolver = getResolverService();
		try {
			final Map<Resource, List<Wire>> result = resolver.resolve(context);
			assertNotNull(result);

			context.checkResolution(result);
			return result;
		} catch (final ResolutionException re) {
			fail(re.getMessage());
			return null;
		}
	}

	private void shouldNotResolve(final ResolveContext context) {
		final Resolver resolver = getResolverService();
		try {
			resolver.resolve(context);
		} catch (final ResolutionException re) {
			return;
		}
		fail();
	}

	private void shouldBeWiredTo(final String b1, final String b2,
			final Map<Resource, List<Wire>> resolution) {
		for (final Resource res : resolution.keySet()) {
			if (res instanceof BundleRevision
					&& b1.equals(((BundleRevision) res).getSymbolicName())) {
				final List<Wire> wires = resolution.get(res);
				for (final Wire wire : wires) {
					if (wire.getProvider() instanceof BundleRevision
							&& b2.equals(((BundleRevision) wire.getProvider())
									.getSymbolicName())) {
						return;
					}
				}
			}
		}
		fail();
	}

	private Resolver getResolverService() {
		final BundleContext context = getContext();
		final ServiceReference<Resolver> sref = context
				.getServiceReference(Resolver.class);
		assertNotNull(sref);
		return context.getService(sref);
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
		final Bundle bundle = installBundle("fragments.tb1a.jar", false);

		final BundleRevision revision = bundle.adapt(BundleRevision.class);
		final List<Capability> caps = revision.getCapabilities(null);
		assertNotNull(caps);
		System.out.println("CAPABILITIES " + caps);
		System.out.println("REQUIREMENTS " + revision.getRequirements(null));

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

	protected class FrameworkTestResolveContext extends ResolveContext {

		private final List<BundleRevision> bundles;

		protected FrameworkTestResolveContext(final String... bundleFileNames)
				throws Exception {
			bundles = new ArrayList<BundleRevision>(bundleFileNames.length);

			for (final String bundleFileName : bundleFileNames) {
				final Bundle bundle = installBundle(bundleFileName, false);
				bundles.add(bundle.adapt(BundleRevision.class));
			}
		}

		protected void checkResolution(
				final Map<Resource, List<Wire>> resolution) {
			final Map<Resource, List<Wire>> res = new HashMap<Resource, List<Wire>>(
					resolution);
			for (final BundleRevision bundle : bundles) {
				assertNotNull(res.remove(bundle));
			}
			assert (res.isEmpty());
		}

		@Override
		public List<Capability> findProviders(Requirement requirement) {
			final List<Capability> result = new ArrayList<Capability>();
			final String namespace = requirement.getNamespace();

			for (final BundleRevision bundle : bundles) {
				final List<Capability> caps = bundle.getCapabilities(namespace);
				for (final Capability cap : caps) {
					if (matches(requirement, cap)) {
						result.add(cap);
					}
				}
			}

			return result;
		}

		@Override
		public int insertHostedCapability(final List<Capability> capabilities,
				final HostedCapability hostedCapability) {
			return 0;
		}

		@Override
		public boolean isEffective(final Requirement requirement) {
			return true;
		}

		@Override
		public Map<Resource, Wiring> getWirings() {
			return null;
		}

		private boolean matches(final Requirement req, final Capability cap) {
			final String reqNamespace = req.getNamespace();
			final String capNamespace = cap.getNamespace();
			final String filter = req.getDirectives().get(
					Namespace.REQUIREMENT_FILTER_DIRECTIVE);

			try {
				return (reqNamespace.equals(capNamespace))
						&& (req.getAttributes().get(reqNamespace).equals(cap
								.getAttributes().get(reqNamespace)))
						&& (filter != null ? FrameworkUtil.createFilter(filter)
								.match(new Hashtable<String, Object>(cap
										.getAttributes())) : true);
			} catch (final InvalidSyntaxException e) {
				e.printStackTrace();
				return false;
			}
		}
	}

}

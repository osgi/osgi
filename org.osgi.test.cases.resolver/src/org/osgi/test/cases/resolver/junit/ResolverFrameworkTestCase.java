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

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
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
import org.osgi.service.resolver.ResolveContext;
import org.osgi.service.resolver.Resolver;

/**
 * This is the Resolver test case
 */
public class ResolverFrameworkTestCase extends AbstractResolverTestCase {

	private Bundle fwtestbundle;

	private FrameworkTestResolveContext rc;

	private ServiceRegistration<ResolverHookFactory> resolverHookRegistration;

	private String webserver;

	private Pattern FILTER_ASSERT_MATCHER = Pattern
			.compile("\\(([^&\\!|=<>~\\(\\)]*)[=|<=|>=|~=]");

	protected void setUp() throws Exception {
		resolverHookRegistration = getContext().registerService(
				ResolverHookFactory.class, new Factory(), null);
	}

	protected void tearDown() throws Exception {
		resolverHookRegistration.unregister();
		resolverHookRegistration = null;
		if (rc != null) {
			rc.cleanup();
			rc = null;
		}
	}

	public void testDynamicImport0() throws Exception {
		final FrameworkTestResolveContext fwtrc = new FrameworkTestResolveContext(
				"classloading.tb17a.jar");

		shouldResolve(fwtrc);
	}

	public void testDynamicImport1() throws Exception {
		rc = new FrameworkTestResolveContext("dynpkgimport.tlx.jar",
				"dynpkgimport.tb0.jar");

		final Map<Resource, List<Wire>> result = shouldResolve(rc);

		rc.shouldBeWiredTo("dynpkgimport.tb0.jar", "dynpkgimport.tlx.jar",
				result);
	}

	public void testDynamicImport2() throws Exception {
		rc = new FrameworkTestResolveContext("dynpkgimport.tlx.jar",
				"dynpkgimport.tb1.jar");

		final Map<Resource, List<Wire>> result = shouldResolve(rc);

		rc.shouldBeWiredTo("dynpkgimport.tb1.jar", "dynpkgimport.tlx.jar",
				result);
	}

	public void testDynamicImport3() throws Exception {
		rc = new FrameworkTestResolveContext("classloading.tb1.jar",
				"classloading.tb8a.jar", "classloading.tb17b.jar");

		final Map<Resource, List<Wire>> result = shouldResolve(rc);

		rc.shouldBeWiredTo("dynpkgimport.tb17b.jar", "dynpkgimport.tb1.jar",
				result);
		rc.shouldBeWiredTo("dynpkgimport.tb17b.jar", "dynpkgimport.tb8a.jar",
				result);
	}

	public void testDynamicImport4() throws Exception {
		rc = new FrameworkTestResolveContext("classloading.tb8a.jar",
				"classloading.tb8b.jar", "classloading.tb17c.jar");

		final Map<Resource, List<Wire>> result = shouldResolve(rc);

		rc.shouldBeWiredTo("dynpkgimport.tb17c.jar", "classloading.tb8a.jar",
				result);
		rc.shouldBeWiredTo("dynpkgimport.tb17b.jar", "classloading.tb8b.jar",
				result);
	}

	public void testDynamicImport5() throws Exception {
		rc = new FrameworkTestResolveContext("classloading.tb1.jar",
				"classloading.tb17i.jar");

		final Map<Resource, List<Wire>> result = shouldResolve(rc);

		rc.shouldBeWiredTo("classloading.tb17i.jar", "classloading.tb1.jar",
				result);
	}

	public void testDynamicImport6() throws Exception {
		rc = new FrameworkTestResolveContext("classloading.tb1.jar",
				"classloading.tb17i.jar", "classloading.tb17j.jar");

		final Map<Resource, List<Wire>> result = shouldResolve(rc);
		rc.shouldBeWiredTo("classloading.tb17i.jar", "classloading.tb1.jar",
				result);
		rc.shouldBeWiredTo("classloading.tb17j.jar", "classloading.tb17i.jar",
				result);
	}

	public void testDynamicImport7() throws Exception {
		rc = new FrameworkTestResolveContext("classloading.tb1.jar",
				"classloading.tb17d.jar");

		shouldResolve(rc);
	}

	public void testDynamicImport8() throws Exception {
		try {
			rc = new FrameworkTestResolveContext("classloading.tb1.jar",
					"classloading.tb17d.jar", "classloading.tb17e.jar");
			shouldNotResolve(rc);
		} catch (final Exception e) {
			// the framework might already catch this one.
			return;
		}
	}

	public void testDynamicImport9() throws Exception {
		rc = new FrameworkTestResolveContext("classloading.tb13g.jar",
				"classloading.tb17g.jar");

		shouldResolve(rc);
	}

	public void testRequireBundle0() throws Exception {
		rc = new FrameworkTestResolveContext("classloading.tb16c.jar");

		shouldNotResolve(rc);
	}

	public void testRequireBundle1() throws Exception {
		rc = new FrameworkTestResolveContext("classloading.tb16a.jar",
				"classloading.tb16b.jar", "classloading.tb16c.jar");

		final Map<Resource, List<Wire>> result = shouldResolve(rc);
		rc.shouldBeWiredTo("classloading.tb16c.jar", "classloading.tb16a.jar",
				result);
		rc.shouldBeWiredTo("classloading.tb16a.jar", "classloading.tb16b.jar",
				result);
		rc.shouldBeWiredTo("classloading.tb16a.jar", "classloading.tb16c.jar",
				result);
	}

	public void testRequireBundle2() throws Exception {
		rc = new FrameworkTestResolveContext("classloading.tb16b.jar",
				"classloading.tb16k.jar", "classloading.tb16i.jar");

		shouldResolve(rc);
	}

	// TODO: test uses
	// TODO: find out why the fragment attachment is not exercised
	
	public void testFragment1() throws Exception {
		rc = new FrameworkTestResolveContext("fragments.tb1a.jar");

		shouldResolve(rc);
	}

	public void testFragment2() throws Exception {
		rc = new FrameworkTestResolveContext("fragments.tb1b.jar");

		shouldNotResolve(rc);
	}

	public void testFragment3() throws Exception {
		rc = new FrameworkTestResolveContext("fragments.tb1a.jar",
				"fragments.tb1b.jar");
		shouldResolve(rc);
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

	protected class FrameworkTestResolveContext extends TestResolveContext {

		private final Map<String, BundleRevision> resourceMap;

		private final BundleRevision systemBundle;

		protected FrameworkTestResolveContext(final String... bundleFileNames)
				throws Exception {
			resourceMap = new HashMap<String, BundleRevision>(
					bundleFileNames.length);

			for (final String bundleFileName : bundleFileNames) {
				final Bundle bundle = installBundle(bundleFileName, false);
				resourceMap.put(bundleFileName,
						bundle.adapt(BundleRevision.class));
			}

			systemBundle = getContext().getBundle(0)
					.adapt(BundleRevision.class);

			mandatoryResources.addAll(resourceMap.values());
			allResources.addAll(mandatoryResources);
			allResources.add(systemBundle);
		}

		protected void cleanup() {
			for (final BundleRevision bundle : resourceMap.values()) {
				try {
					bundle.getBundle().uninstall();
				} catch (final BundleException e) {
					e.printStackTrace();
				}
			}
		}

		protected void shouldBeWiredTo(final String b1, final String b2,
				final Map<Resource, List<Wire>> resolution) {
			final BundleRevision bundle1 = resourceMap.get(b1);
			assertNotNull(bundle1);
			final BundleRevision bundle2 = resourceMap.get(b2);
			assertNotNull(bundle2);

			final List<Wire> wires = resolution.get(bundle1);
			assertNotNull(wires);

			for (final Wire wire : wires) {
				if (wire.getProvider().equals(bundle2)) {
					return;
				}
			}

			fail();
		}

		@Override
		protected boolean matches(final Requirement req, final Capability cap) {
			final String capNamespace = cap.getNamespace();
			final String filter = req.getDirectives().get(
					Namespace.REQUIREMENT_FILTER_DIRECTIVE);

			if (!super.matches(req, cap)) {
				return false;
			}

			if (!capNamespace.startsWith("osgi.wiring.")) {
				return true;
			}

			final String mandatory = cap.getDirectives().get(
					Namespace.RESOLUTION_MANDATORY);
			if (mandatory == null) {
				return true;
			}

			final List<String> mandatoryAttributes = Arrays.asList(mandatory
					.toLowerCase().split("\\*s,\\*s"));
			final Matcher matcher = FILTER_ASSERT_MATCHER
					.matcher(filter == null ? "" : filter);
			while (matcher.find()) {
				mandatoryAttributes.remove(matcher.group(1));
			}
			return mandatoryAttributes.isEmpty();
		}
	}

}

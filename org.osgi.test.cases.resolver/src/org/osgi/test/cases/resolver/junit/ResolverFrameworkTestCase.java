/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.resolver.junit;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.osgi.framework.Bundle;
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

/**
 * Testing the resolver with OSGi bundle from the framework test cases
 * 
 * @author <a href="mailto:rellermeyer@us.ibm.com">Jan S. Rellermeyer</a>
 */
public class ResolverFrameworkTestCase extends AbstractResolverTestCase {

	private FrameworkTestResolveContext rc;

	private ServiceRegistration<ResolverHookFactory> resolverHookRegistration;

	final Pattern										FILTER_ASSERT_MATCHER	= Pattern
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
		rc = new FrameworkTestResolveContext(
				true, "dynpkgimport.tlx.jar",
				"dynpkgimport.tb0.jar");

		final Map<Resource, List<Wire>> result = shouldResolve(rc);

		rc.shouldBeWiredTo("dynpkgimport.tb0.jar", "dynpkgimport.tlx.jar",
				result);
	}

	public void testDynamicImport7() throws Exception {
		rc = new FrameworkTestResolveContext("classloading.tb1.jar",
				"classloading.tb17d.jar");

		shouldResolve(rc);
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

		final Map<Resource, List<Wire>> result = shouldResolve(rc);
		rc.shouldBeWiredTo("fragments.tb1b.jar", "fragments.tb1a.jar", result);
	}

	public void testFragment4() throws Exception {
		rc = new FrameworkTestResolveContext("fragments.tb1a.jar",
				"fragments.tb1b.jar", "fragments.tb1d.jar");

		shouldNotResolve(rc);
	}

	public void testFragment5() throws Exception {
		rc = new FrameworkTestResolveContext("fragments.tb1a.jar",
				"fragments.tb1b.jar", "fragments.tb1g.jar");

		final Map<Resource, List<Wire>> result = shouldResolve(rc);
		rc.shouldBeWiredTo("fragments.tb1b.jar", "fragments.tb1a.jar", result);
		rc.shouldBeWiredTo("fragments.tb1g.jar", "fragments.tb1a.jar", result);
	}

	public void testFragment6() throws Exception {
		rc = new FrameworkTestResolveContext("fragments.tb3a.jar",
				"fragments.tb3d.jar");

		final Map<Resource, List<Wire>> result = shouldResolve(rc);
		rc.shouldBeWiredTo("fragments.tb3d.jar", "fragments.tb3a.jar", result);
	}

	public void testFragment7() throws Exception {
		rc = new FrameworkTestResolveContext("fragments.tb3e.jar",
				"fragments.tb3d.jar");

		shouldNotResolve(rc);
	}

	public void testFragment8() throws Exception {
		rc = new FrameworkTestResolveContext("fragments.tb3f.jar",
				"fragments.tb3d.jar");

		final Map<Resource, List<Wire>> result = shouldResolve(rc);
		rc.shouldBeWiredTo("fragments.tb3d.jar", "fragments.tb3f.jar", result);
	}

	public void testFragment9() throws Exception {
		rc = new FrameworkTestResolveContext("fragments.tb7e.jar",
				"fragments.tb7c.jar", "fragments.tb7h.jar");

		final Map<Resource, List<Wire>> result = shouldResolve(rc);
		rc.shouldBeWiredTo("fragments.tb7e.jar", "fragments.tb7c.jar", result);
		rc.shouldBeWiredTo("fragments.tb7h.jar", "fragments.tb7c.jar", result);
		rc.checkInsertHostedCapabilityCalled();
	}

	public void testReqCap0() throws Exception {
		rc = new FrameworkTestResolveContext("resolver.tb1.v100.jar",
				"resolver.tb5.jar");

		shouldNotResolve(rc);
	}

	public void testReqCap1() throws Exception {
		rc = new FrameworkTestResolveContext("resolver.tb1.v110.jar",
				"resolver.tb5.jar");

		final Map<Resource, List<Wire>> result = shouldResolve(rc);
		rc.shouldBeWiredTo("resolver.tb5.jar", "resolver.tb1.v110.jar", result);
	}

	public void testReqCap2() throws Exception {
		rc = new FrameworkTestResolveContext("resolver.tb1.v120.jar",
				"resolver.tb5.jar");

		final Map<Resource, List<Wire>> result = shouldResolve(rc);
		rc.shouldBeWiredTo("resolver.tb5.jar", "resolver.tb1.v120.jar", result);
	}

	public void testUses0() throws Exception {
		rc = new FrameworkTestResolveContext("classloading.tb9a.jar",
				"classloading.tb8b.jar", "classloading.tb13b.jar",
				"classloading.tb13c.jar");
		final Map<Resource, List<Wire>> result = shouldResolve(rc);
		rc.shouldBeWiredTo("classloading.tb13b.jar", "classloading.tb8b.jar",
				result);
		rc.shouldBeWiredTo("classloading.tb13c.jar", "classloading.tb8b.jar",
				result);
		rc.shouldBeWiredTo("classloading.tb13c.jar", "classloading.tb13b.jar",
				result);
	}

	public void testUses1() throws Exception {
		rc = new FrameworkTestResolveContext("requirebundleB.jar", "requirebundleC.jar",
				"requirebundleD.jar", "requirebundleE.jar");
		final Map<Resource, List<Wire>> result = shouldResolve(rc);
		rc.shouldBeWiredTo("requirebundleD.jar", "requirebundleC.jar",
				result);
		rc.shouldBeWiredTo("requirebundleE.jar", "requirebundleD.jar",
				result);
		rc.shouldBeWiredTo("requirebundleE.jar", "requirebundleC.jar",
				result);
	}

	class Factory implements ResolverHookFactory, ResolverHook {

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
			this(false, bundleFileNames);
		}

		protected FrameworkTestResolveContext(final boolean withDummy,
				final String... bundleFileNames) throws Exception {
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

			if (withDummy) {
				final Bundle dummyBundle = installBundle("tb1.jar", false);
				allResources.add(dummyBundle.adapt(BundleRevision.class));
			}
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

		protected BundleRevision getBundle(final String name) {
			return resourceMap.get(name);
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
		public boolean isEffective(final Requirement requirement) {
			final String effective = requirement.getDirectives().get(
					Namespace.REQUIREMENT_EFFECTIVE_DIRECTIVE);
			return effective == null
					|| effective.equals(Namespace.EFFECTIVE_RESOLVE);
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

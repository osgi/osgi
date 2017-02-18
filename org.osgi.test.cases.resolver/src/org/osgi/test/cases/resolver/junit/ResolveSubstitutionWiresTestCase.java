/*
 * Copyright (c) OSGi Alliance (2012, 2016). All Rights Reserved.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.namespace.BundleNamespace;
import org.osgi.framework.namespace.HostNamespace;
import org.osgi.framework.namespace.IdentityNamespace;
import org.osgi.framework.namespace.PackageNamespace;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.resource.Wiring;
import org.osgi.test.cases.resolver.junit.AbstractResolverTestCase.TestResource.TestWiring;

/**
 * Test that substitutions are properly handled for require-bundle with split
 * packages and that the ResolveContext.getSubstitutionWires method is called
 * properly
 */
public class ResolveSubstitutionWiresTestCase extends AbstractResolverTestCase {

	public static class SubstitutionResolveContext extends TestResolveContext {
		final Map<Requirement,List<Capability>>	candidates;
		final List<Wiring>						checkForSubstitutions	= new ArrayList<Wiring>();

		public SubstitutionResolveContext(
				Map<Requirement,List<Capability>> candidates) {
			this.candidates = candidates;
		}

		@Override
		public List<Capability> findProviders(Requirement requirement) {
			List<Capability> result = candidates.get(requirement);
			return result == null ? new ArrayList<>() : result;
		}
	}

	public void testSubstitutionWiringsWithSubstitute() {
		SubstitutionResolveContext rc = createResolveContext(true, true);
		shouldResolve(rc);
		rc.checkGetSubstitutionWires(
				rc.checkForSubstitutions.toArray(new Wiring[0]));
	}

	public void testSubstitutionWiringsWithNoSubstitute() {
		SubstitutionResolveContext rc = createResolveContext(false, true);
		shouldResolve(rc);
		rc.checkGetSubstitutionWires(
				rc.checkForSubstitutions.toArray(new Wiring[0]));
	}

	public void testSubstitutionNoWiringsWithSubstitute() {
		SubstitutionResolveContext rc = createResolveContext(true, false);
		shouldResolve(rc);
		rc.checkGetSubstitutionWires(
				rc.checkForSubstitutions.toArray(new Wiring[0]));
	}

	public void testSubstitutionNoWiringsWithNoSubstitute() {
		SubstitutionResolveContext rc = createResolveContext(false, false);
		shouldResolve(rc);
		rc.checkGetSubstitutionWires(
				rc.checkForSubstitutions.toArray(new Wiring[0]));
	}

	public SubstitutionResolveContext createResolveContext(
			boolean doSubstitution, boolean existingWires) {
		// core bundle
		// exports pkg1 -> allows substitution
		TestResource core = createResource("core");
		Capability core_pkg1 = core
				.addCapability(PackageNamespace.PACKAGE_NAMESPACE, Collections
				.singletonMap(PackageNamespace.PACKAGE_NAMESPACE, "pkg1"));
		Requirement core_reqPkg1 = core.addRequirement(
				PackageNamespace.PACKAGE_NAMESPACE,
				"(" + PackageNamespace.PACKAGE_NAMESPACE + "=pkg1)");

		// misc bundle
		// exports pkg1 -> no substitution
		// requires core -> splitting pkg1
		TestResource misc = createResource("misc");
		misc.addCapability(PackageNamespace.PACKAGE_NAMESPACE, Collections
				.singletonMap(PackageNamespace.PACKAGE_NAMESPACE, "pkg1"));
		Requirement misc_reqCore = misc.addRequirement(
				BundleNamespace.BUNDLE_NAMESPACE,
				"(" + BundleNamespace.BUNDLE_NAMESPACE + "=core)");

		// importsPkg1 bundle
		// exports pkg2 -> uses pkg1
		// imports pkg1 -> only wires to core
		TestResource importsPkg1 = createResource("importsPkg1");
		Capability pkg2 = importsPkg1.addCapability(
				PackageNamespace.PACKAGE_NAMESPACE,
				Collections.<String, Object> singletonMap(
						PackageNamespace.PACKAGE_NAMESPACE, "pkg2"));
		pkg2.getDirectives().put(PackageNamespace.CAPABILITY_USES_DIRECTIVE,
				"pkg1");
		Requirement importsPkg1_reqPkg1 = importsPkg1.addRequirement(
				PackageNamespace.PACKAGE_NAMESPACE,
				"(" + PackageNamespace.PACKAGE_NAMESPACE + "=pkg1)");

		// requiresMisc bundle
		// imports pkg2
		// requires misc -> should access split pkg2 from misc and core (or
		// core's sub).
		TestResource requiresMisc = createResource("requiresMisc");
		Requirement requiresMisc_reqPkg2 = requiresMisc.addRequirement(
				PackageNamespace.PACKAGE_NAMESPACE,
				"(" + PackageNamespace.PACKAGE_NAMESPACE + "=pkg2)");
		Requirement requiresMisc_reqMisc = requiresMisc.addRequirement(
				BundleNamespace.BUNDLE_NAMESPACE,
				"(" + BundleNamespace.BUNDLE_NAMESPACE + "=misc)");

		TestResource substitutesCore = createResource("substitutesCore");
		Capability substitutesCore_pkg1 = substitutesCore
				.addCapability(PackageNamespace.PACKAGE_NAMESPACE,
						Collections.singletonMap(
								PackageNamespace.PACKAGE_NAMESPACE, "pkg1"));

		Map<Requirement,List<Capability>> candMap = new HashMap<Requirement,List<Capability>>();
		candMap.put(core_reqPkg1, Collections.singletonList(
				doSubstitution ? substitutesCore_pkg1 : core_pkg1));
		candMap.put(misc_reqCore, Collections.singletonList(
				core.getCapabilities(BundleNamespace.BUNDLE_NAMESPACE).get(0)));
		candMap.put(importsPkg1_reqPkg1, Collections.singletonList(
				doSubstitution ? substitutesCore_pkg1 : core_pkg1));
		candMap.put(requiresMisc_reqPkg2, Collections.singletonList(pkg2));
		candMap.put(requiresMisc_reqMisc, Collections.singletonList(
				misc.getCapabilities(BundleNamespace.BUNDLE_NAMESPACE).get(0)));

		Map<Resource,Wiring> wirings = new HashMap<>();
		if (existingWires) {
			TestWiring coreWiring = core.new TestWiring();
			TestWiring miscWiring = misc.new TestWiring();
			TestWiring substitutesCoreWiring = substitutesCore.new TestWiring();
			wirings.put(core, coreWiring);
			wirings.put(misc, miscWiring);
			wirings.put(substitutesCore, substitutesCoreWiring);

			TestWire miscToCore = new TestWire(misc, misc_reqCore, core,
					core.getCapabilities(BundleNamespace.BUNDLE_NAMESPACE)
							.get(0));
			coreWiring.providedWires.add(miscToCore);
			miscWiring.requiredWires.add(miscToCore);

			if (doSubstitution) {
				TestWire coreToSubstitute = new TestWire(core, core_reqPkg1,
						substitutesCore, substitutesCore_pkg1);
				substitutesCoreWiring.providedWires.add(coreToSubstitute);
				coreWiring.requiredWires.add(coreToSubstitute);
				coreWiring.substituted.add(core_pkg1);
			}
		}

		SubstitutionResolveContext rc = new SubstitutionResolveContext(candMap);
		if (existingWires) {
			rc.checkForSubstitutions.add(wirings.get(core));
		}
		rc.wirings.putAll(wirings);
		rc.mandatoryResources.add(requiresMisc);
		return rc;
	}

	private static TestResource createResource(String name) {
		return createResource(name, IdentityNamespace.TYPE_BUNDLE);
	}

	private static TestResource createResource(String name, String type) {
		TestResource resource = new TestResource();

		Map<String,Object> identityAttrs = new HashMap<>();
		identityAttrs.put(IdentityNamespace.IDENTITY_NAMESPACE, name);
		identityAttrs.put(IdentityNamespace.CAPABILITY_TYPE_ATTRIBUTE,
				type == null ? IdentityNamespace.TYPE_BUNDLE : type);
		resource.addCapability(resource.new TestCapability(
				IdentityNamespace.IDENTITY_NAMESPACE, identityAttrs));

		if (type == null || IdentityNamespace.TYPE_BUNDLE.equals(type)) {
			resource.addCapability(resource.new TestCapability(
					BundleNamespace.BUNDLE_NAMESPACE, Collections.singletonMap(
							BundleNamespace.BUNDLE_NAMESPACE, name)));
			resource.addCapability(resource.new TestCapability(
					HostNamespace.HOST_NAMESPACE, Collections
							.singletonMap(HostNamespace.HOST_NAMESPACE, name)));
		}

		return resource;
	}
}

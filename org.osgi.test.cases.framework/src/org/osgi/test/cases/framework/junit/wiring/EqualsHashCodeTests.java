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
package org.osgi.test.cases.framework.junit.wiring;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.namespace.PackageNamespace;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.test.cases.framework.junit.wiring.EqualsHashCodeTests.CollisionHook.Collision;

public class EqualsHashCodeTests extends WiringTest {
	static final class CollisionHook implements org.osgi.framework.hooks.bundle.CollisionHook {
		static final class Collision {
			private final List<Bundle> collisionCandidates;
			private final int operationType;
			private final Bundle target;
			
			public Collision(int operationType, Bundle target, Collection<Bundle> collisionCandidates) {
				this.operationType = operationType;
				this.target = target;
				this.collisionCandidates = new ArrayList<Bundle>(collisionCandidates);
			}
			
			public List<Bundle> getCollisionCandidates() {
				return collisionCandidates;
			}
			
			public int getOperationType() {
				return operationType;
			}
			
			public Bundle getTarget() {
				return target;
			}
		}
		
		private final Collection<Collision> collisions = new ArrayList<Collision>();
		
		public void filterCollisions(int operationType, Bundle target, Collection<Bundle> collisionCandidates) {
			collisions.add(new Collision(operationType, target, collisionCandidates));
			collisionCandidates.clear();
		}
		
		public Collection<Collision> getCollisions() {
			return collisions;
		}
	}
	
	private CollisionHook collisionHook;
	private ServiceRegistration<org.osgi.framework.hooks.bundle.CollisionHook> registration;
	
	public void testEqualsHashCode() throws Exception {
		Bundle tb1 = install("wiring.tb1.jar.a", "wiring.tb1.jar");
		try {
			Bundle tb2 = install("wiring.tb1.jar.b", "wiring.tb1.jar");
			try {
				assertTrue("Bundles should resolve", frameworkWiring.resolveBundles(Arrays.asList(tb1, tb2)));
				assertEquals("Wrong number of calls to collision hook", 1, collisionHook.getCollisions().size());
				Collision collision = collisionHook.getCollisions().iterator().next();
				assertEquals("Wrong target", getContext().getBundle(), collision.getTarget());
				assertEquals("Wrong operation type", org.osgi.framework.hooks.bundle.CollisionHook.INSTALLING, collision.getOperationType());
				assertEquals("Wrong number of collision candidates", 1, collision.getCollisionCandidates().size());
				BundleRevision tb1r = collision.getCollisionCandidates().get(0).adapt(BundleRevision.class);
				assertEquals("Wrong collision candidate", tb1.adapt(BundleRevision.class), tb1r);
				BundleRevision tb2r = tb2.adapt(BundleRevision.class);
				assertFalse("Revisions should not be equal", tb1r.equals(tb2r));
				assertFalse("Hashcodes should not be equal", tb1r.hashCode() == tb2r.hashCode());
				assertCapabilitiesNotEqual(tb1r, tb2r);
				assertRequirementsNotEqual(tb1r, tb2r);
				assertWiresNotEqual(tb1r, tb2r);
			}
			finally {
				uninstallSilently(tb2);
			}
		}
		finally {
			uninstallSilently(tb1);
		}
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		String bsnversion = getContext().getProperty(Constants.FRAMEWORK_BSNVERSION);
		assertTrue(bsnversion == null || bsnversion.equals(Constants.FRAMEWORK_BSNVERSION_MANAGED));
		collisionHook = new CollisionHook();
		registration = getContext().registerService(org.osgi.framework.hooks.bundle.CollisionHook.class, collisionHook, null);
	}
	
	protected void tearDown() throws Exception {
		if (registration != null)
			registration.unregister();
		super.tearDown();
	}
	
	private void assertCapabilitiesNotEqual(BundleRevision tb1, BundleRevision tb2) {
		List<Capability> tb1Capabilities = tb1.getCapabilities(PackageNamespace.PACKAGE_NAMESPACE);
		assertEquals("Wrong number of capabilities", 3, tb1Capabilities.size());
		Capability tb1Capability = tb1Capabilities.get(0);
		assertEquals("Wrong capability", "org.osgi.test.cases.framework.wiring.tb1a", tb1Capability.getAttributes().get(PackageNamespace.PACKAGE_NAMESPACE));
		List<Capability> tb2Capabilities = tb2.getCapabilities(PackageNamespace.PACKAGE_NAMESPACE);
		assertEquals("Wrong number of capabilities", 3, tb1Capabilities.size());
		Capability tb2Capability = tb2Capabilities.get(0);
		assertEquals("Wrong capability", "org.osgi.test.cases.framework.wiring.tb1a", tb2Capability.getAttributes().get(PackageNamespace.PACKAGE_NAMESPACE));
		assertFalse("Capabilities should not be equal", tb1Capability.equals(tb2Capability));
		assertFalse("Hashcodes should not be equal", tb1Capability.hashCode() == tb2Capability.hashCode());
	}
	
	private void assertRequirementsNotEqual(BundleRevision tb1, BundleRevision tb2) {
		List<Requirement> tb1Requirements = tb1.getRequirements(PackageNamespace.PACKAGE_NAMESPACE);
		assertEquals("Wrong number of requirements", 1, tb1Requirements.size());
		Requirement tb1Requirement = tb1Requirements.get(0);
		assertTrue("Wrong requirement", tb1Requirement.getDirectives().get(Constants.FILTER_DIRECTIVE).toString().indexOf(PackageNamespace.PACKAGE_NAMESPACE + "=org.osgi.framework") > 0);
		List<Requirement> tb2Requirements = tb2.getRequirements(PackageNamespace.PACKAGE_NAMESPACE);
		assertEquals("Wrong number of requirements", 1, tb1Requirements.size());
		Requirement tb2Requirement = tb2Requirements.get(0);
		assertTrue("Wrong requirement", tb2Requirement.getDirectives().get(Constants.FILTER_DIRECTIVE).toString().indexOf(PackageNamespace.PACKAGE_NAMESPACE + "=org.osgi.framework") > 0);
		assertFalse("Requirements should not be equal", tb1Requirement.equals(tb2Requirement));
		assertFalse("Hashcodes should not be equal", tb1Requirement.hashCode() == tb2Requirement.hashCode());
	}
	
	private void assertWiresNotEqual(BundleRevision tb1, BundleRevision tb2) {
		List<BundleWire> tb1Wires = tb1.getWiring().getRequiredWires(PackageNamespace.PACKAGE_NAMESPACE);
		assertEquals("Wrong number of wires", 1, tb1Wires.size());
		BundleWire tb1Wire = tb1Wires.get(0);
		assertTrue("Wrong wire", tb1Wire.getRequirement().getDirectives().get(Constants.FILTER_DIRECTIVE).toString().indexOf(PackageNamespace.PACKAGE_NAMESPACE + "=org.osgi.framework") > 0);
		List<BundleWire> tb2Wires = tb2.getWiring().getRequiredWires(PackageNamespace.PACKAGE_NAMESPACE);
		assertEquals("Wrong number of wires", 1, tb2Wires.size());
		BundleWire tb2Wire = tb2Wires.get(0);
		assertTrue("Wrong wire", tb2Wire.getRequirement().getDirectives().get(Constants.FILTER_DIRECTIVE).toString().indexOf(PackageNamespace.PACKAGE_NAMESPACE + "=org.osgi.framework") > 0);
		assertFalse("Wires should not be equal", tb1Wire.equals(tb2Wire));
		assertFalse("Hashcodes should not be equal", tb1Wire.hashCode() == tb2Wire.hashCode());
	}
	
	private Bundle install(String location, String bundle) throws Exception {
		URL entry = getContext().getBundle().getEntry(bundle);
		return getContext().installBundle(location, entry.openStream());
	}
}

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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Version;
import org.osgi.framework.namespace.HostNamespace;
import org.osgi.framework.namespace.IdentityNamespace;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.resource.Wire;
import org.osgi.resource.Wiring;
import org.osgi.test.cases.resolver.junit.AbstractResolverTestCase.TestResource.TestWiring;

/**
 * Testing the findRelatedResources method on ResolveContext
 */
public class ResolveRelatedTestCase extends AbstractResolverTestCase {

	/*
	 * Simple test that pulls in on additional non-fragment resource. Should
	 * result in two resources resolved with no wired.
	 */
	public void testRelatedNonFragment() {
		TestResource rootResource = new TestResource();
		TestResource rootRelatedResource = new TestResource();
		TestResolveContext context = new TestResolveContext(
				Collections.<Resource> singleton(rootResource), null, null);
		context.relatedResources.put(rootResource,
				Collections.<Resource> singleton(rootRelatedResource));
		Map<Resource,List<Wire>> result = shouldResolve(context);
		assertEquals("Wrong number of resolved resoruces.", 2, result.size());
		context.checkEmptyWires(result, rootResource);
		context.checkEmptyWires(result, rootRelatedResource);
	}

	/*
	 * Tests that if a related resource is unresolvable then the resource that
	 * pulls the related resource in is still able to resolve
	 */
	public void testRelatedNonFragmentUnresolvable() {
		TestResource rootResource = new TestResource();
		TestResource rootRelatedResource = new TestResource();
		rootRelatedResource.addRequirement("unresolvable",
				"(unresolvable=true)");
		TestResolveContext context = new TestResolveContext(
				Collections.<Resource> singleton(rootResource), null, null);
		context.relatedResources.put(rootResource,
				Collections.<Resource> singleton(rootRelatedResource));
		Map<Resource,List<Wire>> result = shouldResolve(context);
		assertEquals("Wrong number of resolved resoruces.", 1, result.size());
		context.checkEmptyWires(result, rootResource);
	}

	/*
	 * Tests that if a related resource is resolvable and requires additional
	 * resources to resolve then these resources also get resolved.
	 */
	public void testRelatedNonFragmentResolvable() {
		TestResource rootResource = new TestResource();
		TestResource rootRelatedResource = new TestResource();
		Requirement resolvableReq = rootRelatedResource
				.addRequirement("resolvable", "(resolvable=true)");

		TestResource providerResource = new TestResource();
		Capability resolvableCap = providerResource.addCapability("resolvable",
				"resolvable=true");

		TestResolveContext context = new TestResolveContext(
				Collections.<Resource> singleton(rootResource), null,
				Collections.<Resource> singleton(providerResource));
		context.relatedResources.put(rootResource,
				Collections.<Resource> singleton(rootRelatedResource));

		Map<Resource,List<Wire>> result = shouldResolve(context);
		assertEquals("Wrong number of resolved resoruces.", 3, result.size());
		context.checkEmptyWires(result, rootResource);
		context.checkEmptyWires(result, providerResource);
		context.checkWires(result, new TestWire(rootRelatedResource,
				resolvableReq, providerResource, resolvableCap));
	}

	/*
	 * Tests that if a related non-fragment resource is pulled in but already
	 * resolved then the non-fragment resource has no impact on the end
	 * resolution result
	 */
	public void testRelatedNonFragmentAlreadyResolved() {
		TestResource rootResource = new TestResource();
		TestResource rootRelatedResource = new TestResource();
		Map<Resource,Wiring> wirings = new HashMap<>();
		wirings.put(rootRelatedResource, rootRelatedResource.new TestWiring());
		TestResolveContext context = new TestResolveContext(
				Collections.<Resource> singleton(rootResource), null, null,
				wirings);
		context.relatedResources.put(rootResource,
				Collections.<Resource> singleton(rootRelatedResource));
		Map<Resource,List<Wire>> result = shouldResolve(context);
		assertEquals("Wrong number of resolved resoruces.", 1, result.size());
		context.checkEmptyWires(result, rootResource);
	}

	/*
	 * Simple test for pulling in a related fragment resource
	 */
	public void testRelatedFragment() {
		TestResource rootResource = new TestResource();
		rootResource.addIdentity("host", null, null);

		TestResource rootResourceFrag = new TestResource();
		rootResource.addIdentity("host.frag", null,
				IdentityNamespace.TYPE_FRAGMENT);
		Requirement hostReq = rootResourceFrag.addRequirement(
				HostNamespace.HOST_NAMESPACE,
				"(" + HostNamespace.HOST_NAMESPACE + "=host)");

		TestResolveContext context = new TestResolveContext(
				Collections.<Resource> singleton(rootResource), null, null);
		context.relatedResources.put(rootResource,
				Collections.<Resource> singleton(rootResourceFrag));
		Map<Resource,List<Wire>> result = shouldResolve(context);
		assertEquals("Wrong number of resolved resoruces.", 2, result.size());
		context.checkEmptyWires(result, rootResource);
		context.checkWires(result,
				new TestWire(rootResourceFrag, hostReq, rootResource,
						rootResource
								.getCapabilities(HostNamespace.HOST_NAMESPACE)
								.get(0)));

	}

	/*
	 * Tests that if a related resource fragment is unresolvable then the
	 * resource host that pulls the related resource fragment in is still able
	 * to resolve
	 */
	public void testRelatedFragmentUnresolvable() {
		TestResource rootResource = new TestResource();
		rootResource.addIdentity("host", null, null);
		TestResource rootResourceFrag = new TestResource();
		rootResourceFrag.addIdentity("host.frag", null,
				IdentityNamespace.TYPE_FRAGMENT);
		rootResourceFrag.addRequirement(
				HostNamespace.HOST_NAMESPACE,
				"(" + HostNamespace.HOST_NAMESPACE + "=host)");
		rootResourceFrag.addRequirement("unresolvable", "(unresolvable=true)");
		TestResolveContext context = new TestResolveContext(
				Collections.<Resource> singleton(rootResource), null, null);
		context.relatedResources.put(rootResource,
				Collections.<Resource> singleton(rootResourceFrag));
		Map<Resource,List<Wire>> result = shouldResolve(context);
		assertEquals("Wrong number of resolved resoruces.", 1, result.size());
		context.checkEmptyWires(result, rootResource);
	}

	/*
	 * Tests that if a related resource fragment is resolvable and requires
	 * additional resources to resolve then these resources also get resolved.
	 */
	public void testRelatedFragmentResolvable() {
		TestResource rootResource = new TestResource();
		rootResource.addIdentity("host", null, null);

		TestResource rootResourceFrag = new TestResource();
		rootResourceFrag.addIdentity("host.frag", null,
				IdentityNamespace.TYPE_FRAGMENT);
		Requirement hostReq = rootResourceFrag.addRequirement(
				HostNamespace.HOST_NAMESPACE,
				"(" + HostNamespace.HOST_NAMESPACE + "=host)");
		Requirement resolvableReq = rootResourceFrag
				.addRequirement("resolvable", "(resolvable=true)");

		TestResource providerResource = new TestResource();
		Capability resolvableCap = providerResource.addCapability("resolvable",
				"resolvable=true");

		TestResolveContext context = new TestResolveContext(
				Collections.<Resource> singleton(rootResource), null,
				Collections.<Resource> singleton(providerResource));
		context.relatedResources.put(rootResource,
				Collections.<Resource> singleton(rootResourceFrag));

		Map<Resource,List<Wire>> result = shouldResolve(context);
		assertEquals("Wrong number of resolved resoruces.", 3, result.size());
		context.checkWires(result, new TestWire(rootResource, resolvableReq,
				providerResource, resolvableCap));
		context.checkEmptyWires(result, providerResource);
		context.checkWires(result,
				new TestWire(rootResourceFrag, hostReq, rootResource,
						rootResource
								.getCapabilities(HostNamespace.HOST_NAMESPACE)
								.get(0)));
	}

	/*
	 * Tests that if a related fragment resource is pulled in but already
	 * resolved then the fragment is allowed to establish a new host wire to an
	 * unresolved host
	 */
	public void testRelatedFragmentAlreadyResolved() {
		TestResource rootResource10 = new TestResource();
		rootResource10.addIdentity("host", new Version(1, 0, 0), null);

		TestResource rootResource11 = new TestResource();
		rootResource11.addIdentity("host", new Version(1, 1, 0), null);

		TestResource rootResourceFrag = new TestResource();
		rootResourceFrag.addIdentity("host.frag", null,
				IdentityNamespace.TYPE_FRAGMENT);
		Requirement hostReq = rootResourceFrag.addRequirement(
				HostNamespace.HOST_NAMESPACE,
				"(" + HostNamespace.HOST_NAMESPACE + "=host)");

		TestWire root10HostWire = new TestWire(rootResourceFrag, hostReq,
				rootResource10, rootResource10
						.getCapabilities(HostNamespace.HOST_NAMESPACE).get(0));

		TestWiring root10Wiring = rootResource10.new TestWiring();
		root10Wiring.providedWires.add(root10HostWire);

		TestWiring fragWiring = rootResourceFrag.new TestWiring();
		fragWiring.requiredWires.add(root10HostWire);

		Map<Resource,Wiring> wirings = new HashMap<>();
		wirings.put(rootResourceFrag, fragWiring);
		wirings.put(rootResource10, root10Wiring);

		TestResolveContext context = new TestResolveContext(
				Collections.<Resource> singleton(rootResource11), null, null,
				wirings);
		context.relatedResources.put(rootResource11,
				Collections.<Resource> singleton(rootResourceFrag));
		Map<Resource,List<Wire>> result = shouldResolve(context);
		assertEquals("Wrong number of resolved resoruces.", 2, result.size());
		context.checkEmptyWires(result, rootResource11);
		context.checkWires(result,
				new TestWire(rootResourceFrag, hostReq, rootResource11,
						rootResource11
								.getCapabilities(HostNamespace.HOST_NAMESPACE)
								.get(0)));
	}

	/*
	 * Test that a related resource with related resources resolves the
	 * secondary related resources
	 */
	public void testRelatedWithRelated() {
		TestResource rootResource = new TestResource();
		TestResource rootRelatedResource1 = new TestResource();
		TestResource rootRelatedResource2 = new TestResource();
		TestResolveContext context = new TestResolveContext(
				Collections.<Resource> singleton(rootResource), null, null);
		context.relatedResources.put(rootResource,
				Collections.<Resource> singleton(rootRelatedResource1));
		context.relatedResources.put(rootRelatedResource1,
				Collections.<Resource> singleton(rootRelatedResource2));
		Map<Resource,List<Wire>> result = shouldResolve(context);
		assertEquals("Wrong number of resolved resoruces.", 3, result.size());
		context.checkEmptyWires(result, rootResource);
		context.checkEmptyWires(result, rootRelatedResource1);
		context.checkEmptyWires(result, rootRelatedResource2);
	}

	/*
	 * Tests that if a related resource with a related resource that is
	 * unresolvable then the related resource that pulls the unresolvable
	 * related resource in is still able to resolve
	 */
	public void testRelatedWithUnresolvableRelated() {
		TestResource rootResource = new TestResource();
		TestResource rootRelatedResource1 = new TestResource();
		TestResource rootRelatedResource2 = new TestResource();
		rootRelatedResource2.addRequirement("unresolvable",
				"(unresolvable=true)");
		TestResolveContext context = new TestResolveContext(
				Collections.<Resource> singleton(rootResource), null, null);
		context.relatedResources.put(rootResource,
				Collections.<Resource> singleton(rootRelatedResource1));
		context.relatedResources.put(rootRelatedResource1,
				Collections.<Resource> singleton(rootRelatedResource2));
		Map<Resource,List<Wire>> result = shouldResolve(context);
		assertEquals("Wrong number of resolved resoruces.", 2, result.size());
		context.checkEmptyWires(result, rootResource);
		context.checkEmptyWires(result, rootRelatedResource1);
	}

	/*
	 * Tests that a mandatory resource that is also pulled in as a related
	 * resource resolves fine.
	 */
	public void testRelatedAndMandatoryResource() {
		TestResource rootResource = new TestResource();
		TestResource rootRelatedResource = new TestResource();
		TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(rootResource, rootRelatedResource),
				null, null);
		context.relatedResources.put(rootResource,
				Collections.<Resource> singleton(rootRelatedResource));
		Map<Resource,List<Wire>> result = shouldResolve(context);
		assertEquals("Wrong number of resolved resoruces.", 2, result.size());
		context.checkEmptyWires(result, rootResource);
		context.checkEmptyWires(result, rootRelatedResource);
	}

	/*
	 * Tests that a mandatory resource that is also pulled in as a related
	 * resource but is unresolvable causes a resolution exception.
	 */
	public void testRelatedAndMandatoryResourceUnresolvable() {
		TestResource rootResource = new TestResource();
		TestResource rootRelatedResource = new TestResource();
		rootRelatedResource.addRequirement("unresolvable",
				"(unresolvable=true)");
		TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(rootResource, rootRelatedResource),
				null, null);
		context.relatedResources.put(rootResource,
				Collections.<Resource> singleton(rootRelatedResource));
		shouldNotResolve(context);
	}

	/*
	 * Tests that a optional resource that is also pulled in as a related
	 * resource but is unresolvable does not cause exception.
	 */
	public void testRelatedAndOptionalResourceUnresolvable() {
		TestResource rootResource = new TestResource();
		TestResource rootRelatedResource = new TestResource();
		rootRelatedResource.addRequirement("unresolvable",
				"(unresolvable=true)");
		TestResolveContext context = new TestResolveContext(
				Collections.<Resource> singleton(rootResource),
				Collections.<Resource> singleton(rootRelatedResource), null,
				null);
		context.relatedResources.put(rootResource,
				Collections.<Resource> singleton(rootRelatedResource));
		Map<Resource,List<Wire>> result = shouldResolve(context);
		assertEquals("Wrong number of resolved resoruces.", 1, result.size());
		context.checkEmptyWires(result, rootResource);
	}
}

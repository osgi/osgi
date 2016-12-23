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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.namespace.PackageNamespace;
import org.osgi.resource.Capability;
import org.osgi.resource.Namespace;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.resource.Wire;
import org.osgi.resource.Wiring;
import org.osgi.service.resolver.ResolutionException;
import org.osgi.service.resolver.Resolver;
import org.osgi.test.cases.resolver.junit.AbstractResolverTestCase.TestResource.TestRequirement;
import org.osgi.test.cases.resolver.junit.AbstractResolverTestCase.TestResource.TestWiring;

/**
 * Testing the resolverDynamic method
 */
public class ResolveDynamicTestCase extends AbstractResolverTestCase {

	/*
	 * A simple dynamic resolution with a host wiring that has no other package
	 * wires. The supplier is available and resolved already.
	 */
	public void testDynamicAlreadyResolvedProvider() throws Exception {
		final TestResource dynamicResolve = new TestResource();
		final Requirement dynamicReq = addDynamicImport(dynamicResolve, "foo");
		final TestResource fooProvider = new TestResource();
		final Capability fooCap = fooProvider.addCapability(
				PackageNamespace.PACKAGE_NAMESPACE,
				PackageNamespace.PACKAGE_NAMESPACE + "=foo");
		Map<Resource,Wiring> wirings = new HashMap<>();
		wirings.put(dynamicResolve, dynamicResolve.new TestWiring());
		wirings.put(fooProvider, fooProvider.new TestWiring());
		final TestResolveContext context = new TestResolveContext(
				null, null, Collections.singletonList(fooProvider), wirings);
		Map<Resource,List<Wire>> result = shouldDynamicResolve(context, wirings,
				dynamicReq);
		assertEquals("Should only be one resource in result.", 1,
				result.size());
		// check for the expected dynamic wire
		context.checkWires(result,
				new TestWire(dynamicResolve, dynamicReq, fooProvider, fooCap));
	}

	/*
	 * A simple dynamic resolution with a host wiring that has no other package
	 * wires. The supplier not resolved and should end up resolved after the
	 * dynamic resolution.
	 */
	public void testDynamicResolvableProvider() throws Exception {
		final TestResource dynamicResolve = new TestResource();
		final Requirement dynamicReq = addDynamicImport(dynamicResolve, "foo");
		final TestResource fooProvider = new TestResource();
		final Capability fooCap = fooProvider.addCapability(
				PackageNamespace.PACKAGE_NAMESPACE,
				PackageNamespace.PACKAGE_NAMESPACE + "=foo");
		Map<Resource,Wiring> wirings = new HashMap<>();
		wirings.put(dynamicResolve, dynamicResolve.new TestWiring());
		final TestResolveContext context = new TestResolveContext(null, null,
				Collections.singletonList(fooProvider), wirings);
		Map<Resource,List<Wire>> result = shouldDynamicResolve(context, wirings,
				dynamicReq);
		assertEquals("Wrong number of resources in result.", 2,
				result.size());
		// check for the expected dynamic wire
		context.checkWires(result,
				new TestWire(dynamicResolve, dynamicReq, fooProvider, fooCap));
		// check that the provider is now resolved
		List<Wire> fooProviderWires = result.get(fooProvider);
		assertNotNull("No provider wires.", fooProviderWires);
		// there are no reqs so should be empty wires
		assertEquals("Wrong number of wires.", 0, fooProviderWires.size());
	}

	/*
	 * Attempts to a dynamic resolve with no matching provider
	 */
	public void testDynamicNoProvider() {
		final TestResource dynamicResolve = new TestResource();
		final Requirement dynamicReq = addDynamicImport(dynamicResolve, "foo");
		Map<Resource,Wiring> wirings = new HashMap<>();
		wirings.put(dynamicResolve, dynamicResolve.new TestWiring());
		final TestResolveContext context = new TestResolveContext(null, null,
				null, wirings);
		shouldNotDynamicResolve(context, wirings, dynamicReq);
	}

	/*
	 * Attempts to dynamic resolve with matching provider that cannot resolve.
	 */
	public void testDynamicUnresolvableProvider() throws Exception {
		final TestResource dynamicResolve = new TestResource();
		final Requirement dynamicReq = addDynamicImport(dynamicResolve, "foo");
		final TestResource fooProvider = new TestResource();
		fooProvider.addRequirement(PackageNamespace.PACKAGE_NAMESPACE,
				'(' + PackageNamespace.PACKAGE_NAMESPACE + "=does.not.exist)");
		fooProvider.addCapability(PackageNamespace.PACKAGE_NAMESPACE,
				PackageNamespace.PACKAGE_NAMESPACE + "=foo");
		Map<Resource,Wiring> wirings = new HashMap<>();
		wirings.put(dynamicResolve, dynamicResolve.new TestWiring());
		final TestResolveContext context = new TestResolveContext(null, null,
				Collections.singletonList(fooProvider), wirings);
		shouldNotDynamicResolve(context, wirings, dynamicReq);
	}

	/*
	 * Attempts to dynamic resolve with a matching provider that would introduce
	 * a uses constraint violation. Then updates the dynamic requirement to
	 * allow the dynamic resolution to succeed.
	 */
	public void testDynamicUsesConflict() {
		final TestResource dynamicResolve = new TestResource();
		final Requirement barReq = dynamicResolve.addRequirement(
				PackageNamespace.PACKAGE_NAMESPACE,
				"(&(" + PackageNamespace.PACKAGE_NAMESPACE + "=bar)(x=1))");
		final TestRequirement dynamicReq = addDynamicImport(dynamicResolve,
				"foo",
				"(x=2)");

		final TestResource foo1Provider = new TestResource();
		final Capability bar1Cap = foo1Provider.addCapability(
				PackageNamespace.PACKAGE_NAMESPACE,
				PackageNamespace.PACKAGE_NAMESPACE + "=bar,x=1");
		bar1Cap.getDirectives().put(PackageNamespace.CAPABILITY_USES_DIRECTIVE,
				"foo");
		final Capability foo1Cap = foo1Provider.addCapability(
				PackageNamespace.PACKAGE_NAMESPACE,
				PackageNamespace.PACKAGE_NAMESPACE + "=foo,x=1");

		final TestResource foo2Provider = new TestResource();
		foo2Provider.addCapability(PackageNamespace.PACKAGE_NAMESPACE,
				PackageNamespace.PACKAGE_NAMESPACE + "=foo,x=2");

		Map<Resource,Wiring> wirings = new HashMap<>();

		TestWiring dynamicWiring = dynamicResolve.new TestWiring();
		TestWire barWire = new TestWire(dynamicResolve, barReq, foo1Provider,
				bar1Cap);
		dynamicWiring.requiredWires.add(barWire);
		wirings.put(dynamicResolve, dynamicWiring);

		TestWiring foo1Wiring = foo1Provider.new TestWiring();
		foo1Wiring.providedWires.add(barWire);
		wirings.put(foo1Provider, foo1Wiring);

		wirings.put(foo2Provider, foo2Provider.new TestWiring());

		final TestResolveContext context = new TestResolveContext(null, null,
				Arrays.asList(dynamicResolve, foo2Provider, foo1Provider),
				wirings);
		shouldNotDynamicResolve(context, wirings, dynamicReq);

		// change requirement to allow resolution
		dynamicReq.getDirectives().put(Namespace.REQUIREMENT_FILTER_DIRECTIVE,
				'(' + PackageNamespace.PACKAGE_NAMESPACE + "=foo)");
		Map<Resource,List<Wire>> result = shouldDynamicResolve(context, wirings,
				dynamicReq);

		assertEquals("Should only be one resource in result.", 1,
				result.size());
		// check for the expected dynamic wire
		context.checkWires(result, new TestWire(dynamicResolve, dynamicReq,
				foo1Provider, foo1Cap));
	}

	private Map<Resource,List<Wire>> shouldDynamicResolve(
			TestResolveContext context,
			Map<Resource,Wiring> wirings, Requirement dynamicReq) {
		Resolver resolver = getResolverService();
		Map<Resource,List<Wire>> result = null;
		try {
			result = resolver.resolveDynamic(context,
					wirings.get(dynamicReq.getResource()), dynamicReq);
			assertNotNull("No results from resolveDynamic", result);
			List<Wire> dynamicWires = result.get(dynamicReq.getResource());
			assertNotNull("No dynamic wires found.", dynamicWires);
			assertEquals("Wrong number of dynamic wires found.", 1,
					dynamicWires.size());
		} catch (ResolutionException e) {
			fail("Unexpected error.", e);
		}
		return result;
	}

	private void shouldNotDynamicResolve(TestResolveContext context,
			Map<Resource,Wiring> wirings, Requirement dynamicReq) {
		Resolver resolver = getResolverService();
		try {
			resolver.resolveDynamic(context,
					wirings.get(dynamicReq.getResource()), dynamicReq);
			fail("Expected a ResolutionException for failed resolveDynamic.");
		} catch (ResolutionException e) {
			// expected
		}
	}

	private Requirement addDynamicImport(TestResource resource,
			String pkgName) {
		return addDynamicImport(resource, pkgName, null);
	}

	private TestRequirement addDynamicImport(TestResource resource,
			String pkgName,
			String extraFilter) {
		String filter = '(' + PackageNamespace.PACKAGE_NAMESPACE + '=' + pkgName
				+ ')';
		if (extraFilter != null) {
			filter = "(&" + filter + extraFilter + ")";
		}
		TestRequirement dynamicReq = resource.new TestRequirement(
				PackageNamespace.PACKAGE_NAMESPACE, filter);
		dynamicReq.getDirectives().put(
				PackageNamespace.REQUIREMENT_RESOLUTION_DIRECTIVE,
				PackageNamespace.RESOLUTION_DYNAMIC);
		if (pkgName.endsWith("*")) {
			dynamicReq.getDirectives().put(
					PackageNamespace.REQUIREMENT_CARDINALITY_DIRECTIVE,
					PackageNamespace.CARDINALITY_MULTIPLE);
		}
		resource.addRequirement(dynamicReq);
		return dynamicReq;
	}
}

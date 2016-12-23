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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.osgi.test.cases.resolver.junit.AbstractResolverTestCase.TestResource.TestCapability;

/**
 * Testing the resolver with a synthetic namespace.
 * 
 * @author <a href="mailto:rellermeyer@us.ibm.com">Jan S. Rellermeyer</a>
 */
public class ResolverTestCase extends AbstractResolverTestCase {

	public void testNull() throws Exception {
		final Resolver resolver = getResolverService();

		try {
			resolver.resolve(null);
		} catch (final Exception e) {
			// expected
			return;
		}
		// nothing has happened? That's wrong.
		fail();
	}

	public void testEmpty() throws Exception {
		try {
			final Resolver resolver = getResolverService();
			final Map<Resource, List<Wire>> result = resolver
					.resolve(new ResolveContext() {

						@Override
						public List<Capability> findProviders(
								final Requirement requirement) {
							fail();
							return null;
						}

						@Override
						public int insertHostedCapability(
								final List<Capability> capabilities,
								final HostedCapability hostedCapability) {
							fail();
							return -1;
						}

						@Override
						public boolean isEffective(final Requirement requirement) {
							fail();
							return false;
						}

						@Override
						public Map<Resource, Wiring> getWirings() {
							fail();
							return null;
						}
					});
			assertTrue(result.isEmpty());
		} catch (final Throwable t) {
			fail("Unexpected exception", t);
		}
	}

	/*
	 * "The returned map is the property of the caller and can be modified by
	 * the caller"
	 */
	public void testModifiable() throws Exception {
		final TestResource r1 = new TestResource();
		r1.addRequirement(null, "(cap=*)");
		final TestResource r2 = new TestResource();
		r2.addCapability(null, "cap=true");
		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1, r2), null, null);
		final Map<Resource, List<Wire>> result = shouldResolve(context);
		assertNotNull(result.remove(r1));
		assertNotNull(result.remove(r2));
		result.put(r1, null);
		result.clear();
	}

	/*
	 * test that the result of a resolution preserves the equals relationship
	 * with the original values
	 */
	public void testResolutionIdentity() throws Exception {
		final TestResource r1 = new TestResource();
		final Requirement req = r1.addRequirement(null, "(cap=*)");
		final TestResource r2 = new TestResource();
		final Capability cap = r2.addCapability(null, "cap=true");
		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1, r2), null, null);
		final Map<Resource, List<Wire>> result = shouldResolve(context);
		context.checkFindProviderCalls(req);
		final Set<Resource> keys = result.keySet();
		assertTrue(keys.size() == 2);
		keys.remove(r2);
		assertTrue(keys.size() == 1);
		final Resource key = keys.toArray(new Resource[1])[0];
		assertTrue(key.equals(r1));
		assertTrue(r1.equals(key));
		final List<Wire> wires = result.get(r1);
		assertNotNull(wires);
		assertTrue(wires.size() == 1);
		final Wire wire = wires.get(0);
		final TestWire testWire = new TestWire(r1, req, r2, cap);
		assertTrue(testWire.equals(wire));
		assertTrue(wire.equals(testWire));
		assertTrue(wire.getProvider().equals(r2));
		assertTrue(r2.equals(wire.getProvider()));
	}

	/*
	 * mandatory resource with no requirements
	 */
	public void testMandatoryResources0() throws Exception {
		final TestResource r1 = new TestResource();
		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1), null, null);
		shouldResolve(context);
	}

	/*
	 * the resolver must never match any requirements and capabilities and
	 * should therefore blindly trust the resolve context to return only
	 * matching capabilities. Here, we play foul and expect the resolver to
	 * ignore that.
	 */
	public void testMatching0() throws Exception {
		final TestResource r1 = new TestResource();
		final Requirement req = r1.addRequirement(null, "(cap=*)");
		final TestResource r2 = new TestResource();
		final Capability cap = r2.addCapability(null, "match=false");

		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1, r2), null, null) {

			public List<Capability> findProviders(final Requirement requirement) {
				super.findProviders(requirement);
				return Arrays.asList(cap);
			}
		};

		final Map<Resource, List<Wire>> result = shouldResolve(context);
		context.checkFindProviderCalls(req);
		context.checkWires(result, new TestWire(r1, req, r2, cap));
	}

	/*
	 * the resolver must never match any requirements and capabilities and
	 * should therefore blindly trust the resolve context to return only
	 * matching capabilities. Here, we play foul by returning a different
	 * namespace and expect the resolver to ignore that.
	 */
	public void testMatching1() throws Exception {
		final TestResource r1 = new TestResource();
		final Requirement req = r1.addRequirement(null, "(cap=*)");
		final TestResource r2 = new TestResource();
		final Capability cap = r2.addCapability("other", "cap=true");

		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1, r2), null, null) {

			public List<Capability> findProviders(final Requirement requirement) {
				super.findProviders(requirement);
				return Arrays.asList(cap);
			}
		};

		final Map<Resource, List<Wire>> result = shouldResolve(context);
		context.checkFindProviderCalls(req);
		context.checkWires(result, new TestWire(r1, req, r2, cap));
	}

	/*
	 * even though the resolver gets the mandatory and optional resources it
	 * must not search these for capabilities not returned by findResources.
	 */
	public void testMatching2() throws Exception {
		final TestResource r1 = new TestResource();
		r1.addRequirement(null, "(cap=*)");
		final TestResource r2 = new TestResource();
		r2.addCapability(null, "cap=true");

		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1, r2), null, null) {

			@Override
			public List<Capability> findProviders(final Requirement requirement) {
				super.findProviders(requirement);
				// the returned list must be mutable
				// Collections.emptyList() is therefore not an option.
				return new ArrayList<Capability>();
			}
		};
		shouldNotResolve(context);
	}

	/*
	 * "the same is true for the existing wiring state used.
	 */
	public void testMatching3() throws Exception {
		final TestResource r1 = new TestResource();
		r1.addRequirement(null, "(cap=*)");
		final TestResource r2 = new TestResource();
		final Capability cap = r2.addCapability(null, "cap=true");

		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1), null, null) {

			@Override
			public Map<Resource, Wiring> getWirings() {
				final Map<Resource, Wiring> result = new HashMap<Resource, Wiring>();
				result.put(r2, new Wiring() {

					public List<Capability> getResourceCapabilities(
							String namespace) {
						return Arrays.asList(cap);
					}

					public List<Requirement> getResourceRequirements(
							String namespace) {
						return Collections.emptyList();
					}

					public List<Wire> getProvidedResourceWires(String namespace) {
						return Collections.emptyList();
					}

					public List<Wire> getRequiredResourceWires(String namespace) {
						return Collections.emptyList();
					}

					public Resource getResource() {
						return r2;
					}

				});
				return result;
			}

			@Override
			public List<Capability> findProviders(final Requirement requirement) {
				super.findProviders(requirement);
				// the returned list must be mutable
				// Collections.emptyList() is therefore not an option.
				return new ArrayList<Capability>();
			}
		};
		shouldNotResolve(context);
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
		final Requirement req = r1.addRequirement(null, "(foo=bar)");
		final TestResource r2 = new TestResource();
		final Capability cap = r2.addCapability(null, "foo=bar");
		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1, r2), null, null);
		final Map<Resource, List<Wire>> result = shouldResolve(context);
		context.checkFindProviderCalls(req);
		context.checkWires(result, new TestWire(r1, req, r2, cap));
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

	/*
	 * mandatory resources, one cannot be fulfilled
	 */
	public void testMandatoryResources5() throws Exception {
		final TestResource r1 = new TestResource();
		r1.addRequirement(null, "(foo=bar)");
		final TestResource r2 = new TestResource();
		r2.addCapability(null, "foo=bar");
		final TestResource r3 = new TestResource();
		r3.addRequirement(null, "(not=there)");
		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1, r2, r3), null, null);
		shouldNotResolve(context);
	}

	/*
	 * mandatory resources, one can be resolved through a third resource
	 * provided by the resolve context
	 */
	public void testMandatoryResources6() throws Exception {
		final TestResource r1 = new TestResource();
		final Requirement r1_req = r1.addRequirement(null, "(foo=bar)");
		final TestResource r2 = new TestResource();
		final Capability r2_cap = r2.addCapability(null, "foo=bar");
		final TestResource r3 = new TestResource();
		final Requirement r3_req = r3.addRequirement(null, "(is=*)");
		final TestResource r4 = new TestResource();
		final Capability r4_cap = r4.addCapability(null, "is=there");
		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1, r2, r3), null,
				Arrays.<Resource> asList(r4));
		final Map<Resource, List<Wire>> result = shouldResolve(context);
		context.checkFindProviderCalls(r1_req, r3_req);
		context.checkWires(result, new TestWire(r1, r1_req, r2, r2_cap),
				new TestWire(r3, r3_req, r4, r4_cap));
	}

	/*
	 * mandatory resources, an unresolvable resource returned by findProviders
	 * should not prevent resolution as long as there are alternatives
	 */
	public void testMandatoryResources7() throws Exception {
		final TestResource r1 = new TestResource();
		final Requirement r1_req = r1.addRequirement(null, "(foo=bar)");
		final TestResource r2 = new TestResource();
		r2.addCapability(null, "foo=bar");
		final Requirement r2_req = r2.addRequirement(null, "(unavailable=*)");
		final TestResource r3 = new TestResource();
		final Capability r3_cap = r3.addCapability(null, "foo=bar");
		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1), null, Arrays.<Resource> asList(
						r2, r3));
		final Map<Resource, List<Wire>> result = shouldResolve(context);
		context.checkFindProviderCalls(r1_req, r2_req);
		context.checkWires(result, new TestWire(r1, r1_req, r3, r3_cap));
	}

	/*
	 * duplicate mandatory resource
	 */
	public void testMandatoryResources8() throws Exception {
		final TestResource r1 = new TestResource();
		final Requirement req = r1.addRequirement(null, "(foo=bar)");
		final TestResource r2 = new TestResource();
		final Capability cap = r2.addCapability(null, "foo=bar");
		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1, r1), null,
				Arrays.<Resource> asList(r2));
		final Map<Resource, List<Wire>> result = shouldResolve(context);
		context.checkFindProviderCalls(req);
		context.checkWires(result, new TestWire(r1, req, r2, cap));
		final List<Wire> wires = result.get(r1);
		assertNotNull(wires);
		assertTrue(wires.size() == 1);
	}

	/*
	 * circular dependencies of two
	 */
	public void testCircularDependency0() throws Exception {
		final TestResource r1 = new TestResource();
		final Requirement r1_req = r1.addRequirement(null, "(r2=*)");
		final Capability r1_cap = r1.addCapability(null, "r1=true");
		final TestResource r2 = new TestResource();
		final Requirement r2_req = r2.addRequirement(null, "(r1=*)");
		final Capability r2_cap = r2.addCapability(null, "r2=42");
		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1, r2), null, null);
		final Map<Resource, List<Wire>> result = shouldResolve(context);
		context.checkFindProviderCalls(r1_req, r2_req);
		context.checkWires(result, new TestWire(r1, r1_req, r2, r2_cap),
				new TestWire(r2, r2_req, r1, r1_cap));
	}

	/*
	 * circular dependencies of three
	 */
	public void testCircularDependency1() throws Exception {
		final TestResource r1 = new TestResource();
		final Requirement r1_req = r1.addRequirement(null, "(r3=*)");
		final Capability r1_cap = r1.addCapability(null, "r1=true");
		final TestResource r2 = new TestResource();
		final Requirement r2_req = r2.addRequirement(null, "(r1=*)");
		final Capability r2_cap = r2.addCapability(null, "r2=42");
		final TestResource r3 = new TestResource();
		final Requirement r3_req = r3.addRequirement(null, "(r2=*)");
		final Capability r3_cap = r3.addCapability(null, "r3=abc");
		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1, r2, r3), null, null);
		final Map<Resource, List<Wire>> result = shouldResolve(context);
		context.checkFindProviderCalls(r1_req, r2_req, r3_req);
		context.checkWires(result, new TestWire(r1, r1_req, r3, r3_cap),
				new TestWire(r2, r2_req, r1, r1_cap), new TestWire(r3, r3_req,
						r2, r2_cap));
	}

	/*
	 * the resolver should respect the order of the findProviders list in this
	 * simple situation
	 */
	public void testOrder() throws Exception {
		final TestResource r1 = new TestResource();
		final Requirement req = r1.addRequirement(null, "(cap=*)");
		final TestResource r2 = new TestResource();
		final Capability cap = r2.addCapability(null, "cap=true");
		final TestResource r3 = new TestResource();
		r3.addCapability(null, "cap=42");
		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1, r2, r3), null, null);
		final Map<Resource, List<Wire>> result = shouldResolve(context);
		context.checkFindProviderCalls(req);
		context.checkWires(result, new TestWire(r1, req, r2, cap));
	}

	/*
	 * the resolver needs to respect existing wiring state and create a
	 * consistent resolution
	 */
/* Disabling: see bug 2435
	public void testExistingWiringState0() throws Exception {
		final TestResource r1 = new TestResource();
		final Requirement r1_req1 = r1.addRequirement(null, "(cap=*)");
		final Requirement r1_req2 = r1.addRequirement(null, "(cap2=*)");
		final TestResource r2 = new TestResource();
		final Capability r2_cap = r2.addCapability(null, "cap=42");
		final Requirement r2_req = r2.addRequirement(null, "(cap2=*)");
		final TestResource r3 = new TestResource();
		r3.addCapability(null, "cap2=true");
		final TestResource r4 = new TestResource();
		final Capability r4_cap = r4.addCapability(null, "cap2=42");

		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1), null, Arrays.<Resource> asList(
						r2, r3, r4)) {
			@Override
			public Map<Resource, Wiring> getWirings() {
				final Map<Resource, Wiring> result = new HashMap<Resource, Wiring>();
				result.put(r2, new Wiring() {

					private List<Wire> wireList = Arrays
							.<Wire> asList(new TestWire(r2, r2_req, r4, r4_cap));

					public List<Capability> getResourceCapabilities(
							String namespace) {
						return Arrays.asList(r2_cap);
					}

					public List<Requirement> getResourceRequirements(
							String namespace) {
						return Arrays.asList(r2_req);
					}

					public List<Wire> getProvidedResourceWires(String namespace) {
						return Collections.emptyList();
					}

					public List<Wire> getRequiredResourceWires(String namespace) {
						return wireList;
					}

					public Resource getResource() {
						return r2;
					}

				});

				result.put(r4, new Wiring() {

					public List<Capability> getResourceCapabilities(
							String namespace) {
						return Arrays.asList(r4_cap);
					}

					public List<Requirement> getResourceRequirements(
							String namespace) {
						return Collections.emptyList();
					}

					public List<Wire> getProvidedResourceWires(String namespace) {
						return Collections.emptyList();
					}

					public List<Wire> getRequiredResourceWires(String namespace) {
						return Collections.emptyList();
					}

					public Resource getResource() {
						return r4;
					}

				});

				return result;
			}

		};
		final Map<Resource, List<Wire>> result = shouldResolve1(context);

		context.checkFindProviderCalls(r1_req1, r1_req2);
		// FIXME: remove debug output
		System.err.println("I HAVE WIRES FOR " + result.get(r1));
		System.err.println("R3 is " + r3);
		System.err.println("R4 is " + r4);
		// FIXME: end debug output
		context.checkWires(result, new TestWire(r1, r1_req1, r2, r2_cap),
				new TestWire(r1, r1_req2, r4, r4_cap));
	}
*/
	/*
	 * if the existing wiring is fully resolved, the result of the resolution is
	 * expected to be empty (delta state)
	 */
	public void testExistingWiringState1() throws Exception {
		final TestResource r1 = new TestResource();
		final Requirement req = r1.addRequirement(null, "(cap=*)");
		final TestResource r2 = new TestResource();
		final Capability cap = r2.addCapability(null, "cap=42");

		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1, r2), null, null) {
			@Override
			public Map<Resource, Wiring> getWirings() {
				final Map<Resource, Wiring> result = new HashMap<Resource, Wiring>();
				result.put(r1, new Wiring() {

					private List<Wire> wireList = Arrays
							.<Wire> asList(new TestWire(r1, req, r2, cap));

					public List<Capability> getResourceCapabilities(
							String namespace) {
						return Arrays.asList(cap);
					}

					public List<Requirement> getResourceRequirements(
							String namespace) {
						return Arrays.asList(req);
					}

					public List<Wire> getProvidedResourceWires(String namespace) {
						return wireList;
					}

					public List<Wire> getRequiredResourceWires(String namespace) {
						return wireList;
					}

					public Resource getResource() {
						return r1;
					}

				});
				result.put(r2, new Wiring() {

					public List<Capability> getResourceCapabilities(
							String namespace) {
						return Arrays.asList(cap);
					}

					public List<Requirement> getResourceRequirements(
							String namespace) {
						return Collections.emptyList();
					}

					public List<Wire> getProvidedResourceWires(String namespace) {
						return Collections.emptyList();
					}

					public List<Wire> getRequiredResourceWires(String namespace) {
						return Collections.emptyList();
					}

					public Resource getResource() {
						return r2;
					}

				});
				return result;
			}

		};
		final Map<Resource, List<Wire>> result = shouldResolve1(context);
		assertTrue(result.isEmpty());
	}

	/*
	 * resolution should ignore non-effecive requirements
	 */
	public void testEffective0() throws Exception {
		final TestResource r1 = new TestResource();
		final Requirement req1 = r1.addRequirement(null, "(foo=bar)");
		final Requirement req2 = r1.addRequirement(null, "(unavailable=*)");
		final TestResource r2 = new TestResource();
		final Capability cap = r2.addCapability(null, "foo=bar");
		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1), null,
				Arrays.<Resource> asList(r2)) {
			@Override
			public boolean isEffective(final Requirement requirement) {
				return requirement != req2;
			}
		};
		final Map<Resource, List<Wire>> result = shouldResolve(context);
		context.checkFindProviderCalls(req1);
		context.checkWires(result, new TestWire(r1, req1, r2, cap));
	}

	/*
	 * resolver should ignore effective directives and only rely on the resolve
	 * context to declare which requirements are effective
	 */
	public void testEffective1() throws Exception {
		final TestResource r1 = new TestResource();
		r1.addRequirement(null, "(foo=bar)");
		final Requirement req2 = r1.addRequirement(null, "(unavailable=*)");
		req2.getDirectives().put(Namespace.REQUIREMENT_EFFECTIVE_DIRECTIVE,
				Namespace.EFFECTIVE_ACTIVE);
		final TestResource r2 = new TestResource();
		r2.addCapability(null, "foo=bar");
		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1), null,
				Arrays.<Resource> asList(r2));
		shouldNotResolve(context);
	}

	/*
	 * resolution should ignore mandatory directive in non osgi.wiring.*
	 * namespaces
	 */
	public void testMandatoryDirective() throws Exception {
		final TestResource r1 = new TestResource();
		final Requirement req1 = r1.addRequirement(null, "(foo=bar)");
		final TestResource r2 = new TestResource();
		final Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("foo", "bar");
		final TestCapability cap = r2.new TestCapability(null, attributes) {

			public Map<String, String> getDirectives() {
				final Map<String, String> result = new HashMap<String, String>();
				result.put("mandatory", "not_provided");
				return result;
			}

		};
		r2.addCapability(cap);

		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1), null,
				Arrays.<Resource> asList(r2));
		final Map<Resource, List<Wire>> result = shouldResolve(context);
		context.checkFindProviderCalls(req1);
		context.checkWires(result, new TestWire(r1, req1, r2, cap));
	}

	/*
	 * resolver should ignore the singleton directive
	 */
	public void testSingletonDirective() throws Exception {
		final TestResource r1 = new TestResource();
		final Requirement req1 = r1.addRequirement(null, "(foo=bar)");
		final TestResource r2 = new TestResource();
		final Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("foo", "bar");
		final TestCapability cap = r2.new TestCapability(null, attributes) {

			public Map<String, String> getDirectives() {
				final Map<String, String> result = new HashMap<String, String>();
				result.put("singleton", "true");
				return result;
			}

		};
		r2.addCapability(cap);

		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1), null,
				Arrays.<Resource> asList(r2));
		final Map<Resource, List<Wire>> result = shouldResolve(context);
		context.checkFindProviderCalls(req1);
		context.checkWires(result, new TestWire(r1, req1, r2, cap));
	}

	/*
	 * resolver should ignore unresolvable optional resources
	 */
	public void testOptionalResources0() throws Exception {
		final TestResource r1 = new TestResource();
		final Requirement r1_req = r1.addRequirement(null, "(foo=bar)");
		final TestResource r2 = new TestResource();
		final Capability cap = r2.addCapability(null, "foo=bar");
		final TestResource r3 = new TestResource();
		final Requirement r3_req = r3.addRequirement(null, "(not=provided)");

		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1), Arrays.<Resource> asList(r3),
				Arrays.<Resource> asList(r2));
		final Map<Resource, List<Wire>> result = shouldResolve(context);
		context.checkFindProviderCalls(r1_req, r3_req);
		context.checkWires(result, new TestWire(r1, r1_req, r2, cap));
	}

	/*
	 * resolution should make an effort and resolve optional resources
	 */
	public void testOptionalResources1() throws Exception {
		final TestResource r1 = new TestResource();
		final Requirement req = r1.addRequirement(null, "(foo=bar)");
		final TestResource r2 = new TestResource();
		final Capability cap = r2.addCapability(null, "foo=bar");

		final TestResolveContext context = new TestResolveContext(null,
				Arrays.<Resource> asList(r1), Arrays.<Resource> asList(r2));
		final Map<Resource, List<Wire>> result = shouldResolve(context);
		context.checkFindProviderCalls(req);
		context.checkWires(result, new TestWire(r1, req, r2, cap));
	}

	/*
	 * in this simple case we can test the unresolvable requirements of the
	 * exception
	 */
	public void testUnresolvableRequirements() throws Exception {
		final TestResource r1 = new TestResource();
		final Requirement req = r1.addRequirement(null, "(foo=bar)");

		final TestResolveContext context = new TestResolveContext(
				Arrays.<Resource> asList(r1), null, null);
		final ResolutionException re = shouldNotResolve(context);
		assertNotNull(re);
		final Collection<Requirement> unresolvable = re
				.getUnresolvedRequirements();
		assertNotNull(unresolvable);
		assertTrue(unresolvable.size() == 1);
		assertTrue(unresolvable.toArray()[0].equals(req));
		assertTrue(req.equals(unresolvable.toArray()[0]));
	}
}

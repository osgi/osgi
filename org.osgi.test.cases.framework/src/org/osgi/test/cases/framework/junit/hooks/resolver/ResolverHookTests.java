/*
 * Copyright (c) OSGi Alliance (2010). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.test.cases.framework.junit.hooks.resolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.AssertionFailedError;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.resolver.ResolverHook;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.Capability;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.test.support.OSGiTestCase;

public class ResolverHookTests extends OSGiTestCase {
	private final List bundles = new ArrayList();
	private final List registrations = new ArrayList();
	FrameworkWiring frameworkWiring;
	
	
	public Bundle install(String bundle) {
		Bundle result = null;
		try {
			result = super.install(bundle);
		} catch (BundleException e) {
			fail("failed to install bundle: " + bundle, e);
		} catch (IOException e) {
			fail("failed to install bundle: " + bundle, e);
		}
		if (!bundles.contains(result))
			bundles.add(result);
		return result;
	}

	protected void setUp() throws Exception {
		registrations.clear();
		bundles.clear();
		frameworkWiring = (FrameworkWiring) getContext().getBundle(0).adapt(FrameworkWiring.class);
	}

	protected void tearDown() throws Exception {
		for (Iterator iRegistrations = registrations.iterator(); iRegistrations.hasNext();)
			try {
				((ServiceRegistration) iRegistrations.next()).unregister();
			} catch (IllegalStateException e) {
				// probably unregistered during test
			}
		registrations.clear();
		for (Iterator iBundles = bundles.iterator(); iBundles.hasNext();)
			try {
				((Bundle) iBundles.next()).uninstall();
			} catch (BundleException e) {
				// nothing
			}
		refreshBundles(bundles);
		bundles.clear();
	}

	private void refreshBundles(List bundles) {
		final boolean[] done = new boolean[] {false};
		FrameworkListener listener = new FrameworkListener() {
			public void frameworkEvent(FrameworkEvent event) {
				synchronized (done) {
					if (event.getType() == FrameworkEvent.PACKAGES_REFRESHED) {
						done[0] = true;
						done.notify();
					}
				}
			}
		};
		frameworkWiring.refreshBundles(bundles, new FrameworkListener[] {listener});
		synchronized (done) {
			if (!done[0])
				try {
					done.wait(5000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					fail("Unexepected interruption.", e);
				}
			if (!done[0])
				fail("Timed out waiting for refresh bundles to finish.");
		}
	}

	private ServiceRegistration registerHook(ResolverHook hook, int ranking) {
		Dictionary props = new Hashtable();
		props.put(Constants.SERVICE_RANKING, new Integer(ranking));
		ServiceRegistration reg = getContext().registerService(ResolverHook.class, hook, props);
		registrations.add(reg);
		return reg;
	}

	private Filter createFilter(String filter) {
		try {
			return getContext().createFilter(filter);
		} catch (InvalidSyntaxException e) {
			fail("Failed to create filter: " + filter, e);
		}
		return null;
	}

	public void testBeginEnd01() {
		LinkedList beginOrder = new LinkedList();
		LinkedList endOrder = new LinkedList();
		TestResolverHook hook1 = new TestResolverHook(new Long(1), null, beginOrder, endOrder);
		TestResolverHook hook2 = new TestResolverHook(new Long(2), new RuntimeException("Test hook error"), beginOrder, endOrder);
		TestResolverHook hook3 = new TestResolverHook(new Long(3), null, beginOrder, endOrder);
		registerHook(hook1, 0);
		registerHook(hook2, Integer.MAX_VALUE);
		registerHook(hook3, Integer.MAX_VALUE);
		install("resolver.tb1.v100.jar");
		Bundle tb2 = install("resolver.tb2.jar");
		try {
			tb2.start();
		} catch (BundleException e) {
			fail("failed to start tb2.", e);
		}

		assertEquals("Wrong number of start called", 3, beginOrder.size());
		assertEquals("Wrong hook.begin called first", hook2.getID(), beginOrder.removeFirst());
		assertEquals("Wrong hook.begin called second", hook3.getID(), beginOrder.removeFirst());
		assertEquals("Wrong hook.begin called third", hook1.getID(), beginOrder.removeFirst());

		assertEquals("Wrong number of end called", 3, endOrder.size());
		assertEquals("Wrong hook.end called first", hook2.getID(), endOrder.removeFirst());
		assertEquals("Wrong hook.end called second", hook3.getID(), endOrder.removeFirst());
		assertEquals("Wrong hook.end called third", hook1.getID(), endOrder.removeFirst());

		if (hook1.getError() != null)
			throw hook1.getError();
		if (hook2.getError() != null)
			throw hook2.getError();
		if (hook3.getError() != null)
			throw hook3.getError();
	}

	public void testBeginTriggers() {
		fail("Need to test triggers.");
	}

	public void testFilterResolvable01() {
		final AssertionFailedError[] error = new AssertionFailedError[1];
		ResolverHook hook1 = new ResolverHook(){
			public void begin(Collection triggers) {
			}
			public void end() {
			}
			public void filterMatches(BundleRevision arg0, Collection arg1) {
			}
			public void filterResolvable(Collection arg0) {
				try {
						for(Iterator resolvable = arg0.iterator(); resolvable.hasNext();) {
							BundleRevision revision = (BundleRevision) resolvable.next();
							if ("org.osgi.test.cases.framework.resolver.tb1".equals(revision.getSymbolicName())) {
								arg0.remove(revision);
								try {
									arg0.add(revision);
									fail("Expected failure on add.");
								} catch (UnsupportedOperationException e) {
									//expected 
								}
								try {
									arg0.addAll(Arrays.asList(new Object[]{revision}));
									fail("Expected failure on addAll.");
								} catch (UnsupportedOperationException e) {
									// expected
								}
							}
						}
				} catch (AssertionFailedError e) {
					if (error[0] != null)
						error[0] = e;
				}
			}
			public void filterSingletonCollisions(Capability arg0,
					Collection arg1) {
			}
			
		};
		ServiceRegistration reg = registerHook(hook1, 0);
		Bundle tb1 = install("resolver.tb1.v100.jar");
		Bundle tb2 = install("resolver.tb2.jar");
		try {
			tb2.start();
			fail("Should have failed to start tb2.");
		} catch (BundleException e) {
			// expected
			assertEquals("Wrong exception type.", BundleException.RESOLVE_ERROR, e.getType());
		}
		if (error[0] != null)
			throw error[0];
		assertEquals("Wrong state for tb1", Bundle.INSTALLED, tb1.getState());
		assertEquals("Wrong state for tb2", Bundle.INSTALLED, tb2.getState());
		// unregister the hook; bundles should start fine and resolve
		reg.unregister();
		try {
			tb2.start();
		} catch (BundleException e) {
			fail("Unexpected failed to start.", e);
		}
	}
	public void testFilterImportPackage01() {
		final AssertionFailedError[] error = new AssertionFailedError[1];
		ResolverHook hook1 = new ResolverHook(){
			public void begin(Collection triggers) {
			}
			public void end() {
			}
			public void filterMatches(BundleRevision arg0, Collection arg1) {
				String symbolicName = "org.osgi.test.cases.framework.resolver.tb2";
				String packageName = "org.osgi.test.cases.framework.resolver.tb1";
				if (!symbolicName.equals(arg0.getSymbolicName()))
					return;
				for (Iterator packages = arg1.iterator(); packages.hasNext();) {
					Capability pkg = (Capability) packages.next();
					if (!Capability.PACKAGE_CAPABILITY.equals(pkg.getNamespace()) ||
							!packageName.equals(pkg.getAttributes().get(Capability.PACKAGE_CAPABILITY)))
						return;

					packages.remove();
					try {
						arg1.add(pkg);
						fail("Expected failure on add.");
					} catch (UnsupportedOperationException e) {
						//expected 
					}
					try {
						arg1.addAll(Arrays.asList(new Object[]{pkg}));
						fail("Expected failure on addAll.");
					} catch (UnsupportedOperationException e) {
						// expected
					}
				}
			}
			public void filterResolvable(Collection arg0) {
			}
			public void filterSingletonCollisions(Capability arg0,
					Collection arg1) {
			}
			
		};
		ServiceRegistration reg = registerHook(hook1, 0);
		install("resolver.tb1.v100.jar");
		Bundle tb2 = install("resolver.tb2.jar");
		try {
			tb2.start();
			fail("Should have failed to start tb2.");
		} catch (BundleException e) {
			// expected
			assertEquals("Wrong exception type.", BundleException.RESOLVE_ERROR, e.getType());
		}
		if (error[0] != null)
			throw error[0];
		assertEquals("Wrong state for tb2", Bundle.INSTALLED, tb2.getState());
		// unregister the hook; bundles should start fine and resolve
		reg.unregister();
		try {
			tb2.start();
		} catch (BundleException e) {
			fail("Unexpected failed to start.", e);
		}
	}

	public void testFilterImportPackage02() {
		Filter filterCapabilities1 = createFilter(
				"(&" + 
				  "(osgi.package=org.osgi.test.cases.framework.resolver.tb1)" +
				  "(bundle-symbolic-name=org.osgi.test.cases.framework.resolver.tb1)" + 
				  "(bundle-version>=1.1)(!(bundle-version>=1.2))" + 
				  "(version>=1.1)(!(version>=1.2))" +
				")");
		Filter filterCapabilities2 = createFilter(
				"(&" + 
				  "(osgi.package=org.osgi.test.cases.framework.resolver.tb1)" +
				  "(bundle-symbolic-name=org.osgi.test.cases.framework.resolver.tb1)" + 
				  "(bundle-version>=1.0)(!(bundle-version>=1.1))" +
				  "(version>=1.0)(!(version>=1.1))" +
				")");
		TestFilterCapabilityHook hook1 = new TestFilterCapabilityHook(filterCapabilities1);
		ServiceRegistration reg = registerHook(hook1, 0);

		Bundle tb1v110 = install("resolver.tb1.v110.jar");
		Bundle tb1v100 = install("resolver.tb1.v100.jar");
		assertTrue("Could not resolve tb1 bundles", frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb1v100, tb1v110})));

		System.setProperty(tb1v100.getSymbolicName(), "v110");
		Bundle tb2 = install("resolver.tb2.jar");
		try {
			tb2.start();
			fail("Should have failed to start tb2.");
		} catch (BundleException e) {
			// expected
			assertEquals("Wrong exception type.", BundleException.ACTIVATOR_ERROR, e.getType());
		}
		if (hook1.getError() != null)
			throw hook1.getError();
		assertEquals("Wrong state for tb2", Bundle.RESOLVED, tb2.getState());
		// unregister the hook; and register one that gives us the other package
		//  bundles should start fine after refreshing
		reg.unregister();
		TestFilterCapabilityHook hook2 = new TestFilterCapabilityHook(filterCapabilities2);
		registerHook(hook2, 0);
		refreshBundles(Arrays.asList(new Bundle[] {tb2}));
		try {
			tb2.start();
		} catch (BundleException e) {
			fail("Unexpected failed to start.", e);
		}
		if (hook2.getError() != null)
			throw hook2.getError();
	}

	public void testFilterDyanmicImportPackage01() {
		Filter filterCapabilities1 = createFilter(
				"(&" + 
				  "(osgi.package=org.osgi.test.cases.framework.resolver.tb1)" +
				  "(version>=1.1)(!(version>=1.2))" +
				")");
		Filter filterCapabilities2 = createFilter(
				"(&" + 
				  "(osgi.package=org.osgi.test.cases.framework.resolver.tb1)" +
				  "(version>=1.0)(!(version>=1.1))" +
				")");
		TestFilterCapabilityHook hook1 = new TestFilterCapabilityHook(filterCapabilities1);
		ServiceRegistration reg = registerHook(hook1, 0);

		Bundle tb1v110 = install("resolver.tb1.v110.jar");
		Bundle tb1v100 = install("resolver.tb1.v100.jar");
		assertTrue("Could not resolve tb1 bundles", frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb1v100, tb1v110})));

		System.setProperty(tb1v100.getSymbolicName(), "v110");
		Bundle tb7 = install("resolver.tb7.jar");
		try {
			tb7.start();
			fail("Should have failed to start tb7.");
		} catch (BundleException e) {
			// expected
			assertEquals("Wrong exception type.", BundleException.ACTIVATOR_ERROR, e.getType());
		}
		if (hook1.getError() != null)
			throw hook1.getError();
		assertEquals("Wrong state for tb7", Bundle.RESOLVED, tb7.getState());
		// unregister the hook; and register one that gives us the other package
		//  bundles should start fine after refreshing
		reg.unregister();
		TestFilterCapabilityHook hook2 = new TestFilterCapabilityHook(filterCapabilities2);
		registerHook(hook2, 0);
		refreshBundles(Arrays.asList(new Bundle[] {tb7}));
		try {
			tb7.start();
		} catch (BundleException e) {
			fail("Unexpected failed to start.", e);
		}
		if (hook2.getError() != null)
			throw hook2.getError();
	}

	public void testFilterRequireBundle01() {
		Filter filterCapabilities1 = createFilter(
				"(&" + 
				  "(osgi.bundle=org.osgi.test.cases.framework.resolver.tb1)" +
				  "(bundle-version>=1.1)(!(bundle-version>=1.2))" + 
				")");
		Filter filterCapabilities2 = createFilter(
				"(&" + 
				  "(osgi.bundle=org.osgi.test.cases.framework.resolver.tb1)" +
				  "(bundle-version>=1.0)(!(bundle-version>=1.1))" +
				")");
		TestFilterCapabilityHook hook1 = new TestFilterCapabilityHook(filterCapabilities1);
		ServiceRegistration reg = registerHook(hook1, 0);

		Bundle tb1v110 = install("resolver.tb1.v110.jar");
		Bundle tb1v100 = install("resolver.tb1.v100.jar");
		assertTrue("Could not resolve tb1 bundles", frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb1v100, tb1v110})));

		System.setProperty(tb1v100.getSymbolicName(), "v110");
		Bundle tb3 = install("resolver.tb3.jar");
		try {
			tb3.start();
			fail("Should have failed to start tb3.");
		} catch (BundleException e) {
			// expected
			assertEquals("Wrong exception type.", BundleException.ACTIVATOR_ERROR, e.getType());
		}
		if (hook1.getError() != null)
			throw hook1.getError();
		assertEquals("Wrong state for tb3", Bundle.RESOLVED, tb3.getState());
		// unregister the hook; and register one that gives us the other bundle
		// bundles should start fine after refreshing
		reg.unregister();
		TestFilterCapabilityHook hook2 = new TestFilterCapabilityHook(filterCapabilities2);
		registerHook(hook2, 0);
		refreshBundles(Arrays.asList(new Bundle[] {tb3}));
		try {
			tb3.start();
		} catch (BundleException e) {
			fail("Unexpected failed to start.", e);
		}
		if (hook2.getError() != null)
			throw hook2.getError();
	}

	public void testFilterHost01() {
		Filter filterCapabilities1 = createFilter(
				"(&" + 
				  "(osgi.bundle=org.osgi.test.cases.framework.resolver.tb1)" +
				  "(bundle-version>=1.1)(!(bundle-version>=1.2))" + 
				")");
		Filter filterCapabilities2 = createFilter(
				"(&" + 
				  "(osgi.bundle=org.osgi.test.cases.framework.resolver.tb1)" +
				  "(bundle-version>=1.0)(!(bundle-version>=1.1))" +
				")");
		TestFilterCapabilityHook hook1 = new TestFilterCapabilityHook(filterCapabilities1);
		ServiceRegistration reg = registerHook(hook1, 0);

		Bundle tb1v110 = install("resolver.tb1.v110.jar");
		Bundle tb1v100 = install("resolver.tb1.v100.jar");
		assertTrue("Could not resolve tb1 bundles", frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb1v100, tb1v110})));

		System.setProperty(tb1v100.getSymbolicName(), "v110");
		Bundle tb4 = install("resolver.tb4.jar");
		assertFalse("Should not be able to resolve fragment tb4", frameworkWiring.resolveBundles(Arrays.asList(new Bundle[]{tb4})));
		if (hook1.getError() != null)
			throw hook1.getError();
		assertEquals("Wrong state for tb4", Bundle.INSTALLED, tb4.getState());
		// unregister the hook; and register one that allows the correct host
		// bundles should resolve now
		reg.unregister();
		TestFilterCapabilityHook hook2 = new TestFilterCapabilityHook(filterCapabilities2);
		registerHook(hook2, 0);
		assertTrue("Should resolve fragment tb4", frameworkWiring.resolveBundles(Arrays.asList(new Bundle[]{tb4})));
		if (hook2.getError() != null)
			throw hook2.getError();
		assertEquals("Wrong state for tb4", Bundle.RESOLVED, tb4.getState());
	}

	public void testFilterGenericRequire01() {
		Filter filterCapabilities1 = createFilter(
				"(&" + 
				  "(test=aName)" +
				  "(version>=1.1.0)(!(version>=1.2.0))" + 
				")");
		Filter filterCapabilities2 = createFilter(
				"(&" + 
				  "(test=aName)" +
				  "(version>=1.0.0)(!(version>=1.1.0))" + 
				")");
		TestFilterCapabilityHook hook1 = new TestFilterCapabilityHook(filterCapabilities1);
		ServiceRegistration reg = registerHook(hook1, 0);

		Bundle tb1v110 = install("resolver.tb1.v110.jar");
		Bundle tb1v100 = install("resolver.tb1.v100.jar");
		assertTrue("Could not resolve tb1 bundles", frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb1v100, tb1v110})));

		System.setProperty(tb1v100.getSymbolicName(), "v110");
		Bundle tb5 = install("resolver.tb5.jar");
		assertFalse("Should not be able to resolve bundle tb5", frameworkWiring.resolveBundles(Arrays.asList(new Bundle[]{tb5})));
		if (hook1.getError() != null)
			throw hook1.getError();
		assertEquals("Wrong state for tb5", Bundle.INSTALLED, tb5.getState());
		// unregister the hook; and register one that allows the correct host
		// bundles should resolve now
		reg.unregister();
		TestFilterCapabilityHook hook2 = new TestFilterCapabilityHook(filterCapabilities2);
		registerHook(hook2, 0);
		refreshBundles(Arrays.asList(new Bundle[] {tb5}));
		assertTrue("Should resolve tb5", frameworkWiring.resolveBundles(Arrays.asList(new Bundle[]{tb5})));
		if (hook2.getError() != null)
			throw hook2.getError();
		assertEquals("Wrong state for tb5", Bundle.RESOLVED, tb5.getState());
	}

	public void testFilterSingletonCollisions01() {
		// ensure no resolution while we setup the tests
		PreventResolution preventHook = new PreventResolution();
		ServiceRegistration preventReg = registerHook(preventHook, 0);
		Bundle tb6v100 = install("resolver.tb6.v100.jar");
		Bundle tb6v200 = install("resolver.tb6.v200.jar");
		Bundle tb6v300 = install("resolver.tb6.v300.jar");
		Bundle tb6v400 = install("resolver.tb6.v400.jar");
		List bundles = Arrays.asList(new Bundle[] {tb6v100, tb6v200, tb6v300, tb6v400});

		// confirm we cannot resolve any of the bundles
		assertFalse("Should not be able to resolve the bundles", frameworkWiring.resolveBundles(bundles));
		assertEquals("Wrong state of bundle tb6v100", Bundle.INSTALLED, tb6v100.getState());
		assertEquals("Wrong state of bundle tb6v200", Bundle.INSTALLED, tb6v200.getState());
		assertEquals("Wrong state of bundle tb6v300", Bundle.INSTALLED, tb6v300.getState());
		assertEquals("Wrong state of bundle tb6v400", Bundle.INSTALLED, tb6v400.getState());

		// create a hook that isolates all test singletons
		Map allIsolated = new HashMap();
		allIsolated.put(tb6v100, Arrays.asList(new Bundle[]{tb6v200, tb6v300, tb6v400}));
		allIsolated.put(tb6v200, Arrays.asList(new Bundle[]{tb6v100, tb6v300, tb6v400}));
		allIsolated.put(tb6v300, Arrays.asList(new Bundle[]{tb6v100, tb6v200, tb6v400}));
		allIsolated.put(tb6v400, Arrays.asList(new Bundle[]{tb6v100, tb6v200, tb6v300}));
		TestFilterSingletonCollisions allIsolatedHook = new TestFilterSingletonCollisions(allIsolated);
		ServiceRegistration allIsolatedReg = registerHook(allIsolatedHook, 0);
		// OK, now allow resolution
		preventReg.unregister();

		// confirm we can resolve all of the singleton bundles
		assertTrue("Should be able to resolve the bundles", frameworkWiring.resolveBundles(bundles));
		assertEquals("Wrong state of bundle tb6v100", Bundle.RESOLVED, tb6v100.getState());
		assertEquals("Wrong state of bundle tb6v200", Bundle.RESOLVED, tb6v200.getState());
		assertEquals("Wrong state of bundle tb6v300", Bundle.RESOLVED, tb6v300.getState());
		assertEquals("Wrong state of bundle tb6v400", Bundle.RESOLVED, tb6v400.getState());
		if (allIsolatedHook.getError() != null)
			throw allIsolatedHook.getError();
		allIsolatedReg.unregister();

		// prevent resolution again
		preventReg = registerHook(preventHook, 0);
		refreshBundles(bundles);

		// isolate version 1 and 2 from version 3 and 4
		Map isolate12From34 = new HashMap();
		isolate12From34.put(tb6v100, Arrays.asList(new Bundle[] {tb6v300, tb6v400}));
		isolate12From34.put(tb6v200, Arrays.asList(new Bundle[] {tb6v300, tb6v400}));
		isolate12From34.put(tb6v300, Arrays.asList(new Bundle[] {tb6v100, tb6v200}));
		isolate12From34.put(tb6v400, Arrays.asList(new Bundle[] {tb6v100, tb6v200}));
		TestFilterSingletonCollisions isolate12From34Hook = new TestFilterSingletonCollisions(isolate12From34);
		ServiceRegistration isolate12From34Reg = registerHook(isolate12From34Hook, 0);
		// allow resolution again
		preventReg.unregister();

		// confirm we cannot resolve all bundles
		assertFalse("Should not be able to resolve all the bundles", frameworkWiring.resolveBundles(bundles));
		assertTrue("One and only one of tb6v100 and tb6v200 should be resolved",
				(tb6v100.getState() == Bundle.RESOLVED) ^
				(tb6v200.getState() == Bundle.RESOLVED));
		assertTrue("One and only one of tb6v300 and tb6v400 should be resolved",
				(tb6v300.getState() == Bundle.RESOLVED) ^
				(tb6v400.getState() == Bundle.RESOLVED));
		if (isolate12From34Hook.getError() != null)
			throw isolate12From34Hook.getError();
		isolate12From34Reg.unregister();
		
		// prevent resolution again
		preventReg = registerHook(preventHook, 0);
		refreshBundles(bundles);
		// allow resolution again
		preventReg.unregister();
		assertFalse("Should not be able to resolve all the bundles", frameworkWiring.resolveBundles(bundles));
		assertTrue("One and only one of tb6 bundle should be resolved", 
				(tb6v100.getState() == Bundle.RESOLVED) ^
				(tb6v200.getState() == Bundle.RESOLVED) ^
				(tb6v300.getState() == Bundle.RESOLVED) ^
				(tb6v400.getState() == Bundle.RESOLVED));

		// prevent resolution again
		preventReg = registerHook(preventHook, 0);
		refreshBundles(bundles);
		
		// make all singletons isolated again
		allIsolatedReg = registerHook(allIsolatedHook, 0);
		// Filter version 2 and 4 from resolving; this is to force version 1 and 3 only to resolve
		TestFilterResolvable resolveTwoSingletons = new TestFilterResolvable(Arrays.asList(new Bundle[] {tb6v200, tb6v400}));
		ServiceRegistration resolveTwoReg = registerHook(resolveTwoSingletons, 0);
		// allow resolution again.
		preventReg.unregister();

		assertFalse("Should not be able to resolve all the bundles", frameworkWiring.resolveBundles(bundles));
		assertEquals("Wrong state of bundle tb6v100", Bundle.RESOLVED, tb6v100.getState());
		assertEquals("Wrong state of bundle tb6v200", Bundle.INSTALLED, tb6v200.getState());
		assertEquals("Wrong state of bundle tb6v300", Bundle.RESOLVED, tb6v300.getState());
		assertEquals("Wrong state of bundle tb6v400", Bundle.INSTALLED, tb6v400.getState());

		// again isolate version 1 and 2 from version 3 and 4
		allIsolatedReg.unregister();
		isolate12From34Reg = registerHook(isolate12From34Hook, 0);

		// make sure we cannot resolve 2 and 4 now since 1 and 3 are already resolved
		resolveTwoReg.unregister();
		assertFalse("Should not be able to resolve all the bundles", frameworkWiring.resolveBundles(bundles));
		assertEquals("Wrong state of bundle tb6v100", Bundle.RESOLVED, tb6v100.getState());
		assertEquals("Wrong state of bundle tb6v200", Bundle.INSTALLED, tb6v200.getState());
		assertEquals("Wrong state of bundle tb6v300", Bundle.RESOLVED, tb6v300.getState());
		assertEquals("Wrong state of bundle tb6v400", Bundle.INSTALLED, tb6v400.getState());
	}

	public void testFilterMatchesCandidates() {
		// this test ensures that the candidates passed really match 
		// a constraint before being passed to the hook
		PreventResolution preventHook = new PreventResolution();
		ServiceRegistration preventReg = registerHook(preventHook, 0);

		final Bundle tb1v100 = install("resolver.tb1.v100.jar");
		final Bundle tb1v110 = install("resolver.tb1.v110.jar");
		final Bundle tb2 = install("resolver.tb2.specific.jar");
		final Bundle tb3 = install("resolver.tb3.specific.jar");
		final Bundle tb4 = install("resolver.tb4.jar");
		final Bundle tb5 = install("resolver.tb5.jar");

		Collection testBundles = Arrays.asList(new Bundle[]{tb1v100, tb1v110, tb2, tb3, tb4, tb5});
		final Filter testCapabilities = createFilter(
				"(|" + 
				  "(osgi.package=org.osgi.test.cases.framework.resolver.tb1)" +
				  "(osgi.bundle=org.osgi.test.cases.framework.resolver.tb1)" +
				  "(test=aName)" +
				")");

		final boolean[] called = new boolean[] {false, false, false, false};
		final AssertionFailedError[] errors = new AssertionFailedError[5];
		registerHook(new ResolverHook() {
			
			public void filterSingletonCollisions(Capability arg0, Collection arg1) {
			}
			public void filterResolvable(Collection arg0) {
			}
			public void filterMatches(BundleRevision arg0, Collection arg1) {
				for (Iterator capabilities = arg1.iterator(); capabilities.hasNext();) {
					Capability capability = (Capability) capabilities.next();
					if (!testCapabilities.matchCase(new UnmodifiableDictionary(capability.getAttributes())))
						break;
					synchronized (called) {
						int index = 4;
						if (arg0.getBundle() == tb2)
							index = 0;
						else if (arg0.getBundle() == tb3)
							index = 1;
						else if (arg0.getBundle() == tb4)
							index = 2;
						else if (arg0.getBundle() == tb5)
							index = 3;
						try {
							if (index == 4)
								fail("Wrong bundle as requirer: " + arg0.getBundle());
							called[index] = true;
							assertEquals("Wrong number of capabilities", 1, arg1.size());
							assertEquals("Wrong provider of capability", tb1v110, capability.getProviderRevision().getBundle());
							break;
						} catch (AssertionFailedError e) {
							errors[index] = e;
						}
					}
				}
			}
			public void end() {
			}
			public void begin(Collection triggers) {
			}
		}, 0);
	
		preventReg.unregister();
		assertTrue("Could not resolve test bundles.", frameworkWiring.resolveBundles(testBundles));
		for (int i = 0; i < called.length; i++)
			assertTrue("Not called for: " + i, called[i]);
		for (int i = 0; i < errors.length; i++)
			if (errors[i] != null)
				throw errors[i];
	}

	public void testNestedResolveOperations() {
		// ensure no resolution while we setup the tests
		PreventResolution preventHook = new PreventResolution();
		ServiceRegistration preventReg = registerHook(preventHook, 0);
		Bundle tb6v100 = install("resolver.tb6.v100.jar");
		Bundle tb6v200 = install("resolver.tb6.v200.jar");
		Bundle tb1 = install("resolver.tb1.v100.jar");

		final AssertionFailedError[] error = new AssertionFailedError[1];
		ResolverHook hook1 = new ResolverHook(){
			private void doTest() {
				try {
					Bundle tb2 = install("resolver.tb2.jar");
					try {
						frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb2}));
						fail("Expected to fail nested resolve.");
					} catch (IllegalStateException e) {
						// expected
					}
					try {
						tb2.start();
						fail("Expected failure to start: " + tb2);
					} catch (BundleException e) {
						assertEquals("Wrong exception type", BundleException.RESOLVE_ERROR, e.getType());
						Throwable t = e.getCause();
						assertTrue("Wrong type of exception cause: " + t.getClass().getName(), t instanceof IllegalStateException);
					}
				} catch(AssertionFailedError e) {
					if (error[0] == null)
						error[0] = e;
				}
			}
			public void begin(Collection triggers) {
				doTest();
			}
			public void end() {
				doTest();
			}
			public void filterMatches(BundleRevision arg0, Collection arg1) {
				doTest();
			}
			public void filterResolvable(Collection arg0) {
				doTest();
			}
			public void filterSingletonCollisions(Capability arg0,
					Collection arg1) {
				doTest();
			}
			
		};
		registerHook(hook1, 0);

		// OK now allow resolution for the test
		preventReg.unregister();

		frameworkWiring.resolveBundles(Arrays.asList(new Bundle[]{tb6v100, tb6v200, tb1}));
		if (error[0] != null)
			throw error[0];
	}

	public void testAddRemoveHooks() {
		// ensure no resolution while we setup the tests
		PreventResolution preventHook = new PreventResolution();
		ServiceRegistration preventReg = registerHook(preventHook, 0);
		Collection bundles = new ArrayList();
		bundles.add(install("resolver.tb1.v100.jar"));
		bundles.add(install("resolver.tb2.jar"));
		bundles.add(install("resolver.tb6.v100.jar"));
		bundles.add(install("resolver.tb6.v200.jar"));
		LinkedList callOrderBegin = new LinkedList();
		LinkedList callOrderEnd = new LinkedList();
		TestResolverHook hook1 = new TestResolverHook(new Long(1), null, callOrderBegin, callOrderEnd);
		TestResolverHook hook2 = new TestResolverHook(new Long(2), null, callOrderBegin, callOrderEnd);
		final TestResolverHook hook3 = new TestResolverHook(new Long(3), null, callOrderBegin, callOrderEnd);
		registerHook(hook1, -1);
		final ServiceRegistration hook2Reg = registerHook(hook2, -2);

		registerHook(new ResolverHook(){
			private boolean called = false;
			public void begin(Collection triggers) {
				if (called)
					return;
				called = true;
				hook2Reg.unregister();
				registerHook(hook3, -3);
			}
			public void end() {
			}
			public void filterMatches(BundleRevision arg0, Collection arg1) {
			}
			public void filterResolvable(Collection arg0) {
			}
			public void filterSingletonCollisions(Capability arg0,
					Collection arg1) {
			}
		}, 0);

		// OK now allow resolution for the test
		preventReg.unregister();
		// start a resolve process
		frameworkWiring.resolveBundles(bundles);

		// we expect hook2 to never get called because it is unregistered by hook1
		// we expect hook3 to never get called because it is registered after the resolve process has already started
		assertEquals("Wrong number of start called", 1, callOrderBegin.size());
		assertEquals("Wrong hook.begin called first", hook1.getID(), callOrderBegin.removeFirst());

		assertEquals("Wrong number of end called", 1, callOrderEnd.size());
		assertEquals("Wrong hook.end called first", hook1.getID(), callOrderEnd.removeFirst());
	}

	static class TestResolverHook implements ResolverHook {
		private int beginCalls = 0;
		private int endCalls = 0;
		private AssertionFailedError error = null;
		private final RuntimeException throwException;
		private final List callOrderBegin;
		private final List callOrderEnd;
		private final Long id;
		private final Collection unresolvable;

		
		public TestResolverHook(Long id, RuntimeException throwException, List callOrderBegin, List callOrderEnd) {
			this(id, throwException, callOrderBegin, callOrderEnd, null);
		}

		public TestResolverHook(Long id, RuntimeException throwException, List callOrderBegin, List callOrderEnd, Collection unresolvable) {
			this.id = id;
			this.throwException = throwException;
			this.callOrderBegin = callOrderBegin;
			this.callOrderEnd = callOrderEnd;
			this.unresolvable = unresolvable;
		}
		public void begin(Collection triggers) {
			beginCalls++;
			callOrderBegin.add(id);
			try {
				assertEquals("Begin called too many times.", 1, beginCalls - endCalls);
			} catch (AssertionFailedError e) {
				if (error != null)
					error = e;
			}
			throwException();
		}

		public void end() {
			endCalls++;
			callOrderEnd.add(id);
			try {
				assertEquals("Begin was not called.", 0, beginCalls - endCalls);
			} catch (AssertionFailedError e) {
				if (error != null)
					error = e;
			}
			throwException();
		}

		public void filterMatches(BundleRevision arg0, Collection arg1) {
			try {
				assertEquals("Begin was not called.", 1, beginCalls - endCalls);
			} catch (AssertionFailedError e) {
				if (error != null)
					error = e;
			}
			throwException();
		}

		public void filterResolvable(Collection arg0) {
			try {
				assertEquals("Begin was not called.", 1, beginCalls - endCalls);
				if (unresolvable != null)
					for(Iterator resolvable = arg0.iterator(); resolvable.hasNext();) {
						BundleRevision revision = (BundleRevision) resolvable.next();
						if (unresolvable.contains(revision.getBundle())) {
							arg0.remove(revision);
							try {
								arg0.add(revision);
								fail("Expected failure on add.");
							} catch (UnsupportedOperationException e) {
								//expected 
							}
							try {
								arg0.addAll(Arrays.asList(new Object[]{revision}));
								fail("Expected failure on addAll.");
							} catch (UnsupportedOperationException e) {
								// expected
							}
						}
					}
			} catch (AssertionFailedError e) {
				if (error != null)
					error = e;
			}
			throwException();
		}

		public void filterSingletonCollisions(Capability arg0, Collection arg1) {
			try {
				assertEquals("Begin was not called.", 1, beginCalls - endCalls);
			} catch (AssertionFailedError e) {
				if (error != null)
					error = e;
			}
			throwException();
		}

		public void clear() {
			beginCalls = 0;
			endCalls = 0;
			error = null;
		}

		private void throwException() {
			if (throwException != null)
				throw throwException;
		}

		public int getBeginCalls() {
			return beginCalls;
		}

		public int getEndCalls() {
			return endCalls;
		}

		public AssertionFailedError getError() {
			return error;
		}

		public Long getID() {
			return id;
		}
	}

	static class PreventResolution implements ResolverHook {
		public void begin(Collection triggers) {
		}
		public void end() {
		}
		public void filterMatches(BundleRevision arg0, Collection arg1) {
		}
		public void filterResolvable(Collection arg0) {
			arg0.clear();  // never allow a bundle to resolve
		}
		public void filterSingletonCollisions(Capability arg0,
				Collection arg1) {
		}
	}

	static class TestFilterResolvable implements ResolverHook {
		private final Collection/*<Bundle>*/ unresolvable;
		public TestFilterResolvable(Collection unresolvable) {
			this.unresolvable = unresolvable;
		}
		public void begin(Collection triggers) {
		}
		public void end() {
		}
		public void filterMatches(BundleRevision arg0, Collection arg1) {
		}
		public void filterResolvable(Collection arg0) {
			for(Iterator revisions = arg0.iterator(); revisions.hasNext();) {
				if (unresolvable.contains(((BundleRevision) revisions.next()).getBundle()))
					revisions.remove();				
			}
		}
		public void filterSingletonCollisions(Capability arg0, Collection arg1) {
		}
	}

	static class TestFilterCapabilityHook implements ResolverHook {
		private final Filter filter;
		private AssertionFailedError error = null;
		
		public TestFilterCapabilityHook(Filter filter) {
			this.filter = filter;
		}
		public void begin(Collection triggers) {
		}
		public void end() {
		}

		public void filterMatches(BundleRevision arg0, Collection arg1) {
			for (Iterator capabilities = arg1.iterator(); capabilities.hasNext();) {
				Capability capability = (Capability) capabilities.next();
				if (!filter.matchCase(new UnmodifiableDictionary(capability.getAttributes())))
					continue;
				capabilities.remove();
				try {
					try {
						arg1.add(capability);
						fail("Expected failure on add.");
					} catch (UnsupportedOperationException e) {
						//expected 
					}
					try {
						arg1.addAll(Arrays.asList(new Object[]{capability}));
						fail("Expected failure on addAll.");
					} catch (UnsupportedOperationException e) {
						// expected
					}
				} catch (AssertionFailedError e) {
					if (error != null)
						error = e;
				}
			}
		}

		public void filterResolvable(Collection arg0) {
		}
		public void filterSingletonCollisions(Capability arg0,
				Collection arg1) {
		}

		public AssertionFailedError getError() {
			return error;
		}
	}

	static class TestFilterSingletonCollisions implements ResolverHook {
		private final Map collisions;
		private AssertionFailedError error = null;

		public TestFilterSingletonCollisions(Map collisions) {
			this.collisions = collisions;
		}

		public void begin(Collection triggers) {
		}

		public void end() {
		}

		public void filterMatches(BundleRevision arg0, Collection arg1) {
		}

		public void filterResolvable(Collection arg0) {
		}

		public void filterSingletonCollisions(Capability arg0, Collection arg1) {
			Collection issolatedFromBundle = (Collection) collisions.get(arg0.getProviderRevision().getBundle());
			if (issolatedFromBundle == null)
				return;
			try {
				for (Iterator iCollisions = arg1.iterator(); iCollisions.hasNext();) {
					Capability collision = (Capability) iCollisions.next();
					if (issolatedFromBundle.contains(collision.getProviderRevision().getBundle())) {
						iCollisions.remove();
						try {
							arg1.add(collision);
							fail("Expected failure on add.");
						} catch (UnsupportedOperationException e) {
							//expected 
						}
						try {
							arg1.addAll(Arrays.asList(new Object[]{collision}));
							fail("Expected failure on addAll.");
						} catch (UnsupportedOperationException e) {
							// expected
						}
					}
				}
			} catch (AssertionFailedError e) {
				if (error != null)
					error = e;
			}
		}
		public AssertionFailedError getError() {
			return error;
		}
	}

	static class UnmodifiableDictionary extends Dictionary {
		private final Map	wrapped;
		UnmodifiableDictionary(Map wrapped) {
			this.wrapped = wrapped;
		}
		public Enumeration elements() {
			return Collections.enumeration(wrapped.values());
		}
		public Object get(Object key) {
			return wrapped.get(key);
		}
		public boolean isEmpty() {
			return wrapped.isEmpty();
		}
		public Enumeration keys() {
			return Collections.enumeration(wrapped.keySet());
		}
		public Object put(Object key, Object value) {
			throw new UnsupportedOperationException();
		}
		public Object remove(Object key) {
			throw new UnsupportedOperationException();
		}
		public int size() {
			return wrapped.size();
		}
	}
}

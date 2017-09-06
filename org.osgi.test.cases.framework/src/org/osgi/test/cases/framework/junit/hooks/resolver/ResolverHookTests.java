/*
 * Copyright (c) OSGi Alliance (2010, 2017). All Rights Reserved.
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.resolver.ResolverHook;
import org.osgi.framework.hooks.resolver.ResolverHookFactory;
import org.osgi.framework.namespace.PackageNamespace;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.test.support.wiring.Wiring;

import junit.framework.AssertionFailedError;

public class ResolverHookTests extends OSGiTestCase {
	private final List<Bundle> bundles = new ArrayList<Bundle>();
	private final List<ServiceRegistration<?>> registrations = new ArrayList<ServiceRegistration<?>>();
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
		if (bundle == null)
			fail("Failed to install bundle: " + bundle);
		if (!bundles.contains(result))
			bundles.add(result);
		return result;
	}

	protected void setUp() throws Exception {
		registrations.clear();
		bundles.clear();
		frameworkWiring = getContext().getBundle(0).adapt(FrameworkWiring.class);
	}

	protected void tearDown() throws Exception {
		for (Iterator<ServiceRegistration<?>> iRegistrations = registrations.iterator(); iRegistrations.hasNext();)
			try {
				iRegistrations.next().unregister();
			} catch (IllegalStateException e) {
				// probably unregistered during test
			}
		registrations.clear();
		for (Iterator<Bundle> iBundles = bundles.iterator(); iBundles.hasNext();) {
			Bundle bundle = iBundles.next();
			try {
				if (!(bundle.getState() == Bundle.UNINSTALLED))
					bundle.uninstall();
			} catch (BundleException e) {
				// nothing
			}
		}
		refreshBundles(bundles);
		bundles.clear();
	}

	private void refreshBundles(List<Bundle> b) {
		Wiring.synchronousRefreshBundles(getContext(), b);
	}

	ServiceRegistration<ResolverHookFactory> registerHook(
			ResolverHookFactory hook, int ranking) {
		Dictionary<String, Object> props = new Hashtable<String, Object>();
		props.put(Constants.SERVICE_RANKING, Integer.valueOf(ranking));
		ServiceRegistration<ResolverHookFactory> reg = getContext().registerService(ResolverHookFactory.class, hook, props);
		registrations.add(reg);
		return reg;
	}

	private void setTestProperty(String propName, String propValue) {
		Dictionary<String, String> props = new Hashtable<String, String>();
		props.put(propName, propValue);
		ServiceRegistration< ? > reg = getContext().registerService(
				Object.class, new Object(), props);
		registrations.add(reg);
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
		LinkedList<Long> beginOrder = new LinkedList<Long>();
		LinkedList<Long> endOrder = new LinkedList<Long>();
		TestResolverHook hook1 = new TestResolverHook(Long.valueOf(1), null, beginOrder, endOrder);
		TestResolverHook hook2 = new TestResolverHook(Long.valueOf(2), null, beginOrder, endOrder);
		TestResolverHook hook3 = new TestResolverHook(Long.valueOf(3), null, beginOrder, endOrder);
		TestResolverHook hook4 = new TestResolverHook(Long.valueOf(4), null, beginOrder, endOrder, null, true, null, null);
		TestResolverHook hook5 = new TestResolverHook(Long.valueOf(5), null, beginOrder, endOrder);
		registerHook(hook1, 0);
		registerHook(hook2, Integer.MAX_VALUE);
		registerHook(hook3, Integer.MAX_VALUE);
		registerHook(hook4, Integer.MAX_VALUE);
		registerHook(hook5, Integer.MAX_VALUE);
		install("resolver.tb1.v100.jar");
		Bundle tb2 = install("resolver.tb2.jar");
		try {
			tb2.start();
		} catch (BundleException e) {
			fail("failed to start tb2.", e);
		}

		assertEquals("Wrong number of begin called", 5, beginOrder.size());
		assertEquals("Wrong hook.begin called first", hook2.getID(), beginOrder.removeFirst());
		assertEquals("Wrong hook.begin called second", hook3.getID(), beginOrder.removeFirst());
		assertEquals("Wrong hook.begin called thrid", hook4.getID(), beginOrder.removeFirst());
		assertEquals("Wrong hook.begin called forth", hook5.getID(), beginOrder.removeFirst());
		assertEquals("Wrong hook.begin called fifth", hook1.getID(), beginOrder.removeFirst());

		assertEquals("Wrong number of end called", 4, endOrder.size());
		assertEquals("Wrong hook.end called first", hook2.getID(), endOrder.removeFirst());
		assertEquals("Wrong hook.end called second", hook3.getID(), endOrder.removeFirst());
		assertEquals("Wrong hook.end called third", hook5.getID(), endOrder.removeFirst());
		assertEquals("Wrong hook.end called forth", hook1.getID(), endOrder.removeFirst());

		if (hook1.getError() != null)
			throw hook1.getError();
		if (hook2.getError() != null)
			throw hook2.getError();
		if (hook3.getError() != null)
			throw hook3.getError();
		if (hook4.getError() != null)
			throw hook4.getError();
		if (hook5.getError() != null)
			throw hook5.getError();
	}

	public void testHookErrors01() {
		// install and start bundle first to test refresh properly
		install("resolver.tb1.v100.jar");
		Bundle tb2 = install("resolver.tb2.jar");
		try {
			tb2.start();
		} catch (BundleException e) {
			fail("Failed to start bundle.", e);
		}
		RuntimeException error1 = new RuntimeException("Test hook error 1");
		LinkedList<Long> beginOrder = new LinkedList<Long>();
		LinkedList<Long> endOrder = new LinkedList<Long>();
		TestResolverHook hook1 = new TestResolverHook(Long.valueOf(1), null, beginOrder, endOrder);
		TestResolverHook hook2 = new TestResolverHook(Long.valueOf(2), error1, beginOrder, endOrder);
		TestResolverHook hook3 = new TestResolverHook(Long.valueOf(3), null, beginOrder, endOrder);
		registerHook(hook1, 30);
		registerHook(hook2, 20);
		registerHook(hook3, 10);

		refreshBundles(Arrays.asList(tb2));
		checkTestResolverHookError(tb2, beginOrder, endOrder, hook1, hook2, hook3);

		try {
			tb2.start();
			fail("Expected a failure.");
		} catch (BundleException e) {
			assertEquals("Wrong BundleException type found.", BundleException.REJECTED_BY_HOOK, e.getType());
			boolean found = false;
			for (Throwable t = e.getCause(); t != null; t = t.getCause()) {
				if (t == error1) {
					found = true;
					break;
				}
			}
			if (!found)
				fail("Did not find the expected cause.", e);
		}

		checkTestResolverHookError(tb2, beginOrder, endOrder, hook1, hook2, hook3);

		tb2.getResource("justAtest");
		checkTestResolverHookError(tb2, beginOrder, endOrder, hook1, hook2, hook3);
		try {
			tb2.getResources("justAtest");
		} catch (IOException e) {
			fail("Unexpected exception calling getResources", e);
		}
		checkTestResolverHookError(tb2, beginOrder, endOrder, hook1, hook2, hook3);

		try {
			tb2.loadClass(tb2.getHeaders("").get(Constants.BUNDLE_ACTIVATOR));
			fail("Expected class not found.");
		} catch (ClassNotFoundException e1) {
			// expected;
		}
		checkTestResolverHookError(tb2, beginOrder, endOrder, hook1, hook2, hook3);

		tb2.findEntries("justAtest", "file", false);
		checkTestResolverHookError(tb2, beginOrder, endOrder, hook1, hook2, hook3);

		assertFalse("Should not resolve", frameworkWiring.resolveBundles(Arrays.asList(tb2)));
		checkTestResolverHookError(tb2, beginOrder, endOrder, hook1, hook2, hook3);

		// insert a hook that throws an error on begin()
		RuntimeException error2 = new RuntimeException("Test factory error 2");
		TestResolverHook hook4 = new TestResolverHook(Long.valueOf(4), null, beginOrder, endOrder, null, false, error2, null);
		ServiceRegistration<ResolverHookFactory> hook4Reg = registerHook(hook4, 25);

		try {
			tb2.start();
			fail("Expected a failure.");
		} catch (BundleException e) {
			assertEquals("Wrong BundleException type found.", BundleException.REJECTED_BY_HOOK, e.getType());
			boolean found = false;
			for (Throwable t = e.getCause(); t != null; t = t.getCause()) {
				if (t == error2) {
					found = true;
					break;
				}
			}
			if (!found)
				fail("Did not find the expected cause.", e);
		}

		assertEquals("Wrong number of begin called", 2, beginOrder.size());
		assertEquals("Wrong hook.begin called first", hook1.getID(), beginOrder.removeFirst());
		assertEquals("Wrong hook.begin called second", hook4.getID(), beginOrder.removeFirst());

		assertEquals("Wrong number of end called", 1, endOrder.size());
		assertEquals("Wrong hook.end called first", hook1.getID(), endOrder.removeFirst());

		if (hook1.getError() != null)
			throw hook1.getError();
		if (hook2.getError() != null)
			throw hook2.getError();
		if (hook3.getError() != null)
			throw hook3.getError();
		if (hook4.getError() != null)
			throw hook4.getError();

		// unregister hook4
		hook4Reg.unregister();

		// insert a hook that throws an error on end()
		RuntimeException error3 = new RuntimeException("Test end error");
		TestResolverHook hook5 = new TestResolverHook(Long.valueOf(5), null, beginOrder, endOrder, null, false, null, error3);
		@SuppressWarnings("unused")
		ServiceRegistration<ResolverHookFactory> hook5Reg = registerHook(hook5, 50);

		try {
			tb2.start();
			fail("Expected a failure.");
		} catch (BundleException e) {
			assertEquals("Wrong BundleException type found.", BundleException.REJECTED_BY_HOOK, e.getType());
			boolean found = false;
			for (Throwable t = e.getCause(); t != null; t = t.getCause()) {
				if (t == error3) {
					found = true;
					break;
				}
			}
			if (!found)
				fail("Did not find the expected cause.", e);
		}

		checkTestResolverHookError(tb2, beginOrder, endOrder, hook5, hook1, hook2, hook3);
	}

	public void testHookErrors02() {
		// install and start bundle first to test refresh properly
		install("resolver.tb1.v100.jar");
		Bundle tb2 = install("resolver.tb2.jar");
		try {
			tb2.start();
		} catch (BundleException e) {
			fail("Failed to start bundle.", e);
		}

		// prevent resolution to get bundle back to unresolved
		PreventResolution preventHook = new PreventResolution();
		ServiceRegistration<ResolverHookFactory> preventReg = registerHook(preventHook, 0);
		refreshBundles(Arrays.asList(tb2));
		preventReg.unregister();

		LinkedList<Long> beginOrder = new LinkedList<Long>();
		LinkedList<Long> endOrder = new LinkedList<Long>();
		TestResolverHook hook1 = new TestResolverHook(Long.valueOf(1), null, beginOrder, endOrder);
		TestResolverHook hook2 = new TestResolverHook(Long.valueOf(2), null, beginOrder, endOrder);
		TestResolverHook hook3 = new TestResolverHook(Long.valueOf(3), null, beginOrder, endOrder);
		
		final ServiceRegistration<ResolverHookFactory> hook1Reg = registerHook(hook1, 30);
		registerHook(hook2, 20);
		registerHook(hook3, 10);

		// insert a hook that will unregister hook1 before end can be called on hook1
		ResolverHookFactory unregisterOnEnd = new ResolverHookFactory() {
			public ResolverHook begin(Collection<BundleRevision> triggers) {
				return new ResolverHook() {
					
					public void filterSingletonCollisions(BundleCapability singleton,
							Collection<BundleCapability> collisionCandidates) {
					}
					
					public void filterResolvable(Collection<BundleRevision> candidates) {
					}
					
					public void filterMatches(BundleRequirement requirement,
							Collection<BundleCapability> candidates) {
					}
					
					public void end() {
						hook1Reg.unregister();
					}
				};
			}
		};
		registerHook(unregisterOnEnd, 40);

		try {
			tb2.start();
			fail("Expected a failure.");
		} catch (BundleException e) {
			assertEquals("Wrong BundleException type found.", BundleException.REJECTED_BY_HOOK, e.getType());
		}
		assertEquals("Wrong number of begin called", 3, beginOrder.size());
		assertEquals("Wrong hook.begin called first", hook1.getID(), beginOrder.removeFirst());
		assertEquals("Wrong hook.begin called second", hook2.getID(), beginOrder.removeFirst());
		assertEquals("Wrong hook.begin called third", hook3.getID(), beginOrder.removeFirst());

		assertEquals("Wrong number of end called", 2, endOrder.size());
		assertEquals("Wrong hook.end called first", hook2.getID(), endOrder.removeFirst());
		assertEquals("Wrong hook.end called second", hook3.getID(), endOrder.removeFirst());

		if (hook1.getError() != null)
			throw hook1.getError();
		if (hook2.getError() != null)
			throw hook2.getError();
		if (hook3.getError() != null)
			throw hook3.getError();
	}

	private void checkTestResolverHookError(Bundle testBundle, LinkedList<Long> beginOrder, LinkedList<Long> endOrder, TestResolverHook... hooks) {
		assertEquals("Wrong state for test bundle.", Bundle.INSTALLED, testBundle.getState());
		assertEquals("Wrong number of begin called", hooks.length, beginOrder.size());
		assertEquals("Wrong number of end called", hooks.length, endOrder.size());
		for (int i = 0; i < hooks.length; i++) {
			assertEquals("Wrong hook.begin called: " + i, hooks[i].getID(), beginOrder.removeFirst());
	
			assertEquals("Wrong hook.end called: " + i, hooks[i].getID(), endOrder.removeFirst());
			if (hooks[i].getError() != null)
				throw hooks[i].getError();
		}
	}

	public void testBeginTriggers() {
		// test the begin triggers
		PreventResolution preventHook = new PreventResolution();
		ServiceRegistration<ResolverHookFactory> preventReg = registerHook(preventHook, 0);

		Bundle tb1v100 = install("resolver.tb1.v100.jar");
		Bundle tb1v110 = install("resolver.tb1.v110.jar");
		Bundle tb2 = install("resolver.tb2.specific.jar");
		Bundle tb3 = install("resolver.tb3.specific.jar");
		Bundle tb4 = install("resolver.tb4.jar");
		Bundle tb5 = install("resolver.tb5.jar");
		Bundle tb7 = install("resolver.tb7.jar");

		BundleRevision tb1v100Revision = tb1v100.adapt(BundleRevision.class);
		BundleRevision tb1v110Revision = tb1v110.adapt(BundleRevision.class);
		BundleRevision tb2Revision = tb2.adapt(BundleRevision.class);
		BundleRevision tb3Revision = tb3.adapt(BundleRevision.class);
		BundleRevision tb4Revision = tb4.adapt(BundleRevision.class);
		BundleRevision tb5Revision = tb5.adapt(BundleRevision.class);
		BundleRevision tb7Revision = tb7.adapt(BundleRevision.class);

		List<Bundle> testBundles = Arrays.asList(tb1v100, tb1v110, tb2, tb3, tb4, tb5, tb7);
		List<BundleRevision> testRevisions = Arrays.asList(tb1v100Revision, tb1v110Revision, tb2Revision, tb3Revision, tb4Revision, tb5Revision, tb7Revision);

		LinkedList<Long> beginOrder = new LinkedList<Long>();
		LinkedList<Long> endOrder = new LinkedList<Long>();
		TestResolverHook testHook = new TestResolverHook(Long.valueOf(1), null, beginOrder, endOrder);
		registerHook(testHook, 0);
		preventReg.unregister();

		// test start
		try {
			tb2.start();
		} catch (BundleException e) {
			fail("failed to start bundle: " + tb2, e);
		}
		assertNotNull("revision is null!", tb2Revision);
		Set<BundleRevision> triggers = testHook.getAllTriggers();
		assertEquals("Wrong number of triggers", 1, triggers.size());
		assertTrue("Wrong bundle included in triggers", triggers.contains(tb2Revision));

		preventReg = registerHook(preventHook, 0);
		refreshBundles(testBundles);
		preventReg.unregister();
		testHook.clear();

		// test loadClass
		try {
			tb2.loadClass(tb2.getHeaders("").get(Constants.BUNDLE_ACTIVATOR));
		} catch (ClassNotFoundException e1) {
			fail("failed to find activator");
		}
		triggers = testHook.getAllTriggers();
		assertEquals("Wrong number of triggers", 1, triggers.size());
		assertTrue("Wrong bundle included in triggers", triggers.contains(tb2Revision));

		preventReg = registerHook(preventHook, 0);
		refreshBundles(testBundles);
		preventReg.unregister();
		testHook.clear();

		// test getResource
		URL resoureTest = tb3.getResource("justAtest");
		assertNull("URL is not null!", resoureTest);
		triggers = testHook.getAllTriggers();
		assertEquals("Wrong number of triggers", 1, triggers.size());
		assertTrue("Wrong bundle included in triggers", triggers.contains(tb3Revision));

		preventReg = registerHook(preventHook, 0);
		refreshBundles(testBundles);
		preventReg.unregister();
		testHook.clear();

		// test getResources
		Enumeration<URL> resouresTest = null;
		try {
			resouresTest = tb5.getResources("justAtest");
		} catch (IOException e) {
			fail("Failed to getResources", e);
		}
		assertNull("resources is not null!", resouresTest);
		triggers = testHook.getAllTriggers();
		assertEquals("Wrong number of triggers", 1, triggers.size());
		assertTrue("Wrong bundle included in triggers", triggers.contains(tb5Revision));

		preventReg = registerHook(preventHook, 0);
		refreshBundles(testBundles);
		preventReg.unregister();
		testHook.clear();

		// test findEntries
		Enumeration<URL> findTest = tb3.findEntries("justAtest", "path", false);
		assertNull("Enumeration is not null!", findTest);
		triggers = testHook.getAllTriggers();
		assertEquals("Wrong number of triggers", 1, triggers.size());
		assertTrue("Wrong bundle included in triggers", triggers.contains(tb3Revision));

		preventReg = registerHook(preventHook, 0);
		refreshBundles(testBundles);
		preventReg.unregister();
		testHook.clear();

		// test resolveBundles
		assertTrue("Failed to resolve test bundles", frameworkWiring.resolveBundles(testBundles));
		triggers = testHook.getAllTriggers();
		assertEquals("Wrong number of triggers", 7, triggers.size());
		assertTrue("Wrong bundle included in triggers", triggers.containsAll(testRevisions));

		preventReg = registerHook(preventHook, 0);
		refreshBundles(testBundles);
		preventReg.unregister();
		assertTrue("Failed to resolve test bundles", frameworkWiring.resolveBundles(testBundles));
		testHook.clear();

		// test dynamic import
		try {
			tb7.start();
		} catch (BundleException e) {
			fail("Failed to start bundle: " + tb7, e);
		}
		triggers = testHook.getAllTriggers();
		assertEquals("Wrong number of triggers", 1, triggers.size());
		assertTrue("Wrong bundle included in triggers", triggers.contains(tb7Revision));

		// test refresh
		try {
			// start tb2 to get a second bundle started
			tb2.start();
		} catch (BundleException e) {
			fail("failed to start bundle: " + tb2, e);
		}
		testHook.clear();
		refreshBundles(testBundles);
		// triggers should only contain tb7 and tb2 because they are the only ones active
		triggers = testHook.getAllTriggers();
		assertEquals("Wrong number of triggers", 2, triggers.size());
		assertTrue("Wrong bundle included in triggers", triggers.contains(tb2Revision));
		assertTrue("Wrong bundle included in triggers", triggers.contains(tb7Revision));

		// test resolveBundles with an uninstall bundle in the list
		try {
			tb5.uninstall();
		} catch (BundleException e) {
			fail("Failed to uninstall bundle: " + tb5);
		}
		preventReg = registerHook(preventHook, 0);
		refreshBundles(testBundles);
		preventReg.unregister();
		testHook.clear();

		assertFalse("Should fail to resolve test bundles", frameworkWiring.resolveBundles(testBundles));
		triggers = testHook.getAllTriggers();
		assertEquals("Wrong number of triggers", 6, triggers.size());
		assertTrue("Uninstalled bundle is included: " + tb5Revision, !triggers.contains(tb5Revision));
	}

	public void testFilterResolvable01() {
		final AssertionFailedError[] error = new AssertionFailedError[1];
		ResolverHookFactory hook1 = new ResolverHookFactory(){
			public ResolverHook begin(Collection<BundleRevision> triggers) {
				return new ResolverHook() {
					public void end() {
					}
					public void filterMatches(BundleRequirement arg0, Collection<BundleCapability> arg1) {
					}
					public void filterResolvable(Collection<BundleRevision> arg0) {
						try {
								for(Iterator<BundleRevision> resolvable = arg0.iterator(); resolvable.hasNext();) {
									BundleRevision revision = resolvable.next();
									if ("org.osgi.test.cases.framework.resolver.tb1".equals(revision.getSymbolicName())) {
										resolvable.remove();
										try {
											arg0.add(revision);
											fail("Expected failure on add.");
										} catch (UnsupportedOperationException e) {
											//expected
										}
										try {
											arg0.addAll(Arrays.asList(revision));
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
					public void filterSingletonCollisions(BundleCapability arg0,
							Collection<BundleCapability> arg1) {
					}

				};
			}
		};
		ServiceRegistration<ResolverHookFactory> reg = registerHook(hook1, 0);
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
		ResolverHookFactory hook1 = new ResolverHookFactory(){
			public ResolverHook begin(Collection<BundleRevision> triggers) {
				return new ResolverHook() {
					public void end() {
					}
					public void filterMatches(BundleRequirement arg0, Collection<BundleCapability> arg1) {
						String symbolicName = "org.osgi.test.cases.framework.resolver.tb2";
						String packageName = "org.osgi.test.cases.framework.resolver.tb1";
						if (!symbolicName.equals(arg0.getRevision().getSymbolicName()))
							return;
						for (Iterator<BundleCapability> packages = arg1.iterator(); packages.hasNext();) {
							BundleCapability pkg = packages.next();
							if (!PackageNamespace.PACKAGE_NAMESPACE.equals(pkg
									.getNamespace())
									|| !packageName
											.equals(pkg
													.getAttributes()
													.get(PackageNamespace.PACKAGE_NAMESPACE)))
								return;

							packages.remove();
							try {
								arg1.add(pkg);
								fail("Expected failure on add.");
							} catch (UnsupportedOperationException e) {
								//expected
							}
							try {
								List<BundleCapability> testAdd = Arrays.asList(pkg);
								arg1.addAll(testAdd);
								fail("Expected failure on addAll.");
							} catch (UnsupportedOperationException e) {
								// expected
							}
						}
					}
					public void filterResolvable(Collection<BundleRevision> arg0) {
					}
					public void filterSingletonCollisions(BundleCapability arg0,
							Collection<BundleCapability> arg1) {
					}
				};
			}
		};
		ServiceRegistration<ResolverHookFactory> reg = registerHook(hook1, 0);
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
		Filter filterCapabilities1 = createFilter("(&"
				+ "(osgi.wiring.package=org.osgi.test.cases.framework.resolver.tb1)"
				+ "(bundle-symbolic-name=org.osgi.test.cases.framework.resolver.tb1)"
				+ "(bundle-version>=1.1)(!(bundle-version>=1.2))"
				+ "(version>=1.1)(!(version>=1.2))"
				+ ")");
		Filter filterCapabilities2 = createFilter("(&"
				+ "(osgi.wiring.package=org.osgi.test.cases.framework.resolver.tb1)"
				+ "(bundle-symbolic-name=org.osgi.test.cases.framework.resolver.tb1)"
				+ "(bundle-version>=1.0)(!(bundle-version>=1.1))"
				+ "(version>=1.0)(!(version>=1.1))"
				+ ")");
		TestFilterCapabilityHook hook1 = new TestFilterCapabilityHook(filterCapabilities1);
		ServiceRegistration<ResolverHookFactory> reg = registerHook(hook1, 0);

		Bundle tb1v110 = install("resolver.tb1.v110.jar");
		Bundle tb1v100 = install("resolver.tb1.v100.jar");
		assertTrue("Could not resolve tb1 bundles", frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb1v100, tb1v110})));

		setTestProperty(tb1v100.getSymbolicName(), "v110");
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
		Filter filterCapabilities1 = createFilter("(&"
				+ "(osgi.wiring.package=org.osgi.test.cases.framework.resolver.tb1)"
				+ "(version>=1.1)(!(version>=1.2))"
				+ ")");
		Filter filterCapabilities2 = createFilter("(&"
				+ "(osgi.wiring.package=org.osgi.test.cases.framework.resolver.tb1)"
				+ "(version>=1.0)(!(version>=1.1))"
				+ ")");
		TestFilterCapabilityHook hook1 = new TestFilterCapabilityHook(filterCapabilities1);
		ServiceRegistration<ResolverHookFactory> reg = registerHook(hook1, 0);

		Bundle tb1v110 = install("resolver.tb1.v110.jar");
		Bundle tb1v100 = install("resolver.tb1.v100.jar");
		assertTrue("Could not resolve tb1 bundles", frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb1v100, tb1v110})));

		setTestProperty(tb1v100.getSymbolicName(), "v110");
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
		Filter filterCapabilities1 = createFilter("(&"
				+ "(osgi.wiring.bundle=org.osgi.test.cases.framework.resolver.tb1)"
				+ "(bundle-version>=1.1)(!(bundle-version>=1.2))"
				+ ")");
		Filter filterCapabilities2 = createFilter("(&"
				+ "(osgi.wiring.bundle=org.osgi.test.cases.framework.resolver.tb1)"
				+ "(bundle-version>=1.0)(!(bundle-version>=1.1))"
				+ ")");
		TestFilterCapabilityHook hook1 = new TestFilterCapabilityHook(filterCapabilities1);
		ServiceRegistration<ResolverHookFactory> reg = registerHook(hook1, 0);

		Bundle tb1v110 = install("resolver.tb1.v110.jar");
		Bundle tb1v100 = install("resolver.tb1.v100.jar");
		assertTrue("Could not resolve tb1 bundles", frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb1v100, tb1v110})));

		setTestProperty(tb1v100.getSymbolicName(), "v110");
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
		Filter filterCapabilities1 = createFilter("(&"
				+ "(osgi.wiring.host=org.osgi.test.cases.framework.resolver.tb1)"
				+ "(bundle-version>=1.1)(!(bundle-version>=1.2))"
				+ ")");
		Filter filterCapabilities2 = createFilter("(&"
				+ "(osgi.wiring.host=org.osgi.test.cases.framework.resolver.tb1)"
				+ "(bundle-version>=1.0)(!(bundle-version>=1.1))"
				+ ")");
		TestFilterCapabilityHook hook1 = new TestFilterCapabilityHook(filterCapabilities1);
		ServiceRegistration<ResolverHookFactory> reg = registerHook(hook1, 0);

		// must prevent all resolution to avoid testing for dynamic attachment of fragments
		PreventResolution preventHook = new PreventResolution();
		ServiceRegistration<ResolverHookFactory> preventReg = registerHook(preventHook, 0);
		install("resolver.tb1.v110.jar");
		install("resolver.tb1.v100.jar");
		Bundle tb4 = install("resolver.tb4.jar");
		preventReg.unregister();
		assertFalse("Should not be able to resolve fragment tb4", frameworkWiring.resolveBundles(Arrays.asList(new Bundle[]{tb4})));
		if (hook1.getError() != null)
			throw hook1.getError();
		assertEquals("Wrong state for tb4", Bundle.INSTALLED, tb4.getState());
		// unregister the hook; and register one that allows the correct host
		// bundles should resolve now
		reg.unregister();
		TestFilterCapabilityHook hook2 = new TestFilterCapabilityHook(filterCapabilities2);
		registerHook(hook2, 0);

		// refresh all bundles to get both hosts and fragment back to installed state
		preventReg = registerHook(preventHook, 0);
		refreshBundles(bundles);
		preventReg.unregister();

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
		ServiceRegistration<ResolverHookFactory> reg = registerHook(hook1, 0);

		Bundle tb1v110 = install("resolver.tb1.v110.jar");
		Bundle tb1v100 = install("resolver.tb1.v100.jar");
		assertTrue("Could not resolve tb1 bundles", frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb1v100, tb1v110})));

		setTestProperty(tb1v100.getSymbolicName(), "v110");
		Bundle tb5 = install("resolver.tb5.jar");
		assertFalse("Should not be able to resolve bundle tb5", frameworkWiring.resolveBundles(Arrays.asList(new Bundle[]{tb5})));
		if (hook1.getError() != null)
			throw hook1.getError();
		assertEquals("Wrong state for tb5", Bundle.INSTALLED, tb5.getState());
		// unregister the hook; and register one that allows the correct provider
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
		ServiceRegistration<ResolverHookFactory> preventReg = registerHook(preventHook, 0);
		Bundle tb6v100 = install("resolver.tb6.v100.jar");
		Bundle tb6v200 = install("resolver.tb6.v200.jar");
		Bundle tb6v300 = install("resolver.tb6.v300.jar");
		Bundle tb6v400 = install("resolver.tb6.v400.jar");
		List<Bundle> bundles = Arrays.asList(tb6v100, tb6v200, tb6v300, tb6v400);

		// confirm we cannot resolve any of the bundles
		assertFalse("Should not be able to resolve the bundles", frameworkWiring.resolveBundles(bundles));
		assertEquals("Wrong state of bundle tb6v100", Bundle.INSTALLED, tb6v100.getState());
		assertEquals("Wrong state of bundle tb6v200", Bundle.INSTALLED, tb6v200.getState());
		assertEquals("Wrong state of bundle tb6v300", Bundle.INSTALLED, tb6v300.getState());
		assertEquals("Wrong state of bundle tb6v400", Bundle.INSTALLED, tb6v400.getState());

		// create a hook that isolates all test singletons
		Map<Bundle, List<Bundle>> allIsolated = new HashMap<Bundle, List<Bundle>>();
		allIsolated.put(tb6v100, Arrays.asList(new Bundle[]{tb6v200, tb6v300, tb6v400}));
		allIsolated.put(tb6v200, Arrays.asList(new Bundle[]{tb6v100, tb6v300, tb6v400}));
		allIsolated.put(tb6v300, Arrays.asList(new Bundle[]{tb6v100, tb6v200, tb6v400}));
		allIsolated.put(tb6v400, Arrays.asList(new Bundle[]{tb6v100, tb6v200, tb6v300}));
		TestFilterSingletonCollisions allIsolatedHook = new TestFilterSingletonCollisions(allIsolated);
		ServiceRegistration<ResolverHookFactory> allIsolatedReg = registerHook(allIsolatedHook, 0);
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
		Map<Bundle, List<Bundle>> isolate12From34 = new HashMap<Bundle, List<Bundle>>();
		isolate12From34.put(tb6v100, Arrays.asList(new Bundle[] {tb6v300, tb6v400}));
		isolate12From34.put(tb6v200, Arrays.asList(new Bundle[] {tb6v300, tb6v400}));
		isolate12From34.put(tb6v300, Arrays.asList(new Bundle[] {tb6v100, tb6v200}));
		isolate12From34.put(tb6v400, Arrays.asList(new Bundle[] {tb6v100, tb6v200}));
		TestFilterSingletonCollisions isolate12From34Hook = new TestFilterSingletonCollisions(isolate12From34);
		ServiceRegistration<ResolverHookFactory> isolate12From34Reg = registerHook(isolate12From34Hook, 0);
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
		ServiceRegistration<ResolverHookFactory> resolveTwoReg = registerHook(resolveTwoSingletons, 0);
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

		// make all singletons isolated again
		allIsolatedReg = registerHook(allIsolatedHook, 0);
		isolate12From34Reg.unregister();

		// confirm we can resolve all of the singleton bundles, note that 1 and 3 are already resolved
		assertTrue("Should be able to resolve the bundles", frameworkWiring.resolveBundles(bundles));
		assertEquals("Wrong state of bundle tb6v100", Bundle.RESOLVED, tb6v100.getState());
		assertEquals("Wrong state of bundle tb6v200", Bundle.RESOLVED, tb6v200.getState());
		assertEquals("Wrong state of bundle tb6v300", Bundle.RESOLVED, tb6v300.getState());
		assertEquals("Wrong state of bundle tb6v400", Bundle.RESOLVED, tb6v400.getState());

		allIsolatedReg.unregister();
		try {
			tb6v300.uninstall();
			tb6v400.uninstall();
		} catch (BundleException e) {
			fail("Failed to uninstall bundle", e);
		}
		// prevent resolution again
		preventReg = registerHook(preventHook, 0);
		refreshBundles(bundles);

		// isolate version 1 from 2; but not 2 from 1
		Map<Bundle, List<Bundle>> isolate1From2 = new HashMap<Bundle, List<Bundle>>();
		isolate1From2.put(tb6v200, Arrays.asList(new Bundle[] {tb6v100}));
		TestFilterSingletonCollisions isolate1From2Hook = new TestFilterSingletonCollisions(isolate1From2);
		ServiceRegistration<ResolverHookFactory> isolate1From2Reg = registerHook(isolate1From2Hook, 0);

		// Filter version 2; this is to force version 1 to resolve
		TestFilterResolvable resolveOneSingletons = new TestFilterResolvable(Arrays.asList(tb6v200));
		ServiceRegistration<ResolverHookFactory> resolveOneReg = registerHook(resolveOneSingletons, 0);

		// allow resolution again
		preventReg.unregister();
		// resolve version 1
		assertTrue("Should be able to resolve the bundle", frameworkWiring.resolveBundles(Arrays.asList(tb6v100)));

		// allow version 2 to resolve again
		resolveOneReg.unregister();

		assertFalse("Should not be able to resolve the bundle", frameworkWiring.resolveBundles(Arrays.asList(tb6v200)));
		assertEquals("Wrong state of bundle tb6v100", Bundle.RESOLVED, tb6v100.getState());
		assertEquals("Wrong state of bundle tb6v200", Bundle.INSTALLED, tb6v200.getState());

		isolate1From2Reg.unregister();
	}

	public void testFilterMatchesCandidates() {
		// this test ensures that the candidates passed really match
		// a constraint before being passed to the hook
		PreventResolution preventHook = new PreventResolution();
		ServiceRegistration<ResolverHookFactory> preventReg = registerHook(preventHook, 0);

		final Bundle tb1v100 = install("resolver.tb1.v100.jar");
		final Bundle tb1v110 = install("resolver.tb1.v110.jar");
		final Bundle tb2 = install("resolver.tb2.specific.jar");
		final Bundle tb3 = install("resolver.tb3.specific.jar");
		final Bundle tb4 = install("resolver.tb4.jar");
		final Bundle tb5 = install("resolver.tb5.jar");

		Collection<Bundle> testBundles = Arrays.asList(tb1v100, tb1v110, tb2, tb3, tb4, tb5);
		final Filter testCapabilities = createFilter("(|"
				+ "(osgi.wiring.package=org.osgi.test.cases.framework.resolver.tb1)"
				+ "(osgi.wiring.bundle=org.osgi.test.cases.framework.resolver.tb1)"
				+ "(osgi.wiring.host=org.osgi.test.cases.framework.resolver.tb1)"
				+ "(test=aName)"
				+ ")");

		final boolean[] called = new boolean[] {false, false, false, false};
		final AssertionFailedError[] errors = new AssertionFailedError[5];
		registerHook(new ResolverHookFactory() {
			public ResolverHook begin(Collection<BundleRevision> triggers) {
				return new ResolverHook() {
					public void filterSingletonCollisions(BundleCapability arg0, Collection<BundleCapability> arg1) {
					}
					public void filterResolvable(Collection<BundleRevision> arg0) {
					}
					public void filterMatches(BundleRequirement arg0, Collection<BundleCapability> arg1) {
						for (Iterator<BundleCapability> capabilities = arg1.iterator(); capabilities.hasNext();) {
							BundleCapability capability = capabilities.next();
							if (!testCapabilities.matches(capability.getAttributes()))
								break;
							synchronized (called) {
								int index = 4;
								if (arg0.getRevision().getBundle() == tb2)
									index = 0;
								else if (arg0.getRevision().getBundle() == tb3)
									index = 1;
								else if (arg0.getRevision().getBundle() == tb4)
									index = 2;
								else if (arg0.getRevision().getBundle() == tb5)
									index = 3;
								try {
									if (index == 4)
										fail("Wrong bundle as requirer: " + arg0.getRevision().getBundle());
									called[index] = true;
									assertEquals("Wrong number of capabilities", 1, arg1.size());
									assertEquals("Wrong provider of capability", tb1v110, capability.getRevision().getBundle());
									break;
								} catch (AssertionFailedError e) {
									errors[index] = e;
								}
							}
						}
					}
					public void end() {
					}
				};
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

	class TestNestedResolve implements ResolverHookFactory, ResolverHook {
		private AssertionFailedError error = null;
		public ResolverHook begin(Collection<BundleRevision> triggers) {
			doTest();
			return this;
		}
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
				if (error == null)
					error = e;
			}
		}
		public void end() {
			doTest();
		}
		public void filterMatches(BundleRequirement arg0, Collection<BundleCapability> arg1) {
			doTest();
		}
		public void filterResolvable(Collection<BundleRevision> arg0) {
			doTest();
		}
		public void filterSingletonCollisions(BundleCapability arg0,
				Collection<BundleCapability> arg1) {
			doTest();
		}
		public AssertionFailedError getError() {
			return error;
		}

	}

	public void testNestedResolveOperations() {
		// ensure no resolution while we setup the tests
		PreventResolution preventHook = new PreventResolution();
		ServiceRegistration<ResolverHookFactory> preventReg = registerHook(preventHook, 0);
		Bundle tb6v100 = install("resolver.tb6.v100.jar");
		Bundle tb6v200 = install("resolver.tb6.v200.jar");
		Bundle tb1 = install("resolver.tb1.v100.jar");

		TestNestedResolve hook1 = new TestNestedResolve();
		registerHook(hook1, 0);

		// OK now allow resolution for the test
		preventReg.unregister();

		frameworkWiring.resolveBundles(Arrays.asList(new Bundle[]{tb6v100, tb6v200, tb1}));
		if (hook1.getError() != null)
			throw hook1.getError();
	}

	class TestAddRemoveHook implements ResolverHookFactory, ResolverHook {
		private boolean called = false;
		private final ServiceRegistration<ResolverHookFactory> beginHookToUnregister;
		private final ResolverHookFactory hookToRegister;
		private final ServiceRegistration<ResolverHookFactory> filterHookToUnregister;

		public TestAddRemoveHook(ServiceRegistration<ResolverHookFactory> hookToUnregister,
				ServiceRegistration<ResolverHookFactory> filterHookToUnregister, ResolverHookFactory hookToRegister) {
			this.beginHookToUnregister = hookToUnregister;
			this.hookToRegister = hookToRegister;
			this.filterHookToUnregister = filterHookToUnregister;
		}
		public ResolverHook begin(Collection<BundleRevision> triggers) {
			if (called)
				return this;
			called = true;
			beginHookToUnregister.unregister();
			registerHook(hookToRegister, -3);
			return this;
		}
		public void end() {
		}
		public void filterMatches(BundleRequirement arg0, Collection<BundleCapability> arg1) {
		}
		public void filterResolvable(Collection<BundleRevision> arg0) {
			filterHookToUnregister.unregister();
		}
		public void filterSingletonCollisions(BundleCapability arg0,
				Collection<BundleCapability> arg1) {

		}
	}

	public void testAddRemoveHooks() {
		// ensure no resolution while we setup the tests
		PreventResolution preventHook = new PreventResolution();
		ServiceRegistration<ResolverHookFactory> preventReg = registerHook(preventHook, 0);
		Collection<Bundle> bundles = new ArrayList<Bundle>();
		bundles.add(install("resolver.tb1.v100.jar"));
		bundles.add(install("resolver.tb2.jar"));
		bundles.add(install("resolver.tb6.v100.jar"));
		bundles.add(install("resolver.tb6.v200.jar"));
		LinkedList<Long> callOrderBegin = new LinkedList<Long>();
		LinkedList<Long> callOrderEnd = new LinkedList<Long>();
		TestResolverHook hook1 = new TestResolverHook(Long.valueOf(1), null, callOrderBegin, callOrderEnd);
		TestResolverHook hook2 = new TestResolverHook(Long.valueOf(2), null, callOrderBegin, callOrderEnd);
		TestResolverHook hook3 = new TestResolverHook(Long.valueOf(3), null, callOrderBegin, callOrderEnd);
		TestResolverHook hook4 = new TestResolverHook(Long.valueOf(4), null, callOrderBegin, callOrderEnd);
		registerHook(hook1, -1);
		ServiceRegistration<ResolverHookFactory> hook2Reg = registerHook(hook2, -2);
		ServiceRegistration<ResolverHookFactory> hook4Reg = registerHook(hook4, -2);
		registerHook(new TestAddRemoveHook(hook2Reg, hook4Reg, hook3), 0);

		// OK now allow resolution for the test
		preventReg.unregister();
		// start a resolve process
		assertFalse(frameworkWiring.resolveBundles(bundles));

		// we expect hook2 to never get called because it is unregistered by hook1.begin before we call hook2.begin
		// we expect hook3 to never get called because it is registered after the resolve process has already started
		assertEquals("Wrong number of start called", 2, callOrderBegin.size());
		assertEquals("Wrong hook.begin called first", hook1.getID(), callOrderBegin.removeFirst());
		assertEquals("Wrong hook.begin called second", hook4.getID(), callOrderBegin.removeFirst());
	}

	static class TestResolverHook implements ResolverHookFactory {
		int									beginCalls	= 0;
		int endCalls = 0;
		AssertionFailedError error = null;
		private final Set<BundleRevision> allTriggers = new HashSet<BundleRevision>();
		private final RuntimeException throwException;
		private final RuntimeException endException;
		private final List<Long> callOrderBegin;
		final List<Long>					callOrderEnd;
		final Long							id;
		final Collection<Bundle>			unresolvable;
		private final boolean factoryNull;
		private final RuntimeException factoryThrow;

		public TestResolverHook(Long id, RuntimeException throwException, List<Long> callOrderBegin, List<Long> callOrderEnd) {
			this(id, throwException, callOrderBegin, callOrderEnd, null);
		}
		public TestResolverHook(Long id, RuntimeException throwException, List<Long> callOrderBegin, List<Long> callOrderEnd, Collection<Bundle> unresolvable) {
			this(id, throwException, callOrderBegin, callOrderEnd, unresolvable, false, null, null);
		}
		public TestResolverHook(Long id, RuntimeException throwException, List<Long> callOrderBegin, List<Long> callOrderEnd, Collection<Bundle> unresolvable, boolean factoryNull, RuntimeException factoryThrow, RuntimeException endException) {
			this.id = id;
			this.throwException = throwException;
			this.callOrderBegin = callOrderBegin;
			this.callOrderEnd = callOrderEnd;
			this.unresolvable = unresolvable;
			this.factoryNull = factoryNull;
			this.factoryThrow = factoryThrow;
			this.endException = endException;
		}
		public ResolverHook begin(Collection<BundleRevision> triggers) {
			beginCalls++;
			allTriggers.addAll(triggers);
			callOrderBegin.add(id);
			try {
				assertEquals("Begin called too many times.", 1, beginCalls - endCalls);
			} catch (AssertionFailedError e) {
				if (error != null)
					error = e;
			}
			if (factoryNull)
				return null;
			if (factoryThrow != null)
				throw factoryThrow;
			return new TestResolverHookImpl();
		}

		private class TestResolverHookImpl implements ResolverHook {
			public TestResolverHookImpl() {}
			public void end() {
				endCalls++;
				callOrderEnd.add(id);
				try {
					assertEquals("Begin was not called.", 0, beginCalls - endCalls);
				} catch (AssertionFailedError e) {
					if (error != null)
						error = e;
				}
				endException();
			}

			public void filterMatches(BundleRequirement arg0, Collection<BundleCapability> arg1) {
				try {
					assertEquals("Begin was not called.", 1, beginCalls - endCalls);
				} catch (AssertionFailedError e) {
					if (error != null)
						error = e;
				}
				throwException();
			}

			public void filterResolvable(Collection<BundleRevision> arg0) {
				try {
					assertEquals("Begin was not called.", 1, beginCalls - endCalls);
					if (unresolvable != null)
						for(Iterator<BundleRevision> resolvable = arg0.iterator(); resolvable.hasNext();) {
							BundleRevision revision = resolvable.next();
							if (unresolvable.contains(revision.getBundle())) {
								resolvable.remove();
								try {
									arg0.add(revision);
									fail("Expected failure on add.");
								} catch (UnsupportedOperationException e) {
									//expected
								}
								try {
									List<BundleRevision> testAdd = Arrays.asList(revision);
									arg0.addAll(testAdd);
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

			public void filterSingletonCollisions(BundleCapability arg0, Collection<BundleCapability> arg1) {
				try {
					assertEquals("Begin was not called.", 1, beginCalls - endCalls);
				} catch (AssertionFailedError e) {
					if (error != null)
						error = e;
				}
				throwException();
			}
		}

		public void clear() {
			beginCalls = 0;
			endCalls = 0;
			error = null;
			allTriggers.clear();
		}

		void throwException() {
			if (throwException != null)
				throw throwException;
		}

		void endException() {
			if (endException != null)
				throw endException;
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
		public Set<BundleRevision> getAllTriggers() {
			return allTriggers;
		}
	}

	static class PreventResolution implements ResolverHookFactory, ResolverHook {
		public ResolverHook begin(Collection<BundleRevision> triggers) {
			return this;
		}
		public void end() {
		}
		public void filterMatches(BundleRequirement arg0, Collection<BundleCapability> arg1) {
		}
		public void filterResolvable(Collection<BundleRevision> arg0) {
			arg0.clear();  // never allow a bundle to resolve
		}
		public void filterSingletonCollisions(BundleCapability arg0,
				Collection<BundleCapability> arg1) {
		}
	}

	static class TestFilterResolvable implements ResolverHookFactory, ResolverHook {
		private final Collection<Bundle> unresolvable;
		public TestFilterResolvable(Collection<Bundle> unresolvable) {
			this.unresolvable = unresolvable;
		}
		public ResolverHook begin(Collection<BundleRevision> triggers) {
			return this;
		}
		public void end() {
		}
		public void filterMatches(BundleRequirement arg0, Collection<BundleCapability> arg1) {
		}
		public void filterResolvable(Collection<BundleRevision> arg0) {
			for(Iterator<BundleRevision> revisions = arg0.iterator(); revisions.hasNext();) {
				if (unresolvable.contains(revisions.next().getBundle()))
					revisions.remove();
			}
		}
		public void filterSingletonCollisions(BundleCapability arg0, Collection<BundleCapability> arg1) {
		}
	}

	static class TestFilterCapabilityHook implements ResolverHookFactory, ResolverHook {
		private final Filter filter;
		private AssertionFailedError error = null;

		public TestFilterCapabilityHook(Filter filter) {
			this.filter = filter;
		}
		public ResolverHook begin(Collection<BundleRevision> triggers) {
			return this;
		}
		public void end() {
		}

		public void filterMatches(BundleRequirement arg0, Collection<BundleCapability> arg1) {
			for (Iterator<BundleCapability> capabilities = arg1.iterator(); capabilities.hasNext();) {
				BundleCapability capability = capabilities.next();
				if (!filter.matches(capability.getAttributes()))
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
						List<BundleCapability> testAdd = Arrays.asList(capability);
						arg1.addAll(testAdd);
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

		public void filterResolvable(Collection<BundleRevision> arg0) {
		}
		public void filterSingletonCollisions(BundleCapability arg0,
				Collection<BundleCapability> arg1) {
		}

		public AssertionFailedError getError() {
			return error;
		}
	}

	static class TestFilterSingletonCollisions implements ResolverHookFactory, ResolverHook {
		private final Map<Bundle, List<Bundle>> collisions;
		private AssertionFailedError error = null;

		public TestFilterSingletonCollisions(Map<Bundle, List<Bundle>> collisions) {
			this.collisions = collisions;
		}

		public ResolverHook begin(Collection<BundleRevision> triggers) {
			return this;
		}

		public void end() {
		}

		public void filterMatches(BundleRequirement arg0, Collection<BundleCapability> arg1) {
		}

		public void filterResolvable(Collection<BundleRevision> arg0) {
		}

		public void filterSingletonCollisions(BundleCapability arg0, Collection<BundleCapability> arg1) {
			List<Bundle> issolatedFromBundle = collisions.get(arg0.getRevision().getBundle());
			if (issolatedFromBundle == null)
				return;
			try {
				for (Iterator<BundleCapability> iCollisions = arg1.iterator(); iCollisions.hasNext();) {
					BundleCapability collision = iCollisions.next();
					if (issolatedFromBundle.contains(collision.getRevision().getBundle())) {
						iCollisions.remove();
						try {
							arg1.add(collision);
							fail("Expected failure on add.");
						} catch (UnsupportedOperationException e) {
							//expected
						}
						try {
							Collection<BundleCapability> addTest = Arrays.asList(collision);
							arg1.addAll(addTest);
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
}

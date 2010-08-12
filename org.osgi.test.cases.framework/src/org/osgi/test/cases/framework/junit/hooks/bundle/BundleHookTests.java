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

package org.osgi.test.cases.framework.junit.hooks.bundle;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import junit.framework.AssertionFailedError;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.hooks.bundle.EventHook;
import org.osgi.framework.hooks.bundle.FindHook;
import org.osgi.test.support.OSGiTestCase;

public class BundleHookTests extends OSGiTestCase {
	
	private static final int num = 3;
	private Bundle[] testBundles = new Bundle[3];
	private BundleListener[][] asyncListeners = new BundleListener[num][num];
	private SynchronousBundleListener[] syncListeners = new SynchronousBundleListener[num];

	protected void setUp() throws Exception {
		for (int i = 0; i < num; i++) {
			testBundles[i] = install("hooks.tb" + (i + 1) + ".jar");
			testBundles[i].start();
			BundleContext context = testBundles[i].getBundleContext();
			for (int j = 0; j < num; j++) {
				asyncListeners[i][j] = new BundleListener() {
					public void bundleChanged(BundleEvent event) {
						// nothing
					}
				};
				if (i == 0) {
					syncListeners[j] = new SynchronousBundleListener() {
						public void bundleChanged(BundleEvent event) {
							// nothing
						}
					};
				}
				context.addBundleListener(asyncListeners[i][j]);
				if (i == 0)
					context.addBundleListener(syncListeners[j]);
			}
		}

	}

	protected void tearDown() throws Exception {
		for (int i = 0; i < testBundles.length; i++)
			testBundles[i].uninstall();
	}

	public void testFindHook01() {
		final BundleContext testContext = getContext();

		final int originalNumBundles = testContext.getBundles().length;
		final int[] hookCalled = new int[] {0, 0, 0, 0, 0};
		final AssertionFailedError[] hookError = new AssertionFailedError[] {
				null, null, null, null};

		// register services
		Hashtable props = new Hashtable();
		props.put("name", getName());


		// register find hook 1
		props.put(Constants.SERVICE_DESCRIPTION, "min value");
		props.put(Constants.SERVICE_RANKING, new Integer(Integer.MIN_VALUE));
		ServiceRegistration regHook1 = testContext.registerService(
				FindHook.class.getName(), new FindHook() {
					public void find(BundleContext context,
							Collection bundles) {
						try {
							synchronized (hookCalled) {
								hookCalled[++hookCalled[0]] = 1;
							}
							assertEquals("wrong context in hook", testContext,
									context);
							assertEquals("wrong number of bundles in hook", 1,
									bundles.size());
							for (Iterator iter = bundles.iterator(); iter
									.hasNext();) {
								Bundle bundle = (Bundle) iter.next();
								if (bundle.getBundleId() != testBundles[2].getBundleId())
									fail(bundle.getSymbolicName() + " is present");
							}

							try {
								bundles.add(testBundles[0]);
								fail("add to collection succeeded");
							}
							catch (UnsupportedOperationException e) {
								// should get an exception
							}
							catch (Exception e) {
								fail("incorrect exception", e);
							}
							try {
								bundles.addAll(Arrays
										.asList(new Bundle[] {testBundles[0]}));
								fail("addAll to collection succeeded");
							}
							catch (UnsupportedOperationException e) {
								// should get an exception
							}
							catch (Exception e) {
								fail("incorrect exception", e);
							}
						}
						catch (AssertionFailedError a) {
							hookError[0] = a;
							return;
						}
					}
				}, props);

		// register find hook 2
		props.put(Constants.SERVICE_DESCRIPTION, "max value first");
		props.put(Constants.SERVICE_RANKING, new Integer(Integer.MAX_VALUE));
		ServiceRegistration regHook2 = testContext.registerService(
				FindHook.class.getName(), new FindHook() {
					public void find(BundleContext context,
							Collection bundles) {
						try {
							synchronized (hookCalled) {
								hookCalled[++hookCalled[0]] = 2;
							}
							assertEquals("wrong context in hook", testContext,
									context);
							assertEquals("wrong number of bundles in hook", originalNumBundles,
									bundles.size());
							for (Iterator iter = bundles.iterator(); iter
									.hasNext();) {
								Bundle bundle = (Bundle) iter.next();
								if (!Arrays.asList(testBundles).contains(bundle))
									iter.remove();
							}

							try {
								bundles.add(testContext.getBundle());
								fail("add to collection succeeded");
							}
							catch (UnsupportedOperationException e) {
								// should get an exception
							}
							catch (Exception e) {
								fail("incorrect exception", e);
							}
							try {
								bundles.addAll(Arrays
										.asList(new Bundle[] {testContext.getBundle()}));
								fail("addAll to collection succeeded");
							}
							catch (UnsupportedOperationException e) {
								// should get an exception
							}
							catch (Exception e) {
								fail("incorrect exception", e);
							}
						}
						catch (AssertionFailedError a) {
							hookError[1] = a;
							return;
						}
					}
				}, props);

		// register find hook 3
		props.put(Constants.SERVICE_DESCRIPTION, "max value second");
		props.put(Constants.SERVICE_RANKING, new Integer(Integer.MAX_VALUE));
		ServiceRegistration regHook3 = testContext.registerService(
				FindHook.class.getName(), new FindHook() {
					public void find(BundleContext context,
							Collection bundles) {
						try {
							synchronized (hookCalled) {
								hookCalled[++hookCalled[0]] = 3;
							}
							assertEquals("wrong context in hook", testContext,
									context);
							assertEquals("wrong number of bundles in hook", 3,
									bundles.size());
							assertTrue("Wrong collection of bundles: " + bundles.toString(), bundles.containsAll(Arrays.asList(testBundles)));

							try {
								bundles.add(testContext.getBundle());
								fail("add to collection succeeded");
							}
							catch (UnsupportedOperationException e) {
								// should get an exception
							}
							catch (Exception e) {
								fail("incorrect exception", e);
							}
							try {
								bundles.addAll(Arrays
										.asList(new Bundle[] {testContext.getBundle()}));
								fail("addAll to collection succeeded");
							}
							catch (UnsupportedOperationException e) {
								// should get an exception
							}
							catch (Exception e) {
								fail("incorrect exception", e);
							}
						}
						catch (AssertionFailedError a) {
							hookError[2] = a;
							return;
						}
						// throw an exception from the hook to test that the
						// next hooks are called.
						throw new RuntimeException(getName());
					}
				}, props);

		// register find hook 4
		props.put(Constants.SERVICE_DESCRIPTION, "max value third");
		props.put(Constants.SERVICE_RANKING, new Integer(Integer.MAX_VALUE));
		ServiceRegistration regHook4 = testContext.registerService(
				FindHook.class.getName(), new FindHook() {
					public void find(BundleContext context,
							Collection bundles) {
						try {
							synchronized (hookCalled) {
								hookCalled[++hookCalled[0]] = 4;
							}
							assertEquals("wrong context in hook", testContext,
									context);
							assertEquals("wrong number of bundles in hook", 3,
									bundles.size());
							assertTrue("Did not find bundle.", bundles.remove(testBundles[0]));
							assertTrue("Did not find bundle.", bundles.remove(testBundles[1]));							
						}
						catch (AssertionFailedError a) {
							hookError[3] = a;
							return;
						}
					}
				}, props);

		// get reference and hook removes some services
		try {
			Bundle[] bundles = null;
			bundles = testContext.getBundles();
			assertEquals("all hooks not called", 4, hookCalled[0]);
			assertEquals("hook 2 not called first", 2, hookCalled[1]);
			assertEquals("hook 3 not called second", 3, hookCalled[2]);
			assertEquals("hook 4 not called third", 4, hookCalled[3]);
			assertEquals("hook 1 not called fourth ", 1, hookCalled[4]);
			for (int i = 0; i < hookError.length; i++) {
				if (hookError[i] != null) {
					throw hookError[i];
				}
			}
			assertNotNull("bundles is null", bundles);
			assertEquals("Wrong number of bundles", 1, bundles.length);

			// test removed services are not in the result
			List bundleList = Arrays.asList(bundles);
			assertFalse("contains bundle 1", bundleList.contains(testBundles[0]));
			assertFalse("contains bundle 2", bundleList.contains(testBundles[1]));
			assertTrue("missing bundle 3", bundleList.contains(testBundles[2]));

			// remove the hooks
			regHook1.unregister();
			regHook1 = null;
			regHook2.unregister();
			regHook2 = null;
			regHook3.unregister();
			regHook3 = null;
			regHook4.unregister();
			regHook4 = null;

			// get services and make sure none are filtered
			bundles = null;
			hookCalled[0] = 0;

			bundles = testContext.getBundles();
			assertEquals("hooks called", 0, hookCalled[0]);
			assertNotNull("Bundles is null", bundles);
			assertEquals("Wrong number of references", originalNumBundles, bundles.length);
		}
		finally {
			// unregister hook and services
			if (regHook1 != null)
				regHook1.unregister();
			if (regHook2 != null)
				regHook2.unregister();
			if (regHook3 != null)
				regHook3.unregister();
			if (regHook4 != null)
				regHook4.unregister();
		}
	}

	public void testFindHook02() {
		final BundleContext testContext = getContext();
		final Bundle[] originalBundles = testContext.getBundles();
		final int originalNumBundles = originalBundles.length;

		final AssertionFailedError[] factoryError = new AssertionFailedError[] {null};
		final boolean[] factoryCalled = new boolean[] {false, false};
		final boolean[] hookCalled = new boolean[] {false};
		final FindHook findHook1 = new FindHook() {
			public void find(BundleContext context, Collection bundles) {
				synchronized (hookCalled) {
					hookCalled[0] = true;
				}
			}
		};
		Hashtable props = new Hashtable();
		props.put("name", getName());
		props.put(Constants.SERVICE_DESCRIPTION, "find hook 1");
		synchronized (factoryCalled) {
			factoryCalled[0] = false;
			factoryCalled[1] = false;
		}
		synchronized (factoryError) {
			factoryError[0] = null;
		}
		ServiceRegistration regHook1 = testContext.registerService(
				FindHook.class.getName(), new ServiceFactory() {

					public Object getService(Bundle bundle,
							ServiceRegistration registration) {
						try {
							synchronized (factoryCalled) {
								factoryCalled[0] = true;
							}
							assertNotNull("using bundle null", bundle);
							ServiceReference reference = registration
									.getReference();
							Bundle[] users = reference.getUsingBundles();
							assertNotNull("service not used by a bundle", users);
							List userList = Arrays.asList(users);
							assertTrue("missing using bundle", userList
									.contains(bundle));
						}
						catch (AssertionFailedError a) {
							synchronized (factoryError) {
								factoryError[0] = a;
							}
						}
						return findHook1;
					}

					public void ungetService(Bundle bundle,
							ServiceRegistration registration, Object service) {
						try {
							synchronized (factoryCalled) {
								factoryCalled[1] = true;
							}
							assertNotNull("using bundle null", bundle);
							assertEquals("wrong service", findHook1, service);
							ServiceReference reference = registration
									.getReference();
							Bundle[] users = reference.getUsingBundles();
							assertNotNull("service not used by a bundle", users);
							List userList = Arrays.asList(users);
							assertTrue("missing using bundle", userList
									.contains(bundle));
						}
						catch (AssertionFailedError a) {
							synchronized (factoryError) {
								factoryError[0] = a;
							}
						}
					}
				}, props);

		try {
			synchronized (hookCalled) {
				hookCalled[0] = false;
			}
			Bundle[] bundles = testContext.getBundles();
			synchronized (factoryError) {
				if (factoryError[0] != null) {
					throw factoryError[0];
				}
			}
			synchronized (factoryCalled) {
				assertTrue("factory getService not called", factoryCalled[0]);
			}
			synchronized (hookCalled) {
				assertTrue("hook not called", hookCalled[0]);
			}
			assertNotNull("bundles is null", bundles);
			assertEquals("Wrong number of references", originalNumBundles, bundles.length);

			List bundleList = Arrays.asList(bundles);
			assertTrue("missing service 1", bundleList.containsAll(Arrays.asList(originalBundles)));

			regHook1.unregister();
			regHook1 = null;

			synchronized (factoryError) {
				if (factoryError[0] != null) {
					throw factoryError[0];
				}
			}
			synchronized (factoryCalled) {
				assertTrue("factory ungetService not called", factoryCalled[1]);
			}
		}
		finally {
			// unregister hook and services
			if (regHook1 != null)
				regHook1.unregister();
		}
	}


	public void testEventHook01() {
		final BundleContext testContext = getContext();

		final Integer[] hookIDs = new Integer[] {new Integer(1), new Integer(2), new Integer(3), new Integer(4)};
		final LinkedList hookCalled = new LinkedList();
		final AssertionFailedError[] hookError = new AssertionFailedError[] {
				null, null, null, null};
		final boolean[] filterEvent = new boolean[] {false};
		final LinkedList syncEvents = new LinkedList();
		final LinkedList asyncEvents = new LinkedList();

		final SynchronousBundleListener sbl = new SynchronousBundleListener() {
			public void bundleChanged(BundleEvent event) {
				synchronized (syncEvents) {
					syncEvents.add(event);
				}
			}
		};
		final BundleListener bl = new BundleListener() {
			public void bundleChanged(BundleEvent event) {
				synchronized (asyncEvents) {
					asyncEvents.add(event);
				}
			}
		};

		// register services
		Hashtable props = new Hashtable();
		props.put("name", getName());


		// register event hook 0
		props.put(Constants.SERVICE_DESCRIPTION, "min value");
		props.put(Constants.SERVICE_RANKING, new Integer(Integer.MIN_VALUE));
		ServiceRegistration regHook0 = testContext.registerService(
				EventHook.class.getName(), new EventHook() {
					public void event(BundleEvent event,
							Collection contexts) {
						try {
							synchronized (hookCalled) {
								if (hookCalled.size() == 0 || hookCalled.getLast() != hookIDs[0])
									hookCalled.add(hookIDs[0]);
							}

							if (filterEvent[0])
								assertTrue("Should not send event to test listener.", !contexts.contains(testContext));

							try {
								contexts.add(testContext);
								fail("add to collection succeeded");
							}
							catch (UnsupportedOperationException e) {
								// should get an exception
							}
							catch (Exception e) {
								fail("incorrect exception", e);
							}
							try {
								contexts.addAll(Arrays
										.asList(new BundleContext[] {testContext}));
								fail("addAll to collection succeeded");
							}
							catch (UnsupportedOperationException e) {
								// should get an exception
							}
							catch (Exception e) {
								fail("incorrect exception", e);
							}
						}
						catch (AssertionFailedError a) {
							hookError[0] = a;
							return;
						}
					}
				}, props);

		// register find hook 1
		props.put(Constants.SERVICE_DESCRIPTION, "max value first");
		props.put(Constants.SERVICE_RANKING, new Integer(Integer.MAX_VALUE));
		ServiceRegistration regHook1 = testContext.registerService(
				EventHook.class.getName(), new EventHook() {
					public void event(BundleEvent event,
							Collection contexts) {
						try {
							synchronized (hookCalled) {
								if (hookCalled.size() == 0 || hookCalled.getLast() != hookIDs[1])
									hookCalled.add(hookIDs[1]);
							}
							synchronized (filterEvent) {
								if (filterEvent[0])
									contexts.remove(testContext);
							}
						}
						catch (AssertionFailedError a) {
							hookError[1] = a;
							return;
						}
					}
				}, props);

		// register find hook 2
		props.put(Constants.SERVICE_DESCRIPTION, "max value second");
		props.put(Constants.SERVICE_RANKING, new Integer(Integer.MAX_VALUE));
		ServiceRegistration regHook2 = testContext.registerService(
				EventHook.class.getName(), new EventHook() {
					public void event(BundleEvent event,
							Collection contexts) {
						try {
							synchronized (hookCalled) {
								if (hookCalled.size() == 0 || hookCalled.getLast() != hookIDs[2])
									hookCalled.add(hookIDs[2]);
							}
						}
						catch (AssertionFailedError a) {
							hookError[2] = a;
							return;
						}
						// throw an exception from the hook to test that the
						// next hooks are called.
						throw new RuntimeException(getName());
					}
				}, props);

		// register find hook 4
		props.put(Constants.SERVICE_DESCRIPTION, "max value third");
		props.put(Constants.SERVICE_RANKING, new Integer(Integer.MAX_VALUE));
		ServiceRegistration regHook4 = testContext.registerService(
				EventHook.class.getName(), new EventHook() {
					public void event(BundleEvent event,
							Collection contexts) {
						try {
							synchronized (hookCalled) {
								if (hookCalled.size() == 0 || hookCalled.getLast() != hookIDs[3])
									hookCalled.add(hookIDs[3]);
							}
						}
						catch (AssertionFailedError a) {
							hookError[3] = a;
							return;
						}
					}
				}, props);

		// register the test listeners
		testContext.addBundleListener(sbl);
		testContext.addBundleListener(bl);
		// install a test bundle to test hooks.
		Bundle testBundle = null;
		try {
			try {
				testBundle = install("hooks.tb4.jar");
				testBundle.start();
			} catch (BundleException e) {
				fail("Failed to start test bundle.", e);
			} catch (IOException e) {
				fail("Failed to read test bundle.", e);
			}

			assertEquals("all hooks not called", 16, hookCalled.size());
			BundleEvent[] expectedEvents = new BundleEvent[] {
					new BundleEvent(BundleEvent.INSTALLED, testBundle),
					new BundleEvent(BundleEvent.RESOLVED, testBundle),
					new BundleEvent(BundleEvent.STARTING, testBundle),
					new BundleEvent(BundleEvent.STARTED, testBundle)
			};

			checkHooks(expectedEvents.length, hookCalled, hookIDs);
			// need to sleep to allow async events to be fired
			Thread.sleep(2000);
			checkEvents(expectedEvents, syncEvents, asyncEvents);

			for (int i = 0; i < hookError.length; i++) {
				if (hookError[i] != null) {
					throw hookError[i];
				}
			}

			hookCalled.clear();
			synchronized (filterEvent) {
				filterEvent[0] = true;
			}

			synchronized (asyncEvents) {
				asyncEvents.clear();
			}
			synchronized (syncEvents) {
				syncEvents.clear();
			}

			try {
				testBundle.stop();
			} catch (BundleException e) {
				fail("Unexpected stop error.", e);
			}
			// need to sleep to allow async events to be fired
			Thread.sleep(2000);
			expectedEvents = new BundleEvent[] {
					new BundleEvent(BundleEvent.STOPPING, testBundle),
					new BundleEvent(BundleEvent.STOPPED, testBundle)
			};
			checkHooks(expectedEvents.length, hookCalled, hookIDs);
			checkEvents(new BundleEvent[0], syncEvents, asyncEvents);

			// remove the hooks
			regHook0.unregister();
			regHook0 = null;
			regHook1.unregister();
			regHook1 = null;
			regHook2.unregister();
			regHook2 = null;
			regHook4.unregister();
			regHook4 = null;

			// fire events and make sure none are filtered

			hookCalled.clear();

			try {
				testBundle.start();
			} catch (BundleException e) {
				fail("Unexpected start error.", e);
			}
			// need to sleep to allow async events to be fired
			Thread.sleep(2000);
			expectedEvents = new BundleEvent[] {
					new BundleEvent(BundleEvent.STARTING, testBundle),
					new BundleEvent(BundleEvent.STARTED, testBundle)
			};
			assertEquals("hooks called", 0, hookCalled.size());
			checkEvents(expectedEvents, syncEvents, asyncEvents);

		} catch (InterruptedException e) {
			fail("Unexpected interuption", e);
		}
		finally {
			// unregister hook and services
			if (regHook0 != null)
				regHook0.unregister();
			if (regHook1 != null)
				regHook1.unregister();
			if (regHook2 != null)
				regHook2.unregister();
			if (regHook4 != null)
				regHook4.unregister();
			testContext.removeBundleListener(bl);
			testContext.removeBundleListener(sbl);
			if (testBundle != null)
				try {
					testBundle.uninstall();
				} catch (BundleException e) {
					// nothing
				}
		}
	}

	private void checkEvents(BundleEvent[] expectedEvents,
			LinkedList syncEvents, LinkedList asyncEvents) {
		assertEquals("Wrong number of events captured.", expectedEvents.length, syncEvents.size());
		for (int i = 0; i < expectedEvents.length; i++) {
			BundleEvent actualEvent = (BundleEvent) syncEvents.removeFirst();
			assertEquals("Wrong bundle for event.", expectedEvents[i].getBundle(), actualEvent.getBundle());
			assertEquals("Wrong type of event.", expectedEvents[i].getType(), actualEvent.getType());
			if ((expectedEvents[i].getType() & (BundleEvent.STARTING | BundleEvent.STOPPING | BundleEvent.LAZY_ACTIVATION)) == 0) {
				BundleEvent actualAsyncEvent = (BundleEvent) asyncEvents.removeFirst();
				assertEquals("Wrong bundle for event.", expectedEvents[i].getBundle(), actualAsyncEvent.getBundle());
				assertEquals("Wrong type of event.", expectedEvents[i].getType(), actualAsyncEvent.getType());
			}
		}
		
	}

	private void checkHooks(int numEvents, LinkedList hookCalled, Integer[] hookIDs) {
		for (int i = 0; i < numEvents; i++) {
			assertEquals("Hook 1 not called first.", hookIDs[1], hookCalled.removeFirst());
			assertEquals("Hook 2 not called second.", hookIDs[2], hookCalled.removeFirst());
			assertEquals("Hook 3 not called third.", hookIDs[3], hookCalled.removeFirst());
			assertEquals("Hook 4 not called forth.", hookIDs[0], hookCalled.removeFirst());
		}
	}


	public void testEventHook02() {
		final BundleContext testContext = getContext();

		// register services
		Hashtable props = new Hashtable();
		props.put("name", getName());
		final AssertionFailedError[] factoryError = new AssertionFailedError[] {null};
		final boolean[] factoryCalled = new boolean[] {false, false};
		final boolean[] hookCalled = new boolean[] {false};
		final EventHook eventHook1 = new EventHook() {
			public void event(BundleEvent arg0, Collection arg1) {
				synchronized (hookCalled) {
					hookCalled[0] = true;
				}
			}
		};
		props.put(Constants.SERVICE_DESCRIPTION, "event hook 1");
		synchronized (factoryCalled) {
			factoryCalled[0] = false;
			factoryCalled[1] = false;
		}
		synchronized (factoryError) {
			factoryError[0] = null;
		}
		ServiceRegistration regHook1 = testContext.registerService(
				EventHook.class.getName(), new ServiceFactory() {

					public Object getService(Bundle bundle,
							ServiceRegistration registration) {
						try {
							synchronized (factoryCalled) {
								factoryCalled[0] = true;
							}
							assertNotNull("using bundle null", bundle);
							ServiceReference reference = registration
									.getReference();
							Bundle[] users = reference.getUsingBundles();
							assertNotNull("service not used by a bundle", users);
							List userList = Arrays.asList(users);
							assertTrue("missing using bundle", userList
									.contains(bundle));
						}
						catch (AssertionFailedError a) {
							synchronized (factoryError) {
								factoryError[0] = a;
							}
						}
						return eventHook1;
					}

					public void ungetService(Bundle bundle,
							ServiceRegistration registration, Object service) {
						try {
							synchronized (factoryCalled) {
								factoryCalled[1] = true;
							}
							assertNotNull("using bundle null", bundle);
							assertEquals("wrong service", eventHook1, service);
							ServiceReference reference = registration
									.getReference();
							Bundle[] users = reference.getUsingBundles();
							assertNotNull("service not used by a bundle", users);
							List userList = Arrays.asList(users);
							assertTrue("missing using bundle", userList
									.contains(bundle));
						}
						catch (AssertionFailedError a) {
							synchronized (factoryError) {
								factoryError[0] = a;
							}
						}
					}
				}, props);

		synchronized (hookCalled) {
			hookCalled[0] = false;
		}

		Bundle testBundle = null;
		try {
			try {
				testBundle = install("hooks.tb4.jar");
				testBundle.start();
			} catch (BundleException e) {
				fail("Failed to start test bundle.", e);
			} catch (IOException e) {
				fail("Failed to read test bundle.", e);
			}
			synchronized (factoryError) {
				if (factoryError[0] != null) {
					throw factoryError[0];
				}
			}
			synchronized (factoryCalled) {
				assertTrue("factory getService not called", factoryCalled[0]);
			}
			synchronized (hookCalled) {
				assertTrue("hook not called", hookCalled[0]);
			}

			regHook1.unregister();
			regHook1 = null;

			synchronized (factoryError) {
				if (factoryError[0] != null) {
					throw factoryError[0];
				}
			}
			synchronized (factoryCalled) {
				assertTrue("factory ungetService not called", factoryCalled[1]);
			}
		}
		finally {
			// unregister hook and services
			if (regHook1 != null)
				regHook1.unregister();
		}
	}

}

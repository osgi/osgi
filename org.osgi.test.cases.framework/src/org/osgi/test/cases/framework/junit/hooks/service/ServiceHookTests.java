/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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

package org.osgi.test.cases.framework.junit.hooks.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import junit.framework.AssertionFailedError;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.service.EventHook;
import org.osgi.framework.hooks.service.FindHook;
import org.osgi.framework.hooks.service.ListenerHook;
import org.osgi.test.support.OSGiTestCase;

public class ServiceHookTests extends OSGiTestCase {

	public void testFindHook01() {
		final String testMethodName = "testFindHook01";
		// test the FindHook is called and can remove a reference from the
		// results
		Runnable runIt = new Runnable() {
			public void run() {
				// nothing
			}
		};
		final BundleContext testContext = getContext();
		// register services
		Hashtable props = new Hashtable();
		props.put("name", testMethodName);
		props.put(Constants.SERVICE_DESCRIPTION, "service 1");
		final ServiceRegistration reg1 = testContext.registerService(
				Runnable.class.getName(), runIt, props);

		props.put(Constants.SERVICE_DESCRIPTION, "service 2");
		final ServiceRegistration reg2 = testContext.registerService(
				Runnable.class.getName(), runIt, props);

		props.put(Constants.SERVICE_DESCRIPTION, "service 3");
		final ServiceRegistration reg3 = testContext.registerService(
				Runnable.class.getName(), runIt, props);

		final int[] hookCalled = new int[] {0, 0, 0, 0, 0};
		final AssertionFailedError[] hookError = new AssertionFailedError[] {
				null, null, null, null};

		// register find hook 1
		props.put(Constants.SERVICE_DESCRIPTION, "min value");
		props.put(Constants.SERVICE_RANKING, new Integer(Integer.MIN_VALUE));
		ServiceRegistration regHook1 = testContext.registerService(
				FindHook.class.getName(), new FindHook() {
					public void find(BundleContext context, String name,
							String filter, boolean allServices,
							Collection references) {
						try {
							synchronized (hookCalled) {
								hookCalled[++hookCalled[0]] = 1;
							}
							assertEquals("wrong context in hook", testContext,
									context);
							assertEquals("wrong name in hook", Runnable.class
									.getName(), name);
							assertEquals(
									"wrong filter in hook", "(name=" + testMethodName + ")", filter); //$NON-NLS-2$ //$NON-NLS-3$
							assertEquals("wrong allservices in hook", false,
									allServices);
							assertEquals("wrong number of services in hook", 1,
									references.size());
							for (Iterator iter = references.iterator(); iter
									.hasNext();) {
								ServiceReference ref = (ServiceReference) iter
										.next();
								if (ref.equals(reg1.getReference())) {
									fail("service 1 is present");
								}
								if (ref.equals(reg2.getReference())) {
									fail("service 2 is present");
								}
							}

							try {
								references.add(reg1.getReference());
								fail("add to collection succeeded");
							}
							catch (UnsupportedOperationException e) {
								// should get an exception
							}
							catch (Exception e) {
								fail("incorrect exception", e);
							}
							try {
								references.addAll(Arrays
										.asList(new ServiceReference[] {reg1
												.getReference()}));
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
					public void find(BundleContext context, String name,
							String filter, boolean allServices,
							Collection references) {
						try {
							synchronized (hookCalled) {
								hookCalled[++hookCalled[0]] = 2;
							}
							assertEquals("wrong context in hook", testContext,
									context);
							assertEquals("wrong name in hook", Runnable.class
									.getName(), name);
							assertEquals(
									"wrong filter in hook", "(name=" + testMethodName + ")", filter); //$NON-NLS-2$ //$NON-NLS-3$
							assertEquals("wrong allservices in hook", false,
									allServices);
							assertEquals("wrong number of services in hook", 3,
									references.size());
							for (Iterator iter = references.iterator(); iter
									.hasNext();) {
								ServiceReference ref = (ServiceReference) iter
										.next();
								if (ref.equals(reg2.getReference())) {
									iter.remove();
								}
							}

							try {
								references.add(reg2.getReference());
								fail("add to collection succeeded");
							}
							catch (UnsupportedOperationException e) {
								// should get an exception
							}
							catch (Exception e) {
								fail("incorrect exception", e);
							}
							try {
								references.addAll(Arrays
										.asList(new ServiceReference[] {reg2
												.getReference()}));
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
					public void find(BundleContext context, String name,
							String filter, boolean allServices,
							Collection references) {
						try {
							synchronized (hookCalled) {
								hookCalled[++hookCalled[0]] = 3;
							}
							assertEquals("wrong context in hook", testContext,
									context);
							assertEquals("wrong name in hook", Runnable.class
									.getName(), name);
							assertEquals(
									"wrong filter in hook", "(name=" + testMethodName + ")", filter); //$NON-NLS-2$ //$NON-NLS-3$
							assertEquals("wrong allservices in hook", false,
									allServices);
							assertEquals("wrong number of services in hook", 2,
									references.size());
							for (Iterator iter = references.iterator(); iter
									.hasNext();) {
								ServiceReference ref = (ServiceReference) iter
										.next();
								if (ref.equals(reg2.getReference())) {
									fail("service 2 is present");
								}
							}

							try {
								references.add(reg2.getReference());
								fail("add to collection succeeded");
							}
							catch (UnsupportedOperationException e) {
								// should get an exception
							}
							catch (Exception e) {
								fail("incorrect exception", e);
							}
							try {
								references.addAll(Arrays
										.asList(new ServiceReference[] {reg2
												.getReference()}));
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
						throw new RuntimeException(testMethodName);
					}
				}, props);

		// register find hook 4
		props.put(Constants.SERVICE_DESCRIPTION, "max value third");
		props.put(Constants.SERVICE_RANKING, new Integer(Integer.MAX_VALUE));
		ServiceRegistration regHook4 = testContext.registerService(
				FindHook.class.getName(), new FindHook() {
					public void find(BundleContext context, String name,
							String filter, boolean allServices,
							Collection references) {
						try {
							synchronized (hookCalled) {
								hookCalled[++hookCalled[0]] = 4;
							}
							assertEquals("wrong context in hook", testContext,
									context);
							assertEquals("wrong name in hook", Runnable.class
									.getName(), name);
							assertEquals(
									"wrong filter in hook", "(name=" + testMethodName + ")", filter); //$NON-NLS-2$ //$NON-NLS-3$
							assertEquals("wrong allservices in hook", false,
									allServices);
							assertEquals("wrong number of services in hook", 2,
									references.size());
							for (Iterator iter = references.iterator(); iter
									.hasNext();) {
								ServiceReference ref = (ServiceReference) iter
										.next();
								if (ref.equals(reg1.getReference())) {
									iter.remove();
								}
								if (ref.equals(reg2.getReference())) {
									fail("service 2 is present");
								}
							}

							try {
								references.add(reg2.getReference());
								fail("add to collection succeeded");
							}
							catch (UnsupportedOperationException e) {
								// should get an exception
							}
							catch (Exception e) {
								fail("incorrect exception", e);
							}
							try {
								references.addAll(Arrays
										.asList(new ServiceReference[] {reg2
												.getReference()}));
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
							hookError[3] = a;
							return;
						}
					}
				}, props);

		// get reference and hook removes some services
		try {
			ServiceReference[] refs = null;
			try {
				refs = testContext.getServiceReferences(Runnable.class
						.getName(), "(name=" + testMethodName + ")"); //$NON-NLS-2$
			}
			catch (InvalidSyntaxException e) {
				fail("Unexpected syntax error", e);
			}
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
			assertNotNull("service refs is null", refs);
			assertEquals("Wrong number of references", 1, refs.length);

			// test removed services are not in the result
			List refList = Arrays.asList(refs);
			assertFalse("contains service 1", refList.contains(reg1
					.getReference()));
			assertFalse("contains service 2", refList.contains(reg2
					.getReference()));
			assertTrue("missing service 3", refList.contains(reg3
					.getReference()));

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
			refs = null;
			hookCalled[0] = 0;

			try {
				refs = testContext.getServiceReferences(Runnable.class
						.getName(), "(name=" + testMethodName + ")"); //$NON-NLS-2$
			}
			catch (InvalidSyntaxException e) {
				fail("Unexpected syntax error", e);
			}
			assertEquals("hooks called", 0, hookCalled[0]);
			assertNotNull("service refs is null", refs);
			assertEquals("Wrong number of references", 3, refs.length);

			// test result contains all expected services
			refList = Arrays.asList(refs);
			assertTrue("missing service 1", refList.contains(reg1
					.getReference()));
			assertTrue("missing service 2", refList.contains(reg2
					.getReference()));
			assertTrue("missing service 3", refList.contains(reg3
					.getReference()));
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
			if (reg1 != null)
				reg1.unregister();
			if (reg2 != null)
				reg2.unregister();
			if (reg3 != null)
				reg3.unregister();
		}
	}

	public void testFindHook02() {
		final String testMethodName = "testFindHook02";
		Runnable runIt = new Runnable() {
			public void run() {
				// nothing
			}
		};
		final BundleContext testContext = getContext();

		// register services
		Hashtable props = new Hashtable();
		props.put("name", testMethodName);
		props.put(Constants.SERVICE_DESCRIPTION, "service 1");
		final ServiceRegistration reg1 = testContext.registerService(
				Runnable.class.getName(), runIt, props);

		final AssertionFailedError[] factoryError = new AssertionFailedError[] {null};
		final boolean[] factoryCalled = new boolean[] {false, false};
		final boolean[] hookCalled = new boolean[] {false};
		final FindHook findHook1 = new FindHook() {
			public void find(BundleContext arg0, String arg1, String arg2,
					boolean arg3, Collection arg4) {
				synchronized (hookCalled) {
					hookCalled[0] = true;
				}
			}
		};
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
			ServiceReference[] refs = null;
			try {
				refs = testContext.getServiceReferences(Runnable.class
						.getName(), "(name=" + testMethodName + ")"); //$NON-NLS-2$
			}
			catch (InvalidSyntaxException e) {
				fail("Unexpected syntax error", e);
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
			assertNotNull("service refs is null", refs);
			assertEquals("Wrong number of references", 1, refs.length);

			List refList = Arrays.asList(refs);
			assertTrue("missing service 1", refList.contains(reg1
					.getReference()));

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
			if (reg1 != null)
				reg1.unregister();
		}
	}

	public void testEventHook01() {
		final String testMethodName = "testEventHook01";
		// test the EventHook is called and can remove a reference from the
		// results
		Runnable runIt = new Runnable() {
			public void run() {
				// nothing
			}
		};
		final BundleContext testContext = getContext();

		final int[] hookCalled = new int[] {0, 0};
		final AssertionFailedError[] hookError = new AssertionFailedError[] {null};
		final List events = new ArrayList();

		final ServiceListener sl = new ServiceListener() {
			public void serviceChanged(ServiceEvent event) {
				synchronized (events) {
					events.add(event);
				}
			}
		};

		final String filterString = "(&(name=" + testMethodName + ")(objectClass=java.lang.Runnable))"; //$NON-NLS-2$
		Filter tmpFilter = null;
		try {
			tmpFilter = testContext.createFilter(filterString);
			testContext.addServiceListener(sl, filterString);
		}
		catch (InvalidSyntaxException e) {
			fail("Unexpected syntax error", e);
		}

		final Filter filter = tmpFilter;
		EventHook hook1 = new EventHook() {
			public void event(ServiceEvent event, Collection contexts) {
				try {
					if (!filter.match(event.getServiceReference())) {
						return;
					}
					synchronized (hookCalled) {
						hookCalled[++hookCalled[0]] = 1;
					}
					assertTrue("does not contain test context", contexts
							.contains(testContext));

					try {
						contexts.add(testContext.getBundle(0)
								.getBundleContext());
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
								.asList(new BundleContext[] {testContext
										.getBundle(0).getBundleContext()}));
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
		};
		EventHook hook2 = new EventHook() {
			public void event(ServiceEvent event, Collection contexts) {
				try {
					if (!filter.match(event.getServiceReference())) {
						return;
					}
					synchronized (hookCalled) {
						hookCalled[++hookCalled[0]] = 1;
					}
					assertTrue("does not contain test context", contexts
							.contains(testContext));
					contexts.remove(testContext);
					try {
						contexts.add(testContext.getBundle(0)
								.getBundleContext());
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
								.asList(new BundleContext[] {testContext
										.getBundle(0).getBundleContext()}));
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
		};

		Hashtable props = new Hashtable();
		props.put("name", testMethodName);
		// register event hook 1
		props.put(Constants.SERVICE_DESCRIPTION, "event hook 1");
		ServiceRegistration regHook = testContext.registerService(
				EventHook.class.getName(), hook1, props);

		ServiceRegistration reg1 = null;
		try {
			props.put(Constants.SERVICE_DESCRIPTION, "service 1");
			synchronized (events) {
				events.clear();
			}
			reg1 = testContext.registerService(Runnable.class.getName(), runIt,
					props);
			assertEquals("all hooks not called", 1, hookCalled[0]);
			assertEquals("hook 1 not called first", 1, hookCalled[1]);
			for (int i = 0; i < hookError.length; i++) {
				if (hookError[i] != null) {
					throw hookError[i];
				}
			}
			synchronized (events) {
				assertEquals("listener not called once", 1, events.size());
				for (Iterator iter = events.iterator(); iter.hasNext();) {
					ServiceEvent event = (ServiceEvent) iter.next();
					assertEquals("type not registered",
							ServiceEvent.REGISTERED, event.getType());
					assertEquals("wrong service", reg1.getReference(), event
							.getServiceReference());
				}
			}

			regHook.unregister();
			regHook = null;

			synchronized (events) {
				events.clear();
			}
			hookCalled[0] = 0;
			props.put(Constants.SERVICE_DESCRIPTION, "service 2");
			reg1.setProperties(props);
			synchronized (events) {
				assertEquals("listener not called once", 1, events.size());
				for (Iterator iter = events.iterator(); iter.hasNext();) {
					ServiceEvent event = (ServiceEvent) iter.next();
					assertEquals("type not registered", ServiceEvent.MODIFIED,
							event.getType());
					assertEquals("wrong service", reg1.getReference(), event
							.getServiceReference());
				}
			}
			assertEquals("hooks called", 0, hookCalled[0]);

			props.put(Constants.SERVICE_DESCRIPTION, "event hook 2");
			regHook = testContext.registerService(EventHook.class.getName(),
					hook2, props);

			synchronized (events) {
				events.clear();
			}
			hookCalled[0] = 0;
			reg1.unregister();
			reg1 = null;
			synchronized (events) {
				assertEquals("listener called", 0, events.size());
			}
			assertEquals("all hooks not called", 1, hookCalled[0]);
			assertEquals("hook 1 not called first", 1, hookCalled[1]);
			for (int i = 0; i < hookError.length; i++) {
				if (hookError[i] != null) {
					throw hookError[i];
				}
			}

		}
		finally {
			// unregister hook and services
			if (regHook != null)
				regHook.unregister();
			if (reg1 != null)
				reg1.unregister();
			if (sl != null)
				testContext.removeServiceListener(sl);
		}
	}

	public void testEventHook02() {
		final String testMethodName = "testEventHook02";
		Runnable runIt = new Runnable() {
			public void run() {
				// nothing
			}
		};
		final BundleContext testContext = getContext();

		// register services
		Hashtable props = new Hashtable();
		props.put("name", testMethodName);
		final AssertionFailedError[] factoryError = new AssertionFailedError[] {null};
		final boolean[] factoryCalled = new boolean[] {false, false};
		final boolean[] hookCalled = new boolean[] {false};
		final EventHook eventHook1 = new EventHook() {
			public void event(ServiceEvent arg0, Collection arg1) {
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
		props.put(Constants.SERVICE_DESCRIPTION, "service 1");
		final ServiceRegistration reg1 = testContext.registerService(
				Runnable.class.getName(), runIt, props);
		try {
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
			if (reg1 != null)
				reg1.unregister();
		}
	}

	public void testListenerHook01() {
		final String testMethodName = "testListenerHook01";
		// test the ListenerHook is called
		final BundleContext testContext = getContext();
		final Collection added = new ArrayList();
		final Collection removed = new ArrayList();
		final int[] hookCalled = new int[] {0, 0};

		ListenerHook hook1 = new ListenerHook() {
			public void added(Collection listeners) {
				hookCalled[0]++;
				added.addAll(listeners);
			}

			public void removed(Collection listeners) {
				hookCalled[1]++;
				added.removeAll(listeners);
				removed.addAll(listeners);
			}
		};

		Hashtable props = new Hashtable();
		props.put("name", testMethodName);
		// register listener hook 1
		props.put(Constants.SERVICE_DESCRIPTION, "listener hook 1");
		ServiceRegistration regHook = testContext.registerService(
				ListenerHook.class.getName(), hook1, props);

		try {
			assertFalse("no service listeners found", added.isEmpty());
			assertEquals("added not called", 1, hookCalled[0]);
			assertEquals("removed called", 0, hookCalled[1]);

			int size = added.size();
			ServiceListener testSL = new ServiceListener() {
				public void serviceChanged(ServiceEvent event) {
					// do nothing
				}
			};
			String filterString1 = "(foo=bar)";
			testContext.addServiceListener(testSL, filterString1);
			assertEquals("added not called", 2, hookCalled[0]);
			assertEquals("removed called", 0, hookCalled[1]);
			assertEquals("listener not added", size + 1, added.size());
			boolean found = false;
			for (Iterator iter = added.iterator(); iter.hasNext();) {
				ListenerHook.ListenerInfo info = (ListenerHook.ListenerInfo) iter
						.next();
				assertFalse("isRemoved true", info.isRemoved());
				BundleContext c = info.getBundleContext();
				String f = info.getFilter();
				if ((c == testContext) && (filterString1.equals(f))) {
					if (found) {
						fail("found more than once");
					}
					found = true;
				}
			}
			if (!found) {
				fail("listener not found");
			}
			for (Iterator iter = removed.iterator(); iter.hasNext();) {
				ListenerHook.ListenerInfo info = (ListenerHook.ListenerInfo) iter
						.next();
				assertTrue("isRemoved true", info.isRemoved());
			}

			String filterString2 = "(bar=foo)";
			testContext.addServiceListener(testSL, filterString2);
			assertEquals("added not called", 3, hookCalled[0]);
			assertEquals("removed not called", 1, hookCalled[1]);
			assertEquals("listener not removed and added", size + 1, added
					.size());
			found = false;
			for (Iterator iter = added.iterator(); iter.hasNext();) {
				ListenerHook.ListenerInfo info = (ListenerHook.ListenerInfo) iter
						.next();
				assertFalse("isRemoved true", info.isRemoved());
				BundleContext c = info.getBundleContext();
				String f = info.getFilter();
				if ((c == testContext) && (filterString2.equals(f))) {
					if (found) {
						fail("found more than once");
					}
					found = true;
				}
				if ((c == testContext) && (filterString1.equals(f))) {
					fail("first listener not removed");
				}
			}
			if (!found) {
				fail("listener not found");
			}
			for (Iterator iter = removed.iterator(); iter.hasNext();) {
				ListenerHook.ListenerInfo info = (ListenerHook.ListenerInfo) iter
						.next();
				assertTrue("isRemoved true", info.isRemoved());
			}

			testContext.removeServiceListener(testSL);
			assertEquals("added called", 3, hookCalled[0]);
			assertEquals("removed not called", 2, hookCalled[1]);
			assertEquals("listener not removed", size, added.size());
			for (Iterator iter = added.iterator(); iter.hasNext();) {
				ListenerHook.ListenerInfo info = (ListenerHook.ListenerInfo) iter
						.next();
				assertFalse("isRemoved true", info.isRemoved());
				BundleContext c = info.getBundleContext();
				String f = info.getFilter();
				if ((c == testContext) && (filterString2.equals(f))) {
					fail("second listener not removed");
				}
			}
			for (Iterator iter = removed.iterator(); iter.hasNext();) {
				ListenerHook.ListenerInfo info = (ListenerHook.ListenerInfo) iter
						.next();
				assertTrue("isRemoved true", info.isRemoved());
			}

			testContext.removeServiceListener(testSL);
			assertEquals("added called", 3, hookCalled[0]);
			assertEquals("removed called", 2, hookCalled[1]);
			assertEquals("listener removed", size, added.size());

		}
		catch (InvalidSyntaxException e) {
			fail(e.getMessage());
		}
		finally {
			if (regHook != null) {
				regHook.unregister();
			}
		}
	}

	public void testListenerHook02() {
		final String testMethodName = "testListenerHook02";
		// test the ListenerHook works with the FilteredServiceListener
		// optimization in equinox
		final BundleContext testContext = getContext();
		final Collection added = new ArrayList();
		final Collection removed = new ArrayList();
		final int[] hookCalled = new int[] {0, 0};

		ListenerHook hook1 = new ListenerHook() {
			public void added(Collection listeners) {
				hookCalled[0]++;
				added.addAll(listeners);
			}

			public void removed(Collection listeners) {
				hookCalled[1]++;
				added.removeAll(listeners);
				removed.addAll(listeners);
			}
		};

		Hashtable props = new Hashtable();
		props.put("name", testMethodName);
		// register listener hook 1
		props.put(Constants.SERVICE_DESCRIPTION, "listener hook 1");
		ServiceRegistration regHook = testContext.registerService(
				ListenerHook.class.getName(), hook1, props);

		try {
			assertFalse("no service listeners found", added.isEmpty());
			assertEquals("added not called", 1, hookCalled[0]);
			assertEquals("removed called", 0, hookCalled[1]);

			int size = added.size();
			ServiceListener testSL = new ServiceListener() {
				public void serviceChanged(ServiceEvent event) {
					// do nothing
				}
			};
			String filterString1 = "(" + Constants.OBJECTCLASS + "=bar)"; //$NON-NLS-2$
			testContext.addServiceListener(testSL, filterString1);
			assertEquals("added not called", 2, hookCalled[0]);
			assertEquals("removed called", 0, hookCalled[1]);
			assertEquals("listener not added", size + 1, added.size());
			boolean found = false;
			for (Iterator iter = added.iterator(); iter.hasNext();) {
				ListenerHook.ListenerInfo info = (ListenerHook.ListenerInfo) iter
						.next();
				assertFalse("isRemoved true", info.isRemoved());
				BundleContext c = info.getBundleContext();
				String f = info.getFilter();
				if ((c == testContext) && (filterString1.equals(f))) {
					if (found) {
						fail("found more than once");
					}
					found = true;
				}
			}
			if (!found) {
				fail("listener not found");
			}
			for (Iterator iter = removed.iterator(); iter.hasNext();) {
				ListenerHook.ListenerInfo info = (ListenerHook.ListenerInfo) iter
						.next();
				assertTrue("isRemoved true", info.isRemoved());
			}

			String filterString2 = null;
			testContext.addServiceListener(testSL);
			assertEquals("added not called", 3, hookCalled[0]);
			assertEquals("removed not called", 1, hookCalled[1]);
			assertEquals("listener not removed and added", size + 1, added
					.size());
			found = false;
			for (Iterator iter = added.iterator(); iter.hasNext();) {
				ListenerHook.ListenerInfo info = (ListenerHook.ListenerInfo) iter
						.next();
				assertFalse("isRemoved true", info.isRemoved());
				BundleContext c = info.getBundleContext();
				String f = info.getFilter();
				if ((c == testContext) && (f == filterString2)) {
					if (found) {
						fail("found more than once");
					}
					found = true;
				}
				if ((c == testContext) && (filterString1.equals(f))) {
					fail("first listener not removed");
				}
			}
			if (!found) {
				fail("listener not found");
			}
			for (Iterator iter = removed.iterator(); iter.hasNext();) {
				ListenerHook.ListenerInfo info = (ListenerHook.ListenerInfo) iter
						.next();
				assertTrue("isRemoved true", info.isRemoved());
			}

			testContext.removeServiceListener(testSL);
			assertEquals("added called", 3, hookCalled[0]);
			assertEquals("removed not called", 2, hookCalled[1]);
			assertEquals("listener not removed", size, added.size());
			for (Iterator iter = added.iterator(); iter.hasNext();) {
				ListenerHook.ListenerInfo info = (ListenerHook.ListenerInfo) iter
						.next();
				assertFalse("isRemoved true", info.isRemoved());
				BundleContext c = info.getBundleContext();
				String f = info.getFilter();
				if ((c == testContext) && (f == filterString2)) {
					fail("second listener not removed");
				}
			}
			for (Iterator iter = removed.iterator(); iter.hasNext();) {
				ListenerHook.ListenerInfo info = (ListenerHook.ListenerInfo) iter
						.next();
				assertTrue("isRemoved true", info.isRemoved());
			}

			testContext.removeServiceListener(testSL);
			assertEquals("added called", 3, hookCalled[0]);
			assertEquals("removed called", 2, hookCalled[1]);
			assertEquals("listener removed", size, added.size());

		}
		catch (InvalidSyntaxException e) {
			fail(e.getMessage());
		}
		finally {
			if (regHook != null) {
				regHook.unregister();
			}
		}
	}

	public void testListenerHook03() {
		final String testMethodName = "testListenerHook03";
		final BundleContext testContext = getContext();

		// register services
		Hashtable props = new Hashtable();
		props.put("name", testMethodName);
		final AssertionFailedError[] factoryError = new AssertionFailedError[] {null};
		final boolean[] factoryCalled = new boolean[] {false, false};
		final boolean[] hookCalled = new boolean[] {false};
		final ListenerHook listenerHook1 = new ListenerHook() {
			public void added(Collection arg0) {
				synchronized (hookCalled) {
					hookCalled[0] = true;
				}
			}

			public void removed(Collection arg0) {
				synchronized (hookCalled) {
					hookCalled[0] = true;
				}
			}
		};
		props.put(Constants.SERVICE_DESCRIPTION, "listener hook 1");
		synchronized (factoryCalled) {
			factoryCalled[0] = false;
			factoryCalled[1] = false;
		}
		synchronized (factoryError) {
			factoryError[0] = null;
		}
		ServiceRegistration regHook1 = testContext.registerService(
				ListenerHook.class.getName(), new ServiceFactory() {

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
						return listenerHook1;
					}

					public void ungetService(Bundle bundle,
							ServiceRegistration registration, Object service) {
						try {
							synchronized (factoryCalled) {
								factoryCalled[1] = true;
							}
							assertNotNull("using bundle null", bundle);
							assertEquals("wrong service", listenerHook1,
									service);
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

		final ServiceListener testSL = new ServiceListener() {
			public void serviceChanged(ServiceEvent event) {
				// do nothing
			}
		};
		testContext.addServiceListener(testSL);
		try {
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
			testContext.removeServiceListener(testSL);
		}
	}
}

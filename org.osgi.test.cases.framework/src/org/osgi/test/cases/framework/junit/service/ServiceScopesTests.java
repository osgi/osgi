/*
 * Copyright (c) OSGi Alliance (2013, 2015). All Rights Reserved.
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

package org.osgi.test.cases.framework.junit.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.support.OSGiTestCase;

public class ServiceScopesTests extends OSGiTestCase {

    private BundleContext context;
    private Bundle        self;

	@Override
	protected void setUp() {
        context = getContext();
        self = context.getBundle();
    }

    public void testSingletonScope() throws Exception {
        Hashtable<String, String> properties = new Hashtable<String, String>();
        properties.put("test", "yes");
        properties.put(Constants.SERVICE_SCOPE, Constants.SCOPE_BUNDLE);
        TestService service = new TestServiceImpl(0);
        ServiceRegistration<TestService> registration = context.registerService(TestService.class, service, properties);
        assertNotNull(registration);
        try {
            ServiceReference<TestService> reference = registration.getReference();
            assertNotNull(reference);
            assertEquals("service.scope not set correctly", Constants.SCOPE_SINGLETON, reference.getProperty(Constants.SERVICE_SCOPE));
            assertSame("service object not the same", service, context.getService(reference));
            assertSame("service object not the same", service, context.getService(reference));
            assertSame("service object not the same", service, context.getService(reference));
            assertTrue("service use count wrong", context.ungetService(reference));
            assertTrue("service use count wrong", context.ungetService(reference));
            assertTrue("service use count wrong", context.ungetService(reference));
            assertFalse("service use count wrong", context.ungetService(reference));
            assertFalse("service use count wrong", context.ungetService(reference));
        } finally {
            registration.unregister();
        }
    }

    public void testSingletonScopeWithTwoConsumers() throws Exception {
        Bundle tb1 = install("serviceregistry.tb1.jar");
        assertNotNull(tb1);
        try {
            tb1.start();
            assertTrue((tb1.getState() & Bundle.ACTIVE) != 0);
            BundleContext tb1Context = tb1.getBundleContext();
            assertNotNull(tb1Context);
            Hashtable<String, String> properties = new Hashtable<String, String>();
            properties.put("test", "yes");
            properties.put(Constants.SERVICE_SCOPE, Constants.SCOPE_BUNDLE);
            TestService service = new TestServiceImpl(0);
            ServiceRegistration<TestService> registration = tb1Context.registerService(TestService.class, service, properties);
            assertNotNull(registration);
            ServiceReference<TestService> reference1 = context.getServiceReference(TestService.class);
            assertNotNull(reference1);
            ServiceReference<TestService> reference2 = tb1Context.getServiceReference(TestService.class);
            assertNotNull(reference2);

            assertEquals("service.scope not set correctly", Constants.SCOPE_SINGLETON, reference1.getProperty(Constants.SERVICE_SCOPE));
            assertEquals("service.scope not set correctly", Constants.SCOPE_SINGLETON, reference2.getProperty(Constants.SERVICE_SCOPE));

            assertSame("service object not the same", service, context.getService(reference1));
            assertSame("service object not the same", service, context.getService(reference1));
            assertSame("service object not the same", service, context.getService(reference1));
            assertSame("service object not the same", service, tb1Context.getService(reference2));
            assertSame("service object not the same", service, tb1Context.getService(reference2));

            assertTrue("service use count wrong", context.ungetService(reference1));
            assertTrue("service use count wrong", context.ungetService(reference1));
            assertTrue("service use count wrong", context.ungetService(reference1));
            assertFalse("service use count wrong", context.ungetService(reference1));
            assertFalse("service use count wrong", context.ungetService(reference1));

            assertTrue("service use count wrong", tb1Context.ungetService(reference2));
            assertTrue("service use count wrong", tb1Context.ungetService(reference2));
            assertFalse("service use count wrong", tb1Context.ungetService(reference2));
            assertFalse("service use count wrong", tb1Context.ungetService(reference2));
            registration.unregister();
        } finally {
            tb1.uninstall();
        }
    }

    public void testBundleScope() throws Exception {
        Hashtable<String, String> properties = new Hashtable<String, String>();
        properties.put("test", "yes");
        properties.put(Constants.SERVICE_SCOPE, Constants.SCOPE_SINGLETON);
        TestServiceFactory factory = new TestServiceFactory();
        ServiceRegistration<TestService> registration = context.registerService(TestService.class, factory, properties);
        assertNotNull(registration);
        try {
            ServiceReference<TestService> reference = registration.getReference();
            assertNotNull(reference);
            assertEquals("service.scope not set correctly", Constants.SCOPE_BUNDLE, reference.getProperty(Constants.SERVICE_SCOPE));

            assertEquals(0, factory.services.size());
            TestService service = context.getService(reference);
            assertNotNull(service);
            assertEquals(self.getBundleId(), service.getId());
            assertEquals(1, factory.services.size());
            assertSame("service object not the same", factory.services.get(self), service);
            assertSame("service object not the same", service, context.getService(reference));
            assertEquals(1, factory.services.size());
            assertSame("service object not the same", service, context.getService(reference));
            assertEquals(1, factory.services.size());
            assertTrue("service use count wrong", context.ungetService(reference));
            assertEquals(1, factory.services.size());
            assertTrue("service use count wrong", context.ungetService(reference));
            assertEquals(1, factory.services.size());
            assertTrue("service use count wrong", context.ungetService(reference));
            assertEquals(0, factory.services.size());
            assertFalse("service use count wrong", context.ungetService(reference));
            assertFalse("service use count wrong", context.ungetService(reference));
        } finally {
            registration.unregister();
        }
    }

    public void testBundleScopeWithTwoConsumers() throws Exception {
        Bundle tb1 = install("serviceregistry.tb1.jar");
        assertNotNull(tb1);
        try {
            tb1.start();
            assertTrue((tb1.getState() & Bundle.ACTIVE) != 0);
            BundleContext tb1Context = tb1.getBundleContext();
            assertNotNull(tb1Context);

            Hashtable<String, String> properties = new Hashtable<String, String>();
            properties.put("test", "yes");
            properties.put(Constants.SERVICE_SCOPE, Constants.SCOPE_SINGLETON);
            TestServiceFactory factory = new TestServiceFactory();
            ServiceRegistration<TestService> registration = tb1Context.registerService(TestService.class, factory, properties);
            assertNotNull(registration);
            ServiceReference<TestService> reference1 = context.getServiceReference(TestService.class);
            assertNotNull(reference1);
            ServiceReference<TestService> reference2 = tb1Context.getServiceReference(TestService.class);
            assertNotNull(reference2);

            assertEquals("service.scope not set correctly", Constants.SCOPE_BUNDLE, reference1.getProperty(Constants.SERVICE_SCOPE));
            assertEquals("service.scope not set correctly", Constants.SCOPE_BUNDLE, reference2.getProperty(Constants.SERVICE_SCOPE));

            assertEquals(0, factory.services.size());

            TestService service1 = context.getService(reference1);
            assertNotNull(service1);
            assertSame("service object not the same", factory.services.get(self), service1);
            assertEquals(self.getBundleId(), service1.getId());
            assertEquals(1, factory.services.size());

            TestService service2 = tb1Context.getService(reference2);
            assertNotNull(service2);
            assertSame("service object not the same", factory.services.get(tb1), service2);
            assertEquals(tb1.getBundleId(), service2.getId());
            assertEquals(2, factory.services.size());

            assertNotSame(service1, service2);

            assertSame("service object not the same", service1, context.getService(reference1));
            assertEquals(2, factory.services.size());
            assertSame("service object not the same", service1, context.getService(reference1));
            assertEquals(2, factory.services.size());
            assertSame("service object not the same", service2, tb1Context.getService(reference2));
            assertEquals(2, factory.services.size());

            assertTrue("service use count wrong", context.ungetService(reference1));
            assertEquals(2, factory.services.size());
            assertTrue("service use count wrong", context.ungetService(reference1));
            assertEquals(2, factory.services.size());
            assertTrue("service use count wrong", context.ungetService(reference1));
            assertEquals(1, factory.services.size());
            assertFalse("service use count wrong", context.ungetService(reference1));
            assertEquals(1, factory.services.size());
            assertFalse("service use count wrong", context.ungetService(reference1));
            assertEquals(1, factory.services.size());

            assertTrue("service use count wrong", tb1Context.ungetService(reference2));
            assertEquals(1, factory.services.size());
            assertTrue("service use count wrong", tb1Context.ungetService(reference2));
            assertEquals(0, factory.services.size());
            assertFalse("service use count wrong", tb1Context.ungetService(reference2));
            assertEquals(0, factory.services.size());
            assertFalse("service use count wrong", tb1Context.ungetService(reference2));
            assertEquals(0, factory.services.size());
            registration.unregister();
        } finally {
            tb1.uninstall();
        }
    }

    public void testPrototypeScope() throws Exception {
        Hashtable<String, String> properties = new Hashtable<String, String>();
        properties.put("test", "yes");
        properties.put(Constants.SERVICE_SCOPE, Constants.SCOPE_SINGLETON);
        TestPrototypeServiceFactory factory = new TestPrototypeServiceFactory();
        ServiceRegistration<TestService> registration = context.registerService(TestService.class, factory, properties);
        assertNotNull(registration);
        try {
            ServiceReference<TestService> reference = registration.getReference();
            assertNotNull(reference);
            assertEquals("service.scope not set correctly", Constants.SCOPE_PROTOTYPE, reference.getProperty(Constants.SERVICE_SCOPE));

            assertEquals(0, factory.services.size());
            TestService service = context.getService(reference);
            assertNotNull(service);
            assertEquals(self.getBundleId(), service.getId());
            assertEquals(1, factory.services.size());
            assertEquals(1, factory.services.get(self).size());
            assertTrue(factory.services.get(self).contains(service));
            assertSame("service object not the same", service, context.getService(reference));
            assertEquals(1, factory.services.size());
            assertEquals(1, factory.services.get(self).size());
            assertSame("service object not the same", service, context.getService(reference));
            assertEquals(1, factory.services.size());
            assertEquals(1, factory.services.get(self).size());
            assertTrue("service use count wrong", context.ungetService(reference));
            assertEquals(1, factory.services.size());
            assertEquals(1, factory.services.get(self).size());
            assertTrue("service use count wrong", context.ungetService(reference));
            assertEquals(1, factory.services.size());
            assertEquals(1, factory.services.get(self).size());
            assertTrue("service use count wrong", context.ungetService(reference));
            assertEquals(0, factory.services.size());
            assertFalse("service use count wrong", context.ungetService(reference));
            assertFalse("service use count wrong", context.ungetService(reference));
        } finally {
            registration.unregister();
        }
    }

	public void testMultipleGetsOfServiceObjects() throws Exception {
		ServiceRegistration<TestService> registration = context.registerService(TestService.class, new TestServiceImpl(42), null);
		assertNotNull(registration);
		try {
			ServiceReference<TestService> reference = registration.getReference();
			assertNotNull(reference);
			assertEquals("service.scope not set correctly", Constants.SCOPE_SINGLETON, reference.getProperty(Constants.SERVICE_SCOPE));

			final ServiceObjects<TestService> so1 = context.getServiceObjects(reference);
			assertNotNull(so1);
			TestService service = so1.getService();
			assertNotNull(service);

			final ServiceObjects<TestService> so2 = context.getServiceObjects(reference);
			assertNotNull(so2);
			so2.ungetService(service);
		} finally {
			registration.unregister();
		}
	}

    public void testPrototypeScopeWithTwoConsumers() throws Exception {
        Bundle tb1 = install("serviceregistry.tb1.jar");
        assertNotNull(tb1);
        try {
            tb1.start();
            assertTrue((tb1.getState() & Bundle.ACTIVE) != 0);
            BundleContext tb1Context = tb1.getBundleContext();
            assertNotNull(tb1Context);

            Hashtable<String, String> properties = new Hashtable<String, String>();
            properties.put("test", "yes");
            properties.put(Constants.SERVICE_SCOPE, Constants.SCOPE_SINGLETON);
            TestPrototypeServiceFactory factory = new TestPrototypeServiceFactory();
            ServiceRegistration<TestService> registration = tb1Context.registerService(TestService.class, factory, properties);
            assertNotNull(registration);
            ServiceReference<TestService> reference1 = context.getServiceReference(TestService.class);
            assertNotNull(reference1);
            ServiceReference<TestService> reference2 = tb1Context.getServiceReference(TestService.class);
            assertNotNull(reference2);

            assertEquals("service.scope not set correctly", Constants.SCOPE_PROTOTYPE, reference1.getProperty(Constants.SERVICE_SCOPE));
            assertEquals("service.scope not set correctly", Constants.SCOPE_PROTOTYPE, reference2.getProperty(Constants.SERVICE_SCOPE));

            assertEquals(0, factory.services.size());

            TestService service1 = context.getService(reference1);
            assertNotNull(service1);
            assertEquals(1, factory.services.size());
            assertEquals(1, factory.services.get(self).size());
            assertTrue(factory.services.get(self).contains(service1));
            assertEquals(self.getBundleId(), service1.getId());

            TestService service2 = tb1Context.getService(reference2);
            assertNotNull(service2);
            assertEquals(2, factory.services.size());
            assertEquals(1, factory.services.get(tb1).size());
            assertTrue(factory.services.get(tb1).contains(service2));
            assertEquals(tb1.getBundleId(), service2.getId());

            assertNotSame(service1, service2);

            assertSame("service object not the same", service1, context.getService(reference1));
            assertEquals(2, factory.services.size());
            assertEquals(1, factory.services.get(self).size());
            assertSame("service object not the same", service1, context.getService(reference1));
            assertEquals(2, factory.services.size());
            assertEquals(1, factory.services.get(self).size());
            assertSame("service object not the same", service2, tb1Context.getService(reference2));
            assertEquals(2, factory.services.size());
            assertEquals(1, factory.services.get(tb1).size());

            assertTrue("service use count wrong", context.ungetService(reference1));
            assertEquals(2, factory.services.size());
            assertEquals(1, factory.services.get(self).size());
            assertTrue("service use count wrong", context.ungetService(reference1));
            assertEquals(2, factory.services.size());
            assertEquals(1, factory.services.get(self).size());
            assertTrue("service use count wrong", context.ungetService(reference1));
            assertEquals(1, factory.services.size());
            assertNull(factory.services.get(self));
            assertFalse("service use count wrong", context.ungetService(reference1));
            assertEquals(1, factory.services.size());
            assertNull(factory.services.get(self));
            assertFalse("service use count wrong", context.ungetService(reference1));
            assertEquals(1, factory.services.size());
            assertNull(factory.services.get(self));

            assertTrue("service use count wrong", tb1Context.ungetService(reference2));
            assertEquals(1, factory.services.size());
            assertEquals(1, factory.services.get(tb1).size());
            assertTrue("service use count wrong", tb1Context.ungetService(reference2));
            assertEquals(0, factory.services.size());
            assertFalse("service use count wrong", tb1Context.ungetService(reference2));
            assertEquals(0, factory.services.size());
            assertFalse("service use count wrong", tb1Context.ungetService(reference2));
            assertEquals(0, factory.services.size());
            registration.unregister();
        } finally {
            tb1.uninstall();
        }
    }

    public void testPrototypeConsumerWithSingletonScope() throws Exception {
        Bundle tb1 = install("serviceregistry.tb1.jar");
        assertNotNull(tb1);
        try {
            tb1.start();
            assertTrue((tb1.getState() & Bundle.ACTIVE) != 0);
            BundleContext tb1Context = tb1.getBundleContext();
            assertNotNull(tb1Context);

            Hashtable<String, String> properties = new Hashtable<String, String>();
            properties.put("test", "yes");
            properties.put(Constants.SERVICE_SCOPE, Constants.SCOPE_BUNDLE);
            TestService service = new TestServiceImpl(0);
            ServiceRegistration<TestService> registration = tb1Context.registerService(TestService.class, service, properties);
            assertNotNull(registration);
            ServiceReference<TestService> reference = registration.getReference();
            assertNotNull(reference);
            assertEquals("service.scope not set correctly", Constants.SCOPE_SINGLETON, reference.getProperty(Constants.SERVICE_SCOPE));

            ServiceObjects<TestService> objects = tb1Context.getServiceObjects(reference);
            assertNotNull(objects);
            assertEquals(reference, objects.getServiceReference());

            assertSame("service object not the same", service, tb1Context.getService(reference));
            assertSame("service object not the same", service, tb1Context.getService(reference));
            assertSame("service object not the same", service, tb1Context.getService(reference));
            assertSame("service object not the same", service, objects.getService());
            assertSame("service object not the same", service, objects.getService());
            assertSame("service object not the same", service, objects.getService());
            assertTrue("service use count wrong", tb1Context.ungetService(reference));
            assertTrue("service use count wrong", tb1Context.ungetService(reference));
            assertTrue("service use count wrong", tb1Context.ungetService(reference));
            objects.ungetService(service);
            objects.ungetService(service);
            objects.ungetService(service);
            assertFalse("service use count wrong", tb1Context.ungetService(reference));
            assertFalse("service use count wrong", tb1Context.ungetService(reference));
            registration.unregister();
            assertNull(tb1Context.getServiceObjects(reference));
            tb1.stop();
            try {
                tb1Context.getServiceObjects(reference);
                fail("expected exception");
            } catch (IllegalStateException e) {
                // expected
            }
        } finally {
            tb1.uninstall();
        }
    }

    public void testPrototypeConsumerWithBundleScope() throws Exception {
        Bundle tb1 = install("serviceregistry.tb1.jar");
        assertNotNull(tb1);
        try {
            tb1.start();
            assertTrue((tb1.getState() & Bundle.ACTIVE) != 0);
            BundleContext tb1Context = tb1.getBundleContext();
            assertNotNull(tb1Context);

            Hashtable<String, String> properties = new Hashtable<String, String>();
            properties.put("test", "yes");
            properties.put(Constants.SERVICE_SCOPE, Constants.SCOPE_SINGLETON);
            TestServiceFactory factory = new TestServiceFactory();
            ServiceRegistration<TestService> registration = tb1Context.registerService(TestService.class, factory, properties);
            assertNotNull(registration);
            ServiceReference<TestService> reference1 = context.getServiceReference(TestService.class);
            ServiceReference<TestService> reference2 = tb1Context.getServiceReference(TestService.class);
            assertNotNull(reference1);
            assertNotNull(reference2);

            assertEquals("service.scope not set correctly", Constants.SCOPE_BUNDLE, reference1.getProperty(Constants.SERVICE_SCOPE));
            assertEquals("service.scope not set correctly", Constants.SCOPE_BUNDLE, reference2.getProperty(Constants.SERVICE_SCOPE));

            ServiceObjects<TestService> objects1 = context.getServiceObjects(reference1);
            assertNotNull(objects1);
            assertEquals(reference1, objects1.getServiceReference());
            ServiceObjects<TestService> objects2 = tb1Context.getServiceObjects(reference2);
            assertNotNull(objects2);
            assertEquals(reference2, objects2.getServiceReference());

            assertEquals(0, factory.services.size());

            TestService service1 = objects1.getService();
            assertNotNull(service1);
            assertSame("service object not the same", factory.services.get(self), service1);
            assertEquals(self.getBundleId(), service1.getId());
            assertEquals(1, factory.services.size());

            TestService service2 = objects2.getService();
            assertNotNull(service2);
            assertSame("service object not the same", factory.services.get(tb1), service2);
            assertEquals(tb1.getBundleId(), service2.getId());
            assertEquals(2, factory.services.size());

            assertNotSame(service1, service2);

            assertSame("service object not the same", service1, objects1.getService());
            assertEquals(2, factory.services.size());
            assertSame("service object not the same", service1, context.getService(reference1));
            assertEquals(2, factory.services.size());
            assertSame("service object not the same", service2, tb1Context.getService(reference2));
            assertEquals(2, factory.services.size());

            try {
                objects1.ungetService(service2);
                fail("expected exception");
            } catch (IllegalArgumentException e) {
                // expected
            }
            try {
                objects2.ungetService(service1);
                fail("expected exception");
            } catch (IllegalArgumentException e) {
                // expected
            }

            objects1.ungetService(service1);
            assertEquals(2, factory.services.size());
            objects1.ungetService(service1);
            assertEquals(2, factory.services.size());
            assertTrue("service use count wrong", context.ungetService(reference1));
            assertEquals(1, factory.services.size());
            assertFalse("service use count wrong", context.ungetService(reference1));
            assertEquals(1, factory.services.size());
            assertFalse("service use count wrong", context.ungetService(reference1));
            assertEquals(1, factory.services.size());

            objects2.ungetService(service2);
            assertEquals(1, factory.services.size());
            assertTrue("service use count wrong", tb1Context.ungetService(reference2));
            assertEquals(0, factory.services.size());
            assertFalse("service use count wrong", tb1Context.ungetService(reference2));
            assertEquals(0, factory.services.size());
            assertFalse("service use count wrong", tb1Context.ungetService(reference2));
            assertEquals(0, factory.services.size());
            registration.unregister();
            assertNull(tb1Context.getServiceObjects(reference1));
            tb1.stop();
            try {
                tb1Context.getServiceObjects(reference1);
                fail("expected exception");
            } catch (IllegalStateException e) {
                // expected
            }
            assertEquals(reference1, objects1.getServiceReference());
        } finally {
            tb1.uninstall();
        }
    }

    public void testPrototypeConsumerWithPrototypeScope() throws Exception {
        Bundle tb1 = install("serviceregistry.tb1.jar");
        assertNotNull(tb1);
        try {
            tb1.start();
            assertTrue((tb1.getState() & Bundle.ACTIVE) != 0);
            BundleContext tb1Context = tb1.getBundleContext();
            assertNotNull(tb1Context);

            Hashtable<String, String> properties = new Hashtable<String, String>();
            properties.put("test", "yes");
            properties.put(Constants.SERVICE_SCOPE, Constants.SCOPE_SINGLETON);
            TestPrototypeServiceFactory factory = new TestPrototypeServiceFactory();
            ServiceRegistration<TestService> registration = tb1Context.registerService(TestService.class, factory, properties);
            assertNotNull(registration);
            ServiceReference<TestService> reference1 = context.getServiceReference(TestService.class);
            assertNotNull(reference1);
            ServiceReference<TestService> reference2 = tb1Context.getServiceReference(TestService.class);
            assertNotNull(reference2);

            assertEquals("service.scope not set correctly", Constants.SCOPE_PROTOTYPE, reference1.getProperty(Constants.SERVICE_SCOPE));
            assertEquals("service.scope not set correctly", Constants.SCOPE_PROTOTYPE, reference2.getProperty(Constants.SERVICE_SCOPE));

            ServiceObjects<TestService> objects1 = context.getServiceObjects(reference1);
            assertNotNull(objects1);
            assertEquals(reference1, objects1.getServiceReference());
            ServiceObjects<TestService> objects2 = tb1Context.getServiceObjects(reference2);
            assertNotNull(objects2);
            assertEquals(reference2, objects2.getServiceReference());

            assertEquals(0, factory.services.size());

            TestService service1 = context.getService(reference1);
            assertNotNull(service1);
            assertEquals(1, factory.services.size());
            assertEquals(1, factory.services.get(self).size());
            assertTrue(factory.services.get(self).contains(service1));
            assertEquals(self.getBundleId(), service1.getId());

            TestService service2 = tb1Context.getService(reference2);
            assertNotNull(service2);
            assertEquals(2, factory.services.size());
            assertEquals(1, factory.services.get(tb1).size());
            assertTrue(factory.services.get(tb1).contains(service2));
            assertEquals(tb1.getBundleId(), service2.getId());

            assertNotSame(service1, service2);

            assertSame("service object not the same", service1, context.getService(reference1));
            assertEquals(2, factory.services.size());
            assertEquals(1, factory.services.get(self).size());
            TestService service11 = objects1.getService();
            assertNotSame("service object the same", service1, service11);
            assertEquals(2, factory.services.size());
            assertEquals(2, factory.services.get(self).size());
            TestService service12 = objects1.getService();
            assertNotSame("service object the same", service1, service12);
            assertNotSame("service object the same", service11, service12);
            assertEquals(2, factory.services.size());
            assertEquals(3, factory.services.get(self).size());

            assertSame("service object not the same", service2, tb1Context.getService(reference2));
            assertEquals(2, factory.services.size());
            assertEquals(1, factory.services.get(tb1).size());
            TestService service21 = objects2.getService();
            assertNotSame("service object the same", service2, service21);
            assertEquals(2, factory.services.size());
            assertEquals(2, factory.services.get(tb1).size());
            TestService service22 = objects2.getService();
            assertNotSame("service object the same", service2, service22);
            assertNotSame("service object the same", service21, service22);
            assertEquals(2, factory.services.size());
            assertEquals(3, factory.services.get(tb1).size());

            try {
                objects1.ungetService(service1);
                fail("expected exception");
            } catch (IllegalArgumentException e) {
                // expected
            }
            try {
                objects2.ungetService(service2);
                fail("expected exception");
            } catch (IllegalArgumentException e) {
                // expected
            }

            assertTrue("service use count wrong", context.ungetService(reference1));
            assertEquals(2, factory.services.size());
            assertEquals(3, factory.services.get(self).size());
            assertTrue("service use count wrong", context.ungetService(reference1));
            assertEquals(2, factory.services.size());
            assertEquals(2, factory.services.get(self).size());

            assertFalse("service use count wrong", context.ungetService(reference1));
            assertEquals(2, factory.services.size());
            assertEquals(2, factory.services.get(self).size());
            assertFalse("service use count wrong", context.ungetService(reference1));
            assertEquals(2, factory.services.size());
            assertEquals(2, factory.services.get(self).size());

            objects1.ungetService(service11);
            assertEquals(2, factory.services.size());
            assertEquals(1, factory.services.get(self).size());
            try {
                objects1.ungetService(service11);
                fail("expected exception");
            } catch (IllegalArgumentException e) {
                // expected
            }
            objects1.ungetService(service12);
            assertEquals(1, factory.services.size());
            assertNull(factory.services.get(self));
            try {
                objects1.ungetService(service12);
                fail("expected exception");
            } catch (IllegalArgumentException e) {
                // expected
            }

            assertTrue("service use count wrong", tb1Context.ungetService(reference2));
            assertEquals(1, factory.services.size());
            assertEquals(3, factory.services.get(tb1).size());
            assertTrue("service use count wrong", tb1Context.ungetService(reference2));
            assertEquals(1, factory.services.size());
            assertEquals(2, factory.services.get(tb1).size());

            assertFalse("service use count wrong", tb1Context.ungetService(reference2));
            assertEquals(1, factory.services.size());
            assertEquals(2, factory.services.get(tb1).size());
            assertFalse("service use count wrong", tb1Context.ungetService(reference2));
            assertEquals(1, factory.services.size());
            assertEquals(2, factory.services.get(tb1).size());

            objects2.ungetService(service21);
            assertEquals(1, factory.services.size());
            assertEquals(1, factory.services.get(tb1).size());
            try {
                objects2.ungetService(service21);
                fail("expected exception");
            } catch (IllegalArgumentException e) {
                // expected
            }
            objects2.ungetService(service22);
            assertEquals(0, factory.services.size());
            try {
                objects2.ungetService(service22);
                fail("expected exception");
            } catch (IllegalArgumentException e) {
                // expected
            }

            registration.unregister();
            assertNull(tb1Context.getServiceObjects(reference1));
            tb1.stop();
            try {
                tb1Context.getServiceObjects(reference1);
                fail("expected exception");
            } catch (IllegalStateException e) {
                // expected
            }

        } finally {
            tb1.uninstall();
        }
    }

	public void testPrototypeConsumerWithPrototypeScopeOneService()
			throws Exception {
		Bundle tb1 = install("serviceregistry.tb1.jar");
		assertNotNull(tb1);
		try {
			tb1.start();
			assertTrue((tb1.getState() & Bundle.ACTIVE) != 0);
			BundleContext tb1Context = tb1.getBundleContext();
			assertNotNull(tb1Context);

			Hashtable<String,String> properties = new Hashtable<String,String>();
			properties.put("test", "yes");
			TestPrototypeServiceFactoryOneObject factory = new TestPrototypeServiceFactoryOneObject();
			ServiceRegistration<TestService> registration = tb1Context
					.registerService(TestService.class, factory, properties);
			assertNotNull(registration);
			ServiceReference<TestService> reference1 = context
					.getServiceReference(TestService.class);
			assertNotNull(reference1);
			ServiceReference<TestService> reference2 = tb1Context
					.getServiceReference(TestService.class);
			assertNotNull(reference2);

			ServiceObjects<TestService> objects1 = context
					.getServiceObjects(reference1);
			assertNotNull(objects1);
			assertEquals(reference1, objects1.getServiceReference());
			ServiceObjects<TestService> objects2 = tb1Context
					.getServiceObjects(reference2);
			assertNotNull(objects2);
			assertEquals(reference2, objects2.getServiceReference());

			assertEquals(0, factory.services.size());

			TestService service1 = objects1.getService();
			assertNotNull(service1);
			assertEquals(1, factory.services.size());
			assertEquals(factory.services.get(self), service1);
			assertEquals(self.getBundleId(), service1.getId());

			TestService service2 = objects2.getService();
			assertNotNull(service2);
			assertEquals(2, factory.services.size());
			assertEquals(factory.services.get(tb1), service2);
			assertEquals(tb1.getBundleId(), service2.getId());

			assertNotSame(service1, service2);
			assertEquals(1, service1.getCount().get());
			assertEquals(1, service2.getCount().get());

			TestService service11 = objects1.getService();
			assertSame("service object not same", service1, service11);
			assertEquals(2, service1.getCount().get());
			TestService service12 = objects1.getService();
			assertSame("service object not same", service1, service12);
			assertEquals(3, service1.getCount().get());
			assertSame("service object not same", service11, service12);

			TestService service21 = objects2.getService();
			assertSame("service object not same", service2, service21);
			assertEquals(2, service2.getCount().get());
			TestService service22 = objects2.getService();
			assertSame("service object not same", service2, service22);
			assertEquals(3, service2.getCount().get());
			assertSame("service object not same", service21, service22);


			objects1.ungetService(service1);
			assertEquals(factory.services.get(self), service1);
			assertEquals(3, service1.getCount().get());
			objects1.ungetService(service1);
			assertEquals(factory.services.get(self), service1);
			assertEquals(3, service1.getCount().get());
			objects1.ungetService(service1);
			assertNull(factory.services.get(self));
			try {
				objects1.ungetService(service1);
				fail("expected exception");
			} catch (IllegalArgumentException e) {
				// expected
			}

			objects2.ungetService(service2);
			assertEquals(factory.services.get(tb1), service2);
			assertEquals(3, service2.getCount().get());
			objects2.ungetService(service2);
			assertEquals(factory.services.get(tb1), service2);
			assertEquals(3, service2.getCount().get());
			objects2.ungetService(service2);
			assertNull(factory.services.get(tb1));
			try {
				objects2.ungetService(service2);
				fail("expected exception");
			} catch (IllegalArgumentException e) {
				// expected
			}

			registration.unregister();
			assertNull(tb1Context.getServiceObjects(reference1));
			tb1.stop();
			try {
				tb1Context.getServiceObjects(reference1);
				fail("expected exception");
			} catch (IllegalStateException e) {
				// expected
			}

		} finally {
			tb1.uninstall();
		}
	}

    public void testPrototypeScopeCleanup() throws Exception {
        Bundle tb1 = install("serviceregistry.tb1.jar");
        assertNotNull(tb1);
        try {
            tb1.start();
            assertTrue((tb1.getState() & Bundle.ACTIVE) != 0);
            BundleContext tb1Context = tb1.getBundleContext();
            assertNotNull(tb1Context);

            Hashtable<String, String> properties = new Hashtable<String, String>();
            properties.put("test", "yes");
            properties.put(Constants.SERVICE_SCOPE, Constants.SCOPE_SINGLETON);
            TestPrototypeServiceFactory factory = new TestPrototypeServiceFactory();
            ServiceRegistration<TestService> registration = context.registerService(TestService.class, factory, properties);
            assertNotNull(registration);
            try {
                ServiceReference<TestService> reference = tb1Context.getServiceReference(TestService.class);
                assertNotNull(reference);

                assertEquals("service.scope not set correctly", Constants.SCOPE_PROTOTYPE, reference.getProperty(Constants.SERVICE_SCOPE));

                ServiceObjects<TestService> objects = tb1Context.getServiceObjects(reference);
                assertNotNull(objects);
                assertEquals(reference, objects.getServiceReference());

                assertEquals(0, factory.services.size());

                TestService service1 = tb1Context.getService(reference);
                assertNotNull(service1);
                assertEquals(1, factory.services.size());
                assertEquals(1, factory.services.get(tb1).size());
                assertTrue(factory.services.get(tb1).contains(service1));
                assertEquals(tb1.getBundleId(), service1.getId());

                TestService service2 = objects.getService();
                assertNotSame("service object the same", service1, service2);
                assertEquals(1, factory.services.size());
                assertEquals(2, factory.services.get(tb1).size());
                assertTrue(factory.services.get(tb1).contains(service2));

                TestService service3 = objects.getService();
                assertNotSame("service object the same", service2, service3);
                assertEquals(1, factory.services.size());
                assertEquals(3, factory.services.get(tb1).size());
                assertTrue(factory.services.get(tb1).contains(service3));

                tb1.stop();
                assertEquals(0, factory.services.size());
                assertEquals(reference, objects.getServiceReference());
            } finally {
                registration.unregister();
            }
        } finally {
            tb1.uninstall();
        }
    }

	public void testPrototypeScopeCleanupOnUnregister() throws Exception {
		Bundle tb1 = install("serviceregistry.tb1.jar");
		assertNotNull(tb1);
		try {
			tb1.start();
			assertTrue((tb1.getState() & Bundle.ACTIVE) != 0);
			BundleContext tb1Context = tb1.getBundleContext();
			assertNotNull(tb1Context);

			Hashtable<String, String> properties = new Hashtable<String, String>();
			properties.put("test", "yes");
			TestPrototypeServiceFactory factory = new TestPrototypeServiceFactory();
			ServiceRegistration<TestService> registration = context.registerService(TestService.class, factory, properties);
			assertNotNull(registration);
			try {
				ServiceReference<TestService> reference = tb1Context.getServiceReference(TestService.class);
				assertNotNull(reference);

				assertEquals("service.scope not set correctly", Constants.SCOPE_PROTOTYPE, reference.getProperty(Constants.SERVICE_SCOPE));

				ServiceObjects<TestService> objects = tb1Context.getServiceObjects(reference);
				assertNotNull(objects);
				assertEquals(reference, objects.getServiceReference());

				assertEquals(0, factory.services.size());

				TestService service1 = tb1Context.getService(reference);
				assertNotNull(service1);

				TestService service2 = objects.getService();
				assertNotSame("service object the same", service1, service2);

				TestService service3 = objects.getService();
				assertNotSame("service object the same", service2, service3);

				assertEquals(1, factory.services.size());
				assertEquals(3, factory.services.get(tb1).size());
			} finally {
				registration.unregister();
			}
			// test clean up on unregister now;
			assertEquals(0, factory.services.size());
		} finally {
			tb1.uninstall();
		}
	}

    public interface TestService {
        public long getId();

		public AtomicInteger getCount();
    }

    static class TestServiceImpl implements TestService {
        private final long id;
		private final AtomicInteger	count;

        TestServiceImpl(long id) {
            this.id = id;
			count = new AtomicInteger();
        }

		@Override
		public long getId() {
            return id;
        }

		@Override
		public AtomicInteger getCount() {
			return count;
		}
    }

    static class TestServiceFactory implements ServiceFactory<TestService> {
        Map<Bundle, TestService> services = Collections.synchronizedMap(new HashMap<Bundle, TestService>());

		@Override
		public TestService getService(Bundle bundle,
				ServiceRegistration<TestService> registration) {
            TestService service = new TestServiceImpl(bundle.getBundleId());
            assertNull(services.get(bundle));
            services.put(bundle, service);
            return service;
        }

		@Override
		public void ungetService(Bundle bundle,
				ServiceRegistration<TestService> registration,
				TestService service) {
            assertEquals(services.get(bundle), service);
            assertEquals(bundle.getBundleId(), service.getId());
            services.remove(bundle);
        }
    }

    static class TestPrototypeServiceFactory implements PrototypeServiceFactory<TestService> {
        Map<Bundle, List<TestService>> services = Collections.synchronizedMap(new HashMap<Bundle, List<TestService>>());

		@Override
		public TestService getService(Bundle bundle,
				ServiceRegistration<TestService> registration) {
            List<TestService> bundleServices = services.get(bundle);
            if (bundleServices == null) {
                bundleServices = new ArrayList<TestService>();
            }
            TestService service = new TestServiceImpl(bundle.getBundleId());
            bundleServices.add(service);
            services.put(bundle, bundleServices);
            return service;
        }

		@Override
		public void ungetService(Bundle bundle,
				ServiceRegistration<TestService> registration,
				TestService service) {
            List<TestService> bundleServices = services.get(bundle);
            assertNotNull(bundleServices);
            assertTrue(bundleServices.contains(service));
            assertEquals(bundle.getBundleId(), service.getId());
            bundleServices.remove(service);
            if (bundleServices.isEmpty()) {
                services.remove(bundle);
            }
            else {
                services.put(bundle, bundleServices);
            }
        }
    }

	static class TestPrototypeServiceFactoryOneObject
			implements PrototypeServiceFactory<TestService> {
		Map<Bundle,TestService> services = Collections
				.synchronizedMap(new HashMap<>());

		@Override
		public TestService getService(Bundle bundle,
				ServiceRegistration<TestService> registration) {
			TestService service = services.get(bundle);
			if (service == null) {
				service = new TestServiceImpl(bundle.getBundleId());
				services.put(bundle, service);
			}
			service.getCount().getAndIncrement();
			return service;
		}

		@Override
		public void ungetService(Bundle bundle,
				ServiceRegistration<TestService> registration,
				TestService service) {
			TestService bundleService = services.get(bundle);
			assertNotNull(bundleService);
			assertSame(bundleService, service);
			assertEquals(bundle.getBundleId(), service.getId());
			services.remove(bundle);
		}
	}
}

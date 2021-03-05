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

package org.osgi.test.cases.framework.secure.junit.serviceregistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.framework.secure.junit.serviceregistry.export.ReferenceProvider;
import org.osgi.test.cases.framework.secure.junit.serviceregistry.export.TestService;
import org.osgi.test.support.OSGiTestCase;

public class ServiceObjectsPermissionTests extends OSGiTestCase {

    private BundleContext context;

    protected void setUp() {
        context = getContext();
    }

    public void testHasServicePermission() throws Exception {
        Bundle tb1 = install("serviceregistry.tb1.jar");
        assertNotNull(tb1);
        try {
            TestPrototypeServiceFactory factory = new TestPrototypeServiceFactory();
            ServiceRegistration<TestService> registration = context.registerService(TestService.class, factory, null);
            assertNotNull(registration);
            try {
                assertEquals(0, factory.services.size());

                tb1.start();
                assertTrue((tb1.getState() & Bundle.ACTIVE) != 0);

                assertEquals(1, factory.services.size());
                assertEquals(1, factory.services.get(tb1).size());

                tb1.stop();
                assertTrue((tb1.getState() & Bundle.ACTIVE) == 0);

                assertEquals(0, factory.services.size());

            } finally {
                registration.unregister();
            }
        } finally {
            tb1.uninstall();
        }
    }

    public void testNotServicePermission() throws Exception {
        Bundle tb2 = install("serviceregistry.tb2.jar");
        assertNotNull(tb2);
        try {
            TestPrototypeServiceFactory factory = new TestPrototypeServiceFactory();
            final ServiceRegistration<TestService> registration = context.registerService(TestService.class, factory, null);
            assertNotNull(registration);
            try {
                ServiceRegistration<ReferenceProvider> providerReg = context.registerService(ReferenceProvider.class, new ReferenceProvider() {
                    public ServiceReference<TestService> getReference() {
                        return registration.getReference();
                    }
                }, null);
                assertNotNull(providerReg);
                try {

                    assertEquals(0, factory.services.size());

                    tb2.start();
                    assertTrue((tb2.getState() & Bundle.ACTIVE) != 0);

                    assertEquals(0, factory.services.size());

                    tb2.stop();
                    assertTrue((tb2.getState() & Bundle.ACTIVE) == 0);

                } finally {
                    providerReg.unregister();
                }
                assertEquals(0, factory.services.size());

            } finally {
                registration.unregister();
            }
        } finally {
            tb2.uninstall();
        }
    }

    static class TestServiceImpl implements TestService {
        private final long id;

        TestServiceImpl(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }
    }

    static class TestPrototypeServiceFactory implements PrototypeServiceFactory<TestService> {
        Map<Bundle, List<TestService>> services = Collections.synchronizedMap(new HashMap<Bundle, List<TestService>>());

        public TestService getService(Bundle bundle, ServiceRegistration<TestService> registration) {
            List<TestService> bundleServices = services.get(bundle);
            if (bundleServices == null) {
                bundleServices = new ArrayList<TestService>();
            }
            TestService service = new TestServiceImpl(bundle.getBundleId());
            bundleServices.add(service);
            services.put(bundle, bundleServices);
            return service;
        }

        public void ungetService(Bundle bundle, ServiceRegistration<TestService> registration, TestService service) {
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
}

/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
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

package org.osgi.test.cases.blueprint.components.serviceexport;

import java.util.Map;

import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.TestServiceOne;


/**
 * A listener class for validating register listener calls involving prototype
 * scope services
 */
public class PrototypeRegistrationStateListener extends ServiceOneRegistrationListener {
    // our injected service registration proxy
    protected ServiceRegistration registration;

    public PrototypeRegistrationStateListener(String componentId) {
        super(componentId);
    }

    public void setRegistration(ServiceRegistration proxy) {
        registration = proxy;
    }

    public void registered(TestServiceOne service, Map serviceProperties) {
        // There is just one copy of the prototype scope bean obtained by the service, so this should
        // never be null for these tests.
        AssertionService.assertNotNull(this, "Null service instance expected for a prototype component", service);

        // this should be valid
        if (registration != null) {
            AssertionService.assertNotNull(this, "Null service reference from ServiceRegistration proxy", registration.getReference());
        }
        registered(TestServiceOne.class, serviceProperties, service);
    }

    public void unregistered(TestServiceOne service, Map serviceProperties) {
        // There is just one copy of the prototype scope bean obtained by the service, so this should
        // never be null for these tests.
        AssertionService.assertNotNull(this, "Null service instance expected for a prototype component", service);
        // this should be valid
        if (registration != null) {
            AssertionService.assertNotNull(this, "Null service reference from ServiceRegistration proxy", registration.getReference());
        }
        unregistered(TestServiceOne.class, serviceProperties, service);
    }
}


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
 * A base class for validating the state of the ServiceRegistraton proxy
 * during registration listener calls.
 */
public class RegistrationStateListener extends ServiceOneRegistrationListener {
    // our injected service registration proxy
    protected ServiceRegistration registration;

    public RegistrationStateListener(String componentId) {
        super(componentId);
    }

    public void setRegistration(ServiceRegistration proxy) {
        registration = proxy;
    }

    public void registered(TestServiceOne service, Map serviceProperties) {
        AssertionService.assertNotNull(this, "Non null service instance expected for a singleton component", service);
        // this should be valid
        if (registration != null) {
            AssertionService.assertNotNull(this, "Null service reference from ServiceRegistration proxy", registration.getReference());
        }
        registered(TestServiceOne.class, serviceProperties, service);
    }

    public void unregistered(TestServiceOne service, Map serviceProperties) {
        AssertionService.assertNotNull(this, "Non null service instance expected for a singleton component", service);
        // this should be valid
        if (registration != null) {
            AssertionService.assertNotNull(this, "Null service reference from ServiceRegistration proxy", registration.getReference());
        }
        unregistered(TestServiceOne.class, serviceProperties, service);
    }
}


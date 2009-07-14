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

import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.TestServiceOne;
import org.osgi.test.cases.blueprint.services.ServiceOneRegistrationListenerInterface;

import org.osgi.framework.ServiceFactory;

/**
 * A base class for different reference listeners used to validate
 * reference registration/deregistration calls.
 */
public class ServiceOneRegistrationListener extends RegistrationListener implements ServiceOneRegistrationListenerInterface {
    public ServiceOneRegistrationListener(String componentId) {
        super(componentId);
    }

    public void registered(TestServiceOne service, Map serviceProperties) {
        registered(TestServiceOne.class, serviceProperties, service);
    }

    public void unregistered(TestServiceOne service, Map serviceProperties) {
        unregistered(TestServiceOne.class, serviceProperties, service);
    }

    // the following signature variations are to ensure that the multiple
    // signatures can be tested
    public void registered(ServiceFactory service, Map serviceProperties) {
        registered(ServiceFactory.class, serviceProperties, service);
    }

    public void unregistered(ServiceFactory service, Map serviceProperties) {
        unregistered(ServiceFactory.class, serviceProperties, service);
    }

    public void badRegistered(TestServiceOne service) {
        AssertionService.fail(this, "Invalid registered method call");
    }

    public void badUnregistered(TestServiceOne service) {
        AssertionService.fail(this, "Invalid unregistered method call");
    }

    void nonPublicRegistered(TestServiceOne service, Map serviceProperties) {
        AssertionService.fail(this, "Invalid registered method call");
    }

    void nonPublicUnregistered(TestServiceOne service, Map serviceProperties) {
        AssertionService.fail(this, "Invalid unregistered method call");
    }
}


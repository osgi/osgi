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

package org.osgi.test.cases.blueprint.components.cmsupport;

import java.util.Map;

import org.osgi.test.cases.blueprint.components.serviceexport.RegistrationListener;

import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.TestServiceOne;
import org.osgi.test.cases.blueprint.services.ServiceOneRegistrationListenerInterface;


/**
 * A base class for different reference listeners used to validate
 * reference registration/deregistration calls.
 */
public class ManagedComponentListener extends RegistrationListener {
    public ManagedComponentListener(String componentId) {
        super(componentId);
    }

    public void registered(ManagedComponentInjection service, Map serviceProperties) {
        registered(ManagedComponentInjection.class, serviceProperties);
    }

    public void unregistered(ManagedComponentInjection service, Map serviceProperties) {
        unregistered(ManagedComponentInjection.class, serviceProperties);
    }

    public void badRegistered(ManagedComponentInjection service) {
        AssertionService.fail(this, "Invalid registered method call");
    }

    public void badUnregistered(ManagedComponentInjection service) {
        AssertionService.fail(this, "Invalid unregistered method call");
    }
}


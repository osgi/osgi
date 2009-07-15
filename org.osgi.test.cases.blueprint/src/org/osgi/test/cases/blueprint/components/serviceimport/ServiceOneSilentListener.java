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

package org.osgi.test.cases.blueprint.components.serviceimport;

import java.util.Map;

import org.osgi.framework.ServiceReference;

import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.TestServiceOne;
import org.osgi.test.cases.blueprint.services.ServiceOneListenerInterface;


/**
 * A base class for different reference listeners used to validate
 * reference registration/deregistration calls.  This version makes
 * no calls using the provided listener object to ensure the service
 * proxy does not request the service.  This is used to test
 * lazy proxy service request semantics.
 * this one is also a bit of headache, since it implements all of the
 * supported bind()/unbind() signatures using a common name.  This will
 * drive a lot of listener activity.
 */
public class ServiceOneSilentListener extends ServiceListener implements ServiceOneListenerInterface {

    public ServiceOneSilentListener(String componentId) {
        super(componentId);
    }

    public void bind(TestServiceOne service, Map serviceProperties) {
        AssertionService.assertNotNull(this, "Null service instance passed to reference-listener bind()", service);
        AssertionService.assertNotNull(this, "Null properties map passed to reference-listener bind()", service);
        bind(TestServiceOne.class, serviceProperties);
    }

    public void unbind(TestServiceOne service, Map serviceProperties) {
        AssertionService.assertNotNull(this, "Null service instance passed to reference-listener unbind()", service);
        AssertionService.assertNotNull(this, "Null properties map passed to reference-listener unbind()", service);
        unbind(TestServiceOne.class, serviceProperties);
    }

    public void bind(TestServiceOne service) {
        AssertionService.assertNotNull(this, "Null service instance passed to reference-listener bind()", service);
        bind(TestServiceOne.class);
    }

    public void unbind(TestServiceOne service) {
        AssertionService.assertNotNull(this, "Null service instance passed to reference-listener unbind()", service);
        unbind(TestServiceOne.class);
    }

    public void bind(ServiceReference service) {
        AssertionService.assertNotNull(this, "Null ServiceReference passed to reference-listener bind()", service);
        bindReference(service);
    }

    public void unbind(ServiceReference service) {
        AssertionService.assertNotNull(this, "Null Reference passed to reference-listener unbind()", service);
        unbindReference(service);
    }

    public void bind(Object service) {
        AssertionService.assertNotNull(this, "Null ServiceReference passed to reference-listener bind()", service);
        bind(Object.class);
    }

    public void unbind(Object service) {
        AssertionService.assertNotNull(this, "Null Reference passed to reference-listener unbind()", service);
        unbind(Object.class);
    }

    public void bind(Object service, Map serviceProperties) {
        AssertionService.assertNotNull(this, "Null ServiceReference passed to reference-listener bind()", service);
        bind(Object.class, serviceProperties);
    }

    public void unbind(Object service, Map serviceProperties) {
        AssertionService.assertNotNull(this, "Null Reference passed to reference-listener unbind()", service);
        unbind(Object.class, serviceProperties );
    }
}


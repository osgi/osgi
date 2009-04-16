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

import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.TestServiceOne;


/**
 * A base class for different reference listeners used to validate
 * reference registration/deregistration calls.
 */
public class ServiceOneListener extends ServiceListener {

    public ServiceOneListener(String componentId) {
        super(componentId);
    }

    public void bind(TestServiceOne service, Map serviceProperties) {
        try {
            // Some tests we switch back and forth, so check the name
            // to see which result we should be getting
            if ("BadService".equals(service.getServiceName())) {
                AssertionService.assertFalse(this, "Bad service call", service.testOne());
            }
            else {
                AssertionService.assertTrue(this, "Bad service call", service.testOne());
            }
        } catch (Throwable e) {
            AssertionService.fail(this, "Unexpected exception in service listener", e);
        }
        bind(TestServiceOne.class, serviceProperties);
    }

    public void unbind(TestServiceOne service, Map serviceProperties) {
        try {
            // Some tests we switch back and forth, so check the name
            // to see which result we should be getting
            if ("BadService".equals(service.getServiceName())) {
                AssertionService.assertFalse(this, "Bad service call", service.testOne());
            }
            else {
                AssertionService.assertTrue(this, "Bad service call", service.testOne());
            }
        } catch (Throwable e) {
            AssertionService.fail(this, "Unexpected exception in service listener", e);
        }
        unbind(TestServiceOne.class, serviceProperties);
    }

    public void badBind(TestServiceOne service) {
        AssertionService.fail(this, "Bad bind call");
    }

    public void badUnbind(TestServiceOne service) {
        AssertionService.fail(this, "Bad unbind call");
    }
}


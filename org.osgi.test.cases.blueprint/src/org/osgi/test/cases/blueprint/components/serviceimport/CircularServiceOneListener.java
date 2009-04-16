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
public class CircularServiceOneListener extends ServiceOneListener {
    // an injected service we can test in the service listeners
    protected TestServiceOne injectedService;


    public CircularServiceOneListener(String componentId) {
        super(componentId);
    }

    public void setOne(TestServiceOne service) {
        // the service must not be null and the method must return true.
        AssertionService.assertNotNull(this, "Null service reference received", service);
        checkService(service.testOne());
        injectedService = service;
    }

    /**
     * Override of the default bind/unbind methods to
     * verify that our held service reference is also good.
     *
     * @param service
     * @param serviceProperties
     */
    public void bind(TestServiceOne service, Map serviceProperties) {
        // save the reference if we don't have one yet
        if (injectedService == null) {
            injectedService = service;
        }
        // this should be a good service call, always
        AssertionService.assertTrue(this, "Bad service call", service.testOne());
        bind(TestServiceOne.class, serviceProperties);
    }

    public void unbind(TestServiceOne service, Map serviceProperties) {
        // this should be a good service call, always
        AssertionService.assertTrue(this, "Bad service call", service.testOne());
        if (injectedService != null) {
            // test the injected one as well
            AssertionService.assertTrue(this, "Bad service call", injectedService.testOne());
        }
        // clear this out
        injectedService = null;
        unbind(TestServiceOne.class, serviceProperties);
    }


    /**
     * Utility method for validating the service implementation.
     *
     * @param result The result of the service call.
     */
    protected void checkService(boolean result) {
        if (result) {
            AssertionService.sendEvent(this, AssertionService.SERVICE_SUCCESS);
        }
        else {
            AssertionService.assertTrue(this, "Incorrect service result received", result);
        }
    }
}



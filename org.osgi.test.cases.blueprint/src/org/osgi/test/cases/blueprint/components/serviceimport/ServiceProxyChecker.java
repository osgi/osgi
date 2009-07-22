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

import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;
import org.osgi.test.cases.blueprint.services.TestServiceOne;
import org.osgi.test.cases.blueprint.services.TestServiceTwo;


/**
 * A base class for testing service import/export.  This version really just gets
 * injected with a service instance and then calls a method on the proxy to force the
 * service to be requested.
 */
public class ServiceProxyChecker extends BaseTestComponent {
    // two test services to test reference validity
    protected TestServiceOne serviceOne;
    protected TestServiceTwo serviceTwo;

    public ServiceProxyChecker(String componentId) {
        super(componentId);
    }

    /**
     * Inject a reference to TestServiceOne into this component;
     *
     * @param service The service instance
     */
    public void setOne(TestServiceOne service) {
        AssertionService.assertNotNull(this, "Null service reference injected", service);
        AssertionService.assertTrue(this, "Bad service result", service.testOne());
        AssertionService.sendEvent(this, AssertionService.SERVICE_SUCCESS);
        serviceOne = service;
    }

    /**
     * Inject a reference to TestServiceTwo into this component;
     *
     * @param service The service instance
     */
    public void setTwo(TestServiceTwo service) {
        AssertionService.assertNotNull(this, "Null service reference injected", service);
        AssertionService.assertTrue(this, "Bad service result", service.testTwo());
        AssertionService.sendEvent(this, AssertionService.SERVICE_SUCCESS);
        serviceTwo = service;
    }
}


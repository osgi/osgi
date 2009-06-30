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

import java.util.List;

import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;
import org.osgi.test.cases.blueprint.services.TestGoodService;
import org.osgi.test.cases.blueprint.services.TestGoodServiceSubclass;
import org.osgi.test.cases.blueprint.services.TestServiceAllSubclass;
import org.osgi.test.cases.blueprint.services.TestServiceOne;
import org.osgi.test.cases.blueprint.services.TestServiceTwo;
import org.osgi.test.cases.blueprint.services.TestServiceTwoSubclass;


/**
 * A base class for implementing components that test service dynamics by causing
 * changes in the registration state of services.  This class is injected with a
 * ServiceManager reference that can be used to register/deregister services.
 * this version makes no calls on the injected service instances
 */
public class ServiceReferenceSilentChecker extends BaseTestComponent {

    public ServiceReferenceSilentChecker(String componentId) {
        super(componentId);
    }

    public ServiceReferenceSilentChecker(String componentId, TestGoodService service) {
        super(componentId);
        AssertionService.assertNotNull(this, "Null TestGoodService service object injected", service);
    }

    public ServiceReferenceSilentChecker(String componentId, TestServiceTwoSubclass service) {
        super(componentId);
        AssertionService.assertNotNull(this, "Null TestServiceTwoSubclass service object injected", service);
    }

    public ServiceReferenceSilentChecker(String componentId, TestServiceOne service) {
        super(componentId);
        AssertionService.assertNotNull(this, "Null TestServiceOne service object injected", service);
    }

    public ServiceReferenceSilentChecker(String componentId, TestServiceTwo service) {
        super(componentId);
    }

    /**
     * Inject a reference to TestGoodService into this component;
     *
     * @param service The service instance
     */
    public void setGood(TestGoodService service) {
        AssertionService.assertNotNull(this, "Null TestGoodService service object injected", service);
    }

    /**
     * Inject a reference to TestGoodService into this component;
     *
     * @param service The service instance
     */
    public void setGoodSubclass(TestGoodServiceSubclass service) {
        AssertionService.assertNotNull(this, "Null TestGoodServiceSubclass service object injected", service);
    }

    /**
     * Inject a reference to TestServiceOne into this component;
     *
     * @param service The service instance
     */
    public void setOne(TestServiceOne service) {
        AssertionService.assertNotNull(this, "Null TestServiceOne service object injected", service);
    }

    /**
     * Inject a reference to TestServiceTwo into this component;
     *
     * @param service The service instance
     */
    public void setTwo(TestServiceTwo service) {
        AssertionService.assertNotNull(this, "Null TestServiceTwo service object injected", service);
    }

    /**
     * Inject a reference to TestServiceAllSubclass into this component;
     *
     * @param service The service instance
     */
    public void setAllSubclass(TestServiceAllSubclass service) {
        AssertionService.assertNotNull(this, "Null TestServiceAllSubclass service object injected", service);
    }

    /**
     * Inject a reference to TestServiceTwoSubclass into this component;
     *
     * @param service The service instance
     */
    public void setTwoSubclass(TestServiceTwoSubclass service) {
        AssertionService.assertNotNull(this, "Null TestServiceTwoSubclass service object injected", service);
    }


    public void setList(List references) {
        AssertionService.assertNotNull(this, "Null reference-list injected", references);
    }
}


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
 * Tests for service references that are requested with
 * out an interface name.  The injected proxy object should
 * only match the Object signature and should not implement
 * any of the exported interfaces.
 */
public class ServiceObjectSilentChecker extends BaseTestComponent {

    public ServiceObjectSilentChecker(String componentId) {
        super(componentId);
    }

    /**
     * Inject a reference to an interfaceless proxy into this component.
     *
     * @param service The service instance
     */
    public void setObject(Object service) {
        AssertionService.assertNotNull(this, "Null Object service object injected", service);
        AssertionService.assertFalse(this, "Proxy Object implements exported interface", service instanceof TestServiceOne);
    }


    public void setList(List references) {
        AssertionService.assertNotNull(this, "Null reference-list injected", references);
        Object service = references.get(0);
        AssertionService.assertNotNull(this, "Null Object service object injected", service);
        AssertionService.assertFalse(this, "Proxy Object implements exported interface", service instanceof TestServiceOne);
    }
}


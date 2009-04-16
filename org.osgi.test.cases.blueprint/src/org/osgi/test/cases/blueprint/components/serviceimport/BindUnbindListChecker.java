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
 * Test for injection of an empty List of services no services are available.  This
 * will also register a service using the service manager to see that the new
 * registration is picked up.
 */
public class BindUnbindListChecker extends ReferenceCollectionChecker {

    public BindUnbindListChecker(String componentId) {
        super(componentId);
    }

    /**
     * This is the actual test method.  After the component is
     * instantiated, the init method is called to drive the
     * component test logic.  Each concrete test will override this
     * and verify that the injected results are correct.  Tests
     * dealing with service dynamics will use the ServiceManager to
     * modify the state of the registered services.
     */
    public void init() {
        // ok, all we do is toggle these back and forth and let the
        // listeners handle things
        serviceManager.toggleServices();
        // restored to original state
        serviceManager.toggleServices();
        // and exit in the other state
        serviceManager.toggleServices();
        super.init();
    }


    /**
     * some methods for doubling as service listeners.
     */
    public void bind(TestServiceOne service, Map serviceProperties) {
        try {
            // TODO:  The listener is getting called before the
            // reference is injected
            if (injectedList == null) {
                // do normal "I was called broadcast"
                super.bind(service, serviceProperties);
                return;
            }
            // we're working with a collection that should always have a single
            // item on the bind/unbind calls.
            TestServiceOne refService = (TestServiceOne)injectedList.get(0);
            AssertionService.assertTrue(this, "Bad service call", refService.testOne());

            // do normal "I was called broadcast"
            super.bind(service, serviceProperties);
        } catch (Throwable e) {
            AssertionService.fail(this, "Unexpected exception in bind method", e);
        }
    }

    public void unbind(TestServiceOne service, Map serviceProperties) {
        try {
            // TODO:  The listener is getting called before the
            // reference is injected
            if (injectedList == null) {
                // do normal "I was called broadcast"
                super.bind(service, serviceProperties);
                return;
            }
            // we're working with a collection that should always have a single
            // item on the bind/unbind calls.
            TestServiceOne refService = (TestServiceOne)injectedList.get(0);
            AssertionService.assertTrue(this, "Bad service call", refService.testOne());

            // do normal "I was called broadcast"
            super.unbind(service, serviceProperties);
        } catch (Throwable e) {
            AssertionService.fail(this, "Unexpected exception in unbind method", e);
        }
    }
}


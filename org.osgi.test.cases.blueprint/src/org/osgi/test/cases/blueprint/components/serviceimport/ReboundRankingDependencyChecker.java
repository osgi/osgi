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


/**
 * A base class for implementing components that test service dynamics by causing
 * changes in the registration state of services.  This class is injected with a
 * ServiceManager reference that can be used to register/deregister services.
 * The subclasses use the init() method to trigger state changes that the test driver
 * code will validate via the events the state changes cause (or possibly even
 * directly in the init() code).
 */
public class ReboundRankingDependencyChecker extends DependencyDriver {

    public ReboundRankingDependencyChecker(String componentId) {
        super(componentId);
    }


    /**
     * This is the actual test method.  We've been injected with
     * a service component that is managed by the service manager.
     * We'll verify that this service is rebound to an alternative
     * service when the first service goes away.  This also verifies the
     * behavior of the service listeners in a rebind situation.
     */
    public void init() {
        // first test the service is live
        AssertionService.assertTrue(this, "Bad service injected", serviceOne.testOne());
        // this will register a service with a higher ranking than the original, which
        // should cause a rebind.
        serviceManager.registerService("BadService");
        // verify the service call.  This should now return a false result because
        // the service changed.
        AssertionService.assertFalse(this, "Bad service injected", serviceOne.testOne());
        // this sends out a completion message
        super.init();
    }
}


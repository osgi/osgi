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
public class ReplacementDependencyChecker extends DependencyDriver {

    public ReplacementDependencyChecker(String componentId) {
        super(componentId);
    }


    /**
     * This is the actual test method.  We've been injected with
     * a service component that is managed by the service manager.
     * We'll verify that this service "goes away" when the service
     * is unregistered, and comes back when the service is reinstated.
     * We also have some service listeners attached, so we'll be validating
     * that the correct service listener bind/unbind calls are mad.
     */
    public void init() {
        // first test the service is live
        AssertionService.assertEquals(this, "Bad service injected", "ServiceOneA", serviceOne.getServiceName());
        // this should unregister our dependent service
        serviceManager.unregisterService("ServiceOneA");
        // this functions in one of two manners.  In the first test, a replacement service
        // will be immediately available as a replacement.  In the second test, we'll
        // need to wait on a service instance, which will be registered as soon as our
        // WAITING event arrives.  In either case, this service call should work.
        AssertionService.assertEquals(this, "Bad service injected", "ServiceOneB", serviceOne.getServiceName());
        super.init();
    }
}


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
import org.osgi.service.blueprint.context.ServiceUnavailableException;


/**
 * A base class for implementing components that test service dynamics by causing
 * changes in the registration state of services.  This class is injected with a
 * ServiceManager reference that can be used to register/deregister services.
 * The subclasses use the init() method to trigger state changes that the test driver
 * code will validate via the events the state changes cause (or possibly even
 * directly in the init() code).
 */
public class UnavailableDependencyChecker extends DependencyDriver {

    public UnavailableDependencyChecker(String componentId) {
        super(componentId);
    }


    /**
     * This is the actual test method.  We've been injected with
     * a service component that is managed by the service manager.
     * A service we're injected with was not available at module start up.
     * We A) verify that invoking this service is an error.  B) register
     * a backing service for this and verify it is now valid.  C) unregister
     * the backing service verify it reverts to form.
     */
    public void init() {
        try {
            // this should give an exception
            AssertionService.assertFalse(this, "Service proxy not detached", serviceOne.testOne());
            // should never get here
            AssertionService.fail(this, "Service proxy not detached");
        } catch (ServiceUnavailableException e) {
            // we expect to get here
        }
        // this should register our dependent service
        serviceManager.toggleServices();
        // now test the service is live
        AssertionService.assertTrue(this, "Bad service injected", serviceOne.testOne());
        // this should unregister our dependent service again
        serviceManager.toggleServices();
        try {
            // this should give an exception
            AssertionService.assertFalse(this, "Service proxy not detached", serviceOne.testOne());
        } catch (ServiceUnavailableException e) {
            // we expect to get here
        }
        super.init();
    }
}


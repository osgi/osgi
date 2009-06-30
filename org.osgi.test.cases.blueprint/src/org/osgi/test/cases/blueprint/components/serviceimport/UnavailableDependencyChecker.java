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

import java.util.Properties;

import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.TestServiceOne;
import org.osgi.service.blueprint.container.ServiceUnavailableException;

import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;


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
            try {
                String filterString = e.getFilter();
                AssertionService.assertNotNull(this, "Null filter from ServiceUnavailableException", filterString);
                Filter filter = serviceManager.createFilter(filterString);
                Properties filterProps = new Properties();
                filterProps.put(org.osgi.framework.Constants.OBJECTCLASS, new String[] { TestServiceOne.class.getName() });
                // this is requested using the component-name attribute, so this should be in the filter too.
                filterProps.put("osgi.service.blueprint.compname", "ServiceOneA");
                AssertionService.assertTrue(this, "ServiceUnavailableException filter did not match expected properties", filter.match(filterProps));
            } catch (InvalidSyntaxException ise) {
                AssertionService.fail(this, "Invalid filter syntax for ServiceUnavailableException", ise);
            }
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
            // we expect to get here
            try {
                String filterString = e.getFilter();
                AssertionService.assertNotNull(this, "Null filter from ServiceUnavailableException", filterString);
                Filter filter = serviceManager.createFilter(filterString);
                Properties filterProps = new Properties();
                filterProps.put(org.osgi.framework.Constants.OBJECTCLASS, new String[] { TestServiceOne.class.getName() });
                // this is requested using the component-name attribute, so this should be in the filter too.
                filterProps.put("osgi.service.blueprint.compname", "ServiceOneA");
                // we also include a filter looking for this property with the same name
                filterProps.put("service.name", "ServiceOneA");
                AssertionService.assertTrue(this, "ServiceUnavailableException filter did not match expected properties", filter.match(filterProps));
            } catch (InvalidSyntaxException ise) {
                AssertionService.fail(this, "Invalid filter syntax for ServiceUnavailableException", ise);
            }
        }
        super.init();
    }
}


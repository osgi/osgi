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

import org.osgi.service.blueprint.container.ServiceUnavailableException;
import org.osgi.test.cases.blueprint.framework.TestService;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.TestServiceOne;

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
public class UnregisteredCollectionDependencyChecker extends DependencyDriver {

    public UnregisteredCollectionDependencyChecker(String componentId) {
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
        // retrieve the collection service and validate the initial call
        TestServiceOne service = getCollectionService();

        // first test the service is live
        AssertionService.assertTrue(this, "Bad service injected", service.testOne());
        // this should unregister our dependent service
        serviceManager.toggleServices();
        try {
            // this should give an exception
            AssertionService.assertFalse(this, "Service proxy not detached", service.testOne());
        } catch (ServiceUnavailableException e) {
            try {
                String filterString = e.getFilter();
                AssertionService.assertNotNull(this, "Null filter from ServiceUnavailableException", filterString);
                Filter filter = serviceManager.createFilter(filterString);
                Properties filterProps = new Properties();
                filterProps.put(org.osgi.framework.Constants.OBJECTCLASS, new String[] { TestServiceOne.class.getName() });
                // this is requested using the component-name attribute, so this should be in the filter too.
                filterProps.put("osgi.service.blueprint.compname", "ServiceOneA");
                // we also filter on the service name value
                filterProps.put("test.service.name", "ServiceOneA");
                AssertionService.assertTrue(this, "ServiceUnavailableException filter did not match expected properties: filter=" + filterString + ", properties=" + filterProps, filter.match(filterProps));
            } catch (InvalidSyntaxException ise) {
                AssertionService.fail(this, "Invalid filter syntax for ServiceUnavailableException", ise);
            }
            // we expect to get here
        }
        // this will bring our backing service back to life
        serviceManager.toggleServices();
        try {
            // this should still give an exception since service proxies obtained from
            // reference collections do not have damping behavior.
            AssertionService.assertFalse(this, "Service proxy not detached", service.testOne());
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
                // we also filter on the service name value
                filterProps.put("test.service.name", "ServiceOneA");
                AssertionService.assertTrue(this, "ServiceUnavailableException filter did not match expected properties: filter=" + filterString + ", properties=" + filterProps, filter.match(filterProps));
            } catch (InvalidSyntaxException ise) {
                AssertionService.fail(this, "Invalid filter syntax for ServiceUnavailableException", ise);
            }
        }
        super.init();
    }
}



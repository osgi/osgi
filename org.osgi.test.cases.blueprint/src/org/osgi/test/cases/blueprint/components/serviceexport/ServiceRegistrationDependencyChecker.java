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

package org.osgi.test.cases.blueprint.components.serviceexport;

import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;
import org.osgi.test.cases.blueprint.services.ServiceManager;
import org.osgi.test.cases.blueprint.services.TestUtil;

/**
 * Class for testing the dynamics of injected ServiceRegistration proxies.
 */
public class ServiceRegistrationDependencyChecker extends BaseTestComponent {
    // Our injected ServiceManager reference.  This is used to test service
    // dynamics of reference collections by allowing us to register/deregister
    // components defined in other bundles.
    protected ServiceManager serviceManager;
    // the injected registration object
    protected ServiceRegistration reg;

    public ServiceRegistrationDependencyChecker(String componentId) {
        super(componentId);
    }

    /**
     * Set the ServiceManager if this test requires it.
     *
     * @param manager The ServiceManager instance.
     */
    public void setServiceManager(ServiceManager manager) {
        serviceManager = manager;
    }

    /**
     * Set the registration for this service
     *
     * @param reg    The injected ServiceRegistration.
     */
    public void setRegistration(ServiceRegistration reg) {
        this.reg = reg;
    }


    /**
     * This is the actual test method.  We've been injected with
     * a ServiceRegistration that is dependent on an imported service.
     * We'll make that dependency go away, which should deregister our service
     * and invalidate the ServiceRegistration proxy.  We'll verify that, then make
     * the service return and verify the proxy is still good.
     */
    public void init() {
        // get a reference.  This should work, since the dependency should be there
        ServiceReference ref = reg.getReference();
        // this will degregister one of our dependencies, so this should be unregistered
        serviceManager.toggleServices();

        try {
            ref = reg.getReference();
            // should never get here
            AssertionService.fail(this, "Service proxy not throwing an execption");
            return;
        } catch (IllegalStateException e) {
            // we expect to get here...the spec mandates an IllegalStateException in this situation
        }
        // this should register our dependent service
        serviceManager.toggleServices();

        // get a reference.  This should work, since the dependency should be there
        ref = reg.getReference();
        // this will broadcast a success message
        super.init();
    }
}


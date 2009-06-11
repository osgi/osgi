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

import org.osgi.test.cases.blueprint.services.ManagedService;


/**
 * Test for injection of an empty List of services no services are available.  This
 * will also register a service using the service manager to see that the new
 * registration is picked up.
 */
public class NullReferenceList extends ReferenceCollectionChecker {

    public NullReferenceList(String componentId) {
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
        // this should be an empty list now
        validateList(serviceManager.getActiveServices());
        // now register all of the services in our list
        serviceManager.registerServices();
        // now validate against the active services
        validateList(serviceManager.getActiveServices());
        // now make them all go away
        serviceManager.unregisterServices();
        // this should be back to an empty list
        validateList(new ManagedService[0]);
        // send out the initialized message to indicate we've completed.
        super.init();
    }
}

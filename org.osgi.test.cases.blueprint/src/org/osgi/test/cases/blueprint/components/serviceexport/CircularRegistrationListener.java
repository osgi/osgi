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

import java.util.Map;

import org.osgi.test.cases.blueprint.services.TestServiceOne;


/**
 * A base class for different reference listeners used to validate
 * reference registration/deregistration calls.
 */
public class CircularRegistrationListener extends ServiceOneRegistrationListener implements TestServiceOne {
    public CircularRegistrationListener(String componentId) {
        super(componentId);
    }

    public void registered(TestServiceOne service, Map serviceProperties) {
        registered(TestServiceOne.class, serviceProperties);
    }

    public void unregistered(TestServiceOne service, Map serviceProperties) {
        unregistered(TestServiceOne.class, serviceProperties);
    }

    /**
     * A method that can be called for test verification purposes.
     *
     * @return always returns true
     */
    public boolean testOne() {
        return true;
    }

    /**
     * This is used to allow multiple services to return a unique
     * service id.  This is used for testing reference collections and
     * also for testing reference list sorting.
     *
     * @return An identifier for this service instance.
     */
    public int getServiceId() {
        return 0;  // always returns 0
    }

    /**
     * This is used for service collection matching.  This is generally
     * the component id of the service.  When testing collections, this
     * must be unique among the collections.
     *
     * @return The string name for the service instance.
     */
    public String getServiceName() {
        return componentId;
    }

    public int compareTo(Object other) {
        return getServiceId() - ((TestServiceOne)other).getServiceId();
    }
}


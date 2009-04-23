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

package org.osgi.test.cases.blueprint.namespace;

import java.lang.Comparable;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;
import org.osgi.test.cases.blueprint.services.TestServiceAllSubclass;
import org.osgi.test.cases.blueprint.services.TestServiceOne;

/**
 * Concrete target for a service reference.  This is a subclass of our base
 * test service to have a deeper hierarchy for service registrations.  This implements
 * all of the interface methods plus an additional concrete method that can be accessed.
 * This is a subclass of the base test component so we can track creation events when
 * lazy initialization is used.
 */
public class NamespaceGoodService extends BaseTestComponent implements TestServiceAllSubclass, Comparable {
    // a service identifier that can be used to identify different service
    // instances.  This is also used for testing service sorting.
    public int serviceId = 0;

    /**
     * A null argument constructor is required for exporting
     * as a service interface.
     */
    public NamespaceGoodService() {
        super("NamspaceGoodService");
    }

    public NamespaceGoodService(String componentId) {
        super(componentId);
    }

    public NamespaceGoodService(String componentId, int serviceId) {
        super(componentId);
        this.serviceId = serviceId;
    }

    /**
     * A method that can be called for test verification purposes.  This
     * method is part of the concrete class, not the interfaces.
     *
     * @return always returns true
     */
    public boolean testGood() {
        return true;
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
     * A method that can be called for test verification purposes.
     *
     * @return always returns true
     */
    public boolean testTwo() {
        return true;
    }
    /**
     * A method that can be called for test verification purposes.
     *
     * @return always returns true
     */
    public boolean testTwoSubclass() {
        return true;
    }

    /**
     * A method that can be called for test verification purposes.
     *
     * @return always returns true
     */
    public boolean testAllSubclass() {
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
        return serviceId;
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

    /**
     * Perform an equals comparison on this service instance.
     *
     * @param other  The other service to compare.
     *
     * @return true if the two services are considered equivalent.
     */
    public boolean equals(Object other) {
        // compare based on the service id.
        if (other instanceof TestServiceOne) {
            return componentId.equals(((TestServiceOne)other).getServiceName());
        }
        return false;
    }

    /**
     * The compareTo() method required by the Comparable interface.
     *
     * @param other
     *
     * @return
     */
    public int compareTo(Object other) {
        return serviceId - ((TestServiceOne)other).getServiceId();
    }
}


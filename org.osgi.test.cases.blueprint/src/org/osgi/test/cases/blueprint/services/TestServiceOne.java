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

package org.osgi.test.cases.blueprint.services;


/**
 * Base interface for a test service implementation.
 */
public interface TestServiceOne extends Comparable {
    /**
     * A method that can be called for test verification purposes.
     *
     * @return success/failure indicator
     */
    public boolean testOne();

    /**
     * This is used to allow multiple services to return a unique
     * service id.  This is used for testing reference collections and
     * also for testing reference list sorting.
     *
     * @return An identifier for this service instance.
     */
    public int getServiceId();

    /**
     * This is used for service collection matching.  This is generally
     * the component id of the service.  When testing collections, this
     * must be unique among the collections.
     *
     * @return The string name for the service instance.
     */
    public String getServiceName();


    /**
     * This is used for situations where set semantics get applied
     * to service instances.  This ensures the equals() method of the
     * service is proxied.
     *
     * @param other  The other object for comparison
     *
     * @return true if the objects are equal, false otherwise.
     */
    public boolean equals(Object other);


    /**
     * Override of the hashCode() method to also force proxying
     * of this method.
     *
     * @return
     */
    public int hashCode();
}


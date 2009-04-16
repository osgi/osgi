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
 * Concrete target for a service reference.  This is a subclass of our base
 * test service that will return a failure result on any method calls.  This is used
 * for testing service disambiguation via filters and rankings.
 */
public class TestBadService extends TestGoodServiceSubclass {

    public TestBadService() {
        // always use the same internal component id
        super("BadService");
    }

    public TestBadService(String componentId, int serviceId) {
        super(componentId, serviceId);
    }


    /**
     * A method that can be called for test verification purposes.  This
     * method is part of the concrete class, not the interfaces.
     *
     * @return always returns false
     */
    public boolean testGood() {
        return false;
    }


    /**
     * A method that can be called for test verification purposes.  This
     * method is part of the concrete class, not the interfaces.
     *
     * @return always returns false
     */
    public boolean testGoodSubclass() {
        return false;
    }

    /**
     * A method that can be called for test verification purposes.
     *
     * @return always returns false
     */
    public boolean testOne() {
        return false;
    }

    /**
     * A method that can be called for test verification purposes.
     *
     * @return always returns false
     */
    public boolean testTwo() {
        return false;
    }
    /**
     * A method that can be called for test verification purposes.
     *
     * @return always returns false
     */
    public boolean testTwoSubclass() {
        return false;
    }

    /**
     * A method that can be called for test verification purposes.
     *
     * @return always returns false
     */
    public boolean testAllSubclass() {
        return false;
    }
}



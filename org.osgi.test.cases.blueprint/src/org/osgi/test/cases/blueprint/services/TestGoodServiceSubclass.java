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
 * Concrete target for a service reference.  This will implement all of
 * the interfaces and will be registered different ways.  All test method calls
 * will give a good return.
 */
public class TestGoodServiceSubclass extends TestGoodService {

    /**
     * A null argument constructor is required for exporting
     * as a service interface.
     */
    public TestGoodServiceSubclass() {
        super();
    }


    public TestGoodServiceSubclass(String componentId) {
        super(componentId);
    }

    public TestGoodServiceSubclass(String componentId, int serviceId) {
        super(componentId, serviceId);
    }


    /**
     * A method that can be called for test verification purposes.  This
     * method is part of the concrete class, not the interfaces.
     *
     * @return always returns true
     */
    public boolean testGoodSubclass() {
        return true;
    }
}

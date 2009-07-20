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

package org.osgi.test.cases.blueprint.components.servicedynamics;

import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;
import org.osgi.test.cases.blueprint.services.TestServiceDynamicsInterface;
import org.osgi.test.cases.blueprint.services.TestServiceOne;
import org.osgi.test.cases.blueprint.services.TestServiceTwo;


/**
 * A base class for testing service dynamics.  This component can be injected with
 * service instances, and can also export some of our service interfaces.  Other than
 * that, this really does nothing of note.
 */
public class CounterFactory extends BaseTestComponent {
    // a counter used to create each instance
    public int counter = 0;

    public CounterFactory() {
        super("CounterFactory");
    }

    public Integer makeInstance() {
        return new Integer(counter++);
    }
}


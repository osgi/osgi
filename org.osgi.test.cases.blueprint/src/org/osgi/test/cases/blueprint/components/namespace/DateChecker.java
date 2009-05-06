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

package org.osgi.test.cases.blueprint.components.namespace;

import java.text.SimpleDateFormat;

import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;


/**
 * A base class for implementing components that test service dynamics by causing
 * changes in the registration state of services.  This class is injected with a
 * ServiceManager reference that can be used to register/deregister services.
 * The subclasses use the init() method to trigger state changes that the test driver
 * code will validate via the events the state changes cause (or possibly even
 * directly in the init() code).
 */
public class DateChecker extends BaseTestComponent {
    // the target for this format check
    protected SimpleDateFormat target;

    public DateChecker(String componentId, SimpleDateFormat target) {
        super(componentId);
        this.target = target;
    }

    /**
     * Inject a formatter property, which is checked against
     * a reference instance created via normal blueprint means.
     *
     * @param format The injected format object.
     */
    public void setFormat(SimpleDateFormat format) {
        AssertionService.assertEquals(this, "Mismatch in date format objects", target, format);
    }
}


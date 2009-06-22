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

package org.osgi.test.cases.blueprint.components.injection;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;
import org.osgi.test.cases.blueprint.services.ComponentTestInfo;
import org.osgi.test.cases.blueprint.services.ValueDescriptor;

public class PrototypeComponentInjection extends ComponentInjection {
    // list of initialized dependent components
    protected static Map dependentComponents = new HashMap();


    /**
     * Simple injection with just a component id.  This is used for tests that
     * only test property injection.
     *
     * @param componentId
     *               The component identifier used for test verification purposes.
     */
    public PrototypeComponentInjection(String componentId) {
        super(componentId);
    }

    /**
     * We revert this back to doing an identity equals
     */
    public boolean equals(Object o) {
        return this == o;
    }


    /**
     * Override hashCode() because we overrode the equals()
     */
    public int hashCode() {
        return System.identityHashCode(this);
    }
}


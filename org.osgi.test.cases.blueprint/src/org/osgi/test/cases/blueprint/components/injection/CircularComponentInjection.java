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

public class CircularComponentInjection extends BaseTestComponent {
    // list of initialized dependent components
    protected static Map dependentComponents = new HashMap();


    /**
     * Simple injection with just a component id.  This is used for tests that
     * only test property injection.
     *
     * @param componentId
     *               The component identifier used for test verification purposes.
     */
    public CircularComponentInjection(String componentId) {
        super(componentId);
        setArgumentValue("arg1", componentId);
    }

    /**
     * Simple injection with one component arguments.
     *
     * @param componentId
     *               The component identifier used for test verification purposes.
     */
    public CircularComponentInjection(String componentId, ComponentTestInfo arg2) {
        super(componentId);
        setArgumentValue("arg1", componentId);
        setArgumentValue("arg2", arg2);
    }

    // a series of property setters for some complex test cases
    public void setComponentOne(ComponentTestInfo value) {
        setPropertyValue("componentOne", value);
    }

    public void setComponentTwo(ComponentTestInfo value) {
        setPropertyValue("componentTwo", value);
    }

    public void setComponentThree(ComponentTestInfo value) {
        setPropertyValue("componentThree", value);
    }

    /**
     * Add a component value to our property set.  This is a superclass
     * override that also broadcasts an event.
     *
     * @param value  The value object to add.
     */
    protected void setPropertyValue(String name, ValueDescriptor value) {
        super.setPropertyValue(name, value);
        // send out a trackable event
        Hashtable props = new Hashtable();
        props.put(AssertionService.PROPERTY_NAME, name);
        props.put(AssertionService.PROPERTY_VALUE, value);
        AssertionService.sendEvent(this, AssertionService.BEAN_PROPERTY_SET, props);
    }


    /**
     * Set a set of indirect component dependencies.
     *
     * @param value  The set of string component names.
     */
    public void setDependsOn(Set value) {
        props.put("dependsOn", value);
    }


    /**
     * Used when we're managing is an inner component..
     */
    public boolean equals(Object o) {
        return this == o;
    }
}


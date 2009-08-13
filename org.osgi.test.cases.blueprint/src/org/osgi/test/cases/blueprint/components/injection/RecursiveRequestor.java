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

import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;
import org.osgi.test.cases.blueprint.services.ComponentTestInfo;
import org.osgi.test.cases.blueprint.services.ValueDescriptor;

/**
 * A bean that causes a recursive request situation using
 * BlueprintContainer.getComponentInstance().  The extender
 * must detect this situation and force a failure.
 */
public class RecursiveRequestor extends BaseTestComponent {
    // the container used to request the instance.
    protected BlueprintContainer container;
    // the name to request
    protected String myId;

    /**
     * Normal constructor for tests that make the request during
     * property injection or init phases.
     *
     * @param componentId
     *                  The component ID.
     * @param container Our container context used to request the instance.
     */
    public RecursiveRequestor(String componentId, BlueprintContainer container) {
        super(componentId);
        this.container = container;
        myId = componentId;
    }


    /**
     * constructor for tests that make the request during
     * creation phase.
     *
     * @param componentId
     *                  The component ID.
     * @param container Our container context used to request the instance.
     * @param myId      The id to request (normally the same as the component id).
     */
    public RecursiveRequestor(String componentId, BlueprintContainer container, String myId) {
        super(componentId);
        this.container = container;
        // this should cause an error, which should throw an exception
        Object instance = container.getComponentInstance(myId);
        AssertionService.fail(this, "recursive getComponentInstance() did not throw an exception");
    }


    /**
     * Property setter used to trigger recursion test during
     * injection phase.
     *
     * @param myId   The id to request.
     */
    public void setMyId(String myId) {
        // this is used for singleton tests, which is a breakable cycle from a
        // setter.  This should not cause an error
        try {
            Object instance = container.getComponentInstance(myId);
            AssertionService.assertSame(this, "Invalid recursive getComponentInstance() result returned", this, instance);
        } catch (Throwable e) {
            AssertionService.fail(this, "recursive getComponentInstance() threw an unexpected exception", e);
        }
    }


    /**
     * An init method to test recursion.  This also must
     * be detected.
     */
    public void init() {
        try {
            // this is a breakable cycle because this is a singleton component, so
            // this should not be an error
            Object instance = container.getComponentInstance(myId);
            AssertionService.assertSame(this, "Invalid recursive getComponentInstance() result returned", this, instance);
        } catch (Throwable e) {
            AssertionService.fail(this, "recursive getComponentInstance() threw an unexpected exception", e);
        }
    }


    /**
     * Property setter used to trigger recursion test during
     * injection phase of a prototype scope component.
     *
     * @param myId   The id to request.
     */
    public void setMyPrototypeId(String myId) {
        // this should cause an error, which should throw an exception
        Object instance = container.getComponentInstance(myId);
        AssertionService.fail(this, "recursive getComponentInstance() did not throw an exception");
    }


    /**
     * An init method to test recursion during construction of a prototype scope component.
     */
    public void prototypeInit() {
        // this should cause an error, which should throw an exception
        Object instance = container.getComponentInstance(myId);
        AssertionService.fail(this, "recursive getComponentInstance() did not throw an exception");
    }
}


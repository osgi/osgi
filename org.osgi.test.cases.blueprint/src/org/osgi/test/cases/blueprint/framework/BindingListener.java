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

package org.osgi.test.cases.blueprint.framework;

import junit.framework.Assert;

import org.osgi.service.blueprint.reflect.Listener;
import org.osgi.service.blueprint.reflect.Metadata;

/**
 * A base type for validating BindingListener metadata.
 */
public class BindingListener extends Assert {
    // the listener component name.  This is either a ReferenceValue or a ComponentValue
    protected TestValue listener;
    // The listener Bind method name.
    protected String bindName;
    // the listener unbind method name
    protected String unbindName;

    public BindingListener(String componentName, String bindName, String unbindName) {
        this(new TestRefValue(componentName), bindName, unbindName);
    }

    public BindingListener(TestValue listener, String bindName, String unbindName) {
        this.listener = listener;
        this.bindName = bindName;
        this.unbindName = unbindName;
    }

    /**
     * Test if a binding listener from a set of binding listners
     * matches this expected value.
     *
     * @param meta   The target metadata for the match.
     *
     * @return True if this appears to be the validation target, false otherwise.
     */
    public boolean matches(Listener meta) {
        // the value type does the matching
        return listener.equals(meta.getListenerComponent());
    }


    /**
     * Validate the remainder of the metadata in a binding listener.
     *
     * @param meta   The imput metadata.
     *
     * @exception Exception
     */
    public void validate(BlueprintMetadata blueprintMetadata, Listener meta) throws Exception {
        assertEquals(bindName, meta.getBindMethodName());
        assertEquals(unbindName, meta.getUnbindMethodName());
        listener.validate(blueprintMetadata, meta.getListenerComponent());
    }

    public String toString() {
        return "Binding listener component: " + listener;
    }
}


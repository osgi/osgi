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

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import org.osgi.service.blueprint.reflect.SetValue;
import org.osgi.service.blueprint.reflect.Value;

public class TestSetValue extends TestValue {
    // The expected set of items in the List.
    protected TestValue[] entries;
    // the default element type for the list
    protected String typeName;

    public TestSetValue() {
        this(null, null);
    }

    public TestSetValue(TestValue[] entries) {
        this(entries, null);
    }

    public TestSetValue(TestValue[] entries, Class listType) {
        super(SetValue.class);
        this.entries = entries;
        if (listType != null) {
            typeName = listType.getName();
        }
    }


    /**
     * Validate a ParameterSpecification against an expected value.
     *
     * @param spec   The metadata spec for this argument.
     *
     * @exception Exception
     */
    public void validate(ModuleMetadata moduleMetadata, Value v) throws Exception {
        super.validate(moduleMetadata, v);
        // we might not have a validation list
        if (entries == null) {
            return;
        }
        Set set = ((SetValue)v).getSet();
        // validate the size first
        assertEquals("Set value size mismatch", entries.length, set.size());
        assertEquals("Set default type mismatch", typeName, ((SetValue)v).getValueType());
        // we work off of a copy of this
        Set working = new HashSet(set);
        // now validate each of the entries
        for (int i = 0; i < entries.length; i++) {
            Value target = locateEntry(working, entries[i]);
            assertNotNull("Target value not found in set item: " + entries[i], target);
            // validate the real entry
            entries[i].validate(moduleMetadata, target);
        }
    }

    /**
     * do a comparison between a real metadata item and our test validator.
     * This is used primarily to locate specific values in the different
     * CollectionValues.
     *
     * @param v      The target value item.
     *
     * @return True if this can be considered a match, false for any mismatch.
     */
    public boolean equals(Value v) {
        // must be of matching type
        if (!super.equals(v)) {
            return false;
        }

        // we might not have a validation list, so use this
        // as a wildcard placeholder if asked.
        if (entries == null) {
            return true;
        }

        Set set = ((SetValue)v).getSet();
        // not the one we need
        if (entries.length != set.size()) {
            return false;
        }

        // we work off of a copy of this
        Set working = new HashSet(set);
        // now validate each of the entries
        for (int i = 0; i < entries.length; i++) {
            Value target = locateEntry(working, entries[i]);
            if (target == null) {
                return false;
            }
            // validate the real entry
            if (!entries[i].equals(target)) {
                return false;
            }
        }
        return true;
    }


    protected Value locateEntry(Set values, TestValue target) {
        Iterator i = values.iterator();
        while (i.hasNext()) {
            Value item = (Value)i.next();
            if (target.equals(item)) {
                i.remove();
                return item;
            }
        }
        return null;
    }
}


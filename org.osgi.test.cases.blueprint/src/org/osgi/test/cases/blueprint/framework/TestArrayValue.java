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

import org.osgi.service.blueprint.reflect.ArrayValue;
import org.osgi.service.blueprint.reflect.Value;

public class TestArrayValue extends TestValue {
    // The expected set of items in the List.
    protected TestValue[] entries;
    // the default element type for the list
    protected String typeName;

    /**
     * A TestArrayValue that only verifies that the value
     * is an array type.  No entry validation is done.
     */
    public TestArrayValue() {
        this(null, null);
    }

    public TestArrayValue(TestValue[] entries) {
        this(entries, null);
    }

    public TestArrayValue(TestValue[] entries, Class listType) {
        super(ArrayValue.class);
        this.entries = entries;
        if (listType != null) {
            typeName = listType.getName();
        }
    }


    /**
     * An expected ArrayValue item against a received one.
     *
     * @param moduleMetadata
     *               The metadata source for the validation.
     * @param v
     *
     * @exception Exception
     */
    public void validate(ModuleMetadata moduleMetadata, Value v) throws Exception {
        // This validates the metadata type.
        super.validate(moduleMetadata, v);
        // we might not have a validation list if we're just interested
        // in verifying this is a ListValue
        if (entries == null) {
            return;
        }

        Value[] list = ((ArrayValue)v).getArray();
        // validate the size first
        assertEquals("Array value size mismatch", entries.length, list.length);
        assertEquals("Array value default type mismatch", typeName, ((ArrayValue)v).getValueType());
        // now validate each of the entries
        for (int i = 0; i < entries.length; i++) {
            // nulls appear as NullValue items, so everything should match
            entries[i].validate(moduleMetadata, list[i]);
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

        Value[] list = ((ArrayValue)v).getArray();
        // not the one we need
        if (entries.length != list.length) {
            return false;
        }

        // now validate each of the entries
        for (int i = 0; i < entries.length; i++) {
            // validate the real entry
            if (!entries[i].equals(list[i])) {
                return false;
            }
        }
        return true;
    }
}


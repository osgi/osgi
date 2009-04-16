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

import org.osgi.service.blueprint.reflect.TypedStringValue;
import org.osgi.service.blueprint.reflect.Value;

public class TestStringValue extends TestValue {
    // The target conversion type (can be null)
    protected String targetTypeName;
    // the config file string value (optional validation)
    protected String value;


    /**
     * This version is when you have a string argument with
     * no explicit type specified, and you don't wish
     * to validate the value.
     */
    TestStringValue() {
        this(null, null);
    }

    public TestStringValue(Class targetType) {
        this(targetType, null);
    }

    public TestStringValue(String value) {
        this(null, value);
    }

    public TestStringValue(Class targetType, String value) {
        super(TypedStringValue.class);
        if (targetType != null) {
            this.targetTypeName = targetType.getName();
        }
        this.value = value;
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
        assertEquals("String conversion type mismatch", targetTypeName, ((TypedStringValue)v).getTypeName());
        // if we have a value to test on.  sometimes, the value is two complex
        // to specify directly.
        if (value != null) {
            assertEquals("String conversion value mismatch", value, ((TypedStringValue)v).getStringValue());
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

        // if no type was explicitly specified,
        if (targetTypeName == null) {
            // then this must also be null
            if (((TypedStringValue)v).getTypeName() != null) {
                return false;
            }
        }
        // must match on the type name
        else if (!targetTypeName.equals(((TypedStringValue)v).getTypeName())) {
            return false;
        }

        if (value != null) {
            return value.equals(((TypedStringValue)v).getStringValue());
        }

        return true;
    }

    /**
     * Helpful override for toString().
     *
     * @return a descriptive string value for the type.
     */
    public String toString() {
        if (value != null) {
            return "String value " + value + " converted to " + targetTypeName;
        }
        else {
            return "String value converted to " + targetTypeName;
        }
    }
}


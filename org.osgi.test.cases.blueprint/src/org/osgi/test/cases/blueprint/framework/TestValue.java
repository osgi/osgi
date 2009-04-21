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

import org.osgi.service.blueprint.reflect.Metadata;

import junit.framework.Assert;

public class TestValue extends Assert {
    // expected value metadata type
    protected Class valueType;

    public TestValue(Class valueType) {
        this.valueType = valueType;
    }

    /**
     * Validate a Metadata value against an expected value.
     *
     * @param spec   The metadata spec for this argument.
     *
     * @exception Exception
     */
    public void validate(BlueprintMetadata moduleMetaData, Metadata v) throws Exception {
        assertNotNull("Expected Metadata type " + valueType.getName(), v);
        // make sure the metadata is of the correct type
        assertTrue("Metadata type mismatch.  Expected " + valueType.getName() + " Received " + v, valueType.isInstance(v));
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
    public boolean equals(Metadata v) {
        // this can happen in some validation contexts
        if (v == null) {
            return false;
        }

        // this must be of a matching type
        return valueType.isInstance(v);
    }

    /**
     * Helpful override for toString().
     *
     * @return a descriptive string value for the type.
     */
    public String toString() {
        return "TestValue of type " + valueType.getName();
    }
}


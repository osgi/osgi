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

import java.util.Properties;

import org.osgi.service.blueprint.reflect.PropertiesValue;
import org.osgi.service.blueprint.reflect.Value;

public class TestPropertiesValue extends TestValue {
    // The expected set of properties items.
    protected Properties entries;

    public TestPropertiesValue(Properties entries) {
        super(PropertiesValue.class);
        this.entries = entries;
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
        // don't do anything if we're not validating the contents
        if (entries == null) {
            return;
        }

        Properties props = ((PropertiesValue)v).getPropertiesValue();
        // validate the size first
        assertEquals("PropertiesValue mismatch", entries, props);
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
        // if no entries, then assume a type match is equality
        if (entries == null) {
            return true;
        }

        Properties props = ((PropertiesValue)v).getPropertiesValue();
        return entries.equals(props);
    }
}


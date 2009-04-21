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

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.blueprint.reflect.PropsMetadata;
import org.osgi.service.blueprint.reflect.Metadata;
import org.osgi.service.blueprint.reflect.MapEntry;

public class TestPropertiesValue extends TestValue {
    // The expected set of properties items.
    protected List entries;

    public TestPropertiesValue(List entries) {
        super(PropsMetadata.class);
        this.entries = entries;
    }

    public TestPropertiesValue() {
        super(PropsMetadata.class);
        this.entries = new ArrayList();
    }

    public void add(String key, String value) {
        entries.add(new MapValueEntry(key, value));
    }


    /**
     * Validate a ParameterSpecification against an expected value.
     *
     * @param spec   The metadata spec for this argument.
     *
     * @exception Exception
     */
    public void validate(BlueprintMetadata blueprintMetadata, Metadata v) throws Exception {
        super.validate(blueprintMetadata, v);
        // don't do anything if we're not validating the contents
        if (entries == null) {
            return;
        }

        PropsMetadata meta = (PropsMetadata)v;
        List props = meta.getEntries();

        // validate the size first
        assertEquals("PropertiesValue mismatch", entries.size(), props.size());
        for (int i = 0; i < entries.size(); i++) {
            MapValueEntry entry = (MapValueEntry)entries.get(i);
            entry.validate(blueprintMetadata, (MapEntry)props.get(i));
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
    public boolean equals(Metadata v) {
        // must be of matching type
        if (!super.equals(v)) {
            return false;
        }
        // if no entries, then assume a type match is equality
        if (entries == null) {
            return true;
        }

        PropsMetadata meta = (PropsMetadata)v;
        List props = meta.getEntries();

        // validate the size first
        if (entries.size() != props.size()) {
            return false;
        }

        for (int i = 0; i < entries.size(); i++) {
            MapValueEntry entry = (MapValueEntry)entries.get(i);
            if (!entry.equals((MapEntry)props.get(i))) {
                return false;
            }
        }
        return true;
    }
}


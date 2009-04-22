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
import java.util.Iterator;

import org.osgi.service.blueprint.reflect.MapMetadata;
import org.osgi.service.blueprint.reflect.MapEntry;
import org.osgi.service.blueprint.reflect.Metadata;
import org.osgi.service.blueprint.reflect.CollectionMetadata;

public class TestMapValue extends TestValue {
    // The expected set of items in the List.
    protected List entries;
    // the default value type
    protected String valueType;
    // the default key type
    protected String keyType;

    public TestMapValue() {
        super(MapMetadata.class);
        this.entries = new ArrayList();
    }

    public TestMapValue(MapValueEntry[] e) {
        this(e, null, null);
    }

    public TestMapValue(MapValueEntry[] e, Class key, Class value) {
        this();
        // copy this over to the list
        for (int i = 0; i < e.length; i++) {
            add(e[i]);
        }

        if (key != null) {
            keyType = key.getName();
        }
        if (value != null) {
            valueType = value.getName();
        }
    }


    /**
     * Add a new MapEntry value to the end of the list.
     *
     * @param e      The new entry to add.
     */
    public void add(MapValueEntry e) {
        entries.add(e);
    }

    public void put(TestValue key, TestValue value) {
        add(new MapValueEntry(key, value));
    }

    public void put(String key, TestValue value) {
        add(new MapValueEntry(key, value));
    }

    public void put(String key, String value) {
        add(new MapValueEntry(key, value));
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
        // we might not have a validation list, so use this
        // as a wildcard placeholder if asked.
        if (entries == null) {
            return;
        }
        // convert into the proper metadata type
        MapMetadata meta = (MapMetadata)v;
        List mapEntries = meta.getEntries();
        // validate the size first
        assertEquals("Map value size mismatch", entries.size(), mapEntries.size());
        assertEquals("Map default value type mismatch", valueType, meta.getValueTypeName());
        assertEquals("Map default key type mismatch", keyType, meta.getKeyTypeName());
        // now validate each of the entries
        for (int i = 0; i < entries.size(); i++) {
            MapValueEntry e = (MapValueEntry)entries.get(i);
            // validate the real entry
            e.validate(blueprintMetadata, (MapEntry)mapEntries.get(i));
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
        // we might not have a validation list, so use this
        // as a wildcard placeholder if asked.
        if (entries == null) {
            return true;
        }
        // convert into the proper metadata type
        CollectionMetadata meta = (CollectionMetadata)v;
        List mapEntries = meta.getValues();
        // not the one we need
        if (entries.size() != mapEntries.size()) {
            return false;
        }
        // now validate each of the entries
        for (int i = 0; i < entries.size(); i++) {
            MapValueEntry e = (MapValueEntry)entries.get(i);
            // validate the real entry
            if (!e.equals((MapEntry)mapEntries.get(i))) {
                return false;
            }
        }
        return true;
    }
}


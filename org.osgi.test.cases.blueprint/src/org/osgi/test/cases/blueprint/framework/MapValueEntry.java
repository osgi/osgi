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

import java.util.Map;

import org.osgi.service.blueprint.reflect.Value;

/**
 * A descriptor for an index/value pair we expect to see in
 * component metadata.  This is the metadata descriptors for the
 * map entries, not the values themselves.
 */
public class MapValueEntry {
    // the entry key
    protected TestValue key;
    // the mapped value
    protected TestValue value;

    public MapValueEntry(TestValue key, TestValue value) {
        this.key = key;
        this.value = value;
    }

    public MapValueEntry(String key, String value) {
        this(new TestStringValue(null, key), new TestStringValue(null, value));
    }

    public MapValueEntry(String key, String value, Class type) {
        this(new TestStringValue(null, key), new TestStringValue(type, value));
    }

    public MapValueEntry(String key, TestValue value) {
        this(new TestStringValue(null, key), value);
    }

    public MapValueEntry(TestValue key, String value) {
        this(key, new TestStringValue(null, value));
    }

    public MapValueEntry(TestValue key, String value, Class type) {
        this(key, new TestStringValue(type, value));
    }

    public boolean equals(Map.Entry entry) {
        Value entryKey = (Value)entry.getKey();
        return key.equals(entryKey);
    }

    public void validate(ModuleMetadata moduleMetadata, Map.Entry entry) throws Exception {
        key.validate(moduleMetadata, (Value)entry.getKey());
        value.validate(moduleMetadata, (Value)entry.getValue());
    }

    public String toString() {
        return "MapEntry key: " + key + " value: " + value;
    }
}


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
import java.util.Map;
import java.util.HashMap;

import org.osgi.service.blueprint.reflect.MapValue;
import org.osgi.service.blueprint.reflect.Value;

public class TestMapValue extends TestValue {
    // The expected set of items in the List.
    protected MapValueEntry[] entries;
    // the default value type
    protected String valueType;
    // the default key type
    protected String keyType;

    public TestMapValue() {
        this(null, null, null);
    }

    public TestMapValue(MapValueEntry[] entries) {
        this(entries, null, null);
    }

    public TestMapValue(MapValueEntry[] entries, Class key, Class value) {
        super(MapValue.class);
        this.entries = entries;
        if (key != null) {
            keyType = key.getName();
        }
        if (value != null) {
            valueType = value.getName();
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
        // we might not have a validation list, so use this
        // as a wildcard placeholder if asked.
        if (entries == null) {
            return;
        }
        Map map = ((MapValue)v).getMap();
        // validate the size first
        assertEquals("Map value size mismatch", entries.length, map.size());
        assertEquals("Map default value type mismatch", valueType, ((MapValue)v).getValueType());
        assertEquals("Map default key type mismatch", keyType, ((MapValue)v).getKeyType());
        // we work off of a copy of this
        Map working = new HashMap(map);
        // now validate each of the entries
        for (int i = 0; i < entries.length; i++) {
            Map.Entry target = locateEntry(working, entries[i]);
            assertNotNull("Target value not found in item item", target);
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
        Map map = ((MapValue)v).getMap();
        // not the one we need
        if (entries.length != map.size()) {
            return false;
        }

        // we work off of a copy of this
        Map working = new HashMap(map);
        // now validate each of the entries
        for (int i = 0; i < entries.length; i++) {
            Map.Entry target = locateEntry(working, entries[i]);
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


    protected Map.Entry locateEntry(Map values, MapValueEntry target) {
        Iterator i = values.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry item = (Map.Entry)i.next();
            if (target.equals(item)) {
                i.remove();
                return item;
            }
        }
        return null;
    }
}


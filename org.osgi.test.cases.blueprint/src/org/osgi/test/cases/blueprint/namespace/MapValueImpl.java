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

package org.osgi.test.cases.blueprint.namespace;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.service.blueprint.reflect.*;


/**
 * A MapValue implementation class used for
 * testing NamespaceHandler functions.  The blueprint
 * service implementation must be capable of receiving
 * different implementations of these classes back
 * without error, so we'll generally perform deep
 * copy operations and replace all of the interfaces
 * in the metadata with our replacement versions.
 * As long as we have a true implementation, this
 * should work ok.
 */
public class MapValueImpl implements MapValue {
    private Map map;
    private String valueType;
    private String keyType;

    public MapValueImpl(Map map) {
        this.map = map;
    }

    public MapValueImpl(MapValue source) {
        this.map = new HashMap();
        // both the values and the keys are Value types, so we need to deep copy them
        Iterator i = source.getMap().entrySet().iterator();
        while (i.hasNext()) {
            Entry entry = (Entry)i.next();
            map.put(NamespaceUtil.cloneValue((Value)entry.getKey()), NamespaceUtil.cloneValue((Value)entry.getValue()));
        }
        valueType = source.getValueType();
        keyType = source.getKeyType();
    }


    /**
     * The Map of Value->Value mappings for this map-based value
     */
    public Map getMap() {
        return map;
    }

    /**
     * The value-type specified for the list elements, or null if none given
     */
	public String getValueType() {
        return valueType;
    }

    public void setValueType(String type) {
        valueType = type;
    }

	/**
	 * The key-type specified for map keys, or null if none given
	 */
	public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String type) {
        keyType = type;
    }
}


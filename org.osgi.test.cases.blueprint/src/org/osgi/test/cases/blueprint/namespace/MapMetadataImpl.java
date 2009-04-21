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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.osgi.service.blueprint.reflect.MapEntry;
import org.osgi.service.blueprint.reflect.MapMetadata;
import org.osgi.service.blueprint.reflect.Metadata;

/**
 * Metadata for a collection based value. Members of the array are instances of Metadata.
 * The Collection metadata can constrain (### convert) to a specific type.
 */
public class MapMetadataImpl extends NonNullMetadataImpl implements MapMetadata {
    private String valueTypeName;
    private String keyTypeName;
    private List entries;


    public MapMetadataImpl() {
        super();
        entries = new ArrayList();
    }

    public MapMetadataImpl(MapMetadata source) {
        super();
        valueTypeName = source.getValueTypeName();
        keyTypeName = source.getKeyTypeName();
        entries = new ArrayList();
        setEntries(source.getEntries());
    }


    /**
     * The value-type specified for the array
     *
     * The <code>value-type</code> attribute.
     */
	public String getValueTypeName() {
        return valueTypeName;
    }


    public void setValueTypeName(String typeName) {
        this.valueTypeName = typeName;
    }


    /**
     * The value-type specified for the array
     *
     * The <code>value-type</code> attribute.
     */
	public String getKeyTypeName() {
        return keyTypeName;
    }


    public void setKeyTypeName(String typeName) {
        this.keyTypeName = typeName;
    }

    /**
     * The of Metadata objects that describe the value.
     */
	public List /*<MapEntry>*/ getEntries() {
        return entries;
    }

    public void setEntries(List v) {
        Iterator i = v.iterator();
        while (i.hasNext()) {
            MapEntry entry = (MapEntry)i.next();
            entries.add(new MapEntryImpl(entry));
        }
    }

    public void addEntry(MapEntry e) {
        entries.add(e);
    }
}


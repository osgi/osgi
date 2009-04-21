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
import org.osgi.service.blueprint.reflect.PropsMetadata;
import org.osgi.service.blueprint.reflect.Metadata;
import org.osgi.service.blueprint.reflect.NonNullMetadata;


/**
 * A java.util.Properties based value. The properties are defined as string to
 * string. This means that the actual value can be returned.
 *
 * Defined in the <code>props</code> element.
 *
 */
public class PropsMetadataImpl extends NonNullMetadataImpl implements PropsMetadata {
    private List entries;

    public PropsMetadataImpl() {
        super();
        entries = new ArrayList();
    }

    public PropsMetadataImpl(PropsMetadata source) {
        super();
        entries = new ArrayList();
        setEntries(source.getEntries());
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


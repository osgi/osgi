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

import org.osgi.service.blueprint.reflect.MapEntry;
import org.osgi.service.blueprint.reflect.Metadata;
import org.osgi.service.blueprint.reflect.NonNullMetadata;


/**
 * Metadata for an entry. An entry is the member of a MapMetadata so that it
 * can be treated as a CollectionMetadata with entries.
 *
 * Defined in the <code>entry</code> element.
 *
 */
public class MapEntryImpl implements MapEntry {
    private NonNullMetadata key;
    private Metadata value;

    public MapEntryImpl(NonNullMetadata key, Metadata value) {
        this.key = key;
        this.value = value;
    }


    public MapEntryImpl(MapEntry source) {
        this.key = (NonNullMetadata)NamespaceUtil.cloneMetadata(source.getKey());
        this.value = NamespaceUtil.cloneMetadata(source.getValue());
    }


	/**
	 * Keys must be non-null.
	 *
	 * Defined in the <code>key</code> attribute or element.
	 *
	 * @return the metadata for the key
	 */
	public NonNullMetadata getKey() {
        return key;
    }

    public void setKey(NonNullMetadata key) {
        this.key = key;
    }

	/**
	 * Return the metadata for the value.
	 *
	 * Defined in the <code>value</code> attribute or element<.
	 *
	 * @return the metadata for the value
	 */

	public Metadata getValue() {
        return value;
    }

    public void setValue(Metadata value) {
        this.value = value;
    }
}


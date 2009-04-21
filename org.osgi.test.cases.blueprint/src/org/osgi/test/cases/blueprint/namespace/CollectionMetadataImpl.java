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

import org.osgi.service.blueprint.reflect.CollectionMetadata;
import org.osgi.service.blueprint.reflect.Metadata;

/**
 * Metadata for a collection based value. Members of the array are instances of Metadata.
 * The Collection metadata can constrain (### convert) to a specific type.
 */
public class CollectionMetadataImpl extends NonNullMetadataImpl implements CollectionMetadata {
    private Class collectionClass;
    private String typeName;
    private List values;


    public CollectionMetadataImpl(Class collectionClass) {
        super();
        values = new ArrayList();
    }

    public CollectionMetadataImpl(CollectionMetadata source) {
        this(source.getCollectionClass());
        typeName = source.getValueTypeName();
        setValues(getValues());
    }

	/**
	 * Provide the interface that this collection must implement.
	 *
	 * This is used for Arrays (Object[]), Set, and List. This information
	 * is encoded in the element name.
	 *
	 *
	 * @return The interface class that the collection must implement.
	 */
	public Class/*<?>*/ getCollectionClass() {
        return collectionClass;
    }

    public void setCollectionClass(Class c) {
        collectionClass = c;
    }


    /**
     * The value-type specified for the array
     *
     * The <code>value-type</code> attribute.
     */
	public String getValueTypeName() {
        return typeName;
    }


    public void setValueTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * The of Metadata objects that describe the value.
     */
	public List /*<Metadata>*/ getValues() {
        return values;
    }

    public void setValues(List v) {
        Iterator i = v.iterator();
        while (i.hasNext()) {
            values.add(NamespaceUtil.cloneMetadata((Metadata)i.next()));
        }
    }

    public void addValue(Metadata v) {
        values.add(v);
    }
}



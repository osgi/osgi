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

import java.util.List;

import org.osgi.service.blueprint.reflect.CollectionMetadata;
import org.osgi.service.blueprint.reflect.Metadata;


public class TestCollectionValue extends TestValue {
    // The expected set of items in the List.
    protected TestValue[] entries;
    // the default element type for the list
    protected String typeName;
    // The class of collection to be created
    protected Class collectionType;

    public TestCollectionValue(Class collectionType, TestValue[] entries, Class listType) {
        super(CollectionMetadata.class);
        this.collectionType = collectionType;
        this.entries = entries;
        if (listType != null) {
            typeName = listType.getName();
        }
    }


    /**
     * An expected CollectionMetadata item against a received one.
     *
     * @param blueprintMetadata
     *               The metadata source for the validation.
     * @param v
     *
     * @exception Exception
     */
    public void validate(BlueprintMetadata blueprintMetadata, Metadata v) throws Exception {
        // This validates the metadata type.
        super.validate(blueprintMetadata, v);
        // we might not have a validation list if we're just interested
        // in verifying this is a ListValue
        if (entries == null) {
            return;
        }

        CollectionMetadata meta = (CollectionMetadata)v;
        assertEquals("Mismatch in collection type.", collectionType, meta.getCollectionClass());
        // get the array of values
        List list = meta.getValues();
        // validate the size first
        assertEquals("Collection value size mismatch", entries.length, list.size());
        assertEquals("Collection value default type mismatch", typeName, meta.getValueType());
        // now validate each of the entries
        for (int i = 0; i < entries.length; i++) {
            // nulls appear as NullValue items, so everything should match
            entries[i].validate(blueprintMetadata, (Metadata)list.get(i));
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
        CollectionMetadata meta = (CollectionMetadata)v;

        if (collectionType != meta.getCollectionClass()) {
            return false;
        }

        // get the array of values
        List list = meta.getValues();
        // not the one we need
        if (entries.length != list.size()) {
            return false;
        }

        // now validate each of the entries
        for (int i = 0; i < entries.length; i++) {
            // validate the real entry
            if (!entries[i].equals((Metadata)list.get(i))) {
                return false;
            }
        }
        return true;
    }
}


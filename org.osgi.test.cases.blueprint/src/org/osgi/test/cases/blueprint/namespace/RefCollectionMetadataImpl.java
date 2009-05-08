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

import java.util.List;

import org.osgi.service.blueprint.reflect.RefCollectionMetadata;
import org.osgi.service.blueprint.reflect.Metadata;
import org.osgi.service.blueprint.reflect.Target;


/**
 * Service reference that binds to a collection of matching services from the
 * OSGi service registry. This is the <code>ref-list</code> or
 * <code>ref-set</code> element.
 *
 */
public class RefCollectionMetadataImpl extends ServiceReferenceMetadataImpl implements RefCollectionMetadata {
    private Target comparator;
    private Class collectionType = List.class;
    private int orderingBasis;
    private int memberType;

    public RefCollectionMetadataImpl() {
        this((String)null);
    }

    public RefCollectionMetadataImpl(String name) {
        super(name);
    }


    public RefCollectionMetadataImpl(RefCollectionMetadata source) {
        super(source);

        comparator = (Target)NamespaceUtil.cloneMetadata(source.getComparator());
        collectionType = source.getCollectionType();
        orderingBasis = source.getOrderingBasis();
    }

    /**
     * The comparator specified for ordering the collection, or null if no
     * comparator was specified.
     *
     * @return if a comparator was specified then a Value object identifying the
     * comparator (a ComponentValue, ReferenceValue, or ReferenceNameValue) is
     * returned. If no comparator was specified then null will be returned.
     */
    public Target getComparator() {
        return comparator;
    }

    public void setComparator(Target comparator) {
        this.comparator = comparator;
    }


    /**
     * The type of collection to be created.
     *
     * @return Class object for the specified collection type (List, Set).
     */
    public Class getCollectionType() {
        return collectionType;
    }


    public void setCollectionType(Class type) {
        collectionType = type;
    }


    /**
     * The basis on which to perform natural ordering, if specified.
     *
     * @return one of ORDERING_BASIS_SERVICE and ORDERING_BASIS_SERVICE_REFERENCE
     */
    public int getOrderingBasis() {
        return orderingBasis;
    }

    public void setOrderingBasis(int b) {
        orderingBasis = b;
    }

    /**
     * Whether the collection will contain service instances, or service references
     */
    public int getMemberType() {
        return memberType;
    }

    public void setMemberType(int memberType) {
           this.memberType = memberType;
    }
}


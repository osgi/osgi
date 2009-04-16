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

import org.osgi.service.blueprint.reflect.CollectionBasedServiceReferenceComponentMetadata;
import org.osgi.service.blueprint.reflect.Value;


/**
 * A CollectionBasedServiceReferenceComponentMetadata implementation class used for
 * testing NamespaceHandler functions.  The blueprint
 * service implementation must be capable of receiving
 * different implementations of these classes back
 * without error, so we'll generally perform deep
 * copy operations and replace all of the interfaces
 * in the metadata with our replacement versions.
 * As long as we have a true implementation, this
 * should work ok.
 */
public class CollectionBasedServiceReferenceComponentMetadataImpl extends ServiceReferenceComponentMetadataImpl implements CollectionBasedServiceReferenceComponentMetadata {
    private Value comparator;
    private Class collectionType = List.class;
    private int orderingBasis;
    private int memberType;

    public CollectionBasedServiceReferenceComponentMetadataImpl() {
        this((String)null);
    }

    public CollectionBasedServiceReferenceComponentMetadataImpl(String name) {
        super(name);
    }


    public CollectionBasedServiceReferenceComponentMetadataImpl(CollectionBasedServiceReferenceComponentMetadata source) {
        super(source);

        comparator = NamespaceUtil.cloneValue(source.getComparator());
        collectionType = source.getCollectionType();
        orderingBasis = source.getOrderingComparisonBasis();
    }

    /**
     * The comparator specified for ordering the collection, or null if no
     * comparator was specified.
     *
     * @return if a comparator was specified then a Value object identifying the
     * comparator (a ComponentValue, ReferenceValue, or ReferenceNameValue) is
     * returned. If no comparator was specified then null will be returned.
     */
    public Value getComparator() {
        return comparator;
    }

    public void setComparator(Value comparator) {
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
     * @return one of ORDER_BASIS_SERVICES and ORDER_BASIS_SERVICE_REFERENCES
     */
    public int getOrderingComparisonBasis() {
        return orderingBasis;
    }

    public void setOrderingComparisonBasis(int b) {
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

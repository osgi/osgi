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

import org.osgi.service.blueprint.reflect.CollectionBasedServiceReferenceComponentMetadata;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.ServiceReferenceComponentMetadata;

/**
 * A single referenced service in the BlueprintContext metadata.
 */
public class ReferenceCollection extends ReferencedServiceBase {
    protected Class collectionType;
    protected TestValue comparator;
    protected int orderingBasis;
    protected int memberType;

    /**
     * Create a ReferenceService descriptor from a single interface.
     *
     * @param interfaceClass
     *                  A single interface class used for the reference.
     * @param availability
     *                  The availability setting.
     * @param filter    The declared filter string for the reference.
     * @param listeners An expected set of listener metadata.
     */
    public ReferenceCollection(String name, Class interfaceClass, int availability, String filter, BindingListener[] listeners,
            Class collectionType, TestValue comparator, int orderingBasis, int memberType) {
        this(name, new Class[] { interfaceClass }, availability, filter, listeners, collectionType, comparator, orderingBasis, memberType);
    }

    /**
     * Create a ReferenceService descriptor.
     *
     * @param interfaces The set of interfaces to access.
     * @param availability
     *                   The availability setting.
     * @param filter     The declared filter string for the reference.
     * @param listeners  An expected set of listener metadata.
     */
    public ReferenceCollection(String name, Class[] interfaces, int availability, String filter, BindingListener[] listeners,
             Class collectionType, TestValue comparator, int orderingBasis, int memberType) {
        super(name, interfaces, availability, filter, listeners);
        this.collectionType = collectionType;
        this.comparator = comparator;
        this.orderingBasis = orderingBasis;
        this.memberType = memberType;
    }


    /**
     * Perform additional validation on a service.
     *
     * @param meta   The service metadata
     *
     * @exception Exception
     */
    public void validate(ModuleMetadata moduleMetadata, ServiceReferenceComponentMetadata metadata) throws Exception {
        assertTrue("Mismatch on service reference type", metadata instanceof CollectionBasedServiceReferenceComponentMetadata);
        // do the base validation
        super.validate(moduleMetadata, metadata);
        CollectionBasedServiceReferenceComponentMetadata meta = (CollectionBasedServiceReferenceComponentMetadata)metadata;
        assertEquals(collectionType, meta.getCollectionType());
        if (comparator != null) {
            comparator.validate(moduleMetadata, meta.getComparator());
        }
        assertEquals(orderingBasis, meta.getOrderingComparisonBasis());
    }

    /**
     * Determine if this descriptor matches the basic attributes of
     * an exported service.  This is used to locate potential matches.
     *
     * @param meta   The candidate exported service
     *
     * @return true if this service matches on all of the specifics.
     */
    public boolean matches(ComponentMetadata componentMeta) {
        // we only handle service reference component references.
        if (!(componentMeta instanceof CollectionBasedServiceReferenceComponentMetadata)) {
            return false;
        }

        CollectionBasedServiceReferenceComponentMetadata meta = (CollectionBasedServiceReferenceComponentMetadata)componentMeta;
        if (collectionType != meta.getCollectionType()) {
            return false;
        }

        // match on the remaining bits
        return super.matches(componentMeta);
    }
}


/*
 * $Id$
 *
 * Copyright (c) The OSGi Alliance (2009). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.blueprint.namespace;

import java.util.Collection;
import java.util.List;

import org.osgi.service.blueprint.reflect.*;


/**
 * A ComponentMetadata implementation class used for
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

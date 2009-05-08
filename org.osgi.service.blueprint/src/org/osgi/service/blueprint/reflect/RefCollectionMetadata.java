/*
 * Copyright (c) OSGi Alliance (2008, 2009). All Rights Reserved.
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
package org.osgi.service.blueprint.reflect;

/**
 * Service reference that binds to a collection of matching services from the
 * OSGi service registry. This is the <code>ref-list</code> or
 * <code>ref-set</code> element.
 *
 */
public interface RefCollectionMetadata extends ServiceReferenceMetadata {

	/**
	 * Create ordering based on comparison of service objects.
	 */
	public static final int ORDERING_BASIS_SERVICE= 1;

	/**
	 * Create ordering based on comparison of service reference objects.
	 */
	public static final int ORDERING_BASIS_SERVICE_REFERENCE = 2;

	/**
	 * Collection contains service instances
	 *
	 */
	public static final int MEMBER_TYPE_SERVICE_INSTANCE = 1;

	/**
	 * Collection contains service references
	 */
	public static final int MEMBER_TYPE_SERVICE_REFERENCE = 2;

	/**
	 * The type of collection to be created.
	 *
	 * This is implied by the element name: <code>ref-list</code> or <code>ref-set</code>.
	 *
	 * @return Class object for the specified collection type (List, Set).
	 */
	Class/* <?> */getCollectionType();

	/**
	 * The comparator specified for ordering the collection, or <code>null</code> if no
	 * comparator was specified.
	 *
	 * Defined in the <code>comparator</code> child element or <code>comparator-ref</code>
	 * attribute.
	 *
	 * @return if a comparator was specified then a Value object identifying the
	 *         comparator (a ComponentValue, ReferenceValue, or
	 *         ReferenceNameValue) is returned. If no comparator was specified
	 *         then null will be returned.
	 */
	Target getComparator();

	/**
	 * The basis on which to perform ordering, if specified.
	 *
	 * Defined in the <code>ordering-basis</code> attribute.
	 *
	 * @return one of ORDERING_BASIS_SERVICES and ORDERING_BASIS_SERVICE_REFERENCE
	 */
	int getOrderingBasis();

	/**
	 * Whether the collection will contain service instances, or service
	 * references
	 * Defined in the <code>member-type</code> attribute.
	 */
	int getMemberType();
}

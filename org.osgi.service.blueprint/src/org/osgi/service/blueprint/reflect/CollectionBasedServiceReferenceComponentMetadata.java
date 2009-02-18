/*
 * Copyright (c) OSGi Alliance (2000, 2008). All Rights Reserved.
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
 * Service reference that binds to a collection of matching services from
 * the OSGi service registry.
 *
 */
public interface CollectionBasedServiceReferenceComponentMetadata extends
		ServiceReferenceComponentMetadata {
	
		/**
		 * Create ordering based on comparison of service objects.
		 */
		public static final int ORDER_BASIS_SERVICES = 1;
		
		/**
		 * Create ordering based on comparison of service reference objects.
		 */
		public static final int ORDER_BASIS_SERVICE_REFERENCES = 2;
		
		/**
		 * Collection contains service instances
		 */
		public static final int MEMBER_TYPE_SERVICES = 1;
		
		/**
		 * Collection contains service references
		 */
		public static final int MEMBER_TYPE_SERVICE_REFERENCES = 2;

		/**
		 * The type of collection to be created.
		 * 
		 * @return Class object for the specified collection type (List, Set).
		 */
		Class getCollectionType();
	
		/**
		 * The comparator specified for ordering the collection, or null if no
		 * comparator was specified.
		 * 
		 * @return if a comparator was specified then a Value object identifying the 
		 * comparator (a ComponentValue, ReferenceValue, or ReferenceNameValue) is 
		 * returned. If no comparator was specified then null will be returned.
		 */
		Value getComparator();
		
		/**
		 * The basis on which to perform ordering, if specified.
		 * 
		 * @return one of ORDER_BASIS_SERVICES and ORDER_BASIS_SERVICE_REFERENCES
		 */
		int getOrderingComparisonBasis();
		
		/**
		 * Whether the collection will contain service instances, or service references
		 */
		int getMemberType();
}

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

import java.util.List;

/**
 * Metadata for a collection based value. Members of the collection are
 * instances of Metadata. This Metadata can constrain the members to a specific
 * type.
 */

public interface CollectionMetadata extends NonNullMetadata {

	/**
	 * Provide the interface that this collection must implement.
	 * 
	 * This is used for <code>Arrays (Object[])</code>, <code>Set</code>,
	 * and <code>List</code>. This information is encoded in the element
	 * name.
	 * 
	 * @return The interface class that the collection must implement or
	 *         <code>Object[]</code> for arrays.
	 */
	Class/* <?> */getCollectionClass();

	/**
	 * The value-type specified for the members of the collection or array.
	 * 
	 * The <code>value-type</code> attribute.
	 * 
	 * @return The value type for this Collection Metadata
	 */
	String getValueType();

	/**
	 * The of Metadata that describe the actual Collection or array.
	 * 
	 * @return A list of Metadata for the values of a collection or array.
	 */
	List /* <Metadata> */getValues();
}

/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.dmt.ddf;

import java.util.Iterator;

/**
 * The NODE represents a node in the Device Management Tree.
 */
public interface NODE {
	/**
	 * The node's name in the tree. This is only the name, not the complete
	 * path.
	 * 
	 * @return Node's name
	 */
	String getName();

	/**
	 * The complete path up unto the root of the current session.
	 * 
	 * @return the relative part unto the root of the current session.
	 */
	String getPath();

	/**
	 * The children of this node or {@code null} if this is a leaf node.
	 * 
	 * @return the children of this node.
	 */
	Iterator<NODE> getChildren();

	/**
	 * Return the parent node of this node.
	 * 
	 * @return the parent node.
	 */
	NODE getParent();

	/**
	 * Only for a leaf node, return its value converted to the given type.
	 * 
	 * @param <T> The requested type
	 * @param type the class representing the requested type
	 * @return A converted object or {@code null}.
	 */
	<T> T getValue(Class<T> type);

}

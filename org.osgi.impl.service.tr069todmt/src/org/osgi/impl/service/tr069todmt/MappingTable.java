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

package org.osgi.impl.service.tr069todmt;

import java.util.Enumeration;
import java.util.Hashtable;
import org.osgi.service.dmt.Uri;

/**
 *
 */
public class MappingTable extends Hashtable<String, Object> {

	private static final long	serialVersionUID	= 1L;

	@Override
	public synchronized Object remove(Object key) {
		/* remove the whole subtree */
		String nodeToRemove = ((String) key).concat(Uri.PATH_SEPARATOR);
		Enumeration<String> nodes = keys();
		while (nodes.hasMoreElements()) {
			String node = nodes.nextElement();
			if (node.startsWith(nodeToRemove) || node.equals(key)) {
				super.remove(node);
			}

		}
		return super.remove(key);
	}

	void rename(String oldKey, String newKey) {
		Enumeration<String> nodes = keys();
		String prefix = oldKey.concat(Uri.PATH_SEPARATOR);
		while (nodes.hasMoreElements()) {
			String node = nodes.nextElement();
			if (node.startsWith(prefix) || node.equals(oldKey)) {
				super.put(node.replaceAll(oldKey, newKey), super.remove(node));
			}
		}
	}
}

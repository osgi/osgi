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
package org.osgi.impl.service.dmt.export;

import java.io.IOException;
import java.util.Map;

import org.osgi.service.permissionadmin.PermissionInfo;

public interface DmtPrincipalPermissionAdmin {
    /**
     * Returns the mapping of principal names to Java permissions. The
     * permissions of a principal are represented as an array of PermissionInfo
     * objects. 
     * <p>
     * The returned map may be modified without affecting the stored
     * permissions.
     * 
     * @return a <code>Map</code> containing principal names and the
     *         permissions associated with them
     */
	Map<String,PermissionInfo[]> getPrincipalPermissions();
    
    /**
     * Replaces the current permission table with the argument. The given map
     * must contain <code>String</code> keys and <code>PermissionInfo[]</code>
     * values, otherwise an exception is thrown.
     * 
     * @param permissions the new set of principals and their permissions
     * @throws IllegalArgumentException if a key in the given <code>Map</code>
     *         is not a <code>String</code> or a value is not an array of
     *         <code>PermissionInfo</code> objects
     * @throws IOException if there is an error updating the persistent
     *         permission store
     */
	void setPrincipalPermissions(Map<String,PermissionInfo[]> permissions)
			throws IOException;
}

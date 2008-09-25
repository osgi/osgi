/*
 * Copyright (c) OSGi Alliance (2004, 2008). All Rights Reserved.
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

package org.osgi.service.condpermadmin;

/**
 * A binding of a set of Conditions to a set of Permissions. Instances of this
 * interface are obtained from the Conditional Permission Admin service.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public interface ConditionalPermissionInfo extends ConditionalPermissionInfoBase {
	/**
	 * Removes this Conditional Permission Info from the Conditional Permission
	 * Table.
	 * <p>
	 * Since this method changes the underlying permission table any
	 * {@link ConditionalPermissionsUpdate}s that were created prior to calling
	 * this method can no longer be committed.
	 * 
	 * @throws SecurityException If the caller does not have
	 *         <code>AllPermission</code>.
	 */
	public void delete();
}

/*
 * $Date$
 * 
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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

import java.util.List;

/**
 * Update the Conditional Permission Table. There may be many update objects in
 * the system at one time. If commit is called and the Conditional Permission
 * Table has been modified since this update was created, then the call to
 * commit will fail and this object should be discarded.
 * 
 * @version $Revision$
 * @since 1.1
 */
public interface ConditionalPermissionsUpdate {
	/**
	 * This method returns the list of {@link ConditionalPermissionInfoBase}s
	 * for this update. This list is originally based on the Conditional
	 * Permission Table at the time this update was created. The list returned
	 * by this method will be replace the Conditional Permission Table if commit
	 * is called and is successful.
	 * <p>
	 * The elements of the list must NOT be instances of type
	 * {@link ConditionalPermissionInfo}, but must rather be of type
	 * {@link ConditionalPermissionInfoBase}. This is to ensure the
	 * {@link ConditionalPermissionInfo#delete delete} method cannot be
	 * mistakenly used.
	 * <p>
	 * The list returned by this method is ordered and the most significant
	 * table entry is the first entry in the list.
	 * 
	 * @return A <code>List</code> of the Conditional Permission Info Bases
	 *         which represent the Conditional Permissions maintained by this
	 *         update. Modifications to this list will not affect the
	 *         Conditional Permission Table until successfully committed. The
	 *         elements in this list must be of type
	 *         {@link ConditionalPermissionInfoBase}. The list may be empty if
	 *         the Conditional Permission Table was empty when this update was
	 *         created.
	 */
  public List getConditionalPermissionInfoBases();

	/**
	 * Commit the update. If no changes have been made to the Conditional
	 * Permission Table since this update was created, then this method will
	 * replace the Conditional Permission Table with this update's Conditional
	 * Permissions. This method may only be successfully called once on this
	 * object.
	 * <p>
	 * If any of the {@link ConditionalPermissionInfoBase} objects in the update
	 * list has <code>null</code> as a name it will be replaced with a
	 * {@link ConditionalPermissionInfoBase} object that has a generated name
	 * which is unique within the list.
	 * <p>
	 * No two entries in this update's Conditional Permissions may have the same
	 * name. Other consistency checks may also be performed. If the update's
	 * Conditional Permissions are determined to be inconsistent in some way
	 * then an <code>IllegalStateException</code> will be thrown.
	 * <p>
	 * This method returns <code>false</code> if the Conditional Permission
	 * Table has been modified since the creation of this update.
	 * 
	 * @return <code>true</code> if the commit was successful.
	 *         <code>false</code> if the Conditional Permission Table has been
	 *         modified since the creation of this update.
	 * @throws SecurityException If the caller does not have
	 *         <code>AllPermission</code>.
	 * @throws IllegalStateException If the update's Conditional Permissions are
	 *         not valid or inconsistent. For example, if this update has two
	 *         Conditional Permissions in it with the same name, then this
	 *         exception will be thrown.
	 * 
	 */
  public boolean commit();
}

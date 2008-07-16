/*
 * $Date: 2007-12-19 14:42:59 -0600 (Wed, 19 Dec 2007) $
 * 
 * Copyright (c) OSGi Alliance (2005, 2007). All Rights Reserved.
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
 * An object that can be used to create updates to
 * the ConditionalPermissionAdmin table.  There may
 * be many Update objects in the system at one time.
 * If commit is called and the underlying Permission
 * Table has previously been modified then commit will
 * fail.
 * 
 * @version $Revision: 1.0 $
 */
public interface ConditionalPermissionAdminUpdate {
  /**
   * This method returns the list of
   * org.osgi.service.condpermadmin ConditionalPermissionInfoBase object
   * for this Update.  This list is originally based on the active
   * Conditional Permission Admin table at the time the Update was
   * created.  The list returned by this method will be the list
   * that becomes the running Conditional Permission Admin table if
   * commit is called and is successful.
   * <p>
   * The elements of the list must NOT be instances of type
   * org.osgi.service.condpermadmin.ConditionalPermissionInfo,
   * but must rather be of type
   * org.osgi.service.condpermadmin.ConditionalPermissionInfoBase.  This
   * is to ensure the "delete" method cannot be mistakenly used.
   * <p>
   * The List returned is ordered and has the most significant
   * table entries in the list first
   * 
   * @return A List of the Conditional Permission Info Bases which represent
   * a Conditional Permission Admin table maintained by this Update.  Modifications
   * to this list will not affect the running Conditional Permission Admin
   * table.  The elements in this List will be of type
   * org.osgi.service.condpermadmin.ConditionalPermissionInfoBase.  If
   * there are no elements in this Updates' Conditional
   * Permission Admin table then this method will return an empty 
   * non-null List.
   */
  public List getConditionalPermissionInfos();
  
  /**
   * Commits this Update.  If no changes have been made to the
   * Conditional Permission Admin table since this Update was created
   * then calling this method will make the running Conditional Permission
   * Admin table reflect this Updates' Conditional Permission Admin table.
   * Any Update may only call commit successfully once.
   * <p>
   * If any of the ConditionalPermissionInfoBase
   * objects in the update list has null as a name it will be replaced
   * with a ConditionalPermisionInfoBase object that has a list wide
   * unique generated name.
   * <p>
   * No two entries in the Updates' Conditional Permission Admin table
   * may have the same name.  Other consistency checks may also be performed.
   * If the Updates' Conditional Permission Admin table is determined to 
   * be inconsistent in some way then an IllegalStateException will be
   * thrown.
   * <p>
   * This method returns false if the running Conditional Permission table
   * has been modified since the creation of this Update.
   * 
   * @return true if the commit was successful, false if this commit
   * was called on an update that lost a race with some other update
   * in the system
   * @throws SecurityException If the caller does not have
   *         <code>AllPermission</code>
   * @throws IllegalStateException If the Updates' Conditional Permission
   *         Admin table is not valid.  For example if this Updates' Conditional
   *         Permission Admin table has two objects in it with the same name then
   *         this exception will be thrown
   *         
   */
  public boolean commit();
}

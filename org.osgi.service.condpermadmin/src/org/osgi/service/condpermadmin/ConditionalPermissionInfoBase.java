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

import org.osgi.service.permissionadmin.PermissionInfo;

/**
 * A binding of a set of Conditions to a set of Permissions. Instances of this
 * interface are obtained from the Conditional Permission Admin service.
 * 
 * @Immutable
 * @version $Revision: 1.11 $
 */
public interface ConditionalPermissionInfoBase {
  /**
   * This string is used to indicate that a row in the conditional
   * permission admin table should return a decision of ALLOWs if
   * the conditions all match and at least one of the permissions
   * indicate the requested right.
   */
  public final static String ALLOW = "allow";
  
  /**
   * This string is used to indicate that a row in the conditional
   * permission admin table should return a decision of DENY if
   * the conditions all match and at least one of the permissions
   * indicate the requested right.
   */
  public final static String DENY = "deny";
  
  /**
   * Returns the Condition Infos for the Conditions that must be satisfied to
   * enable the Permissions.
   * 
   * @return The Condition Infos for the Conditions in this Conditional
   *         Permission Info.
   */
  public ConditionInfo[] getConditionInfos();

  /**
   * Returns the Permission Infos for the Permission in this Conditional
   * Permission Info.
   * 
   * @return The Permission Infos for the Permission in this Conditional
   *         Permission Info.
   */
  public PermissionInfo[] getPermissionInfos();
  
  /**
   * Returns the Decision for this Conditional Permission Info.
   * 
   * @return One of the following values:<UL>
   * <LI>ALLOW - The right should be granted</LI>
   * <LI>DENY - The right should NOT be granted</LI>
   * </UL>
   */
  public String getDecision();

  /**
   * Returns the name of this Conditional Permission Info.
   * 
   * @return The name of this Conditional Permission Info.
   */
  public String getName();
}

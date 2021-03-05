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

package org.osgi.test.cases.condpermadmin.testcond;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Permission;
import java.util.Dictionary;

import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;

public class TestConditionRecursive extends TestCondition {
	private static Permission permission;
  private static Object service;

  public TestConditionRecursive(Bundle bundle, ConditionInfo info) {
    super(bundle, info);
  }

  public static Condition getCondition(Bundle bundle, ConditionInfo info) {
	  TestConditionRecursive tc = new TestConditionRecursive(bundle, info);
	  if (tc.testBundleId < 0 || bundle.getBundleId() == tc.testBundleId)
		  return tc;
	  return Condition.FALSE;
  }
  
  public static void setPermission(Permission perm) {
    permission = perm;
  }
  
  public static void setService(Object serv) {
    service = serv;
  }
  
	public boolean isSatisfied() {
		// System.out.println("#isSatisfied method checked of " + info);
		satisfOrder.add(name);
		try {
			try {
				Method method = service.getClass().getMethod("checkPermission",
						new Class[] {Permission.class});
				method.invoke(service, new Object[] {permission});
			}
			catch (InvocationTargetException ite) {
				throw ite.getTargetException();
			}
		}
		catch (SecurityException e) {
			satisfOrder.add(SecurityException.class.getName());
		}
		catch (Throwable t) {
			satisfOrder.add(t.toString());
		}
		return satisfied;
	}

	public boolean isSatisfied(Condition[] conds,
			Dictionary<Object,Object> context) {
    for (int i = 0; i < conds.length; i++) {
      if (!conds[i].isSatisfied()) {
        return false;
      }
    }
    return true;
  }
}

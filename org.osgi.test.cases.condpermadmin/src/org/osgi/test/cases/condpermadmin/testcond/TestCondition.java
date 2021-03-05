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

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;


public class TestCondition implements Condition {
  private static final String CONDITION_TYPE = TestCondition.class.getName();
  private static final String CONDITION_TYPE2 = TestConditionRecursive.class.getName();
  
	public static List<String>	satisfOrder		= new ArrayList<>();
  private   static boolean varmutable;
  
  protected boolean postponed;
  protected boolean satisfied;
  protected boolean mutable;
  protected String  name;
  protected String  info;
  protected final long testBundleId;
  
  public TestCondition(Bundle bundle, ConditionInfo info) {
    if (!CONDITION_TYPE.equals(info.getType()) && !CONDITION_TYPE2.equals(info.getType()))
      throw new IllegalArgumentException("ConditionInfo must be of type \"" + CONDITION_TYPE + "\"");
    String[] args = info.getArgs();
    if (args.length < 4 || args.length > 5)
      throw new IllegalArgumentException("Illegal number of args: " + args.length);
		postponed = Boolean.parseBoolean(args[0]);
		satisfied = Boolean.parseBoolean(args[1]);
		mutable = Boolean.parseBoolean(args[2]);
    name = args[3];
    setInfoString(args);
    if (args.length == 5)
    	testBundleId = Long.parseLong(args[4]);
    else
    	testBundleId = -1;
	}	
  
  public static Condition getCondition(Bundle bundle, ConditionInfo info) {
	  TestCondition tc = new TestCondition(bundle, info);
	  if (tc.testBundleId < 0 || bundle.getBundleId() == tc.testBundleId)
		  return tc;
    return Condition.FALSE;
  }
  
	public boolean isPostponed() {
		return postponed;
	}

	public boolean isSatisfied() {
//    System.out.println("#isSatisfied method checked of " + info);
    if (varmutable) mutable = false;
		satisfOrder.add(name);
		return satisfied;
	}

	public boolean isMutable() {
		return mutable;
	}
  
  public static void changeToImmutable(boolean state) {
    varmutable = state;
  }
  
  private void setInfoString(String[] args) {
    StringBuffer buf = new StringBuffer();
    buf.append("[");
    buf.append(args[3]);
    for (int i = 0; i < args.length - 1; i++) {
      buf.append(" \"");
      buf.append(args[i]);
      buf.append("\"");
    }
    buf.append("]");
    info = buf.toString();
  }

  public int hashCode() {
    return name.hashCode();
  }
  
  public boolean equals(Object obj) {
    return obj instanceof TestCondition && name.equals(((TestCondition) obj).name);
  }
  
	public boolean isSatisfied(Condition[] conds,
			Dictionary<Object,Object> context) {
		for (int i = 0; i < conds.length; i++) {
      Boolean isSatisfied = (Boolean) context.get(conds[i]);
      if (isSatisfied == null) {
        isSatisfied = conds[i].isSatisfied() ? Boolean.TRUE : Boolean.FALSE; 
        context.put(conds[i], isSatisfied);
      }
			if (!isSatisfied.booleanValue()) {
				return false;
			}
		}
		return true;
	}
  
  public static String[] getSatisfOrder() {
		String[] result = satisfOrder.toArray(new String[0]);
		satisfOrder.clear();
    return result;
  }	
}

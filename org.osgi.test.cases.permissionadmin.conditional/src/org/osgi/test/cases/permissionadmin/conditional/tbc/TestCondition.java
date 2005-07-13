
package org.osgi.test.cases.permissionadmin.conditional.tbc;

import java.util.Dictionary;

import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.framework.Bundle;


public class TestCondition implements Condition {
  private static final String CONDITION_TYPE = "org.osgi.test.cases.permissionadmin.conditional.tbc.TestCondition";
	  
  private boolean postponed;
	private boolean satisfied;
	private boolean mutable;
	
	public TestCondition(Bundle bundle, ConditionInfo info) {
    if (!CONDITION_TYPE.equals(info.getType()))
      throw new IllegalArgumentException("ConditionInfo must be of type \"" + CONDITION_TYPE + "\"");
    String[] args = info.getArgs();
    if (args.length != 3)
      throw new IllegalArgumentException("Illegal number of args: " + args.length);
    postponed = (new Boolean(args[0])).booleanValue();
		satisfied = (new Boolean(args[1])).booleanValue();
		mutable = (new Boolean(args[2])).booleanValue();
	}	
	
	public boolean isPostponed() {
		return postponed;
	}

	public boolean isSatisfied() {
    if (postponed) System.out.println("#isSatisfied method cheched of postponed TestCondition");
		return satisfied;
	}

	public boolean isMutable() {
		return mutable;
	}

	public boolean isSatisfied(Condition[] conds, Dictionary context) {
		for (int i = 0; i < conds.length; i++) {
			if (!conds[i].isSatisfied()) {
				return false;
			}
		}
		return true;
	}

	
}

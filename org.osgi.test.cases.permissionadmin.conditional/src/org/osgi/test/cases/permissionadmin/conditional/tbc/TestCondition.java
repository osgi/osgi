
package org.osgi.test.cases.permissionadmin.conditional.tbc;

import java.util.Dictionary;

import org.osgi.service.condpermadmin.Condition;
import org.osgi.framework.Bundle;


public class TestCondition implements Condition {

	private boolean evaluated;
	private boolean satisfied;
	private boolean mutable;
	
	public TestCondition(Bundle bundle, String isEvaluated, String isSatisfied, String isMutable) {
		evaluated = (new Boolean(isEvaluated)).booleanValue();
		satisfied = (new Boolean(isSatisfied)).booleanValue();
		mutable = (new Boolean(isMutable)).booleanValue();
	}
	
//	public static Condition getInstance(Bundle bundle, String[] args) {
//		return null;
//	}
	
	
	public boolean isPostponed() {
		return !evaluated;
	}


	public boolean isSatisfied() {
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

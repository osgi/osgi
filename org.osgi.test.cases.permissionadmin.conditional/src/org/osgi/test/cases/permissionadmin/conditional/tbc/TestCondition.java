
package org.osgi.test.cases.permissionadmin.conditional.tbc;

import java.util.Dictionary;
import java.util.Vector;

import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.framework.Bundle;


public class TestCondition implements Condition {
  private static final String CONDITION_TYPE = "org.osgi.test.cases.permissionadmin.conditional.tbc.TestCondition";
	private static final String TEST_BUNDLE = "http://172.22.102.4/permissionadmin.conditional/tb1.jar";
  public static Vector satisfOrder = new Vector();
  
  private String info;
  private boolean postponed;
	private boolean satisfied;
	private boolean mutable;
  private String  name;
  
  public TestCondition(Bundle bundle, ConditionInfo info) {
    if (!CONDITION_TYPE.equals(info.getType()))
      throw new IllegalArgumentException("ConditionInfo must be of type \"" + CONDITION_TYPE + "\"");
    String[] args = info.getArgs();
    if (args.length != 4)
      throw new IllegalArgumentException("Illegal number of args: " + args.length);
    postponed = (new Boolean(args[0])).booleanValue();
		satisfied = (new Boolean(args[1])).booleanValue();
		mutable = (new Boolean(args[2])).booleanValue();
    name = args[3];
    setInfoString(args);
	}	
  
  static public Condition getCondition(Bundle bundle, ConditionInfo info) {
    if (TEST_BUNDLE.equals(bundle.getLocation())) {
      return new TestCondition(bundle, info);
    }
    return Condition.FALSE;
  }    
  
	
	public boolean isPostponed() {
		return postponed;
	}

	public boolean isSatisfied() {
    System.out.println("isSatisfied method checked of " + info);
    satisfOrder.addElement(name);
		return satisfied;
	}

	public boolean isMutable() {
		return mutable;
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
    this.info =buf.toString();
  }

	public boolean isSatisfied(Condition[] conds, Dictionary context) {
		for (int i = 0; i < conds.length; i++) {
			if (!conds[i].isSatisfied()) {
				return false;
			}
		}
		return true;
	}
  
  public static String[] getSatisfOrder() {
    String[] result = new String[satisfOrder.size()];
    satisfOrder.copyInto(result);
    satisfOrder.removeAllElements();
    return result;
  }	
}

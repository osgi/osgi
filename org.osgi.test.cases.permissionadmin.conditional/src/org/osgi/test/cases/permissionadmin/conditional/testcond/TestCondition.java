
package org.osgi.test.cases.permissionadmin.conditional.testcond;

import java.util.Dictionary;
import java.util.Vector;

import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.framework.Bundle;


public class TestCondition implements Condition {
  private static final String CONDITION_TYPE = TestCondition.class.getName();
  private static final String CONDITION_TYPE2 = TestConditionRecursive.class.getName();
  
  protected static Bundle testBundle = null;
  public    static Vector satisfOrder = new Vector();
  private   static boolean varmutable;
  
  protected boolean postponed;
  protected boolean satisfied;
  protected boolean mutable;
  protected String  name;
  protected String  info;
  
  public TestCondition(Bundle bundle, ConditionInfo info) {
    if (!CONDITION_TYPE.equals(info.getType()) && !CONDITION_TYPE2.equals(info.getType()))
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
  
  public static Condition getCondition(Bundle bundle, ConditionInfo info) {
    if (testBundle == bundle) {
      return new TestCondition(bundle, info);
    }
    return Condition.FALSE;
  }
  
  public static void setTestBundleLocation(Bundle bundle) {
    testBundle = bundle;
  }
  
	public boolean isPostponed() {
		return postponed;
	}

	public boolean isSatisfied() {
//    System.out.println("#isSatisfied method checked of " + info);
    if (varmutable) mutable = false;
    satisfOrder.addElement(name);
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
  
	public boolean isSatisfied(Condition[] conds, Dictionary context) {
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
    String[] result = new String[satisfOrder.size()];
    satisfOrder.copyInto(result);
    satisfOrder.removeAllElements();
    return result;
  }	
}

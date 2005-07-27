
package org.osgi.test.cases.permissionadmin.conditional.testcond;

import java.util.Dictionary;
import java.util.Vector;

import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.framework.Bundle;


public class TestCondition implements Condition {
  private static final String CONDITION_TYPE = TestCondition.class.getName();
	private static final String TEST_BUNDLE = "http://127.0.0.1/permissionadmin.conditional/tb1.jar";
  
  private static String testBundleLocation = TEST_BUNDLE;
  public  static Vector satisfOrder = new Vector();
  
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
  
  public static Condition getCondition(Bundle bundle, ConditionInfo info) {
    if (testBundleLocation.equals(bundle.getLocation())) {
      return new TestCondition(bundle, info);
    }
    return Condition.FALSE;
  }
  
  public static void setTestBundleLocation(String bundleLocation) {
    testBundleLocation = bundleLocation;
  }
  
	public boolean isPostponed() {
		return postponed;
	}

	public boolean isSatisfied() {
//    System.out.println("#isSatisfied method checked of " + info);
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
    this.info = buf.toString();
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


package org.osgi.test.cases.permissionadmin.conditional.testcond;

import java.security.Permission;
import java.util.Dictionary;

import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;

public class TestConditionRecursive extends TestCondition {
	private static Permission permission;

  public TestConditionRecursive(Bundle bundle, ConditionInfo info) {
    super(bundle, info);
  }

  public static Condition getCondition(Bundle bundle, ConditionInfo info) {
    if (testBundleLocation.equals(bundle.getLocation())) {
      return new TestConditionRecursive(bundle, info);
    }
    return Condition.FALSE;
  }
  
  public static void setPermission(Permission perm) {
    permission = perm;
  }
  
  public boolean isSatisfied() {
//    System.out.println("#isSatisfied method checked of " + info);
    satisfOrder.addElement(name);
    SecurityManager security = System.getSecurityManager();
    security.checkPermission(permission);
		return satisfied;
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

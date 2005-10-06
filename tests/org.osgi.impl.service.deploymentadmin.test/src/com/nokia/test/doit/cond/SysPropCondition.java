package com.nokia.test.doit.cond;

import java.util.Dictionary;

import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;

public final class SysPropCondition implements Condition {

    public static final String PROP_NAME = 
        "org.osgi.impl.service.deploymentadmin.test.SysPropCondition";
    
    private static SysPropCondition theCondition;

    public static Condition getCondition(Bundle bundle,ConditionInfo conditionInfo) {
        if (null == theCondition)
            theCondition = new SysPropCondition(bundle, conditionInfo);
        return theCondition;
    }
    
    public SysPropCondition(Bundle bundle,ConditionInfo conditionInfo) {
        System.out.println(">>> SysPropCondition <<<");
    }

    public boolean isPostponed() {
        return false;
    }

    public synchronized boolean isSatisfied() {
        try {
            String s = System.getProperty(PROP_NAME);
            Boolean b = Boolean.valueOf(s);
            return b.booleanValue();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isMutable() {
        return true;
    }

    public synchronized boolean isSatisfied(Condition[] conds, Dictionary context) {
        for (int i = 0; i < conds.length; i++) {
            if (!conds[i].isSatisfied())
                return false;
        }
        return true;
    }

}
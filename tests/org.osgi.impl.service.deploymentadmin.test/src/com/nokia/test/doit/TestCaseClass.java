package com.nokia.test.doit;

import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;

import com.nokia.test.db.Db;

public abstract class TestCaseClass {
    
    public abstract void doTest(Db db, DeploymentAdmin da, String dpHome) throws Exception;
    public abstract PermissionInfo[] getNeededPermissions();
    public abstract String getDescription();
    public abstract String[] getAsserts();

}

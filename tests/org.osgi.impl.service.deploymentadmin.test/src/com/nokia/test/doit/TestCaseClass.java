package com.nokia.test.doit;

import java.io.File;

import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;

import com.nokia.test.db.Db;

public abstract class TestCaseClass {
    
    private File dpHomeDir;
    
    protected TestCaseClass(String dpHome) throws Exception {
        dpHomeDir = new File(dpHome).getCanonicalFile(); 
    }
    
    protected File getFile(String file) {
        return new File(dpHomeDir, file);
    }
    
    public abstract void doTest(Db db, DeploymentAdmin da) throws Exception;
    public abstract PermissionInfo[] getNeededPermissions();
    public abstract String getDescription();
    public abstract String[] getAsserts();

}

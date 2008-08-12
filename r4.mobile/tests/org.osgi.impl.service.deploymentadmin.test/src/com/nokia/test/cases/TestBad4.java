package com.nokia.test.cases;

import java.io.FileInputStream;
import java.io.FilePermission;
import java.io.InputStream;

import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.permissionadmin.PermissionInfo;

import com.nokia.test.db.Db;
import com.nokia.test.doit.TestCaseClass;
import com.nokia.test.doit.TestCaseException;

public class TestBad4 extends TestCaseClass {

    protected TestBad4(TestRunnerImpl tRunner) throws Exception {
        super(tRunner);
    }

    public void doTest(Db db, DeploymentAdmin da) throws Exception {
        InputStream is = null;
        DeploymentPackage dp = null;
        try {
            is = new FileInputStream(tRunner.getFile("bad_db_test_04.dp"));
            try {
                dp = da.installDeploymentPackage(is);
            } catch (DeploymentException e) {
                return;
            }
            throw new TestCaseException("Negative test failed");
        } finally {
            if (null != dp)
                dp.uninstall();
            if (null != is)
                is.close();
        }
    }

    public PermissionInfo[] getNeededPermissions() {
        return new PermissionInfo[] {
                new PermissionInfo(FilePermission.class.getName(), 
                        tRunner.getFile("bad_db_test_04.dp").getAbsolutePath(), "read"),
                new PermissionInfo(DeploymentAdminPermission.class.getName(), 
                        "(name=db_test_01)", "install, uninstall")
            };
    }

    public String getDescription() {
        return "";
    }
    
    public String[] getAsserts() {
        return new String[] {};
    }

}

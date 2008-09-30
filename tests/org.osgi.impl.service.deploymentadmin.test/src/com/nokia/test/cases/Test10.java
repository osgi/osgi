package com.nokia.test.cases;

import java.io.FileInputStream;
import java.io.FilePermission;
import java.io.InputStream;

import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.permissionadmin.PermissionInfo;

import com.nokia.test.db.Db;
import com.nokia.test.doit.TestCaseClass;

public class Test10 extends TestCaseClass {

    protected Test10(TestRunnerImpl tRunner) throws Exception {
        super(tRunner);
    }

    public void doTest(Db db, DeploymentAdmin da) throws Exception {
        InputStream is = null;
        DeploymentPackage dp = null;
        try {
            is = new FileInputStream(tRunner.getFile("db_test_10.dp"));
            dp = da.installDeploymentPackage(is);

            is = new FileInputStream(tRunner.getFile("db_test_10_update_01.dp"));
            dp = da.installDeploymentPackage(is);
        } finally {
            if (null != dp)
            	// because of the missing RP
                dp.uninstallForced();
            if (null != is)
                is.close();
        }
    }

    public PermissionInfo[] getNeededPermissions() {
        return new PermissionInfo[] {
                new PermissionInfo(FilePermission.class.getName(), 
                        tRunner.getFile("db_test_10.dp").getAbsolutePath(), "read"),
                new PermissionInfo(FilePermission.class.getName(), 
                        tRunner.getFile("db_test_10_update_01.dp").getAbsolutePath(), "read"),
                new PermissionInfo(DeploymentAdminPermission.class.getName(), 
                        "(name=db_test_10)", "install, uninstall_forced")
            };
    }

    public String getDescription() {
        return "Installs a resource that has no pid";
    }
    
    public String[] getAsserts() {
        return new String[] {};
    }

}

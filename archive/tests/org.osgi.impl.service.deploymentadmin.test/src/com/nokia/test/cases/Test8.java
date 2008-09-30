package com.nokia.test.cases;

import java.io.FileInputStream;
import java.io.FilePermission;
import java.io.InputStream;

import org.osgi.service.deploymentadmin.BundleInfo;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.permissionadmin.PermissionInfo;

import com.nokia.test.db.Db;
import com.nokia.test.doit.TestCaseClass;
import com.nokia.test.doit.TestCaseException;

public class Test8 extends TestCaseClass {

    protected Test8(TestRunnerImpl tRunner) throws Exception {
        super(tRunner);
    }

    public void doTest(Db db, DeploymentAdmin da) throws Exception {
        InputStream is = null;
        DeploymentPackage dp = null;
        try {
            is = new FileInputStream(tRunner.getFile("db_test_08.dp"));
            dp = da.installDeploymentPackage(is);
            
            BundleInfo[] bis = dp.getBundleInfos();
            if (!bis[0].getSymbolicName().equals("no_bsn"))
            	throw new TestCaseException("Expected BSN 'no_bsn' but got '" + 
            			bis[0].getSymbolicName() + "'.");
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
                        tRunner.getFile("db_test_08.dp").getAbsolutePath(), "read"),
                new PermissionInfo(DeploymentAdminPermission.class.getName(), 
                        "(name=db_test_08)", "install, uninstall, metadata"),
            };
    }

    public String getDescription() {
        return "If the bundle doesn't have BSN the the BSN in the \n" +
            "DP manifest is used.";
    }
    
    public String[] getAsserts() {
        return new String[] {
        };
    }

}
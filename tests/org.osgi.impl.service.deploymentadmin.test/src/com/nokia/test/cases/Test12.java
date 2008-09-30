package com.nokia.test.cases;

import java.io.FileInputStream;
import java.io.FilePermission;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.permissionadmin.PermissionInfo;

import com.nokia.test.db.Db;
import com.nokia.test.doit.TestCaseClass;
import com.nokia.test.doit.TestCaseException;

public class Test12 extends TestCaseClass {

    protected Test12(TestRunnerImpl tRunner) throws Exception {
        super(tRunner);
    }

    public void doTest(Db db, DeploymentAdmin da) throws Exception {
        InputStream is1 = null;
        InputStream is2 = null;
        DeploymentPackage dp = null;
        try {
            is1 = new FileInputStream(tRunner.getFile("db_test_12.dp"));
            dp = da.installDeploymentPackage(is1);
            
            Set s1 = new HashSet(Arrays.asList(dp.getResources()));

            is2 = new FileInputStream(tRunner.getFile("db_test_12_update_01.dp"));
            dp = da.installDeploymentPackage(is2);

            Set s2 = new HashSet(Arrays.asList(dp.getResources()));
            
            if (s1.contains("simple_resource.xml") || !s2.contains("simple_resource.xml"))
            	throw new TestCaseException("Failed");
        } finally {
            if (null != dp)
                dp.uninstallForced();
            if (null != is1)
                is1.close();
            if (null != is2)
            	is2.close();
        }
    }

    public PermissionInfo[] getNeededPermissions() {
        return new PermissionInfo[] {
                new PermissionInfo(FilePermission.class.getName(), 
                        tRunner.getFile("db_test_12.dp").getAbsolutePath(), "read"),
                new PermissionInfo(FilePermission.class.getName(), 
                        tRunner.getFile("db_test_12_update_01.dp").getAbsolutePath(), "read"),
                new PermissionInfo(DeploymentAdminPermission.class.getName(), 
                        "(name=db_test_12)", "install, uninstall_forced, metadata")
            };
    }

    public String getDescription() {
    	return "DeploymentPackage-Missing: false EQUALS WITH the lack of the header";
    }
    
    public String[] getAsserts() {
        return new String[] {
            "the resource in the update is not missing SO it has to be installed"
        };
    }

}

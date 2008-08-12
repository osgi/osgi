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

public class Test11 extends TestCaseClass {

    protected Test11(TestRunnerImpl tRunner) throws Exception {
        super(tRunner);
    }

    public void doTest(Db db, DeploymentAdmin da) throws Exception {
        InputStream is1 = null;
        InputStream is2 = null;
        DeploymentPackage dp = null;
        try {
          is1 = new FileInputStream(tRunner.getFile("db_test_11.dp"));
          dp = da.installDeploymentPackage(is1);
          
          BundleInfo[] bis = dp.getBundleInfos();
          if (bis.length != 2)
              throw new TestCaseException("Test failed");
  
          is2 = new FileInputStream(tRunner.getFile("db_test_11_update_01.dp"));
          dp = da.installDeploymentPackage(is2);
          
          bis = dp.getBundleInfos();
          if (bis.length != 3)
              throw new TestCaseException("Test failed");
        } finally {
            if (null != dp)
                dp.uninstall();
            if (null != is1)
                is1.close();
            if (null != is2)
            	is2.close();
        }
    }

    public PermissionInfo[] getNeededPermissions() {
        return new PermissionInfo[] {
                new PermissionInfo(FilePermission.class.getName(), 
                        tRunner.getFile("db_test_11.dp").getAbsolutePath(), "read"),
                new PermissionInfo(FilePermission.class.getName(), 
                        tRunner.getFile("db_test_11_update_01.dp").getAbsolutePath(), "read"),
                new PermissionInfo(DeploymentAdminPermission.class.getName(), 
                        "(name=db_test_11)", "install, uninstall, metadata")
            };
    }

    public String getDescription() {
    	return "Update adds a new bundle";
    }
    
    public String[] getAsserts() {
        return new String[] {
        	"new element in getBundleInfos() result"
        };
    }

}

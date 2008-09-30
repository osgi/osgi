package com.nokia.test.cases;

import java.io.FileInputStream;
import java.io.FilePermission;
import java.io.InputStream;

import org.osgi.framework.Version;
import org.osgi.service.deploymentadmin.BundleInfo;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.permissionadmin.PermissionInfo;

import com.nokia.test.db.Db;
import com.nokia.test.doit.TestCaseClass;
import com.nokia.test.doit.TestCaseException;

public class Test9 extends TestCaseClass {

    protected Test9(TestRunnerImpl tRunner) throws Exception {
        super(tRunner);
    }

    public void doTest(Db db, DeploymentAdmin da) throws Exception {
        InputStream is1 = null;
        InputStream is2 = null;
        DeploymentPackage dp = null;
        try {
              is1 = new FileInputStream(tRunner.getFile("db_test_09.dp"));
              da.installDeploymentPackage(is1);
              
              dp = da.getDeploymentPackage("db_test_09");
              BundleInfo[] bis = dp.getBundleInfos();
              boolean b1 = bis[0].getVersion().equals(new Version("1.0.0"));
      
              is2 = new FileInputStream(tRunner.getFile("db_test_09_update_01.dp"));
              da.installDeploymentPackage(is2);
      
              dp = da.getDeploymentPackage("db_test_09");
              bis = dp.getBundleInfos();
              boolean b2 = bis[0].getVersion().equals(new Version("1.5.0"));
      		
              if (!(b1 && b2))
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
                        tRunner.getFile("db_test_09.dp").getAbsolutePath(), "read"),
                new PermissionInfo(FilePermission.class.getName(), 
                		tRunner.getFile("db_test_09_update_01.dp").getAbsolutePath(), "read"),
                new PermissionInfo(DeploymentAdminPermission.class.getName(), 
                        "(name=db_test_09)", "install, uninstall, list"),
                new PermissionInfo(DeploymentAdminPermission.class.getName(), 
                		"(name=db_test_09)", "install, uninstall, list, metadata")
            };
    }

    public String getDescription() {
        return "Get bundle infos. " +
        	   "Updates bundles and version has to be changed in " +
        	   "getBundleInfos() result";
    }
    
    public String[] getAsserts() {
        return new String[] {
        	"bundle version changes from 1.0.0 to 1.5.0"
        };
    }

}

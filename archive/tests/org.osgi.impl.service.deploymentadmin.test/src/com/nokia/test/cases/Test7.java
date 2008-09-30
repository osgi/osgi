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
import com.nokia.test.doit.TestCaseException;

public class Test7 extends TestCaseClass {

    protected Test7(TestRunnerImpl tRunner) throws Exception {
        super(tRunner);
    }

    public void doTest(Db db, DeploymentAdmin da) throws Exception {
        InputStream is1 = null;
        InputStream is2 = null;
        DeploymentPackage dp1 = null;
        DeploymentPackage dp2 = null;
        try {
        	is1 = new FileInputStream(tRunner.getFile("db_test_01.dp"));
        	dp1 = da.installDeploymentPackage(is1);
        	
        	is2 = new FileInputStream(tRunner.getFile("db_test_07.dp"));
        	dp2 = da.installDeploymentPackage(is2);
            
    		DeploymentPackage[] dps = da.listDeploymentPackages();
    		boolean b1 = false;
    		boolean b2 = false;
    		boolean b3 = false;
    		for (int i = 0; i < dps.length; i++) {
                if (dps[i].getName().equals("db_test_01"))
                    b1 = true;
                if (dps[i].getName().equals("db_test_07"))
                	b2 = true;
                if (dps[i].getName().equalsIgnoreCase("system"))
                    b3 = true;
            }
    		if (!b1)
                throw new TestCaseException("'db_test_01' DP should be visble");
    		if (b2)
    			throw new TestCaseException("'db_test_07' DP should NOT be visble");
    		if (b3)
                throw new TestCaseException("'System' DP should NOT be visble");
        } finally {
            if (null != dp1)
                dp1.uninstall();
            if (null != dp2)
            	dp2.uninstall();
            if (null != is1)
                is1.close();
            if (null != is2)
            	is2.close();
        }
    }

    public PermissionInfo[] getNeededPermissions() {
        return new PermissionInfo[] {
                new PermissionInfo(FilePermission.class.getName(), 
                        tRunner.getFile("db_test_01.dp").getAbsolutePath(), "read"),
                new PermissionInfo(DeploymentAdminPermission.class.getName(), 
                        "(name=db_test_01)", "install, uninstall, list"),
                new PermissionInfo(FilePermission.class.getName(), 
                		tRunner.getFile("db_test_07.dp").getAbsolutePath(), "read"),
        		new PermissionInfo(DeploymentAdminPermission.class.getName(), 
        				"(name=db_test_07)", "install, uninstall")
            };
    }

    public String getDescription() {
        return "Lists deployment packages. Shows only those DPs it has permissions to.";
    }
    
    public String[] getAsserts() {
        return new String[] {
        		"'System' DP should NOT be visble",
        	    "'db_test_01' DP should be visble"};
    }

}

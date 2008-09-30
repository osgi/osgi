package com.nokia.test.cases;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.AllPermission;
import java.util.Arrays;

import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.spi.ResourceProcessor;
import org.osgi.service.permissionadmin.PermissionInfo;

import com.nokia.test.db.Db;
import com.nokia.test.doit.TestCaseClass;
import com.nokia.test.doit.TestCaseException;
import com.nokia.test.exampleresourceprocessor.db.api.DbRpTest;

public class Test6 extends TestCaseClass {

    protected Test6(TestRunnerImpl tRunner) throws Exception {
        super(tRunner);
    }

    public void doTest(Db db, DeploymentAdmin da) throws Exception {
        InputStream is = null;
        DeploymentPackage dp = null;
        try {
        	is = new FileInputStream(tRunner.getFile("db_test_06.dp"));
    		dp = da.installDeploymentPackage(is);
    		
            ResourceProcessor rp = tRunner.getRp("db_test_06");
            
            File f = ((DbRpTest) rp).getBundlePrivateArea();
            if (null == f)
                throw new TestCaseException("Private area error: null returned");
            if (!f.exists())
                throw new TestCaseException("Private area error: does not exist");
            File[] fs = f.listFiles();
            System.out.println(Arrays.asList(fs));
        } finally {
            if (null != dp)
                dp.uninstall();
            if (null != is)
                is.close();
        }
    }

    public PermissionInfo[] getNeededPermissions() {
        return new PermissionInfo[] {
        		new PermissionInfo(AllPermission.class.getName(), "*", "*")
//                new PermissionInfo(FilePermission.class.getName(), 
//                        tRunner.getFile("db_test_06.dp").getAbsolutePath(), "read"),
//                new PermissionInfo(DeploymentAdminPermission.class.getName(), 
//                        "(name=db_test_06)", "install, uninstall, metadata")
            };
    }

    public String getDescription() {
        return "Private area";
    }
    
    public String[] getAsserts() {
        return new String[] {};
    }

}

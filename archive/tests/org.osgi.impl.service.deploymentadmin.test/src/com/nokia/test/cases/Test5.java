package com.nokia.test.cases;

import java.io.FileInputStream;
import java.io.FilePermission;
import java.io.InputStream;
import java.util.Arrays;

import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.permissionadmin.PermissionInfo;

import com.nokia.test.db.Db;
import com.nokia.test.doit.TestCaseClass;
import com.nokia.test.doit.TestCaseException;
import com.nokia.test.doit.TestRunner;

public class Test5 extends TestCaseClass {

    protected Test5(TestRunner tRunner) throws Exception {
        super(tRunner);
    }

    public void doTest(Db db, DeploymentAdmin da) throws Exception {
        InputStream is = null;
        DeploymentPackage dp = null;
        try {
            is = new FileInputStream(tRunner.getFile("db_test_05.dp"));
            dp = da.installDeploymentPackage(is);
            
            if (-1 == Arrays.asList(db.tableNames(null)).indexOf("player"))
                throw new TestCaseException("Table 'player' is missing");
            if (-1 == Arrays.asList(db.tableNames(null)).indexOf("game"))
                throw new TestCaseException("Table 'game' is missing");
            if (-1 == Arrays.asList(db.tableNames(null)).indexOf("score"))
                throw new TestCaseException("Table 'score' is missing");
            if (-1 == Arrays.asList(db.tableNames(null)).indexOf("tmp"))
                throw new TestCaseException("Table 'tmp' is missing");
            if (-1 == Arrays.asList(db.tableNames(null)).indexOf("tmp2"))
                throw new TestCaseException("Table 'tmp2' is missing");
        }
        finally {
            if (null != dp)
                dp.uninstall();
            if (null != is)
                is.close();
        }
    }

    public PermissionInfo[] getNeededPermissions() {
        return new PermissionInfo[] {
                new PermissionInfo(FilePermission.class.getName(), 
                        tRunner.getFile("db_test_05.dp").getAbsolutePath(), "read"),
                new PermissionInfo(DeploymentAdminPermission.class.getName(), 
                        "(name=db_test_05)", "install, uninstall")
            };
    }

    public String getDescription() {
        return "Same as Test3 but the DP is signed";
    }

    public String[] getAsserts() {
        return new String[] {};
    }

}

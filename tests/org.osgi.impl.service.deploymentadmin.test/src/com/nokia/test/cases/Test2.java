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

public class Test2 extends TestCaseClass {

    protected Test2(String dpHome) throws Exception {
        super(dpHome);
    }

    public void doTest(Db db, DeploymentAdmin da) throws Exception {
        InputStream is = new FileInputStream(getFile("db_test_02.dp"));
        DeploymentPackage dp = da.installDeploymentPackage(is);
        
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("player"))
            throw new Exception("Table 'player' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("game"))
            throw new Exception("Table 'game' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("score"))
            throw new Exception("Table 'score' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("tmp"))
            throw new Exception("Table 'tmp' is missing");

        is = new FileInputStream(getFile("db_test_02_update_01.dp"));
        dp = da.installDeploymentPackage(is);
        
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("player"))
            throw new Exception("Table 'player' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("game"))
            throw new Exception("Table 'game' is missing");
        if (-1 != Arrays.asList(db.tableNames(null)).indexOf("score"))
            throw new Exception("Table 'score' is missing");
        if (-1 == Arrays.asList(db.tableNames(null)).indexOf("tmp"))
            throw new Exception("Table 'tmp' is missing");
        
        dp.uninstall();
        db.reset(null);
    }

    public PermissionInfo[] getNeededPermissions() {
        return new PermissionInfo[] {
                new PermissionInfo(FilePermission.class.getName(), 
                        getFile("db_test_02.dp").getAbsolutePath(), "read"),
                new PermissionInfo(FilePermission.class.getName(), 
                        getFile("db_test_02_update_01.dp").getAbsolutePath(), "read"),
                new PermissionInfo(DeploymentAdminPermission.class.getName(), 
                        "(name=db_test_02)", "install, uninstall")
            };
    }

    public String getDescription() {
        return "Uses default RP, two resource files (one of them " +
        "is updated the other is a Missing resource)";
    }

    public String[] getAsserts() {
        return new String[] {"table of the missing resource mustn't disappera after uninstall"};
    }

}

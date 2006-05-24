package com.nokia.test.cases;

import java.io.FileInputStream;
import java.io.FilePermission;
import java.io.InputStream;
import java.util.Arrays;

import org.osgi.framework.Version;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.permissionadmin.PermissionInfo;

import com.nokia.test.db.Db;
import com.nokia.test.doit.TestCaseClass;
import com.nokia.test.doit.TestCaseException;

public class Test1 extends TestCaseClass {

    protected Test1(TestRunnerImpl tRunner) throws Exception {
        super(tRunner);
    }

    public void doTest(Db db, DeploymentAdmin da) throws Exception {
        InputStream is = null;
        DeploymentPackage dp = null;
        try {
            is = new FileInputStream(tRunner.getFile("db_test_01.dp"));
            dp = da.installDeploymentPackage(is);

            if (-1 == Arrays.asList(db.tableNames(null)).indexOf("player"))
                throw new TestCaseException("Table 'player' is missing");
            if (-1 == Arrays.asList(db.tableNames(null)).indexOf("game"))
                throw new TestCaseException("Table 'game' is missing");
            if (-1 == Arrays.asList(db.tableNames(null)).indexOf("score"))
                throw new TestCaseException("Table 'score' is missing");
            if (-1 == Arrays.asList(db.tableNames(null)).indexOf("tmp"))
                throw new TestCaseException("Table 'tmp' is missing");
            if (null == db.findRow(null, "player", new Integer(1)))
                throw new TestCaseException(
                        "Row with '1' primary key is missing");
            if (null == db.findRow(null, "game", new Integer(1)))
                throw new TestCaseException(
                        "Row with '1' primary key is missing");
            if (null == db.findRow(null, "score", new Integer(1)))
                throw new TestCaseException(
                        "Row with '1' primary key is missing");
            if (null == db.findRow(null, "tmp", new Integer(1)))
                throw new TestCaseException(
                        "Row with '1' primary key is missing");
            if (!dp.getVersion().equals(new Version("1.0")))
                throw new TestCaseException("Version should be 1.0");
            if (!dp.getHeader("Other-Main-header").equals("1"))
                throw new TestCaseException(
                        "Header value (Other-Main-header) should be 1");
            if (!dp.getResourceHeader("db_test_01.dbscript", "Other-header")
                    .equals("1"))
                throw new TestCaseException(
                        "Header value (Other-header) should be 1");
    
            is = new FileInputStream(tRunner.getFile("db_test_01_update_01.dp"));
            dp = da.installDeploymentPackage(is);
    
            if (-1 == Arrays.asList(db.tableNames(null)).indexOf("player"))
                throw new TestCaseException("Table 'player' is missing");
            if (-1 == Arrays.asList(db.tableNames(null)).indexOf("game"))
                throw new TestCaseException("Table 'game' is missing");
            if (-1 != Arrays.asList(db.tableNames(null)).indexOf("score"))
                throw new TestCaseException("Table 'score' is present");
            if (-1 != Arrays.asList(db.tableNames(null)).indexOf("tmp"))
                throw new TestCaseException("Table 'tmp' is present");
            if (!db.findRow(null, "player", new Integer(1))[1]
                    .equals("Joe_Upd"))
                throw new TestCaseException(
                        "Row with '1' primary key is not updated");
            if (!db.findRow(null, "game", new Integer(1))[1]
                    .equals("chess_Upd"))
                throw new TestCaseException(
                        "Row with '1' primary key is not updated");
            if (!dp.getVersion().equals(new Version("2.0")))
                throw new TestCaseException("Version should be 2.0");
            if (!dp.getHeader("Other-Main-header").equals("2"))
                throw new TestCaseException(
                        "Header value (Other-Main-header) should be 2");
            if (!dp.getResourceHeader("db_test_01.dbscript", "Other-header")
                    .equals("2"))
                throw new TestCaseException(
                        "Header value (Other-header) shoul dbe 2");
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
                        tRunner.getFile("db_test_01.dp").getAbsolutePath(), "read"),
                new PermissionInfo(FilePermission.class.getName(), 
                        tRunner.getFile("db_test_01_update_01.dp").getAbsolutePath(), "read"),
                new PermissionInfo(DeploymentAdminPermission.class.getName(), 
                        "(name=db_test_01)", "install, uninstall, metadata")
            };
    }

    public String getDescription() {
        return "Uses default RP, two resource files (one of them " + 
            "is updated the other removed)";
    }
    
    public String[] getAsserts() {
        return new String[] {
            "tables exist and certains disappear after update",
            "rows in tables exist",
            "DP version changes",
            "main header changes",
            "resource header changes"};
    }

}

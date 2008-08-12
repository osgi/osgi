package com.nokia.test.cases;

import java.io.FileInputStream;
import java.io.FilePermission;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Set;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.spi.ResourceProcessor;
import org.osgi.service.permissionadmin.PermissionInfo;

import com.nokia.test.db.Db;
import com.nokia.test.doit.TestCaseClass;
import com.nokia.test.doit.TestCaseException;
import com.nokia.test.exampleresourceprocessor.db.api.DbRpTest;

public class Test3 extends TestCaseClass {

    protected Test3(TestRunnerImpl tRunner) throws Exception {
        super(tRunner);
    }

    public void doTest(Db db, DeploymentAdmin da) throws Exception {
        InputStream is = null;
        DeploymentPackage dp = null;
        try {
            is = new FileInputStream(tRunner.getFile("db_test_03.dp"));
            dp = da.installDeploymentPackage(is);
            
            if (-1 == Arrays.asList(db.tableNames(null)).indexOf("player"))
                throw new TestCaseException("Table 'player' is missing");
            if (-1 == Arrays.asList(db.tableNames(null)).indexOf("game"))
                throw new TestCaseException("Table 'game' is missing");
            if (-1 == Arrays.asList(db.tableNames(null)).indexOf("score"))
                throw new TestCaseException("Table 'score' is missing");
            if (-1 == Arrays.asList(db.tableNames(null)).indexOf("tmp"))
                throw new TestCaseException("Table 'tmp' is missing");
            
            
            ResourceProcessor rp = tRunner.getRp("db_test_03");
            Set s = ((DbRpTest) rp).getResources(dp, "db_test_01_t.dbscript");
            if (null == s || !s.contains("tmp"))
                throw new TestCaseException("RP with id 'db_test_03' HASN'T receive the " +
                        "'db_test_01_t.dbscript' resource");
            ResourceProcessor rp_def = tRunner.getRp("default_pid");
            s = ((DbRpTest) rp_def).getResources(dp, "db_test_01_t.dbscript");
            if (null != s && s.contains("tmp"))
                throw new TestCaseException("RP with id 'default_id' HAS receive the " +
                        "'db_test_01_t.dbscript' resource");
            
            is = new FileInputStream(tRunner.getFile("db_test_03_update_01.dp"));
            dp = da.installDeploymentPackage(is);
            
            Bundle[] bs = tRunner.getBundles();
            Bundle b_eg = null;
            Bundle b_hg = null;
            for (int i = 0; i < bs.length; i++) {
                String sn = bs[i].getSymbolicName();
                if (null == sn)
                    continue;
                if (sn.equals("easygame"))
                    b_eg = bs[i];
                if (sn.equals("hardgame"))
                    b_hg = bs[i];
            }
            if (null == b_eg)
                throw new TestCaseException("Test Failed");
            if (null != b_hg)
                throw new TestCaseException("Test Failed");
            
            String bv = (String) b_eg.getHeaders().get("Bundle-Version");
            if (null == bv)
                throw new TestCaseException("Test Failed");
            if ( !(new Version(bv).equals(new Version(2, 0, 0))) )
                throw new TestCaseException("Test Failed");
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
                        tRunner.getFile("db_test_03.dp").getAbsolutePath(), "read"),
                new PermissionInfo(FilePermission.class.getName(), 
                        tRunner.getFile("db_test_03_update_01.dp").getAbsolutePath(), "read"),
                new PermissionInfo(DeploymentAdminPermission.class.getName(), 
                        "(name=db_test_03)", "install, uninstall"),
                new PermissionInfo(AdminPermission.class.getName(), "*", "metadata")
            };
    }

    public String getDescription() {
        return "Uses customizer, two resource files (one processed by the " +
            "preinstalled RP, one by the customizer). Two bundles " +
            "(one of them is updated the other removed).";
    }
    
    public String[] getAsserts() {
        return new String[] {
                "tables exist and certains disappear after update",
                "preinstalled RP gets the first res. file",
                "customizer gets the other",
                "'hardgame' bundle has to disappear after update",
                "'easygame' bundle has to remain after update",
                "'easygame' version changes"
        };
    }

}
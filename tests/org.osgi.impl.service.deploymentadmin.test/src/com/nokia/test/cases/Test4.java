package com.nokia.test.cases;

import java.io.FileInputStream;
import java.io.FilePermission;
import java.io.InputStream;

import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.permissionadmin.PermissionInfo;

import com.nokia.test.db.Db;
import com.nokia.test.doit.TestCaseClass;
import com.nokia.test.doit.TestCaseException;
import com.nokia.test.doit.TestRunner;

public class Test4 extends TestCaseClass {

    protected Test4(TestRunner tRunner) throws Exception {
        super(tRunner);
    }

    public void doTest(Db db, final DeploymentAdmin da) throws Exception {
        InputStream is = null;
        DeploymentPackage dp = null;
        try {
            // creates a thread that calls the cancel() methods
            Thread cancelThread = new Thread(new Runnable() {
                public void run() {
                    try {Thread.sleep(3000);} catch (Exception e) {}
                    da.cancel();                
                }
            });
            cancelThread.start();
            
            is = new FileInputStream(tRunner.getFile("db_test_04.dp"));
            
            DeploymentException de = null;
            try {
            	dp = da.installDeploymentPackage(is);
            } catch (DeploymentException e) {
            	if (DeploymentException.CODE_CANCELLED != e.getCode())
            		throw new TestCaseException("Bad exception code");
            	de = e;
			}
            
            DeploymentPackage[] dps = da.listDeploymentPackages();
            for (int i = 0; i < dps.length; i++) {
                if (dps[i].getName().equals("db_test_04"))
                    throw new TestCaseException("Operation has not been cancelled");
            }

            if (null == de)
            	throw new TestCaseException("Operation has not been cancelled");
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
                        tRunner.getFile("db_test_04.dp").getAbsolutePath(), "read"),
                new PermissionInfo(DeploymentAdminPermission.class.getName(), 
                        "(name=db_test_04)", "install, uninstall, list, cancel")
            };
    }

    public String getDescription() {
        return "Tests cancelling an install operation";
    }

    public String[] getAsserts() {
        return new String[] {};
    }

}

package com.nokia.test.cases;

import java.io.FileInputStream;
import java.io.FilePermission;
import java.io.InputStream;

import org.osgi.framework.Version;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.permissionadmin.PermissionInfo;

import com.nokia.test.db.Db;
import com.nokia.test.doit.TestCaseClass;
import com.nokia.test.doit.TestCaseException;
import com.nokia.test.doit.TestRunner;

public class Test17 extends TestCaseClass {

    protected Test17(TestRunner tRunner) throws Exception {
        super(tRunner);
    }

    public void doTest(Db db, final DeploymentAdmin da) throws Exception {
        InputStream         is1 = null;
        final InputStream[] is2 = new InputStream[1];
        DeploymentPackage   dp  = null;
        try {
        	is1 = new FileInputStream(tRunner.getFile("db_test_17.dp"));
        	dp = da.installDeploymentPackage(is1);
        	
            // creates a thread that updates the old one
            Thread updateThread = new Thread(new Runnable() {
                public void run() {
                    try {Thread.sleep(3000);} catch (Exception e) {}
                    try {
                    	is2[0] = new FileInputStream(tRunner.getFile("db_test_17_update_01.dp"));
						da.installDeploymentPackage(is2[0]);
						synchronized (Test17.this){
							Test17.this.notify();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}                
                }
            });
            updateThread.start();

            if (!dp.getVersion().equals(new Version("1.0")))
            	throw new TestCaseException("Wrong version");
            if (dp.getResources().length != 1)
            	throw new TestCaseException("Wrong resources array");
            
            try {Thread.sleep(6000);} catch (Exception e) {}

            if (!dp.getVersion().equals(new Version("1.0")))
            	throw new TestCaseException("Wrong version");
            if (dp.getResources().length != 1)
            	throw new TestCaseException("Wrong resources array");
            
            synchronized (this) {
            	wait();
            }
            
            if (!dp.getVersion().equals(new Version("2.0")))
            	throw new TestCaseException("Wrong version");
            if (dp.getResources().length != 5)
            	throw new TestCaseException("Wrong resources array");
        }
        finally {
            if (null != dp)
                dp.uninstall();
            if (null != is1)
                is1.close();
            if (null != is2)
            	is2[0].close();
        }
    }

    public PermissionInfo[] getNeededPermissions() {
        return new PermissionInfo[] {
                new PermissionInfo(FilePermission.class.getName(), 
                        tRunner.getFile("db_test_17.dp").getAbsolutePath(), "read"),
                new PermissionInfo(FilePermission.class.getName(), 
                		tRunner.getFile("db_test_17_update_01.dp").getAbsolutePath(), "read"),
                new PermissionInfo(DeploymentAdminPermission.class.getName(), 
                        "(name=db_test_17)", "install, uninstall, list, metadata")
            };
    }

    public String getDescription() {
        return "";
    }

    public String[] getAsserts() {
        return new String[] {};
    }

}

package com.nokia.test.cases;

import java.io.FileInputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;

import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.permissionadmin.PermissionInfo;

import com.nokia.test.db.Db;
import com.nokia.test.doit.TestCaseClass;

public class Test14 extends TestCaseClass {

    protected Test14(TestRunnerImpl tRunner) throws Exception {
        super(tRunner);
    }

    public void doTest(Db db, final DeploymentAdmin da) throws Exception {
    	final DeploymentPackage[] dps = new DeploymentPackage[3];
    	final InputStream[] iss = new InputStream[3];
        try {
            final DeploymentException[] ex = new DeploymentException[2];
            
            // INSTALL
            
            // creates a thread that calls install
            Thread installThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        try {Thread.sleep(1000);} catch (Exception e) {}
                        iss[0] = new FileInputStream(tRunner.getFile("db_test_14_02.dp"));
                        dps[0] = da.installDeploymentPackage(iss[0]);
                    } catch (DeploymentException e) {
                        ex[0] = e;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            installThread.start();

            iss[1] = new FileInputStream(tRunner.getFile("db_test_14_01.dp"));
            dps[1] = da.installDeploymentPackage(iss[1]);

            if (null == ex[0] || ex[0].getCode() != DeploymentException.CODE_TIMEOUT)
                throw new Exception("Test failed");
            
            // UNINSTALL
            
            // creates a thread that calls uninstall
            Thread uninstallThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        try {Thread.sleep(1000);} catch (Exception e) {}
                        dps[1].uninstall();
                    } catch (DeploymentException e) {
                        ex[1] = e;
                    }
                }
            });
            uninstallThread.start();
            
            iss[2] = new FileInputStream(tRunner.getFile("db_test_14_02.dp"));
            dps[2] = da.installDeploymentPackage(iss[2]);
            
            if (null == ex[1] || ex[1].getCode() != DeploymentException.CODE_TIMEOUT)
                throw new Exception("Test failed");
        } finally {
            if (null != dps[0])
                dps[0].uninstall();
            if (null != dps[1])
            	dps[1].uninstall();
            if (null != dps[2])
            	dps[2].uninstall();
            if (null != iss[0])
            	iss[0].close();
            if (null != iss[1])
            	iss[1].close();
            if (null != iss[2])
            	iss[2].close();
        }
    }

    public PermissionInfo[] getNeededPermissions() {
        return new PermissionInfo[] {
                new PermissionInfo(FilePermission.class.getName(), 
                        tRunner.getFile("db_test_14.dp").getAbsolutePath(), "read"),
                new PermissionInfo(FilePermission.class.getName(), 
                        tRunner.getFile("db_test_14_01.dp").getAbsolutePath(), "read"),
                new PermissionInfo(FilePermission.class.getName(), 
                		tRunner.getFile("db_test_14_02.dp").getAbsolutePath(), "read"),
                new PermissionInfo(DeploymentAdminPermission.class.getName(), 
                        "(name=db_test_14)", "install, uninstall"),
                new PermissionInfo(DeploymentAdminPermission.class.getName(), 
                		"(name=db_test_14_01)", "install, uninstall"),
                new PermissionInfo(DeploymentAdminPermission.class.getName(), 
                		"(name=db_test_14_02)", "install, uninstall")
            };
    }

    public String getDescription() {
    	return "When a client requests a new session \n" +
    		"with an install or uninstall operation, it must block that call until \n" +
    		"the earlier session is completed. The Deployment Admin service must \n" +
    		"throw a DeploymentException when the session can not be created after \n" +
    		"an appropriate time out period.";
    }
    
    public String[] getAsserts() {
        return new String[] {
        };
    }

}

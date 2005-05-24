package com.nokia.test.unittest;

import org.osgi.service.deploymentadmin.DeploymentAdminPermission;

import junit.framework.TestCase;

public class DeploymentAdminPermissionTest extends TestCase {
    
    private DeploymentAdminPermission perm;
    
    public void setUp() throws Exception {
    }
    
    public void tearDown() throws Exception {
    }

    public void test_01() {
        perm = new DeploymentAdminPermission("(name=NAME)", "install");
        perm = new DeploymentAdminPermission("(signer=signer=-;CN=vCN,OU=vOU,O=vO,L=vL,C=vC)", 
                "install");
        assertTrue(true);
    }
    
    public void test_bad_01() {
        try {
            perm = new DeploymentAdminPermission("(error=NAME)", "install");
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            return;
        }
        assertTrue(false);
    }

    public void test_bad_02() {
        try {
            perm = new DeploymentAdminPermission("(signer=" +
            		"CN=vCN1,OU=vOU1,O=vO1,L=vL1,C=vC1;" +
            		"-;" +  // error
            		"CN=vCN1,OU=vOU1,O=vO1,L=vL1,C=vC1)", 
            		"install");
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            return;
        }
        assertTrue(false);
    }
    
}

package com.nokia.test.unittest;

import org.osgi.service.deploymentadmin.DeploymentAdminPermission;

import junit.framework.TestCase;

public class DeploymentAdminPermissionTest extends TestCase {
    
    private DeploymentAdminPermission perm;
    
    public void setUp() throws Exception {
    }
    
    public void tearDown() throws Exception {
    }

    public void test_constructor_01() {
        perm = new DeploymentAdminPermission("(name=NAME)", "install");
        perm = new DeploymentAdminPermission("(&(name=NAME1)(name=NAME2)(name=NAME3))", "install");
        perm = new DeploymentAdminPermission("(|(name=NAME1)(name=NAME2)(name=NAME3))", "install");
        perm = new DeploymentAdminPermission("(!(name=NAME))", "install");
        perm = new DeploymentAdminPermission("(signer=signer=-;CN=vCN,OU=vOU,O=vO,L=vL,C=vC)", 
                "install");
        perm = new DeploymentAdminPermission("(&(name=*)(signer=signer=-;" +
        		"CN=vCN,OU=vOU,O=vO,L=vL,C=vC))", "install");
        assertTrue(true);
    }
    
    public void test_implies_01() {
        perm = new DeploymentAdminPermission("(name=NAME)", "install");
        assertTrue(perm.implies(
                new DeploymentAdminPermission("(name=NAME)", "install")
            ));

        perm = new DeploymentAdminPermission("(name=NAME*)", "install");
        assertTrue(perm.implies(
                new DeploymentAdminPermission("(name=NAMExxxxx)", "install")
            ));

        perm = new DeploymentAdminPermission("(name=*NAME)", "install");
        assertTrue(perm.implies(
                new DeploymentAdminPermission("(name=xxxxxNAME)", "install")
            ));
    }

    public void test_implies_02() {
        perm = new DeploymentAdminPermission("(signer=" +
        			"CN=Root1,OU=FAKEDONTUSE,O=CASoft,L=Budapest,C=HU" +
        		")",
        	"install");
        assertTrue(perm.implies(
                new DeploymentAdminPermission("(name=NAME)", "install")
            ));
        
        perm = new DeploymentAdminPermission("(signer=" +
    				"*, o=ACME, c=*" +
    			")",
    		"install");
        assertTrue(perm.implies(
                new DeploymentAdminPermission("(signer=" +
                	"cn=One, o=Two, o=ACME, c=somethin" +
                ")", 
            "install")
            ));
    }
    
    public void test_implies_spec() {
        perm = new DeploymentAdminPermission("(signer=*, o=ACME, c=US)", "install");
        assertTrue(perm.implies(
                new DeploymentAdminPermission("(signer=cn = Bugs Bunny, o = ACME, c = US)", "install")
            ));
        assertTrue(perm.implies(
                new DeploymentAdminPermission("(signer=ou = Carots, cn=Daffy Duck, o=ACME, c=US)", "install")
            ));
        assertTrue(perm.implies(
                new DeploymentAdminPermission("(signer=street = 9C\\, Avenue St. Drézéry, o=ACME, c=US)", "install")
            ));
        assertTrue(perm.implies(
                new DeploymentAdminPermission("(signer=dc=www, dc=acme, dc=com, o=ACME, c=US)", "install")
            ));
        
        perm = new DeploymentAdminPermission("(signer=cn=*,o=ACME,c=*)", "install");
        assertTrue(perm.implies(
                new DeploymentAdminPermission("(signer=cn=Bugs Bunny,o=ACME,c=US)", "install")
            ));
        assertTrue(perm.implies(
                new DeploymentAdminPermission("(signer=cn = Daffy Duck , o = ACME , c = US)", "install")
            ));
        assertTrue(perm.implies(
                new DeploymentAdminPermission("(signer=cn=Road Runner, o=ACME, c=NL)", "install")
            ));
    }
    
    public void test_bad_implies_spec() {
        perm = new DeploymentAdminPermission("(signer=*, o=ACME, c=US)", "install");
        assertFalse(perm.implies(
                new DeploymentAdminPermission("(signer=street = 9C\\, Avenue St. Drézéry, o=ACME, c=FR)", "install")
            ));
        assertFalse(perm.implies(
                new DeploymentAdminPermission("(signer=dc=www, dc=acme, dc=com, c=US)", "install")
            ));
        //assertFalse(perm.implies(
        //        new DeploymentAdminPermission("(signer=o=ACME, c=US)", "install")
        //    ));
        
        perm = new DeploymentAdminPermission("(signer=cn=*,o=ACME,c=*)", "install");
        assertFalse(perm.implies(
                new DeploymentAdminPermission("(signer=o=ACME, c=NL)", "install")
            ));
        assertFalse(perm.implies(
                new DeploymentAdminPermission("(signer=dc=acme.com, cn=Bugs Bunny, o=ACME, c=US)", "install")
            ));
    }
    
    public void test_bad_implies_01() {
        perm = new DeploymentAdminPermission("(name=NAME)", "list");
        assertFalse(perm.implies(
                new DeploymentAdminPermission("(name=NAME)", "install")
            ));

        perm = new DeploymentAdminPermission("(name=NAME*)", "install");
        assertFalse(perm.implies(
                new DeploymentAdminPermission("(name=NAM_ERROR_xxxxx)", "install")
            ));
    }
    
    public void test_bad_constructor_01() {
        try {
            perm = new DeploymentAdminPermission("(error=NAME)", "install");
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            return;
        }
        assertTrue(false);
    }

    public void test_bad_constructor_02() {
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
    
    public void test_bad_constructor_03() {
        try {
            // ()() is bad
            perm = new DeploymentAdminPermission("(name=NAME)(signer=signer=" +
            		"-;CN=vCN,OU=vOU,O=vO,L=vL,C=vC)", 
                    "install");
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            return;
        }
        assertTrue(false);
    }

    public void test_bad_constructor_04() {
        try {
            // ()() is bad
            perm = new DeploymentAdminPermission("!(name=NAME)", "install");
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            return;
        }
        assertTrue(false);
    }

    public void test_bad_constructor_05() {
        try {
            // (!()()) is bad ('!' is unary operator)
            perm = new DeploymentAdminPermission("(!(name=NAME1)((name=NAME2)))", "install");
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            return;
        }
        assertTrue(false);
    }
    
    public void test_bad_constructor_06() {
        try {
            perm = new DeploymentAdminPermission("(error=NAME)", "install, error, list");
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            return;
        }
        assertTrue(false);
    }

}

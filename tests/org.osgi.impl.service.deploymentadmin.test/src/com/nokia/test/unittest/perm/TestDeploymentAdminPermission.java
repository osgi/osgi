package com.nokia.test.unittest.perm;

import org.osgi.impl.service.deploymentadmin.perm.DeploymentAdminPermission;

import junit.framework.TestCase;

public class TestDeploymentAdminPermission extends TestCase {

	public void testImplies() {
		assertTrue(new DeploymentAdminPermission("(name=apple)", "install").implies(
				new DeploymentAdminPermission("(name=apple)", "install")));
		
		assertTrue(new DeploymentAdminPermission("(name=*)", "install").implies(
				new DeploymentAdminPermission("(name=apple)", "install")));
    	
		assertTrue(new DeploymentAdminPermission("(name=app*)", "install").implies(
				new DeploymentAdminPermission("(name=apple)", "install")));
    	
		assertTrue(new DeploymentAdminPermission("(name=app*le)", "install").implies(
				new DeploymentAdminPermission("(name=apple)", "install")));
    	
		assertTrue(new DeploymentAdminPermission("(name=app?e)", "install").implies(
				new DeploymentAdminPermission("(name=apple)", "install")));
    	
    	assertTrue(new DeploymentAdminPermission("(name=app?e_tree_*_on_it)", "install").implies(
    			new DeploymentAdminPermission("(name=apple_tree_bird_on_it)", "install")));
    	
    	assertFalse(new DeploymentAdminPermission("(name=app?e_tree_*_on_it)", "install").implies(
    			new DeploymentAdminPermission("(name=apple_tree_fox_under_it)", "install")));
    	
    	assertFalse(new DeploymentAdminPermission("(name=apX*le)", "install").implies(
    			new DeploymentAdminPermission("(name=apple)", "install")));
    	
    	assertFalse(new DeploymentAdminPermission("(name=app*)", "install").implies(
    			new DeploymentAdminPermission("(name=apXle)", "install")));
    	
    	assertTrue(new DeploymentAdminPermission("(signer=*)", "install").implies(
    			new DeploymentAdminPermission("(&(signer=" + 
    			"CN=John Smith, O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US" +
    			")(name=apple))", "install")));
    	
		assertTrue(new DeploymentAdminPermission("(name=*)", "install").implies(
				new DeploymentAdminPermission("(&(signer=" + 
				"CN=John Smith, O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US" +
				")(name=*))", "install")));

		assertTrue(new DeploymentAdminPermission("(signer=CN=*, O=ACME Inc, " +
				"OU=ACME Cert Authority, L=Austin, ST=Texas, C=US)", "install").implies(
						new DeploymentAdminPermission("(&(signer=" + 
				"CN=John Smith, O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US" +
				")(name=*))", "install")));
		
		assertFalse(new DeploymentAdminPermission("(signer=CN=*Big, O=ACME Inc, " +
				"OU=ACME Cert Authority, L=Austin, ST=Texas, C=US)", "install").implies(
						new DeploymentAdminPermission("(&(signer=" + 
				"CN=John Smith, O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US" +
				")(name=*))", "install")));
		
		assertTrue(new DeploymentAdminPermission("(signer=-;CN=*, O=ACME Inc, " +
				"OU=ACME Cert Authority, L=Austin, ST=Texas, C=US)", "install").implies(
						new DeploymentAdminPermission(
				"(signer=CN=John Smith, O=ACME Inc, OU=ACME Cert Authority, L=Austin, " +
				"ST=Texas, C=US)", "install")));

		assertTrue(new DeploymentAdminPermission("(signer=-;CN=*, O=ACME Inc, " +
				"OU=ACME Cert Authority, L=Austin, ST=Texas, C=US)", "install").implies(
						new DeploymentAdminPermission("(signer=CN=First;CN=John Smith, " +
								"O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, " +
								"C=US)", "install")));
	
		assertTrue(new DeploymentAdminPermission("(signer=-;CN=*, O=ACME Inc, " +
				"OU=ACME Cert Authority, L=Austin, ST=Texas, C=US)", "install").implies(
						new DeploymentAdminPermission("(&(name=rp_resource_dp)(signer=-;" +
								"CN=*, O=ACME Inc, OU=ACME Cert Authority, L=Austin, " +
								"ST=Texas, C=US))", "install")));
		
		assertFalse(new DeploymentAdminPermission("(&(signer=CN=John Smith, " +
				"O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US)" +
				"(name=*))", "install").implies(
						new DeploymentAdminPermission("(signer=CN=John Big, O=ACME Inc, " +
								"OU=ACME Cert Authority, L=Austin, ST=Texas, C=US)", 
								"install")));
	}
	
}

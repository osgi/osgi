package com.nokia.test.unittest;

import org.osgi.impl.service.deploymentadmin.perm.DeploymentAdminPermission;

import junit.framework.TestCase;

public class TestDeploymentAdminPermission extends TestCase {

	public void testImplies001() {
		assertTrue(new DeploymentAdminPermission("(name=apple)", "install").implies(
				new DeploymentAdminPermission("(name=apple)", "install")));
	}
	
	public void testImplies002() {
		assertTrue(new DeploymentAdminPermission("(name=*)", "install").implies(
				new DeploymentAdminPermission("(name=apple)", "install")));
	}
	
	public void testImplies003() {    	
		assertTrue(new DeploymentAdminPermission("(name=app*)", "install").implies(
				new DeploymentAdminPermission("(name=apple)", "install")));
	}
	
	public void testImplies004() {
		assertTrue(new DeploymentAdminPermission("(name=app*le)", "install").implies(
				new DeploymentAdminPermission("(name=apple)", "install")));
	}
	
	public void testImplies005() {
		assertTrue(new DeploymentAdminPermission("(name=app?e)", "install").implies(
				new DeploymentAdminPermission("(name=apple)", "install")));
	}
	
	public void testImplies006() {
    	assertTrue(new DeploymentAdminPermission("(name=app?e_tree_*_on_it)", "install").implies(
    			new DeploymentAdminPermission("(name=apple_tree_bird_on_it)", "install")));
	}
	
	public void testImplies007() {
    	assertFalse(new DeploymentAdminPermission("(name=app?e_tree_*_on_it)", "install").implies(
    			new DeploymentAdminPermission("(name=apple_tree_fox_under_it)", "install")));
	}
	
	public void testImplies008() {
    	assertFalse(new DeploymentAdminPermission("(name=apX*le)", "install").implies(
    			new DeploymentAdminPermission("(name=apple)", "install")));
	}
	
	public void testImplies009() {
    	assertFalse(new DeploymentAdminPermission("(name=app*)", "install").implies(
    			new DeploymentAdminPermission("(name=apXle)", "install")));
	}
	
	public void testImplies010() {
    	assertTrue(new DeploymentAdminPermission("(signer=*)", "install").implies(
    			new DeploymentAdminPermission("(&(signer=" + 
    			"CN=John Smith, O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US" +
    			")(name=apple))", "install")));
	}
	
	public void testImplies011() {
		assertTrue(new DeploymentAdminPermission("(name=*)", "install").implies(
				new DeploymentAdminPermission("(&(signer=" + 
				"CN=John Smith, O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US" +
				")(name=*))", "install")));
	}
	
	public void testImplies012() {
		assertTrue(new DeploymentAdminPermission("(signer=CN=*, O=ACME Inc, " +
				"OU=ACME Cert Authority, L=Austin, ST=Texas, C=US)", "install").implies(
						new DeploymentAdminPermission("(&(signer=" + 
				"CN=John Smith, O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US" +
				")(name=*))", "install")));
	}
	
	public void testImplies013() {
		assertFalse(new DeploymentAdminPermission("(signer=CN=*Big, O=ACME Inc, " +
				"OU=ACME Cert Authority, L=Austin, ST=Texas, C=US)", "install").implies(
						new DeploymentAdminPermission("(&(signer=" + 
				"CN=John Smith, O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US" +
				")(name=*))", "install")));
	}
	
	public void testImplies014() {
		assertTrue(new DeploymentAdminPermission("(signer=-;CN=*, O=ACME Inc, " +
				"OU=ACME Cert Authority, L=Austin, ST=Texas, C=US)", "install").implies(
						new DeploymentAdminPermission(
				"(signer=CN=John Smith, O=ACME Inc, OU=ACME Cert Authority, L=Austin, " +
				"ST=Texas, C=US)", "install")));
	}
	
	public void testImplies015() {
		assertTrue(new DeploymentAdminPermission("(signer=-;CN=*, O=ACME Inc, " +
				"OU=ACME Cert Authority, L=Austin, ST=Texas, C=US)", "install").implies(
						new DeploymentAdminPermission("(signer=CN=First;CN=John Smith, " +
								"O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, " +
								"C=US)", "install")));
	}
	
	public void testImplies016() {
		assertTrue(new DeploymentAdminPermission("(signer=-;CN=*, O=ACME Inc, " +
				"OU=ACME Cert Authority, L=Austin, ST=Texas, C=US)", "install").implies(
						new DeploymentAdminPermission("(&(name=rp_resource_dp)(signer=-;" +
								"CN=*, O=ACME Inc, OU=ACME Cert Authority, L=Austin, " +
								"ST=Texas, C=US))", "install")));
	}
	
	public void testImplies017() {
		assertFalse(new DeploymentAdminPermission("(&(signer=CN=John Smith, " +
				"O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US)" +
				"(name=*))", "install").implies(
						new DeploymentAdminPermission("(signer=CN=John Big, O=ACME Inc, " +
								"OU=ACME Cert Authority, L=Austin, ST=Texas, C=US)", 
								"install")));
	}
	
	public void testEquals001() {
		DeploymentAdminPermission p1 = new DeploymentAdminPermission("(name=apple)", "install");
		DeploymentAdminPermission p2 = new DeploymentAdminPermission("(name=apple)", "install");
		DeploymentAdminPermission p3 = new DeploymentAdminPermission("(name=banana)", "install");
		DeploymentAdminPermission p4 = new DeploymentAdminPermission("(name=apple)", "list");
		DeploymentAdminPermission p5 = new DeploymentAdminPermission("(name=apple)", "install, list");
		
		assertTrue(p1.equals(p2));
		
		assertFalse(p1.equals(p3));
		assertFalse(p1.equals(p4));
		assertFalse(p1.equals(p5));
	}
	
	public void testEquals002() {
		DeploymentAdminPermission p1 = new DeploymentAdminPermission("(&(name=apple)" +
				"(signer=CN=Joe))", "install");
		DeploymentAdminPermission p2 = new DeploymentAdminPermission("(&(name=apple)" +
				"(signer=CN=Joe))", "install");
		
		assertTrue(p1.equals(p2));
		assertTrue(p1.equals(p2));
	}

	public void testEquals003() {
		DeploymentAdminPermission p1 = new DeploymentAdminPermission("(&(name=apple)" +
				"(signer=CN=Joe))", "install");
		DeploymentAdminPermission p2 = new DeploymentAdminPermission("(&(name=apple)" +
				"(signer=CN=Joe))", "install");
		assertTrue(p1.equals(p2));
		assertTrue(p1.equals(p2));
	}
	
}

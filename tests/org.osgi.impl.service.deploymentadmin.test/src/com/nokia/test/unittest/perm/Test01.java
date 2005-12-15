package com.nokia.test.unittest.perm;

import org.osgi.impl.service.deploymentadmin.perm.DeploymentAdminPermission;

public class Test01 {

	public static void main(String[] args) {
    	System.out.println(new DeploymentAdminPermission(
    			"(name=apple)", "install"
    	).implies(new DeploymentAdminPermission(
    			"(name=apple)", "install")
    	) == true);
    	
    	System.out.println(new DeploymentAdminPermission(
    			"(name=*)", "install"
    	).implies(new DeploymentAdminPermission(
    			"(name=apple)", "install")
    	) == true);
    	
    	System.out.println(new DeploymentAdminPermission(
    			"(name=app*)", "install"
    	).implies(new DeploymentAdminPermission(
    			"(name=apple)", "install")
    	) == true);
    	
    	System.out.println(new DeploymentAdminPermission(
    			"(name=app*le)", "install"
    	).implies(new DeploymentAdminPermission(
    			"(name=apple)", "install")
    	) == true);
    	
    	System.out.println(new DeploymentAdminPermission(
    			"(name=app?e)", "install"
    	).implies(new DeploymentAdminPermission(
    			"(name=apple)", "install")
    	) == true);
    	
    	System.out.println(new DeploymentAdminPermission(
    			"(name=app?e_tree_*_on_it)", "install"
    	).implies(new DeploymentAdminPermission(
    			"(name=apple_tree_bird_on_it)", "install")
    	) == true);
    	
    	System.out.println(new DeploymentAdminPermission(
    			"(name=app?e_tree_*_on_it)", "install"
    	).implies(new DeploymentAdminPermission(
    			"(name=apple_tree_fox_under_it)", "install")
    	) == false);
    	
    	System.out.println(new DeploymentAdminPermission(
    			"(name=apX*le)", "install"
    	).implies(new DeploymentAdminPermission(
    			"(name=apple)", "install")
    	) == false);
    	
    	System.out.println(new DeploymentAdminPermission(
    			"(name=app*)", "install"
    	).implies(new DeploymentAdminPermission(
    			"(name=apXle)", "install")
    	) == false);
    	
    	System.out.println(new DeploymentAdminPermission(
    			"(signer=*)", "install"
    	).implies(new DeploymentAdminPermission(
    			"(&(signer=" + 
    			"CN=John Smith, O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US" +
    			")(name=apple))", "install")
    	) == true);
    	
		System.out.println(new DeploymentAdminPermission(
				"(name=*)", "install"
		).implies(new DeploymentAdminPermission(
				"(&(signer=" + 
				"CN=John Smith, O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US" +
				")(name=*))", "install")
		) == true);

		System.out.println(new DeploymentAdminPermission(
				"(signer=CN=*, O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US)", "install"
		).implies(new DeploymentAdminPermission(
				"(&(signer=" + 
				"CN=John Smith, O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US" +
				")(name=*))", "install")
		) == true);
		
		System.out.println(new DeploymentAdminPermission(
				"(signer=CN=*Big, O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US)", "install"
		).implies(new DeploymentAdminPermission(
				"(&(signer=" + 
				"CN=John Smith, O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US" +
				")(name=*))", "install")
		) == false);
		
    }

}

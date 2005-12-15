package com.nokia.test.unittest.perm;

import org.osgi.impl.service.deploymentadmin.perm.DeploymentAdminPermission;

public class Test02 {
	
	public static void main(String[] args) {
		
		System.out.println(new DeploymentAdminPermission(
				"(signer=-;CN=*, O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US)", "install"
		).implies(new DeploymentAdminPermission(
				"(signer=CN=John Smith, O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US)", "install")
		) == true);

		System.out.println(new DeploymentAdminPermission(
				"(signer=-;CN=*, O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US)", "install"
		).implies(new DeploymentAdminPermission(
				"(signer=CN=First;CN=John Smith, O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US)", "install")
		) == true);
		
		System.out.println(new DeploymentAdminPermission(
				"(signer=-;CN=* Smith, O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US)", "install"
		).implies(new DeploymentAdminPermission(
				"(signer=CN=John Big, O=ACME Inc, OU=ACME Cert Authority, L=Austin, ST=Texas, C=US)", "install")
		) == false);
		
	}
	
}

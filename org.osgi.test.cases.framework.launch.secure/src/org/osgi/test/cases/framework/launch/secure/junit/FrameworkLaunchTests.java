/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.framework.launch.secure.junit;

import java.io.IOException;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;

public class FrameworkLaunchTests extends LaunchTest {
	private static final String	TEST_TRUST_REPO		= "org.osgi.test.cases.framework.launch.secure.trust.repositories";

	private Map<String, String> getConfiguration(String testName) {
		return getConfiguration(testName, true);
	}

	private Map<String, String> getConfiguration(String testName, boolean delete) {
		Map<String, String> configuration = new HashMap<String, String>();
		if (testName != null)
			configuration.put(Constants.FRAMEWORK_STORAGE, getStorageArea(testName, delete).getAbsolutePath());
		return configuration;
	}

	public void testSecurity() {
		SecurityManager previousSM = System.getSecurityManager();
		if (previousSM != null) {
			// need to remove security manager to test this
			System.setSecurityManager(null);
		}
		Policy previous = Policy.getPolicy();
		Policy.setPolicy(new AllPolicy());
		try {
			Map<String, String> configuration = getConfiguration(getName());
			configuration.put(Constants.FRAMEWORK_SECURITY, "osgi");
			Framework framework = createFramework(configuration);
			initFramework(framework);
			assertNotNull("Null SecurityManager", System.getSecurityManager());
			stopFramework(framework);
			assertNull("SecurityManager is not null", System
					.getSecurityManager());

			System.setSecurityManager(new SecurityManager());
			try {
				framework.start();
				fail("Expected an exception when starting with a SecurityManager already set");
			}
			catch (Exception e) {
				// expected
			}
			finally {
				System.setSecurityManager(null);
			}
		}
		finally {
			if (previousSM != null)
				System.setSecurityManager(previousSM);
			Policy.setPolicy(previous);
		}
	}

	public void testTrustRepositories() throws BundleException, IOException {
		String testRepo = getProperty(TEST_TRUST_REPO);
		if (testRepo == null)
			fail("Must set property to test: \"" + TEST_TRUST_REPO + "\"");
		Map<String, String> configuration = getConfiguration(getName());
		doTestTrustRepository(configuration, null, false);
		doTestTrustRepository(configuration, testRepo, true);
	}


	private void doTestTrustRepository(Map<String, String> configuration,
			String testRepo, boolean trusted) throws BundleException,
			IOException {
		if (testRepo != null)
			configuration.put(Constants.FRAMEWORK_TRUST_REPOSITORIES, testRepo);
		else
			configuration.remove(Constants.FRAMEWORK_TRUST_REPOSITORIES);
		Framework framework = createFramework(configuration);
		startFramework(framework);
		Bundle testBundle = installBundle(framework, "/launch.secure.tb1.jar");
		Map<X509Certificate, List<X509Certificate>> signers = testBundle
				.getSignerCertificates(Bundle.SIGNERS_ALL);
		assertEquals("Expecting 1 signer", 1, signers.size());
		Map<X509Certificate, List<X509Certificate>> trustedSigners = testBundle
				.getSignerCertificates(Bundle.SIGNERS_TRUSTED);
		if (trusted)
			assertEquals("Expecting 1 signer", 1, trustedSigners.size());
		else
			assertEquals("Expecting 0 signers", 0, trustedSigners.size());
		stopFramework(framework);
	}

	static class AllPolicy extends Policy {
		static final PermissionCollection	all;
		static {
			AllPermission perm = new AllPermission();
			all = perm.newPermissionCollection();
			all.add(perm);
			all.setReadOnly();
		}

        public PermissionCollection getPermissions(ProtectionDomain domain) {
        	// causes recursive permission check (StackOverflowError)
        	// System.out.println("Returning all permission for " + domain == null ? null : domain.getCodeSource());
        	return all;
        }
        public PermissionCollection getPermissions(CodeSource codesource) {
            System.out.println("Returning all permission for " + codesource);
            return all;
        }

        public boolean implies(ProtectionDomain domain, Permission permission) {
        	// causes recursive permission check (StackOverflowError)
        	// System.out.println("Granting permission for " + domain == null ? null : domain.getCodeSource());
        	return true;
        }

        public void refresh() {
			// empty
        }
    }
}

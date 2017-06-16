/*
 * Copyright (c) OSGi Alliance (2013, 2014). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.test.cases.subsystem.junit;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Constants;
import org.osgi.service.subsystem.Subsystem;
import org.osgi.service.subsystem.SubsystemConstants;
import org.osgi.test.cases.subsystem.util.SubsystemBuilder;

/*
 * This is just a basic, sanity check test.
 */
public class DeploymentHeadersTests extends SubsystemTest {
	public void testGetDeploymentHeaders() throws Exception {
		Pattern pattern;
		Matcher matcher;
		String version = "1.0.1";
		String deployedContent = "deployment.headers.a;deployed-version=\"1\";type=osgi.bundle";
		String provisionResource = "deployment.headers.b;deployed-version=\"1\";type=osgi.bundle";
		String importPackage = "org.osgi.service.log";
		String resolutionDirective = "mandatory";
		String exportPackage = "foo";
		String requireBundle = "system.bundle";
		String provideCapability = "foo";
		String requireCapability = "osgi.ee; filter:=\"(osgi.ee=*)\"";
		String importService = "org.osgi.service.log.LogService";
		String exportService = "foo";
		String symbolicNameA = "deployment.headers.a";
		String symbolicNameB = "deployment.headers.b";
		Subsystem root = getRootSubsystem();
		Subsystem subsystem = root.install(
				getName(),
				new SubsystemBuilder(getContext())
						.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getName())
						.header(SubsystemConstants.SUBSYSTEM_VERSION, version)
						.header(SubsystemConstants.SUBSYSTEM_TYPE, SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE)
						.header(SubsystemConstants.SUBSYSTEM_CONTENT, symbolicNameA + ";version=\"[1,1]\"")
						.header(Constants.IMPORT_PACKAGE, importPackage)
						.header(Constants.EXPORT_PACKAGE, exportPackage)
						.header(Constants.REQUIRE_BUNDLE, requireBundle)
						.header(Constants.PROVIDE_CAPABILITY, provideCapability)
						.header(Constants.REQUIRE_CAPABILITY, requireCapability)
						.header(SubsystemConstants.SUBSYSTEM_IMPORTSERVICE, importService)
						.header(SubsystemConstants.SUBSYSTEM_EXPORTSERVICE, exportService)
						.bundle(symbolicNameA + ".jar")
						.bundle(symbolicNameB + ".jar").deploymentHeader(SubsystemConstants.DEPLOYMENT_MANIFESTVERSION, "1")
						.deploymentHeader(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getName())
						.deploymentHeader(SubsystemConstants.SUBSYSTEM_VERSION, version)
						.deploymentHeader(SubsystemConstants.DEPLOYED_CONTENT, deployedContent)
						.deploymentHeader(SubsystemConstants.PROVISION_RESOURCE, provisionResource)
						.deploymentHeader(Constants.IMPORT_PACKAGE,
								importPackage)
						.deploymentHeader(Constants.EXPORT_PACKAGE, exportPackage)
						.deploymentHeader(Constants.REQUIRE_BUNDLE, requireBundle)
						.deploymentHeader(Constants.PROVIDE_CAPABILITY, provideCapability)
						.deploymentHeader(Constants.REQUIRE_CAPABILITY, requireCapability)
						.deploymentHeader(SubsystemConstants.SUBSYSTEM_IMPORTSERVICE, importService)
						.deploymentHeader(SubsystemConstants.SUBSYSTEM_EXPORTSERVICE, exportService)
						.build());
		try {
			Map<String, String> headers = subsystem.getDeploymentHeaders();
			assertEquals(
					"Wrong " + SubsystemConstants.DEPLOYMENT_MANIFESTVERSION
							+ " header",
					"1",
					headers.get(SubsystemConstants.DEPLOYMENT_MANIFESTVERSION));
			assertEquals(
					"Wrong " + SubsystemConstants.SUBSYSTEM_SYMBOLICNAME
							+ " header",
					getName(),
					headers.get(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME));
			assertEquals(
					"Wrong " + SubsystemConstants.SUBSYSTEM_VERSION + " header",
					version, headers.get(SubsystemConstants.SUBSYSTEM_VERSION));
			assertTrue(
					"Wrong " + SubsystemConstants.DEPLOYED_CONTENT + "header",
					headers.get(SubsystemConstants.DEPLOYED_CONTENT).matches(
							".*deployment\\.headers\\.a;.*deployed-version=\"?1(?:\\.0(?:\\.0)?)?\"?.*"));
			assertTrue(
					"Wrong " + SubsystemConstants.PROVISION_RESOURCE + "header",
					headers.get(SubsystemConstants.PROVISION_RESOURCE).matches(
							".*deployment\\.headers\\.b;.*deployed-version=\"?1(?:\\.0(?:\\.0)?)?\"?.*"));
			
			assertTrue("Wrong " + Constants.EXPORT_PACKAGE + " header",
					headers.get(Constants.EXPORT_PACKAGE).matches(exportPackage
							+ "(?:;version=\"?0(?:\\.0(?:\\.0)?)?\"?)?"));
			
			assertTrue("Wrong " + Constants.PROVIDE_CAPABILITY + " header",
					headers.get(Constants.PROVIDE_CAPABILITY)
							.matches(provideCapability + ".*"));
			assertTrue("Wrong " + Constants.REQUIRE_CAPABILITY + " header",
					headers.get(Constants.REQUIRE_CAPABILITY).matches(
							"osgi\\.ee;.*filter\\:=\"\\(osgi\\.ee=\\*\\)\".*"));
			assertTrue(
					"Wrong " + SubsystemConstants.SUBSYSTEM_IMPORTSERVICE
							+ " header",
					headers.get(SubsystemConstants.SUBSYSTEM_IMPORTSERVICE)
							.matches(importService + ".*"));
			assertEquals(
					"Wrong " + SubsystemConstants.SUBSYSTEM_EXPORTSERVICE
							+ " header",
					exportService,
					headers.get(SubsystemConstants.SUBSYSTEM_EXPORTSERVICE));
			

			String importPackHeader[] = headers.get(Constants.IMPORT_PACKAGE)
					.split(";");

			for (int i = 0; i < importPackHeader.length; i++) {
				if (importPackHeader[i].contains(Constants.VERSION_ATTRIBUTE)) {
					assertTrue("Wrong " + Constants.IMPORT_PACKAGE + " header",
							importPackHeader[i].matches(
									"version=\"?0(?:\\.0(?:\\.0)?)?\"?"));
				} else if (importPackHeader[i]
						.contains(Constants.RESOLUTION_DIRECTIVE)) {
					assertTrue("Wrong " + Constants.IMPORT_PACKAGE + " header",
							importPackHeader[i]
									.matches("resolution:=\"?mandatory\"?"));
				} else {
					assertTrue("Wrong " + Constants.IMPORT_PACKAGE + " header",
							importPackHeader[i].matches(importPackage));
				}
			}

			String requireBundleHeader[] = headers.get(Constants.REQUIRE_BUNDLE)
					.split(";");

			for (int i = 0; i < requireBundleHeader.length; i++) {
				if (requireBundleHeader[i]
						.contains(Constants.VISIBILITY_DIRECTIVE)) {
					assertTrue("Wrong " + Constants.REQUIRE_BUNDLE + " header",
							requireBundleHeader[i]
									.matches("visibility:=\"?private\"?"));
				} else if (requireBundleHeader[i]
						.contains(Constants.RESOLUTION_DIRECTIVE)) {
					assertTrue("Wrong " + Constants.REQUIRE_BUNDLE + " header",
							requireBundleHeader[i]
									.matches("resolution:=\"?mandatory\"?"));
				} else if (requireBundleHeader[i]
						.contains(Constants.BUNDLE_VERSION_ATTRIBUTE)) {
					assertTrue("Wrong " + Constants.REQUIRE_BUNDLE + " header",
							requireBundleHeader[i].matches(
									"bundle-version=\"?0(?:\\.0(?:\\.0)?)?\"?"));
				} else {
					assertTrue("Wrong " + Constants.REQUIRE_BUNDLE + " header",
							requireBundleHeader[i].matches(requireBundle));
				}
			}



		}
		finally {
			subsystem.uninstall();
		}
	}
}

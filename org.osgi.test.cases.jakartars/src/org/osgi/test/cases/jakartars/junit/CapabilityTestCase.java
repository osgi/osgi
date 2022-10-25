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
package org.osgi.test.cases.jakartars.junit;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.osgi.namespace.contract.ContractNamespace.CAPABILITY_VERSION_ATTRIBUTE;
import static org.osgi.namespace.contract.ContractNamespace.CONTRACT_NAMESPACE;
import static org.osgi.namespace.implementation.ImplementationNamespace.IMPLEMENTATION_NAMESPACE;
import static org.osgi.namespace.service.ServiceNamespace.CAPABILITY_OBJECTCLASS_ATTRIBUTE;
import static org.osgi.namespace.service.ServiceNamespace.SERVICE_NAMESPACE;
import static org.osgi.resource.Namespace.CAPABILITY_USES_DIRECTIVE;
import static org.osgi.service.jakartars.whiteboard.JakartarsWhiteboardConstants.JAKARTA_RS_WHITEBOARD_SPECIFICATION_VERSION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.resource.Capability;
import org.osgi.service.jakartars.client.SseEventSourceFactory;
import org.osgi.service.jakartars.runtime.JakartarsServiceRuntime;

import jakarta.ws.rs.client.ClientBuilder;

public class CapabilityTestCase extends AbstractJakartarsTestCase {
	
	private static final List<String> JAX_RS_PACKAGES = Arrays.asList(
			"jakarta.ws.rs", "jakarta.ws.rs.core",
			"jakarta.ws.rs.ext", "jakarta.ws.rs.client",
			"jakarta.ws.rs.container", "jakarta.ws.rs.sse");

	/**
	 * A basic test that ensures the provider of the JaxrsServiceRuntime service
	 * advertises the JaxrsServiceRuntime service capability (151.10.3)
	 * 
	 * @throws Exception
	 */
	@Test
	public void testJaxRsServiceRuntimeServiceCapability() throws Exception {

		List<BundleCapability> capabilities = runtime.getBundle()
				.adapt(BundleWiring.class)
				.getCapabilities(SERVICE_NAMESPACE);

		boolean hasCapability = false;
		boolean uses = false;

		for (Capability cap : capabilities) {
			Object o = cap.getAttributes()
					.get(CAPABILITY_OBJECTCLASS_ATTRIBUTE);
			@SuppressWarnings("unchecked")
			List<String> objectClass = o instanceof List ? (List<String>) o
					: asList(valueOf(o));

			if (objectClass.contains(JakartarsServiceRuntime.class.getName())) {
				hasCapability = true;

				String usesDirective = cap.getDirectives()
						.get(CAPABILITY_USES_DIRECTIVE);
				if (usesDirective != null) {
					Set<String> packages = new HashSet<String>(Arrays
							.asList(usesDirective.trim().split("\\s*,\\s*")));
					uses = packages
							.contains("org.osgi.service.jakartars.runtime")
							&& packages.contains(
									"org.osgi.service.jakartars.runtime.dto");
				}

				break;
			}
		}
		assertTrue(
				"No osgi.service capability for the JaxrsServiceRuntime service",
				hasCapability);
		assertTrue(
				"No suitable uses constraint on the runtime package for the JaxrsServiceRuntime service",
				uses);
	}

	/**
	 * A basic test that ensures the provider of the ClientBuilder service
	 * advertises the ClientBuilder service capability (151.10.3)
	 * 
	 * @throws Exception
	 */
	@Test
	public void testJaxRsClientBuilderServiceCapability() throws Exception {

		List<BundleCapability> capabilities = runtime.getBundle()
				.adapt(BundleWiring.class)
				.getCapabilities(SERVICE_NAMESPACE);

		boolean hasCapability = false;
		boolean uses = false;

		for (Capability cap : capabilities) {
			Object o = cap.getAttributes()
					.get(CAPABILITY_OBJECTCLASS_ATTRIBUTE);
			@SuppressWarnings("unchecked")
			List<String> objectClass = o instanceof List ? (List<String>) o
					: asList(valueOf(o));

			if (objectClass.contains(ClientBuilder.class.getName())) {
				hasCapability = true;

				String usesDirective = cap.getDirectives()
						.get(CAPABILITY_USES_DIRECTIVE);
				if (usesDirective != null) {
					Set<String> packages = new HashSet<String>(Arrays
							.asList(usesDirective.trim().split("\\s*,\\s*")));
					uses = packages.contains("jakarta.ws.rs.client") &&
							packages.contains(
									"org.osgi.service.jakartars.client");
				}

				break;
			}
		}
		assertTrue(
				"No osgi.service capability for the ClientBuilder service",
				hasCapability);
		assertTrue(
				"No suitable uses constraint on the runtime package for the ClientBuilder service",
				uses);
	}

	/**
	 * A basic test that ensures the provider of the SseEventSourceFactory
	 * service advertises the SseEventSourceFactory service capability
	 * (151.10.3)
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSseEventSourceFactoryServiceCapability() throws Exception {

		List<BundleCapability> capabilities = runtime.getBundle()
				.adapt(BundleWiring.class)
				.getCapabilities(SERVICE_NAMESPACE);

		boolean hasCapability = false;
		boolean uses = false;

		for (Capability cap : capabilities) {
			Object o = cap.getAttributes()
					.get(CAPABILITY_OBJECTCLASS_ATTRIBUTE);
			@SuppressWarnings("unchecked")
			List<String> objectClass = o instanceof List ? (List<String>) o
					: asList(valueOf(o));

			if (objectClass.contains(SseEventSourceFactory.class.getName())) {
				hasCapability = true;

				String usesDirective = cap.getDirectives()
						.get(CAPABILITY_USES_DIRECTIVE);
				if (usesDirective != null) {
					Set<String> packages = new HashSet<String>(Arrays
							.asList(usesDirective.trim().split("\\s*,\\s*")));
					uses = packages
							.contains("org.osgi.service.jakartars.client");
				}

				break;
			}
		}
		assertTrue(
				"No osgi.service capability for the SseEventSourceFactory service",
				hasCapability);
		assertTrue(
				"No suitable uses constraint on the runtime package for the SseEventSourceFactory service",
				uses);
	}

	/**
	 * A basic test that ensures that the implementation advertises the JAX-RS
	 * implementation capability (151.10.1)
	 * 
	 * @throws Exception
	 */
	@Test
	public void testJaxRsServiceWhiteboardImplementationCapability()
			throws Exception {

		boolean hasCapability = false;
		boolean uses = false;
		boolean version = false;

		bundles: for (Bundle bundle : context.getBundles()) {
			List<BundleCapability> capabilities = bundle
					.adapt(BundleWiring.class)
					.getCapabilities(IMPLEMENTATION_NAMESPACE);

			for (Capability cap : capabilities) {
				hasCapability = "osgi.jakartars".equals(
						cap.getAttributes().get(IMPLEMENTATION_NAMESPACE));
				if (hasCapability) {
					Version required = Version
							.valueOf(
									JAKARTA_RS_WHITEBOARD_SPECIFICATION_VERSION);
					Version toCheck = (Version) cap.getAttributes()
							.get(CAPABILITY_VERSION_ATTRIBUTE);

					version = required.equals(toCheck);

					String usesDirective = cap.getDirectives()
							.get(CAPABILITY_USES_DIRECTIVE);
					if (usesDirective != null) {
						Collection<String> requiredPackages = new ArrayList<>(
								JAX_RS_PACKAGES);
						requiredPackages
								.add("org.osgi.service.jakartars.whiteboard");

						Set<String> packages = new HashSet<String>(
								Arrays.asList(usesDirective.trim()
										.split("\\s*,\\s*")));

						uses = packages.containsAll(requiredPackages);
					}

					break bundles;
				}
			}
		}

		assertTrue(
				"No osgi.implementation capability for the JAX-RS whiteboard implementation",
				hasCapability);

		assertTrue(
				"No osgi.implementation capability for the JAX-RS Whiteboard at version "
						+ JAKARTA_RS_WHITEBOARD_SPECIFICATION_VERSION,
				version);
		assertTrue(
				"The osgi.implementation capability for the JAX-RS API does not have the correct uses constraint",
				uses);
	}

	/**
	 * A basic test that ensures that the implementation advertises the JAX-RS
	 * contract capability (151.10.2)
	 * 
	 * @throws Exception
	 */
	@Test
	public void testJaxRsContractCapability()
			throws Exception {
		
		boolean hasCapability = false;
		boolean uses = false;
		boolean version = false;
		
		bundles: for (Bundle bundle : context.getBundles()) {
			List<BundleCapability> capabilities = bundle
					.adapt(BundleWiring.class)
					.getCapabilities(CONTRACT_NAMESPACE);
			
			for (Capability cap : capabilities) {
				hasCapability = "JavaJAXRS".equals(
						cap.getAttributes().get(CONTRACT_NAMESPACE));
				if (hasCapability) {
					Version required = Version.valueOf("2.1");
					List<Version> toCheck = Collections.emptyList();
					
					Object rawVersion = cap.getAttributes().get(CAPABILITY_VERSION_ATTRIBUTE);
					if(rawVersion instanceof Version) {
						toCheck = Collections.singletonList((Version)rawVersion);
					} else if (rawVersion instanceof Version[]) {
						toCheck = Arrays.asList((Version[])rawVersion);
					} else if (rawVersion instanceof List) {
						@SuppressWarnings("unchecked")
						List<Version> tmp = (List<Version>) rawVersion;
						toCheck = tmp;
					}
					
					version = toCheck.contains(required);

					String usesDirective = cap.getDirectives()
							.get(CAPABILITY_USES_DIRECTIVE);
					if (usesDirective != null) {
						Collection<String> requiredPackages = JAX_RS_PACKAGES;

						Set<String> packages = new HashSet<String>(Arrays
								.asList(usesDirective.trim().split("\\s*,\\s*")));

						uses = packages.containsAll(requiredPackages);
					}

					break bundles;
				}
			}
		}
		
		assertTrue(
				"No osgi.contract capability for the JAX-RS API",
				hasCapability);
		assertTrue(
				"No osgi.contract capability for the JAX-RS API at version 2.1",
				version);
		assertTrue(
				"The osgi.contract capability for the JAX-RS API does not have the correct uses constraint",
				uses);
	}
}

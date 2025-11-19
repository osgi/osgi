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
package org.osgi.test.cases.webservice.junit;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.osgi.namespace.implementation.ImplementationNamespace.CAPABILITY_VERSION_ATTRIBUTE;
import static org.osgi.namespace.implementation.ImplementationNamespace.IMPLEMENTATION_NAMESPACE;
import static org.osgi.namespace.service.ServiceNamespace.CAPABILITY_OBJECTCLASS_ATTRIBUTE;
import static org.osgi.namespace.service.ServiceNamespace.SERVICE_NAMESPACE;
import static org.osgi.resource.Namespace.CAPABILITY_USES_DIRECTIVE;
import static org.osgi.service.webservice.whiteboard.WebserviceWhiteboardConstants.WEBSERVICE_IMPLEMENTATION;
import static org.osgi.service.webservice.whiteboard.WebserviceWhiteboardConstants.WEBSERVICE_SPECIFICATION_VERSION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.resource.Capability;
import org.osgi.service.webservice.runtime.WebserviceServiceRuntime;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;

public class CapabilityTestCase {
	
	private static final List<String> JAKARTA_XML_WS_PACKAGES = Arrays.asList(
			"jakarta.xml.ws", "jakarta.xml.ws.handler"
			// TODO Add other packages once they are defined
			);

	/**
	 * A basic test that ensures the provider of the WebserviceServiceRuntime service
	 * advertises the WebserviceServiceRuntime service capability
	 * 
	 * @throws Exception
	 */
	@Test
	public void testWebserviceServiceRuntimeServiceCapability(
			@InjectService ServiceAware<WebserviceServiceRuntime> runtimeAware) throws Exception {

		// TODO link to specification section
		
		ServiceReference<WebserviceServiceRuntime> ref = runtimeAware.getServiceReference();
		
		List<BundleCapability> capabilities = ref.getBundle()
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

			if (objectClass.contains(WebserviceServiceRuntime.class.getName())) {
				hasCapability = true;

				String usesDirective = cap.getDirectives()
						.get(CAPABILITY_USES_DIRECTIVE);
				if (usesDirective != null) {
					Set<String> packages = new HashSet<String>(Arrays
							.asList(usesDirective.trim().split("\\s*,\\s*")));
					uses = packages
							.contains("org.osgi.service.webservice.runtime")
							&& packages.contains(
									"org.osgi.service.webservice.runtime.dto");
					if (uses)
						break;
				}
			}
		}
		assertTrue(hasCapability,
				"No osgi.service capability for the WebserviceServiceRuntime service");
		assertTrue(uses,
				"No suitable uses constraint on the runtime package for the WebserviceServiceRuntime service");
	}

	/**
	 * A basic test that ensures that the implementation advertises the Jakarta XML Web Service Whiteboard
	 * implementation capability
	 * 
	 * @throws Exception
	 */
	@Test
	public void testWebserviceWhiteboardImplementationCapability(@InjectBundleContext BundleContext context)
			throws Exception {

		// TODO link to specification section
		
		boolean hasCapability = false;
		boolean uses = false;
		boolean version = false;

		bundles: for (Bundle bundle : context.getBundles()) {
			List<BundleCapability> capabilities = bundle
					.adapt(BundleWiring.class)
					.getCapabilities(IMPLEMENTATION_NAMESPACE);

			for (Capability cap : capabilities) {
				hasCapability = WEBSERVICE_IMPLEMENTATION.equals(
						cap.getAttributes().get(IMPLEMENTATION_NAMESPACE));
				if (hasCapability) {
					Version required = Version
							.valueOf(WEBSERVICE_SPECIFICATION_VERSION);
					Version toCheck = (Version) cap.getAttributes()
							.get(CAPABILITY_VERSION_ATTRIBUTE);

					version = required.equals(toCheck);

					String usesDirective = cap.getDirectives()
							.get(CAPABILITY_USES_DIRECTIVE);
					if (usesDirective != null) {
						Collection<String> requiredPackages = new ArrayList<>(
								JAKARTA_XML_WS_PACKAGES);
						requiredPackages
								.add("org.osgi.service.webservice.whiteboard");

						Set<String> packages = new HashSet<String>(
								Arrays.asList(usesDirective.trim()
										.split("\\s*,\\s*")));

						uses = packages.containsAll(requiredPackages);
					}

					break bundles;
				}
			}
		}

		assertTrue(hasCapability,
				"No osgi.implementation capability for the Web Service whiteboard implementation");

		assertTrue(version,
				"No osgi.implementation capability for the Web Service Whiteboard at version "
						+ WEBSERVICE_SPECIFICATION_VERSION);
		assertTrue(uses,
				"The osgi.implementation capability for the JAX-RS API does not have the correct uses constraint");
	}
}

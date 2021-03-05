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

package org.osgi.test.cases.event.junit;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.namespace.implementation.ImplementationNamespace;
import org.osgi.namespace.service.ServiceNamespace;
import org.osgi.resource.Namespace;
import org.osgi.service.event.EventAdmin;
import org.osgi.test.support.junit4.AbstractOSGiTestCase;
import org.osgi.test.support.string.Strings;
import org.osgi.util.tracker.ServiceTracker;

public class CapabilitiesTestCase extends AbstractOSGiTestCase {
	private static final Version					VERSION	= new Version(1, 4,
			0);
	private static final String						PACKAGE	= "org.osgi.service.event";
	private static final String						NAME	= "osgi.event";
	private static final String						SERVICE	= EventAdmin.class
			.getName();
	private ServiceTracker<EventAdmin,EventAdmin>	eaTracker;
	private BundleWiring							wiring;

	@Before
	public void setUp() throws Exception {
		eaTracker = new ServiceTracker<>(getContext(),
				EventAdmin.class.getName(), null);
		eaTracker.open();
		assertThat(eaTracker.size()).as("EventAdmin services").isEqualTo(1);
		Bundle impl = eaTracker.getServiceReference().getBundle();
		assertThat(impl).as("EventAdmin implementation bundle").isNotNull();
		wiring = impl.adapt(BundleWiring.class);
		assertThat(wiring)
				.as("BundleWiring of EventAdmin implementation bundle")
				.isNotNull();
	}

	@After
	public void tearDown() throws Exception {
		eaTracker.close();
	}

	@Test
	public void testImplementationCapability() throws Exception {
		final String namespace = ImplementationNamespace.IMPLEMENTATION_NAMESPACE;
		List<BundleCapability> implementations = wiring
				.getCapabilities(namespace);
		for (BundleCapability implementation : implementations) {
			assertThat(implementation.getAttributes())
					.as("capability attributes")
					.containsKey(namespace);
			if (NAME.equals(implementation.getAttributes().get(namespace))) {
				assertThat(implementation.getAttributes())
						.as("capability attributes")
						.containsEntry(
								ImplementationNamespace.CAPABILITY_VERSION_ATTRIBUTE,
								VERSION);
				assertThat(implementation.getDirectives())
						.as("capability directives")
						.containsKey(Namespace.CAPABILITY_USES_DIRECTIVE);
				String uses = implementation.getDirectives()
						.get(Namespace.CAPABILITY_USES_DIRECTIVE);
				assertThat(Strings.split(uses)).as("capability uses directive")
						.contains(PACKAGE);
				return;
			}
		}
		fail("missing %s capability for %s", namespace, NAME);
	}

	@Test
	public void testServiceCapability() throws Exception {
		final String namespace = ServiceNamespace.SERVICE_NAMESPACE;
		List<BundleCapability> services = wiring.getCapabilities(namespace);
		for (BundleCapability service : services) {
			assertThat(service.getAttributes()).as("capability attributes")
					.containsKey(
							ServiceNamespace.CAPABILITY_OBJECTCLASS_ATTRIBUTE);
			@SuppressWarnings("unchecked")
			List<String> objectClass = (List<String>) service.getAttributes()
					.get(ServiceNamespace.CAPABILITY_OBJECTCLASS_ATTRIBUTE);
			if (objectClass.contains(SERVICE)) {
				assertThat(service.getDirectives()).as("capability directives")
						.containsKey(Namespace.CAPABILITY_USES_DIRECTIVE);
				String uses = service.getDirectives()
						.get(Namespace.CAPABILITY_USES_DIRECTIVE);
				assertThat(Strings.split(uses)).as("capability uses directive")
						.contains(PACKAGE);
				return;
			}
		}
		fail("missing %s capability for %s", namespace, SERVICE);
	}

}

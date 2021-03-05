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
package org.osgi.test.cases.transaction.control.jpa;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.resource.Capability;
import org.osgi.resource.Namespace;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProviderFactory;

public class CapabilityTestCase extends JPAResourceTestCase {
	
	/**
	 * A basic test that ensures the provider of the TransactionControl service advertises
	 * the TransactionControl service capability
	 * 
	 * @throws Exception
	 */
	public void testTxControlServiceCapability() throws Exception {

		List<BundleCapability> capabilities = jpaResourceProviderBundle
				.adapt(BundleWiring.class).getCapabilities("osgi.service");

		boolean hasCapability = false;
		boolean matchingAttrs = false;
		boolean uses = false;

		for (Capability cap : capabilities) {
			Object o = cap.getAttributes().get("objectClass");
			@SuppressWarnings("unchecked")
			List<String> objectClass = o instanceof List ? (List<String>) o
					: asList(valueOf(o));

			if (objectClass
					.contains(
							JPAEntityManagerProviderFactory.class.getName())) {
				hasCapability = true;
				
				Map<String, Object> attrs = cap.getAttributes();
				
				matchingAttrs = (localEnabled == toBoolean(
						attrs.get("osgi.local.enabled")))
						&&
						(xaEnabled == toBoolean(attrs.get("osgi.xa.enabled")));
				
				String usesDirective = cap.getDirectives().get(Namespace.CAPABILITY_USES_DIRECTIVE);
				if (usesDirective != null) {
					Set<String> packages = new HashSet<String>(Arrays.asList(usesDirective.trim().split("\\s*,\\s*")));
					uses = packages
							.contains(
									"org.osgi.service.transaction.control.jpa");
				}
				break;
			}
		}
		assertTrue(
				"No osgi.service capability for the Transaction Control service",
				hasCapability);
		assertTrue(
				"The osgi.service capability has invalid local/xa attributes",
				matchingAttrs);
		assertTrue("Missing uses constraint on the osgi.service capability", uses);
	}
}

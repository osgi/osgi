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

package org.osgi.test.cases.component.annotations.junit;

import static org.osgi.test.cases.component.annotations.junit.DescriptionXPathAssert.assertThat;

import org.junit.Test;

/**
 * Test case for Declarative Services 1.6 annotations
 * @author $Id$
 */
public class DS16AnnotationsTestCase extends AnnotationsTestCase {

	@Test
	public void testHelloWorld16() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				.hasNamespace(xmlns_scr160)
				// v1.0.0
				.hasOptionalValue("@immediate", "true")
				.hasOptionalValue("@enabled", "true")
				.doesNotContain("@factory")
				.hasValue("implementation/@class",
						"org.osgi.impl.bundle.component.annotations."
								+ name.substring(4))
				.hasCount("implementation", 1)
				.hasCount("service", 0)
				.hasCount("reference", 0)
				// v1.1.0
				.hasOptionalValue("@configuration-policy", "optional")
				.hasOptionalValue("@activate", "activate")
				.hasOptionalValue("@deactivate", "deactivate")
				.hasValue("@modified", "modified")
				// v1.2.0
				.hasOptionalValue("@configuration-pid", name)
				// v1.3.0
				.hasOptionalValue("@scope", "singleton")
				// v1.4.0
				.doesNotContain("factory-property")
				.doesNotContain("factory-properties")
				.doesNotContain("@activation-fields")
				.doesNotContain("reference/@parameter")
				// v1.5.0
				// no new attributes/elements in v1.5.0
				// v1.6.0
				.hasOptionalValue("@retention-policy", "discard")
				;
	}

	@Test
	public void testRetentionPolicyKeep() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				.hasNamespace(xmlns_scr160)
				.hasValue("@name", name)
				.hasOptionalValue("@immediate", "false")
				.hasValue("@retention-policy", "keep")
				.hasValue("implementation/@class",
						"org.osgi.impl.bundle.component.annotations.RetentionPolicyKeep")
				.hasCount("service", 1)
				.hasValue("service/provide[1]/@interface",
						"org.osgi.impl.bundle.component.annotations.RetentionPolicyKeep")
				;
	}

	@Test
	public void testRetentionPolicyDiscard() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				.hasNamespace(xmlns_scr160)
				.hasValue("@name", name)
				.hasOptionalValue("@immediate", "false")
				.hasOptionalValue("@retention-policy", "discard")
				.hasValue("implementation/@class",
						"org.osgi.impl.bundle.component.annotations.RetentionPolicyDiscard")
				.hasCount("service", 1)
				.hasValue("service/provide[1]/@interface",
						"org.osgi.impl.bundle.component.annotations.RetentionPolicyDiscard")
				;
	}
}

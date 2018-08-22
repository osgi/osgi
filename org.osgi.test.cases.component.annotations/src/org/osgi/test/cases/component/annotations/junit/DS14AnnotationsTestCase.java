/*
 * Copyright (c) OSGi Alliance (2012, 2016). All Rights Reserved.
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

package org.osgi.test.cases.component.annotations.junit;

import static org.osgi.test.cases.component.annotations.junit.DescriptionXPathAssert.assertThat;

import org.junit.Test;

/**
 * @author $Id$
 */
public class DS14AnnotationsTestCase extends AnnotationsTestCase {


	@Test
	public void testHelloWorld14() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				.hasNamespace(xmlns_scr140)
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
				.doesNotContain("reference/@parameter");
	}

	@Test
	public void testFactoryProperties() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.4.0
				.hasCount("factory-properties", 1)
				.hasValue("factory-properties/@entry",
						"OSGI-INF/vendor.properties")
				.hasCount("factory-property", 10)
				.hasFactoryPropertyValue("a", "String", "foo")
				.hasFactoryPropertyArrayValue("b", "Integer", "2", "3")
				.hasFactoryPropertyValue("c", "Boolean", "true")
				.hasFactoryPropertyValue("d", "Long", "4")
				.hasFactoryPropertyValue("e", "Double", "5.0")
				.hasFactoryPropertyValue("f", "Float", "6.0")
				.hasFactoryPropertyValue("g", "Byte", "7")
				.hasFactoryPropertyValue("h", "Character", "8")
				.hasFactoryPropertyValue("i", "Short", "9")
				.hasFactoryPropertyValue("j", "String", "bar");
	}

}

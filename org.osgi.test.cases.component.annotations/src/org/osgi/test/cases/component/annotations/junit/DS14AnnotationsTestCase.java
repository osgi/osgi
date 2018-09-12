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

	@Test
	public void testNameMapping() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				.hasCount("property", 10)
				.hasPropertyValue("pre.myProperty143", "String",
						"default.myProperty143")
				.hasPropertyValue("pre.new", "String", "default.new")
				.hasPropertyValue("pre.dot.prop", "String", "default.dot.prop")
				.hasPropertyValue("pre.another_prop", "String",
						"default.another_prop")
				.hasPropertyValue("pre.three_.prop", "String",
						"default.three_.prop")
				.hasPropertyValue("pre.four._prop", "String",
						"default.four._prop")
				.hasPropertyValue("pre.five..prop", "String",
						"default.five..prop")
				// v1.4.0
				.hasPropertyValue("pre.six-prop", "String", "default.six-prop")
				.hasPropertyValue("pre.seven$.prop", "String",
						"default.seven$.prop");
	}

	@Test
	public void testComponentPropertyTypes() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				.hasCount("property", 13)
				.hasPropertyValue("pre.myProperty143", "String",
						"specified.myProperty143")
				.hasPropertyValue("pre.new", "String", "specified.new")
				.hasPropertyValue("pre.dot.prop", "String",
						"specified.dot.prop")
				.hasPropertyValue("pre.another_prop", "String",
						"specified.another_prop")
				.hasPropertyValue("pre.three_.prop", "String",
						"specified.three_.prop")
				.hasPropertyValue("pre.four._prop", "String",
						"specified.four._prop")
				.hasPropertyValue("pre.five..prop", "String",
						"specified.five..prop")
				.hasPropertyValue("pre.six-prop", "String",
						"specified.six-prop")
				.hasPropertyValue("pre.seven$.prop", "String",
						"specified.seven$.prop")
				.hasPropertyValue("service.description", "String",
						"Test case for Component Property Type annotations")
				.hasPropertyValue("service.ranking", "Integer", "42")
				.hasPropertyValue("service.vendor", "String", "OSGi Alliance")
				.doesNotContain("property[@name='ignored']");
	}

	@Test
	public void testActivationFields() throws Exception {
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
				.hasValuesExactlyInAnyOrder("@activation-fields", "bc",
						"configNames", "cc", "props")
				.doesNotContain("reference/@parameter");
	}

	@Test
	public void testPropertyOrdering14() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				.hasNamespace(xmlns_scr140)
				.hasCount("properties", 1)
				.hasValue("properties/@entry", "OSGI-INF/vendor.properties")
				.hasCount("property", 24)
				.hasValue("@activate", "activate1")
				.hasValue("@modified", "modified2")
				.hasValue("@deactivate", "deactivate3")
				.hasValue("@init", "2")
				.hasValuesExactlyInAnyOrder("@activation-fields", "c3", "c4")

				.hasPropertyValue("config1", "String", "config1")
				.hasPropertyValue("config2", "String", "config2")
				.hasPropertyValue("config3", "String", "config3")
				.hasPropertyValue("config4", "String", "config4")
				.hasPropertyValue("config5", "String", "config5")
				.hasPropertyValue("config6", "String", "config6")
				.hasPropertyValue("config7", "String", "config7")
				.hasPropertyValue("config8", "String", "config8")
				.hasPropertyValue("config9", "String", "config9")
				.hasPropertyValue("configA", "String", "configA")
				.hasPropertyValue("configB", "String", "configB")
				.hasPropertyValue("configC", "String", "configC")

				.hasPropertyValue("string1", "String", "config/string1")
				.hasPropertyValue("string2", "String", "config/string2")
				.hasPropertyValue("string3", "String", "config/string3")
				.hasPropertyValue("string4", "String", "config/string4")
				.hasPropertyValue("string5", "String", "config/string5")
				.hasPropertyValue("string6", "String", "config/string6")
				.hasPropertyValue("string7", "String", "config/string7")
				.hasPropertyValue("string8", "String", "config/string8")
				.hasPropertyValue("string9", "String", "config/string9")
				.hasPropertyValue("stringA", "String", "config/stringA")
				.hasPropertyValue("stringB", "String", "config/stringB")
				.hasPropertyValue("stringC", "String", "config/stringC");

	}

}

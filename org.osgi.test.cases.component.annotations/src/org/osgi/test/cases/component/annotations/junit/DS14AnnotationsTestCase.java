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
				.hasFactoryPropertyValue("h", "Character", Integer.toString('8'))
				.hasFactoryPropertyValue("i", "Short", "9")
				.hasFactoryPropertyValue("j", "String", "bar");
	}

	@Test
	public void testNameMapping() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				.hasCount("property", 12)
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
						"default.seven$.prop")
				.hasPropertyValue("single.single.element", "String",
						"default.single.single.element")
				.hasPropertyValue("marker.marker", "Boolean", "true");
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
				.hasPropertyValue("service.vendor", "String", "OSGi TCK")
				.doesNotContain("property[@name='ignored']");
	}

	@Test
	public void testComponentPropertyTypesWithEmptyArray() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as(() -> {
			try {
				return toString(description.getElement());
			} catch (Exception e) {
				return e.toString();
			}
		})
				.isNotNull()
				.as("component %s", name)
				.hasCount("property", 1)
				.hasPropertyValue("adaptableClass", "String",
						"java.lang.Runnable")
				.doesNotContain("property[@name='adapterNames']")
				.hasValue("implementation/@class",
						"org.osgi.impl.bundle.component.annotations.ComponentPropertyTypesWithEmptyArray");
	}

	@Test
	public void testActivationFields() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
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
	public void testLoggerComponent() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				.hasValue("@init", "2")

				.hasValue("reference[@name='loggerC']/@interface",
						"org.osgi.service.log.LoggerFactory")
				.hasValue("reference[@name='loggerC']/@parameter", "0")
				.doesNotContain("reference[@name='loggerC']/@bind")
				.doesNotContain("reference[@name='loggerC']/@unbind")
				.doesNotContain("reference[@name='loggerC']/@field")
				.hasOptionalValue("reference[@name='loggerC']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='loggerC']/@policy",
						"static")
				.doesNotContain("reference[@name='loggerC']/@target")

				.hasValue("reference[@name='formatterLoggerC']/@interface",
						"org.osgi.service.log.LoggerFactory")
				.hasValue("reference[@name='formatterLoggerC']/@parameter", "1")
				.doesNotContain("reference[@name='formatterLoggerC']/@bind")
				.doesNotContain("reference[@name='formatterLoggerC']/@unbind")
				.doesNotContain("reference[@name='formatterLoggerC']/@field")
				.hasOptionalValue(
						"reference[@name='formatterLoggerC']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='formatterLoggerC']/@policy",
						"static")
				.doesNotContain("reference[@name='formatterLoggerC']/@target")

				.hasValue("reference[@name='loggerF']/@interface",
						"org.osgi.service.log.LoggerFactory")
				.hasValue("reference[@name='loggerF']/@field", "loggerF")
				.doesNotContain("reference[@name='loggerF']/@bind")
				.doesNotContain("reference[@name='loggerF']/@unbind")
				.doesNotContain("reference[@name='loggerF']/@parameter")
				.hasOptionalValue("reference[@name='loggerF']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='loggerF']/@policy",
						"static")
				.doesNotContain("reference[@name='loggerF']/@target")

				.hasValue("reference[@name='formatterLoggerF']/@interface",
						"org.osgi.service.log.LoggerFactory")
				.hasValue("reference[@name='formatterLoggerF']/@field",
						"formatterLoggerF")
				.doesNotContain("reference[@name='formatterLoggerF']/@bind")
				.doesNotContain("reference[@name='formatterLoggerF']/@unbind")
				.doesNotContain(
						"reference[@name='formatterLoggerF']/@parameter")
				.hasOptionalValue(
						"reference[@name='formatterLoggerF']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='formatterLoggerF']/@policy",
						"static")
				.doesNotContain("reference[@name='formatterLoggerF']/@target")

				.hasValue("reference[@name='Logger']/@interface",
						"org.osgi.service.log.LoggerFactory")
				.doesNotContain("reference[@name='Logger']/@parameter")
				.hasValue("reference[@name='Logger']/@bind", "bindLogger")
				.doesNotContain("reference[@name='Logger']/@unbind")
				.doesNotContain("reference[@name='Logger']/@field")
				.hasOptionalValue("reference[@name='Logger']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='Logger']/@policy", "static")
				.doesNotContain("reference[@name='Logger']/@target")

				.hasValue("reference[@name='FormatterLogger']/@interface",
						"org.osgi.service.log.LoggerFactory")
				.doesNotContain("reference[@name='FormatterLogger']/@parameter")
				.hasValue("reference[@name='FormatterLogger']/@bind",
						"bindFormatterLogger")
				.doesNotContain("reference[@name='FormatterLogger']/@unbind")
				.doesNotContain("reference[@name='FormatterLogger']/@field")
				.hasOptionalValue(
						"reference[@name='FormatterLogger']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='FormatterLogger']/@policy",
						"static")
				.doesNotContain("reference[@name='FormatterLogger']/@target")

		;
	}

	@Test
	public void testPropertyOrdering14() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
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

	@Test
	public void testConstructorInjection() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				.hasValue("@init", "25")
				.doesNotContain("@activation-fields")
				.hasPropertyValue("prop", "String", "default.prop")

				.hasValue("reference[@name='atleastone']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='atleastone']/@bind")
				.doesNotContain("reference[@name='atleastone']/@unbind")
				.doesNotContain("reference[@name='atleastone']/@field")
				.hasValue("reference[@name='atleastone']/@parameter", "8")
				.hasValue("reference[@name='atleastone']/@cardinality", "1..n")
				.hasOptionalValue("reference[@name='atleastone']/@policy",
						"static")
				.hasOptionalValue(
						"reference[@name='atleastone']/@policy-option",
						"greedy")
				.hasOptionalValue("reference[@name='atleastone']/@scope",
						"bundle")
				.doesNotContain("reference[@name='atleastone']/@target")
				.hasOptionalValue(
						"reference[@name='atleastone']/@field-collection-type",
						"service")

				.hasValue("reference[@name='bundle']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='bundle']/@bind")
				.doesNotContain("reference[@name='bundle']/@unbind")
				.doesNotContain("reference[@name='bundle']/@field")
				.hasValue("reference[@name='bundle']/@parameter", "13")
				.hasOptionalValue("reference[@name='bundle']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='bundle']/@policy", "static")
				.hasOptionalValue("reference[@name='bundle']/@policy-option",
						"greedy")
				.hasOptionalValue("reference[@name='bundle']/@scope", "bundle")
				.doesNotContain("reference[@name='bundle']/@target")
				.doesNotContain(
						"reference[@name='bundle']/@field-collection-type")

				.hasValue("reference[@name='collection_properties']/@interface",
						"java.util.EventListener")
				.doesNotContain(
						"reference[@name='collection_properties']/@bind")
				.doesNotContain(
						"reference[@name='collection_properties']/@unbind")
				.doesNotContain(
						"reference[@name='collection_properties']/@field")
				.hasValue("reference[@name='collection_properties']/@parameter",
						"22")
				.hasValue(
						"reference[@name='collection_properties']/@cardinality",
						"0..n")
				.hasOptionalValue(
						"reference[@name='collection_properties']/@policy",
						"static")
				.hasOptionalValue(
						"reference[@name='collection_properties']/@policy-option",
						"greedy")
				.hasOptionalValue(
						"reference[@name='collection_properties']/@scope",
						"bundle")
				.doesNotContain(
						"reference[@name='collection_properties']/@target")
				.hasValue(
						"reference[@name='collection_properties']/@field-collection-type",
						"properties")

				.hasValue(
						"reference[@name='collection_serviceobjects']/@interface",
						"java.util.EventListener")
				.doesNotContain(
						"reference[@name='collection_serviceobjects']/@bind")
				.doesNotContain(
						"reference[@name='collection_serviceobjects']/@unbind")
				.doesNotContain(
						"reference[@name='collection_serviceobjects']/@field")
				.hasValue(
						"reference[@name='collection_serviceobjects']/@parameter",
						"20")
				.hasValue(
						"reference[@name='collection_serviceobjects']/@cardinality",
						"0..n")
				.hasOptionalValue(
						"reference[@name='collection_serviceobjects']/@policy",
						"static")
				.hasOptionalValue(
						"reference[@name='collection_serviceobjects']/@policy-option",
						"greedy")
				.hasOptionalValue(
						"reference[@name='collection_serviceobjects']/@scope",
						"bundle")
				.doesNotContain(
						"reference[@name='collection_serviceobjects']/@target")
				.hasValue(
						"reference[@name='collection_serviceobjects']/@field-collection-type",
						"serviceobjects")

				.hasValue(
						"reference[@name='collection_reference']/@interface",
						"java.util.EventListener")
				.doesNotContain(
						"reference[@name='collection_reference']/@bind")
				.doesNotContain(
						"reference[@name='collection_reference']/@unbind")
				.doesNotContain(
						"reference[@name='collection_reference']/@field")
				.hasValue(
						"reference[@name='collection_reference']/@parameter",
						"21")
				.hasValue(
						"reference[@name='collection_reference']/@cardinality",
						"0..n")
				.hasOptionalValue(
						"reference[@name='collection_reference']/@policy",
						"static")
				.hasOptionalValue(
						"reference[@name='collection_reference']/@policy-option",
						"greedy")
				.hasOptionalValue(
						"reference[@name='collection_reference']/@scope",
						"bundle")
				.doesNotContain(
						"reference[@name='collection_reference']/@target")
				.hasValue(
						"reference[@name='collection_reference']/@field-collection-type",
						"reference")

				.hasValue("reference[@name='collection_specified']/@interface",
						"java.util.Map")
				.doesNotContain("reference[@name='collection_specified']/@bind")
				.doesNotContain(
						"reference[@name='collection_specified']/@unbind")
				.doesNotContain(
						"reference[@name='collection_specified']/@field")
				.hasValue("reference[@name='collection_specified']/@parameter",
						"24")
				.hasValue(
						"reference[@name='collection_specified']/@cardinality",
						"0..n")
				.hasOptionalValue(
						"reference[@name='collection_specified']/@policy",
						"static")
				.hasOptionalValue(
						"reference[@name='collection_specified']/@policy-option",
						"greedy")
				.hasOptionalValue(
						"reference[@name='collection_specified']/@scope",
						"bundle")
				.doesNotContain(
						"reference[@name='collection_specified']/@target")
				.hasValue(
						"reference[@name='collection_specified']/@field-collection-type",
						"service")

				.hasValue("reference[@name='collection_tuple']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='collection_tuple']/@bind")
				.doesNotContain("reference[@name='collection_tuple']/@unbind")
				.doesNotContain("reference[@name='collection_tuple']/@field")
				.hasValue("reference[@name='collection_tuple']/@parameter",
						"23")
				.hasValue("reference[@name='collection_tuple']/@cardinality",
						"0..n")
				.hasOptionalValue("reference[@name='collection_tuple']/@policy",
						"static")
				.hasOptionalValue(
						"reference[@name='collection_tuple']/@policy-option",
						"greedy")
				.hasOptionalValue("reference[@name='collection_tuple']/@scope",
						"bundle")
				.doesNotContain("reference[@name='collection_tuple']/@target")
				.hasValue(
						"reference[@name='collection_tuple']/@field-collection-type",
						"tuple")

				.hasValue("reference[@name='greedy']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='greedy']/@bind")
				.doesNotContain("reference[@name='greedy']/@unbind")
				.doesNotContain("reference[@name='greedy']/@field")
				.hasValue("reference[@name='greedy']/@parameter", "9")
				.hasOptionalValue("reference[@name='greedy']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='greedy']/@policy", "static")
				.hasOptionalValue("reference[@name='greedy']/@policy-option",
						"greedy")
				.hasOptionalValue("reference[@name='greedy']/@scope", "bundle")
				.doesNotContain("reference[@name='greedy']/@target")
				.doesNotContain(
						"reference[@name='greedy']/@field-collection-type")

				.hasValue("reference[@name='mandatory']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='mandatory']/@bind")
				.doesNotContain("reference[@name='mandatory']/@unbind")
				.doesNotContain("reference[@name='mandatory']/@field")
				.hasValue("reference[@name='mandatory']/@parameter", "5")
				.hasOptionalValue("reference[@name='mandatory']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='mandatory']/@policy",
						"static")
				.hasOptionalValue("reference[@name='mandatory']/@policy-option",
						"greedy")
				.hasOptionalValue("reference[@name='mandatory']/@scope",
						"bundle")
				.doesNotContain("reference[@name='mandatory']/@target")
				.doesNotContain(
						"reference[@name='mandatory']/@field-collection-type")

				.hasValue("reference[@name='multiple']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='multiple']/@bind")
				.doesNotContain("reference[@name='multiple']/@unbind")
				.doesNotContain("reference[@name='multiple']/@field")
				.hasValue("reference[@name='multiple']/@parameter", "7")
				.hasValue("reference[@name='multiple']/@cardinality", "0..n")
				.hasOptionalValue("reference[@name='multiple']/@policy",
						"static")
				.hasOptionalValue("reference[@name='multiple']/@policy-option",
						"greedy")
				.hasOptionalValue("reference[@name='multiple']/@scope",
						"bundle")
				.doesNotContain("reference[@name='multiple']/@target")
				.hasValue("reference[@name='multiple']/@field-collection-type",
						"service")

				.hasValue("reference[@name='optional']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='optional']/@bind")
				.doesNotContain("reference[@name='optional']/@unbind")
				.doesNotContain("reference[@name='optional']/@field")
				.hasValue("reference[@name='optional']/@parameter", "6")
				.hasValue("reference[@name='optional']/@cardinality", "0..1")
				.hasOptionalValue("reference[@name='optional']/@policy",
						"static")
				.hasOptionalValue("reference[@name='optional']/@policy-option",
						"greedy")
				.hasOptionalValue("reference[@name='optional']/@scope",
						"bundle")
				.doesNotContain("reference[@name='optional']/@target")
				.doesNotContain(
						"reference[@name='optional']/@field-collection-type")

				.hasValue("reference[@name='properties']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='properties']/@bind")
				.doesNotContain("reference[@name='properties']/@unbind")
				.doesNotContain("reference[@name='properties']/@field")
				.hasValue("reference[@name='properties']/@parameter", "18")
				.hasOptionalValue("reference[@name='properties']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='properties']/@policy",
						"static")
				.hasOptionalValue(
						"reference[@name='properties']/@policy-option",
						"greedy")
				.hasOptionalValue("reference[@name='properties']/@scope",
						"bundle")
				.doesNotContain("reference[@name='properties']/@target")
				.doesNotContain(
						"reference[@name='properties']/@field-collection-type")

				.hasValue("reference[@name='prototype']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='prototype']/@bind")
				.doesNotContain("reference[@name='prototype']/@unbind")
				.doesNotContain("reference[@name='prototype']/@field")
				.hasValue("reference[@name='prototype']/@parameter", "14")
				.hasOptionalValue("reference[@name='prototype']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='prototype']/@policy",
						"static")
				.hasOptionalValue("reference[@name='prototype']/@policy-option",
						"greedy")
				.hasValue("reference[@name='prototype']/@scope", "prototype")
				.doesNotContain("reference[@name='prototype']/@target")
				.doesNotContain(
						"reference[@name='prototype']/@field-collection-type")

				.hasValue("reference[@name='prototype_required']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='prototype_required']/@bind")
				.doesNotContain("reference[@name='prototype_required']/@unbind")
				.doesNotContain("reference[@name='prototype_required']/@field")
				.hasValue("reference[@name='prototype_required']/@parameter",
						"15")
				.hasOptionalValue(
						"reference[@name='prototype_required']/@cardinality",
						"1..1")
				.hasOptionalValue(
						"reference[@name='prototype_required']/@policy",
						"static")
				.hasOptionalValue(
						"reference[@name='prototype_required']/@policy-option",
						"greedy")
				.hasValue("reference[@name='prototype_required']/@scope",
						"prototype_required")
				.doesNotContain("reference[@name='prototype_required']/@target")
				.doesNotContain(
						"reference[@name='prototype_required']/@field-collection-type")

				.hasValue("reference[@name='reference']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='reference']/@bind")
				.doesNotContain("reference[@name='reference']/@unbind")
				.doesNotContain("reference[@name='reference']/@field")
				.hasValue("reference[@name='reference']/@parameter", "16")
				.hasOptionalValue("reference[@name='reference']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='reference']/@policy",
						"static")
				.hasOptionalValue("reference[@name='reference']/@policy-option",
						"greedy")
				.hasOptionalValue("reference[@name='reference']/@scope",
						"bundle")
				.doesNotContain("reference[@name='reference']/@target")
				.doesNotContain(
						"reference[@name='reference']/@field-collection-type")

				.hasValue("reference[@name='reluctant']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='reluctant']/@bind")
				.doesNotContain("reference[@name='reluctant']/@unbind")
				.doesNotContain("reference[@name='reluctant']/@field")
				.hasValue("reference[@name='reluctant']/@parameter", "10")
				.hasOptionalValue("reference[@name='reluctant']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='reluctant']/@policy",
						"static")
				.hasValue("reference[@name='reluctant']/@policy-option",
						"reluctant")
				.hasOptionalValue("reference[@name='reluctant']/@scope",
						"bundle")
				.doesNotContain("reference[@name='reluctant']/@target")
				.doesNotContain(
						"reference[@name='reluctant']/@field-collection-type")

				.hasValue("reference[@name='replace']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='replace']/@bind")
				.doesNotContain("reference[@name='replace']/@unbind")
				.doesNotContain("reference[@name='replace']/@field")
				.hasValue("reference[@name='replace']/@parameter", "11")
				.hasOptionalValue("reference[@name='replace']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='replace']/@policy",
						"static")
				.hasOptionalValue("reference[@name='replace']/@policy-option",
						"greedy")
				.hasOptionalValue("reference[@name='replace']/@scope", "bundle")
				.doesNotContain("reference[@name='replace']/@target")
				.doesNotContain(
						"reference[@name='replace']/@field-collection-type")

				.hasValue("reference[@name='serviceobjects']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='serviceobjects']/@bind")
				.doesNotContain("reference[@name='serviceobjects']/@unbind")
				.doesNotContain("reference[@name='serviceobjects']/@field")
				.hasValue("reference[@name='serviceobjects']/@parameter", "17")
				.hasOptionalValue(
						"reference[@name='serviceobjects']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='serviceobjects']/@policy",
						"static")
				.hasOptionalValue(
						"reference[@name='serviceobjects']/@policy-option",
						"greedy")
				.hasOptionalValue("reference[@name='serviceobjects']/@scope",
						"bundle")
				.doesNotContain("reference[@name='serviceobjects']/@target")
				.doesNotContain(
						"reference[@name='serviceobjects']/@field-collection-type")

				.hasValue("reference[@name='static']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='static']/@bind")
				.doesNotContain("reference[@name='static']/@unbind")
				.doesNotContain("reference[@name='static']/@field")
				.hasValue("reference[@name='static']/@parameter", "4")
				.hasOptionalValue("reference[@name='static']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='static']/@policy", "static")
				.hasOptionalValue("reference[@name='static']/@policy-option",
						"greedy")
				.hasOptionalValue("reference[@name='static']/@scope", "bundle")
				.doesNotContain("reference[@name='static']/@target")
				.doesNotContain(
						"reference[@name='static']/@field-collection-type")

				.hasValue("reference[@name='target']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='target']/@bind")
				.doesNotContain("reference[@name='target']/@unbind")
				.doesNotContain("reference[@name='target']/@field")
				.hasValue("reference[@name='target']/@parameter", "12")
				.hasOptionalValue("reference[@name='target']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='target']/@policy", "static")
				.hasOptionalValue("reference[@name='target']/@policy-option",
						"greedy")
				.hasOptionalValue("reference[@name='target']/@scope", "bundle")
				.hasValue("reference[@name='target']/@target",
						"(test.attr=foo)")
				.doesNotContain(
						"reference[@name='target']/@field-collection-type")

				.hasValue("reference[@name='tuple']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='tuple']/@bind")
				.doesNotContain("reference[@name='tuple']/@unbind")
				.doesNotContain("reference[@name='tuple']/@field")
				.hasValue("reference[@name='tuple']/@parameter", "19")
				.hasOptionalValue("reference[@name='tuple']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='tuple']/@policy", "static")
				.hasOptionalValue("reference[@name='tuple']/@policy-option",
						"greedy")
				.hasOptionalValue("reference[@name='tuple']/@scope", "bundle")
				.doesNotContain("reference[@name='tuple']/@target")
				.doesNotContain(
						"reference[@name='tuple']/@field-collection-type")

		;
	}
}

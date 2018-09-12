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
public class DSAnnotationsTestCase extends AnnotationsTestCase {

	/**
	 * @throws Exception
	 */
	@Test
	public void testHelloWorld10() throws Exception {
		String name = "org.osgi.impl.bundle.component.annotations.HelloWorld10";
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				.hasNamespace(xmlns_scr100)
				// v1.0.0
				.hasOptionalValue("@immediate", "true")
				.hasOptionalValue("@enabled", "true")
				.doesNotContain("@factory")
				.hasValue("implementation/@class", name)
				.hasCount("implementation", 1)
				.hasCount("service", 0)
				.hasCount("reference", 0)
				// v1.1.0
				.doesNotContain("@configuration-policy")
				.doesNotContain("@activate")
				.doesNotContain("@deactivate")
				.doesNotContain("@modified")
				// v1.2.0
				.doesNotContain("@configuration-pid");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testHelloWorld11() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				.hasNamespace(xmlns_scr110)
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
				.hasValue("@activate", "activator")
				.hasValue("@deactivate", "deactivator")
				.hasValue("@modified", "modified")
				// v1.2.0
				.doesNotContain("@configuration-pid");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testHelloWorld12() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				.hasNamespace(xmlns_scr120)
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
				.hasOptionalValue("@configuration-pid", name);
	}

	@Test
	public void testHelloWorld13() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				.hasNamespace(xmlns_scr130)
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
				.hasOptionalValue("@scope", "singleton");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testService() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.0.0
				.hasOptionalValue("@immediate", "false")
				.hasOptionalValue("@enabled", "true")
				.doesNotContain("@factory")
				.hasCount("implementation", 1)
				.hasValue("implementation/@class",
						"org.osgi.impl.bundle.component.annotations."
								+ name.substring(4))
				.hasCount("service/provide", 1)
				.hasValue("service/provide/@interface",
						"java.util.EventListener")
				.hasCount("reference", 1)
				.hasValue("reference/@name", "Log")
				.hasValue("reference/@interface",
						"org.osgi.service.log.LogService")
				.hasValue("reference/@bind", "bindLog")
				.hasValue("reference/@unbind", "unbindLog")
				.hasOptionalValue("reference/@cardinality", "1..1")
				.hasOptionalValue("reference/@policy", "static")
				.doesNotContain("reference/@target")
				// v1.1.0
				.hasOptionalValue("@configuration-policy", "optional")
				.hasOptionalValue("@activate", "activate")
				.hasOptionalValue("@deactivate", "deactivate")
				.doesNotContain("@modified")
				// v1.2.0
				.hasOptionalValue("@configuration-pid", name)
				.hasOptionalValue("reference/@policy-option", "reluctant")
				.doesNotContain("reference/@updated")
				// v1.3.0
				.hasOptionalValue("@scope", "singleton");
	}

	@Test
	public void testComponentReferences() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.0.0
				.hasOptionalValue("@immediate", "false")
				.hasOptionalValue("@enabled", "true")
				.doesNotContain("@factory")
				.hasCount("implementation", 1)
				.hasValue("implementation/@class",
						"org.osgi.impl.bundle.component.annotations."
								+ name.substring(4))
				.hasCount("service/provide", 1)
				.hasValue("service/provide/@interface",
						"java.util.EventListener")
				.hasOptionalValue("service/@servicefactory", "false")
				.hasCount("reference", 2)
				.hasValue("reference[@name='log1']/@interface",
						"org.osgi.service.log.LogService")
				.doesNotContain("reference[@name='log1']/@bind")
				.doesNotContain("reference[@name='log1']/@unbind")
				.hasOptionalValue("reference[@name='log1']/@cardinality", "1..1")
				.hasOptionalValue("reference[@name='log1']/@policy", "static")
				.doesNotContain("reference[@name='log1']/@target")
				.hasValue("reference[@name='log2']/@interface",
						"org.osgi.service.log.LogService")
				.hasValue("reference[@name='log2']/@bind", "bindLog")
				.hasValue("reference[@name='log2']/@unbind", "unbindLog")
				.hasOptionalValue("reference[@name='log2']/@cardinality", "1..1")
				.hasOptionalValue("reference[@name='log2']/@policy", "static")
				.doesNotContain("reference[@name='log2']/@target")
				// v1.1.0
				.hasOptionalValue("@configuration-policy", "optional")
				.hasOptionalValue("@activate", "activate")
				.hasOptionalValue("@deactivate", "deactivate")
				.doesNotContain("@modified")
				// v1.2.0
				.hasOptionalValue("@configuration-pid", name)
				.hasOptionalValue("reference/@policy-option", "reluctant")
				.doesNotContain("reference/@updated")
				// v1.3.0
				.hasOptionalValue("@scope", "singleton")
				.hasValue("reference[@name='log1']/@field", "logfield")
				.hasOptionalValue("reference[@name='log1']/@field-option",
						"replace")
				.hasOptionalValue("reference[@name='log1']/@scope", "bundle")
				.doesNotContain("reference[@name='log2']/@field")
				.doesNotContain("reference[@name='log2']/@field-option")
				.hasOptionalValue("reference[@name='log2']/@scope", "bundle");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testNoService() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.0.0
				.hasCount("service", 0);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testNoInheritService() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.0.0
				.hasCount("service", 0);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testReferences() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.0.0
				.hasCount("reference", 13)
				.hasValue("reference[@name='static']/@interface",
						"java.util.EventListener")
				.hasValue("reference[@name='static']/@bind", "bindStatic")
				.hasValue("reference[@name='static']/@unbind", "unbindStatic")
				.hasOptionalValue("reference[@name='static']/@cardinality", "1..1")
				.hasOptionalValue("reference[@name='static']/@policy", "static")
				.doesNotContain("reference[@name='static']/@target")
				.hasValue("reference[@name='dynamic']/@interface",
						"java.util.EventListener")
				.hasValue("reference[@name='dynamic']/@bind", "bindDynamic")
				.hasValue("reference[@name='dynamic']/@unbind", "unbindDynamic")
				.hasOptionalValue("reference[@name='dynamic']/@cardinality",
						"1..1")
				.hasValue("reference[@name='dynamic']/@policy", "dynamic")
				.doesNotContain("reference[@name='dynamic']/@target")
				.hasValue("reference[@name='mandatory']/@interface",
						"java.util.EventListener")
				.hasValue("reference[@name='mandatory']/@bind", "bindMandatory")
				.hasValue("reference[@name='mandatory']/@unbind",
						"unbindMandatory")
				.hasOptionalValue("reference[@name='mandatory']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='mandatory']/@policy", "static")
				.doesNotContain("reference[@name='mandatory']/@target")
				.hasValue("reference[@name='optional']/@interface",
						"java.util.EventListener")
				.hasValue("reference[@name='optional']/@bind", "bindOptional")
				.hasValue("reference[@name='optional']/@unbind",
						"unbindOptional")
				.hasValue("reference[@name='optional']/@cardinality", "0..1")
				.hasOptionalValue("reference[@name='optional']/@policy", "static")
				.doesNotContain("reference[@name='optional']/@target")
				.hasValue("reference[@name='multiple']/@interface",
						"java.util.EventListener")
				.hasValue("reference[@name='multiple']/@bind", "bindMultiple")
				.hasValue("reference[@name='multiple']/@unbind",
						"unbindMultiple")
				.hasValue("reference[@name='multiple']/@cardinality", "0..n")
				.hasOptionalValue("reference[@name='multiple']/@policy", "static")
				.doesNotContain("reference[@name='multiple']/@target")
				.hasValue("reference[@name='atleastone']/@interface",
						"java.util.EventListener")
				.hasValue("reference[@name='atleastone']/@bind",
						"bindAtLeastOne")
				.hasValue("reference[@name='atleastone']/@unbind",
						"unbindAtLeastOne")
				.hasValue("reference[@name='atleastone']/@cardinality", "1..n")
				.hasOptionalValue("reference[@name='atleastone']/@policy",
						"static")
				.doesNotContain("reference[@name='atleastone']/@target")
				.hasValue("reference[@name='greedy']/@interface",
						"java.util.EventListener")
				.hasValue("reference[@name='greedy']/@bind", "bindGreedy")
				.hasValue("reference[@name='greedy']/@unbind", "unbindGreedy")
				.hasOptionalValue("reference[@name='greedy']/@cardinality", "1..1")
				.hasOptionalValue("reference[@name='greedy']/@policy", "static")
				.doesNotContain("reference[@name='greedy']/@target")
				.hasValue("reference[@name='reluctant']/@interface",
						"java.util.EventListener")
				.hasValue("reference[@name='reluctant']/@bind", "bindReluctant")
				.hasValue("reference[@name='reluctant']/@unbind",
						"unbindReluctant")
				.hasOptionalValue("reference[@name='reluctant']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='reluctant']/@policy", "static")
				.doesNotContain("reference[@name='reluctant']/@target")
				.hasValue("reference[@name='updated']/@interface",
						"java.util.EventListener")
				.hasValue("reference[@name='updated']/@bind", "bindUpdated")
				.hasValue("reference[@name='updated']/@unbind", "unbindUpdated")
				.hasOptionalValue("reference[@name='updated']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='updated']/@policy", "static")
				.doesNotContain("reference[@name='updated']/@target")
				.hasValue("reference[@name='unbind']/@interface",
						"java.util.EventListener")
				.hasValue("reference[@name='unbind']/@bind", "bindUnbind")
				.hasValue("reference[@name='unbind']/@unbind", "fooUnbind")
				.hasOptionalValue("reference[@name='unbind']/@cardinality", "1..1")
				.hasOptionalValue("reference[@name='unbind']/@policy", "static")
				.doesNotContain("reference[@name='unbind']/@target")
				.hasValue("reference[@name='nounbind']/@interface",
						"java.util.EventListener")
				.hasValue("reference[@name='nounbind']/@bind", "bindNoUnbind")
				.doesNotContain("reference[@name='nounbind']/@unbind")
				.hasOptionalValue("reference[@name='nounbind']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='nounbind']/@policy", "static")
				.doesNotContain("reference[@name='nounbind']/@target")
				.hasValue("reference[@name='missingunbind']/@interface",
						"java.util.EventListener")
				.hasValue("reference[@name='missingunbind']/@bind",
						"bindMissingUnbind")
				.doesNotContain("reference[@name='missingunbind']/@unbind")
				.hasOptionalValue("reference[@name='missingunbind']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='missingunbind']/@policy",
						"static")
				.doesNotContain("reference[@name='missingunbind']/@target")
				.hasValue("reference[@name='target']/@interface",
						"java.util.EventListener")
				.hasValue("reference[@name='target']/@bind", "bindTarget")
				.hasValue("reference[@name='target']/@unbind", "unbindTarget")
				.hasOptionalValue("reference[@name='target']/@cardinality", "1..1")
				.hasOptionalValue("reference[@name='target']/@policy", "static")
				.hasValue("reference[@name='target']/@target",
						"(test.attr=foo)")
				// v1.2.0
				.hasOptionalValue("reference[@name='static']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='static']/@updated")
				.hasOptionalValue("reference[@name='dynamic']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='dynamic']/@updated")
				.hasOptionalValue("reference[@name='mandatory']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='mandatory']/@updated")
				.hasOptionalValue("reference[@name='optional']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='optional']/@updated")
				.hasOptionalValue("reference[@name='multiple']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='multiple']/@updated")
				.hasOptionalValue("reference[@name='atleastone']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='atleastone']/@updated")
				.hasValue("reference[@name='greedy']/@policy-option", "greedy")
				.doesNotContain("reference[@name='greedy']/@updated")
				.hasOptionalValue("reference[@name='reluctant']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='reluctant']/@updated")
				.hasOptionalValue("reference[@name='updated']/@policy-option",
						"reluctant")
				.hasValue("reference[@name='updated']/@updated",
						"updatedUpdated")
				.hasOptionalValue("reference[@name='unbind']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='unbind']/@updated")
				.hasOptionalValue("reference[@name='nounbind']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='nounbind']/@updated")
				.hasOptionalValue(
						"reference[@name='missingunbind']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='missingunbind']/@updated")
				.hasOptionalValue("reference[@name='target']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='target']/@updated");
	}

	@Test
	public void testFieldReferences() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.0.0
				.hasCount("reference", 23)

				.hasValue("reference[@name='static']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='static']/@bind")
				.doesNotContain("reference[@name='static']/@unbind")
				.hasOptionalValue("reference[@name='static']/@cardinality", "1..1")
				.hasOptionalValue("reference[@name='static']/@policy", "static")
				.doesNotContain("reference[@name='static']/@target")

				.hasValue("reference[@name='dynamic']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='dynamic']/@bind")
				.doesNotContain("reference[@name='dynamic']/@unbind")
				.hasOptionalValue("reference[@name='dynamic']/@cardinality",
						"1..1")
				.hasValue("reference[@name='dynamic']/@policy", "dynamic")
				.doesNotContain("reference[@name='dynamic']/@target")

				.hasValue("reference[@name='mandatory']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='mandatory']/@bind")
				.doesNotContain("reference[@name='mandatory']/@unbind")
				.hasOptionalValue("reference[@name='mandatory']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='mandatory']/@policy", "static")
				.doesNotContain("reference[@name='mandatory']/@target")

				.hasValue("reference[@name='optional']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='optional']/@bind")
				.doesNotContain("reference[@name='optional']/@unbind")
				.hasValue("reference[@name='optional']/@cardinality", "0..1")
				.hasOptionalValue("reference[@name='optional']/@policy", "static")
				.doesNotContain("reference[@name='optional']/@target")

				.hasValue("reference[@name='multiple']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='multiple']/@bind")
				.doesNotContain("reference[@name='multiple']/@unbind")
				.hasValue("reference[@name='multiple']/@cardinality", "0..n")
				.hasOptionalValue("reference[@name='multiple']/@policy", "static")
				.doesNotContain("reference[@name='multiple']/@target")

				.hasValue("reference[@name='atleastone']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='atleastone']/@bind")
				.doesNotContain("reference[@name='atleastone']/@unbind")
				.hasValue("reference[@name='atleastone']/@cardinality", "1..n")
				.hasOptionalValue("reference[@name='atleastone']/@policy",
						"static")
				.doesNotContain("reference[@name='atleastone']/@target")

				.hasValue("reference[@name='greedy']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='greedy']/@bind")
				.doesNotContain("reference[@name='greedy']/@unbind")
				.hasOptionalValue("reference[@name='greedy']/@cardinality", "1..1")
				.hasOptionalValue("reference[@name='greedy']/@policy", "static")
				.doesNotContain("reference[@name='greedy']/@target")

				.hasValue("reference[@name='reluctant']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='reluctant']/@bind")
				.doesNotContain("reference[@name='reluctant']/@unbind")
				.hasOptionalValue("reference[@name='reluctant']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='reluctant']/@policy", "static")
				.hasValue("reference[@name='reluctant']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='reluctant']/@target")

				.hasValue("reference[@name='update']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='update']/@bind")
				.doesNotContain("reference[@name='update']/@unbind")
				.hasOptionalValue("reference[@name='update']/@cardinality", "1..1")
				.hasOptionalValue("reference[@name='update']/@policy", "static")
				.doesNotContain("reference[@name='update']/@target")

				.hasValue("reference[@name='replace']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='replace']/@bind")
				.doesNotContain("reference[@name='replace']/@unbind")
				.hasOptionalValue("reference[@name='replace']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='replace']/@policy", "static")
				.doesNotContain("reference[@name='replace']/@target")

				.hasValue("reference[@name='target']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='target']/@bind")
				.doesNotContain("reference[@name='target']/@unbind")
				.hasOptionalValue("reference[@name='target']/@cardinality", "1..1")
				.hasOptionalValue("reference[@name='target']/@policy", "static")
				.hasValue("reference[@name='target']/@target",
						"(test.attr=foo)")

				.hasValue("reference[@name='bundle']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='bundle']/@bind")
				.doesNotContain("reference[@name='bundle']/@unbind")
				.hasOptionalValue("reference[@name='bundle']/@cardinality", "1..1")
				.hasValue("reference[@name='bundle']/@policy", "dynamic")
				.doesNotContain("reference[@name='bundle']/@target")

				.hasValue("reference[@name='prototype']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='prototype']/@bind")
				.doesNotContain("reference[@name='prototype']/@unbind")
				.hasOptionalValue("reference[@name='prototype']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='prototype']/@policy", "static")
				.doesNotContain("reference[@name='prototype']/@target")

				.hasValue("reference[@name='prototype_required']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='prototype_required']/@bind")
				.doesNotContain("reference[@name='prototype_required']/@unbind")
				.hasOptionalValue(
						"reference[@name='prototype_required']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='prototype_required']/@policy",
						"static")
				.doesNotContain("reference[@name='prototype_required']/@target")

				.hasValue("reference[@name='reference']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='reference']/@bind")
				.doesNotContain("reference[@name='reference']/@unbind")
				.hasOptionalValue("reference[@name='reference']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='reference']/@policy", "static")
				.doesNotContain("reference[@name='reference']/@target")

				.hasValue("reference[@name='serviceobjects']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='serviceobjects']/@bind")
				.doesNotContain("reference[@name='serviceobjects']/@unbind")
				.hasOptionalValue("reference[@name='serviceobjects']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='serviceobjects']/@policy",
						"static")
				.doesNotContain("reference[@name='serviceobjects']/@target")

				.hasValue("reference[@name='properties']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='properties']/@bind")
				.doesNotContain("reference[@name='properties']/@unbind")
				.hasOptionalValue("reference[@name='properties']/@cardinality",
						"1..1")
				.hasValue("reference[@name='properties']/@policy", "dynamic")
				.doesNotContain("reference[@name='properties']/@target")

				.hasValue("reference[@name='tuple']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='tuple']/@bind")
				.doesNotContain("reference[@name='tuple']/@unbind")
				.hasOptionalValue("reference[@name='tuple']/@cardinality", "1..1")
				.hasOptionalValue("reference[@name='tuple']/@policy", "static")
				.doesNotContain("reference[@name='tuple']/@target")

				.hasValue("reference[@name='collection_reference']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='collection_reference']/@bind")
				.doesNotContain(
						"reference[@name='collection_reference']/@unbind")
				.hasOptionalValue(
						"reference[@name='collection_reference']/@cardinality",
						"0..n")
				.hasOptionalValue(
						"reference[@name='collection_reference']/@policy",
						"dynamic")
				.doesNotContain(
						"reference[@name='collection_reference']/@target")

				.hasValue(
						"reference[@name='collection_serviceobjects']/@interface",
						"java.util.EventListener")
				.doesNotContain(
						"reference[@name='collection_serviceobjects']/@bind")
				.doesNotContain(
						"reference[@name='collection_serviceobjects']/@unbind")
				.hasOptionalValue(
						"reference[@name='collection_serviceobjects']/@cardinality",
						"0..n")
				.hasOptionalValue(
						"reference[@name='collection_serviceobjects']/@policy",
						"static")
				.doesNotContain(
						"reference[@name='collection_serviceobjects']/@target")

				.hasValue("reference[@name='collection_properties']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='collection_properties']/@bind")
				.doesNotContain(
						"reference[@name='collection_properties']/@unbind")
				.hasOptionalValue(
						"reference[@name='collection_properties']/@cardinality",
						"0..n")
				.hasOptionalValue(
						"reference[@name='collection_properties']/@policy",
						"static")
				.doesNotContain(
						"reference[@name='collection_properties']/@target")

				.hasValue("reference[@name='collection_tuple']/@interface",
						"java.util.EventListener")
				.doesNotContain("reference[@name='collection_tuple']/@bind")
				.doesNotContain("reference[@name='collection_tuple']/@unbind")
				.hasOptionalValue(
						"reference[@name='collection_tuple']/@cardinality",
						"0..n")
				.hasOptionalValue("reference[@name='collection_tuple']/@policy",
						"static")
				.doesNotContain("reference[@name='collection_tuple']/@target")

				.hasValue("reference[@name='collection_specified']/@interface",
						"java.util.Map")
				.doesNotContain("reference[@name='collection_specified']/@bind")
				.doesNotContain(
						"reference[@name='collection_specified']/@unbind")
				.hasOptionalValue(
						"reference[@name='collection_specified']/@cardinality",
						"0..n")
				.hasOptionalValue(
						"reference[@name='collection_specified']/@policy",
						"static")
				.doesNotContain(
						"reference[@name='collection_specified']/@target")

				// v1.2.0
				.hasOptionalValue("reference[@name='static']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='static']/@updated")

				.hasOptionalValue("reference[@name='dynamic']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='dynamic']/@updated")

				.hasOptionalValue("reference[@name='mandatory']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='mandatory']/@updated")

				.hasOptionalValue("reference[@name='optional']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='optional']/@updated")

				.hasOptionalValue("reference[@name='multiple']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='multiple']/@updated")

				.hasOptionalValue("reference[@name='atleastone']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='atleastone']/@updated")

				.hasValue("reference[@name='greedy']/@policy-option", "greedy")
				.doesNotContain("reference[@name='greedy']/@updated")

				.hasOptionalValue("reference[@name='reluctant']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='reluctant']/@updated")

				.hasOptionalValue("reference[@name='update']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='update']/@update")

				.hasOptionalValue("reference[@name='replace']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='replace']/@update")

				.hasOptionalValue("reference[@name='target']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='target']/@updated")

				.hasOptionalValue("reference[@name='bundle']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='bundle']/@updated")

				.hasOptionalValue("reference[@name='prototype']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='prototype']/@updated")

				.hasOptionalValue(
						"reference[@name='prototype_required']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='prototype_required']/@updated")

				.hasOptionalValue("reference[@name='reference']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='reference']/@updated")

				.hasOptionalValue(
						"reference[@name='serviceobjects']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='serviceobjects']/@updated")

				.hasOptionalValue("reference[@name='properties']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='properties']/@updated")

				.hasOptionalValue("reference[@name='tuple']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='tuple']/@updated")

				.hasOptionalValue(
						"reference[@name='collection_reference']/@policy-option",
						"reluctant")
				.doesNotContain(
						"reference[@name='collection_reference']/@updated")

				.hasOptionalValue(
						"reference[@name='collection_serviceobjects']/@policy-option",
						"reluctant")
				.doesNotContain(
						"reference[@name='collection_serviceobjects']/@updated")

				.hasOptionalValue(
						"reference[@name='collection_properties']/@policy-option",
						"reluctant")
				.doesNotContain(
						"reference[@name='collection_properties']/@updated")

				.hasOptionalValue(
						"reference[@name='collection_tuple']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='collection_tuple']/@updated")

				.hasOptionalValue(
						"reference[@name='collection_specified']/@policy-option",
						"reluctant")
				.doesNotContain(
						"reference[@name='collection_specified']/@updated")

				// v1.3.0
				.hasValue("reference[@name='static']/@field", "fieldStatic")
				.hasOptionalValue("reference[@name='static']/@field-option",
						"replace")
				.doesNotContain(
						"reference[@name='static']/@field-collection-type")
				.hasOptionalValue("reference[@name='static']/@scope", "bundle")

				.hasValue("reference[@name='dynamic']/@field", "fieldDynamic")
				.hasOptionalValue("reference[@name='dynamic']/@field-option",
						"replace")
				.doesNotContain(
						"reference[@name='dynamic']/@field-collection-type")
				.hasOptionalValue("reference[@name='dynamic']/@scope", "bundle")

				.hasValue("reference[@name='mandatory']/@field",
						"fieldMandatory")
				.hasOptionalValue("reference[@name='mandatory']/@field-option",
						"replace")
				.doesNotContain(
						"reference[@name='mandatory']/@field-collection-type")
				.hasOptionalValue("reference[@name='mandatory']/@scope", "bundle")

				.hasValue("reference[@name='optional']/@field", "fieldOptional")
				.hasOptionalValue("reference[@name='optional']/@field-option",
						"replace")
				.doesNotContain(
						"reference[@name='optional']/@field-collection-type")
				.hasOptionalValue("reference[@name='optional']/@scope", "bundle")

				.hasValue("reference[@name='multiple']/@field", "fieldMultiple")
				.hasOptionalValue("reference[@name='multiple']/@field-option",
						"replace")
				.hasOptionalValue(
						"reference[@name='multiple']/@field-collection-type",
						"service")
				.hasOptionalValue("reference[@name='multiple']/@scope", "bundle")

				.hasValue("reference[@name='atleastone']/@field",
						"fieldAtLeastOne")
				.hasOptionalValue("reference[@name='atleastone']/@field-option",
						"replace")
				.hasOptionalValue(
						"reference[@name='atleastone']/@field-collection-type",
						"service")
				.hasOptionalValue("reference[@name='atleastone']/@scope", "bundle")

				.hasValue("reference[@name='greedy']/@field", "fieldGreedy")
				.hasOptionalValue("reference[@name='greedy']/@field-option",
						"replace")
				.doesNotContain(
						"reference[@name='greedy']/@field-collection-type")
				.hasOptionalValue("reference[@name='greedy']/@scope", "bundle")

				.hasValue("reference[@name='reluctant']/@field",
						"fieldReluctant")
				.hasOptionalValue("reference[@name='reluctant']/@field-option",
						"replace")
				.doesNotContain(
						"reference[@name='reluctant']/@field-collection-type")
				.hasOptionalValue("reference[@name='reluctant']/@scope", "bundle")

				.hasValue("reference[@name='update']/@field", "fieldUpdate")

				.hasValue("reference[@name='update']/@field-option", "update")
				.doesNotContain(
						"reference[@name='update']/@field-collection-type")
				.hasOptionalValue("reference[@name='update']/@scope", "bundle")

				.hasValue("reference[@name='replace']/@field", "fieldReplace")
				.hasOptionalValue("reference[@name='replace']/@field-option",
						"replace")
				.doesNotContain(
						"reference[@name='replace']/@field-collection-type")
				.hasOptionalValue("reference[@name='replace']/@scope", "bundle")

				.hasValue("reference[@name='target']/@field", "fieldTarget")
				.hasOptionalValue("reference[@name='target']/@field-option",
						"replace")
				.doesNotContain(
						"reference[@name='target']/@field-collection-type")
				.hasOptionalValue("reference[@name='target']/@scope", "bundle")

				.hasValue("reference[@name='bundle']/@field", "fieldBundle")
				.hasOptionalValue("reference[@name='bundle']/@field-option",
						"replace")
				.doesNotContain(
						"reference[@name='bundle']/@field-collection-type")
				.hasOptionalValue("reference[@name='bundle']/@scope", "bundle")

				.hasValue("reference[@name='prototype']/@field",
						"fieldPrototype")
				.hasOptionalValue("reference[@name='prototype']/@field-option",
						"replace")
				.doesNotContain(
						"reference[@name='prototype']/@field-collection-type")
				.hasValue("reference[@name='prototype']/@scope", "prototype")

				.hasValue("reference[@name='prototype_required']/@field",
						"fieldPrototypeRequired")
				.hasOptionalValue(
						"reference[@name='prototype_required']/@field-option",
						"replace")
				.doesNotContain(
						"reference[@name='prototype_required']/@field-collection-type")
				.hasValue("reference[@name='prototype_required']/@scope",
						"prototype_required")

				.hasValue("reference[@name='reference']/@field",
						"fieldReference")
				.hasOptionalValue("reference[@name='reference']/@field-option",
						"replace")
				.doesNotContain(
						"reference[@name='reference']/@field-collection-type")
				.hasOptionalValue("reference[@name='reference']/@scope", "bundle")

				.hasValue("reference[@name='serviceobjects']/@field",
						"fieldServiceObjects")
				.hasOptionalValue(
						"reference[@name='serviceobjects']/@field-option",
						"replace")
				.doesNotContain(
						"reference[@name='serviceobjects']/@field-collection-type")
				.hasOptionalValue("reference[@name='serviceobjects']/@scope",
						"bundle")

				.hasValue("reference[@name='properties']/@field",
						"fieldProperties")
				.hasOptionalValue("reference[@name='properties']/@field-option",
						"replace")
				.doesNotContain(
						"reference[@name='properties']/@field-collection-type")
				.hasOptionalValue("reference[@name='properties']/@scope", "bundle")

				.hasValue("reference[@name='tuple']/@field", "fieldTuple")
				.hasOptionalValue("reference[@name='tuple']/@field-option",
						"replace")
				.doesNotContain(
						"reference[@name='tuple']/@field-collection-type")
				.hasOptionalValue("reference[@name='tuple']/@scope", "bundle")

				.hasValue("reference[@name='collection_reference']/@field",
						"fieldReferenceM")
				.hasValue(
						"reference[@name='collection_reference']/@field-option",
						"update")
				.hasValue(
						"reference[@name='collection_reference']/@field-collection-type",
						"reference")
				.hasOptionalValue("reference[@name='collection_reference']/@scope",
						"bundle")

				.hasValue("reference[@name='collection_serviceobjects']/@field",
						"fieldServiceObjectsM")
				.hasOptionalValue(
						"reference[@name='collection_serviceobjects']/@field-option",
						"replace")
				.hasValue(
						"reference[@name='collection_serviceobjects']/@field-collection-type",
						"serviceobjects")
				.hasOptionalValue(
						"reference[@name='collection_serviceobjects']/@scope",
						"bundle")

				.hasValue("reference[@name='collection_properties']/@field",
						"fieldPropertiesM")
				.hasOptionalValue(
						"reference[@name='collection_properties']/@field-option",
						"replace")
				.hasValue(
						"reference[@name='collection_properties']/@field-collection-type",
						"properties")
				.hasOptionalValue(
						"reference[@name='collection_properties']/@scope",
						"bundle")

				.hasValue("reference[@name='collection_tuple']/@field",
						"fieldTupleM")
				.hasOptionalValue(
						"reference[@name='collection_tuple']/@field-option",
						"replace")
				.hasValue(
						"reference[@name='collection_tuple']/@field-collection-type",
						"tuple")
				.hasOptionalValue("reference[@name='collection_tuple']/@scope",
						"bundle")

				.hasValue("reference[@name='collection_specified']/@field",
						"fieldServiceM")
				.hasOptionalValue(
						"reference[@name='collection_specified']/@field-option",
						"replace")
				.hasValue(
						"reference[@name='collection_specified']/@field-collection-type",
						"service")
				.hasOptionalValue(
						"reference[@name='collection_specified']/@scope",
						"bundle");
	}

	@Test
	public void testReferenceScopes() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.0.0
				.hasCount("reference", 3)

				.hasValue("reference[@name='bundle']/@interface",
						"java.util.EventListener")
				.hasValue("reference[@name='bundle']/@bind", "bindBundle")
				.hasValue("reference[@name='bundle']/@unbind", "unbindBundle")
				.hasOptionalValue("reference[@name='bundle']/@cardinality", "1..1")

				.hasOptionalValue("reference[@name='bundle']/@policy", "static")

				.doesNotContain("reference[@name='bundle']/@target")

				.hasValue("reference[@name='prototype']/@interface",
						"java.util.EventListener")
				.hasValue("reference[@name='prototype']/@bind", "bindPrototype")
				.hasValue("reference[@name='prototype']/@unbind",
						"unbindPrototype")
				.hasOptionalValue("reference[@name='prototype']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='prototype']/@policy", "static")

				.doesNotContain("reference[@name='prototype']/@target")

				.hasValue("reference[@name='prototype_required']/@interface",
						"java.util.EventListener")
				.hasValue("reference[@name='prototype_required']/@bind",
						"bindPrototypeRequired")
				.hasValue("reference[@name='prototype_required']/@unbind",
						"unbindPrototypeRequired")
				.hasOptionalValue(
						"reference[@name='prototype_required']/@cardinality",
						"1..1")
				.hasOptionalValue("reference[@name='prototype_required']/@policy",
						"static")

				.doesNotContain("reference[@name='prototype_required']/@target")

				// v1.2.0
				.hasOptionalValue("reference[@name='bundle']/@policy-option",
						"reluctant")

				.doesNotContain("reference[@name='bundle']/@updated")

				.hasOptionalValue("reference[@name='prototype']/@policy-option",
						"reluctant")

				.doesNotContain("reference[@name='prototype']/@updated")

				.hasOptionalValue(
						"reference[@name='prototype_required']/@policy-option",
						"reluctant")
				.doesNotContain("reference[@name='prototype_required']/@updated")

				// v1.3.0

				.doesNotContain("reference[@name='bundle']/@field")

				.doesNotContain("reference[@name='bundle']/@field-option")
				.doesNotContain(
						"reference[@name='bundle']/@field-collection-type")

				.hasOptionalValue("reference[@name='bundle']/@scope", "bundle")

				.doesNotContain("reference[@name='prototype']/@field")

				.doesNotContain("reference[@name='prototype']/@field-option")
				.doesNotContain(
						"reference[@name='prototype']/@field-collection-type")
				.hasValue("reference[@name='prototype']/@scope", "prototype")

				.doesNotContain("reference[@name='prototype_required']/@field")
				.doesNotContain(
						"reference[@name='prototype_required']/@field-option")
				.doesNotContain(
						"reference[@name='prototype_required']/@field-collection-type")
				.hasValue("reference[@name='prototype_required']/@scope",
						"prototype_required");

	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testReferenceNames() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.0.0
				.hasCount("reference", 4)

				.hasValue("reference[@name='NameAdd']/@bind", "addNameAdd")
				.hasValue("reference[@name='NameAdd']/@unbind", "removeNameAdd")

				.hasValue("reference[@name='NameSet']/@bind", "setNameSet")
				.hasValue("reference[@name='NameSet']/@unbind", "unsetNameSet")

				.hasValue("reference[@name='name']/@bind", "name")
				.hasValue("reference[@name='name']/@unbind", "unname")

				.hasValue("reference[@name='NameBind']/@bind", "bindNameBind")
				.hasValue("reference[@name='NameBind']/@unbind",
						"unbindNameBind");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testReferenceService() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.0.0
				.hasValue("reference/@interface", "java.util.EventListener")
				.hasValue("reference/@bind", "bindObject");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testServiceFactory() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.0.0
				.hasOptionalValue("@immediate", "false")
				.hasOptionalValue("@enabled", "true")
				.doesNotContain("@factory")
				.hasCount("implementation", 1)
				.hasValue("implementation/@class",
						"org.osgi.impl.bundle.component.annotations."
								+ name.substring(4))
				.hasCount("service/provide", 1)
				.hasValue("service/provide/@interface",
						"java.util.EventListener")
				.hasOptionalValue("service/@servicefactory", "true")
				.hasCount("reference", 0)
				// v1.1.0
				.hasOptionalValue("@configuration-policy", "optional")
				.hasOptionalValue("@activate", "activate")
				.hasOptionalValue("@deactivate", "deactivate")
				.doesNotContain("@modified")
				// v1.2.0
				.hasOptionalValue("@configuration-pid", name)
				.hasOptionalValue("reference/@policy-option", "reluctant")
				.doesNotContain("reference/@updated")
				// v1.3.0
				.hasOptionalValue("service/@scope", "bundle")
				.containsAnyOf("service/@servicefactory", "service/@scope");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testNoServiceFactory() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.0.0
				.hasOptionalValue("@immediate", "false")
				.hasOptionalValue("@enabled", "true")
				.doesNotContain("@factory")
				.hasCount("implementation", 1)
				.hasValue("implementation/@class",
						"org.osgi.impl.bundle.component.annotations."
								+ name.substring(4))
				.hasCount("service/provide", 1)
				.hasValue("service/provide/@interface",
						"java.util.EventListener")
				.hasOptionalValue("service/@servicefactory", "false")
				.hasCount("reference", 0)
				// v1.1.0
				.hasOptionalValue("@configuration-policy", "optional")
				.hasOptionalValue("@activate", "activate")
				.hasOptionalValue("@deactivate", "deactivate")
				.doesNotContain("@modified")
				// v1.2.0
				.hasOptionalValue("@configuration-pid", name)
				.hasOptionalValue("reference/@policy-option", "reluctant")
				.doesNotContain("reference/@updated")
				// v1.3.0
				.hasOptionalValue("service/@scope", "singleton");
	}

	@Test
	public void testServiceSingleton() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.0.0
				.hasOptionalValue("@immediate", "false")
				.hasOptionalValue("@enabled", "true")
				.doesNotContain("@factory")
				.hasCount("implementation", 1)
				.hasValue("implementation/@class",
						"org.osgi.impl.bundle.component.annotations."
								+ name.substring(4))
				.hasCount("service/provide", 1)
				.hasValue("service/provide/@interface",
						"java.util.EventListener")
				.hasOptionalValue("service/@servicefactory", "false")
				.hasCount("reference", 0)
				// v1.1.0
				.hasOptionalValue("@configuration-policy", "optional")
				.hasOptionalValue("@activate", "activate")
				.hasOptionalValue("@deactivate", "deactivate")
				.doesNotContain("@modified")
				// v1.2.0
				.hasOptionalValue("@configuration-pid", name)
				.hasOptionalValue("reference/@policy-option", "reluctant")
				.doesNotContain("reference/@updated")
				// v1.3.0
				.hasOptionalValue("service/@scope", "singleton");
	}

	@Test
	public void testServiceBundle() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.0.0
				.hasOptionalValue("@immediate", "false")
				.hasOptionalValue("@enabled", "true")
				.doesNotContain("@factory")
				.hasCount("implementation", 1)
				.hasValue("implementation/@class",
						"org.osgi.impl.bundle.component.annotations."
								+ name.substring(4))
				.hasCount("service/provide", 1)
				.hasValue("service/provide/@interface",
						"java.util.EventListener")
				.hasOptionalValue("service/@servicefactory", "true")
				.hasCount("reference", 0)
				// v1.1.0
				.hasOptionalValue("@configuration-policy", "optional")
				.hasOptionalValue("@activate", "activate")
				.hasOptionalValue("@deactivate", "deactivate")
				.doesNotContain("@modified")
				// v1.2.0
				.hasOptionalValue("@configuration-pid", name)
				.hasOptionalValue("reference/@policy-option", "reluctant")
				.doesNotContain("reference/@updated")
				// v1.3.0
				.hasOptionalValue("service/@scope", "bundle")
				.containsAnyOf("service/@servicefactory", "service/@scope");
	}

	@Test
	public void testServicePrototype() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.0.0
				.hasOptionalValue("@immediate", "false")
				.hasOptionalValue("@enabled", "true")
				.doesNotContain("@factory")
				.hasCount("implementation", 1)
				.hasValue("implementation/@class",
						"org.osgi.impl.bundle.component.annotations."
								+ name.substring(4))
				.hasCount("service/provide", 1)
				.hasValue("service/provide/@interface",
						"java.util.EventListener")
				.hasOptionalValue("service/@servicefactory", "false")
				.hasCount("reference", 0)
				// v1.1.0
				.hasOptionalValue("@configuration-policy", "optional")
				.hasOptionalValue("@activate", "activate")
				.hasOptionalValue("@deactivate", "deactivate")
				.doesNotContain("@modified")
				// v1.2.0
				.hasOptionalValue("@configuration-pid", name)
				.hasOptionalValue("reference/@policy-option", "reluctant")
				.doesNotContain("reference/@updated")
				// v1.3.0
				.hasValue("service/@scope", "prototype");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testFactory() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.0.0
				.hasOptionalValue("@immediate", "false")
				.hasOptionalValue("@enabled", "true")
				.hasValue("@factory", "test.factory.name")
				.hasCount("implementation", 1)
				.hasValue("implementation/@class",
						"org.osgi.impl.bundle.component.annotations."
								+ name.substring(4))
				.hasCount("service/provide", 1)
				.hasValue("service/provide/@interface",
						"java.util.EventListener")
				.hasOptionalValue("service/@servicefactory", "false")
				.hasCount("reference", 0)
				// v1.1.0
				.hasOptionalValue("@configuration-policy", "optional")
				.hasOptionalValue("@activate", "activate")
				.hasOptionalValue("@deactivate", "deactivate")
				.doesNotContain("@modified")
				// v1.2.0
				.hasOptionalValue("@configuration-pid", name)
				.hasOptionalValue("reference/@policy-option", "reluctant")
				.doesNotContain("reference/@updated")
				// v1.3.0
				.hasOptionalValue("service/@scope", "singleton");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testImmediate() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.0.0
				.hasValue("@immediate", "true")
				.hasOptionalValue("@enabled", "true");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testDelayed() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.0.0
				.hasValue("@immediate", "false")
				.hasOptionalValue("@enabled", "true");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testEnabled() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.0.0
				.hasOptionalValue("@immediate", "true")
				.hasValue("@enabled", "true");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testDisabled() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.0.0
				.hasOptionalValue("@immediate", "true")
				.hasValue("@enabled", "false");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testConfigPolicyOptional() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.1.0
				.hasOptionalValue("@configuration-policy", "optional");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testConfigPolicyRequire() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.1.0
				.hasValue("@configuration-policy", "require");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testConfigPolicyIgnore() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.1.0
				.hasValue("@configuration-policy", "ignore");
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testConfigPid() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.2.0
				.hasValue("@configuration-pid", "test.config.pid");
	}

	@Test
	public void testConfigPidMultiple() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.3.0
				.hasValue("@configuration-pid[normalize-space()]",
						"test.config.pid " + name);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testProperties() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.0.0
				.hasCount("properties", 1)
				.hasValue("properties/@entry", "OSGI-INF/vendor.properties")
				.hasCount("property", 10)
				.hasPropertyValue("a", "String", "foo")
				.hasPropertyArrayValue("b", "Integer", "2", "3")
				.hasPropertyValue("c", "Boolean", "true")
				.hasPropertyValue("d", "Long", "4")
				.hasPropertyValue("e", "Double", "5.0")
				.hasPropertyValue("f", "Float", "6.0")
				.hasPropertyValue("g", "Byte", "7")
				.hasPropertyValue("h", "Character", "8")
				.hasPropertyValue("i", "Short", "9")
				.hasPropertyValue("j", "String", "bar");
	}

	@Test
	public void testPropertyOrdering() throws Exception {
		String name = testName.getMethodName();
		Description description = descriptions.get(name);
		assertThat(description).as("component %s", name)
				// v1.0.0
				.hasCount("properties", 1)
				.hasValue("properties/@entry", "OSGI-INF/vendor.properties")
				.hasCount("property", 29)
				.hasValue("@activate", "activate1")
				.hasValue("@modified", "modified2")
				.hasValue("@deactivate", "deactivate3")

				.hasPropertyValue("config1", "String", "config1")
				.hasPropertyValue("config2", "String", "config2")
				.hasPropertyValue("config3", "String", "config3")
				.hasPropertyValue("config4", "String", "config4")

				.hasPropertyValue("string1", "String", "config/string1")
				.hasPropertyValue("string2", "String", "config/string2")
				.hasPropertyValue("string3", "String", "config/string3")
				.hasPropertyValue("string4", "String", "config/string4")

				.hasPropertyArrayValue("stringarray1", "String",
						"config/1stringarray1", "config/2stringarray1")

				.hasPropertyValue("boolean1", "Boolean", "true")
				.hasPropertyArrayValue("booleanarray1", "Boolean", "true",
						"false")

				.hasPropertyValue("char1", "Character", "64")
				.hasPropertyArrayValue("chararray1", "Character", "64", "43")

				.hasPropertyValue("byte1", "Byte", "2")
				.hasPropertyArrayValue("bytearray1", "Byte", "2", "-3")

				.hasPropertyValue("short1", "Short", "1034")
				.hasPropertyArrayValue("shortarray1", "Short", "1034", "-1043")

				.hasPropertyValue("int1", "Integer", "123456")
				.hasPropertyArrayValue("intarray1", "Integer", "123456",
						"-234567")

				.hasPropertyValue("long1", "Long", "9876543210")
				.hasPropertyArrayValue("longarray1", "Long", "9876543210",
						"-987654321")

				.hasPropertyValue("float1", "Float", "3.14")
				.hasPropertyArrayValue("floatarray1", "Float", "3.14", "-4.56")

				.hasPropertyValue("double1", "Double", "2.1")
				.hasPropertyArrayValue("doublearray1", "Double", "2.1", "-1.2")

				.hasPropertyValue("class1", "String",
						"org.osgi.impl.bundle.component.annotations.TestEnum")
				.hasPropertyArrayValue("classarray1", "String",
						"org.osgi.impl.bundle.component.annotations.TestEnum",
						"java.lang.Object")

				.hasPropertyValue("enum1", "String", "ITEM1")
				.hasPropertyArrayValue("enumarray1", "String", "ITEM1",
						"ITEM2");

	}

}

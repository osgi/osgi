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

package org.osgi.test.cases.metatype.annotations.junit;

import static org.osgi.test.cases.metatype.annotations.junit.OCDXPathAssert.assertThat;
import static org.osgi.test.support.xpath.XPathAssert.assertThat;

import java.util.Arrays;

import org.junit.Test;

/**
 * @author $Id$
 */
public class MetatypeAnnotationsTestCase extends AnnotationsTestCase {

	@Test
	public void testConfigurationPropertyType() throws Exception {
		String name = testName.getMethodName();
		OCD ocd = ocds.get(name);
		assertThat(ocd).as("OCD %s", name)
				.hasValue("../@localization", "OSGI-INF/l10n/" + getSelf(ocd))
				.hasValue("@description", "")
				.doesNotContainText(".")
				.hasAD("string1", "String", 0, "config/string1")
				.hasAD("stringarray1", "String", 1, "config/1stringarray1",
						"config/2stringarray1")

				.hasAD("boolean1", "Boolean", 0, "true")
				.hasAD("booleanarray1", "Boolean", 1, "true", "false")

				.hasAD("char1", ocd.character(), 0, "@")
				.hasAD("chararray1", ocd.character(), 1, "@", "+")

				.hasAD("byte1", "Byte", 0, "2")
				.hasAD("bytearray1", "Byte", 1, "2", "-3")

				.hasAD("short1", "Short", 0, "1034")
				.hasAD("shortarray1", "Short", 1, "1034", "-1043")

				.hasAD("int1", "Integer", 0, "123456")
				.hasAD("intarray1", "Integer", 1, "123456", "-234567")

				.hasAD("long1", "Long", 0, "9876543210")
				.hasAD("longarray1", "Long", 1, "9876543210", "-987654321")

				.hasAD("float1", "Float", 0, "3.14")
				.hasAD("floatarray1", "Float", 1, "3.14", "-4.56")

				.hasAD("double1", "Double", 0, "2.1")
				.hasAD("doublearray1", "Double", 1, "2.1", "-1.2")

				.hasAD("class1", "String", 0,
						"org.osgi.impl.bundle.metatype.annotations.TestEnum")
				.hasAD("classarray1", "String", 1,
						"org.osgi.impl.bundle.metatype.annotations.TestEnum",
						"java.lang.Object")

				.hasAD("enum1", "String", 0, "ITEM1")
				.hasCount("AD[@id='enum1']/Option", 4)
				.hasOption("enum1", "ITEM1", "ITEM1")
				.hasOption("enum1", "ITEM2", "ITEM2")
				.hasOption("enum1", "ITEM3", "ITEM3")
				.hasOption("enum1", "ITEM4", "ITEM4")

				.hasAD("enumarray1", "String", 1, "ITEM1", "ITEM2")
				.hasCount("AD[@id='enumarray1']/Option", 4)
				.hasOption("enumarray1", "ITEM1", "ITEM1")
				.hasOption("enumarray1", "ITEM2", "ITEM2")
				.hasOption("enumarray1", "ITEM3", "ITEM3")
				.hasOption("enumarray1", "ITEM4", "ITEM4")

				.hasAD(".password1", "Password", 0, "config/password1")
				.hasAD(".passwordarray1", "Password", 1,
						"config/1passwordarray1", "config/2passwordarray1");
	}

	@Test
	public void testSubInterface() throws Exception {
		String name = testName.getMethodName();
		OCD ocd = ocds.get(name);
		assertThat(ocd).as("OCD %s", name)
				.hasValue("@description", "")
				.doesNotContainText(".")
				.hasAD("string1", "String", 0)
				.hasAD("string2", "String", 0)
				.hasAD("boolean1", "Boolean", 0)
				.hasAD("char1", ocd.character(), 0)
				.hasAD("byte1", "Byte", 0)
				.hasAD("short1", "Short", 0)
				.hasAD("int1", "Integer", 0)
				.hasAD("long1", "Long", 0)
				.hasAD("float1", "Float", 0)
				.hasAD("double1", "Double", 0)
				.hasAD("class1", "String", 0)
				.hasAD("enum1", "String", 0)
				.hasAD("enumcoll2", "String", -1)
				.hasAD("enumlist2", "String", -1)
				.hasAD("enumset2", "String", -1)
				.hasAD("enumiter2", "String", -1)
				.hasAD("enumarraylist2", "String", -1)

				.hasCount("AD[@id='enum1']/Option", 4)
				.hasOption("enum1", "ITEM1", "ITEM1")
				.hasOption("enum1", "ITEM2", "ITEM2")
				.hasOption("enum1", "ITEM3", "ITEM3")
				.hasOption("enum1", "ITEM4", "ITEM4")

				.hasCount("AD[@id='enumcoll2']/Option", 4)
				.hasOption("enumcoll2", "ITEM1", "ITEM1")
				.hasOption("enumcoll2", "ITEM2", "ITEM2")
				.hasOption("enumcoll2", "ITEM3", "ITEM3")
				.hasOption("enumcoll2", "ITEM4", "ITEM4")

				.hasCount("AD[@id='enumlist2']/Option", 4)
				.hasOption("enumlist2", "ITEM1", "ITEM1")
				.hasOption("enumlist2", "ITEM2", "ITEM2")
				.hasOption("enumlist2", "ITEM3", "ITEM3")
				.hasOption("enumlist2", "ITEM4", "ITEM4")

				.hasCount("AD[@id='enumset2']/Option", 4)
				.hasOption("enumset2", "ITEM1", "ITEM1")
				.hasOption("enumset2", "ITEM2", "ITEM2")
				.hasOption("enumset2", "ITEM3", "ITEM3")
				.hasOption("enumset2", "ITEM4", "ITEM4")

				.hasCount("AD[@id='enumiter2']/Option", 4)
				.hasOption("enumiter2", "ITEM1", "ITEM1")
				.hasOption("enumiter2", "ITEM2", "ITEM2")
				.hasOption("enumiter2", "ITEM3", "ITEM3")
				.hasOption("enumiter2", "ITEM4", "ITEM4")

				.hasCount("AD[@id='enumarraylist2']/Option", 4)
				.hasOption("enumarraylist2", "ITEM1", "ITEM1")
				.hasOption("enumarraylist2", "ITEM2", "ITEM2")
				.hasOption("enumarraylist2", "ITEM3", "ITEM3")
				.hasOption("enumarraylist2", "ITEM4", "ITEM4");
	}

	@Test
	public void testNameMapping() throws Exception {
		String name = testName.getMethodName();
		OCD ocd = ocds.get(name);
		assertThat(ocd).as("OCD %s", name)
				.hasValue("@description", "")
				.doesNotContainText(".")
				.hasAD("myProperty143", "String", 0)
				.hasAD("new", "String", 0)
				.hasAD("my$prop", "String", 0)
				.hasAD("dot.prop", "String", 0)
				.hasAD(".secret", "String", 0)
				.hasAD("another_prop", "String", 0)
				.hasAD("three_.prop", "String", 0)
				.hasAD("four._prop", "String", 0)
				.hasAD("five..prop", "String", 0);
	}

	@Test
	public void testNoDefaults() throws Exception {
		String name = testName.getMethodName();
		OCD ocd = ocds.get(name);
		assertThat(ocd).as("OCD %s", name)
				.doesNotContainText(".")
				.hasValue("../@localization", "OSGI-INF/l10n/member")

				.hasValue("@description", "%member.description")
				.hasValue("@name", "%member.name")
				.hasAD(".password", "Password", 0)
				.hasValue("AD[@id='.password']/@description",
						"%member.password.description")
				.hasValue("AD[@id='.password']/@name", "%member.password.name")
				.hasAD("type", "String", 12, "contributing")
				.hasValue("AD[@id='type']/@cardinality", "12")
				.hasValue("AD[@id='type']/@max", "max1")
				.hasValue("AD[@id='type']/@min", "min1")
				.hasCount("AD[@id='type']/Option", 3)
				.hasOption("type", "%strategic", "strategic")
				.hasOption("type", "%principal", "principal")
				.hasOption("type", "%contributing", "contributing")
				.hasCount("Icon", 2)
				.hasValue("Icon[@resource='icon/member-32.png']/@size", "32")
				.hasValue("Icon[@resource='icon/member-64.png']/@size", "64");
		for (String pid : Arrays.asList("pid1", "pid2")) {
			Designate designate = designatePids.get(pid);
			assertThat(designate).as("designate pid %s", pid)
					.hasValue("Object/@ocdref", "testNoDefaults")
					.doesNotContain("@factoryPid");
		}
		for (String factoryPid : Arrays.asList("factoryPid1", "factoryPid2")) {
			Designate designate = designateFactoryPids.get(factoryPid);
			assertThat(designate).as("designate factoryPid %s", factoryPid)
					.hasValue("Object/@ocdref", "testNoDefaults")
					.doesNotContain("@pid");
		}
	}

	@Test
	public void testDesignateConfig() throws Exception {
		String name = testName.getMethodName();
		OCD ocd = ocds.get(name);
		assertThat(ocd).as("OCD %s", name)
				.doesNotContainText(".")
				.hasValue("@description", "")
				.hasAD("string1", "String", 0, "config/string1");

		String pid = "testDesignateComponent";
		Designate designate = designatePids.get(pid);
		assertThat(designate).as("designate pid %s", pid)
				.hasValue("Object/@ocdref", name)
				.doesNotContain("@factoryPid");
	}

	@Test
	public void testDesignateFactoryConfig() throws Exception {
		String name = testName.getMethodName();
		OCD ocd = ocds.get(name);
		assertThat(ocd).as("OCD %s", name)
				.doesNotContainText(".")
				.hasValue("@description", "")
				.hasAD("string1", "String", 0, "config/string1");

		String factoryPid = "testDesignateFactoryComponent";
		Designate designate = designateFactoryPids.get(factoryPid);
		assertThat(designate).as("designate factoryPid %s", factoryPid)
				.hasValue("Object/@ocdref", name)
				.doesNotContain("@pid");
	}

}

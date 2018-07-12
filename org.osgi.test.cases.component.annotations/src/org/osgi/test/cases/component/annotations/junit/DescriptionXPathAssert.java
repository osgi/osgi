/*
 * Copyright (c) OSGi Alliance (2018). All Rights Reserved.
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

import java.util.List;

import org.assertj.core.api.Assertions;
import org.osgi.test.support.string.Strings;
import org.osgi.test.support.xpath.AbstractXPathAssert;
import org.w3c.dom.Node;

public class DescriptionXPathAssert
		extends AbstractXPathAssert<DescriptionXPathAssert,Description> {

	public DescriptionXPathAssert(Description actual) {
		super(actual, DescriptionXPathAssert.class);
	}

	public static DescriptionXPathAssert assertThat(Description actual) {
		return new DescriptionXPathAssert(actual);
	}

	public DescriptionXPathAssert hasPropertyValue(String name, String type,
			String value) {
		isNotNull();
		final String expr = "property[@name='" + name + "']";
		hasValue(expr + "/@value", value);
		if (type.equals("String")) {
			hasOptionalValue(expr + "/@type", "String");
		} else {
			hasValue(expr + "/@type", type);
		}
		doesNotContainText(expr);
		return myself;
	}

	public DescriptionXPathAssert hasPropertyArrayValue(String name,
			String type, String... values) {
		isNotNull();
		final String expr = "property[@name='" + name + "']";
		doesNotContain(expr + "/@value");
		if (type.equals("String")) {
			hasOptionalValue(expr + "/@type", "String");
		} else {
			hasValue(expr + "/@type", type);
		}
		Node result = getNode(expr + "/text()");
		Assertions.assertThat(result)
				.as("%s/text() value for node %s", expr, actual.getId())
				.isNotNull();
		List<String> split = Strings.split("\\s*\\n\\s*",
				result.getNodeValue());
		Assertions.assertThat(split)
				.as("%s/text() value for node %s", expr, actual.getId())
				.containsExactly(values);
		return myself;
	}

}

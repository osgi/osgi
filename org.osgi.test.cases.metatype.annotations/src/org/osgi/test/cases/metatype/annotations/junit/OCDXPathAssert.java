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

package org.osgi.test.cases.metatype.annotations.junit;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.osgi.test.support.string.Strings;
import org.osgi.test.support.xpath.XPathAssert;
import org.w3c.dom.Node;

public class OCDXPathAssert<SELF extends OCDXPathAssert<SELF,ACTUAL>, ACTUAL extends OCD>
		extends XPathAssert<SELF,ACTUAL> {

	public OCDXPathAssert(ACTUAL actual) {
		this(actual, OCDXPathAssert.class);
	}

	protected OCDXPathAssert(ACTUAL actual, Class< ? > selfType) {
		super(actual, selfType);
	}

	public static <SELF extends OCDXPathAssert<SELF,ACTUAL>, ACTUAL extends OCD> OCDXPathAssert<SELF,ACTUAL> assertThat(
			ACTUAL actual) {
		return new OCDXPathAssert<>(actual);
	}

	public SELF hasOption(String ad, String label, String value) {
		hasValue("AD[@id='" + ad + "']/Option[@label='" + label + "']/@value",
				value);
		return myself;
	}

	public SELF hasAD(String ad, String type, int cardinality,
			String... values) {
		final String expr = "AD[@id='" + ad + "']";
		hasValue(expr + "/@type", type);
		Node result = getNode(expr + "/@cardinality");
		int c = (result == null) ? 0 : Integer.parseInt(result.getNodeValue());
		Assertions.assertThat(Integer.signum(c))
				.as("%s/@cardinality for node %s", expr, actual.getId())
				.isEqualTo(Integer.signum(cardinality));

		final int size = values.length;
		if (size > 0) {
			result = getNode(expr + "/@default");
			Assertions.assertThat(result)
					.as("%s/@default for node %s", expr, actual.getId())
					.isNotNull();
			List<String> split = Strings.split(result.getNodeValue());
			Assertions.assertThat(split)
					.as("%s/@default value for node %s", expr, actual.getId())
					.containsExactly(values);
		}

		doesNotContainText(expr);
		return myself;
	}

}

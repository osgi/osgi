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

package org.osgi.test.support.xpath;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.assertj.core.api.AbstractAssert;
import org.osgi.test.support.string.Strings;
import org.w3c.dom.Node;

public abstract class AbstractXPathAssert<SELF extends AbstractXPathAssert<SELF,ACTUAL>, ACTUAL extends BaseElement>
		extends AbstractAssert<SELF,ACTUAL> {
	private static final XPathFactory	xpf	= XPathFactory.newInstance();
	protected final XPath				xpath;

	public AbstractXPathAssert(ACTUAL actual, Class< ? > selfType) {
		super(actual, selfType);
		xpath = xpf.newXPath();
		if (actual != null) {
			xpath.setNamespaceContext(actual.getNamespaceContext());
		}
	}

	public SELF hasNamespace(String namespace) {
		isNotNull();
		assertThat(actual.getNamespaceContext().getURI())
				.as("namespace for node %s", actual.getId())
				.isEqualTo(namespace);
		return myself;
	}

	public SELF contains(String expr) {
		isNotNull();
		Node result = getNode(expr);
		assertThat(result).as("%s for node %s", expr, actual.getId())
				.isNotNull();
		return myself;
	}

	public SELF doesNotContain(String expr) {
		isNotNull();
		Node result = getNode(expr);
		assertThat(result).as("%s for node %s", expr, actual.getId()).isNull();
		return myself;
	}

	public SELF containsAnyOf(String... expressions) {
		isNotNull();
		for (String expr : expressions) {
			Node result = getNode(expr);
			if (result != null) {
				return myself;
			}
		}
		failWithMessage("node %s did not match any of the expressions <'%s'>",
				actual.getId(), Arrays.toString(expressions));
		return myself;
	}

	public SELF hasValue(String expr, String value) {
		isNotNull();
		Node result = getNode(expr);
		assertThat(result).as("%s for node %s", expr, actual.getId())
				.isNotNull();
		assertThat(result.getNodeValue())
				.as("%s for node %s", expr, actual.getId())
				.isEqualTo(value);
		return myself;
	}

	public SELF hasValuesExactlyInAnyOrder(String expr, String... values) {
		isNotNull();
		Node result = getNode(expr);
		assertThat(result).as("%s for node %s", expr, actual.getId())
				.isNotNull();
		assertThat(Strings.split("\\s+", result.getNodeValue()))
				.containsExactlyInAnyOrder(values);
		return myself;
	}

	public SELF hasOptionalValue(String expr, String value) {
		isNotNull();
		Node result = getNode(expr);
		if (result != null) {
			assertThat(result.getNodeValue())
					.as("%s for node %s", expr, actual.getId())
					.isEqualTo(value);
		}
		return myself;
	}

	public SELF hasCount(String expr, int value) {
		isNotNull();
		int count = getCount(expr);
		assertThat(count).as("count(%s) for node %s", expr, actual.getId())
				.isEqualTo(value);
		return myself;
	}

	public SELF doesNotContainText(String expr) {
		isNotNull();
		Node result = getNode(expr + "/text()");
		if (result != null) {
			assertThat(result.getNodeValue())
					.as("%s/text() value for node %s", expr, actual.getId())
					.isBlank();
		}
		return myself;
	}

	public Node getNode(String expr) {
		try {
			Node result = (Node) xpath.evaluate(expr, actual.getElement(),
					XPathConstants.NODE);
			return result;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			failWithMessage("invalid xpath expression %s", expr);
		}
		return null;
	}

	public int getCount(String expr) {
		try {
			String result = (String) xpath.evaluate("count(" + expr + ")",
					actual.getElement(), XPathConstants.STRING);
			assertThat(result).as("count(%s) for node %s", expr, actual.getId())
					.containsOnlyDigits();
			return Integer.parseInt(result);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			failWithMessage("invalid xpath expression %s", expr);
		}
		return -1;
	}

}

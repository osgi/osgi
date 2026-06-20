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

import static org.assertj.core.api.Assertions.assertThat;
import static org.osgi.test.assertj.dictionary.DictionaryAssert.assertThat;

import java.io.StringWriter;
import java.net.URL;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.junit.Before;
import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentConstants;
import org.osgi.test.support.junit4.AbstractOSGiTestCase;
import org.osgi.test.support.string.Strings;
import org.osgi.test.support.xpath.BaseNamespaceContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AnnotationsTestCase extends AbstractOSGiTestCase {
	public static final String			xmlns_scr100	= "http://www.osgi.org/xmlns/scr/v1.0.0";
	public static final String			xmlns_scr110	= "http://www.osgi.org/xmlns/scr/v1.1.0";
	public static final String			xmlns_scr120	= "http://www.osgi.org/xmlns/scr/v1.2.0";
	public static final String			xmlns_scr130	= "http://www.osgi.org/xmlns/scr/v1.3.0";
	public static final String			xmlns_scr140	= "http://www.osgi.org/xmlns/scr/v1.4.0";
	public static final String			xmlns_scr150	= "http://www.osgi.org/xmlns/scr/v1.5.0";
	public static final String			xmlns_scr160	= "http://www.osgi.org/xmlns/scr/v1.6.0";
	protected Map<String,Description>	descriptions;

	@Before
	public void loadComponentDescriptions() throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();

		final XPathFactory xpf = XPathFactory.newInstance();
		XPath xpath = xpf.newXPath();

		descriptions = new HashMap<>();

		String propName = "org.osgi.test.cases.component.annotations.bundle.symbolic.name";
		String bsn = getProperty(propName);
		assertThat(bsn).as(
				"The system property \"%s\" must be set to the Bundle Symbolic Name of the bundle processed by the Component Annotations tool.",
				propName).isNotNull();
		Bundle impl = null;
		for (Bundle b : getContext().getBundles()) {
			if (bsn.equals(b.getSymbolicName())) {
				impl = b;
				break;
			}
		}
		assertThat(impl).as(
				"Could not locate the bundle with Bundle Symbolic Name %s", bsn)
				.isNotNull();

		Dictionary<String,String> headers = impl.getHeaders();
		assertThat(headers).containsKey(ComponentConstants.SERVICE_COMPONENT);
		String header = headers.get(ComponentConstants.SERVICE_COMPONENT);
		assertThat(header).as("%s header is missing",
				ComponentConstants.SERVICE_COMPONENT).isNotNull();
		List<String> clauses = Strings.split(header);
		assertThat(clauses)
				.as("%s header is empty", ComponentConstants.SERVICE_COMPONENT)
				.isNotEmpty();
		XPathExpression expr = xpath.compile(String.format(
				"//namespace::*[.='%s' or .='%s' or .='%s' or .='%s' or .='%s' or .='%s' or .='%s']",
				xmlns_scr100, xmlns_scr110, xmlns_scr120, xmlns_scr130,
				xmlns_scr140, xmlns_scr150, xmlns_scr160));
		for (String clause : clauses) {
			// System.out.println(clause);

			URL url = impl.getEntry(clause);
			assertThat(url)
					.as("Entry %s could not be located in bundle", clause)
					.isNotNull();

			Document document = db.parse(url.openStream());

			Element root = document.getDocumentElement();
			if ("component".equals(root.getNodeName())
					&& (root.getNamespaceURI() == null)) {
				String name = getComponentName(xpath, root);
				assertThat(descriptions)
						.as("multiple descriptions with the same name %s", name)
						.doesNotContainKey(name);
				descriptions.put(name, new Description(name,
						new BaseNamespaceContext("scr", xmlns_scr100), root));
				continue;
			}
			NodeList namespaces = (NodeList) expr.evaluate(document,
					XPathConstants.NODESET);
			for (int i = 0; i < namespaces.getLength(); i++) {
				Attr namespace = (Attr) namespaces.item(i);
				BaseNamespaceContext context = new BaseNamespaceContext(
						namespace);
				xpath.setNamespaceContext(context);
				NodeList components = (NodeList) xpath.evaluate(
						"//" + namespace.getLocalName() + ":component",
						namespace.getOwnerElement(), XPathConstants.NODESET);
				for (int j = 0; j < components.getLength(); j++) {
					Element component = (Element) components.item(j);
					// System.out.println(toString(component));
					String name = getComponentName(xpath, component);
					assertThat(descriptions).as(
							"multiple descriptions with the same name %s", name)
							.doesNotContainKey(name);
					descriptions.put(name,
							new Description(name, context, component));
				}
			}
		}

	}

	private String getComponentName(XPath xpath, Element component)
			throws Exception {
		String name = (String) xpath.evaluate("@name", component,
				XPathConstants.STRING);
		if (name.length() == 0) {
			name = (String) xpath.evaluate("implementation/@class", component,
					XPathConstants.STRING);
		}
		return name;
	}

	/* For test debugging use */
	public static String toString(Node node) throws Exception {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "4");
		DOMSource source = new DOMSource(node);
		StreamResult result = new StreamResult(new StringWriter());
		transformer.transform(source, result);
		return result.getWriter().toString();
	}

}

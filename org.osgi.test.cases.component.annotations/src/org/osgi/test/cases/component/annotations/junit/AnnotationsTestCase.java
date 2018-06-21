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

import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
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

import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentConstants;
import org.osgi.test.support.OSGiTestCase;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * 
 * @author $Id$
 */
public class AnnotationsTestCase extends OSGiTestCase {
	public static final String				xmlns_scr100	= "http://www.osgi.org/xmlns/scr/v1.0.0";
	public static final String				xmlns_scr110	= "http://www.osgi.org/xmlns/scr/v1.1.0";
	public static final String				xmlns_scr120	= "http://www.osgi.org/xmlns/scr/v1.2.0";
	public static final String				xmlns_scr130	= "http://www.osgi.org/xmlns/scr/v1.3.0";
	private final DocumentBuilder			db;
	private final XPath						xpath;
	private final Map<String, Description>	descriptions;

	/**
	 * @throws Exception
	 */
	public AnnotationsTestCase() throws Exception {
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		db = dbf.newDocumentBuilder();

		final XPathFactory xpf = XPathFactory.newInstance();
		xpath = xpf.newXPath();

		descriptions = new HashMap<String, Description>();
	}

	protected void setUp() throws Exception {
		final String propName = "org.osgi.test.cases.component.annotations.bundle.symbolic.name";
		String bsn = getProperty(propName);
		assertNotNull(
				"The system property \""
						+ propName
						+ "\" must be set to the Bundle Symbolic Name of the bundle processed by the Component Annotations tool.",
				bsn);
		Bundle impl = null;
		for (Bundle b : getContext().getBundles()) {
			if (bsn.equals(b.getSymbolicName())) {
				impl = b;
				break;
			}
		}
		assertNotNull("Could not locate the bundle with Bundle Symbolic Name "
				+ bsn, impl);

		String header = impl.getHeaders().get(
				ComponentConstants.SERVICE_COMPONENT);
		assertNotNull(ComponentConstants.SERVICE_COMPONENT
				+ " header is missing", header);
		String[] clauses = header.split(",");
		assertFalse(ComponentConstants.SERVICE_COMPONENT + " header is empty",
				clauses.length == 0);
		XPathExpression expr = xpath.compile("//namespace::*[.='"
				+ xmlns_scr100 + "' or .='" + xmlns_scr110 + "' or .='"
				+ xmlns_scr120 + "' or .='" + xmlns_scr130 + "']");
		for (String clause : clauses) {
			// System.out.println(clause);

			URL url = impl.getEntry(clause);
			assertNotNull(
					"Entry " + clause + " could not be located in bundle", url);

			Document document = db.parse(url.openStream());

			Element root = document.getDocumentElement();
			if ("component".equals(root.getNodeName())
					&& (root.getNamespaceURI() == null)) {
				String name = getComponentName(root);
				assertNull("multiple descriptions with the same name",
						descriptions.put(name, new Description(name,
								new NamespaceContextImpl("scr", xmlns_scr100),
								root)));
				continue;
			}
			NodeList namespaces = (NodeList) expr.evaluate(document,
					XPathConstants.NODESET);
			for (int i = 0; i < namespaces.getLength(); i++) {
				Attr namespace = (Attr) namespaces.item(i);
				NamespaceContextImpl context = new NamespaceContextImpl(
						namespace);
				xpath.setNamespaceContext(context);
				NodeList components = (NodeList) xpath.evaluate("//"
						+ namespace.getLocalName() + ":component",
						namespace.getOwnerElement(), XPathConstants.NODESET);
				for (int j = 0; j < components.getLength(); j++) {
					Element component = (Element) components.item(j);
					// System.out.println(toString(component));
					String name = getComponentName(component);
					assertNull("multiple descriptions with the same name",
							descriptions.put(name, new Description(name,
									context, component)));
				}
			}
		}
	}

	/**
	 * @throws Exception
	 */
	public void testHelloWorld10() throws Exception {
		String name = "org.osgi.impl.bundle.component.annotations.HelloWorld10";
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		assertEquals("incorrect namespace", xmlns_scr100, description
				.getNamespaceContext().getUri());
		// v1.0.0
		assertXPathValueIfSet(description, "@immediate", "true");
		assertXPathValueIfSet(description, "@enabled", "true");
		assertXPathValue(description, "@factory", null);
		assertXPathValue(description, "implementation/@class", name);
		assertXPathCount(description, "implementation", 1);
		assertXPathCount(description, "service", 0);
		assertXPathCount(description, "reference", 0);
		// v1.1.0
		assertXPathValue(description, "@configuration-policy", null);
		assertXPathValue(description, "@activate", null);
		assertXPathValue(description, "@deactivate", null);
		assertXPathValue(description, "@modified", null);
		// v1.2.0
		assertXPathValue(description, "@configuration-pid", null);

	}

	/**
	 * @throws Exception
	 */
	public void testHelloWorld11() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		assertEquals("incorrect namespace", xmlns_scr110, description
				.getNamespaceContext().getUri());
		// v1.0.0
		assertXPathValueIfSet(description, "@immediate", "true");
		assertXPathValueIfSet(description, "@enabled", "true");
		assertXPathValue(description, "@factory", null);
		assertXPathValue(
				description,
				"implementation/@class",
				"org.osgi.impl.bundle.component.annotations."
						+ name.substring(4));
		assertXPathCount(description, "implementation", 1);
		assertXPathCount(description, "service", 0);
		assertXPathCount(description, "reference", 0);
		// v1.1.0
		assertXPathValueIfSet(description, "@configuration-policy", "optional");
		assertXPathValue(description, "@activate", "activator");
		assertXPathValue(description, "@deactivate", "deactivator");
		assertXPathValue(description, "@modified", "modified");
		// v1.2.0
		assertXPathValue(description, "@configuration-pid", null);
	}

	/**
	 * @throws Exception
	 */
	public void testHelloWorld12() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		assertEquals("incorrect namespace", xmlns_scr120, description
				.getNamespaceContext().getUri());
		// v1.0.0
		assertXPathValueIfSet(description, "@immediate", "true");
		assertXPathValueIfSet(description, "@enabled", "true");
		assertXPathValue(description, "@factory", null);
		assertXPathValue(
				description,
				"implementation/@class",
				"org.osgi.impl.bundle.component.annotations."
						+ name.substring(4));
		assertXPathCount(description, "implementation", 1);
		assertXPathCount(description, "service", 0);
		assertXPathCount(description, "reference", 0);
		// v1.1.0
		assertXPathValueIfSet(description, "@configuration-policy", "optional");
		assertXPathValueIfSet(description, "@activate", "activate");
		assertXPathValueIfSet(description, "@deactivate", "deactivate");
		assertXPathValue(description, "@modified", "modified");
		// v1.2.0
		assertXPathValueIfSet(description, "@configuration-pid", name);
	}

	public void testHelloWorld13() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		assertEquals("incorrect namespace", xmlns_scr130, description
				.getNamespaceContext().getUri());
		// v1.0.0
		assertXPathValueIfSet(description, "@immediate", "true");
		assertXPathValueIfSet(description, "@enabled", "true");
		assertXPathValue(description, "@factory", null);
		assertXPathValue(
				description,
				"implementation/@class",
				"org.osgi.impl.bundle.component.annotations."
						+ name.substring(4));
		assertXPathCount(description, "implementation", 1);
		assertXPathCount(description, "service", 0);
		assertXPathCount(description, "reference", 0);
		// v1.1.0
		assertXPathValueIfSet(description, "@configuration-policy", "optional");
		assertXPathValueIfSet(description, "@activate", "activate");
		assertXPathValueIfSet(description, "@deactivate", "deactivate");
		assertXPathValue(description, "@modified", "modified");
		// v1.2.0
		assertXPathValueIfSet(description, "@configuration-pid", name);
		// v1.3.0
		assertXPathValueIfSet(description, "@scope", "singleton");
	}

	// assertXPathValue(description,
	// "service/provide[@interface='java.util.EventListener']/@interface",
	// "java.util.EventListener");
	/**
	 * @throws Exception
	 */
	public void testService() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		// v1.0.0
		assertXPathValueIfSet(description, "@immediate", "false");
		assertXPathValueIfSet(description, "@enabled", "true");
		assertXPathValue(description, "@factory", null);
		assertXPathCount(description, "implementation", 1);
		assertXPathValue(
				description,
				"implementation/@class",
				"org.osgi.impl.bundle.component.annotations."
						+ name.substring(4));
		assertXPathCount(description, "service/provide", 1);
		assertXPathValue(description, "service/provide/@interface",
				"java.util.EventListener");
		assertXPathValueIfSet(description, "service/@servicefactory", "false");
		assertXPathCount(description, "reference", 1);
		assertXPathValue(description, "reference/@name", "Log");
		assertXPathValue(description, "reference/@interface",
				"org.osgi.service.log.LogService");
		assertXPathValue(description, "reference/@bind", "bindLog");
		assertXPathValue(description, "reference/@unbind", "unbindLog");
		assertXPathValueIfSet(description, "reference/@cardinality", "1..1");
		assertXPathValueIfSet(description, "reference/@policy", "static");
		assertXPathValue(description, "reference/@target", null);
		// v1.1.0
		assertXPathValueIfSet(description, "@configuration-policy", "optional");
		assertXPathValueIfSet(description, "@activate", "activate");
		assertXPathValueIfSet(description, "@deactivate", "deactivate");
		assertXPathValue(description, "@modified", null);
		// v1.2.0
		assertXPathValueIfSet(description, "@configuration-pid", name);
		assertXPathValueIfSet(description, "reference/@policy-option",
				"reluctant");
		assertXPathValue(description, "reference/@updated", null);
		// v1.3.0
		assertXPathValueIfSet(description, "@scope", "singleton");
	}

	public void testComponentReferences() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		// v1.0.0
		assertXPathValueIfSet(description, "@immediate", "false");
		assertXPathValueIfSet(description, "@enabled", "true");
		assertXPathValue(description, "@factory", null);
		assertXPathCount(description, "implementation", 1);
		assertXPathValue(
				description,
				"implementation/@class",
				"org.osgi.impl.bundle.component.annotations."
						+ name.substring(4));
		assertXPathCount(description, "service/provide", 1);
		assertXPathValue(description,
				"service/provide/@interface",
				"java.util.EventListener");
		assertXPathValueIfSet(description, "service/@servicefactory", "false");
		assertXPathCount(description, "reference", 2);
		assertXPathValue(description, "reference[@name='log1']/@interface", "org.osgi.service.log.LogService");
		assertXPathValue(description, "reference[@name='log1']/@bind", null);
		assertXPathValue(description, "reference[@name='log1']/@unbind", null);
		assertXPathValueIfSet(description, "reference[@name='log1']/@cardinality", "1..1");
		assertXPathValueIfSet(description, "reference[@name='log1']/@policy", "static");
		assertXPathValue(description, "reference[@name='log1']/@target", null);
		assertXPathValue(description, "reference[@name='log2']/@interface", "org.osgi.service.log.LogService");
		assertXPathValue(description, "reference[@name='log2']/@bind", "bindLog");
		assertXPathValue(description, "reference[@name='log2']/@unbind", "unbindLog");
		assertXPathValueIfSet(description, "reference[@name='log2']/@cardinality", "1..1");
		assertXPathValueIfSet(description, "reference[@name='log2']/@policy", "static");
		assertXPathValue(description, "reference[@name='log2']/@target", null);
		// v1.1.0
		assertXPathValueIfSet(description, "@configuration-policy", "optional");
		assertXPathValueIfSet(description, "@activate", "activate");
		assertXPathValueIfSet(description, "@deactivate", "deactivate");
		assertXPathValue(description, "@modified", null);
		// v1.2.0
		assertXPathValueIfSet(description, "@configuration-pid", name);
		assertXPathValueIfSet(description,
				"reference/@policy-option",
				"reluctant");
		assertXPathValue(description, "reference/@updated", null);
		// v1.3.0
		assertXPathValueIfSet(description, "@scope", "singleton");
		assertXPathValue(description, "reference[@name='log1']/@field", "logfield");
		assertXPathValueIfSet(description, "reference[@name='log1']/@field-option", "replace");
		assertXPathValueIfSet(description, "reference[@name='log1']/@scope", "bundle");
		assertXPathValue(description, "reference[@name='log2']/@field", null);
		assertXPathValue(description, "reference[@name='log2']/@field-option", null);
		assertXPathValueIfSet(description, "reference[@name='log2']/@scope", "bundle");
	}

	/**
	 * @throws Exception
	 */
	public void testNoService() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		// v1.0.0
		assertXPathCount(description, "service", 0);
	}

	/**
	 * @throws Exception
	 */
	public void testNoInheritService() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		// v1.0.0
		assertXPathCount(description, "service", 0);
	}

	/**
	 * @throws Exception
	 */
	public void testReferences() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		// v1.0.0
		assertXPathCount(description, "reference", 13);

		assertXPathValue(description, "reference[@name='static']/@interface",
				"java.util.EventListener");
		assertXPathValue(description, "reference[@name='static']/@bind",
				"bindStatic");
		assertXPathValue(description, "reference[@name='static']/@unbind",
				"unbindStatic");
		assertXPathValueIfSet(description,
				"reference[@name='static']/@cardinality", "1..1");
		assertXPathValueIfSet(description, "reference[@name='static']/@policy",
				"static");
		assertXPathValue(description, "reference[@name='static']/@target", null);

		assertXPathValue(description, "reference[@name='dynamic']/@interface",
				"java.util.EventListener");
		assertXPathValue(description, "reference[@name='dynamic']/@bind",
				"bindDynamic");
		assertXPathValue(description, "reference[@name='dynamic']/@unbind",
				"unbindDynamic");
		assertXPathValueIfSet(description,
				"reference[@name='dynamic']/@cardinality", "1..1");
		assertXPathValue(description, "reference[@name='dynamic']/@policy",
				"dynamic");
		assertXPathValue(description, "reference[@name='dynamic']/@target",
				null);

		assertXPathValue(description,
				"reference[@name='mandatory']/@interface",
				"java.util.EventListener");
		assertXPathValue(description, "reference[@name='mandatory']/@bind",
				"bindMandatory");
		assertXPathValue(description, "reference[@name='mandatory']/@unbind",
				"unbindMandatory");
		assertXPathValueIfSet(description,
				"reference[@name='mandatory']/@cardinality", "1..1");
		assertXPathValueIfSet(description,
				"reference[@name='mandatory']/@policy", "static");
		assertXPathValue(description, "reference[@name='mandatory']/@target",
				null);

		assertXPathValue(description, "reference[@name='optional']/@interface",
				"java.util.EventListener");
		assertXPathValue(description, "reference[@name='optional']/@bind",
				"bindOptional");
		assertXPathValue(description, "reference[@name='optional']/@unbind",
				"unbindOptional");
		assertXPathValue(description,
				"reference[@name='optional']/@cardinality", "0..1");
		assertXPathValueIfSet(description,
				"reference[@name='optional']/@policy", "static");
		assertXPathValue(description, "reference[@name='optional']/@target",
				null);

		assertXPathValue(description, "reference[@name='multiple']/@interface",
				"java.util.EventListener");
		assertXPathValue(description, "reference[@name='multiple']/@bind",
				"bindMultiple");
		assertXPathValue(description, "reference[@name='multiple']/@unbind",
				"unbindMultiple");
		assertXPathValue(description,
				"reference[@name='multiple']/@cardinality", "0..n");
		assertXPathValueIfSet(description,
				"reference[@name='multiple']/@policy", "static");
		assertXPathValue(description, "reference[@name='multiple']/@target",
				null);

		assertXPathValue(description,
				"reference[@name='atleastone']/@interface",
				"java.util.EventListener");
		assertXPathValue(description, "reference[@name='atleastone']/@bind",
				"bindAtLeastOne");
		assertXPathValue(description, "reference[@name='atleastone']/@unbind",
				"unbindAtLeastOne");
		assertXPathValue(description,
				"reference[@name='atleastone']/@cardinality", "1..n");
		assertXPathValueIfSet(description,
				"reference[@name='atleastone']/@policy", "static");
		assertXPathValue(description, "reference[@name='atleastone']/@target",
				null);

		assertXPathValue(description, "reference[@name='greedy']/@interface",
				"java.util.EventListener");
		assertXPathValue(description, "reference[@name='greedy']/@bind",
				"bindGreedy");
		assertXPathValue(description, "reference[@name='greedy']/@unbind",
				"unbindGreedy");
		assertXPathValueIfSet(description,
				"reference[@name='greedy']/@cardinality", "1..1");
		assertXPathValueIfSet(description, "reference[@name='greedy']/@policy",
				"static");
		assertXPathValue(description, "reference[@name='greedy']/@target", null);

		assertXPathValue(description,
				"reference[@name='reluctant']/@interface",
				"java.util.EventListener");
		assertXPathValue(description, "reference[@name='reluctant']/@bind",
				"bindReluctant");
		assertXPathValue(description, "reference[@name='reluctant']/@unbind",
				"unbindReluctant");
		assertXPathValueIfSet(description,
				"reference[@name='reluctant']/@cardinality", "1..1");
		assertXPathValueIfSet(description,
				"reference[@name='reluctant']/@policy", "static");
		assertXPathValue(description, "reference[@name='reluctant']/@target",
				null);

		assertXPathValue(description, "reference[@name='updated']/@interface",
				"java.util.EventListener");
		assertXPathValue(description, "reference[@name='updated']/@bind",
				"bindUpdated");
		assertXPathValue(description, "reference[@name='updated']/@unbind",
				"unbindUpdated");
		assertXPathValueIfSet(description,
				"reference[@name='updated']/@cardinality", "1..1");
		assertXPathValueIfSet(description,
				"reference[@name='updated']/@policy", "static");
		assertXPathValue(description, "reference[@name='updated']/@target",
				null);

		assertXPathValue(description, "reference[@name='unbind']/@interface",
				"java.util.EventListener");
		assertXPathValue(description, "reference[@name='unbind']/@bind",
				"bindUnbind");
		assertXPathValue(description, "reference[@name='unbind']/@unbind",
				"fooUnbind");
		assertXPathValueIfSet(description,
				"reference[@name='unbind']/@cardinality", "1..1");
		assertXPathValueIfSet(description, "reference[@name='unbind']/@policy",
				"static");
		assertXPathValue(description, "reference[@name='unbind']/@target", null);

		assertXPathValue(description, "reference[@name='nounbind']/@interface",
				"java.util.EventListener");
		assertXPathValue(description, "reference[@name='nounbind']/@bind",
				"bindNoUnbind");
		assertXPathValue(description, "reference[@name='nounbind']/@unbind",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='nounbind']/@cardinality", "1..1");
		assertXPathValueIfSet(description,
				"reference[@name='nounbind']/@policy", "static");
		assertXPathValue(description, "reference[@name='nounbind']/@target",
				null);

		assertXPathValue(description,
				"reference[@name='missingunbind']/@interface",
				"java.util.EventListener");
		assertXPathValue(description, "reference[@name='missingunbind']/@bind",
				"bindMissingUnbind");
		assertXPathValue(description,
				"reference[@name='missingunbind']/@unbind", null);
		assertXPathValueIfSet(description,
				"reference[@name='missingunbind']/@cardinality", "1..1");
		assertXPathValueIfSet(description,
				"reference[@name='missingunbind']/@policy", "static");
		assertXPathValue(description,
				"reference[@name='missingunbind']/@target", null);

		assertXPathValue(description, "reference[@name='target']/@interface",
				"java.util.EventListener");
		assertXPathValue(description, "reference[@name='target']/@bind",
				"bindTarget");
		assertXPathValue(description, "reference[@name='target']/@unbind",
				"unbindTarget");
		assertXPathValueIfSet(description,
				"reference[@name='target']/@cardinality", "1..1");
		assertXPathValueIfSet(description, "reference[@name='target']/@policy",
				"static");
		assertXPathValue(description, "reference[@name='target']/@target",
				"(test.attr=foo)");

		// v1.2.0
		assertXPathValueIfSet(description,
				"reference[@name='static']/@policy-option", "reluctant");
		assertXPathValue(description, "reference[@name='static']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='dynamic']/@policy-option", "reluctant");
		assertXPathValue(description, "reference[@name='dynamic']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='mandatory']/@policy-option", "reluctant");
		assertXPathValue(description, "reference[@name='mandatory']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='optional']/@policy-option", "reluctant");
		assertXPathValue(description, "reference[@name='optional']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='multiple']/@policy-option", "reluctant");
		assertXPathValue(description, "reference[@name='multiple']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='atleastone']/@policy-option", "reluctant");
		assertXPathValue(description, "reference[@name='atleastone']/@updated",
				null);

		assertXPathValue(description,
				"reference[@name='greedy']/@policy-option", "greedy");
		assertXPathValue(description, "reference[@name='greedy']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='reluctant']/@policy-option", "reluctant");
		assertXPathValue(description, "reference[@name='reluctant']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='updated']/@policy-option", "reluctant");
		assertXPathValue(description, "reference[@name='updated']/@updated",
				"updatedUpdated");

		assertXPathValueIfSet(description,
				"reference[@name='unbind']/@policy-option", "reluctant");
		assertXPathValue(description, "reference[@name='unbind']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='nounbind']/@policy-option", "reluctant");
		assertXPathValue(description, "reference[@name='nounbind']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='missingunbind']/@policy-option", "reluctant");
		assertXPathValue(description,
				"reference[@name='missingunbind']/@updated", null);

		assertXPathValueIfSet(description,
				"reference[@name='target']/@policy-option", "reluctant");
		assertXPathValue(description, "reference[@name='target']/@updated",
				null);
	}

	public void testFieldReferences() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		// v1.0.0
		assertXPathCount(description, "reference", 22);

		assertXPathValue(description,
				"reference[@name='static']/@interface",
				"java.util.EventListener");
		assertXPathValue(description,
				"reference[@name='static']/@bind",
				null);
		assertXPathValue(description,
				"reference[@name='static']/@unbind",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='static']/@cardinality",
				"1..1");
		assertXPathValueIfSet(description,
				"reference[@name='static']/@policy",
				"static");
		assertXPathValue(description, "reference[@name='static']/@target", null);

		assertXPathValue(description,
				"reference[@name='dynamic']/@interface",
				"java.util.EventListener");
		assertXPathValue(description,
				"reference[@name='dynamic']/@bind",
				null);
		assertXPathValue(description,
				"reference[@name='dynamic']/@unbind",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='dynamic']/@cardinality",
				"1..1");
		assertXPathValue(description,
				"reference[@name='dynamic']/@policy",
				"dynamic");
		assertXPathValue(description,
				"reference[@name='dynamic']/@target",
				null);

		assertXPathValue(description,
				"reference[@name='mandatory']/@interface",
				"java.util.EventListener");
		assertXPathValue(description,
				"reference[@name='mandatory']/@bind",
				null);
		assertXPathValue(description,
				"reference[@name='mandatory']/@unbind",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='mandatory']/@cardinality",
				"1..1");
		assertXPathValueIfSet(description,
				"reference[@name='mandatory']/@policy",
				"static");
		assertXPathValue(description,
				"reference[@name='mandatory']/@target",
				null);

		assertXPathValue(description,
				"reference[@name='optional']/@interface",
				"java.util.EventListener");
		assertXPathValue(description,
				"reference[@name='optional']/@bind",
				null);
		assertXPathValue(description,
				"reference[@name='optional']/@unbind",
				null);
		assertXPathValue(description,
				"reference[@name='optional']/@cardinality",
				"0..1");
		assertXPathValueIfSet(description,
				"reference[@name='optional']/@policy",
				"static");
		assertXPathValue(description,
				"reference[@name='optional']/@target",
				null);

		assertXPathValue(description,
				"reference[@name='multiple']/@interface",
				"java.util.EventListener");
		assertXPathValue(description,
				"reference[@name='multiple']/@bind",
				null);
		assertXPathValue(description,
				"reference[@name='multiple']/@unbind",
				null);
		assertXPathValue(description,
				"reference[@name='multiple']/@cardinality",
				"0..n");
		assertXPathValueIfSet(description,
				"reference[@name='multiple']/@policy",
				"static");
		assertXPathValue(description,
				"reference[@name='multiple']/@target",
				null);

		assertXPathValue(description,
				"reference[@name='atleastone']/@interface",
				"java.util.EventListener");
		assertXPathValue(description,
				"reference[@name='atleastone']/@bind",
				null);
		assertXPathValue(description,
				"reference[@name='atleastone']/@unbind",
				null);
		assertXPathValue(description,
				"reference[@name='atleastone']/@cardinality",
				"1..n");
		assertXPathValueIfSet(description,
				"reference[@name='atleastone']/@policy",
				"static");
		assertXPathValue(description,
				"reference[@name='atleastone']/@target",
				null);

		assertXPathValue(description,
				"reference[@name='greedy']/@interface",
				"java.util.EventListener");
		assertXPathValue(description,
				"reference[@name='greedy']/@bind",
				null);
		assertXPathValue(description,
				"reference[@name='greedy']/@unbind",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='greedy']/@cardinality",
				"1..1");
		assertXPathValueIfSet(description,
				"reference[@name='greedy']/@policy",
				"static");
		assertXPathValue(description, "reference[@name='greedy']/@target", null);

		assertXPathValue(description,
				"reference[@name='reluctant']/@interface",
				"java.util.EventListener");
		assertXPathValue(description,
				"reference[@name='reluctant']/@bind",
				null);
		assertXPathValue(description,
				"reference[@name='reluctant']/@unbind",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='reluctant']/@cardinality",
				"1..1");
		assertXPathValueIfSet(description,
				"reference[@name='reluctant']/@policy",
				"static");
		assertXPathValue(description,
				"reference[@name='reluctant']/@target",
				null);

		assertXPathValue(description,
				"reference[@name='update']/@interface",
				"java.util.EventListener");
		assertXPathValue(description,
				"reference[@name='update']/@bind",
				null);
		assertXPathValue(description,
				"reference[@name='update']/@unbind",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='update']/@cardinality",
				"1..1");
		assertXPathValueIfSet(description,
				"reference[@name='update']/@policy",
				"static");
		assertXPathValue(description,
				"reference[@name='update']/@target",
				null);

		assertXPathValue(description,
				"reference[@name='replace']/@interface",
				"java.util.EventListener");
		assertXPathValue(description,
				"reference[@name='replace']/@bind",
				null);
		assertXPathValue(description,
				"reference[@name='replace']/@unbind",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='replace']/@cardinality",
				"1..1");
		assertXPathValueIfSet(description,
				"reference[@name='replace']/@policy",
				"static");
		assertXPathValue(description,
				"reference[@name='replace']/@target",
				null);

		assertXPathValue(description,
				"reference[@name='target']/@interface",
				"java.util.EventListener");
		assertXPathValue(description,
				"reference[@name='target']/@bind",
				null);
		assertXPathValue(description,
				"reference[@name='target']/@unbind",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='target']/@cardinality",
				"1..1");
		assertXPathValueIfSet(description,
				"reference[@name='target']/@policy",
				"static");
		assertXPathValue(description,
				"reference[@name='target']/@target",
				"(test.attr=foo)");

		assertXPathValue(description,
				"reference[@name='bundle']/@interface",
				"java.util.EventListener");
		assertXPathValue(description,
				"reference[@name='bundle']/@bind",
				null);
		assertXPathValue(description,
				"reference[@name='bundle']/@unbind",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='bundle']/@cardinality",
				"1..1");
		assertXPathValue(description,
				"reference[@name='bundle']/@policy",
				"dynamic");
		assertXPathValue(description,
				"reference[@name='bundle']/@target",
				null);

		assertXPathValue(description,
				"reference[@name='prototype']/@interface",
				"java.util.EventListener");
		assertXPathValue(description,
				"reference[@name='prototype']/@bind",
				null);
		assertXPathValue(description,
				"reference[@name='prototype']/@unbind",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='prototype']/@cardinality",
				"1..1");
		assertXPathValueIfSet(description,
				"reference[@name='prototype']/@policy",
				"static");
		assertXPathValue(description,
				"reference[@name='prototype']/@target",
				null);

		assertXPathValue(description,
				"reference[@name='prototype_required']/@interface",
				"java.util.EventListener");
		assertXPathValue(description,
				"reference[@name='prototype_required']/@bind",
				null);
		assertXPathValue(description,
				"reference[@name='prototype_required']/@unbind",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='prototype_required']/@cardinality",
				"1..1");
		assertXPathValueIfSet(description,
				"reference[@name='prototype_required']/@policy",
				"static");
		assertXPathValue(description,
				"reference[@name='prototype_required']/@target",
				null);

		assertXPathValue(description,
				"reference[@name='reference']/@interface",
				"java.util.EventListener");
		assertXPathValue(description,
				"reference[@name='reference']/@bind",
				null);
		assertXPathValue(description,
				"reference[@name='reference']/@unbind",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='reference']/@cardinality",
				"1..1");
		assertXPathValueIfSet(description,
				"reference[@name='reference']/@policy",
				"static");
		assertXPathValue(description, "reference[@name='reference']/@target", null);

		assertXPathValue(description,
				"reference[@name='serviceobjects']/@interface",
				"java.util.EventListener");
		assertXPathValue(description,
				"reference[@name='serviceobjects']/@bind",
				null);
		assertXPathValue(description,
				"reference[@name='serviceobjects']/@unbind",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='serviceobjects']/@cardinality",
				"1..1");
		assertXPathValueIfSet(description,
				"reference[@name='serviceobjects']/@policy",
				"static");
		assertXPathValue(description, "reference[@name='serviceobjects']/@target", null);

		assertXPathValue(description,
				"reference[@name='properties']/@interface",
				"java.util.EventListener");
		assertXPathValue(description,
				"reference[@name='properties']/@bind",
				null);
		assertXPathValue(description,
				"reference[@name='properties']/@unbind",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='properties']/@cardinality",
				"1..1");
		assertXPathValue(description,
				"reference[@name='properties']/@policy",
				"dynamic");
		assertXPathValue(description, "reference[@name='properties']/@target", null);

		assertXPathValue(description,
				"reference[@name='tuple']/@interface",
				"java.util.EventListener");
		assertXPathValue(description,
				"reference[@name='tuple']/@bind",
				null);
		assertXPathValue(description,
				"reference[@name='tuple']/@unbind",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='tuple']/@cardinality",
				"1..1");
		assertXPathValueIfSet(description,
				"reference[@name='tuple']/@policy",
				"static");
		assertXPathValue(description, "reference[@name='tuple']/@target", null);

		assertXPathValue(description,
				"reference[@name='collection_reference']/@interface",
				"java.util.EventListener");
		assertXPathValue(description,
				"reference[@name='collection_reference']/@bind",
				null);
		assertXPathValue(description,
				"reference[@name='collection_reference']/@unbind",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='collection_reference']/@cardinality",
				"0..n");
		assertXPathValueIfSet(description,
				"reference[@name='collection_reference']/@policy",
				"dynamic");
		assertXPathValue(description, "reference[@name='collection_reference']/@target", null);

		assertXPathValue(description,
				"reference[@name='collection_serviceobjects']/@interface",
				"java.util.EventListener");
		assertXPathValue(description,
				"reference[@name='collection_serviceobjects']/@bind",
				null);
		assertXPathValue(description,
				"reference[@name='collection_serviceobjects']/@unbind",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='collection_serviceobjects']/@cardinality",
				"0..n");
		assertXPathValueIfSet(description,
				"reference[@name='collection_serviceobjects']/@policy",
				"static");
		assertXPathValue(description, "reference[@name='collection_serviceobjects']/@target", null);

		assertXPathValue(description,
				"reference[@name='collection_properties']/@interface",
				"java.util.EventListener");
		assertXPathValue(description,
				"reference[@name='collection_properties']/@bind",
				null);
		assertXPathValue(description,
				"reference[@name='collection_properties']/@unbind",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='collection_properties']/@cardinality",
				"0..n");
		assertXPathValueIfSet(description,
				"reference[@name='collection_properties']/@policy",
				"static");
		assertXPathValue(description, "reference[@name='collection_properties']/@target", null);

		assertXPathValue(description,
				"reference[@name='collection_tuple']/@interface",
				"java.util.EventListener");
		assertXPathValue(description,
				"reference[@name='collection_tuple']/@bind",
				null);
		assertXPathValue(description,
				"reference[@name='collection_tuple']/@unbind",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='collection_tuple']/@cardinality",
				"0..n");
		assertXPathValueIfSet(description,
				"reference[@name='collection_tuple']/@policy",
				"static");
		assertXPathValue(description, "reference[@name='collection_tuple']/@target", null);

		// v1.2.0
		assertXPathValueIfSet(description,
				"reference[@name='static']/@policy-option",
				"reluctant");
		assertXPathValue(description,
				"reference[@name='static']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='dynamic']/@policy-option",
				"reluctant");
		assertXPathValue(description,
				"reference[@name='dynamic']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='mandatory']/@policy-option",
				"reluctant");
		assertXPathValue(description,
				"reference[@name='mandatory']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='optional']/@policy-option",
				"reluctant");
		assertXPathValue(description,
				"reference[@name='optional']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='multiple']/@policy-option",
				"reluctant");
		assertXPathValue(description,
				"reference[@name='multiple']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='atleastone']/@policy-option",
				"reluctant");
		assertXPathValue(description,
				"reference[@name='atleastone']/@updated",
				null);

		assertXPathValue(description,
				"reference[@name='greedy']/@policy-option",
				"greedy");
		assertXPathValue(description,
				"reference[@name='greedy']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='reluctant']/@policy-option",
				"reluctant");
		assertXPathValue(description,
				"reference[@name='reluctant']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='update']/@policy-option",
				"reluctant");
		assertXPathValue(description,
				"reference[@name='update']/@update",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='replace']/@policy-option",
				"reluctant");
		assertXPathValue(description,
				"reference[@name='replace']/@update",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='target']/@policy-option",
				"reluctant");
		assertXPathValue(description,
				"reference[@name='target']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='bundle']/@policy-option",
				"reluctant");
		assertXPathValue(description,
				"reference[@name='bundle']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='prototype']/@policy-option",
				"reluctant");
		assertXPathValue(description,
				"reference[@name='prototype']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='prototype_required']/@policy-option",
				"reluctant");
		assertXPathValue(description,
				"reference[@name='prototype_required']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='reference']/@policy-option",
				"reluctant");
		assertXPathValue(description,
				"reference[@name='reference']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='serviceobjects']/@policy-option",
				"reluctant");
		assertXPathValue(description,
				"reference[@name='serviceobjects']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='properties']/@policy-option",
				"reluctant");
		assertXPathValue(description,
				"reference[@name='properties']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='tuple']/@policy-option",
				"reluctant");
		assertXPathValue(description,
				"reference[@name='tuple']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='collection_reference']/@policy-option",
				"reluctant");
		assertXPathValue(description,
				"reference[@name='collection_reference']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='collection_serviceobjects']/@policy-option",
				"reluctant");
		assertXPathValue(description,
				"reference[@name='collection_serviceobjects']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='collection_properties']/@policy-option",
				"reluctant");
		assertXPathValue(description,
				"reference[@name='collection_properties']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='collection_tuple']/@policy-option",
				"reluctant");
		assertXPathValue(description,
				"reference[@name='collection_tuple']/@updated",
				null);

		// v1.3.0
		assertXPathValue(description,
				"reference[@name='static']/@field",
				"fieldStatic");
		assertXPathValueIfSet(description,
				"reference[@name='static']/@field-option",
				"replace");
		assertXPathValue(description,
				"reference[@name='static']/@field-collection-type",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='static']/@scope",
				"bundle");

		assertXPathValue(description,
				"reference[@name='dynamic']/@field",
				"fieldDynamic");
		assertXPathValueIfSet(description,
				"reference[@name='dynamic']/@field-option",
				"replace");
		assertXPathValue(description,
				"reference[@name='dynamic']/@field-collection-type",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='dynamic']/@scope",
				"bundle");

		assertXPathValue(description,
				"reference[@name='mandatory']/@field",
				"fieldMandatory");
		assertXPathValueIfSet(description,
				"reference[@name='mandatory']/@field-option",
				"replace");
		assertXPathValue(description,
				"reference[@name='mandatory']/@field-collection-type",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='mandatory']/@scope",
				"bundle");

		assertXPathValue(description,
				"reference[@name='optional']/@field",
				"fieldOptional");
		assertXPathValueIfSet(description,
				"reference[@name='optional']/@field-option",
				"replace");
		assertXPathValue(description,
				"reference[@name='optional']/@field-collection-type",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='optional']/@scope",
				"bundle");

		assertXPathValue(description,
				"reference[@name='multiple']/@field",
				"fieldMultiple");
		assertXPathValueIfSet(description,
				"reference[@name='multiple']/@field-option",
				"replace");
		assertXPathValueIfSet(description,
				"reference[@name='multiple']/@field-collection-type",
				"service");
		assertXPathValueIfSet(description,
				"reference[@name='multiple']/@scope",
				"bundle");

		assertXPathValue(description,
				"reference[@name='atleastone']/@field",
				"fieldAtLeastOne");
		assertXPathValueIfSet(description,
				"reference[@name='atleastone']/@field-option",
				"replace");
		assertXPathValueIfSet(description,
				"reference[@name='atleastone']/@field-collection-type",
				"service");
		assertXPathValueIfSet(description,
				"reference[@name='atleastone']/@scope",
				"bundle");

		assertXPathValue(description,
				"reference[@name='greedy']/@field",
				"fieldGreedy");
		assertXPathValueIfSet(description,
				"reference[@name='greedy']/@field-option",
				"replace");
		assertXPathValue(description,
				"reference[@name='greedy']/@field-collection-type",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='greedy']/@scope",
				"bundle");

		assertXPathValue(description,
				"reference[@name='reluctant']/@field",
				"fieldReluctant");
		assertXPathValueIfSet(description,
				"reference[@name='reluctant']/@field-option",
				"replace");
		assertXPathValue(description,
				"reference[@name='reluctant']/@field-collection-type",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='reluctant']/@scope",
				"bundle");

		assertXPathValue(description,
				"reference[@name='update']/@field",
				"fieldUpdate");
		assertXPathValue(description,
				"reference[@name='update']/@field-option",
				"update");
		assertXPathValue(description,
				"reference[@name='update']/@field-collection-type",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='update']/@scope",
				"bundle");

		assertXPathValue(description,
				"reference[@name='replace']/@field",
				"fieldReplace");
		assertXPathValueIfSet(description,
				"reference[@name='replace']/@field-option",
				"replace");
		assertXPathValue(description,
				"reference[@name='replace']/@field-collection-type",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='replace']/@scope",
				"bundle");

		assertXPathValue(description,
				"reference[@name='target']/@field",
				"fieldTarget");
		assertXPathValueIfSet(description,
				"reference[@name='target']/@field-option",
				"replace");
		assertXPathValue(description,
				"reference[@name='target']/@field-collection-type",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='target']/@scope",
				"bundle");

		assertXPathValue(description,
				"reference[@name='bundle']/@field",
				"fieldBundle");
		assertXPathValueIfSet(description,
				"reference[@name='bundle']/@field-option",
				"replace");
		assertXPathValue(description,
				"reference[@name='bundle']/@field-collection-type",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='bundle']/@scope",
				"bundle");

		assertXPathValue(description,
				"reference[@name='prototype']/@field",
				"fieldPrototype");
		assertXPathValueIfSet(description,
				"reference[@name='prototype']/@field-option",
				"replace");
		assertXPathValue(description,
				"reference[@name='prototype']/@field-collection-type",
				null);
		assertXPathValue(description,
				"reference[@name='prototype']/@scope",
				"prototype");

		assertXPathValue(description,
				"reference[@name='prototype_required']/@field",
				"fieldPrototypeRequired");
		assertXPathValueIfSet(description,
				"reference[@name='prototype_required']/@field-option",
				"replace");
		assertXPathValue(description,
				"reference[@name='prototype_required']/@field-collection-type",
				null);
		assertXPathValue(description,
				"reference[@name='prototype_required']/@scope",
				"prototype_required");

		assertXPathValue(description,
				"reference[@name='reference']/@field",
				"fieldReference");
		assertXPathValueIfSet(description,
				"reference[@name='reference']/@field-option",
				"replace");
		assertXPathValue(description,
				"reference[@name='reference']/@field-collection-type",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='reference']/@scope",
				"bundle");

		assertXPathValue(description,
				"reference[@name='serviceobjects']/@field",
				"fieldServiceObjects");
		assertXPathValueIfSet(description,
				"reference[@name='serviceobjects']/@field-option",
				"replace");
		assertXPathValue(description,
				"reference[@name='serviceobjects']/@field-collection-type",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='serviceobjects']/@scope",
				"bundle");

		assertXPathValue(description,
				"reference[@name='properties']/@field",
				"fieldProperties");
		assertXPathValueIfSet(description,
				"reference[@name='properties']/@field-option",
				"replace");
		assertXPathValue(description,
				"reference[@name='properties']/@field-collection-type",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='properties']/@scope",
				"bundle");

		assertXPathValue(description,
				"reference[@name='tuple']/@field",
				"fieldTuple");
		assertXPathValueIfSet(description,
				"reference[@name='tuple']/@field-option",
				"replace");
		assertXPathValue(description,
				"reference[@name='tuple']/@field-collection-type",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='tuple']/@scope",
				"bundle");

		assertXPathValue(description,
				"reference[@name='collection_reference']/@field",
				"fieldReferenceM");
		assertXPathValue(description,
				"reference[@name='collection_reference']/@field-option",
				"update");
		assertXPathValue(description,
				"reference[@name='collection_reference']/@field-collection-type",
				"reference");
		assertXPathValueIfSet(description,
				"reference[@name='collection_reference']/@scope",
				"bundle");

		assertXPathValue(description,
				"reference[@name='collection_serviceobjects']/@field",
				"fieldServiceObjectsM");
		assertXPathValueIfSet(description,
				"reference[@name='collection_serviceobjects']/@field-option",
				"replace");
		assertXPathValue(description,
				"reference[@name='collection_serviceobjects']/@field-collection-type",
				"serviceobjects");
		assertXPathValueIfSet(description,
				"reference[@name='collection_serviceobjects']/@scope",
				"bundle");

		assertXPathValue(description,
				"reference[@name='collection_properties']/@field",
				"fieldPropertiesM");
		assertXPathValueIfSet(description,
				"reference[@name='collection_properties']/@field-option",
				"replace");
		assertXPathValue(description,
				"reference[@name='collection_properties']/@field-collection-type",
				"properties");
		assertXPathValueIfSet(description,
				"reference[@name='collection_properties']/@scope",
				"bundle");

		assertXPathValue(description,
				"reference[@name='collection_tuple']/@field",
				"fieldTupleM");
		assertXPathValueIfSet(description,
				"reference[@name='collection_tuple']/@field-option",
				"replace");
		assertXPathValue(description,
				"reference[@name='collection_tuple']/@field-collection-type",
				"tuple");
		assertXPathValueIfSet(description,
				"reference[@name='collection_tuple']/@scope",
				"bundle");
	}

	public void testReferenceScopes() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		// v1.0.0
		assertXPathCount(description, "reference", 3);

		assertXPathValue(description,
				"reference[@name='bundle']/@interface",
				"java.util.EventListener");
		assertXPathValue(description,
				"reference[@name='bundle']/@bind",
				"bindBundle");
		assertXPathValue(description,
				"reference[@name='bundle']/@unbind",
				"unbindBundle");
		assertXPathValueIfSet(description,
				"reference[@name='bundle']/@cardinality",
				"1..1");
		assertXPathValueIfSet(description,
				"reference[@name='bundle']/@policy",
				"static");
		assertXPathValue(description, "reference[@name='bundle']/@target", null);

		assertXPathValue(description,
				"reference[@name='prototype']/@interface",
				"java.util.EventListener");
		assertXPathValue(description,
				"reference[@name='prototype']/@bind",
				"bindPrototype");
		assertXPathValue(description,
				"reference[@name='prototype']/@unbind",
				"unbindPrototype");
		assertXPathValueIfSet(description,
				"reference[@name='prototype']/@cardinality",
				"1..1");
		assertXPathValueIfSet(description,
				"reference[@name='prototype']/@policy",
				"static");
		assertXPathValue(description, "reference[@name='prototype']/@target", null);

		assertXPathValue(description,
				"reference[@name='prototype_required']/@interface",
				"java.util.EventListener");
		assertXPathValue(description,
				"reference[@name='prototype_required']/@bind",
				"bindPrototypeRequired");
		assertXPathValue(description,
				"reference[@name='prototype_required']/@unbind",
				"unbindPrototypeRequired");
		assertXPathValueIfSet(description,
				"reference[@name='prototype_required']/@cardinality",
				"1..1");
		assertXPathValueIfSet(description,
				"reference[@name='prototype_required']/@policy",
				"static");
		assertXPathValue(description, "reference[@name='prototype_required']/@target", null);

		// v1.2.0
		assertXPathValueIfSet(description,
				"reference[@name='bundle']/@policy-option",
				"reluctant");
		assertXPathValue(description,
				"reference[@name='bundle']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='prototype']/@policy-option",
				"reluctant");
		assertXPathValue(description,
				"reference[@name='prototype']/@updated",
				null);

		assertXPathValueIfSet(description,
				"reference[@name='prototype_required']/@policy-option",
				"reluctant");
		assertXPathValue(description,
				"reference[@name='prototype_required']/@updated",
				null);

		// v1.3.0
		assertXPathValue(description,
				"reference[@name='bundle']/@field",
				null);
		assertXPathValue(description,
				"reference[@name='bundle']/@field-option",
				null);
		assertXPathValue(description,
				"reference[@name='bundle']/@field-collection-type",
				null);
		assertXPathValueIfSet(description,
				"reference[@name='bundle']/@scope",
				"bundle");

		assertXPathValue(description,
				"reference[@name='prototype']/@field",
				null);
		assertXPathValue(description,
				"reference[@name='prototype']/@field-option",
				null);
		assertXPathValue(description,
				"reference[@name='prototype']/@field-collection-type",
				null);
		assertXPathValue(description,
				"reference[@name='prototype']/@scope",
				"prototype");

		assertXPathValue(description,
				"reference[@name='prototype_required']/@field",
				null);
		assertXPathValue(description,
				"reference[@name='prototype_required']/@field-option",
				null);
		assertXPathValue(description,
				"reference[@name='prototype_required']/@field-collection-type",
				null);
		assertXPathValue(description,
				"reference[@name='prototype_required']/@scope",
				"prototype_required");

	}

	/**
	 * @throws Exception
	 */
	public void testReferenceNames() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		// v1.0.0
		assertXPathCount(description, "reference", 4);

		assertXPathValue(description, "reference[@name='NameAdd']/@bind",
				"addNameAdd");
		assertXPathValue(description, "reference[@name='NameAdd']/@unbind",
				"removeNameAdd");

		assertXPathValue(description, "reference[@name='NameSet']/@bind",
				"setNameSet");
		assertXPathValue(description, "reference[@name='NameSet']/@unbind",
				"unsetNameSet");

		assertXPathValue(description, "reference[@name='name']/@bind", "name");
		assertXPathValue(description, "reference[@name='name']/@unbind",
				"unname");

		assertXPathValue(description, "reference[@name='NameBind']/@bind",
				"bindNameBind");
		assertXPathValue(description, "reference[@name='NameBind']/@unbind",
				"unbindNameBind");
	}

	/**
	 * @throws Exception
	 */
	public void testReferenceService() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		// v1.0.0
		assertXPathValue(description, "reference/@interface",
				"java.util.EventListener");
		assertXPathValue(description, "reference/@bind", "bindObject");
	}

	/**
	 * @throws Exception
	 */
	public void testServiceFactory() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		// v1.0.0
		assertXPathValueIfSet(description, "@immediate", "false");
		assertXPathValueIfSet(description, "@enabled", "true");
		assertXPathValue(description, "@factory", null);
		assertXPathCount(description, "implementation", 1);
		assertXPathValue(
				description,
				"implementation/@class",
				"org.osgi.impl.bundle.component.annotations."
						+ name.substring(4));
		assertXPathCount(description, "service/provide", 1);
		assertXPathValue(description, "service/provide/@interface",
				"java.util.EventListener");
		boolean servicefactory = assertXPathValueIfSet(description, "service/@servicefactory", "true");
		assertXPathCount(description, "reference", 0);
		// v1.1.0
		assertXPathValueIfSet(description, "@configuration-policy", "optional");
		assertXPathValueIfSet(description, "@activate", "activate");
		assertXPathValueIfSet(description, "@deactivate", "deactivate");
		assertXPathValue(description, "@modified", null);
		// v1.2.0
		assertXPathValueIfSet(description, "@configuration-pid", name);
		assertXPathValueIfSet(description, "reference/@policy-option",
				"reluctant");
		assertXPathValue(description, "reference/@updated", null);
		// v1.3.0
		boolean scope = assertXPathValueIfSet(description, "service/@scope", "bundle");
		if (!servicefactory && !scope) {
			fail("neither servicefactory=\"true\" nor scope=\"bundle\" were specified");
		}
	}

	/**
	 * @throws Exception
	 */
	public void testNoServiceFactory() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		// v1.0.0
		assertXPathValueIfSet(description, "@immediate", "false");
		assertXPathValueIfSet(description, "@enabled", "true");
		assertXPathValue(description, "@factory", null);
		assertXPathCount(description, "implementation", 1);
		assertXPathValue(
				description,
				"implementation/@class",
				"org.osgi.impl.bundle.component.annotations."
						+ name.substring(4));
		assertXPathCount(description, "service/provide", 1);
		assertXPathValue(description, "service/provide/@interface",
				"java.util.EventListener");
		assertXPathValueIfSet(description, "service/@servicefactory", "false");
		assertXPathCount(description, "reference", 0);
		// v1.1.0
		assertXPathValueIfSet(description, "@configuration-policy", "optional");
		assertXPathValueIfSet(description, "@activate", "activate");
		assertXPathValueIfSet(description, "@deactivate", "deactivate");
		assertXPathValue(description, "@modified", null);
		// v1.2.0
		assertXPathValueIfSet(description, "@configuration-pid", name);
		assertXPathValueIfSet(description, "reference/@policy-option",
				"reluctant");
		assertXPathValue(description, "reference/@updated", null);
		// v1.3.0
		assertXPathValueIfSet(description, "service/@scope", "singleton");
	}

	public void testServiceSingleton() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		// v1.0.0
		assertXPathValueIfSet(description, "@immediate", "false");
		assertXPathValueIfSet(description, "@enabled", "true");
		assertXPathValue(description, "@factory", null);
		assertXPathCount(description, "implementation", 1);
		assertXPathValue(
				description,
				"implementation/@class",
				"org.osgi.impl.bundle.component.annotations."
						+ name.substring(4));
		assertXPathCount(description, "service/provide", 1);
		assertXPathValue(description,
				"service/provide/@interface",
				"java.util.EventListener");
		assertXPathValueIfSet(description, "service/@servicefactory", "false");
		assertXPathCount(description, "reference", 0);
		// v1.1.0
		assertXPathValueIfSet(description, "@configuration-policy", "optional");
		assertXPathValueIfSet(description, "@activate", "activate");
		assertXPathValueIfSet(description, "@deactivate", "deactivate");
		assertXPathValue(description, "@modified", null);
		// v1.2.0
		assertXPathValueIfSet(description, "@configuration-pid", name);
		assertXPathValueIfSet(description,
				"reference/@policy-option",
				"reluctant");
		assertXPathValue(description, "reference/@updated", null);
		// v1.3.0
		assertXPathValueIfSet(description, "service/@scope", "singleton");
	}

	public void testServiceBundle() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		// v1.0.0
		assertXPathValueIfSet(description, "@immediate", "false");
		assertXPathValueIfSet(description, "@enabled", "true");
		assertXPathValue(description, "@factory", null);
		assertXPathCount(description, "implementation", 1);
		assertXPathValue(
				description,
				"implementation/@class",
				"org.osgi.impl.bundle.component.annotations."
						+ name.substring(4));
		assertXPathCount(description, "service/provide", 1);
		assertXPathValue(description,
				"service/provide/@interface",
				"java.util.EventListener");
		boolean servicefactory = assertXPathValueIfSet(description, "service/@servicefactory", "true");
		assertXPathCount(description, "reference", 0);
		// v1.1.0
		assertXPathValueIfSet(description, "@configuration-policy", "optional");
		assertXPathValueIfSet(description, "@activate", "activate");
		assertXPathValueIfSet(description, "@deactivate", "deactivate");
		assertXPathValue(description, "@modified", null);
		// v1.2.0
		assertXPathValueIfSet(description, "@configuration-pid", name);
		assertXPathValueIfSet(description,
				"reference/@policy-option",
				"reluctant");
		assertXPathValue(description, "reference/@updated", null);
		// v1.3.0
		boolean scope = assertXPathValueIfSet(description, "service/@scope", "bundle");
		if (!servicefactory && !scope) {
			fail("neither servicefactory=\"true\" nor scope=\"bundle\" were specified");
		}
	}

	public void testServicePrototype() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		// v1.0.0
		assertXPathValueIfSet(description, "@immediate", "false");
		assertXPathValueIfSet(description, "@enabled", "true");
		assertXPathValue(description, "@factory", null);
		assertXPathCount(description, "implementation", 1);
		assertXPathValue(
				description,
				"implementation/@class",
				"org.osgi.impl.bundle.component.annotations."
						+ name.substring(4));
		assertXPathCount(description, "service/provide", 1);
		assertXPathValue(description,
				"service/provide/@interface",
				"java.util.EventListener");
		assertXPathValueIfSet(description, "service/@servicefactory", "false");
		assertXPathCount(description, "reference", 0);
		// v1.1.0
		assertXPathValueIfSet(description, "@configuration-policy", "optional");
		assertXPathValueIfSet(description, "@activate", "activate");
		assertXPathValueIfSet(description, "@deactivate", "deactivate");
		assertXPathValue(description, "@modified", null);
		// v1.2.0
		assertXPathValueIfSet(description, "@configuration-pid", name);
		assertXPathValueIfSet(description,
				"reference/@policy-option",
				"reluctant");
		assertXPathValue(description, "reference/@updated", null);
		// v1.3.0
		assertXPathValue(description, "service/@scope", "prototype");
	}

	/**
	 * @throws Exception
	 */
	public void testFactory() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		// v1.0.0
		assertXPathValueIfSet(description, "@immediate", "false");
		assertXPathValueIfSet(description, "@enabled", "true");
		assertXPathValue(description, "@factory", "test.factory.name");
		assertXPathCount(description, "implementation", 1);
		assertXPathValue(
				description,
				"implementation/@class",
				"org.osgi.impl.bundle.component.annotations."
						+ name.substring(4));
		assertXPathCount(description, "service/provide", 1);
		assertXPathValue(description, "service/provide/@interface",
				"java.util.EventListener");
		assertXPathValueIfSet(description, "service/@servicefactory", "false");
		assertXPathCount(description, "reference", 0);
		// v1.1.0
		assertXPathValueIfSet(description, "@configuration-policy", "optional");
		assertXPathValueIfSet(description, "@activate", "activate");
		assertXPathValueIfSet(description, "@deactivate", "deactivate");
		assertXPathValue(description, "@modified", null);
		// v1.2.0
		assertXPathValueIfSet(description, "@configuration-pid", name);
		assertXPathValueIfSet(description, "reference/@policy-option",
				"reluctant");
		assertXPathValue(description, "reference/@updated", null);
		// v1.3.0
		assertXPathValueIfSet(description, "service/@scope", "singleton");
	}

	/**
	 * @throws Exception
	 */
	public void testImmediate() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		// v1.0.0
		assertXPathValue(description, "@immediate", "true");
		assertXPathValueIfSet(description, "@enabled", "true");
	}

	/**
	 * @throws Exception
	 */
	public void testDelayed() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		// v1.0.0
		assertXPathValue(description, "@immediate", "false");
		assertXPathValueIfSet(description, "@enabled", "true");
	}

	/**
	 * @throws Exception
	 */
	public void testEnabled() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		// v1.0.0
		assertXPathValueIfSet(description, "@immediate", "true");
		assertXPathValue(description, "@enabled", "true");
	}

	/**
	 * @throws Exception
	 */
	public void testDisabled() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		// v1.0.0
		assertXPathValueIfSet(description, "@immediate", "true");
		assertXPathValue(description, "@enabled", "false");
	}

	/**
	 * @throws Exception
	 */
	public void testConfigPolicyOptional() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		// v1.1.0
		assertXPathValueIfSet(description, "@configuration-policy", "optional");
	}

	/**
	 * @throws Exception
	 */
	public void testConfigPolicyRequire() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		// v1.1.0
		assertXPathValue(description, "@configuration-policy", "require");
	}

	/**
	 * @throws Exception
	 */
	public void testConfigPolicyIgnore() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		// v1.1.0
		assertXPathValue(description, "@configuration-policy", "ignore");
	}

	/**
	 * @throws Exception
	 */
	public void testConfigPid() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		// v1.2.0
		assertXPathValue(description, "@configuration-pid", "test.config.pid");
	}

	public void testConfigPidMultiple() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		// v1.3.0
		assertXPathValue(description, "@configuration-pid[normalize-space()]", "test.config.pid " + name);
	}

	/**
	 * @throws Exception
	 */
	public void testProperties() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		// v1.0.0
		assertXPathCount(description, "properties", 1);
		assertXPathValue(description, "properties/@entry",
				"OSGI-INF/vendor.properties");
		assertXPathCount(description, "property", 10);
		assertPropertyValue(description, "a", "String", "foo");
		assertPropertyArrayValue(description, "b", "Integer", "2", "3");
		assertPropertyValue(description, "c", "Boolean", "true");
		assertPropertyValue(description, "d", "Long", "4");
		assertPropertyValue(description, "e", "Double", "5.0");
		assertPropertyValue(description, "f", "Float", "6.0");
		assertPropertyValue(description, "g", "Byte", "7");
		assertPropertyValue(description, "h", "Character", "8");
		assertPropertyValue(description, "i", "Short", "9");
		assertPropertyValue(description, "j", "String", "bar");
	}

	public void testPropertyOrdering() throws Exception {
		String name = getName();
		Description description = descriptions.get(name);
		assertNotNull("Unable to find " + name + " component description",
				description);
		// v1.0.0
		assertXPathCount(description, "properties", 1);
		assertXPathValue(description,
				"properties/@entry",
				"OSGI-INF/vendor.properties");
		assertXPathCount(description, "property", 29);

		assertPropertyValue(description, "config1", "String", "config1");
		assertPropertyValue(description, "config2", "String", "config2");
		assertPropertyValue(description, "config3", "String", "config3");
		assertPropertyValue(description, "config4", "String", "config4");

		assertPropertyValue(description, "string1", "String", "config/string1");
		assertPropertyValue(description, "string2", "String", "config/string2");
		assertPropertyValue(description, "string3", "String", "config/string3");
		assertPropertyValue(description, "string4", "String", "config/string4");

		assertPropertyArrayValue(description, "stringarray1", "String", "config/1stringarray1", "config/2stringarray1");

		assertPropertyValue(description, "boolean1", "Boolean", "true");
		assertPropertyArrayValue(description, "booleanarray1", "Boolean", "true", "false");

		assertPropertyValue(description, "char1", "Character", "64");
		assertPropertyArrayValue(description, "chararray1", "Character", "64", "43");

		assertPropertyValue(description, "byte1", "Byte", "2");
		assertPropertyArrayValue(description, "bytearray1", "Byte", "2", "-3");

		assertPropertyValue(description, "short1", "Short", "1034");
		assertPropertyArrayValue(description, "shortarray1", "Short", "1034", "-1043");

		assertPropertyValue(description, "int1", "Integer", "123456");
		assertPropertyArrayValue(description, "intarray1", "Integer", "123456", "-234567");

		assertPropertyValue(description, "long1", "Long", "9876543210");
		assertPropertyArrayValue(description, "longarray1", "Long", "9876543210", "-987654321");

		assertPropertyValue(description, "float1", "Float", "3.14");
		assertPropertyArrayValue(description, "floatarray1", "Float", "3.14", "-4.56");

		assertPropertyValue(description, "double1", "Double", "2.1");
		assertPropertyArrayValue(description, "doublearray1", "Double", "2.1", "-1.2");

		assertPropertyValue(description, "class1", "String", "org.osgi.impl.bundle.component.annotations.TestEnum");
		assertPropertyArrayValue(description, "classarray1", "String", "org.osgi.impl.bundle.component.annotations.TestEnum", "java.lang.Object");

		assertPropertyValue(description, "enum1", "String", "ITEM1");
		assertPropertyArrayValue(description, "enumarray1", "String", "ITEM1", "ITEM2");

		assertXPathValue(description, "@activate", "activate1");
		assertXPathValue(description, "@modified", "modified2");
		assertXPathValue(description, "@deactivate", "deactivate3");
	}

	public void assertPropertyValue(Description description, String name, String type, String value)
			throws Exception {
		final String expr = "property[@name='" + name + "']";
		assertXPathValue(description, expr + "/@value", value);
		if (type.equals("String")) {
			assertXPathValueIfSet(description, expr + "/@type", "String");
		} else {
			assertXPathValue(description, expr + "/@type", type);
		}
		Node result = getXPathValue(description, expr + "/text()");
		if (result == null) {
			return;
		}
		assertEquals(expr + "/text() evaluated on component " + description.getName(), "", result.getNodeValue().trim());
	}

	public void assertPropertyArrayValue(Description description, String name, String type, String... list)
			throws Exception {
		final String expr = "property[@name='" + name + "']";
		assertXPathValue(description, expr + "/@value", null);
		if (type.equals("String")) {
			assertXPathValueIfSet(description, expr + "/@type", "String");
		} else {
			assertXPathValue(description, expr + "/@type", type);
		}
		Node result = getXPathValue(description, expr + "/text()");
		assertNotNull(expr + "/text() evaluated to a null value on component "
				+ description.getName(), result);
		String[] values = result.getNodeValue().trim().split("\\s*\\n\\s*");
		final int size = list.length;
		assertEquals("incorrect number of values: " + result, size, values.length);
		for (int i = 0; i < size; i++) {
			assertEquals(expr + "/text() item[" + i + "] evaluated on component " + description.getName(),
					list[i],
					values[i]);
		}
	}

	/**
	 * @param description
	 * @param expr
	 * @return result
	 * @throws Exception
	 */
	public Node getXPathValue(Description description, String expr)
			throws Exception {
		xpath.setNamespaceContext(description.getNamespaceContext());
		Node result = (Node) xpath.evaluate(expr, description.getComponent(),
				XPathConstants.NODE);
		return result;
	}


	/**
	 * @param description
	 * @param expr
	 * @param value
	 * @throws Exception
	 */
	public void assertXPathValue(Description description, String expr,
			String value) throws Exception {
		Node result = getXPathValue(description, expr);
		if (value == null) {
			assertNull(expr + " evaluated to a non-null value for component "
					+ description.getName(), result);
			return;
		}
		assertNotNull(expr + " evaluated to a null value on component "
				+ description.getName(), result);
		assertEquals(expr + " evaluated on component " + description.getName(),
				value, result.getNodeValue());
	}

	/**
	 * @param description
	 * @param expr
	 * @param value
	 * @return boolean
	 * @throws Exception
	 */
	public boolean assertXPathValueIfSet(Description description, String expr,
			String value) throws Exception {
		Node result = getXPathValue(description, expr);
		if (result == null) {
			return false;
		}
		assertEquals(expr + " evaluated on component " + description.getName(),
				value, result.getNodeValue());
		return true;
	}

	/**
	 * @param description
	 * @param expr
	 * @param value
	 * @throws Exception
	 */
	public void assertXPathCount(Description description, String expr, int value)
			throws Exception {
		xpath.setNamespaceContext(description.getNamespaceContext());
		expr = "count(" + expr + ")";
		String result = (String) xpath.evaluate(expr,
				description.getComponent(), XPathConstants.STRING);
		assertNotNull(expr + " evaluated to a null value on component "
				+ description.getName(), result);
		assertEquals(expr + " evaluated on component " + description.getName(),
				value, Integer.valueOf(result).intValue());
	}

	private String getComponentName(Element component) throws Exception {
		String name = (String) xpath.evaluate("@name", component,
				XPathConstants.STRING);
		if (name.length() == 0) {
			name = (String) xpath.evaluate("implementation/@class", component,
					XPathConstants.STRING);
		}
		return name;
	}

	/* For test debugging use */
	@SuppressWarnings("unused")
	private static String toString(Node node) throws Exception {
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

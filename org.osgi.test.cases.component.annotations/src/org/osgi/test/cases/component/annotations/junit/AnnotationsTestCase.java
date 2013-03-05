/*
 * Copyright (c) OSGi Alliance (2012, 2013). All Rights Reserved.
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
		String bsn = System.getProperty(propName);
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
				+ xmlns_scr120 + "']");
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
		assertXPathValue(description, "service/@servicefactory", "true");
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
		assertXPathValueIfSet(description, "property[@name='a']/@type",
				"String");
		assertXPathValue(description, "property[@name='b']/@type", "Integer");
		assertXPathValue(description, "property[@name='c']/@type", "Boolean");
		assertXPathValue(description, "property[@name='d']/@type", "Long");
		assertXPathValue(description, "property[@name='e']/@type", "Double");
		assertXPathValue(description, "property[@name='f']/@type", "Float");
		assertXPathValue(description, "property[@name='g']/@type", "Byte");
		assertXPathValue(description, "property[@name='h']/@type", "Character");
		assertXPathValue(description, "property[@name='i']/@type", "Short");
		assertXPathValueIfSet(description, "property[@name='j']/@type",
				"String");
		assertXPathValue(description, "property[@name='a']/@value", "foo");
		assertXPathValue(description, "property[@name='c']/@value", "true");
		assertXPathValue(description, "property[@name='d']/@value", "4");
		assertXPathValue(description, "property[@name='e']/@value", "5.0");
		assertXPathValue(description, "property[@name='f']/@value", "6.0");
		assertXPathValue(description, "property[@name='g']/@value", "7");
		assertXPathValue(description, "property[@name='h']/@value", "8");
		assertXPathValue(description, "property[@name='i']/@value", "9");
		assertXPathValue(description, "property[@name='j']/@value", "bar");
		assertXPathTextEmpty(description, "property[@name='a']");
		assertXPathTextEmpty(description, "property[@name='c']");
		assertXPathTextEmpty(description, "property[@name='d']");
		assertXPathTextEmpty(description, "property[@name='e']");
		assertXPathTextEmpty(description, "property[@name='f']");
		assertXPathTextEmpty(description, "property[@name='g']");
		assertXPathTextEmpty(description, "property[@name='h']");
		assertXPathTextEmpty(description, "property[@name='i']");
		assertXPathTextEmpty(description, "property[@name='j']");
		assertXPathValue(description, "property[@name='b']/@value", null);
		String expr = "property[@name='b']/text()";
		Node node = getXPathValue(description, expr);
		assertNotNull(expr + " evaluated to a null value on component "
				+ description.getName(), node);
		String result = node.getNodeValue();
		List<String> values = Arrays.asList(result.trim().split("\\s*\\n\\s*"));
		assertEquals("incorrect number of values: " + result, 2, values.size());
		assertTrue("missing value", values.contains("2"));
		assertTrue("missing value", values.contains("3"));
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
	 * @throws Exception
	 */
	public void assertXPathTextEmpty(Description description, String expr)
			throws Exception {
		xpath.setNamespaceContext(description.getNamespaceContext());
		expr = expr + "/text()";
		Node result = (Node) xpath.evaluate(expr, description.getComponent(),
				XPathConstants.NODE);
		if (result == null) {
			return;
		}
		assertEquals(expr + " evaluated on component " + description.getName(),
				"", result.getNodeValue().trim());
	}

	/**
	 * @param description
	 * @param expr
	 * @param value
	 * @throws Exception
	 */
	public void assertXPathValue(Description description, String expr,
			String value) throws Exception {
		xpath.setNamespaceContext(description.getNamespaceContext());
		Node result = (Node) xpath.evaluate(expr, description.getComponent(),
				XPathConstants.NODE);
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
	 * @throws Exception
	 */
	public void assertXPathValueIfSet(Description description, String expr,
			String value) throws Exception {
		xpath.setNamespaceContext(description.getNamespaceContext());
		Node result = (Node) xpath.evaluate(expr, description.getComponent(),
				XPathConstants.NODE);
		if (result == null) {
			return;
		}
		assertEquals(expr + " evaluated on component " + description.getName(),
				value, result.getNodeValue());
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

	private static String toString(Node node) throws Exception {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		DOMSource source = new DOMSource(node);
		StreamResult result = new StreamResult(new StringWriter());
		transformer.transform(source, result);
		return result.getWriter().toString();
	}
}

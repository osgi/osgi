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

import static org.assertj.core.api.Assertions.assertThat;
import static org.osgi.test.cases.metatype.annotations.junit.OCDXPathAssert.assertThat;

import java.io.StringWriter;
import java.net.URL;
import java.util.Enumeration;
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

import org.junit.Assert;
import org.junit.Before;
import org.osgi.framework.Bundle;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.test.support.junit4.AbstractOSGiTestCase;
import org.osgi.test.support.xpath.BaseNamespaceContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author $Id$
 */
public abstract class AnnotationsTestCase extends AbstractOSGiTestCase {
	public static final String		xmlns_metatype120	= "http://www.osgi.org/xmlns/metatype/v1.2.0";
	public static final String		xmlns_metatype130	= "http://www.osgi.org/xmlns/metatype/v1.3.0";
	public static final String		xmlns_metatype140	= "http://www.osgi.org/xmlns/metatype/v1.4.0";
	protected static final float	deltaFloat			= 0x1.0p-126f;
	protected Map<String,OCD>		ocds;
	protected Map<String,Designate>	designatePids;
	protected Map<String,Designate>	designateFactoryPids;

	/**
	 * @throws Exception
	 */
	@Before
	public void loadMetatypeResources() throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();

		XPathFactory xpf = XPathFactory.newInstance();
		XPath xpath = xpf.newXPath();

		ocds = new HashMap<String,OCD>();
		designatePids = new HashMap<String,Designate>();
		designateFactoryPids = new HashMap<String,Designate>();

		final String propName = "org.osgi.test.cases.metatype.annotations.bundle.symbolic.name";
		String bsn = getProperty(propName);
		assertThat(bsn).as(
				"The system property \"%s\" must be set to the Bundle Symbolic Name of the bundle processed by the Metatype Annotations tool.",
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

		Enumeration<URL> documents = impl.findEntries(
				MetaTypeService.METATYPE_DOCUMENTS_LOCATION, "*.xml", false);
		Assert.assertTrue(
				MetaTypeService.METATYPE_DOCUMENTS_LOCATION
						+ " folder has no metatype documents",
				documents.hasMoreElements());
		XPathExpression expr = xpath.compile("//namespace::*[.='"
				+ xmlns_metatype120 + "' or .='" + xmlns_metatype130
				+ "' or .='" + xmlns_metatype140 + "']");
		while (documents.hasMoreElements()) {
			URL url = documents.nextElement();
			// System.out.println(url);
			Document document = db.parse(url.openStream());

			NodeList namespaces = (NodeList) expr.evaluate(document,
					XPathConstants.NODESET);
			for (int i = 0; i < namespaces.getLength(); i++) {
				Attr namespace = (Attr) namespaces.item(i);
				BaseNamespaceContext context = new BaseNamespaceContext(
						namespace);
				xpath.setNamespaceContext(context);
				NodeList elements = (NodeList) xpath.evaluate(
						"//" + namespace.getLocalName() + ":MetaData/OCD",
						namespace.getOwnerElement(), XPathConstants.NODESET);
				for (int j = 0; j < elements.getLength(); j++) {
					Element element = (Element) elements.item(j);
					// System.out.println(toString(element));
					String id = getOCDId(xpath, element);
					assertThat(ocds).as("multiple OCDs with the same id %s", id)
							.doesNotContainKey(id);
					ocds.put(id, new OCD(id, context, element));
				}
				elements = (NodeList) xpath.evaluate(
						"//" + namespace.getLocalName() + ":MetaData/Designate",
						namespace.getOwnerElement(), XPathConstants.NODESET);
				for (int j = 0; j < elements.getLength(); j++) {
					Element element = (Element) elements.item(j);
					// System.out.println(toString(element));
					String factoryPid = getDesignateFactoryPid(xpath, element);
					if (factoryPid.length() > 0) {
						assertThat(designateFactoryPids).as(
								"multiple Designate with the same factoryPid %s",
								factoryPid).doesNotContainKey(factoryPid);
						designateFactoryPids.put(factoryPid,
								new Designate(factoryPid, context, element));
						continue;
					}
					String pid = getDesignatePid(xpath, element);
					assertThat(designatePids)
							.as("multiple Designate with the same pid %s", pid)
							.doesNotContainKey(pid);
					designatePids.put(pid,
							new Designate(pid, context, element));
				}
			}
		}
	}

	public String getSelf(OCD ocd) {
		final String expr = "AD[@id='self']/@default";
		Node result = assertThat(ocd).isNotNull().getNode(expr);
		assertThat(result).as("self %s", expr).isNotNull();
		String self = result.getNodeValue();
		return self;
	}

	private String getOCDId(XPath xpath, Element ocd) throws Exception {
		String id = (String) xpath.evaluate("@id", ocd, XPathConstants.STRING);
		return id;
	}

	private String getDesignatePid(XPath xpath, Element designate)
			throws Exception {
		String pid = (String) xpath.evaluate("@pid", designate,
				XPathConstants.STRING);
		return pid;
	}

	private String getDesignateFactoryPid(XPath xpath, Element designate)
			throws Exception {
		String factoryPid = (String) xpath.evaluate("@factoryPid", designate,
				XPathConstants.STRING);
		return factoryPid;
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

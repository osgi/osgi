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

package org.osgi.test.cases.metatype.annotations.junit;

import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
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
import org.osgi.service.metatype.MetaTypeService;
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
	public static final String				xmlns_metatype120	= "http://www.osgi.org/xmlns/metatype/v1.2.0";
	public static final String		xmlns_metatype130	= "http://www.osgi.org/xmlns/metatype/v1.3.0";
	static final float				deltaFloat			= 0x1.0p-126f;
	private final DocumentBuilder	db;
	private final XPath				xpath;
	private final Map<String, OCD>	ocds;
	private final Map<String, Designate>	designatePids;
	private final Map<String, Designate>	designateFactoryPids;

	/**
	 * @throws Exception
	 */
	public AnnotationsTestCase() throws Exception {
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		db = dbf.newDocumentBuilder();

		final XPathFactory xpf = XPathFactory.newInstance();
		xpath = xpf.newXPath();

		ocds = new HashMap<String, OCD>();
		designatePids = new HashMap<String, Designate>();
		designateFactoryPids = new HashMap<String, Designate>();
	}

	protected void setUp() throws Exception {
		final String propName = "org.osgi.test.cases.metatype.annotations.bundle.symbolic.name";
		String bsn = getProperty(propName);
		assertNotNull(
				"The system property \""
						+ propName
						+ "\" must be set to the Bundle Symbolic Name of the bundle processed by the Metatype Annotations tool.",
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

		Enumeration<URL> documents = impl.findEntries(MetaTypeService.METATYPE_DOCUMENTS_LOCATION, "*.xml", false);
		assertTrue(MetaTypeService.METATYPE_DOCUMENTS_LOCATION + " folder has no metatype documents",
				documents.hasMoreElements());
		XPathExpression expr = xpath.compile("//namespace::*[.='"
				+ xmlns_metatype120 + "' or .='" + xmlns_metatype130 + "']");
		while (documents.hasMoreElements()) {
			URL url = documents.nextElement();
			// System.out.println(url);
			Document document = db.parse(url.openStream());

			NodeList namespaces = (NodeList) expr.evaluate(document,
					XPathConstants.NODESET);
			for (int i = 0; i < namespaces.getLength(); i++) {
				Attr namespace = (Attr) namespaces.item(i);
				NamespaceContextImpl context = new NamespaceContextImpl(
						namespace);
				xpath.setNamespaceContext(context);
				NodeList elements = (NodeList) xpath.evaluate("//"
						+ namespace.getLocalName() + ":MetaData/OCD",
						namespace.getOwnerElement(), XPathConstants.NODESET);
				for (int j = 0; j < elements.getLength(); j++) {
					Element element = (Element) elements.item(j);
					// System.out.println(toString(element));
					String id = getOCDId(element);
					assertNull("multiple OCD with the same id",
							ocds.put(id, new OCD(id, context, element)));
				}
				elements = (NodeList) xpath.evaluate("//"
						+ namespace.getLocalName() + ":MetaData/Designate",
						namespace.getOwnerElement(),
						XPathConstants.NODESET);
				for (int j = 0; j < elements.getLength(); j++) {
					Element element = (Element) elements.item(j);
					// System.out.println(toString(element));
					String factoryPid = getDesignateFactoryPid(element);
					if (factoryPid.length() > 0) {
						assertNull("multiple Designate with the same factoryPid",
								designateFactoryPids.put(factoryPid, new Designate(factoryPid, context, element)));
						continue;
					}
					String pid = getDesignatePid(element);
					assertNull("multiple Designate with the same pid",
							designatePids.put(pid, new Designate(pid, context, element)));
				}
			}
		}
	}

	public void testConfigurationPropertyType() throws Exception {
		String name = getName();
		OCD ocd = ocds.get(name);
		assertNotNull("Unable to find " + name + " OCD", ocd);
		String expr = "AD[@id='self']/@default";
		Node self = getXPathValue(ocd, expr);
		assertNotNull("Unable to find " + expr + " in OCD", self);
		assertXPathValue(ocd, "../@localization", "OSGI-INF/l10n/" + self.getNodeValue());
		assertXPathValue(ocd, "@description", "");
		Node result = getXPathValue(ocd, "text()");
		if (result != null) {
			assertEquals("text() evaluated on OCD[@id='" + ocd.getId() + "']", "", result.getNodeValue().trim());
		}
		assertAD(ocd, "string1", "String", 0, "config/string1");
		assertAD(ocd, "stringarray1", "String", 1, "config/1stringarray1", "config/2stringarray1");

		assertAD(ocd, "boolean1", "Boolean", 0, "true");
		assertAD(ocd, "booleanarray1", "Boolean", 1, "true", "false");

		final String CHARACTER = xmlns_metatype120.equals(ocd.getNamespaceContext().getUri()) ? "Char" : "Character";
		assertAD(ocd, "char1", CHARACTER, 0, "64");
		assertAD(ocd, "chararray1", CHARACTER, 1, "64", "43");

		assertAD(ocd, "byte1", "Byte", 0, "2");
		assertAD(ocd, "bytearray1", "Byte", 1, "2", "-3");

		assertAD(ocd, "short1", "Short", 0, "1034");
		assertAD(ocd, "shortarray1", "Short", 1, "1034", "-1043");

		assertAD(ocd, "int1", "Integer", 0, "123456");
		assertAD(ocd, "intarray1", "Integer", 1, "123456", "-234567");

		assertAD(ocd, "long1", "Long", 0, "9876543210");
		assertAD(ocd, "longarray1", "Long", 1, "9876543210", "-987654321");

		assertAD(ocd, "float1", "Float", 0, "3.14");
		assertAD(ocd, "floatarray1", "Float", 1, "3.14", "-4.56");

		assertAD(ocd, "double1", "Double", 0, "2.1");
		assertAD(ocd, "doublearray1", "Double", 1, "2.1", "-1.2");

		assertAD(ocd, "class1", "String", 0, "org.osgi.impl.bundle.metatype.annotations.TestEnum");
		assertAD(ocd, "classarray1", "String", 1, "org.osgi.impl.bundle.metatype.annotations.TestEnum", "java.lang.Object");

		assertAD(ocd, "enum1", "String", 0, "ITEM1");
		assertXPathCount(ocd, "AD[@id='enum1']/Option", 4);
		assertOption(ocd, "enum1", "ITEM1", "ITEM1");
		assertOption(ocd, "enum1", "ITEM2", "ITEM2");
		assertOption(ocd, "enum1", "ITEM3", "ITEM3");
		assertOption(ocd, "enum1", "ITEM4", "ITEM4");

		assertAD(ocd, "enumarray1", "String", 1, "ITEM1", "ITEM2");
		assertXPathCount(ocd, "AD[@id='enumarray1']/Option", 4);
		assertOption(ocd, "enumarray1", "ITEM1", "ITEM1");
		assertOption(ocd, "enumarray1", "ITEM2", "ITEM2");
		assertOption(ocd, "enumarray1", "ITEM3", "ITEM3");
		assertOption(ocd, "enumarray1", "ITEM4", "ITEM4");

		assertAD(ocd, ".password1", "Password", 0, "config/password1");
		assertAD(ocd, ".passwordarray1", "Password", 1, "config/1passwordarray1", "config/2passwordarray1");
	}

	public void testSubInterface() throws Exception {
		String name = getName();
		OCD ocd = ocds.get(name);
		assertNotNull("Unable to find " + name + " OCD", ocd);
		assertXPathValue(ocd, "@description", "");
		Node result = getXPathValue(ocd, "text()");
		if (result != null) {
			assertEquals("text() evaluated on OCD[@id='" + ocd.getId() + "']", "", result.getNodeValue().trim());
		}
		assertAD(ocd, "string1", "String", 0);
		assertAD(ocd, "string2", "String", 0);
		assertAD(ocd, "boolean1", "Boolean", 0);
		final String CHARACTER = xmlns_metatype120.equals(ocd.getNamespaceContext().getUri()) ? "Char" : "Character";
		assertAD(ocd, "char1", CHARACTER, 0);
		assertAD(ocd, "byte1", "Byte", 0);
		assertAD(ocd, "short1", "Short", 0);
		assertAD(ocd, "int1", "Integer", 0);
		assertAD(ocd, "long1", "Long", 0);
		assertAD(ocd, "float1", "Float", 0);
		assertAD(ocd, "double1", "Double", 0);
		assertAD(ocd, "class1", "String", 0);
		assertAD(ocd, "enum1", "String", 0);
		assertAD(ocd, "enumcoll2", "String", -1);
		assertAD(ocd, "enumlist2", "String", -1);
		assertAD(ocd, "enumset2", "String", -1);
		assertAD(ocd, "enumiter2", "String", -1);
		assertAD(ocd, "enumarraylist2", "String", -1);

		assertXPathCount(ocd, "AD[@id='enum1']/Option", 4);
		assertOption(ocd, "enum1", "ITEM1", "ITEM1");
		assertOption(ocd, "enum1", "ITEM2", "ITEM2");
		assertOption(ocd, "enum1", "ITEM3", "ITEM3");
		assertOption(ocd, "enum1", "ITEM4", "ITEM4");

		assertXPathCount(ocd, "AD[@id='enumcoll2']/Option", 4);
		assertOption(ocd, "enumcoll2", "ITEM1", "ITEM1");
		assertOption(ocd, "enumcoll2", "ITEM2", "ITEM2");
		assertOption(ocd, "enumcoll2", "ITEM3", "ITEM3");
		assertOption(ocd, "enumcoll2", "ITEM4", "ITEM4");

		assertXPathCount(ocd, "AD[@id='enumlist2']/Option", 4);
		assertOption(ocd, "enumlist2", "ITEM1", "ITEM1");
		assertOption(ocd, "enumlist2", "ITEM2", "ITEM2");
		assertOption(ocd, "enumlist2", "ITEM3", "ITEM3");
		assertOption(ocd, "enumlist2", "ITEM4", "ITEM4");

		assertXPathCount(ocd, "AD[@id='enumset2']/Option", 4);
		assertOption(ocd, "enumset2", "ITEM1", "ITEM1");
		assertOption(ocd, "enumset2", "ITEM2", "ITEM2");
		assertOption(ocd, "enumset2", "ITEM3", "ITEM3");
		assertOption(ocd, "enumset2", "ITEM4", "ITEM4");

		assertXPathCount(ocd, "AD[@id='enumiter2']/Option", 4);
		assertOption(ocd, "enumiter2", "ITEM1", "ITEM1");
		assertOption(ocd, "enumiter2", "ITEM2", "ITEM2");
		assertOption(ocd, "enumiter2", "ITEM3", "ITEM3");
		assertOption(ocd, "enumiter2", "ITEM4", "ITEM4");

		assertXPathCount(ocd, "AD[@id='enumarraylist2']/Option", 4);
		assertOption(ocd, "enumarraylist2", "ITEM1", "ITEM1");
		assertOption(ocd, "enumarraylist2", "ITEM2", "ITEM2");
		assertOption(ocd, "enumarraylist2", "ITEM3", "ITEM3");
		assertOption(ocd, "enumarraylist2", "ITEM4", "ITEM4");
	}

	public void testNameMapping() throws Exception {
		String name = getName();
		OCD ocd = ocds.get(name);
		assertNotNull("Unable to find " + name + " OCD", ocd);
		assertXPathValue(ocd, "@description", "");
		Node result = getXPathValue(ocd, "text()");
		if (result != null) {
			assertEquals("text() evaluated on OCD[@id='" + ocd.getId() + "']", "", result.getNodeValue().trim());
		}
		assertAD(ocd, "myProperty143", "String", 0);
		assertAD(ocd, "new", "String", 0);
		assertAD(ocd, "my$prop", "String", 0);
		assertAD(ocd, "dot.prop", "String", 0);
		assertAD(ocd, ".secret", "String", 0);
		assertAD(ocd, "another_prop", "String", 0);
		assertAD(ocd, "three_.prop", "String", 0);
		assertAD(ocd, "four._prop", "String", 0);
		assertAD(ocd, "five..prop", "String", 0);
	}

	public void testNoDefaults() throws Exception {
		String name = getName();
		OCD ocd = ocds.get(name);
		assertNotNull("Unable to find " + name + " OCD", ocd);
		Node result = getXPathValue(ocd, "text()");
		if (result != null) {
			assertEquals("text() evaluated on OCD[@id='" + ocd.getId() + "']", "", result.getNodeValue().trim());
		}
		assertXPathValue(ocd, "../@localization", "OSGI-INF/l10n/member");

		assertXPathValue(ocd, "@description", "%member.description");
		assertXPathValue(ocd, "@name", "%member.name");
		assertAD(ocd, ".password", "Password", 0);
		assertXPathValue(ocd, "AD[@id='.password']/@description", "%member.password.description");
		assertXPathValue(ocd, "AD[@id='.password']/@name", "%member.password.name");
		assertAD(ocd, "type", "String", 12, "contributing");
		assertXPathValue(ocd, "AD[@id='type']/@cardinality", "12");
		assertXPathValue(ocd, "AD[@id='type']/@max", "max1");
		assertXPathValue(ocd, "AD[@id='type']/@min", "min1");
		assertXPathCount(ocd, "AD[@id='type']/Option", 3);
		assertOption(ocd, "type", "%strategic", "strategic");
		assertOption(ocd, "type", "%principal", "principal");
		assertOption(ocd, "type", "%contributing", "contributing");
		assertXPathCount(ocd, "Icon", 2);
		assertXPathValue(ocd, "Icon[@resource='icon/member-32.png']/@size", "32");
		assertXPathValue(ocd, "Icon[@resource='icon/member-64.png']/@size", "64");
		for (String pid : Arrays.asList("pid1", "pid2")) {
			Designate designate = designatePids.get(pid);
			assertNotNull("Unable to find pid " + pid + " Designate", designate);
			assertXPathValue(designate, "Object/@ocdref", "testNoDefaults");
			assertXPathCount(designate, "@factoryPid", 0);
		}
		for (String factoryPid : Arrays.asList("factoryPid1", "factoryPid2")) {
			Designate designate = designateFactoryPids.get(factoryPid);
			assertNotNull("Unable to find factoryPid " + factoryPid + " Designate", designate);
			assertXPathValue(designate, "Object/@ocdref", "testNoDefaults");
			assertXPathCount(designate, "@pid", 0);
		}
	}

	public void testDesignateConfig() throws Exception {
		String name = getName();
		OCD ocd = ocds.get(name);
		assertNotNull("Unable to find " + name + " OCD", ocd);
		Node result = getXPathValue(ocd, "text()");
		if (result != null) {
			assertEquals("text() evaluated on OCD[@id='" + ocd.getId() + "']", "", result.getNodeValue().trim());
		}

		assertXPathValue(ocd, "@description", "");
		assertAD(ocd, "string1", "String", 0, "config/string1");

		String pid = "testDesignateComponent";
		Designate designate = designatePids.get(pid);
		assertNotNull("Unable to find pid " + pid + " Designate", designate);
		assertXPathValue(designate, "Object/@ocdref", name);
		assertXPathCount(designate, "@factoryPid", 0);
	}

	public void testDesignateFactoryConfig() throws Exception {
		String name = getName();
		OCD ocd = ocds.get(name);
		assertNotNull("Unable to find " + name + " OCD", ocd);
		Node result = getXPathValue(ocd, "text()");
		if (result != null) {
			assertEquals("text() evaluated on OCD[@id='" + ocd.getId() + "']", "", result.getNodeValue().trim());
		}

		assertXPathValue(ocd, "@description", "");
		assertAD(ocd, "string1", "String", 0, "config/string1");

		String factoryPid = "testDesignateFactoryComponent";
		Designate designate = designateFactoryPids.get(factoryPid);
		assertNotNull("Unable to find factoryPid " + factoryPid + " Designate", designate);
		assertXPathValue(designate, "Object/@ocdref", name);
		assertXPathCount(designate, "@pid", 0);
	}

	public void assertOption(OCD ocd, String ad, String label, String value) throws Exception {
		assertXPathValue(ocd, "AD[@id='" + ad + "']/Option[@label='" + label + "']/@value", value);
	}

	public void assertAD(OCD ocd, String ad, String type, int cardinality, String... list)
			throws Exception {
		final String expr = "AD[@id='" + ad + "']";
		assertXPathValue(ocd, expr + "/@type", type);
		Node result = getXPathValue(ocd, expr + "/@cardinality");
		int c = (result == null) ? 0 : Integer.parseInt(result.getNodeValue());
		assertEquals("bad " + expr + "/@cardinality in OCD", Math.signum(cardinality), Math.signum(c), deltaFloat);

		final int size = list.length;
		if (size > 0) {
			result = getXPathValue(ocd, expr + "/@default");
			assertNotNull(expr + "/@default evaluated to a null value on OCD "
					+ ocd.getId(), result);
			String[] values = result.getNodeValue().trim().split("\\s*,\\s*");
			assertEquals("incorrect number of values: " + result, size, values.length);
			for (int i = 0; i < size; i++) {
				assertEquals(expr + "/@default item[" + i + "] evaluated on OCD " + ocd.getId(),
						list[i],
						values[i]);
			}
		}

		result = getXPathValue(ocd, expr + "/text()");
		if (result != null) {
			assertEquals(expr + "/text() evaluated on OCD[@id='" + ocd.getId() + "']/" + expr, "", result.getNodeValue().trim());
		}
	}

	/**
	 * @param description
	 * @param expr
	 * @return result
	 * @throws Exception
	 */
	public Node getXPathValue(BaseElement element, String expr)
			throws Exception {
		xpath.setNamespaceContext(element.getNamespaceContext());
		Node result = (Node) xpath.evaluate(expr, element.getElement(), XPathConstants.NODE);
		return result;
	}

	/**
	 * @param description
	 * @param expr
	 * @param value
	 * @throws Exception
	 */
	public void assertXPathValue(BaseElement element, String expr,
			String value) throws Exception {
		Node result = getXPathValue(element, expr);
		if (value == null) {
			assertNull(expr + " evaluated to a non-null value for component "
					+ element.getId(), result);
			return;
		}
		assertNotNull(expr + " evaluated to a null value on component "
				+ element.getId(), result);
		assertEquals(expr + " evaluated on component " + element.getId(),
				value, result.getNodeValue());
		xpath.setNamespaceContext(element.getNamespaceContext());
	}

	/**
	 * @param description
	 * @param expr
	 * @param value
	 * @throws Exception
	 */
	public boolean assertXPathValueIfSet(BaseElement element, String expr,
			String value) throws Exception {
		Node result = getXPathValue(element, expr);
		if (result == null) {
			return false;
		}
		assertEquals(expr + " evaluated on component " + element.getId(),
				value, result.getNodeValue());
		return true;
	}

	/**
	 * @param description
	 * @param expr
	 * @param value
	 * @throws Exception
	 */
	public void assertXPathCount(BaseElement element, String expr, int value)
			throws Exception {
		xpath.setNamespaceContext(element.getNamespaceContext());
		expr = "count(" + expr + ")";
		String result = (String) xpath.evaluate(expr, element.getElement(), XPathConstants.STRING);
		assertNotNull(expr + " evaluated to a null value on component "
				+ element.getId(), result);
		assertEquals(expr + " evaluated on component " + element.getId(),
				value, Integer.valueOf(result).intValue());
	}

	private String getOCDId(Element ocd) throws Exception {
		String id = (String) xpath.evaluate("@id", ocd, XPathConstants.STRING);
		return id;
	}

	private String getDesignatePid(Element designate) throws Exception {
		String pid = (String) xpath.evaluate("@pid", designate, XPathConstants.STRING);
		return pid;
	}

	private String getDesignateFactoryPid(Element designate) throws Exception {
		String factoryPid = (String) xpath.evaluate("@factoryPid", designate, XPathConstants.STRING);
		return factoryPid;
	}

	/* For test debugging use */
	@SuppressWarnings("unused")
	private static String toString(Node node) throws Exception {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		DOMSource source = new DOMSource(node);
		StreamResult result = new StreamResult(new StringWriter());
		transformer.transform(source, result);
		return result.getWriter().toString();
	}
}

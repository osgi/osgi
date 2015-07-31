/*
 * Copyright (c) 2015 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */
package org.osgi.test.cases.rest.junit;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Tests REST Management Service XML Representations.
 *
 * @author Petia Sotirova
 */
public class RestServiceXMLTestCase extends RestTestUtils {

  private Validator validator;
  private DocumentBuilder documentBuilder;

  public void setUp() throws Exception {
    super.setUp();
    SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
    URL schemaResource = getClass().getResource("schema-rest.xsd");
    Schema schema = factory.newSchema(new StreamSource(schemaResource.openStream()));
    validator = schema.newValidator();

    documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
  }

  // 5.1.1
  public void testFrameworkStartLevel() throws IOException, ParserConfigurationException, SAXException, TransformerConfigurationException,
      TransformerException {
    FrameworkStartLevel frameworkStartLevel = getFrameworkStartLevel();
    int originalStartLevel = frameworkStartLevel.getStartLevel();
    int originalInitialBundleStartLevel = frameworkStartLevel.getInitialBundleStartLevel();

    Element xmlStartLevel = getXMLObject(FW_START_LEVEL_URI, FW_START_LEVEL_CONTENT_TYPE_XML, HttpURLConnection.HTTP_OK);
    assertFWStartLevel(frameworkStartLevel, xmlStartLevel);

    int startLevel = originalStartLevel;
    int initialBundleStartLevel = originalInitialBundleStartLevel + 1;
    updateFWStartLevel(startLevel, initialBundleStartLevel, HttpURLConnection.HTTP_NO_CONTENT, null);

    frameworkStartLevel = getFrameworkStartLevel();

    assertEquals("startLevel after PUT.", startLevel, frameworkStartLevel.getStartLevel());
    assertEquals("initialBundleStartLevel after PUT.", initialBundleStartLevel, frameworkStartLevel.getInitialBundleStartLevel());

    xmlStartLevel = getXMLObject(FW_START_LEVEL_URI, FW_START_LEVEL_CONTENT_TYPE_XML, HttpURLConnection.HTTP_OK);
    assertFWStartLevel(frameworkStartLevel, xmlStartLevel);

		frameworkStartLevel.setStartLevel(originalStartLevel);
    frameworkStartLevel.setInitialBundleStartLevel(originalInitialBundleStartLevel);

    // Updated frameworkStartLevel
    xmlStartLevel = getXMLObject(FW_START_LEVEL_URI, FW_START_LEVEL_CONTENT_TYPE_XML, HttpURLConnection.HTTP_OK);
    assertFWStartLevel(frameworkStartLevel, xmlStartLevel);
  }

  // tests list bundles and install bundle from invalid location/content.
  // successful install is covered by RestServiceTestCase
  public void testBundleList() throws BundleException, IOException, ParserConfigurationException, SAXException,
      TransformerConfigurationException, TransformerException {
    Element xmlBundleList = getXMLObject(BUNDLE_LIST_URI, BUNDLE_LIST_CONTENT_TYPE_XML, HttpURLConnection.HTTP_OK);

    Bundle[] bundles = getInstalledBundles();
    assertBundleListRepresentation(bundles, xmlBundleList);

    xmlBundleList = getXMLObject(getBundleListURI(getFilter(TEST_BUNDLE_SYMBOLIC_NAME)), BUNDLE_LIST_CONTENT_TYPE_XML,
        HttpURLConnection.HTTP_OK);
    assertBundleListRepresentation(new Bundle[]{getBundle(TEST_BUNDLE_SYMBOLIC_NAME)}, xmlBundleList);

    Element xmlBundleException = installBundle(BUNDLE_LIST_URI, null, "invalid bundle location", null, true,
        HttpURLConnection.HTTP_BAD_REQUEST);
    assertBundleException(xmlBundleException, "Install bundle by invalid URI.");

    xmlBundleException = installBundle(BUNDLE_LIST_URI, null, "invalid bundle location", null, false, HttpURLConnection.HTTP_BAD_REQUEST);
    assertBundleException(xmlBundleException, "Install bundle by invalid bundle content.");
  }

  // 5.1.2.2
  public void testBundleRepresentationsList() throws IOException, ParserConfigurationException, SAXException,
      TransformerConfigurationException, TransformerException {
    Element xmlBundleRepresentationsList = getXMLObject(BUNDLE_REPRESENTATIONS_LIST_URI, BUNDLE_REPRESENTATIONS_LIST_CONTENT_TYPE_XML,
        HttpURLConnection.HTTP_OK);
    assertNotNull("Bundle representations list.", xmlBundleRepresentationsList);

		fail("TODO"); // TODO

  }

  // 5.1.3
  public void testBundle() throws IOException, ParserConfigurationException, SAXException, TransformerConfigurationException,
      TransformerException {
    // GET
    Bundle bundle = getRandomBundle();
    Element xmlBundleRepresentation = getXMLObject(getBundleURI(bundle.getBundleId()), BUNDLE_CONTENT_TYPE_XML, HttpURLConnection.HTTP_OK);
    assertNotNull("Bundle representation : " + bundle.getBundleId() + " :", xmlBundleRepresentation);

		fail("TODO"); // TODO
  }

  // 5.1.4
  public void testBundleState() throws IOException, ParserConfigurationException, SAXException, TransformerConfigurationException,
      TransformerException {
    // GET
    Bundle bundle = getRandomBundle();
    Element xmlBundleStateRepresentation = getXMLObject(getBundleStateURI(bundle.getBundleId()), BUNDLE_STATE_CONTENT_TYPE_XML,
        HttpURLConnection.HTTP_OK);
    assertNotNull("Bundle state " + bundle.getBundleId() + " :", xmlBundleStateRepresentation);

		fail("TODO"); // TODO
  }

  // 5.1.5
  public void testBundleHeader() throws IOException, ParserConfigurationException, SAXException, TransformerConfigurationException,
      TransformerException {
    // GET
    Bundle bundle = getRandomBundle();
    Element xmlBundleHeaderRepresentation = getXMLObject(getBundleHeaderURI(bundle.getBundleId()), BUNDLE_HEADER_CONTENT_TYPE_XML,
        HttpURLConnection.HTTP_OK);
    assertNotNull("Bundle header " + bundle.getBundleId() + ": ", xmlBundleHeaderRepresentation);

		fail("TODO"); // TODO
  }

  // 5.1.6
  public void testBundleStartLevel() throws IOException, ParserConfigurationException, SAXException, TransformerConfigurationException,
      TransformerException {
    // GET
    Bundle bundle = getRandomBundle();
    Element xmlBundleStartLevelRepresentation = getXMLObject(getBundleStartLevelURI(bundle.getBundleId()),
        BUNDLE_START_LEVEL_CONTENT_TYPE_XML, HttpURLConnection.HTTP_OK);
    assertNotNull("Bundle start level " + bundle.getBundleId() + " :", xmlBundleStartLevelRepresentation);

		fail("TODO"); // TODO
  }

  // 5.1.7.1
  public void testServiceList() throws IOException, InvalidSyntaxException, ParserConfigurationException, SAXException,
      TransformerConfigurationException, TransformerException {
    String filter = null;
    ServiceReference<?>[] serviceRefs = getServices(filter);

    Element xmlServiceList = getXMLObject(getServiceListURI(filter), SERVICE_LIST_CONTENT_TYPE_XML, HttpURLConnection.HTTP_OK);

		fail("TODO"); // TODO
  }

  // 5.1.7.2
  public void testServiceRepresentationsList() throws IOException, InvalidSyntaxException, ParserConfigurationException, SAXException,
  TransformerConfigurationException, TransformerException {
    String filter = null;
    ServiceReference<?>[] serviceRefs = getServices(filter);
    Element xmlServiceRepresentationsList = getXMLObject(getServiceRepresentationListURI(filter),
        SERVICE_REPRESENTATIONS_LIST_CONTENT_TYPE_XML, HttpURLConnection.HTTP_OK);
    assertNotNull("Service representations list", xmlServiceRepresentationsList);
  }

  // 5.1.8
  public void testService() throws Exception {
    ServiceReference<?> serviceRef = getRandomService();

    // GET
    Element xmlServiceRepresentation = getXMLObject(getServiceURI(serviceRef), SERVICE_CONTENT_TYPE_XML, HttpURLConnection.HTTP_OK);
    assertNotNull("Service representation " + serviceRef + " :", xmlServiceRepresentation);

    assertService(xmlServiceRepresentation, serviceRef);

    String notExistingSeriveId = "not-existing-servicve-" + System.currentTimeMillis();
    xmlServiceRepresentation = getXMLObject(getServiceURI(notExistingSeriveId), SERVICE_CONTENT_TYPE_XML, HttpURLConnection.HTTP_NOT_FOUND);
    assertNull("Service representation for not existing service " + notExistingSeriveId + " :", xmlServiceRepresentation);
  }

  // 5.6.1 The Extensions Resource
  public void testExtensions() throws IOException, BundleException, ParserConfigurationException, SAXException,
      TransformerConfigurationException, TransformerException {
    Element xmlExtensions = getXMLObject("extensions", EXTENSIONS_CONTENT_TYPE_XML, HttpURLConnection.HTTP_OK);
    assertExtensions(xmlExtensions, null, 0);

    Bundle bundle = getTestBundle(TB5_TEST_BUNDLE_SYMBOLIC_NAME, TB5);
    bundle.start();

    xmlExtensions = getXMLObject(EXTENSIONS_URI, EXTENSIONS_CONTENT_TYPE_XML, HttpURLConnection.HTTP_OK);
    assertExtensions(xmlExtensions, Arrays.asList(new String[]{"REST CT Extension", "contributions/extension"})/* name, path*/, 1);

    bundle = getTestBundle(TB6_TEST_BUNDLE_SYMBOLIC_NAME, TB6);
    bundle.start();

    xmlExtensions = getXMLObject(EXTENSIONS_URI, EXTENSIONS_CONTENT_TYPE_XML, HttpURLConnection.HTTP_OK);
    assertExtensions(xmlExtensions, Arrays.asList(new String[]{"REST CT Extension", "contributions/extension",
        "REST Extension full URI", "http://127.0.0.1/ct/rest/extension"})/* name, path, name, path*/, 2);

    bundle.stop();
    xmlExtensions = getXMLObject(EXTENSIONS_URI, EXTENSIONS_CONTENT_TYPE_XML, HttpURLConnection.HTTP_OK);
    assertExtensions(xmlExtensions, Arrays.asList(new String[]{"REST CT Extension", "contributions/extension"}), 1);
  }


  protected void assertFWStartLevel(FrameworkStartLevel frameworkStartLevel, Element element) {
    String startLevelResult = getTextValue(element, "startLevel");
    String initialBundleStartLevelResult = getTextValue(element, "initialBundleStartLevel");

    assertNotNull("startLevel element missing.", startLevelResult);
    assertNotNull("initialBundleStartLevel element missing", initialBundleStartLevelResult);

    assertEquals("startLevel is not equal.", frameworkStartLevel.getStartLevel(), Integer.parseInt(startLevelResult));
    assertEquals("initialBundleStartLevel is not equal.", frameworkStartLevel.getInitialBundleStartLevel(),
        Integer.parseInt(initialBundleStartLevelResult));
  }

  protected void assertBundleListRepresentation(Bundle[] bundles, Element xmlBundleList) {
    assertNotNull("Bundle list not null.", xmlBundleList);

		fail("TODO"); // TODO
  }

  protected void assertBundleException(Element xmlBundleException, String assertMessage) {
    assertNotNull(assertMessage, xmlBundleException);

    String typecodeResult = getTextValue(xmlBundleException, "typecode");
    String messageResult = getTextValue(xmlBundleException, "message");

    assertTrue(assertMessage, Integer.parseInt(typecodeResult) >= 0); // some of BundleException error codes
    assertTrue("Error message is not null.", messageResult != null);
  }

  /*
<service>
  <id>10</id>
  <properties>
    <property name="prop1" value="val1"/>
    <property name="prop2" type="Float" value="2.82"/>
    ...
    <property name="prop3" type="Boolean" value="true"/>
  </properties>
  <bundle>bundleURI</bundle>
  <usingBundles>
    <bundle>bundleURI</bundle>
    <bundle>bundleURI</bundle>
    ...
    <bundle>bundleURI</bundle>
  </usingBundles>
</service>
   */
  protected void assertService(Element xmlServiceRepresentation, ServiceReference<?> serviceRef) throws Exception {
    assertNotNull("Service representation ", xmlServiceRepresentation);

    String idResult = getTextValue(xmlServiceRepresentation, "id");
    assertNotNull("Id property.", idResult);

    String bundleResult = getTextValue(xmlServiceRepresentation, "bundle");
    assertEquals("Bundle property.", getBundleURI(serviceRef.getBundle()), bundleResult);

    Bundle[] usingBundles = serviceRef.getUsingBundles();
    NodeList nodes = xmlServiceRepresentation.getElementsByTagName("usingBundles");
    if (usingBundles != null) {
      HashSet<String> uBundlesSet = new HashSet<String>();
      Element element = (Element)nodes.item(0);
      NodeList bundles = element.getElementsByTagName("bundle");
      assertNotNull("usingBundles not null.", bundles);
      for (int k = 0; k < bundles.getLength(); k++) {
        Element bundle = (Element)bundles.item(k);
        String value = bundle.getFirstChild().getNodeValue();
        uBundlesSet.add(value);
      }

      assertEquals("usingBundles length.", usingBundles.length, uBundlesSet.size());

      for (int k = 0; k < usingBundles.length; k++) {
        assertTrue("usingBundle " + usingBundles[k].getBundleId(), uBundlesSet.contains(getBundleURI(usingBundles[k])));
      }
    } else {
      if (nodes != null && nodes.getLength() != 0) {
        Element element = (Element)nodes.item(0);
        NodeList bundles = element.getElementsByTagName("bundle");

        assertTrue("usingBundles.", bundles == null || bundles.getLength() == 0);
      }
    }

    String[] propKeys = serviceRef.getPropertyKeys();
    NodeList props = xmlServiceRepresentation.getElementsByTagName("properties");

    if (propKeys != null) {
      Hashtable<String, Element> xmlProps = new Hashtable<String, Element>();
      Element propsElement = (Element)props.item(0);
      NodeList propertyNodeList = propsElement.getElementsByTagName("property");
      assertNotNull("properties not null.", propertyNodeList);
      for (int k = 0; k < propertyNodeList.getLength(); k++) {
        Element propertyElement = (Element)propertyNodeList.item(k);
        String name = propertyElement.getAttribute("name");
        xmlProps.put(name, propertyElement);
      }

      assertEquals("Properties size.", propKeys.length, xmlProps.size());
      for (String key : propKeys) {
        Object value = serviceRef.getProperty(key);

        Element propertyElement = xmlProps.get(key);
        assertNotNull("Service property " + key, propertyElement.getAttributes());
        if (value.getClass().isArray()) {
					fail("TODO"); // TODO
        } else if (value instanceof Number) {
					System.err.println("key " + key + " elem " + propertyElement);
          assertEquals("Service number property " + key, ((Number)value).intValue(),
              Integer.parseInt(propertyElement.getAttribute("value")));
        } else {
          assertEquals("Service property value for " + key, value, propertyElement.getAttribute("value"));
        }
      }
    } else {
      assertTrue("No properties for service " + serviceRef.getProperty(Constants.SERVICE_ID) + ".",
          props == null || props.getLength() == 0);
    }
  }

  /*
  <extensions>
    <extension>
      <name>org.osgi.service.event</name>
      <path>contributions/eventadmin</path>
      <service>12</service>
    </extension>
  </extensions>
  */
  protected void assertExtensions(Element xmlExtensionsList, List<String> extProps /* name, path, name, path*/,
      int expectedExtensionsCount) {
    if (expectedExtensionsCount == 0) {
      assertTrue("No extensions.", xmlExtensionsList == null || !xmlExtensionsList.hasChildNodes());
      return;
    }

    assertNotNull("Extensions are not null.", xmlExtensionsList);

    NodeList extNodeList = xmlExtensionsList.getElementsByTagName("extension");
    assertEquals("Extensions size is " + expectedExtensionsCount + ".", expectedExtensionsCount, extNodeList.getLength());

    HashMap<String, String> extensions = new HashMap<String, String>();
    for (int k = 0; k < extNodeList.getLength(); k++) {
      Element extElement = (Element)extNodeList.item(k);

      String name = getTextValue(extElement, "name");
      String path = getTextValue(extElement, "path");

      assertNotNull("Name is not null.", name);
      assertNotNull("Path is not null.", path);

      extensions.put(name, path);
    }

    for (int k = 0; k < extProps.size(); k++) {
      String expectedName = extProps.get(k++);
      String expectedPath = extProps.get(k);

      String path = extensions.get(expectedName);

      assertTrue("Extensions list contains " + expectedName + ".", path != null);
      assertEquals("Extension path.", expectedPath, path);
    }
  }

  protected void updateFWStartLevel(int startLevel, int initialBundleStartLevel, int expectedStatusCode, String acceptType)
      throws ParserConfigurationException, TransformerConfigurationException, TransformerException {
    Document doc = getDocumentBuilder().newDocument();
    Element rootNode = doc.createElement("frameworkStartLevel");
    doc.appendChild(rootNode);

    Element startLevelElement = doc.createElement("startLevel");
    rootNode.appendChild(startLevelElement);
    startLevelElement.setTextContent(String.valueOf(startLevel));

    Element initialBundleStartLevelElement = doc.createElement("initialBundleStartLevel");
    rootNode.appendChild(initialBundleStartLevelElement);
    initialBundleStartLevelElement.setTextContent(String.valueOf(initialBundleStartLevel));

    String strBody = documentToString(doc);
    debug("strBody:" + strBody, null);

    executeRequest(FW_START_LEVEL_URI, "PUT", APPLICATION_XML, acceptType, null, expectedStatusCode, null, strBody);
  }

  protected Element installBundle(String requestURI, URL url, String invalidLocation, String locationHeader, boolean byLocation,
      int expectedStatusCode) throws IOException, ParserConfigurationException, SAXException {
    InputStream is = null;
    if (byLocation) {
      is = executeRequest(requestURI, "POST", "text/plain", APPLICATION_XML, BUNDLE_EXCEPTION_CONTENT_TYPE_XML, expectedStatusCode,
          null /* additionalProps */, invalidLocation == null ? url.toString() : invalidLocation);
    } else {
      HashMap<String, String> additionalProps = new HashMap<String, String>();
      if (locationHeader != null) {
        additionalProps.put("Content-Location", locationHeader);
      }

      is = executeRequest(requestURI, "POST", "vnd.osgi.bundle", APPLICATION_XML, BUNDLE_EXCEPTION_CONTENT_TYPE_XML, expectedStatusCode,
          additionalProps, invalidLocation == null ? url.openStream() : new ByteArrayInputStream(invalidLocation.getBytes()));
    }

    if (is != null && expectedStatusCode == HttpURLConnection.HTTP_BAD_REQUEST) { // BundleException
      InputStream data = validate(is);
      Document document = getDocumentBuilder().parse(data);
      return document.getDocumentElement();
    }

    if (is != null) {
      is.close();
    }

    return null;
  }

  protected Element getXMLObject(String uri, String expectedContentType, int expectedResponseCode)
      throws IOException, ParserConfigurationException, SAXException {
    InputStream result = executeRequest(uri, "GET", null, APPLICATION_XML, expectedContentType, expectedResponseCode, null, null);
    InputStream data = validate(result);

    if (expectedResponseCode == HttpURLConnection.HTTP_OK) {
      Document document = getDocumentBuilder().parse(data);
      return document.getDocumentElement();
    }

    return null;
  }

  protected InputStream executeRequest(String uri, String method, String contentType, String acceptType, String expectedContentType,
      int expectedResponseCode, Map<String, String> additionalProps, Object body) {
    HttpURLConnection connection = null;
    InputStream in = null;
    try {
      connection = getHttpConnection(baseURI + uri, method, acceptType, contentType, additionalProps);
      if (body != null) {
        connection.setDoInput(true);
        connection.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        if (body instanceof String) {
          out.writeBytes((String)body);
        } else if (body instanceof InputStream) {
          InputStream is = (InputStream)body;
          try {
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = is.read(buffer, 0, 1024)) != -1) {
              out.write(buffer, 0, bytesRead);
            }
          } finally {
            try {
              is.close();
						} catch (Throwable _) {
							/**/}
          }
        }
        out.flush();
        out.close();
      }

      connection.connect();

      int responseCode = connection.getResponseCode();
      assertEquals("Response code", expectedResponseCode, responseCode);

      if (responseCode == HttpURLConnection.HTTP_OK) {
        String responseContentType = connection.getContentType();
        debug("Response ContentType:" + responseContentType, null);
        if (expectedContentType != null) {
          assertTrue("ContentType", (responseContentType != null) && (responseContentType.startsWith(expectedContentType)));
        }
        in = connection.getInputStream();
      } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
        in = connection.getErrorStream();
      }

      return in;
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }


  protected InputStream validate(InputStream data) throws SAXException, IOException {
    if (validateXMLRepresentations) {
      byte[] byteArrCopy = toByteArray(data);
      Source source = new StreamSource(new ByteArrayInputStream(byteArrCopy));
      validator.validate(source);

      return new ByteArrayInputStream(byteArrCopy);
    }

    if (debugOn) {
      byte[] byteArrCopy = toByteArray(data);
      BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(byteArrCopy)));

      String temp = null;
      StringBuilder sb = new StringBuilder();
      while ((temp = br.readLine()) != null) {
        sb.append(temp);
      }
      System.out.println("xml:" + sb.toString());

      return new ByteArrayInputStream(byteArrCopy);
    }

    return data;
  }

  private String getTextValue(Element ele, String tagName) {
    String textVal = null;
    NodeList nl = ele.getElementsByTagName(tagName);
    if (nl != null && nl.getLength() > 0) {
      Element el = (Element)nl.item(0);
      textVal = el.getFirstChild().getNodeValue();
    }

    return textVal;
  }

  private String documentToString(Document doc) throws TransformerConfigurationException, TransformerException {
    StringWriter writer = new StringWriter();
    StreamResult result = new StreamResult(writer);
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.transform(new DOMSource(doc), result);
    return writer.toString();
  }

  private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
    return documentBuilder;
  }

}

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
package org.osgi.test.cases.rest.junit;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
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
import org.osgi.framework.startlevel.BundleStartLevel;
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
  // successful install is covered by RestServiceJSONTestCase
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

    Bundle[] bundles = getInstalledBundles();
    assertBundleRepresentationList(bundles, xmlBundleRepresentationsList);

    // test with filter
    xmlBundleRepresentationsList = getXMLObject(getBundleRepresentationListURI(getFilter(TEST_BUNDLE_SYMBOLIC_NAME)),
      BUNDLE_REPRESENTATIONS_LIST_CONTENT_TYPE_XML, HttpURLConnection.HTTP_OK);
    Bundle testBundle = getBundle(TEST_BUNDLE_SYMBOLIC_NAME);
    assertBundleRepresentationList(new Bundle[]{testBundle}, xmlBundleRepresentationsList);
  }

  // 5.1.3
  public void testBundle() throws IOException, ParserConfigurationException, SAXException, TransformerConfigurationException,
      TransformerException, BundleException {
    // GET
    Bundle bundle = getRandomBundle();
    Element xmlBundleRepresentation = getXMLObject(getBundleURI(bundle.getBundleId()), BUNDLE_CONTENT_TYPE_XML, HttpURLConnection.HTTP_OK);
    assertNotNull("Bundle representation : " + bundle.getBundleId() + " :", xmlBundleRepresentation);

    assertBundleRepresentation(bundle, xmlBundleRepresentation);

    long notExistingBundleId = getNotExistingBundleId();
    Object object = getXMLObject(getBundleURI(notExistingBundleId), BUNDLE_CONTENT_TYPE_XML, HttpURLConnection.HTTP_NOT_FOUND);
    assertNull("Get not existing bundle " + notExistingBundleId + " :", object);

    // PUT with location
    Bundle tb1Bundle = getTestBundle(TB1_TEST_BUNDLE_SYMBOLIC_NAME, TB1);
    Element result = updateBundle(getBundleURI(tb1Bundle.getBundleId()), getContext().getBundle().getEntry(TB11), null, true,
      HttpURLConnection.HTTP_NO_CONTENT, null);
    assertNull("Update bundle by location " + tb1Bundle.getBundleId() + " :", result);

    result = updateBundle(getBundleURI(notExistingBundleId), getContext().getBundle().getEntry(TB11), null, true,
      HttpURLConnection.HTTP_NOT_FOUND, null);
    assertNull("Update not existing bundle " + notExistingBundleId + " :", result);

    result = updateBundle(getBundleURI(tb1Bundle.getBundleId()), null, "invalid bundle location", true, HttpURLConnection.HTTP_BAD_REQUEST,
      BUNDLE_EXCEPTION_CONTENT_TYPE_XML);
    assertBundleException(result, "Update bundle with invalid location " + tb1Bundle.getBundleId());

    // PUT with bundle content
    Bundle tb2Bundle = getTestBundle(TB2_TEST_BUNDLE_SYMBOLIC_NAME, TB2);

    result = updateBundle(getBundleURI(tb2Bundle.getBundleId()), getContext().getBundle().getEntry(TB21), null, false,
      HttpURLConnection.HTTP_NO_CONTENT, null);
    assertNull("Update bundle by content " + tb2Bundle.getBundleId() + " :", result);

    result = updateBundle(getBundleURI(notExistingBundleId), getContext().getBundle().getEntry(TB21), null, false,
      HttpURLConnection.HTTP_NOT_FOUND, null);
    assertNull("Update not existing bundle " + notExistingBundleId + " :", result);

    result = updateBundle(getBundleURI(tb2Bundle.getBundleId()), null, "invalid bundle location", true, HttpURLConnection.HTTP_BAD_REQUEST,
      BUNDLE_EXCEPTION_CONTENT_TYPE_XML);

    assertBundleException(result, "Update bundle with invalid content " + tb2Bundle.getBundleId());

    // DELETE
    result = uninstallBundle(getBundleURI(tb1Bundle.getBundleId()), HttpURLConnection.HTTP_NO_CONTENT);
    assertNull("Uninstall bundle " + tb1Bundle.getBundleId() + " :", result);
    assertNull("Framework bundle " + tb1Bundle.getBundleId() + " :", getContext().getBundle(tb1Bundle.getBundleId()));

    result = uninstallBundle(getBundleURI(tb1Bundle.getBundleId()), HttpURLConnection.HTTP_NOT_FOUND);
    assertNull("Uninstall already uninstalled bundle " + tb1Bundle.getBundleId() + " :", result);

    // stop tb2 bundle throws Exception
    tb2Bundle.start();
    result = uninstallBundle(getBundleURI(tb2Bundle.getBundleId()), HttpURLConnection.HTTP_NO_CONTENT);
  }

  // 5.1.4
  public void testBundleState() throws IOException, ParserConfigurationException, SAXException, TransformerConfigurationException,
      TransformerException, BundleException {
    // GET
    Bundle bundle = getRandomBundle();
    Element xmlBundleStateRepresentation = getXMLObject(getBundleStateURI(bundle.getBundleId()), BUNDLE_STATE_CONTENT_TYPE_XML,
      HttpURLConnection.HTTP_OK);
    assertNotNull("Bundle state " + bundle.getBundleId() + " :", xmlBundleStateRepresentation);
    assertBundleStateRepresentation(bundle, xmlBundleStateRepresentation);

    long notExistingBundleId = getNotExistingBundleId();

    xmlBundleStateRepresentation = getXMLObject(getBundleStateURI(notExistingBundleId), BUNDLE_STATE_CONTENT_TYPE_XML,
      HttpURLConnection.HTTP_NOT_FOUND);
    assertNull("Bundle state for not existing bundle " + notExistingBundleId + " :", xmlBundleStateRepresentation);

    // PUT
    Bundle tb1Bundle = getTestBundle(TB1_TEST_BUNDLE_SYMBOLIC_NAME, TB1);

    int tb1State = tb1Bundle.getState();

    int newState = tb1State == Bundle.INSTALLED ? Bundle.ACTIVE : Bundle.RESOLVED;
    xmlBundleStateRepresentation = updateBundleState(getBundleStateURI(tb1Bundle.getBundleId()), newState, -1, HttpURLConnection.HTTP_OK,
      APPLICATION_XML, APPLICATION_XML, BUNDLE_STATE_CONTENT_TYPE_XML);
    assertNotNull("Bundle state updated  " + tb1Bundle.getBundleId() + " :", xmlBundleStateRepresentation);

    assertEquals("New state ", newState, tb1Bundle.getState());
    assertBundleStateRepresentation(tb1Bundle, xmlBundleStateRepresentation);

    // start bundle with Bundle-ActivationPolicy by bundle id
    Bundle tb3Bundle = getTestBundle(TB3_TEST_BUNDLE_SYMBOLIC_NAME, TB3);
    int tb3State = tb3Bundle.getState();
    if (tb3State == Bundle.ACTIVE || tb3State == Bundle.STARTING) {
      tb3Bundle.stop();
    }

    xmlBundleStateRepresentation = updateBundleState(getBundleStateURI(tb3Bundle.getBundleId()), Bundle.ACTIVE,
      Bundle.START_ACTIVATION_POLICY, HttpURLConnection.HTTP_OK, APPLICATION_XML, APPLICATION_XML, BUNDLE_STATE_CONTENT_TYPE_XML);
    assertNotNull("Bundle state updated  " + tb3Bundle.getBundleId() + " :", xmlBundleStateRepresentation);

    assertEquals("New state for 'lazy' bundle ", Bundle.STARTING, tb3Bundle.getState());
    assertBundleStateRepresentation(tb3Bundle, xmlBundleStateRepresentation);

    xmlBundleStateRepresentation = updateBundleState(getBundleStateURI(notExistingBundleId), newState, -1,
      HttpURLConnection.HTTP_NOT_FOUND, APPLICATION_XML, APPLICATION_XML, null);
    assertNull("Bundle state updated for not existing bundle " + notExistingBundleId + " :", xmlBundleStateRepresentation);

    Bundle tb21Bundle = getTestBundle(TB21_TEST_BUNDLE_SYMBOLIC_NAME, TB21);
    if (tb21Bundle.getState() != Bundle.ACTIVE) {
      tb21Bundle.start();
    }

    Element exceptionelement = updateBundleState(getBundleStateURI(tb21Bundle.getBundleId()), Bundle.RESOLVED, -1,
      HttpURLConnection.HTTP_BAD_REQUEST, APPLICATION_XML, APPLICATION_XML, BUNDLE_EXCEPTION_CONTENT_TYPE_XML);
    assertBundleException(exceptionelement, "Stop bundle for bundle with error in stop method  " + tb21Bundle.getBundleId());

    // stop bundle with options
    xmlBundleStateRepresentation = updateBundleState(getBundleStateURI(tb3Bundle.getBundleId()), Bundle.RESOLVED, Bundle.STOP_TRANSIENT,
      HttpURLConnection.HTTP_OK, APPLICATION_XML, APPLICATION_XML, BUNDLE_STATE_CONTENT_TYPE_XML);
    assertNotNull("Bundle state updated  " + tb3Bundle.getBundleId() + " :", xmlBundleStateRepresentation);

    assertEquals("New state ", Bundle.RESOLVED, tb3Bundle.getState());
    assertBundleStateRepresentation(tb3Bundle, xmlBundleStateRepresentation);
  }

  // 5.1.5
  public void testBundleHeader() throws IOException, ParserConfigurationException, SAXException, TransformerConfigurationException,
      TransformerException {
    // GET
    Bundle bundle = getRandomBundle();
    Element xmlBundleHeaderRepresentation = getXMLObject(getBundleHeaderURI(bundle.getBundleId()), BUNDLE_HEADER_CONTENT_TYPE_XML,
        HttpURLConnection.HTTP_OK);
    assertNotNull("Bundle header " + bundle.getBundleId() + ": ", xmlBundleHeaderRepresentation);
    assertBundleHeaderRepresentation(bundle, xmlBundleHeaderRepresentation);

    long notExistingBundleId = getNotExistingBundleId();
    xmlBundleHeaderRepresentation = getXMLObject(getBundleHeaderURI(notExistingBundleId), BUNDLE_HEADER_CONTENT_TYPE_XML,
      HttpURLConnection.HTTP_NOT_FOUND);
    assertNull("Bundle header for not existing bundle " + notExistingBundleId + " :", xmlBundleHeaderRepresentation);
  }

  // 5.1.6
  public void testBundleStartLevel() throws IOException, ParserConfigurationException, SAXException, TransformerConfigurationException,
      TransformerException, BundleException {
    // GET
    Bundle bundle = getRandomBundle();
    Element xmlBundleStartLevelRepresentation = getXMLObject(getBundleStartLevelURI(bundle.getBundleId()),
        BUNDLE_START_LEVEL_CONTENT_TYPE_XML, HttpURLConnection.HTTP_OK);
    assertBundleStartLevelRepresentation(bundle, xmlBundleStartLevelRepresentation);

    long notExistingBundleId = getNotExistingBundleId();
    xmlBundleStartLevelRepresentation = getXMLObject(getBundleStartLevelURI(notExistingBundleId), BUNDLE_START_LEVEL_CONTENT_TYPE_XML,
      HttpURLConnection.HTTP_NOT_FOUND);
    assertNull("Bundle start level for not existing bundle " + notExistingBundleId + " :", xmlBundleStartLevelRepresentation);

    // PUT
    Bundle tb1Bundle = getTestBundle(TB1_TEST_BUNDLE_SYMBOLIC_NAME, TB1);

    int tb1StartLevel = getBundleStartLevel(tb1Bundle).getStartLevel();
    int newStartLevel = tb1StartLevel + 1;
    xmlBundleStartLevelRepresentation = updateBundleStartLevel(getBundleStartLevelURI(tb1Bundle.getBundleId()), newStartLevel,
      HttpURLConnection.HTTP_OK, APPLICATION_XML, APPLICATION_XML);

    tb1Bundle = getTestBundle(TB1_TEST_BUNDLE_SYMBOLIC_NAME, TB1);
    assertBundleStartLevelRepresentation(tb1Bundle, xmlBundleStartLevelRepresentation);

    tb1StartLevel = getBundleStartLevel(tb1Bundle).getStartLevel();
    assertEquals("New start level ", newStartLevel, tb1StartLevel);

    xmlBundleStartLevelRepresentation = updateBundleStartLevel(getBundleStartLevelURI(notExistingBundleId), newStartLevel,
      HttpURLConnection.HTTP_NOT_FOUND, APPLICATION_XML, APPLICATION_XML);
    assertNull("Bundle start level updated for not existing bundle " + notExistingBundleId + " :", xmlBundleStartLevelRepresentation);

    updateBundleStartLevel(getBundleStartLevelURI(tb1Bundle.getBundleId()), -1, HttpURLConnection.HTTP_BAD_REQUEST,
      APPLICATION_XML, APPLICATION_XML);
  }

  // 5.1.7.1
  public void testServiceList() throws IOException, InvalidSyntaxException, ParserConfigurationException, SAXException,
      TransformerConfigurationException, TransformerException {
    String filter = null;
    ServiceReference<?>[] serviceRefs = getServices(filter);

    Element xmlServiceList = getXMLObject(getServiceListURI(filter), SERVICE_LIST_CONTENT_TYPE_XML, HttpURLConnection.HTTP_OK);
    assertServiceList(xmlServiceList, serviceRefs);

    filter = "(&(" + Constants.SERVICE_ID + ">=" + serviceRefs.length / 2 + ")"
            + "(" + Constants.SERVICE_ID + "<=" + serviceRefs.length + "))";
    serviceRefs = getServices(filter);
    xmlServiceList = getXMLObject(getServiceListURI(filter), SERVICE_LIST_CONTENT_TYPE_XML, HttpURLConnection.HTTP_OK);
    assertServiceList(xmlServiceList, serviceRefs);

    String invalidFilterURI = SERVICE_LIST_URI + "?filter=invalid-filter";
    xmlServiceList = getXMLObject(invalidFilterURI, null, HttpURLConnection.HTTP_BAD_REQUEST);
    assertNull("Request with invalid filter " + invalidFilterURI, xmlServiceList);
  }

  // 5.1.7.2
  public void testServiceRepresentationsList() throws IOException, InvalidSyntaxException, ParserConfigurationException, SAXException,
      TransformerConfigurationException, TransformerException {
    String filter = null;
    ServiceReference<?>[] serviceRefs = getServices(filter);
    Element xmlServiceRepresentationsList = getXMLObject(getServiceRepresentationListURI(filter),
        SERVICE_REPRESENTATIONS_LIST_CONTENT_TYPE_XML, HttpURLConnection.HTTP_OK);
    assertServiceRepresentationList(xmlServiceRepresentationsList, serviceRefs);

    filter = "(&(" + Constants.SERVICE_ID + ">=" + serviceRefs.length / 2 + ")"
            + "(" + Constants.SERVICE_ID + "<=" + serviceRefs.length + "))";

    serviceRefs = getServices(filter);
    xmlServiceRepresentationsList = getXMLObject(getServiceRepresentationListURI(filter), SERVICE_REPRESENTATIONS_LIST_CONTENT_TYPE_XML,
      HttpURLConnection.HTTP_OK);
    assertServiceRepresentationList(xmlServiceRepresentationsList, serviceRefs);

    filter = "invalid-filter";
    xmlServiceRepresentationsList = getXMLObject(getServiceRepresentationListURI(filter), null, HttpURLConnection.HTTP_BAD_REQUEST);
    assertNull("Request with invalid filter '" + filter + "'", xmlServiceRepresentationsList);
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
    assertExtensions(xmlExtensions, Arrays.asList(new String[]{"REST TCK Extension", "contributions/extension"})/* name, path*/, 1);

    bundle = getTestBundle(TB6_TEST_BUNDLE_SYMBOLIC_NAME, TB6);
    bundle.start();

    xmlExtensions = getXMLObject(EXTENSIONS_URI, EXTENSIONS_CONTENT_TYPE_XML, HttpURLConnection.HTTP_OK);
    assertExtensions(xmlExtensions, Arrays.asList(new String[]{"REST TCK Extension", "contributions/extension",
        "REST Extension full URI", "http://127.0.0.1/ct/rest/extension"})/* name, path, name, path*/, 2);

    bundle.stop();
    xmlExtensions = getXMLObject(EXTENSIONS_URI, EXTENSIONS_CONTENT_TYPE_XML, HttpURLConnection.HTTP_OK);
    assertExtensions(xmlExtensions, Arrays.asList(new String[]{"REST TCK Extension", "contributions/extension"}), 1);

    bundle = getTestBundle(TB5_TEST_BUNDLE_SYMBOLIC_NAME, TB5);
    bundle.stop();

    xmlExtensions = getXMLObject("extensions", EXTENSIONS_CONTENT_TYPE_XML, HttpURLConnection.HTTP_OK);
    assertExtensions(xmlExtensions, null, 0);
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

  protected void assertBundleRepresentation(Bundle bundle, Element element) {
    String lastModifiedStr = getTextValue(element, "lastModified");
    assertNotNull("lastModified element missing.", lastModifiedStr);

    String locationStr = getTextValue(element, "location");
    assertNotNull("location element missing.", locationStr);

    String stateStr = getTextValue(element, "state");
    assertNotNull("state element missing.", stateStr);

    String symbolicNameStr = getTextValue(element, "symbolicName");
    assertNotNull("symbolicName element missing.", symbolicNameStr);

    String versionStr = getTextValue(element, "version");
    assertNotNull("version element missing.", versionStr);

    assertEquals("lastModified:", bundle.getLastModified(), Long.valueOf(lastModifiedStr).longValue());
    assertEquals("location:", bundle.getLocation(), locationStr);
    assertEquals("state:", bundle.getState(), Integer.valueOf(stateStr).intValue());
    assertEquals("symbolicName:", bundle.getSymbolicName(), symbolicNameStr);
    assertEquals("version:", bundle.getVersion().toString(), versionStr);
  }

  protected void assertBundleListRepresentation(Bundle[] bundles, Element xmlBundleList) {
    assertNotNull("Bundle list not null.", xmlBundleList);

    NodeList bundleList = xmlBundleList.getElementsByTagName("uri");
    assertEquals("Bundle list length:", bundles.length, bundleList.getLength());

    HashSet<String> bundleURIs = new HashSet<String>();
    for (int k = 0; k < bundleList.getLength(); k++) {
      Element bundleElement = (Element)bundleList.item(k);
      String uri = getTextValue(bundleElement);
      bundleURIs.add(uri);
    }

    for (Bundle bundle : bundles) {
      String bundleURI = getBundleURI(bundle.getBundleId());
      assertTrue("Bundle list contains " + bundleURI + " :", bundleURIs.contains(bundleURI));
    }
  }

  protected void assertBundleRepresentationList(Bundle[] bundles, Element xmlBundleList) {
    assertNotNull("Bundle list not null.", xmlBundleList);
    NodeList bundleList = xmlBundleList.getElementsByTagName("bundle");

    assertEquals("Bundle representations list length:", bundles.length, bundleList.getLength());

    Hashtable<String, Element> bundleRepresentations = new Hashtable<String, Element>();
    for (int k = 0; k < bundleList.getLength(); k++) {
      Element bundleElement = (Element)bundleList.item(k);
      String id = getTextValue(bundleElement, "id");

      bundleRepresentations.put(id, bundleElement);
    }

    for (Bundle bundle : bundles) {
      long bundleId = bundle.getBundleId();
      Element bundleRepresentation = bundleRepresentations.get(String.valueOf(bundleId));
      assertNotNull("Bundle representations list contains " + bundleId + " :", bundleRepresentation);
      assertBundleRepresentation(bundle, bundleRepresentation);
    }
  }

  protected void assertBundleStateRepresentation(Bundle bundle, Element bundleState) {
    String stateStr = getTextValue(bundleState, "state");
    assertNotNull("Bundle state.", stateStr);

    assertEquals("state:", bundle.getState(), Integer.valueOf(stateStr).intValue());
  }

  protected void assertBundleHeaderRepresentation(Bundle bundle, Element bundleHeader) {
    assertNotNull("Bundle header not null.", bundleHeader);
    NodeList headerList = bundleHeader.getElementsByTagName("entry");

    Hashtable<String, String> responseHeaders = new Hashtable<String, String>();
    for (int k = 0; k < headerList.getLength(); k++) {
      Element entryElement = (Element)headerList.item(k);
      String key = entryElement.getAttribute("key");
      String value = entryElement.getAttribute("value");

      responseHeaders.put(key, value);
    }

    Dictionary<String, String> headers = bundle.getHeaders();

    assertEquals("Headers size", headers.size(), responseHeaders.size());

    for (Enumeration<String> keys = headers.keys(); keys.hasMoreElements();) {
      String key = keys.nextElement();
      assertEquals(key + ":", headers.get(key), responseHeaders.get(key));
    }
  }

  protected void assertBundleStartLevelRepresentation(Bundle bundle, Element bundleStartLevelRepresentation) {
    assertNotNull("Bundle StartLevel not null.", bundleStartLevelRepresentation);

    String startLevelStr = getTextValue(bundleStartLevelRepresentation, "startLevel");
    assertNotNull("startLevel not null.", startLevelStr);

    String activationPolicyUsedStr = getTextValue(bundleStartLevelRepresentation, "activationPolicyUsed");
    assertNotNull("activationPolicyUsed not null.", activationPolicyUsedStr);

    String persistentlyStartedStr = getTextValue(bundleStartLevelRepresentation, "persistentlyStarted");
    assertNotNull("persistentlyStarted not null.", persistentlyStartedStr);

    BundleStartLevel bundleStartLevel = getBundleStartLevel(bundle);

    assertEquals("startLevel:", bundleStartLevel.getStartLevel(), Integer.valueOf(startLevelStr).intValue());
    assertEquals("activationPolicyUsed:", bundleStartLevel.isActivationPolicyUsed(),
      Boolean.valueOf(activationPolicyUsedStr).booleanValue());
    assertEquals("persistentlyStarted:", bundleStartLevel.isPersistentlyStarted(), Boolean.valueOf(persistentlyStartedStr).booleanValue());
  }

  protected void assertServiceList(Element xmlServiceList, ServiceReference<?>[] serviceRefs) {
    assertNotNull("Service list", xmlServiceList);

    NodeList serviceList = xmlServiceList.getElementsByTagName("uri");
    assertNotNull("Service uri list ", serviceList);
    assertEquals("Service list length:", serviceRefs.length, serviceList.getLength());

    HashSet<String> serviceURIs = new HashSet<String>();
    for (int k = 0; k < serviceList.getLength(); k++) {
      Element serviceURIElement = (Element)serviceList.item(k);
      String uri = getTextValue(serviceURIElement);
      serviceURIs.add(uri);
    }

    for (ServiceReference<?> serviceRef : serviceRefs) {
      String serviceURI = getServiceURI(serviceRef.getProperty(Constants.SERVICE_ID).toString());
      assertTrue("Service list contains " + serviceURI + ": ", serviceURIs.contains(serviceURI));
    }
  }

  protected void assertServiceRepresentationList(Element xmlServiceRepresentationList, ServiceReference<?>[] serviceRefs) {
    if (serviceRefs != null) {
      assertNotNull("Service representation list", xmlServiceRepresentationList);

      NodeList serviceList = xmlServiceRepresentationList.getElementsByTagName("service");
      assertNotNull("Service list", serviceList);

      Hashtable<Object, Element> serviceRepresentationHT = new Hashtable<Object, Element>();

      for (int k = 0; k < serviceList.getLength(); k++) {
        Element serviceElement = (Element)serviceList.item(k);
        try {
          NodeList propertiesList = serviceElement.getElementsByTagName("properties");
          assertNotNull("Service representation properties ", propertiesList);
          Element propertiesElement = (Element)propertiesList.item(0);

          NodeList propertyList = propertiesElement.getElementsByTagName("property");
          assertNotNull("Property list", propertyList);

          for (int i = 0; i < propertyList.getLength(); i++) {
            Element propertyElement = (Element)propertyList.item(i);
            String name = propertyElement.getAttribute("name");
            if (Constants.SERVICE_ID.equals(name)) {

              String serviceId = propertyElement.getAttribute("value");
              serviceRepresentationHT.put(serviceId, serviceElement);
            }
          }

        } catch (Exception cause) {
          fail(cause.getMessage());
        }
      }

      assertEquals("Service representation list size", serviceRefs.length, serviceRepresentationHT.size());
      for (ServiceReference<?> serviceRef : serviceRefs) {
        String id = String.valueOf(serviceRef.getProperty(Constants.SERVICE_ID));
        debug("Assert service:" + id, null);
        assertService(serviceRepresentationHT.get(id), serviceRef);
      }
    }
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
  protected void assertService(Element xmlServiceRepresentation, ServiceReference<?> serviceRef) {
    assertNotNull("Service representation ", xmlServiceRepresentation);

    String serviceId = serviceRef.getProperty(Constants.SERVICE_ID).toString();

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
      Hashtable<String, List<String>> xmlProps = new Hashtable<String, List<String>>();
      Element propsElement = (Element)props.item(0);

      NodeList propertyNodeList = propsElement.getElementsByTagName("property");
      assertNotNull("properties not null.", propertyNodeList);

      for (int k = 0; k < propertyNodeList.getLength(); k++) {
        Element propertyElement = (Element)propertyNodeList.item(k);
        String name = propertyElement.getAttribute("name");

        List<String> val = new ArrayList<String>();
        xmlProps.put(name, val);

				if (propertyElement.hasAttribute("value")) {
					String valueAttribute = propertyElement.getAttribute("value");
					val.add(valueAttribute);
				} else {
					String valueContent = propertyElement.getTextContent();
					if (valueContent != null) {
						String[] values = valueContent.split("\\n");
						for (String value : values) {
							String v = value.trim();
							if (v.length() > 0)
								val.add(v);
						}
					}
				}
      }

      assertEquals("Properties size.", propKeys.length, xmlProps.size());
      for (String key : propKeys) {
        Object value = serviceRef.getProperty(key);

        List<String> propertyList = xmlProps.get(key);
        assertNotNull("Service property " + key, propertyList);

        if (value.getClass().isArray()) {
          int length = Array.getLength(value);

          debug("Service property '" + key + "' has Array value", null);
          debug("Property value from xml:" + propertyList, null);
          debug("Property value :" + Arrays.toString((Object[])value), null);

          assertEquals("Service '" + serviceId + "'. Array property '" + key + "' value size.", length, propertyList.size());

          for (int k = 0; k < length; k++) {
            Object arrayValue = Array.get(value, k);
            assertTrue("Service '" + serviceId + "'. Array property '" + key + "' arrayValue:'" + arrayValue + "'.",
              propertyList.contains(arrayValue.toString()));
          }

        } else {
          assertTrue("Single property value.", propertyList.size() == 1);
          if (value instanceof Number) {
            assertEquals("Service number property " + key, ((Number)value).intValue(),
              Integer.parseInt(propertyList.get(0)));
          } else {
            assertEquals("Service property value for " + key, value, propertyList.get(0));
          }
        }
      }
    } else {
      assertTrue("No properties for service '" + serviceId + "'.",
        props == null || props.getLength() == 0);
    }
  }

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

    try {
      if (expectedStatusCode == HttpURLConnection.HTTP_BAD_REQUEST) { // BundleException
        return getXMLElement(is);
      }
    } finally {
      if (is != null) {
        try {
          is.close();
				} catch (Throwable i) {
          /**/
        }
      }
    }

    return null;
  }

  protected Element updateBundle(String requestURI, URL url, String invalidLocation, boolean byLocation, int expectedStatusCode,
      String expectedContentType) throws IOException, ParserConfigurationException, SAXException {
    InputStream is = null;
    if (byLocation) {
      is = executeRequest(requestURI, "PUT", "text/plain", APPLICATION_XML, expectedContentType, expectedStatusCode,
        null /* additionalProps */, invalidLocation == null ? url.toString() : invalidLocation);
    } else {
      is = executeRequest(requestURI, "PUT", "vnd.osgi.bundle", APPLICATION_XML, expectedContentType, expectedStatusCode,
        null /* additionalProps */, invalidLocation == null ? url.openStream() : new ByteArrayInputStream(invalidLocation.getBytes()));
    }

    try {
      return getXMLElement(is);
    } finally {
      if (is != null) {
        try {
          is.close();
				} catch (Throwable i) {
          /**/
        }
      }
    }
  }

  protected Element uninstallBundle(String requestURI, int expectedStatusCode) throws IOException, SAXException,
      ParserConfigurationException {
    InputStream is = executeRequest(requestURI, "DELETE", null, APPLICATION_XML, null, expectedStatusCode,
      null /* additionalProps */, null);

    try {
      return getXMLElement(is);
    } finally {
      if (is != null) {
        try {
          is.close();
				} catch (Throwable i) {
          /**/
        }
      }
    }
  }

  protected Element updateBundleState(String requestURI, int newState, int options, int expectedResponseCode, String acceptType,
      String contentType, String expectedContentType) throws IOException, SAXException, ParserConfigurationException,
      TransformerConfigurationException, TransformerException {
    Document doc = getDocumentBuilder().newDocument();
    Element rootNode = doc.createElement("bundleState");
    doc.appendChild(rootNode);

    Element stateElement = doc.createElement("state");
    rootNode.appendChild(stateElement);
    stateElement.setTextContent(String.valueOf(newState));

    if (options != -1) {
      Element optionsElement = doc.createElement("options");
      rootNode.appendChild(optionsElement);
      optionsElement.setTextContent(String.valueOf(options));
    }

    String strBody = documentToString(doc);
    debug("strBody:" + strBody, null);

    InputStream is = executeRequest(requestURI, "PUT", contentType, acceptType, expectedContentType, expectedResponseCode, null, strBody);

    // Bundle state representation or BundleException
    try {
      return getXMLElement(is);
    } finally {
      if (is != null) {
        try {
          is.close();
				} catch (Throwable i) {
          /**/
        }
      }
    }
  }

  protected Element updateBundleStartLevel(String requestURI, int newStartLevel, int expectedResponseCode, String acceptType,
      String contentType) throws IOException, TransformerConfigurationException, SAXException, ParserConfigurationException,
      TransformerException {
    Document doc = getDocumentBuilder().newDocument();
    Element rootNode = doc.createElement("bundleStartLevel");
    doc.appendChild(rootNode);

    Element startLevelElement = doc.createElement("startLevel");
    rootNode.appendChild(startLevelElement);
    startLevelElement.setTextContent(String.valueOf(newStartLevel));

    String strBody = documentToString(doc);
    debug("strBody:" + strBody, null);

    InputStream is = executeRequest(requestURI, "PUT", contentType, acceptType, null, expectedResponseCode, null, strBody);

    try {
      if (expectedResponseCode == HttpURLConnection.HTTP_OK || expectedResponseCode == HttpURLConnection.HTTP_NOT_FOUND) {
        return getXMLElement(is);
      }
    } finally {
      if (is != null) {
        try {
          is.close();
				} catch (Throwable i) {
          /**/
        }
      }
    }

    return null;
  }

  protected Element getXMLObject(String uri, String expectedContentType, int expectedResponseCode)
      throws IOException, ParserConfigurationException, SAXException {
    InputStream is = executeRequest(uri, "GET", null, APPLICATION_XML, expectedContentType, expectedResponseCode, null, null);

    try {
      if (expectedResponseCode == HttpURLConnection.HTTP_OK) {
        return getXMLElement(is);
      }
    } finally {
      if (is != null) {
        try {
          is.close();
				} catch (Throwable i) {
          /**/
        }
      }
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
						} catch (Throwable i) {
							/**/
            }
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
        String responseContentType = connection.getContentType();
        debug("Response ContentType:" + responseContentType, null);
        if (expectedContentType != null) {
          assertTrue("ContentType", (responseContentType != null) && (responseContentType.startsWith(expectedContentType)));
        }

        in = connection.getErrorStream();
      }

      return in;
    } catch (IOException cause) {
      fail(cause.getMessage());
    }

    return null;
  }

  private Element getXMLElement(InputStream is) throws IOException, ParserConfigurationException, SAXException {
    if (is != null) {
      InputStream data = validate(is);
      Document document = getDocumentBuilder().parse(data);
			if (debugOn) {
				printDocument(document, System.out);
			}
      return document.getDocumentElement();
    }

    return null;
  }

	public static void printDocument(Document doc, OutputStream out)
			throws IOException {
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
					"no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "4");

			transformer.transform(new DOMSource(doc),
					new StreamResult(new OutputStreamWriter(out, "UTF-8")));
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

  protected InputStream validate(InputStream data) throws SAXException, IOException {
    if (validateXMLRepresentations) {
      byte[] byteArrCopy = toByteArray(data);
      Source source = new StreamSource(new ByteArrayInputStream(byteArrCopy));
      validator.validate(source);

      return new ByteArrayInputStream(byteArrCopy);
    }

    return data;
  }

  private String getTextValue(Element ele, String tagName) {
    String textVal = null;
    NodeList nl = ele.getElementsByTagName(tagName);
		if (nl == null) {
			return textVal;
		}
		int len = nl.getLength();
		for (int i = 0; i < len; i++) {
			Element el = (Element) nl.item(i);
			if (ele.isSameNode(el.getParentNode())) {
				textVal = getTextValue(el);
				break;
			}
		}

    return textVal;
  }

  private String getTextValue(Element element) {
    return element.getFirstChild().getNodeValue();
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

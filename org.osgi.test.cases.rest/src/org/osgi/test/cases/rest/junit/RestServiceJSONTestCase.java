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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONWriter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;

/**
 * Tests REST Management Service JSON Representations.
 *
 * @author Petia Sotirova
 */
public class RestServiceJSONTestCase extends RestTestUtils {

  // 5.1.1
  public void testFrameworkStartLevel() throws JSONException, IOException {
    FrameworkStartLevel frameworkStartLevel = getFrameworkStartLevel();
    int originalStartLevel = frameworkStartLevel.getStartLevel();
    int originalInitialBundleStartLevel = frameworkStartLevel.getInitialBundleStartLevel();

    JSONObject jsonStartLevel = getJSONObject(FW_START_LEVEL_URI, FW_START_LEVEL_CONTENT_TYPE_JSON, HttpURLConnection.HTTP_OK);

    assertEquals("original startLevel", frameworkStartLevel.getStartLevel(), jsonStartLevel.getInt("startLevel"));
    assertEquals("original initialBundleStartLevel", frameworkStartLevel.getInitialBundleStartLevel(),
      jsonStartLevel.getInt("initialBundleStartLevel"));

    Object object = getNonSupportedMediaTypeObject(FW_START_LEVEL_URI, FW_START_LEVEL_CONTENT_TYPE_JSON,
      HttpURLConnection.HTTP_NOT_ACCEPTABLE);
    assertNull("Request with not supported media type " + NON_SUPPORTED_MEDIA_TYPE, object);

    int startLevel = originalStartLevel;
    int initialBundleStartLevel = originalInitialBundleStartLevel + 1;
    updateFWStartLevel(startLevel, initialBundleStartLevel, HttpURLConnection.HTTP_NO_CONTENT, null, true);

    frameworkStartLevel = getFrameworkStartLevel();

    assertEquals("startLevel after put", startLevel, frameworkStartLevel.getStartLevel());
    assertEquals("initialBundleStartLevel after put", initialBundleStartLevel, frameworkStartLevel.getInitialBundleStartLevel());

    jsonStartLevel = getJSONObject(FW_START_LEVEL_URI, FW_START_LEVEL_CONTENT_TYPE_JSON, HttpURLConnection.HTTP_OK);
    assertEquals("startLevel", frameworkStartLevel.getStartLevel(), jsonStartLevel.getInt("startLevel"));
    assertEquals("initialBundleStartLevel", frameworkStartLevel.getInitialBundleStartLevel(),
      jsonStartLevel.getInt("initialBundleStartLevel"));

    frameworkStartLevel.setStartLevel(originalStartLevel);
    frameworkStartLevel.setInitialBundleStartLevel(originalInitialBundleStartLevel);

    jsonStartLevel = getJSONObject(FW_START_LEVEL_URI, FW_START_LEVEL_CONTENT_TYPE_JSON, HttpURLConnection.HTTP_OK);
    assertEquals("updated startLevel", originalStartLevel, jsonStartLevel.getInt("startLevel"));
    assertEquals("updated initialBundleStartLevel", originalInitialBundleStartLevel, jsonStartLevel.getInt("initialBundleStartLevel"));

    //  Check PUT with Illegal Arguments
    updateFWStartLevel(-1, originalInitialBundleStartLevel, HttpURLConnection.HTTP_BAD_REQUEST, null, true);
  }

  //5.1.2.1
  public void testBundleList() throws JSONException, IOException, BundleException {
    JSONArray jsonBundleList = getJSONArray(BUNDLE_LIST_URI, BUNDLE_LIST_CONTENT_TYPE_JSON, HttpURLConnection.HTTP_OK);

    Bundle[] bundles = getInstalledBundles();
    assertBundleListRepresentation(bundles, jsonBundleList);

    jsonBundleList = getJSONArray(getBundleListURI(getFilter(TEST_BUNDLE_SYMBOLIC_NAME)), BUNDLE_LIST_CONTENT_TYPE_JSON,
      HttpURLConnection.HTTP_OK);
    assertBundleListRepresentation(new Bundle[]{getBundle(TEST_BUNDLE_SYMBOLIC_NAME)}, jsonBundleList);

    Object object = getNonSupportedMediaTypeObject(BUNDLE_LIST_URI, BUNDLE_LIST_CONTENT_TYPE_JSON, HttpURLConnection.HTTP_NOT_ACCEPTABLE);
    assertNull("Request with not supported media type " + NON_SUPPORTED_MEDIA_TYPE, object);

    Bundle tb1Bundle = getBundle(TB1_TEST_BUNDLE_SYMBOLIC_NAME);
    if (tb1Bundle != null) { // test bundle is already installed => uninstall
      tb1Bundle.uninstall();
    }
    URL url = getContext().getBundle().getEntry(TB1);

    // install bundle with location
    Object result = installBundle(BUNDLE_LIST_URI, url, null, null, true, HttpURLConnection.HTTP_OK, null);
    assertNotNull("Install bundle by URI:", result);
    if (result instanceof String) {
      assertTrue("Bundle URI is returend", ((String)result).indexOf(BUNDLE_URI) != -1);
    } else {
      fail("Location string expected");
    }

    // same bundle location
    result = installBundle(BUNDLE_LIST_URI, url, null, null, true, HttpURLConnection.HTTP_CONFLICT, null);
    assertNull("Install bundle by same URI:", result);

    result = installBundle(BUNDLE_LIST_URI, null, "invalid bundle location", null, true, HttpURLConnection.HTTP_BAD_REQUEST,
      BUNDLE_EXCEPTION_CONTENT_TYPE_JSON);
    assertBundleException(result, "Install bundle by invalid URI.");

    // install bundle with bundle content
    Bundle tb2Bundle = getBundle(TB2_TEST_BUNDLE_SYMBOLIC_NAME);
    if (tb2Bundle != null) { // test bundle is already installed => uninstall
      tb2Bundle.uninstall();
    }

    url = getContext().getBundle().getEntry(TB2);
    String locationHeader = "/tb2.rest.test.location";

    result = installBundle(BUNDLE_LIST_URI, url, null, locationHeader, false, HttpURLConnection.HTTP_OK, null);
    assertNotNull("Install bundle by bundle content:", result);
    if (result instanceof String) {
      assertTrue("Bundle URI is returend", ((String)result).indexOf(BUNDLE_URI) != -1);
    } else {
      fail("Location string expected");
    }

    // same bundle location
    result = installBundle(BUNDLE_LIST_URI, url, null, locationHeader, false, HttpURLConnection.HTTP_CONFLICT, null);
    assertNull("Install bundle by same bundle content:", result);

    result = installBundle(BUNDLE_LIST_URI, null, "invalid bundle location", null, false, HttpURLConnection.HTTP_BAD_REQUEST,
      BUNDLE_EXCEPTION_CONTENT_TYPE_JSON);
    assertBundleException(result, "Install bundle by invalid bundle content.");
  }

  // 5.1.2.2
  public void testBundleRepresentationsList() throws JSONException, IOException {
    JSONArray jsonBundleRepresentationsList = getJSONArray(BUNDLE_REPRESENTATIONS_LIST_URI, BUNDLE_REPRESENTATIONS_LIST_CONTENT_TYPE_JSON,
      HttpURLConnection.HTTP_OK);
    assertNotNull("Bundle representations list", jsonBundleRepresentationsList);

    Bundle[] bundles = getInstalledBundles();

    assertEquals("Bundle representations list length:", bundles.length, jsonBundleRepresentationsList.length());

    Hashtable<Long, JSONObject> bundleRepresentations = new Hashtable<Long, JSONObject>();
    for (int k = 0; k < jsonBundleRepresentationsList.length(); k++) {
      JSONObject bundleRepresentation = jsonBundleRepresentationsList.getJSONObject(k);
      bundleRepresentations.put(bundleRepresentation.getLong("id"), bundleRepresentation);
    }

    for (Bundle bundle : bundles) {
      long bundleId = bundle.getBundleId();
      JSONObject bundleRepresentation = bundleRepresentations.get(bundleId);
      assertNotNull("Bundle representations list contains " + bundleId + " :", bundleRepresentation);
      assertBundleRepresentation(bundle, bundleRepresentation);
    }

    // test with filter
    jsonBundleRepresentationsList = getJSONArray(getBundleRepresentationListURI(getFilter(TEST_BUNDLE_SYMBOLIC_NAME)),
      BUNDLE_REPRESENTATIONS_LIST_CONTENT_TYPE_JSON, HttpURLConnection.HTTP_OK);

    Bundle testBundle = getBundle(TEST_BUNDLE_SYMBOLIC_NAME);
    assertEquals("Bundle representations list length:", 1, jsonBundleRepresentationsList.length());

    JSONObject bundleRepresentation = jsonBundleRepresentationsList.getJSONObject(0);
    assertBundleRepresentation(testBundle, bundleRepresentation);

    Object object = getNonSupportedMediaTypeObject(BUNDLE_REPRESENTATIONS_LIST_URI, BUNDLE_REPRESENTATIONS_LIST_CONTENT_TYPE_JSON,
      HttpURLConnection.HTTP_NOT_ACCEPTABLE);
    assertNull("Request with not supported media type " + NON_SUPPORTED_MEDIA_TYPE, object);
  }

  // 5.1.3
  public void testBundle() throws JSONException, IOException, BundleException {
    // GET
    Bundle bundle = getRandomBundle();
    JSONObject bundleRepresentation = getJSONObject(getBundleURI(bundle.getBundleId()), BUNDLE_CONTENT_TYPE_JSON,
      HttpURLConnection.HTTP_OK);
    assertNotNull("Bundle representation : " + bundle.getBundleId() + " :", bundleRepresentation);
    assertBundleRepresentation(bundle, bundleRepresentation);

    long notExistingBundleId = getNotExistingBundleId();
    Object object = getJSONObject(getBundleURI(notExistingBundleId), BUNDLE_CONTENT_TYPE_JSON, HttpURLConnection.HTTP_NOT_FOUND);
    assertNull("Get not existing bundle " + notExistingBundleId + " :", object);

    object = getNonSupportedMediaTypeObject(getBundleURI(bundle.getBundleId()), BUNDLE_CONTENT_TYPE_JSON,
      HttpURLConnection.HTTP_NOT_ACCEPTABLE);
    assertNull("Request with not supported media type " + NON_SUPPORTED_MEDIA_TYPE, object);

    // PUT with location
    Bundle tb1Bundle = getTestBundle(TB1_TEST_BUNDLE_SYMBOLIC_NAME, TB1);

    Object result = updateBundle(getBundleURI(tb1Bundle.getBundleId()), getContext().getBundle().getEntry(TB11), null, true,
      HttpURLConnection.HTTP_NO_CONTENT, null);
    assertNull("Update bundle by location " + tb1Bundle.getBundleId() + " :", result);

    result = updateBundle(getBundleURI(notExistingBundleId), getContext().getBundle().getEntry(TB11), null, true,
      HttpURLConnection.HTTP_NOT_FOUND, null);
    assertNull("Update not existing bundle " + notExistingBundleId + " :", result);

    result = updateBundle(getBundleURI(tb1Bundle.getBundleId()), null, "invalid bundle location", true, HttpURLConnection.HTTP_BAD_REQUEST,
      BUNDLE_EXCEPTION_CONTENT_TYPE_JSON);

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
      BUNDLE_EXCEPTION_CONTENT_TYPE_JSON);

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
  public void testBundleState() throws JSONException, IOException, BundleException {
    // GET
    Bundle bundle = getRandomBundle();
    JSONObject bundleStateRepresentation = getJSONObject(getBundleStateURI(bundle.getBundleId()), BUNDLE_STATE_CONTENT_TYPE_JSON,
      HttpURLConnection.HTTP_OK);
    assertNotNull("Bundle state " + bundle.getBundleId() + " :", bundleStateRepresentation);
    assertBundleStateRepresentation(bundle, bundleStateRepresentation);

    long notExistingBundleId = getNotExistingBundleId();
    Object object = getJSONObject(getBundleStateURI(notExistingBundleId), BUNDLE_STATE_CONTENT_TYPE_JSON, HttpURLConnection.HTTP_NOT_FOUND);
    assertNull("Bundle state for not existing bundle " + notExistingBundleId + " :", object);

    Object result = getNonSupportedMediaTypeObject(getBundleStateURI(bundle.getBundleId()), BUNDLE_STATE_CONTENT_TYPE_JSON,
      HttpURLConnection.HTTP_NOT_ACCEPTABLE);
    assertNull("Request with not supported media type " + NON_SUPPORTED_MEDIA_TYPE, result);

    // PUT
    Bundle tb1Bundle = getTestBundle(TB1_TEST_BUNDLE_SYMBOLIC_NAME, TB1);

    int tb1State = tb1Bundle.getState();

    int newState = tb1State == Bundle.INSTALLED ? Bundle.ACTIVE : Bundle.RESOLVED;
    bundleStateRepresentation = updateBundleState(getBundleStateURI(tb1Bundle.getBundleId()), newState, -1, HttpURLConnection.HTTP_OK,
      null, APPLICATION_JSON, BUNDLE_STATE_CONTENT_TYPE_JSON);
    assertNotNull("Bundle state updated  " + tb1Bundle.getBundleId() + " :", bundleStateRepresentation);

    assertEquals("New state ", newState, tb1Bundle.getState());
    assertBundleStateRepresentation(tb1Bundle, bundleStateRepresentation);

    // start bundle with Bundle-ActivationPolicy by bundle id
    Bundle tb3Bundle = getTestBundle(TB3_TEST_BUNDLE_SYMBOLIC_NAME, TB3);
    int tb3State = tb3Bundle.getState();
    if (tb3State == Bundle.ACTIVE || tb3State == Bundle.STARTING) {
      tb3Bundle.stop();
    }

    bundleStateRepresentation = updateBundleState(getBundleStateURI(tb3Bundle.getBundleId()), Bundle.ACTIVE, Bundle.START_ACTIVATION_POLICY,
      HttpURLConnection.HTTP_OK, null, APPLICATION_JSON, BUNDLE_STATE_CONTENT_TYPE_JSON);
    assertNotNull("Bundle state updated  " + tb3Bundle.getBundleId() + " :", bundleStateRepresentation);

    assertEquals("New state for 'lazy' bundle ", Bundle.STARTING, tb3Bundle.getState());
    assertBundleStateRepresentation(tb3Bundle, bundleStateRepresentation);

    bundleStateRepresentation = updateBundleState(getBundleStateURI(notExistingBundleId), newState, -1, HttpURLConnection.HTTP_NOT_FOUND,
      null, APPLICATION_JSON, null);
    assertNull("Bundle state updated for not existing bundle " + notExistingBundleId + " :", bundleStateRepresentation);

    if (notAcceptableCheck) {
      bundleStateRepresentation = updateBundleState(getBundleStateURI(tb1Bundle.getBundleId()), newState, -1,
        HttpURLConnection.HTTP_NOT_ACCEPTABLE, NON_SUPPORTED_MEDIA_TYPE, APPLICATION_JSON, null);
      assertNull("Bundle state updated for not acceptable media type " + NON_SUPPORTED_MEDIA_TYPE + " :", bundleStateRepresentation);
    }

    Bundle tb21Bundle = getTestBundle(TB21_TEST_BUNDLE_SYMBOLIC_NAME, TB21);
    if (tb21Bundle.getState() != Bundle.ACTIVE) {
      tb21Bundle.start();
    }

    result = updateBundleState(getBundleStateURI(tb21Bundle.getBundleId()), Bundle.RESOLVED, -1, HttpURLConnection.HTTP_BAD_REQUEST, null,
      APPLICATION_JSON, BUNDLE_EXCEPTION_CONTENT_TYPE_JSON);
    assertBundleException(result, "Stop bundle for bundle with error in stop method  " + tb21Bundle.getBundleId());

    // stop bundle with options
    bundleStateRepresentation = updateBundleState(getBundleStateURI(tb3Bundle.getBundleId()), Bundle.RESOLVED, Bundle.STOP_TRANSIENT,
      HttpURLConnection.HTTP_OK, null, APPLICATION_JSON, BUNDLE_STATE_CONTENT_TYPE_JSON);
    assertNotNull("Bundle state updated  " + tb3Bundle.getBundleId() + " :", bundleStateRepresentation);

    assertEquals("New state ", Bundle.RESOLVED, tb3Bundle.getState());
    assertBundleStateRepresentation(tb3Bundle, bundleStateRepresentation);
  }

  // 5.1.5
  public void testBundleHeader() throws JSONException, IOException {
    // GET
    Bundle bundle = getRandomBundle();
    JSONObject bundleHeaderRepresentation = getJSONObject(getBundleHeaderURI(bundle.getBundleId()), BUNDLE_HEADER_CONTENT_TYPE_JSON,
      HttpURLConnection.HTTP_OK);
    assertNotNull("Bundle header " + bundle.getBundleId() + ": ", bundleHeaderRepresentation);
    assertBundleHeaderRepresentation(bundle, bundleHeaderRepresentation);

    long notExistingBundleId = getNotExistingBundleId();
    Object object = getJSONObject(getBundleHeaderURI(notExistingBundleId), BUNDLE_HEADER_CONTENT_TYPE_JSON,
      HttpURLConnection.HTTP_NOT_FOUND);
    assertNull("Bundle header for not existing bundle " + notExistingBundleId + " :", object);

    Object result = getNonSupportedMediaTypeObject(getBundleHeaderURI(bundle.getBundleId()), BUNDLE_HEADER_CONTENT_TYPE_JSON,
      HttpURLConnection.HTTP_NOT_ACCEPTABLE);
    assertNull("Request with not acceptable media type " + NON_SUPPORTED_MEDIA_TYPE, result);
  }

  // 5.1.6
  public void testBundleStartLevel() throws JSONException, IOException, BundleException {
    // GET
    Bundle bundle = getRandomBundle();
    JSONObject bundleStartLevelRepresentation = getJSONObject(getBundleStartLevelURI(bundle.getBundleId()),
      BUNDLE_START_LEVEL_CONTENT_TYPE_JSON, HttpURLConnection.HTTP_OK);
    assertNotNull("Bundle start level " + bundle.getBundleId() + " :", bundleStartLevelRepresentation);
    assertBundleStartLevelRepresentation(bundle, bundleStartLevelRepresentation);

    long notExistingBundleId = getNotExistingBundleId();
    Object object = getJSONObject(getBundleStartLevelURI(notExistingBundleId), BUNDLE_START_LEVEL_CONTENT_TYPE_JSON,
      HttpURLConnection.HTTP_NOT_FOUND);
    assertNull("Bundle start level for not existing bundle " + notExistingBundleId + " :", object);

    Object result = getNonSupportedMediaTypeObject(getBundleStartLevelURI(bundle.getBundleId()), BUNDLE_START_LEVEL_CONTENT_TYPE_JSON,
      HttpURLConnection.HTTP_NOT_ACCEPTABLE);
    assertNull("Request with not acceptable media type " + NON_SUPPORTED_MEDIA_TYPE, result);

    // PUT
    Bundle tb1Bundle = getTestBundle(TB1_TEST_BUNDLE_SYMBOLIC_NAME, TB1);

    int tb1StartLevel = getBundleStartLevel(tb1Bundle).getStartLevel();
    int newStartLevel = tb1StartLevel + 1;
    bundleStartLevelRepresentation = updateBundleStartLevel(getBundleStartLevelURI(tb1Bundle.getBundleId()), newStartLevel,
      HttpURLConnection.HTTP_OK, null, APPLICATION_JSON);
    assertNotNull("Bundle start level updated " + tb1Bundle.getBundleId() + " :", bundleStartLevelRepresentation);

    tb1Bundle = getTestBundle(TB1_TEST_BUNDLE_SYMBOLIC_NAME, TB1);
    assertBundleStartLevelRepresentation(tb1Bundle, bundleStartLevelRepresentation);

    tb1StartLevel = getBundleStartLevel(tb1Bundle).getStartLevel();

    assertEquals("New start level ", newStartLevel, tb1StartLevel);

    bundleStartLevelRepresentation = updateBundleStartLevel(getBundleStartLevelURI(notExistingBundleId), newStartLevel,
      HttpURLConnection.HTTP_NOT_FOUND, null, APPLICATION_JSON);
    assertNull("Bundle start level updated for not existing bundle " + notExistingBundleId + " :", bundleStartLevelRepresentation);

    if (notAcceptableCheck) {
      bundleStartLevelRepresentation = updateBundleStartLevel(getBundleStartLevelURI(tb1Bundle.getBundleId()), newStartLevel,
        HttpURLConnection.HTTP_NOT_ACCEPTABLE, NON_SUPPORTED_MEDIA_TYPE, APPLICATION_JSON);
      assertNull("Bundle start level updated for not acceptable media type " + NON_SUPPORTED_MEDIA_TYPE + " :",
        bundleStartLevelRepresentation);
    }

    result = updateBundleStartLevel(getBundleStartLevelURI(tb1Bundle.getBundleId()), -1, HttpURLConnection.HTTP_BAD_REQUEST,
      null, APPLICATION_JSON);
  }

  // 5.1.7.1
  public void testServiceList() throws JSONException, IOException, InvalidSyntaxException {
    String filter = null;
    ServiceReference<?>[] serviceRefs = getServices(filter);

    JSONArray jsonServiceList = getJSONArray(getServiceListURI(filter), SERVICE_LIST_CONTENT_TYPE_JSON, HttpURLConnection.HTTP_OK);
    assertServiceList(jsonServiceList, serviceRefs);

    filter = "(&(" + Constants.SERVICE_ID + ">=" + serviceRefs.length / 2 + ")"
        + "(" + Constants.SERVICE_ID + "<=" + serviceRefs.length + "))";

    serviceRefs = getServices(filter);
    jsonServiceList = getJSONArray(getServiceListURI(filter), SERVICE_LIST_CONTENT_TYPE_JSON, HttpURLConnection.HTTP_OK);
    assertServiceList(jsonServiceList, serviceRefs);

    String invalidFilterURI = SERVICE_LIST_URI + "?filter=invalid-filter";
    jsonServiceList = getJSONArray(invalidFilterURI, null, HttpURLConnection.HTTP_BAD_REQUEST);
    assertNull("Request with invalid filter " + invalidFilterURI, jsonServiceList);

    Object object = getNonSupportedMediaTypeObject(SERVICE_LIST_URI, SERVICE_LIST_CONTENT_TYPE_JSON, HttpURLConnection.HTTP_NOT_ACCEPTABLE);
    assertNull("Request with not supported media type " + NON_SUPPORTED_MEDIA_TYPE, object);
  }

  // 5.1.7.2
  public void testServiceRepresentationsList() throws JSONException, IOException, InvalidSyntaxException {
    String filter = null;
    ServiceReference<?>[] serviceRefs = getServices(filter);
    JSONArray jsonServiceRepresentationsList = getJSONArray(getServiceRepresentationListURI(filter),
      SERVICE_REPRESENTATIONS_LIST_CONTENT_TYPE_JSON, HttpURLConnection.HTTP_OK);
    assertNotNull("Service representations list", jsonServiceRepresentationsList);

    assertServiceRepresentationList(jsonServiceRepresentationsList, serviceRefs);

    filter = "(&(" + Constants.SERVICE_ID + ">=" + serviceRefs.length / 2 + ")"
        + "(" + Constants.SERVICE_ID + "<=" + serviceRefs.length + "))";

    serviceRefs = getServices(filter);
    jsonServiceRepresentationsList = getJSONArray(getServiceRepresentationListURI(filter), SERVICE_REPRESENTATIONS_LIST_CONTENT_TYPE_JSON,
      HttpURLConnection.HTTP_OK);
    assertServiceRepresentationList(jsonServiceRepresentationsList, serviceRefs);

    filter = "invalid-filter";
    jsonServiceRepresentationsList = getJSONArray(getServiceRepresentationListURI(filter), null, HttpURLConnection.HTTP_BAD_REQUEST);
    assertNull("Request with invalid filter '" + filter + "'", jsonServiceRepresentationsList);

    Object object = getNonSupportedMediaTypeObject(getServiceRepresentationListURI(null), SERVICE_REPRESENTATIONS_LIST_CONTENT_TYPE_JSON,
      HttpURLConnection.HTTP_NOT_ACCEPTABLE);
    assertNull("Request with not supported media type " + NON_SUPPORTED_MEDIA_TYPE, object);
  }

  // 5.1.8
  public void testService() throws InvalidSyntaxException, JSONException, IOException {
    ServiceReference<?> serviceRef = getRandomService();

    // GET
    JSONObject serviceRepresentation = getJSONObject(getServiceURI(serviceRef), SERVICE_CONTENT_TYPE_JSON, HttpURLConnection.HTTP_OK);
    assertNotNull("Service representation " + serviceRef + " :", serviceRepresentation);
    assertService(serviceRepresentation, serviceRef);

    String notExistingSeriveId = "not-existing-servicve-" + System.currentTimeMillis();
    serviceRepresentation = getJSONObject(getServiceURI(notExistingSeriveId), SERVICE_CONTENT_TYPE_JSON, HttpURLConnection.HTTP_NOT_FOUND);
    assertNull("Service representation for not existing service " + notExistingSeriveId + " :", serviceRepresentation);

    Object result = getNonSupportedMediaTypeObject(getServiceURI(serviceRef), SERVICE_CONTENT_TYPE_JSON,
      HttpURLConnection.HTTP_NOT_ACCEPTABLE);
    assertNull("Request with not acceptable media type " + NON_SUPPORTED_MEDIA_TYPE, result);
  }

  // 5.6.1 The Extensions Resource
  public void testExtensions() throws JSONException, IOException, BundleException {
    JSONArray result = getJSONArray("extensions", EXTENSIONS_CONTENT_TYPE_JSON, HttpURLConnection.HTTP_OK);
    assertExtensions(result, null, 0);

    Bundle bundle = getTestBundle(TB5_TEST_BUNDLE_SYMBOLIC_NAME, TB5);
    bundle.start();

    result = getJSONArray(EXTENSIONS_URI, EXTENSIONS_CONTENT_TYPE_JSON, HttpURLConnection.HTTP_OK);
    assertExtensions(result, Arrays.asList(new String[]{"REST CT Extension", "contributions/extension"})/* name, path*/, 1);

    bundle = getTestBundle(TB6_TEST_BUNDLE_SYMBOLIC_NAME, TB6);
    bundle.start();

    result = getJSONArray(EXTENSIONS_URI, EXTENSIONS_CONTENT_TYPE_JSON, HttpURLConnection.HTTP_OK);
    assertExtensions(result, Arrays.asList(new String[]{"REST CT Extension", "contributions/extension",
            "REST Extension full URI", "http://127.0.0.1/ct/rest/extension"})/* name, path, name, path*/, 2);

    bundle.stop();
    result = getJSONArray(EXTENSIONS_URI, EXTENSIONS_CONTENT_TYPE_JSON, HttpURLConnection.HTTP_OK);
    assertExtensions(result, Arrays.asList(new String[]{"REST CT Extension", "contributions/extension"}), 1);

    Object notSupportedResult = getNonSupportedMediaTypeObject(EXTENSIONS_URI, EXTENSIONS_CONTENT_TYPE_JSON,
      HttpURLConnection.HTTP_NOT_ACCEPTABLE);
    assertNull("Request with not acceptable media type " + NON_SUPPORTED_MEDIA_TYPE, notSupportedResult);

    bundle = getTestBundle(TB5_TEST_BUNDLE_SYMBOLIC_NAME, TB5);
    bundle.stop();

    result = getJSONArray("extensions", EXTENSIONS_CONTENT_TYPE_JSON, HttpURLConnection.HTTP_OK);
    assertExtensions(result, null, 0);
  }

// protected

  protected void assertExtensions(JSONArray jsonExtensionsList, List<String> extProps /* name, path, name, path*/,
      int expectedExtensionsCount) throws JSONException {
    if (expectedExtensionsCount == 0) {
      assertEquals("No extensions.", expectedExtensionsCount, jsonExtensionsList.length());
      return;
    }

    assertNotNull("Extensions are not null.", jsonExtensionsList);
    assertEquals("Extensions size is " + expectedExtensionsCount + ".", expectedExtensionsCount, jsonExtensionsList.length());

    HashMap<String, JSONObject> extensions = new HashMap<String, JSONObject>();
    for (int k = 0; k < jsonExtensionsList.length(); k++) {
      JSONObject ext = jsonExtensionsList.getJSONObject(k);

      String name = ext.getString("name");
      String path = ext.getString("path");

      assertNotNull("Name is not null.", name);
      assertNotNull("Path is not null.", path);

      extensions.put(name, ext);
    }

    for (int k = 0; k < extProps.size(); k++) {
      String expectedName = extProps.get(k++);
      String expectedPath = extProps.get(k);

      JSONObject ext = extensions.get(expectedName);

      assertTrue("Extensions list contains " + expectedName + ".", ext != null);
      assertEquals("Extension path.", expectedPath, ext.getString("path"));
    }
  }


  protected void assertBundleRepresentation(Bundle bundle, JSONObject bundleRepresentation) throws JSONException {
    assertEquals("lastModified:", bundle.getLastModified(), bundleRepresentation.getLong("lastModified"));
    assertEquals("location:", bundle.getLocation(), bundleRepresentation.getString("location"));
    assertEquals("state:", bundle.getState(), bundleRepresentation.getInt("state"));
    assertEquals("symbolicName:", bundle.getSymbolicName(), bundleRepresentation.getString("symbolicName"));
    assertEquals("version:", bundle.getVersion().toString(), bundleRepresentation.getString("version"));
  }

  protected void assertBundleListRepresentation(Bundle[] bundles, JSONArray jsonBundleList) throws JSONException {
    assertNotNull("Bundle list", jsonBundleList);
    assertEquals("Bundle list length:", bundles.length, jsonBundleList.length());

    HashSet<String> bundleURIs = new HashSet<String>();
    for (int k = 0; k < jsonBundleList.length(); k++) {
      bundleURIs.add(jsonBundleList.getString(k));
    }

    for (Bundle bundle : bundles) {
      String bundleURI = getBundleURI(bundle.getBundleId());
      assertTrue("Bundle list contains " + bundleURI + " :", bundleURIs.contains(bundleURI));
    }
  }

  protected void assertBundleStateRepresentation(Bundle bundle, JSONObject bundleStateRepresentation) throws JSONException {
    assertEquals("state:", bundle.getState(), bundleStateRepresentation.getInt("state"));
  }

  protected void assertBundleHeaderRepresentation(Bundle bundle, JSONObject bundleHeaderRepresentation) throws JSONException {
    Dictionary<String, String> headers = bundle.getHeaders();

    for (Enumeration<String> keys = headers.keys(); keys.hasMoreElements();) {
      String key = keys.nextElement();
      assertEquals(key + ":", headers.get(key), bundleHeaderRepresentation.getString(key));
    }
  }

  protected void assertBundleStartLevelRepresentation(Bundle bundle, JSONObject bundleStartLevelRepresentation) throws JSONException {
    BundleStartLevel bundleStartLevel = getBundleStartLevel(bundle);

    assertEquals("startLevel:", bundleStartLevel.getStartLevel(), bundleStartLevelRepresentation.getInt("startLevel"));
    assertEquals("activationPolicyUsed:", bundleStartLevel.isActivationPolicyUsed(),
      bundleStartLevelRepresentation.getBoolean("activationPolicyUsed"));
    assertEquals("persistentlyStarted:", bundleStartLevel.isPersistentlyStarted(),
      bundleStartLevelRepresentation.getBoolean("persistentlyStarted"));
  }

  protected void assertServiceList(JSONArray jsonServiceList, ServiceReference<?>[] serviceRefs) throws JSONException {
    assertNotNull("Service list", jsonServiceList);

    assertEquals("Service list length:", serviceRefs.length, jsonServiceList.length());

    HashSet<String> serviceURIs = new HashSet<String>();
    for (int k = 0; k < jsonServiceList.length(); k++) {
      serviceURIs.add(jsonServiceList.getString(k));
    }

    for (ServiceReference<?> serviceRef : serviceRefs) {
      String serviceURI = getServiceURI(serviceRef.getProperty(Constants.SERVICE_ID).toString());
      assertTrue("Service list contains " + serviceURI + ": ", serviceURIs.contains(serviceURI));
    }
  }

  protected void assertServiceRepresentationList(JSONArray jsonServiceRepresentationList, ServiceReference<?>[] serviceRefs)
      throws JSONException {
    if (serviceRefs != null) {
      assertNotNull("Service representation list", jsonServiceRepresentationList);

      Hashtable<Object, JSONObject> serviceRepresentationHT = new Hashtable<Object, JSONObject>();

      for (int k = 0; k < jsonServiceRepresentationList.length(); k++) {
        JSONObject serviceRepresentation = jsonServiceRepresentationList.getJSONObject(k);
        try {
          JSONObject props = serviceRepresentation.getJSONObject("properties");
          assertNotNull("Service representation properties ", props);

          Object id = props.get(Constants.SERVICE_ID);
          assertNotNull("Service id ", id);
          String serviceId = String.valueOf(id);

          serviceRepresentationHT.put(serviceId, serviceRepresentation);
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

  protected void assertService(JSONObject serviceRepresentation, ServiceReference<?> serviceRef) throws JSONException {
    assertNotNull("Service representation ", serviceRepresentation);

    String[] propKeys = serviceRef.getPropertyKeys();

    JSONObject propsRepresentation = serviceRepresentation.getJSONObject("properties");

    if (propKeys != null) {
      String[] names = JSONObject.getNames(propsRepresentation);
      HashSet<String> propNames = new HashSet<String>();
      propNames.addAll(Arrays.asList(names));

      assertEquals("Properties size ", propKeys.length, names.length);
      for (String key : propKeys) {
        Object value = serviceRef.getProperty(key);

        assertTrue("Service property " + key, propNames.contains(key));
        if (value.getClass().isArray()) {
          JSONArray arrayProp = propsRepresentation.getJSONArray(key);

          HashSet<Object> arrayPropValues = new HashSet<Object>();
          for (int k = 0; k < arrayProp.length(); k++) {
            arrayPropValues.add(arrayProp.get(k));
          }

          int length = Array.getLength(value);
          assertEquals("Service array property size ", arrayProp.length(), length);
          for (int k = 0; k < length; k++) {
            assertTrue("Service array property " + key, arrayPropValues.contains(Array.get(value, k)));
          }
        } else if (value instanceof Number) {
          assertEquals("Service number property " + key, ((Number)value).intValue(), propsRepresentation.get(key));
        } else {
          assertEquals("Service property value for " + key, value, propsRepresentation.get(key));
        }
      }
    } else { // Is it possible?
      assertNull("No properties for service " + serviceRef.getProperty(Constants.SERVICE_ID) + " :", propsRepresentation);
    }

    assertEquals("Bundle property", getBundleURI(serviceRef.getBundle()), serviceRepresentation.getString("bundle"));

    Bundle[] usingBundles = serviceRef.getUsingBundles();
    if (usingBundles != null) {
      HashSet<String> uBundlesSet = new HashSet<String>();
      for (Bundle uBundle : usingBundles) {
        uBundlesSet.add(getBundleURI(uBundle));
      }

      JSONArray usingBundlesRepresentation = serviceRepresentation.getJSONArray("usingBundles");
      assertNotNull("usingBundles not null", usingBundlesRepresentation);
      assertEquals("usingBundles length", uBundlesSet.size(), usingBundlesRepresentation.length());

      for (int k = 0; k < usingBundlesRepresentation.length(); k++) {
        String ub = usingBundlesRepresentation.getString(k);
        assertTrue("usingBundles element " + ub, uBundlesSet.contains(ub));
      }
    } else {
      JSONArray uBundles = serviceRepresentation.getJSONArray("usingBundles");
      assertTrue("usingBundles", uBundles == null || uBundles.length() == 0);
    }
  }

  protected void assertBundleException(Object result, String assertMessage) throws JSONException  {
    assertNotNull(assertMessage, result);

    if (result instanceof JSONObject) {
      int typeCode = ((JSONObject)result).getInt("typecode");
      String message = ((JSONObject)result).getString("message");

      assertTrue(assertMessage, typeCode >= 0); // some of BundleException error codes
      assertTrue("Error message is not null.", message != null);
    } else {
      fail("BundleException Representation expected.");
    }
  }

  protected void updateFWStartLevel(int startLevel, int initialBundleStartLevel, int expectedStatusCode, String acceptType,
      boolean jsonMediaType) throws JSONException {
    JSONWriter jsonWriter = new JSONStringer().object();
    jsonWriter.key("startLevel").value(startLevel);
    jsonWriter.key("initialBundleStartLevel").value(initialBundleStartLevel);

    String strBody = jsonWriter.endObject().toString();
    String contentType = jsonMediaType ? APPLICATION_JSON : NON_SUPPORTED_MEDIA_TYPE;

    executeRequest(FW_START_LEVEL_URI, "PUT", contentType, acceptType, null, expectedStatusCode, null, strBody);
  }

  protected Object installBundle(String requestURI, URL url, String invalidLocation, String locationHeader, boolean byLocation,
      int expectedStatusCode, String expectedContentType) throws IOException, JSONException {
    String result = null;
    if (byLocation) {
      result = executeRequest(requestURI, "POST", "text/plain", null, expectedContentType, expectedStatusCode, null /* additionalProps */,
        invalidLocation == null ? url.toString() : invalidLocation);
    } else {
      HashMap<String, String> additionalProps = new HashMap<String, String>();
      if (locationHeader != null) {
        additionalProps.put("Content-Location", locationHeader);
      }

      result = executeRequest(requestURI, "POST", "vnd.osgi.bundle", null, expectedContentType, expectedStatusCode, additionalProps,
          invalidLocation == null ? url.openStream() : new ByteArrayInputStream(invalidLocation.getBytes()));
    }

    if (result != null && expectedStatusCode == HttpURLConnection.HTTP_BAD_REQUEST) { // BundleException
      return new JSONObject(result);
    }

    return result;
  }

  protected Object updateBundle(String requestURI, URL url, String invalidLocation, boolean byLocation, int expectedStatusCode,
      String expectedContentType) throws IOException, JSONException {
    String result = null;
    if (byLocation) {
      result = executeRequest(requestURI, "PUT", "text/plain", null, expectedContentType, expectedStatusCode, null /* additionalProps */,
          invalidLocation == null ? url.toString() : invalidLocation);
    } else {
      result = executeRequest(requestURI, "PUT", "vnd.osgi.bundle", null, expectedContentType, expectedStatusCode, null /* additionalProps */,
          invalidLocation == null ? url.openStream() : new ByteArrayInputStream(invalidLocation.getBytes()));
    }

    debug("RESULT IS " + result, null);

    if (result != null && expectedStatusCode == HttpURLConnection.HTTP_BAD_REQUEST) { // BundleException
      return new JSONObject(result);
    }

    return result;
  }

  protected Object uninstallBundle(String requestURI, int expectedStatusCode) throws IOException, JSONException {
    String result = executeRequest(requestURI, "DELETE", null, null, null, expectedStatusCode, null /* additionalProps */, null);

    if (result != null && expectedStatusCode == HttpURLConnection.HTTP_BAD_REQUEST) { // BundleException
      return new JSONObject(result);
    }

    return result;
  }

  protected JSONObject updateBundleState(String requestURI, int newState, int options, int expectedResponseCode, String acceptType,
      String contentType, String expectedContentType) throws IOException, JSONException {
    JSONWriter jsonWriter = new JSONStringer().object();
    jsonWriter.key("state").value(newState);
    if (options != -1) {
      jsonWriter.key("options").value(options);
    }

    String result = executeRequest(requestURI, "PUT", contentType, acceptType, expectedContentType, expectedResponseCode, null,
      jsonWriter.endObject().toString());

    if (result != null) {  // Bundle state representation or BundleException
      return new JSONObject(result);
    }

    return null;
  }

  protected JSONObject updateBundleStartLevel(String requestURI, int newStartLevel, int expectedResponseCode, String acceptType,
      String contentType) throws IOException, JSONException {
    JSONWriter jsonWriter = new JSONStringer().object();
    jsonWriter.key("startLevel").value(newStartLevel);

    String result = executeRequest(requestURI, "PUT", contentType, acceptType, null, expectedResponseCode, null,
      jsonWriter.endObject().toString());

    if (result != null) { // Bundle start level representation or BundleException
      try {
        return new JSONObject(result);
			} catch (JSONException e) {
        return null;
      }
    }

    return null;
  }

  protected JSONObject getJSONObject(String uri, String expectedContentType, int expectedResponseCode) throws JSONException, IOException {
    String result = executeRequest(uri, "GET", null, APPLICATION_JSON, expectedContentType, expectedResponseCode, null, null);
    if (expectedResponseCode == HttpURLConnection.HTTP_OK) {
      return new JSONObject(result);
    }

    return null;
  }

  protected JSONArray getJSONArray(String uri, String expectedContentType, int expectedResponseCode) throws JSONException, IOException {
    String result = executeRequest(uri, "GET", null, APPLICATION_JSON, expectedContentType, expectedResponseCode, null, null);
    if (expectedResponseCode == HttpURLConnection.HTTP_OK) {
      return new JSONArray(result);
    }

    return null;
  }

  protected Object getNonSupportedMediaTypeObject(String uri, String expectedContentType, int expectedResponseCode)
      throws JSONException, IOException {
    if (notAcceptableCheck) {
      return executeRequest(uri, "GET", null, NON_SUPPORTED_MEDIA_TYPE, expectedContentType, expectedResponseCode, null, null);
    }

    return null;
  }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


  protected String executeRequest(String uri, String method, String contentType, String acceptType, String expectedContentType,
      int expectedResponseCode, Map<String, String> additionalProps, Object body) {
    HttpURLConnection connection = null;
    BufferedReader in = null;
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
        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
        String responseContentType = connection.getContentType();
        debug("Response ContentType:" + responseContentType, null);
        if (expectedContentType != null) {
          assertTrue("ContentType", (responseContentType != null) && (responseContentType.startsWith(expectedContentType)));
        }
        in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
      }

      if (in != null) {
        String temp = null;
        StringBuilder sb = new StringBuilder();
        while ((temp = in.readLine()) != null) {
          sb.append(temp);
        }
        String result = sb.toString();

        debug("Result:" + result, null);
        return result;
      }
    } catch (IOException cause) {
      fail(cause.getMessage());
    } finally {
      if (in != null) {
        try {
          in.close();
				} catch (Throwable i) {
          /**/
        }
      }
    }

    return null;
  }

}

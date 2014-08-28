/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
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
package org.osgi.test.cases.rest.junit;

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

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

public class RestServiceTestCase extends RestTestUtils {

  // TODO - Events
  // 5.1.1
  public void testFrameworkStartLevel() throws JSONException, IOException {
    FrameworkStartLevel frameworkStartLevel = getFrameworkStartLevel();
    int originalStartLevel = frameworkStartLevel.getStartLevel();
    int originalInitialBundleStartLevel = frameworkStartLevel.getInitialBundleStartLevel();

    JSONObject jsonStartLevel = getJSONObject(FW_START_LEVEL_URI, FW_START_LEVEL_CONTENT_TYPE_JSON, Status.SUCCESS_OK);

    assertEquals("original startLevel", frameworkStartLevel.getStartLevel(), jsonStartLevel.getInt("startLevel"));
    assertEquals("original initialBundleStartLevel", frameworkStartLevel.getInitialBundleStartLevel(), jsonStartLevel.getInt("initialBundleStartLevel"));

    Object object = getNonSupportedMediaTypeObject(FW_START_LEVEL_URI, FW_START_LEVEL_CONTENT_TYPE_JSON, Status.CLIENT_ERROR_NOT_ACCEPTABLE);
    assertNull("Request with not supported media type " + NON_SUPPORTED_MEDIA_TYPE, object);

    int startLevel = originalStartLevel /* TODO + 1 */;
    int initialBundleStartLevel = originalInitialBundleStartLevel + 1;
    updateFWStartLevel(startLevel, initialBundleStartLevel, Status.SUCCESS_NO_CONTENT.getCode(), null, null);

    frameworkStartLevel = getFrameworkStartLevel();  // TODO Is it necessary

    assertEquals("startLevel after put", startLevel, frameworkStartLevel.getStartLevel());
    assertEquals("initialBundleStartLevel after put", initialBundleStartLevel, frameworkStartLevel.getInitialBundleStartLevel());

    jsonStartLevel = getJSONObject(FW_START_LEVEL_URI, FW_START_LEVEL_CONTENT_TYPE_JSON, Status.SUCCESS_OK);
    assertEquals("startLevel", frameworkStartLevel.getStartLevel(), jsonStartLevel.getInt("startLevel"));
    assertEquals("initialBundleStartLevel", frameworkStartLevel.getInitialBundleStartLevel(), jsonStartLevel.getInt("initialBundleStartLevel"));

    frameworkStartLevel.setStartLevel(originalStartLevel, this);
    frameworkStartLevel.setInitialBundleStartLevel(originalInitialBundleStartLevel);

    jsonStartLevel = getJSONObject(FW_START_LEVEL_URI, FW_START_LEVEL_CONTENT_TYPE_JSON, Status.SUCCESS_OK);
    assertEquals("updated startLevel", originalStartLevel, jsonStartLevel.getInt("startLevel"));
    assertEquals("updated initialBundleStartLevel", originalInitialBundleStartLevel, jsonStartLevel.getInt("initialBundleStartLevel"));

    // Check PUT with UNSUPPORTED MEDIA TYPE)
    updateFWStartLevel(startLevel, initialBundleStartLevel, Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE.getCode(), null, NON_SUPPORTED_MEDIA_TYPE);

    //  Check PUT with Illegal Arguments
    Object result = updateFWStartLevel(-1, originalInitialBundleStartLevel, Status.SERVER_ERROR_INTERNAL.getCode(), null, null);

    assertBundleException(result, "Update framewprk start level with negative value.");
  }

  //5.1.2.1
  public void testBundleList() throws JSONException, IOException, BundleException {
    JSONArray jsonBundleList = getJSONArray(BUNDLE_LIST_URI, BUNDLE_LIST_CONTENT_TYPE_JSON, Status.SUCCESS_OK);

    Bundle[] bundles = getInstalledBundles();
    assertBundleListRepresentation(bundles, jsonBundleList);

    jsonBundleList = getJSONArray(getBundleListURI(getFilter(TEST_BUNDLE_SYMBOLIC_NAME)), BUNDLE_LIST_CONTENT_TYPE_JSON, Status.SUCCESS_OK);
    assertBundleListRepresentation(new Bundle[]{getBundle(TEST_BUNDLE_SYMBOLIC_NAME)}, jsonBundleList);

    Object object = getNonSupportedMediaTypeObject(BUNDLE_LIST_URI, BUNDLE_LIST_CONTENT_TYPE_JSON, Status.CLIENT_ERROR_NOT_ACCEPTABLE);
    assertNull("Request with not supported media type " + NON_SUPPORTED_MEDIA_TYPE, object);

    Bundle tb1Bundle = getBundle(TB1_TEST_BUNDLE_SYMBOLIC_NAME);
    if (tb1Bundle != null) { // test bundle is already installed => uninstall
      tb1Bundle.uninstall();
    }
    URL url = getContext().getBundle().getEntry(TB1);

    // install bundle with location
    Object result = installBundle(BUNDLE_LIST_URI, url, null, null, true, Status.SUCCESS_OK.getCode());
    assertNotNull("Install bundle by URI:", result);
    if (result instanceof String) {
      // TODO
    } else {
      fail("Location string expected");
    }

    // same bundle location
    result = installBundle(BUNDLE_LIST_URI, url, null, null, true, Status.CLIENT_ERROR_CONFLICT.getCode());
    assertNull("Install bundle by same URI:", result);

    result = installBundle(BUNDLE_LIST_URI, null, "invalid bundle location", null, true, Status.SERVER_ERROR_INTERNAL.getCode());
    assertBundleException(result, "Install bundle by invalid URI.");

    // install bundle with bundle content
    Bundle tb2Bundle = getBundle(TB2_TEST_BUNDLE_SYMBOLIC_NAME);
    if (tb2Bundle != null) { // test bundle is already installed => uninstall
      tb2Bundle.uninstall();
    }

    url = getContext().getBundle().getEntry(TB2);
    String locationHeader = "/tb2.rest.test.location";

    result = installBundle(BUNDLE_LIST_URI, url, null, locationHeader, false, Status.SUCCESS_OK.getCode());
    assertNotNull("Install bundle by bundle content:", result);
    if (result instanceof String) {
      // TODO
    } else {
      fail("Location string expected");
    }

    // same bundle location
    result = installBundle(BUNDLE_LIST_URI, url, null, locationHeader, false, Status.CLIENT_ERROR_CONFLICT.getCode());
    assertNull("Install bundle by same bundle content:", result);

    result = installBundle(BUNDLE_LIST_URI, null, "invalid bundle location", null, false, Status.SERVER_ERROR_INTERNAL.getCode());

    assertBundleException(result, "Install bundle by invalid bundle content.");
  }

  // 5.1.2.2
  public void testBundleRepresentationsList() throws JSONException, IOException {
    JSONArray jsonBundleRepresentationsList = getJSONArray(BUNDLE_REPRESENTATIONS_LIST_URI, BUNDLE_REPRESENTATIONS_LIST_CONTENT_TYPE_JSON, Status.SUCCESS_OK);
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
    jsonBundleRepresentationsList = getJSONArray(getBundleRepresentationListURI(getFilter(TEST_BUNDLE_SYMBOLIC_NAME)), BUNDLE_REPRESENTATIONS_LIST_CONTENT_TYPE_JSON, Status.SUCCESS_OK);

    Bundle testBundle = getBundle(TEST_BUNDLE_SYMBOLIC_NAME);
    assertEquals("Bundle representations list length:", 1, jsonBundleRepresentationsList.length());

    JSONObject bundleRepresentation = jsonBundleRepresentationsList.getJSONObject(0);
    assertBundleRepresentation(testBundle, bundleRepresentation);

    Object object = getNonSupportedMediaTypeObject(BUNDLE_REPRESENTATIONS_LIST_URI, BUNDLE_REPRESENTATIONS_LIST_CONTENT_TYPE_JSON, Status.CLIENT_ERROR_NOT_ACCEPTABLE);
    assertNull("Request with not supported media type " + NON_SUPPORTED_MEDIA_TYPE, object);
  }

  // 5.1.3
  public void testBundle() throws JSONException, IOException, BundleException {
    // GET
    Bundle bundle = getRondomBundle();
    JSONObject bundleRepresentation = getJSONObject(getBundleURI(bundle.getBundleId()), BUNDLE_CONTENT_TYPE_JSON, Status.SUCCESS_OK);
    assertNotNull("Bundle representation : " + bundle.getBundleId() + " :", bundleRepresentation);
    assertBundleRepresentation(bundle, bundleRepresentation);

    long notExistingBundleId = getNotExistingBundleId();
    bundleRepresentation = getJSONObject(getBundleURI(notExistingBundleId), BUNDLE_CONTENT_TYPE_JSON, Status.CLIENT_ERROR_NOT_FOUND);
    assertNull("Bundle representation for not existing bundle " + notExistingBundleId + " :", bundleRepresentation);

    Object object = getNonSupportedMediaTypeObject(getBundleURI(bundle.getBundleId()), BUNDLE_CONTENT_TYPE_JSON, Status.CLIENT_ERROR_NOT_ACCEPTABLE);
    assertNull("Request with not supported media type " + NON_SUPPORTED_MEDIA_TYPE, object);

    // PUT with location
    Bundle tb1Bundle = getTestBundle(TB1_TEST_BUNDLE_SYMBOLIC_NAME, TB1);

    Object result = updateBundle(getBundleURI(tb1Bundle.getBundleId()), getContext().getBundle().getEntry(TB11), null, true, Status.SUCCESS_NO_CONTENT.getCode());
    assertNull("Update bundle by location " + tb1Bundle.getBundleId() + " :", result);

    result = updateBundle(getBundleURI(notExistingBundleId), getContext().getBundle().getEntry(TB11), null, true, Status.CLIENT_ERROR_NOT_FOUND.getCode());
    assertNull("Update not existing bundle " + notExistingBundleId + " :", result);

    result = updateBundle(getBundleURI(tb1Bundle.getBundleId()), null, "invalid bundle location", true, Status.SERVER_ERROR_INTERNAL.getCode());

    assertBundleException(result, "Update bundle with invalid location " + tb1Bundle.getBundleId());

    // PUT with bundle content
    Bundle tb2Bundle = getTestBundle(TB2_TEST_BUNDLE_SYMBOLIC_NAME, TB2);

    result = updateBundle(getBundleURI(tb2Bundle.getBundleId()), getContext().getBundle().getEntry(TB21), null, false, Status.SUCCESS_NO_CONTENT.getCode());
    assertNull("Update bundle by content " + tb2Bundle.getBundleId() + " :", result);

    result = updateBundle(getBundleURI(notExistingBundleId), getContext().getBundle().getEntry(TB21), null, false, Status.CLIENT_ERROR_NOT_FOUND.getCode());
    assertNull("Update not existing bundle " + notExistingBundleId + " :", result);

    result = updateBundle(getBundleURI(tb2Bundle.getBundleId()), null, "invalid bundle location", true, Status.SERVER_ERROR_INTERNAL.getCode());

    assertBundleException(result, "Update bundle with invalid content " + tb2Bundle.getBundleId());

    // DELETE
    result = uninstallBundle(getBundleURI(tb1Bundle.getBundleId()), Status.SUCCESS_NO_CONTENT.getCode());
    assertNull("Uninstall bundle " + tb1Bundle.getBundleId() + " :", result);
    assertNull("Framework bundle " + tb1Bundle.getBundleId() + " :", getContext().getBundle(tb1Bundle.getBundleId()));

    result = uninstallBundle(getBundleURI(tb1Bundle.getBundleId()), Status.CLIENT_ERROR_NOT_FOUND.getCode());
    assertNull("Uninstall already uninstalled bundle " + tb1Bundle.getBundleId() + " :", result);

    // stop tb2 bundle throws Exception
    tb2Bundle.start();
    result = uninstallBundle(getBundleURI(tb2Bundle.getBundleId()), Status.SERVER_ERROR_INTERNAL.getCode());
    assertBundleException(result, "Uninstall bundle with error in stop method " + tb2Bundle.getBundleId());
  }

  // 5.1.4
  public void testBundleState() throws JSONException, IOException, BundleException {
    // GET
    Bundle bundle = getRondomBundle();
    JSONObject bundleStateRepresentation = getJSONObject(getBundleStateURI(bundle.getBundleId()), BUNDLE_STATE_CONTENT_TYPE_JSON, Status.SUCCESS_OK);
    assertNotNull("Bundle state " + bundle.getBundleId() + " :", bundleStateRepresentation);
    assertBundleStateRepresentation(bundle, bundleStateRepresentation);

    long notExistingBundleId = getNotExistingBundleId();
    bundleStateRepresentation = getJSONObject(getBundleStateURI(notExistingBundleId), BUNDLE_STATE_CONTENT_TYPE_JSON, Status.CLIENT_ERROR_NOT_FOUND);
    assertNull("Bundle representation for not existing bundle " + notExistingBundleId + " :", bundleStateRepresentation);

    Object result = getNonSupportedMediaTypeObject(getBundleStateURI(bundle.getBundleId()), BUNDLE_STATE_CONTENT_TYPE_JSON, Status.CLIENT_ERROR_NOT_ACCEPTABLE);
    assertNull("Request with not supported media type " + NON_SUPPORTED_MEDIA_TYPE, result);

    // PUT
    Bundle tb1Bundle = getTestBundle(TB1_TEST_BUNDLE_SYMBOLIC_NAME, TB1);

    int tb1State = tb1Bundle.getState();
    int newState = tb1State == Bundle.INSTALLED ? Bundle.ACTIVE : Bundle.RESOLVED;
    bundleStateRepresentation = updateBundleState(getBundleStateURI(tb1Bundle.getBundleId()), newState, -1, Status.SUCCESS_OK.getCode(), null, MediaType.APPLICATION_JSON);
    assertNotNull("Bundle state updated  " + tb1Bundle.getBundleId() + " :", bundleStateRepresentation);

    tb1Bundle = getContext().getBundle(TB1_TEST_BUNDLE_SYMBOLIC_NAME);  // is it needed?

    assertEquals("New state ", newState, tb1Bundle.getState());
    assertBundleStateRepresentation(tb1Bundle, bundleStateRepresentation);

    // start bundle with Bundle-ActivationPolicy by bundle id
    Bundle tb3Bundle = getTestBundle(TB3_TEST_BUNDLE_SYMBOLIC_NAME, TB3);
    int tb3State = tb3Bundle.getState();
    if (tb3State == Bundle.ACTIVE || tb3State == Bundle.STARTING) {
      tb3Bundle.stop();
    }

    bundleStateRepresentation = updateBundleState(getBundleStateURI(tb3Bundle.getBundleId()), Bundle.ACTIVE, Bundle.START_ACTIVATION_POLICY, Status.SUCCESS_OK.getCode(), null, MediaType.APPLICATION_JSON);
    assertNotNull("Bundle state updated  " + tb3Bundle.getBundleId() + " :", bundleStateRepresentation);

    assertEquals("New state for 'lazy' bundle ", Bundle.STARTING, tb3Bundle.getState());
    assertBundleStateRepresentation(tb3Bundle, bundleStateRepresentation);

    bundleStateRepresentation = updateBundleState(getBundleStateURI(notExistingBundleId), newState, -1, Status.CLIENT_ERROR_NOT_FOUND.getCode(), null, MediaType.APPLICATION_JSON);
    assertNull("Bundle state updated for not existing bundle " + notExistingBundleId + " :", bundleStateRepresentation);

    bundleStateRepresentation = updateBundleState(getBundleStateURI(tb1Bundle.getBundleId()), newState, -1, Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE.getCode(), null, NON_SUPPORTED_MEDIA_TYPE);
    assertNull("Bundle state updated with not supportable media type " + NON_SUPPORTED_MEDIA_TYPE + " :", bundleStateRepresentation);

    bundleStateRepresentation = updateBundleState(getBundleStateURI(tb1Bundle.getBundleId()), newState, -1, Status.CLIENT_ERROR_NOT_ACCEPTABLE.getCode(), NON_SUPPORTED_MEDIA_TYPE, MediaType.APPLICATION_JSON);
    assertNull("Bundle state updated for not acceptable media type " + NON_SUPPORTED_MEDIA_TYPE + " :", bundleStateRepresentation);

    Bundle tb21Bundle = getTestBundle(TB21_TEST_BUNDLE_SYMBOLIC_NAME, TB21);
    if (tb21Bundle.getState() != Bundle.ACTIVE) {
      tb21Bundle.start();
    }

    result = updateBundleState(getBundleStateURI(tb21Bundle.getBundleId()), Bundle.RESOLVED, -1, Status.SERVER_ERROR_INTERNAL.getCode(), null, MediaType.APPLICATION_JSON);
    assertBundleException(result, "Stop bundle for bundle with  error in stop method  " + tb21Bundle.getBundleId());

    // stop bundle with options
    bundleStateRepresentation = updateBundleState(getBundleStateURI(tb3Bundle.getBundleId()), Bundle.RESOLVED, Bundle.STOP_TRANSIENT, Status.SUCCESS_OK.getCode(), null, MediaType.APPLICATION_JSON);
    assertNotNull("Bundle state updated  " + tb3Bundle.getBundleId() + " :", bundleStateRepresentation);

    assertEquals("New state ", Bundle.RESOLVED, tb3Bundle.getState());
    assertBundleStateRepresentation(tb3Bundle, bundleStateRepresentation);
  }

  // 5.1.5
  public void testBundleHeader() throws JSONException, IOException {
    // GET
    Bundle bundle = getRondomBundle();
    JSONObject bundleHeaderRepresentation = getJSONObject(getBundleHeaderURI(bundle.getBundleId()), BUNDLE_HEADER_CONTENT_TYPE_JSON, Status.SUCCESS_OK);
    assertNotNull("Bundle header " + bundle.getBundleId() + ": ", bundleHeaderRepresentation);
    assertBundleHeaderRepresentation(bundle, bundleHeaderRepresentation);

    long notExistingBundleId = getNotExistingBundleId();
    bundleHeaderRepresentation = getJSONObject(getBundleHeaderURI(notExistingBundleId), BUNDLE_HEADER_CONTENT_TYPE_JSON, Status.CLIENT_ERROR_NOT_FOUND);
    assertNull("Bundle header representation for not existing bundle " + notExistingBundleId + " :", bundleHeaderRepresentation);

    Object result = getNonSupportedMediaTypeObject(getBundleHeaderURI(bundle.getBundleId()), BUNDLE_HEADER_CONTENT_TYPE_JSON, Status.CLIENT_ERROR_NOT_ACCEPTABLE);
    assertNull("Request with not acceptable media type " + NON_SUPPORTED_MEDIA_TYPE, result);
  }

  // 5.1.6
  public void testBundleStartLevel() throws JSONException, IOException, BundleException {
    // GET
    Bundle bundle = getRondomBundle();
    JSONObject bundleStartLevelRepresentation = getJSONObject(getBundleStartLevelURI(bundle.getBundleId()), BUNDLE_START_LEVEL_CONTENT_TYPE_JSON, Status.SUCCESS_OK);
    assertNotNull("Bundle start level " + bundle.getBundleId() + " :", bundleStartLevelRepresentation);
    assertBundleStartLevelRepresentation(bundle, bundleStartLevelRepresentation);

    long notExistingBundleId = getNotExistingBundleId();
    bundleStartLevelRepresentation = getJSONObject(getBundleStartLevelURI(notExistingBundleId), BUNDLE_START_LEVEL_CONTENT_TYPE_JSON, Status.CLIENT_ERROR_NOT_FOUND);
    assertNull("Bundle start level representation for not existing bundle " + notExistingBundleId + " :", bundleStartLevelRepresentation);

    Object result = getNonSupportedMediaTypeObject(getBundleStartLevelURI(bundle.getBundleId()), BUNDLE_START_LEVEL_CONTENT_TYPE_JSON, Status.CLIENT_ERROR_NOT_ACCEPTABLE);
    assertNull("Request with not acceptable media type " + NON_SUPPORTED_MEDIA_TYPE, result);

    // PUT
    Bundle tb1Bundle = getTestBundle(TB1_TEST_BUNDLE_SYMBOLIC_NAME, TB1);

    int tb1StartLevel = getBundleStartLevel(tb1Bundle).getStartLevel();
    int newStartLevel = tb1StartLevel + 1;
    bundleStartLevelRepresentation = updateBundleStartLevel(getBundleStartLevelURI(tb1Bundle.getBundleId()), newStartLevel, Status.SUCCESS_NO_CONTENT.getCode(), null, MediaType.APPLICATION_JSON);
    assertNotNull("Bundle start level updated  " + tb1Bundle.getBundleId() + " :", bundleStartLevelRepresentation);

    tb1StartLevel = getBundleStartLevel(tb1Bundle).getStartLevel();

    assertEquals("New start level ", newStartLevel, tb1StartLevel);
    assertBundleStartLevelRepresentation(tb1Bundle, bundleStartLevelRepresentation);

    bundleStartLevelRepresentation = updateBundleStartLevel(getBundleStartLevelURI(notExistingBundleId), newStartLevel, Status.CLIENT_ERROR_NOT_FOUND.getCode(), null, MediaType.APPLICATION_JSON);
    assertNull("Bundle start level updated for not existing bundle " + notExistingBundleId + " :", bundleStartLevelRepresentation);

    bundleStartLevelRepresentation = updateBundleStartLevel(getBundleStartLevelURI(tb1Bundle.getBundleId()), newStartLevel, Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE.getCode(), null, NON_SUPPORTED_MEDIA_TYPE);
    assertNull("Bundle start level updated with not supportable media type " + NON_SUPPORTED_MEDIA_TYPE + " :", bundleStartLevelRepresentation);

    bundleStartLevelRepresentation = updateBundleStartLevel(getBundleStartLevelURI(tb1Bundle.getBundleId()), newStartLevel, Status.CLIENT_ERROR_NOT_ACCEPTABLE.getCode(), NON_SUPPORTED_MEDIA_TYPE, MediaType.APPLICATION_JSON);
    assertNull("Bundle start level updated for not acceptable media type " + NON_SUPPORTED_MEDIA_TYPE + " :", bundleStartLevelRepresentation);

    result = updateBundleStartLevel(getBundleStartLevelURI(tb1Bundle.getBundleId()), -1, Status.SERVER_ERROR_INTERNAL.getCode(), null, MediaType.APPLICATION_JSON);
    assertBundleException(result, "Bundle start level updated with negative value " + tb1Bundle.getBundleId());
  }

  // 5.1.7.1
  public void testServiceList() throws JSONException, IOException, InvalidSyntaxException {
    String filter = null;
    ServiceReference<?>[] serviceRefs = getServices(filter);

    JSONArray jsonServiceList = getJSONArray(getServiceListURI(filter), SERVICE_LIST_CONTENT_TYPE_JSON, Status.SUCCESS_OK);
    assertServiceList(jsonServiceList, serviceRefs);

    filter = "(&(" + Constants.SERVICE_ID + ">=" + serviceRefs.length / 2 + ")"
        + "(" + Constants.SERVICE_ID + "<=" + serviceRefs.length + "))";

    serviceRefs = getServices(filter);
    jsonServiceList = getJSONArray(getServiceListURI(filter), SERVICE_LIST_CONTENT_TYPE_JSON, Status.SUCCESS_OK);
    assertServiceList(jsonServiceList, serviceRefs);

    String invalidFilterURI = SERVICE_LIST_URI + "?filter=invalid-filter";
    Object object = getNonSupportedMediaTypeObject(invalidFilterURI, SERVICE_LIST_CONTENT_TYPE_JSON, Status.CLIENT_ERROR_BAD_REQUEST);
    assertNull("Request with invalid filter " + invalidFilterURI, object);

    object = getNonSupportedMediaTypeObject(SERVICE_LIST_URI, SERVICE_LIST_CONTENT_TYPE_JSON, Status.CLIENT_ERROR_NOT_ACCEPTABLE);
    assertNull("Request with not supported media type " + NON_SUPPORTED_MEDIA_TYPE, object);
  }

  // 5.1.7.2
  public void testServiceRepresentationsList() throws JSONException, IOException, InvalidSyntaxException {
    String filter = null;
    ServiceReference<?>[] serviceRefs = getServices(filter);
    JSONArray jsonServiceRepresentationsList = getJSONArray(getServiceRepresentationListURI(filter), SERVICE_REPRESENTATIONS_LIST_CONTENT_TYPE_JSON, Status.SUCCESS_OK);
    assertNotNull("Service representations list", jsonServiceRepresentationsList);

    assertServiceRepresentationList(jsonServiceRepresentationsList, serviceRefs);

    filter = "(&(" + Constants.SERVICE_ID + ">=" + serviceRefs.length / 2 + ")"
        + "(" + Constants.SERVICE_ID + "<=" + serviceRefs.length + "))";

    serviceRefs = getServices(filter);
    jsonServiceRepresentationsList = getJSONArray(getServiceRepresentationListURI(filter), SERVICE_REPRESENTATIONS_LIST_CONTENT_TYPE_JSON, Status.SUCCESS_OK);
    assertServiceRepresentationList(jsonServiceRepresentationsList, serviceRefs);

    filter = "invalid-filter";
    Object object = getNonSupportedMediaTypeObject(getServiceRepresentationListURI(filter), SERVICE_REPRESENTATIONS_LIST_CONTENT_TYPE_JSON, Status.CLIENT_ERROR_BAD_REQUEST);
    assertNull("Request with invalid filter '" + filter + "'", object);

    object = getNonSupportedMediaTypeObject(getServiceRepresentationListURI(null), SERVICE_REPRESENTATIONS_LIST_CONTENT_TYPE_JSON, Status.CLIENT_ERROR_NOT_ACCEPTABLE);
    assertNull("Request with not supported media type " + NON_SUPPORTED_MEDIA_TYPE, object);
  }

  // 5.1.8
  public void testService() throws InvalidSyntaxException, JSONException, IOException {
    ServiceReference<?> serviceRef = getRondomService();

    // GET
    JSONObject serviceRepresentation = getJSONObject(getServiceURI(serviceRef), SERVICE_CONTENT_TYPE_JSON, Status.SUCCESS_OK);
    assertNotNull("Service representation " + serviceRef + " :", serviceRepresentation);
    assertService(serviceRepresentation, serviceRef);

    String notExistingSeriveId = "not-existing-servicve-" + System.currentTimeMillis();
    serviceRepresentation = getJSONObject(getServiceURI(notExistingSeriveId), SERVICE_CONTENT_TYPE_JSON, Status.CLIENT_ERROR_NOT_FOUND);
    assertNull("Service representation for not existing service " + notExistingSeriveId + " :", serviceRepresentation);

    Object result = getNonSupportedMediaTypeObject(getServiceURI(serviceRef), SERVICE_CONTENT_TYPE_JSON, Status.CLIENT_ERROR_NOT_ACCEPTABLE);
    assertNull("Request with not acceptable media type " + NON_SUPPORTED_MEDIA_TYPE, result);
  }


// protected

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
      long bundleId = bundle.getBundleId();
      assertTrue("Bundle list contains " + bundleId + " :", bundleURIs.contains(getBundleURI(bundleId)));
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
    assertEquals("activationPolicyUsed:", bundleStartLevel.isActivationPolicyUsed(), bundleStartLevelRepresentation.getBoolean("activationPolicyUsed"));
    assertEquals("persistentlyStarted:", bundleStartLevel.isPersistentlyStarted(), bundleStartLevelRepresentation.getBoolean("persistentlyStarted"));
  }

  protected void assertServiceList(JSONArray jsonServiceList, ServiceReference<?>[] serviceRefs) throws JSONException {
    assertNotNull("Service list", jsonServiceList);

    assertEquals("Service list length:", serviceRefs.length, jsonServiceList.length());

    HashSet<String> serviceURIs = new HashSet<String>();
    for (int k = 0; k < jsonServiceList.length(); k++) {
      serviceURIs.add(jsonServiceList.getString(k));
    }

    for (ServiceReference<?> serviceRef : serviceRefs) {
      String serviceId = serviceRef.getProperty(Constants.SERVICE_ID).toString();
      assertTrue("Service list contains " + serviceId + " :", serviceURIs.contains(getServiceURI(serviceId)));
    }
  }

  protected void assertServiceRepresentationList(JSONArray jsonServiceRepresentationList, ServiceReference<?>[] serviceRefs) throws JSONException {
    if (serviceRefs != null) {
      assertNotNull("Service representation list", jsonServiceRepresentationList);

      Hashtable<Object, JSONObject> serviceRepresentationHT = new Hashtable<Object, JSONObject>();

      for (int k = 0; k < jsonServiceRepresentationList.length(); k++) {
        JSONObject serviceRepresentation = jsonServiceRepresentationList.getJSONObject(k);
        try {
          Object serviceId = serviceRepresentation.getJSONObject("properties");
          serviceRepresentationHT.put(serviceId, serviceRepresentation);
        } catch (Exception cause) {
          fail(cause.getMessage());
        }
      }

      assertEquals("Service representation list size", serviceRefs.length, serviceRepresentationHT.size());
      for (ServiceReference<?> serviceRef : serviceRefs) {
        assertService(serviceRepresentationHT.get(serviceRef.getProperty(Constants.SERVICE_ID)), serviceRef);
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
        assertEquals("Service property value ", value, propsRepresentation.get(key));
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
      assertNull("usingBundles is null", serviceRepresentation.getJSONArray("usingBundles"));
    }
  }

  protected void assertBundleException(Object result, String assertMessage) throws JSONException  {
    assertNotNull(assertMessage, result);

    if (result instanceof JSONObject) {	
      // TODO
      int typeCode = ((JSONObject)result).getInt("typecode");
      String message = ((JSONObject)result).getString("message");

      assertTrue("typecode:" + typeCode, true);  // print value?
      assertTrue("message:" + message, true);  // print values?
    } else {
      fail("BundleException Representation expected.");
    }
  }

  protected Object updateFWStartLevel(int startLevel, int initialBundleStartLevel, int expectedStatusCode, MediaType acceptMediaType, MediaType contentMediaType) throws JSONException {
    ClientResource resource = new ClientResource(baseURI + FW_START_LEVEL_URI);
    JSONWriter jsonWriter = new JSONStringer().object();
    jsonWriter.key("startLevel").value(startLevel);
    jsonWriter.key("initialBundleStartLevel").value(initialBundleStartLevel);

    if (acceptMediaType != null) {
      resource.getRequestAttributes().put("Accept", acceptMediaType.toString());
    }
    if (contentMediaType != null) {
      resource.getRequestAttributes().put("Content-Type", contentMediaType.toString());
    }

    resource.put(jsonWriter.endObject().toString());

    assertEquals("Expected response status " + expectedStatusCode + ":", expectedStatusCode, resource.getStatus().getCode());

    if (resource.getStatus().getCode() == Status.SERVER_ERROR_INTERNAL.getCode()) { // BundleException
      return new JSONObject(resource.getResponseEntity());
    }

    return null;
  }

  protected Object installBundle(String requestURI, URL url, String invalidLocation, String locationHeader, boolean byLocation, int expectedStatusCode) throws ResourceException, IOException {
    ClientResource resource = new ClientResource(baseURI + requestURI);
    if (byLocation) {
      resource.post(invalidLocation == null ? url.toString() : invalidLocation, MediaType.TEXT_PLAIN);
    } else {
      // HTTP Content-Location
      resource.getRequestAttributes().put("Content-Location", locationHeader);
      resource.post(invalidLocation == null ? url.openStream() : invalidLocation.getBytes(), new MediaType("vnd.osgi.bundle"));
    }

    assertEquals("Expected response status " + expectedStatusCode + ":", expectedStatusCode, resource.getStatus().getCode());

    if (resource.getStatus().isSuccess()) {
      Representation representation = resource.getResponseEntity();
      if (representation instanceof StringRepresentation) {
        return ((StringRepresentation)representation).getText();
      }
    } else if (resource.getStatus().getCode() == Status.SERVER_ERROR_INTERNAL.getCode()) { // BundleException
      return new JSONObject(resource.getResponseEntity());
    }

    return null;
  }

  protected Object updateBundle(String requestURI, URL url, String invalidLocation, boolean byLocation, int expectedStatusCode) throws ResourceException, IOException {
    ClientResource resource = new ClientResource(baseURI + requestURI);
    if (byLocation) {
      resource.put(invalidLocation == null ? url.toString() : invalidLocation, MediaType.TEXT_PLAIN);
    } else {
      // HTTP Content-Location
      resource.put(invalidLocation == null ? url.openStream() : invalidLocation.getBytes(), new MediaType("vnd.osgi.bundle"));
    }

    assertEquals("Expected response status " + expectedStatusCode + ":", expectedStatusCode, resource.getStatus().getCode());

    if (resource.getStatus().getCode() == Status.SERVER_ERROR_INTERNAL.getCode()) { // BundleException
      return new JSONObject(resource.getResponseEntity());
    }

    return null;
  }

  protected Object uninstallBundle(String requestURI, int expectedStatusCode) throws ResourceException, IOException {
    ClientResource resource = new ClientResource(baseURI + requestURI);
    resource.delete();

    assertEquals("Expected response status " + expectedStatusCode + ":", expectedStatusCode, resource.getStatus().getCode());

    if (resource.getStatus().getCode() == Status.SERVER_ERROR_INTERNAL.getCode()) { // BundleException
      return new JSONObject(resource.getResponseEntity());
    }

    return null;
  }

  protected JSONObject updateBundleState(String requestURI, int newState, int options, int expectedStatusCode, MediaType acceptMediaType, MediaType contentMediaType) throws ResourceException, IOException, JSONException {
    ClientResource resource = new ClientResource(baseURI + requestURI);
    JSONWriter jsonWriter = new JSONStringer().object();
    jsonWriter.key("state").value(newState);
    if (options != -1) {
      jsonWriter.key("options").value(options);
    }

    if (acceptMediaType != null) {
      resource.getRequestAttributes().put("Accept", acceptMediaType.toString());
    }
    if (contentMediaType != null) {
      resource.getRequestAttributes().put("Content-Type", contentMediaType.toString());
    }

    resource.put(jsonWriter.endObject().toString());

    assertEquals("Expected response status " + expectedStatusCode + ":", expectedStatusCode, resource.getStatus().getCode());

    if (resource.getStatus().isSuccess()) {  // Bundle state representation
      return new JSONObject(resource.getResponseEntity());
    } else if (resource.getStatus().getCode() == Status.SERVER_ERROR_INTERNAL.getCode()) { // BundleException
      return new JSONObject(resource.getResponseEntity());
    }

    return null;
  }

  protected JSONObject updateBundleStartLevel(String requestURI, int newStartLevel, int expectedStatusCode, MediaType acceptMediaType, MediaType contentMediaType) throws ResourceException, IOException, JSONException {
    ClientResource resource = new ClientResource(baseURI + requestURI);
    JSONWriter jsonWriter = new JSONStringer().object();
    jsonWriter.key("startLevel").value(newStartLevel);

    if (acceptMediaType != null) {
      resource.getRequestAttributes().put("Accept", acceptMediaType.toString());
    }
    if (contentMediaType != null) {
      resource.getRequestAttributes().put("Content-Type", contentMediaType.toString());
    }

    resource.put(jsonWriter.endObject().toString());

    assertEquals("Expected response status " + expectedStatusCode + ":", expectedStatusCode, resource.getStatus().getCode());

    if (resource.getStatus().getCode() == Status.SERVER_ERROR_INTERNAL.getCode()) { // BundleException
      return new JSONObject(resource.getResponseEntity());
    }

    return null;
  }

  protected JSONObject getJSONObject(String uri, String expectedContentType, Status expectedStatus) throws JSONException, IOException {
    return (JSONObject)getObject(uri, MediaType.APPLICATION_JSON, expectedContentType, expectedStatus, false);
  }

  protected JSONArray getJSONArray(String uri, String expectedContentType, Status expectedStatus) throws JSONException, IOException {
    return (JSONArray)getObject(uri, MediaType.APPLICATION_JSON, expectedContentType, expectedStatus, true);
  }

  protected Object getNonSupportedMediaTypeObject(String uri, String expectedContentType, Status expectedStatus) throws JSONException, IOException {
    return getObject(uri, NON_SUPPORTED_MEDIA_TYPE, expectedContentType, expectedStatus, false);
  }

  protected Object getObject(String uri, MediaType mediaType, String expectedContentType, Status expectedStatus, boolean isArray) throws JSONException, IOException {
    ClientResource resource = new ClientResource(baseURI + uri);
    if (authentication != null) {
      resource.setChallengeResponse(authentication);
    }

    if (mediaType == null) {
      resource.get(MediaType.ALL);
    } else {
      resource.get(mediaType);
    }

    if (!resource.getStatus().equals(expectedStatus)) {
      fail("An unexpected status was returned: " + resource.getStatus() + ". Expected " + expectedStatus);
    }

    if (resource.getStatus().isSuccess()) {
      String contentType = (String)resource.getResponseAttributes().get("Content-Type");
      if (expectedContentType.equals(contentType)) {
        if (expectedContentType.endsWith("+json")) {
          if (isArray) {
            return new JSONArray(resource.getResponseEntity());
          } else {
            return new JSONObject(resource.getResponseEntity());
          }
        } else {
          // TODO
        }
      } else {
        fail("An unexpected Content-Type: " + contentType);
      }
    }

    return null;
  }

}

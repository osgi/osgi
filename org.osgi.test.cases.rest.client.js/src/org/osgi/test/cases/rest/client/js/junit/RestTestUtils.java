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
package org.osgi.test.cases.rest.client.js.junit;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.test.support.OSGiTestCase;

public abstract class RestTestUtils extends OSGiTestCase {

  protected Random random;

  public static String APPLICATION_JSON = "application/json";
  public static String APPLICATION_XML = "application/xml";

  public static String FW_START_LEVEL_URI = "framework/startlevel";
  public static String FW_START_LEVEL_CONTENT_TYPE_JSON = "application/org.osgi.frameworkstartlevel+json";
  public static String FW_START_LEVEL_CONTENT_TYPE_XML = "application/org.osgi.frameworkstartlevel+xml";

  public static String BUNDLE_LIST_URI = "framework/bundles";
  public static String BUNDLE_LIST_CONTENT_TYPE_JSON = "application/org.osgi.bundles+json";
  public static String BUNDLE_LIST_CONTENT_TYPE_XML = "application/org.osgi.bundles+xml";

  public static String BUNDLE_REPRESENTATIONS_LIST_URI = "framework/bundles/representations";
  public static String BUNDLE_REPRESENTATIONS_LIST_CONTENT_TYPE_JSON = "application/org.osgi.bundles.representations+json";
  public static String BUNDLE_REPRESENTATIONS_LIST_CONTENT_TYPE_XML = "application/org.osgi.bundles.representations+xml";

  public static String BUNDLE_URI = "framework/bundle/";
  public static String BUNDLE_CONTENT_TYPE_JSON = "application/org.osgi.bundle+json";
  public static String BUNDLE_CONTENT_TYPE_XML = "application/org.osgi.bundle+xml";

  public static String BUNDLE_STATE_CONTENT_TYPE_JSON = "application/org.osgi.bundlestate+json";
  public static String BUNDLE_STATE_CONTENT_TYPE_XML = "application/org.osgi.bundlestate+xml";

  public static String BUNDLE_START_LEVEL_CONTENT_TYPE_JSON = "application/org.osgi.bundlestartlevel+json";
  public static String BUNDLE_START_LEVEL_CONTENT_TYPE_XML = "application/org.osgi.bundlestartlevel+xml";

  public static String BUNDLE_HEADER_CONTENT_TYPE_JSON = "application/org.osgi.bundleheader+json";
  public static String BUNDLE_HEADER_CONTENT_TYPE_XML = "application/org.osgi.bundleheader+xml";

  public static String SERVICE_CONTENT_TYPE_JSON = "application/org.osgi.service+json";
  public static String SERVICE_CONTENT_TYPE_XML = "application/org.osgi.service+xml";

  public static String SERVICE_LIST_URI = "framework/services";
  public static String SERVICE_LIST_CONTENT_TYPE_JSON = "application/org.osgi.services+json";
  public static String SERVICE_LIST_CONTENT_TYPE_XML = "application/org.osgi.services+xml";

  public static String SERVICE_REPRESENTATIONS_LIST_CONTENT_TYPE_JSON = "application/org.osgi.services.representations+json";
  public static String SERVICE_REPRESENTATIONS_LIST_CONTENT_TYPE_XML = "application/org.osgi.services.representations+xml";

  public static String EXTENSIONS_CONTENT_TYPE_JSON = "application/org.osgi.extensions+json";
  public static String EXTENSIONS_CONTENT_TYPE_XML = "application/org.osgi.extensions+xml";

  public static String BUNDLE_EXCEPTION_CONTENT_TYPE_JSON = "application/org.osgi.bundleexception+json";
  public static String BUNDLE_EXCEPTION_CONTENT_TYPE_XML = "application/org.osgi.bundleexception+xml";

  public static String NON_SUPPORTED_MEDIA_TYPE = "application/vnd.oasis.opendocument.chart";

  public static String EXTENSIONS_URI = "extensions";

  protected String baseURI;
  protected String user;
  protected String pass;
  protected boolean debugOn;
  protected boolean notAcceptableCheck;
  protected boolean validateXMLRepresentations;

  public static String TEST_BUNDLE_SYMBOLIC_NAME = "org.osgi.test.cases.rest.client.js";
  public static String TB1_TEST_BUNDLE_SYMBOLIC_NAME = TEST_BUNDLE_SYMBOLIC_NAME + ".tb1";
  public static String TB11_TEST_BUNDLE_SYMBOLIC_NAME = TEST_BUNDLE_SYMBOLIC_NAME + ".tb1";
  public static String TB2_TEST_BUNDLE_SYMBOLIC_NAME = TEST_BUNDLE_SYMBOLIC_NAME + ".tb2";

  public static String TB11_TEST_BUNDLE_VERSION = "1.0.1";

  public static String TB1 = "/tb1.jar";
  public static String TB11 = "/tb11.jar";
  public static String TB2 = "/tb2.jar";

  @Override
	protected void setUp() throws Exception {
    super.setUp();

    baseURI = getProperty("rest.ct.base.uri", "http://localhost:8888/");
    debugOn = getBooleanProperty("rest.ct.debug", true);
    notAcceptableCheck = getBooleanProperty("rest.ct.not_acceptable.check", false);
    validateXMLRepresentations = getBooleanProperty("rest.ct.validate.xmls", false);

    String mediaType = getProperty("rest.ct.non.supported.media.type");
    if (mediaType != null) {
      NON_SUPPORTED_MEDIA_TYPE = mediaType;
    }

    random = new Random(System.nanoTime());
  }

  @Override
	protected void tearDown() throws Exception {
    super.tearDown();
    //ungetAllServices();

    unisntallIfInstalled(TB1_TEST_BUNDLE_SYMBOLIC_NAME);
    unisntallIfInstalled(TB2_TEST_BUNDLE_SYMBOLIC_NAME);
  }

  protected String getBundleURI(long bundleId) {
    return BUNDLE_URI + bundleId;
  }

  protected String getBundleListURI(String filter) throws UnsupportedEncodingException {
    return BUNDLE_LIST_URI + (filter == null ? "" : "?osgi.identity=" + URLEncoder.encode(filter, "UTF-8"));
  }

  protected String getBundleRepresentationListURI(String filter) throws UnsupportedEncodingException {
    return BUNDLE_REPRESENTATIONS_LIST_URI + (filter == null ? "" : "?" + URLEncoder.encode(filter, "UTF-8"));
  }

  protected String getBundleStateURI(long bundleId) {
    return BUNDLE_URI + bundleId + "/state";
  }

  protected String getBundleHeaderURI(long bundleId) {
    return BUNDLE_URI + bundleId + "/header";
  }

  protected String getBundleStartLevelURI(long bundleId) {
    return BUNDLE_URI + bundleId + "/startlevel";
  }

  protected String getServiceListURI(String filter) throws UnsupportedEncodingException {
    return "framework/services" + (filter == null ? "" : "?filter=" + URLEncoder.encode(filter, "UTF-8"));
  }

  protected String getServiceRepresentationListURI(String filter) throws UnsupportedEncodingException {
    return "framework/services/representations" + (filter == null ? "" : "?filter=" + URLEncoder.encode(filter, "UTF-8"));
  }

  protected String getServiceURI(String serviceId) {
    return "framework/service/" + serviceId;
  }

  protected String getServicePath(ServiceReference<?> service) {
    return getServiceURI(service);
  }

  protected long getServiceId(ServiceReference<?> service) {
    return (Long)service.getProperty(Constants.SERVICE_ID);
  }

  protected String getServiceURI(ServiceReference<?> service) {
    return getServiceURI(service.getProperty(Constants.SERVICE_ID).toString());
  }

  protected String getBundleURI(Bundle bundle) {
    if (bundle == null) {
      return null;
    }

    return getBundleURI(bundle.getBundleId());
  }

  // for RestClient
  protected String getBundlePath(Bundle bundle) {
    return getBundleURI(bundle);
  }

  protected FrameworkStartLevel getFrameworkStartLevel() {
    return getContext().getBundle(0).adapt(FrameworkStartLevel.class);
  }

  protected BundleStartLevel getBundleStartLevel(Bundle bundle) {
    return bundle.adapt(BundleStartLevel.class);
  }

  protected Bundle[] getInstalledBundles() {
    return getContext().getBundles();
  }

  protected ServiceReference<?>[] getServices(String filter) throws InvalidSyntaxException {
    return getContext().getAllServiceReferences(null, filter);
  }

  protected Bundle getRandomBundle() {
    Bundle[] bundles = getInstalledBundles();
    int index = random.nextInt(bundles.length);
    return bundles[index];
  }

  protected ServiceReference<?> getRandomService() throws InvalidSyntaxException {
    ServiceReference<?>[] serviceRefs = getServices(null);
    int index = random.nextInt(serviceRefs.length);
    return serviceRefs[index];
  }

  protected long getNotExistingBundleId() {
    Bundle[] bundles = getInstalledBundles();
    long result = -1;
    for (Bundle bundle : bundles) {
      if (result > bundle.getBundleId()) {
        result = bundle.getBundleId();
      }
    }

    return result + 100;
  }

  protected String getNotExistingBundlePath() {
		return String.valueOf(getNotExistingBundleId());
  }

  protected String getFilter(String bundleSymbolicName) {
    //return "osgi.identity=(&(type=\"osgi.bundle\")(bundle-symbolic-name=\"" + bundleSymbolicName + "\"))";
    return "(&(type=osgi.bundle)(osgi.identity=" + bundleSymbolicName + "))";
  }

  protected Bundle getBundle(String bundleSymbolicName) {
    Bundle[] bundles = getInstalledBundles();
    for (Bundle bundle : bundles) {
      if (bundleSymbolicName.equals(bundle.getSymbolicName())) {
        return bundle;
      }
    }

    return null;
  }

  protected Bundle getTestBundle(String symbolicName, String entryName) throws BundleException, IOException {
    Bundle tbBundle = getBundle(symbolicName);
    if (tbBundle == null) {
      tbBundle = install(entryName);
    }

    return tbBundle;
  }

  protected void unisntallIfInstalled(String bundleSymbolicName) {
    try {
      Bundle bundle = getBundle(bundleSymbolicName);
      if (bundle != null) {
        bundle.uninstall();
      }
    } catch (Exception e) {
			// ignored
    }
  }

  protected void debug(String message, Throwable cause) {
    if (debugOn) {
      if (message != null) {
        System.out.println("[REST CT] " + message);
      }

      if (cause != null) {
        cause.printStackTrace();
      }
    }
  }

  protected byte[] toByteArray(InputStream is) {
    if (is == null) return null;

    try {
      byte[] res = new byte[4096];
      byte[] b;

      byte[] buffer = new byte[1024];
      int bytesRead = 0;
      int pos = 0;

      while ((bytesRead = is.read(buffer, 0, 1024)) != -1) {
        if ((pos + bytesRead) > res.length) {
          b = new byte[2 * res.length];
          System.arraycopy(res, 0, b, 0, res.length);
          res = b;
        }

        System.arraycopy(buffer, 0, res, pos, bytesRead);
        pos += bytesRead;
      }

      byte[] result = new byte[pos];
      System.arraycopy(res, 0, result, 0, pos);
      return result;
    } catch (IOException ioe) {
      throw new IllegalStateException("Error encountered while reading stream: "+ioe.getMessage());
    }
  }

  protected HttpURLConnection getHttpConnection(String url, String method, String acceptType, String contentType, Map<String, String> additionalProps) throws IOException {
    debug(method + " " + url, null);

    URL uri = new URL(url);
    HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
    connection.setRequestMethod(method); //type: POST, PUT, DELETE, GET
    connection.setDoOutput(true);
    connection.setConnectTimeout(60000); //60 secs
    connection.setReadTimeout(60000); //60 secs
    if (acceptType != null) {
      connection.setRequestProperty("Accept", acceptType);
      debug("Accept:" + acceptType, null);
    } else {
      connection.setRequestProperty("Accept", "*/*");
    }
    if (contentType != null) {
      connection.setRequestProperty("Content-Type", contentType);
      debug("Content-Type:" + contentType, null);
    }

    if (additionalProps != null) {
      for (Iterator<String> iterator = additionalProps.keySet().iterator(); iterator.hasNext();) {
        String key = iterator.next();
        connection.setRequestProperty(key, additionalProps.get(key));
      }
    }

    return connection;
  }

}

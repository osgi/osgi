/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */
package org.osgi.test.cases.rest.junit;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.test.support.OSGiTestCase;

public abstract class RestTestUtils extends OSGiTestCase implements FrameworkListener {

  protected Random random;

  public static String APPLICATION_JSON = "application/json";

  public static String FW_START_LEVEL_URI = "framework/startlevel";
  public static String FW_START_LEVEL_CONTENT_TYPE_JSON = "application/org.osgi.frameworkstartlevel+json";

  public static String BUNDLE_LIST_URI = "framework/bundles";
  public static String BUNDLE_LIST_CONTENT_TYPE_JSON = "application/org.osgi.bundles+json";

  public static String BUNDLE_REPRESENTATIONS_LIST_URI = "framework/bundles/representations";
  public static String BUNDLE_REPRESENTATIONS_LIST_CONTENT_TYPE_JSON = "application/org.osgi.bundles.representations+json";

  public static String BUNDLE_URI = "framework/bundle/";
  public static String BUNDLE_CONTENT_TYPE_JSON = "application/org.osgi.bundle+json";

  public static String BUNDLE_STATE_CONTENT_TYPE_JSON = "application/org.osgi.bundlestate+json";

  public static String BUNDLE_START_LEVEL_CONTENT_TYPE_JSON = "application/org.osgi.bundlestartlevel+json";

  public static String BUNDLE_HEADER_CONTENT_TYPE_JSON = "application/org.osgi.bundleheader+json";

  public static String SERVICE_CONTENT_TYPE_JSON = "application/org.osgi.service+json";

  public static String SERVICE_LIST_URI = "framework/services";
  public static String SERVICE_LIST_CONTENT_TYPE_JSON = "application/org.osgi.services+json";

  public static String SERVICE_REPRESENTATIONS_LIST_CONTENT_TYPE_JSON = "application/org.osgi.services.representations+json";

  public static String NON_SUPPORTED_MEDIA_TYPE = "application/vnd.oasis.opendocument.chart";

  protected String baseURI;
  protected String user;
  protected String pass;
  protected boolean debugOn;
  protected boolean notAcceptableCheck;

  public static String TEST_BUNDLE_SYMBOLIC_NAME = "org.osgi.test.cases.rest";
  public static String TB1_TEST_BUNDLE_SYMBOLIC_NAME = TEST_BUNDLE_SYMBOLIC_NAME + ".tb1";
  public static String TB11_TEST_BUNDLE_SYMBOLIC_NAME = TEST_BUNDLE_SYMBOLIC_NAME + ".tb1";
  public static String TB2_TEST_BUNDLE_SYMBOLIC_NAME = TEST_BUNDLE_SYMBOLIC_NAME + ".tb2";
  public static String TB21_TEST_BUNDLE_SYMBOLIC_NAME = TEST_BUNDLE_SYMBOLIC_NAME + ".tb2";
  public static String TB3_TEST_BUNDLE_SYMBOLIC_NAME = TEST_BUNDLE_SYMBOLIC_NAME + ".tb3";
  public static String TB4_TEST_BUNDLE_SYMBOLIC_NAME = TEST_BUNDLE_SYMBOLIC_NAME + ".tb4";

  public static String TB11_TEST_BUNDLE_VERSION = "1.0.1";
  public static String TB21_TEST_BUNDLE_VERSION = "1.0.1";

  public static String TB1 = "/tb1.jar";
  public static String TB11 = "/tb11.jar";
  public static String TB2 = "/tb2.jar";
  public static String TB21 = "/tb21.jar";
  public static String TB3 = "/tb3.jar";
  public static String TB4 = "/tb4.jar";

  @Override
  public void setUp() throws Exception {
    super.setUp();

    baseURI = getProperty("rest.ct.base.uri", "http://localhost:8888/");
    debugOn = getBooleanProperty("rest.ct.debug", true);
    notAcceptableCheck = getBooleanProperty("rest.ct.not_acceptable.check", false);

    String mediaType = getProperty("rest.ct.non.supported.media.type");
    if (mediaType != null) {
      NON_SUPPORTED_MEDIA_TYPE = mediaType;
    }

		random = new Random(System.nanoTime());
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
    //ungetAllServices();

    unisntallIfInstalled(TB1_TEST_BUNDLE_SYMBOLIC_NAME);
    unisntallIfInstalled(TB2_TEST_BUNDLE_SYMBOLIC_NAME);
    unisntallIfInstalled(TB3_TEST_BUNDLE_SYMBOLIC_NAME);
    unisntallIfInstalled(TB4_TEST_BUNDLE_SYMBOLIC_NAME);
  }

  public void frameworkEvent(FrameworkEvent event) {
    // TODO Auto-generated method stub

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
    // TODO
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
    // TODO
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
    // TODO
    return "" + getNotExistingBundleId();
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
      // TODO
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

}

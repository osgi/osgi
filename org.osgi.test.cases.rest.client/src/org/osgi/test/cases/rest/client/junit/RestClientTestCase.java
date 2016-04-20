/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */
package org.osgi.test.cases.rest.client.junit;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.dto.BundleDTO;
import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.framework.startlevel.dto.BundleStartLevelDTO;
import org.osgi.framework.startlevel.dto.FrameworkStartLevelDTO;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.resource.Capability;
import org.osgi.resource.Namespace;
import org.osgi.service.rest.client.RestClient;
import org.osgi.service.rest.client.RestClientFactory;

/**
 * Tests {@link RestClient} OSGi Service.
 *
 * @author Petia Sotirova
 */
public class RestClientTestCase extends RestTestUtils {

  private ServiceReference<RestClientFactory> restClientFactoryRef;
  private RestClient restClient;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    restClientFactoryRef = getContext().getServiceReference(RestClientFactory.class);
		assertNotNull("RestClientFactory ServiceReference is not available!", restClientFactoryRef);
    RestClientFactory restClientFactory = getContext().getService(restClientFactoryRef);
		assertNotNull("RestClientFactory service is not available!", restClientFactory);
    restClient = restClientFactory.createRestClient(new URI(baseURI));
		assertNotNull("RestClient is not available!", restClient);
  }

  @Override
  protected void tearDown() throws Exception {
    if (restClientFactoryRef != null) {
      getContext().ungetService(restClientFactoryRef);
    }
    super.tearDown();
  }

  public void testFrameworkStartLevelRestClient() throws Exception {
    FrameworkStartLevel frameworkStartLevel = getFrameworkStartLevel();
    int originalStartLevel = frameworkStartLevel.getStartLevel();
    int originalInitialBundleStartLevel = frameworkStartLevel.getInitialBundleStartLevel();

    FrameworkStartLevelDTO frameworkStartLevelDTO = getRestClient().getFrameworkStartLevel();
    assertNotNull("frameworkStartLevelDTO", frameworkStartLevelDTO);

    assertEquals("original startLevel", frameworkStartLevel.getStartLevel(), frameworkStartLevelDTO.startLevel);
    assertEquals("original initialBundleStartLevel", frameworkStartLevel.getInitialBundleStartLevel(), frameworkStartLevelDTO.initialBundleStartLevel);

    FrameworkStartLevelDTO updateFWStartLevelDTO = new FrameworkStartLevelDTO();
    updateFWStartLevelDTO.startLevel = originalStartLevel;
    updateFWStartLevelDTO.initialBundleStartLevel = originalInitialBundleStartLevel + 1;

    getRestClient().setFrameworkStartLevel(updateFWStartLevelDTO);

    frameworkStartLevel = getFrameworkStartLevel();

    assertEquals("startLevel after set", updateFWStartLevelDTO.startLevel, frameworkStartLevel.getStartLevel());
    assertEquals("initialBundleStartLevel after set", updateFWStartLevelDTO.initialBundleStartLevel, frameworkStartLevel.getInitialBundleStartLevel());

    frameworkStartLevelDTO = getRestClient().getFrameworkStartLevel();
    assertNotNull("frameworkStartLevelDTO", frameworkStartLevelDTO);
    assertEquals("startLevel", frameworkStartLevel.getStartLevel(), frameworkStartLevelDTO.startLevel);
    assertEquals("initialBundleStartLevel", frameworkStartLevel.getInitialBundleStartLevel(), frameworkStartLevelDTO.initialBundleStartLevel);

		frameworkStartLevel.setStartLevel(originalStartLevel);
    frameworkStartLevel.setInitialBundleStartLevel(originalInitialBundleStartLevel);

    frameworkStartLevelDTO = getRestClient().getFrameworkStartLevel();
    assertNotNull("frameworkStartLevelDTO", frameworkStartLevelDTO);
    assertEquals("updated startLevel", originalStartLevel, frameworkStartLevelDTO.startLevel);
    assertEquals("updated initialBundleStartLevel", frameworkStartLevel.getInitialBundleStartLevel(), frameworkStartLevelDTO.initialBundleStartLevel);

    updateFWStartLevelDTO = new FrameworkStartLevelDTO();
    updateFWStartLevelDTO.startLevel = -1;
    updateFWStartLevelDTO.initialBundleStartLevel = originalInitialBundleStartLevel;

    boolean receiveError = false;
    try {
      getRestClient().setFrameworkStartLevel(updateFWStartLevelDTO);
    } catch (Exception cause) {
      receiveError = true;
    }
    assertTrue("Error for updating framework start level with negative value ", receiveError);
  }

  public void testBundleListRestClient() throws Exception {
    Collection<String> bundleCollection = getRestClient().getBundlePaths();

    Bundle[] bundles = getInstalledBundles();
    assertBundleCollection(bundles, bundleCollection);

    Bundle tb1Bundle = getBundle(TB1_TEST_BUNDLE_SYMBOLIC_NAME);
    if (tb1Bundle != null) { // test bundle is already installed => uninstall
      tb1Bundle.uninstall();
    }
    String url = getContext().getBundle().getEntry(TB1).toString();

    // install bundle with location
    BundleDTO result = getRestClient().installBundle(url);
    assertNotNull("Bundle location for installed bundle is not null", result);

    tb1Bundle = getBundle(TB1_TEST_BUNDLE_SYMBOLIC_NAME);
    assertNotNull("Test bundle TB1 is installed", tb1Bundle);

    assertEquals("Bundle location", getBundleURI(tb1Bundle), getBundleURI(result.id));

    // same bundle location
    boolean receiveError = false;
    try {
      getRestClient().installBundle(url);
    } catch (Exception cause) {
      receiveError = true;
    }
    assertTrue("Install bundle by same URI", receiveError);

    // invalid bundle location
    receiveError = false;
    try {
      getRestClient().installBundle("invalid bundle location");
    } catch (Exception cause) {
      receiveError = true;
    }
    assertTrue("Install bundle by invalid URI", receiveError);

    // install bundle with bundle content
    Bundle tb2Bundle = getBundle(TB2_TEST_BUNDLE_SYMBOLIC_NAME);
    if (tb2Bundle != null) { // test bundle is already installed => uninstall
      tb2Bundle.uninstall();
    }

    String locationHeader = "/tb2.rest.test.location";
    InputStream in = getContext().getBundle().getEntry(TB2).openStream();
    result = getRestClient().installBundle(locationHeader, in);

    assertNotNull("Bundle location for installed bundle is not null", result);

    tb2Bundle = getBundle(TB2_TEST_BUNDLE_SYMBOLIC_NAME);
    assertNotNull("Test bundle TB2 is installed", tb2Bundle);

	assertEquals("Bundle location", getBundleURI(tb2Bundle), getBundleURI(result.id));

    // same bundle location
    receiveError = false;
    try {
      in = getContext().getBundle().getEntry(TB2).openStream();
      getRestClient().installBundle(locationHeader, in);
    } catch (Exception cause) {
      receiveError = true;
    }
    assertTrue("Install bundle by same location header", receiveError);

    // "Install bundle by invalid bundle content."
    receiveError = false;
    try {
      getRestClient().installBundle("invalid-bundle-content", new ByteArrayInputStream("invalid-bundle-content".getBytes()));
    } catch (Exception cause) {
      receiveError = true;
    }
    assertTrue("Install bundle by invalid bundle content.", receiveError);
  }

  public void testBundleRepresentationsListRestClient() throws Exception {
    Collection<BundleDTO> bundleRepresentationsList = getRestClient().getBundles();
    assertNotNull("Bundle representations list", bundleRepresentationsList);

    Bundle[] bundles = getInstalledBundles();
    assertEquals("Bundle representations list length:", bundles.length, bundleRepresentationsList.size());

    Hashtable<Long, BundleDTO> bundleRepresentations = new Hashtable<Long, BundleDTO>();
    for (Iterator<BundleDTO> i = bundleRepresentationsList.iterator(); i.hasNext();) {
      BundleDTO bundleRepresentation = i.next();
      bundleRepresentations.put(bundleRepresentation.id, bundleRepresentation);
    }

    for (Bundle bundle : bundles) {
      long bundleId = bundle.getBundleId();
      BundleDTO bundleRepresentation = bundleRepresentations.get(bundleId);
      assertNotNull("Bundle representations list contains " + bundleId + " :", bundleRepresentation);
      assertBundleRepresentation(bundle, bundleRepresentation);
    }

    // filter ?
  }

  public void testBundleRestClient() throws Exception  {
    Bundle bundle = getRandomBundle();

    // GET by bundleId
    BundleDTO bundleRepresentation = getRestClient().getBundle(bundle.getBundleId());
    assertNotNull("Bundle representation : " + bundle.getBundleId() + " :", bundleRepresentation);
    assertBundleRepresentation(bundle, bundleRepresentation);

    long notExistingBundleId = getNotExistingBundleId();
    bundleRepresentation = getRestClient().getBundle(notExistingBundleId);
    assertNull("Bundle representation for not existing bundle " + notExistingBundleId + " :", bundleRepresentation);

    // GET by bundle path
    bundleRepresentation = getRestClient().getBundle(getBundlePath(bundle));
    assertNotNull("Bundle representation : " + bundle.getBundleId() + " :", bundleRepresentation);
    assertBundleRepresentation(bundle, bundleRepresentation);

    String url = getContext().getBundle().getEntry(TB11).toString();

    // update with location
    Bundle tb1Bundle = getTestBundle(TB1_TEST_BUNDLE_SYMBOLIC_NAME, TB1);
    getRestClient().updateBundle(tb1Bundle.getBundleId(), url);

    Bundle tb11Bundle = getBundle(TB11_TEST_BUNDLE_SYMBOLIC_NAME);
    assertNotNull("Updated bundle not null", tb11Bundle);
    assertEquals("Updated bundle version", TB11_TEST_BUNDLE_VERSION, tb11Bundle.getVersion().toString());

    boolean receiveError = false;
    try {
      getRestClient().updateBundle(notExistingBundleId, url);
    } catch (Exception cause) {
      receiveError = true;
    }
    assertTrue("Update not existing bundle " + notExistingBundleId + " finished with error", receiveError);

    receiveError = false;
    try {
      getRestClient().updateBundle(tb1Bundle.getBundleId(), "Invalid-bundle-location");
    } catch (Exception cause) {
      receiveError = true;
    }
    assertTrue("Update with invalid URI finished with error", receiveError);

    // update with bundle content
    Bundle tb2Bundle = getTestBundle(TB2_TEST_BUNDLE_SYMBOLIC_NAME, TB2);
    getRestClient().updateBundle(tb2Bundle.getBundleId(), getContext().getBundle().getEntry(TB21).openStream());

    Bundle tb21Bundle = getBundle(TB21_TEST_BUNDLE_SYMBOLIC_NAME);
    assertNotNull("Updated bundle not null", tb21Bundle);
    assertEquals("Updated bundle version", TB21_TEST_BUNDLE_VERSION, tb21Bundle.getVersion().toString());

    receiveError = false;
    try {
      getRestClient().updateBundle(notExistingBundleId, getContext().getBundle().getEntry(TB21).openStream());
    } catch (Exception cause) {
      receiveError = true;
    }
    assertTrue("Update not existing bundle " + notExistingBundleId + " finished with error", receiveError);

    receiveError = false;
    try {
      getRestClient().updateBundle(tb2Bundle.getBundleId(), new ByteArrayInputStream("Invalid-bundle-content".getBytes()));
    } catch (Exception cause) {
      receiveError = true;
    }
    assertTrue("Update with invalid content finished with error", receiveError);

    long tb1BundleId = tb11Bundle.getBundleId();
    getRestClient().uninstallBundle(tb1BundleId);
    tb11Bundle = getBundle(TB11_TEST_BUNDLE_SYMBOLIC_NAME);
    assertNull("Framework bundle for uninstalled bundle", tb11Bundle);

    receiveError = false;
    try {
      getRestClient().uninstallBundle(tb1BundleId);
    } catch (Exception cause) {
      receiveError = true;
    }
    assertTrue("Uninstall already uninstalled bundle " + tb1BundleId + " finished with error", receiveError);

    // stop tb2 bundle throws Exception
    tb21Bundle = getBundle(TB21_TEST_BUNDLE_SYMBOLIC_NAME);
    tb21Bundle.start();

    String tb2BundlePath = getBundlePath(tb2Bundle);

    receiveError = false;
    try {
      getRestClient().uninstallBundle(tb2BundlePath);
    } catch (Exception cause) {
      receiveError = true;
    }
    // assertTrue("Uninstall bundle with error in stop method " + tb2BundlePath, receiveError);

    tb21Bundle = getBundle(TB21_TEST_BUNDLE_SYMBOLIC_NAME);
    assertNull("Framework bundle for uninstalled bundle " + tb2BundlePath, tb21Bundle);
  }

  public void testBundleStateRestClient() throws Exception {
    // GET
    Bundle bundle = getRandomBundle();
    int state = getRestClient().getBundleState(bundle.getBundleId());
    assertEquals("Get state by bundle ID " + bundle.getBundleId(), bundle.getState(), state);

    state = getRestClient().getBundleState(getBundlePath(bundle));
    assertEquals("Bundle state by bundle path " + getBundlePath(bundle), bundle.getState(), state);

    long notExistingBundleId = getNotExistingBundleId();
    boolean receiveError = false;
    try {
      getRestClient().getBundleState(notExistingBundleId);
    } catch (Exception cause) {
      receiveError = true;
    }
    assertTrue("Try to get state for non existing bundle " + notExistingBundleId, receiveError);

    receiveError = false;
    try {
      getRestClient().getBundleState("Illegal-bundle-path");
    } catch (Exception cause) {
      receiveError = true;
    }
    assertTrue("Try to get state with illegal bundle path", receiveError);

    // start by bundle id
    Bundle tb1Bundle = getTestBundle(TB1_TEST_BUNDLE_SYMBOLIC_NAME, TB1);

    int tb1State = tb1Bundle.getState();
    if (tb1State == Bundle.ACTIVE) {
      tb1Bundle.stop();
    }

    getRestClient().startBundle(tb1Bundle.getBundleId());

    tb1Bundle = getBundle(TB1_TEST_BUNDLE_SYMBOLIC_NAME);  // is it needed?
    assertEquals("New state ", Bundle.ACTIVE, tb1Bundle.getState());

    // start by bundle path
    Bundle tb2Bundle = getTestBundle(TB2_TEST_BUNDLE_SYMBOLIC_NAME, TB2);
    if (tb2Bundle.getState() == Bundle.ACTIVE) {
      tb2Bundle.stop();
    }

    getRestClient().startBundle(getBundlePath(tb2Bundle));

    tb2Bundle = getBundle(TB2_TEST_BUNDLE_SYMBOLIC_NAME);
    assertEquals("New state ", Bundle.ACTIVE, tb2Bundle.getState());
    unisntallIfInstalled(TB2_TEST_BUNDLE_SYMBOLIC_NAME);

    receiveError = false;
    try {
      getRestClient().startBundle(notExistingBundleId);
    } catch (Exception cause) {
      receiveError = true;
    }
    assertTrue("Try to start non existing bundle " + notExistingBundleId, receiveError);

    // start bundle with Bundle-ActivationPolicy by bundle id
    Bundle tb3Bundle = getTestBundle(TB3_TEST_BUNDLE_SYMBOLIC_NAME, TB3);
    int tb3State = tb3Bundle.getState();
    if (tb3State == Bundle.ACTIVE || tb3State == Bundle.STARTING) {
      tb3Bundle.stop();
    }

    getRestClient().startBundle(tb3Bundle.getBundleId(), Bundle.START_ACTIVATION_POLICY);
    assertEquals("New state for 'lazy' bundle ", Bundle.STARTING, tb3Bundle.getState());

    // start bundle with Bundle-ActivationPolicy by bundle path
    Bundle tb4Bundle = getTestBundle(TB4_TEST_BUNDLE_SYMBOLIC_NAME, TB4);
    int tb4State = tb3Bundle.getState();
    if (tb4State == Bundle.ACTIVE || tb4State == Bundle.STARTING) {
      tb4Bundle.stop();
    }

    getRestClient().startBundle(getBundlePath(tb4Bundle), Bundle.START_ACTIVATION_POLICY);
    assertEquals("New state for 'lazy' bundle ", Bundle.STARTING, tb4Bundle.getState());

    // stop by bundle id
    Bundle tb21Bundle = getTestBundle(TB21_TEST_BUNDLE_SYMBOLIC_NAME, TB21);
    if (tb21Bundle.getState() != Bundle.ACTIVE) {
      tb21Bundle.start();
    }

    receiveError = false;
    try {
      getRestClient().stopBundle(tb21Bundle.getBundleId());
    } catch (Exception cause) {
      receiveError = true;
    }
    assertTrue("Stop bundle for bundle with error in stop method " + tb21Bundle.getBundleId(), receiveError);

    // stop by bundle path
    getRestClient().stopBundle(getBundlePath(tb1Bundle));
    assertEquals("New state ", Bundle.RESOLVED, tb1Bundle.getState());

    // stop bundle by bundle id and options
    getRestClient().stopBundle(tb3Bundle.getBundleId(), Bundle.STOP_TRANSIENT);
    assertEquals("New state ", Bundle.RESOLVED, tb3Bundle.getState());

    // stop bundle by bundle path and options
    getRestClient().stopBundle(getBundlePath(tb4Bundle), Bundle.STOP_TRANSIENT);
    assertEquals("New state ", Bundle.RESOLVED, tb4Bundle.getState());
  }

  public void testBundleHeaderRestClient() throws Exception {
    Bundle bundle = getRandomBundle();
    Map<String, String> bHeaders = getRestClient().getBundleHeaders(bundle.getBundleId());
    assertBundleHeaderRepresentation(bundle, bHeaders);

    bHeaders = getRestClient().getBundleHeaders(getBundlePath(bundle));
    assertBundleHeaderRepresentation(bundle, bHeaders);

    long notExistingBundleId = getNotExistingBundleId();

    boolean receiveError = false;
    try {
      getRestClient().getBundleHeaders(notExistingBundleId);
    } catch (Exception cause) {
      receiveError = true;
    }
    assertTrue("Get bundle headers for not existing bundle " + notExistingBundleId, receiveError);
  }

  public void testBundleStartLevelRestClient() throws Exception {
    Bundle bundle = getRandomBundle();

    // get start level by bundle id
    BundleStartLevelDTO bundleStartLevelDTO = getRestClient().getBundleStartLevel(bundle.getBundleId());
    assertBundleStartLevelRepresentation(bundle, bundleStartLevelDTO);

    // get start level for non existing bundle id
    long notExistingBundleId = getNotExistingBundleId();
    boolean receiveError = false;
    try {
      getRestClient().getBundleStartLevel(notExistingBundleId);
    } catch (Exception cause) {
      receiveError = true;
    }
    assertTrue("Get bundle start level for not existing bundle " + notExistingBundleId, receiveError);

    // get start level by bundle path
    bundleStartLevelDTO = getRestClient().getBundleStartLevel(getBundlePath(bundle));
    assertBundleStartLevelRepresentation(bundle, bundleStartLevelDTO);

    // get start level for non existing bundle path
    String notExistingBundlePath = getNotExistingBundlePath();
    receiveError = false;
    try {
      getRestClient().getBundleStartLevel(notExistingBundlePath);
    } catch (Exception cause) {
      receiveError = true;
    }
    assertTrue("Get bundle start level for not existing bundle " + notExistingBundlePath, receiveError);

    // set start level by bundle id
    Bundle tb1Bundle = getTestBundle(TB1_TEST_BUNDLE_SYMBOLIC_NAME, TB1);

    int tb1StartLevel = getBundleStartLevel(tb1Bundle).getStartLevel();
    int tb1NewStartLevel = tb1StartLevel + 1;
    BundleStartLevelDTO tb1StartLevelDTO = new BundleStartLevelDTO();
    tb1StartLevelDTO.startLevel = tb1NewStartLevel;

    getRestClient().setBundleStartLevel(tb1Bundle.getBundleId(), tb1StartLevelDTO.startLevel);

    tb1StartLevel = getBundleStartLevel(tb1Bundle).getStartLevel();
    assertEquals("New start level ", tb1NewStartLevel, tb1StartLevel);

    // set start level by bundle id for non existing bundle id
    receiveError = false;
    try {
      getRestClient().setBundleStartLevel(notExistingBundleId, tb1StartLevelDTO.startLevel);
    } catch (Exception cause) {
      receiveError = true;
    }
    assertTrue("Set bundle start level for not existing bundle " + notExistingBundlePath, receiveError);

    // set illegal start level by bundle id
    receiveError = false;
    try {
      tb1StartLevelDTO.startLevel = -1;
      getRestClient().setBundleStartLevel(tb1Bundle.getBundleId(), tb1StartLevelDTO.startLevel);
    } catch (Exception cause) {
      receiveError = true;
    }
    assertTrue("Set negative bundle start level", receiveError);

    // set start level by bundle path
    Bundle tb2Bundle = getTestBundle(TB2_TEST_BUNDLE_SYMBOLIC_NAME, TB2);
    int tb2StartLevel = getBundleStartLevel(tb2Bundle).getStartLevel();
    int tb2NewStartLevel = tb2StartLevel + 1;
    BundleStartLevelDTO tb2StartLevelDTO = new BundleStartLevelDTO();
    tb2StartLevelDTO.startLevel = tb2NewStartLevel;

    getRestClient().setBundleStartLevel(getBundlePath(tb2Bundle), tb2StartLevelDTO.startLevel);
    tb2StartLevel = getBundleStartLevel(tb2Bundle).getStartLevel();
    assertEquals("New start level ", tb2NewStartLevel, tb2StartLevel);

    // set start level by bundle path for non existing bundle path
    receiveError = false;
    try {
      getRestClient().setBundleStartLevel(notExistingBundlePath, tb2StartLevelDTO.startLevel);
    } catch (Exception cause) {
      receiveError = true;
    }
    assertTrue("Set bundle start level for not existing bundle path " + notExistingBundlePath, receiveError);

    // set illegal start level by bundle path
    receiveError = false;
    try {
      tb2StartLevelDTO.startLevel = -1;
      getRestClient().setBundleStartLevel(getBundlePath(tb2Bundle), tb2StartLevelDTO.startLevel);
    } catch (Exception cause) {
      receiveError = true;
    }
    assertTrue("Set negative bundle start level", receiveError);
  }

  public void testServiceListRestClient() throws Exception {
    String filter = null;

    Collection<String> services = getRestClient().getServicePaths();
    ServiceReference<?>[] serviceRefs = getServices(filter);
    assertServiceList(serviceRefs, services);

    filter = "(&(" + Constants.SERVICE_ID + ">=" + serviceRefs.length / 2 + ")"
        + "(" + Constants.SERVICE_ID + "<=" + serviceRefs.length + "))";

    services = getRestClient().getServicePaths(filter);
    serviceRefs = getServices(filter);
    assertServiceList(serviceRefs, services);

    String invalidFilter = "invalid-filter";
    boolean receiveError = false;
    try {
      services = getRestClient().getServicePaths(invalidFilter);
    } catch (Exception cause) {
      receiveError = true;
    }
    assertTrue("Request with invalid filter " + invalidFilter, receiveError);
  }

  public void testServiceRepresentationsListRestClient() throws Exception {
    String filter = null;
    Collection<ServiceReferenceDTO> serviceRepresentationsDTO = getRestClient().getServiceReferences();
    ServiceReference<?>[] serviceRefs = getServices(filter);
    assertServiceRepresentationList(serviceRefs, serviceRepresentationsDTO);

    filter = "(&(" + Constants.SERVICE_ID + ">=" + serviceRefs.length / 2 + ")"
        + "(" + Constants.SERVICE_ID + "<=" + serviceRefs.length + "))";

    serviceRefs = getServices(filter);
    serviceRepresentationsDTO = getRestClient().getServiceReferences(filter);
    assertServiceRepresentationList(serviceRefs, serviceRepresentationsDTO);

    filter = "invalid-filter";
    boolean receiveError = false;
    try {
      getRestClient().getServicePaths(filter);
    } catch (Exception cause) {
      receiveError = true;
    }
    assertTrue("Request with invalid filter " + filter, receiveError);
  }

  public void testServiceRestClient() throws Exception {
    ServiceReference<?> serviceRef = getRandomService();

    ServiceReferenceDTO serviceReferenceDTO = getRestClient().getServiceReference(getServiceId(serviceRef));
    assertService(serviceRef, serviceReferenceDTO);

    long notExistingSeriveId = System.currentTimeMillis();
    boolean receiveError = false;
    try {
      getRestClient().getServiceReference(notExistingSeriveId);
    } catch (Exception cause) {
      receiveError = true;
    }
    assertTrue("Request with non existing service id " + notExistingSeriveId, receiveError);

    serviceReferenceDTO = getRestClient().getServiceReference(getServicePath(serviceRef));
    assertService(serviceRef, serviceReferenceDTO);

    String notExistingSerivePath = "not-existing-servicve-" + System.currentTimeMillis();
    receiveError = false;
    try {
      getRestClient().getServiceReference(notExistingSerivePath);
    } catch (Exception cause) {
      receiveError = true;
    }
    assertTrue("Request with non existing service path " + notExistingSerivePath, receiveError);
  }

	/**
	 * A basic test that ensures the provider of the RestClientFactory service
	 * advertises the service capability
	 * 
	 * @throws Exception
	 */
	public void testServiceCapability() throws Exception {

		List<BundleCapability> capabilities = restClientFactoryRef.getBundle()
				.adapt(BundleWiring.class)
				.getCapabilities("osgi.service");

		boolean hasCapability = false;
		boolean uses = false;

		for (Capability cap : capabilities) {
			@SuppressWarnings("unchecked")
			List<String> objectClass = (List<String>) cap.getAttributes().get("objectClass");

			if (objectClass.contains(RestClientFactory.class.getName())) {
				hasCapability = true;
				String usesDirective = cap.getDirectives().get(Namespace.CAPABILITY_USES_DIRECTIVE);
				if (usesDirective != null) {
					Set<String> packages = new HashSet<String>(Arrays.asList(usesDirective.trim().split("\\s*,\\s*")));
					uses = packages.contains("org.osgi.service.rest.client");
				}
				break;
			}
		}
		assertTrue("No osgi.service capability for the RestClientFactory service", hasCapability);
		assertTrue("Missing uses constraint on the osgi.service capability", uses);
	}

  public RestClient getRestClient() {
    return restClient;
  }

  protected void assertServiceRepresentationList(ServiceReference<?>[] serviceRefs, Collection<ServiceReferenceDTO> serviceRepresentationsDTO) {
    if (serviceRefs != null) {
      assertNotNull("Service representation list", serviceRepresentationsDTO);

      Hashtable<Long, ServiceReferenceDTO> serviceRepresentationHT = new Hashtable<Long, ServiceReferenceDTO>();

      for (Iterator<ServiceReferenceDTO> i = serviceRepresentationsDTO.iterator(); i.hasNext();) {
        ServiceReferenceDTO serviceReferenceDTO = i.next();
        try {
          serviceRepresentationHT.put(serviceReferenceDTO.id, serviceReferenceDTO);
        } catch (Exception cause) {
          fail(cause.getMessage());
        }
      }

      assertEquals("Service representation list size", serviceRefs.length, serviceRepresentationHT.size());
      for (ServiceReference<?> serviceRef : serviceRefs) {
        assertService(serviceRef, serviceRepresentationHT.get(serviceRef.getProperty(Constants.SERVICE_ID)));
      }
    }
  }

  protected void assertService(ServiceReference<?> serviceRef, ServiceReferenceDTO serviceReferenceDTO) {
    assertNotNull("Service representation ", serviceReferenceDTO);

    String[] propKeys = serviceRef.getPropertyKeys();
    Map<String, Object> propertiesDTO = serviceReferenceDTO.properties;

    if (propKeys != null) {
      assertEquals("Properties size ", propKeys.length, propertiesDTO.size());
      for (String key : propKeys) {
        Object value = serviceRef.getProperty(key);

        assertTrue("Service property " + key, propertiesDTO.containsKey(key));
        if (value instanceof String[]) {
          assertEquivalent((String[]) value, (String[]) propertiesDTO.get(key));
        } else if (value instanceof Number) {
          // Need to handle cases where round trip of Long results in
          // Integer and
          // round trip of Double results in Float.
          // But this does not handle cases where round trip of Double
          // or Float
          // result in Integer or Long
          assertEquals("Service property value ",
            String.valueOf(value),
            String.valueOf(propertiesDTO.get(key)));
        } else {
          assertEquals("Service property value ", value, propertiesDTO.get(key));
        }
      }
    } else { // Is it possible?
      assertNull("No properties for service " + serviceRef.getProperty(Constants.SERVICE_ID) + " :", propertiesDTO);
    }

    assertEquals("Bundle property", serviceRef.getBundle().getBundleId(), serviceReferenceDTO.bundle);

    long[] usingBundlesDTO = serviceReferenceDTO.usingBundles;
    Bundle[] usingBundles = serviceRef.getUsingBundles();

    if (usingBundles != null) {
      HashSet<Long> uBundlesSet = new HashSet<Long>();
      for (Bundle uBundle : usingBundles) {
        uBundlesSet.add(uBundle.getBundleId());
      }

      assertNotNull("usingBundles not null", usingBundlesDTO);
      assertEquals("usingBundles length", uBundlesSet.size(), usingBundlesDTO.length);

      for (int k = 0; k < usingBundlesDTO.length; k++) {
        assertTrue("usingBundles element " + usingBundlesDTO[k], uBundlesSet.contains(usingBundlesDTO[k]));
      }
    } else {
      assertNull("usingBundles is null", usingBundlesDTO);
    }
  }

  protected void assertServiceList(ServiceReference<?>[] serviceRefs, Collection<String> services) {
    assertNotNull("Service list", services);
    assertEquals("Service list length:", serviceRefs.length, services.size());

    for (ServiceReference<?> serviceRef : serviceRefs) {
      String serviceId = serviceRef.getProperty(Constants.SERVICE_ID).toString();
      assertTrue("Service list contains " + serviceId + " :", services.contains(getServiceURI(serviceId)));
    }
  }

  protected void assertBundleStartLevelRepresentation(Bundle bundle, BundleStartLevelDTO bundleStartLevelDTO) {
    BundleStartLevel bundleStartLevel = getBundleStartLevel(bundle);

    assertEquals("startLevel:", bundleStartLevel.getStartLevel(), bundleStartLevelDTO.startLevel);
    assertEquals("activationPolicyUsed:", bundleStartLevel.isActivationPolicyUsed(), bundleStartLevelDTO.activationPolicyUsed);
    assertEquals("persistentlyStarted:", bundleStartLevel.isPersistentlyStarted(), bundleStartLevelDTO.persistentlyStarted);
  }

  protected void assertBundleHeaderRepresentation(Bundle bundle, Map<String, String> bHeaders) {
    Dictionary<String, String> headers = bundle.getHeaders();
    if (headers == null) {
      assertNull("Bundle headers" + bundle.getBundleId() + ": ", bHeaders);
      return;
    }

    assertNotNull("Bundle headers" + bundle.getBundleId() + ": ", bHeaders);
    assertEquals("Headers size", headers.size(), bHeaders.size());

    for (Enumeration<String> keys = headers.keys(); keys.hasMoreElements();) {
      String key = keys.nextElement();
      assertEquals(key + ":", headers.get(key), bHeaders.get(key));
    }
  }

  protected void assertBundleCollection(Bundle[] bundles, Collection<String> bundleCollection) {
    assertNotNull("Bundle collection is not null.", bundleCollection);
    assertEquals("Bundle list length", bundles.length, bundleCollection.size());

    for (Bundle bundle : bundles) {
      long bundleId = bundle.getBundleId();
      assertTrue("Bundle list contains " + bundleId + ".", bundleCollection.contains(getBundleURI(bundleId)));
    }
  }

  protected void assertBundleRepresentation(Bundle bundle, BundleDTO bundleRepresentation) {
    assertEquals("lastModified:", bundle.getLastModified(), bundleRepresentation.lastModified);
    assertEquals("state:", bundle.getState(), bundleRepresentation.state);
    assertEquals("symbolicName:", bundle.getSymbolicName(), bundleRepresentation.symbolicName);
    assertEquals("version:", bundle.getVersion().toString(), bundleRepresentation.version);
  }

  protected void assertEquivalent(final String[] a1, final String[] a2) {
    assertEquals(a1.length, a2.length);
    for (int i = 0; i < a1.length; i++) {
      assertEquals(a1[i], a2[i]);
    }
  }

}

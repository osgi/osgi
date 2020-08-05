package org.osgi.test.cases.jmx.framework.junit;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.jmx.framework.BundleStateMBean;
import org.osgi.jmx.framework.PackageStateMBean;
import org.osgi.test.support.wiring.Wiring;


public class BundleStateMBeanTestCase extends MBeanGeneralTestCase {
	private Bundle testBundle1;
	private Bundle testBundle2;
	private BundleStateMBean bsMBean;
	private BundleContext bundleContext;

	private static final String EXPORTED_PACKAGE = "org.osgi.test.cases.jmx.framework.tb2.api";

	@Override
	public void setUp() throws Exception {
		super.setUp();
		testBundle2 = super.install("tb2.jar");
		testBundle2.start(Bundle.START_ACTIVATION_POLICY);

		testBundle1 = super.install("tb1.jar");
		testBundle1.start();

		super.waitForRegistering(createObjectName(BundleStateMBean.OBJECTNAME));
		bsMBean = getMBeanFromServer(BundleStateMBean.OBJECTNAME, BundleStateMBean.class);
		bundleContext = super.getContext();
	}

	public void testObjectNameStructure() throws Exception {
	    ObjectName queryName = new ObjectName(BundleStateMBean.OBJECTNAME + ",*");
	    Set<ObjectName> names = getMBeanServer().queryNames(queryName, null);
	    assertEquals(1, names.size());

	    ObjectName name = names.iterator().next();
	    Hashtable<String, String> props = name.getKeyPropertyList();

	    String type = props.get("type");
	    assertEquals("bundleState", type);
	    String version = props.get("version");
	    assertEquals("1.7", version);
	    String framework = props.get("framework");
	    assertEquals(bundleContext.getBundle(0).getSymbolicName(), framework);
	    String uuid = props.get("uuid");
	    assertEquals(bundleContext.getProperty(Constants.FRAMEWORK_UUID), uuid);

	    assertTrue(name.getKeyPropertyListString().startsWith(
	            "type=" + type + ",version=" + version + ",framework=" + framework + ",uuid=" + uuid));
	}

	public void testGetDependencies() throws IOException {
		assertNotNull(bsMBean);

		//assuming that tb1 depends on tb2
		long[] requiredBundles = bsMBean.getRequiredBundles(testBundle1.getBundleId());
		boolean found = false;
		for(long bundleId : requiredBundles) {
			if(bundleId == testBundle2.getBundleId()) {
				found = true;
				break;
			}
		}
		assertTrue(
				"testbundle1 depends on testbundle2. This dependency was not reflected "
						+ "by the result of the call to getRequiredBundles. result: "
						+ bsMBean.getRequiredBundles(testBundle1.getBundleId()),
				found);
	}

	public void testGetRequiringBundles() throws IOException {
		assertNotNull(bsMBean);
		long expectedBundleID = testBundle1.getBundleId();

		boolean found = false;
		for(long bundleId : bsMBean.getRequiringBundles(testBundle2.getBundleId())) {
			if(bundleId == expectedBundleID) {
				found = true;
				break;
			}
		}
		assertTrue("tb2 is required by tb1. getRequiringBundles() was not able to detect this dependency.", found);
	}

	@SuppressWarnings("unchecked")
	public void testGetBundle() throws IOException {
		CompositeData bundle = bsMBean.getBundle(testBundle1.getBundleId());
		assertCompositeDataKeys(bundle, "BUNDLE_TYPE", new String[] {"ExportedPackages", "Fragment", "Fragments", "Headers", "Hosts",
                "Identifier", "ImportedPackages", "LastModified", "Location", "PersistentlyStarted",
                "RegisteredServices", "RemovalPending", "Required", "RequiredBundles", "RequiringBundles",
                "StartLevel", "State", "ServicesInUse", "SymbolicName", "Version"});
		String [] exportedPackages = (String[]) bundle.get("ExportedPackages");
		assertEquals(0, exportedPackages.length);

		boolean packageFound = false;
		String [] importedPackages = (String[]) bundle.get("ImportedPackages");
		for (int i=0; i < importedPackages.length; i++) {
		    if (importedPackages[i].startsWith(EXPORTED_PACKAGE)) {
		        packageFound = true;
		        break;
		    }
		}
		assertTrue(packageFound);

		assertEquals(testBundle1.getLastModified(), bundle.get("LastModified"));
		assertEquals(testBundle1.getLocation(), bundle.get("Location"));
		assertEquals(0, ((Long []) bundle.get("RegisteredServices")).length);
		assertFalse((Boolean) bundle.get("Required"));
		// TODO create a test for RequiredBundles
		assertEquals(0, ((Long []) bundle.get("RequiringBundles")).length);
		assertEquals(bsMBean.getStartLevel(testBundle1.getBundleId()), bundle.get("StartLevel"));
		assertEquals(bsMBean.getState(testBundle1.getBundleId()), bundle.get("State"));
		assertEquals(1, ((Long[]) bundle.get("ServicesInUse")).length);
		assertFalse((Boolean) bundle.get("Fragment"));
		assertEquals(0, ((Long[]) bundle.get("Fragments")).length);
		TabularData headers = (TabularData) bundle.get("Headers");
		assertTabularDataStructure(headers, "HEADERS_TYPE", "Key", new String [] {"Key", "Value"});
		int foundKeys = 0;
		for (CompositeData data : (Collection<CompositeData>) headers.values()) {
		    String key = (String) data.get("Key");
		    if ("Bundle-Version".equals(key)) {
		        assertEquals(testBundle1.getVersion().toString(), data.get("Value"));
		        foundKeys++;
		    } else if ("Bundle-Activator".equals(key)) {
		        assertEquals(testBundle1.getHeaders().get(Constants.BUNDLE_ACTIVATOR), data.get("Value"));
                foundKeys++;
		    } else if ("Bundle-SymbolicName".equals(key)) {
		        assertEquals(testBundle1.getSymbolicName(), data.get("Value"));
                foundKeys++;
		    }
		}
		assertEquals(3, foundKeys);
		assertTrue((Boolean) bundle.get("PersistentlyStarted"));
		assertFalse((Boolean) bundle.get("RemovalPending"));
		assertEquals(testBundle1.getSymbolicName(), bundle.get("SymbolicName"));
		assertEquals(testBundle1.getVersion().toString(), bundle.get("Version"));
	}

	public void testGetBundles() throws IOException {
		assertNotNull(bsMBean);
		TabularData bundleList = bsMBean.listBundles();
		assertTabularDataStructure(bundleList, "BUNDLES_TYPE", "Identifier",
				new String[] {	"ExportedPackages", "Fragment", "Fragments", "Headers", "Hosts",
								"Identifier", "ImportedPackages", "LastModified", "Location", "PersistentlyStarted",
								"RegisteredServices", "RemovalPending", "Required", "RequiredBundles", "RequiringBundles",
								"StartLevel", "State", "ServicesInUse", "SymbolicName", "Version"});

		Collection< ? > values = bundleList.values();
		Iterator< ? > iter = values.iterator();
		boolean foundBundle1 = false;
		boolean foundBundle2 = false;
		while (iter.hasNext()) {
			CompositeData item = (CompositeData) iter.next();
			long tempBundleId = ((Long) item.get("Identifier")).longValue();

			if ((tempBundleId == testBundle1.getBundleId()) || (tempBundleId == testBundle2.getBundleId())) {

				String[] exportedPackages = (String[]) item.get("ExportedPackages");

				String[] importedPackages = (String[])	item.get("ImportedPackages");

				long lastModified = ((Long)	item.get("LastModified")).longValue();

				String location = (String) item.get("Location");

				Long[] registeredServices = (Long[]) item.get("RegisteredServices");

				boolean required = ((Boolean) item.get("Required")).booleanValue();

				Long[] requiredBundles = (Long[]) item.get("RequiredBundles");

				Long[] requiringBundles = (Long[]) item.get("RequiringBundles");

				int startLevel = ((Integer) item.get("StartLevel")).intValue();

				String state = (String) item.get("State");

				Long[] servicesInUse = (Long[]) item.get("ServicesInUse");

				if (tempBundleId == testBundle1.getBundleId()) {
					foundBundle1 = true;

					assertTrue("wrong bundle1 export packages info", exportedPackages.length == 0);

					boolean packageFound = false;
					for (int i = 0; i < importedPackages.length; i++) {
						if (importedPackages[i].startsWith(EXPORTED_PACKAGE)) {
							packageFound = true;
							break;
						}
					}
					assertTrue("wrong bundle1 import packages info", packageFound);

					assertTrue("wrong bundle last modified time", lastModified == testBundle1.getLastModified());

					assertTrue("wrong bundle location", location.equals(testBundle1.getLocation()));

					assertTrue("wrong bundle registered services info", registeredServices.length == 0);

					assertFalse("wrong bundle required started info for tb1",
							required);

					assertEquals("wrong bundle required bundles for tb1 ", 2,
							requiredBundles.length);
					assertTrue(
							"wrong bundle required bundles for tb1 ",
							Arrays.asList(requiredBundles).contains(
									Long.valueOf(testBundle2.getBundleId())));
					assertTrue("wrong bundle required bundles for tb1 ", Arrays
							.asList(requiredBundles).contains(Long.valueOf(0)));

					assertTrue("wrong bundle requiring bundles for tb1",
							requiringBundles.length == 0);

					assertTrue("wrong bundle start level info", startLevel == bsMBean.getStartLevel(testBundle1.getBundleId()));

					assertTrue("wrong bundle state info", state.equals(bsMBean.getState(testBundle1.getBundleId())));

					assertTrue("wrong bundle services in use info", servicesInUse.length == 1);
				} else {
					foundBundle2 = true;

					assertTrue("wrong bundle2 export packages info " + exportedPackages[0], (exportedPackages.length == 1) && (exportedPackages[0].startsWith(EXPORTED_PACKAGE)));

					assertTrue("wrong bundle last modified time", lastModified == testBundle2.getLastModified());

					assertTrue("wrong bundle location", location.equals(testBundle2.getLocation()));

					assertTrue("wrong bundle registered services info", (registeredServices.length == 3));

					assertTrue("wrong bundle required started info for tb2", required);

					assertEquals("wrong bundle requiring bundles for tb2 ", 1,
							requiringBundles.length);
					assertTrue(
							"wrong bundle requiring bundles for tb2",
							Arrays.asList(requiringBundles).contains(
									Long.valueOf(testBundle1.getBundleId())));

					assertTrue("wrong bundle start level info", startLevel == bsMBean.getStartLevel(testBundle2.getBundleId()));

					assertTrue("wrong bundle state info", state.equals(bsMBean.getState(testBundle2.getBundleId())));

					assertTrue("wrong bundle services in use info", servicesInUse.length == 0);
				}

				boolean fragment = ((Boolean) item.get("Fragment")).booleanValue();
				assertFalse("wrong bundle fragment info", fragment);

				Long[] fragments = (Long[])	item.get("Fragments");
				assertTrue("wrong bundle fragments info", fragments.length == 0);

				TabularData headersList = (TabularData)	item.get("Headers");
				assertTabularDataStructure(headersList, "HEADERS_TYPE", "Key",	new String[] { "Key", "Value" });
				int foundKeys = 0;
				Iterator< ? > headersListIter = headersList.values().iterator();
				while (headersListIter.hasNext()) {
					String key = (String) ((CompositeData) headersListIter.next()).get("Key");
					if (key.equals("Bundle-Version") || key.equals("Bundle-Activator") || key.equals("Bundle-Vendor")) {
						foundKeys++;
					}
				}
				assertTrue("some header keys were not found", foundKeys == 3);

				Long[] hosts = (Long[])	item.get("Hosts");
				assertTrue("wrong bundle hosts info", hosts.length == 0);

				assertTrue("wrong bundle persistently started info", ((Boolean) item.get("PersistentlyStarted")).booleanValue());

				assertFalse("wrong bundle removal pending started info", ((Boolean) item.get("RemovalPending")).booleanValue());

				String symbolicName = (String) item.get("SymbolicName");

				assertNotNull(symbolicName);

				String version = (String) item.get("Version");

				assertNotNull(version);
			}
		}
		assertTrue(
				"listBundles() did not return data for some of the test bundles",
				foundBundle1 && foundBundle2);
	}

	@SuppressWarnings("unchecked")
	public void testGetBundlesRestricted() throws IOException {
        TabularData bundleList = bsMBean.listBundles(BundleStateMBean.SYMBOLIC_NAME, BundleStateMBean.VERSION);
        assertTabularDataStructure(bundleList, "BUNDLES_TYPE", BundleStateMBean.IDENTIFIER,
                new String[] {BundleStateMBean.IDENTIFIER, BundleStateMBean.SYMBOLIC_NAME, BundleStateMBean.VERSION});

        int foundBundles = 0;
        for (CompositeData data : (Collection<CompositeData>) bundleList.values()) {
            long id = (Long) data.get(BundleStateMBean.IDENTIFIER);
            Bundle bundle = null;
            if (testBundle1.getBundleId() == id) {
                bundle = testBundle1;
            } else if (testBundle2.getBundleId() == id) {
                bundle = testBundle2;
            }
            if (bundle == null)
                continue;

            foundBundles++;
            int foundValues = 0;
			for (String key : BundleStateMBean.BUNDLE_TYPE.keySet()) {
                Object value = data.get(key);
                if (BundleStateMBean.IDENTIFIER.equals(key)) {
                    assertEquals(bundle.getBundleId(), value);
                    foundValues++;
                } else if (BundleStateMBean.SYMBOLIC_NAME.equals(key)) {
                    assertEquals(bundle.getSymbolicName(), value);
                    foundValues++;
                } else if (BundleStateMBean.VERSION.equals(key)) {
                    assertEquals(bundle.getVersion().toString(), value);
                    foundValues++;
                } else {
                    assertNull("Other values should not be available", value);
                }
            }
            assertEquals(3, foundValues);
        }
        assertEquals(2, foundBundles);
	}

	public void testGetBundleIds() throws IOException {
	    long[] ids = bsMBean.getBundleIds();
	    Set<Long> actualIDs = new HashSet<Long>();
	    for (long id : ids) {
	        actualIDs.add(id);
	    }

	    assertTrue(actualIDs.contains(0l));
	    assertTrue(actualIDs.contains(testBundle1.getBundleId()));
	    assertTrue(actualIDs.contains(testBundle2.getBundleId()));
	}

	public void testGetExportedPackages() throws IOException {
		assertNotNull(bsMBean);
		String expectedExportedPackages = "org.osgi.test.cases.jmx.framework.tb2.api";


		String[] exportedPackages = bsMBean.getExportedPackages(testBundle2.getBundleId());

		boolean found = false;
		for(String exportedPackage : exportedPackages) {
			if(exportedPackage.startsWith(expectedExportedPackages)) {
				found = true;
				break;
			}
		}

		if(!found) {
			fail("failed to find the exported package " + expectedExportedPackages + " in the bundle " + testBundle2.getSymbolicName());
		}
	}

	public void testGetImportedPackages() throws IOException {
		assertNotNull(bsMBean);
		String expectedImportedPackages = "org.osgi.test.cases.jmx.framework.tb2.api";

		String[] importedPackages = bsMBean.getImportedPackages(testBundle1.getBundleId());

		boolean found = false;
		for(String importedPackage : importedPackages) {
			if(importedPackage.startsWith(expectedImportedPackages)) {
				found = true;
				break;
			}
		}

		if(!found) {
			fail("failed to find the imported package " + expectedImportedPackages + " in the bundle " + testBundle1.getSymbolicName());
		}
	}

	public void testGetFragments() throws IOException {
		long[] fragments = bsMBean.getFragments(testBundle1.getBundleId());
		assertTrue("no fragments defined for test bundle1", fragments.length == 0);
		fragments = bsMBean.getFragments(testBundle2.getBundleId());
		assertTrue("no fragments defined for test bundle2", fragments.length == 0);
	}

	 //TODO - add third test bundle that is a fragment
	/*public void testGetHosts() throws IOException {

		long[] hosts = bsMBean.getHosts(...);
		assertTrue("", hosts.length == 0);
		hosts = bsMBean.getHosts(...);
		assertTrue("", hosts.length == 0);
	}*/

	public void testGetHeaders() throws IOException {
		assertNotNull(bsMBean);
		TabularData headersList = bsMBean.getHeaders(testBundle1.getBundleId());
		assertTabularDataStructure(headersList, "HEADERS_TYPE", "Key",	new String[] { "Key", "Value" });
		int foundKeys = 0;
		Iterator< ? > headersListIter = headersList.values().iterator();
		while (headersListIter.hasNext()) {
			String key = (String) ((CompositeData) headersListIter.next()).get("Key");
			if (key.equals("Bundle-Version") || key.equals("Bundle-Activator") || key.equals("Bundle-Vendor")) {
				foundKeys++;
			}
		}
		assertTrue("some header keys were not found", foundKeys == 3);
	}

	public void testGetLocalizedHeaders() throws IOException {
	    testGetLocalizedHeaders("en", "Description");
        testGetLocalizedHeaders("nl", "Omschrijving");
	}

    private void testGetLocalizedHeaders(String lang, String value) throws IOException {
        TabularData headersTable = bsMBean.getHeaders(testBundle1.getBundleId(), lang);
        assertTabularDataStructure(headersTable, "HEADERS_TYPE", "Key",  new String[] { "Key", "Value" });

        CompositeData descEn = headersTable.get(new Object [] {Constants.BUNDLE_DESCRIPTION});
        assertEquals(value, descEn.get(BundleStateMBean.VALUE));
    }

	public void testGetHeader() throws IOException {
	    assertEquals(testBundle1.getSymbolicName(),
            bsMBean.getHeader(testBundle1.getBundleId(), Constants.BUNDLE_SYMBOLICNAME));
	}

	public void testGetLocalizedHeader() throws IOException {
	    assertEquals("Description",
            bsMBean.getHeader(testBundle1.getBundleId(), Constants.BUNDLE_DESCRIPTION, "en"));
        assertEquals("Omschrijving",
                bsMBean.getHeader(testBundle1.getBundleId(), Constants.BUNDLE_DESCRIPTION, "nl"));
	}

	public void testGetRegisteredServices() throws IOException {
		long[] serviceIdentifiers = bsMBean.getRegisteredServices(testBundle2.getBundleId());
		assertTrue("testBundle2 defines three services", serviceIdentifiers.length == 3);
		ServiceReference< ? > ref = getContext().getServiceReference(
				"org.osgi.test.cases.jmx.framework.tb2.api.HelloSayer");
		Long expectedserviceId = (Long)ref.getProperty("service.id");
		boolean matched = false;
		for (int i = 0; i < serviceIdentifiers.length; i++) {
			if (serviceIdentifiers[i] == expectedserviceId.longValue()) {
				matched = true;
			}
		}
		assertTrue("testBundle2 service id is wrong", matched);

		serviceIdentifiers = bsMBean.getRegisteredServices(testBundle1.getBundleId());
		assertTrue("testBundle2 defines no services", serviceIdentifiers.length == 0);
	}

	public void testGetServicesInUse() throws IOException {
		assertNotNull(bsMBean);
		ServiceReference< ? > ref = getContext().getServiceReference(
				"org.osgi.test.cases.jmx.framework.tb2.api.HelloSayer");
		Long expectedserviceId = (Long)ref.getProperty("service.id");
		long[] servicesInUse = bsMBean.getServicesInUse(testBundle1.getBundleId());
		boolean found = false;

		for(long serviceId : servicesInUse) {
			if(serviceId == expectedserviceId) {
				found = true;
				break;
			}
		}

		assertTrue("tb1 is using service HelloSayer from tb2. However the method call did not return the expected result.", found);
	}

	public void testGetLocation() throws IOException {
		assertNotNull(bsMBean);
		String location = bsMBean.getLocation(testBundle1.getBundleId());
		assertTrue("wrong location", "tb1.jar".equals(location));
	}

	public void testGetState() throws IOException {
		assertNotNull(bsMBean);
		String bundleState = bsMBean.getState(testBundle1.getBundleId());
		final String expectedBundleState;

		boolean isBundleStateCorrect = false;
		switch (testBundle1.getState()) {
			case Bundle.ACTIVE :
				if(bundleState.equals("ACTIVE")) {
					isBundleStateCorrect = true;
				}
				expectedBundleState = "ACTIVE";
				break;

			case Bundle.INSTALLED :
				if(bundleState.equals("INSTALLED")) {
					isBundleStateCorrect = true;

				}
				expectedBundleState = "INSTALLED";
				break;

			case Bundle.STARTING :
				if(bundleState.equals("STARTING")) {
					isBundleStateCorrect = true;
				}
				expectedBundleState = "STARTING";
				break;

			case Bundle.RESOLVED :
				if(bundleState.equals("RESOLVED")) {
					isBundleStateCorrect = true;
				}
				expectedBundleState = "RESOLVED";
				break;

			case Bundle.STOPPING :
				if(bundleState.equals("STOPPING")) {
					isBundleStateCorrect = true;
				}
				expectedBundleState = "STOPPING";
				break;

			case Bundle.UNINSTALLED :
				if(bundleState.equals("UNINSTALLED")) {
					isBundleStateCorrect = true;
				}
				expectedBundleState = "UNINSTALLED";
				break;

			default:
				expectedBundleState = "none";
				fail();
		}
		assertTrue("could not retrieve the correct bundlestate for testbundle1 got state " + bundleState + " but expected state " + expectedBundleState, isBundleStateCorrect);
	}

	public void testGetSymbolicName() throws IOException {
		assertNotNull(bsMBean);
		String symbolicName = bsMBean.getSymbolicName(testBundle1.getBundleId());
		assertEquals("tb1 has wrong symbolic name",
				testBundle1.getSymbolicName(), symbolicName);
	}

	public void testGetVersion() throws IOException {
		assertNotNull(bsMBean);
		String version = bsMBean.getVersion(testBundle1.getBundleId());
		assertNotNull(version);
	}

	public void testIsFragment() throws IOException {
		assertNotNull(bsMBean);
		assertFalse("tb1 is a bundle and not a fragment.", bsMBean.isFragment(testBundle1.getBundleId()));
	}

	public void testIsRemovalPending() throws IOException {
		assertNotNull(bsMBean);
		assertTrue("removal is pending for testbundle1 which is not going to be removed ",!bsMBean.isRemovalPending(testBundle1.getBundleId()));
	}

	public void testIsRequired() throws IOException {
		assertNotNull(bsMBean);
		assertTrue("tb2 is required by tb1, thus the isRequired call on tb2 should be true.", bsMBean.isRequired(testBundle2.getBundleId()));
	}

	public void testIsActivationPolicyUsed() throws IOException {
	    assertFalse(bsMBean.isActivationPolicyUsed(testBundle1.getBundleId()));
        assertTrue(bsMBean.isActivationPolicyUsed(testBundle2.getBundleId()));
	}

	public void testIsPersistentlyStarted() throws IOException {
		boolean isPersistentlyStarted = bsMBean.isPersistentlyStarted(testBundle2.getBundleId());
		assertTrue(isPersistentlyStarted);
	}

	public void testGetHosts() throws IOException {
		TabularData bundleList = bsMBean.listBundles();
		Collection< ? > values = bundleList.values();
		Iterator< ? > iter = values.iterator();
		while (iter.hasNext()) {
			CompositeData item = (CompositeData) iter.next();
			long tempBundleId = ((Long) item.get("Identifier")).longValue();
			BundleRevision fragment = bundleContext.getBundle(tempBundleId)
					.adapt(BundleRevision.class);
			List<BundleRevision> hosts = Wiring.getHosts(bundleContext,
					fragment);
			if (!hosts.isEmpty()) {
				long[] hostIds = bsMBean.getHosts(tempBundleId);
				assertNotNull(hostIds);
				assertEquals("wrong hosts info returned for bundle with id"
						+ tempBundleId, hosts.size(), hostIds.length);
			}
		}
	}

	public void testExceptions() {
		assertNotNull(bsMBean);

		//test listBundles method
		try {
			bsMBean.listBundles();
			// This method works, this test just try to ensure no random runtime
			// exception is thrown?
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}

		//test getExportedPackages method
		try {
			bsMBean.getExportedPackages( LONG_NEGATIVE );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			bsMBean.getExportedPackages( LONG_BIG );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test getFragments method
		try {
			bsMBean.getFragments( LONG_NEGATIVE );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			bsMBean.getFragments( LONG_BIG );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test getHeaders method
		try {
			bsMBean.getHeaders( LONG_NEGATIVE );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			bsMBean.getHeaders( LONG_BIG );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test getHosts method
		try {
			bsMBean.getHosts( LONG_NEGATIVE );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
	    }
		try {
			bsMBean.getHosts( LONG_BIG );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test getImportedPackages method
		try {
			bsMBean.getImportedPackages( LONG_NEGATIVE );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			bsMBean.getImportedPackages( LONG_BIG );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test getLastModified method
		try {
			bsMBean.getLastModified( LONG_NEGATIVE );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			bsMBean.getLastModified( LONG_BIG );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test getLocation method
		try {
			bsMBean.getLocation( LONG_NEGATIVE );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			bsMBean.getLocation( LONG_BIG );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test getRegisteredServices method
		try {
			bsMBean.getRegisteredServices( LONG_NEGATIVE );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			bsMBean.getRegisteredServices( LONG_BIG );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test getRequiredBundles method
		try {
			bsMBean.getRequiredBundles( LONG_NEGATIVE );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			bsMBean.getRequiredBundles( LONG_BIG );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test getRequiringBundles method
		try {
			bsMBean.getRequiringBundles( LONG_NEGATIVE );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			bsMBean.getRequiringBundles( LONG_BIG );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test getServicesInUse method
		try {
			bsMBean.getServicesInUse( LONG_NEGATIVE );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			bsMBean.getServicesInUse( LONG_BIG );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test getStartLevel method
		try {
			bsMBean.getStartLevel( LONG_NEGATIVE );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			bsMBean.getStartLevel( LONG_BIG );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test getState method
		try {
			bsMBean.getState( LONG_NEGATIVE );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			bsMBean.getState( LONG_BIG );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test getSymbolicName method
		try {
			bsMBean.getSymbolicName( LONG_NEGATIVE );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			bsMBean.getSymbolicName( LONG_BIG );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test getVersion method
		try {
			bsMBean.getVersion( LONG_NEGATIVE );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			bsMBean.getVersion( LONG_BIG );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test isFragment method
		try {
			bsMBean.isFragment( LONG_NEGATIVE );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			bsMBean.isFragment( LONG_BIG );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test isPersistentlyStarted method
		try {
			bsMBean.isPersistentlyStarted( LONG_NEGATIVE );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			bsMBean.isPersistentlyStarted( LONG_BIG );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test isRemovalPending method
		try {
			bsMBean.isRemovalPending( LONG_NEGATIVE );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			bsMBean.isRemovalPending( LONG_BIG );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test isRequired method
		try {
			bsMBean.isRequired( LONG_NEGATIVE );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			bsMBean.isRequired( LONG_BIG );
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		super.waitForUnRegistering(createObjectName(PackageStateMBean.OBJECTNAME));
		if (testBundle1 != null) {
			try {
				super.uninstallBundle(testBundle1);
			} catch (Exception io) {}
		}
		if (testBundle2 != null) {
			try {
				super.uninstallBundle(testBundle2);
			} catch (Exception io) {}
		}
	}
}

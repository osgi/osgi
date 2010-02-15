package org.osgi.test.cases.jmx.junit;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.TabularData;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.jmx.framework.BundleStateMBean;
import org.osgi.jmx.framework.PackageStateMBean;

public class BundleStateMBeanTestCase extends MBeanGeneralTestCase {
	private Bundle testBundle1;
	private Bundle testBundle2;
	private BundleStateMBean bsMBean;
	
	private static String expectedInterface = "org.osgi.test.cases.jmx.tb2.api.HelloSayer";
	private static final String EXPORTED_PACKAGE = "org.osgi.test.cases.jmx.tb2.api";
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		testBundle2 = super.install("tb2.jar");
		testBundle2.start();
		
		testBundle1 = super.install("tb1.jar");
		testBundle1.start();
	
		super.waitForRegistering(createObjectName(BundleStateMBean.OBJECTNAME));
		bsMBean = getMBeanFromServer(BundleStateMBean.OBJECTNAME,
				BundleStateMBean.class);	
		
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
		/*
		 * TODO FIXME: https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1388
		 */
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
	
	public void testGetBundles() throws IOException {
		assertNotNull(bsMBean);
		TabularData bundleList = bsMBean.listBundles();
		assertTabularDataStructure(bundleList, "BUNDLES_TYPE", "Identifier", 
				new String[] {	"ExportedPackages", "Fragment", "Fragments", "Headers", "Hosts",
								"Identifier", "ImportedPackages", "LastModified", "Location", "PersistentlyStarted",
								"RegisteredServices", "RemovalPending", "Required", "RequiredBundles", "RequiringBundles", 
								"StartLevel", "State", "ServicesInUse", "SymbolicName", "Version"});
		
		Collection values = bundleList.values();
		Iterator iter = values.iterator();
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
					
					//add when 1388 bug is fixed 
					//assertFalse("wrong bundle required started info for tb1", required);
					
					//add when 1388 bug is fixed
					//assertTrue("wrong bundle required bundles for tb1 ", (requiredBundles.length == 1) && (requiredBundles[0] == testBundle2.getBundleId()));
					
					//add when 1388 bug is fixed					
					//assertTrue("wrong bundle requiring bundles for tb1", requiringBundles.length == 0);
					
					assertTrue("wrong bundle start level info", startLevel == bsMBean.getStartLevel(testBundle1.getBundleId()));
					
					assertTrue("wrong bundle state info", state.equals(bsMBean.getState(testBundle1.getBundleId())));
					
					assertTrue("wrong bundle services in use info", servicesInUse.length == 1);					
				} else {
					foundBundle2 = true;
					
					assertTrue("wrong bundle2 export packages info " + exportedPackages[0], (exportedPackages.length == 1) && (exportedPackages[0].startsWith(EXPORTED_PACKAGE)));
					
					assertTrue("wrong bundle last modified time", lastModified == testBundle2.getLastModified());
					
					assertTrue("wrong bundle location", location.equals(testBundle2.getLocation()));	
					
					assertTrue("wrong bundle registered services info", (registeredServices.length == 1));	
					
					assertTrue("wrong bundle required started info for tb2", required);
					
					//add when 1388 bug is fixed
					//assertTrue("wrong bundle required bundles for tb2", requiredBundles.length == 0);
					
					//add when 1388 bug is fixed					
					//assertTrue("wrong bundle requiring bundles for tb2", (requiringBundles.length == 1) && (requiringBundles[0] == testBundle1.getBundleId()));
					
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
				Iterator headersListIter = headersList.values().iterator();
				while (headersListIter.hasNext()) {
					String key = (String) ((CompositeData) headersListIter.next()).get("Key");
					if (key.equals("Bundle-Version") || key.equals("Bundle-Activator") || key.equals("Bundle-Vendor")) {
						foundKeys++;
					}
				}
				assertTrue("some header keys were not found", foundKeys == 3);
				
				Long[] hosts = (Long[])	item.get("Hosts");
				assertTrue("wrong bundle hosts info", fragments.length == 0);				
				
				assertTrue("wrong bundle persistently started info", ((Boolean) item.get("PersistentlyStarted")).booleanValue());
				
				assertFalse("wrong bundle removal pending started info", ((Boolean) item.get("RemovalPending")).booleanValue());
				
				String symbolicName = (String) item.get("SymbolicName");
				
				assertNotNull(symbolicName);
				
				String version = (String) item.get("Version");
				
				assertNotNull(version);
			}
		}
		assertTrue("listBundles() did not return data for some of the test bundles", foundBundle1 && foundBundle1);
	}

	public void testGetExportedPackages() throws IOException {
		assertNotNull(bsMBean);
		String expectedExportedPackages = "org.osgi.test.cases.jmx.tb2.api";
		
		
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
		String expectedImportedPackages = "org.osgi.test.cases.jmx.tb2.api";
		
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

	
	//TODO
	public void testGetHeaders() throws IOException {
		assertNotNull(bsMBean);
		TabularData headersList = (TabularData)	bsMBean.getHeaders(testBundle1.getBundleId());
		assertTabularDataStructure(headersList, "HEADERS_TYPE", "Key",	new String[] { "Key", "Value" });
		int foundKeys = 0;
		Iterator headersListIter = headersList.values().iterator();
		while (headersListIter.hasNext()) {
			String key = (String) ((CompositeData) headersListIter.next()).get("Key");
			if (key.equals("Bundle-Version") || key.equals("Bundle-Activator") || key.equals("Bundle-Vendor")) {
				foundKeys++;
			}
		}
		assertTrue("some header keys were not found", foundKeys == 3);
	}

	public void testGetRegisteredServices() throws IOException {
		long[] serviceIdentifiers = bsMBean.getRegisteredServices(testBundle2.getBundleId());
		assertTrue("testBundle2 defines one service", serviceIdentifiers.length == 1);
		ServiceReference ref = getContext().getServiceReference("org.osgi.test.cases.jmx.tb2.api.HelloSayer");
		Long expectedserviceId = (Long)ref.getProperty("service.id"); 
		assertTrue("testBundle2 service id is wrong", serviceIdentifiers[0] == expectedserviceId.longValue());
		
		serviceIdentifiers = bsMBean.getRegisteredServices(testBundle1.getBundleId());
		assertTrue("testBundle2 defines no services", serviceIdentifiers.length == 0);		 
	}

	public void testGetServicesInUse() throws IOException {
		assertNotNull(bsMBean);
		ServiceReference ref = getContext().getServiceReference("org.osgi.test.cases.jmx.tb2.api.HelloSayer");
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
		assertTrue("tb1 has wrong symbolic name", "tb1".equals(symbolicName));
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
	
	public void testIsPersistentlyStarted() throws IOException {
		boolean isPersistentlyStarted = bsMBean.isPersistentlyStarted(testBundle2.getBundleId());
		assertTrue(isPersistentlyStarted);		
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
	
	private void assertTabularDataStructure(TabularData td, String type, String key, String[] compositeDataKeys) {
		List<String> indexNames = td.getTabularType().getIndexNames();
		assertNotNull(indexNames);
		if (key != null) {
			assertTrue("tabular data " + type + " has wrong key set", indexNames.size() == 1);
			assertTrue("tabular data " + type + " doesn't contain key " + key, indexNames.iterator().next().equals(key));
		}
		CompositeType ct = td.getTabularType().getRowType();
		for (int i = 0; i < compositeDataKeys.length; i++) {
			assertTrue("tabular data row type " + type + " doesn't contain key " + compositeDataKeys[i], ct.containsKey(compositeDataKeys[i]));
		}
	}	
}
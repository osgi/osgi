package org.osgi.test.cases.jmx.junit;

import java.io.*;

import org.osgi.framework.*;
import org.osgi.jmx.framework.*;

public class BundleStateMBeanTestCase extends MBeanGeneralTestCase {
	private Bundle testBundle1;
	private Bundle testBundle2;
	private BundleStateMBean bsMBean;
	
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
		bsMBean.getRequiredBundles(testBundle1.getBundleId());
		boolean found = false; 
		for(long bundleId : bsMBean.getRequiredBundles(testBundle1.getBundleId())) {
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

	public void testGetBundles() throws IOException {
		assertNotNull(bsMBean);
		assertTrue("listBundles() did not return any data.", bsMBean.listBundles().size()> 0);
		/*
		 * TODO FIXME:  
		 * https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1386
		 */
		//Hashtable<String, Object> props = OSGiProperties.propertiesFrom(bsMBean.listBundles());
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
//
//	public void testGetFragments()  {
//		throw new UnsupportedOperationException("not yet implemented.");
//	}
//
	public void testGetHeaders() throws IOException {
		assertNotNull(bsMBean);
		assertTrue("getHeaders() did not return any headers", bsMBean.getHeaders(testBundle1.getBundleId()).size() > 0);
	}

//	public void testGetHosts() {
//		throw new UnsupportedOperationException("not yet implemented.");
//	}

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
//
//	public void testGetLastModified() {
//		throw new UnsupportedOperationException("not yet implemented.");
//	}
//
	public void testGetRegisteredServices() throws IOException {
		
//		 long[] serviceIdentifiers = bsMBean.getRegisteredServices(testBundle2.getBundleId());
//		 ServiceReference ref = getContext().getServiceReference("org.osgi.test.cases.jmx.tb2.api.HelloSayer");
		 
	}
//
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

//	public void testGetStartLevel() {
//		throw new UnsupportedOperationException("not yet implemented.");
//	}
//
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
//
	public void testGetSymbolicName() {
		assertNotNull(bsMBean);
		

	}
//
//	public void testIsPersistentlyStarted() {
//		throw new UnsupportedOperationException("not yet implemented.");
//	}
//	
	public void testIsFragment() throws IOException {
		assertNotNull(bsMBean);
		assertFalse("tb1 is a bundle and not a fragment.", bsMBean.isFragment(testBundle1.getBundleId()));
	}
//
	public void testIsRemovalPending() throws IOException {
		assertNotNull(bsMBean);
		assertTrue("removal is pending for testbundle1 which is not going to be removed ",!bsMBean.isRemovalPending(testBundle1.getBundleId()));
	}

	public void testIsRequired() throws IOException { 
		assertNotNull(bsMBean);
		assertTrue("tb2 is required by tb1, thus the isRequired call on tb2 should be true.", bsMBean.isRequired(testBundle2.getBundleId()));
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
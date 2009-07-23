package org.osgi.test.cases.jmx.junit;

import java.io.IOException;

import org.osgi.framework.Bundle;
import org.osgi.jmx.JmxConstants;
import org.osgi.jmx.framework.BundleStateMBean;

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
	
		super.waitForRegistering(createObjectName(JmxConstants.BUNDLE_STATE));
		bsMBean = getMBeanFromServer(JmxConstants.BUNDLE_STATE,
				BundleStateMBean.class);	
		
	}
	
	public void testGetDependencies() throws IOException {
		assertNotNull(bsMBean);

		//assuming that tb1 depends on tb2
		bsMBean.getDependencies(testBundle1.getBundleId());
		boolean found = false; 
		for(long bundleId : bsMBean.getDependencies(testBundle1.getBundleId())) {
			if(bundleId == testBundle2.getBundleId()) {
				found = true;
				break;
			}
		}
		/*
		 * FIXME: commented because of
		 * https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1388
		 */
//		assertTrue(
//				"testbundle1 depends on testbundle2. This dependency was not reflected "
//						+ "by the result of the call to getDependencies. result: "
//						+ bsMBean.getDependencies(testBundle1.getBundleId()),
//				found);
	}

	public void testGetBundles() throws IOException {
		assertNotNull(bsMBean);
		
		/*
		 * FIXME: commented because of 
		 * https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1386
		 */
//		Hashtable<String, Object> props = OSGiProperties.propertiesFrom(bsMBean.getBundles());
	}


//	public void testGetExportedPackages() {
//
//		throw new UnsupportedOperationException("not yet implemented.");
//	}
//
//	public void testGetFragments()  {
//		throw new UnsupportedOperationException("not yet implemented.");
//	}
//
//	public void testGetHeaders() {
//		throw new UnsupportedOperationException("not yet implemented.");
//		
//	}
//
//	public void testGetHosts() {
//		throw new UnsupportedOperationException("not yet implemented.");
//	}
//
//	public void testGetImportedPackages() {
//		throw new UnsupportedOperationException("not yet implemented.");
//	}
//
//	public void testGetLastModified() {
//		throw new UnsupportedOperationException("not yet implemented.");
//	}
//
//	public void testGetRegisteredServices()  {
//		throw new UnsupportedOperationException("not yet implemented.");
//	}
//
//	public void testGetRequiringBundles() {
//		throw new UnsupportedOperationException("not yet implemented.");
//	}
//
//
//	public void testGetServicesInUse() {
//		throw new UnsupportedOperationException("not yet implemented.");
//	}
//
//	public void testGetStartLevel() {
//		throw new UnsupportedOperationException("not yet implemented.");
//	}
//
//	public void testGetState() {
//		throw new UnsupportedOperationException("not yet implemented.");
//	}
//
//	public void testGetSymbolicName() {
//		throw new UnsupportedOperationException("not yet implemented.");
//	}
//
//	public void testIsPersistentlyStarted() {
//		throw new UnsupportedOperationException("not yet implemented.");
//	}
//	
//	public void testIsFragment() {
//		throw new UnsupportedOperationException("not yet implemented.");
//	}
//
//	public void isRemovalPending() {
//		throw new UnsupportedOperationException("not yet implemented.");
//	}
//
//	public void isRequired() { 
//		throw new UnsupportedOperationException("not yet implemented.");
//	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		super.waitForUnRegistering(createObjectName(JmxConstants.PACKAGE_STATE));
	}
}
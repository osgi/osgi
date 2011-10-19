/*
 * Copyright (c) OSGi Alliance (2000, 2010). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.residentialmanagement;

import org.osgi.framework.Bundle;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtSession;

/**
 * This test case checks the possible operations on the Framework subtree
 * This includes 
 * - get and set the Framework-Startlevel
 * - install and uninstall a bundle
 * - change the state of an installed undle
 * 
 * @author Steffen Druesedow (Deutsche Telekom Laboratories)
 */
public class FrameworkOperationsTestCase extends RMTTestBase {

	
	/**
	 * asserts that StartLevel values from the RMT are the same as the one retrieved from FrameworkStartLevel
	 * @throws Exception 
	 */
	public void testGetFrameworkStartLevels() throws Exception {

		FrameworkStartLevel fwStartLevel = (FrameworkStartLevel) getContext().getBundle(0).adapt(FrameworkStartLevel.class);
		session = dmtAdmin.getSession(FRAMEWORK_ROOT, DmtSession.LOCK_TYPE_SHARED);
		int startLevel = session.getNodeValue(FRAMEWORK_ROOT + "/" + STARTLEVEL ).getInt();
		int initialBundleStartLevel = session.getNodeValue(FRAMEWORK_ROOT + "/" + INITIAL_BUNDLE_STARTLEVEL ).getInt();
		
		assertEquals("The value of the StartLevel is incorrect.", fwStartLevel.getStartLevel(), startLevel);
		assertEquals("The value of the InitialBundleStartLevel is incorrect.", fwStartLevel.getInitialBundleStartLevel(), initialBundleStartLevel);

	}
	
	/**
	 * asserts that StartLevels can be set via the RMT
	 * @throws Exception 
	 */
	public void testSetFrameworkStartLevels() throws Exception {

		FrameworkStartLevel fwStartLevel = (FrameworkStartLevel) getContext().getBundle(0).adapt(FrameworkStartLevel.class);
		int initialStartLevel = fwStartLevel.getStartLevel();
		int initialBundleStartLevel = fwStartLevel.getInitialBundleStartLevel();
		
		// read value and perform changes in an atomic session
		session = dmtAdmin.getSession(FRAMEWORK_ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		assertNotNull(session);
		int rmtStartLevel = session.getNodeValue(FRAMEWORK_ROOT + "/" + STARTLEVEL ).getInt();
		int rmtInitialBundleStartLevel = session.getNodeValue(FRAMEWORK_ROOT + "/" + INITIAL_BUNDLE_STARTLEVEL ).getInt();
		
		assertEquals("The value of the StartLevel is incorrect.", initialStartLevel, rmtStartLevel);
		assertEquals("The value of the InitialBundleStartLevel is incorrect.", initialBundleStartLevel, rmtInitialBundleStartLevel);

		try {
			// set a new value for the startlevel
			session.setNodeValue(FRAMEWORK_ROOT + "/" + STARTLEVEL , new DmtData(initialStartLevel + 1));
			session.setNodeValue(FRAMEWORK_ROOT + "/" + INITIAL_BUNDLE_STARTLEVEL, new DmtData(initialBundleStartLevel + 1));
			
			session.commit();
			
			// check the new values in a new shared session
			session = dmtAdmin.getSession(FRAMEWORK_ROOT, DmtSession.LOCK_TYPE_SHARED);
			rmtStartLevel = session.getNodeValue(FRAMEWORK_ROOT + "/" + STARTLEVEL ).getInt();
			rmtInitialBundleStartLevel = session.getNodeValue(FRAMEWORK_ROOT + "/" + INITIAL_BUNDLE_STARTLEVEL ).getInt();
			
			assertEquals("The value of the Framework StartLevel was not set correctly.", fwStartLevel.getStartLevel(), initialStartLevel + 1);
			assertEquals("The value of the Framework initialBundleStartLevel was not set correctly.", fwStartLevel.getStartLevel(), initialStartLevel - 1);
		}
		finally {
			fwStartLevel.setStartLevel(initialStartLevel, null);
			fwStartLevel.setInitialBundleStartLevel(initialBundleStartLevel);
		}
	}
	
	
	/**
	 * tests installation and uninstallation of a bundle via RMT
	 * @throws Exception 
	 */
	public void testBundleInstallUninstall() throws Exception {
		final String BUNDLE_LOCATION = "TestBundle";
		final String BUNDLE_URL = getContext().getBundle().getEntry(TESTBUNDLE_EXPORTPACKAGE).toString();

		
		String uri = FRAMEWORK_ROOT + "/" + BUNDLE;
		session = dmtAdmin.getSession(uri, DmtSession.LOCK_TYPE_ATOMIC);
		assertNotNull(session);

		// ensure that no such bundle exists already in the RMT
		assertFalse( "TestBundle must not be in the bundle list initially!", session.isNodeUri(BUNDLE_LOCATION));
		session.createInteriorNode(BUNDLE_LOCATION);
		session.setNodeValue(BUNDLE_LOCATION + "/" + URL, new DmtData(BUNDLE_URL));
		session.setNodeValue(BUNDLE_LOCATION + "/" + AUTOSTART, new DmtData(true));
		session.setNodeValue(BUNDLE_LOCATION + "/" + REQUESTED_STATE, new DmtData("ACTIVE"));

		session.commit();
		
		// check that the bundle has been installed, there are no faults and the state is active
		session = dmtAdmin.getSession(uri, DmtSession.LOCK_TYPE_ATOMIC);
		assertEquals( session.getNodeValue(BUNDLE_LOCATION + "/" + FAULT_TYPE).getInt(), -1);
		assertEquals( session.getNodeValue(BUNDLE_LOCATION + "/" + FAULT_MESSAGE).getString(), "");
		assertEquals( "ACTIVE", session.getNodeValue(BUNDLE_LOCATION + "/" + STATE).getString());
		assertTrue( "TestBundle must have a bundle id after successfull installation!", session.isNodeUri(BUNDLE_LOCATION + "/" + BUNDLEID));
		assertTrue( "TestBundle must have a bundle-version after successfull installation!", session.isNodeUri(BUNDLE_LOCATION + "/" + VERSION));
		assertTrue( "TestBundle must have a SymbolicName after successfull installation!", session.isNodeUri(BUNDLE_LOCATION + "/" + SYMBOLIC_NAME));
		
		long id = session.getNodeValue(BUNDLE_LOCATION + "/" + BUNDLEID).getLong();
		
		// check real installation state
		testBundle1 = getContext().getBundle(id);
		assertNotNull("The testBundle was not installed successfully!", testBundle1 );
		assertEquals( "The testBundle should be in 'RESOLVED' state now.", Bundle.RESOLVED, testBundle1.getState());
		
		// uninstall bundle via RMT
		session.setNodeValue(BUNDLE_LOCATION + "/" + REQUESTED_STATE, new DmtData("UNINSTALLED"));
		session.commit();
		
		// check that the bundle has been uninstalled and there are no faults 
		session = dmtAdmin.getSession(uri, DmtSession.LOCK_TYPE_ATOMIC);
		assertEquals( session.getNodeValue(BUNDLE_LOCATION + "/" + FAULT_TYPE).getInt(), -1);
		assertEquals( session.getNodeValue(BUNDLE_LOCATION + "/" + FAULT_MESSAGE).getString(), "");
		assertEquals( "UNINSTALLED", session.getNodeValue(BUNDLE_LOCATION + "/" + STATE).getString());
		assertTrue( "TestBundle must not have a bundle id after successfull de-installation!", session.isNodeUri(BUNDLE_LOCATION + "/" + BUNDLEID));
		assertTrue( "TestBundle must not have a bundle-version after successfull de-installation!", session.isNodeUri(BUNDLE_LOCATION + "/" + VERSION));
		assertTrue( "TestBundle must not have a SymbolicName after successfull de-installation!", session.isNodeUri(BUNDLE_LOCATION + "/" + SYMBOLIC_NAME));
		
		session.commit();
		assertNull("The testBundle was not un-installed successfully!", getContext().getBundle(id) );
		
		// in case something failed, testBundle1 will be uninstalled in tearDown
		
	}
	
	
	/**
	 * tests state-changes of a bundle via the RMT (starting/stopping)
	 * @throws Exception 
	 */
	public void testBundleStateChanges() throws Exception {
		Bundle testBundle = installBundle(TESTBUNDLE_EXPORTPACKAGE, false);
		
		// ensure that the bundle is in INSTALLED state
		assertFalse( "testbundle should not be in 'ACTIVE' state initially", Bundle.ACTIVE == testBundle.getState() );
		
		String uri = FRAMEWORK_ROOT + "/" + BUNDLE;
		session = dmtAdmin.getSession(uri, DmtSession.LOCK_TYPE_ATOMIC);
		assertNotNull(session);
		
		// get the corresponding bundle entry in the RMT 
		String bundleBaseUri = null;
		String[] bundles = session.getChildNodeNames(uri);
		for (int i = 0; i < bundles.length; i++) {
			long id = session.getNodeValue(uri + "/" + bundles[i]).getLong();
			if ( id == testBundle.getBundleId() )
				bundleBaseUri = uri + "/" + bundles[i]; 
		}
		assertNotNull("Can't find the testBundle in the RMT bundle map!", bundleBaseUri );

		// check initial state of testBundle in the RMT
		assertEquals( "INSTALLED", session.getNodeValue(bundleBaseUri + "/" + STATE).getString());
		// attempt bundle resolving
		session.setNodeValue(bundleBaseUri + "/" + REQUESTED_STATE, new DmtData("RESOLVED"));
		session.commit();

		// check real bundle state
		assertEquals( "testBundle should be in 'RESOLVED' state now.", Bundle.RESOLVED, testBundle.getState());

		session = dmtAdmin.getSession(uri, DmtSession.LOCK_TYPE_ATOMIC);
		// check for Faults in previous commit
		assertEquals( session.getNodeValue(bundleBaseUri + "/" + FAULT_TYPE).getInt(), -1);
		assertEquals( session.getNodeValue(bundleBaseUri + "/" + FAULT_MESSAGE).getString(), "");
		// check state of testBundle in the RMT
		assertEquals( "RESOLVED", session.getNodeValue(bundleBaseUri + "/" + STATE).getString());
		// attempt bundle start
		session.setNodeValue(bundleBaseUri + "/" + REQUESTED_STATE, new DmtData("ACTIVE"));
		session.commit();
		
		// check real bundle state
		assertEquals( "testBundle should be in 'ACTIVE' state now.", Bundle.ACTIVE, testBundle.getState());
		
		// check bundle state in RMT now and stop it again via RMT
		session = dmtAdmin.getSession(uri, DmtSession.LOCK_TYPE_ATOMIC);
		// check for Faults in previous commit
		assertEquals( session.getNodeValue(bundleBaseUri + "/" + FAULT_TYPE).getInt(), -1);
		assertEquals( session.getNodeValue(bundleBaseUri + "/" + FAULT_MESSAGE).getString(), "");
		assertEquals( "ACTIVE", session.getNodeValue(bundleBaseUri + "/" + STATE).getString());
		session.setNodeValue(bundleBaseUri + "/" + REQUESTED_STATE, new DmtData("RESOLVED"));
		session.commit();
		
		// check real bundle state
		assertEquals( "testBundle should be in 'RESOLVED' state now.", Bundle.RESOLVED, testBundle.getState());

		// check bundle state in RMT again
		session = dmtAdmin.getSession(uri, DmtSession.LOCK_TYPE_SHARED);
		assertEquals( "RESOLVED", session.getNodeValue(bundleBaseUri + "/" + STATE).getString());
	}

}

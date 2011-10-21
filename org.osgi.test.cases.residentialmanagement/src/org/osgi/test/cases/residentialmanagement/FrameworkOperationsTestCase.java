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

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtSession;

/**
 * This test case checks the possible operations on the Framework subtree
 * This includes 
 * - get and set the Framework-Startlevel
 * - install and uninstall a bundle
 * - change the state of an installed bundle
 * 
 * @author Steffen Druesedow (Deutsche Telekom Laboratories)
 */
public class FrameworkOperationsTestCase extends RMTTestBase {

	
	/**
	 * asserts that StartLevel values from the RMT are the same as the one retrieved from FrameworkStartLevel
	 * @throws Exception 
	 */
	public void testGetFrameworkStartLevels() throws Exception {

		FrameworkStartLevel fwStartLevel = getContext().getBundle(0).adapt(FrameworkStartLevel.class);
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

		FrameworkStartLevel fwStartLevel = getContext().getBundle(0).adapt(FrameworkStartLevel.class);
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
			rmtStartLevel = session.getNodeValue(FRAMEWORK_ROOT + "/" + STARTLEVEL ).getInt();
			rmtInitialBundleStartLevel = session.getNodeValue(FRAMEWORK_ROOT + "/" + INITIAL_BUNDLE_STARTLEVEL ).getInt();
			
			assertEquals("The value of the Framework StartLevel was not set correctly.", fwStartLevel.getStartLevel(), initialStartLevel + 1);
			assertEquals("The value of the Framework initialBundleStartLevel was not set correctly.", fwStartLevel.getStartLevel(), initialStartLevel - 1);
		}
		finally {
			fwStartLevel.setStartLevel(initialStartLevel, (FrameworkListener[]) null);
			fwStartLevel.setInitialBundleStartLevel(initialBundleStartLevel);
		}
	}
	
	
	/**
	 * asserts that the properties of the bundles are correctly reflected in the RMT
	 * NOTE: This just tests the first level children of the bundle in the RMT, 
	 * deeper structures are tested in individual testcases.
	 * Only nodes are tested that can be directly matched against bundle properties (i.e. no RequestedState etc.). 
	 * @throws Exception
	 */
	public void testBundleFields() throws Exception {
		session = dmtAdmin.getSession(FRAMEWORK_ROOT, DmtSession.LOCK_TYPE_SHARED);
		assertNotNull(session);

		String uri = FRAMEWORK_ROOT + "/" + BUNDLE;
		String[] bundleKeys = session.getChildNodeNames(uri);
		for (String bundleKey : bundleKeys) {
			String bundleUri = uri + "/" + bundleKey;
			long id = session.getNodeValue(bundleUri + "/" + BUNDLEID).getLong();
			Bundle bundle = getContext().getBundle(id);

			String value = session.getNodeValue(bundleUri + "/" + STATE ).getString();
			assertEquals("The bundle state doesn't match for bundle: " + id, getBundleStateString(bundle.getState()), value );
			value = session.getNodeValue(bundleUri + "/" + SYMBOLIC_NAME ).getString();
			assertEquals("The bundles symbolic name doesn't match for bundle: " + id, bundle.getLocation(), value );
			value = session.getNodeValue(bundleUri + "/" + VERSION ).getString();
			assertEquals("The bundles version doesn't match for bundle: " + id, bundle.getVersion(), value );
			value = session.getNodeValue(bundleUri + "/" + STARTLEVEL ).getString();
			int startLevel = bundle.adapt(BundleStartLevel.class).getStartLevel();
			assertEquals("The bundles symbolic name doesn't match for bundle: " + id, startLevel, value );
			long rmtLastModified = session.getNodeValue(bundleUri + "/" + LAST_MODIFIED ).getDateTime().getTime();
			assertEquals("The bundles last modification timestamp name doesn't match for bundle: " + id, bundle.getLastModified(), rmtLastModified );
		}
	}
	
	/**
	 * Asserts that the BundleType structure is correctly filled.
	 * Currently only the FRAGMENT type is supported. Thats why a fragment bundle is installed first.
	 * For all other bundles the BundleType node is expected with an empty list.
	 * @throws Exception
	 */
	public void testBundleType() throws Exception {
		
		testBundle1 = installBundle(TESTBUNDLE_FRAGMENT, false);
		
		session = dmtAdmin.getSession(FRAMEWORK_ROOT, DmtSession.LOCK_TYPE_SHARED);
		assertNotNull(session);

		String uri = FRAMEWORK_ROOT + "/" + BUNDLE;
		String[] bundleKeys = session.getChildNodeNames(uri);
		for (String bundleKey : bundleKeys) {
			String bundleUri = uri + "/" + bundleKey;
			long id = session.getNodeValue( bundleUri + "/" + BUNDLEID ).getLong();
			boolean isFragment = getContext().getBundle(id).getHeaders().get("Fragment-Host") != null;
			assertTrue( "The BundleType node must exist for installed bundles.", session.isNodeUri(bundleUri+"/"+BUNDLETYPE));
			String[] types = session.getChildNodeNames(bundleUri + "/" + BUNDLETYPE );
			if ( isFragment ) {
				assertNotNull( "The list of BundleTypes must not be null for a fragment bundle.", types );
				assertEquals( "The list of BundleTypes must have exactly one entry for a fragment bundle.", 1, types.length );
				assertEquals( "The bundle type for a fragment bundle must be 'FRAGMENT'", "FRAGMENT", types[0]);
			}
			else {
				assertNotNull( "The list of BundleTypes must not be null.", types );
				assertEquals( "The list of BundleTypes must be empty for a non-fragment bundle.", 0, types.length );
			}
		}
	}
	
	/**
	 * asserts that the bundle headers are correctly reflected in the RMT 
	 * 
	 * @throws Exception 
	 * 
	 */
	public void testBundleHeaders() throws Exception {

		session = dmtAdmin.getSession(FRAMEWORK_ROOT, DmtSession.LOCK_TYPE_SHARED);
		assertNotNull(session);

		String uri = FRAMEWORK_ROOT + "/" + BUNDLE;
		String[] bundleKeys = session.getChildNodeNames(uri);
		for (String bundleKey : bundleKeys) {
			long id = session.getNodeValue(uri + "/" + bundleKey + "/" + BUNDLEID ).getLong();
			// get headers from real bundle
			Dictionary<String, String> realHeaders = getContext().getBundle(id).getHeaders();
			
			String headersUri = uri + "/" + bundleKey + "/" + HEADERS;
			String[] headers = session.getChildNodeNames(headersUri);
			for (String header : headers ) {
				String value = session.getNodeValue(headersUri + "/" + header).getString();
				assertTrue( "Header '"+header+"' exists in RMT but not in the bundle.", realHeaders.get(header) != null );
				assertEquals( "Values for header '"+header+"' are different in RMT and bundle.", realHeaders.get(header), value);
			} 
		}
	}
	
	/**
	 * This test asserts that the bundle-wiring is correctly reflected in the RMT
	 * The order of the Wires per bundle in the RMT is not guaranteed to be the same as in a Wiring-Api snapshot 
	 * and there is common identifier for a wire in RMT and API.
	 * Therefore the strategy is to loop through the RMT-Wires and to find matches in the API snapshot by doing a subtree comparision.
	 * The test is successful, if each RMT wire has exactly one matching counterpart in the snapshot. 
	 */
	public void testBundleWires() throws Exception {

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertNotNull(session);
		
		String uri = FRAMEWORK_ROOT + "/" + BUNDLE;
		String[] bundleKeys = session.getChildNodeNames(uri);
		for (String bundleKey : bundleKeys) {
			String bundleUri = uri + "/" + bundleKey;
			long id = session.getNodeValue( bundleUri + "/" + BUNDLEID ).getLong();

			String[] nameSpaces = session.getChildNodeNames( bundleUri + "/" + WIRES );
			for ( String nameSpace : nameSpaces ) {
				if ( ! "osgi.wiring.rmt.service".equals(nameSpace) )
					assertStandardNameSpaceTree(nameSpace, id, session, bundleUri + "/" + WIRES + "/" + nameSpace);
				else 
					assertServiceNameSpaceTree(nameSpace, id, session, bundleUri + "/" + WIRES + "/" + nameSpace);
			}
		}
	}

	
	/**
	 * tests installation and uninstallation of a bundle via RMT
	 * @throws Exception 
	 */
	public void testBundleInstallUninstall() throws Exception {
		final String BUNDLE_KEY = "TestBundle";
		final String BUNDLE_URL = getContext().getBundle().getEntry(TESTBUNDLE_EXPORTPACKAGE).toString();

		
		String uri = FRAMEWORK_ROOT + "/" + BUNDLE;
		session = dmtAdmin.getSession(uri, DmtSession.LOCK_TYPE_ATOMIC);
		assertNotNull(session);

		// --------- Install a bundle -----------
		// ensure that no such bundle exists already in the RMT
		assertFalse( "TestBundle must not be in the bundle list initially!", session.isNodeUri(BUNDLE_KEY));
		session.createInteriorNode(BUNDLE_KEY);
		session.setNodeValue(BUNDLE_KEY + "/" + URL, new DmtData(BUNDLE_URL));
		session.setNodeValue(BUNDLE_KEY + "/" + AUTOSTART, new DmtData(true));
		session.setNodeValue(BUNDLE_KEY + "/" + REQUESTED_STATE, new DmtData("ACTIVE"));

		session.commit();
		
		// check that the bundle has been installed, there are no faults and the state is active
		assertEquals( session.getNodeValue(BUNDLE_KEY + "/" + FAULT_TYPE).getInt(), -1);
		assertEquals( session.getNodeValue(BUNDLE_KEY + "/" + FAULT_MESSAGE).getString(), "");
		assertEquals( "ACTIVE", session.getNodeValue(BUNDLE_KEY + "/" + STATE).getString());
		assertTrue( "TestBundle must have a bundle id after successfull installation!", session.isNodeUri(BUNDLE_KEY + "/" + BUNDLEID));
		assertTrue( "TestBundle must have a bundle-version after successfull installation!", session.isNodeUri(BUNDLE_KEY + "/" + VERSION));
		assertTrue( "TestBundle must have a SymbolicName after successfull installation!", session.isNodeUri(BUNDLE_KEY + "/" + SYMBOLIC_NAME));
		
		long id = session.getNodeValue(BUNDLE_KEY + "/" + BUNDLEID).getLong();
		
		// check real installation state
		testBundle1 = getContext().getBundle(id);
		assertNotNull("The testBundle was not installed successfully!", testBundle1 );
		assertEquals( "The testBundle should be in 'ACTIVE' state now.", Bundle.ACTIVE, testBundle1.getState());
		
		// --------- Uninstall a bundle -----------
		// uninstall bundle via RMT
		session.setNodeValue(BUNDLE_KEY + "/" + REQUESTED_STATE, new DmtData("UNINSTALLED"));
		session.commit();
		
		// check that the bundle has been uninstalled and there are no faults 
		assertEquals( session.getNodeValue(BUNDLE_KEY + "/" + FAULT_TYPE).getInt(), -1);
		assertEquals( session.getNodeValue(BUNDLE_KEY + "/" + FAULT_MESSAGE).getString(), "");
		assertEquals( "UNINSTALLED", session.getNodeValue(BUNDLE_KEY + "/" + STATE).getString());
		assertTrue( "TestBundle must not have a bundle id after successfull de-installation!", session.isNodeUri(BUNDLE_KEY + "/" + BUNDLEID));
		assertTrue( "TestBundle must not have a bundle-version after successfull de-installation!", session.isNodeUri(BUNDLE_KEY + "/" + VERSION));
		assertTrue( "TestBundle must not have a SymbolicName after successfull de-installation!", session.isNodeUri(BUNDLE_KEY + "/" + SYMBOLIC_NAME));
		
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
		for (String bundle : bundles ) {
			long id = session.getNodeValue(uri + "/" + bundle).getLong();
			if ( id == testBundle.getBundleId() )
				bundleBaseUri = uri + "/" + bundle; 
		}
		assertNotNull("Can't find the testBundle in the RMT bundle map!", bundleBaseUri );

		// check initial state of testBundle in the RMT
		assertEquals( "INSTALLED", session.getNodeValue(bundleBaseUri + "/" + STATE).getString());
		// attempt bundle resolving
		session.setNodeValue(bundleBaseUri + "/" + REQUESTED_STATE, new DmtData("RESOLVED"));
		session.commit();

		// check real bundle state
		assertEquals( "testBundle should be in 'RESOLVED' state now.", Bundle.RESOLVED, testBundle.getState());

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

	// ************ Utility 
	
	/**
	 * This method performs a comparision of the wires for a given bundle and namespace.
	 * @param nameSpace ... the current namespace
	 * @param bundleId ... the bundle id to get the corresponding bundle for the Wiring-API check
	 * @param session ... the session to use
	 * @param nameSpaceUri ... the DMT uri of the bundles wiring namespace subtree
	 * @throws Exception
	 */
	private void assertStandardNameSpaceTree( String nameSpace, long bundleId, DmtSession session, String nameSpaceUri ) throws Exception {

		// first get a snapshot of the wiring for the given bundle 
		Bundle bundle = getContext().getBundle(bundleId);
		BundleWiring wiring = bundle.adapt(BundleWiring.class);
		
		// create a list that holds all wires (provided and required) for the current bundle 
		List<BundleWire> allApiWires = new ArrayList<BundleWire>();
		allApiWires.addAll(wiring.getProvidedWires(nameSpace));
		allApiWires.addAll(wiring.getRequiredWires(nameSpace));

		// get Wires from RMT
		String[] wires = session.getChildNodeNames(nameSpaceUri);
		for (String wire : wires ) {
			String wireUri = nameSpaceUri + "/" + wire;
			// check for match
			int matchIndex = matchWireTree(session, wireUri, nameSpace, allApiWires);
			if ( matchIndex >= 0 ) {
				pass("Found match for wire: " + wireUri );
				// remove matching wire
				allApiWires.remove(matchIndex);
			}
			else 
				fail("Found no matching wire in the wiring API snapshot for " + wireUri );
		}
		
		assertEquals("Did not find all Wires in RMT that are reported by the Wiring API!", 0, allApiWires.size());
		
	}
	
	
	private void assertServiceNameSpaceTree( String nameSpace, long bundleId, DmtSession session, String nameSpaceUri ) throws Exception {
		// TODO: implement this
	}

	/**
	 * This method tries to find a match for the given RMT wire subtree in the list of all wires from the API snapshot.
	 * @param session ... the session to access the RMT
	 * @param uri ... the root uri of the wire subtree
	 * @param nameSpace ... the current namespace
	 * @param allApiWires ... the list of wires from the API snapshot 
	 * @return the index of the matching API wire or -1, in case of no match
	 */
	private int matchWireTree(DmtSession session, String uri, String nameSpace, List<BundleWire> allApiWires) throws Exception {
		int index = -1;
		boolean match = false;
		for (BundleWire wire : allApiWires) {
			index++;
			String provider = wire.getProviderWiring().getBundle().getLocation();
			String requirer = wire.getRequirerWiring().getBundle().getLocation();

			String rmtProvider = session.getNodeValue(uri + "/" + PROVIDER ).getString();
			String rmtRequirer = session.getNodeValue(uri + "/" + REQUIRER ).getString();
			String rmtNameSpace = session.getNodeValue(uri + "/" + NAMESPACE ).getString();
			
			if ( ! provider.equals(rmtProvider) )
				continue;
			if ( ! requirer.equals(rmtRequirer) )
				continue;
			if ( ! nameSpace.equals(rmtNameSpace) )
				continue;

			// ******* REQUIREMENT part *********
			BundleRequirement requirement = wire.getRequirement();
			String reqUri = uri + "/" + REQUIREMENT;
			// directives
			Map<String, String> directivesMap = requirement.getDirectives();
			String[] children = session.getChildNodeNames( reqUri + "/" + DIRECTIVE);
			Map<String, String> rmtDirectivesMap = new HashMap<String, String>();
			for (String rmtKey : children)
				rmtDirectivesMap.put(rmtKey, session.getNodeValue(reqUri + "/" + DIRECTIVE + "/" + rmtKey ).getString());
			if ( ! directivesMap.equals(rmtDirectivesMap) ) 
				continue;
			
			// attributes
			Map<String, Object> attributeMap = requirement.getAttributes();
			children = session.getChildNodeNames(reqUri + "/" + ATTRIBUTE);
			Map<String, Object> rmtAttributeMap = new HashMap<String, Object>();
			for (String rmtKey : children)
				// we must add the object values as String, because they are also Strings in the RMT
				rmtAttributeMap.put(rmtKey, session.getNodeValue(reqUri + "/" + ATTRIBUTE + "/" + rmtKey ).getString());
			if ( ! attributeMap.equals(rmtAttributeMap) ) 
				continue;
			
			// FILTER
			// TODO: ?? Is this the correct source of the filter?
			// TODO: ?? Must filter still be present in the directive map?
			String filter = directivesMap.get(FILTER);
			String rmtFilter = session.getNodeValue(reqUri + "/" + FILTER ).getString();
			if ( ! filter.equals(rmtFilter) )
				continue;
			

			// ******* CAPABILITY part *********
			BundleCapability capability = wire.getCapability();
			String capUri = uri + "/" + CAPABILITY;
			// directives
			directivesMap = capability.getDirectives();
			children = session.getChildNodeNames(capUri + "/" + DIRECTIVE);
			rmtDirectivesMap = new HashMap<String, String>();
			for (String rmtKey : children)
				rmtDirectivesMap.put(rmtKey, session.getNodeValue(capUri + "/" + DIRECTIVE + "/" + rmtKey ).getString());
			if ( ! directivesMap.equals(rmtDirectivesMap) ) 
				continue;
			
			// attributes
			attributeMap = requirement.getAttributes();
			children = session.getChildNodeNames(capUri + "/" + ATTRIBUTE);
			rmtAttributeMap = new HashMap<String, Object>();
			for (String rmtKey : children)
				// we must add the object values as String, because they are also Strings in the RMT
				rmtAttributeMap.put(rmtKey, session.getNodeValue(capUri + "/" + ATTRIBUTE + "/" + rmtKey ).getString());
			if ( ! attributeMap.equals(rmtAttributeMap) ) 
				continue;

			// if we reach this point then we have a match
			match = true;
			break;
		}
		return match ? index : -1;
	}

	private void dumpTree(DmtSession session, String uri ) throws Exception {
		System.out.println( uri );
		if ( session.isLeafNode(uri) )
			System.out.println( ">>>" + session.getNodeValue(uri));
		else {
			String[] children = session.getChildNodeNames(uri);
			for (String child : children)
				dumpTree(session, uri + "/" + child );
		}
	}


}

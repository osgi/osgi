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
package org.osgi.test.cases.residentialmanagement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.Uri;
/**
 * This test case checks for the correct structure and metadata of the Framework tree.
 * It asserts that all mandatory nodes are there and compares the reported metadata and node types with the specified values.
 * 
 * @author Steffen Druesedow (Deutsche Telekom Laboratories)
 */
public class FrameworkStructureTestCase extends RMTTestBase {

	
	/**
	 * asserts that the Framework node has correct children
	 * @throws DmtException
	 */
	public void testFrameworkStructure() throws DmtException {
		String[] children;
		HashSet<String> mandatory = new HashSet<String>();
		HashSet<String> unknown = new HashSet<String>();

		session = dmtAdmin.getSession(FRAMEWORK_ROOT, DmtSession.LOCK_TYPE_SHARED);

		// 1st descendants
		children = session.getChildNodeNames(FRAMEWORK_ROOT);
		mandatory.add(STARTLEVEL);
		mandatory.add(INITIAL_BUNDLE_STARTLEVEL );
		mandatory.add(BUNDLE);
		mandatory.add(PROPERTY);
		for (String child : children) {
			if ( ! mandatory.contains(child))
				unknown.add(child);
			mandatory.remove(child);
		}
		assertEquals("There are children missing in the Framework node:" + unknown, 0,
				mandatory.size());
		assertEquals("There are unknown children in the Framework node: " + unknown, 0,
				unknown.size());
		

	}

	/**
	 * asserts that the Bundle subtree has the right structure
	 * @throws Exception 
	 */
	public void testBundleStructure() throws Exception {
		String[] children;
		HashSet<String> expected = new HashSet<String>();
		HashSet<String> optional = new HashSet<String>(); 

		Bundle[] bundles = getContext().getBundles();
		assertNotNull("This object should not be null.", bundles);
		
		for (Bundle bundle : bundles )
			expected.add(Uri.encode(bundle.getLocation()));

		session = dmtAdmin.getSession(FRAMEWORK_ROOT, DmtSession.LOCK_TYPE_SHARED);
		String[] bundleKeys = session.getChildNodeNames(FRAMEWORK_ROOT + "/" + BUNDLE );
		assertNotNull("These objects must exist.", bundleKeys);
		assertFalse("These objects must exist.", bundleKeys.length == 0);
		
		// compare keys of bundle map from context with the one from session
		for (String bundleKey : bundleKeys)
			expected.remove(bundleKey);
		assertEquals("Some nodes are missing in the bundle map.", 0, expected.size());

		optional = new HashSet<String>();
		optional.add(STATE);
		optional.add(FAULT_TYPE);
		optional.add(FAULT_MESSAGE);
		optional.add(BUNDLEID);
		optional.add(SYMBOLIC_NAME);
		optional.add(VERSION);
		optional.add(BUNDLETYPE);
		optional.add(HEADERS);
		optional.add(LOCATION);
		optional.add(REQUESTED_STATE);
		optional.add(LAST_MODIFIED);
		optional.add(WIRES);
		optional.add(SIGNERS);
		optional.add(ENTRIES);

		// children of: Framework.Bundle.[i]
		for (String bundleKey : bundleKeys) {
			List<String> undefined = new ArrayList<String>();
			expected = new HashSet<String>();
			expected.add(STARTLEVEL);
			expected.add(INSTANCEID);
			expected.add(URL_STRING);
			expected.add(AUTOSTART);

			children = session.getChildNodeNames(FRAMEWORK_ROOT + "/" + BUNDLE + "/" + bundleKey);
			assertNotNull("The bundle children must not be null for: "
					+ bundleKey, children);
			assertTrue("The bundle children must exist for: " + bundleKey,
					children.length > 0);
			for (String child : children) {
				if ( ! expected.contains(child) && ! optional.contains(child) )
					undefined.add(child);
				expected.remove(child);
			}

			assertEquals("Mandatory nodes are missing in Framework.Bundle." + bundleKey, 0,
					expected.size());
			
			assertEquals("There are un-specified nodes in Framework.Bundle." + bundleKey + ": " + undefined, 0,
					undefined.size());
		}
		
	}
	
	/**
	 * checks that the Bundle.Wires subtree has the right structure
	 * @throws Exception
	 */
	public void testBundleWiresStructure() throws Exception {
		testBundle1 = installAndStartBundle(TESTBUNDLE_EXPORTPACKAGE);
		testBundle2 = installAndStartBundle(TESTBUNDLE_IMPORTPACKAGE);
		testBundle3 = installBundle(TESTBUNDLE_FRAGMENT, false);
		testBundle4 = installAndStartBundle(TESTBUNDLE_REQUIRE);

		List<String> nameSpaces = new ArrayList<String>();
		nameSpaces.add(NAMESPACE_BUNLDE);
		nameSpaces.add(NAMESPACE_HOST);
		nameSpaces.add(NAMESPACE_PACKAGE);
		nameSpaces.add(NAMESPACE_RMT_SERVICE);
		
		session = dmtAdmin.getSession(FRAMEWORK_ROOT, DmtSession.LOCK_TYPE_SHARED);
		String[] bundleKeys = session.getChildNodeNames(FRAMEWORK_ROOT + "/" + BUNDLE );

		// Removed this check, because there can be extended namespaces as well.
		// These CT's only handles namespaces described in Residential spec. but should not fail 
		// on addional namespaces (see BUG 2402)
//		// check that only allowed namespaces are used as children of the Wires node
//		for (String bundleKey : bundleKeys) {
//			String uri = FRAMEWORK_ROOT + "/" + BUNDLE + "/" + bundleKey + "/" + WIRES;
//			String[] children = session.getChildNodeNames(uri);
//			assertTrue("These objects must exist.", children != null && children.length > 0 );
//			for (int k = 0; k < children.length; k++)
//				assertTrue( "The NameSpace is invalid: " + children[k], nameSpaces.contains(children[k]));
//						
//		}
		
		// check structure of the individual Wire nodes: 
		// Framework.Bundle.[i].Wires.NameSpace.xxx. children
		for (String bundleKey : bundleKeys) {
			// go through all known namespaces
			for ( String nameSpace : nameSpaces ) {
				
				String namespaceUri = FRAMEWORK_ROOT + "/" + BUNDLE + "/" + bundleKey + "/" + WIRES + "/" + nameSpace;
				// there must not necessarily be wires for each bundle in every known namespace
				if ( ! session.isNodeUri(namespaceUri) )
					continue; // with next namespace
				
				String[] listIndexes = session.getChildNodeNames(namespaceUri);
				
				for (String index : listIndexes) {
					Set<String> expected = new HashSet<String>();
					expected.add(NAMESPACE);
					expected.add(REQUIREMENT);
					expected.add(CAPABILITY);
					expected.add(REQUIRER);
					expected.add(PROVIDER);
					expected.add(INSTANCEID);

					String[] children = session.getChildNodeNames(namespaceUri + "/" + index);
					for (String child : children) 					
						expected.remove(child);
	
					assertEquals("Nodes are missing in " + namespaceUri + "/" + index, 0,
							expected.size());
	
				}
				// check children of Requirement node
				for (String index : listIndexes) { 
					Set<String> expected2 = new HashSet<String>();
					expected2.add(FILTER);
					expected2.add(DIRECTIVE);
					expected2.add(ATTRIBUTE);
					String[] children2 = session.getChildNodeNames(namespaceUri + "/" + index + "/" + REQUIREMENT);
					assertNotNull("The requirement children must not be null.",
							children2);
					assertTrue("The requirement children must exist.",
							children2.length > 0);
					for (String child2 : children2) 
						expected2.remove(child2);
	
					assertEquals("Nodes are missing in Framework.Bundle.[i].Wires." + namespaceUri + "/" + index + "/"+REQUIREMENT, 0,
							expected2.size());
				}
				// check children of Capability node
				for (String index : listIndexes) { 
					Set<String> expected2 = new HashSet<String>();
					expected2.add(DIRECTIVE);
					expected2.add(ATTRIBUTE);
					String[] children2 = session.getChildNodeNames(namespaceUri + "/" + index + "/" + CAPABILITY);
					assertNotNull("The capability children must not be null.",
							children2);
					assertTrue("The capability children must exist.",
							children2.length > 0);
					for (String child2 : children2) 
						expected2.remove(child2);
	
					assertEquals("Nodes are missing in Framework.Bundle.[i].Wires." + namespaceUri + "/" + index + "/"+CAPABILITY, 0,
							expected2.size());
				}
			}
		}
	}

	/**
	 * assert that the Bundle.Signers subtree has right structure
	 * @throws Exception
	 */
	public void testBundleSignersNodes() throws Exception {
		testBundle1 = installAndStartBundle(TESTBUNDLE_TRUSTED);
		testBundle2 = installAndStartBundle(TESTBUNDLE_NON_TRUSTED);

		String[] children;
		HashSet<String> expected = new HashSet<String>();
		
		session = dmtAdmin.getSession(FRAMEWORK_ROOT, DmtSession.LOCK_TYPE_SHARED);
		String[] bundleKeys = session.getChildNodeNames(FRAMEWORK_ROOT + "/" + BUNDLE );
		
		for (String bundleKey : bundleKeys) {
			String uri = FRAMEWORK_ROOT + "/" + BUNDLE + "/" + bundleKey;
			long bundleID = session.getNodeValue(uri + "/" + BUNDLEID).getLong();
			if ( bundleID == testBundle1.getBundleId() || bundleID == testBundle2.getBundleId() ) {
				String[] signers = session.getChildNodeNames( uri  + "/" + SIGNERS );
				for ( String signer : signers ) {
					children = session.getChildNodeNames(uri + "/" + SIGNERS + "/" + signer );
					expected = new HashSet<String>();
					expected.add(INSTANCEID);
					expected.add(ISTRUSTED);
					expected.add(CERTIFICATECHAIN);
					for (String child : children)
						expected.remove(child);
					assertEquals("Nodes are missing in Framework.Bundle.[i].Signers." + signer, 0,
							expected.size());
				}
			}
		}
	}
	
	/**
	 * assert that the Bundle-Entries node has the right structure
	 * @throws Exception
	 */
	public void testBundleEntries() throws Exception {

		String[] children;
		HashSet<String> expected = new HashSet<String>();
		
		session = dmtAdmin.getSession(FRAMEWORK_ROOT, DmtSession.LOCK_TYPE_SHARED);
		String[] bundleKeys = session.getChildNodeNames(FRAMEWORK_ROOT + "/" + BUNDLE );
		
		for (String bundleKey : bundleKeys) {
			String uri = FRAMEWORK_ROOT + "/" + BUNDLE + "/" + bundleKey + "/" + ENTRIES;
			String[] entries = session.getChildNodeNames( uri );
			for (String entry : entries) {
				children = session.getChildNodeNames(uri + "/" + entry );
				expected = new HashSet<String>();
				expected.add(INSTANCEID);
				expected.add(PATH);
				expected.add(CONTENT);
				for (int k = 0; k < children.length; k++)
					expected.remove(children[k]);
				assertEquals("Nodes are missing in Framework.Bundle.[i].Entries." + entry, 0,
						expected.size());
			}
		}
	}
	
	/**
	 * asserts that the Framework tree provides correct metadata and node types
	 * @throws Exception
	 */
	public void testMetaDataAndType() throws Exception {
		String[] children;
		testBundle1 = installAndStartBundle(TESTBUNDLE_EXPORTPACKAGE);
		testBundle2 = installAndStartBundle(TESTBUNDLE_IMPORTPACKAGE);
		testBundle3 = installBundle(TESTBUNDLE_FRAGMENT, false);
		testBundle4 = installAndStartBundle(TESTBUNDLE_REQUIRE);
		
		try {
			session = dmtAdmin.getSession(FRAMEWORK_ROOT, DmtSession.LOCK_TYPE_SHARED);
			assertNotNull(session);
			assertMetaData( FRAMEWORK_ROOT, false, "_G__", "1", MetaNode.PERMANENT, DmtData.FORMAT_NODE);
			assertMetaData( FRAMEWORK_ROOT + "/" + STARTLEVEL, true, "_GR_", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_INTEGER);
			assertMetaData( FRAMEWORK_ROOT + "/" + INITIAL_BUNDLE_STARTLEVEL, true, "_GR_", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_INTEGER);
			assertMetaData( FRAMEWORK_ROOT + "/" + BUNDLE, false, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_NODE);
			assertEquals( "The nodeType of the Framework.Bundle node must be " + DmtConstants.DDF_MAP, DmtConstants.DDF_MAP, session.getNodeType(FRAMEWORK_ROOT + "/" + BUNDLE));
	
			String[] bundleIds = session.getChildNodeNames(FRAMEWORK_ROOT + "/" + BUNDLE);
			for (String bundleId : bundleIds) {
				String uri = FRAMEWORK_ROOT + "/" + BUNDLE + "/" + bundleId;
				assertMetaData( uri, false, "AG__", "0..*", MetaNode.DYNAMIC, DmtData.FORMAT_NODE);
				
				uri+= "/";
				assertMetaData( uri + STATE, 	  	true, "_G__", "0,1", MetaNode.AUTOMATIC, DmtData.FORMAT_STRING);
				assertMetaData( uri + STARTLEVEL, 	true, "_GR_", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_INTEGER);
				assertMetaData( uri + INSTANCEID, 	true, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_INTEGER);
				assertMetaData( uri + URL_STRING, 		  	true, "_GR_", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_STRING);
				assertMetaData( uri + AUTOSTART,  	true, "_GR_", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_BOOLEAN);
				assertMetaData( uri + FAULT_TYPE, 	true, "_G__", "0,1", MetaNode.AUTOMATIC, DmtData.FORMAT_INTEGER);
				assertMetaData( uri + FAULT_MESSAGE,true, "_G__", "0,1", MetaNode.AUTOMATIC, DmtData.FORMAT_STRING);
				assertMetaData( uri + BUNDLEID,   	true, "_G__", "0,1", MetaNode.AUTOMATIC, DmtData.FORMAT_LONG);
				assertMetaData( uri + SYMBOLIC_NAME,true, "_G__", "0,1", MetaNode.AUTOMATIC, DmtData.FORMAT_STRING);
				assertMetaData( uri + VERSION, 	  	true, "_G__", "0,1", MetaNode.AUTOMATIC, DmtData.FORMAT_STRING);
				assertMetaData( uri + BUNDLETYPE, 	false,"_G__", "0,1", MetaNode.AUTOMATIC, DmtData.FORMAT_NODE);
				assertEquals( "The nodeType of 'BundleType' node must be " + DmtConstants.DDF_LIST, DmtConstants.DDF_LIST, session.getNodeType(uri + BUNDLETYPE));
				// plugin must return valid metadata also for non-existing nodes (will not check every map element)
				assertMetaData( uri + BUNDLETYPE + "/<>", 	true,"_G__", "0..*", MetaNode.DYNAMIC, DmtData.FORMAT_STRING);
				assertMetaData( uri + HEADERS, 		false,"_G__", "0,1", MetaNode.AUTOMATIC, DmtData.FORMAT_NODE);
				assertEquals( "The nodeType of 'Headers' node must be " + DmtConstants.DDF_MAP, DmtConstants.DDF_MAP, session.getNodeType(uri + HEADERS));
				assertMetaData( uri + HEADERS + "/<>", true,"_G__", "0..*", MetaNode.DYNAMIC, DmtData.FORMAT_STRING);
				assertMetaData( uri + LAST_MODIFIED, true, "_G__", "0,1", MetaNode.AUTOMATIC, DmtData.FORMAT_DATE_TIME);
				
				String uriWires = uri + WIRES;
				assertMetaData( uriWires, false,"_G__", "0,1", MetaNode.AUTOMATIC, DmtData.FORMAT_NODE);
				assertEquals( "The nodeType of 'Wires' node must be " + DmtConstants.DDF_MAP, DmtConstants.DDF_MAP, session.getNodeType(uri + WIRES));
				children = session.getChildNodeNames(uriWires);
				for (String nameSpace : children) {
					assertMetaData(uriWires + "/" + nameSpace, false, "_G__", "0..*", MetaNode.DYNAMIC, DmtData.FORMAT_NODE);
					assertEquals( "The nodeType must be " + DmtConstants.DDF_LIST + " for uri: " + uriWires + "/" + nameSpace, DmtConstants.DDF_LIST, session.getNodeType(uriWires + "/" + nameSpace));
					String[] wires = session.getChildNodeNames(uriWires + "/" + nameSpace);
					for (String wire : wires) {
						String uriWire = uriWires + "/" + nameSpace + "/" + wire; 
						// bugfix for 
						//assertMetaData( uriWire, false, "_G__", "0..*", MetaNode.DYNAMIC, DmtData.FORMAT_NODE);
						//assertEquals( "The nodeType must be " + DmtConstants.DDF_LIST + " for uri: " + uriWire, DmtConstants.DDF_LIST, session.getNodeType(uriWire));
						assertMetaData( uriWire, false, "_G__", "0..*", MetaNode.DYNAMIC, DmtData.FORMAT_NODE);
						assertMetaData( uriWire + "/" + PROVIDER, true, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_STRING);
						assertMetaData( uriWire + "/" + INSTANCEID, true, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_INTEGER);
						assertMetaData( uriWire + "/" + NAMESPACE, true, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_STRING);
						assertMetaData( uriWire + "/" + REQUIREMENT, false, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_NODE);
						assertMetaData( uriWire + "/" + REQUIREMENT + "/" + FILTER, true, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_STRING);
						assertMetaData( uriWire + "/" + REQUIREMENT + "/" + DIRECTIVE, false, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_NODE);
						assertMetaData( uriWire + "/" + REQUIREMENT + "/" + DIRECTIVE + "/<>", true, "_G__", "0..*", MetaNode.DYNAMIC, DmtData.FORMAT_STRING);
						assertMetaData( uriWire + "/" + REQUIREMENT + "/" + ATTRIBUTE, false, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_NODE);
						assertMetaData( uriWire + "/" + REQUIREMENT + "/" + ATTRIBUTE + "/<>", true, "_G__", "0..*", MetaNode.DYNAMIC, DmtData.FORMAT_STRING);
						assertMetaData( uriWire + "/" + CAPABILITY, false, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_NODE);
						assertMetaData( uriWire + "/" + CAPABILITY + "/" + DIRECTIVE, false, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_NODE);
						assertMetaData( uriWire + "/" + CAPABILITY + "/" + DIRECTIVE + "/<>", true, "_G__", "0..*", MetaNode.DYNAMIC, DmtData.FORMAT_STRING);
						assertMetaData( uriWire + "/" + CAPABILITY + "/" + ATTRIBUTE, false, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_NODE);
						assertMetaData( uriWire + "/" + CAPABILITY + "/" + ATTRIBUTE + "/<>", true, "_G__", "0..*", MetaNode.DYNAMIC, DmtData.FORMAT_STRING);

						String nodeType = session.getNodeType(uriWire + "/" + REQUIREMENT + "/" + DIRECTIVE);
						assertEquals( "The nodeType must be " + DmtConstants.DDF_MAP + " for uri: " + uriWire + "/" + REQUIREMENT + "/" + DIRECTIVE, DmtConstants.DDF_MAP, nodeType);
						nodeType = session.getNodeType(uriWire + "/" + REQUIREMENT + "/" + ATTRIBUTE);
						assertEquals( "The nodeType must be " + DmtConstants.DDF_MAP + " for uri: " + uriWire + "/" + REQUIREMENT + "/" + ATTRIBUTE, DmtConstants.DDF_MAP, nodeType);
						nodeType = session.getNodeType(uriWire + "/" + CAPABILITY + "/" + DIRECTIVE);
						assertEquals( "The nodeType must be " + DmtConstants.DDF_MAP + " for uri: " + uriWire + "/" + CAPABILITY + "/" + DIRECTIVE, DmtConstants.DDF_MAP, nodeType);
						nodeType = session.getNodeType(uriWire + "/" + CAPABILITY + "/" + ATTRIBUTE);
						assertEquals( "The nodeType must be " + DmtConstants.DDF_MAP + " for uri: " + uriWire + "/" + CAPABILITY + "/" + ATTRIBUTE, DmtConstants.DDF_MAP, nodeType);
					}
				}
				String uriSigners = uri + SIGNERS;
				assertMetaData( uriSigners, false,"_G__", "0,1", MetaNode.AUTOMATIC, DmtData.FORMAT_NODE);
				assertEquals( "The nodeType of 'Signers' node must be " + DmtConstants.DDF_LIST, DmtConstants.DDF_LIST, session.getNodeType(uri + SIGNERS));
				children = session.getChildNodeNames(uriSigners);
				for (String signer : children) {
					String uriSigner = uriSigners + "/" + signer;
					assertMetaData( uriSigner, false, "_G__", "0..*", MetaNode.DYNAMIC, DmtData.FORMAT_NODE);
					assertMetaData( uriSigner + "/" + INSTANCEID, true, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_INTEGER);
					assertMetaData( uriSigner + "/" + ISTRUSTED,  true, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_BOOLEAN);
					assertMetaData( uriSigner + "/" + CERTIFICATECHAIN, false, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_NODE);
					assertMetaData( uriSigner + "/" + CERTIFICATECHAIN + "/<>", true, "_G__", "0..*", MetaNode.DYNAMIC, DmtData.FORMAT_STRING);
					String nodeType = session.getNodeType(uriSigner + "/" + CERTIFICATECHAIN);
					assertEquals( "The nodeType must be " + DmtConstants.DDF_LIST + " for uri: " + uriSigner + "/" + CERTIFICATECHAIN, DmtConstants.DDF_LIST, nodeType);
				}
				String uriEntries = uri + ENTRIES;
				assertMetaData( uriEntries, false,"_G__", "0,1", MetaNode.AUTOMATIC, DmtData.FORMAT_NODE);
				assertEquals( "The nodeType of 'Entries' node must be " + DmtConstants.DDF_LIST, DmtConstants.DDF_LIST, session.getNodeType(uriEntries));
				children = session.getChildNodeNames(uriEntries);
				for (String entry : children) {
					String uriEntry = uriEntries + "/" + entry;
					assertMetaData( uriEntry, false, "_G__", "0..*", MetaNode.DYNAMIC, DmtData.FORMAT_NODE);
					assertMetaData( uriEntry + "/" + INSTANCEID, true, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_INTEGER);
					assertMetaData( uriEntry + "/" + PATH,  true, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_STRING);
					assertMetaData( uriEntry + "/" + CONTENT, true, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_BINARY);
				}
				assertMetaData( FRAMEWORK_ROOT + "/" + PROPERTY, false, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_NODE);
				assertMetaData( FRAMEWORK_ROOT + "/" + PROPERTY + "/<>", true, "_G__", "0..*", MetaNode.DYNAMIC, DmtData.FORMAT_STRING);
				assertEquals( "The nodeType of 'Property' node must be " + DmtConstants.DDF_MAP, DmtConstants.DDF_MAP, session.getNodeType(FRAMEWORK_ROOT + "/" + PROPERTY));
			}
		} catch (DmtException de) {
			de.printStackTrace();
			fail("unexpeced DmtException: " + de.getMessage());
		}
	}

}

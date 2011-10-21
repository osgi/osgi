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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
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
		HashSet mandatory;

		session = dmtAdmin.getSession(FRAMEWORK_ROOT, DmtSession.LOCK_TYPE_SHARED);

		// 1st descendants
		children = session.getChildNodeNames(FRAMEWORK_ROOT);
		mandatory = new HashSet();
		mandatory.add(STARTLEVEL);
		mandatory.add(INITIAL_BUNDLE_STARTLEVEL );
		mandatory.add(BUNDLE);
		mandatory.add(PROPERTY);
		for (int i = 0; i < children.length; i++)
			mandatory.remove(children[i]);
		assertEquals("There are undefined children in the Framework node.", 0,
				mandatory.size());

	}

	/**
	 * asserts that the Bundle subtree has the right structure
	 * @throws DmtException
	 */
	public void testBundleStructure() throws Exception {
		String[] children;
		HashSet expected = new HashSet();
		HashSet optional = new HashSet(); 

		Bundle[] bundles = getContext().getBundles();
		assertNotNull("This object should not be null.", bundles);
		
		for (int i = 0; i < bundles.length; i++)
			expected.add(bundles[i].getLocation());

		session = dmtAdmin.getSession(FRAMEWORK_ROOT, DmtSession.LOCK_TYPE_SHARED);
		String[] bundleKeys = session.getChildNodeNames(FRAMEWORK_ROOT + "/" + BUNDLE );
		assertNotNull("These objects must exist.", bundleKeys);
		assertFalse("These objects must exist.", bundleKeys.length == 0);
		
		// compare keys of bundle map from context with the one from session
		for (int j = 0; j < bundleKeys.length; j++)
			expected.remove(bundleKeys[j]);
		assertEquals("Some nodes are missing in the bundle map.", 0, expected.size());

		optional = new HashSet();
		optional.add(STATE);
		optional.add(FAULT_TYPE);
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
		for (int i = 0; i < bundleKeys.length; i++) {
			String bundleKey = bundleKeys[i];
			List undefined = new ArrayList();
			expected = new HashSet();
			expected.add(STARTLEVEL);
			expected.add(INSTANCEID);
			expected.add(URL);
			expected.add(AUTOSTART);

			children = session.getChildNodeNames(FRAMEWORK_ROOT + "/" + BUNDLE + "/" + bundleKey);
			assertTrue("These objects must exist.", children != null && children.length > 0 );
			for (int j = 0; j < children.length; j++) {
				String child = children[j];
				expected.remove(child);
				if ( ! expected.contains(child) && ! optional.contains(child) )
					undefined.add(child);
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

		List nameSpaces = new ArrayList();
		nameSpaces.add(NAMESPACE_BUNLDE);
		nameSpaces.add(NAMESPACE_HOST);
		nameSpaces.add(NAMESPACE_PACKAGE);
		nameSpaces.add(NAMESPACE_RMT_SERVICE);
		
		session = dmtAdmin.getSession(FRAMEWORK_ROOT, DmtSession.LOCK_TYPE_SHARED);
		String[] bundleKeys = session.getChildNodeNames(FRAMEWORK_ROOT + "/" + BUNDLE );
		
		// check that only allowed namespaces are used as children of the Wires node
		for (int j = 0; j < bundleKeys.length; j++) {
			String bundleKey = bundleKeys[j];
			String uri = FRAMEWORK_ROOT + "/" + BUNDLE + "/" + bundleKey + "/" + WIRES;
			String[] children = session.getChildNodeNames(uri);
			assertTrue("These objects must exist.", children != null && children.length > 0 );
			for (int k = 0; k < children.length; k++)
				assertTrue( "The NameSpace is invalid: " + children[k], nameSpaces.contains(children[k]));
						
		}
		
		// check structure of the individual Wire nodes: 
		// Framework.Bundle.[i].Wires.NameSpace.xxx. children
		for (int i = 0; i < bundleKeys.length; i++) {
			String bundleKey = bundleKeys[i];
			// go through all namespaces
			Iterator iterator = nameSpaces.iterator();
			while (iterator.hasNext()) {
				String nameSpace = (String) iterator.next();
				Set expected = new HashSet();
				expected.add(NAMESPACE);
				expected.add(REQUIREMENT);
				expected.add(CAPABILITY);
				expected.add(REQUIRER);
				expected.add(PROVIDER);
				expected.add(INSTANCEID);
				
				String uri = FRAMEWORK_ROOT + "/" + BUNDLE + "/" + bundleKey + "/" + WIRES + "/" + nameSpace;
				String[] children = session.getChildNodeNames(uri);
				assertTrue("These objects must exist.", children != null && children.length > 0 );
				for (int j = 0; j < children.length; j++) 
					expected.remove(children[j]);

				assertEquals("Nodes are missing in Framework.Bundle.[i].Wires." +nameSpace+ "/" + children[i], 0,
						expected.size());

				// check children of Requirement node
				for (int k = 0; k < children.length; k++) {
					String child = children[k];
					Set expected2 = new HashSet();
					expected2.add(FILTER);
					expected2.add(DIRECTIVE);
					expected2.add(ATTRIBUTE);
					String[] children2 = session.getChildNodeNames(uri + "/" + child + "/" + REQUIREMENT);
					assertTrue("These objects must exist.", children2 != null && children2.length > 0 );
					for (int l = 0; l < children2.length; l++)
						expected2.remove(children2[l]);
	
					assertEquals("Nodes are missing in Framework.Bundle.[i].Wires." + nameSpace +"/"+ child + "/"+REQUIREMENT, 0,
							expected.size());
				}
				// check children of Capability node
				for (int l = 0; l < children.length; l++) {
					String child = children[l];
					Set expected2 = new HashSet();
					expected2.add(DIRECTIVE);
					expected2.add(ATTRIBUTE);
					String[] children2 = session.getChildNodeNames(uri + "/" + child + "/" + CAPABILITY);
					assertTrue("These objects must exist.", children2 != null && children2.length > 0 );
					for (int m = 0; m < children2.length; m++)
						expected2.remove(children2[m]);
	
					assertEquals("Nodes are missing in Framework.Bundle.[i].Wires." + nameSpace +"/"+ child + "/"+CAPABILITY, 0,
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
		HashSet expected = new HashSet();
		
		session = dmtAdmin.getSession(FRAMEWORK_ROOT, DmtSession.LOCK_TYPE_SHARED);
		String[] bundleKeys = session.getChildNodeNames(FRAMEWORK_ROOT + "/" + BUNDLE );
		
		for (int i = 0; i < bundleKeys.length; i++) {
			String uri = FRAMEWORK_ROOT + "/" + BUNDLE + "/" + bundleKeys[i];
			long bundleID = ((DmtData) session.getNodeValue(uri + "/" + BUNDLEID)).getLong();
			if ( bundleID == testBundle1.getBundleId() || bundleID == testBundle2.getBundleId() ) {
				String[] signers = session.getChildNodeNames( uri  );
				for (int j = 0; j < signers.length; j++) {
					String signer = signers[j];
					children = session.getChildNodeNames(uri + "/" + SIGNERS + "/" + signer );
					expected = new HashSet();
					expected.add(INSTANCEID);
					expected.add(ISTRUSTED);
					expected.add(CERTIFICATECHAIN);
					for (int k = 0; k < children.length; k++)
						expected.remove(children[k]);
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
		HashSet expected = new HashSet();
		
		session = dmtAdmin.getSession(FRAMEWORK_ROOT, DmtSession.LOCK_TYPE_SHARED);
		String[] bundleKeys = session.getChildNodeNames(FRAMEWORK_ROOT + "/" + BUNDLE );
		
		for (int i = 0; i < bundleKeys.length; i++) {
			String uri = FRAMEWORK_ROOT + "/" + BUNDLE + "/" + bundleKeys[i] + "/" + ENTRIES;
			String[] entries = session.getChildNodeNames( uri );
			for (int j = 0; j < entries.length; j++) {
				String entry = entries[j];
				children = session.getChildNodeNames(uri + "/" + entry );
				expected = new HashSet();
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
		testBundle3 = installAndStartBundle(TESTBUNDLE_FRAGMENT);
		testBundle4 = installAndStartBundle(TESTBUNDLE_REQUIRE);
		
		try {

			assertMetaData( FRAMEWORK_ROOT, false, "_G__", "1", MetaNode.PERMANENT, DmtData.FORMAT_NODE);
			assertMetaData( FRAMEWORK_ROOT + "/" + STARTLEVEL, true, "_GR_", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_NODE);
			assertMetaData( FRAMEWORK_ROOT + "/" + INITIAL_BUNDLE_STARTLEVEL, true, "_GR_", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_NODE);
			assertMetaData( FRAMEWORK_ROOT + "/" + BUNDLE, false, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_NODE);
			assertEquals( "The nodeType of the Framework.Bundle node must be " + DmtConstants.DDF_MAP, DmtConstants.DDF_MAP, session.getNodeType(FRAMEWORK_ROOT + "/" + BUNDLE));
	
			session = dmtAdmin.getSession(FRAMEWORK_ROOT, DmtSession.LOCK_TYPE_SHARED);
			String[] bundleIds = session.getChildNodeNames(FRAMEWORK_ROOT + "/" + BUNDLE);
			for (int i = 0; i < bundleIds.length; i++) {
				String uri = FRAMEWORK_ROOT + "/" + BUNDLE + "/" + bundleIds[i];
				assertMetaData( uri, false, "AG__", "0..*", MetaNode.DYNAMIC, DmtData.FORMAT_NODE);
				
				uri+= "/";
				assertMetaData( uri + STATE, 	  	true, "_G__", "0,1", MetaNode.AUTOMATIC, DmtData.FORMAT_STRING);
				assertMetaData( uri + STARTLEVEL, 	true, "_GR_", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_INTEGER);
				assertMetaData( uri + INSTANCEID, 	true, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_INTEGER);
				assertMetaData( uri + URL, 		  	true, "_GR_", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_STRING);
				assertMetaData( uri + AUTOSTART,  	true, "_GR_", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_BOOLEAN);
				assertMetaData( uri + FAULT_TYPE, 	true, "_G__", "0,1", MetaNode.AUTOMATIC, DmtData.FORMAT_INTEGER);
				assertMetaData( uri + FAULT_MESSAGE,true, "_G__", "0,1", MetaNode.AUTOMATIC, DmtData.FORMAT_STRING);
				assertMetaData( uri + BUNDLEID,   	true, "_G__", "0,1", MetaNode.AUTOMATIC, DmtData.FORMAT_LONG);
				assertMetaData( uri + SYMBOLIC_NAME,true, "_G__", "0,1", MetaNode.AUTOMATIC, DmtData.FORMAT_STRING);
				assertMetaData( uri + VERSION, 	  	true, "_G__", "0,1", MetaNode.AUTOMATIC, DmtData.FORMAT_STRING);
				assertMetaData( uri + BUNDLETYPE, 	false,"_GR_", "0,1", MetaNode.AUTOMATIC, DmtData.FORMAT_NODE);
				assertEquals( "The nodeType of 'BundleType' node must be " + DmtConstants.DDF_LIST, DmtConstants.DDF_LIST, session.getNodeType(uri + BUNDLETYPE));
				// plugin must return valid metadata also for non-existing nodes (will not check every map element)
				assertMetaData( uri + BUNDLETYPE + "/<>", 	true,"_G__", "0..*", MetaNode.DYNAMIC, DmtData.FORMAT_STRING);
				assertMetaData( uri + HEADERS, 		false,"_G__", "0,1", MetaNode.AUTOMATIC, DmtData.FORMAT_NODE);
				assertEquals( "The nodeType of 'Headers' node must be " + DmtConstants.DDF_MAP, DmtConstants.DDF_MAP, session.getNodeType(uri + HEADERS));
				assertMetaData( uri + HEADERS + "/<>", true,"_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_STRING);
				assertMetaData( uri + LAST_MODIFIED, true, "_G__", "0,1", MetaNode.AUTOMATIC, DmtData.FORMAT_DATE_TIME);
				
				String uriWires = uri + WIRES;
				assertMetaData( uriWires, false,"_G__", "0,1", MetaNode.AUTOMATIC, DmtData.FORMAT_NODE);
				assertEquals( "The nodeType of 'Wires' node must be " + DmtConstants.DDF_MAP, DmtConstants.DDF_MAP, session.getNodeType(uri + WIRES));
				children = session.getChildNodeNames(uriWires);
				for (int j = 0; j < children.length; j++) {
					String namespace = children[j];
					assertMetaData(uriWires + "/" + namespace, false, "_G__", "0..*", MetaNode.DYNAMIC, DmtData.FORMAT_NODE);
					String[] wires = session.getChildNodeNames(uriWires + "/" + namespace);
					for (int k = 0; k < wires.length; k++) {
						String wire = wires[k];
						String uriWire = uriWires + "/" + namespace + "/" + wire; 
						assertMetaData( uriWire, false, "_G__", "0..*", MetaNode.DYNAMIC, DmtData.FORMAT_NODE);
						assertEquals( "The nodeType must be " + DmtConstants.DDF_LIST + " for uri: " + uriWire, DmtConstants.DDF_LIST, session.getNodeType(uriWire));
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
						assertEquals( "The nodeType must be " + DmtConstants.DDF_MAP + " for uri: " + uriWire + "/" + REQUIREMENT + "/" + DIRECTIVE, DmtConstants.DDF_LIST, nodeType);
						nodeType = session.getNodeType(uriWire + "/" + REQUIREMENT + "/" + ATTRIBUTE);
						assertEquals( "The nodeType must be " + DmtConstants.DDF_MAP + " for uri: " + uriWire + "/" + REQUIREMENT + "/" + ATTRIBUTE, DmtConstants.DDF_LIST, nodeType);
						nodeType = session.getNodeType(uriWire + "/" + CAPABILITY + "/" + DIRECTIVE);
						assertEquals( "The nodeType must be " + DmtConstants.DDF_MAP + " for uri: " + uriWire + "/" + CAPABILITY + "/" + DIRECTIVE, DmtConstants.DDF_LIST, nodeType);
						nodeType = session.getNodeType(uriWire + "/" + CAPABILITY + "/" + ATTRIBUTE);
						assertEquals( "The nodeType must be " + DmtConstants.DDF_MAP + " for uri: " + uriWire + "/" + CAPABILITY + "/" + ATTRIBUTE, DmtConstants.DDF_LIST, nodeType);
					}
				}
				String uriSigners = uri + SIGNERS;
				assertMetaData( uriSigners, false,"_G__", "0,1", MetaNode.AUTOMATIC, DmtData.FORMAT_NODE);
				assertEquals( "The nodeType of 'Signers' node must be " + DmtConstants.DDF_LIST, DmtConstants.DDF_LIST, session.getNodeType(uri + SIGNERS));
				children = session.getChildNodeNames(uriSigners);
				for (int j = 0; j < children.length; j++) {
					String signer = children[j];
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
				for (int j = 0; j < children.length; j++) {
					String entry = children[j];
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

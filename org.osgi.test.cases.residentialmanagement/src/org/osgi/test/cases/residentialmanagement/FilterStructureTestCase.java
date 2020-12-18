/*
 * Copyright (c) OSGi Alliance (2000, 2020). All Rights Reserved.
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

import java.util.HashSet;

import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.log.LogService;
/**
 * This test case checks for the correct structure and metadata of the Filter tree.
 * It asserts that all mandatory nodes are there and compares the reported metadata 
 * and node types with the specified values.
 * 
 * @author Steffen Druesedow (Deutsche Telekom Laboratories)
 */
public class FilterStructureTestCase extends RMTTestBase {

	@SuppressWarnings("unused")
	private LogService log;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		log = getService(LogService.class);
	}

	/**
	 * asserts that the Filter node has correct children
	 * @throws DmtException
	 */
	public void testFilterStructure() throws DmtException {
		String[] children;
		HashSet<String> mandatory = new HashSet<String>();
		HashSet<String> unknown = new HashSet<String>();

		session = dmtAdmin.getSession(FILTER_ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		assertNotNull("Null DMT session for: " + FILTER_ROOT, session);
		// create one exemplary filter to initialize creation of the automatic nodes
		String relUri = "example";
		session.createInteriorNode(relUri);
		// TODO: This commit should not be necessary - needs check 
		session.commit();
		
		// 1st descendants
		children = session.getChildNodeNames(FILTER_ROOT + "/" + relUri);
		mandatory.add(FILTER);
		mandatory.add(TARGET);
		mandatory.add(LIMIT);
		mandatory.add(RESULT);
		mandatory.add(RESULT_URI_LIST);
		mandatory.add(INSTANCEID);

		for (String child : children) {
			if ( ! mandatory.contains(child))
				unknown.add(child);
			mandatory.remove(child);
		}
		assertEquals("There are children missing in the Filter node:" + mandatory, 0, mandatory.size());
		assertEquals("There are unknown children in the Filter node:" + unknown, 0, unknown.size());
		
		session.deleteNode(FILTER_ROOT + "/" + relUri);
		session.commit();
	}

	
	/**
	 * asserts that the Filter tree provides correct metadata and node types
	 * @throws Exception
	 */
	public void testMetaDataAndTypes() throws Exception {
		session = dmtAdmin.getSession(FILTER_ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		assertNotNull("Null DMT session for: " + FILTER_ROOT, session);
		String uri = FILTER_ROOT + "/" + "example";

		assertMetaData( FILTER_ROOT, false, "_G__", "0,1", MetaNode.PERMANENT, DmtData.FORMAT_NODE);
		assertMetaData( FILTER_ROOT + "/<>", false, "AG_D", "0..*", MetaNode.DYNAMIC, DmtData.FORMAT_NODE);
		assertMetaData( FILTER_ROOT + "/<>/" + FILTER, true, "_GR_", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_STRING);
		assertMetaData( FILTER_ROOT + "/<>/" + TARGET, true, "_GR_", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_STRING);
		assertMetaData( FILTER_ROOT + "/<>/" + LIMIT,  true, "_GR_", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_INTEGER);
		assertMetaData( FILTER_ROOT + "/<>/" + RESULT, false, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_NODE);
		assertMetaData( FILTER_ROOT + "/<>/" + INSTANCEID, true, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_INTEGER);
		assertMetaData( FILTER_ROOT + "/<>/" + RESULT_URI_LIST, false, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_NODE);
		assertMetaData( FILTER_ROOT + "/<>/" + RESULT_URI_LIST + "/<>", true, "_G__", "0..*", MetaNode.DYNAMIC, DmtData.FORMAT_STRING);

		// create one exemplary filter to initialize creation of the automatic nodes
		session.createInteriorNode(uri);
		// TODO: This commit should not be necessary - needs check 
		session.commit();
		try {
			

			assertEquals( "The nodeType of the Filter node must be " + DmtConstants.DDF_MAP, DmtConstants.DDF_MAP, session.getNodeType(FILTER_ROOT));
			assertEquals( "The nodeType of the ResultUriList node must be " + DmtConstants.DDF_LIST, DmtConstants.DDF_LIST, session.getNodeType(uri + "/" + RESULT_URI_LIST));
			
		} catch (DmtException de) {
			de.printStackTrace();
			fail("unexpeced DmtException: " + de.getMessage());
		}
		finally {
			session.deleteNode(uri);
			session.commit();
		}
	}

	/**
	 * asserts that the children of the Filter node have the correct defaults
	 * @throws DmtException
	 */
	public void testDefaults() throws DmtException {

		session = dmtAdmin.getSession(FILTER_ROOT, DmtSession.LOCK_TYPE_ATOMIC);
		assertNotNull("Null DMT session for: " + FILTER_ROOT, session);
		// create one exemplary filter to initialize creation of the automatic nodes
		String uri = FILTER_ROOT + "/" + "example";
		session.createInteriorNode(uri);
		session.commit(); // TODO: This commit should not be necessary - needs check

		try {
			assertEquals("The Default value of the Target node must be an empty String", "", session.getNodeValue(uri + "/" + TARGET).getString());
			assertEquals("The Default value of the Filter node must be \"*\"", "*", session.getNodeValue(uri + "/" + FILTER).getString());
			// Default of Limit is "not set", but cardinality is 1 --> hard to test
		}
		finally {
			session.deleteNode(uri);
			session.commit();
		}
	}

}

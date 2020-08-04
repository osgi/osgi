/*
 * Copyright (c) OSGi Alliance (2000, 2017). All Rights Reserved.
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
import java.util.List;

import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.log.LogService;
/**
 * This test case checks for the correct structure and metadata of the Log tree.
 * It asserts that all mandatory nodes are there and compares the reported metadata and node types with the specified values.
 * 
 * @author Steffen Druesedow (Deutsche Telekom Laboratories)
 */
@SuppressWarnings("deprecation")
public class LogStructureTestCase extends RMTTestBase {

	private LogService log;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		log = getService(LogService.class);
	}

	/**
	 * asserts that the Log node has correct children
	 * @throws DmtException
	 */
	public void testLogStructure() throws DmtException {
		String[] children;
		HashSet<String> mandatory = new HashSet<String>();
		HashSet<String> unknown = new HashSet<String>();

		session = dmtAdmin.getSession(LOG_ROOT, DmtSession.LOCK_TYPE_SHARED);

		// 1st descendants
		children = session.getChildNodeNames(LOG_ROOT);
		mandatory.add(LOG_ENTRIES);
		for (String child : children) {
			if ( ! mandatory.contains(child))
				unknown.add(child);
			mandatory.remove(child);
		}
		assertEquals("There are children missing in the Log node:" + mandatory, 0, mandatory.size());
		assertEquals("There are unknown children in the Log node:" + unknown, 0, unknown.size());
		
	}

	/**
	 * asserts that the LogEntry has the right structure
	 * @throws Exception 
	 */
	public void testLogEntryStructure() throws Exception {
		String[] children;
		HashSet<String> expected = new HashSet<String>();
		HashSet<String> optional = new HashSet<String>(); 
		
		// ensure that there is at least one log entry
		assertNotNull("Null LogService.", log);
		log.log(LogService.LOG_INFO, "This is a testlog!");

		session = dmtAdmin.getSession(LOG_ROOT, DmtSession.LOCK_TYPE_EXCLUSIVE);
		String[] entries = session.getChildNodeNames(LOG_ROOT + "/" + LOG_ENTRIES );
		assertNotNull("No LogEntries found.", entries);
		assertFalse("No LogEntries found.", entries.length == 0);
		
		optional = new HashSet<String>();
		optional.add(EXCEPTION);

		// children of: Log.xxx.LogEntries
		for (String entry : entries) {
			List<String> undefined = new ArrayList<String>();
			expected = new HashSet<String>();
			expected.add(BUNDLE);
			expected.add(TIME);
			expected.add(LEVEL);
			expected.add(MESSAGE);

			children = session.getChildNodeNames(LOG_ROOT + "/" + LOG_ENTRIES + "/" + entry);
			assertNotNull("Log entry children must not be null for: " + entry,
					children);
			assertTrue("Log entry children must exist for: " + entry,
					children.length > 0);
			for (String child : children) {
				if ( ! expected.contains(child) && ! optional.contains(child) )
					undefined.add(child);
				expected.remove(child);
			}

			assertEquals("Mandatory nodes are missing in Framework.Bundle." + entry, 0,
					expected.size());
			
			assertEquals("There are un-specified nodes in Framework.Bundle." + entry + ": " + undefined, 0,
					undefined.size());
		}
		
	}
	
	
	/**
	 * asserts that the Log tree provides correct metadata and node types
	 * @throws Exception
	 */
	public void testMetaDataAndType() throws Exception {
		try {
			session = dmtAdmin.getSession(LOG_ROOT, DmtSession.LOCK_TYPE_SHARED);
			assertNotNull("Null DMT session.", session);
			assertMetaData( LOG_ROOT, false, "_G__", "0,1", MetaNode.PERMANENT, DmtData.FORMAT_NODE);
			assertMetaData( LOG_ROOT + "/" + LOG_ENTRIES, false, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_NODE);
			assertEquals( "The nodeType of the Log.LogEntries node must be " + DmtConstants.DDF_LIST, DmtConstants.DDF_LIST, session.getNodeType(LOG_ROOT + "/" + LOG_ENTRIES));
	
			String[] logEntries = session.getChildNodeNames(LOG_ROOT + "/" + LOG_ENTRIES);
			for (String entry : logEntries) {
				String uri = LOG_ROOT + "/" + LOG_ENTRIES + "/" + entry;
				assertMetaData( uri, false, "_G__", "0..*", MetaNode.DYNAMIC, DmtData.FORMAT_NODE);
				
				uri+= "/";
				assertMetaData( uri + BUNDLE, 	  	true, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_STRING);
				assertMetaData( uri + TIME, 		true, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_DATE_TIME);
				assertMetaData( uri + LEVEL, 		true, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_INTEGER);
				assertMetaData( uri + MESSAGE,	  	true, "_G__", "1", MetaNode.AUTOMATIC, DmtData.FORMAT_STRING);
				assertMetaData( uri + EXCEPTION,  	true, "_G__", "0,1", MetaNode.AUTOMATIC, DmtData.FORMAT_STRING);
			}
		} catch (DmtException de) {
			de.printStackTrace();
			fail("unexpeced DmtException: " + de.getMessage());
		}
	}

}

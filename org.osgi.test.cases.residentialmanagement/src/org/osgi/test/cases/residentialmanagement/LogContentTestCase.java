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

import java.util.Date;
import java.util.Enumeration;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.Uri;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;

/**
 * This test case checks that the logs are correctly reflected in the Log subtree.
 * 
 * @author Steffen Druesedow (Deutsche Telekom Laboratories)
 */
public class LogContentTestCase extends RMTTestBase {

	private LogService log;
	private LogReaderService logReader;
	
	protected void setUp() throws Exception {
		super.setUp();
		log = getService(LogService.class);
		logReader = getService(LogReaderService.class);
	}

	/**
	 * asserts that the current log entries are correctly reflected in the RMT 
	 * @throws Exception
	 */
	public void testLogEntries() throws Exception {
		// ensure that there is at least one log entry
		assertNotNull(log);
		log.log(LogService.LOG_INFO, "This is a testlog!");

		// opening session exclusively, that must stop the RMT from adding new entries
		session = dmtAdmin.getSession(LOG_ROOT, DmtSession.LOCK_TYPE_EXCLUSIVE);
		
		// Hmm ... There is a short delay between opening the session and getting the logs
		assertNotNull(logReader);
		Enumeration<LogEntry> logEntries = logReader.getLog();
		
		String[] children = session.getChildNodeNames(LOG_ROOT + "/" + LOG_ENTRIES );
		assertNotNull("No LogEntries found.", children);
		assertFalse("No LogEntries found.", children.length == 0);

		
		for (String child : children ) {
			String uri = LOG_ROOT + "/" + child + "/";
			LogEntry logEntry = logEntries.nextElement();
			
			String bundleLocation = Uri.decode(session.getNodeValue(uri + BUNDLE).getString());
			Date time = session.getNodeValue(uri + TIME).getDateTime();
			int level = session.getNodeValue(uri + LEVEL).getInt();
			String message = session.getNodeValue(uri + MESSAGE).getString();
			
			assertEquals("The log level differs.",logEntry.getLevel(), level);
			assertEquals("The log timestamp differs.",logEntry.getTime(), time.getTime());
			assertEquals("The log message differs.",logEntry.getMessage(), message);
			assertEquals("The bundle location of the logging bundle differs.",logEntry.getBundle().getLocation(), bundleLocation);
			DmtData ex = session.getNodeValue(uri + EXCEPTION);
			String exception = ex != null ? ex.getString() : null;
			assertEquals("The exception field of the logEntry differs.", logEntry.getException(), exception);
		}
	}

	/**
	 * this test checks that no new entries are added to the RMT-Log, if there is an open exclusive session.
	 * @throws Exception
	 */
	public void testLogEntriesInExclusiveSession() throws Exception {
		assertNoNewEntriesDuringExclusiveSession(DmtSession.LOCK_TYPE_EXCLUSIVE);
		assertNoNewEntriesDuringExclusiveSession(DmtSession.LOCK_TYPE_ATOMIC);
	}
	
	/**
	 * this test checks that new entries are added to the RMT-Log, if there is an open shared session.
	 * @throws Exception
	 */
	public void testLogEntriesInSharedSession() throws Exception {
		assertEquals(null, null);
		// ensure that there is at least one log entry
		assertNotNull(log);
		log.log(LogService.LOG_INFO, "Infolog 1");

		// opening session in shared mode
		session = dmtAdmin.getSession(LOG_ROOT, DmtSession.LOCK_TYPE_SHARED);

		String[] children = session.getChildNodeNames(LOG_ROOT + "/" + LOG_ENTRIES );
		assertNotNull("No LogEntries found.", children);
		assertFalse("No LogEntries found.", children.length == 0);

		String uri = LOG_ROOT + "/" + children[0] + "/";
		Date oldTime 			 	= session.getNodeValue(uri + TIME).getDateTime();

		// wait a while to ensure that the timestamp of a new log entry changes
		Thread.sleep(100);
		// write a new log, that should not be in the RMT, because blocking session is running
		log.log(LogService.LOG_WARNING, "Warninglog 1");

		// read logs again and compare the first entry with the previous first one
		children = session.getChildNodeNames(LOG_ROOT + "/" + LOG_ENTRIES );
		assertNotNull("No LogEntries found.", children);
		assertFalse("No LogEntries found.", children.length == 0);

		uri = LOG_ROOT + "/" + children[0] + "/";
		Date time 			 	= session.getNodeValue(uri + TIME).getDateTime();

		assertEquals("The timestamp of the first log entry should have changed while in shared session",oldTime, time);
	}

	// ********** Utility 
	private void assertNoNewEntriesDuringExclusiveSession(int sessionType) throws Exception {

		assertEquals(null, null);
		// ensure that there is at least one log entry
		assertNotNull(log);
		log.log(LogService.LOG_INFO, "Infolog 1");

		// opening session exclusively, that must stop the RMT from adding new entries
		session = dmtAdmin.getSession(LOG_ROOT, sessionType);

		String[] children = session.getChildNodeNames(LOG_ROOT + "/" + LOG_ENTRIES );
		assertNotNull("No LogEntries found.", children);
		assertFalse("No LogEntries found.", children.length == 0);

		String uri = LOG_ROOT + "/" + children[0] + "/";
		String oldBundleLocation 	= Uri.decode(session.getNodeValue(uri + BUNDLE).getString());
		Date oldTime 			 	= session.getNodeValue(uri + TIME).getDateTime();
		int oldLevel 			 	= session.getNodeValue(uri + LEVEL).getInt();
		String oldMessage 			= session.getNodeValue(uri + MESSAGE).getString();
		DmtData oldEx				= session.getNodeValue(uri + EXCEPTION);
		String oldException = oldEx != null ? oldEx.getString() : null;
		
		// write a new log, that should not be in the RMT, because blocking session is running
		log.log(LogService.LOG_INFO, "Infolog 2");

		// read logs again and compare the first entry with the previous first one
		children = session.getChildNodeNames(LOG_ROOT + "/" + LOG_ENTRIES );
		assertNotNull("No LogEntries found.", children);
		assertFalse("No LogEntries found.", children.length == 0);

		uri = LOG_ROOT + "/" + children[0] + "/";
		String bundleLocation 	= Uri.decode(session.getNodeValue(uri + BUNDLE).getString());
		Date time 			 	= session.getNodeValue(uri + TIME).getDateTime();
		int level 			 	= session.getNodeValue(uri + LEVEL).getInt();
		String message 			= session.getNodeValue(uri + MESSAGE).getString();
		DmtData ex 				= session.getNodeValue(uri + EXCEPTION);
		String exception = ex != null ? ex.getString() : null;

		assertEquals("The first log entry in RMT has changed while in exclusive session (level differs)",oldLevel, level);
		assertEquals("The first log entry in RMT has changed while in exclusive session (time differs)",oldTime, time);
		assertEquals("The first log entry in RMT has changed while in exclusive session (message differs)",oldMessage, message);
		assertEquals("The first log entry in RMT has changed while in exclusive session (bundlelocation differs)",oldBundleLocation, bundleLocation);
		assertEquals("The first log entry in RMT has changed while in exclusive session (exception differs)",oldException, exception);

		if ( sessionType == DmtSession.LOCK_TYPE_ATOMIC)
			session.commit();
		session.close();
	}
	
}

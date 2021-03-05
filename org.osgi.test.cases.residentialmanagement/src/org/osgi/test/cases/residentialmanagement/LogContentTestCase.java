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
import java.util.Date;
import java.util.List;

import org.osgi.service.dmt.DmtSession;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;
import org.osgi.test.support.sleep.Sleep;

/**
 * This test case checks that the logs are correctly reflected in the Log subtree.
 *
 * @author Steffen Druesedow (Deutsche Telekom Laboratories)
 */
@SuppressWarnings("deprecation")
public class LogContentTestCase extends RMTTestBase implements LogListener {

	private LogReaderService logReader;
	private final List<LogEntry>	localLogEntries	= new ArrayList<LogEntry>();
	private volatile boolean	enableLog;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		log = getService(LogService.class);
		logReader = getService(LogReaderService.class);
		logReader.addLogListener(this);
		this.enableLog = false;
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		try {
			logReader.removeLogListener(this);
		} catch (Exception e) {	}
		this.enableLog = false;
	}

	/**
	 * tests that the log entries from the RMT are in cronological order with the most recent one first.
	 * @throws Exception
	 */
	public void testLogEntryOrder() throws Exception {
		int max = 100;
		resetLocalLogs();
		assertEquals("Log entries are available!", 0, getLocalLogEntries()
				.size());

		this.enableLog = true;
		createRandomLogs(max);
		
		Sleep.sleep(DELAY);
		// open session exclusively, that must stop the RMT from adding new entries
		session = dmtAdmin.getSession(LOG_ROOT, DmtSession.LOCK_TYPE_EXCLUSIVE);
		this.enableLog = false;

		String[] children = session.getChildNodeNames(LOG_ROOT + "/" + LOG_ENTRIES );
		assertNotNull("No LogEntries found.", children);
		assertFalse("No LogEntries found.", children.length == 0);

		// the impl. might only support less than our choosen max.
		max = Math.min( children.length, max );

		int index = 0;
		long oldTime = Long.MAX_VALUE;
		// elements returned by getChildNodeNames are not in particular order,
		// but its a list that must start from 0 and be continuous
		for (int i = 0; i < max; i++) {
			String uri = LOG_ROOT + "/" + LOG_ENTRIES + "/" + i + "/";

			long time = session.getNodeValue(uri + TIME).getDateTime().getTime();
			@SuppressWarnings("unused")
			int level = session.getNodeValue(uri + LEVEL).getInt();
			@SuppressWarnings("unused")
			String message = session.getNodeValue(uri + MESSAGE).getString();
			assertTrue( "The log entries are not ordered correctly (most recent to oldest)", time <= oldTime );
			oldTime = time;
			index++;
			if ( index >= max )
				break;
		}
	}


	/**
	 * Asserts that the latest log entries are correctly reflected in the RMT
	 * - creates a number of random logs
	 * - records all logs that come in during this period
	 * - compares the recorded logs with the ones from the RMT
	 * This test relies on the correct blocking of new log entries in exclusive sessions.
	 * @throws Exception
	 */
	public void testLogEntries() throws Exception {
		int max = 100;
		resetLocalLogs();
		assertEquals("Log entries are available!", 0, getLocalLogEntries()
				.size());

		this.enableLog = true;
		createRandomLogs(max);
		
		Sleep.sleep(DELAY);
		// opening session exclusively, that must stop the RMT from adding new entries
		session = dmtAdmin.getSession(LOG_ROOT, DmtSession.LOCK_TYPE_EXCLUSIVE);
		this.enableLog = false;

		String[] children = session.getChildNodeNames(LOG_ROOT + "/" + LOG_ENTRIES );
		assertNotNull("No LogEntries found.", children);
		assertFalse("No LogEntries found.", children.length == 0);

		// the impl. might only support less than our choosen max.
		max = Math.min( children.length, max );

		int index = 0;
		int localSize = getLocalLogEntries().size();
		// elements returned by getChildNodeNames are not in particular order,
		// but its a list that must start from 0 and be continuous
		for (int i = 0; i < max; i++) {
			String uri = LOG_ROOT + "/" + LOG_ENTRIES + "/" + i + "/";
			LogEntry localLogEntry = getLocalLogEntries().get(localSize - i-1);

			String bundleLocation = session.getNodeValue(uri + BUNDLE).getString();
			Date time = session.getNodeValue(uri + TIME).getDateTime();
			int level = session.getNodeValue(uri + LEVEL).getInt();
			String message = session.getNodeValue(uri + MESSAGE).getString();

			assertEquals(
					"This is not the expected logEntry. The log message differs.",
					localLogEntry.getMessage(), message);
			assertEquals(
					"This is not the expected logEntry. The bundle location differs.",
					localLogEntry.getBundle().getLocation(), bundleLocation);
			assertEquals("This is not the expected logEntry. The log level differs.",localLogEntry.getLevel(), level);
			assertEquals("This is not the expected logEntry. The log timestamp differs.",localLogEntry.getTime(), time.getTime());
			if ( session.isNodeUri(uri + EXCEPTION )) {
				// its not specified that only ERROR logs can have an exception
				String ex = session.getNodeValue(uri + EXCEPTION).getString();
				// this string must contain "human readable information about the exception" and optionally a stack trace
				// --> check that at least the exception message is part of the node value
				assertTrue("The exception field does not contain the exception message.", ex.indexOf(localLogEntry.getException().getMessage()) >= 0);
			}
			index++;
			if ( index >= max )
				break;
		}
	}

	/**
	 * this test checks that new entries are added to the RMT-Log, if there is an open shared session.
	 * @throws Exception
	 */
	public void testLogEntriesInSharedSession() throws Exception {
		// ensure that there is at least one log entry
		assertNotNull("Null LogService.", log);
		log.log(LogService.LOG_INFO, "Infolog 1");
		Sleep.sleep(DELAY);

		// opening session in shared mode
		session = dmtAdmin.getSession(LOG_ROOT, DmtSession.LOCK_TYPE_SHARED);

		String uri = LOG_ROOT + "/" + LOG_ENTRIES + "/0/" + TIME;
		assertTrue("There must be at least one log entry: " + uri, session.isNodeUri(uri));
		Date oldTime = session.getNodeValue(uri).getDateTime();

		// wait a while to ensure that the timestamp of a new log entry changes
		Sleep.sleep(DELAY);
		// write a new log
		log.log(LogService.LOG_WARNING, "Warninglog 1");

		Sleep.sleep(DELAY);
		Date time = session.getNodeValue(uri).getDateTime();

		assertFalse("The log list must be updated while in a shared session",
				oldTime.equals(time));
	}

	/**
	 * this test checks that no new entries are added to the RMT-Log, if there is an open exclusive session.
	 * @throws Exception
	 */
	public void testLogEntriesInExclusiveSession() throws Exception {
		assertNoUpdatesDuringExclusiveSession(DmtSession.LOCK_TYPE_EXCLUSIVE);
		assertNoUpdatesDuringExclusiveSession(DmtSession.LOCK_TYPE_ATOMIC);
	}



	// ********** Utility
	private void assertNoUpdatesDuringExclusiveSession(int sessionType) throws Exception {

		// ensure that there is at least one log entry
		assertNotNull("Null LogService.", log);
		log.log(LogService.LOG_INFO, "Infolog 1");
		Sleep.sleep(DELAY);

		// opening session exclusively, that must stop the RMT from adding new entries
		session = dmtAdmin.getSession(LOG_ROOT, sessionType);

		String uri = LOG_ROOT + "/" + LOG_ENTRIES + "/0/" + TIME;
		assertTrue("There must be at least one log entry: " + uri, session.isNodeUri(uri));
		Date oldTime = session.getNodeValue(uri).getDateTime();

		// wait a while to ensure that the timestamp of a new log entry changes
		Sleep.sleep(DELAY);
		// write a new log
		log.log(LogService.LOG_WARNING, "Warninglog 1");

		Sleep.sleep(DELAY);
		Date time = session.getNodeValue(uri).getDateTime();

		assertEquals("The log list must not be updated while in an exclusive session",oldTime, time);

		if ( sessionType == DmtSession.LOCK_TYPE_ATOMIC)
			session.commit();
		session.close();
	}

	//********* Utilities

	@Override
	public synchronized void logged(LogEntry entry) {
		if (enableLog && entry.getMessage().startsWith(LOG_TEST_MESSAGE_PREFIX)) {
			localLogEntries.add(entry);
		}
	}

	private synchronized List<LogEntry> getLocalLogEntries() {
		return new ArrayList<LogEntry>(localLogEntries);
	}

	private synchronized void resetLocalLogs() {
		localLogEntries.clear();
	}


}

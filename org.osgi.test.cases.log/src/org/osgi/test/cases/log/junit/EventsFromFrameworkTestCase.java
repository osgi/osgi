/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.test.cases.log.junit;

import java.io.IOException;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogLevel;
import org.osgi.service.log.LogService;
import org.osgi.test.support.wiring.Wiring;

public class EventsFromFrameworkTestCase extends AbstractLogTestCase {
	private static final String	EVENTS_FRAMEWORK	= "Events.Framework";
	private static final String	EVENTS_BUNDLE		= "Events.Bundle";
	private static final String	EVENTS_SERVICE		= "Events.Service";

	static class EventLogListener extends LogReader {
		private final String			loggerName;

		public EventLogListener(String loggerName) {
			this.loggerName = loggerName;
		}

		@Override
		public void logged(LogEntry entry) {
			if (loggerName.equals(entry.getLoggerName())
					|| entry.getLoggerName().startsWith(loggerName + ".")) {
				synchronized (log) {
					log.add(entry);
					log.notifyAll();
				}
			}
		}

		public LogEntry getEntry(int timeout) {
			synchronized (log) {
				if (log.size() == 0) {
					try {
						log.wait(timeout);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
				LogEntry entry = log.size() == 0 ? null : log.remove(0);
				return entry;
			}
		}

		public void waitForEntry(long timeToWait, String message) {
			synchronized (log) {
				try {
					long startTime = System.currentTimeMillis();
					while (log.size() > 0 && !message.equals(log.get(log.size() - 1).getMessage())
							&& timeToWait > 0) {
						log.wait(timeToWait);
						timeToWait = timeToWait
								- (System.currentTimeMillis() - startTime);
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}

				if (log.size() > 0 && !message.equals(log.get(log.size() - 1).getMessage())) {
					fail(message + " not found in logs");
				}
			}
		}

		public List<LogEntry> getLogs() {
			return log;
		}
	};

	private EventLogListener eventLogListener = null;



	@Override
	protected void tearDown() throws Exception {
		if (eventLogListener != null) {
			logReaderService.removeLogListener(eventLogListener);
		}
		super.tearDown();
	}

	@SuppressWarnings("deprecation")
	public void testFrameworkEventsLogged()
			throws BundleException, IOException {
		// Only reliable events to test are STARTLEVEL_CHANGED and
		// PACKAGES_REFRESHED
		tb1 = install("tb1.jar");
		tb1.start();

		eventLogListener = new EventLogListener(EVENTS_FRAMEWORK);
		logReaderService.addLogListener(eventLogListener);
		Wiring.synchronousRefreshBundles(getContext(), tb1);

		LogEntry refreshLogEntry = eventLogListener.getEntry(10000);
		assertNotNull("No entry for refresh.", refreshLogEntry);
		assertEventLog(refreshLogEntry, EVENTS_FRAMEWORK,
				"FrameworkEvent PACKAGES REFRESHED", systemBundle(),
				LogService.LOG_INFO, LogLevel.INFO, null);

		FrameworkStartLevel fwkStartLevel = systemBundle()
				.adapt(FrameworkStartLevel.class);

		fwkStartLevel.setStartLevel(fwkStartLevel.getStartLevel() + 1);
		LogEntry startLevelLog1 = eventLogListener.getEntry(10000);

		fwkStartLevel.setStartLevel(fwkStartLevel.getStartLevel() - 1);
		LogEntry startLevelLog2 = eventLogListener.getEntry(10000);

		assertNotNull("No entry for start level change 1.", startLevelLog1);
		assertEventLog(startLevelLog1, EVENTS_FRAMEWORK,
				"FrameworkEvent STARTLEVEL CHANGED", systemBundle(),
				LogService.LOG_INFO, LogLevel.INFO, null);

		assertNotNull("No entry for start level change 2.", startLevelLog2);
		assertEventLog(startLevelLog2, EVENTS_FRAMEWORK,
				"FrameworkEvent STARTLEVEL CHANGED", systemBundle(),
				LogService.LOG_INFO, LogLevel.INFO, null);
	}

	public void testBundleEventsLogged() throws BundleException, IOException {
		eventLogListener = new EventLogListener(EVENTS_BUNDLE);
		logReaderService.addLogListener(eventLogListener);

		tb1 = install("tb1.jar");
		tb1.start();
		tb1.stop();
		tb1.update(entryStream("tb1.jar"));
		tb1.start();
		tb1.stop();
		tb1.uninstall();

		List<String> expectedEventTypes1 = Arrays.asList("INSTALLED",
				"RESOLVED", "STARTED", "STOPPED", "UNRESOLVED", "UPDATED",
				"RESOLVED", "STARTED", "STOPPED", "UNRESOLVED", "UNINSTALLED");

		List<String> expectedEventTypes2 = Arrays.asList("INSTALLED",
				"RESOLVED", "STARTING", "STARTED", "STOPPING", "STOPPED",
				"UNRESOLVED", "UPDATED", "RESOLVED", "STARTING", "STARTED",
				"STOPPING", "STOPPED", "UNRESOLVED", "UNINSTALLED");

		eventLogListener.waitForEntry(10000, "BundleEvent UNINSTALLED");
		List<LogEntry> actualLogs = eventLogListener.getLogs();

		int logsCount = actualLogs.size();
		if (logsCount == expectedEventTypes1.size()) {
			assertBundleEventLog(actualLogs, expectedEventTypes1);
		} else if (logsCount == expectedEventTypes2.size()) {
			assertBundleEventLog(actualLogs, expectedEventTypes2);
		} else {
			fail("Expected bundle events are not logged");
		}
	}

	public void testServiceEventsLogged() {
		eventLogListener = new EventLogListener(EVENTS_SERVICE);
		logReaderService.addLogListener(eventLogListener);

		ServiceRegistration<String> testReg = getContext()
				.registerService(String.class, "TestService", null);
		ServiceReference<String> testRef = testReg.getReference();
		Dictionary<String,String> props = new Hashtable<>();
		props.put("test", "value");
		testReg.setProperties(props);
		testReg.unregister();

		assertServiceEventLog("REGISTERED", LogLevel.INFO, testRef);
		assertServiceEventLog("MODIFIED", LogLevel.DEBUG, testRef);
		assertServiceEventLog("UNREGISTERING", LogLevel.INFO, testRef);
	}

	private void assertServiceEventLog(String eventType, LogLevel logLevel,
			ServiceReference<String> ref) {
		LogEntry entry = eventLogListener.getEntry(10000);
		assertEventLog(entry, EVENTS_SERVICE, "ServiceEvent " + eventType,
				getContext().getBundle(), logLevel.ordinal(), logLevel,
				ref);
	}

	@SuppressWarnings("deprecation")
	private void assertBundleEventLog(List<LogEntry> actualLogs,
			List<String> expectedEventTypes) {
		for (int i = 0; i < actualLogs.size(); i++) {
			assertEventLog(actualLogs.get(i), EVENTS_BUNDLE,
					"BundleEvent " + expectedEventTypes.get(i), tb1,
					LogService.LOG_INFO, LogLevel.INFO, null);
		}
	}

	@SuppressWarnings("deprecation")
	private void assertEventLog(LogEntry logEntry, String loggerName,
			String message, Bundle bundle, int level, LogLevel logLevel,
			ServiceReference< ? > reference) {
		assertTrue("Wrong logger name.",
				loggerName.equals(logEntry.getLoggerName())
						|| logEntry.getLoggerName().startsWith(loggerName + "."));
		assertEquals("Wrong message.", message, logEntry.getMessage());
		assertEquals("Wrong bundle.", bundle, logEntry.getBundle());
		assertEquals("Wrong level.", level, logEntry.getLevel());
		assertEquals("Wrong log level.", logLevel, logEntry.getLogLevel());
		assertEquals("Wrong reference.", reference,
				logEntry.getServiceReference());
	}

	Bundle systemBundle() {
		return getContext().getBundle(Constants.SYSTEM_BUNDLE_LOCATION);
	}
}

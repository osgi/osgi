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
import java.util.Dictionary;
import java.util.Hashtable;

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
			if (loggerName.equals(entry.getLoggerName())) {
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
	};

	private EventLogListener eventLogListener = null;



	@Override
	protected void tearDown() throws Exception {
		if (eventLogListener != null) {
			logReaderService.removeLogListener(eventLogListener);
		}
		super.tearDown();
	}

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

		assertBundleEventLog("INSTALLED");
		assertBundleEventLog("RESOLVED");
		assertBundleEventLog("STARTED");
		assertBundleEventLog("STOPPED");
		assertBundleEventLog("UNRESOLVED");
		assertBundleEventLog("UPDATED");
		assertBundleEventLog("RESOLVED");
		assertBundleEventLog("STARTED");
		assertBundleEventLog("STOPPED");
		assertBundleEventLog("UNRESOLVED");
		assertBundleEventLog("UNINSTALLED");

	}

	public void testServiceEventsLogged() {
		eventLogListener = new EventLogListener(EVENTS_SERVICE);
		logReaderService.addLogListener(eventLogListener);

		ServiceRegistration<String> testReg = getContext()
				.registerService(String.class, "TestService", null);
		Dictionary<String,String> props = new Hashtable<>();
		props.put("test", "value");
		testReg.setProperties(props);
		testReg.unregister();

		assertServiceEventLog("REGISTERED", LogLevel.INFO);
		assertServiceEventLog("MODIFIED", LogLevel.DEBUG);
		assertServiceEventLog("UNREGISTERING", LogLevel.INFO);
	}

	private void assertServiceEventLog(String eventType, LogLevel logLevel) {
		LogEntry entry = eventLogListener.getEntry(10000);
		assertEventLog(entry, EVENTS_SERVICE, "ServiceEvent " + eventType,
				getContext().getBundle(), logLevel.ordinal(), logLevel,
				null);
	}

	private void assertBundleEventLog(String eventType) {
		LogEntry entry = eventLogListener.getEntry(10000);
		assertEventLog(entry, EVENTS_BUNDLE, "BundleEvent " + eventType, tb1,
				LogService.LOG_INFO, LogLevel.INFO, null);
	}

	private void assertEventLog(LogEntry logEntry, String loggerName,
			String message, Bundle bundle, int level, LogLevel logLevel,
			ServiceReference< ? > reference) {
		assertEquals("Wrong logger name.", loggerName,
				logEntry.getLoggerName());
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

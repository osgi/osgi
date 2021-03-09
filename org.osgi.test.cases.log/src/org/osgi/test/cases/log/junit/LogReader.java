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
package org.osgi.test.cases.log.junit;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogLevel;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogService;

import junit.framework.TestCase;

public class LogReader implements LogListener {
	final List<LogEntry> log = new ArrayList<>();

	/**
	 * Add any entry that has 4711 in the message to the log list
	 */
	public void logged(LogEntry entry) {
		if (entry.getMessage().indexOf("<4711>") >= 0) {
			synchronized (log) {
				log.add(entry);
				log.notifyAll();
			}
		}
	}

	public LogEntry getEntry(int timeout, String message, int level) {
		synchronized (log) {
			if (log.size() == 0) {
				try {
					log.wait(timeout);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			LogEntry entry = log.size() == 0 ? null : log.remove(0);
			TestCase.assertNotNull("No log entry found: " + message + " - "
					+ getLogLevel(level), entry);
			return entry;
		}
	}

	public int size() {
		synchronized (log) {
			return log.size();
		}
	}

	@SuppressWarnings("deprecation")
	static LogLevel getLogLevel(int level) {
		switch (level) {
			case 0 :
				return LogLevel.AUDIT;
			case LogService.LOG_DEBUG :
				return LogLevel.DEBUG;
			case LogService.LOG_ERROR :
				return LogLevel.ERROR;
			case LogService.LOG_INFO :
				return LogLevel.INFO;
			case LogService.LOG_WARNING :
				return LogLevel.WARN;
			default :
				return LogLevel.TRACE;
		}
	}

	public void clear() {
		synchronized (log) {
			log.clear();
		}
	}
}

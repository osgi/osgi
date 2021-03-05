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
/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 */
package org.osgi.test.cases.log.junit;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class LogServiceTestCase extends AbstractLogTestCase {

	/**
	 * Uses the deprecated LogSerivce log methods at all levels with all
	 * combinations of log entry arguments. All log levels must be effective.
	 */
	@SuppressWarnings("deprecation")
	public void testLogServiceLogger() {
		Collection<LogReader> readers = Collections.singletonList(reader);
		logServiceThenAssertLog(true, readers, LogService.LOG_ERROR,
				errorMessage, null, null);
		logServiceThenAssertLog(true, readers, LogService.LOG_WARNING,
				warnMessage, null, null);
		logServiceThenAssertLog(true, readers, LogService.LOG_INFO,
				infoMessage, null, null);
		logServiceThenAssertLog(true, readers, LogService.LOG_DEBUG,
				debugMessage, null, null);
		logServiceThenAssertLog(true, readers, -12, unknownMessage, null,
				null);

		logServiceThenAssertLog(true, readers, LogService.LOG_ERROR,
				errorMessage, errorThrowable, null);
		logServiceThenAssertLog(true, readers, LogService.LOG_WARNING,
				warnMessage, warnThrowable, null);
		logServiceThenAssertLog(true, readers, LogService.LOG_INFO,
				infoMessage, infoThrowable, null);
		logServiceThenAssertLog(true, readers, LogService.LOG_DEBUG,
				debugMessage, debugThrowable, null);
		logServiceThenAssertLog(true, readers, -12, unknownMessage,
				unknownThrowable, null);

		logServiceThenAssertLog(true, readers, LogService.LOG_ERROR,
				errorMessage, null, errorRegistration.getReference());
		logServiceThenAssertLog(true, readers, LogService.LOG_WARNING,
				warnMessage, null, warnRegistration.getReference());
		logServiceThenAssertLog(true, readers, LogService.LOG_INFO,
				infoMessage, null, infoRegistration.getReference());
		logServiceThenAssertLog(true, readers, LogService.LOG_DEBUG,
				debugMessage, null, debugRegistration.getReference());
		logServiceThenAssertLog(true, readers, -12, unknownMessage, null,
				unknownRegistration.getReference());

		logServiceThenAssertLog(true, readers, LogService.LOG_ERROR,
				errorMessage, errorThrowable, errorRegistration.getReference());
		logServiceThenAssertLog(true, readers, LogService.LOG_WARNING,
				warnMessage, warnThrowable, warnRegistration.getReference());
		logServiceThenAssertLog(true, readers, LogService.LOG_INFO,
				infoMessage, infoThrowable, infoRegistration.getReference());
		logServiceThenAssertLog(true, readers, LogService.LOG_DEBUG,
				debugMessage, debugThrowable, debugRegistration.getReference());
		logServiceThenAssertLog(true, readers, -12, unknownMessage,
				unknownThrowable, unknownRegistration.getReference());
	}

	/**
	 * Tests that a LogEntry is fired to multiple LogListeners. All log levels
	 * must be effective.
	 */
	@SuppressWarnings("deprecation")
	public void testMultipleListeners() {
		LogReader reader1 = new LogReader();
		LogReader reader2 = new LogReader();
		Collection<LogReader> readers = Arrays.asList(reader1, reader2);
		Collection<LogReader> onlyReader1 = Collections.singletonList(reader1);
		Collection<LogReader> noReaders = Collections.emptyList();

		RuntimeException e = new RuntimeException();
		String message = "'The message <4711>'";
		ServiceReference<LogService> sr = logServiceReference;

		logReaderService.addLogListener(reader1);
		logReaderService.addLogListener(reader2);

		logServiceThenAssertLog(true, readers, LogService.LOG_ERROR, message,
				null, null);
		logServiceThenAssertLog(true, readers, LogService.LOG_WARNING,
				message, null, null);
		logServiceThenAssertLog(true, readers, LogService.LOG_INFO, message,
				null, sr);
		logServiceThenAssertLog(true, readers, LogService.LOG_DEBUG, message,
				e, null);
		logServiceThenAssertLog(true, readers, -12, message, e, null);

		// Now remove reader2 so that we can check if 2 is really
		// removed
		logReaderService.removeLogListener(reader2);

		logServiceThenAssertLog(true, onlyReader1, LogService.LOG_ERROR,
				message, null, null);
		logServiceThenAssertLog(true, onlyReader1, LogService.LOG_WARNING,
				message, null, null);
		logServiceThenAssertLog(true, onlyReader1, LogService.LOG_INFO,
				message, null, sr);
		logServiceThenAssertLog(true, onlyReader1, LogService.LOG_DEBUG,
				message, e, null);
		logServiceThenAssertLog(true, onlyReader1, -12, message, e, null);

		assertEquals(0, reader2.size());

		// Now we also removed reader2 so that we can check if
		// the listeners re really removed
		logReaderService.removeLogListener(reader1);

		logServiceThenAssertLog(true, noReaders, LogService.LOG_ERROR,
				message, null, null);
		logServiceThenAssertLog(true, noReaders, LogService.LOG_WARNING,
				message, null, null);
		logServiceThenAssertLog(true, noReaders, LogService.LOG_INFO,
				message, null, sr);
		logServiceThenAssertLog(true, noReaders, LogService.LOG_DEBUG,
				message, e, null);
		logServiceThenAssertLog(true, noReaders, -12, message, e, null);

		assertEquals(0, reader1.size());
		assertEquals(0, reader2.size());
	}

}

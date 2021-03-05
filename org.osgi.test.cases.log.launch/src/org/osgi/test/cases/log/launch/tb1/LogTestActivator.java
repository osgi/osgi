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
package org.osgi.test.cases.log.launch.tb1;

import java.util.Collections;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogLevel;
import org.osgi.service.log.admin.LoggerAdmin;
import org.osgi.service.log.admin.LoggerContext;

public class LogTestActivator implements BundleActivator {

	ServiceReference<LoggerAdmin>	loggerAdminReference;
	LoggerAdmin						loggerAdmin;
	LoggerContext					rootContext;
	static final String				OSGI_TEST_LOGGER_NAME	= "org.osgi.test.cases.log.level";

	@Override
	public void start(BundleContext context) throws Exception {

		// get the LoggerAdmin service
		loggerAdminReference = context.getServiceReference(LoggerAdmin.class);
		loggerAdmin = context.getService(loggerAdminReference);

		// get the root LoggerContext
		rootContext = loggerAdmin.getLoggerContext(null);

		// get the effective log level of some name e.g.
		// org.osgi.test.cases.log.level
		String testExpectedLogLevel = context
				.getProperty("test.expected.log.level");
		testExpectedLogLevel = testExpectedLogLevel == null ? "WARN"
				: testExpectedLogLevel;

		String testSetToLogLevel = context.getProperty("test.set.log.level");
		if (testSetToLogLevel != null) {
			testExpectedLogLevel = testSetToLogLevel;
			rootContext.setLogLevels(
					Collections.singletonMap(OSGI_TEST_LOGGER_NAME,
							LogLevel.valueOf(testSetToLogLevel)));
		}

		LogLevel level = rootContext
				.getEffectiveLogLevel(OSGI_TEST_LOGGER_NAME);
		String actualLevel = String.valueOf(level);

		System.out.println("Expected level:" + testExpectedLogLevel);
		System.out.println("Actual level:" + actualLevel);
		// test it is the expect value
		if (actualLevel.equals(testExpectedLogLevel)) {
			System.out.println("Actual level matches expected level");
		} else {
			throw new IllegalStateException("Wrong log level, expected: "
					+ testExpectedLogLevel + " but was: " + actualLevel);
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// context.ungetService(loggerAdminReference);

	}

}

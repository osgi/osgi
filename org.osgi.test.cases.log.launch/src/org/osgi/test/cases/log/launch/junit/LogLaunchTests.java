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
package org.osgi.test.cases.log.launch.junit;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.service.log.admin.LoggerContext;

public class LogLaunchTests extends LaunchTest {

	private Map<String,String> getConfiguration(String testName) {
		return getConfiguration(testName, true);
	}

	private Map<String,String> getConfiguration(String testName,
			boolean delete) {
		Map<String,String> configuration = new HashMap<String,String>();
		if (testName != null)
			configuration.put(Constants.FRAMEWORK_STORAGE,
					getStorageArea(testName, delete).getAbsolutePath());
		return configuration;
	}

	public void testLogNoDefault() throws Exception {
		Map<String,String> configuration = getConfiguration(getName());
		Framework framework = createFramework(configuration);
		startFramework(framework);
		Bundle tb1 = null;
		try {
			// install the test bundle and start it
			tb1 = installBundle(framework, "/log.launch.tb1.jar");
			tb1.start();
		} catch (Exception e) {
			fail("Test bundle indicates a failure, see exception cause for the error.",
					e);
		} finally {
			if (tb1 != null) {
				tb1.uninstall();
			}
			stopFramework(framework);
		}
	}

	public void testLogSetDefault() throws Exception {
		Map<String,String> configuration = getConfiguration(getName());
		configuration.put(LoggerContext.LOGGER_CONTEXT_DEFAULT_LOGLEVEL,
				"INFO");
		configuration.put("test.expected.log.level", "INFO");

		Framework framework = createFramework(configuration);
		startFramework(framework);
		Bundle tb1 = null;
		try {
			// install the test bundle and start it

			tb1 = installBundle(framework, "/log.launch.tb1.jar");
			tb1.start();
		} catch (Exception e) {
			fail("Test bundle indicates a failure, see exception cause for the error.",
					e);
		} finally {
			if (tb1 != null) {
				tb1.uninstall();
			}
			stopFramework(framework);
		}
	}

	public void testLogSetOverride() throws Exception {
		Map<String,String> configuration = getConfiguration(getName());

		configuration.put("test.set.log.level", "ERROR");

		Framework framework = createFramework(configuration);
		startFramework(framework);
		Bundle tb1 = null;
		try {
			// install the test bundle and start it

			tb1 = installBundle(framework, "/log.launch.tb1.jar");
			tb1.start();
		} catch (Exception e) {
			fail("Test bundle indicates a failure, see exception cause for the error.",
					e);
		} finally {
			if (tb1 != null) {
				tb1.uninstall();
			}
			stopFramework(framework);
		}

	}
}

/*
 * Copyright (c) OSGi Alliance (2013, 2015). All Rights Reserved.
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

package org.osgi.impl.service.zigbee.util.teststep;

import org.osgi.impl.service.zigbee.basedriver.ZigBeeBaseDriver;
import org.osgi.impl.service.zigbee.util.Logger;
import org.osgi.test.support.step.TestStep;

/**
 * An implementation of the test step based on RI simulator. This implementation
 * is to be registered in the OSGi service registry.
 * 
 * @author $Id$
 */
public class TestStepForZigBeeImpl implements TestStep {

	static public final String CONF_FILE_PATH = "file path";
	static public final String LOAD_CONF = "load conf";

	static private final String TAG = "TestStepForZigBeeImpl";
	private String confFilePath = "template.xml";
	private ZigBeeBaseDriver baseDriver;

	/**
	 * It is possible to change this constructor in order to add some parameters
	 * to specify simulators.
	 */
	public TestStepForZigBeeImpl(ZigBeeBaseDriver baseDriver) {
		this.baseDriver = baseDriver;
	}

	public String execute(String stepId, String userPrompt) {

		Logger.d(TAG, "execute the stepId: " + stepId + ", userPrompt: "
				+ userPrompt);
		if (stepId == null) {
			Logger.e(TAG, "The given stepId is null, but it can NOT be.");
			return null;
		}

		if (stepId.startsWith(CONF_FILE_PATH)) {
			confFilePath = stepId.replaceAll(CONF_FILE_PATH, "");
			return null;
		} else if (stepId.equals(LOAD_CONF)) {
			baseDriver.loadConfigurationFIle(confFilePath);
			return null;
		}

		Logger.e(TAG, "The given command is UNKNOWN.");
		return null;
	}
}

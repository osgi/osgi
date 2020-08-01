/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
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

import java.io.FileInputStream;
import java.io.InputStream;

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

	static private final String	TAG						= TestStepForZigBeeImpl.class.getName();

	static public final String	ACTIVATE_ZIGBEE_DEVICES	= "activate_devices";
	public static final String	EVENT_REPORTABLE		= "event_reportable";

	private static final String	configFilePath			= System.getProperty("org.osgi.test.cases.zigbee.template", "zigbee-ct-template.xml");

	private ZigBeeBaseDriver	baseDriver;

	/**
	 * It is possible to change this constructor in order to add some parameters
	 * to specify simulators.
	 * 
	 * @param baseDriver The base driver implementation.
	 */
	public TestStepForZigBeeImpl(ZigBeeBaseDriver baseDriver) {
		this.baseDriver = baseDriver;
	}

	@Override
	public String execute(String stepId, String userPrompt) {
		Logger.d(TAG, "execute the stepId: " + stepId + ", userPrompt: " + userPrompt);

		if (stepId == null) {
			Logger.e(TAG, "The given stepId is null, but it can NOT be.");
			return null;
		}

		if (stepId.equals(ACTIVATE_ZIGBEE_DEVICES)) {
			/*
			 * Load and parse the template configuration file. This produces the
			 * registration of all the services related to the content of this
			 * file.
			 */
			try {
				InputStream is = new FileInputStream(configFilePath);
				baseDriver.loadConfigurationFile(is);
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
				Logger.d(TAG, "returns: null");
				return null;
			}
			Logger.d(TAG, "returns: 'success'");
			return "success";

		} else if (stepId.equals(EVENT_REPORTABLE)) {
			// TODO
		}

		Logger.e(TAG, "The given command is UNKNOWN.");
		return null;
	}
}

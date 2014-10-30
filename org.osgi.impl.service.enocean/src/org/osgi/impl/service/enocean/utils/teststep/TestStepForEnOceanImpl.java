/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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

package org.osgi.impl.service.enocean.utils.teststep;

import org.osgi.impl.service.enocean.utils.Logger;
import org.osgi.test.support.step.TestStep;

/**
 * An implementation of the test step based on RI simulator. This implementation
 * is to be registered in the OSGi service registry.
 */
public class TestStepForEnOceanImpl implements TestStep {

	private byte[]	currentCommand	= null;
	private byte[]	currentData		= null;

	/**
	 * It is possible to change this contructor in order to add some parameters
	 * to specify simulators.
	 */
	public TestStepForEnOceanImpl() {

	}

	/**
	 * Get current command and replace it by null.
	 * 
	 * @return the current command.
	 */
	public byte[] getCurrentCommandAndReplaceItByNull() {
		Logger.d(TestStepForEnOceanImpl.class.getName(), "getCurrentCommandAndReplaceItByNull() returns currentCommand: " + currentCommand);
		byte[] result = currentCommand;
		currentCommand = null;
		return result;
	}

	/**
	 * Push data in test step.
	 * 
	 * @param data
	 */
	public void pushDataInTestStep(byte[] data) {
		Logger.d(TestStepForEnOceanImpl.class.getName(), "pushDataInTestStep(data: " + data + ")");
		currentData = data;
	}

	public String execute(String stepId, String userPrompt) {
		Logger.d(TestStepForEnOceanImpl.class.getName(), "execute the stepId: " + stepId + ", userPrompt: " + userPrompt);
		String result = null;
		if (stepId == null) {
			Logger.e(TestStepForEnOceanImpl.class.getName(), "The given stepId is null, but it can NOT be.");
		} else
			if (stepId.startsWith("MessageExample1_")) {
				currentCommand = stepId.replaceFirst("MessageExample1_", "").getBytes();
				// ignore result;
			} else
				if (stepId.startsWith("MessageExample2_")) {
					currentCommand = stepId.replaceFirst("MessageExample2_", "").getBytes();
					// ignore result;
				} else {
					if ("EnOceanMessageDescriptionSet_with_an_EnOceanMessageDescription".equals(stepId)) {
						currentCommand = stepId.getBytes();
						// ignore result;
					} else
						if ("EnOceanChannelDescriptionSet_with_an_EnOceanChannelDescription_CID".equals(stepId)) {
							currentCommand = stepId.getBytes();
							// ignore result;
						} else
							if ("Any_new_data".equals(stepId)) {
								Logger.d(TestStepForEnOceanImpl.class.getName(), "execute(...) returns currentData: " + currentData + ", currentData.length: " + currentData.length);
								result = new String(currentData);
								currentData = null;
								return result;
							} else
								if ("Plug the EnOcean USB dongle".equals(stepId)) {
									Logger.d(TestStepForEnOceanImpl.class.getName(), "execute(...) returns currentData: " + currentData + ", currentData.length: " + currentData.length);
									// This message should be display on the
									// user
									// screen when testing EnOcean
									// implementation
									// for real.
								} else {
									Logger.e(TestStepForEnOceanImpl.class.getName(), "The given command is UNKNOWN.");
									// ignore result;
								}
				}
		return result;
	}

}

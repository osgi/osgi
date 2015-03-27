/*
 * Copyright (c) OSGi Alliance (2013, 2014). All Rights Reserved.
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
		Logger.d(TestStepForEnOceanImpl.class.getName(), "execute the stepId: "
				+ clean(stepId) + ", userPrompt: " + userPrompt);
		String result = null;
		if (stepId == null) {
			Logger.e(TestStepForEnOceanImpl.class.getName(), "The given stepId is null, but it can NOT be.");
		} else
			if (stepId.equals("MessageExample1_A")) {
				// Teach in telegram, see
				// MessageExample1.generateTeachInMsg(Fixtures.HOST_ID,
				// Fixtures.MANUFACTURER) in test.cases.enocean
				byte[] teachInTelegram = {85, 0, 10, 7, 1, -21, -91, 8, 14, -22, -128, 18, 52, 86, 120, -128, 3, -1, -1, -1, -1, -1, 0, -78};
				currentCommand = teachInTelegram;
				// ignore result;
			} else
				if (stepId.equals("MessageExample1_B")) {
					byte[] teachInTelegram = {85, 0, 10, 7, 1, -21, -91, 0, 0, 127, 8, 18, 52, 86, 120, -128, 3, -1, -1, -1, -1, -1, 0, 48};
					currentCommand = teachInTelegram;
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
								if ("Get_the_event_that_the_base_driver_should_have_received".equals(stepId)) {
									Logger.d(TestStepForEnOceanImpl.class.getName(), "execute(...) returns currentData: " + currentData + ", currentData.length: " + currentData.length);
									if (currentData == null) {
										result = null;
									} else {
										result = new String(currentData);
										currentData = null;
									}
									return result;
								} else
									if ("Plug the EnOcean USB dongle".equals(stepId)) {
										Logger.d(TestStepForEnOceanImpl.class.getName(), "execute(...) returns currentData: " + currentData);
										// This message should be display on the
										// user screen when testing EnOcean
										// implementation for real.
									} else {
										Logger.e(TestStepForEnOceanImpl.class.getName(), "The given command is UNKNOWN.");
										// ignore result;
									}
					}
		return result;
	}

	private String clean(String input) {
		char[] result = new char[input.length()];
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			result[i] = c >= ' ' ? c : '?';
		}
		return new String(result);
	}
}

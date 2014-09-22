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
import org.osgi.test.cases.enoceansimulation.teststep.TestStep;

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

	public String[] execute(String command, String[] parameters) {
		Logger.d(TestStepForEnOceanImpl.class.getName(), "execute(command: " + command + ", parameters: " + parameters + ")");
		String[] result = null;
		if ("MessageExample1".equals(command)) {
			currentCommand = parameters[0].getBytes();
			// ignore result;
		} else
			if ("MessageExample2".equals(command)) {
				currentCommand = parameters[0].getBytes();
				// ignore result;
			} else {
				if ("EnOceanMessageDescriptionSet_with_an_EnOceanMessageDescription_A5_02_01".equals(command)) {
					currentCommand = command.getBytes();
					// ignore result;
				} else
					if ("EnOceanChannelDescriptionSet_with_an_EnOceanChannelDescription_TMP_00".equals(command)) {
						currentCommand = command.getBytes();
						// ignore result;
					} else
						if ("Any_new_data".equals(command)) {
							Logger.d(TestStepForEnOceanImpl.class.getName(), "execute(...) returns currentData: " + currentData + ", currentData.length: " + currentData.length);
							result = new String[] {new String(currentData)};
							currentData = null;
							return result;
						} else
							if ("Plug the EnOcean USB dongle".equals(command)) {
								Logger.d(TestStepForEnOceanImpl.class.getName(), "execute(...) returns currentData: " + currentData + ", currentData.length: " + currentData.length);
								// This message should be display on the user
								// screen when testing EnOcean implementation
								// for real.
							} else {
								Logger.e(TestStepForEnOceanImpl.class.getName(), "The given command is UNKNOWN.");
								// ignore result;
							}
			}
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

}

/*
 * Copyright (c) OSGi Alliance (2015). All Rights Reserved.
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

package org.osgi.impl.service.serial;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.osgi.service.device.Constants;
import org.osgi.service.serial.SerialDevice;
import org.osgi.test.support.step.TestStep;

public class TestStepImpl implements TestStep {
	private static final String	SEPARATOR_COMMA	= ",";
	private static final String	SPACE			= " ";
	private static final String	EMPTY			= "";
	private SerialEventManager	eventManager;

	private int index = 0;

	TestStepImpl(SerialEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public String[] executeTestStep(String command, String[] parameters) {
		if (Commands.ADD.equals(command)) {
			String id = addSerialDevice(parameters);
			return new String[]{id};
		} else if (Commands.REMOVE.equals(command)) {
			removeSerialDevice(parameters);
		} else if (Commands.EVENT.equals(command)) {
			sendEvent(parameters);
		} else {
			throw new IllegalArgumentException("The stepId is not supported.");
		}
		return null;
	}

	/**
	 * The method that adds the serial device. <br>
	 * Based on an appointed parameter, registers the SerialDevice service.<br>
	 *
	 * @param parameters The parameter mentioned above
	 */
	private String addSerialDevice(String[] parameters) {
		String comportName = Integer.toString(index);
		Dictionary<String,Object> props = new Hashtable<>();
		props.put(SerialDevice.SERIAL_COMPORT, comportName);
		props.put(Constants.DEVICE_CATEGORY, new String[] {SerialDevice.DEVICE_CATEGORY});
		String id = SerialDeviceManager.getInstance().addSerialDevice(props);

		index++;

		return id;
	}

	/**
	 * The method that removes the serial device. <br>
	 * The parameters are as follows.<br>
	 * <ul>
	 * <li>index 0 : id
	 * </ul>
	 * Based on an appointed parameter, unregisters the SerialDevice service.<br>
	 *
	 * @param parameters The parameter mentioned above
	 */
	private void removeSerialDevice(String[] parameters) {
		SerialDeviceManager.getInstance().removeSerialDevice(parameters[0]);
	}

	/**
	 * The method that send event to SerialEventListener service. <br>
	 * The parameters are as follows.<br>
	 * <ul>
	 * <li>index 0 : id
	 * </ul>
	 * Based on an appointed parameter, send event to SerialEventListener
	 * service.<br>
	 *
	 * @param parameters The parameter mentioned above
	 */
	private void sendEvent(String[] parameters) {
		eventManager.sendEvent(parameters[0]);
	}

	@Override
	public String execute(String stepId, String userPrompt) {
		String command = stepId;
		userPrompt = userPrompt.substring(userPrompt.indexOf(SEPARATOR_COMMA));
		String[] parameters = toStringArray(userPrompt);
		String[] ids = executeTestStep(command, parameters);
		return toString(ids);
	}

	private String[] toStringArray(String str) {
		if (str == null || str.length() == 0) {
			return new String[0];
		}
		StringTokenizer tokenizer = new StringTokenizer(str, SEPARATOR_COMMA);
		String[] stringArray = new String[tokenizer.countTokens()];
		for (int i = 0; i < stringArray.length; i++) {
			stringArray[i] = tokenizer.nextToken();
			if (SPACE.equals(stringArray[i])) {
				stringArray[i] = null;
			}
		}
		return stringArray;
	}

	private String toString(String[] stringArray) {
		if (stringArray == null || stringArray.length == 0) {
			return new String();
		}
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < stringArray.length; i++) {
			if (stringArray[i] == null || EMPTY.equals(stringArray[i])) {
				buf.append(SPACE);
			} else {
				buf.append(stringArray[i]);
			}
			buf.append(SEPARATOR_COMMA);
		}
		return buf.toString();
	}
}

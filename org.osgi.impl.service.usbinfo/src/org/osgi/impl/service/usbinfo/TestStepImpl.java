/*
 * Copyright (c) OSGi Alliance (2014, 2015). All Rights Reserved.
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
package org.osgi.impl.service.usbinfo;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import org.osgi.service.usbinfo.USBInfoDevice;
import org.osgi.test.support.step.TestStep;

public class TestStepImpl implements TestStep {
	private static final String	SEPARATOR_COMMA	= ",";
	private static final String	SPACE			= " ";
	private static final String	EMPTY			= "";

    public String[] executeTestStep(String command, String[] parameters) {
        if (Commands.REGISTER_DEVICE.equals(command)) {
            String id = registerNewDevice(parameters);
            return new String[]{id};
        } else if (Commands.UNREGISTER_DEVICE.equals(command)) {
            unregisterNewDevice(parameters);
            return null;
        } else {
            throw new IllegalArgumentException("The stepId is not supported.");
        }
    }

    /**
     * A method of USBInfoDevice service registration.
     * <br>
     */
    private String registerNewDevice(String[] parameters) {

		Dictionary<String,Object> prop = new Hashtable<>();

        // Device Access Category
		List<String> category = new ArrayList<>();
        category.add(USBInfoDevice.DEVICE_CATEGORY);
        prop.put(org.osgi.service.device.Constants.DEVICE_CATEGORY, category.toArray(new String[0]));

        // Service properties from USB Specification
        // Device Descriptor and Service Property
        prop.put(USBInfoDevice.USB_BDEVICECLASS, "ff");
        prop.put(USBInfoDevice.USB_BDEVICESUBCLASS, "ff");
        prop.put(USBInfoDevice.USB_BDEVICEPROTOCOL, "ff");
        prop.put(USBInfoDevice.USB_IDVENDOR, "0403");
        prop.put(USBInfoDevice.USB_IDPRODUCT, "8372");
        prop.put(USBInfoDevice.USB_BCDDEVICE, "0200");

        // Interface Descriptor and Service Property
        prop.put(USBInfoDevice.USB_BINTERFACENUMBER, Integer.valueOf("8"));
        prop.put(USBInfoDevice.USB_BINTERFACECLASS, "ff");
        prop.put(USBInfoDevice.USB_BINTERFACESUBCLASS, "ff");
        prop.put(USBInfoDevice.USB_BINTERFACEPROTOCOL, "ff");

        // Other Service properties
        prop.put(USBInfoDevice.USB_BUS, Integer.valueOf("1"));
        prop.put(USBInfoDevice.USB_ADDRESS, Integer.valueOf("2"));

        return USBTracker.getInstance().addUsb(prop);
    }

    private void unregisterNewDevice(String[] parameters) {
        USBTracker.getInstance().removeUsb(parameters[0]);
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
		if (stringArray == null || stringArray.length == 0){
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

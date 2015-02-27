/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
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
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import org.osgi.service.usbinfo.USBInfoDevice;
import org.osgi.test.support.step.TestStep;

public class TestStepImpl implements TestStep {
	private static final String	SEPARATOR_COMMA	= ",";
	private static final String	SPACE			= " ";
	private static final String	EMPTY			= "";

    public String[] executeTestStep(String command, String[] parameters) {
        if (Commands.REGISTER_DEVICE.equals(command)) {
            long id = registerNewDevice(parameters);
            return new String[]{Long.toString(id)};
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
     * Parameters are the below.<br>
     * <ul>
     * <li>index 0  :"usbinfo.bcdUSB"
     * <li>index 1  :"usbinfo.bDeviceClass"
     * <li>index 2  :"usbinfo.bDeviceSubClas"
     * <li>index 3  :"usbinfo.bDeviceProtoco"
     * <li>index 4  :"usbinfo.bMaxPacketSize0"
     * <li>index 5  :"usbinfo.idVendor"
     * <li>index 6  :"usbinfo.idProduct"
     * <li>index 7  :"usbinfo.bcdDevice"
     * <li>index 8  :"usbinfo.Manufacturer"
     * <li>index 9  :"usbinfo.Product"
     * <li>index 10 :"usbinfo.SerialNumber"
     * <li>index 11 :"usbinfo.bNumConfigurations"
     * <li>index 12 :"usbinfo.bInterfaceNumber"
     * <li>index 13 :"usbinfo.bAlternateSetting"
     * <li>index 14 :"usbinfo.bNumEndpoints"
     * <li>index 15 :"usbinfo.bInterfaceClass"
     * <li>index 16 :"usbinfo.bInterfaceSubClass"
     * <li>index 17 :"usbinfo.bInterfaceProtocol"
     * <li>index 18 :"usbinfo.Interface"
     * <li>index 19 :"usbinfo.bus"
     * <li>index 20 :"usbinfo.address"
     * </ul>
     *
     */
    private long registerNewDevice(String[] parameters) {

        Dictionary prop = new Properties();

        // Device Access Category
        List category = new ArrayList();
        category.add(USBInfoDevice.DEVICE_CATEGORY);
        prop.put(org.osgi.service.device.Constants.DEVICE_CATEGORY, (String[])category.toArray(new String[0]));

        // Service properties from USB Specification
        // Device Descriptor and Service Property
        if (parameters[0] != null) {
            prop.put(USBInfoDevice.USB_BCDUSB, parameters[0]);
        }
        prop.put(USBInfoDevice.USB_BDEVICECLASS, parameters[1]);
        prop.put(USBInfoDevice.USB_BDEVICESUBCLASS, parameters[2]);
        prop.put(USBInfoDevice.USB_BDEVICEPROTOCOL, parameters[3]);
        if (parameters[4] != null) {
            prop.put(USBInfoDevice.USB_BMAXPACKETSIZE0, Integer.decode(parameters[4]));
        }
        prop.put(USBInfoDevice.USB_IDVENDOR, parameters[5]);
        prop.put(USBInfoDevice.USB_IDPRODUCT, parameters[6]);
        prop.put(USBInfoDevice.USB_BCDDEVICE, parameters[7]);
        if (parameters[8] != null) {
            prop.put(USBInfoDevice.USB_MANUFACTURER, parameters[8]);
        }
        if (parameters[9] != null) {
            prop.put(USBInfoDevice.USB_PRODUCT, parameters[9]);
        }
        if (parameters[10] != null) {
            prop.put(USBInfoDevice.USB_SERIALNUMBER, parameters[10]);
        }
        if (parameters[11] != null) {
            prop.put(USBInfoDevice.USB_BNUMCONFIGURATIONS, Integer.valueOf(parameters[11]));
        }

        // Interface Descriptor and Service Property
        prop.put(USBInfoDevice.USB_BINTERFACENUMBER, Integer.valueOf(parameters[12]));
        if (parameters[13] != null) {
            prop.put(USBInfoDevice.USB_BALTERNATESETTING, Integer.valueOf(parameters[13]));
        }
        if (parameters[14] != null) {
            prop.put(USBInfoDevice.USB_BNUMENDPOINTS, Integer.valueOf(parameters[14]));
        }
        prop.put(USBInfoDevice.USB_BINTERFACECLASS, parameters[15]);
        prop.put(USBInfoDevice.USB_BINTERFACESUBCLASS, parameters[16]);
        prop.put(USBInfoDevice.USB_BINTERFACEPROTOCOL, parameters[17]);
        if (parameters[18] != null) {
            prop.put(USBInfoDevice.USB_INTERFACE, parameters[18]);
        }

        // Other Service properties
        prop.put(USBInfoDevice.USB_BUS, Integer.valueOf(parameters[19]));
        prop.put(USBInfoDevice.USB_ADDRESS, Integer.valueOf(parameters[20]));

        return USBTracker.getInstance().addUsb(prop);
    }

    private void unregisterNewDevice(String[] parameters) {
        USBTracker.getInstance().removeUsb(Long.parseLong(parameters[0]));
    }

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
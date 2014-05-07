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


package org.osgi.test.cases.usb.util;

import java.util.StringTokenizer;

public class USBTestUtil {

    public static final String PROP_BCDUSB = "bcdUSB";
    public static final String PROP_BDEVICECLASS = "bDeviceClass";
    public static final String PROP_BDEVICESUBCLASS = "bDeviceSubClass";
    public static final String PROP_BDEVICEPROTOCOL = "bDeviceProtocol";
    public static final String PROP_IDVENDOR = "idVendor";
    public static final String PROP_IDPRODUCT = "idProduct";
    public static final String PROP_BCDDEVICE = "bcdDevice";
    public static final String PROP_IMANUFACTURER = "iManufacturer";
    public static final String PROP_IPRODUCT = "iProduct";
    public static final String PROP_ISERIALNUMBER = "iSerialNumber";
    public static final String PROP_INTERFACECLASSES = "interfaceclasses";
    public static final String PROP_BUS = "bus";
    public static final String PROP_ADDRESS = "address";

    public static final String PROP_BCDUSB_2 = "bcdUSB.2";
    public static final String PROP_BDEVICECLASS_2 = "bDeviceClass.2";
    public static final String PROP_BDEVICESUBCLASS_2 = "bDeviceSubClass.2";
    public static final String PROP_BDEVICEPROTOCOL_2 = "bDeviceProtocol.2";
    public static final String PROP_IDVENDOR_2 = "idVendor.2";
    public static final String PROP_IDPRODUCT_2 = "idProduct.2";
    public static final String PROP_BCDDEVICE_2 = "bcdDevice.2";
    public static final String PROP_IMANUFACTURER_2 = "iManufacturer.2";
    public static final String PROP_IPRODUCT_2 = "iProduct.2";
    public static final String PROP_ISERIALNUMBER_2 = "iSerialNumber.2";
    public static final String PROP_INTERFACECLASSES_2 = "interfaceclasses.2";
    public static final String PROP_BUS_2 = "bus.2";
    public static final String PROP_ADDRESS_2 = "address.2";

    public static final String PROP_SERIAL_BCDUSB = "serial.bcdUSB";
    public static final String PROP_SERIAL_BDEVICECLASS = "serial.bDeviceClass";
    public static final String PROP_SERIAL_BDEVICESUBCLASS = "serial.bDeviceSubClass";
    public static final String PROP_SERIAL_BDEVICEPROTOCOL = "serial.bDeviceProtocol";
    public static final String PROP_SERIAL_IDVENDOR = "serial.idVendor";
    public static final String PROP_SERIAL_IDPRODUCT = "serial.idProduct";
    public static final String PROP_SERIAL_BCDDEVICE = "serial.bcdDevice";
    public static final String PROP_SERIAL_INTERFACECLASSES = "serial.interfaceclasses";
    public static final String PROP_SERIAL_BUS = "serial.bus";
    public static final String PROP_SERIAL_ADDRESS = "serial.address";
    public static final String PROP_SERIAL_COMPORT = "serial.comport";

    public static final String PROP_STORAGE_BCDUSB = "storage.bcdUSB";
    public static final String PROP_STORAGE_BDEVICECLASS = "storage.bDeviceClass";
    public static final String PROP_STORAGE_BDEVICESUBCLASS = "storage.bDeviceSubClass";
    public static final String PROP_STORAGE_BDEVICEPROTOCOL = "storage.bDeviceProtocol";
    public static final String PROP_STORAGE_IDVENDOR = "storage.idVendor";
    public static final String PROP_STORAGE_IDPRODUCT = "storage.idProduct";
    public static final String PROP_STORAGE_BCDDEVICE = "storage.bcdDevice";
    public static final String PROP_STORAGE_INTERFACECLASSES = "storage.interfaceclasses";
    public static final String PROP_STORAGE_BUS = "storage.bus";
    public static final String PROP_STORAGE_ADDRESS = "storage.address";
    public static final String PROP_STORAGE_MOUNTPOINTS = "storage.mountpoints";

    private static final String SEPARATOR_COMMAS = ",";

    public static int[] toIntArray(String str) {

        StringTokenizer tokenizer = new StringTokenizer(str, SEPARATOR_COMMAS);
        int[] intArray = new int[tokenizer.countTokens()];

        for (int i = 0; i < intArray.length; i++) {
            intArray[i] = Integer.decode(tokenizer.nextToken()).intValue();
        }

        return intArray;
    }

    public static String[] toStringArray(String str) {

        StringTokenizer tokenizer = new StringTokenizer(str, SEPARATOR_COMMAS);
        String[] stringArray = new String[tokenizer.countTokens()];

        for (int i = 0; i < stringArray.length; i++) {
            stringArray[i] = tokenizer.nextToken();
        }

        return stringArray;
    }
}

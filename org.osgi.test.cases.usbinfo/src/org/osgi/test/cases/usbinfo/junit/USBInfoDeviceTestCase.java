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

package org.osgi.test.cases.usbinfo.junit;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.usbinfo.USBInfoDevice;
import org.osgi.test.cases.usbinfo.util.TestServiceListener;
import org.osgi.test.cases.usbinfo.util.USBTestProxy;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.step.TestStepProxy;

public class USBInfoDeviceTestCase extends DefaultTestBundleControl {
    private USBTestProxy	testProxy;

    protected void setUp() throws Exception {
        this.testProxy = new USBTestProxy(new TestStepProxy(getContext()));
    }

    protected void tearDown() throws Exception {
        this.testProxy.close();
    }

    /**
     * Tests register a USBInfoDevice service.
     */
    public void testRegisterDevice01() {
        String[] ids = null;
        try {
			TestServiceListener<USBInfoDevice> listener = new TestServiceListener<>(
					ServiceEvent.REGISTERED);
            getContext().addServiceListener(listener, "(objectClass=org.osgi.service.usbinfo.USBInfoDevice)");

            String command = "registerDevice";
            String message = "[TEST-RD01] Connect a device.";
            ids = testProxy.executeTestStep(command, message, new String[]{});

            getContext().removeServiceListener(listener);

            assertEquals("Only one USBInfoDevice service event is expected.", 1, listener.size());
			ServiceReference<USBInfoDevice> ref = listener.get(0);
            String[] category = (String[]) ref.getProperty(org.osgi.service.device.Constants.DEVICE_CATEGORY);
            boolean categoryFlag = false;
            for (int i = 0; i < category.length; i++) {
                if (category[i].equals(USBInfoDevice.DEVICE_CATEGORY)) {
                    categoryFlag = true;
                    break;
                }
            }
            assertTrue("The following service property is not correct: " + org.osgi.service.device.Constants.DEVICE_CATEGORY, categoryFlag);

            // usbinfo.bDeviceClass: String, Hexadecimal, 2-digits.
            Object value = ref.getProperty(USBInfoDevice.USB_BDEVICECLASS);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BDEVICECLASS, value instanceof String);
            assertEquals("The following service property is not correct: " + USBInfoDevice.USB_BDEVICECLASS, 2, ((String) value).length());
            try {
                Integer.parseInt(((String) value), 16);
            } catch (NumberFormatException e) {
                fail("The following service property is not correct: " + USBInfoDevice.USB_BDEVICECLASS, e);
            }

            // usbinfo.bDeviceSubClass: String, Hexadecimal, 2-digits.
            value = ref.getProperty(USBInfoDevice.USB_BDEVICESUBCLASS);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BDEVICESUBCLASS, value instanceof String);
            assertEquals("The following service property is not correct: " + USBInfoDevice.USB_BDEVICESUBCLASS, 2, ((String) value).length());
            try {
                Integer.parseInt(((String) value), 16);
            } catch (NumberFormatException e) {
                fail("The following service property is not correct: " + USBInfoDevice.USB_BDEVICESUBCLASS, e);
            }

            // usbinfo.bDeviceProtocol: String, Hexadecimal, 2-digits.
            value = ref.getProperty(USBInfoDevice.USB_BDEVICEPROTOCOL);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BDEVICEPROTOCOL, value instanceof String);
            assertEquals("The following service property is not correct: " + USBInfoDevice.USB_BDEVICEPROTOCOL, 2, ((String) value).length());
            try {
                Integer.parseInt(((String) value), 16);
            } catch (NumberFormatException e) {
                fail("The following service property is not correct: " + USBInfoDevice.USB_BDEVICEPROTOCOL, e);
            }

            // usbinfo.idVendor: String, Hexadecimal, 4-digits.
            value = ref.getProperty(USBInfoDevice.USB_IDVENDOR);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_IDVENDOR, value instanceof String);
            assertEquals("The following service property is not correct: " + USBInfoDevice.USB_IDVENDOR, 4, ((String) value).length());
            try {
                Integer.parseInt(((String) value), 16);
            } catch (NumberFormatException e) {
            	fail("The following service property is not correct: " + USBInfoDevice.USB_IDVENDOR, e);
            }

            // usbinfo.idProduct: String, Hexadecimal, 4-digits.
            value = ref.getProperty(USBInfoDevice.USB_IDPRODUCT);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_IDPRODUCT, value instanceof String);
            assertEquals("The following service property is not correct: " + USBInfoDevice.USB_IDPRODUCT, 4, ((String) value).length());
            try {
                Integer.parseInt(((String) value), 16);
            } catch (NumberFormatException e) {
                fail("The following service property is not correct: " + USBInfoDevice.USB_IDPRODUCT, e);
            }

            // usbinfo.bcdDevice: String, BCD format, 4-digits.
            value = ref.getProperty(USBInfoDevice.USB_BCDDEVICE);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BCDDEVICE, value instanceof String);
            assertEquals("The following service property is not correct: " + USBInfoDevice.USB_BCDDEVICE, 4, ((String) value).length());
            try {
                Integer.parseInt(((String) value), 10);
            } catch (NumberFormatException e) {
                fail("The following service property is not correct: " + USBInfoDevice.USB_BCDDEVICE, e);
            }

            // usbinfo.bInterfaceNumber: Integer.
            value = ref.getProperty(USBInfoDevice.USB_BINTERFACENUMBER);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BINTERFACENUMBER, value instanceof Integer);

            // usbinfo.bInterfaceClass: String, Hexadecimal, 2-digits.
            value = ref.getProperty(USBInfoDevice.USB_BINTERFACECLASS);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BINTERFACECLASS, value instanceof String);
            assertEquals("The following service property is not correct: " + USBInfoDevice.USB_BINTERFACECLASS, 2, ((String) value).length());
            try {
                Integer.parseInt(((String) value), 16);
            } catch (NumberFormatException e) {
                fail("The following service property is not correct: " + USBInfoDevice.USB_BINTERFACECLASS, e);
            }

            // usbinfo.bInterfaceSubClass: String, Hexadecimal, 2-digits.
            value = ref.getProperty(USBInfoDevice.USB_BINTERFACESUBCLASS);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BINTERFACESUBCLASS, value instanceof String);
            assertEquals("The following service property is not correct: " + USBInfoDevice.USB_BINTERFACESUBCLASS, 2, ((String) value).length());
            try {
                Integer.parseInt(((String) value), 16);
            } catch (NumberFormatException e) {
                fail("The following service property is not correct: " + USBInfoDevice.USB_BINTERFACESUBCLASS, e);
            }

            // usbinfo.bInterfaceProtocol: String, Hexadecimal, 2-digits.
            value = ref.getProperty(USBInfoDevice.USB_BINTERFACEPROTOCOL);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BINTERFACEPROTOCOL, value instanceof String);
            assertEquals("The following service property is not correct: " + USBInfoDevice.USB_BINTERFACEPROTOCOL, 2, ((String) value).length());
            try {
                Integer.parseInt(((String) value), 16);
            } catch (NumberFormatException e) {
                fail("The following service property is not correct: " + USBInfoDevice.USB_BINTERFACEPROTOCOL, e);
            }

            // usbinfo.bus: Integer.
            value = ref.getProperty(USBInfoDevice.USB_BUS);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BUS, value instanceof Integer);

            // usbinfo.address: Integer(001-127).
            value = ref.getProperty(USBInfoDevice.USB_ADDRESS);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_ADDRESS, value instanceof Integer);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_ADDRESS, ((Integer) value).intValue() >= 1);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_ADDRESS, ((Integer) value).intValue() <= 127);

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
                testProxy.executeTestStep("unregisterDevice", "Disconnect the remaining device.", new String[] {ids[0]});
            }
        }
    }

    /**
     * Tests register two USBInfoDevice services.
     */
    public void testRegisterDevice02() {
        String[] ids = null;
        String[] ids2 = null;
        try {
			TestServiceListener<USBInfoDevice> listener = new TestServiceListener<>(
					ServiceEvent.REGISTERED);
            getContext().addServiceListener(listener, "(objectClass=org.osgi.service.usbinfo.USBInfoDevice)");

            String command = "registerDevice";
            String message = "[TEST-RD02] Connect the first device.";
            ids = testProxy.executeTestStep(command, message, new String[]{});

            String message2 = "Connect the second device.";
            ids2 = testProxy.executeTestStep(command, message2, new String[]{});

            getContext().removeServiceListener(listener);

            assertEquals("Two USBInfoDevice service events are expected.", 2, listener.size());
			ServiceReference<USBInfoDevice> ref = listener.get(0);
            String[] category = (String[]) ref.getProperty(org.osgi.service.device.Constants.DEVICE_CATEGORY);
            boolean categoryFlag = false;
            for (int i = 0; i < category.length; i++) {
                if (category[i].equals(USBInfoDevice.DEVICE_CATEGORY)) {
                    categoryFlag = true;
                    break;
                }
            }
            assertTrue("The following service property is not correct: " + org.osgi.service.device.Constants.DEVICE_CATEGORY, categoryFlag);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BDEVICECLASS, ref.getProperty(USBInfoDevice.USB_BDEVICECLASS) instanceof String);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BDEVICESUBCLASS, ref.getProperty(USBInfoDevice.USB_BDEVICESUBCLASS) instanceof String);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BDEVICEPROTOCOL, ref.getProperty(USBInfoDevice.USB_BDEVICEPROTOCOL) instanceof String);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_IDVENDOR, ref.getProperty(USBInfoDevice.USB_IDVENDOR) instanceof String);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_IDPRODUCT, ref.getProperty(USBInfoDevice.USB_IDPRODUCT) instanceof String);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BCDDEVICE, ref.getProperty(USBInfoDevice.USB_BCDDEVICE) instanceof String);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BINTERFACENUMBER, ref.getProperty(USBInfoDevice.USB_BINTERFACENUMBER) instanceof Integer);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BINTERFACECLASS, ref.getProperty(USBInfoDevice.USB_BINTERFACECLASS) instanceof String);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BINTERFACESUBCLASS, ref.getProperty(USBInfoDevice.USB_BINTERFACESUBCLASS) instanceof String);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BINTERFACEPROTOCOL, ref.getProperty(USBInfoDevice.USB_BINTERFACEPROTOCOL) instanceof String);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BUS, ref.getProperty(USBInfoDevice.USB_BUS) instanceof Integer);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_ADDRESS, ref.getProperty(USBInfoDevice.USB_ADDRESS) instanceof Integer);


            ref = listener.get(1);
            category = (String[]) ref.getProperty(org.osgi.service.device.Constants.DEVICE_CATEGORY);
            categoryFlag = false;
            for (int i = 0; i < category.length; i++) {
                if (category[i].equals(USBInfoDevice.DEVICE_CATEGORY)) {
                    categoryFlag = true;
                    break;
                }
            }
            assertTrue("The following service property is not correct: " + org.osgi.service.device.Constants.DEVICE_CATEGORY, categoryFlag);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BDEVICECLASS, ref.getProperty(USBInfoDevice.USB_BDEVICECLASS) instanceof String);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BDEVICESUBCLASS, ref.getProperty(USBInfoDevice.USB_BDEVICESUBCLASS) instanceof String);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BDEVICEPROTOCOL, ref.getProperty(USBInfoDevice.USB_BDEVICEPROTOCOL) instanceof String);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_IDVENDOR, ref.getProperty(USBInfoDevice.USB_IDVENDOR) instanceof String);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_IDPRODUCT, ref.getProperty(USBInfoDevice.USB_IDPRODUCT) instanceof String);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BCDDEVICE, ref.getProperty(USBInfoDevice.USB_BCDDEVICE) instanceof String);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BINTERFACENUMBER, ref.getProperty(USBInfoDevice.USB_BINTERFACENUMBER) instanceof Integer);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BINTERFACECLASS, ref.getProperty(USBInfoDevice.USB_BINTERFACECLASS) instanceof String);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BINTERFACESUBCLASS, ref.getProperty(USBInfoDevice.USB_BINTERFACESUBCLASS) instanceof String);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BINTERFACEPROTOCOL, ref.getProperty(USBInfoDevice.USB_BINTERFACEPROTOCOL) instanceof String);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_BUS, ref.getProperty(USBInfoDevice.USB_BUS) instanceof Integer);
            assertTrue("The following service property is not correct: " + USBInfoDevice.USB_ADDRESS, ref.getProperty(USBInfoDevice.USB_ADDRESS) instanceof Integer);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
                testProxy.executeTestStep("unregisterDevice", "Discconect the remaining device.", new String[] {ids[0]});
            }
            if (ids2 != null) {
                testProxy.executeTestStep("unregisterDevice", "Discconect the remaining device.", new String[] {ids2[0]});
            }
        }
    }

    /**
     * Tests unregister a USBInfoDevice service.
     */
    public void testUnregisterDevice01() {
        String[] ids = null;
        try {
            String command = "registerDevice";
            String message = "[TEST-UD01] Connect a device.";
            ids = testProxy.executeTestStep(command, message, new String[]{});

			TestServiceListener<USBInfoDevice> listener = new TestServiceListener<>(
					ServiceEvent.UNREGISTERING);
            getContext().addServiceListener(listener, "(objectClass=org.osgi.service.usbinfo.USBInfoDevice)");

            command = "unregisterDevice";
            message = "Disonnect the device.";
            testProxy.executeTestStep(command, message, new String[] {ids[0]});

            getContext().removeServiceListener(listener);

            assertEquals("Only one USBInfoDevice service event is expected.", 1, listener.size());
            Long registerId = Long.valueOf(ids[0]);
            Long unregisterId = (Long) listener.get(0).getProperty(Constants.SERVICE_ID);
            assertEquals("The unregisterd service is not correct.", registerId, unregisterId);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        }
    }

    /**
     * Tests unregister a USBInfoDevice service. 1. register A 2. register B 3.
     * unregister A
     */
    public void testUnregisterDevice02() {
        String[] ids = null;
        String[] ids2 = null;
        try {
            String command = "registerDevice";
            String message = "[TEST-UD02] Connect the first device.";
            ids = testProxy.executeTestStep(command, message, new String[]{});

            message = "Connect the second device.";
            ids2 = testProxy.executeTestStep(command, message, new String[]{});

			TestServiceListener<USBInfoDevice> listener = new TestServiceListener<>(
					ServiceEvent.UNREGISTERING);
            getContext().addServiceListener(listener, "(objectClass=org.osgi.service.usbinfo.USBInfoDevice)");

            command = "unregisterDevice";
            message = "Disconnect the first device.";
            testProxy.executeTestStep(command, message, new String[] {ids[0]});

            getContext().removeServiceListener(listener);

            assertEquals("Only one USBInfoDevice service event is expected.", 1, listener.size());
            Long registerId = Long.valueOf(ids[0]);
            Long unregisterId = (Long) listener.get(0).getProperty(Constants.SERVICE_ID);
            assertEquals("The unregisterd service is not correct.", registerId, unregisterId);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids2 != null) {
                testProxy.executeTestStep("unregisterDevice", "Discconect the remaining device.", new String[] {ids2[0]});
            }
        }
    }

    /**
     * Tests unregister a USBInfoDevice service. 1. register A 2. register B 3.
     * unregister A 4. unregister B
     */
    public void testUnregisterDevice03() {
        String[] ids = null;
        String[] ids2 = null;
        try {
            String command = "registerDevice";
            String message = "[TEST-UD03] Connect the first device.";
            ids = testProxy.executeTestStep(command, message, new String[]{});

            message = "Connect the second device.";
            ids2 = testProxy.executeTestStep(command, message, new String[]{});

			TestServiceListener<USBInfoDevice> listener = new TestServiceListener<>(
					ServiceEvent.UNREGISTERING);
            getContext().addServiceListener(listener, "(objectClass=org.osgi.service.usbinfo.USBInfoDevice)");

            command = "unregisterDevice";
            message = "Disconnect the first device.";
            testProxy.executeTestStep(command, message, new String[] {ids[0]});

            message = "Disconnect the second device.";
            testProxy.executeTestStep(command, message, new String[] {ids2[0]});

            getContext().removeServiceListener(listener);

            assertEquals("Only two USBInfoDevice service events are expected.", 2, listener.size());
            Long registerId = Long.valueOf(ids[0]);
            Long unregisterId = (Long) listener.get(0).getProperty(Constants.SERVICE_ID);
            assertEquals("The unregister service is not correct.", registerId, unregisterId);
            registerId = Long.valueOf(ids2[0]);
            unregisterId = (Long) listener.get(1).getProperty(Constants.SERVICE_ID);
            assertEquals("The unregister service is not correct.", registerId, unregisterId);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        }
    }
}

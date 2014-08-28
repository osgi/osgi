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
package org.osgi.test.cases.serial.junit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.serial.PortInUseException;
import org.osgi.service.serial.SerialConnection;
import org.osgi.service.serial.SerialDevice;
import org.osgi.service.serial.UnsupportedCommOperationException;
import org.osgi.service.serial.usb.USBSerialDevice;
import org.osgi.test.cases.serial.util.TestServiceListener;
import org.osgi.test.cases.step.TestStep;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class SerialDeviceTestCase extends DefaultTestBundleControl {

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Tests service register/unregister.
     */
    public void testSerialDevice01() {

        TestStep testStep = null;

        String serialComport1 = System.getProperty("serial.comport.1");
        String busType1 = System.getProperty("bus.type.1");

        try {
            String filter = "(" +  Constants.OBJECTCLASS + "=" + SerialDevice.class.getName() + ")";
            TestServiceListener regListener = new TestServiceListener(ServiceEvent.REGISTERED);
            TestServiceListener unregListener = new TestServiceListener(ServiceEvent.UNREGISTERING);
            getContext().addServiceListener(regListener, filter);
            getContext().addServiceListener(unregListener, filter);

            testStep = (TestStep) getService(TestStep.class, "(" + Constants.SERVICE_PID + "=org.osgi.service.serial)");
            testStep.execute("add", new String[] {serialComport1, busType1});

            assertEquals(1, regListener.size());

            ServiceReference ref = regListener.get(0);
            Long serviceId1 = (Long) ref.getProperty(Constants.SERVICE_ID);

            testStep.execute("remove", new String[] {serialComport1});

            assertEquals(1, unregListener.size());

            ref = unregListener.get(0);
            assertEquals(serviceId1, ref.getProperty(Constants.SERVICE_ID));

            getContext().removeServiceListener(regListener);
            getContext().removeServiceListener(unregListener);

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);

        } finally {
            testStep.execute("remove", new String[] {serialComport1});
        }
    }

    /**
     * Tests services register/unregister.
     */
    public void testSerialDevice02() {

        TestStep testStep = null;

        String serialComport1 = System.getProperty("serial.comport.1");
        String busType1 = System.getProperty("bus.type.1");
        String serialComport2 = System.getProperty("serial.comport.2");
        String busType2 = System.getProperty("bus.type.2");

        try {
            String filter = "(" +  Constants.OBJECTCLASS + "=" + SerialDevice.class.getName() + ")";
            TestServiceListener regListener = new TestServiceListener(ServiceEvent.REGISTERED);
            TestServiceListener unregListener = new TestServiceListener(ServiceEvent.UNREGISTERING);
            getContext().addServiceListener(regListener, filter);
            getContext().addServiceListener(unregListener, filter);

            testStep = (TestStep) getService(TestStep.class, "(" + Constants.SERVICE_PID + "=org.osgi.service.serial)");
            testStep.execute("add", new String[] {serialComport1, busType1});

            assertEquals(1, regListener.size());

            ServiceReference ref = regListener.get(0);
            Long serviceId1 = (Long) ref.getProperty(Constants.SERVICE_ID);

            testStep = (TestStep) getService(TestStep.class, "(" + Constants.SERVICE_PID + "=org.osgi.service.serial)");
            testStep.execute("add", new String[] {serialComport2, busType2});

            assertEquals(2, regListener.size());

            ref = regListener.get(1);
            Long serviceId2 = (Long) ref.getProperty(Constants.SERVICE_ID);


            testStep.execute("remove", new String[] {serialComport1});

            assertEquals(1, unregListener.size());

            ref = unregListener.get(0);
            assertEquals(serviceId1, ref.getProperty(Constants.SERVICE_ID));

            testStep.execute("remove", new String[] {serialComport2});

            assertEquals(2, unregListener.size());

            ref = unregListener.get(1);
            assertEquals(serviceId2, ref.getProperty(Constants.SERVICE_ID));

            getContext().removeServiceListener(regListener);
            getContext().removeServiceListener(unregListener);

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);

        } finally {
            testStep.execute("remove", new String[] {serialComport1});
            testStep.execute("remove", new String[] {serialComport2});
        }
    }

    /**
     * Tests SerialDevice service properties.
     */
    public void testSerialDevice03() {

        TestStep testStep = null;

        String serialComport = System.getProperty("serial.comport.1");
        String busType = System.getProperty("bus.type.1");

        try {
            TestServiceListener listener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(listener, "(" +  Constants.OBJECTCLASS + "=" + SerialDevice.class.getName() + ")");

            testStep = (TestStep) getService(TestStep.class, "(" + Constants.SERVICE_PID + "=org.osgi.service.serial)");
            testStep.execute("add", new String[] {serialComport, busType});

            getContext().removeServiceListener(listener);

            ServiceReference ref = listener.get(0);

            assertTrue(ref.getProperty(SerialDevice.SERIAL_COMPORT) instanceof String);
            assertEquals(serialComport, ref.getProperty(SerialDevice.SERIAL_COMPORT));

            assertEquals(SerialDevice.EMPTY_STRING, ref.getProperty(SerialDevice.CURRENT_OWNER));

            SerialDevice serialDevice = (SerialDevice) getContext().getService(ref);
            String appname = "test";
            SerialConnection serialConnection = serialDevice.open(appname, 1000);

            assertTrue(ref.getProperty(SerialDevice.CURRENT_OWNER) instanceof String);
            assertEquals(appname, ref.getProperty(SerialDevice.CURRENT_OWNER));

            serialConnection.close();

            assertEquals(SerialDevice.EMPTY_STRING, ref.getProperty(SerialDevice.CURRENT_OWNER));

            assertTrue(ref.getProperty(org.osgi.service.device.Constants.DEVICE_CATEGORY) instanceof String);
            assertEquals(SerialDevice.DEVICE_CATEGORY, ref.getProperty(org.osgi.service.device.Constants.DEVICE_CATEGORY));

            if (busType != null && busType.length() > 0) {
                assertTrue(ref.getProperty(SerialDevice.BUS_TYPE) instanceof String);
                assertEquals(busType, ref.getProperty(SerialDevice.BUS_TYPE));
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);

        } finally {
            testStep.execute("remove", new String[] {serialComport});
        }
    }

    /**
     * Tests open and close.
     */
    public void testSerialDevice04() {

        TestStep testStep = null;

        String serialComport = System.getProperty("serial.comport.1");
        String busType = System.getProperty("bus.type.1");

        try {
            TestServiceListener listener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(listener, "(" +  Constants.OBJECTCLASS + "=" + SerialDevice.class.getName() + ")");

            testStep = (TestStep) getService(TestStep.class, "(" + Constants.SERVICE_PID + "=org.osgi.service.serial)");
            testStep.execute("add", new String[] {serialComport, busType});

            getContext().removeServiceListener(listener);

            SerialDevice serialDevice = (SerialDevice) getContext().getService(listener.get(0));
            SerialConnection serialConnection = serialDevice.open("test1", 1000);
            assertNotNull(serialConnection);

            serialConnection.close();
            serialConnection.close();


            MyRunnable run1 = new MyRunnable(serialDevice, 5000);
            new Thread(run1).start();
            Thread.sleep(1000);

            serialConnection = serialDevice.open("test2", 10000);
            assertNotNull(serialConnection);

            serialConnection.close();
            MyRunnable run2 = new MyRunnable(serialDevice, 15000);
            new Thread(run2).start();
            Thread.sleep(1000);

            try {
                serialConnection = serialDevice.open("test3", 10000);
                fail("Not throw PortInUseException.");

            } catch (PortInUseException e) {
                trace("catch PortInUseException.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);

        } finally {
            testStep.execute("remove", new String[] {serialComport});
        }
    }

    /**
     * Tests USB-SerialDevice service properties.
     */
    public void testSerialDevice05() {

        TestStep testStep = null;

        String serialComport = System.getProperty("serial.comport.1");
        String usbBus = System.getProperty("usb.bus");
        String usbAddress = System.getProperty("usb.address");
        String usbBcdUSB = System.getProperty("usb.bcdUSB");
        String usbBDeviceClass = System.getProperty("usb.bDeviceClass");
        String usbBDeviceSubClass = System.getProperty("usb.bDeviceSubClass");
        String usbBDeviceProtocol = System.getProperty("usb.bDeviceProtocol");
        String usbBMaxPacketSize0 = System.getProperty("usb.bMaxPacketSize0");
        String usbIdVendor = System.getProperty("usb.idVendor");
        String usbIdProduct = System.getProperty("usb.idProduct");
        String usbBcdDevice = System.getProperty("usb.bcdDevice");
        String usbManufacturer = System.getProperty("usb.Manufacturer");
        String deviceDescription = System.getProperty("device.description");
        String deviceSerial = System.getProperty("device.serial");
        String usbBNumConfigurations = System.getProperty("usb.bNumConfigurations");
        String usbBInterfaceNumber = System.getProperty("usb.bInterfaceNumber");
        String usbBAlternateSetting = System.getProperty("usb.bAlternateSetting");
        String usbBNumEndpoints = System.getProperty("usb.bNumEndpoints");
        String usbBInterfaceClass = System.getProperty("usb.bInterfaceClass");
        String usbBInterfaceSubClass = System.getProperty("usb.bInterfaceSubClass");
        String usbBInterfaceProtocol = System.getProperty("usb.bInterfaceProtocol");
        String usbInterface = System.getProperty("usb.Interface");

        try {
            TestServiceListener listener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(listener, "(" +  Constants.OBJECTCLASS + "=" + SerialDevice.class.getName() + ")");

            String[] parameters = new String[]{
                    serialComport, usbBus, usbAddress, usbBcdUSB, usbBDeviceClass, usbBDeviceSubClass, usbBDeviceProtocol,
                    usbBMaxPacketSize0, usbIdVendor, usbIdProduct, usbBcdDevice, usbManufacturer, deviceDescription, deviceSerial,
                    usbBNumConfigurations, usbBInterfaceNumber, usbBAlternateSetting, usbBNumEndpoints, usbBInterfaceClass,
                    usbBInterfaceSubClass, usbBInterfaceProtocol, usbInterface
                    };

            testStep = (TestStep) getService(TestStep.class, "(" + Constants.SERVICE_PID + "=org.osgi.service.serial)");
            testStep.execute("addUSB", parameters);

            getContext().removeServiceListener(listener);

            ServiceReference ref = listener.get(0);

            assertTrue(ref.getProperty(SerialDevice.BUS_TYPE) instanceof String);
            assertEquals(USBSerialDevice.BUS_TYPE_USB, ref.getProperty(SerialDevice.BUS_TYPE));

            assertTrue(ref.getProperty(USBSerialDevice.USB_BUS) instanceof Integer);
            assertEquals(new Integer(usbBus), ref.getProperty(USBSerialDevice.USB_BUS));

            assertTrue(ref.getProperty(USBSerialDevice.USB_ADDRESS) instanceof Integer);
            assertEquals(new Integer(usbAddress), ref.getProperty(USBSerialDevice.USB_ADDRESS));

            if (usbBcdUSB != null) {
                assertTrue(ref.getProperty(USBSerialDevice.USB_BCDUSB) instanceof String);
                assertEquals(usbBcdUSB, ref.getProperty(USBSerialDevice.USB_BCDUSB));
            }

            assertTrue(ref.getProperty(USBSerialDevice.USB_BDEVICECLASS) instanceof String);
            assertEquals(usbBDeviceClass, ref.getProperty(USBSerialDevice.USB_BDEVICECLASS));

            assertTrue(ref.getProperty(USBSerialDevice.USB_BDEVICESUBCLASS) instanceof String);
            assertEquals(usbBDeviceSubClass, ref.getProperty(USBSerialDevice.USB_BDEVICESUBCLASS));

            assertTrue(ref.getProperty(USBSerialDevice.USB_BDEVICEPROTOCOL) instanceof String);
            assertEquals(usbBDeviceProtocol, ref.getProperty(USBSerialDevice.USB_BDEVICEPROTOCOL));

            if (usbBMaxPacketSize0 != null) {
                assertTrue(ref.getProperty(USBSerialDevice.USB_BMAXPACKETSIZE0) instanceof Integer);
                assertEquals(new Integer(usbBMaxPacketSize0), ref.getProperty(USBSerialDevice.USB_BMAXPACKETSIZE0));
            }

            assertTrue(ref.getProperty(USBSerialDevice.USB_IDVENDOR) instanceof String);
            assertEquals(usbIdVendor, ref.getProperty(USBSerialDevice.USB_IDVENDOR));

            assertTrue(ref.getProperty(USBSerialDevice.USB_IDPRODUCT) instanceof String);
            assertEquals(usbIdProduct, ref.getProperty(USBSerialDevice.USB_IDPRODUCT));

            assertTrue(ref.getProperty(USBSerialDevice.USB_BCDDEVICE) instanceof String);
            assertEquals(usbBcdDevice, ref.getProperty(USBSerialDevice.USB_BCDDEVICE));

            if (usbManufacturer != null) {
                assertTrue(ref.getProperty(USBSerialDevice.USB_MANUFACTURER) instanceof String);
                assertEquals(usbManufacturer, ref.getProperty(USBSerialDevice.USB_MANUFACTURER));
            }

            if (deviceDescription != null) {
                assertTrue(ref.getProperty(org.osgi.service.device.Constants.DEVICE_DESCRIPTION) instanceof String);
                assertEquals(deviceDescription, ref.getProperty(org.osgi.service.device.Constants.DEVICE_DESCRIPTION));
            }

            if (deviceSerial != null) {
                assertTrue(ref.getProperty(org.osgi.service.device.Constants.DEVICE_SERIAL) instanceof String);
                assertEquals(deviceSerial, ref.getProperty(org.osgi.service.device.Constants.DEVICE_SERIAL));
            }

            if (usbBNumConfigurations != null) {
                assertTrue(ref.getProperty(USBSerialDevice.USB_BNUMCONFIGURATIONS) instanceof Integer);
                assertEquals(new Integer(usbBNumConfigurations), ref.getProperty(USBSerialDevice.USB_BNUMCONFIGURATIONS));
            }

            assertTrue(ref.getProperty(USBSerialDevice.USB_BINTERFACENUMBER) instanceof Integer);
            assertEquals(new Integer(usbBInterfaceNumber), ref.getProperty(USBSerialDevice.USB_BINTERFACENUMBER));

            if (usbBAlternateSetting != null) {
                assertTrue(ref.getProperty(USBSerialDevice.USB_BALTERNATESETTING) instanceof Integer);
                assertEquals(new Integer(usbBAlternateSetting), ref.getProperty(USBSerialDevice.USB_BALTERNATESETTING));
            }

            if (usbBNumEndpoints != null) {
                assertTrue(ref.getProperty(USBSerialDevice.USB_BNUMENDPOINTS) instanceof Integer);
                assertEquals(new Integer(usbBNumEndpoints), ref.getProperty(USBSerialDevice.USB_BNUMENDPOINTS));
            }

            assertTrue(ref.getProperty(USBSerialDevice.USB_BINTERFACECLASS) instanceof String);
            assertEquals(usbBInterfaceClass, ref.getProperty(USBSerialDevice.USB_BINTERFACECLASS));

            assertTrue(ref.getProperty(USBSerialDevice.USB_BINTERFACESUBCLASS) instanceof String);
            assertEquals(usbBInterfaceSubClass, ref.getProperty(USBSerialDevice.USB_BINTERFACESUBCLASS));

            assertTrue(ref.getProperty(USBSerialDevice.USB_BINTERFACEPROTOCOL) instanceof String);
            assertEquals(usbBInterfaceProtocol, ref.getProperty(USBSerialDevice.USB_BINTERFACEPROTOCOL));

            if (usbInterface != null) {
                assertTrue(ref.getProperty(USBSerialDevice.USB_INTERFACE) instanceof String);
                assertEquals(usbInterface, ref.getProperty(USBSerialDevice.USB_INTERFACE));
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);

        } finally {
            testStep.execute("remove", new String[] {serialComport});
        }
    }

    /**
     * Tests SerialConnection stream.
     */
    public void testSerialDevice06() {

        TestStep testStep = null;

        String serialComport = System.getProperty("serial.comport.1");
        String busType = System.getProperty("bus.type.1");

        try {
            TestServiceListener listener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(listener, "(" +  Constants.OBJECTCLASS + "=" + SerialDevice.class.getName() + ")");

            testStep = (TestStep) getService(TestStep.class, "(" + Constants.SERVICE_PID + "=org.osgi.service.serial)");
            testStep.execute("add", new String[] {serialComport, busType});

            getContext().removeServiceListener(listener);

            SerialDevice serialDevice = (SerialDevice) getContext().getService(listener.get(0));
            SerialConnection serialConnection = serialDevice.open("test1", 1000);
            boolean supportInput = Boolean.getBoolean("support.inputstream");

            InputStream is = serialConnection.getInputStream();
            if (supportInput) {
                assertNotNull(is);
            } else {
                assertNull(is);
            }
            serialConnection.close();
            if (supportInput) {
                try {
                    is = serialConnection.getInputStream();
                    fail("Not throw IOException.");

                } catch (IOException e) {
                    trace("catch IOException.");
                }
            } else {
                is = serialConnection.getInputStream();
                assertNull(is);
            }

            serialConnection = serialDevice.open("test1", 1000);
            boolean supportOutput = Boolean.getBoolean("support.outputstream");

            OutputStream os = serialConnection.getOutputStream();
            if (supportOutput) {
                assertNotNull(os);
            } else {
                assertNull(os);
            }
            serialConnection.close();
            if (supportOutput) {
                try {
                    os = serialConnection.getOutputStream();
                    fail("Not throw IOException.");

                } catch (IOException e) {
                    trace("catch IOException.");
                }
            } else {
                os = serialConnection.getOutputStream();
                assertNull(is);
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);

        } finally {
            testStep.execute("remove", new String[] {serialComport});
        }
    }

    /**
     * Tests SerialConnection getter (default).
     */
    public void testSerialDevice07() {

        TestStep testStep = null;

        String serialComport = System.getProperty("serial.comport.1");
        String busType = System.getProperty("bus.type.1");

        try {
            TestServiceListener listener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(listener, "(" +  Constants.OBJECTCLASS + "=" + SerialDevice.class.getName() + ")");

            testStep = (TestStep) getService(TestStep.class, "(" + Constants.SERVICE_PID + "=org.osgi.service.serial)");
            testStep.execute("add", new String[] {serialComport, busType});

            getContext().removeServiceListener(listener);

            SerialDevice serialDevice = (SerialDevice) getContext().getService(listener.get(0));
            SerialConnection serialConnection = serialDevice.open("test1", 1000);

            assertEquals(9600, serialConnection.getBaudRate());
            assertEquals(SerialConnection.DATABITS_8, serialConnection.getDataBits());

            boolean flowControlModeFlag = false;
            int flowControlMode = serialConnection.getFlowControlMode();
            if (flowControlMode == SerialConnection.FLOWCONTROL_NONE
                    || flowControlMode == SerialConnection.FLOWCONTROL_RTSCTS_IN
                    || flowControlMode == SerialConnection.FLOWCONTROL_RTSCTS_OUT
                    || flowControlMode == SerialConnection.FLOWCONTROL_XONXOFF_IN
                    || flowControlMode == SerialConnection.FLOWCONTROL_XONXOFF_OUT) {

                flowControlModeFlag = true;
            }

            assertTrue(flowControlModeFlag);
            assertEquals(SerialConnection.PARITY_NONE, serialConnection.getParity());
            assertEquals(SerialConnection.STOPBITS_1, serialConnection.getStopBits());

            serialConnection.isDTR();
            serialConnection.isRTS();

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);

        } finally {
            testStep.execute("remove", new String[] {serialComport});
        }
    }

    /**
     * Tests SerialConnection setter/getter.
     */
    public void testSerialDevice08() {

        TestStep testStep = null;

        String serialComport = System.getProperty("serial.comport.1");
        String busType = System.getProperty("bus.type.1");

        try {
            TestServiceListener listener = new TestServiceListener(ServiceEvent.REGISTERED);
            getContext().addServiceListener(listener, "(" +  Constants.OBJECTCLASS + "=" + SerialDevice.class.getName() + ")");

            testStep = (TestStep) getService(TestStep.class, "(" + Constants.SERVICE_PID + "=org.osgi.service.serial)");
            testStep.execute("add", new String[] {serialComport, busType});

            getContext().removeServiceListener(listener);

            SerialDevice serialDevice = (SerialDevice) getContext().getService(listener.get(0));
            SerialConnection serialConnection = serialDevice.open("test1", 1000);

            // setDTR test
            serialConnection.setDTR(true);
            assertTrue(serialConnection.isDTR());
            serialConnection.setDTR(false);
            assertFalse(serialConnection.isDTR());

            // setFlowControlMode test
            try {
                serialConnection.setFlowControlMode(SerialConnection.FLOWCONTROL_NONE);
                assertEquals(SerialConnection.FLOWCONTROL_NONE, serialConnection.getFlowControlMode());
            } catch (UnsupportedCommOperationException e) {
                trace("Unsupported operation FLOWCONTROL_NONE.");
            }
            try {
                serialConnection.setFlowControlMode(SerialConnection.FLOWCONTROL_RTSCTS_IN);
                assertEquals(SerialConnection.FLOWCONTROL_RTSCTS_IN, serialConnection.getFlowControlMode());
            } catch (UnsupportedCommOperationException e) {
                trace("Unsupported operation FLOWCONTROL_RTSCTS_IN.");
            }
            try {
                serialConnection.setFlowControlMode(SerialConnection.FLOWCONTROL_RTSCTS_OUT);
                assertEquals(SerialConnection.FLOWCONTROL_RTSCTS_OUT, serialConnection.getFlowControlMode());
            } catch (UnsupportedCommOperationException e) {
                trace("Unsupported operation FLOWCONTROL_RTSCTS_OUT.");
            }
            try {
                serialConnection.setFlowControlMode(SerialConnection.FLOWCONTROL_XONXOFF_IN);
                assertEquals(SerialConnection.FLOWCONTROL_XONXOFF_IN, serialConnection.getFlowControlMode());
            } catch (UnsupportedCommOperationException e) {
                trace("Unsupported operation FLOWCONTROL_XONXOFF_IN.");
            }
            try {
                serialConnection.setFlowControlMode(SerialConnection.FLOWCONTROL_XONXOFF_OUT);
                assertEquals(SerialConnection.FLOWCONTROL_XONXOFF_OUT, serialConnection.getFlowControlMode());
            } catch (UnsupportedCommOperationException e) {
                trace("Unsupported operation FLOWCONTROL_XONXOFF_OUT.");
            }
            try {
                serialConnection.setFlowControlMode(-1);
                fail("Not throw UnsupportedCommOperationException.");
            } catch (UnsupportedCommOperationException e) {
                trace("catch UnsupportedCommOperationException.");
            }

            // setRTS test
            serialConnection.setRTS(true);
            assertTrue(serialConnection.isRTS());
            serialConnection.setRTS(false);
            assertFalse(serialConnection.isRTS());

            // setSerialPortParams test
            int supportBaudrate = Integer.getInteger("support.baudrate").intValue();
            serialConnection.setSerialPortParams(supportBaudrate, SerialConnection.DATABITS_5, SerialConnection.STOPBITS_1, SerialConnection.PARITY_NONE);
            assertEquals(supportBaudrate, serialConnection.getBaudRate());
            assertEquals(SerialConnection.DATABITS_5, serialConnection.getDataBits());
            assertEquals(SerialConnection.STOPBITS_1, serialConnection.getStopBits());
            assertEquals(SerialConnection.PARITY_NONE, serialConnection.getParity());

            int unsupportBaudrate = Integer.getInteger("unsupport.baudrate").intValue();
            try {
                serialConnection.setSerialPortParams(unsupportBaudrate, SerialConnection.DATABITS_5, SerialConnection.STOPBITS_1, SerialConnection.PARITY_NONE);
                fail("Not throw UnsupportedCommOperationException.");
            } catch (UnsupportedCommOperationException e) {
                trace("catch UnsupportedCommOperationException.");
            }

            serialConnection.setSerialPortParams(supportBaudrate, SerialConnection.DATABITS_6, SerialConnection.STOPBITS_1, SerialConnection.PARITY_NONE);
            assertEquals(SerialConnection.DATABITS_6, serialConnection.getDataBits());
            serialConnection.setSerialPortParams(supportBaudrate, SerialConnection.DATABITS_7, SerialConnection.STOPBITS_1, SerialConnection.PARITY_NONE);
            assertEquals(SerialConnection.DATABITS_7, serialConnection.getDataBits());
            serialConnection.setSerialPortParams(supportBaudrate, SerialConnection.DATABITS_8, SerialConnection.STOPBITS_1, SerialConnection.PARITY_NONE);
            assertEquals(SerialConnection.DATABITS_8, serialConnection.getDataBits());
            try {
                serialConnection.setSerialPortParams(supportBaudrate, -1, SerialConnection.STOPBITS_1, SerialConnection.PARITY_NONE);
                fail("Not throw UnsupportedCommOperationException.");
            } catch (UnsupportedCommOperationException e) {
                trace("catch UnsupportedCommOperationException.");
            }

            serialConnection.setSerialPortParams(supportBaudrate, SerialConnection.DATABITS_5, SerialConnection.STOPBITS_2, SerialConnection.PARITY_NONE);
            assertEquals(SerialConnection.STOPBITS_2, serialConnection.getStopBits());
            serialConnection.setSerialPortParams(supportBaudrate, SerialConnection.DATABITS_5, SerialConnection.STOPBITS_1_5, SerialConnection.PARITY_NONE);
            assertEquals(SerialConnection.STOPBITS_1_5, serialConnection.getStopBits());
            try {
                serialConnection.setSerialPortParams(supportBaudrate, SerialConnection.DATABITS_5, -1, SerialConnection.PARITY_NONE);
                fail("Not throw UnsupportedCommOperationException.");
            } catch (UnsupportedCommOperationException e) {
                trace("catch UnsupportedCommOperationException.");
            }

            serialConnection.setSerialPortParams(supportBaudrate, SerialConnection.DATABITS_5, SerialConnection.STOPBITS_1, SerialConnection.PARITY_ODD);
            assertEquals(SerialConnection.PARITY_ODD, serialConnection.getParity());
            serialConnection.setSerialPortParams(supportBaudrate, SerialConnection.DATABITS_5, SerialConnection.STOPBITS_1, SerialConnection.PARITY_EVEN);
            assertEquals(SerialConnection.PARITY_EVEN, serialConnection.getParity());
            serialConnection.setSerialPortParams(supportBaudrate, SerialConnection.DATABITS_5, SerialConnection.STOPBITS_1, SerialConnection.PARITY_MARK);
            assertEquals(SerialConnection.PARITY_MARK, serialConnection.getParity());
            serialConnection.setSerialPortParams(supportBaudrate, SerialConnection.DATABITS_5, SerialConnection.STOPBITS_1, SerialConnection.PARITY_SPACE);
            assertEquals(SerialConnection.PARITY_SPACE, serialConnection.getParity());
            try {
                serialConnection.setSerialPortParams(supportBaudrate, SerialConnection.DATABITS_5, SerialConnection.STOPBITS_1, -1);
                fail("Not throw UnsupportedCommOperationException.");
            } catch (UnsupportedCommOperationException e) {
                trace("catch UnsupportedCommOperationException.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);

        } finally {
            testStep.execute("remove", new String[] {serialComport});
        }
    }
}

class MyRunnable implements Runnable {

    private SerialDevice serialDevice;
    private long time;

    public MyRunnable(SerialDevice serialDevice, long time) {
        this.serialDevice = serialDevice;
        this.time = time;
    }

    public void run() {
        try {
            SerialConnection serialConnection = serialDevice.open("test1", 10000);
            Thread.sleep(time);
            serialConnection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
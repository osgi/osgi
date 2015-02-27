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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Dictionary;
import java.util.Properties;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.serial.SerialDevice;
import org.osgi.service.serial.SerialDeviceException;
import org.osgi.service.serial.SerialEvent;
import org.osgi.service.serial.SerialEventListener;
import org.osgi.service.serial.SerialPortConfiguration;
import org.osgi.test.cases.serial.util.SerialTestProxy;
import org.osgi.test.cases.serial.util.TestSerialEventListener;
import org.osgi.test.cases.serial.util.TestServiceListener;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.step.TestStepProxy;

public class SerialDeviceTestCase extends DefaultTestBundleControl {
	private SerialTestProxy	testProxy;

	protected void setUp() throws Exception {
		this.testProxy = new SerialTestProxy(new TestStepProxy(getContext()));
	}

	protected void tearDown() throws Exception {
		this.testProxy.close();
	}

	/**
	 * Tests service register/unregister. Please set in advance the information
	 * of your device to the system property (bnd.bnd file).
	 */
	public void testSerialDevice01() {
		String serialComport = System.getProperty(SerialTestConstants.PROP_SERIAL_COMPORT);
		try {
			String filter = "(" + Constants.OBJECTCLASS + "=" + SerialDevice.class.getName() + ")";
			TestServiceListener regListener = new TestServiceListener(ServiceEvent.REGISTERED);
			TestServiceListener unregListener = new TestServiceListener(ServiceEvent.UNREGISTERING);
			getContext().addServiceListener(regListener, filter);
			getContext().addServiceListener(unregListener, filter);

			String message = "[TEST-SD01] Connect the device set in System Properties.";
			testProxy.executeTestStep("add", message, new String[] {serialComport});

			assertEquals("Only one registered service event is expected.", 1, regListener.size());

			ServiceReference ref = regListener.get(0);
			Long serviceId1 = (Long) ref.getProperty(Constants.SERVICE_ID);

			message = "Disconnect the device.";
			testProxy.executeTestStep("remove", message, new String[] {serialComport});

			assertEquals("Only one unregistering service event is expected.", 1, unregListener.size());
			ref = unregListener.get(0);
			assertEquals("The unregisterd service is not correct.", serviceId1, ref.getProperty(Constants.SERVICE_ID));

			getContext().removeServiceListener(regListener);
			getContext().removeServiceListener(unregListener);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage(), e);
		}
	}

	/**
	 * Tests services register/unregister.
	 */
	public void testSerialDevice02() {
		String serialComport1 = System.getProperty(SerialTestConstants.PROP_SERIAL_COMPORT);
		String serialComport2 = System.getProperty(SerialTestConstants.PROP_SERIAL_COMPORT_2);
		try {
			String filter = "(" + Constants.OBJECTCLASS + "=" + SerialDevice.class.getName() + ")";
			TestServiceListener regListener = new TestServiceListener(ServiceEvent.REGISTERED);
			TestServiceListener unregListener = new TestServiceListener(ServiceEvent.UNREGISTERING);
			getContext().addServiceListener(regListener, filter);
			getContext().addServiceListener(unregListener, filter);

			String message = "[TEST-SD02] Connect the first device set in System Properties.";
			testProxy.executeTestStep("add", message, new String[] {serialComport1});

			assertEquals("Only one registered service event is expected.", 1, regListener.size());

			ServiceReference ref = regListener.get(0);
			Long serviceId1 = (Long) ref.getProperty(Constants.SERVICE_ID);

			message = "Connect the second device set in System Properties.";
			testProxy.executeTestStep("add", message, new String[] {serialComport2});

			assertEquals("Only two registered service events are expected.", 2, regListener.size());

			ref = regListener.get(1);
			Long serviceId2 = (Long) ref.getProperty(Constants.SERVICE_ID);

			message = "Disconnect the first device.";
			testProxy.executeTestStep("remove", message, new String[] {serialComport1});

			assertEquals("Only one unregistering service event is expected.", 1, unregListener.size());

			ref = unregListener.get(0);
			assertEquals("The unregisterd service is not correct.", serviceId1, ref.getProperty(Constants.SERVICE_ID));

			message = "Disconnect the second device.";
			testProxy.executeTestStep("remove", message, new String[] {serialComport2});

			assertEquals("Only two unregistering service events are expected.", 2, unregListener.size());

			ref = unregListener.get(1);
			assertEquals("The unregisterd service is not correct.", serviceId2, ref.getProperty(Constants.SERVICE_ID));

			getContext().removeServiceListener(regListener);
			getContext().removeServiceListener(unregListener);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage(), e);
		}
	}

	/**
	 * Tests SerialDevice service properties.
	 */
	public void testSerialDevice03() {
		String serialComport = System.getProperty(SerialTestConstants.PROP_SERIAL_COMPORT);
		try {
			TestServiceListener listener = new TestServiceListener(ServiceEvent.REGISTERED);
			getContext().addServiceListener(listener, "(" + Constants.OBJECTCLASS + "=" + SerialDevice.class.getName() + ")");

			String message = "[TEST-SD03] Connect the device set in System Properties.";
			testProxy.executeTestStep("add", message, new String[] {serialComport});

			getContext().removeServiceListener(listener);

			ServiceReference ref = listener.get(0);
			String[] category = (String[]) ref.getProperty(org.osgi.service.device.Constants.DEVICE_CATEGORY);
			boolean categoryFlag = false;
			for (int i = 0; i < category.length; i++) {
				if (category[i].equals(SerialDevice.DEVICE_CATEGORY)) {
					categoryFlag = true;
					break;
				}
			}
			assertTrue("The following service property is not correct: " + org.osgi.service.device.Constants.DEVICE_CATEGORY, categoryFlag);
			assertNotNull("The following service property is null: " + SerialDevice.SERIAL_COMPORT, ref.getProperty(SerialDevice.SERIAL_COMPORT));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage(), e);
		} finally {
			testProxy.executeTestStep("remove", "Disconnect the remaining device.", new String[] {serialComport});
		}
	}

	/**
	 * Tests Serial stream.
	 */
	public void testSerialDevice04() {
		String serialComport = System.getProperty(SerialTestConstants.PROP_SERIAL_COMPORT);
		try {
			TestServiceListener listener = new TestServiceListener(ServiceEvent.REGISTERED);
			getContext().addServiceListener(listener, "(" + Constants.OBJECTCLASS + "=" + SerialDevice.class.getName() + ")");

			String message = "[TEST-SD04] Connect the device set in System Properties.";
			testProxy.executeTestStep("add", message, new String[] {serialComport});

			getContext().removeServiceListener(listener);

			SerialDevice serialDevice = (SerialDevice) getContext().getService(listener.get(0));

			int baudRate = Integer.getInteger(SerialTestConstants.PROP_BAUDRATE).intValue();
			int dataBits = Integer.getInteger(SerialTestConstants.PROP_DATABITS).intValue();
			int flowControl = Integer.getInteger(SerialTestConstants.PROP_FLOWCONTROL).intValue();
			int parity = Integer.getInteger(SerialTestConstants.PROP_PARITY).intValue();
			int stopBits = Integer.getInteger(SerialTestConstants.PROP_STOPBITS).intValue();
			serialDevice.setConfiguration(new SerialPortConfiguration(baudRate, dataBits, flowControl, parity, stopBits));

			boolean supportInput = Boolean.getBoolean("support.inputstream");
			if (supportInput) {
				InputStream is = serialDevice.getInputStream();
				assertNotNull("The input stream is null.", is);
				is.close();
			}
			boolean supportOutput = Boolean.getBoolean("support.outputstream");
			if (supportOutput) {
				OutputStream os = serialDevice.getOutputStream();
				assertNotNull("The output stream is null", os);
				os.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage(), e);
		} finally {
			testProxy.executeTestStep("remove", "Disconnect the remaining device.", new String[] {serialComport});
		}
	}

	/**
	 * Tests SerialDevice configuration.
	 */
	public void testSerialDevice05() {
		String serialComport = System.getProperty(SerialTestConstants.PROP_SERIAL_COMPORT);
		try {
			TestServiceListener listener = new TestServiceListener(ServiceEvent.REGISTERED);
			getContext().addServiceListener(listener, "(" + Constants.OBJECTCLASS + "=" + SerialDevice.class.getName() + ")");

			String message = "[TEST-SD05] Connect the device set in System Properties.";
			testProxy.executeTestStep("add", message, new String[] {serialComport});

			getContext().removeServiceListener(listener);
			SerialDevice serialDevice = (SerialDevice) getContext().getService(listener.get(0));

			int baudRate = Integer.getInteger(SerialTestConstants.PROP_BAUDRATE).intValue();
			int dataBits = Integer.getInteger(SerialTestConstants.PROP_DATABITS).intValue();
			int flowControl = Integer.getInteger(SerialTestConstants.PROP_FLOWCONTROL).intValue();
			int parity = Integer.getInteger(SerialTestConstants.PROP_PARITY).intValue();
			int stopBits = Integer.getInteger(SerialTestConstants.PROP_STOPBITS).intValue();
			serialDevice.setConfiguration(new SerialPortConfiguration(baudRate, dataBits, flowControl, parity, stopBits));

			SerialPortConfiguration config2 = serialDevice.getConfiguration();
			assertEquals("The following configuration is not correct: " + SerialTestConstants.PROP_BAUDRATE, baudRate, config2.getBaudRate());
			assertEquals("The following configuration is not correct: " + SerialTestConstants.PROP_DATABITS, dataBits, config2.getDataBits());
			assertEquals("The following configuration is not correct: " + SerialTestConstants.PROP_FLOWCONTROL, flowControl, config2.getFlowControl());
			assertEquals("The following configuration is not correct: " + SerialTestConstants.PROP_PARITY, parity, config2.getParity());
			assertEquals("The following configuration is not correct: " + SerialTestConstants.PROP_STOPBITS, stopBits, config2.getStopBits());

			try {
				int unsupportBaudRate = Integer.getInteger(SerialTestConstants.PROP_UNSUPPORT_BAUDRATE).intValue();
				serialDevice.setConfiguration(new SerialPortConfiguration(unsupportBaudRate));
				fail("Not throw SerialDeviceException.");
			} catch (SerialDeviceException e) {
				assertEquals("The type of SerialDeviceExeption is not UNSUPPORTED_OPERATION.", SerialDeviceException.UNSUPPORTED_OPERATION, e.getType());
				trace("catch SerialDeviceException.");
			}
			try {
				serialDevice.setConfiguration(new SerialPortConfiguration(baudRate, -1, flowControl, parity, stopBits));
				fail("Not throw SerialDeviceException.");
			} catch (SerialDeviceException e) {
				assertEquals("The type of SerialDeviceExeption is not UNSUPPORTED_OPERATION.", SerialDeviceException.UNSUPPORTED_OPERATION, e.getType());
				trace("catch SerialDeviceException.");
			}
			try {
				serialDevice.setConfiguration(new SerialPortConfiguration(baudRate, dataBits, -1, parity, stopBits));
				fail("Not throw SerialDeviceException.");
			} catch (SerialDeviceException e) {
				assertEquals("The type of SerialDeviceExeption is not UNSUPPORTED_OPERATION.", SerialDeviceException.UNSUPPORTED_OPERATION, e.getType());
				trace("catch SerialDeviceException.");
			}
			try {
				serialDevice.setConfiguration(new SerialPortConfiguration(baudRate, dataBits, flowControl, -1, stopBits));
				fail("Not throw SerialDeviceException.");
			} catch (SerialDeviceException e) {
				assertEquals("The type of SerialDeviceExeption is not UNSUPPORTED_OPERATION.", SerialDeviceException.UNSUPPORTED_OPERATION, e.getType());
				trace("catch SerialDeviceException.");
			}
			try {
				serialDevice.setConfiguration(new SerialPortConfiguration(baudRate, dataBits, flowControl, parity, -1));
				fail("Not throw SerialDeviceException.");
			} catch (SerialDeviceException e) {
				assertEquals("The type of SerialDeviceExeption is not UNSUPPORTED_OPERATION.", SerialDeviceException.UNSUPPORTED_OPERATION, e.getType());
				trace("catch SerialDeviceException.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage(), e);
		} finally {
			testProxy.executeTestStep("remove", "Disconnect the remaining device.", new String[] {System.getProperty(SerialTestConstants.PROP_SERIAL_COMPORT)});
		}
	}

	/**
	 * Tests Serial event.
	 */
	public void testSerialDevice06() {
		String serialComport1 = System.getProperty(SerialTestConstants.PROP_SERIAL_COMPORT);
		String serialComport2 = System.getProperty(SerialTestConstants.PROP_SERIAL_COMPORT_2);
		try {
			TestServiceListener listener = new TestServiceListener(ServiceEvent.REGISTERED);
			getContext().addServiceListener(listener, "(" + Constants.OBJECTCLASS + "=" + SerialDevice.class.getName() + ")");

			String message = "[TEST-SD06] Connect the first device set in System Properties.";
			testProxy.executeTestStep("add", message, new String[] {serialComport1});

			getContext().removeServiceListener(listener);
			SerialDevice serialDevice = (SerialDevice) getContext().getService(listener.get(0));

			int baudRate = Integer.getInteger(SerialTestConstants.PROP_BAUDRATE).intValue();
			int dataBits = Integer.getInteger(SerialTestConstants.PROP_DATABITS).intValue();
			int flowControl = Integer.getInteger(SerialTestConstants.PROP_FLOWCONTROL).intValue();
			int parity = Integer.getInteger(SerialTestConstants.PROP_PARITY).intValue();
			int stopBits = Integer.getInteger(SerialTestConstants.PROP_STOPBITS).intValue();
			serialDevice.setConfiguration(new SerialPortConfiguration(baudRate, dataBits, flowControl, parity, stopBits));

			message = "Connect the second device set in System Properties.";
			testProxy.executeTestStep("add", message, new String[] {serialComport2});

			TestSerialEventListener serialListenerAll = new TestSerialEventListener();
			getContext().registerService(SerialEventListener.class.getName(), serialListenerAll, null);

			TestSerialEventListener serialListener1 = new TestSerialEventListener();
			Dictionary props1 = new Properties();
			props1.put(SerialEventListener.SERIAL_COMPORT, serialComport1);
			getContext().registerService(SerialEventListener.class.getName(), serialListener1, props1);

			TestSerialEventListener serialListener2 = new TestSerialEventListener();
			Dictionary props2 = new Properties();
			props2.put(SerialEventListener.SERIAL_COMPORT, serialComport2);
			getContext().registerService(SerialEventListener.class.getName(), serialListener2, props2);

			message = "Send data from the first device.";
			testProxy.executeTestStep("event", message, new String[] {serialComport1});

			assertTrue("It is expected that the SerialListener does not have serial.comport receive all events.", serialListenerAll.isReceived());
			assertEquals("The ComPort name of the event is not correct.", serialComport1, serialListenerAll.getReceivedComPort());
			assertEquals("The type of the event is not correct.", SerialEvent.DATA_AVAILABLE, serialListenerAll.getReceivedType());

			assertTrue("It is expected that the SerialListener has serial.comport receive events from the port.", serialListener1.isReceived());
			assertEquals("The ComPort name of the event is not correct.", serialComport1, serialListener1.getReceivedComPort());
			assertEquals("The type of the event is not correct.", SerialEvent.DATA_AVAILABLE, serialListener1.getReceivedType());

			assertFalse("It is expected that the SerialListener has serial.comport receive events from only the port.", serialListener2.isReceived());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage(), e);
		} finally {
			testProxy.executeTestStep("remove", "Disconnect the remaining device.", new String[] {serialComport1});
			testProxy.executeTestStep("remove", "Disconnect the remaining device.", new String[] {serialComport2});
		}
	}
}

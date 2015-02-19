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
import org.osgi.service.serial.SerialConstants;
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
	}

	/**
	 * Tests service register/unregister. Please set in advance the information
	 * of your device to the system property (bnd.bnd file).
	 */
	public void testSerialDevice01() {
		String serialComport = System.getProperty("serial.comport.1");
		try {
			String filter = "(" + Constants.OBJECTCLASS + "=" + SerialDevice.class.getName() + ")";
			TestServiceListener regListener = new TestServiceListener(ServiceEvent.REGISTERED);
			TestServiceListener unregListener = new TestServiceListener(ServiceEvent.UNREGISTERING);
			getContext().addServiceListener(regListener, filter);
			getContext().addServiceListener(unregListener, filter);

			String message = "Connect the device set in System Properties.";
			testProxy.executeTestStep("add", message, new String[] {serialComport});

			assertEquals(1, regListener.size());

			ServiceReference ref = regListener.get(0);
			Long serviceId1 = (Long) ref.getProperty(Constants.SERVICE_ID);

			message = "Disconnect the device.";
			testProxy.executeTestStep("remove", message, new String[] {serialComport});

			assertEquals(1, unregListener.size());

			ref = unregListener.get(0);
			assertEquals(serviceId1, ref.getProperty(Constants.SERVICE_ID));

			getContext().removeServiceListener(regListener);
			getContext().removeServiceListener(unregListener);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage(), e);
		} finally {
			testProxy.executeTestStep("remove", "Disconnect the remaining device.", new String[] {serialComport});
		}
	}

	/**
	 * Tests services register/unregister.
	 */
	public void testSerialDevice02() {
		String serialComport1 = System.getProperty("serial.comport.1");
		String serialComport2 = System.getProperty("serial.comport.2");
		try {
			String filter = "(" + Constants.OBJECTCLASS + "=" + SerialDevice.class.getName() + ")";
			TestServiceListener regListener = new TestServiceListener(ServiceEvent.REGISTERED);
			TestServiceListener unregListener = new TestServiceListener(ServiceEvent.UNREGISTERING);
			getContext().addServiceListener(regListener, filter);
			getContext().addServiceListener(unregListener, filter);

			String message = "Connect the first device set in System Properties.";
			testProxy.executeTestStep("add", message, new String[] {serialComport1});

			assertEquals(1, regListener.size());

			ServiceReference ref = regListener.get(0);
			Long serviceId1 = (Long) ref.getProperty(Constants.SERVICE_ID);

			message = "Connect the second device set in System Properties.";
			testProxy.executeTestStep("add", message, new String[] {serialComport2});

			assertEquals(2, regListener.size());

			ref = regListener.get(1);
			Long serviceId2 = (Long) ref.getProperty(Constants.SERVICE_ID);

			message = "Disconnect the first device.";
			testProxy.executeTestStep("remove", message, new String[] {serialComport1});

			assertEquals(1, unregListener.size());

			ref = unregListener.get(0);
			assertEquals(serviceId1, ref.getProperty(Constants.SERVICE_ID));

			message = "Disconnect the second device.";
			testProxy.executeTestStep("remove", message, new String[] {serialComport2});

			assertEquals(2, unregListener.size());

			ref = unregListener.get(1);
			assertEquals(serviceId2, ref.getProperty(Constants.SERVICE_ID));

			getContext().removeServiceListener(regListener);
			getContext().removeServiceListener(unregListener);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage(), e);
		} finally {
			testProxy.executeTestStep("remove", "Disconnect the remaining device.", new String[] {serialComport1});
			testProxy.executeTestStep("remove", "Disconnect the remaining device.", new String[] {serialComport2});
		}
	}

	/**
	 * Tests SerialDevice service properties.
	 */
	public void testSerialDevice03() {
		String serialComport = System.getProperty("serial.comport.1");
		try {
			TestServiceListener listener = new TestServiceListener(ServiceEvent.REGISTERED);
			getContext().addServiceListener(listener, "(" + Constants.OBJECTCLASS + "=" + SerialDevice.class.getName() + ")");

			String message = "Connect the device set in System Properties.";
			testProxy.executeTestStep("add", message, new String[] {serialComport});

			getContext().removeServiceListener(listener);

			ServiceReference ref = listener.get(0);

			assertTrue(ref.getProperty(SerialDevice.SERIAL_COMPORT) instanceof String);
			assertEquals(serialComport, ref.getProperty(SerialDevice.SERIAL_COMPORT));

			assertTrue(ref.getProperty(org.osgi.service.device.Constants.DEVICE_CATEGORY) instanceof String);
			assertEquals(SerialDevice.DEVICE_CATEGORY, ref.getProperty(org.osgi.service.device.Constants.DEVICE_CATEGORY));
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
		String serialComport = System.getProperty("serial.comport.1");
		try {
			TestServiceListener listener = new TestServiceListener(ServiceEvent.REGISTERED);
			getContext().addServiceListener(listener, "(" + Constants.OBJECTCLASS + "=" + SerialDevice.class.getName() + ")");

			String message = "Connect the device set in System Properties.";
			testProxy.executeTestStep("add", message, new String[] {serialComport});

			getContext().removeServiceListener(listener);

			SerialDevice serialDevice = (SerialDevice) getContext().getService(listener.get(0));

			boolean supportInput = Boolean.getBoolean("support.inputstream");

			InputStream is = serialDevice.getInputStream();
			if (supportInput) {
				assertNotNull(is);
			} else {
				assertNull(is);
			}
			is.close();
			if (supportInput) {
			} else {
				is = serialDevice.getInputStream();
				assertNull(is);
			}
			boolean supportOutput = Boolean.getBoolean("support.outputstream");

			OutputStream os = serialDevice.getOutputStream();
			if (supportOutput) {
				assertNotNull(os);
			} else {
				assertNull(os);
			}
			os.close();
			if (supportOutput) {
			} else {
				os = serialDevice.getOutputStream();
				assertNull(is);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage(), e);
		} finally {
			testProxy.executeTestStep("remove", "Disconnect the remaining device.", new String[] {serialComport});
		}
	}

	/**
	 * Tests SerialDevice setter/getter.
	 */
	public void testSerialDevice05() {
		String serialComport = System.getProperty("serial.comport.1");
		try {
			TestServiceListener listener = new TestServiceListener(ServiceEvent.REGISTERED);
			getContext().addServiceListener(listener, "(" + Constants.OBJECTCLASS + "=" + SerialDevice.class.getName() + ")");

			String message = "Connect the device set in System Properties.";
			testProxy.executeTestStep("add", message, new String[] {serialComport});

			getContext().removeServiceListener(listener);

			SerialDevice serialDevice = (SerialDevice) getContext().getService(listener.get(0));

			// setConfiguration test
			// Baud rate
			int supportBaudrate = Integer.getInteger("support.baudrate").intValue();
			int unsupportBaudrate = Integer.getInteger("unsupport.baudrate").intValue();
			serialDevice.setConfiguration(new SerialPortConfiguration(supportBaudrate));
			SerialPortConfiguration config = serialDevice.getConfiguration();
			assertEquals(supportBaudrate, config.getBaudRate());
			assertEquals(SerialConstants.DATABITS_8, config.getDataBits());
			assertEquals(SerialConstants.FLOWCONTROL_NONE, config.getFlowControl());
			assertEquals(SerialConstants.PARITY_NONE, config.getParity());
			assertEquals(SerialConstants.STOPBITS_1, config.getStopBits());
			try {
				serialDevice.setConfiguration(new SerialPortConfiguration(unsupportBaudrate));
				fail("Not throw SerialDeviceException.");
			} catch (SerialDeviceException e) {
				assertEquals(SerialDeviceException.UNSUPPORTED_OPERATION, e.getType());
				trace("catch SerialDeviceException.");
			}

			// Data Bits
			serialDevice.setConfiguration(new SerialPortConfiguration(supportBaudrate,
					SerialConstants.DATABITS_6,
					SerialConstants.FLOWCONTROL_NONE,
					SerialConstants.PARITY_NONE,
					SerialConstants.STOPBITS_1));
			assertEquals(SerialConstants.DATABITS_6, serialDevice.getConfiguration().getDataBits());
			serialDevice.setConfiguration(new SerialPortConfiguration(supportBaudrate,
					SerialConstants.DATABITS_7,
					SerialConstants.FLOWCONTROL_NONE,
					SerialConstants.PARITY_NONE,
					SerialConstants.STOPBITS_1));
			assertEquals(SerialConstants.DATABITS_7, serialDevice.getConfiguration().getDataBits());
			serialDevice.setConfiguration(new SerialPortConfiguration(supportBaudrate,
					SerialConstants.DATABITS_8,
					SerialConstants.FLOWCONTROL_NONE,
					SerialConstants.PARITY_NONE,
					SerialConstants.STOPBITS_1));
			assertEquals(SerialConstants.DATABITS_8, serialDevice.getConfiguration().getDataBits());
			try {
				serialDevice.setConfiguration(new SerialPortConfiguration(supportBaudrate, -1, SerialConstants.FLOWCONTROL_NONE, SerialConstants.PARITY_NONE, SerialConstants.STOPBITS_1));
				fail("Not throw SerialDeviceException.");
			} catch (SerialDeviceException e) {
				assertEquals(SerialDeviceException.UNSUPPORTED_OPERATION, e.getType());
				trace("catch SerialDeviceException.");
			}

			// Flow Control
			try {
				serialDevice.setConfiguration(new SerialPortConfiguration(supportBaudrate,
						SerialConstants.DATABITS_8,
						SerialConstants.FLOWCONTROL_NONE,
						SerialConstants.PARITY_NONE,
						SerialConstants.STOPBITS_1));
				assertEquals(SerialConstants.FLOWCONTROL_NONE, serialDevice.getConfiguration().getFlowControl());
			} catch (SerialDeviceException e) {
				assertEquals(SerialDeviceException.UNSUPPORTED_OPERATION, e.getType());
				trace("catch SerialDeviceException.");
			}
			try {
				serialDevice.setConfiguration(new SerialPortConfiguration(supportBaudrate,
						SerialConstants.DATABITS_8,
						SerialConstants.FLOWCONTROL_RTSCTS_IN,
						SerialConstants.PARITY_NONE,
						SerialConstants.STOPBITS_1));
				assertEquals(SerialConstants.FLOWCONTROL_RTSCTS_IN, serialDevice.getConfiguration().getFlowControl());
			} catch (SerialDeviceException e) {
				assertEquals(SerialDeviceException.UNSUPPORTED_OPERATION, e.getType());
				trace("catch SerialDeviceException.");
			}
			try {
				serialDevice.setConfiguration(new SerialPortConfiguration(supportBaudrate,
						SerialConstants.DATABITS_8,
						SerialConstants.FLOWCONTROL_RTSCTS_OUT,
						SerialConstants.PARITY_NONE,
						SerialConstants.STOPBITS_1));
				assertEquals(SerialConstants.FLOWCONTROL_RTSCTS_OUT, serialDevice.getConfiguration().getFlowControl());
			} catch (SerialDeviceException e) {
				assertEquals(SerialDeviceException.UNSUPPORTED_OPERATION, e.getType());
				trace("catch SerialDeviceException.");
			}
			try {
				serialDevice.setConfiguration(new SerialPortConfiguration(supportBaudrate,
						SerialConstants.DATABITS_8,
						SerialConstants.FLOWCONTROL_XONXOFF_IN,
						SerialConstants.PARITY_NONE,
						SerialConstants.STOPBITS_1));
				assertEquals(SerialConstants.FLOWCONTROL_XONXOFF_IN, serialDevice.getConfiguration().getFlowControl());
			} catch (SerialDeviceException e) {
				assertEquals(SerialDeviceException.UNSUPPORTED_OPERATION, e.getType());
				trace("catch SerialDeviceException.");
			}
			try {
				serialDevice.setConfiguration(new SerialPortConfiguration(supportBaudrate,
						SerialConstants.DATABITS_8,
						SerialConstants.FLOWCONTROL_XONXOFF_OUT,
						SerialConstants.PARITY_NONE,
						SerialConstants.STOPBITS_1));
				assertEquals(SerialConstants.FLOWCONTROL_XONXOFF_OUT, serialDevice.getConfiguration().getFlowControl());
			} catch (SerialDeviceException e) {
				assertEquals(SerialDeviceException.UNSUPPORTED_OPERATION, e.getType());
				trace("catch SerialDeviceException.");
			}
			try {
				serialDevice.setConfiguration(new SerialPortConfiguration(supportBaudrate, SerialConstants.DATABITS_8, -1, SerialConstants.PARITY_NONE, SerialConstants.STOPBITS_1));
				fail("Not throw SerialDeviceException.");
			} catch (SerialDeviceException e) {
				assertEquals(SerialDeviceException.UNSUPPORTED_OPERATION, e.getType());
				trace("catch SerialDeviceException.");
			}

			// Parity
			serialDevice.setConfiguration(new SerialPortConfiguration(supportBaudrate,
					SerialConstants.DATABITS_8,
					SerialConstants.FLOWCONTROL_NONE,
					SerialConstants.PARITY_EVEN,
					SerialConstants.STOPBITS_1));
			assertEquals(SerialConstants.PARITY_EVEN, serialDevice.getConfiguration().getParity());
			serialDevice.setConfiguration(new SerialPortConfiguration(supportBaudrate,
					SerialConstants.DATABITS_8,
					SerialConstants.FLOWCONTROL_NONE,
					SerialConstants.PARITY_MARK,
					SerialConstants.STOPBITS_1));
			assertEquals(SerialConstants.PARITY_MARK, serialDevice.getConfiguration().getParity());
			serialDevice.setConfiguration(new SerialPortConfiguration(supportBaudrate,
					SerialConstants.DATABITS_8,
					SerialConstants.FLOWCONTROL_NONE,
					SerialConstants.PARITY_ODD,
					SerialConstants.STOPBITS_1));
			assertEquals(SerialConstants.PARITY_ODD, serialDevice.getConfiguration().getParity());
			serialDevice.setConfiguration(new SerialPortConfiguration(supportBaudrate,
					SerialConstants.DATABITS_8,
					SerialConstants.FLOWCONTROL_NONE,
					SerialConstants.PARITY_SPACE,
					SerialConstants.STOPBITS_1));
			assertEquals(SerialConstants.PARITY_SPACE, serialDevice.getConfiguration().getParity());
			try {
				serialDevice.setConfiguration(new SerialPortConfiguration(supportBaudrate, SerialConstants.DATABITS_8, SerialConstants.FLOWCONTROL_NONE, -1, SerialConstants.STOPBITS_1));
				fail("Not throw SerialDeviceException.");
			} catch (SerialDeviceException e) {
				assertEquals(SerialDeviceException.UNSUPPORTED_OPERATION, e.getType());
				trace("catch SerialDeviceException.");
			}

			// Stop Bits
			serialDevice.setConfiguration(new SerialPortConfiguration(supportBaudrate,
					SerialConstants.DATABITS_8,
					SerialConstants.FLOWCONTROL_NONE,
					SerialConstants.PARITY_NONE,
					SerialConstants.STOPBITS_1_5));
			assertEquals(SerialConstants.STOPBITS_1_5, serialDevice.getConfiguration().getStopBits());
			serialDevice.setConfiguration(new SerialPortConfiguration(supportBaudrate,
					SerialConstants.DATABITS_8,
					SerialConstants.FLOWCONTROL_NONE,
					SerialConstants.PARITY_NONE,
					SerialConstants.STOPBITS_2));
			assertEquals(SerialConstants.STOPBITS_2, serialDevice.getConfiguration().getStopBits());
			try {
				serialDevice.setConfiguration(new SerialPortConfiguration(supportBaudrate, SerialConstants.DATABITS_8, SerialConstants.FLOWCONTROL_NONE, SerialConstants.PARITY_NONE, -1));
				fail("Not throw SerialDeviceException.");
			} catch (SerialDeviceException e) {
				assertEquals(SerialDeviceException.UNSUPPORTED_OPERATION, e.getType());
				trace("catch SerialDeviceException.");
			}

			// setDTR test
			serialDevice.setDTR(true);
			assertTrue(serialDevice.isDTR());
			serialDevice.setDTR(false);
			assertFalse(serialDevice.isDTR());

			// setRTS test
			serialDevice.setRTS(true);
			assertTrue(serialDevice.isRTS());
			serialDevice.setRTS(false);
			assertFalse(serialDevice.isRTS());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage(), e);
		} finally {
			testProxy.executeTestStep("remove", "Disconnect the remaining device.", new String[] {serialComport});
		}
	}

	/**
	 * Tests Serial event.
	 */
	public void testSerialDevice06() {
		String serialComport1 = System.getProperty("serial.comport.1");
		String serialComport2 = System.getProperty("serial.comport.2");
		try {
			String message = "Connect the first device set in System Properties.";
			testProxy.executeTestStep("add", message, new String[] {serialComport1});

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

			assertTrue(serialListenerAll.isReceived());
			assertEquals(serialComport1, serialListenerAll.getReceivedComPort());
			assertEquals(SerialEvent.DATA_AVAILABLE, serialListenerAll.getReceivedType());
			assertTrue(serialListener1.isReceived());
			assertEquals(serialComport1, serialListener1.getReceivedComPort());
			assertEquals(SerialEvent.DATA_AVAILABLE, serialListenerAll.getReceivedType());
			assertFalse(serialListener2.isReceived());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage(), e);
		} finally {
			testProxy.executeTestStep("remove", "Disconnect the remaining device.", new String[] {serialComport1});
		}
	}
}

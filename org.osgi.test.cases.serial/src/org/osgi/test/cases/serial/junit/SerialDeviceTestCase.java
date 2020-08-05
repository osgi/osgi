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

package org.osgi.test.cases.serial.junit;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.serial.SerialDevice;
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
		try {
			String filter = "(" + Constants.OBJECTCLASS + "=" + SerialDevice.class.getName() + ")";
			TestServiceListener<SerialDevice> regListener = new TestServiceListener<>(
					ServiceEvent.REGISTERED);
			TestServiceListener<SerialDevice> unregListener = new TestServiceListener<>(
					ServiceEvent.UNREGISTERING);
			getContext().addServiceListener(regListener, filter);
			getContext().addServiceListener(unregListener, filter);

			String message = "[TEST-SD01] Connect a device.";
			String[] ids = testProxy.executeTestStep("add", message, new String[] {});

			assertEquals("Only one registered service event is expected.", 1, regListener.size());

			ServiceReference<SerialDevice> ref = regListener.get(0);
			Long serviceId1 = (Long) ref.getProperty(Constants.SERVICE_ID);

			message = "Disconnect the device.";
			testProxy.executeTestStep("remove", message, new String[] {ids[0]});

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
		try {
			String filter = "(" + Constants.OBJECTCLASS + "=" + SerialDevice.class.getName() + ")";
			TestServiceListener<SerialDevice> regListener = new TestServiceListener<>(
					ServiceEvent.REGISTERED);
			TestServiceListener<SerialDevice> unregListener = new TestServiceListener<>(
					ServiceEvent.UNREGISTERING);
			getContext().addServiceListener(regListener, filter);
			getContext().addServiceListener(unregListener, filter);

			String message = "[TEST-SD02] Connect the first device.";
			String[] ids1 = testProxy.executeTestStep("add", message, new String[] {});

			assertEquals("Only one registered service event is expected.", 1, regListener.size());

			ServiceReference<SerialDevice> ref = regListener.get(0);
			Long serviceId1 = (Long) ref.getProperty(Constants.SERVICE_ID);

			message = "Connect the second device.";
			String[] ids2 = testProxy.executeTestStep("add", message, new String[] {});

			assertEquals("Only two registered service events are expected.", 2, regListener.size());

			ref = regListener.get(1);
			Long serviceId2 = (Long) ref.getProperty(Constants.SERVICE_ID);

			message = "Disconnect the first device.";
			testProxy.executeTestStep("remove", message, new String[] {ids1[0]});

			assertEquals("Only one unregistering service event is expected.", 1, unregListener.size());

			ref = unregListener.get(0);
			assertEquals("The unregisterd service is not correct.", serviceId1, ref.getProperty(Constants.SERVICE_ID));

			message = "Disconnect the second device.";
			testProxy.executeTestStep("remove", message, new String[] {ids2[0]});

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
		String[] ids = null;
		try {
			TestServiceListener<SerialDevice> listener = new TestServiceListener<>(
					ServiceEvent.REGISTERED);
			getContext().addServiceListener(listener, "(" + Constants.OBJECTCLASS + "=" + SerialDevice.class.getName() + ")");

			String message = "[TEST-SD03] Connect a device.";
			ids = testProxy.executeTestStep("add", message, new String[] {});

			getContext().removeServiceListener(listener);

			ServiceReference<SerialDevice> ref = listener.get(0);
			String[] category = (String[]) ref.getProperty(org.osgi.service.device.Constants.DEVICE_CATEGORY);
			boolean categoryFlag = false;
			for (int i = 0; i < category.length; i++) {
				if (category[i].equals(SerialDevice.DEVICE_CATEGORY)) {
					categoryFlag = true;
					break;
				}
			}
			assertTrue("The following service property is not correct: " + org.osgi.service.device.Constants.DEVICE_CATEGORY, categoryFlag);

			Object comport = ref.getProperty(SerialDevice.SERIAL_COMPORT);
			assertTrue("The following service property is not correct: " + SerialDevice.SERIAL_COMPORT, comport instanceof String);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage(), e);
		} finally {
			if (ids != null) {
				testProxy.executeTestStep("remove", "Disconnect the remaining device.", new String[] {ids[0]});
			}
		}
	}

	/**
	 * Tests SerialDevice configuration.
	 */
	public void testSerialDevice04() {
		String[] ids = null;
		try {
			TestServiceListener<SerialDevice> listener = new TestServiceListener<>(
					ServiceEvent.REGISTERED);
			getContext().addServiceListener(listener, "(" + Constants.OBJECTCLASS + "=" + SerialDevice.class.getName() + ")");

			String message = "[TEST-SD04] Connect a device.";
			ids = testProxy.executeTestStep("add", message, new String[] {});

			getContext().removeServiceListener(listener);
			SerialDevice serialDevice = getContext().getService(listener.get(0));

			SerialPortConfiguration config = serialDevice.getConfiguration();
			config.getBaudRate();
			config.getDataBits();
			config.getFlowControl();
			config.getParity();
			config.getStopBits();

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage(), e);
		} finally {
			if (ids != null) {
				testProxy.executeTestStep("remove", "Disconnect the remaining device.", new String[] {ids[0]});
			}
		}
	}

	/**
	 * Tests Serial event.
	 */
	public void testSerialDevice05() {
		String[] ids1 = null;
		String[] ids2 = null;
		try {
			String message = "[TEST-SD05] Connect the first device.";
			ids1 = testProxy.executeTestStep("add", message, new String[] {});

			message = "Connect the second device.";
			ids2 = testProxy.executeTestStep("add", message, new String[] {});

			TestSerialEventListener serialListenerAll = new TestSerialEventListener();
			getContext().registerService(SerialEventListener.class.getName(), serialListenerAll, null);

			Collection<ServiceReference<SerialDevice>> refs1 = getContext()
					.getServiceReferences(SerialDevice.class,
							"(service.id=" + ids1[0] + ")");
			String comport1 = (String) refs1.iterator()
					.next()
					.getProperty(SerialDevice.SERIAL_COMPORT);
			TestSerialEventListener serialListener1 = new TestSerialEventListener();
			Dictionary<String,Object> props1 = new Hashtable<>();
			props1.put(SerialEventListener.SERIAL_COMPORT, comport1);
			getContext().registerService(SerialEventListener.class.getName(), serialListener1, props1);

			Collection<ServiceReference<SerialDevice>> refs2 = getContext()
					.getServiceReferences(SerialDevice.class,
							"(service.id=" + ids2[0] + ")");
			String comport2 = (String) refs2.iterator()
					.next()
					.getProperty(SerialDevice.SERIAL_COMPORT);
			TestSerialEventListener serialListener2 = new TestSerialEventListener();
			Dictionary<String,Object> props2 = new Hashtable<>();
			props2.put(SerialEventListener.SERIAL_COMPORT, comport2);
			getContext().registerService(SerialEventListener.class.getName(), serialListener2, props2);

			message = "Send data from the first device.";
			testProxy.executeTestStep("event", message, new String[] {ids1[0]});

			assertTrue("It is expected that the SerialListener does not have serial.comport receive all events.", serialListenerAll.isReceived());
			assertEquals("The ComPort name of the event is not correct.", comport1, serialListenerAll.getReceivedComPort());
			assertEquals("The type of the event is not correct.", SerialEvent.DATA_AVAILABLE, serialListenerAll.getReceivedType());

			assertTrue("It is expected that the SerialListener has serial.comport receive events from the port.", serialListener1.isReceived());
			assertEquals("The ComPort name of the event is not correct.", comport1, serialListener1.getReceivedComPort());
			assertEquals("The type of the event is not correct.", SerialEvent.DATA_AVAILABLE, serialListener1.getReceivedType());

			assertFalse("It is expected that the SerialListener has serial.comport receive events from only the port.", serialListener2.isReceived());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage(), e);
		} finally {
			if (ids1 != null) {
				testProxy.executeTestStep("remove", "Disconnect the remaining device.", new String[] {ids1[0]});
			}
			if (ids2 != null) {
				testProxy.executeTestStep("remove", "Disconnect the remaining device.", new String[] {ids2[0]});
			}
		}
	}
}

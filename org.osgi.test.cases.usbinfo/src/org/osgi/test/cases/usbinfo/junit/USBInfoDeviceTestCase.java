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
	}

	/**
	 * Tests register a USBInfoDevice service.
	 * Please set in advance the information of your device to the system property (bnd.bnd file).
	 */
	public void testRegistDevice01() {
		String[] ids = null;
		try {
			TestServiceListener listener = new TestServiceListener(ServiceEvent.REGISTERED);
			getContext().addServiceListener(listener, "(objectClass=org.osgi.service.usbinfo.USBInfoDevice)");

			String command = "registerDevice";
			String message = "Connect the device set in System Properties.";
			String[] parameters = new String[23];
			parameters[0] = System.getProperty(USBTestConstants.PROP_BCDUSB);
			parameters[1] = System.getProperty(USBTestConstants.PROP_BDEVICECLASS);
			parameters[2] = System.getProperty(USBTestConstants.PROP_BDEVICESUBCLAS);
			parameters[3] = System.getProperty(USBTestConstants.PROP_BDEVICEPROTOCO);
			parameters[4] = System.getProperty(USBTestConstants.PROP_BMAXPACKETSIZE0);
			parameters[5] = System.getProperty(USBTestConstants.PROP_IDVENDOR);
			parameters[6] = System.getProperty(USBTestConstants.PROP_IDPRODUCT);
			parameters[7] = System.getProperty(USBTestConstants.PROP_BCDDEVICE);
			parameters[8] = System.getProperty(USBTestConstants.PROP_MANUFACTURER);
			parameters[9] = System.getProperty(USBTestConstants.PROP_PRODUCT);
			parameters[10] = System.getProperty(USBTestConstants.PROP_SERIALNUMBER);
			parameters[11] = System.getProperty(USBTestConstants.PROP_BNUMCONFIGURATIONS);
			parameters[12] = System.getProperty(USBTestConstants.PROP_BINTERFACENUMBER);
			parameters[13] = System.getProperty(USBTestConstants.PROP_BALTERNATESETTING);
			parameters[14] = System.getProperty(USBTestConstants.PROP_BNUMENDPOINTS);
			parameters[15] = System.getProperty(USBTestConstants.PROP_BINTERFACECLASS);
			parameters[16] = System.getProperty(USBTestConstants.PROP_BINTERFACESUBCLASS);
			parameters[17] = System.getProperty(USBTestConstants.PROP_BINTERFACEPROTOCOL);
			parameters[18] = System.getProperty(USBTestConstants.PROP_INTERFACE);
			parameters[19] = System.getProperty(USBTestConstants.PROP_BUS);
			parameters[20] = System.getProperty(USBTestConstants.PROP_ADDRESS);
			ids = testProxy.executeTestStep(command, message, parameters);

			getContext().removeServiceListener(listener);

			assertEquals(1, listener.size());

			ServiceReference ref = listener.get(0);
			String[] category = (String[]) ref.getProperty(org.osgi.service.device.Constants.DEVICE_CATEGORY);
			boolean categoryFlag = false;
			for (int i = 0; i < category.length; i++) {
				if (category[i].equals(USBInfoDevice.DEVICE_CATEGORY)) {
					categoryFlag = true;
					break;
				}
			}
			assertTrue(categoryFlag);
			String bcdUSB = (String) ref.getProperty(USBInfoDevice.USB_BCDUSB);
			if (bcdUSB != null) {
				assertEquals(parameters[0], bcdUSB);
			}
			assertEquals(parameters[1], ref.getProperty(USBInfoDevice.USB_BDEVICECLASS));
			assertEquals(parameters[2], ref.getProperty(USBInfoDevice.USB_BDEVICESUBCLASS));
			assertEquals(parameters[3], ref.getProperty(USBInfoDevice.USB_BDEVICEPROTOCOL));
			Integer bMaxPacketSize0 = (Integer) ref.getProperty(USBInfoDevice.USB_BMAXPACKETSIZE0);
			if (bMaxPacketSize0 != null) {
				assertEquals(Integer.decode(parameters[4]), bMaxPacketSize0);
			}
			assertEquals(parameters[5], ref.getProperty(USBInfoDevice.USB_IDVENDOR));
			assertEquals(parameters[6], ref.getProperty(USBInfoDevice.USB_IDPRODUCT));
			assertEquals(parameters[7], ref.getProperty(USBInfoDevice.USB_BCDDEVICE));
			String manufacturer = (String) ref.getProperty(USBInfoDevice.USB_MANUFACTURER);
			if (manufacturer != null) {
				assertEquals(parameters[8], manufacturer);
			}
			String product = (String) ref.getProperty(USBInfoDevice.USB_PRODUCT);
			if (product != null) {
				assertEquals(parameters[9], product);
			}
			String serialNumber = (String) ref.getProperty(USBInfoDevice.USB_SERIALNUMBER);
			if (serialNumber != null) {
				assertEquals(parameters[10], serialNumber);
			}
			Integer bNumConfigurations = (Integer) ref.getProperty(USBInfoDevice.USB_BNUMCONFIGURATIONS);
			if (bNumConfigurations != null) {
				assertEquals(Integer.decode(parameters[11]), bNumConfigurations);
			}
			assertEquals(Integer.decode(parameters[12]), ref.getProperty(USBInfoDevice.USB_BINTERFACENUMBER));
			Integer bAlternateSetting = (Integer) ref.getProperty(USBInfoDevice.USB_BALTERNATESETTING);
			if (bAlternateSetting != null) {
				assertEquals(Integer.decode(parameters[13]), bAlternateSetting);
			}
			Integer bNumEndpoints = (Integer) ref.getProperty(USBInfoDevice.USB_BNUMENDPOINTS);
			if (bNumEndpoints != null) {
				assertEquals(Integer.decode(parameters[14]), bNumEndpoints);
			}
			assertEquals(parameters[15], ref.getProperty(USBInfoDevice.USB_BINTERFACECLASS));
			assertEquals(parameters[16], ref.getProperty(USBInfoDevice.USB_BINTERFACESUBCLASS));
			assertEquals(parameters[17], ref.getProperty(USBInfoDevice.USB_BINTERFACEPROTOCOL));
			String interfaceDescription = (String) ref.getProperty(USBInfoDevice.USB_INTERFACE);
			if (interfaceDescription != null) {
				assertEquals(parameters[18], interfaceDescription);
			}
			assertEquals(Integer.decode(parameters[19]), ref.getProperty(USBInfoDevice.USB_BUS));
			assertEquals(Integer.decode(parameters[20]), ref.getProperty(USBInfoDevice.USB_ADDRESS));
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
	public void testRegistDevice02() {
		String[] ids = null;
		String[] ids2 = null;
		try {
			TestServiceListener listener = new TestServiceListener(ServiceEvent.REGISTERED);
			getContext().addServiceListener(listener, "(objectClass=org.osgi.service.usbinfo.USBInfoDevice)");

			String command = "registerDevice";
			String message = "Connect the first device set in System Properties.";
			String[] parameters = new String[23];
			parameters[0] = System.getProperty(USBTestConstants.PROP_BCDUSB);
			parameters[1] = System.getProperty(USBTestConstants.PROP_BDEVICECLASS);
			parameters[2] = System.getProperty(USBTestConstants.PROP_BDEVICESUBCLAS);
			parameters[3] = System.getProperty(USBTestConstants.PROP_BDEVICEPROTOCO);
			parameters[4] = System.getProperty(USBTestConstants.PROP_BMAXPACKETSIZE0);
			parameters[5] = System.getProperty(USBTestConstants.PROP_IDVENDOR);
			parameters[6] = System.getProperty(USBTestConstants.PROP_IDPRODUCT);
			parameters[7] = System.getProperty(USBTestConstants.PROP_BCDDEVICE);
			parameters[8] = System.getProperty(USBTestConstants.PROP_MANUFACTURER);
			parameters[9] = System.getProperty(USBTestConstants.PROP_PRODUCT);
			parameters[10] = System.getProperty(USBTestConstants.PROP_SERIALNUMBER);
			parameters[11] = System.getProperty(USBTestConstants.PROP_BNUMCONFIGURATIONS);
			parameters[12] = System.getProperty(USBTestConstants.PROP_BINTERFACENUMBER);
			parameters[13] = System.getProperty(USBTestConstants.PROP_BALTERNATESETTING);
			parameters[14] = System.getProperty(USBTestConstants.PROP_BNUMENDPOINTS);
			parameters[15] = System.getProperty(USBTestConstants.PROP_BINTERFACECLASS);
			parameters[16] = System.getProperty(USBTestConstants.PROP_BINTERFACESUBCLASS);
			parameters[17] = System.getProperty(USBTestConstants.PROP_BINTERFACEPROTOCOL);
			parameters[18] = System.getProperty(USBTestConstants.PROP_INTERFACE);
			parameters[19] = System.getProperty(USBTestConstants.PROP_BUS);
			parameters[20] = System.getProperty(USBTestConstants.PROP_ADDRESS);
			ids = testProxy.executeTestStep(command, message, parameters);

			String[] parameters2 = new String[23];
			String message2 = "Connect the second device set in System Properties.";
			parameters2[0] = System.getProperty(USBTestConstants.PROP_BCDUSB_2);
			parameters2[1] = System.getProperty(USBTestConstants.PROP_BDEVICECLASS_2);
			parameters2[2] = System.getProperty(USBTestConstants.PROP_BDEVICESUBCLAS_2);
			parameters2[3] = System.getProperty(USBTestConstants.PROP_BDEVICEPROTOCO_2);
			parameters2[4] = System.getProperty(USBTestConstants.PROP_BMAXPACKETSIZE0_2);
			parameters2[5] = System.getProperty(USBTestConstants.PROP_IDVENDOR_2);
			parameters2[6] = System.getProperty(USBTestConstants.PROP_IDPRODUCT_2);
			parameters2[7] = System.getProperty(USBTestConstants.PROP_BCDDEVICE_2);
			parameters2[8] = System.getProperty(USBTestConstants.PROP_MANUFACTURER_2);
			parameters2[9] = System.getProperty(USBTestConstants.PROP_PRODUCT_2);
			parameters2[10] = System.getProperty(USBTestConstants.PROP_SERIALNUMBER_2);
			parameters2[11] = System.getProperty(USBTestConstants.PROP_BNUMCONFIGURATIONS_2);
			parameters2[12] = System.getProperty(USBTestConstants.PROP_BINTERFACENUMBER_2);
			parameters2[13] = System.getProperty(USBTestConstants.PROP_BALTERNATESETTING_2);
			parameters2[14] = System.getProperty(USBTestConstants.PROP_BNUMENDPOINTS_2);
			parameters2[15] = System.getProperty(USBTestConstants.PROP_BINTERFACECLASS_2);
			parameters2[16] = System.getProperty(USBTestConstants.PROP_BINTERFACESUBCLASS_2);
			parameters2[17] = System.getProperty(USBTestConstants.PROP_BINTERFACEPROTOCOL_2);
			parameters2[18] = System.getProperty(USBTestConstants.PROP_INTERFACE_2);
			parameters2[19] = System.getProperty(USBTestConstants.PROP_BUS_2);
			parameters2[20] = System.getProperty(USBTestConstants.PROP_ADDRESS_2);
			ids2 = testProxy.executeTestStep(command, message2, parameters2);

			getContext().removeServiceListener(listener);

			assertEquals(2, listener.size());

			ServiceReference ref = listener.get(0);
			String[] category = (String[]) ref.getProperty(org.osgi.service.device.Constants.DEVICE_CATEGORY);
			boolean categoryFlag = false;
			for (int i = 0; i < category.length; i++) {
				if (category[i].equals(USBInfoDevice.DEVICE_CATEGORY)) {
					categoryFlag = true;
					break;
				}
			}
			assertTrue(categoryFlag);
			String bcdUSB = (String) ref.getProperty(USBInfoDevice.USB_BCDUSB);
			if (bcdUSB != null) {
				assertEquals(parameters[0], bcdUSB);
			}
			assertEquals(parameters[1], ref.getProperty(USBInfoDevice.USB_BDEVICECLASS));
			assertEquals(parameters[2], ref.getProperty(USBInfoDevice.USB_BDEVICESUBCLASS));
			assertEquals(parameters[3], ref.getProperty(USBInfoDevice.USB_BDEVICEPROTOCOL));
			Integer bMaxPacketSize0 = (Integer) ref.getProperty(USBInfoDevice.USB_BMAXPACKETSIZE0);
			if (bMaxPacketSize0 != null) {
				assertEquals(Integer.decode(parameters[4]), bMaxPacketSize0);
			}
			assertEquals(parameters[5], ref.getProperty(USBInfoDevice.USB_IDVENDOR));
			assertEquals(parameters[6], ref.getProperty(USBInfoDevice.USB_IDPRODUCT));
			assertEquals(parameters[7], ref.getProperty(USBInfoDevice.USB_BCDDEVICE));
			String manufacturer = (String) ref.getProperty(USBInfoDevice.USB_MANUFACTURER);
			if (manufacturer != null) {
				assertEquals(parameters[8], manufacturer);
			}
			String product = (String) ref.getProperty(USBInfoDevice.USB_PRODUCT);
			if (product != null) {
				assertEquals(parameters[9], product);
			}
			String serialNumber = (String) ref.getProperty(USBInfoDevice.USB_SERIALNUMBER);
			if (serialNumber != null) {
				assertEquals(parameters[10], serialNumber);
			}
			Integer bNumConfigurations = (Integer) ref.getProperty(USBInfoDevice.USB_BNUMCONFIGURATIONS);
			if (bNumConfigurations != null) {
				assertEquals(Integer.decode(parameters[11]), bNumConfigurations);
			}
			assertEquals(Integer.decode(parameters[12]), ref.getProperty(USBInfoDevice.USB_BINTERFACENUMBER));
			Integer bAlternateSetting = (Integer) ref.getProperty(USBInfoDevice.USB_BALTERNATESETTING);
			if (bAlternateSetting != null) {
				assertEquals(Integer.decode(parameters[13]), bAlternateSetting);
			}
			Integer bNumEndpoints = (Integer) ref.getProperty(USBInfoDevice.USB_BNUMENDPOINTS);
			if (bNumEndpoints != null) {
				assertEquals(Integer.decode(parameters[14]), bNumEndpoints);
			}
			assertEquals(parameters[15], ref.getProperty(USBInfoDevice.USB_BINTERFACECLASS));
			assertEquals(parameters[16], ref.getProperty(USBInfoDevice.USB_BINTERFACESUBCLASS));
			assertEquals(parameters[17], ref.getProperty(USBInfoDevice.USB_BINTERFACEPROTOCOL));
			String interfaceDescription = (String) ref.getProperty(USBInfoDevice.USB_INTERFACE);
			if (interfaceDescription != null) {
				assertEquals(parameters[18], interfaceDescription);
			}
			assertEquals(Integer.decode(parameters[19]), ref.getProperty(USBInfoDevice.USB_BUS));
			assertEquals(Integer.decode(parameters[20]), ref.getProperty(USBInfoDevice.USB_ADDRESS));

			ref = listener.get(1);
			category = (String[]) ref.getProperty(org.osgi.service.device.Constants.DEVICE_CATEGORY);
			categoryFlag = false;
			for (int i = 0; i < category.length; i++) {
				if (category[i].equals(USBInfoDevice.DEVICE_CATEGORY)) {
					categoryFlag = true;
					break;
				}
			}
			assertTrue(categoryFlag);
			bcdUSB = (String) ref.getProperty(USBInfoDevice.USB_BCDUSB);
			if (bcdUSB != null) {
				assertEquals(parameters2[0], bcdUSB);
			}
			assertEquals(parameters2[1], ref.getProperty(USBInfoDevice.USB_BDEVICECLASS));
			assertEquals(parameters2[2], ref.getProperty(USBInfoDevice.USB_BDEVICESUBCLASS));
			assertEquals(parameters2[3], ref.getProperty(USBInfoDevice.USB_BDEVICEPROTOCOL));
			bMaxPacketSize0 = (Integer) ref.getProperty(USBInfoDevice.USB_BMAXPACKETSIZE0);
			if (bMaxPacketSize0 != null) {
				assertEquals(Integer.decode(parameters2[4]), bMaxPacketSize0);
			}
			assertEquals(parameters2[5], ref.getProperty(USBInfoDevice.USB_IDVENDOR));
			assertEquals(parameters2[6], ref.getProperty(USBInfoDevice.USB_IDPRODUCT));
			assertEquals(parameters2[7], ref.getProperty(USBInfoDevice.USB_BCDDEVICE));
			manufacturer = (String) ref.getProperty(USBInfoDevice.USB_MANUFACTURER);
			if (manufacturer != null) {
				assertEquals(parameters2[8], manufacturer);
			}
			product = (String) ref.getProperty(USBInfoDevice.USB_PRODUCT);
			if (product != null) {
				assertEquals(parameters2[9], product);
			}
			serialNumber = (String) ref.getProperty(USBInfoDevice.USB_SERIALNUMBER);
			if (serialNumber != null) {
				assertEquals(parameters2[10], serialNumber);
			}
			bNumConfigurations = (Integer) ref.getProperty(USBInfoDevice.USB_BNUMCONFIGURATIONS);
			if (bNumConfigurations != null) {
				assertEquals(Integer.decode(parameters2[11]), bNumConfigurations);
			}
			assertEquals(Integer.decode(parameters2[12]), ref.getProperty(USBInfoDevice.USB_BINTERFACENUMBER));
			bAlternateSetting = (Integer) ref.getProperty(USBInfoDevice.USB_BALTERNATESETTING);
			if (bAlternateSetting != null) {
				assertEquals(Integer.decode(parameters2[13]), bAlternateSetting);
			}
			bNumEndpoints = (Integer) ref.getProperty(USBInfoDevice.USB_BNUMENDPOINTS);
			if (bNumEndpoints != null) {
				assertEquals(Integer.decode(parameters2[14]), bNumEndpoints);
			}
			assertEquals(parameters2[15], ref.getProperty(USBInfoDevice.USB_BINTERFACECLASS));
			assertEquals(parameters2[16], ref.getProperty(USBInfoDevice.USB_BINTERFACESUBCLASS));
			assertEquals(parameters2[17], ref.getProperty(USBInfoDevice.USB_BINTERFACEPROTOCOL));
			interfaceDescription = (String) ref.getProperty(USBInfoDevice.USB_INTERFACE);
			if (interfaceDescription != null) {
				assertEquals(parameters2[18], interfaceDescription);
			}
			assertEquals(Integer.decode(parameters2[19]), ref.getProperty(USBInfoDevice.USB_BUS));
			assertEquals(Integer.decode(parameters2[20]), ref.getProperty(USBInfoDevice.USB_ADDRESS));
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
	public void testUuregistDevice01() {
		String[] ids = null;
		try {
			String command = "registerDevice";
			String message = "Connect the device set in System Properties.";
			String[] parameters = new String[23];
			parameters[0] = null;
			parameters[1] = System.getProperty(USBTestConstants.PROP_BDEVICECLASS);
			parameters[2] = System.getProperty(USBTestConstants.PROP_BDEVICESUBCLAS);
			parameters[3] = System.getProperty(USBTestConstants.PROP_BDEVICEPROTOCO);
			parameters[4] = null;
			parameters[5] = System.getProperty(USBTestConstants.PROP_IDVENDOR);
			parameters[6] = System.getProperty(USBTestConstants.PROP_IDPRODUCT);
			parameters[7] = System.getProperty(USBTestConstants.PROP_BCDDEVICE);
			parameters[8] = null;
			parameters[9] = null;
			parameters[10] = null;
			parameters[11] = null;
			parameters[12] = System.getProperty(USBTestConstants.PROP_BINTERFACENUMBER);
			parameters[13] = null;
			parameters[14] = null;
			parameters[15] = System.getProperty(USBTestConstants.PROP_BINTERFACECLASS);
			parameters[16] = System.getProperty(USBTestConstants.PROP_BINTERFACESUBCLASS);
			parameters[17] = System.getProperty(USBTestConstants.PROP_BINTERFACEPROTOCOL);
			parameters[18] = null;
			parameters[19] = System.getProperty(USBTestConstants.PROP_BUS);
			parameters[20] = System.getProperty(USBTestConstants.PROP_ADDRESS);
			ids = testProxy.executeTestStep(command, message, parameters);

			TestServiceListener listener = new TestServiceListener(ServiceEvent.UNREGISTERING);
			getContext().addServiceListener(listener, "(objectClass=org.osgi.service.usbinfo.USBInfoDevice)");

			command = "unregisterDevice";
			message = "Disonnect the device.";
			parameters = new String[] {ids[0]};
			testProxy.executeTestStep(command, message, parameters);

			getContext().removeServiceListener(listener);

			assertEquals(1, listener.size());

			Long registerId = Long.valueOf(ids[0]);
			Long unregisterId = (Long) listener.get(0).getProperty(Constants.SERVICE_ID);
			assertEquals(registerId, unregisterId);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage(), e);
		} finally {
		}
	}

	/**
	 * Tests unregister a USBInfoDevice service. 1. register A 2. register B 3.
	 * unregister A
	 */
	public void testUuregistDevice02() {
		String[] ids = null;
		String[] ids2 = null;
		try {
			String command = "registerDevice";
			String message = "Connect the first device set in System Properties.";
			String[] parameters = new String[23];
			parameters[0] = null;
			parameters[1] = System.getProperty(USBTestConstants.PROP_BDEVICECLASS);
			parameters[2] = System.getProperty(USBTestConstants.PROP_BDEVICESUBCLAS);
			parameters[3] = System.getProperty(USBTestConstants.PROP_BDEVICEPROTOCO);
			parameters[4] = null;
			parameters[5] = System.getProperty(USBTestConstants.PROP_IDVENDOR);
			parameters[6] = System.getProperty(USBTestConstants.PROP_IDPRODUCT);
			parameters[7] = System.getProperty(USBTestConstants.PROP_BCDDEVICE);
			parameters[8] = null;
			parameters[9] = null;
			parameters[10] = null;
			parameters[11] = null;
			parameters[12] = System.getProperty(USBTestConstants.PROP_BINTERFACENUMBER);
			parameters[13] = null;
			parameters[14] = null;
			parameters[15] = System.getProperty(USBTestConstants.PROP_BINTERFACECLASS);
			parameters[16] = System.getProperty(USBTestConstants.PROP_BINTERFACESUBCLASS);
			parameters[17] = System.getProperty(USBTestConstants.PROP_BINTERFACEPROTOCOL);
			parameters[18] = null;
			parameters[19] = System.getProperty(USBTestConstants.PROP_BUS);
			parameters[20] = System.getProperty(USBTestConstants.PROP_ADDRESS);
			ids = testProxy.executeTestStep(command, message, parameters);

			message = "Connect the second device set in System Properties.";
			parameters[0] = null;
			parameters[1] = System.getProperty(USBTestConstants.PROP_BDEVICECLASS_2);
			parameters[2] = System.getProperty(USBTestConstants.PROP_BDEVICESUBCLAS_2);
			parameters[3] = System.getProperty(USBTestConstants.PROP_BDEVICEPROTOCO_2);
			parameters[4] = null;
			parameters[5] = System.getProperty(USBTestConstants.PROP_IDVENDOR_2);
			parameters[6] = System.getProperty(USBTestConstants.PROP_IDPRODUCT_2);
			parameters[7] = System.getProperty(USBTestConstants.PROP_BCDDEVICE_2);
			parameters[8] = null;
			parameters[9] = null;
			parameters[10] = null;
			parameters[11] = null;
			parameters[12] = System.getProperty(USBTestConstants.PROP_BINTERFACENUMBER_2);
			parameters[13] = null;
			parameters[14] = null;
			parameters[15] = System.getProperty(USBTestConstants.PROP_BINTERFACECLASS_2);
			parameters[16] = System.getProperty(USBTestConstants.PROP_BINTERFACESUBCLASS_2);
			parameters[17] = System.getProperty(USBTestConstants.PROP_BINTERFACEPROTOCOL_2);
			parameters[18] = null;
			parameters[19] = System.getProperty(USBTestConstants.PROP_BUS_2);
			parameters[20] = System.getProperty(USBTestConstants.PROP_ADDRESS_2);
			ids2 = testProxy.executeTestStep(command, message, parameters);

			TestServiceListener listener = new TestServiceListener(ServiceEvent.UNREGISTERING);
			getContext().addServiceListener(listener, "(objectClass=org.osgi.service.usbinfo.USBInfoDevice)");

			command = "unregisterDevice";
			message = "Disconnect the first device.";
			parameters = new String[] {ids[0]};
			testProxy.executeTestStep(command, message, parameters);

			getContext().removeServiceListener(listener);

			assertEquals(1, listener.size());

			Long registerId = Long.valueOf(ids[0]);
			Long unregisterId = (Long) listener.get(0).getProperty(Constants.SERVICE_ID);
			assertEquals(registerId, unregisterId);
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
	public void testUuregistDevice03() {
		String[] ids = null;
		String[] ids2 = null;
		try {
			String command = "registerDevice";
			String message = "Connect the first device set in System Properties.";
			String[] parameters = new String[23];
			parameters[0] = null;
			parameters[1] = System.getProperty(USBTestConstants.PROP_BDEVICECLASS);
			parameters[2] = System.getProperty(USBTestConstants.PROP_BDEVICESUBCLAS);
			parameters[3] = System.getProperty(USBTestConstants.PROP_BDEVICEPROTOCO);
			parameters[4] = null;
			parameters[5] = System.getProperty(USBTestConstants.PROP_IDVENDOR);
			parameters[6] = System.getProperty(USBTestConstants.PROP_IDPRODUCT);
			parameters[7] = System.getProperty(USBTestConstants.PROP_BCDDEVICE);
			parameters[8] = null;
			parameters[9] = null;
			parameters[10] = null;
			parameters[11] = null;
			parameters[12] = System.getProperty(USBTestConstants.PROP_BINTERFACENUMBER);
			parameters[13] = null;
			parameters[14] = null;
			parameters[15] = System.getProperty(USBTestConstants.PROP_BINTERFACECLASS);
			parameters[16] = System.getProperty(USBTestConstants.PROP_BINTERFACESUBCLASS);
			parameters[17] = System.getProperty(USBTestConstants.PROP_BINTERFACEPROTOCOL);
			parameters[18] = null;
			parameters[19] = System.getProperty(USBTestConstants.PROP_BUS);
			parameters[20] = System.getProperty(USBTestConstants.PROP_ADDRESS);
			ids = testProxy.executeTestStep(command, message, parameters);

			message = "Connect the second device set in System Properties.";
			parameters[0] = null;
			parameters[1] = System.getProperty(USBTestConstants.PROP_BDEVICECLASS_2);
			parameters[2] = System.getProperty(USBTestConstants.PROP_BDEVICESUBCLAS_2);
			parameters[3] = System.getProperty(USBTestConstants.PROP_BDEVICEPROTOCO_2);
			parameters[4] = null;
			parameters[5] = System.getProperty(USBTestConstants.PROP_IDVENDOR_2);
			parameters[6] = System.getProperty(USBTestConstants.PROP_IDPRODUCT_2);
			parameters[7] = System.getProperty(USBTestConstants.PROP_BCDDEVICE_2);
			parameters[8] = null;
			parameters[9] = null;
			parameters[10] = null;
			parameters[11] = null;
			parameters[12] = System.getProperty(USBTestConstants.PROP_BINTERFACENUMBER_2);
			parameters[13] = null;
			parameters[14] = null;
			parameters[15] = System.getProperty(USBTestConstants.PROP_BINTERFACECLASS_2);
			parameters[16] = System.getProperty(USBTestConstants.PROP_BINTERFACESUBCLASS_2);
			parameters[17] = System.getProperty(USBTestConstants.PROP_BINTERFACEPROTOCOL_2);
			parameters[18] = null;
			parameters[19] = System.getProperty(USBTestConstants.PROP_BUS_2);
			parameters[20] = System.getProperty(USBTestConstants.PROP_ADDRESS_2);
			ids2 = testProxy.executeTestStep(command, message, parameters);

			TestServiceListener listener = new TestServiceListener(ServiceEvent.UNREGISTERING);
			getContext().addServiceListener(listener, "(objectClass=org.osgi.service.usbinfo.USBInfoDevice)");

			command = "unregisterDevice";
			message = "Disconnect the first device.";
			parameters = new String[] {ids[0]};
			testProxy.executeTestStep(command, message, parameters);

			message = "Disconnect the second device.";
			parameters = new String[] {ids2[0]};
			testProxy.executeTestStep(command, message, parameters);

			getContext().removeServiceListener(listener);

			assertEquals(2, listener.size());

			Long registerId = Long.valueOf(ids[0]);
			Long unregisterId = (Long) listener.get(0).getProperty(Constants.SERVICE_ID);
			assertEquals(registerId, unregisterId);

			registerId = Long.valueOf(ids2[0]);
			unregisterId = (Long) listener.get(1).getProperty(Constants.SERVICE_ID);
			assertEquals(registerId, unregisterId);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage(), e);
		} finally {
		}
	}
}

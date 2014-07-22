
package org.osgi.test.cases.enocean;

import org.osgi.framework.ServiceReference;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.test.cases.enocean.messages.MessageA5_02_01;
import org.osgi.test.cases.enocean.serial.EspRadioPacket;
import org.osgi.test.cases.enocean.utils.Fixtures;
import org.osgi.test.cases.enocean.utils.Logger;

/**
 *
 */
public class RegistrationTests extends EnOceanTestCase {

	/**
	 * Tests initial device registration from a raw Radio teach-in packet.
	 * 
	 * @throws Exception
	 */
	public void testAutoDeviceRegistration() throws Exception {

		/* Insert a device */
		MessageA5_02_01 teachIn = MessageA5_02_01.generateTeachInMsg(Fixtures.HOST_ID, Fixtures.MANUFACTURER);
		EspRadioPacket pkt = new EspRadioPacket(teachIn);
		serialOutputStream.write(pkt.serialize());
		serialOutputStream.flush();

		// Device added
		String lastServiceEvent = devices.waitForService();
		Logger.d("Device added, lastServiceEvent: " + lastServiceEvent);
		// Device modified (profile)
		lastServiceEvent = devices.waitForService();
		Logger.d("Device modified (profile), lastServiceEvent: " + lastServiceEvent);

		/*
		 * NOTE: The service should have been modified AFTER insertion,
		 * nevertheless it seems that when registration and modification happen
		 * almost in the same time, OSGi only generates a single SERVICE_ADDED
		 * event.
		 */
		ServiceReference ref = devices.getServiceReference();

		/*
		 * Verify that the device has been registered with the correct service
		 * properties
		 */
		assertEquals("category mismatch", EnOceanDevice.DEVICE_CATEGORY, ref.getProperty(org.osgi.service.device.Constants.DEVICE_CATEGORY));
		assertEquals("CHIP_ID mismatch", Fixtures.STR_HOST_ID, ref.getProperty(EnOceanDevice.CHIP_ID));
		assertEquals("RORG mismatch", Fixtures.STR_RORG, ref.getProperty(EnOceanDevice.RORG));
		assertEquals("FUNC mismatch", Fixtures.STR_FUNC, ref.getProperty(EnOceanDevice.FUNC));
		assertEquals("TYPE mismatch", Fixtures.STR_TYPE_1, ref.getProperty(EnOceanDevice.TYPE));
		assertEquals("MANUFACTURER mismatch", Fixtures.STR_MANUFACTURER, ref.getProperty(EnOceanDevice.MANUFACTURER));

		getContext().ungetService(ref);
	}
}

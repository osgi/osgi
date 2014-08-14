
package org.osgi.test.cases.enocean;

import org.osgi.framework.ServiceReference;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.test.cases.enocean.messages.MessageF6_02_01;
import org.osgi.test.cases.enocean.serial.EspRadioPacket;
import org.osgi.test.cases.enocean.utils.Fixtures;
import org.osgi.test.cases.enocean.utils.ServiceListener;

/**
 *
 */
public class ManualRegistrationTestCase extends EnOceanTestCase {

	/**
	 * Tests initial device registration from a raw Radio teach-in packet.
	 * 
	 * @throws Exception
	 */
	public void testManualDeviceRegistration() throws Exception {

		/* Insert a device */
		MessageF6_02_01 teachIn = new MessageF6_02_01(1, true, 1, true);
		teachIn.setSenderId(Fixtures.HOST_ID_2);
		EspRadioPacket pkt = new EspRadioPacket(teachIn);
		// Push everything in the command...
		testStepService.execute(pkt.serialize());

		String lastServiceEvent = devices.waitForService();
		log("DEBUG: lastServiceEvent: " + lastServiceEvent);
		ServiceReference ref = devices.getServiceReference();
		log("DEBUG: ref: " + ref);

		assertEquals(ServiceListener.SERVICE_ADDED, lastServiceEvent);
		assertEquals("CHIP_ID mismatch", Fixtures.STR_HOST_ID_2, ref.getProperty(EnOceanDevice.CHIP_ID));
		assertEquals("RORG mismatch", Fixtures.STR_RORG_RPS, ref.getProperty(EnOceanDevice.RORG));
		assertNull(ref.getProperty(EnOceanDevice.FUNC));

		log("DEBUG: Thread.sleep(500);");
		Thread.sleep(500); // for some reason the fw may not have had time to
							// register properly

		EnOceanDevice dev = (EnOceanDevice) getContext().getService(ref);
		dev.setFunc(Fixtures.FUNC);
		lastServiceEvent = devices.waitForService();
		assertEquals(ServiceListener.SERVICE_MODIFIED, lastServiceEvent);

		dev.setType(Fixtures.TYPE_1);
		lastServiceEvent = devices.waitForService();
		assertEquals(ServiceListener.SERVICE_MODIFIED, lastServiceEvent);

		assertEquals("FUNC mismatch", Fixtures.STR_FUNC, ref.getProperty(EnOceanDevice.FUNC));
		assertEquals("TYPE mismatch", Fixtures.STR_TYPE_1, ref.getProperty(EnOceanDevice.TYPE));

		getContext().ungetService(ref);
	}

}

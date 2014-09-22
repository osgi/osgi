
package org.osgi.test.cases.enocean;

import org.osgi.framework.ServiceReference;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.test.cases.enocean.messages.MessageExample2;
import org.osgi.test.cases.enocean.serial.EspRadioPacket;
import org.osgi.test.cases.enocean.utils.Fixtures;
import org.osgi.test.cases.enocean.utils.ServiceListener;

/**
 * ManualRegistrationTestCase:
 * 
 * - testManualDeviceRegistration, tests initial device registration from a raw
 * Radio teach-in packet that doesn’t contain any profile data, and that is
 * triggered through the step service. This test thus requires the end-user
 * involvement. Finally, the CT checks that the device's profile has been
 * properly updated.
 */
public class ManualRegistrationTestCase extends AbstractEnOceanTestCase {

	/**
	 * Tests initial device registration from a raw Radio teach-in packet.
	 * 
	 * @throws Exception
	 */
	public void testManualDeviceRegistration() throws Exception {

		/* Insert a device */
		MessageExample2 teachIn = new MessageExample2(1, true, 1, true);
		teachIn.setSenderId(Fixtures.HOST_ID_2);
		EspRadioPacket pkt = new EspRadioPacket(teachIn);
		// Push everything in the command...
		String[] params = {new String(pkt.serialize())};
		testStepService.execute("MessageExample2", params);

		String lastServiceEvent = devices.waitForService();
		log("DEBUG: lastServiceEvent: " + lastServiceEvent);
		assertNotNull("Timeout reached.", lastServiceEvent);
		ServiceReference ref = devices.getServiceReference();
		log("DEBUG: ref: " + ref);

		assertEquals("Event mismatch", ServiceListener.SERVICE_ADDED, lastServiceEvent);
		assertEquals("CHIP_ID mismatch", Fixtures.STR_HOST_ID_2, ref.getProperty(EnOceanDevice.CHIP_ID));
		assertEquals("RORG mismatch", Fixtures.STR_RORG_RPS, ref.getProperty(EnOceanDevice.RORG));
		assertNull("ref.getProperty(EnOceanDevice.FUNC) must not be null.", ref.getProperty(EnOceanDevice.FUNC));

		log("DEBUG: Thread.sleep(500);");
		Thread.sleep(500); // for some reason the fw may not have had time to
							// register properly

		EnOceanDevice dev = (EnOceanDevice) getContext().getService(ref);
		dev.setFunc(Fixtures.FUNC);
		lastServiceEvent = devices.waitForService();
		assertNotNull("Timeout reached.", lastServiceEvent);
		assertEquals("Event mismatch", ServiceListener.SERVICE_MODIFIED, lastServiceEvent);

		dev.setType(Fixtures.TYPE_1);
		lastServiceEvent = devices.waitForService();
		assertNotNull("Timeout reached.", lastServiceEvent);
		assertEquals("Event mismatch", ServiceListener.SERVICE_MODIFIED, lastServiceEvent);

		assertEquals("FUNC mismatch", Fixtures.STR_FUNC, ref.getProperty(EnOceanDevice.FUNC));
		assertEquals("TYPE mismatch", Fixtures.STR_TYPE_1, ref.getProperty(EnOceanDevice.TYPE));

		getContext().ungetService(ref);
	}

}

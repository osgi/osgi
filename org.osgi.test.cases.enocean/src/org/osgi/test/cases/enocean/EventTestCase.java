
package org.osgi.test.cases.enocean;

import org.osgi.framework.ServiceReference;
import org.osgi.service.enocean.EnOceanChannel;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanEvent;
import org.osgi.service.enocean.EnOceanMessage;
import org.osgi.service.enocean.descriptions.EnOceanMessageDescription;
import org.osgi.service.event.Event;
import org.osgi.test.cases.enocean.descriptions.EnOceanMessageDescription_A5_02_01;
import org.osgi.test.cases.enocean.messages.MessageExample1;
import org.osgi.test.cases.enocean.serial.EspRadioPacket;
import org.osgi.test.cases.enocean.utils.Fixtures;
import org.osgi.test.cases.enocean.utils.ServiceListener;

/**
 * This class contains:
 * 
 * - testEventNotification, tests event notification when passing an actual
 * message to the Base Driver. This also tests the MessageSet registration since
 * the Base Driver needs to know about it before firing on EventAdmin.
 */
public class EventTestCase extends AbstractEnOceanTestCase {

	/**
	 * Test event notification in the context of an actual message passing to
	 * the Base Driver.
	 * 
	 * This actually also tests the MessageSet registration since the Base
	 * Driver needs to know about it before firing on EventAdmin.
	 * 
	 * @throws InterruptedException
	 */
	public void testEventNotification() throws InterruptedException {
		/* Insert a device */
		MessageExample1 teachIn = MessageExample1.generateTeachInMsg(Fixtures.HOST_ID, Fixtures.MANUFACTURER);
		EspRadioPacket teachInPkt = new EspRadioPacket(teachIn);
		// Push everything in the command...
		String[] params = {new String(teachInPkt.serialize())};
		testStepService.execute("MessageExample1", params);

		/* First get a reference towards the device */
		String lastServiceEvent = devices.waitForService();
		assertNotNull("Timeout reached.", lastServiceEvent);
		ServiceReference ref = devices.getServiceReference();
		getContext().ungetService(ref);
		assertEquals("did not have service addition", ServiceListener.SERVICE_ADDED, lastServiceEvent);

		/* Send a message from that device */
		MessageExample1 measure = new MessageExample1(Fixtures.TEMPERATURE);
		measure.setSenderId(Fixtures.HOST_ID);
		EspRadioPacket measurePkt = new EspRadioPacket(measure);
		// Push everything in the command...
		String[] params2 = {new String(measurePkt.serialize())};
		testStepService.execute("MessageExample1", params2);

		Event event = events.waitForEvent();
		assertNotNull("Timeout reached.", event);

		assertEquals("topic mismatch", EnOceanEvent.TOPIC_MSG_RECEIVED, event.getTopic());
		assertEquals("senderId mismatch", Fixtures.STR_HOST_ID, event.getProperty(EnOceanDevice.CHIP_ID));
		assertEquals("rorg mismatch", Fixtures.STR_RORG, event.getProperty(EnOceanDevice.RORG));
		assertEquals("func mismatch", Fixtures.STR_FUNC, event.getProperty(EnOceanDevice.FUNC));
		assertEquals("type mismatch", Fixtures.STR_TYPE_1, event.getProperty(EnOceanDevice.TYPE));

		EnOceanMessage msg = (EnOceanMessage) event.getProperty(EnOceanEvent.PROPERTY_MESSAGE);
		assertNotNull("Msg must not be null.", msg);
		EnOceanMessageDescription description = new EnOceanMessageDescription_A5_02_01();
		EnOceanChannel[] channels = description.deserialize(msg.getPayloadBytes());
		assertEquals("temperature mismatch", Fixtures.RAW_TEMPERATURE, channels[0].getRawValue()[0]);
	}
}

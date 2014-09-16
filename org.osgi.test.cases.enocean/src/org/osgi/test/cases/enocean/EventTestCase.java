
package org.osgi.test.cases.enocean;

import java.util.Hashtable;
import java.util.Map;
import org.osgi.framework.ServiceReference;
import org.osgi.service.enocean.EnOceanChannel;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanEvent;
import org.osgi.service.enocean.EnOceanMessage;
import org.osgi.service.enocean.descriptions.EnOceanMessageDescription;
import org.osgi.service.event.Event;
import org.osgi.test.cases.enocean.descriptions.EnOceanMessageDescription_A5_02_01;
import org.osgi.test.cases.enocean.messages.MessageA5_02_01;
import org.osgi.test.cases.enocean.serial.EspRadioPacket;
import org.osgi.test.cases.enocean.utils.Fixtures;
import org.osgi.test.cases.enocean.utils.ServiceListener;

/**
 * This class contains:
 * 
 * - testSelfEventReception, tests that the test suite is able to locally send
 * and receive messages.
 * 
 * - testEventNotification, tests event notification when passing an actual
 * message to the Base Driver. This also tests the MessageSet registration since
 * the Base Driver needs to know about it before firing on EventAdmin.
 */
public class EventTestCase extends EnOceanTestCase {

	/**
	 * Checks that our test suite is able to locally send and receive messages.
	 * Necessary for the rest of the code.
	 * 
	 * @throws Exception
	 */
	public void testSelfEventReception() throws Exception {
		Map properties = new Hashtable();
		properties.put(Fixtures.SELF_TEST_EVENT_KEY, Fixtures.SELF_TEST_EVENT_VALUE);
		Event sourceEvent = new Event(Fixtures.SELF_TEST_EVENT_TOPIC, properties);
		eventAdmin.sendEvent(sourceEvent);

		Event destinationEvent = events.waitForEvent();
		assertEquals("event name mismatch", Fixtures.SELF_TEST_EVENT_TOPIC, destinationEvent.getTopic());
		assertEquals("event property mismatch", Fixtures.SELF_TEST_EVENT_VALUE, destinationEvent.getProperty(Fixtures.SELF_TEST_EVENT_KEY));
	}

	/**
	 * Test event notification in the context of an actual message passing to
	 * the Base Driver.
	 * 
	 * This actually also tests the MessageSet registration since the Base
	 * Driver needs to know about it before firing on EventAdmin.
	 * 
	 * @throws Exception
	 */
	public void testEventNotification() throws Exception {
		/* Insert a device */
		MessageA5_02_01 teachIn = MessageA5_02_01.generateTeachInMsg(Fixtures.HOST_ID, Fixtures.MANUFACTURER);
		EspRadioPacket teachInPkt = new EspRadioPacket(teachIn);
		// Push everything in the command...
		String[] params = {new String(teachInPkt.serialize())};
		testStepService.execute("MessageA5_02_01", params);

		/* First get a reference towards the device */
		String lastServiceEvent = devices.waitForService();
		ServiceReference ref = devices.getServiceReference();
		getContext().ungetService(ref);
		assertEquals("did not have service addition", ServiceListener.SERVICE_ADDED, lastServiceEvent);

		/* Send a message from that device */
		MessageA5_02_01 measure = new MessageA5_02_01(Fixtures.TEMPERATURE);
		measure.setSenderId(Fixtures.HOST_ID);
		EspRadioPacket measurePkt = new EspRadioPacket(measure);
		// Push everything in the command...
		String[] params2 = {new String(measurePkt.serialize())};
		testStepService.execute("MessageA5_02_01", params2);

		Event event = events.waitForEvent();

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

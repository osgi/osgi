
package org.osgi.test.cases.enocean;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.Map;
import org.osgi.framework.ServiceReference;
import org.osgi.service.enocean.EnOceanChannel;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanEvent;
import org.osgi.service.enocean.EnOceanMessage;
import org.osgi.service.enocean.descriptions.EnOceanMessageDescription;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.test.cases.enocean.descriptions.EnOceanChannelDescription_TMP_00;
import org.osgi.test.cases.enocean.descriptions.EnOceanMessageDescription_A5_02_01;
import org.osgi.test.cases.enocean.messages.MessageA5_02_01;
import org.osgi.test.cases.enocean.serial.EspRadioPacket;
import org.osgi.test.cases.enocean.sets.EnOceanChannelDescriptionSetImpl;
import org.osgi.test.cases.enocean.sets.EnOceanMessageDescriptionSetImpl;
import org.osgi.test.cases.enocean.utils.EventListener;
import org.osgi.test.cases.enocean.utils.Fixtures;
import org.osgi.test.cases.enocean.utils.ServiceListener;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class EventTests extends DefaultTestBundleControl {

	private FileOutputStream	outStream;
	private ServiceListener		devices;
	private String				lastServiceEvent;
	private EventListener		events;
	private EventAdmin	eventAdmin;
	private EnOceanMessageDescriptionSetImpl	msgDescriptionSet;
	private EnOceanChannelDescriptionSetImpl	channelDescriptionSet;
	private ServiceReference					eventAdminRef;

	protected void setUp() throws Exception {
		String fakeDriverPath = System.getProperty("org.osgi.service.enocean.host.path");
		File file = new File(fakeDriverPath);
		if (!file.exists()) {
			file.createNewFile();
		}
		outStream = new FileOutputStream(file);

		/* Tracks device creation */
		devices = new ServiceListener(getContext(), EnOceanDevice.class);

		/* Tracks device events */
		String[] topics = new String[] {Fixtures.SELF_TEST_EVENT_TOPIC};
		events = new EventListener(getContext(), topics, null);

		/* Get a global eventAdmin handle */
		eventAdminRef = getContext().getServiceReference(EventAdmin.class.getName());
		eventAdmin = (EventAdmin) getContext().getService(eventAdminRef);

		/* Inserts some message documentation classes */
		msgDescriptionSet = new EnOceanMessageDescriptionSetImpl();
		msgDescriptionSet.putMessage(Fixtures.RORG, Fixtures.FUNC, Fixtures.TYPE_1, -1, new EnOceanMessageDescription_A5_02_01());

		channelDescriptionSet = new EnOceanChannelDescriptionSetImpl();
		channelDescriptionSet.putChannelDescription(Fixtures.TMP_CHANNEL_ID, new EnOceanChannelDescription_TMP_00());
	}

	protected void tearDown() throws Exception {
		devices.close();
		events.close();
		getContext().ungetService(eventAdminRef);
		cleanupServices();
	}

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
		outStream.write(teachInPkt.serialize());
	
		/* First get a reference towards the device */
		lastServiceEvent = devices.waitForService();
		ServiceReference ref = devices.getServiceReference();
		getContext().ungetService(ref);
		assertEquals("did not have service addition", ServiceListener.SERVICE_ADDED, lastServiceEvent);

		/* Send a message from that device */
		MessageA5_02_01 measure = new MessageA5_02_01(Fixtures.TEMPERATURE);
		measure.setSenderId(Fixtures.HOST_ID);
		EspRadioPacket measurePkt = new EspRadioPacket(measure);
		outStream.write(measurePkt.serialize());
	
		Event event = events.waitForEvent();
	
		assertEquals("topic mismatch", EnOceanEvent.TOPIC_MSG_RECEIVED, event.getTopic());
		assertEquals("senderId mismatch", Fixtures.STR_HOST_ID, event.getProperty(EnOceanDevice.CHIP_ID));
		assertEquals("rorg mismatch", Fixtures.STR_RORG, event.getProperty(EnOceanDevice.RORG));
		assertEquals("func mismatch", Fixtures.STR_FUNC, event.getProperty(EnOceanDevice.FUNC));
		assertEquals("type mismatch", Fixtures.STR_TYPE_1, event.getProperty(EnOceanDevice.TYPE));
	
		EnOceanMessage msg = (EnOceanMessage) event.getProperty(EnOceanEvent.PROPERTY_MESSAGE);
		assertNotNull(msg);
		EnOceanMessageDescription description = new EnOceanMessageDescription_A5_02_01();
		EnOceanChannel[] channels = description.deserialize(msg.getPayloadBytes());
		assertEquals("temperature mismatch", Fixtures.RAW_TEMPERATURE, channels[0].getRawValue()[0]);
	}

	private void cleanupServices() throws InterruptedException {
		ServiceReference[] references = devices.getServiceReferences();
		if (references != null) {
			for (int i = 0; i < references.length; i++) {
				ServiceReference ref = references[i];
				getContext().ungetService(ref);
				devices.remove(ref);
				String msg = devices.waitForService();
				log("Unregistering device service got  : " + msg);
				assertEquals("did not remove service", ServiceListener.SERVICE_REMOVED, msg);
			}
		} else {
			log("There were no extra references to cleanup");
		}
	
	}

}
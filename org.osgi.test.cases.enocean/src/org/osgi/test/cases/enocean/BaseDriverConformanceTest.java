
package org.osgi.test.cases.enocean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import org.osgi.framework.ServiceReference;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanMessage;
import org.osgi.service.enocean.sets.EnOceanMessageSet;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.test.cases.enocean.descriptions.EnOceanMessageSetImpl;
import org.osgi.test.cases.enocean.descriptions.EnOceanMessage_A5_02_01;
import org.osgi.test.cases.enocean.esp.EspRadioPacket;
import org.osgi.test.cases.enocean.radio.MessageA5_02_01;
import org.osgi.test.cases.enocean.utils.EventListener;
import org.osgi.test.cases.enocean.utils.ServiceListener;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class BaseDriverConformanceTest extends DefaultTestBundleControl {

	private FileInputStream		inStream;
	private FileOutputStream	outStream;
	private ServiceListener		devices;
	private ServiceListener		messageSets;
	private String				lastServiceEvent;
	private EventListener		events;
	private EventAdmin	eventAdmin;

	protected void setUp() throws Exception {
		String fakeDriverPath = System.getProperty("org.osgi.service.enocean.host.path");
		File file = new File(fakeDriverPath);
		if (!file.exists()) {
			file.createNewFile();
		}
		inStream = new FileInputStream(file);
		outStream = new FileOutputStream(file);

		/* Tracks device creation */
		devices = new ServiceListener(getContext(), EnOceanDevice.class);

		/* Tracks EnOceanMessageSet creation */
		messageSets = new ServiceListener(getContext(), EnOceanMessageSet.class);

		/* Tracks device events */
		String[] topics = new String[] {"org/osgi/service/enocean/EnOceanEvent/*"};
		events = new EventListener(getContext(), topics, null);

		/* Get a global eventAdmin handle */
		ServiceReference ref = getContext().getServiceReference(EventAdmin.class.getName());
		eventAdmin = (EventAdmin) getContext().getService(ref);

		/* Inserts some message documentation classes */
		EnOceanMessageSetImpl msgSet = new EnOceanMessageSetImpl();
		msgSet.putMessage(Fixtures.RORG, Fixtures.FUNC, Fixtures.TYPE, new EnOceanMessage_A5_02_01());

		Dictionary props = new Properties();
		props.put(EnOceanMessageSet.PROVIDER_ID, Fixtures.MESSAGESET_PROVIDER);
		props.put(EnOceanMessageSet.VERSION, Fixtures.MESSAGESET_VERSION);
		getContext().registerService(EnOceanMessageSet.class.getName(), msgSet, props);
	}

	protected void tearDown() throws Exception {
		cleanupServices();
		devices.close();
	}

	/**
	 * Tests initial device registration from a raw Radio teach-in packet.
	 * 
	 * @throws Exception
	 */
	public void testDeviceRegistration() throws Exception {

		/* Insert a device */
		MessageA5_02_01 teachIn = MessageA5_02_01.generateTeachInMsg(Fixtures.HOST_ID, Fixtures.MANUFACTURER);
		EspRadioPacket pkt = new EspRadioPacket(teachIn);
		outStream.write(pkt.serialize());

		lastServiceEvent = devices.waitForService();
		assertEquals("did not have service addition", ServiceListener.SERVICE_ADDED, lastServiceEvent);

		/*
		 * NOTE: The service should have been modified AFTER insertion,
		 * nevertheless it seems that when registration and modification happen
		 * almost in the same time, OSGi only generates a single SERVICE_ADDED
		 * event.
		 */
		log("Device service event happened : " + lastServiceEvent);

		/*
		 * Verify that the device has been registered with the correct service
		 * properties
		 */
		ServiceReference ref = devices.getServiceReference();
		assertEquals("CHIP_ID mismatch", Fixtures.STR_HOST_ID, ref.getProperty(EnOceanDevice.CHIP_ID));
		assertEquals("RORG mismatch", Fixtures.STR_RORG, ref.getProperty(EnOceanDevice.RORG));
		assertEquals("FUNC mismatch", Fixtures.STR_FUNC, ref.getProperty(EnOceanDevice.FUNC));
		assertEquals("TYPE mismatch", Fixtures.STR_TYPE, ref.getProperty(EnOceanDevice.TYPE));
		assertEquals("MANUFACTURER mismatch", Fixtures.STR_MANUFACTURER, ref.getProperty(EnOceanDevice.MANUFACTURER));
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

		/* Send a message from that device */
		MessageA5_02_01 measure = new MessageA5_02_01(Fixtures.TEMPERATURE);
		measure.setSenderId(Fixtures.HOST_ID);
		EspRadioPacket measurePkt = new EspRadioPacket(measure);
		outStream.write(measurePkt.serialize());

		/* First get a reference towards the device */
		lastServiceEvent = devices.waitForService();
		assertEquals("did not have service addition", ServiceListener.SERVICE_ADDED, lastServiceEvent);

		Event event = events.waitForEvent();

		assertEquals("topic mismatch", Fixtures.SELF_TEST_EVENT_TOPIC, event.getTopic());

		assertEquals("senderId mismatch", Fixtures.STR_HOST_ID, event.getProperty("enocean.senderId"));
		assertEquals("rorg mismatch", Fixtures.STR_RORG, event.getProperty("enocean.rorg"));
		assertEquals("func mismatch", Fixtures.STR_FUNC, event.getProperty("enocean.func"));
		assertEquals("type mismatch", Fixtures.STR_TYPE, event.getProperty("enocean.type"));

		EnOceanMessage_A5_02_01 msg = (EnOceanMessage_A5_02_01) event.getProperty("enocean.message");
		assertNotNull(msg);
		assertEquals("temperature mismatch", Fixtures.RAW_TEMPERATURE, msg.getChannels()[0].getRawValue()[0]);
	}

	/**
	 * Checks that the injected documentation (through service registration) has
	 * been correctly registered into the BaseDriver.
	 * 
	 * @throws InterruptedException
	 */
	public void testMessageDescriptionRegistration() throws InterruptedException {
		String type = messageSets.waitForService();
		ServiceReference ref = messageSets.getServiceReference();
		assertEquals("providerId mismatch", Fixtures.MESSAGESET_PROVIDER, ref.getProperty(EnOceanMessageSet.PROVIDER_ID));
		assertEquals("version mismatch", Fixtures.MESSAGESET_VERSION, ref.getProperty(EnOceanMessageSet.VERSION));
		EnOceanMessageSet set = (EnOceanMessageSet) getContext().getService(ref);
		EnOceanMessage cls = set.getMessage(0xA5, 0x02, 0x01, -1);
		assertEquals("class type mismatch", EnOceanMessage_A5_02_01.class, cls.getClass());
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
		}
	
	}

	private EnOceanDevice getLatestRegisteredDevice() {
		ServiceReference ref = devices.getServiceReference();
		Object service = getContext().getService(ref);
		if (service instanceof EnOceanDevice) {
			return (EnOceanDevice) service;
		}
		return null;
	}

}
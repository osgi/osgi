
package org.osgi.test.cases.enocean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.Map;
import org.osgi.framework.ServiceReference;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.test.cases.enocean.esp.EspRadioPacket;
import org.osgi.test.cases.enocean.radio.MessageA5_02_01;
import org.osgi.test.cases.enocean.utils.EventListener;
import org.osgi.test.cases.enocean.utils.ServiceListener;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class BaseDriverConformanceTest extends DefaultTestBundleControl {

	private FileInputStream		inStream;
	private FileOutputStream	outStream;
	private ServiceListener		devices;
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

		/* Tracks device events */
		String[] topics = new String[] {"org/osgi/service/enocean/EnOceanEvent/*"};
		events = new EventListener(getContext(), topics, null);

		/* Get a global eventAdmin handle */
		ServiceReference ref = getContext().getServiceReference(EventAdmin.class.getName());
		eventAdmin = (EventAdmin) getContext().getService(ref);

	}

	protected void tearDown() throws Exception {
		devices.close();
	}

	/**
	 * Tests initial device registration from a raw Radio teach-in packet.
	 * 
	 * @throws Exception
	 */
	public void testDeviceRegistration() throws Exception {
		/* Insert a device */
		MessageA5_02_01 teachIn = MessageA5_02_01.teachIn(Fixtures.HOST_ID, Fixtures.MANUFACTURER);
		EspRadioPacket pkt = new EspRadioPacket(teachIn);
		outStream.write(pkt.serialize());

		lastServiceEvent = devices.waitForService();
		assertEquals("First event is not a device addition", ServiceListener.SERVICE_ADDED, lastServiceEvent);
		lastServiceEvent = devices.waitForService();
		assertEquals("Second event is not a device modification", ServiceListener.SERVICE_MODIFIED, lastServiceEvent);

		/*
		 * Verify that the device has been registered with the correct service
		 * properties
		 */
		ServiceReference ref = devices.getServiceReference();
		assertEquals("CHIP_ID mismatch", Fixtures.HOST_ID, intProp(ref, EnOceanDevice.CHIP_ID));
		assertEquals("RORG mismatch", Fixtures.RORG, intProp(ref, EnOceanDevice.RORG));
		assertEquals("FUNC mismatch", Fixtures.FUNC, intProp(ref, EnOceanDevice.FUNC));
		assertEquals("TYPE mismatch", Fixtures.TYPE, intProp(ref, EnOceanDevice.TYPE));
		assertEquals("MANUFACTURER mismatch", Fixtures.MANUFACTURER, intProp(ref, EnOceanDevice.MANUFACTURER));
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
	 * @throws Exception
	 */
	public void testEventNotification() throws Exception {

		/* Insert a device */
		MessageA5_02_01 teachIn = MessageA5_02_01.teachIn(Fixtures.HOST_ID, Fixtures.MANUFACTURER);
		EspRadioPacket teachInPkt = new EspRadioPacket(teachIn);
		outStream.write(teachInPkt.serialize());

		/* Insert a message */
		MessageA5_02_01 measure = new MessageA5_02_01(-12.0f);
		EspRadioPacket measurePkt = new EspRadioPacket(measure);
		outStream.write(measurePkt.serialize());

		/* Wait for message notification */
		// TODO: send a temperature report and check how the message is passed
		// along.
		// Do we need to have a proper device registered to pass messages from
		// the network ?
		// MessageA5_02_01 teachIn = MessageA5_02_01.teachIn(Fixtures.HOST_ID,
		// Fixtures.MANUFACTURER);
		// EspRadioPacket pkt = new EspRadioPacket(teachIn);
		// outStream.write(pkt.serialize());


		// services.waitForRegistration();
	}

	public void testMessageDescriptionUse() {
		// TODO: test how a MessageDescriptionSet, once registered, is used
		// by the BaseDriver to build complex EnOceanMessage representations.
	}

	private int intProp(ServiceReference r, String key) {
		String property = (String) r.getProperty(key);
		// log("key = '" + key + "', value='" + property + "'");
		Integer p = Integer.valueOf(property);
		return p.intValue();
	}

	private EnOceanDevice serviceRef2Device(ServiceReference ref) {
		Object service = getContext().getService(ref);
		if (service instanceof EnOceanDevice) {
			return (EnOceanDevice) service;
		}
		return null;
	}

}
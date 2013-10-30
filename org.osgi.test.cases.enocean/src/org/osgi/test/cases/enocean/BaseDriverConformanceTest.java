
package org.osgi.test.cases.enocean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanMessage;
import org.osgi.service.enocean.EnOceanMessageDescription;
import org.osgi.service.enocean.channels.EnOceanChannel;
import org.osgi.service.enocean.channels.EnOceanChannelDescription;
import org.osgi.service.enocean.channels.EnOceanDataChannelDescription;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.test.cases.enocean.descriptions.EnOceanChannelDescription_TMP_00;
import org.osgi.test.cases.enocean.descriptions.EnOceanMessageDescription_A5_02_01;
import org.osgi.test.cases.enocean.devices.TemperatureSensingDevice;
import org.osgi.test.cases.enocean.messages.MessageA5_02_01;
import org.osgi.test.cases.enocean.serial.EspRadioPacket;
import org.osgi.test.cases.enocean.sets.EnOceanChannelDescriptionSetImpl;
import org.osgi.test.cases.enocean.sets.EnOceanMessageDescriptionSetImpl;
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
	private EnOceanMessageDescriptionSetImpl	msgDescriptionSet;
	private EnOceanChannelDescriptionSetImpl	channelDescriptionSet;

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
		String[] topics = new String[] {Fixtures.SELF_TEST_EVENT_TOPIC};
		events = new EventListener(getContext(), topics, null);

		/* Get a global eventAdmin handle */
		ServiceReference ref = getContext().getServiceReference(EventAdmin.class.getName());
		eventAdmin = (EventAdmin) getContext().getService(ref);

		/* Inserts some message documentation classes */
		msgDescriptionSet = new EnOceanMessageDescriptionSetImpl();
		msgDescriptionSet.putMessage(Fixtures.RORG, Fixtures.FUNC, Fixtures.TYPE_1, -1, new EnOceanMessageDescription_A5_02_01());

		channelDescriptionSet = new EnOceanChannelDescriptionSetImpl();
		channelDescriptionSet.putChannelDescription(Fixtures.TMP_CHANNEL_ID, new EnOceanChannelDescription_TMP_00());
	}

	protected void tearDown() throws Exception {
		devices.close();
		events.close();
		cleanupServices();
	}

	/**
	 * Test that a properly set profile ID in a raw EnOceanMessage is enough to
	 * extract all the information we need, provided we have the necessary
	 * descriptions.
	 * 
	 * @throws Exception
	 */
	public void testUseOfDescriptions() throws Exception {
		
		EnOceanMessage temperatureMsg = new MessageA5_02_01(Fixtures.TEMPERATURE);
		
		/*
		 * Here, we _know_ the message profile (A5-02-01). In a real context,
		 * the base driver would provide this information directly in the
		 * broadcasted EnOceanMessage objects through the getFunc() / getType()
		 * interface methods.
		 */
		EnOceanMessageDescription msgDescription = msgDescriptionSet.getMessageDescription(Fixtures.RORG, Fixtures.FUNC, Fixtures.TYPE_1, -1);
		assertNotNull(msgDescription);
		EnOceanChannel[] channels = msgDescription.deserialize(temperatureMsg.getPayloadBytes());
		assertEquals(2, channels.length);
		EnOceanChannel temperatureChannel = channels[0];
		String tmpChannelId = temperatureChannel.getChannelId();
		assertEquals(Fixtures.TMP_CHANNEL_ID, tmpChannelId);
		EnOceanChannelDescription channelDescription = channelDescriptionSet.getChannelDescription(tmpChannelId);
		assertEquals(Fixtures.TMP_CHANNEL_TYPE, channelDescription.getType());
		EnOceanDataChannelDescription dataChannelDescription = (EnOceanDataChannelDescription) channelDescription;
		// It's a float because it's a DATA channel
		Float deserializedTemperature = (Float) dataChannelDescription.deserialize(temperatureChannel.getRawValue());
		assertEquals(Fixtures.TEMPERATURE, deserializedTemperature.floatValue(), 0.1);
	}

	/**
	 * Test that a properly set profile ID in a raw EnOceanMessage is enough to
	 * extract all the information we need, provided we have the necessary
	 * descriptions.
	 * 
	 * Pay attention that almost all of those tests look more like a
	 * "recommendation" to be ran on external description bundles, since the
	 * description objects are not supposed to be part of the RI.
	 * 
	 * @throws Exception
	 */
	public void testInterfaceExceptions() throws Exception {
		boolean exceptionCaught = false;

		try { /*
			 * Check that passing a NULL array of bytes results in an
			 * IllegalArgumentException
			 */
			exceptionCaught = false;
			EnOceanMessageDescription msgDescription = new EnOceanMessageDescription_A5_02_01();
			msgDescription.deserialize(null);
		} catch (IllegalArgumentException e) {
			exceptionCaught = true;
		}
		assertEquals(true, exceptionCaught);

		try { /*
			 * Check that passing a wrongly sized byte array also results in an
			 * exception
			 */
			exceptionCaught = false;
			EnOceanMessageDescription msgDescription = new EnOceanMessageDescription_A5_02_01();
			msgDescription.deserialize(new byte[] {0x55, 0x02, 0x34, 0x56, 0x67});
		} catch (IllegalArgumentException e) {
			exceptionCaught = true;
		}
		assertEquals(true, exceptionCaught);
		
		try { /*
			 * Tests that serializing a NULL object in EnOceanChannelDescription
			 * in an exception
			 */
			exceptionCaught = false;
			EnOceanChannelDescription channelDescription = new EnOceanChannelDescription_TMP_00();
			channelDescription.serialize(null);
		} catch (IllegalArgumentException e) {
			exceptionCaught = true;
		}
		assertEquals(true, exceptionCaught);

		try { /*
			 * Tests that serializing a wrong object in
			 * EnOceanChannelDescription in an exception
			 */
			exceptionCaught = false;
			EnOceanChannelDescription channelDescription = new EnOceanChannelDescription_TMP_00();
			channelDescription.serialize(new String("foo"));
		} catch (IllegalArgumentException e) {
			exceptionCaught = true;
		}
		assertEquals(true, exceptionCaught);

		try { /*
			 * Tests that serializing a wrong value in EnOceanChannelDescription
			 * in an exception
			 */
			exceptionCaught = false;
			EnOceanChannelDescription channelDescription = new EnOceanChannelDescription_TMP_00();
			channelDescription.serialize(new Float(-2000.0f));
		} catch (IllegalArgumentException e) {
			exceptionCaught = true;
		}
		assertEquals(true, exceptionCaught);

		try { /*
			 * Tests that deserializing a NULL value in
			 * EnOceanChannelDescription in an exception
			 */
			exceptionCaught = false;
			EnOceanChannelDescription channelDescription = new EnOceanChannelDescription_TMP_00();
			channelDescription.deserialize(null);
		} catch (IllegalArgumentException e) {
			exceptionCaught = true;
		}
		assertEquals(true, exceptionCaught);

		try { /*
			 * Tests that deserializing a wrong object in
			 * EnOceanChannelDescription in an exception
			 */
			exceptionCaught = false;
			EnOceanChannelDescription channelDescription = new EnOceanChannelDescription_TMP_00();
			channelDescription.deserialize(new byte[] {0x45, 0x56});
		} catch (IllegalArgumentException e) {
			exceptionCaught = true;
		}
		assertEquals(true, exceptionCaught);

		try { /*
			 * Tests that getting a NULL index in an
			 * EnOceanChannelDescriptionSet results in an
			 * IllegalArgumentException
			 */
			exceptionCaught = false;
			channelDescriptionSet.getChannelDescription(null);
		} catch (IllegalArgumentException e) {
			exceptionCaught = true;
		}
		assertEquals(true, exceptionCaught);

		try { /*
			 * Tests that sending a message EnOceanChannelDescriptionSet results
			 * in an IllegalArgumentException
			 */
			exceptionCaught = false;
			channelDescriptionSet.getChannelDescription(null);
		} catch (IllegalArgumentException e) {
			exceptionCaught = true;
		}
		assertEquals(true, exceptionCaught);

	}

	/**
	 * Tests device exportation.
	 * 
	 * @throws Exception
	 */
	public void testDeviceExport() throws Exception {
		EnOceanDevice device = new TemperatureSensingDevice();
		Dictionary props = new Properties();
		props.put(EnOceanDevice.ENOCEAN_EXPORT, Boolean.TRUE);
		props.put(Constants.SERVICE_PID, Fixtures.DEVICE_PID);
		props.put(EnOceanDevice.RORG, Fixtures.STR_RORG);
		props.put(EnOceanDevice.FUNC, Fixtures.STR_FUNC);
		props.put(EnOceanDevice.TYPE, Fixtures.STR_TYPE_1);
		props.put(EnOceanDevice.MANUFACTURER, Fixtures.STR_MANUFACTURER);
		registerService(EnOceanDevice.class.getName(), device, props);

		lastServiceEvent = devices.waitForService();
		assertEquals("did not have service addition", ServiceListener.SERVICE_ADDED, lastServiceEvent);

		/*
		 * NOTE: The service should have been modified AFTER insertion,
		 * nevertheless it seems that when registration and modification happen
		 * almost in the same time, OSGi only generates a single SERVICE_ADDED
		 * event.
		 */
		log("Device service event happened : " + lastServiceEvent);
		ServiceReference ref = devices.getServiceReference();

		assertEquals("SERVICE_PID mismatch", Fixtures.DEVICE_PID, ref.getProperty(Constants.SERVICE_PID));
		assertEquals("RORG mismatch", Fixtures.STR_RORG, ref.getProperty(EnOceanDevice.RORG));
		assertEquals("FUNC mismatch", Fixtures.STR_FUNC, ref.getProperty(EnOceanDevice.FUNC));
		assertEquals("TYPE mismatch", Fixtures.STR_TYPE_1, ref.getProperty(EnOceanDevice.TYPE));
		assertEquals("MANUFACTURER mismatch", Fixtures.STR_MANUFACTURER, ref.getProperty(EnOceanDevice.MANUFACTURER));
		// TODO check that the BaseDriver correctly set some chip ID based on
		// EnOceanHosts's BASE_ID

		/*
		 * Now that we have gotten the device registered and all, we are able to
		 * try and make it send data.
		 */

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
		assertEquals("TYPE mismatch", Fixtures.STR_TYPE_1, ref.getProperty(EnOceanDevice.TYPE));
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
		assertEquals("type mismatch", Fixtures.STR_TYPE_1, event.getProperty("enocean.type"));

		EnOceanMessage msg = (EnOceanMessage) event.getProperty("enocean.message");
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
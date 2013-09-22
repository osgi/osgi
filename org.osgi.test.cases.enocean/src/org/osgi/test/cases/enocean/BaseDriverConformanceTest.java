
package org.osgi.test.cases.enocean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Hashtable;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.test.cases.enocean.esp.EspRadioPacket;
import org.osgi.test.cases.enocean.radio.Message;
import org.osgi.test.cases.enocean.radio.MessageA5_02_01;
import org.osgi.test.cases.enocean.utils.ServiceListener;
import org.osgi.test.cases.enocean.utils.Utils;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class BaseDriverConformanceTest extends DefaultTestBundleControl {

	private FileInputStream		inStream;
	private FileOutputStream	outStream;
	private ServiceListener		services;
	private String				lastEvent;

	protected void setUp() throws Exception {
		String fakeDriverPath = System.getProperty("org.osgi.service.enocean.host.path");
		File file = new File(fakeDriverPath);
		if (!file.exists()) {
			file.createNewFile();
		}
		inStream = new FileInputStream(file);
		outStream = new FileOutputStream(file);

		/* Tacks device creation */
		services = new ServiceListener(getContext(), EnOceanDevice.class);
	}

	protected void tearDown() throws Exception {
	}

	public void testMessageParsing() throws Exception {
		Message msg = new MessageA5_02_01(-22.0f);
		msg.setSenderId(0x12345678);
		EspRadioPacket pkt = new EspRadioPacket(msg);
		log("A5-02-01 RADIO msg: " + Utils.bytesToHex(msg.serialize()));
		log("A5-02-01 ESP packet: " + Utils.bytesToHex(pkt.serialize()));
	}

	public void testDeviceRegistration() throws Exception {
		/* Insert a device */
		MessageA5_02_01 teachIn = MessageA5_02_01.teachIn(Fixtures.HOST_ID, Fixtures.MANUFACTURER);
		EspRadioPacket pkt = new EspRadioPacket(teachIn);
		outStream.write(pkt.serialize());
		
		lastEvent = services.waitForEvent();
		assertEquals("First event is not a device addition", ServiceListener.SERVICE_ADDED, lastEvent);
		lastEvent = services.waitForEvent();
		assertEquals("Second event is not a device modification", ServiceListener.SERVICE_MODIFIED, lastEvent);
		
		/*
		 * Verify that the device has been registered with the correct service
		 * properties
		 */
		ServiceReference ref = services.getServiceReference();
		assertEquals("CHIP_ID mismatch", Fixtures.HOST_ID, intProp(ref, EnOceanDevice.CHIP_ID));
		assertEquals("RORG mismatch", Fixtures.RORG, intProp(ref, EnOceanDevice.RORG));
		assertEquals("FUNC mismatch", Fixtures.FUNC, intProp(ref, EnOceanDevice.FUNC));
		assertEquals("TYPE mismatch", Fixtures.TYPE, intProp(ref, EnOceanDevice.TYPE));
		assertEquals("MANUFACTURER mismatch", Fixtures.MANUFACTURER, intProp(ref, EnOceanDevice.MANUFACTURER));
	}

	public void testEventNotification() throws Exception {

		EventHandler handler = new EventHandler() {
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub

			}
		};
		String[] topics = new String[] {"org/osgi/service/enocean/EnOceanEvent/*"};
		String filter = "(uid=some_uid/*)";

		Hashtable ht = new Hashtable();
		ht.put(org.osgi.service.event.EventConstants.EVENT_TOPIC, topics);
		ht.put(org.osgi.service.event.EventConstants.EVENT_FILTER, filter);
		ServiceRegistration eventReg = getContext().registerService(EventHandler.class.getName(), handler, ht);

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
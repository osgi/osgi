
package org.osgi.test.cases.enocean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.Map;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanEvent;
import org.osgi.service.enocean.EnOceanHost;
import org.osgi.service.enocean.EnOceanMessage;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.test.cases.enocean.descriptions.EnOceanChannelDescription_TMP_00;
import org.osgi.test.cases.enocean.descriptions.EnOceanMessageDescription_A5_02_01;
import org.osgi.test.cases.enocean.messages.MessageA5_02_01;
import org.osgi.test.cases.enocean.serial.EspPacket;
import org.osgi.test.cases.enocean.serial.EspRadioPacket;
import org.osgi.test.cases.enocean.sets.EnOceanChannelDescriptionSetImpl;
import org.osgi.test.cases.enocean.sets.EnOceanMessageDescriptionSetImpl;
import org.osgi.test.cases.enocean.utils.EventListener;
import org.osgi.test.cases.enocean.utils.Fixtures;
import org.osgi.test.cases.enocean.utils.ServiceListener;
import org.osgi.test.cases.enocean.utils.Utils;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class ExportTestCase extends DefaultTestBundleControl {

	private FileInputStream		inStream;
	private FileOutputStream	outStream;
	private ServiceListener		devices;
	private ServiceListener		messageSets;
	private String				lastServiceEvent;
	private EventListener		events;
	private EventAdmin	eventAdmin;
	private EnOceanMessageDescriptionSetImpl	msgDescriptionSet;
	private EnOceanChannelDescriptionSetImpl	channelDescriptionSet;
	private ServiceReference					eventAdminRef;

	private static final String					TOPIC_CLEAN_DEVICES	= "org/osgi/impl/service/enocean/EnOceanBaseDriver/CLEAN_DEVICES";

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
		eventAdminRef = getContext().getServiceReference(EventAdmin.class.getName());
		eventAdmin = (EventAdmin) getContext().getService(eventAdminRef);

		/* Inserts some message documentation classes */
		msgDescriptionSet = new EnOceanMessageDescriptionSetImpl();
		msgDescriptionSet.putMessage(Fixtures.RORG, Fixtures.FUNC, Fixtures.TYPE_1, -1, new EnOceanMessageDescription_A5_02_01());

		channelDescriptionSet = new EnOceanChannelDescriptionSetImpl();
		channelDescriptionSet.putChannelDescription(Fixtures.TMP_CHANNEL_ID, new EnOceanChannelDescription_TMP_00());
	}

	protected void tearDown() throws Exception {
		Map properties = new Hashtable();
		Event sourceEvent = new Event(TOPIC_CLEAN_DEVICES, properties);
		eventAdmin.sendEvent(sourceEvent);
		devices.close();
		events.close();
		getContext().ungetService(eventAdminRef);
		cleanupServices();
	}

	/**
	 * Tests device exportation.
	 * 
	 * @throws Exception
	 */
	public void testDeviceExport() throws Exception {
		ServiceRegistration sReg = Fixtures.registerDevice(getContext());
	
		/* Wait for the proper and full registration */
		lastServiceEvent = devices.waitForService();
		assertEquals("did not have service addition", ServiceListener.SERVICE_ADDED, lastServiceEvent);
	
		/* Get CHIP_ID attributed by the driver from the given service PID. */
		ServiceReference hostRef = getContext().getServiceReference(EnOceanHost.class.getName());
		EnOceanHost defaultHost = (EnOceanHost) getContext().getService(hostRef);
		int dynamicChipId = defaultHost.getChipId(Fixtures.DEVICE_PID);
		assertTrue("The created CHIP_ID for an exported device was not created", dynamicChipId != -1);
		
		
		/*
		 * Now that we have gotten the device registered and all, we are able to
		 * try and make it send data, via EventAdmin broadcast.
		 */
		Map properties = new Hashtable();
		EnOceanMessage msg = new MessageA5_02_01(Fixtures.TEMPERATURE);
		properties.put(Constants.SERVICE_PID, Fixtures.DEVICE_PID);
		properties.put(EnOceanDevice.RORG, Fixtures.STR_RORG);
		properties.put(EnOceanDevice.FUNC, Fixtures.STR_FUNC);
		properties.put(EnOceanDevice.TYPE, Fixtures.STR_TYPE_1);
		properties.put(EnOceanEvent.PROPERTY_EXPORTED, "1");
		properties.put(EnOceanEvent.PROPERTY_MESSAGE, msg);
		Event evt = new Event(EnOceanEvent.TOPIC_MSG_RECEIVED, properties);
		eventAdmin.sendEvent(evt);
	
		byte[] data = new byte[256];
		int size = inStream.read(data);
		log("READ PACKET: " + Utils.bytesToHex(Utils.byteRange(data, 0, size)));
		EspPacket pkt = new EspPacket(Utils.byteRange(data, 0, size));
		assertEquals("EnOcean radio message and forged message mismatch", Utils.bytesToHex(msg.getBytes()), Utils.bytesToHex(pkt.getData().getBytes()));
	
		// Needed not to mess with further tests
		sReg.unregister();
		getContext().ungetService(hostRef);
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
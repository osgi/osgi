
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
import org.osgi.test.cases.enocean.messages.MessageF6_02_01;
import org.osgi.test.cases.enocean.serial.EspPacket;
import org.osgi.test.cases.enocean.serial.EspRadioPacket;
import org.osgi.test.cases.enocean.sets.EnOceanChannelDescriptionSetImpl;
import org.osgi.test.cases.enocean.sets.EnOceanMessageDescriptionSetImpl;
import org.osgi.test.cases.enocean.utils.EventListener;
import org.osgi.test.cases.enocean.utils.Fixtures;
import org.osgi.test.cases.enocean.utils.ServiceListener;
import org.osgi.test.cases.enocean.utils.Utils;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class ManualRegistrationTestCase extends DefaultTestBundleControl {

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
	 * Tests initial device registration from a raw Radio teach-in packet.
	 * 
	 * @throws Exception
	 */
	public void testManualDeviceRegistration() throws Exception {

		/* Insert a device */
		MessageF6_02_01 teachIn = new MessageF6_02_01(1, true, 1, true);
		teachIn.setSenderId(Fixtures.HOST_ID);
		EspRadioPacket pkt = new EspRadioPacket(teachIn);
		outStream.write(pkt.serialize());
		outStream.flush();

		lastServiceEvent = devices.waitForService();
		ServiceReference ref = devices.getServiceReference();
		
		assertEquals(ServiceListener.SERVICE_ADDED, lastServiceEvent);
		assertEquals("CHIP_ID mismatch", Fixtures.STR_HOST_ID, ref.getProperty(EnOceanDevice.CHIP_ID));
		assertEquals("RORG mismatch", Fixtures.STR_RORG_RPS, ref.getProperty(EnOceanDevice.RORG));
		assertNull(ref.getProperty(EnOceanDevice.FUNC));

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
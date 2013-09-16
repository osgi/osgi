
package org.osgi.test.cases.enocean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanException;
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

	public void testMessageParsing() throws EnOceanException, IOException {
		Message msg = new MessageA5_02_01(-22.0f);
		msg.setSenderId(0x12345678);
		EspRadioPacket pkt = new EspRadioPacket(msg);
		log("A5-02-01 RADIO msg: " + Utils.bytesToHex(msg.serialize()));
		log("A5-02-01 ESP packet: " + Utils.bytesToHex(pkt.serialize()));
	}

	public void testDeviceRegistration() throws IOException {
		MessageA5_02_01 teachIn = MessageA5_02_01.teachIn(Fixtures.HOST_ID, Fixtures.MANUFACTURER);
		EspRadioPacket pkt = new EspRadioPacket(teachIn);
		outStream.write(pkt.serialize());
		
		services.waitForRegistration();
		
		ServiceReference ref = services.getDeviceReference();
		assertEquals("CHIP_ID mismatch", Fixtures.HOST_ID, intProp(ref, EnOceanDevice.CHIP_ID));
		assertEquals("RORG mismatch", Fixtures.RORG, intProp(ref, EnOceanDevice.RORG));
		assertEquals("FUNC mismatch", Fixtures.FUNC, intProp(ref, EnOceanDevice.FUNC));
		assertEquals("TYPE mismatch", Fixtures.TYPE, intProp(ref, EnOceanDevice.TYPE));
		assertEquals("MANUFACTURER mismatch", Fixtures.MANUFACTURER, intProp(ref, EnOceanDevice.MANUFACTURER));
	}

	public void testEventNotification() throws IOException {
		// TODO: send a temperature report and check how the message is passed
		// along.
		// Do we need to have a proper device registered to pass messages from
		// the network ?
	}

	public void testMessageDescriptionUse() {
		// TODO: test how a MessageDescriptionSet, once registered, is used
		// by the BaseDriver to build complex EnOceanMessage representations.
	}

	private int intProp(ServiceReference r, String prop) {
		Integer p = (Integer) r.getProperty(prop);
		return p.intValue();
	}

}

package org.osgi.test.cases.enocean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.osgi.service.enocean.EnOceanException;
import org.osgi.test.cases.enocean.packets.radio.Message;
import org.osgi.test.cases.enocean.packets.radio.MessageA5_02_01;
import org.osgi.test.cases.enocean.packets.serial.SerialPacket_Radio;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class BaseDriverConformanceTest extends DefaultTestBundleControl {

	private FileInputStream		inStream;
	private FileOutputStream	outStream;

	protected void setUp() throws Exception {
		File file = new File("/tmp/testdriver");
		if (!file.exists()) {
			file.createNewFile();
		}
		inStream = new FileInputStream(file);
		outStream = new FileOutputStream(file);
	}

	protected void tearDown() throws Exception {
	}

	public void testMessageParsing() throws EnOceanException, IOException {
		Message msg = new MessageA5_02_01(-22.0f);
		msg.setSenderId(0x12345678);
		SerialPacket_Radio pkt = new SerialPacket_Radio(msg);
		log("A5-02-01 RADIO msg: " + Utils.bytesToHex(msg.serialize()));
		log("A5-02-01 ESP packet: " + Utils.bytesToHex(pkt.serialize()));
		Message teachIn = new MessageA5_02_01(0x6ea);
		log("A5-02-01 TEACHIN msg: " + Utils.bytesToHex(teachIn.serialize()));
	}

	public void testDeviceRegistration() throws IOException {
		// TODO: test how a teach-in message actually ends up creating a proper
		// EnOceanDevice service object, which gets registered.
		Message teachIn = new MessageA5_02_01(0x6ea);
		SerialPacket_Radio pkt = new SerialPacket_Radio(teachIn);
		log("sent packet: " + Utils.bytesToHex(pkt.serialize()));
		outStream.write(pkt.serialize());

		// Check that an EnOceanDevice has been registered, verify its
		// properties
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
}

package org.osgi.test.cases.enocean;

import org.osgi.framework.ServiceReference;
import org.osgi.service.enocean.EnOceanChannel;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanMessage;
import org.osgi.service.enocean.EnOceanRPC;
import org.osgi.service.enocean.descriptions.EnOceanChannelDescription;
import org.osgi.service.enocean.descriptions.EnOceanDataChannelDescription;
import org.osgi.service.enocean.descriptions.EnOceanMessageDescription;
import org.osgi.test.cases.enocean.descriptions.EnOceanChannelDescription_TMP_00;
import org.osgi.test.cases.enocean.descriptions.EnOceanMessageDescription_A5_02_01;
import org.osgi.test.cases.enocean.messages.MessageA5_02_01;
import org.osgi.test.cases.enocean.rpc.QueryFunction;
import org.osgi.test.cases.enocean.serial.EspRadioPacket;
import org.osgi.test.cases.enocean.utils.Fixtures;



public class BaseTests extends EnOceanTestCase {

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
	 * Tests RPC sending and receiving.
	 * 
	 * @throws Exception
	 */
	public void testRPC() throws Exception {

		/* Insert a device */
		MessageA5_02_01 teachIn = MessageA5_02_01.generateTeachInMsg(Fixtures.HOST_ID, Fixtures.MANUFACTURER);
		EspRadioPacket pkt = new EspRadioPacket(teachIn);
		outputStream.write(pkt.serialize());

		devices.waitForService();

		ServiceReference ref = devices.getServiceReference();
		EnOceanDevice device = (EnOceanDevice) getContext().getService(ref);

		EnOceanRPC rpc = new QueryFunction();
		device.invoke(rpc, null);

		getContext().ungetService(ref);
	}

}

package org.osgi.test.cases.enocean;

import org.osgi.framework.ServiceReference;
import org.osgi.service.enocean.EnOceanChannel;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanException;
import org.osgi.service.enocean.EnOceanMessage;
import org.osgi.service.enocean.EnOceanRPC;
import org.osgi.service.enocean.descriptions.EnOceanChannelDescription;
import org.osgi.service.enocean.descriptions.EnOceanDataChannelDescription;
import org.osgi.service.enocean.descriptions.EnOceanMessageDescription;
import org.osgi.service.enocean.descriptions.EnOceanMessageDescriptionSet;
import org.osgi.test.cases.enocean.descriptions.EnOceanChannelDescription_TMP_00;
import org.osgi.test.cases.enocean.messages.MessageA5_02_01;
import org.osgi.test.cases.enocean.rpc.QueryFunction;
import org.osgi.test.cases.enocean.serial.EspRadioPacket;
import org.osgi.test.cases.enocean.sets.EnOceanChannelDescriptionSetImpl;
import org.osgi.test.cases.enocean.utils.Fixtures;

/**
 * BaseTests contains the following tests:
 * 
 * - testInterfaceExceptions, tests that common errors cases are properly
 * handled (i.e. that the relevant exceptions are thrown).
 * 
 * - testRPC, tests RPC sending and receiving, i.e. insert an EnOcean
 * temperature sensor device, and test a RPC invocation on this device.
 * 
 * - testUseOfDescriptions, tests that a properly set profile ID in a raw
 * EnOceanMessage is enough to extract all the needed information, provided the
 * necessary descriptions are known.
 */
public class BaseTestCase extends AbstractEnOceanTestCase {

	/**
	 * Test that a properly set profile ID in a raw EnOceanMessage is enough to
	 * extract all the information we need, provided we have the necessary
	 * descriptions.
	 * 
	 * Pay attention that almost all of those tests look more like a
	 * "recommendation" to be ran on external description bundles, since the
	 * description objects are not supposed to be part of the RI.
	 * 
	 * @throws InterruptedException
	 */
	public void testInterfaceExceptions() throws InterruptedException {
		// Request the registration of an EnOceanMessageDescriptionSet
		// containing an EnOceanMessageDescription_A5_02_01.
		testStepService.execute("EnOceanMessageDescriptionSet_with_an_EnOceanMessageDescription_A5_02_01", null);
		log("testInterfaceExceptions(), enOceanMessageDescriptionSets.waitForService()");
		String enOceanMessageDescriptionSetsWFS = enOceanMessageDescriptionSets.waitForService();
		log("testInterfaceExceptions(), enOceanMessageDescriptionSets.waitForService() returned: " + enOceanMessageDescriptionSetsWFS);
		// testInterfaceExceptions(),
		// enOceanMessageDescriptionSets.waitForService() returned:
		// SERVICE_ADDED

		ServiceReference sr = getContext().getServiceReference(EnOceanMessageDescriptionSet.class.getName());
		log("testInterfaceExceptions(), EnOceanMessageDescriptionSet sr: " + sr);
		EnOceanMessageDescriptionSet enOceanMessageDescriptionSet = (EnOceanMessageDescriptionSet) getContext().getService(sr);
		EnOceanMessageDescription enOceanMessageDescription = enOceanMessageDescriptionSet.getMessageDescription(Fixtures.RORG, Fixtures.FUNC, Fixtures.TYPE_1, -1);
		log("testInterfaceExceptions(), enOceanMessageDescription: " + enOceanMessageDescription);

		//
		// test enOceanMessageDescription
		try {
			log("testInterfaceExceptions(), Check that passing a NULL array of bytes throws an EnOceanException.");
			enOceanMessageDescription.deserialize(null);
			fail("Passing a NULL array of bytes to EnOceanMessageDescription.deserialize must throw an EnOceanException.");
		} catch (EnOceanException e) {
			// Here, everything worked as expected.
			log("testInterfaceExceptions(), --> OK.");
		}

		try {
			log("testInterfaceExceptions(), Check that passing a wrongly sized byte array also throws an EnOceanException.");
			enOceanMessageDescription.deserialize(new byte[] {0x55, 0x02, 0x34, 0x56, 0x67});
			fail("Passing a wrongly sized byte array to EnOceanMessageDescription.deserialize must throw an EnOceanException.");
		} catch (EnOceanException e) {
			log("testInterfaceExceptions(), --> OK.");
		}

		// // Request the registration of an EnOceanChannelDescriptionSet
		// // containing an EnOceanChannelDescription_TMP_00.
		// testStepService.execute("EnOceanChannelDescriptionSet_with_an_EnOceanChannelDescription_TMP_00",
		// null);
		// log("testInterfaceExceptions(), enOceanMessageDescriptionSets.waitForService()");
		// String enOceanChannelDescriptionSetsWFS =
		// enOceanChannelDescriptionSets.waitForService();
		// log("testInterfaceExceptions(), enOceanChannelDescriptionSets.waitForService() returned: "
		// + enOceanChannelDescriptionSetsWFS);

		EnOceanChannelDescriptionSetImpl channelDescriptionSet = new EnOceanChannelDescriptionSetImpl();
		EnOceanChannelDescription_TMP_00 enOceanChannelDescription_TMP_00 = new EnOceanChannelDescription_TMP_00();
		channelDescriptionSet.putChannelDescription(Fixtures.TMP_CHANNEL_ID, enOceanChannelDescription_TMP_00);

		//
		// test channelDescriptionSet
		try { /*
			 * Tests that getting a NULL index in an
			 * EnOceanChannelDescriptionSet results in an
			 * IllegalArgumentException
			 */
			channelDescriptionSet.getChannelDescription(null);
			fail("The expected exception hasn't been caught.");
		} catch (IllegalArgumentException e) {
			log("testInterfaceExceptions(), --> OK.");
		}

		try { /*
			 * Tests that sending a message EnOceanChannelDescriptionSet results
			 * in an IllegalArgumentException
			 */
			channelDescriptionSet.getChannelDescription(null);
			fail("The expected exception hasn't been caught.");
		} catch (IllegalArgumentException e) {
			log("testInterfaceExceptions(), --> OK.");
		}

		//
		// test channelDescription
		try {
			log("testInterfaceExceptions(), Check that serializing a NULL object in EnOceanChannelDescription throws an IllegalArgumentException.");
			enOceanChannelDescription_TMP_00.serialize(null);
			fail("Passing a NULL object to EnOceanMessageDescription.serialize must throw an IllegalArgumentException.");
		} catch (IllegalArgumentException e) {
			log("testInterfaceExceptions(), --> OK.");
		}

		try { /*
			 * Tests that serializing a wrong object in
			 * EnOceanChannelDescription in an exception
			 */
			enOceanChannelDescription_TMP_00.serialize(new String("foo"));
			fail("The expected exception hasn't been caught.");
		} catch (IllegalArgumentException e) {
			log("testInterfaceExceptions(), --> OK.");
		}

		try { /*
			 * Tests that serializing a wrong value in EnOceanChannelDescription
			 * in an exception
			 */
			enOceanChannelDescription_TMP_00.serialize(new Float(-2000.0f));
			fail("The expected exception hasn't been caught.");
		} catch (IllegalArgumentException e) {
			log("testInterfaceExceptions(), --> OK.");
		}

		try { /*
			 * Tests that deserializing a NULL value in
			 * EnOceanChannelDescription in an exception
			 */
			enOceanChannelDescription_TMP_00.deserialize(null);
			fail("The expected exception hasn't been caught.");
		} catch (IllegalArgumentException e) {
			log("testInterfaceExceptions(), --> OK.");
		}

		try { /*
			 * Tests that deserializing a wrong object in
			 * EnOceanChannelDescription in an exception
			 */
			enOceanChannelDescription_TMP_00.deserialize(new byte[] {0x45, 0x56});
			fail("The expected exception hasn't been caught.");
		} catch (IllegalArgumentException e) {
			log("testInterfaceExceptions(), --> OK.");
		}
	}

	/**
	 * Tests RPC sending and receiving.
	 * 
	 * @throws InterruptedException
	 */
	public void testRPC() throws InterruptedException {
		log("testRPC(), Insert a device");
		/* Insert a device */
		MessageA5_02_01 teachIn = MessageA5_02_01.generateTeachInMsg(Fixtures.HOST_ID, Fixtures.MANUFACTURER);
		EspRadioPacket pkt = new EspRadioPacket(teachIn);
		// Push everything in the command...
		String[] params = {new String(pkt.serialize())};
		testStepService.execute("MessageA5_02_01", params);

		log("testRPC(), devices.waitForService()");
		String wfs = devices.waitForService();
		log("testRPC(), waitForService returned: " + wfs);
		// [bnd] testRPC(), waitForService returned: SERVICE_ADDED

		ServiceReference ref = devices.getServiceReference();
		log("testRPC(), ref: " + ref);
		// [bnd] testRPC(), ref:
		// {org.osgi.service.enocean.EnOceanDevice}={enocean.device.chip_id=305419896,
		// enocean.device.profile.rorg=165, DEVICE_CATEGORY=EnOcean,
		// service.id=40, service.bundleid=7, service.scope=singleton}
		EnOceanDevice device = (EnOceanDevice) getContext().getService(ref);
		log("testRPC(), device: " + device);
		// [bnd] testRPC(), device:
		// org.osgi.impl.service.enocean.basedriver.impl.EnOceanDeviceImpl@49f92de5

		EnOceanRPC rpc = new QueryFunction();
		log("testRPC(), rpc: " + rpc);
		device.invoke(rpc, null);

		log("testRPC(), unget service with service reference ref: " + ref);
		getContext().ungetService(ref);
	}

	/**
	 * Test that a properly set profile ID in a raw EnOceanMessage is enough to
	 * extract all the information needed, provided the necessary descriptions
	 * are known.
	 */
	public void testUseOfDescriptions() {
		EnOceanMessage temperatureMsg = new MessageA5_02_01(Fixtures.TEMPERATURE);

		/*
		 * Here, the message profile is A5-02-01. In a real context, the base
		 * driver would provide this information directly in the broadcasted
		 * EnOceanMessage objects through the getFunc() / getType() interface
		 * methods.
		 */
		EnOceanMessageDescription msgDescription = msgDescriptionSet.getMessageDescription(Fixtures.RORG, Fixtures.FUNC, Fixtures.TYPE_1, -1);
		assertNotNull("MsgDescription must not be null.", msgDescription);
		EnOceanChannel[] channels = msgDescription.deserialize(temperatureMsg.getPayloadBytes());
		assertEquals("2 channels are expected here.", 2, channels.length);
		EnOceanChannel temperatureChannel = channels[0];
		String tmpChannelId = temperatureChannel.getChannelId();
		assertEquals("Fixtures.TMP_CHANNEL_ID is expected here.", Fixtures.TMP_CHANNEL_ID, tmpChannelId);
		EnOceanChannelDescriptionSetImpl channelDescriptionSet = new EnOceanChannelDescriptionSetImpl();
		channelDescriptionSet.putChannelDescription(Fixtures.TMP_CHANNEL_ID, new EnOceanChannelDescription_TMP_00());
		EnOceanChannelDescription channelDescription = channelDescriptionSet.getChannelDescription(tmpChannelId);
		assertEquals("Fixtures.TMP_CHANNEL_TYPE is expected here.", Fixtures.TMP_CHANNEL_TYPE, channelDescription.getType());
		EnOceanDataChannelDescription dataChannelDescription = (EnOceanDataChannelDescription) channelDescription;
		// It's a float because it's a DATA channel
		Float deserializedTemperature = (Float) dataChannelDescription.deserialize(temperatureChannel.getRawValue());
		assertEquals("Fixtures.TEMPERATURE is expected here.", Fixtures.TEMPERATURE, deserializedTemperature.floatValue(), 0.1);
	}

}

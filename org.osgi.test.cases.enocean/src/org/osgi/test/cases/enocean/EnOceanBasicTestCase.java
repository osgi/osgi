/*
 * Copyright (c) OSGi Alliance (2014, 2017). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.test.cases.enocean;

import org.osgi.framework.ServiceReference;
import org.osgi.service.enocean.EnOceanChannel;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanRPC;
import org.osgi.service.enocean.descriptions.EnOceanChannelDescription;
import org.osgi.service.enocean.descriptions.EnOceanChannelDescriptionSet;
import org.osgi.service.enocean.descriptions.EnOceanDataChannelDescription;
import org.osgi.service.enocean.descriptions.EnOceanMessageDescription;
import org.osgi.service.enocean.descriptions.EnOceanMessageDescriptionSet;
import org.osgi.test.cases.enocean.messages.MessageExample1;
import org.osgi.test.cases.enocean.rpc.QueryFunction;
import org.osgi.test.cases.enocean.utils.Fixtures;

/**
 * EnOceanBasicTestCase contains the following tests:
 * 
 * - testInterfaceExceptions, tests that common errors cases are properly
 * handled (i.e. that the relevant exceptions are thrown).
 * 
 * - testRPC, tests RPC sending and receiving, i.e. insert an EnOcean device,
 * and test a RPC invocation on this device.
 * 
 * - testUseOfDescriptions, tests that a properly set profile ID in a raw
 * EnOceanMessage is enough to extract all the needed information, provided the
 * necessary descriptions are known.
 * 
 * @author $Id$
 */
public class EnOceanBasicTestCase extends AbstractEnOceanTestCase {

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
	// containing an EnOceanMessageDescription.
	super.testStepProxy.execute(MDSET_WITH_MD,
		"Request the registration of an EnOceanMessageDescriptionSet containing an EnOceanMessageDescription.");
	String result = enOceanMessageDescriptionSets.waitForService();
	tlog("testInterfaceExceptions(), enOceanMessageDescriptionSets.waitForService() returned: " + result);
	assertNotNull("Timeout reached.", result);
	// testInterfaceExceptions(),
	// enOceanMessageDescriptionSets.waitForService() returned:
	// SERVICE_ADDED

	ServiceReference<EnOceanMessageDescriptionSet> sr1 = getContext()
			.getServiceReference(EnOceanMessageDescriptionSet.class);
	tlog("testInterfaceExceptions(), EnOceanMessageDescriptionSet sr: " + sr1);
	EnOceanMessageDescriptionSet mdSet = getContext().getService(sr1);
	EnOceanMessageDescription msgDescription = mdSet.getMessageDescription(Fixtures.RORG,
		Fixtures.FUNC, Fixtures.TYPE_1, -1);
	tlog("testInterfaceExceptions(), enOceanMessageDescription: " + msgDescription);

	//
	// test enOceanMessageDescription
	try {
	    tlog("testInterfaceExceptions(), Check that passing a NULL array of bytes throws an IllegalArgumentException.");
	    msgDescription.deserialize(null);
	    fail("Passing a NULL array of bytes to EnOceanMessageDescription.deserialize must throw an IllegalArgumentException.");
	} catch (IllegalArgumentException e) {
	    // Here, everything worked as expected.
	    tlog("testInterfaceExceptions(), --> OK.");
	}

	try {
	    tlog("testInterfaceExceptions(), Check that passing a wrongly sized byte array also throws an IllegalArgumentException.");
	    msgDescription.deserialize(new byte[] { 0x55, 0x02, 0x34, 0x56, 0x67 });
	    fail("Passing a wrongly sized byte array to EnOceanMessageDescription.deserialize must throw an IllegalArgumentException.");
	} catch (IllegalArgumentException e) {
	    tlog("testInterfaceExceptions(), --> OK.");
	}

	// Request the registration of an EnOceanChannelDescriptionSet
	// containing an EnOceanChannelDescription_CID.
	super.testStepProxy.execute(CDSET_WITH_CD,
		"Request the registration of an EnOceanChannelDescriptionSet containing an EnOceanChannelDescription_CID.");
	result = enOceanChannelDescriptionSets.waitForService();
	tlog("testInterfaceExceptions(), enOceanChannelDescriptionSets.waitForService() returned: " + result);
	assertNotNull("Timeout reached.", result);

	ServiceReference<EnOceanChannelDescriptionSet> sr2 = getContext()
			.getServiceReference(EnOceanChannelDescriptionSet.class);
	tlog("testInterfaceExceptions(), EnOceanChannelDescriptionSet sr2: " + sr2);
	EnOceanChannelDescriptionSet cdSet = getContext().getService(sr2);
	EnOceanChannelDescription channelDescription = cdSet.getChannelDescription(Fixtures.CHANNEL_ID);
	tlog("testInterfaceExceptions(), enOceanChannelDescription: " + channelDescription);

	//
	// test channelDescriptionSet
	try { /*
	       * Tests that getting a NULL index in an EnOceanChannelDescriptionSet results in an
	       * IllegalArgumentException
	       */
	    cdSet.getChannelDescription(null);
	    fail("The expected exception hasn't been caught.");
	} catch (IllegalArgumentException e) {
	    tlog("testInterfaceExceptions(), --> OK.");
	}

	try { /*
	       * Tests that sending a message EnOceanChannelDescriptionSet
	       * results in an IllegalArgumentException
	       */
	    cdSet.getChannelDescription(null);
	    fail("The expected exception hasn't been caught.");
	} catch (IllegalArgumentException e) {
	    tlog("testInterfaceExceptions(), --> OK.");
	}

	//
	// test channelDescription
	try {
	    tlog("testInterfaceExceptions(), Check that serializing a NULL object in EnOceanChannelDescription throws an IllegalArgumentException.");
	    channelDescription.serialize(null);
	    fail("Passing a NULL object to EnOceanMessageDescription.serialize must throw an IllegalArgumentException.");
	} catch (IllegalArgumentException e) {
	    tlog("testInterfaceExceptions(), --> OK.");
	}

	try { /*
	       * Tests that serializing a wrong object in
	       * EnOceanChannelDescription in an exception
	       */
	    channelDescription.serialize(new String("foo"));
	    fail("The expected exception hasn't been caught.");
	} catch (IllegalArgumentException e) {
	    tlog("testInterfaceExceptions(), --> OK.");
	}

	try { /*
	       * Tests that serializing a wrong value in
	       * EnOceanChannelDescription in an exception
	       */
	    channelDescription.serialize(Float.valueOf(-2000.0f));
	    fail("The expected exception hasn't been caught.");
	} catch (IllegalArgumentException e) {
	    tlog("testInterfaceExceptions(), --> OK.");
	}

	try { /*
	       * Tests that deserializing a NULL value in
	       * EnOceanChannelDescription in an exception
	       */
	    channelDescription.deserialize(null);
	    fail("The expected exception hasn't been caught.");
	} catch (IllegalArgumentException e) {
	    tlog("testInterfaceExceptions(), --> OK.");
	}

	try { /*
	       * Tests that deserializing a wrong object in
	       * EnOceanChannelDescription in an exception
	       */
	    channelDescription.deserialize(new byte[] { 0x45, 0x56 });
	    fail("The expected exception hasn't been caught.");
	} catch (IllegalArgumentException e) {
	    tlog("testInterfaceExceptions(), --> OK.");
	}

	tlog("Unget service with service reference: " + sr1);
	getContext().ungetService(sr1);
	tlog("Unget service with service reference: " + sr2);
	getContext().ungetService(sr2);
    }

    /**
     * Tests RPC sending and receiving.
     * 
     * @throws InterruptedException
     */
    public void testRPC() throws InterruptedException {
	tlog("testRPC(), Ask the user to plug the EnOcean USB dongle.");
	/* Plug the EnOcean USB dongle */
	super.testStepProxy.execute(PLUG_DONGLE, "Plug the EnOcean USB dongle.");

	tlog("testRPC(), Insert a device");
	/* Insert a device */
	super.testStepProxy.execute(MSG_EXAMPLE_1A, "Insert an a5_02_01 device.");

	tlog("testRPC(), devices.waitForService()");
	String wfs = devices.waitForService();
	tlog("testRPC(), waitForService returned: " + wfs);
	// [bnd] testRPC(), waitForService returned: SERVICE_ADDED
	assertNotNull("Timeout reached.", wfs);

	ServiceReference<EnOceanDevice> sr = devices.getServiceReference();
	tlog("testRPC(), sr: " + sr);
	// [bnd] testRPC(), ref:
	// {org.osgi.service.enocean.EnOceanDevice}={enocean.device.chip_id=305419896,
	// enocean.device.profile.rorg=165, DEVICE_CATEGORY=EnOcean,
	// service.id=40, service.bundleid=7, service.scope=singleton}
	EnOceanDevice device = getContext().getService(sr);
	tlog("testRPC(), device: " + device);
	// [bnd] testRPC(), device:
	// org.osgi.impl.service.enocean.basedriver.impl.EnOceanDeviceImpl@49f92de5

	EnOceanRPC rpc = new QueryFunction();
	tlog("testRPC(), rpc: " + rpc);
	try {
	    // Sending a request is tested here, and not getting a response. So,
	    // there is not need to check the handler content.
	    device.invoke(rpc, null);
	} catch (Exception e) {
	    fail("The base driver was NOT able to send an RPC to the remote device.", e);
	}

	tlog("Unget service with service reference: " + sr);
	getContext().ungetService(sr);
    }

    /**
     * Test that a properly set profile ID in a raw EnOceanMessage is enough to
     * extract all the information needed, provided the necessary descriptions
     * are known.
     * 
     * @throws InterruptedException
     */
    public void testUseOfDescriptions() throws InterruptedException {
	/*
	 * Here, the message profile is A5-02-01. In a real context, the base
	 * driver would provide this information directly in the broadcasted
	 * EnOceanMessage objects through the getFunc() / getType() interface
	 * methods.
	 */

	/* Inserts some message documentation classes */
	// Use teststep to add an EnOceanMessageDescriptionSet
	// Request the registration of an EnOceanMessageDescriptionSet
	// containing an EnOceanMessageDescription.
	super.testStepProxy.execute(MDSET_WITH_MD,
		"Request the registration of an EnOceanMessageDescriptionSet containing an EnOceanMessageDescription.");
	String result = enOceanMessageDescriptionSets.waitForService();
	tlog("testUseOfDescriptions(), enOceanMessageDescriptionSets.waitForService() returned: " + result);
	// testUseOfDescriptions(),
	// enOceanMessageDescriptionSets.waitForService() returned:
	// SERVICE_ADDED
	assertNotNull("Timeout reached.", result);

	ServiceReference<EnOceanMessageDescriptionSet> sr1 = getContext()
			.getServiceReference(EnOceanMessageDescriptionSet.class);
	tlog("testUseOfDescriptions(), EnOceanMessageDescriptionSet sr: " + sr1);
	EnOceanMessageDescriptionSet mdSet = getContext().getService(sr1);
	EnOceanMessageDescription msgDescription = mdSet.getMessageDescription(Fixtures.RORG,
		Fixtures.FUNC, Fixtures.TYPE_1, -1);
	tlog("testUseOfDescriptions(), enOceanMessageDescription: " + msgDescription);

	assertNotNull("enOceanMessageDescription must not be null.", msgDescription);

	EnOceanChannel[] channels = msgDescription.deserialize(new MessageExample1(
		Fixtures.FLOATVALUE).getPayloadBytes());
	assertEquals("2 channels are expected here.", 2, channels.length);

	EnOceanChannel channel = channels[0];
	String channelId = channel.getChannelId();
	assertEquals("Fixtures.CHANNEL_ID is expected here.", Fixtures.CHANNEL_ID, channelId);

	// Use teststep to add an EnOceanChannelDescriptionSet
	// Request the registration of an EnOceanChannelDescriptionSet
	// containing an EnOceanChannelDescription_CID.

	super.testStepProxy.execute(CDSET_WITH_CD,
		"Request the registration of an EnOceanChannelDescriptionSet containing an EnOceanDataChannelDescription_CID.");
	tlog("testUseOfDescriptions(), enOceanChannelDescriptionSets.waitForService()");
	result = enOceanChannelDescriptionSets.waitForService();
	tlog("testUseOfDescriptions(), enOceanChannelDescriptionSets.waitForService() returned: " + result);
	assertNotNull("Timeout reached.", result);

	ServiceReference<EnOceanChannelDescriptionSet> sr2 = getContext()
			.getServiceReference(EnOceanChannelDescriptionSet.class);
	tlog("testUseOfDescriptions(), EnOceanChannelDescriptionSet sr2: " + sr2);
	EnOceanChannelDescriptionSet cdSet = getContext().getService(sr2);
	EnOceanChannelDescription channelDescription = cdSet.getChannelDescription(Fixtures.CHANNEL_ID);
	tlog("testUseOfDescriptions(), enOceanChannelDescription: " + channelDescription);

	assertEquals("Fixtures.CHANNEL_TYPE is expected here.", Fixtures.CHANNEL_TYPE,
		channelDescription.getType());

	EnOceanDataChannelDescription dataChannelDescription = (EnOceanDataChannelDescription) channelDescription;
	// It's a float because it's a DATA channel
	Float deserializedValue = (Float) dataChannelDescription.deserialize(channel.getRawValue());
	assertEquals("Fixtures.FLOATVALUE is expected here.", Fixtures.FLOATVALUE,
		deserializedValue.floatValue(), 0.1);

	tlog("Unget service with service reference: " + sr1);
	getContext().ungetService(sr1);
	tlog("Unget service with service reference: " + sr2);
	getContext().ungetService(sr2);
    }

}

/*
 * Copyright (c) OSGi Alliance (2014, 2015). All Rights Reserved.
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

import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.enocean.EnOceanEvent;
import org.osgi.service.enocean.EnOceanHost;
import org.osgi.service.enocean.EnOceanRPC;
import org.osgi.service.event.Event;
import org.osgi.test.cases.enocean.messages.MessageSYS_EX;
import org.osgi.test.cases.enocean.rpc.QueryFunction;
import org.osgi.test.cases.enocean.serial.EspPacket;
import org.osgi.test.cases.enocean.utils.Fixtures;
import org.osgi.test.cases.enocean.utils.ServiceListener;
import org.osgi.test.cases.enocean.utils.Utils;

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
public class EnOceanBroadcastRPCTestCase extends AbstractEnOceanTestCase {

    /**
     * Tests device exportation.
     * 
     * @throws Exception
     */
    public void testExportBroadcastRPC() throws Exception {

	ServiceRegistration sReg = Fixtures.registerDevice(getContext());

	/* Wait for the proper and full registration */
	String lastServiceEvent = devices.waitForService();
	assertNotNull("Timeout reached.", lastServiceEvent);
	assertEquals("did not have service addition", ServiceListener.SERVICE_ADDED,
		lastServiceEvent);

	/* Get CHIP_ID attributed by the driver from the given service PID. */
	ServiceReference hostRef = getContext().getServiceReference(EnOceanHost.class.getName());
	EnOceanHost defaultHost = (EnOceanHost) getContext().getService(hostRef);
	int dynamicChipId = defaultHost.getChipId(Fixtures.DEVICE_PID);
	assertTrue("The created CHIP_ID for an exported device was not created",
		dynamicChipId != -1);

	/*
	 * Now that we have gotten the device registered and all, we are able to
	 * try and make it send data, via EventAdmin broadcast.
	 */
	EnOceanRPC rpc = new QueryFunction();

	Map properties = new Hashtable();
	properties.put(EnOceanEvent.PROPERTY_EXPORTED, "1");
	properties.put(EnOceanEvent.PROPERTY_RPC, rpc);
	Event evt = new Event(EnOceanEvent.TOPIC_RPC_BROADCAST, properties);
	eventAdmin.sendEvent(evt);

	tlog("get any new data from testStepService.");

	String executionResult = super.testStepProxy
		.execute(
			GET_EVENT,
			"An event has been sent by the test to the base driver. Just hit [enter] to continue, and get this event.");
	assertNotNull(
		"The base driver didn't received the expected event (i.e., here, it didn't received any event at all).",
		executionResult);

	byte[] data = Utils.hex2Bytes(executionResult);
	tlog("executionResult: " + executionResult);

	try {
	    MessageSYS_EX msg = new MessageSYS_EX(new EspPacket(data).getData().getBytes());
	    tlog("msg data: " + Utils.bytesToHex(msg.getBytes()));

	    tlog("Utils.bytesToHex(rpc.getPayload()): " + Utils.bytesToHex(rpc.getPayload())
		    + ", Utils.bytesToHex(msg.getRPC().getPayload()): "
		    + Utils.bytesToHex(msg.getRPC().getPayload()));

	    assertEquals("EnOcean radio message and forged message mismatch",
		    Utils.bytesToHex(rpc.getPayload()), Utils.bytesToHex(msg.getRPC().getPayload()));
	} catch (Exception e) {
	    e.printStackTrace();
	    tlog("Error: " + e.getMessage());
	}

	// Needed not to mess with further tests
	sReg.unregister();
	tlog("Unget service with service reference: " + hostRef);
	getContext().ungetService(hostRef);
    }

}

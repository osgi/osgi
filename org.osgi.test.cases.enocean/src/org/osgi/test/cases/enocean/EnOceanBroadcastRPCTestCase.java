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

import org.osgi.service.enocean.EnOceanEvent;
import org.osgi.service.enocean.EnOceanRPC;
import org.osgi.service.event.Event;
import org.osgi.test.cases.enocean.messages.MessageSYS_EX;
import org.osgi.test.cases.enocean.rpc.QueryFunction;
import org.osgi.test.cases.enocean.serial.EspPacket;
import org.osgi.test.cases.enocean.utils.Utils;

/**
 * EnOceanBroadcastRPCTestCase contains the following tests:
 * 
 * - testExportBroadcastRPC, tests broadcast RPC exportation: creates a RPC,
 * sends it as a TOPIC_RPC_BROADCAST event, checks that the message has been
 * sent, received by the base driver, and checked through the step service, and
 * that the payload of the received event, processed as a SYS_EX message, is
 * equal to the payload of the original RPC.
 * 
 * @author $Id$
 */
public class EnOceanBroadcastRPCTestCase extends AbstractEnOceanTestCase {

    /**
     * Tests broadcast RPC exportation.
     * 
     * @throws Exception
     */
    public void testExportBroadcastRPC() throws Exception {

	/*
	 * Create a RPC, and broadcast it via EventAdmin.
	 */
	EnOceanRPC rpc = new QueryFunction();

	Map<String,Object> properties = new Hashtable<>();
	properties.put(EnOceanEvent.PROPERTY_EXPORTED, "1");
	properties.put(EnOceanEvent.PROPERTY_RPC, rpc);
	Event evt = new Event(EnOceanEvent.TOPIC_RPC_BROADCAST, properties);
	tlog("Send RPC as an event");
	eventAdmin.sendEvent(evt);

	tlog("get any new data from testStepService.");

	String executionResult = super.testStepProxy
		.execute(
			GET_EVENT,
			"An event has been sent by the test to the base driver. Just hit [enter] to continue, and get this event.");
	assertNotNull(
		"The base driver didn't receive the expected event (i.e., here, it didn't received any event at all).",
		executionResult);

	byte[] data = Utils.hex2Bytes(executionResult);
	tlog("executionResult: " + executionResult);

	MessageSYS_EX msg = new MessageSYS_EX(new EspPacket(data).getData().getBytes());
	tlog("msg SYS_EX data: " + Utils.bytesToHex(msg.getBytes()));

	tlog("Utils.bytesToHex(rpc.getPayload()): " + Utils.bytesToHex(rpc.getPayload())
		+ ", Utils.bytesToHex(msg.getRPC().getPayload()): "
		+ Utils.bytesToHex(msg.getRPC().getPayload()));

	assertEquals("EnOcean radio message and forged message mismatch",
		Utils.bytesToHex(rpc.getPayload()), Utils.bytesToHex(msg.getRPC().getPayload()));
    }

}

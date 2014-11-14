/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
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
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanEvent;
import org.osgi.service.enocean.EnOceanHost;
import org.osgi.service.enocean.EnOceanMessage;
import org.osgi.service.event.Event;
import org.osgi.test.cases.enocean.messages.MessageExample1;
import org.osgi.test.cases.enocean.serial.EspPacket;
import org.osgi.test.cases.enocean.utils.Fixtures;
import org.osgi.test.cases.enocean.utils.ServiceListener;
import org.osgi.test.cases.enocean.utils.Utils;

/**
 * ExportTestCase:
 * 
 * - testDeviceExport, tests device exportation: registers a local device as a
 * service to be exported by the base driver, checks that a chip ID has been
 * created and set as a property, sends a message on the EnOcean network, checks
 * that the message has been sent, received by the base driver, and checked by
 * the CT through the step service.
 * 
 * @author $Id$
 */
public class ExportTestCase extends AbstractEnOceanTestCase {

	/**
	 * Tests device exportation.
	 * 
	 * @throws Exception
	 */
	public void testDeviceExport() throws Exception {

		ServiceRegistration sReg = Fixtures.registerDevice(getContext());

		/* Wait for the proper and full registration */
		String lastServiceEvent = devices.waitForService();
		assertNotNull("Timeout reached.", lastServiceEvent);
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
		EnOceanMessage msg = new MessageExample1(Fixtures.FLOATVALUE);
		properties.put(Constants.SERVICE_PID, Fixtures.DEVICE_PID);
		properties.put(EnOceanDevice.RORG, Fixtures.STR_RORG);
		properties.put(EnOceanDevice.FUNC, Fixtures.STR_FUNC);
		properties.put(EnOceanDevice.TYPE, Fixtures.STR_TYPE_1);
		properties.put(EnOceanEvent.PROPERTY_EXPORTED, "1");
		properties.put(EnOceanEvent.PROPERTY_MESSAGE, msg);
		Event evt = new Event(EnOceanEvent.TOPIC_MSG_RECEIVED, properties);
		eventAdmin.sendEvent(evt);

		byte[] data = new byte[256];
		log("DEBUG: get any new data from testStepService.");
		
		String executionResult = super.testStepProxy.execute("Get_the_event_that_the_base_driver_should_have_received",
				"An event has been sent by the test to the base driver. Just hit [enter] to continue, and get this event.");
		assertNotNull("The base driver didn't received the expected event (i.e., here, it didn't received any event at all).", executionResult);
		data = executionResult.getBytes();
		int size = data.length;
		EspPacket pkt = new EspPacket(Utils.byteRange(data, 0, size));

		log("DEBUG: Utils.bytesToHex(msg.getBytes()): " + Utils.bytesToHex(msg.getBytes()) + ", Utils.bytesToHex(pkt.getData().getBytes()): " + Utils.bytesToHex(pkt.getData().getBytes()));

		assertEquals("EnOcean radio message and forged message mismatch", Utils.bytesToHex(msg.getBytes()), Utils.bytesToHex(pkt.getData().getBytes()));

		// Needed not to mess with further tests
		sReg.unregister();
		log("Unget service with service reference: " + hostRef);
		getContext().ungetService(hostRef);
	}
}

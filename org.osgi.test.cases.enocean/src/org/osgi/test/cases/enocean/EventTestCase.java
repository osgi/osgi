/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.test.cases.enocean;

import org.osgi.service.enocean.EnOceanChannel;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanEvent;
import org.osgi.service.enocean.EnOceanMessage;
import org.osgi.service.enocean.descriptions.EnOceanMessageDescription;
import org.osgi.service.event.Event;
import org.osgi.test.cases.enocean.descriptions.EnOceanMessageDescription2;
import org.osgi.test.cases.enocean.utils.Fixtures;
import org.osgi.test.cases.enocean.utils.ServiceListener;

/**
 * This class contains:
 * 
 * - testEventNotification, tests event notification when passing an actual
 * message to the Base Driver. This also tests the MessageSet registration since
 * the Base Driver needs to know about it before firing on EventAdmin.
 * 
 * @author $Id$
 */
public class EventTestCase extends AbstractEnOceanTestCase {

    /**
     * Test event notification in the context of an actual message passing to
     * the Base Driver.
     * 
     * This actually also tests the MessageSet registration since the Base
     * Driver needs to know about it before firing on EventAdmin.
     * 
     * @throws InterruptedException
     */
    public void testEventNotification() throws InterruptedException {
	/* Insert a device */
	super.testStepProxy.execute(MSG_EXAMPLE_1A, "Insert an a5_02_01 device.");

	/* First get a reference towards the device */
	String lastServiceEvent = devices.waitForService();
	assertNotNull("Timeout reached.", lastServiceEvent);
	assertEquals("did not have service addition", ServiceListener.SERVICE_ADDED, lastServiceEvent);

	/* Make the inserted device send a message */
	super.testStepProxy.execute(MSG_EXAMPLE_1B, "Make the inserted device send a message.");

	Event event = events.waitForEvent();
	assertNotNull("Timeout reached (no event has been received).", event);

	assertEquals("topic mismatch", EnOceanEvent.TOPIC_MSG_RECEIVED, event.getTopic());
	assertEquals("senderId mismatch", Fixtures.STR_HOST_ID, event.getProperty(EnOceanDevice.CHIP_ID));
	assertEquals("rorg mismatch", Fixtures.STR_RORG, event.getProperty(EnOceanDevice.RORG));
	assertEquals("func mismatch", Fixtures.STR_FUNC, event.getProperty(EnOceanDevice.FUNC));
	assertEquals("type mismatch", Fixtures.STR_TYPE_1, event.getProperty(EnOceanDevice.TYPE));

	EnOceanMessage msg = (EnOceanMessage) event.getProperty(EnOceanEvent.PROPERTY_MESSAGE);
	assertNotNull("Msg must not be null.", msg);
	EnOceanMessageDescription description = new EnOceanMessageDescription2();
	EnOceanChannel[] channels = description.deserialize(msg.getPayloadBytes());

	assertNotNull("The EnOcean message description is expected to contain channels.", channels);
	assertNotNull("The EnOcean message description is expected to contain at least one channel.", channels[0]);
	assertNotNull("The EnOcean message description is expected to contain at least one channel with a raw value.",
		channels[0].getRawValue());
	assertEquals("The raw value is expected to be 1 byte long.", 1, channels[0].getRawValue().length);
    }

}

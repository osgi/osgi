/*
 * Copyright (c) OSGi Alliance (2014, 2020). All Rights Reserved.
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
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.test.cases.enocean.utils.Fixtures;

/**
 * This class contains:
 * 
 * - testAutoDeviceRegistration, tests initial, and automatic device
 * registration from a raw Radio teach-in packet that is triggered through the
 * step service.
 * 
 * @author $Id$
 */
public class RegistrationTestCase extends AbstractEnOceanTestCase {

    /**
     * Tests initial device registration from a raw Radio teach-in packet.
     * 
     * @throws Exception
     */
    public void testAutoDeviceRegistration() throws Exception {

	/* Insert a device */
	super.testStepProxy.execute(MSG_EXAMPLE_1A, "Insert an a5_02_01 device.");

	// Device added
	String lastServiceEvent = devices.waitForService();
	tlog("Device added, lastServiceEvent: " + lastServiceEvent);
	assertNotNull("Timeout reached.", lastServiceEvent);

	// Device modified (profile)
	lastServiceEvent = devices.waitForService();
	tlog("Device modified (profile), lastServiceEvent: " + lastServiceEvent);
	assertNotNull("Timeout reached.", lastServiceEvent);

	/*
	 * NOTE: The service should have been modified AFTER insertion,
	 * nevertheless it seems that when registration and modification happen
	 * almost in the same time, OSGi only generates a single SERVICE_ADDED
	 * event.
	 */
	ServiceReference<EnOceanDevice> ref = devices.getServiceReference();

	/*
	 * Verify that the device has been registered with the correct service
	 * properties
	 */
	assertEquals("category mismatch", EnOceanDevice.DEVICE_CATEGORY,
		ref.getProperty(org.osgi.service.device.Constants.DEVICE_CATEGORY));
	assertEquals("RORG mismatch; 0xA5 is expected.", Fixtures.STR_RORG, ref.getProperty(EnOceanDevice.RORG));
	assertEquals("FUNC mismatch; 0x02 is expected.", Fixtures.STR_FUNC, ref.getProperty(EnOceanDevice.FUNC));
	assertEquals("TYPE mismatch; 0x01 is expected.", Fixtures.STR_TYPE_1, ref.getProperty(EnOceanDevice.TYPE));

	// Check the existency of the following properties, and that they are
	// String, and defined.
	Object chipId = ref.getProperty(EnOceanDevice.CHIP_ID);
	assertNotNull("The service representing the just registered device should have the EnOceanDevice.CHIP_ID: "
		+ EnOceanDevice.CHIP_ID + " property.", chipId);
	if (! (chipId instanceof String)) {
	    fail("The EnOceanDevice.CHIP_ID is expected to be a String.");
	}
	String chipIdAsAString = (String) chipId;
	if ("".equals(chipIdAsAString)) {
	    fail("The EnOceanDevice.CHIP_ID is expected to be a defined String; \"\" is not an acceptable value.");
	}

	Object manuf = ref.getProperty(EnOceanDevice.MANUFACTURER);
	assertNotNull("The service representing the just registered device should have the EnOceanDevice.MANUFACTURER: " 
		+ EnOceanDevice.MANUFACTURER + " property.", manuf);
	if (! (manuf instanceof String)) {
	    fail("The EnOceanDevice.MANUFACTURER is expected to be a String.");
	}
	String manufAsAString = (String) manuf;
	if ("".equals(manufAsAString)) {
	    fail("The EnOceanDevice.MANUFACTURER is expected to be a defined String; \"\" is not an acceptable value.");
	}
    }
    
}

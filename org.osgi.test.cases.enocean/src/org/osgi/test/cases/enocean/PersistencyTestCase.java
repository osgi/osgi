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

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanHost;
import org.osgi.test.cases.enocean.utils.Fixtures;
import org.osgi.test.support.OSGiTestCaseProperties;
import org.osgi.test.support.sleep.Sleep;

/**
 * This class contains:
 * 
 * - testDeviceExportPersistency, tests device export persistency: registers a
 * device, get its chip ID (referred to as the original chip ID), stops, and
 * restarts the base driver, finally get the device’s new chip ID, and checks
 * that the original chip ID, and the new one are equal.
 * 
 * @author $Id$
 */
public class PersistencyTestCase extends AbstractEnOceanTestCase {

    /**
     * Tests device export persistency.
     * 
     * @throws Exception
     */
    public void testDeviceExportPersistency() throws Exception {
		ServiceRegistration<EnOceanDevice> sReg = Fixtures
				.registerDevice(getContext());

	/* Get CHIP_ID attributed by the driver from the given service PID. */
		ServiceReference<EnOceanHost> hostRef = getContext()
				.getServiceReference(EnOceanHost.class);
	EnOceanHost defaultHost = getContext().getService(hostRef);
	int originalChipId = defaultHost.getChipId(Fixtures.DEVICE_PID);

	Bundle baseDriver = hostRef.getBundle();
	assertNotNull("baseDriver must not be null.", baseDriver);

	baseDriver.stop();
	Sleep.sleep(1000 * OSGiTestCaseProperties.getScaling());

	hostRef = getContext().getServiceReference(EnOceanHost.class);
	assertNull("hostRef must be null, once the base driver is stopped.", hostRef);

	baseDriver.start();
	String lastServiceEvent = enOceanHostServiceListener.waitForService();
	assertNotNull("Timeout reached.", lastServiceEvent);

	hostRef = getContext().getServiceReference(EnOceanHost.class);
	assertNotNull("One EnOceanHost service must be registered (hostRef must not be null), once the base driver is started.",
		hostRef);
	defaultHost = getContext().getService(hostRef);
	int newChipId = defaultHost.getChipId(Fixtures.DEVICE_PID);

	assertEquals("Original, and new chip ids mismatch.", originalChipId, newChipId);

	sReg.unregister();
	tlog("Unget service with service reference: " + hostRef);
	getContext().ungetService(hostRef);
    }

}

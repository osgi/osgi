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

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.enocean.EnOceanHost;

/**
 * Tests related to the EnOceanHost interface.
 * 
 * This class is intented to contain the tests related to EnOceanHost:
 * 
 * - testEnOceanHostServiceAvailability, tests that at least one EnOceanHost
 * service is registered when an implementation of the EnOcean specification is
 * running.
 * 
 * @author $Id$
 */
public class EnOceanHostTestCase extends AbstractEnOceanTestCase {

    /**
     * Test that at least one EnOceanHost service is registered when an
     * implementation of the EnOcean specification is running.
     */
    public void testEnOceanHostServiceAvailability() {
	try {
	    super.testStepProxy.execute(PLUG_DONGLE, "Plug the EnOcean USB dongle.");
		ServiceReference< ? >[] srs = getContext()
				.getAllServiceReferences(EnOceanHost.class.getName(), null);
	    assertNotNull("Test failed: no EnOceanHost service has been found in the OSGi service registry.", srs);
	    tlog("The test found " + srs.length + " EnOceanHost service(s).");
	} catch (InvalidSyntaxException e) {
	    e.printStackTrace();
	    fail("If this exception occurred, then there is a pb. in the test implementation.");
	}
    }
    
}

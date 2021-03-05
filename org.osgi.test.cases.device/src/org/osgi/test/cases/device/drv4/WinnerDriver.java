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
package org.osgi.test.cases.device.drv4;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;
import org.osgi.test.cases.device.tbc.TestBundleControl;

/**
 * This driver is intended to win every time. It is used in all the tests except
 * of the device detection test.
 * 
 * @author ProSyst
 * @version 1.0
 */
public class WinnerDriver implements Driver, BundleActivator {
	private ServiceRegistration<Driver>			driverRegistration	= null;
	private BundleContext		bc					= null;
	private TestBundleControl	master				= null;
	private ServiceReference<TestBundleControl>	masterRef			= null;
	/*
	 * !!!FIX!!! The device service this driver is attaching to The driver must
	 * hold this until it is uninstalled (or the device is uninstalled) else the
	 * device will be considered idle and will be included in any searches.
	 */
	@SuppressWarnings("unused")
	private Device				device				= null;
	private ServiceReference< ? >				deviceRef			= null;

	/**
	 * The start method of the activator of this Driver bundle. As required
	 * registers the Driver service synchronously with the bundle start.
	 */
	public void start(BundleContext bc) {
		this.bc = bc;
		/* get the master of this test case */
		masterRef = bc.getServiceReference(TestBundleControl.class);
		master = bc.getService(masterRef);
		log("starting driver bundle");
		/* register the driver service */
		Hashtable<String,Object> h = new Hashtable<>();
		h.put("DRIVER_ID", "Driver_Winner");
		h.put(org.osgi.framework.Constants.SERVICE_RANKING, Integer.valueOf(4242));
		log("registering service");
		driverRegistration = bc.registerService(
				Driver.class, this, h);
	}

	/**
	 * Unregisters the Driver service and ungets the master service
	 */
	public void stop(BundleContext bc) {
		bc.ungetService(deviceRef);
		driverRegistration.unregister();
	}

	/**
	 * Checks if this dirver matches to the Device service reference passed.
	 * This implementation retunrs 255 every time
	 * 
	 * @param reference service reference to the registred device.
	 * @return 255
	 */
	public int match(ServiceReference< ? > reference) {
		return 255;
	}

	/**
	 * Basic implementation of a driver. Attaches successfully every time
	 * 
	 * @param reference service reference to the registred device. This dirver
	 *        attachtes always successfully
	 * @returns null
	 */
	public String attach(ServiceReference< ? > reference) throws Exception {
		// FIX
		device = (Device) bc.getService(reference);
		deviceRef = reference;
		// FIX
		log("attaching to " + (String) reference.getProperty("deviceID"));
		master.setMessage(TestBundleControl.MESSAGE_OK);
		bc.ungetService(masterRef);
		return null;
	}

	public void log(String toLog) {
		//  	master.logRemark("winner driver", toLog);
	}
}

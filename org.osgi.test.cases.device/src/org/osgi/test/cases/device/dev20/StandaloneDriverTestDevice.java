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
package org.osgi.test.cases.device.dev20;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * The first registered driver will not attach to this device. The second will
 * do.
 * 
 * @author ProSyst
 * @version 1.0
 */
public class StandaloneDriverTestDevice implements BundleActivator {
	private ServiceRegistration< ? >	deviceSR	= null;
	private String[]			category	= {"test"};

	/**
	 * Will register a org.osgi.service.device.Device service
	 * 
	 * @param bc BundleContext from the fw
	 * @exception Exception maybe never
	 */
	public void start(BundleContext bc) throws Exception {
		Hashtable<String,Object> h = new Hashtable<>();
		h.put("deviceID", "standalone driver test device");
		h.put("DEVICE_CATEGORY", category);
		h.put("device.test", Boolean.TRUE);
		deviceSR = bc.registerService("java.lang.Object", this, h);
	}

	/**
	 * unregisters the device service
	 * 
	 * @param bc BundleContext from the fw
	 * @exception Exception maybe never
	 */
	public void stop(BundleContext bc) throws Exception {
		deviceSR.unregister();
	}
}

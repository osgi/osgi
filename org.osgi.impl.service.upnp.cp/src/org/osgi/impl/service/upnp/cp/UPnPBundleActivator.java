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
package org.osgi.impl.service.upnp.cp;

import org.osgi.framework.*;
import org.osgi.impl.service.upnp.cp.basedriver.UPnPBaseDriver;

public class UPnPBundleActivator implements BundleActivator {
	private UPnPControllerImpl	controller;
	private UPnPBaseDriver		basedriver;

	// This method is called when UPnP Bundle starts,so that the Bundle can
	// perform Bundle specific operations.
	@Override
	public void start(BundleContext bc) {
		try {
			controller = new UPnPControllerImpl();
			controller.start(bc);
			basedriver = new UPnPBaseDriver(controller, bc);
			basedriver.start();
			//ServiceRegistration sreg =
			// bc.registerService("org.osgi.impl.service.upnp.cp.util.UPnPController",
			// controller, null);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// This method is called when UPnPCP Bundle stops.
	@Override
	public void stop(BundleContext bc) {
		try {
			controller.stop();
			basedriver.stop();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}

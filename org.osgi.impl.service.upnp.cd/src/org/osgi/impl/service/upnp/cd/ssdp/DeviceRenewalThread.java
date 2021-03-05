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
package org.osgi.impl.service.upnp.cd.ssdp;

import java.util.Enumeration;

// This class renewals the device for each 35 minutes. It invokes NOITY after expiration
// of device. By default for every device 35 minutes is expiration time.
public class DeviceRenewalThread extends Thread {
	private SSDPComponent	ssdpcomp;
	private boolean			flag	= true;
	private DeviceExporter	exporter;

	// This constructor constructs the DeviceRenewalThread.
	DeviceRenewalThread(SSDPComponent comp) {
		ssdpcomp = comp;
		exporter = new DeviceExporter();
	}

	// This methos checks contineously check the device created time. If the
	// created time expires
	// expires it will renewal device.
	@Override
	public void run() {
		while (flag) {
			try {
				for (Enumeration<DeviceDetails> enumeration = ssdpcomp.allDeviceDetails
						.elements(); enumeration
						.hasMoreElements();) {
					DeviceDetails device = enumeration.nextElement();
					if (device.getTime() <= System.currentTimeMillis()) {
						System.out.println("send renew notify message");
						exporter.sendDeviceForNotify(device);
					}
				}
				Thread.sleep(5000); //### added so this thread does not consume
				// all time - pkr
			}
			catch (Exception e) {
				// ignored
			}
		}
	}

	// This methos kills device renewal thread.
	public void killDeviceRenewalThread() {
		flag = false;
		interrupt();
	}
}

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
package org.osgi.impl.service.upnp.cp.ssdp;

import java.util.Enumeration;

// This class checks device expiration times  
public class DeviceExpirationThread extends Thread {
	private SSDPComponent	ssdpcomp;
	private boolean			flag	= true;

	// This constructor constructs DeviceExpirationThread.
	DeviceExpirationThread(SSDPComponent comp) {
		ssdpcomp = comp;
	}

	// This method contineously checks all devices expiration times
	@Override
	public void run() {
		while (flag) {
			try {
				Enumeration<String> enumeration = ssdpcomp.devExpTimes.keys();
				while (enumeration.hasMoreElements()) {
					String uuid = enumeration.nextElement();
					Long expTime = ssdpcomp.devExpTimes.get(uuid);
					if (expTime.longValue() <= System.currentTimeMillis()) {
						ssdpcomp.removeDevice(uuid);
					}
				}
				Thread.sleep(5000);//### added so this thread does not consume
				// all time - pkr
			}
			catch (Exception e) {
				// ignored
			}
		}
	}

	// This method kills DeviceExpirationThread.
	public void killDeviceExpirationThread() {
		flag = false;
		interrupt();
	}
}

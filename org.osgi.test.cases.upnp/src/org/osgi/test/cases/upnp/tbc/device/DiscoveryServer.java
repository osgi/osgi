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
package org.osgi.test.cases.upnp.tbc.device;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

import org.osgi.test.cases.upnp.tbc.UPnPConstants;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.sleep.Sleep;

/**
 * 
 * 
 */
public class DiscoveryServer {
	private final MulticastSocket	msocket;
	private final InetAddress		address;
	private final List<DiscoveryClient>	senders;

	public DiscoveryServer() throws Exception {
		msocket = new MulticastSocket(UPnPConstants.UPnPMCPort);
		address = InetAddress.getByName(UPnPConstants.UPnPMCAddress);
		msocket.joinGroup(address);
		senders = new ArrayList<>();
	}

	public void registerSender(DiscoveryClient client) {
		DefaultTestBundleControl.log("Starting Discovery messages sender");
		synchronized (senders) {
			senders.add(client);
			Thread th = new Thread(client, "MSG Sender Client");
			th.start();
		}
	}

	public void unregisterSender(DiscoveryClient client) {
		client.stop();
		synchronized (senders) {
			senders.remove(client);
		}
	}

	public void send(DatagramPacket p) {
		if (msocket != null && p != null) {
			try {
				for (int i = 0; i < UPnPConstants.UDP_SEND_COUNT; i++) {
					msocket.send(p);
					Sleep.sleep(100);
				}
			}
			catch (IOException er) {
				// ignored
			} catch (InterruptedException e) {
				// ignored
			}
		}
	}

	public void finish() {
		try {
			msocket.leaveGroup(address);
			msocket.close();
		}
		catch (Exception er) {
			// ignored
		}
	}
}

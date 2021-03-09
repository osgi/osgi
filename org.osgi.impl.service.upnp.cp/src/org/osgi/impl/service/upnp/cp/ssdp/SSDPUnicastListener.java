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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

// This class listenes on unicast channel for M-SEARCH responses
public class SSDPUnicastListener extends Thread {
	private SSDPComponent	ssdpcomp;
	private DatagramSocket	unisock;
	private DatagramPacket	dpack;
	private boolean			flag	= true;

	SSDPUnicastListener(SSDPComponent comp) throws Exception {
		ssdpcomp = comp;
		try {
			unisock = new DatagramSocket();
		}
		catch (SocketException e) {
			System.out.println("UnicastListener Exception");
		}
	}

	// This method continously listens on unicast channel.
	@Override
	public void run() {
		while (flag) {
			byte data[] = new byte[1025];
			String recvData = null;
			if (data != null) {
				dpack = new DatagramPacket(data, data.length);
			}
			try {
				unisock.receive(dpack);
				recvData = new String(data, 0, dpack.getLength());
				SSDPParser parser = new SSDPParser(ssdpcomp);
				if (recvData != null) {
					parser.setData(recvData);
				}
				if (ssdpcomp != null) {
					(new Thread(parser)).start();
				}
			}
			catch (Exception e) {
				// ignored
			}
		}
	}

	// This method kills unicast listener.
	void killUnicastListener() {
		flag = false;
		try {
			unisock.close();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		unisock = null;
		dpack = null;
	}
}

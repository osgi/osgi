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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

// This class contineously listens on mulicast port and give the received packet to the parser. 
public class SSDPMulticastListener extends Thread {
	private SSDPComponent	ssdpcomp;
	private MulticastSocket	msock;
	private DatagramPacket	dpack;
	private boolean			flag	= true;
	private InetAddress		inet;

	// This constructor constructs the SSDPMulticastListener.
	// It creates ssdp multicast socket and joins into multicast group.
	SSDPMulticastListener(SSDPComponent comp) throws Exception {
		ssdpcomp = comp;
		try {
			msock = new MulticastSocket(SSDPConstants.HOST_PORT);
			inet = InetAddress.getByName(SSDPConstants.HOST_IP);
			msock.joinGroup(inet);
		}
		catch (IOException e) {
			throw e;
		}
	}

	// This method receives mulcast packets and gives to SSDPParser.
	@Override
	public void run() {
		while (flag) {
			String recvData = null;
			byte data[] = new byte[1025];
			if (data != null) {
				dpack = new DatagramPacket(data, data.length);
			}
			try {
				msock.receive(dpack);
			}
			catch (IOException e) {
				if ( flag )
					e.printStackTrace();
			}
			if (dpack != null) {
				recvData = new String(data, 0, dpack.getLength());
			}
			try {
				InetAddress address = dpack.getAddress();
				int port = dpack.getPort();
				SSDPParser parser = new SSDPParser(ssdpcomp);
				if (recvData != null) {
					parser.setData(recvData, address, port);
				}
				if (ssdpcomp != null) {
					(new Thread(parser)).start();
				}
			}
			catch (Exception e) {
				if ( flag )
					e.printStackTrace();
			}
		}
	}

	// This method kills the ssdp mulicast listener.
	void killMulticastListener() {
		flag = false;
		try {
			msock.leaveGroup(inet);
			msock.close();
		}
		catch (IOException e) {
			// ignored
		}
		msock = null;
		dpack = null;
	}
}

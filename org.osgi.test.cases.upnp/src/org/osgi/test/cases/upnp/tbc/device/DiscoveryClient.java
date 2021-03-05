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

import java.net.DatagramPacket;

/**
 * 
 * 
 */
public abstract class DiscoveryClient implements Runnable {
	private final DiscoveryServer	server;
	public volatile boolean			running;
	private volatile boolean		isDone;

	public DiscoveryClient(DiscoveryServer server) {
		running = true;
		isDone = false;
		this.server = server;
	}

	public abstract DatagramPacket[] getAliveDiscoveries();

	public abstract DatagramPacket[] getByeDiscoveries();

	public abstract long getTimeout();

	public synchronized void stop() {
		running = false;
		notifyAll();
	}

	public void run() {
		try {
			DatagramPacket[] packet;
			while (running) {
				packet = getAliveDiscoveries();
				int len = packet.length;
				if (getTimeout() <= 0) {
					break;
				}
				for (int i = 0; i < len; i++) {
					server.send(packet[i]);
				}
				if (getTimeout() <= 0) {
					break;
				}
				try {
					synchronized (this) {
						if (!running) {
							break;
						}
						wait(getTimeout());
					}
				}
				catch (InterruptedException er) {
					er.printStackTrace();
				}
			}
			/// server.logger.log ("Taking all bye discoveries and sending");
			packet = getByeDiscoveries();
			for (int j = 0; j < packet.length; j++) {
				server.send(packet[j]);
			}
		}
		catch (Exception er) {
			er.printStackTrace();
		}
		finally {
			isDone = true;
		}
	}

	public boolean isDone() {
		return isDone;
	}
}

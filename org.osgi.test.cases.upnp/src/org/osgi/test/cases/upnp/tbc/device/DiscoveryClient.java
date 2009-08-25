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
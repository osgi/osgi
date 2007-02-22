package org.osgi.test.cases.upnp.tbc.device;

import java.io.*;
import java.net.*;
import java.util.*;
import org.osgi.test.cases.upnp.tbc.*;
import org.osgi.test.cases.util.*;

/**
 * 
 * 
 */
public class DiscoveryServer extends Thread {
	public boolean					isDone	= false;
	public DefaultTestBundleControl	logger;
	private MulticastSocket			msocket;
	private InetAddress				address;
	private DatagramPacket			packet;
	private boolean					running;
	private Vector					senders;

	public DiscoveryServer(DefaultTestBundleControl logger) throws Exception {
		super("DiscoveryServer");
		this.logger = logger;
		msocket = new MulticastSocket(UPnPConstants.UPnPMCPort);
		address = InetAddress.getByName(UPnPConstants.UPnPMCAddress);
		msocket.joinGroup(address);
		senders = new Vector();
	}

	public void registerSender(DiscoveryClient client) {
		logger.log("Starting Discovery messages sender");
		synchronized (senders) {
			senders.addElement(client);
			Thread th = new Thread(client, "MSG Sender Client");
			//th.setName("MSG Sender Client");
			th.start();
		}
	}

	public void unregisterSender(DiscoveryClient client) {
		client.running = false;
		synchronized (this) {
			this.notifyAll();
		}
		synchronized (senders) {
			senders.removeElement(client);
		}
		isDone = true;
	}

	public void run() {
		try {
			byte[] bytes = new byte[1048];
			packet = new DatagramPacket(bytes, bytes.length);
			msocket.receive(packet);
			byte[] len = new byte[packet.getLength()];
			System.arraycopy(packet.getData(), 0, len, 0, packet.getLength());
			System.out.println("RECEIVED PACKET: " + len.toString());
		}
		catch (Exception er) {
			er.printStackTrace();
		}
	}

	public void send(DatagramPacket p) {
		if (msocket != null && p != null) {
			try {
				msocket.send(p);
			}
			catch (IOException er) {
			}
		}
	}

	public void finish() {
		try {
			msocket.leaveGroup(address);
			msocket.close();
		}
		catch (Exception er) {
		}
	}
}
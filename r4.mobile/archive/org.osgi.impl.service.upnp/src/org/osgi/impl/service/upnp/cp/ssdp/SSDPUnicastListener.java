package org.osgi.impl.service.upnp.cp.ssdp;

import java.net.*;

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

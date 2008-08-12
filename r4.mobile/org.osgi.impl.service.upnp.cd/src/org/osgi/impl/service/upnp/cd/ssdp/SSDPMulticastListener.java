package org.osgi.impl.service.upnp.cd.ssdp;

import java.io.IOException;
import java.net.*;

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
		}
		msock = null;
		dpack = null;
	}
}

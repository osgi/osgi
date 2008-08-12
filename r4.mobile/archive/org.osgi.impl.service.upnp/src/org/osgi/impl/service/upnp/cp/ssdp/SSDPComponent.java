package org.osgi.impl.service.upnp.cp.ssdp;

import java.io.IOException;
import java.net.*;
import java.util.Hashtable;
import org.osgi.impl.service.upnp.cp.util.*;

public class SSDPComponent implements SSDPConstants {
	private SSDPMulticastListener	multicastListener;
	private SSDPUnicastListener		unicastListener;
	private DeviceExpirationThread	deviceExpirationThread;
	private String					server;
	private String					notifyMessage;
	private String					notifyByeMessage;
	private StringBuffer			msearchRequest;
	private String					msearchResponse;
	private MulticastSocket			multicastsock;
	private String					uuid;
	private long					sentPacketsTime;
	private InetAddress				ssdpinet;
	private Hashtable				ssdpdevices;
	public Hashtable				devExpTimes;
	private UPnPController			controller;

	// This constructor construct the SDDPcomponent. It initializez all required
	// information for discovery layer.
	public SSDPComponent(UPnPController contrl) throws UPnPException {
		String osname = System.getProperty("os.name");
		String osver = System.getProperty("os.version");
		server = new String(osname + "/" + osver + OSVERSION);
		ssdpdevices = new Hashtable(10, 10);
		devExpTimes = new Hashtable(10, 10);
		controller = contrl;
		//Creating SSDP multicast socket
		try {
			multicastsock = new MulticastSocket(1900);
			ssdpinet = InetAddress.getByName(MADD);
			multicastsock.joinGroup(ssdpinet);
			startSSDPFunctionality();
		}
		catch (Exception e) {
			throw new UPnPException("Unable to start Multicast Socket");
		}
	}

	// This method starts the Discovery functionality by checking the stack
	// type.
	void startSSDPFunctionality() throws UPnPException {
		try {
			startMulticastListener();
			startUnicastListener();
			startDeviceExpirationThread();
			sendMsearchRequest();
		}
		catch (Exception e) {
			throw new UPnPException("Unable to start SSDP");
		}
	}

	// This method starts the multicast listener
	void startMulticastListener() throws UPnPException {
		try {
			multicastListener = new SSDPMulticastListener(this);
			multicastListener.start();
		}
		catch (Exception e) {
			throw new UPnPException("Unable to create multicast listener");
		}
	}

	// This method starts the unicast listener
	void startUnicastListener() throws UPnPException {
		try {
			unicastListener = new SSDPUnicastListener(this);
			unicastListener.start();
		}
		catch (Exception e) {
			throw new UPnPException("Unable to create unicast listener");
		}
	}

	// This method starts the device expiration thread
	void startDeviceExpirationThread() throws UPnPException {
		try {
			deviceExpirationThread = new DeviceExpirationThread(this);
			deviceExpirationThread.start();
		}
		catch (Exception e) {
			throw new UPnPException("Unable to create DeviceExpirationThread");
		}
	}

	// This method sends M-Search request on SSDP multicast channel
	public void sendMsearchRequest() {
		String msearch = makeMsearchRequest();
		byte data[] = msearch.getBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length, ssdpinet,
				1900);
		for (int i = 0; i <= 1; i++) {
			try {
				multicastsock.setTimeToLive(TTL);
				multicastsock.send(packet);
			}
			catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	// This method makes M-SEARCH request string
	public String makeMsearchRequest() {
		msearchRequest = new StringBuffer();
		msearchRequest.append(MSEARCH);
		msearchRequest.append(RN);
		msearchRequest.append(HOST);
		msearchRequest.append(MADD).append(":");
		msearchRequest.append(MPORT);
		msearchRequest.append(RN);
		msearchRequest.append(MAN).append(" ");
		msearchRequest.append(DISCOVER);
		msearchRequest.append(RN);
		msearchRequest.append(MX).append(" ");
		msearchRequest.append(MAXWAIT);
		msearchRequest.append(RN);
		msearchRequest.append(ST).append(" ");
		msearchRequest.append(STALL);
		msearchRequest.append(RN);
		msearchRequest.append(RN);
		return msearchRequest.toString();
	}

	// This method adds a new device to the controller devices list
	synchronized void addDevice(String uuid, String location, String cache) {
		if (ssdpdevices.get(uuid) == null) {
			ssdpdevices.put(uuid, location);
			controller.addDevice(uuid, location);
		}
		Long exp = new Long(cache);
		long time1 = System.currentTimeMillis() + (exp.longValue() * 1000);
		Long exp1 = new Long(time1);
		devExpTimes.put(uuid, exp1);
	}

	// This method removes a device from the controller devices list
	synchronized void removeDevice(String uuid) {
		if (uuid != null) {
			if (ssdpdevices.get(uuid) != null) {
				ssdpdevices.remove(uuid);
				controller.removeDevice(uuid);
			}
		}
	}

	// This method kills all ssdp functionalities
	public void killSSDP() {
		if (multicastListener != null) {
			multicastListener.killMulticastListener();
		}
		if (unicastListener != null) {
			unicastListener.killUnicastListener();
		}
		if (deviceExpirationThread != null) {
			deviceExpirationThread.killDeviceExpirationThread();
		}
		multicastListener = null;
		unicastListener = null;
		deviceExpirationThread = null;
	}
}

package org.osgi.impl.service.upnp.cd.ssdp;

import java.io.IOException;
import java.net.*;
import java.util.*;

// This class used for parsing the M-SEARCH requests and sending M-SEARCH responses. It 
// uses the ssdp related databse. 
public class SSDPParser implements SSDPConstants, Runnable {
	private SSDPComponent	ssdpcomp;
	private String			data;
	private DatagramSocket	datasoc;
	private String			err	= "HTTP/1.1 412 Precondition Failed";
	private byte[]			errstring;
	private InetAddress		address;
	private int				port;

	// This constructor constructs the SSDPParser
	SSDPParser(SSDPComponent comp) {
		ssdpcomp = comp;
		errstring = err.getBytes();
	}

	// This method sets data receives through multicast channel or unicast
	// channel.
	void setData(String recvData, InetAddress ip, int recvPort) {
		data = recvData;
		address = ip;
		port = recvPort;
		try {
			datasoc = new DatagramSocket();
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	// This method invoked by thread pool
	public void run() {
		if (data != null) {
			parseData();
		}
	}

	// This method parses the msearch request data.
	void parseData() {
		StringTokenizer stn = new StringTokenizer(data, new String("\r\n"));
		StringTokenizer stn1 = new StringTokenizer(data, new String("\r\n"));
		int count = stn1.countTokens();
		String tokensArray[] = new String[count];
		for (; count > 0; count--) {
			tokensArray[count - 1] = stn1.nextToken();
		}
		String header = stn.nextToken().toUpperCase();
		if (header.startsWith("M-SEARCH")) {
			try {
				if (header.equals(MSEARCH)) {
					String host = getHeaderValue("HOST:", tokensArray);
					if (host.equals(HOST_IP + ":" + HOST_PORT)) {
						parseMsearchRequest(tokensArray);
					}
					else {
						DatagramPacket packet = new DatagramPacket(errstring,
								errstring.length, address, port);
						for (int l = 0; l <= 1; l++) {
							datasoc.send(packet);
						}
					}
				}
				else {
					DatagramPacket packet = new DatagramPacket(errstring,
							errstring.length, address, port);
					for (int l = 0; l <= 1; l++) {
						datasoc.send(packet);
					}
				}
			}
			catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
		else
			if ((data.indexOf("412") != -1) || (data.indexOf("ERROR") != -1)) {
				System.out.println("error" + data);
			}
	}

	// This method validates the headers of M-SEARCH request. And send the
	// M-SEARC response.
	void parseMsearchRequest(String[] tokensArray) {
		String man = getHeaderValue(MAN, tokensArray);
		String maxWait = getHeaderValue(MX, tokensArray);
		String st = getHeaderValue(ST, tokensArray);
		try {
			if ((man != null) && (man.equals(DISCOVER)) && (maxWait != null)
					&& (st != null)) {
				if (st.equals("ssdp:all")) {
					sendReplyForSsdpAll(maxWait);
				}
				else
					if (st.equals("upnp:rootdevice")) {
						sendReplyForRoot(maxWait);
					}
					else
						if (st.startsWith("uuid")) {
							sendReplyForDevice(st, maxWait);
						}
						else
							if (st.indexOf(":device:") != -1) {
								sendReplyForDeviceType(st, maxWait);
							}
							else
								if (st.indexOf(":service:") != -1) {
									sendReplyForServiceType(st, maxWait);
								}
								else {
									DatagramPacket packet = new DatagramPacket(
											errstring, errstring.length,
											address, port);
									for (int l = 0; l <= 1; l++) {
										datasoc.send(packet);
									}
								}
			}
			else {
				DatagramPacket packet = new DatagramPacket(errstring,
						errstring.length, address, port);
				for (int l = 0; l <= 1; l++) {
					datasoc.send(packet);
				}
			}
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	// This method reads all devices details and calls ssdpAll method by passing
	// device ,embedded
	// device or service information.It is for M-SEARCH request with ssdp:all.
	void sendReplyForSsdpAll(String maxWait) {
		for (Enumeration enumeration = ssdpcomp.allDeviceDetails.elements(); enumeration
				.hasMoreElements();) {
			DeviceDetails devDet = (DeviceDetails) enumeration.nextElement();
			if (devDet.isRoot()) {
				String[] response = new String[3];
				ssdpAll(devDet, maxWait, response, null);
			}
			else {
				String[] response = new String[2];
				ssdpAll(devDet, maxWait, response, null);
			}
			Hashtable services = devDet.getServices();
			for (Enumeration e = services.elements(); e.hasMoreElements();) {
				String type = (String) e.nextElement();
				String response[] = new String[1];
				ssdpAll(devDet, maxWait, response, type);
			}
		}
	}

	// This method make M-SEARCH responses for device ,embedded device or
	// service.It is for
	// M-SEARCH request with ssdp:all.
	void ssdpAll(DeviceDetails devDet, String maxWait, String[] response,
			String type) {
		Date dt = new Date(System.currentTimeMillis());
		String usn = null;
		String location = devDet.getLocation();
		String uuid = devDet.getUUID();
		if (response.length == 3) {
			usn = new String(uuid + "::" + ROOTDEVICE);
			response[0] = ssdpcomp.makeMsearchResponse(dt.toString(),
					ROOTDEVICE, usn, location);
			response[1] = ssdpcomp.makeMsearchResponse(dt.toString(), uuid,
					uuid, location);
			usn = new String(uuid + "::" + devDet.getDevType());
			response[2] = ssdpcomp.makeMsearchResponse(dt.toString(), devDet
					.getDevType(), usn, location);
		}
		else
			if (response.length == 2) {
				response[0] = ssdpcomp.makeMsearchResponse(dt.toString(), uuid,
						uuid, location);
				usn = new String(uuid + "::" + devDet.getDevType());
				response[1] = ssdpcomp.makeMsearchResponse(dt.toString(),
						devDet.getDevType(), usn, location);
			}
			else
				if (response.length == 1) {
					usn = new String(uuid + "::" + type);
					response[0] = ssdpcomp.makeMsearchResponse(dt.toString(),
							type, usn, location);
				}
		sendResponse(response, maxWait);
	}

	// This method make M-SEARCH responses for root device .It is for
	// M-SEARCH request with upnp:rootdevice.
	void sendReplyForRoot(String maxWait) {
		Date dt = new Date(System.currentTimeMillis());
		String usn = null;
		String[] response = new String[1];
		for (Enumeration enumeration = ssdpcomp.allDeviceDetails.elements(); enumeration
				.hasMoreElements();) {
			DeviceDetails devDet = (DeviceDetails) enumeration.nextElement();
			if (devDet.isRoot()) {
				usn = new String(devDet.getUUID() + "::" + ROOTDEVICE);
				response[0] = ssdpcomp.makeMsearchResponse(dt.toString(),
						ROOTDEVICE, usn, devDet.getLocation());
				sendResponse(response, maxWait);
			}
		}
	}

	// This method make M-SEARCH responses for perticuler device.
	void sendReplyForDevice(String uuid, String maxWait) {
		Date dt = new Date(System.currentTimeMillis());
		String[] response = new String[1];
		if (ssdpcomp.allDeviceDetails.get(uuid) != null) {
			DeviceDetails devDet = (DeviceDetails) ssdpcomp.allDeviceDetails
					.get(uuid);
			response[0] = ssdpcomp.makeMsearchResponse(dt.toString(), uuid,
					uuid, devDet.getLocation());
			sendResponse(response, maxWait);
		}
		else {
			response[0] = new String("ERROR: DEVICE WITH THIS ID  " + uuid
					+ "  NOT FOUND");
			sendResponse(response, maxWait);
		}
	}

	// This method make M-SEARCH responses for perticuler device type and
	// version.
	void sendReplyForDeviceType(String st, String maxWait) {
		Date dt = new Date(System.currentTimeMillis());
		String usn = null;
		String[] response = new String[1];
		for (Enumeration enumeration = ssdpcomp.allDeviceDetails.elements(); enumeration
				.hasMoreElements();) {
			DeviceDetails devDet = (DeviceDetails) enumeration.nextElement();
			if (devDet.isDevAvailable(st)) {
				usn = new String(devDet.getUUID() + "::" + st);
				response[0] = ssdpcomp.makeMsearchResponse(dt.toString(), st,
						usn, devDet.getLocation());
				sendResponse(response, maxWait);
			}
			else {
				response[0] = new String(
						"ERROR: DEVICE WITH THIS TYPE AND VERSION " + st
								+ "  NOT FOUND");
				sendResponse(response, maxWait);
			}
		}
	}

	// This method make M-SEARCH responses for perticuler service type and
	// version.
	void sendReplyForServiceType(String st, String maxWait) {
		Date dt = new Date(System.currentTimeMillis());
		String usn = null;
		String[] response = new String[1];
		for (Enumeration enumeration = ssdpcomp.allDeviceDetails.elements(); enumeration
				.hasMoreElements();) {
			DeviceDetails devDet = (DeviceDetails) enumeration.nextElement();
			if (devDet.isServiceAvailable(st)) {
				usn = new String(devDet.getUUID() + "::" + st);
				response[0] = ssdpcomp.makeMsearchResponse(dt.toString(), st,
						usn, devDet.getLocation());
				sendResponse(response, maxWait);
			}
			else {
				response[0] = new String(
						"ERROR: SERVICE WITH THIS TYPE AND VERSION " + st
								+ "  NOT FOUND");
				sendResponse(response, maxWait);
			}
		}
	}

	// This method cretes datagram socket with M-SEARCH request sender address
	// and port then sends responses.
	void sendResponse(String[] response, String maxWait) {
		waitRandomTime(maxWait);
		for (int l = 0; l < response.length; l++) {
			byte[] data1 = response[l].getBytes();
			DatagramPacket packet = new DatagramPacket(data1, data1.length,
					address, port);
			for (int n = 0; n <= 1; n++) {
				try {
					DatagramSocket datasoc1 = new DatagramSocket();
					datasoc1.send(packet);
					datasoc1.close();
				}
				catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}

	// This method provides waiting a random time between random number.and
	// maxwait.
	void waitRandomTime(String maxWait) {
		long resTime = 0;
		try {
			Integer in = new Integer(maxWait);
			double rand = Math.random();
			rand = rand * 1000;
			String rands = Double.toString(rand);
			rands = rands.substring(0, rands.indexOf("."));
			Long lval = new Long(rands);
			resTime = (long) (lval.longValue() * in.intValue());
		}
		catch (Exception e) {
			Long wt = new Long(maxWait);
			resTime = wt.longValue();
		}
		try {
			Thread.sleep(resTime);
		}
		catch (InterruptedException e) {
		}
	}

	// This method returns the required header value from received data
	String getHeaderValue(String header, String[] tokensArray) {
		int j = 0;
		String s = null;
		String s1 = null;
		String s2 = null;
		String s3 = null;
		for (int i = 0; i < tokensArray.length; i++) {
			s = header.toUpperCase();
			s1 = tokensArray[i].toUpperCase();
			if (s1.indexOf(s) != -1) {
				if (header.equals(EXT)) {
					return tokensArray[i].trim();
				}
				if (tokensArray[i].charAt(s.length()) == ' ') {
					j = 1;
				}
				if (header.equals(ST)) {
					if (tokensArray[i].startsWith("ST")) {
						s2 = tokensArray[i].substring(header.length() + j);
						return s2.trim();
					}
				}
				else {
					s2 = tokensArray[i].substring(header.length() + j);
					return s2.trim();
				}
			}
		}
		return null;
	}
}

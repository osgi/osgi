package org.osgi.impl.service.upnp.cp.ssdp;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.StringTokenizer;

// This class parses the NOTIFY messages and M-SEARCH responses. 
public class SSDPParser implements SSDPConstants, Runnable {
	private SSDPComponent	ssdpcomp;
	private String			data;
	private DatagramSocket	datasoc;

	// This constructor constructs the SSDPParser
	SSDPParser(SSDPComponent comp) {
		ssdpcomp = comp;
	}

	// This method sets data receives through multicast channel or unicast
	// channel.
	void setData(String recvData) {
		data = recvData;
		try {
			datasoc = new DatagramSocket();
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	// This method invoke by thread pool
	public void run() {
		if (data != null) {
			parseData(data);
		}
	}

	// This method parses the data.
	synchronized void parseData(String data) {
		StringTokenizer stn = new StringTokenizer(data, RN);
		StringTokenizer stn1 = new StringTokenizer(data, RN);
		int count = stn1.countTokens();
		String tokensArray[] = new String[count];
		for (; count > 0; count--) {
			tokensArray[count - 1] = stn1.nextToken();
		}
		String header = stn.nextToken().toUpperCase();
		if (header.equals(NOTIFY)) {
			String host = getHeaderValue(HOST.trim(), tokensArray);
			if (host.equals(MADD + ":" + MPORT))
				parseNotify(data, tokensArray);
		}
		else
			if (header.equals(MSEARCH_RESP))
				parseMsearchResponse(data, tokensArray);
	}

	// This method parses notify messages
	void parseNotify(String data, String[] tokensArray) {
		String ch = null;
		String loc = null;
		String nt = null;
		String nts = null;
		String ser = null;
		String usn = null;
		nts = getHeaderValue(NTS, tokensArray);
		if ((nts != null) && (nts.equals(ALIVE))) {
			ch = getHeaderValue(CACHE, tokensArray);
			if (ch.indexOf(MAXAGE) != -1) {
				ch = ch.substring(ch.indexOf("=") + 1);
				ch = ch.trim();
			}
			else {
				ch = null;
			}
			loc = getHeaderValue(LOC, tokensArray);
			nt = getHeaderValue(NT, tokensArray);
			ser = getHeaderValue(SERVER, tokensArray);
			usn = getHeaderValue(USN, tokensArray);
			if ((ch != null) && (loc != null) && (nt != null) && (ser != null)
					&& (usn != null)) {
				addNewDevice(getUUID(usn), loc, ch);
			}
			else {
				System.out.println("Invalid HEADER in NOTIFY alive");
			}
		}
		else
			if ((nts != null) && (nts.equals(BYEBYE))) {
				usn = getHeaderValue(USN, tokensArray);
				if (usn != null) {
					if (usn.indexOf("::") != -1) {
						usn = usn.substring(0, usn.indexOf("::"));
					}
					ssdpcomp.removeDevice(getUUID(usn));
				}
			}
			else {
				System.out.println("Invalid NOTIFY message");
			}
	}

	// This method adds device which is detected through notify
	void addNewDevice(String uuid, String location, String cache) {
		if ((uuid != null) && (location != null) && (cache != null)) {
			ssdpcomp.addDevice(uuid, location, cache);
		}
	}

	// This method returns the device uuid by extracting from device USN message
	String getUUID(String usn) {
		String uuid = null;
		if (usn.startsWith(UUID)) {
			int i = usn.indexOf("::");
			i = (i != -1) ? i : usn.length();
			uuid = usn.substring(0, i);
		}
		return uuid;
	}

	// This method parses M-SEARCH responses messages
	void parseMsearchResponse(String data, String[] tokensArray) {
		String ch = getHeaderValue(CACHE, tokensArray);
		if (ch.indexOf(MAXAGE) != -1) {
			ch = ch.substring(ch.indexOf("=") + 1);
			ch = ch.trim();
		}
		else {
			ch = null;
		}
		String date1 = getHeaderValue(DATE1, tokensArray);
		String ext = getHeaderValue(EXT, tokensArray);
		String loc = getHeaderValue(LOC, tokensArray);
		String ser = getHeaderValue(SERVER, tokensArray);
		String st = getHeaderValue(ST, tokensArray);
		String usn = getHeaderValue(USN, tokensArray);
		if ((ch != null) && (ext != null) && (loc != null) && (ser != null)
				&& (st != null) && (usn != null)) {
			addNewDevice(getUUID(usn), loc, ch);
		}
		else
			if (data.indexOf("412") != -1) {
				System.out.println("ERROR MESSAGE " + data);
			}
			else {
				System.out.println("Inavalid MSEARCH Response message");
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
					return tokensArray[i];
				}
				if (tokensArray[i].charAt(s.length()) == ' ') {
					j = 1;
				}
				s2 = tokensArray[i].substring(header.length() + j);
				return s2;
			}
		}
		return null;
	}
}

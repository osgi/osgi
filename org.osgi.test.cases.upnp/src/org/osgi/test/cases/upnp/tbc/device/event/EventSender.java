package org.osgi.test.cases.upnp.tbc.device.event;

import java.io.*;
import java.net.*;
import java.util.*;
import org.osgi.test.cases.upnp.tbc.*;
import org.osgi.test.cases.upnp.tbc.device.description.Hex;
import org.osgi.test.cases.util.*;

/**
 * 
 * 
 */
public class EventSender implements Runnable {
	private String						sid;
	private URL							loc;
	private int							next;
	private DefaultTestBundleControl	logger;

	public EventSender(String sid, URL loc, DefaultTestBundleControl logger) {
		this.sid = sid;
		this.loc = loc;
		this.logger = logger;
	}

	public void run() {
		Hashtable hash = new Hashtable();
		hash.put(UPnPConstants.N_INT, UPnPConstants.V_INT);
		hash.put(UPnPConstants.N_UI4, UPnPConstants.V_UI4);
		Double duble = new Double(UPnPConstants.V_NUMBER);
		hash.put(UPnPConstants.N_NUMBER, duble);
		Float fl = new Float(UPnPConstants.V_FLOAT);
		hash.put(UPnPConstants.N_FLOAT, fl);
		hash.put(UPnPConstants.N_CHAR, UPnPConstants.V_CHAR);
		hash.put(UPnPConstants.N_STRING, UPnPConstants.V_STRING);
		hash.put(UPnPConstants.N_BOOLEAN, UPnPConstants.V_BOOLEAN);
		hash.put(UPnPConstants.N_HEX, Hex
				.encode(UPnPConstants.V_HEX.getBytes()));
		hash.put(UPnPConstants.N_OUT, UPnPConstants.V_OUT);
		logger.log("Sending the initial event");
		try {
			genEvent(hash, 0);
		}
		catch (IOException exc) {
			exc.printStackTrace();
		}
		hash.clear();
		logger.log("Sending an event with name " + UPnPConstants.N_CHAR
				+ " and value " + UPnPConstants.V_CHAR);
		if (sid.indexOf("e") >= 0) {
			hash.put(UPnPConstants.N_INT, UPnPConstants.V_INT);
			logger.log("Sending an event with name " + UPnPConstants.N_INT
					+ " and value " + UPnPConstants.V_INT);
			try {
				genEvent(hash, 0);
			}
			catch (IOException exc) {
				exc.printStackTrace();
			}
			hash.put(UPnPConstants.N_UI4, UPnPConstants.V_UI4);
			logger.log("Sending an event with name " + UPnPConstants.N_UI4
					+ " and value " + UPnPConstants.V_UI4);
			try {
				genEvent(hash, 0);
			}
			catch (IOException exc) {
				exc.printStackTrace();
			}
			duble = new Double(UPnPConstants.V_NUMBER);
			hash.put(UPnPConstants.N_NUMBER, duble);
			logger.log("Sending an event with name " + UPnPConstants.N_NUMBER
					+ " and value " + UPnPConstants.V_NUMBER);
			try {
				genEvent(hash, 0);
			}
			catch (IOException exc) {
				exc.printStackTrace();
			}
			fl = new Float(UPnPConstants.V_FLOAT);
			hash.put(UPnPConstants.N_FLOAT, fl);
			logger.log("Sending an event with name " + UPnPConstants.N_FLOAT
					+ " and value " + UPnPConstants.V_FLOAT);
			try {
				genEvent(hash, 0);
			}
			catch (IOException exc) {
				exc.printStackTrace();
			}
			hash.put(UPnPConstants.N_CHAR, UPnPConstants.V_CHAR);
			logger.log("Sending an event with name " + UPnPConstants.N_CHAR
					+ " and value " + UPnPConstants.V_CHAR);
			try {
				genEvent(hash, 0);
			}
			catch (IOException exc) {
				exc.printStackTrace();
			}
			hash.put(UPnPConstants.N_STRING, UPnPConstants.V_STRING);
			logger.log("Sending an event with name " + UPnPConstants.N_STRING
					+ " and value " + UPnPConstants.V_STRING);
			try {
				genEvent(hash, 0);
			}
			catch (IOException exc) {
				exc.printStackTrace();
			}
			hash.put(UPnPConstants.N_BOOLEAN, UPnPConstants.V_BOOLEAN);
			logger.log("Sending an event with name " + UPnPConstants.N_BOOLEAN
					+ " and value " + UPnPConstants.V_BOOLEAN);
			try {
				genEvent(hash, 0);
			}
			catch (IOException exc) {
				exc.printStackTrace();
			}
			try {
				hash.put(UPnPConstants.N_HEX, Hex.encode(UPnPConstants.V_HEX
						.getBytes()));
				logger.log("Sending an event with name " + UPnPConstants.N_HEX
						+ " and value " + UPnPConstants.V_HEX);
			}
			catch (Exception exc) {
				hash.put(UPnPConstants.N_HEX, UPnPConstants.V_HEX);
			}
			try {
				genEvent(hash, 0);
			}
			catch (IOException exc) {
				exc.printStackTrace();
			}
			hash.put(UPnPConstants.N_OUT, UPnPConstants.V_OUT);
			try {
				genEvent(hash, 0);
			}
			catch (IOException exc) {
				exc.printStackTrace();
			}
		}
		else {
			hash.put(UPnPConstants.N_OUT_STR, UPnPConstants.V_OUT_STR);
			try {
				genEvent(hash, 0);
			}
			catch (IOException exc) {
				exc.printStackTrace();
			}
		}
	}

	private void genEvent(Dictionary dict, int err) throws IOException {
		StringBuffer sb = new StringBuffer();
		sb.append(UPnPConstants.PROPSET_ST);
		sb.append(UPnPConstants.CRLF);
		Enumeration enumeration = dict.keys();
		while (enumeration.hasMoreElements()) {
			String key = (String) enumeration.nextElement();
			sb.append(UPnPConstants.PROP_ST);
			sb.append(UPnPConstants.CRLF);
			sb.append(UPnPConstants.LB);
			sb.append(key);
			sb.append(UPnPConstants.RB);
			sb.append(dict.get(key));
			sb.append(UPnPConstants.LCB);
			sb.append(key);
			sb.append(UPnPConstants.RB);
			sb.append(UPnPConstants.CRLF);
			sb.append(UPnPConstants.PROP_END);
			sb.append(UPnPConstants.CRLF);
		}
		sb.append(UPnPConstants.PROPSET_END);
		String body = sb.toString();
		sb.setLength(0);
		sb.append(UPnPConstants.NOTIFY);
		sb.append(" " + loc.getFile() + " ");
		sb.append(UPnPConstants.HVER);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.H_HOST);
		sb.append(loc.getHost());
		sb.append(UPnPConstants.DD);
		sb.append(loc.getPort() == -1 ? 80 : loc.getPort());
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.H_CT);
		sb.append(UPnPConstants.V_CT);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.H_CL);
		sb.append(body.getBytes().length);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.H_NT);
		sb.append(UPnPConstants.V_NT);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.H_NTS);
		sb.append(UPnPConstants.V_NTS_E);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.H_SID);
		sb.append(sid);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.H_SEQ);
		sb.append(next++);
		sb.append(UPnPConstants.CRLF);
		sb.append(UPnPConstants.CRLF);
		String head = sb.toString();
		System.out.println(head);
		System.out.println(body);
		//    System.out.println("CLIENT: Sending event for sid: " + sid);
		Socket sock = new Socket(loc.getHost(), loc.getPort() == -1 ? 80 : loc
				.getPort());
		BufferedReader br = new BufferedReader(new InputStreamReader(sock
				.getInputStream()));
		PrintStream ps = new PrintStream(sock.getOutputStream());
		ps.print(head);
		ps.write(body.getBytes());
		String ret = br.readLine();
		//    System.out.println("CLIENT: RE: " + ret);
		ps.close();
		br.close();
		sock.close();
	}
}
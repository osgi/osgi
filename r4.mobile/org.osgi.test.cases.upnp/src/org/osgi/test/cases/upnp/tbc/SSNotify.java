package org.osgi.test.cases.upnp.tbc;

import java.io.*;
import java.net.*;
import java.util.*;
import org.osgi.test.cases.upnp.tbc.parser.*;
import org.osgi.test.cases.util.*;

/**
 * 
 * 
 */
public class SSNotify extends Thread {
	public boolean						isFinished	= false;
	private ServerSocket				ssocket;
	private Socket						sock;
	private InputStream					is;
	private DefaultTestBundleControl	control;

	public SSNotify(int port, DefaultTestBundleControl control)
			throws Exception {
		super("SSNotify");
		this.control = control;
		ssocket = new ServerSocket(port);
	}

	public void run() {
		try {
			sock = ssocket.accept();
			sock.setSoTimeout(5000);
			is = sock.getInputStream();
			control.log("Waiting for events");
			Hashtable ht = readHeaders(is, 30);
			String len = (String) ht.get("CONTENT-LENGTH");
			long length = Long.MAX_VALUE;
			if (len != null) {
				try {
					length = Long.parseLong(len);
				}
				catch (Exception e) {
				}
			}
			byte[] doc = getDocBytes(is, length, 30);
			String ans = new String(doc);
			if (checkEvents(parseNotify(ans))) {
				control.log("All values of event are OK.Event Test is OK");
				isFinished = true;
			}
			else {
				control.log("The events are not ok!!!");
			}
		}
		catch (Exception er) {
			control.log("Unxepected Exception: " + er.getMessage());
			er.printStackTrace();
		}
	}

	public static byte[] getDocBytes(InputStream in, long length, int timeout)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte buff[] = new byte[2048];
		int count = 0;
		if (length < buff.length) {
			count = in.read(buff, 0, (int) length);
		}
		else {
			count = in.read(buff);
		}
		int timer = 0;
		while (count == -1 && timer < timeout) {
			try {
				if (length < buff.length) {
					count = in.read(buff, 0, (int) length);
				}
				else {
					count = in.read(buff);
				}
			}
			catch (Exception e) {
				timer++;
				if (timer > timeout) {
					throw new InterruptedIOException("Operation time out!!!");
				}
				continue;
			}
			if (count == -1) {
				throw new InterruptedIOException("Operation time out!!!");
			}
		}
		if (timer >= timeout) {
			byte[] result = baos.toByteArray();
			if (result != null && result.length > 0) {
				return result;
			}
			else {
				throw new InterruptedIOException("Operation time out!!!");
			}
		}
		if (count >= length) {
			baos.write(buff, 0, (int) length);
			baos.flush();
			return baos.toByteArray();
		}
		long all = count;
		int rTimeOut = 0;
		while (rTimeOut < timeout && -1 < count && all <= length) {
			if (all == length) {
				baos.write(buff, 0, count);
				break;
			}
			baos.write(buff, 0, count);
			try {
				count = in.read(buff);
			}
			catch (InterruptedIOException iio) {
				rTimeOut++;
			}
			all += count;
			baos.flush();
		}
		baos.flush();
		return baos.toByteArray();
	}

	private Hashtable readHeaders(InputStream is, int maxTime)
			throws IOException {
		String tmp = null;
		Hashtable headers = null;
		boolean pos = true;
		boolean run = true;
		int timeout = 0;
		while (run) {
			if (tmp == null) {
				try {
					tmp = new String(readln(is));
				}
				catch (Exception e) {
					run = !(++timeout > maxTime);
					if (!run) {
						throw new InterruptedIOException("Operation timeout");
					}
					continue;
				}
			}
			if (pos) {
				pos = false;
			}
			while ((!"\r\n".equals(tmp)) && (!"\n\r".equals(tmp))
					&& (!"\n".equals(tmp)) && (!"\r".equals(tmp))
					&& (!"".equals(tmp))) {
				if (pos) {
					//// System.out.println("POS");
					pos = false;
				}
				else {
					int xpos = tmp.indexOf(":");
					String header;
					String value;
					if (xpos > -1) {
						header = tmp.substring(0, xpos).toUpperCase().trim();
						value = "";
						try {
							value = tmp.substring(xpos + 1).trim();
						}
						catch (RuntimeException rte) {
						}
					}
					else {
						header = tmp;
						value = tmp;
					}
					//// System.out.println("header: " + header);
					//// System.out.println("value: " + value);
					if (headers == null) {
						headers = new Hashtable(6);
					}
					headers.put(header, value);
				}
				try {
					tmp = new String(readln(is));
				}
				catch (Exception e) {
					tmp = null;
				}
			}
			if (tmp != null && (tmp.equals("\r\n")) || (!tmp.equals("\n\r"))
					|| (!tmp.equals("\n")) || (!tmp.equals("\r"))) {
				run = false;
			}
		}
		return headers;
	}

	private int	last	= -1;

	private byte[] readln(InputStream is) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		boolean first = false;
		boolean second = false;
		int in;
		if (last != -1) {
			in = last;
		}
		else {
			in = is.read();
		}
		while ((!first && !second) && (in > -1)) {
			if (first && in != 13 && in != 10) {
				last = in;
				return bos.toByteArray();
			}
			first = first ? first : in == 13 || in == 10;
			second = first ? in == 13 || in == 10 : false;
			if (!first && !second) {
				bos.write(in);
			}
			in = is.read();
		}
		last = -1;
		//// System.out.println("line = " + new String(bos.toByteArray()));
		return bos.toByteArray();
	}

	private Dictionary parseNotify(String ans) throws Exception {
		Hashtable events = new Hashtable();
		String xml = ans.substring(ans.indexOf('<'), ans.length());
		XMLParser parser = new XMLParser(xml);
		XMLTag rootTag = parser.getRootXMLTag();
		if (!rootTag.hasOnlyTags()) {
			throw new Exception(
					"CONTROL SERVER: Root tag has something else except tags");
		}
		Vector content = rootTag.getContent();
		for (int i = 0; i < content.size(); i++) {
			XMLTag tag = (XMLTag) content.elementAt(i);
			Vector elems = tag.getContent();
			for (int j = 0; j < elems.size(); j++) {
				XMLTag elem = (XMLTag) elems.elementAt(j);
				if (elem.hasOnlyText()) {
					events.put(elem.getName(), elem.getContent().elementAt(0)
							.toString());
				}
			}
		}
		return events;
	}

	private boolean checkEvents(Dictionary evs) {
		boolean test = true;
		if (!evs.get(UPnPConstants.N_IN_STRING).equals(UPnPConstants.E_STRING)) {
			test = false;
		}
		if (!evs.get(UPnPConstants.N_IN_BOOLEAN)
				.equals(UPnPConstants.E_BOOLEAN)) {
			test = false;
		}
		if (!evs.get(UPnPConstants.N_OUT_NUMBER).equals(UPnPConstants.E_NUMBER)) {
			test = false;
		}
		if (!evs.get(UPnPConstants.N_OUT_INT).equals(UPnPConstants.E_INT)) {
			test = false;
		}
		if (!evs.get(UPnPConstants.N_OUT_CHAR).equals(UPnPConstants.E_CHAR)) {
			test = false;
		}
		if (!evs.get(UPnPConstants.N_OUT_FLOAT).equals(UPnPConstants.E_FLOAT)) {
			test = false;
		}
		return test;
	}

	public void finish() {
		try {
			ssocket.close();
			sock.close();
			is.close();
		}
		catch (Exception er) {
			er.printStackTrace();
		}
	}
}
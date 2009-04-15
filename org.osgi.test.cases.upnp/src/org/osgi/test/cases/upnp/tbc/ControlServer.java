package org.osgi.test.cases.upnp.tbc;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;

import junit.framework.Assert;

import org.osgi.test.cases.upnp.tbc.parser.XMLParser;
import org.osgi.test.cases.upnp.tbc.parser.XMLTag;

/**
 * 
 * 
 */
public class ControlServer extends Assert implements Runnable {
	public volatile boolean	isFinished	= false;
	private Hashtable		hash;
	private Socket			socket;
	private InetAddress		address;
	private XMLParser		parser;
	private OutputStream	os;
	private InputStream		is;
	private volatile String	ans;

	public ControlServer(String host, int port) throws Exception {
		address = InetAddress.getByName(host);
		socket = new Socket(address, port);
	}

	public void send(byte[] bytes) {
		try {
			os = socket.getOutputStream();
			os.write(bytes);
		}
		catch (Exception er) {
			er.printStackTrace();
		}
	}

	public void run() {
		try {
			socket.setSoTimeout(32000);
			is = socket.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024];
			int rd = is.read(bytes);
			while (rd != -1) {
				baos.write(bytes, 0, rd);
				rd = is.read(bytes);
			}
			ans = new String(baos.toByteArray());
			isFinished = true;
		}
		catch (Exception er) {
			er.printStackTrace();
		}
	}

	public void checkAns() throws Exception {
		BufferedReader br = new BufferedReader(new StringReader(ans));
		hash = new Hashtable(6);
		String ll = br.readLine();
		if (ll.equals("HTTP/1.1 200 OK")) {
			String body = ans.substring(ans.indexOf('<'), ans.length());
			parser = new XMLParser(body);
			XMLTag rootTag = parser.getRootXMLTag();
			if (!rootTag.hasOnlyTags()) {
				fail("CONTROL SERVER: Root tag has something else except tags");
			}
			Vector content = rootTag.getContent();
			for (int i = 0; i < content.size(); i++) {
				XMLTag tag = (XMLTag) content.elementAt(i);
				Vector elems = tag.getContent();
				for (int j = 0; j < elems.size(); j++) {
					XMLTag elem = (XMLTag) elems.elementAt(j);
					Vector params = elem.getContent();
					for (int k = 0; k < params.size(); k++) {
						XMLTag param = (XMLTag) params.elementAt(k);
						if (param.hasOnlyText()) {
							hash.put(param.getName(), param.getContent()
									.elementAt(0).toString());
						}
					}
				}
			}
			controlTest(hash);
			log("Control test of Export is OK");
		}
		else {
			fail("RECEIVED ERROR : " + ans);
		}
	}

	private static void controlTest(Dictionary val) {
		assertNotNull(val);

		String str = val.get(UPnPConstants.N_OUT_STRING).toString();
		log("Received String value in CP:" + str);
		assertEquals("SERVER: " + UPnPConstants.N_OUT_STRING
				+ " value is not ok: " + str, UPnPConstants.V_OUT_STRING, str);

		str = val.get(UPnPConstants.N_OUT_STR).toString();
		log("Received String value in CP:" + str);
		assertEquals("SERVER: " + UPnPConstants.N_OUT_STRING
				+ " value is not ok: " + str, UPnPConstants.V_OUT_STR, str);

		str = val.get(UPnPConstants.N_OUT_BOOLEAN).toString();
		log("Received Boolean value in CP:" + str);
		assertEquals("SERVER: " + UPnPConstants.N_OUT_BOOLEAN
				+ " value is not ok: " + str, UPnPConstants.V_OUT_BOOLEAN, str);

		str = val.get(UPnPConstants.N_OUT_NUMBER).toString();
		log("Received Number value in CP:" + str);
		assertEquals("SERVER: " + UPnPConstants.N_OUT_NUMBER
				+ " value is not ok: " + str, UPnPConstants.V_OUT_NUMBER, str);

		str = val.get(UPnPConstants.N_OUT_INT).toString();
		log("Received Int value in CP:" + str);
		assertEquals("SERVER: " + UPnPConstants.N_OUT_INT
				+ " value is not ok: " + str, UPnPConstants.V_OUT_INT, str);

		str = val.get(UPnPConstants.N_OUT_CHAR).toString();
		if (str.equals("&quot;")) {
			str = "\"";
		}
		else {
			log("SERVER: "
					+ UPnPConstants.N_OUT_CHAR
					+ " expected value was \"&quot;\" since in XML \" char have to be encoded as the entity \"&quot;\", but the received value is: "
					+ str);
		}
		log("Received Char value in CP:" + str);
		assertEquals("SERVER: " + UPnPConstants.N_OUT_CHAR
				+ " value is not ok: " + str, UPnPConstants.V_OUT_CHAR, str);

		str = val.get(UPnPConstants.N_OUT_FLOAT).toString();
		log("Received Float value in CP:" + str);
		assertEquals("SERVER: " + UPnPConstants.N_OUT_FLOAT
				+ " value is not ok: " + str, UPnPConstants.V_OUT_FLOAT, str);
	}

	public void finish() throws Exception {
		socket.close();
		os.close();
		is.close();
	}

	private static void log(String message) {
		UPnPControl.log(message);
	}
}
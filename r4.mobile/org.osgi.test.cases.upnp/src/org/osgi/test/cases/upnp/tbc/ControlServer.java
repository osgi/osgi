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
public class ControlServer implements Runnable {
	public boolean						isFinished	= false;
	public boolean						isError		= false;
	private Hashtable					hash;
	private Socket						socket;
	private InetAddress					address;
	private XMLParser					parser;
	private OutputStream				os;
	private InputStream					is;
	private DefaultTestBundleControl	control;

	public ControlServer(String host, int port, DefaultTestBundleControl control)
			throws Exception {
		address = InetAddress.getByName(host);
		socket = new Socket(address, port);
		this.control = control;
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
			String ans = new String(baos.toByteArray());
			checkAns(ans);
		}
		catch (Exception er) {
			er.printStackTrace();
		}
	}

	private void checkAns(String ans) throws Exception {
		BufferedReader br = new BufferedReader(new StringReader(ans));
		hash = new Hashtable(6);
		String ll = br.readLine();
		if (ll.equals("HTTP/1.1 200 OK")) {
			String body = ans.substring(ans.indexOf('<'), ans.length());
			parser = new XMLParser(body);
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
			if (controlTest(hash)) {
				control.log("Control test of Export is OK");
				isFinished = true;
			}
		}
		else
			if (ll.equals("HTTP/1.1 500 Internal Server Error")) {
				//System.out.println("RECEIVED ERROR : " + ans);
				isError = true;
			}
			else {
				System.out.println("Unsupported answer is received");
			}
	}

	private boolean controlTest(Dictionary val) {
    boolean test = true;

    String str = val.get(UPnPConstants.N_OUT_STRING).toString();
    control.log ("Received String value in CP:" + str);
    if (!str.equals(UPnPConstants.V_OUT_STRING)) {
      control.log ("SERVER: " + UPnPConstants.N_OUT_STRING + " value is not ok: " + str);
      test = false;
    }

    str = val.get(UPnPConstants.N_OUT_STR).toString();
    control.log ("Received String value in CP:" + str);
    if (!str.equals(UPnPConstants.V_OUT_STR)) {
      control.log ("SERVER: " + UPnPConstants.N_OUT_STRING + " value is not ok: " + str);
      test = false;
    }


    str = val.get(UPnPConstants.N_OUT_BOOLEAN).toString();
    control.log ("Received Boolean value in CP:" + str);
    if (!str.equals(UPnPConstants.V_OUT_BOOLEAN)) {
      control.log ("SERVER: " + UPnPConstants.N_OUT_BOOLEAN + " value is not ok: " + str);
      test = false;
    }

    str = val.get(UPnPConstants.N_OUT_NUMBER).toString();
    control.log ("Received Number value in CP:" + str);
    if (!str.equals(UPnPConstants.V_OUT_NUMBER)) {
      control.log ("SERVER: " + UPnPConstants.N_OUT_NUMBER + " value is not ok: " + str);
      test = false;
    }

    str = val.get(UPnPConstants.N_OUT_INT).toString();
    control.log ("Received Int value in CP:" + str);
    if (!str.equals(UPnPConstants.V_OUT_INT)) {
      control.log ("SERVER: " + UPnPConstants.N_OUT_INT + " value is not ok: " + str);
      test = false;
    }

    str = val.get(UPnPConstants.N_OUT_CHAR).toString();
    if (str.equals("&quot;")) {
      str = "\"";
    }
    else {
      control.log("SERVER: " + UPnPConstants.N_OUT_CHAR + " expected value was \"&quot;\" since in XML \" char have to be encoded as the entity \"&quot;\", but the received value is: " + str);
    }
    control.log ("Received Char value in CP:" + str);
    if (!str.equals(UPnPConstants.V_OUT_CHAR)) {
      control.log ("SERVER: " + UPnPConstants.N_OUT_CHAR + " value is not ok: " + str);
      test = false;
    }

    str = val.get(UPnPConstants.N_OUT_FLOAT).toString();
    control.log ("Received Float value in CP:" + str);
    if (!str.equals(UPnPConstants.V_OUT_FLOAT)) {
      control.log ("SERVER: " + UPnPConstants.N_OUT_FLOAT + " value is not ok: " + str);
      test = false;
    }

    return test;
	}

	public void finish() throws Exception {
		socket.close();
		os.close();
		is.close();
	}
}
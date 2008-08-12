package org.osgi.impl.service.upnp.cp.description;

import java.io.*;
import java.net.*;
import org.osgi.framework.BundleContext;

public class Description {
	private String				XMLfileContents	= "";
	public static String		cdPath;
	public static BundleContext	bcd;

	public Description(BundleContext bc1) {
		bcd = bc1;
	}

	// This method returns the document object based on the xml file.
	public Document getDocument(String cdURL) throws Exception {
		return getDocument(cdURL, bcd);
	}

	// This method returns the document object based on the xml file.
	public Document getDocument(String cdURL, BundleContext bc)
			throws Exception {
		cdPath = cdURL;
		URL ur1 = null;
		try {
			ur1 = new URL(cdURL);
		}
		catch (Exception eobj) {
			XMLfileContents = "ERROR" + eobj.getMessage();
			return null;
		}
		String fileN = ur1.getFile().trim();
		String ipaddr = ur1.getHost();
		int portN = ur1.getPort();
		if (portN == -1) {
			portN = 80;
		}
		accept(ipaddr, portN, fileN);
		if (XMLfileContents.startsWith("ERROR")) {
			return null;
		}
		else
			if (XMLfileContents.length() != 0) {
				try {
					Document docs1 = new Document(XMLfileContents, false, bcd);
					docs1.rootDevice.bc1 = bcd;
					return docs1;
				}
				catch (Exception e) {
					return null;
				}
			}
			else {
				return null;
			}
	}

	// This method accepts the request from the CD.
	void accept(String cdaddress, int port, String fileN) {
		Socket sock = null;
		try {
			sock = new Socket(cdaddress, port);
			sock.setSoTimeout(20000);
			sendAgain(sock, fileN, cdaddress, port);
		}
		catch (java.io.InterruptedIOException iioe) {
			try {
				sendAgain(sock, fileN, cdaddress, port);
			}
			catch (Exception e) {
				XMLfileContents = "ERROR:" + e.getMessage();
			}
		}
		catch (java.net.UnknownHostException uhe) {
			XMLfileContents = "ERROR: " + uhe.getMessage();
		}
		catch (java.net.SocketException se) {
			XMLfileContents = "ERROR:" + se.getMessage();
		}
		catch (java.io.IOException ioe) {
			XMLfileContents = "ERROR:" + ioe.getMessage();
		}
		catch (Exception e) {
			XMLfileContents = "ERROR:" + e.getMessage();
		}
	}

	// This method sends the message to the CD.
	void sendAgain(Socket sock1, String fileN, String ipaddr, int portN)
			throws Exception {
		OutputStream dos = sock1.getOutputStream();
		String msgToSend = makeDescriptionRequest(ipaddr, portN, fileN);
		dos.write(msgToSend.getBytes());
		dos.flush();
		receiveData(sock1, fileN, ipaddr, portN);
		sock1.close();
	}

	// This method receives the message from the CD.
	void receiveData(Socket sock, String fileN, String ipaddr, int portN)
			throws Exception {
		InputStream din = sock.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		/*
		 * int cctr=din.read(); XMLfileContents = XMLfileContents + (char)cctr;
		 * while(cctr!=-1){ cctr = din.read(); XMLfileContents = XMLfileContents +
		 * (char)cctr; }
		 */
		int j;
		while ((j = din.read()) != -1) {
			baos.write(j);
		}
		XMLfileContents = baos.toString();
	}

	public String makeDescriptionRequest(String ipaddr, int portN,
			String filePath) {
		StringBuffer msgToSend = new StringBuffer();
		msgToSend.append("GET ");
		msgToSend.append(filePath);
		msgToSend.append(" HTTP/1.0\r\n");
		msgToSend.append("HOST: ");
		msgToSend.append(ipaddr);
		msgToSend.append(":");
		msgToSend.append(portN);
		msgToSend.append("\r\n\r\n");
		return msgToSend.toString();
	}
}

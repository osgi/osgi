package org.osgi.impl.service.upnp.cp.description;

import java.io.*;
import java.net.*;
import org.osgi.framework.BundleContext;

public class Description {
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
			log("ERROR" + eobj.getMessage() + " for " + cdURL );
			return null;
		}
		String fileN = ur1.getFile().trim();
		String ipaddr = ur1.getHost();
		int portN = ur1.getPort();
		if (portN == -1) {
			portN = 80;
		}
		String xmlFileContents = accept(ipaddr, portN, fileN);
		if (xmlFileContents.startsWith("ERROR")) {
			log("Error in reading device document " + xmlFileContents );
			return null;
		}
		else
			if (xmlFileContents.length() != 0) {
				try {
					Document docs1 = new Document(xmlFileContents, false, bcd);
					docs1.rootDevice.bc1 = bcd;
					return docs1;
				}
				catch (Exception e) {
					log("Building document" + e);
					return null;
				}
			}
			else {
				log("No Contents for " + ur1 );
				return null;
			}
	}

	private void log(String string) {
		//System.out.println("UPNP: " + string );
	}

	// This method accepts the request from the CD.
	String accept(String cdaddress, int port, String fileN) {
		Socket sock = null;
		try {
			sock = new Socket(cdaddress, port);
			sock.setSoTimeout(5000);
			return sendAgain(sock, fileN, cdaddress, port);
		}
		catch (java.io.InterruptedIOException iioe) {
			try {
				log("Retrying " + cdaddress );
				return sendAgain(sock, fileN, cdaddress, port);
			}
			catch (Exception e) {
				return "ERROR:" + e.getMessage();
			}
		}
		catch (Exception uhe) {
			return "ERROR: " + uhe.getMessage();
		}
	}

	// This method sends the message to the CD.
	String sendAgain(Socket sock1, String fileN, String ipaddr, int portN)
			throws Exception {
		OutputStream dos = sock1.getOutputStream();
		String msgToSend = makeDescriptionRequest(ipaddr, portN, fileN);
		log("write to " + ipaddr + ":" + portN + " " + msgToSend );
		dos.write(msgToSend.getBytes());
		dos.flush();
		log("will read from " + ipaddr );
		String result = receiveData(sock1, fileN, ipaddr, portN);
		log("have read from " + ipaddr );
		sock1.close();
		return result;
	}

	// This method receives the message from the CD.
	String receiveData(Socket sock, String fileN, String ipaddr, int portN)
			throws Exception {
		InputStream din = sock.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		/*
		 * int cctr=din.read(); XMLfileContents = XMLfileContents + (char)cctr;
		 * while(cctr!=-1){ cctr = din.read(); XMLfileContents = XMLfileContents +
		 * (char)cctr; }
		 */
		byte [] buffer = new byte[4096];
		int size = din.read(buffer);
		while (size>0) {
			baos.write(buffer,0,size);
			size = din.read(buffer);
		}
		return baos.toString();
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

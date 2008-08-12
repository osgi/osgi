package org.osgi.test.cases.upnp.tbc;

import java.io.*;
import java.net.*;

/**
 * 
 * 
 */
public class ControlPoint extends Thread {
	public String			received;
	private DatagramSocket	msocket;
	private InetAddress		address;
	private DatagramPacket	packet;
	private boolean			running;
	private final int		SEC_TO_DELAY	= 6;
	private final String	NS_VALUE		= "01";

	public ControlPoint() throws Exception {
		super("Control Point");
		msocket = new DatagramSocket();
		running = true;
		address = InetAddress.getByName(UPnPConstants.UPnPMCAddress);
		start();
		sleep(2000);
	}

	public void send(DatagramPacket pack) {
		try {
			msocket.send(pack);
		}
		catch (Exception er) {
			er.printStackTrace();
		}
	}

	public void run() {
		try {
			byte[] bytes = new byte[1048];
			packet = new DatagramPacket(bytes, bytes.length);
			msocket.receive(packet);
			//      System.out.println("RECEIVED DATA: " + new
			// String(packet.getData()));
			received = parse(new String(packet.getData()));
		}
		catch (Exception er) {
			er.printStackTrace();
		}
	}

	public void interrupt() {
		msocket.close();
		running = false;
	}

	private String parse(String resp) throws Exception {
		String path = null;
		String loc = null;
		BufferedReader br = new BufferedReader(new StringReader(resp));
		String ll = br.readLine();
		ll = br.readLine();
		while (!ll.equals(" ")) {
			if (ll.substring(0, ll.indexOf(':')).equals("LOCATION")) {
				loc = ll.substring(ll.indexOf(':') + 2, ll.length());
				break;
			}
			else {
				ll = br.readLine();
			}
		}
		try {
			path = getXML(loc);
		}
		catch (Exception er) {
			er.printStackTrace();
		}
		return path;
	}

	public String getXML(String path) throws IOException, MalformedURLException {
		URL url = new URL(path);
		InputStream input = url.openStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] bytes = new byte[1024];
		int rd = input.read(bytes);
		while (rd != -1) {
			baos.write(bytes, 0, rd);
			rd = input.read(bytes);
		}
		String xml = new String(baos.toByteArray());
		return xml;
	}

	public DatagramPacket createMSearch() {
		StringBuffer buff = new StringBuffer();
		buff.append(UPnPConstants.M_SEARCH);
		buff.append(UPnPConstants.H_HOST);
		buff.append(UPnPConstants.SEARCH_ADDRESS);
		buff.append(UPnPConstants.CRLF);
		buff.append(UPnPConstants.H_MAN);
		buff.append(UPnPConstants.V_NTS_D);
		buff.append(UPnPConstants.CRLF);
		buff.append(UPnPConstants.H_MX);
		buff.append(SEC_TO_DELAY);
		buff.append(UPnPConstants.CRLF);
		buff.append(UPnPConstants.H_ST);
		//    buff.append(UPnPConstants.V_ST_ALL);
		buff.append("urn:schemas-prosyst-com:device:UPnPTesterType:1");
		buff.append(UPnPConstants.CRLF);
		buff.append(UPnPConstants.CRLF);
		byte[] bytes = buff.toString().getBytes();
		DatagramPacket search = new DatagramPacket(bytes, bytes.length,
				address, UPnPConstants.UPnPMCPort);
		return search;
	}

	public byte[] createPOST(String pathCntrl, String host, int port,
			String servType, String actName, String[] argName, String[] argValue)
			throws Exception {
		StringBuffer buf = new StringBuffer();
		buf.append(UPnPConstants.ENV_ST);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.ENV_XMLNS);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.ENV_S);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.BODY_ST);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.ULB);
		buf.append(actName + " xmlns:u=" + "\"" + servType + "\""
				+ UPnPConstants.RB);
		buf.append(UPnPConstants.CRLF);
		if (argName.length != argValue.length) {
			throw new Exception(
					"The number of argument names have to be equal to number of argument values");
		}
		for (int i = 0; i < argName.length; i++) {
			buf.append(UPnPConstants.LB + argName[i] + UPnPConstants.RB);
			buf.append(argValue[i]);
			buf.append(UPnPConstants.LCB + argName[i] + UPnPConstants.RB);
			buf.append(UPnPConstants.CRLF);
		}
		buf.append(UPnPConstants.UCLB + actName + UPnPConstants.RB);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.BODY_END);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.ENV_END);
		String body = buf.toString();
		int bodyLength = body.getBytes().length;
		buf.setLength(0);
		buf.append(UPnPConstants.POST);
		buf.append(pathCntrl);
		buf.append(" ");
		buf.append(UPnPConstants.HVER);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.H_HOST);
		buf.append(host + UPnPConstants.DD + port);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.H_CL);
		buf.append(bodyLength);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.H_CT);
		buf.append(UPnPConstants.V_FCT);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.H_SOAPACTION);
		buf.append("\"" + servType + "#" + actName + "\"");
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.CRLF); // empty line
		buf.append(body);
		//    System.out.println("POST: " + buf.toString());
		byte[] bytes = buf.toString().getBytes();
		return bytes;
	}

	public byte[] createMPOST(String pathCntrl, String host, String port,
			String service, String actName, String[] argName, String[] argValue)
			throws Exception {
		StringBuffer buf = new StringBuffer();
		buf.append(UPnPConstants.ENV_ST);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.ENV_XMLNS);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.ENV_S);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.BODY_ST);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.ULB);
		buf.append(actName + " xmlns:u=" + "\"" + service + "\""
				+ UPnPConstants.RB);
		buf.append(UPnPConstants.CRLF);
		if (argName.length != argValue.length) {
			throw new Exception(
					"The number of argument names have to be equal to number of argument values");
		}
		for (int i = 0; i < argName.length; i++) {
			buf.append(UPnPConstants.LB + argName[i] + UPnPConstants.RB);
			buf.append(argValue[i]);
			buf.append(UPnPConstants.LCB + argName[i] + UPnPConstants.RB);
			buf.append(UPnPConstants.CRLF);
		}
		buf.append(UPnPConstants.UCLB + actName + UPnPConstants.RB);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.BODY_END);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.ENV_END);
		String body = buf.toString();
		int bodyLength = body.getBytes().length;
		buf.setLength(0);
		buf.append(UPnPConstants.M_POST);
		buf.append(pathCntrl);
		buf.append(" ");
		buf.append(UPnPConstants.HVER);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.H_HOST);
		buf.append(host + UPnPConstants.DD + port);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.H_CL);
		buf.append(bodyLength);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.H_CT);
		buf.append(UPnPConstants.V_FCT);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.H_MAN);
		buf.append(UPnPConstants.V_MAN + UPnPConstants.CC);
		buf.append(" ");
		buf.append(UPnPConstants.V_NS + NS_VALUE);
		buf.append(UPnPConstants.CRLF);
		buf.append(NS_VALUE + "-" + UPnPConstants.H_SOAPACTION);
		buf.append("\"" + service + "#" + actName + "\"");
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.CRLF);
		byte[] bytes = buf.toString().getBytes();
		return bytes;
	}

	public byte[] createSUBSCRIBE(String publisher, String host, int port,
			String delivery, int dur) {
		StringBuffer buf = new StringBuffer();
		buf.append(UPnPConstants.SUBSCR);
		buf.append(publisher);
		buf.append(" ");
		buf.append(UPnPConstants.HVER);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.H_HOST);
		buf.append(host + UPnPConstants.DD + port);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.H_CALLBACK);
		buf.append(UPnPConstants.LB);
		buf.append(delivery);
		buf.append(UPnPConstants.RB);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.H_NT);
		buf.append(UPnPConstants.V_NT);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.H_TIMEOUT);
		buf.append(dur);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.CRLF);
		//    System.out.println("SUBSCR: " + buf.toString());
		byte[] bytes = buf.toString().getBytes();
		return bytes;
	}
}
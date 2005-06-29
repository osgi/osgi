package org.osgi.test.cases.upnp.tbc.device.discovery;

import java.net.*;
import org.osgi.test.cases.upnp.tbc.*;

/**
 * 
 * 
 */
public class DiscoveryMsgCreator {
	public DatagramPacket createAlive(String nt, String usn, String location) {
		return createAlive(nt, usn, location, UPnPConstants.V_CC);
	}

	public DatagramPacket createAlive(String nt, String usn, String location,
			String cachecontrol) {
		return createAlive(nt, usn, location, cachecontrol,
				UPnPConstants.V_NTS_A);
	}

	public DatagramPacket createAlive(String nt, String usn, String location,
			String cachecontrol, String nts) {
		return createAlive(nt, usn, location, cachecontrol, nts,
				UPnPConstants.V_MC_HOST);
	}

	public DatagramPacket createAlive(String nt, String usn, String location,
			String cachecontrol, String nts, String host) {
		return createAlive(nt, usn, location, cachecontrol, nts, host,
				UPnPConstants.V_SERVER, 0);
	}

	public DatagramPacket createAlive(String nt, String usn, String location,
			String cachecontrol, String nts, String host, String server, int err) {
		StringBuffer buf = new StringBuffer();
		buf.append(UPnPConstants.MC_NOTIFY);
		if (err != 1) {
			buf.append(UPnPConstants.H_HOST);
			buf.append(host);
			buf.append(UPnPConstants.CRLF);
		}
		if (err != 2) {
			buf.append(UPnPConstants.H_CC);
			buf.append(cachecontrol);
			buf.append(UPnPConstants.CRLF);
		}
		if (err != 3) {
			buf.append(UPnPConstants.H_LOC);
			buf.append(location);
			buf.append(UPnPConstants.CRLF);
		}
		if (err != 4) {
			buf.append(UPnPConstants.H_NT);
			buf.append(nt);
			buf.append(UPnPConstants.CRLF);
		}
		if (err != 5) {
			buf.append(UPnPConstants.H_NTS);
			buf.append(nts);
			buf.append(UPnPConstants.CRLF);
		}
		if (err != 6) {
			buf.append(UPnPConstants.H_SERVER);
			buf.append(server);
			buf.append(UPnPConstants.CRLF);
		}
		if (err != 7) {
			buf.append(UPnPConstants.H_USN);
			buf.append(usn);
			buf.append(UPnPConstants.CRLF);
		}
		buf.append(UPnPConstants.CRLF); // emptry line at the end of the
										// discovery
		byte[] bytes = buf.toString().getBytes();
		DatagramPacket pack = new DatagramPacket(bytes, bytes.length);
		return pack;
	}

	public DatagramPacket createByeBye(String nt, String usn) {
		return createByeBye(nt, usn, UPnPConstants.V_NTS_B);
	}

	public DatagramPacket createByeBye(String nt, String usn, String nts) {
		return createByeBye(nt, usn, nts, UPnPConstants.V_MC_HOST);
	}

	public DatagramPacket createByeBye(String nt, String usn, String nts,
			String host) {
		StringBuffer buf = new StringBuffer();
		buf.append(UPnPConstants.MC_NOTIFY);
		buf.append(UPnPConstants.H_HOST);
		buf.append(host);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.H_NT);
		buf.append(nt);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.H_NTS);
		buf.append(nts);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.H_USN);
		buf.append(usn);
		buf.append(UPnPConstants.CRLF);
		buf.append(UPnPConstants.CRLF); // empty line after last header
		byte[] bytes = buf.toString().getBytes();
		DatagramPacket pack = new DatagramPacket(bytes, bytes.length);
		return pack;
	}
}
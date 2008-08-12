package org.osgi.test.cases.upnp.tbc.device.discovery;

import java.net.*;
import org.osgi.test.cases.upnp.tbc.*;
import org.osgi.test.cases.upnp.tbc.device.*;

/**
 * 
 * 
 */
public class DiscoveryMsgSender extends DiscoveryClient {
	private DiscoveryServer		server;
	private DiscoveryMsgCreator	creator;
	private DatagramPacket[]	adp;
	private DatagramPacket[]	bdp;

	public DiscoveryMsgSender(DiscoveryServer server,
			DiscoveryMsgCreator creator) {
		super(server);
		this.creator = creator;
	}

	public DatagramPacket[] getAliveDiscoveries() {
		adp = new DatagramPacket[22];
		String location = "http://" + UPnPConstants.LOCAL_HOST + ":"
				+ UPnPConstants.HTTP_PORT + "/description?xml=root";
		adp[0] = creator.createAlive(UPnPConstants.V_ROOT,
				UPnPConstants.UDN_ROOT + UPnPConstants.QD
						+ UPnPConstants.V_ROOT, location);
		adp[1] = creator.createAlive(UPnPConstants.UDN_ROOT,
				UPnPConstants.UDN_ROOT, location);
		adp[2] = creator.createAlive(UPnPConstants.DEVICE_TYPE,
				UPnPConstants.DEVICE_TYPE + UPnPConstants.QD
						+ UPnPConstants.UDN_ROOT, location);
		adp[3] = creator.createAlive(UPnPConstants.UDN_EMB1,
				UPnPConstants.UDN_EMB1, location);
		adp[4] = creator.createAlive(UPnPConstants.DEVICE_TYPE,
				UPnPConstants.DEVICE_TYPE + UPnPConstants.QD
						+ UPnPConstants.UDN_EMB1, location);
		adp[5] = creator.createAlive(UPnPConstants.UDN_EMB2,
				UPnPConstants.UDN_EMB2, location);
		adp[6] = creator.createAlive(UPnPConstants.DEVICE_TYPE,
				UPnPConstants.DEVICE_TYPE + UPnPConstants.QD
						+ UPnPConstants.UDN_EMB2, location);
		adp[7] = creator.createAlive(UPnPConstants.SCONT_TYPE,
				UPnPConstants.UDN_ROOT + UPnPConstants.QD
						+ UPnPConstants.SCONT_TYPE, location);
		adp[8] = creator.createAlive(UPnPConstants.SEV_TYPE,
				UPnPConstants.UDN_ROOT + UPnPConstants.QD
						+ UPnPConstants.SEV_TYPE, location);
		adp[9] = creator.createAlive(UPnPConstants.SREV_TYPE,
				UPnPConstants.UDN_ROOT + UPnPConstants.QD
						+ UPnPConstants.SREV_TYPE, location);
		adp[10] = creator.createAlive(UPnPConstants.SSEV_TYPE,
				UPnPConstants.UDN_ROOT + UPnPConstants.QD
						+ UPnPConstants.SSEV_TYPE, location);
		adp[11] = creator.createAlive(UPnPConstants.SUEV_TYPE,
				UPnPConstants.UDN_ROOT + UPnPConstants.QD
						+ UPnPConstants.SUEV_TYPE, location);
		adp[12] = creator.createAlive(UPnPConstants.SCONT_TYPE,
				UPnPConstants.UDN_EMB1 + UPnPConstants.QD
						+ UPnPConstants.SCONT_TYPE, location);
		adp[13] = creator.createAlive(UPnPConstants.SEV_TYPE,
				UPnPConstants.UDN_EMB1 + UPnPConstants.QD
						+ UPnPConstants.SEV_TYPE, location);
		adp[14] = creator.createAlive(UPnPConstants.SREV_TYPE,
				UPnPConstants.UDN_EMB1 + UPnPConstants.QD
						+ UPnPConstants.SREV_TYPE, location);
		adp[15] = creator.createAlive(UPnPConstants.SSEV_TYPE,
				UPnPConstants.UDN_EMB1 + UPnPConstants.QD
						+ UPnPConstants.SSEV_TYPE, location);
		adp[16] = creator.createAlive(UPnPConstants.SUEV_TYPE,
				UPnPConstants.UDN_EMB1 + UPnPConstants.QD
						+ UPnPConstants.SUEV_TYPE, location);
		adp[17] = creator.createAlive(UPnPConstants.SCONT_TYPE,
				UPnPConstants.UDN_EMB2 + UPnPConstants.QD
						+ UPnPConstants.SCONT_TYPE, location);
		adp[18] = creator.createAlive(UPnPConstants.SEV_TYPE,
				UPnPConstants.UDN_EMB2 + UPnPConstants.QD
						+ UPnPConstants.SEV_TYPE, location);
		adp[19] = creator.createAlive(UPnPConstants.SREV_TYPE,
				UPnPConstants.UDN_EMB2 + UPnPConstants.QD
						+ UPnPConstants.SREV_TYPE, location);
		adp[20] = creator.createAlive(UPnPConstants.SSEV_TYPE,
				UPnPConstants.UDN_EMB2 + UPnPConstants.QD
						+ UPnPConstants.SSEV_TYPE, location);
		adp[21] = creator.createAlive(UPnPConstants.SUEV_TYPE,
				UPnPConstants.UDN_EMB2 + UPnPConstants.QD
						+ UPnPConstants.SUEV_TYPE, location);
		return adp;
	}

	public DatagramPacket[] getByeDiscoveries() {
		bdp = new DatagramPacket[22];
		bdp[0] = creator.createByeBye(UPnPConstants.V_ROOT,
				UPnPConstants.UDN_ROOT + UPnPConstants.QD
						+ UPnPConstants.V_ROOT);
		bdp[1] = creator.createByeBye(UPnPConstants.UDN_ROOT,
				UPnPConstants.UDN_ROOT);
		bdp[2] = creator.createByeBye(UPnPConstants.DEVICE_TYPE,
				UPnPConstants.DEVICE_TYPE + UPnPConstants.QD
						+ UPnPConstants.UDN_ROOT);
		bdp[3] = creator.createByeBye(UPnPConstants.UDN_EMB1,
				UPnPConstants.UDN_EMB1);
		bdp[4] = creator.createByeBye(UPnPConstants.DEVICE_TYPE,
				UPnPConstants.DEVICE_TYPE + UPnPConstants.QD
						+ UPnPConstants.UDN_EMB1);
		bdp[5] = creator.createByeBye(UPnPConstants.UDN_EMB2,
				UPnPConstants.UDN_EMB2);
		bdp[6] = creator.createByeBye(UPnPConstants.DEVICE_TYPE,
				UPnPConstants.DEVICE_TYPE + UPnPConstants.QD
						+ UPnPConstants.UDN_EMB2);
		bdp[7] = creator.createByeBye(UPnPConstants.SCONT_TYPE,
				UPnPConstants.UDN_ROOT + UPnPConstants.QD
						+ UPnPConstants.SCONT_TYPE);
		bdp[8] = creator.createByeBye(UPnPConstants.SEV_TYPE,
				UPnPConstants.UDN_ROOT + UPnPConstants.QD
						+ UPnPConstants.SEV_TYPE);
		bdp[9] = creator.createByeBye(UPnPConstants.SREV_TYPE,
				UPnPConstants.UDN_ROOT + UPnPConstants.QD
						+ UPnPConstants.SREV_TYPE);
		bdp[10] = creator.createByeBye(UPnPConstants.SSEV_TYPE,
				UPnPConstants.UDN_ROOT + UPnPConstants.QD
						+ UPnPConstants.SSEV_TYPE);
		bdp[11] = creator.createByeBye(UPnPConstants.SUEV_TYPE,
				UPnPConstants.UDN_ROOT + UPnPConstants.QD
						+ UPnPConstants.SUEV_TYPE);
		bdp[12] = creator.createByeBye(UPnPConstants.SCONT_TYPE,
				UPnPConstants.UDN_EMB1 + UPnPConstants.QD
						+ UPnPConstants.SCONT_TYPE);
		bdp[13] = creator.createByeBye(UPnPConstants.SEV_TYPE,
				UPnPConstants.UDN_EMB1 + UPnPConstants.QD
						+ UPnPConstants.SEV_TYPE);
		bdp[14] = creator.createByeBye(UPnPConstants.SREV_TYPE,
				UPnPConstants.UDN_EMB1 + UPnPConstants.QD
						+ UPnPConstants.SREV_TYPE);
		bdp[15] = creator.createByeBye(UPnPConstants.SSEV_TYPE,
				UPnPConstants.UDN_EMB1 + UPnPConstants.QD
						+ UPnPConstants.SSEV_TYPE);
		bdp[16] = creator.createByeBye(UPnPConstants.SUEV_TYPE,
				UPnPConstants.UDN_EMB1 + UPnPConstants.QD
						+ UPnPConstants.SUEV_TYPE);
		bdp[17] = creator.createByeBye(UPnPConstants.SCONT_TYPE,
				UPnPConstants.UDN_EMB2 + UPnPConstants.QD
						+ UPnPConstants.SCONT_TYPE);
		bdp[18] = creator.createByeBye(UPnPConstants.SEV_TYPE,
				UPnPConstants.UDN_EMB2 + UPnPConstants.QD
						+ UPnPConstants.SEV_TYPE);
		bdp[19] = creator.createByeBye(UPnPConstants.SREV_TYPE,
				UPnPConstants.UDN_EMB2 + UPnPConstants.QD
						+ UPnPConstants.SREV_TYPE);
		bdp[20] = creator.createByeBye(UPnPConstants.SSEV_TYPE,
				UPnPConstants.UDN_EMB2 + UPnPConstants.QD
						+ UPnPConstants.SSEV_TYPE);
		bdp[21] = creator.createByeBye(UPnPConstants.SUEV_TYPE,
				UPnPConstants.UDN_EMB2 + UPnPConstants.QD
						+ UPnPConstants.SUEV_TYPE);
		return bdp;
	}

	public long getTimeout() {
		return 1800 * 1000;
	}

	public void request() {
		server.send(adp[0]);
	}
}
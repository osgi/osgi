
package org.osgi.impl.service.zigbee.basedriver;

import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.ZigBeeHandler;
import org.osgi.service.zigbee.ZigBeeHost;

public class ZigBeeHostImpl extends ZigBeeNodeImpl implements ZigBeeHost {

	private int	channel;
	private int	securityLevel;

	public ZigBeeHostImpl(int panId, int channel, int baud, int securityLevel, Long IEEEAddress, short nwkAddress, ZigBeeEndpoint[] endpoints) {
		super(IEEEAddress, nwkAddress, endpoints);
		this.channel = channel;
		this.securityLevel = securityLevel;
	}

	public String getNetworkKey() throws ZigBeeException {
		// TODO Auto-generated method stub
		return null;
	}

	public void start() throws ZigBeeException {
		// TODO Auto-generated method stub
	}

	public void refreshNetwork() throws ZigBeeException {
		// TODO Auto-generated method stub
	}

	public void permitJoin(short duration) throws ZigBeeException {
		// TODO Auto-generated method stub
	}

	public int getChannelMask() throws ZigBeeException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getChannel() {
		return channel;
	}

	public int getSecurityLevel() {
		return securityLevel;
	}

	public void setChannel(ZigBeeHandler handler, byte channel) throws ZigBeeException {
		// TODO Auto-generated method stub
	}

	public void setLogicalType(short logicalNodeType) throws ZigBeeException {
		// TODO Auto-generated method stub
	}

	public void setChannelMask(ZigBeeHandler handler, int mask) throws ZigBeeException {
		// TODO Auto-generated method stub
	}
}

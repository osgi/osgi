
package org.osgi.impl.service.zigbee.basedriver;

import java.io.IOException;
import org.osgi.service.zigbee.ZCLCommandHandler;
import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeHost;

/**
 * Mocked impl.
 */
public class ZigBeeHostImpl extends ZigBeeNodeImpl implements ZigBeeHost {

	private int	channel;
	private int	securityLevel;

	/**
	 * @param hostPId
	 * @param panId
	 * @param channel
	 * @param baud
	 * @param securityLevel
	 * @param IEEEAddress
	 * @param nwkAddress
	 * @param endpoints
	 */
	public ZigBeeHostImpl(String hostPId, int panId, int channel, int baud, int securityLevel, Long IEEEAddress, short nwkAddress, ZigBeeEndpoint[] endpoints) {
		super(IEEEAddress, nwkAddress, hostPId, endpoints);
		this.channel = channel;
		this.securityLevel = securityLevel;
	}

	public void start() throws Exception {
		// TODO Auto-generated method stub
	}

	public void stop() throws Exception {
		// TODO Auto-generated method stub
	}

	public boolean isStarted() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setPanId(int panId) {
		// TODO Auto-generated method stub
	}

	public void setExtendedPanId(long extendedPanId) {
		// TODO Auto-generated method stub
	}

	public String getNetworkKey() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public void refreshNetwork() throws Exception {
		// TODO Auto-generated method stub
	}

	public void permitJoin(short duration) throws Exception {
		// TODO Auto-generated method stub
	}

	public int getChannelMask() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getChannel() {
		return channel;
	}

	public int getSecurityLevel() {
		return securityLevel;
	}

	public void setChannel(byte channel) throws IOException, IllegalStateException {
		// TODO Auto-generated method stub
	}

	public void setLogicalType(short logicalNodeType) throws Exception {
		// TODO Auto-generated method stub
	}

	public void setChannelMask(int mask) throws IOException, IllegalStateException {
		// TODO Auto-generated method stub
	}

	public void createGroupService(String pid, int groupAddress, ZCLCommandHandler handler) {
		// TODO Auto-generated method stub
	}

	public void broadcast(Integer clusterID, ZCLFrame frame, ZCLCommandHandler handler) {
		// TODO Auto-generated method stub
	}

	public void broadcast(Integer clusterID, ZCLFrame frame, ZCLCommandHandler handler, String exportedServicePID) {
		// TODO Auto-generated method stub
	}

}

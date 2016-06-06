
package org.osgi.test.cases.zigbee.impl;

import java.io.IOException;
import java.math.BigInteger;
import org.osgi.service.zigbee.ZCLCommandHandler;
import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.ZDPFrame;
import org.osgi.service.zigbee.ZDPHandler;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeHandler;
import org.osgi.service.zigbee.ZigBeeHost;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;

/**
 * Mocked impl.
 * 
 * @author $Id$
 */
public class ZigBeeHostImpl extends ZigBeeNodeImpl implements ZigBeeHost {

	private int	channelAsInt;
	private int	securityLevel;

	/**
	 * @param hostPId
	 * @param panId
	 * @param channel
	 * @param baud
	 * @param securityLevel
	 * @param IEEEAddress
	 * @param endpoints
	 */
	public ZigBeeHostImpl(String hostPId, int panId, int channel,
			int securityLevel, BigInteger IEEEAddress,
			ZigBeeEndpoint[] endpoints) {
		super(IEEEAddress, hostPId, endpoints);
		this.channelAsInt = channel;
		this.securityLevel = securityLevel;
	}

	public ZigBeeHostImpl(String hostPId, int panId, int channel,
			int securityLevel, BigInteger IEEEAddress,
			ZigBeeEndpoint[] endpoints, ZigBeeNodeDescriptor nodeDesc,
			ZigBeePowerDescriptor powerDesc, String userdescription) {
		super(IEEEAddress,
				hostPId,
				endpoints,
				nodeDesc,
				powerDesc,
				userdescription);
		this.channelAsInt = channel;
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

	public String getPreconfiguredLinkKey() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public void refreshNetwork(ZigBeeHandler handler) throws Exception {
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
		return channelAsInt;
	}

	public int getSecurityLevel() {
		return securityLevel;
	}

	public void setLogicalType(short logicalNodeType) throws Exception {
		// TODO Auto-generated method stub
	}

	public void setChannelMask(int mask) throws IOException,
			IllegalStateException {
		// TODO Auto-generated method stub
	}

	public void createGroupService(int groupAddress) throws Exception {
		// TODO Auto-generated method stub
	}

	public void removeGroupService(int groupAddress) throws Exception {
		// TODO Auto-generated method stub
	}

	public void broadcast(int clusterID, ZCLFrame frame,
			ZCLCommandHandler handler) {
		// TODO Auto-generated method stub
	}

	public void broadcast(int clusterID, ZCLFrame frame,
			ZCLCommandHandler handler, String exportedServicePID) {
		// TODO Auto-generated method stub
	}

	public void updateNetworkChannel(byte channel)
			throws IllegalStateException, IOException {
		// TODO Auto-generated method stub
	}

	public short getBroadcastRadius() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setBroadcastRadius(short broadcastRadius)
			throws IllegalArgumentException, IllegalStateException {
		// TODO Auto-generated method stub
	}

	public BigInteger getIEEEAddress() {
		// TODO Auto-generated method stub
		return IEEEAddress;
	}

	public int getNetworkAddress() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getHostPid() {
		// TODO Auto-generated method stub
		return hostPId;
	}

	public int getPanId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public BigInteger getExtendedPanId() {
		// TODO Auto-generated method stub
		return null;
	}

	public void getLinksQuality(ZigBeeHandler handler) {
		// TODO Auto-generated method stub

	}

	public void getRoutingTable(ZigBeeHandler handler) {
		// TODO Auto-generated method stub

	}

	public void leave(ZigBeeHandler handler) {
		// TODO Auto-generated method stub

	}

	public void leave(boolean rejoin, boolean removeChildren,
			ZigBeeHandler handler) {
		// TODO Auto-generated method stub

	}

	public void invoke(int clusterIdReq, int expectedClusterIdRsp,
			ZDPFrame message, ZDPHandler handler) {
		// TODO Auto-generated method stub

	}

	public void invoke(int clusterIdReq, ZDPFrame message, ZDPHandler handler) {
		// TODO Auto-generated method stub

	}
}


package org.osgi.impl.service.zigbee.basedriver;

import java.io.IOException;
import java.math.BigInteger;
import org.osgi.impl.service.zigbee.event.EndResponse;
import org.osgi.impl.service.zigbee.event.ZCLCommandResponseImpl;
import org.osgi.impl.service.zigbee.event.ZCLCommandResponseStreamImpl;
import org.osgi.service.zigbee.ZCLCommandResponseStream;
import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.ZDPFrame;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeHost;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

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

	}

	public void stop() throws Exception {

	}

	public boolean isStarted() {

		return false;
	}

	public void setPanId(int panId) {

	}

	public void setExtendedPanId(long extendedPanId) {

	}

	public String getPreconfiguredLinkKey() throws Exception {

		return null;
	}

	public Promise refreshNetwork() throws Exception {
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}

	public void permitJoin(short duration) throws Exception {

	}

	public int getChannelMask() throws Exception {

		return 0;
	}

	public int getChannel() {
		return channelAsInt;
	}

	public int getSecurityLevel() {
		return securityLevel;
	}

	public void setLogicalType(short logicalNodeType) throws Exception {

	}

	public void setChannelMask(int mask) throws IOException,
			IllegalStateException {

	}

	public void createGroupService(int groupAddress) throws Exception {

	}

	public void removeGroupService(int groupAddress) throws Exception {

	}

	public ZCLCommandResponseStream broadcast(int clusterID, ZCLFrame frame) {

		ZCLCommandResponseStreamImpl impl = new ZCLCommandResponseStreamImpl();

		// Stub out the response by immediately filling it with an Unsupported
		// Operation Exception and ending it
		impl.handleResponse(new ZCLCommandResponseImpl(Promises.failed(
				new UnsupportedOperationException("Not yet implemented"))));
		impl.handleResponse(new EndResponse());

		return impl;
	}

	public ZCLCommandResponseStream broadcast(int clusterID, ZCLFrame frame,
			String exportedServicePID) {
		ZCLCommandResponseStreamImpl impl = new ZCLCommandResponseStreamImpl();

		// Stub out the response by immediately filling it with an Unsupported
		// Operation Exception and ending it
		impl.handleResponse(new ZCLCommandResponseImpl(Promises.failed(
				new UnsupportedOperationException("Not yet implemented"))));
		impl.handleResponse(new EndResponse());

		return impl;

	}

	public void updateNetworkChannel(byte channel)
			throws IllegalStateException, IOException {

	}

	public short getBroadcastRadius() {

		return 0;
	}

	public void setBroadcastRadius(short broadcastRadius)
			throws IllegalArgumentException, IllegalStateException {

	}

	public BigInteger getIEEEAddress() {

		return IEEEAddress;
	}

	public int getNetworkAddress() {

		return 0;
	}

	public String getHostPid() {

		return hostPId;
	}

	public int getPanId() {

		return 0;
	}

	public BigInteger getExtendedPanId() {

		return null;
	}

	public Promise getLinksQuality() {
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}

	public Promise getRoutingTable() {
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}

	public Promise leave() {
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}

	public Promise leave(boolean rejoin, boolean removeChildren) {
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}

	public Promise invoke(int clusterIdReq, int expectedClusterIdRsp,
			ZDPFrame message) {
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}

	public Promise invoke(int clusterIdReq, ZDPFrame message) {
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}
}

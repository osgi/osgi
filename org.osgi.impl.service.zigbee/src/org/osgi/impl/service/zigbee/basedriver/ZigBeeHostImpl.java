
package org.osgi.impl.service.zigbee.basedriver;

import java.io.IOException;
import java.math.BigInteger;
import org.osgi.impl.service.zigbee.event.EndResponse;
import org.osgi.impl.service.zigbee.event.ZCLCommandResponseImpl;
import org.osgi.impl.service.zigbee.event.ZCLCommandResponseStreamImpl;
import org.osgi.service.zigbee.ZCLCommandResponseStream;
import org.osgi.service.zigbee.ZCLFrame;
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

	private long		communicationTimeout	= 60 * 1000;

	private boolean		isStarted				= false;

	private int			channelAsInt;
	private int			securityLevel;
	private int			panId;
	private BigInteger	extendedPanId;

	public ZigBeeHostImpl(String hostPid, int panId, int channel, int securityLevel, BigInteger IEEEAddress, ZigBeeEndpoint[] endpoints, ZigBeeNodeDescriptor nodeDesc, ZigBeePowerDescriptor powerDesc,
			String userdescription) {
		super(IEEEAddress, endpoints, nodeDesc, powerDesc, userdescription);

		this.channelAsInt = channel;
		this.securityLevel = securityLevel;
		this.extendedPanId = new BigInteger("-1");
		this.panId = panId;
	}

	public void start() throws Exception {
		this.isStarted = true;
	}

	public void stop() throws Exception {
		this.isStarted = false;
	}

	public boolean isStarted() {
		return isStarted;
	}

	public void setPanId(int panId) throws IllegalStateException {
		this.panId = panId;
	}

	public int getPanId() {
		return panId;
	}

	public BigInteger getExtendedPanId() {
		return extendedPanId;
	}

	public void setExtendedPanId(BigInteger extendedPanId) {
		this.extendedPanId = extendedPanId;
	}

	public String getPreconfiguredLinkKey() throws Exception {
		return null;
	}

	public Promise refreshNetwork() throws Exception {
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}

	public void permitJoin(short duration) throws Exception {
		// DO NOTHING
	}

	public int getChannelMask() throws Exception {
		throw new UnsupportedOperationException("Not implemented");
	}

	public int getChannel() {
		return channelAsInt;
	}

	public int getSecurityLevel() {
		return securityLevel;
	}

	public void setLogicalType(short logicalNodeType) throws Exception {
		new UnsupportedOperationException("Not implemented");
	}

	public void setChannelMask(int mask) throws IOException, IllegalStateException {
		new UnsupportedOperationException("Not implemented");
	}

	public void createGroupService(int groupAddress) throws Exception {
		new UnsupportedOperationException("Not implemented");
	}

	public void removeGroupService(int groupAddress) throws Exception {
		new UnsupportedOperationException("Not implemented");
	}

	public ZCLCommandResponseStream broadcast(int clusterID, ZCLFrame frame) {

		ZCLCommandResponseStreamImpl stream = new ZCLCommandResponseStreamImpl();

		/*
		 * Stub out the response by immediately filling it with an Unsupported
		 * Operation Exception and ending it
		 */
		stream.handleResponse(new ZCLCommandResponseImpl(Promises.failed(new UnsupportedOperationException("Not yet implemented"))));
		stream.handleResponse(new EndResponse());

		return stream;
	}

	public ZCLCommandResponseStream broadcast(int clusterID, ZCLFrame frame,
			String exportedServicePID) {
		ZCLCommandResponseStreamImpl stream = new ZCLCommandResponseStreamImpl();

		/*
		 * Stub out the response by immediately filling it with an Unsupported
		 * Operation Exception and ending it
		 */
		stream.handleResponse(new ZCLCommandResponseImpl(Promises.failed(new UnsupportedOperationException("Not yet implemented"))));
		stream.handleResponse(new EndResponse());

		return stream;
	}

	public void updateNetworkChannel(byte channel) throws IllegalStateException, IOException {
		this.channelAsInt = channel & 0xff;
	}

	public short getBroadcastRadius() {
		throw new UnsupportedOperationException("Not implemented");
	}

	public void setBroadcastRadius(short broadcastRadius) throws IllegalArgumentException, IllegalStateException {
		new UnsupportedOperationException("Not implemented");
	}

	public int getNetworkAddress() {
		return 0;
	}

	public void setCommunicationTimeout(long timeout) {
		this.communicationTimeout = timeout;
	}

	public long getCommunicationTimeout() {
		return communicationTimeout;
	}

	public Promise getLinksQuality() {
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}

	public Promise getRoutingTable() {
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}
}

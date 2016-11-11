
package org.osgi.impl.service.zigbee.basedriver;

import java.io.IOException;
import java.math.BigInteger;
import org.osgi.impl.service.zigbee.event.EndResponse;
import org.osgi.impl.service.zigbee.event.ZCLCommandResponseImpl;
import org.osgi.impl.service.zigbee.event.ZCLCommandResponseStreamImpl;
import org.osgi.service.zigbee.ZCLCommandResponseStream;
import org.osgi.service.zigbee.ZCLFrame;
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

	private int			currentChannel;
	private int			securityLevel;
	private int			panId;
	private BigInteger	extendedPanId;
	private int			channelMask;
	private short		broadcastRadius;

	/**
	 * Creates a ZigBeeHost object.
	 * 
	 * @param hostPid
	 * @param panId
	 * @param channel
	 * @param securityLevel
	 * @param IEEEAddress
	 * @param endpoints
	 * @param nodeDescriptor
	 * @param powerDescriptor
	 * @param userdescription
	 */
	public ZigBeeHostImpl(String hostPid, int panId, int channel, int nwkAddress, int securityLevel, BigInteger IEEEAddress, ZigBeeEndpointImpl[] endpoints, ZigBeeNodeDescriptor nodeDescriptor,
			ZigBeePowerDescriptor powerDescriptor, String userdescription) {
		super(IEEEAddress, nwkAddress, endpoints, nodeDescriptor, powerDescriptor, userdescription);

		this.currentChannel = channel;
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
		// FIXME: CT must check type of returned value or exception
		return Promises.failed(new UnsupportedOperationException("This feature is not implemented because cannot be tested by the CT."));
	}

	public void permitJoin(short duration) throws Exception {
		// DO NOTHING
	}

	public int getChannelMask() throws Exception {
		return channelMask;
	}

	public int getChannel() {
		return currentChannel;
	}

	public int getSecurityLevel() {
		return securityLevel;
	}

	public void setLogicalType(short logicalNodeType) throws Exception {
		new UnsupportedOperationException("This feature is not implemented because cannot be tested by the CT.");
	}

	public void setChannelMask(int mask) throws IOException, IllegalStateException {
		this.channelMask = mask;
	}

	public void createGroupService(int groupAddress) throws Exception {
		// FIXME: Do we have a specific exception if this is not supported?
		new UnsupportedOperationException("This feature is not implemented because cannot be tested by the CT.");
	}

	public void removeGroupService(int groupAddress) throws Exception {
		// FIXME: Do we have a specific exception if this is not supported?
		new UnsupportedOperationException("This feature is not implemented because cannot be tested by the CT.");
	}

	public ZCLCommandResponseStream broadcast(int clusterID, ZCLFrame frame) {

		ZCLCommandResponseStreamImpl stream = new ZCLCommandResponseStreamImpl();

		/*
		 * Stub out the response by immediately filling it with an Unsupported
		 * Operation Exception and ending it
		 */
		stream.handleResponse(new ZCLCommandResponseImpl(Promises.failed(new UnsupportedOperationException("Not yet implemented"))));
		stream.handleResponse(new EndResponse());
		// FIXME: CT must check type of returned value or exception
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

		// FIXME: CT must check type of returned value or exception
		return stream;
	}

	public void updateNetworkChannel(byte channel) throws IllegalStateException, IOException {
		this.currentChannel = channel & 0xff;
	}

	public short getBroadcastRadius() {
		return broadcastRadius;
	}

	public void setBroadcastRadius(short broadcastRadius) throws IllegalArgumentException, IllegalStateException {
		this.broadcastRadius = broadcastRadius;
	}

	public void setCommunicationTimeout(long timeout) {
		this.communicationTimeout = timeout;
	}

	public long getCommunicationTimeout() {
		return communicationTimeout;
	}

	public Promise getLinksQuality() {
		// FIXME: Return a value. CT must check type of returned value or
		// exception
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}

	public Promise getRoutingTable() {
		// FIXME: Return a value. CT must check type of returned value or
		// exception
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}
}

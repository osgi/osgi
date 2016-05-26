/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
	public ZigBeeHostImpl(String hostPId, int panId, int channel, int securityLevel, BigInteger IEEEAddress,
			ZigBeeEndpoint[] endpoints) {
		super(IEEEAddress, hostPId, endpoints);
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

	public void setChannelMask(int mask) throws IOException, IllegalStateException {
		// TODO Auto-generated method stub
	}

	public void createGroupService(int groupAddress) throws Exception {
		// TODO Auto-generated method stub
	}

	public void removeGroupService(int groupAddress) throws Exception {
		// TODO Auto-generated method stub
	}

	public void broadcast(int clusterID, ZCLFrame frame, ZCLCommandHandler handler) {
		// TODO Auto-generated method stub
	}

	public void broadcast(int clusterID, ZCLFrame frame, ZCLCommandHandler handler, String exportedServicePID) {
		// TODO Auto-generated method stub
	}

	public void updateNetworkChannel(byte channel) throws IllegalStateException, IOException {
		// TODO Auto-generated method stub
	}

	public short getBroadcastRadius() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setBroadcastRadius(short broadcastRadius) throws IllegalArgumentException, IllegalStateException {
		// TODO Auto-generated method stub
	}

	public BigInteger getIEEEAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getNetworkAddress() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getHostPid() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getPanId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public BigInteger getExtendedPanId() {
		// TODO Auto-generated method stub
		return null;
	}

	public void getNodeDescriptor(ZigBeeHandler handler) {
		// TODO Auto-generated method stub

	}

	public void getPowerDescriptor(ZigBeeHandler handler) {
		// TODO Auto-generated method stub

	}

	public void getComplexDescriptor(ZigBeeHandler handler) {
		// TODO Auto-generated method stub

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

	public void leave(boolean rejoin, boolean removeChildren, ZigBeeHandler handler) {
		// TODO Auto-generated method stub

	}

	public void invoke(int clusterIdReq, int expectedClusterIdRsp, ZDPFrame message, ZDPHandler handler) {
		// TODO Auto-generated method stub

	}

	public void invoke(int clusterIdReq, ZDPFrame message, ZDPHandler handler) {
		// TODO Auto-generated method stub

	}

	public void getUserDescription(ZigBeeHandler handler) {
		// TODO Auto-generated method stub

	}

	public void setUserDescription(String userDescription, ZigBeeHandler handler) {
		// TODO Auto-generated method stub

	}

}

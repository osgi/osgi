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

package org.osgi.impl.service.zigbee.basedriver.configuration;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.osgi.impl.service.zigbee.basedriver.ZCLClusterImpl;
import org.osgi.impl.service.zigbee.basedriver.ZigBeeEndpointImpl;
import org.osgi.impl.service.zigbee.basedriver.ZigBeeHostImpl;
import org.osgi.impl.service.zigbee.basedriver.ZigBeeNodeImpl;
import org.osgi.impl.service.zigbee.basedriver.descriptors.ZigBeeNodeDescriptorImpl;
import org.osgi.impl.service.zigbee.basedriver.descriptors.ZigBeePowerDescriptorImpl;
import org.osgi.impl.service.zigbee.basedriver.descriptors.ZigBeeSimpleDescriptorImpl;
import org.osgi.service.zigbee.ZCLCluster;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.descriptions.ZCLGlobalClusterDescription;
import org.osgi.service.zigbee.descriptors.ZigBeeMacCapabiliyFlags;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author $Id$
 */
public class ConfigurationFileReader {

	private ZigBeeNodeImpl[]				nodes;

	private static ConfigurationFileReader	instance;
	private ZigBeeHostImpl					host;

	private int								discoveryTimeout	= -1;
	private int								invokeTimeout		= -1;

	private int								headerMinSize		= -1;
	private int								headerMaxSize		= -1;

	private ZigBeeProfiles					profiles;

	private int								hostNetworkAddress	= 0;

	/**
	 * This is the base value of the short address that will be assigned to the
	 * ZigBeeNode services.
	 */
	private int								nwkAddress			= 0x1000;

	private ConfigurationFileReader(InputStream is) throws Exception {
		readXmlFile(is);
	}

	public static ConfigurationFileReader getInstance(InputStream is) throws Exception {
		if (instance == null) {
			return new ConfigurationFileReader(is);
		}
		return instance;
	}

	private static final String		zclFilename			= System.getProperty("org.osgi.impl.service.zigbee.zcl", "zcl.xml");

	/**
	 * Parse the input stream passed as parameter. The input stream refers to
	 * the xml file that contains the node configuratiosn.
	 * 
	 * @param is an InputStream
	 */

	private void readXmlFile(InputStream is) throws Exception {

		profiles = ZigBeeProfiles.getInstance(new FileInputStream(zclFilename));

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;

		dBuilder = dbFactory.newDocumentBuilder();

		Document doc = dBuilder.parse(is);

		parseFrameInfo(doc);
		host = parseHostElement(doc);
		parseNodesElement(doc);

		/*
		 * Post checks
		 */

		if (this.headerMinSize <= 0) {
			throw new Exception("please set the headerMinSize attribute to a positive value");
		}

		if (this.headerMaxSize <= 0) {
			throw new Exception("please set the headerMaxSize attribute to a positive value");
		}

		if (this.discoveryTimeout <= 0) {
			throw new Exception("please set the discoveryTimeout attribute to a positive value");
		}

		if (this.invokeTimeout <= 0) {
			throw new Exception("please set the invokeTimeout attribute to a positive value");
		}
	}

	/**
	 * @return the first node in the configuration file
	 */
	public ZigBeeNodeImpl getFirstNode() {
		ZigBeeNodeImpl node = nodes[0];
		return node;
	}

	/**
	 * Return the minimum ZCL Header Size, that is the ZCL Header size when the
	 * ZCLFrame is not manufacturer specific. This information is avaiable in
	 * the ZCL specification and must be written in the CT configuration file.
	 * 
	 * @return the requested information.
	 */

	public int getHeaderMinSize() {
		return headerMinSize;
	}

	/**
	 * Return the maximum ZCL Header Size, that is the ZCL Header size when the
	 * ZCLFrame is manufacturer specific. This information is avaiable in the
	 * ZCL specification and must be written in the CT configuration file.
	 * 
	 * @return the requested information.
	 */

	public int getHeaderMaxSize() {
		return headerMaxSize;
	}

	/**
	 * Timeout used for the ZigBeeNode discovery phase.
	 * 
	 * @return the timeout value.
	 */
	public int getDiscoveryTimeout() {
		return discoveryTimeout;
	}

	/**
	 * Timeout used for the timing out all the methods belonging to the
	 * ZigBeeHost, ZigBeeNode, ZigBeeEndpoint, ZigBeeCluster interfaces, that
	 * are also returning a Promise.
	 * 
	 * @return the timeout value
	 */
	public int getInvokeTimeout() {
		return invokeTimeout;
	}

	/**
	 * Returns the ZigBeeHostImpl object read from the configuration file.
	 * 
	 * @return a ZigBeeHost.
	 */

	public ZigBeeHostImpl getZigBeeHost() {
		return host;
	}

	/**
	 * Retrieves the Endpoint objects available in a specific node.
	 * 
	 * @param node The node.
	 * @return The endpoints belonging to this node.
	 */

	public ZigBeeEndpoint[] getEnpoints(ZigBeeNodeImpl node) {
		return node.getEndpoints();
	}

	/**
	 * Returns the ZigBeeNodes read from the configuration file.
	 * 
	 * @return An array of the read ZigBeeNodeImpl instances.
	 */
	public ZigBeeNodeImpl[] getZigBeeNodes() {
		return nodes;
	}

	private ZigBeeHostImpl parseHostElement(Document doc) throws Exception {
		NodeList nList = doc.getElementsByTagName("host");
		Node node = nList.item(0);
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element hostElement = (Element) node;

			int panId = ParserUtils.getAttribute(hostElement, "panId", ParserUtils.MANDATORY, -1);
			int channel = ParserUtils.getAttribute(hostElement, "channel", ParserUtils.MANDATORY, -1);
			int securityLevel = ParserUtils.getAttribute(hostElement, "securityLevel", ParserUtils.MANDATORY, -1);
			BigInteger ieeeAddress = ParserUtils.getAttribute(hostElement, "ieeeAddress", ParserUtils.MANDATORY, new BigInteger("-1"));
			discoveryTimeout = ParserUtils.getAttribute(hostElement, "discoveryTimeout", ParserUtils.MANDATORY, -1);
			invokeTimeout = ParserUtils.getAttribute(hostElement, "invokeTimeout", ParserUtils.MANDATORY, -1);

			String hostPid = "host.pid." + hostNetworkAddress;

			ZigBeeNodeDescriptor nodeDescriptor = parseNodeDescriptorElement(hostElement);
			ZigBeePowerDescriptor powerDescriptor = new ZigBeePowerDescriptorImpl(ZigBeePowerDescriptor.FULL_LEVEL, ZigBeePowerDescriptor.FULL_LEVEL, ZigBeePowerDescriptor.FULL_LEVEL, true);
			return new ZigBeeHostImpl(hostPid, panId, channel, hostNetworkAddress, securityLevel, ieeeAddress, nodeDescriptor, powerDescriptor, "");
		}

		throw new Exception("host element not found in the zigbee-ct configuration xml file");
	}

	/**
	 * Parse the <nodes> element, by looking at embedded <node> elements
	 * 
	 * @param doc The root of the DOM.
	 * @throws Exception In case of failure in parsing the configuration file.
	 */

	private void parseNodesElement(Document doc) throws Exception {

		NodeList nList = doc.getElementsByTagName("nodes");
		Node nodesNode = nList.item(0);
		if (nodesNode.getNodeType() == Node.ELEMENT_NODE) {
			Element nodesElement = (Element) nodesNode;

			NodeList nodeList = nodesElement.getElementsByTagName("node");

			int listLength = nodeList.getLength();
			nodes = new ZigBeeNodeImpl[listLength];
			for (int i = 0; i < listLength; i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element nodeElement = (Element) node;

					nodes[i] = parseNodeElement(nodeElement);
					host.add(nodes[i]);
				}
			}
		}
	}

	private ZigBeeNodeImpl parseNodeElement(Element nodeElement) throws Exception {
		BigInteger ieeeAddress = ParserUtils.getAttribute(nodeElement, "ieeeAddress", ParserUtils.MANDATORY, new BigInteger("-1"));
		BigInteger hostIeeeAddress = ParserUtils.getAttribute(nodeElement, "hostIeeeAddress", ParserUtils.MANDATORY, new BigInteger("-1"));
		String userDescription = ParserUtils.getAttribute(nodeElement, "userDescription", ParserUtils.OPTIONAL, "");
		int activeEndpointsNumber = ParserUtils.getAttribute(nodeElement, "activeEndpointsNumber", ParserUtils.MANDATORY, -1);

		if (!hostIeeeAddress.equals(host.getIEEEAddress())) {
			throw new Exception("node with ieeeAddress=" + ieeeAddress + " must have an hostIeeeAddress equal to that of the defined host.");
		}

		NodeList enpointsList = nodeElement.getElementsByTagName("endpoints");
		Node endpoints = enpointsList.item(0);
		ZigBeeEndpointImpl[] endpointsConfig = null;
		if (endpoints != null && endpoints.getNodeType() == Node.ELEMENT_NODE) {
			Element endpointsElement = (Element) endpoints;
			NodeList enpointList = endpointsElement.getElementsByTagName("endpoint");

			int endpointsLength = enpointList.getLength();
			endpointsConfig = new ZigBeeEndpointImpl[endpointsLength];
			for (int i = 0; i < endpointsLength; i++) {
				Node enpointNode = enpointList.item(i);
				if (enpointNode != null && enpointNode.getNodeType() == Node.ELEMENT_NODE) {
					Element enpointElement = (Element) enpointNode;
					endpointsConfig[i] = parseEndpointElement(enpointElement);
				}
			}
		}

		ZigBeeNodeDescriptor nodeDescriptor = parseNodeDescriptorElement(nodeElement);
		ZigBeePowerDescriptor powerDescriptor = parsePowerDescriptorElement(nodeElement);

		ZigBeeNodeImpl nodeConfig = new ZigBeeNodeImpl(host, ieeeAddress, nwkAddress++, endpointsConfig, nodeDescriptor, powerDescriptor, userDescription);
		return nodeConfig;
	}

	private ZigBeeEndpointImpl parseEndpointElement(Element endpointElement) throws Exception {
		short endpointId = ParserUtils.getAttribute(endpointElement, "id", ParserUtils.MANDATORY, (short) -1);

		ZigBeeSimpleDescriptor simpleDescriptor = parseSimpleDescriptorElement(endpointElement, endpointId);

		/*
		 * The simple descriptor contains the list of the clusters to ckeck.
		 */

		int[] inputClusters = simpleDescriptor.getInputClusters();

		ZCLCluster[] serverClusters = new ZCLCluster[inputClusters.length];
		for (int i = 0; i < inputClusters.length; i++) {
			int clusterId = inputClusters[i];

			ZCLGlobalClusterDescription global = profiles.getZCLGlobalDescription(clusterId);
			if (global == null) {
				parserError(" the simpleDescriptor of endpoint " + endpointId + " refers to a input clusterId= " + clusterId + " that is not defined in file zcl.xml");
			}
			serverClusters[i] = new ZCLClusterImpl(global, true);
		}

		int[] outputClusters = simpleDescriptor.getOutputClusters();
		ZCLCluster[] clientClusters = new ZCLCluster[outputClusters.length];

		for (int i = 0; i < outputClusters.length; i++) {
			int clusterId = outputClusters[i];

			ZCLGlobalClusterDescription global = profiles.getZCLGlobalDescription(clusterId);
			if (global == null) {
				parserError(" the simpleDescriptor of endpoint " + endpointId + " refers to a output clusterId= " + clusterId + " that is not defined in file zcl.xml");
			}
			clientClusters[i] = new ZCLClusterImpl(global, false);
		}

		return new ZigBeeEndpointImpl(endpointId, serverClusters, clientClusters, simpleDescriptor);
	}

	/**
	 * Converts the DOM element into a ZigBeeNodeDescriptor object
	 * 
	 * @param nodeElement The DOM element
	 * @return
	 */

	private ZigBeeNodeDescriptor parseNodeDescriptorElement(Element nodeElement) {
		ZigBeeNodeDescriptor descriptor = null;
		NodeList descList = nodeElement.getElementsByTagName("nodeDescriptor");
		Node nodeDesc = descList.item(0);
		if (nodeDesc != null && nodeDesc.getNodeType() == Node.ELEMENT_NODE) {
			Element nodeDescElt = (Element) nodeDesc;
			short type = ParserUtils.getAttribute(nodeDescElt, "type", ParserUtils.MANDATORY, (short) -1);
			short band = ParserUtils.getAttribute(nodeDescElt, "frequencyBand", ParserUtils.MANDATORY, (short) -1);
			int manufCode = ParserUtils.getAttribute(nodeDescElt, "manufacturerCode", ParserUtils.MANDATORY, -1);
			int maxBufSize = ParserUtils.getAttribute(nodeDescElt, "maxBufferSize", ParserUtils.MANDATORY, -1);
			boolean isComplexAvail = ParserUtils.getAttribute(nodeDescElt, "isComplexDescriptorAvailable", ParserUtils.MANDATORY, false);
			boolean isUserAvail = ParserUtils.getAttribute(nodeDescElt, "isUserDescriptorAvailable", ParserUtils.MANDATORY, false);

			ZigBeeMacCapabiliyFlags flags = new ZigBeeMacCapabiliyFlags() {

				public boolean isSecurityCapable() {
					return true;
				}

				public boolean isReceiverOnWhenIdle() {
					return true;
				}

				public boolean isMainsPower() {
					return false;
				}

				public boolean isFullFunctionDevice() {
					return true;
				}

				public boolean isAlternatePANCoordinator() {
					return false;
				}

				public boolean isAddressAllocate() {
					return false;
				}
			};

			descriptor = new ZigBeeNodeDescriptorImpl(type, band, manufCode, maxBufSize, isComplexAvail, isUserAvail, flags);
		}
		return descriptor;
	}

	/**
	 * Fill a ZigBeePowerDescriptor with the information from the DOM element
	 * 
	 * @param nodeElement The DOM element that conteins the siple descritor.
	 * @return
	 */

	private ZigBeePowerDescriptor parsePowerDescriptorElement(Element nodeElement) {
		ZigBeePowerDescriptor descriptor = null;

		NodeList descList = nodeElement.getElementsByTagName("powerDescriptor");
		Node nodeDesc = descList.item(0);
		if (nodeDesc != null && nodeDesc.getNodeType() == Node.ELEMENT_NODE) {
			Element powerDescElt = (Element) nodeDesc;

			short powerMode = ParserUtils.getAttribute(powerDescElt, "currentPowerMode", ParserUtils.MANDATORY, (short) -1);
			short powerSource = ParserUtils.getAttribute(powerDescElt, "currentPowerSource", ParserUtils.MANDATORY, (short) -1);
			short powerSourceLevel = ParserUtils.getAttribute(powerDescElt, "currentPowerSourceLevel", ParserUtils.MANDATORY, (short) -1);
			boolean isconstant = ParserUtils.getAttribute(powerDescElt, "isConstantMainsPowerAvailable", ParserUtils.MANDATORY, false);

			descriptor = new ZigBeePowerDescriptorImpl(powerMode, powerSource, powerSourceLevel, isconstant);
		}
		return descriptor;
	}

	private ZigBeeSimpleDescriptorImpl parseSimpleDescriptorElement(Element endPoint, short endpointId) {

		ZigBeeSimpleDescriptorImpl simpleDescriptor = null;

		NodeList endpointDescList = endPoint.getElementsByTagName("simpleDescriptor");
		Node descriptorNode = endpointDescList.item(0);
		if (descriptorNode != null
				&& descriptorNode.getNodeType() == Node.ELEMENT_NODE) {
			Element descElt = (Element) descriptorNode;

			int deviceId = ParserUtils.getAttribute(descElt, "deviceId", ParserUtils.MANDATORY, -1);
			byte version = ParserUtils.getAttribute(descElt, "version", ParserUtils.MANDATORY, (byte) -1);
			int profileId = ParserUtils.getAttribute(descElt, "profileId", ParserUtils.MANDATORY, -1);

			String inputClustersList = ParserUtils.getAttribute(descElt, "inputClusters", ParserUtils.MANDATORY, "");
			String outputClustersList = ParserUtils.getAttribute(descElt, "outputClusters", ParserUtils.MANDATORY, "");

			int[] inputClusters = ParserUtils.toArray(inputClustersList);
			int[] outputClusters = ParserUtils.toArray(outputClustersList);

			simpleDescriptor = new ZigBeeSimpleDescriptorImpl(endpointId, deviceId, profileId, version, inputClusters, outputClusters);
		}
		return simpleDescriptor;
	}

	private void parseFrameInfo(Document doc) {
		NodeList nList = doc.getElementsByTagName("frame");
		Node node = nList.item(0);
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element frameElement = (Element) node;
			headerMaxSize = ParserUtils.getAttribute(frameElement, "headerMaxSize", ParserUtils.MANDATORY, -1);
			headerMinSize = ParserUtils.getAttribute(frameElement, "headerMinSize", ParserUtils.MANDATORY, -1);
		}
	}

	private void parserError(String message) throws Exception {
		throw new Exception("CT parser: " + message);
	}
}

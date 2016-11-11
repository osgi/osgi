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

package org.osgi.test.cases.zigbee.config.file;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.osgi.service.zigbee.descriptions.ZCLAttributeDescription;
import org.osgi.service.zigbee.descriptions.ZCLClusterDescription;
import org.osgi.service.zigbee.descriptions.ZCLGlobalClusterDescription;
import org.osgi.service.zigbee.descriptions.ZCLSimpleTypeDescription;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;
import org.osgi.test.cases.zigbee.configuration.ParserUtils;
import org.osgi.test.cases.zigbee.configuration.ZigBeeProfiles;
import org.osgi.test.cases.zigbee.descriptors.ZigBeeNodeDescriptorImpl;
import org.osgi.test.cases.zigbee.descriptors.ZigBeePowerDescriptorImpl;
import org.osgi.test.cases.zigbee.descriptors.ZigBeeSimpleDescriptorImpl;
import org.osgi.test.cases.zigbee.mock.ZCLHeaderImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is used to parse the CT configuration file. Currently it has been
 * designed to be compatible with older JRE, but in the future this class could
 * be really simplified by using JAXB.
 * 
 * @author $Id$
 */
public class ConfigurationFileReader {

	private static ConfigurationFileReader	instance;

	private ZigBeeHostConfig				host;
	private ZigBeeNodeConfig[]				nodes;

	private int								headerMinSize		= -1;
	private int								headerMaxSize		= -1;

	private int								discoveryTimeout	= -1;
	private int								invokeTimeout		= -1;

	private ZigBeeProfiles					profiles;

	private ConfigurationFileReader(InputStream is) throws Exception {
		readXmlFile(is);
	}

	/**
	 * Retrieves a singleton instance of the ConfigurationFileReader class.
	 * 
	 * @param is An {@link InputStream} of the xml file to parse.
	 * @return a {@link ConfigurationFileReader} instance.
	 * @throws Exception
	 */

	public synchronized static ConfigurationFileReader getInstance(InputStream is) throws Exception {
		if (instance == null) {
			return new ConfigurationFileReader(is);
		}
		return instance;
	}

	private void readXmlFile(InputStream is) throws Exception {
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

		profiles = ZigBeeProfiles.getInstance(new FileInputStream("zcl.xml"));
	}

	/**
	 * @return the first node in the configuration file
	 */
	public ZigBeeNodeConfig getFirstNode() {
		ZigBeeNodeConfig node = nodes[0];
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

	public ZigBeeHostConfig getZigBeeHost() {
		return host;
	}

	/**
	 * Returns all the ZigBeeEndpoints belonging to the specified ZigBeeNode.
	 * 
	 * @param node A ZigBeeNodeConfig object.
	 * @return
	 */

	public ZigBeeEndpointConfig[] getEnpoints(ZigBeeNodeConfig node) {
		return node.getEndpoints();
	}

	private ZigBeeHostConfig parseHostElement(Document doc) throws Exception {
		NodeList nList = doc.getElementsByTagName("host");
		Node node = nList.item(0);
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element hostElement = (Element) node;

			// FIXME: do we need the PID, unless getting the ieeeAddress?
			String hostPid = ParserUtils.getAttribute(hostElement, "pid", ParserUtils.MANDATORY, "");
			int panId = ParserUtils.getAttribute(hostElement, "panId", ParserUtils.MANDATORY, -1);
			int channel = ParserUtils.getAttribute(hostElement, "channel", ParserUtils.MANDATORY, -1);
			int securityLevel = ParserUtils.getAttribute(hostElement, "securityLevel", ParserUtils.MANDATORY, -1);
			BigInteger ieeeAddress = ParserUtils.getAttribute(hostElement, "ieeeAddress", ParserUtils.MANDATORY, new BigInteger("-1"));
			discoveryTimeout = ParserUtils.getAttribute(hostElement, "discoveryTimeout", ParserUtils.MANDATORY, -1);
			invokeTimeout = ParserUtils.getAttribute(hostElement, "invokeTimeout", ParserUtils.MANDATORY, -1);

			ZigBeeNodeDescriptor nodeDescriptor = parseNodeDescriptorElement(hostElement);

			return new ZigBeeHostConfig(hostPid, panId, channel, securityLevel, ieeeAddress, nodeDescriptor, null, null);
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
			nodes = new ZigBeeNodeConfig[listLength];
			for (int i = 0; i < listLength; i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element nodeElement = (Element) node;

					ZigBeeNodeConfig nodeConfig = parseNodeElement(nodeElement);
					nodes[i] = nodeConfig;
				}
			}
		}
	}

	private ZigBeeNodeConfig parseNodeElement(Element nodeElement) throws Exception {
		BigInteger ieeeAddress = ParserUtils.getAttribute(nodeElement, "ieeeAddress", ParserUtils.MANDATORY, new BigInteger("-1"));
		String userDescription = ParserUtils.getAttribute(nodeElement, "userDescription", ParserUtils.OPTIONAL, "");
		int activeEndpointsNumber = ParserUtils.getAttribute(nodeElement, "activeEndpointsNumber", ParserUtils.MANDATORY, -1);

		NodeList enpointsList = nodeElement.getElementsByTagName("endpoints");
		Node endpoints = enpointsList.item(0);
		ZigBeeEndpointConfig[] endpointsConfig = null;
		if (endpoints != null && endpoints.getNodeType() == Node.ELEMENT_NODE) {
			Element endpointsElement = (Element) endpoints;
			NodeList enpointList = endpointsElement.getElementsByTagName("endpoint");

			int endpointsLength = enpointList.getLength();
			endpointsConfig = new ZigBeeEndpointConfig[endpointsLength];
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

		ZigBeeNodeConfig nodeConfig = new ZigBeeNodeConfig(host, ieeeAddress, endpointsConfig, nodeDescriptor, powerDescriptor, userDescription);
		nodeConfig.setActualEndpointsNumber(activeEndpointsNumber);

		return nodeConfig;
	}

	private ZigBeeEndpointConfig parseEndpointElement(Element endpointElement) throws Exception {
		short endpointId = ParserUtils.getAttribute(endpointElement, "id", ParserUtils.MANDATORY, (short) -1);

		ZigBeeSimpleDescriptor simpleDescriptor = parseSimpleDescriptorElement(endpointElement, endpointId);

		/*
		 * The simple descriptor contains the list of the clusters to ckeck.
		 */

		int[] inputClusters = simpleDescriptor.getInputClusters();

		// FIXME: check if input <-> Server!!!
		ZCLClusterDescription[] serverClustersDescriptions = new ZCLClusterDescription[inputClusters.length];
		for (int i = 0; i < inputClusters.length; i++) {
			int clusterId = inputClusters[i];

			ZCLGlobalClusterDescription global = profiles.getZCLGlobalDescription(clusterId);
			serverClustersDescriptions[i] = global.getServerClusterDescription();
		}

		// FIXME: check if output <-> Client!!!
		int[] outputClusters = simpleDescriptor.getOutputClusters();
		ZCLClusterDescription[] clientClustersDescriptions = new ZCLClusterDescription[outputClusters.length];
		for (int i = 0; i < outputClusters.length; i++) {
			int clusterId = outputClusters[i];

			ZCLGlobalClusterDescription global = profiles.getZCLGlobalDescription(clusterId);
			clientClustersDescriptions[i] = global.getClientClusterDescription();
		}

		return new ZigBeeEndpointConfig(endpointId, serverClustersDescriptions, clientClustersDescriptions, simpleDescriptor);
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

			descriptor = new ZigBeeNodeDescriptorImpl(type, band, manufCode, maxBufSize, isComplexAvail, isUserAvail);
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

			// FIXME: where to store this????
			int outputClustersNumber = ParserUtils.getAttribute(descElt, "outputClustersNumber", ParserUtils.MANDATORY, -1);
			int inputClustersNumber = ParserUtils.getAttribute(descElt, "inputClustersNumber", ParserUtils.MANDATORY, -1);

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

	/**
	 * Not used anymore
	 * 
	 * FIXME: remove it!
	 * 
	 * @param doc
	 */
	private void parseFrameElement(Document doc) {
		NodeList nList = doc.getElementsByTagName("frame");
		Node node = nList.item(0);
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element frameElement = (Element) node;
			headerMaxSize = ParserUtils.getAttribute(frameElement, "headerMaxSize", ParserUtils.MANDATORY, -1);
			headerMinSize = ParserUtils.getAttribute(frameElement, "headerMinSize", ParserUtils.MANDATORY, -1);

			nList = doc.getElementsByTagName("requestFullFrame");
			node = nList.item(0);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element requestFullFrameElement = (Element) node;
				String payloadString = ParserUtils.getAttribute(requestFullFrameElement, "hex", ParserUtils.MANDATORY, "");
				byte[] requestFullFrame = ParserUtils.hexStringToByteArray(payloadString);

				int commandId = ParserUtils.getAttribute(requestFullFrameElement, "commandId", ParserUtils.MANDATORY, -1);

				boolean isClusterSpecificCommand = ParserUtils.getAttribute(requestFullFrameElement, "isClusterSpecificCommand", ParserUtils.MANDATORY, false);
				boolean isClientServerDirection = ParserUtils.getAttribute(requestFullFrameElement, "isClientServerDirection", ParserUtils.MANDATORY, false);
				boolean disableDefaultResponse = ParserUtils.getAttribute(requestFullFrameElement, "disableDefaultResponse", ParserUtils.MANDATORY, false);
				byte sequenceNumber = ParserUtils.getAttribute(requestFullFrameElement, "sequenceNumber", ParserUtils.MANDATORY, (byte) -1);

				ZCLHeaderImpl requestHeader = new ZCLHeaderImpl(commandId, isClusterSpecificCommand, isClientServerDirection, disableDefaultResponse, sequenceNumber);
			}
			nList = doc.getElementsByTagName("responseFullFrame");
			node = nList.item(0);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element headerdElement = (Element) node;
				String payloadString = ParserUtils.getAttribute(headerdElement, "hex", ParserUtils.MANDATORY, "");
				byte[] responseFullFrame = ParserUtils.hexStringToByteArray(payloadString);
			}
		}
	}

	/**
	 * Retrieve the first attribute that satisfy the given requirements.
	 * 
	 * @param isWritable Ask for an attribute that may be writable or read only
	 *        (pass null if you don't care).
	 * 
	 * @param isReportable Ask for an attribute that may be reportable or not
	 *        (pass null if you don't care).
	 * @return
	 */

	public AttributeCoordinates findAttribute(Boolean isWritable, Boolean isReportable, ZCLSimpleTypeDescription dataTypeDescription) {

		for (int i = 0; i < nodes.length; i++) {
			ZigBeeEndpointConfig[] endpoints = nodes[i].getEndpoints();

			for (int j = 0; j < endpoints.length; j++) {

				ZigBeeEndpointConfig endpoint = endpoints[j];

				ZCLClusterDescription[] serverClusters = endpoint.getServerClusters();
				for (int k = 0; k < serverClusters.length; k++) {

					ZCLAttributeDescription[] attributeDescriptions = serverClusters[k].getAttributeDescriptions();

					for (int l = 0; l < attributeDescriptions.length; l++) {
						ZCLAttributeDescription attributeDescription = attributeDescriptions[l];

						boolean passed;
						passed = (isWritable == null) ? true : isWritable.booleanValue() == !attributeDescription.isReadOnly();
						if (!passed) {
							continue;
						}

						passed = (isReportable == null) ? true : isReportable.booleanValue() == attributeDescription.isReportable();
						if (!passed) {
							continue;
						}
						passed = (dataTypeDescription == null) ? true : dataTypeDescription.getId() == attributeDescription.getDataType().getId();
						if (!passed) {
							continue;
						}

						/*
						 * Found an attribute matching the requested
						 * constraints.
						 */
						AttributeCoordinates attributeCoordinates = new AttributeCoordinates();
						attributeCoordinates.expectedEndpoint = endpoint;
						attributeCoordinates.expectedCluster = serverClusters[k];
						attributeCoordinates.expectedAttributeDescription = attributeDescriptions[l];
						return attributeCoordinates;
					}
				}
			}

		}
		return null;
	}
}

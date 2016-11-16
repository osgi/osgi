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

package org.osgi.test.cases.zigbee.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.osgi.service.zigbee.descriptions.ZCLDataTypeDescription;
import org.osgi.service.zigbee.descriptions.ZCLGlobalClusterDescription;
import org.osgi.test.cases.zigbee.config.file.ConfigurationFileReader;
import org.osgi.test.cases.zigbee.descriptions.ZCLAttributeDescriptionImpl;
import org.osgi.test.cases.zigbee.descriptions.ZCLClusterDescriptionImpl;
import org.osgi.test.cases.zigbee.descriptions.ZCLCommandDescriptionImpl;
import org.osgi.test.cases.zigbee.descriptions.ZCLGlobalClusterDescriptionImpl;
import org.osgi.test.cases.zigbee.descriptions.ZCLParameterDescriptionImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class is loading the ZCL profile used by the CT. This file contains the
 * description of the profile, and of the clusters that belongs to it.
 * 
 * @author portinaro
 *
 */
public class ZigBeeProfiles {

	private static ZigBeeProfiles	instance;
	/**
	 * List of the ZCLGlobalDescriptions parsed from the file
	 */
	List							globalDescriptions	= new ArrayList();

	public void loadProfile(InputStream is) throws SAXException, IOException, ParserConfigurationException {

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		saxParser.parse(is, new Parser());;
	}

	public ZCLGlobalClusterDescription getZCLGlobalDescription(int clusterId) {
		for (Iterator iterator = globalDescriptions.iterator(); iterator.hasNext();) {
			ZCLGlobalClusterDescription description = (ZCLGlobalClusterDescription) iterator.next();

			if (description.getClusterId() == clusterId) {
				return description;
			}
		}
		return null;
	}

	/**
	 * Retrieves a singleton instance of the ProfileDescriptor class.
	 * 
	 * @param is An {@link InputStream} of the xml file to parse.
	 * @return a {@link ConfigurationFileReader} instance.
	 * @throws Exception
	 */

	public synchronized static ZigBeeProfiles getInstance(InputStream is) throws Exception {
		if (instance == null) {
			instance = new ZigBeeProfiles();
			instance.loadProfile(is);
		}
		return instance;
	}

	private class Parser extends DefaultHandler {

		private Map								clusterAttributes		= null;

		private ZCLAttributeDescriptionImpl		attributeDescription;
		private ZCLParameterDescriptionImpl		parameterDescription;
		private ZCLGlobalClusterDescriptionImpl	gobalClusterDescription;

		private List							parameterDescriptions	= new ArrayList();
		private List							attributeDescriptions	= new ArrayList();
		private List							commandDescriptions		= new ArrayList();

		private Map								commandAttributes;

		private boolean							isServerSide;

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {

			if (qName.equalsIgnoreCase("profiles")) {
				// not parsed
			} else if (qName.equalsIgnoreCase("profile")) {
				// do not contain relevant attributes
			} else if (qName.equalsIgnoreCase("cluster")) {
				this.clusterAttributes = getAttributes(attributes);

				int id = ParserUtils.getAttribute(clusterAttributes, "id", ParserUtils.MANDATORY, -1);
				String name = ParserUtils.getAttribute(clusterAttributes, "name", ParserUtils.MANDATORY, "");
				String domain = ParserUtils.getAttribute(clusterAttributes, "domain", ParserUtils.OPTIONAL, "undefined");

				/*
				 * This instance will be filled when ending the server or client
				 * tags.
				 */
				gobalClusterDescription = new ZCLGlobalClusterDescriptionImpl(id, name, domain);

				globalDescriptions.add(gobalClusterDescription);

			} else if (qName.equalsIgnoreCase("server")) {
				this.isServerSide = true;

			} else if (qName.equalsIgnoreCase("client")) {
				this.isServerSide = false;

			} else if (qName.equalsIgnoreCase("attribute")) {
				Map attributesMap = getAttributes(attributes);
				int id = ParserUtils.getAttribute(attributesMap, "id", ParserUtils.MANDATORY, -1);
				boolean isReadOnly = ParserUtils.getAttribute(attributesMap, "readOnly", ParserUtils.MANDATORY, false);
				String defaultValue = ParserUtils.getAttribute(attributesMap, "defaultValue", ParserUtils.OPTIONAL, "");
				String name = ParserUtils.getAttribute(attributesMap, "name", ParserUtils.MANDATORY, "");
				// boolean isMandatory = ParserUtils.getAttribute(attributes,
				// "mandatory", ParserUtils.MANDATORY, false);
				boolean isReportable = ParserUtils.getAttribute(attributesMap, "reportable", ParserUtils.MANDATORY, false);
				String dataTypeName = ParserUtils.getAttribute(attributesMap, "dataType", ParserUtils.MANDATORY, "");

				ZCLDataTypeDescription dataType = null;
				try {
					dataType = ParserUtils.dataTypeName2dataType(dataTypeName);
				} catch (Exception e) {
					throw new SAXException(e);
				}

				attributeDescription = new ZCLAttributeDescriptionImpl(id, isReadOnly, defaultValue, name, true, isReportable, dataType);
				attributeDescriptions.add(attributeDescription);

			} else if (qName.equalsIgnoreCase("command")) {
				this.commandAttributes = getAttributes(attributes);
			} else if (qName.equalsIgnoreCase("param")) {
				Map attributesMap = getAttributes(attributes);
				String dataTypeName = ParserUtils.getAttribute(attributesMap, "dataType", ParserUtils.MANDATORY, "");
				ZCLDataTypeDescription dataType;
				try {
					dataType = ParserUtils.dataTypeName2dataType(dataTypeName);
				} catch (Exception e) {
					throw new SAXException(e);
				}

				parameterDescription = new ZCLParameterDescriptionImpl(dataType);
				parameterDescriptions.add(parameterDescription);
			}
		}

		private Map getAttributes(Attributes attributes) {
			Map map = new HashMap();
			for (int i = 0; i < attributes.getLength(); i++) {
				map.put(attributes.getQName(i), attributes.getValue(i));
			}
			return map;
		}

		public void endElement(String uri, String localName,
				String qName) throws SAXException {

			if (qName.equalsIgnoreCase("profiles")) {
			} else if (qName.equalsIgnoreCase("profile")) {
			} else if (qName.equalsIgnoreCase("cluster")) {
			} else if (qName.equalsIgnoreCase("server") || qName.equalsIgnoreCase("client")) {

				ZCLAttributeDescriptionImpl[] attributes = new ZCLAttributeDescriptionImpl[attributeDescriptions.size()];
				for (int i = 0; i < attributes.length; i++) {
					attributes[i] = (ZCLAttributeDescriptionImpl) attributeDescriptions.get(i);
				}

				ZCLCommandDescriptionImpl[] commands = new ZCLCommandDescriptionImpl[commandDescriptions.size()];
				for (int i = 0; i < commands.length; i++) {
					commands[i] = (ZCLCommandDescriptionImpl) commandDescriptions.get(i);
				}

				attributeDescriptions.clear();
				commandDescriptions.clear();

				ZCLClusterDescriptionImpl clusterDescription = new ZCLClusterDescriptionImpl(attributes, commands, gobalClusterDescription, isServerSide);

				if (isServerSide) {
					gobalClusterDescription.setServerClusterDescription(clusterDescription);
				} else {
					gobalClusterDescription.setClientClusterDescription(clusterDescription);
				}

			} else if (qName.equalsIgnoreCase("attribute")) {
				/*
				 * nothing to do because the attribute tag do not have any
				 * children so they are handled in the beginElement() callback.
				 */
			} else if (qName.equalsIgnoreCase("command")) {

				int id = ParserUtils.getAttribute(commandAttributes, "id", ParserUtils.MANDATORY, -1);
				int responseId = ParserUtils.getAttribute(commandAttributes, "responseId", ParserUtils.OPTIONAL, -1);
				boolean isMandatory = ParserUtils.getAttribute(commandAttributes, "mandatory", ParserUtils.OPTIONAL, true);
				String name = ParserUtils.getAttribute(commandAttributes, "name", ParserUtils.MANDATORY, "");

				ZCLParameterDescriptionImpl[] parameters = new ZCLParameterDescriptionImpl[parameterDescriptions.size()];
				for (int i = 0; i < parameters.length; i++) {
					parameters[i] = (ZCLParameterDescriptionImpl) parameterDescriptions.get(i);
				}

				ZCLCommandDescriptionImpl commandDescription = new ZCLCommandDescriptionImpl(id, name, isMandatory, isServerSide, -1, true, responseId, parameters);
				commandDescriptions.add(commandDescription);
				parameterDescriptions.clear();
			}
		}

		public void characters(char ch[], int start, int length) throws SAXException {
		}
	};
}

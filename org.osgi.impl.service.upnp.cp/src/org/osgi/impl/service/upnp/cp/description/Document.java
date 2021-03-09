/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.impl.service.upnp.cp.description;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;

import org.osgi.framework.BundleContext;

public class Document {
	private int				xmlPtr		= 0;
	private int				ctr			= 0;
	private boolean			flag		= true;
	private String			rootE		= "";
	private String			ended		= "";
	private String			otherDec	= "";
	private String			elemVal		= "";
	@SuppressWarnings("unused")
	private String			selement	= "";
	private Vector<String[]>	element;
	private File			fil;
	public BundleContext	bcc1;
	public RootDevice		rootDevice;
	public ServiceInfo		sdesc;
	private Element			rootElement;

	// This constructor creates the document object based on the given
	// arguments.
	public Document(String url, boolean file, BundleContext bc) {
		bcc1 = bc;
		element = new Vector<>();
		rootElement = new Element();
		rootDevice = new RootDevice(bc);
		String xmlFile = "";
		xmlPtr = 0;
		if (file == true) {
			String fullPath = "xml" + File.separator + url;
			URL ur1 = bc.getBundle().getResource(fullPath);
			fullPath = ur1.toString();
			fullPath = fullPath.substring(fullPath.indexOf(File.separator),
					fullPath.length());
			fil = new File(fullPath);
			try {
				FileInputStream fis = new FileInputStream(fullPath);
				byte barr[] = new byte[(int) fil.length()];
				fis.read(barr);
				xmlFile = new String(barr);
				fis.close();
			}
			catch (Exception eI) {
				System.out.println(eI.getMessage());
			}
		}
		else {
			xmlFile = url;
		}
		try {
			doParse(xmlFile);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// This method checks for the schema,stylesheet, comment , prolog or a dtd
	// declarations at the top of the xml document.
	void checkSchemaOrDtd(String cont) {
		char value = ' ';
		boolean chec = false;
		if (cont.substring(xmlPtr, xmlPtr + 8).equals("!DOCTYPE")) {
			chec = true;
		}
		else
			if (cont.substring(xmlPtr, xmlPtr + 3).equals("!--")) {
				chec = true;
			}
			else
				if (cont.substring(xmlPtr, xmlPtr + 3).equals("xml")) {
					chec = true;
				}
				else
					if (cont.substring(xmlPtr, xmlPtr + 15).equals(
							"?xml-stylesheet")) {
						chec = true;
					}
					else
						if (cont.substring(xmlPtr, xmlPtr + 14).equals(
								"xsl:stylesheet")) {
							chec = true;
						}
		// moves the xml pointer till the end of the xml statement.
		if (chec) {
			while (value != '>') {
				value = cont.charAt(xmlPtr);
				otherDec = otherDec + value;
				xmlPtr++;
			}
		}
		// eliminates all white spaces.
		move(cont);
		// checks if there are any more schema or dtd declarations.
		if (chec) {
			xmlPtr++;
			checkSchemaOrDtd(cont);
		}
	}

	// This method parses the xml document.
	void doParse(String xmlFile) throws Exception {
		try {
			otherDec = "<";
			String rootAttr = "";
			boolean rflag = false;
			char value = ' ';
			while (xmlFile.charAt(xmlPtr) != '<') {
				xmlPtr++;
			}
			String tmpVal = xmlFile.substring(xmlPtr, xmlPtr + 5);
			// checks for the xml prolog.
			if (tmpVal.equals("<?xml")) {
				while (value != '>') {
					xmlPtr++;
					value = xmlFile.charAt(xmlPtr);
					otherDec = otherDec + value;
				}
			}
			else {
				xmlPtr--;
			}
			xmlPtr++;
			move(xmlFile);
			xmlPtr++;
			checkSchemaOrDtd(xmlFile); // Checks for all the schema and dtd
			// declarations.
			value = xmlFile.charAt(xmlPtr);
			while (value != '>') { // Fetches the root element name and root
				// element attributes.
				if (rflag == true) {
					rootAttr = rootAttr + value;
				}
				else {
					rootE = rootE + value;
				}
				xmlPtr++;
				value = xmlFile.charAt(xmlPtr);
				if (value == ' ') {
					rflag = true;
				}
			}
			String sar[] = {rootE, rootAttr};
			element.addElement(sar);
			rootElement = new Element(rootE);
			rootElement.addAttributes(rootAttr);
			while (value != '<') { // eliminates white spaces after the root
				// element.
				value = xmlFile.charAt(xmlPtr);
				xmlPtr++;
			}
			xmlPtr--;
			try {
				while (flag) { // reads all the elemnts from xml file
					selement = readStartElement(xmlFile);
				}
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
			distribute(); // places all the elements into the xml tree.
			makeDeviceOrService(); // Identifies whether xml file is of type
			// device or service.
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// This method loads the xml file to a device object or a service object
	// based on the given xml document.
	void makeDeviceOrService() {
		String name = rootElement.getName();
		if (name.equals("root")) {
			try {
				distributeDevice();
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		else
			if (name.equals("scpd")) {
				try {
					distributeService();
				}
				catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
	}

	// This method creates the Device object and loads all the details from the
	// xml file.
	void distributeDevice() throws Exception {
		Vector<Attribute> attrs = rootElement.getAttributes();
		for (int rootatt = 0; rootatt < attrs.size(); rootatt++) {
			Attribute att = attrs.elementAt(rootatt);
			if (att.getName().trim().equals("xmlns")) {
				rootDevice.setRootAttribute(att.getName(), att.getValue());
			}
		}
		//sets the major value.
		Vector<Element> elements = getElementsByTagName("major");
		Element el = elements.elementAt(0);
		try {
			rootDevice.setMajor(Integer.parseInt(el.getValue().trim()));
		}
		catch (NumberFormatException e) {
			System.out.println(e.getMessage());
		}
		//sets the minor value.
		elements = getElementsByTagName("minor");
		el = elements.elementAt(0);
		try {
			rootDevice.setMinor(Integer.parseInt(el.getValue()));
		}
		catch (NumberFormatException e) {
			System.out.println(e.getMessage());
		}
		//sets the urlBase.
		elements = getElementsByTagName("URLBase");
		if (elements.size() > 0) {
			el = elements.elementAt(0);
			rootDevice.setURLBase(el.getValue());
		}
		//sets the device object.
		elements = getElementsByTagName("device");
		el = elements.elementAt(0);
		elements = el.getAllElements();
		try {
			// sets all the device properties.
			for (int ii = 0; ii < elements.size(); ii++) {
				Element elm = elements.elementAt(ii);
				String ename = elm.getName().trim();
				String eval = elm.getValue().trim();
				if (ename.equals("deviceType")) {
					rootDevice.setDeviceType(eval);
				}
				else
					if (ename.equals("friendlyName")) {
						rootDevice.setFriendlyName(eval);
					}
					else
						if (ename.equals("manufacturer")) {
							rootDevice.setManufacturer(eval);
						}
						else
							if (ename.equals("manufacturerURL")) {
								rootDevice.setManufacturerURL(eval);
							}
							else
								if (ename.equals("modelDescription")) {
									rootDevice.setModelDescription(eval);
								}
								else
									if (ename.equals("modelName")) {
										rootDevice.setModelName(eval);
									}
									else
										if (ename.equals("modelNumber")) {
											rootDevice.setModelNumber(eval);
										}
										else
											if (ename.equals("modelURL")) {
												rootDevice.setModelURL(eval);
											}
											else
												if (ename
														.equals("serialNumber")) {
													rootDevice
															.setSerialNumber(eval);
												}
												else
													if (ename.equals("UDN")) {
														rootDevice.setUDN(eval);
													}
													else
														if (ename.equals("UPC")) {
															rootDevice
																	.setUPC(eval);
														}
														else
															if (ename
																	.equals("iconList")) {
																Vector<Element> v1 = getElementsByTagName(
																		"iconList");
																Element elment = v1
																		.elementAt(0);
																v1 = elment
																		.getAllElements();
																Icon iarr[] = new Icon[v1
																		.size()];
																for (int iconctr = 0; iconctr < iarr.length; iconctr++) {
																	iarr[iconctr] = new Icon();
																}
																//sets the
																// values for
																// all the
																// icons.
																for (int iconctr = 0; iconctr < v1
																		.size(); iconctr++) {
																	elment = v1
																			.elementAt(iconctr);
																	Vector<Element> vvec = elment
																			.getAllElements();
																	for (int j = 0; j < vvec
																			.size(); j++) {
																		Element elmt = vvec
																				.elementAt(j);
																		String enam = elmt
																				.getName();
																		String eva = elmt
																				.getValue()
																				.trim();
																		if (enam
																				.equals("mimetype")) {
																			iarr[iconctr]
																					.setMimeType(eva);
																		}
																		else
																			if (enam
																					.equals("width")) {
																				iarr[iconctr]
																						.setWidth(Integer
																								.parseInt(eva));
																			}
																			else
																				if (enam
																						.equals("height")) {
																					iarr[iconctr]
																							.setHeight(Integer
																									.parseInt(eva));
																				}
																				else
																					if (enam
																							.equals("depth")) {
																						iarr[iconctr]
																								.setDepth(Integer
																										.parseInt(eva));
																					}
																					else
																						if (enam
																								.equals("url")) {
																							iarr[iconctr]
																									.setURL(eva);
																						}
																	}
																}
																rootDevice
																		.setIcons(iarr);
															}
															//sets the values
															// for all the
															// services.
															else
																if (ename
																		.equals("serviceList")) {
																	Vector<Element> v1 = getElementsByTagName(
																			"serviceList");
																	Element elment = v1
																			.elementAt(0);
																	v1 = elment
																			.getAllElements();
																	ServiceInfo sarr[] = new ServiceInfo[v1
																			.size()];
																	for (int servctr = 0; servctr < sarr.length; servctr++) {
																		sarr[servctr] = new ServiceInfo();
																	}
																	for (int servctr = 0; servctr < v1
																			.size(); servctr++) {
																		elment = v1
																				.elementAt(servctr);
																		Vector<Element> allservs = elment
																				.getAllElements();
																		for (int sctr = 0; sctr < allservs
																				.size(); sctr++) {
																			Element elmt = allservs
																					.elementAt(sctr);
																			String enam = elmt
																					.getName();
																			String eva = elmt
																					.getValue();
																			if (enam
																					.equals("serviceType")) {
																				sarr[servctr]
																						.setServiceType(eva);
																			}
																			if (enam
																					.equals("serviceId")) {
																				sarr[servctr]
																						.setServiceID(eva);
																			}
																			if (enam
																					.equals("SCPDURL")) {
																				sarr[servctr]
																						.setSCPDURL(eva);
																			}
																			if (enam
																					.equals("controlURL")) {
																				sarr[servctr]
																						.setControlURL(eva);
																			}
																			if (enam
																					.equals("eventSubURL")) {
																				sarr[servctr]
																						.setEventSubURL(eva);
																			}
																		}
																	}
																	rootDevice
																			.setServices(
																					sarr,
																					rootDevice);
																}
																// sets the
																// values for
																// the embedded
																// devices.
																else
																	if (ename
																			.equals("deviceList")) {
																		Vector<Element> dlist = getElementsByTagName(
																				"deviceList");
																		if (dlist
																				.size() > 0) {
																			Element el1 = dlist
																					.elementAt(0);
																			Vector<Element> embdevices = el1
																					.getAllElements();
																			RootDevice eDevices[] = new RootDevice[embdevices
																					.size()];
																			for (int edevctr = 0; edevctr < embdevices
																					.size(); edevctr++) {
																				eDevices[edevctr] = new RootDevice(
																						rootDevice,
																						bcc1);
																				eDevices[edevctr]
																						.setMajor(rootDevice
																								.getMajor());
																				eDevices[edevctr]
																						.setMinor(rootDevice
																								.getMinor());
																				eDevices[edevctr]
																						.setURLBase(rootDevice
																								.getURLBase());
																				Element el2 = embdevices
																						.elementAt(edevctr);
																				addEmbeddedDevice(
																						el2,
																						eDevices[edevctr]);
																			}
																			rootDevice
																					.setEmbeddedDevices(eDevices);
																		}
																	}
																	else
																		if (ename
																				.equals("presentationURL")) {
																			rootDevice
																					.setPresentationURL(eval);
																		}
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// This method adds the embedded devices to the device object.
	void addEmbeddedDevice(Element el, RootDevice dev) throws Exception {
		Vector<Element> v = el.getAllElements();
		try {
			//sets all the properties for the device.
			for (int ii = 0; ii < v.size(); ii++) {
				Element elm = v.elementAt(ii);
				String ename = elm.getName().trim();
				String eval = elm.getValue().trim();
				if (ename.equals("deviceType")) {
					dev.setDeviceType(eval);
				}
				else
					if (ename.equals("friendlyName")) {
						dev.setFriendlyName(eval);
					}
					else
						if (ename.equals("manufacturer")) {
							dev.setManufacturer(eval);
						}
						else
							if (ename.equals("manufacturerURL")) {
								dev.setManufacturerURL(eval);
							}
							else
								if (ename.equals("modelDescription")) {
									dev.setModelDescription(eval);
								}
								else
									if (ename.equals("modelName")) {
										dev.setModelName(eval);
									}
									else
										if (ename.equals("modelNumber")) {
											dev.setModelNumber(eval);
										}
										else
											if (ename.equals("modelURL")) {
												dev.setModelURL(eval);
											}
											else
												if (ename
														.equals("serialNumber")) {
													dev.setSerialNumber(eval);
												}
												else
													if (ename.equals("UDN")) {
														dev.setUDN(eval);
													}
													else
														if (ename.equals("UPC")) {
															dev.setUPC(eval);
														}
														//sets the values for
														// the icons.
														else
															if (ename
																	.equals("iconList")) {
																Vector<Element> v1 = getElementsByTagName(
																		"iconList");
																Element elment = v1
																		.elementAt(ctr);
																v1 = elment
																		.getAllElements();
																Icon iarr[] = new Icon[v1
																		.size()];
																for (int ictr = 0; ictr < iarr.length; ictr++) {
																	iarr[ictr] = new Icon();
																}
																for (int ictr = 0; ictr < v1
																		.size(); ictr++) {
																	elment = v1
																			.elementAt(ictr);
																	Vector<Element> allIcons = elment
																			.getAllElements();
																	for (int iconctr = 0; iconctr < allIcons
																			.size(); iconctr++) {
																		Element elmt = allIcons
																				.elementAt(iconctr);
																		String enam = elmt
																				.getName();
																		String eva = elmt
																				.getValue()
																				.trim();
																		if (enam
																				.equals("mimetype")) {
																			iarr[ictr]
																					.setMimeType(eva);
																		}
																		else
																			if (enam
																					.equals("width")) {
																				iarr[ictr]
																						.setWidth(Integer
																								.parseInt(eva));
																			}
																			else
																				if (enam
																						.equals("height")) {
																					iarr[ictr]
																							.setHeight(Integer
																									.parseInt(eva));
																				}
																				else
																					if (enam
																							.equals("depth")) {
																						iarr[ictr]
																								.setDepth(Integer
																										.parseInt(eva));
																					}
																					else
																						if (enam
																								.equals("url")) {
																							iarr[ictr]
																									.setURL(eva);
																						}
																	}
																}
																dev
																		.setIcons(iarr);
															}
															//sets the values
															// for the services
															// of the embedded
															// device.
															else
																if (ename
																		.equals("serviceList")) {
																	Vector<Element> v1 = getElementsByTagName(
																			"serviceList");
																	Element elment = v1
																			.elementAt(ctr + 1);
																	v1 = elment
																			.getAllElements();
																	ServiceInfo sarr[] = new ServiceInfo[v1
																			.size()];
																	for (int sctr = 0; sctr < sarr.length; sctr++) {
																		sarr[sctr] = new ServiceInfo();
																	}
																	for (int sctr = 0; sctr < v1
																			.size(); sctr++) {
																		elment = v1
																				.elementAt(sctr);
																		Vector<Element> vvec = elment
																				.getAllElements();
																		for (int servicctr = 0; servicctr < vvec
																				.size(); servicctr++) {
																			Element elmt = vvec
																					.elementAt(servicctr);
																			String enam = elmt
																					.getName();
																			String eva = elmt
																					.getValue();
																			if (enam
																					.equals("serviceType")) {
																				sarr[sctr]
																						.setServiceType(eva);
																			}
																			if (enam
																					.equals("serviceId")) {
																				sarr[sctr]
																						.setServiceID(eva);
																			}
																			if (enam
																					.equals("SCPDURL")) {
																				sarr[sctr]
																						.setSCPDURL(eva);
																			}
																			if (enam
																					.equals("controlURL")) {
																				sarr[sctr]
																						.setControlURL(eva);
																			}
																			if (enam
																					.equals("eventSubURL")) {
																				sarr[sctr]
																						.setEventSubURL(eva);
																			}
																		}
																	}
																	dev
																			.setServices(
																					sarr,
																					rootDevice);
																}
																// checks for
																// embedded
																// device of a
																// device.
																else
																	if (ename
																			.equals("deviceList")) {
																		Vector<Element> v1 = getElementsByTagName(
																				"deviceList");
																		Element ee1 = null;
																		ee1 = v1
																				.elementAt(++ctr);
																		Vector<Element> v2 = ee1
																				.getAllElements();
																		RootDevice eDev[] = new RootDevice[v2
																				.size()];
																		if (v2
																				.size() == 0) {
																			eDev = null;
																		}
																		for (int edctr = 0; edctr < v2
																				.size(); edctr++) {
																			Element ee2 = v2
																					.elementAt(edctr);
																			eDev[edctr] = new RootDevice(
																					rootDevice,
																					bcc1);
																			addEmbeddedDevice(
																					ee2,
																					eDev[edctr]);
																		}
																		dev
																				.setEmbeddedDevices(eDev);
																	}
																	else
																		if (ename
																				.equals("presentationURL")) {
																			dev
																					.setPresentationURL(eval);
																		}
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// This method creates the ServiceDescription object and loads all the
	// details from the xml file.
	void distributeService() throws Exception {
		sdesc = new ServiceInfo();
		Vector<Attribute> attrs = rootElement.getAttributes();
		// identifies the root attributes.
		for (int ii = 0; ii < attrs.size(); ii++) {
			Attribute att = attrs.elementAt(ii);
			if (att.getName().trim().equals("xmlns")) {
				sdesc.setServiceAttribute(att.getName(), att.getValue());
			}
		}
		Vector<Element> v = getElementsByTagName("major");
		Element el = v.elementAt(0);
		sdesc.setMajor(Integer.parseInt(el.getValue()));
		v = getElementsByTagName("minor");
		el = v.elementAt(0);
		try {
			sdesc.setMinor(Integer.parseInt(el.getValue()));
		}
		catch (Exception e) {
			System.out.println("Minor value should be int");
		}
		// checks for all the actions.
		Vector<Element> v3 = getElementsByTagName("action");
		Action aarr[] = new Action[v3.size()];
		for (int j = 0; j < v3.size(); j++) {
			aarr[j] = new Action();
			Element el3 = v3.elementAt(j);
			Vector<Element> v4 = el3.getAllElements();
			for (int k = 0; k < v4.size(); k++) {
				Element el4 = v4.elementAt(k);
				if (el4.getName().equals("name")) {
					aarr[j].setName(el4.getValue());
				}
				else
					if (el4.getName().equals("argumentList")) {
						Vector<Element> v5 = el4.getAllElements();
						ArgumentList args[] = new ArgumentList[v5.size()];
						for (int argument = 0; argument < v5.size(); argument++) {
							args[argument] = new ArgumentList();
							Element el5 = v5.elementAt(argument);
							Vector<Element> v6 = el5.getAllElements();
							for (int argList = 0; argList < v6.size(); argList++) {
								Element el6 = v6.elementAt(argList);
								String ena = el6.getName();
								String eva = el6.getValue();
								if (ena.equals("name")) {
									args[argument].setName(eva);
								}
								else
									if (ena.equals("direction")) {
										args[argument].setDirection(eva);
									}
									else
										if (ena.equals("retval")) {
											args[argument].setReturnValue(eva);
										}
										else
											if (ena
													.equals("relatedStateVariable")) {
												args[argument]
														.setRelatedStateVariable(eva);
											}
							}
						}
						aarr[j].setArgumentList(args);
					}
			}
		}
		sdesc.setActions(aarr);
		// checks for all the state variables.
		Vector<Element> v2 = getElementsByTagName("stateVariable");
		StateVariable sv[] = new StateVariable[v2.size()];
		for (int j = 0; j < v2.size(); j++) {
			sv[j] = new StateVariable();
			Element el2 = v2.elementAt(j);
			Vector<Element> v4 = el2.getAllElements();
			Vector<Attribute> attsendEvents = el2.getAttributes();
			for (int se = 0; se < attsendEvents.size(); se++) {
				Attribute attr = attsendEvents.elementAt(se);
				if (attr.getName().trim().equals("sendEvents")) {
					sv[j].sendEvents(attr.getValue());
				}
			}
			for (int se = 0; se < v4.size(); se++) {
				Element el3 = v4.elementAt(se);
				String ena = el3.getName();
				String eva = el3.getValue();
				//System.out.println(ena + eva);
				if (ena.equals("name")) {
					sv[j].setName(eva);
				}
				else
					if (ena.equals("dataType")) {
						sv[j].setDataType(eva);
					}
					else
						if (ena.equals("defaultValue")) {
							sv[j].setDefaultValue(eva);
						}
						else
							if (ena.equals("allowedValueList")) {
								Vector<String> v6 = new Vector<>();
								Vector<Element> v5 = el3.getAllElements();
								for (int k1 = 0; k1 < v5.size(); k1++) {
									Element el4 = v5.elementAt(k1);
									if (el4.getName().equals("allowedValue")) {
										String allowedVal = el4.getValue();
										v6.addElement(allowedVal);
									}
								}
								sv[j].setAllowedValueList(v6);
							}
							else
								if (ena.equals("allowedValueRange")) {
									@SuppressWarnings("unused")
									Vector<Element> v5 = el3.getAllElements();
									/*
									 * for(int k1=0;k1 <v5.size();k1++){ Element
									 * el4 = (Element) v5.elementAt(k1); String
									 * allowedrange = el4.getName();
									 * if(allowedrange.equals("maximum")){
									 * String maxval = el4.getValue().trim();
									 * Number nor ; if(maxval.indexOf('.')!=-1){
									 * nor = Long.valueOf(maxval); } else {
									 * if(maxval != null){ nor = new
									 * Double(maxval); }else{ nor = null; } }
									 * sv[j].setMaximum(nor); } else
									 * if(allowedrange.equals("minimum")){
									 * String minval = el4.getValue().trim();
									 * Number nor ; if(minval.indexOf('.')!=-1){
									 * nor = Long.valueOf(minval); } else { nor =
									 * Double.valueOf(minval); }
									 * sv[j].setMinimum(nor); } else
									 * if(allowedrange.equals("step")){ String
									 * stepval = el4.getValue().trim(); Number
									 * nor ; if(stepval.indexOf('.')!=-1){ nor =
									 * Long.valueOf(stepval); } else { nor = new
									 * Double(stepval); } sv[j].setStep(nor); } }
									 */
								}
			}
		}
		sdesc.setStateVariable(sv);
	}

	// This method adds the attributes for the element.
	void attachAttribs(String aval, Vector<Attribute> attributes) {
		int ptr = aval.indexOf('=');
		Attribute attr = new Attribute(aval.substring(0, ptr), aval.substring(
				ptr + 2, aval.length()));
		attributes.addElement(attr);
	}

	// This method creates a tree of all the child elements based on the given
	// xml document.
	void distribute() throws Exception {
		try {
			Vector<Element> dict = new Vector<>();
			Vector<Attribute> attributes = new Vector<>();
			StringTokenizer st1;
			String strValue = "";
			String atts = "";
			String obj[] = element.elementAt(0);
			String strVal = "";
			strVal = obj[0];
			atts = obj[1];
			if (atts.length() > 1) { // Attaches the root attribute.
				String key = "";
				int ptr = 0;
				int ssiz = atts.length();
				boolean fflag = false;
				char mainch = ' ';
				while (ptr < ssiz) {
					char ch = atts.charAt(ptr);
					if (ch == '\'' || ch == '\"') {
						if (mainch == ch) {
							mainch = ' ';
							attachAttribs(key, attributes);
							key = "";
							fflag = true;
						}
						else {
							mainch = ch;
						}
					}
					if (fflag == false) {
						key = key + ch;
					}
					if (fflag) {
						fflag = false;
					}
					ptr++;
				}
			}
			st1 = new StringTokenizer(strVal);
			strVal = st1.nextToken();
			while (st1.hasMoreTokens()) {
				strValue = strValue + " " + st1.nextToken();
			}
			//adds every element to the DOM tree.
			strValue = strValue.trim();
			strVal = strVal.trim();
			rootElement = new Element(strVal, strValue);
			rootElement.setAttributes(attributes);
			dict.addElement(rootElement);
			boolean disFlag = false;
			for (int i = 1; i < element.size(); i++) {
				disFlag = true;
				strValue = "";
				attributes = new Vector<>();
				obj = element.elementAt(i);
				strVal = obj[0];
				atts = obj[1];
				if (atts.length() > 1) {
					String key = "";
					int ptr = 0;
					int ssiz = atts.length();
					boolean fflag = false;
					char mainch = ' ';
					// Identifies the attribute.
					while (ptr < ssiz) {
						char ch = atts.charAt(ptr);
						if (ch == '\'' || ch == '\"') {
							if (mainch == ch) {
								mainch = ' ';
								attachAttribs(key, attributes);
								key = "";
								fflag = true;
							}
							else {
								mainch = ch;
							}
						}
						if (fflag == false) {
							key = key + ch;
						}
						if (fflag) {
							fflag = false;
						}
						ptr++;
					}
				}
				// Identifies the element and value.
				st1 = new StringTokenizer(strVal, "^");
				strVal = st1.nextToken();
				while (st1.hasMoreTokens()) {
					strValue = strValue + "" + st1.nextToken();
				}
				Element prevE = dict.elementAt(dict.size() - 1);
				if (strVal.charAt(strVal.length() - 1) == '/') {
					Element elem = new Element(strVal);
					prevE.addOneMoreElement(elem, attributes);
				}
				else {
					//adds the current element to the previous element.
					if (strVal.length() > 3) {
						if (strVal.substring(0, 3).equals(">>>")) {
							prevE
									.setValue(obj[0].substring(3, obj[0]
											.length()));
							disFlag = false;
						}
					}
					if (disFlag) {
						Element elem = new Element(strVal, strValue);
						if (strVal.charAt(0) == '/') {
							dict.removeElementAt(dict.size() - 1);
						}
						else {
							dict.addElement(elem);
							prevE.addOneMoreElement(elem, attributes);
						}
					}
				}
			}
		}
		catch (Exception e) {
			System.out.println("Invalid XML Format");
		}
	}

	// This method eliminates the white space, tab chars, new line and other
	// escape sequences.
	boolean move(String cont) {
		char value = ' ';
		boolean myFlag = false;
		while (myFlag == false) {
			if (xmlPtr == cont.length()) {
				this.flag = false;
				myFlag = true;
				break;
			}
			value = cont.charAt(xmlPtr);
			if (value == ' ' || value == '\n' || value == '\r' || value == '\f'
					|| value == '\t') {
				xmlPtr++;
			}
			else {
				myFlag = true;
				break;
			}
		}
		return myFlag;
	}

	// This method checks for the comment in the element.
	boolean checkComment(String cont) {
		try {
			if (cont.substring(xmlPtr, xmlPtr + 4).equals("<!--")) {
				return true;
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	// This method reads the starting element of the xml Element.
	String readStartElement(String cont) {
		char value = cont.charAt(xmlPtr);
		@SuppressWarnings("hiding")
		String selement = "";
		String attr = "";
		@SuppressWarnings({
				"hiding", "unused"
		})
		boolean flag = false;
		boolean att = false;
		String comt = "";
		boolean commt = checkComment(cont);
		boolean cmt = true;
		// identifies if there is a comment.
		if (commt) {
			while (cmt) {
				comt = comt + value;
				value = cont.charAt(xmlPtr);
				if (value == '-') {
					if (cont.charAt(xmlPtr + 1) == '-'
							&& cont.charAt(xmlPtr + 2) == '>') {
						xmlPtr += 3;
						cmt = false;
						break;
					}
				}
				xmlPtr++;
			}
			comt = comt.substring(2, comt.length());
			move(cont);
			readStartElement(cont);
		}
		else {
			// reads the start element value.
			if (value == '<') {
				if (cont.charAt(xmlPtr + 1) == '/') {
					readEndElement(cont);
				}
				else {
					while (value != '>') {
						xmlPtr++;
						value = cont.charAt(xmlPtr);
						if (value == ' ' && att == false) {
							att = true;
						}
						if (value == '/' && cont.charAt(xmlPtr + 1) == '>') {
							selement = selement.trim();
							selement = selement + value;
							xmlPtr += 2;
							break;
						}
						if (value != '>') {
							if (att == true) {
								attr = attr + value;
							}
							else {
								selement = selement + value;
							}
						}
					}
					xmlPtr++;
				}
			}
			else {
				@SuppressWarnings("unused")
				String tmpString = readElementValue(cont);
				flag = true;
			}
			flag = move(cont);
			if (selement.length() >= 1) {
				if (selement.charAt(0) == '/') {
					endElement(selement);
				}
				else {
					String sele[] = new String[2];
					sele[1] = attr;
					sele[0] = selement;
					element.addElement(sele);
				}
			}
		}
		return selement;
	}

	// This method reads the element value of the xml Element.
	String readElementValue(String cont) {
		char value = cont.charAt(xmlPtr);
		String commt = "";
		String elemen = "";
		while (value != '<') {
			value = cont.charAt(xmlPtr);
			if (value == '<' && cont.charAt(xmlPtr + 1) == '!'
					&& cont.charAt(xmlPtr + 2) == '-'
					&& cont.charAt(xmlPtr + 3) == '-') {
				xmlPtr += 4;
				value = cont.charAt(xmlPtr);
				while (value != '-') { // Fetches the comment if there are any.
					while (cont.charAt(xmlPtr + 1) != '-') {
						while (cont.charAt(xmlPtr + 2) != '>') {
							commt = commt + value;
							xmlPtr++;
							value = cont.charAt(xmlPtr);
						}
					}
				}
				xmlPtr += 3;
				move(cont);
				value = cont.charAt(xmlPtr);
			}
			if (value != '<' && value != '>') {
				elemen = elemen + value;
			}
			xmlPtr++;
		}
		//adds the element to the dictionary.
		xmlPtr--;
		move(cont);
		elemVal = elemen;
		int siz = element.size() - 1;
		String sa[] = element.elementAt(siz);
		String sval = sa[0];
		if (sval.equals(ended)) {
			String sar[] = new String[2];
			sar[0] = ">>>" + elemen;
			sar[1] = sa[1];
			element.addElement(sar);
		}
		else {
			String sar[] = new String[2];
			sar[0] = sval + "^" + elemVal;
			sar[1] = sa[1];
			element.removeElementAt(siz);
			element.addElement(sar);
		}
		readEndElement(cont);
		return elemen;
	}

	// This method returns the ending element name.
	void readEndElement(String cont) {
		char value = cont.charAt(xmlPtr);
		String eelement = "";
		move(cont);
		xmlPtr++;
		if (!(cont.charAt(xmlPtr) == '/')) {
			xmlPtr--;
			readStartElement(cont);
		}
		else {
			while (value != '>') {
				xmlPtr++;
				value = cont.charAt(xmlPtr);
				if (value != '>') {
					eelement = eelement + value;
				}
			}
			xmlPtr++;
			if (eelement.equals(rootE)) {
				flag = false;
			}
			else {
				move(cont);
			}
			String sar[] = new String[2];
			sar[0] = "/" + eelement;
			sar[1] = "";
			element.addElement(sar);
			ended = "/" + eelement;
		}
	}

	// This method receives the ending element name.
	public void endElement(String ele) {
		String sar[] = new String[2];
		sar[0] = "/" + ele;
		sar[1] = "";
		element.addElement(sar);
		ended = sar[0];
	}

	// This method returns all the Elements of the xml file which matches the
	// given tag name
	public Vector<Element> getElementsByTagName(String tag) {
		Vector<Element> elements = new Vector<>();
		if (rootElement.getName().equals(tag)) {
			elements.addElement(rootElement);
		}
		else {
			if (rootElement.hasMoreElements()) {
				Vector<Element> v = rootElement.getAllElements();
				for (int size = 0; size < v.size(); size++) {
					Element ele = v.elementAt(size);
					String snam = ele.getName();
					if (snam.charAt(snam.length() - 1) == '/') {
						snam = snam.substring(0, snam.length() - 1);
					}
					if (snam.equals(tag)) {
						elements.addElement(ele);
					}
					checkElement(ele, elements, tag);
				}
			}
		}
		return elements;
	}

	// This method checks for the matching tag name in the loop. And which ever
	// tag matches it adds
	// it to the vector object.
	void checkElement(Element elmnt, Vector<Element> elements, String tag) {
		if (elmnt.hasMoreElements()) {
			Vector<Element> vele = elmnt.getAllElements();
			for (int size = 0; size < vele.size(); size++) {
				Element el = vele.elementAt(size);
				String snam = el.getName();
				if (snam.charAt(snam.length() - 1) == '/') {
					snam = snam.substring(0, snam.length() - 1);
				}
				if (snam.equals(tag)) {
					elements.addElement(el);
				}
				checkElement(el, elements, tag);
			}
		}
	}

	// This method returns the root element of the xml document.
	public Element getRootElement() {
		return rootElement;
	}

	// This method returns the device details.
	public RootDevice getRootDevice() {
		return rootDevice;
	}

	// This method returns the service details.
	public ServiceInfo getServiceInfo() {
		return sdesc;
	}

	// This method returns the type of the xml document, whether it is of type
	// device or service.
	public String getType() {
		if (rootDevice != null) {
			return "Device";
		}
		else
			if (sdesc != null) {
				return "Service";
			}
			else {
				return "None";
			}
	}
}

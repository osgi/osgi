package org.osgi.service.onem2m.impl.serialization.xml;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.osgi.service.onem2m.dto.NotificationDTO;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.osgi.service.onem2m.impl.serialization.BaseSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XmlSerialize implements BaseSerialize{

	private static final Logger LOGGER = LoggerFactory.getLogger(XmlSerialize.class);

	@Override
	public ResourceDTO responseToResource(Object orgXml) throws Exception {
		LOGGER.info("Start xmlToResource");
		String xml = (String) orgXml;

		ResourceDTO res = new ResourceDTO();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(xml)));
		NodeList cbNodes = doc.getElementsByTagName("m2m:cb");
		Element element = (Element) cbNodes.item(0);

		res.resourceName = element.getAttribute("rn");
		res.resourceType = Integer.parseInt(element.getElementsByTagName("ty").item(0).getFirstChild().getTextContent());
		res.resourceID = element.getElementsByTagName("ri").item(0).getFirstChild().getTextContent();
		res.creationTime = element.getElementsByTagName("ct").item(0).getFirstChild().getTextContent();
		res.lastModifiedTime = element.getElementsByTagName("lt").item(0).getFirstChild().getTextContent();

		res.attribute = new HashMap<String, Object>();

		List<String> acpi = new ArrayList<String>();
		acpi.add(element.getElementsByTagName("acpi").item(0).getFirstChild().getTextContent());
		res.setAttribute("AccessControlPolicyIDs", acpi);

		res.setAttribute("cseType", element.getElementsByTagName("cst").item(0).getFirstChild().getTextContent());
		res.setAttribute("CSE-ID", element.getElementsByTagName("csi").item(0).getFirstChild().getTextContent());
		res.setAttribute("supportedResourceType", element.getElementsByTagName("srt").item(0).getFirstChild().getTextContent());

		List<String> poa = new ArrayList<String>();
		poa.add(element.getElementsByTagName("poa").item(0).getFirstChild().getTextContent());
		res.setAttribute("pointOfAccess", poa);

		LOGGER.info("End xmlToResource");
		return res;
	}

	@Override
	public Object resourceToRequest(ResourceDTO dto) throws Exception {
		LOGGER.warn("Not implemented.");
		return null;
	}

	@Override
	public Object notificationToRequest(NotificationDTO dto) throws Exception {
		LOGGER.warn("Not implemented.");
		return null;
	}

}

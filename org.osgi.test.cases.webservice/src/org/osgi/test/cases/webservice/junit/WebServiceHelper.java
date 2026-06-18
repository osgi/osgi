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
package org.osgi.test.cases.webservice.junit;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * This helper class creates SOAP messages and extracts SOAP responses
 */
public class WebServiceHelper {

	public static final String SOAP_NS = "http://schemas.xmlsoap.org/soap/envelope/";
	
	/**
	 * Create a SOAP message for the supplied namespace, root element name, and child parameters
	 * @param namespaceURI
	 * @param rootElement
	 * @param childElements  a map where the keys will be used as element names and the values
	 * as the text content of those elements
	 * @return the SOAP message
	 * @throws Exception
	 */
	public static String createSOAPMessage(String namespaceURI, String rootElement, Map<String, String> childElements) throws Exception {
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document message = dbf.newDocumentBuilder().newDocument();
		
		Element envelope = message.createElementNS(SOAP_NS, "soap:Envelope");
		envelope.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:ws", namespaceURI);
		message.appendChild(envelope);
		
		Element body = message.createElementNS(SOAP_NS, "soap:Body");
		envelope.appendChild(body);
		
		Element request = message.createElementNS(namespaceURI, "ws:" + rootElement);
		body.appendChild(request);
		
		childElements.entrySet().stream()
			.map(e -> {
				Element element = message.createElement(e.getKey());
				element.setTextContent(String.valueOf(e.getValue()));
				return element;
			})
			.forEach(request::appendChild);
		
		
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer trans = tf.newTransformer();
		StringWriter sw = new StringWriter();
		trans.transform(new DOMSource(message), new StreamResult(sw));
		return sw.toString();
	}

	/**
	 * Submit a SOAP message to the supplied uri
	 * @param uri
	 * @param soapMessage
	 * @return
	 * @throws Exception
	 */
	public static String getSoapResponse(String uri, String soapMessage) throws Exception {
		URL url = new URL(uri);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.addRequestProperty("Content-Type", "text/xml;charset=utf-8");
		conn.addRequestProperty("Accept", "text/xml;charset=utf-8");
		byte[] bytes = soapMessage.getBytes(UTF_8);
		conn.addRequestProperty("Content-Length", Integer.toString(bytes.length));
		conn.getOutputStream().write(bytes);
		
		assertEquals(200, conn.getResponseCode());
		
		StringBuilder sb = new StringBuilder();
		try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), UTF_8))) {
			String line;
			while((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}
		}
		return sb.toString();
	}

	/**
	 * Get the actual response from a SOAP response 
	 * @param responseName
	 * @param xmlResponse
	 * @return
	 * @throws XPathExpressionException
	 */
	public static String extractResponse(String responseName, String xmlResponse) throws XPathExpressionException {
		XPathExpression xpath = XPathFactory.newInstance().newXPath().compile(
				"/*[local-name() = 'Envelope']/*[local-name() = 'Body']/*[local-name() = '" 
				+ responseName + "']/*[local-name() = 'return']");
		
		return xpath.evaluate(new InputSource(new StringReader(xmlResponse)));
	}
}

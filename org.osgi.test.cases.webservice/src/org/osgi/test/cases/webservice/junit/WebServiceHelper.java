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

public class WebServiceHelper {

	public static final String SOAP_NS = "http://schemas.xmlsoap.org/soap/envelope/";
	
	public static String createSOAPMessage(String namespaceURI, String rootElement, Map<String, Object> childElements) throws Exception {
		
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

	public static String extractResponse(String responseName, String xmlResponse) throws XPathExpressionException {
		XPathExpression xpath = XPathFactory.newInstance().newXPath().compile(
				"/*[local-name() = 'Envelope']/*[local-name() = 'Body']/*[local-name() = '" 
				+ responseName + "']/*[local-name() = 'return']");
		
		return xpath.evaluate(new InputSource(new StringReader(xmlResponse)));
	}
}

package org.osgi.test.cases.xml.tb3;

import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

import org.osgi.framework.Constants;
import org.osgi.util.xml.XMLParserActivator;

import junit.framework.TestCase;

public class CustomXMLParserActivator extends XMLParserActivator {
	public void setSAXProperties(SAXParserFactory factory,
			Hashtable<String,Object> props) {
		if ((factory instanceof SimpleSAXParserFactory) == false) {
			TestCase.fail(
					"factory is not a SimpleSAXParserFactory");
		}
		super.setSAXProperties(factory, props);
		props.put(Constants.SERVICE_DESCRIPTION, "A simple SAX parser");
		props.put("marsupial", "kangaroo");
	}

	public void setDOMProperties(DocumentBuilderFactory factory,
			Hashtable<String,Object> props) {
		if ((factory instanceof SimpleDocumentBuilderFactory) == false) {
			TestCase.fail(
					"factory is not a SimpleDocumentBuilderFactory");
		}
		super.setDOMProperties(factory, props);
		props.put(Constants.SERVICE_DESCRIPTION, "A simple DOM parser");
		props.put("marsupial", "koala");
	}
}

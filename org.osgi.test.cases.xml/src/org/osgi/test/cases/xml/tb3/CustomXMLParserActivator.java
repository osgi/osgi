package org.osgi.test.cases.xml.tb3;

import java.util.Hashtable;
import javax.xml.parsers.*;
import org.osgi.framework.Constants;
import org.osgi.test.cases.util.AssertionFailedError;
import org.osgi.util.xml.XMLParserActivator;

public class CustomXMLParserActivator extends XMLParserActivator {
	public CustomXMLParserActivator() {
	}

	public void setSAXProperties(SAXParserFactory factory, Hashtable props) {
		if ((factory instanceof SimpleSAXParserFactory) == false) {
			throw new AssertionFailedError(
					"factory is not a SimpleSAXParserFactory");
		}
		super.setSAXProperties(factory, props);
		props.put(Constants.SERVICE_DESCRIPTION, "A simple SAX parser");
		props.put("marsupial", "kangaroo");
	}

	public void setDOMProperties(DocumentBuilderFactory factory, Hashtable props) {
		if ((factory instanceof SimpleDocumentBuilderFactory) == false) {
			throw new AssertionFailedError(
					"factory is not a SimpleDocumentBuilderFactory");
		}
		super.setDOMProperties(factory, props);
		props.put(Constants.SERVICE_DESCRIPTION, "A simple DOM parser");
		props.put("marsupial", "koala");
	}
}

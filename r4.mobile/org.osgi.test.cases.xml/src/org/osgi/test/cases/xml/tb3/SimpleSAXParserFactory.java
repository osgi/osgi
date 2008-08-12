package org.osgi.test.cases.xml.tb3;

import javax.xml.parsers.*;
import org.xml.sax.*;

public class SimpleSAXParserFactory extends SAXParserFactory {
	public SimpleSAXParserFactory() {
	}

	public SAXParser newSAXParser() throws ParserConfigurationException,
			SAXException {
		return null;
	}

	public boolean getFeature(String s) throws ParserConfigurationException,
			SAXNotRecognizedException, SAXNotSupportedException {
		throw new SAXNotRecognizedException(s);
	}

	public void setFeature(String s, boolean b)
			throws ParserConfigurationException, SAXNotRecognizedException,
			SAXNotSupportedException {
		throw new SAXNotRecognizedException(s);
	}
}

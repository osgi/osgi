package org.osgi.test.cases.xml.tb3;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class SimpleSAXParserFactory extends SAXParserFactory {
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

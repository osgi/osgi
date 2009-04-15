package org.osgi.test.cases.xml.tb3;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class SimpleDocumentBuilderFactory extends DocumentBuilderFactory {
	public DocumentBuilder newDocumentBuilder()
			throws ParserConfigurationException {
		return null;
	}

	public Object getAttribute(String s) throws IllegalArgumentException {
		throw new IllegalArgumentException(s);
	}

	public void setAttribute(String s, Object o)
			throws IllegalArgumentException {
		throw new IllegalArgumentException(s);
	}
}

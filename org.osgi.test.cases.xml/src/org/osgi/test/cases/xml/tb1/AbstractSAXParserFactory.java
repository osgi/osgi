package org.osgi.test.cases.xml.tb1;

import javax.xml.parsers.*;
import org.xml.sax.SAXNotRecognizedException;

abstract class AbstractSAXParserFactory extends SAXParserFactory {
	private final boolean	m_validating;
	private final boolean	m_namespaceAware;

	protected AbstractSAXParserFactory(boolean validating,
			boolean namespaceAware) {
		m_validating = validating;
		m_namespaceAware = namespaceAware;
	}

	public boolean getFeature(String name) throws SAXNotRecognizedException {
		throw new SAXNotRecognizedException(name);
	}

	public void setFeature(String name, boolean value)
			throws SAXNotRecognizedException {
		throw new SAXNotRecognizedException(name);
	}

	public SAXParser newSAXParser() throws ParserConfigurationException {
		if (isValidating() && (m_validating == false)) {
			throw new ParserConfigurationException();
		}
		if (isNamespaceAware() && (m_namespaceAware == false)) {
			throw new ParserConfigurationException();
		}
		return null;
	}
}

/*
 * Copyright (c) IBM Corporation (2005)
 *
 * These materials have been contributed  to the OSGi Alliance as 
 * "MEMBER LICENSED MATERIALS" as defined in, and subject to the terms of, 
 * the OSGi Member Agreement, specifically including but not limited to, 
 * the license rights and warranty disclaimers as set forth in Sections 3.2 
 * and 12.1 thereof, and the applicable Statement of Work. 
 *
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.
 */

package org.eclipse.osgi.component.parser;

import java.util.List;
import org.eclipse.osgi.component.Main;
import org.eclipse.osgi.component.model.ComponentDescription;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkEvent;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

/**
 * ParserHandler implements the methods for the DefaultHandler of the SAX
 * parser. Each Service Component bundle contains a set of xml files which are parsed.
 * 
 * @version $Revision$
 */

public class ParserHandler extends DefaultHandler {

	/* set this to true to compile in debug messages */
	static final boolean DEBUG = false;

	protected DefaultHandler handler;
	protected List components;
	protected Main main;
	protected Bundle bundle;
	protected int depth;
	protected boolean error;

	public ParserHandler(Main main, Bundle bundle, List components) {
		this.main = main;
		this.bundle = bundle;
		this.components = components;
	}

	public void setHandler(DefaultHandler handler) {
		this.handler = handler;
	}

	public void addComponentDescription(ComponentDescription component) {
		components.add(component);
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public boolean isError() {
		return error;
	}

	public void logError(String msg) {
		error = true;
		main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, bundle, new SAXException(msg));
	}

	public void startDocument() throws SAXException {
		handler = this;
		depth = 0;
	}

	public void startPrefixMapping(String prefix, String uri) throws SAXException {

		if (DEBUG) {
			System.out.println("[startPrefixMapping:prefix]" + prefix);
			System.out.println("[startPrefixMapping:uri]" + uri);
		}
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		depth++;

		if (DEBUG) {
			System.out.println("[startElement:begin]");
			System.out.println(" [uri]" + uri);
			System.out.println(" [localName]" + localName);
			System.out.println(" [qName]" + qName);

			int size = attributes.getLength();
			for (int i = 0; i < size; i++) {
				String key = attributes.getQName(i);
				String value = attributes.getValue(i);
				System.out.println(" [attr:" + i + ":localName]" + attributes.getLocalName(i));
				System.out.println(" [attr:" + i + ":qName]" + attributes.getQName(i));
				System.out.println(" [attr:" + i + ":type]" + attributes.getType(i));
				System.out.println(" [attr:" + i + ":URI]" + attributes.getURI(i));
				System.out.println(" [attr:" + i + ":value]" + attributes.getValue(i));
			}
			System.out.println("[startElement:end]");
		}

		if (handler != this) {
			handler.startElement(uri, localName, qName, attributes);
			return;
		}

		if (localName.equals(ParserConstants.COMPONENT_ELEMENT)) {
			if (((depth == 1) && (uri.length() == 0)) || uri.equals(ParserConstants.SCR_NAMESPACE)) {
				setHandler(new ComponentElement(this, attributes));
			}
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {

		if (DEBUG) {
			System.out.print("[characters:begin]");
			System.out.print(new String(ch, start, length));
			System.out.println("[characters:end]");
		}

		if (handler != this) {
			handler.characters(ch, start, length);
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (DEBUG) {
			System.out.println("[endElement:uri]" + uri);
			System.out.println("[endElement:localName]" + localName);
			System.out.println("[endElement:qName]" + qName);
		}

		if (handler != this) {
			handler.endElement(uri, localName, qName);
		}

		depth--;
	}

	public void endPrefixMapping(String prefix) throws SAXException {
		if (DEBUG) {
			System.out.println("[endPrefixMapping:prefix]" + prefix);
		}
	}

	public void endDocument() throws SAXException {
		if (DEBUG) {
			System.out.println("[endDocument]");
		}
	}

	public void warning(SAXParseException e) throws SAXException {
		if (DEBUG) {
			System.out.println("[warning]");
			e.printStackTrace();
		}
	}

	public void error(SAXParseException e) throws SAXException {
		if (DEBUG) {
			System.out.println("[error]");
			e.printStackTrace();
		}
	}

	public void fatalError(SAXParseException e) throws SAXException {
		if (DEBUG) {
			System.out.println("[fatalError]");
			e.printStackTrace();
		}
		throw e;
	}
}

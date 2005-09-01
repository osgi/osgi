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

import org.eclipse.osgi.component.model.ComponentDescription;
import org.eclipse.osgi.component.model.PropertyResourceDescription;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class PropertiesElement extends DefaultHandler {
	protected ParserHandler					root;
	protected ComponentElement				parent;
	protected PropertyResourceDescription	properties;

	public PropertiesElement(ParserHandler root, ComponentElement parent,
			Attributes attributes) {
		this.root = root;
		this.parent = parent;
		properties = new PropertyResourceDescription(parent
				.getComponentDescription());

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);

			if (key.equals(ParserConstants.ENTRY_ATTRIBUTE)) {
				properties.setEntry(value);
				continue;
			}
			root.logError("unrecognized properties element attribute: " + key);
		}

		if (properties.getEntry() == null) {
			root.logError("properties entry not specified");
		}
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		root.logError("properties does not support nested elements");
	}

	public void characters(char[] ch, int start, int length) {
		int end = start + length;
		for (int i = start; i < end; i++) {
			if (!Character.isWhitespace(ch[i])) {
				root.logError("element body must be empty");
			}
		}
	}

	public void endElement(String uri, String localName, String qName) {
		ComponentDescription component = parent.getComponentDescription();
		component.addProperty(properties);
		root.setHandler(parent);
	}
}
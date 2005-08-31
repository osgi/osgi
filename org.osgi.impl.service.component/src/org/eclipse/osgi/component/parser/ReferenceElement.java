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
import org.eclipse.osgi.component.model.ReferenceDescription;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class ReferenceElement extends DefaultHandler {
	protected ParserHandler root;
	protected ComponentElement parent;
	protected ReferenceDescription reference;

	public ReferenceElement(ParserHandler root, ComponentElement parent, Attributes attributes) {
		this.root = root;
		this.parent = parent;
		reference = new ReferenceDescription(parent.getComponentDescription());

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);

			if (key.equals(ParserConstants.NAME_ATTRIBUTE)) {
				reference.setName(value);
				continue;
			}

			if (key.equals(ParserConstants.INTERFACE_ATTRIBUTE)) {
				reference.setInterfacename(value);
				continue;
			}

			if (key.equals(ParserConstants.CARDINALITY_ATTRIBUTE)) {
				reference.setCardinality(value);
				continue;
			}

			if (key.equals(ParserConstants.POLICY_ATTRIBUTE)) {
				reference.setPolicy(value);
				continue;
			}

			if (key.equals(ParserConstants.TARGET_ATTRIBUTE)) {
				reference.setTarget(value);
				continue;
			}

			if (key.equals(ParserConstants.BIND_ATTRIBUTE)) {
				reference.setBind(value);
				continue;
			}

			if (key.equals(ParserConstants.UNBIND_ATTRIBUTE)) {
				reference.setUnbind(value);
				continue;
			}
			root.logError("unrecognized reference element attribute: " + key);
		}

		if (reference.getName() == null) {
			root.logError("reference name not specified");
		}
		if (reference.getInterfacename() == null) {
			root.logError("reference interface not specified");
		}
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		root.logError("reference does not support nested elements");
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
		component.addReferenceDescription(reference);
		root.setHandler(parent);
	}
}
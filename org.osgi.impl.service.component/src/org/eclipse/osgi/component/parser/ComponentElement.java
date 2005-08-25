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
import org.eclipse.osgi.component.model.PropertyValueDescription;
import org.osgi.service.component.ComponentConstants;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ComponentElement extends DefaultHandler {
	protected ParserHandler root;
	protected ParserHandler parent;
	protected ComponentDescription component;
	protected boolean immediateSet;

	public ComponentElement(ParserHandler root, Attributes attributes) {
		this.root = root;
		this.parent = root;
		component = new ComponentDescription(root.bundle);
		immediateSet = false;

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);

			if (key.equals(ParserConstants.NAME_ATTRIBUTE)) {
				component.setName(value);
				PropertyValueDescription nameProperty = new PropertyValueDescription(component);
				nameProperty.setName(ComponentConstants.COMPONENT_NAME);
				nameProperty.setValue(value);
				component.addProperty(nameProperty);
				continue;
			}
			if (key.equals(ParserConstants.ENABLED_ATTRIBUTE)) {
				component.setAutoenable(value.equalsIgnoreCase("true"));
				continue;
			}
			if (key.equals(ParserConstants.FACTORY_ATTRIBUTE)) {
				component.setFactory(value);
				continue;
			}
			if (key.equals(ParserConstants.IMMEDIATE_ATTRIBUTE)) {
				component.setImmediate(value.equalsIgnoreCase("true"));
				immediateSet = true;
				continue;
			}
			root.logError("unrecognized component element attribute: " + key);
		}

		if (component.getName() == null) {
			root.logError("component name not specified");
		}
	}

	public ComponentDescription getComponentDescription() {
		return component;
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (localName.equals(ParserConstants.IMPLEMENTATION_ELEMENT)) {
			root.setHandler(new ImplementationElement(root, this, attributes));
			return;
		}

		if (localName.equals(ParserConstants.PROPERTY_ELEMENT)) {
			root.setHandler(new PropertyElement(root, this, attributes));
			return;
		}

		if (localName.equals(ParserConstants.PROPERTIES_ELEMENT)) {
			root.setHandler(new PropertiesElement(root, this, attributes));
			return;
		}

		if (localName.equals(ParserConstants.SERVICE_ELEMENT)) {
			root.setHandler(new ServiceElement(root, this, attributes));
			return;
		}

		if (localName.equals(ParserConstants.REFERENCE_ELEMENT)) {
			root.setHandler(new ReferenceElement(root, this, attributes));
			return;
		}
		root.logError("unrecognized element of component: " + localName);
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
		
		// if unset, immediate attribute is false if service element is specified or true otherwise
		// except if component factory then immediate by default is false
		if (!immediateSet && (component.getFactory() == null)) {
			component.setImmediate(component.getService() == null);
		}
		
		if (component.getImplementation() == null) {
			root.logError("no implementation element");
		}

		if (root.isError()) {
			root.setError(false);
		} else {
			parent.addComponentDescription(component);
		}

		root.setHandler(parent);
	}
}
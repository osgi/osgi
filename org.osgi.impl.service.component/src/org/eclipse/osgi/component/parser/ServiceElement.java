/*
 * $Header$
 *
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
import org.eclipse.osgi.component.model.ServiceDescription;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class ServiceElement extends DefaultHandler {
	protected ParserHandler			root;
	protected ComponentElement		parent;
	protected ServiceDescription	service;

	public ServiceElement(ParserHandler root, ComponentElement parent,
			Attributes attributes) {
		this.root = root;
		this.parent = parent;
		service = new ServiceDescription(parent.getComponentDescription());

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);

			if (key.equals(ParserConstants.SERVICEFACTORY_ATTRIBUTE)) {
				service.setServicefactory(value.equalsIgnoreCase("true"));
				continue;
			}
			root.logError("unrecognized service element attribute: " + key);
		}
	}

	public ServiceDescription getServiceDescription() {
		return service;
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		if (localName.equals(ParserConstants.PROVIDE_ELEMENT)) {
			root.setHandler(new ProvideElement(root, this, attributes));
			return;
		}
		root.logError("unrecognized element of service: " + localName);
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
		if (component.getService() != null) {
			root.logError("more than one service element");
		}

		if (service.getProvides().length == 0) {
			root.logError("no provide elements specified");
		}

		if ((component.getFactory() != null) && service.isServicefactory()) {
			root
					.logError("component factory is incompatible with Service factory ");
		}

		component.setService(service);
		root.setHandler(parent);
	}
}
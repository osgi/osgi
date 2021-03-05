/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.impl.service.upnp.cp.description;

import java.util.StringTokenizer;
import java.util.Vector;

public class Element {
	private String	name;
	private String	value;
	private Vector<Element>		element;
	private Vector<Attribute>	attributes;

	Element() {
		element = new Vector<>();
		attributes = new Vector<>();
	}

	// This constructor creates the Element object using the name and the value
	// of the element.
	Element(String nam, String val) {
		this();
		this.name = nam;
		this.value = val;
	}

	// This constructor creates the Element object using the name of the
	// element.
	Element(String nam) {
		this();
		this.name = nam;
	}

	// This method sets the value for the element. And this method is called
	// when the comment is
	void setValue(String val) {
		value = value + " " + val;
	}

	// This method adds the attributes to the element.
	void addAttributes(String attrib) {
		StringTokenizer allAttributes = new StringTokenizer(attrib, "=");
		String key = "";
		String val = "";
		if (allAttributes.hasMoreTokens()) {
			key = allAttributes.nextToken();
		}
		if (allAttributes.hasMoreTokens()) {
			val = allAttributes.nextToken();
			Attribute attrb = new Attribute(key, val);
			attributes.addElement(attrb);
		}
	}

	// This method returns all the child elements of the current element.
	public Vector<Element> getAllElements() {
		return element;
	}

	// This method adds the child element to the current element.
	void addOneMoreElement(Element elem, Vector<Attribute> allAtts) {
		elem.setAttributes(allAtts);
		element.addElement(elem);
	}

	// This method checks whether the element has child elements or not.
	public boolean hasMoreElements() {
		if (element.size() >= 1) {
			return true;
		}
		return false;
	}

	// This method returns the name of the element.
	public String getName() {
		return name;
	}

	// This method returns the value of the element.
	public String getValue() {
		return value;
	}

	// This method returns all the attributes of the element.
	public Vector<Attribute> getAttributes() {
		return attributes;
	}

	// This method sets the attributes for the current element.
	void setAttributes(Vector<Attribute> atts) {
		attributes = atts;
	}

	// This method returns the count of the attributes in the current element.
	public int attributesSize() {
		return attributes.size();
	}

	// This method returns the count of the number of child elements to the
	// current element.
	public int elementsSize() {
		return element.size();
	}
}

package org.osgi.impl.service.upnp.cp.description;

import java.util.Vector;
import java.util.StringTokenizer;

public class Element {
	private String	name;
	private String	value;
	private Vector	element;
	private Vector	attributes;

	Element() {
		element = new Vector();
		attributes = new Vector();
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
	public Vector getAllElements() {
		return element;
	}

	// This method adds the child element to the current element.
	void addOneMoreElement(Element elem, Vector allAtts) {
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
	public Vector getAttributes() {
		return attributes;
	}

	// This method sets the attributes for the current element.
	void setAttributes(Vector atts) {
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

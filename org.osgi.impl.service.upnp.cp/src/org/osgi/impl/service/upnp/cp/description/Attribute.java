package org.osgi.impl.service.upnp.cp.description;

public class Attribute {
	private String	name;
	private String	value;

	Attribute(String nam, String val) {
		name = nam;
		value = val;
	}

	// This method returns the name of the current Attribute.
	public String getName() {
		return name;
	}

	// This method returns the value of the attribute.
	public String getValue() {
		return value;
	}
}
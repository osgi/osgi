/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.shared;

import java.io.Serializable;

/**
 * The ID class implements a logical representation/name of a java object. It
 * can be used to hand out to other processes. For this reason, it implements
 * equality/hashCode.
 */
public class ID implements Serializable {
	public final static long	serialVersionUID	= 1;
	String						_title;
	String						_name;
	String						_description;
	transient Object			_data;

	public ID(String name, String title, String description, Object data) {
		_name = name;
		_title = title;
		_description = description;
		_data = data;
	}

	public String getTitle() {
		return _title;
	}

	public String getName() {
		return _name;
	}

	public String getDescription() {
		return _description;
	}

	public void setDescription(String d) {
		_description = d;
	}

	public void setData(Object data) {
		_data = data;
	}

	public Object getData() {
		return _data;
	}

	public int hashCode() {
		return getTitle().hashCode();
	}

	public String toString() {
		return getName();
	}

	public boolean equals(Object o) {
		if (o instanceof ID) {
			ID id = (ID) o;
			return _name.equals(id._name);
		}
		if (o instanceof String) {
			String name = (String) o;
			return _name.equals(name);
		}
		return false;
	}
}

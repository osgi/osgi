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
package org.osgi.impl.service.metatype;

import org.osgi.framework.Bundle;

/**
 * Represents an Icon with a name and a size
 * 
 * @author	Julian Chen
 * @version 1.0
 */
class Icon implements Cloneable {

	private String	_fileName;
	private int		_size;
	private Bundle	_bundle;

	/**
	 * Constructor of class Icon.
	 */
	public Icon(String fileName, int size, Bundle bundle) {

		this._fileName = fileName;
		this._size = size;
		this._bundle = bundle;
	}

	/**
	 * Constructor of class Icon.
	 */
	public Icon(String fileName, Bundle bundle) {

		// Integer.MIN_VALUE signifies size was not specified
		this(fileName, Integer.MIN_VALUE, bundle);
	}

	/*
	 * 
	 */
	public synchronized Object clone() {
		return new Icon(this._fileName, this._size, this._bundle);
	}

	/**
	 * Method to get the icon's file name.
	 */
	String getIconName() {
		return _fileName;
	}

	/**
	 * returns the size specified when the icon was created
	 * 
	 * @return size or Integer.MIN_VALUE if no size was specified
	 */
	int getIconSize() {
		return _size;
	}

	/**
	 * Method to get the bundle having this Icon.
	 */
	Bundle getIconBundle() {
		return _bundle;
	}
}

/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.defaultadaptor;

import java.io.*;
import java.util.Properties;

/**
 * This class uses a Properties object to store and get MetaData information.  
 * All data is converted into String data before saving.
 */
public class MetaData {

	/**
	 * The Properties file to store the data.
	 */
	Properties properties = new Properties();

	/**
	 * The File object to store and load the Properties object.
	 */
	File datafile;

	/**
	 * The header string to use when storing data to the datafile.
	 */
	String header;

	/**
	 * Constructs a MetaData object that uses the datafile to persistently
	 * store data.
	 * @param datafile The File object used to persistently load and store data.
	 * @param header The header to use when storing data persistently.
	 */
	public MetaData(File datafile, String header) {
		this.datafile = datafile;
		this.header = header;
	}

	/**
	 * Gets the metadata value for the key.
	 * @param key the key of the metadata
	 * @param def the default value to return if the key does not exist
	 * @return the value of the metadata or null if the key does not exist
	 * and the specified default is null.
	 */
	public String get(String key, String def) {
		return properties.getProperty(key, def);
	}

	/**
	 * Gets the integer value for the key.
	 * @param key the key of the metadata
	 * @param def the default value to return if the key does not exist
	 * @return the value of the metadata; if the key does not exist or
	 * the value cannot be converted to an int value then the
	 * specified default value is returned.
	 */
	public int getInt(String key, int def) {
		String result = get(key, null);
		if (result == null) {
			return def;
		}
		try {
			return Integer.parseInt(result);
		} catch (NumberFormatException nfe) {
			return def;
		}
	}

	/**
	 * Gets the long value for the key.
	 * @param key the key of the metadata
	 * @param def the default value to return if the key does not exist
	 * @return the value of the metadata; if the key does not exist or
	 * the value cannot be converted to an long value then the
	 * specified default value is returned.
	 */
	public long getLong(String key, long def) {
		String result = get(key, null);
		if (result == null) {
			return def;
		}
		try {
			return Long.parseLong(result);
		} catch (NumberFormatException nfe) {
			return def;
		}
	}

	/**
	 * Gets the boolean value for the key.
	 * @param key the key of the metadata
	 * @param def the default value to return if the key does not exist
	 * @return the value of the metadata; if the key does not exist then the
	 * specified default value is returned.
	 */
	public boolean getBoolean(String key, boolean def) {
		String result = get(key, null);
		if (result == null) {
			return def;
		}
		return Boolean.valueOf(result).booleanValue();
	}

	/**
	 * Sets the String value for a key.
	 * @param key the key of the metadata
	 * @param val the value of the metadata
	 */
	public void set(String key, String val) {
		properties.put(key, val);
	}

	/**
	 * Sets the int value for a key.
	 * @param key the key of the metadata
	 * @param val the value of the metadata
	 */
	public void setInt(String key, int val) {
		properties.put(key, Integer.toString(val));
	}

	/**
	 * Sets the long value for a key.
	 * @param key the key of the metadata
	 * @param val the value of the metadata
	 */
	public void setLong(String key, long val) {
		properties.put(key, Long.toString(val));
	}

	/**
	 * Sets the boolean value for a key.
	 * @param key the key of the metadata
	 * @param val the value of the metadata
	 */
	public void setBoolean(String key, boolean val) {
		properties.put(key, new Boolean(val).toString());
	}

	/**
	 * Removes the metadata value with the specified key.
	 * @param key the key of the metadata to be removed
	 */
	public void remove(String key) {
		properties.remove(key);
	}

	/**
	 * Saves the metadata to persistent storage.
	 * @throws IOException if there is a problem saving to persistent storage.
	 */
	public void save() throws IOException {
		FileOutputStream fos = new FileOutputStream(datafile);
		try {
			properties.store(fos, header);
		} finally {
			fos.close();
		}
	}

	/**
	 * Loads the metadata from persistent storage
	 * @throws IOException if there is a problem reading from persistent storage.
	 */
	public void load() throws IOException {
		properties.clear();
		if (datafile.exists()) {
			FileInputStream fis = new FileInputStream(datafile);
			try {
				properties.load(fis);
			} finally {
				fis.close();
			}
		}
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return properties.toString();
	}

}
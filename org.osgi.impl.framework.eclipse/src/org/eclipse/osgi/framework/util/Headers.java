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

package org.eclipse.osgi.framework.util;

import java.io.*;
import java.util.*;
import org.eclipse.osgi.framework.internal.core.Msg;
import org.osgi.framework.BundleException;

/**
 * Headers classes. This class implements a Dictionary that has
 * the following behaviour:
 * <ul>
 * <li>put and remove clear throw UnsupportedOperationException.
 * The Dictionary is thus read-only to others.
 * <li>The String keys in the Dictionary are case-preserved,
 * but the get operation is case-insensitive.
 * </ul>
 */
public class Headers extends Dictionary {
	/**
	 * Dictionary of keys: Lower case key => Case preserved key.
	 */
	protected Dictionary headers;

	/**
	 * Dictionary of values: Case preserved key => value.
	 */
	protected Dictionary values;

	/**
	 * Create an empty Headers dictionary.
	 *
	 * @param initialCapacity The initial capacity of this Headers object.
	 */
	public Headers(int initialCapacity) {
		super();

		headers = new Hashtable(initialCapacity);
		values = new Hashtable(initialCapacity);
	}

	/**
	 * Create a Headers dictionary from a Dictionary.
	 *
	 * @param values The initial dictionary for this Headers object.
	 * @exception IllegalArgumentException If a case-variant of the key is
	 * in the dictionary parameter.
	 */
	public Headers(Dictionary values) {
		super();

		headers = new Hashtable(values.size());
		this.values = values;

		/* initialize headers dictionary */
		Enumeration keys = values.keys();

		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();

			if (key instanceof String) {
				String header = ((String) key).toLowerCase();

				if (headers.put(header, key) != null) /* if case-variant already present */
				{
					throw new IllegalArgumentException(Msg.formatter.getString("HEADER_DUPLICATE_KEY_EXCEPTION", header)); //$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * Case-preserved keys.
	 */
	public synchronized Enumeration keys() {
		return (values.keys());
	}

	/**
	 * Values.
	 */
	public synchronized Enumeration elements() {
		return (values.elements());
	}

	/**
	 * Support case-insensitivity for keys.
	 *
	 * @param key name.
	 */
	public synchronized Object get(Object key) {
		Object value = values.get(key);

		if ((value == null) && (key instanceof String)) {
			Object keyLower = headers.get(((String) key).toLowerCase());

			if (keyLower != null) {
				value = values.get(keyLower);
			}
		}

		return (value);
	}

	/**
	 * Set a header value.
	 *
	 * @param key Key name.
	 * @param value Value of the key or null to remove key.
	 * @return the previous value to which the key was mapped,
	 * or null if the key did not have a previous mapping.
	 *
	 * @exception IllegalArgumentException If a case-variant of the key is
	 * already present.
	 */
	public synchronized Object set(Object key, Object value) {
		String header = (key instanceof String) ? ((String) key).toLowerCase() : null;

		if (value == null) /* remove */
		{
			if (header != null) /* String key */
			{
				key = headers.remove(header);

				if (key != null) /* is String key in hashtable? */
				{
					value = values.remove(key);
				}
			} else /* non-String key */
			{
				value = values.remove(key);
			}

			return (value);
		} else /* put */
		{
			if (header != null) /* String key */
			{
				Object oldKey = headers.put(header, key);

				if ((oldKey != null) && !header.equals(oldKey)) /* if case-variant already present */
				{
					headers.put(header, oldKey); /* put old case-variant back */

					throw new IllegalArgumentException(Msg.formatter.getString("HEADER_DUPLICATE_KEY_EXCEPTION", header)); //$NON-NLS-1$
				}

			}

			return (values.put(key, value));
		}

	}

	/**
	 * Returns the number of entries (distinct keys) in this dictionary.
	 *
	 * @return  the number of keys in this dictionary.
	 */
	public synchronized int size() {
		return (values.size());
	}

	/**
	 * Tests if this dictionary maps no keys to value. The general contract
	 * for the <tt>isEmpty</tt> method is that the result is true if and only
	 * if this dictionary contains no entries.
	 *
	 * @return  <code>true</code> if this dictionary maps no keys to values;
	 *          <code>false</code> otherwise.
	 */
	public synchronized boolean isEmpty() {
		return (values.isEmpty());
	}

	/**
	 * Always throws UnsupportedOperationException.
	 *
	 * @param key header name.
	 * @param value header value.
	 * @throws UnsupportedOperationException
	 */
	public Object put(Object key, Object value) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Always throws UnsupportedOperationException.
	 *
	 * @param key header name.
	 * @throws UnsupportedOperationException
	 */
	public Object remove(Object key) {
		throw new UnsupportedOperationException();
	}

	public String toString() {
		return (values.toString());
	}

	public static Headers parseManifest(InputStream in) throws BundleException {
		try {
			Headers headers = new Headers(10);
			BufferedReader br;
			try {
				br = new BufferedReader(new InputStreamReader(in, "UTF8")); //$NON-NLS-1$
			} catch (UnsupportedEncodingException e) {
				br = new BufferedReader(new InputStreamReader(in));
			}

			String header = null;
			StringBuffer value = new StringBuffer(256);
			boolean firstLine = true;

			while (true) {
				String line = br.readLine();
				/* The java.util.jar classes in JDK 1.3 use the value of the last
				 * encountered manifest header. So we do the same to emulate
				 * this behavior. We no longer throw a BundleException
				 * for duplicate manifest headers.
				 */

				if ((line == null) || (line.length() == 0)) /* EOF or empty line */
				{
					if (!firstLine) /* flush last line */
					{
						headers.set(header, null); /* remove old attribute,if present */
						headers.set(header, value.toString().trim());
					}
					break; /* done processing main attributes */
				}

				if (line.charAt(0) == ' ') /* continuation */
				{
					if (firstLine) /* if no previous line */
					{
						throw new BundleException(Msg.formatter.getString("MANIFEST_INVALID_SPACE", line)); //$NON-NLS-1$
					}
					value.append(line.substring(1));
					continue;
				}

				if (!firstLine) {
					headers.set(header, null); /* remove old attribute,if present */
					headers.set(header, value.toString().trim());
					value.setLength(0); /* clear StringBuffer */
				}

				int colon = line.indexOf(':');
				if (colon == -1) /* no colon */
				{
					throw new BundleException(Msg.formatter.getString("MANIFEST_INVALID_LINE_NOCOLON", line)); //$NON-NLS-1$
				}
				header = line.substring(0, colon).trim();
				value.append(line.substring(colon + 1));
				firstLine = false;
			}
			return headers;
		} catch (IOException e) {
			throw new BundleException(Msg.formatter.getString("MANIFEST_IOEXCEPTION"), e); //$NON-NLS-1$
		} finally {
			try {
				in.close();
			} catch (IOException ee) {
			}
		}
	}
}
/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.util;

import java.io.*;
import java.util.Dictionary;
import java.util.Enumeration;
import org.eclipse.osgi.framework.internal.core.Msg;
import org.eclipse.osgi.util.NLS;
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
	Object[] headers;
	Object[] values;
	int size = 0;

	/**
	 * Create an empty Headers dictionary.
	 *
	 * @param initialCapacity The initial capacity of this Headers object.
	 */
	public Headers(int initialCapacity) {
		super();
		headers = new Object[initialCapacity];
		values = new Object[initialCapacity];
	}

	/**
	 * Create a Headers dictionary from a Dictionary.
	 *
	 * @param values The initial dictionary for this Headers object.
	 * @exception IllegalArgumentException If a case-variant of the key is
	 * in the dictionary parameter.
	 */
	public Headers(Dictionary values) {
		this(values.size());
		/* initialize the headers and values */
		Enumeration keys = values.keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			set(key, values.get(key));
		}
	}

	/**
	 * Case-preserved keys.
	 */
	public synchronized Enumeration keys() {
		return new ArrayEnumeration(headers, size);
	}

	/**
	 * Values.
	 */
	public synchronized Enumeration elements() {
		return new ArrayEnumeration(values, size);
	}

	private int getIndex(Object key) {
		boolean stringKey = key instanceof String;
		for (int i = 0; i < size; i++) {
			if (headers[i].equals(key))
				return i;
			if (stringKey && (headers[i] instanceof String) && ((String) headers[i]).equalsIgnoreCase((String) key))
				return i;
		}
		return -1;
	}

	private Object remove(int remove) {
		Object removed = values[remove];
		for (int i = remove; i < size; i++) {
			if (i == headers.length - 1) {
				headers[i] = null;
				values[i] = null;
			}
			else {
				headers[i] = headers[i + 1];
				values[i] = values[i + 1];
			}
		}
		if (remove < size)
			size--;
		return removed;
	}

	private void add(Object header, Object value) {
		if (size == headers.length) {
			// grow the arrays
			Object[] newHeaders = new Object[headers.length + 10];
			Object[] newValues = new Object[values.length + 10];
			System.arraycopy(headers, 0, newHeaders, 0, headers.length);
			System.arraycopy(values, 0, newValues, 0, values.length);
			headers = newHeaders;
			values = newValues;
		}
		headers[size] = header;
		values[size] = value;
		size++;
	}

	/**
	 * Support case-insensitivity for keys.
	 *
	 * @param key name.
	 */
	public synchronized Object get(Object key) {
		int i = -1;
		if ((i = getIndex(key)) != -1)
			return values[i];
		return null;
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
		if (key instanceof String)
			key = ((String)key).intern();
		int i = getIndex(key);
		if (value == null) /* remove */
		{
			if (i != -1)
				return remove(i);
		} else /* put */
		{
			if (i != -1) /* duplicate key */
				throw new IllegalArgumentException(NLS.bind(Msg.HEADER_DUPLICATE_KEY_EXCEPTION, key));
			add(key, value);
		}
		return null;
	}

	/**
	 * Returns the number of entries (distinct keys) in this dictionary.
	 *
	 * @return  the number of keys in this dictionary.
	 */
	public synchronized int size() {
		return size;
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
		return size == 0;
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
						throw new BundleException(NLS.bind(Msg.MANIFEST_INVALID_SPACE, line));
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
					throw new BundleException(NLS.bind(Msg.MANIFEST_INVALID_LINE_NOCOLON, line));
				}
				header = line.substring(0, colon).trim();
				value.append(line.substring(colon + 1));
				firstLine = false;
			}
			return headers;
		} catch (IOException e) {
			throw new BundleException(Msg.MANIFEST_IOEXCEPTION, e);
		} finally {
			try {
				in.close();
			} catch (IOException ee) {
			}
		}
	}

	class ArrayEnumeration implements Enumeration {
		private Object[] array;
		int cur = 0;

		public ArrayEnumeration(Object[] array, int size) {
			this.array = new Object[size];
			System.arraycopy(array, 0, this.array, 0, this.array.length);
		}

		public boolean hasMoreElements() {
			return cur < array.length;
		}

		public Object nextElement() {
			return array[cur++];
		}
		
	}
}

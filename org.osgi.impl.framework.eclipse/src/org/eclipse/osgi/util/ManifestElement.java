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

package org.eclipse.osgi.util;

import java.util.*;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.framework.internal.core.Msg;
import org.eclipse.osgi.framework.internal.core.Tokenizer;
import org.osgi.framework.BundleException;

/**
 * This class represents a single manifest element.  A manifest element must consist of a single
 * {@link String} value.  The {@link String} value may be split up into component values each
 * separated by a semi-colon (';').  A manifest element may optionally have a set of 
 * attribute values associated with it. The general syntax of a manifest element is as follows:
 * <p>
 * <pre>
 * ManifestElement ::= headervalues (';' attribute)*
 * headervalues ::= headervalue (';' headervalue)*
 * headervalue ::= <any string value that does not have ';'>
 * attribute ::= key '=' value
 * key ::= token
 * value ::= token | quoted-string
 * </pre>
 * </p>
 * <p>
 * For example, The following is an example of a manifest element to the <tt>Export-Package</tt> header:
 * </p>
 * <p>
 * <pre>
 * org.osgi.framework; specification-version="1.2"; another-attr="examplevalue"
 * </pre>
 * </p>
 * <p>
 * This manifest element has a value of <tt>org.osgi.framework</tt> and it has two attributes, 
 * <tt>specification-version</tt> and <tt>another-attr</tt>. 
 * </p>
 * <p>
 * The following manifest element is an example of a manifest element that has multiple
 * components to its value: 
 * </p>
 * <p>
 * <pre>
 * code1.jar;code2.jar;code3.jar;attr1=value1;attr2=value2;attr3=value3
 * </pre>
 * </p>
 * <p>
 * This manifest element has a value of <tt>code1.jar;code2.jar;code3.jar</tt>.  
 * This is an example of a multiple component value.  This value has three
 * components: <tt>code1.jar</tt>, <tt>code2.jar</tt>, and <tt>code3.jar</tt>.
 * </p>
 * <p>
 * This class is not intended to be subclassed by clients.
 * </p>
 * 
 * @since 3.0
 */
public class ManifestElement {

	/**
	 * The value of the manifest element.
	 */
	protected String value;

	/**
	 * The value components of the manifest element.
	 */
	protected String[] valueComponents;

	/**
	 * The table of attributes for the manifest element.
	 */
	protected Hashtable attributes;

	/**
	 * The table of directives for the manifest element.
	 */
	protected Hashtable directives;

	/**
	 * Constructs an empty manifest element with no value or attributes.
	 */
	protected ManifestElement() {
		super();
	}

	/**
	 * Returns the value of the manifest element.  The value returned is the
	 * complete value up to the first attribute.  For example, the 
	 * following manifest element: 
	 * <p>
	 * <pre>
	 * test1.jar;test2.jar;test3.jar;selection-filter="(os.name=Windows XP)"
	 * </pre>
	 * </p>
	 * <p>
	 * This manifest element has a value of <tt>test1.jar;test2.jar;test3.jar</tt>
	 * </p>
	 * 
	 * @return the value of the manifest element.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Returns the value components of the manifest element. The value
	 * components returned are the complete list of value components up to 
	 * the first attribute.  
	 * For example, the folowing manifest element: 
	 * <p>
	 * <pre>
	 * test1.jar;test2.jar;test3.jar;selection-filter="(os.name=Windows XP)"
	 * </pre>
	 * </p>
	 * <p>
	 * This manifest element has the value components array 
	 * <tt>{ "test1.jar", "test2.jar", "test3.jar" }</tt>
	 * Each value component is delemited by a semi-colon (<tt>';'</tt>).
	 * </p>
	 * 
	 * @return the String[] of value components
	 */
	public String[] getValueComponents() {
		return valueComponents;
	}

	/**
	 * Returns the value for the specified attribute or <code>null</code> if it does 
	 * not exist.  If the attribute has multiple values specified then the last value 
	 * specified is returned. For example the following manifest element: 
	 * <p>
	 * <pre>
	 * elementvalue; myattr="value1"; myattr="value2"
	 * </pre>
	 * </p>
	 * <p>
	 * specifies two values for the attribute key <tt>myattr</tt>.  In this case <tt>value2</tt>
	 * will be returned because it is the last value specified for the attribute
	 * <tt>myattr</tt>.
	 * </p>
	 * 
	 * @param key the attribute key to return the value for
	 * @return the attribute value or <code>null</code>
	 */
	public String getAttribute(String key) {
		return getTableValue(attributes, key);
	}

	/**
	 * Returns an array of values for the specified attribute or 
	 * <code>null</code> if the attribute does not exist.
	 * 
	 * @param key the attribute key to return the values for
	 * @return the array of attribute values or <code>null</code> 
	 * @see #getAttribute(String)
	 */
	public String[] getAttributes(String key) {
		return getTableValues(attributes, key);
	}

	/**
	 * Returns an enumeration of attribute keys for this manifest element or
	 * <code>null</code> if none exist.
	 * 
	 * @return the enumeration of attribute keys or null if none exist.
	 */
	public Enumeration getKeys() {
		return getTableKeys(attributes);
	}

	/**
	 * Add an attribute to this manifest element.
	 * 
	 * @param key the key of the attribute
	 * @param value the value of the attribute
	 */
	protected void addAttribute(String key, String value) {
		attributes = addTableValue(attributes, key, value);
	}

	/**
	 * Returns the value for the specified directive or <code>null</code> if it 
	 * does not exist.  If the directive has multiple values specified then the 
	 * last value specified is returned. For example the following manifest element: 
	 * <p>
	 * <pre>
	 * elementvalue; mydir:="value1"; mydir:="value2"
	 * </pre>
	 * </p>
	 * <p>
	 * specifies two values for the directive key <tt>mydir</tt>.  In this case <tt>value2</tt>
	 * will be returned because it is the last value specified for the directive <tt>mydir</tt>.
	 * </p>
	 * 
	 * @param key the directive key to return the value for
	 * @return the directive value or <code>null</code>
	 */
	public String getDirective(String key) {
		return getTableValue(directives, key);
	}

	/**
	 * Returns an array of string values for the specified directives or 
	 * <code>null</code> if it does not exist.
	 * 
	 * @param key the directive key to return the values for
	 * @return the array of directive values or <code>null</code>
	 * @see #getDirective(String)
	 */
	public String[] getDirectives(String key) {
		return getTableValues(directives, key);
	}

	/**
	 * Return an enumeration of directive keys for this manifest element or
	 * <code>null</code> if there are none.
	 * 
	 * @return the enumeration of directive keys or <code>null</code>
	 */
	public Enumeration getDirectiveKeys() {
		return getTableKeys(directives);
	}

	/**
	 * Add a directive to this manifest element.
	 * 
	 * @param key the key of the attribute
	 * @param value the value of the attribute
	 */
	protected void addDirective(String key, String value) {
		directives = addTableValue(directives, key, value);
	}

	/*
	 * Return the last value associated with the given key in the specified table.
	 */
	private String getTableValue(Hashtable table, String key) {
		if (table == null)
			return null;
		Object result = table.get(key);
		if (result == null)
			return null;
		if (result instanceof String)
			return (String) result;

		ArrayList valueList = (ArrayList) result;
		//return the last value
		return (String) valueList.get(valueList.size() - 1);
	}

	/*
	 * Return the values associated with the given key in the specified table.
	 */
	private String[] getTableValues(Hashtable table, String key) {
		if (table == null)
			return null;
		Object result = table.get(key);
		if (result == null)
			return null;
		if (result instanceof String)
			return new String[] {(String) result};
		ArrayList valueList = (ArrayList) result;
		return (String[]) valueList.toArray(new String[valueList.size()]);
	}

	/*
	 * Return an enumeration of table keys for the specified table. 
	 */
	private Enumeration getTableKeys(Hashtable table) {
		if (table == null)
			return null;
		return table.keys();
	}

	/*
	 * Add the given key/value association to the specified table. If an entry already exists
	 * for this key, then create an array list from the current value (if necessary) and
	 * append the new value to the end of the list.
	 */
	private Hashtable addTableValue(Hashtable table, String key, String value) {
		if (table == null) {
			table = new Hashtable(7);
		}
		Object curValue = table.get(key);
		if (curValue != null) {
			ArrayList newList;
			// create a list to contain multiple values
			if (curValue instanceof ArrayList) {
				newList = (ArrayList) curValue;
			} else {
				newList = new ArrayList(5);
				newList.add(curValue);
			}
			newList.add(value);
			table.put(key, newList);
		} else {
			table.put(key, value);
		}
		return table;
	}

	/**
	 * Parses a manifest header value into an array of ManifestElements.  Each
	 * ManifestElement returned will have a non-null value returned by getValue().
	 * 
	 * @param header the header name to parse.  This is only specified to provide error messages
	 * 	when the header value is invalid.
	 * @param value the header value to parse.
	 * @return the array of ManifestElements that are represented by the header value; null will be
	 * 	returned if the value specified is null or if the value does not parse into
	 * 	one or more ManifestElements.
	 * @throws BundleException if the header value is invalid
	 */
	public static ManifestElement[] parseHeader(String header, String value) throws BundleException {
		if (value == null) {
			return (null);
		}
		Vector headerElements = new Vector(10, 10);
		Tokenizer tokenizer = new Tokenizer(value);
		parseloop: while (true) {
			String next = tokenizer.getToken(";,"); //$NON-NLS-1$
			if (next == null) {
				throw new BundleException(NLS.bind(Msg.MANIFEST_INVALID_HEADER_EXCEPTION, header, value));
			}
			ArrayList headerValues = new ArrayList();
			StringBuffer headerValue = new StringBuffer(next);
			headerValues.add(next);

			if (Debug.DEBUG && Debug.DEBUG_MANIFEST) {
				Debug.print("paserHeader: " + next); //$NON-NLS-1$
			}
			char c = tokenizer.getChar();
			// Header values may be a list of ';' separated values.  Just append them all into one value until the first '=' or ','
			while (c == ';') {
				next = tokenizer.getToken(";,=:"); //$NON-NLS-1$
				if (next == null) {
					throw new BundleException(NLS.bind(Msg.MANIFEST_INVALID_HEADER_EXCEPTION, header, value));
				}
				c = tokenizer.getChar();
				if (c == ';' || c == '\0') /* more */{
					headerValues.add(next);
					headerValue.append(";").append(next); //$NON-NLS-1$
					if (Debug.DEBUG && Debug.DEBUG_MANIFEST) {
						Debug.print(";" + next); //$NON-NLS-1$
					}
				}
			}
			// found the header value create a manifestElement for it.
			ManifestElement manifestElement = new ManifestElement();
			manifestElement.value = headerValue.toString();
			manifestElement.valueComponents = (String[]) headerValues.toArray(new String[headerValues.size()]);
			boolean directive = false;
			if (c == ':') {
				c = tokenizer.getChar();
				if (c != '=')
					throw new BundleException(NLS.bind(Msg.MANIFEST_INVALID_HEADER_EXCEPTION, header, value));
				directive = true;
			}
			// now add any attributes for the manifestElement.
			while (c == '=') {
				String val = tokenizer.getString(";,"); //$NON-NLS-1$
				if (val == null) {
					throw new BundleException(NLS.bind(Msg.MANIFEST_INVALID_HEADER_EXCEPTION, header, value));
				}

				if (Debug.DEBUG && Debug.DEBUG_MANIFEST) {
					Debug.print(";" + next + "=" + val); //$NON-NLS-1$ //$NON-NLS-2$
				}
				try {
					if (directive)
						manifestElement.addDirective(next, val);
					else
						manifestElement.addAttribute(next, val);
					directive = false;
				} catch (Exception e) {
					throw new BundleException(NLS.bind(Msg.MANIFEST_INVALID_HEADER_EXCEPTION, header, value)); 
				}
				c = tokenizer.getChar();
				if (c == ';') /* more */{
					next = tokenizer.getToken("=:"); //$NON-NLS-1$
					if (next == null) {
						throw new BundleException(NLS.bind(Msg.MANIFEST_INVALID_HEADER_EXCEPTION, header, value)); 
					}
					c = tokenizer.getChar();
					if (c == ':') {
						c = tokenizer.getChar();
						if (c != '=')
							throw new BundleException(NLS.bind(Msg.MANIFEST_INVALID_HEADER_EXCEPTION, header, value)); 
						directive = true;
					}
				}
			}
			headerElements.addElement(manifestElement);
			if (Debug.DEBUG && Debug.DEBUG_MANIFEST) {
				Debug.println(""); //$NON-NLS-1$
			}
			if (c == ',') /* another manifest element */{
				continue parseloop;
			}
			if (c == '\0') /* end of value */{
				break parseloop;
			}
			throw new BundleException(NLS.bind(Msg.MANIFEST_INVALID_HEADER_EXCEPTION, header, value)); 
		}
		int size = headerElements.size();
		if (size == 0) {
			return (null);
		}
		ManifestElement[] result = new ManifestElement[size];
		headerElements.copyInto(result);
		return (result);
	}

	/**
	 * Returns the result of converting a list of comma-separated tokens into an array.
	 * 
	 * @return the array of string tokens or <code>null</code> if there are none
	 * @param stringList the initial comma-separated string
	 */
	public static String[] getArrayFromList(String stringList) {
		String[] result = getArrayFromList(stringList, ","); //$NON-NLS-1$
		return result.length == 0 ? null : result;
	}

	/**
	 * Returns the result of converting a list of tokens into an array.  The tokens
	 * are split using the specified separator.
	 * 
	 * @return the array of string tokens.  If there are none then an empty array
	 * is returned.
	 * @param stringList the initial string list
	 * @param separator the separator to use to split the list into tokens.
	 */
	public static String[] getArrayFromList(String stringList, String separator) {
		if (stringList == null || stringList.trim().length() == 0)
			return new String[0];
		ArrayList list = new ArrayList();
		StringTokenizer tokens = new StringTokenizer(stringList, separator);
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken().trim();
			if (token.length() != 0)
				list.add(token);
		}
		return (String[]) list.toArray(new String[list.size()]);
	}
}

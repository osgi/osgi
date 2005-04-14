/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.util;

import org.eclipse.osgi.framework.internal.core.MessageResourceBundle;

/**
 * Common superclass for all message bundle classes.  Provides convenience
 * methods for manipulating messages.
 * <p>
 * The #bind methods perform string substitution and should be considered a
 * convenience and <em>not</em> a full substitute replacement for MessageFormat#format
 * method calls. 
 * </p>
 * <p>Text appearing within curly braces in the given message, will be interpreted
 * as a numeric index to the corresponding substitution object in the given array.
 * </p>
 * <p>
 * Text appearing within single quotes is treated as a literal. A single quote is escaped by
 * a preceeding single quote.
 * </p>
 * <p>
 * Clients who wish to use the full substitution power of the MessageFormat class should
 * call that class directly and not use these #bind methods.
 * </p>
 * 
 * @since 3.1
 */
public abstract class NLS {

	/**
	 * @deprecated This was never intended to be API.  It will be
	 * removed prior to 3.1 M7.
	 */
	public static boolean DEBUG_MESSAGE_BUNDLES = false;

	private static final Object[] EMPTY_ARGS = new Object[0];

	/**
	 * Bind the given message's substitution locations with the given string values.
	 * 
	 * @param message the message to be manipulated
	 * @param binding the object to be inserted into the message
	 * @return the manipulated String
	 */
	public static String bind(String message, Object binding) {
		return internalBind(message, null, String.valueOf(binding), null);
	}

	/**
	 * Bind the given message's substitution locations with the given string values.
	 * 
	 * @param message the message to be manipulated
	 * @param binding1 An object to be inserted into the message
	 * @param binding2 A second object to be inserted into the message
	 * @return the manipulated String
	 */
	public static String bind(String message, Object binding1, Object binding2) {
		return internalBind(message, null, String.valueOf(binding1), String.valueOf(binding2));
	}

	/**
	 * Bind the given message's substitution locations with the given string values.
	 * 
	 * @param message the message to be manipulated
	 * @param bindings An array of objects to be inserted into the message
	 * @return the manipulated String
	 */
	public static String bind(String message, Object[] bindings) {
		if (message == null)
			return "No message available."; //$NON-NLS-1$
		return internalBind(message, bindings, null, null);
	}

	/**
	 * Initialize the given class with the values from the specified message bundle.
	 * 
	 * @param bundleName fully qualified path of the class name
	 * @param clazz the class where the constants will exist
	 */
	public static void initializeMessages(String bundleName, Class clazz) {
		MessageResourceBundle.load(bundleName, clazz);
	}

	/*
	 * Perform the string substitution on the given message with the specified args.
	 * See the class comment for exact details.
	 */
	private static String internalBind(String message, Object[] args, String argZero, String argOne) {
		if (args == null || args.length == 0)
			args = EMPTY_ARGS;

		int length = message.length();
		//estimate correct size of string buffer to avoid growth
		int bufLen = length + (args.length * 5);
		if (argZero != null)
			bufLen += argZero.length() - 3;
		if (argOne != null)
			bufLen += argOne.length() - 3;
		StringBuffer buffer = new StringBuffer(bufLen);
		for (int i = 0; i < length; i++) {
			char c = message.charAt(i);
			switch (c) {
				case '{' :
					int index = message.indexOf('}', i);
					// if we don't have a matching closing brace then...
					if (index == -1) {
						buffer.append(c);
						break;
					}
					i++;
					if (i >= length) {
						buffer.append(c);
						break;
					}
					// look for a substitution
					int number = -1;
					try {
						number = Integer.parseInt(message.substring(i, index));
					} catch (NumberFormatException e) {
						buffer.append("<missing argument>"); //$NON-NLS-1$
						i = index;
						break;
					}
					if (number == 0 && argZero != null)
						buffer.append(argZero);
					else if (number == 1 && argOne != null)
						buffer.append(argOne);
					else {
						if (number >= args.length || number < 0) {
							buffer.append("<missing argument>"); //$NON-NLS-1$
							i = index;
							break;
						}
						buffer.append(args[number]);
					}
					i = index;
					break;
				case '\'' :
					// if a single quote is the last char on the line then skip it
					int nextIndex = i + 1;
					if (nextIndex >= length) {
						buffer.append(c);
						break;
					}
					char next = message.charAt(nextIndex);
					// if the next char is another single quote then write out one
					if (next == '\'') {
						i++;
						buffer.append(c);
						break;
					}
					// otherwise we want to read until we get to the next single quote
					index = message.indexOf('\'', nextIndex);
					// if there are no more in the string, then skip it
					if (index == -1) {
						buffer.append(c);
						break;
					}
					// otherwise write out the chars inside the quotes
					buffer.append(message.substring(nextIndex, index));
					i = index;
					break;
				default :
					buffer.append(c);
			}
		}
		return buffer.toString();
	}

	/**
	 * Creates a new NLS instance.
	 */
	protected NLS() {
		super();
	}
}
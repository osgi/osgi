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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.eclipse.osgi.framework.internal.core.MessageResourceBundle;
import org.eclipse.osgi.framework.msg.MessageFormat;


/**
 * Common superclass for all message bundle classes.  Provides convenience
 * methods for manipulating messages.
 * 
 * @since 3.1
 */
public abstract class NLS {
	
	
	public static boolean DEBUG_MESSAGE_BUNDLES = false;
	/**
	 * Creates a new NLS instance.
	 */
	protected NLS() {
		super();
	}

	/**
	 * Bind the given message's substitution locations with the given string values.
	 * 
	 * @param message the message to be manipulated
	 * @param binding the object to be inserted into the message
	 * @return the manipulated String
	 */
	public static String bind(String message, Object binding) {
		return bind(message, new Object[] {binding});
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
		return bind(message, new Object[] {binding1, binding2});
	}

	/**
	 * Bind the given message's substitution locations with the given string values.
	 * 
	 * @param message the message to be manipulated
	 * @param bindings[] An array of objects to be inserted into the message
	 * @return the manipulated String
	 */
	public static String bind(String message, Object[] bindings) {
		if (message == null)
			return "No message available"; //$NON-NLS-1$
		if (bindings == null)
			return message;
		return MessageFormat.format(message, bindings);
	}

	/**
	 * Initialize the given class with the values from the specified message bundle.
	 * <p>
	 * Note this is interim API and may change before the 3.1 release.
	 * </p>
	 * 
	 * @param bundleName fully qualified path of the class name
	 * @param clazz the class where the constants will exist
	 */
	
	public static void initializeMessages(String bundleName, Class clazz) {
		long start = System.currentTimeMillis();
		// load the resource bundle and set the fields
		final Field[] fields = clazz.getDeclaredFields();
		MessageResourceBundle.load(bundleName, clazz.getClassLoader(), fields);

		// iterate over the fields in the class to make sure that there aren't any empty ones
		final int MOD_EXPECTED = Modifier.PUBLIC | Modifier.STATIC;
		final int MOD_MASK = MOD_EXPECTED | Modifier.FINAL;
		final int numFields = fields.length;
		for (int i = 0; i < numFields; i++) {
			Field field = fields[i];
			if ((field.getModifiers() & MOD_MASK) != MOD_EXPECTED)
				continue;
			try {
				// Set the value into the field if its empty. We should never get an exception here because
				// we know we have a public static non-final field. If we do get an exception, silently
				// log it and continue. This means that the field will (most likely) be un-initialized and
				// will fail later in the code and if so then we will see both the NPE and this error.
				if (field.get(clazz) == null) {
					String value = "Missing message: " + field.getName() + " in: " + bundleName; //$NON-NLS-1$ //$NON-NLS-2$
					if (DEBUG_MESSAGE_BUNDLES)
						System.out.println(value);
					field.set(null, value);
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (DEBUG_MESSAGE_BUNDLES)
			System.out.println("Time to load message bundle: " + bundleName + " was " + (System.currentTimeMillis() - start) + "ms."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}

/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.framework.internal.core;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import org.eclipse.osgi.framework.adaptor.FrameworkAdaptor;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.framework.log.FrameworkLogEntry;

/**
 * Class responsible for loading message values from a property file
 * and assigning them directly to the fields of a messages class.
 * @since 3.1
 */
public class MessageResourceBundle {
	/**
	 * Class which sub-classes java.util.Properties and uses the #put method
	 * to set field values rather than storing the values in the table.
	 * 
	 * @since 3.1
	 */
	private static class MessagesProperties extends Properties {

		private static final int MOD_EXPECTED = Modifier.PUBLIC | Modifier.STATIC;
		private static final int MOD_MASK = MOD_EXPECTED | Modifier.FINAL;
		private static final long serialVersionUID = 1L;

		private final String bundleName;
		private final Map fields;
		private final boolean isAccessible;

		public MessagesProperties(Map fieldMap, String bundleName, boolean isAccessible) {
			super();
			this.fields = fieldMap;
			this.bundleName = bundleName;
			this.isAccessible = isAccessible;
		}

		/* (non-Javadoc)
		 * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
		 */
		public synchronized Object put(Object key, Object value) {
			Object fieldObject = fields.put(key, ASSIGNED);
			// if already assigned, there is nothing to do
			if (fieldObject == ASSIGNED)
				return null;
			if (fieldObject == null) {
				final String msg = "NLS unused message: " + key + " in: " + bundleName;//$NON-NLS-1$ //$NON-NLS-2$
				if (Debug.DEBUG_MESSAGE_BUNDLES)
					System.out.println(msg); 
				log(SEVERITY_WARNING, msg, null);
				return null;
			}
			final Field field = (Field) fieldObject;
			//can only set value of public static non-final fields
			if ((field.getModifiers() & MOD_MASK) != MOD_EXPECTED)
				return null;
			try {
				// Check to see if we are allowed to modify the field. If we aren't (for instance 
				// if the class is not public) then change the accessible attribute of the field
				// before trying to set the value.
				if (!isAccessible)
					makeAccessible(field);
				// Set the value into the field. We should never get an exception here because
				// we know we have a public static non-final field. If we do get an exception, silently
				// log it and continue. This means that the field will (most likely) be un-initialized and
				// will fail later in the code and if so then we will see both the NPE and this error.
				field.set(null, value);
			} catch (Exception e) {
				log(SEVERITY_ERROR, "Exception setting field value.", e); //$NON-NLS-1$
			}
			return null;
		}
	}

	private static FrameworkAdaptor adaptor;
	/**
	 * This object is assigned to the value of a field map to indicate
	 * that a translated message has already been assigned to that field.
	 */
	static final Object ASSIGNED = new Object();

	private static final String EXTENSION = ".properties"; //$NON-NLS-1$

	private static String[] nlSuffixes;

	static int SEVERITY_ERROR = 0x04;

	static int SEVERITY_WARNING = 0x02;

	/*
	 * Build an array of property files to search.  The returned array contains
	 * the property fields in order from most specific to most generic.
	 * So, in the FR_fr locale, it will return file_fr_FR.properties, then
	 * file_fr.properties, and finally file.properties.
	 */
	private static String[] buildVariants(String root) {
		if (nlSuffixes == null) {
			//build list of suffixes for loading resource bundles
			String nl = Locale.getDefault().toString();
			ArrayList result = new ArrayList(4);
			int lastSeparator;
			while (true) {
				result.add('_' + nl + EXTENSION);
				lastSeparator = nl.lastIndexOf('_');
				if (lastSeparator == -1)
					break;
				nl = nl.substring(0, lastSeparator);
			}
			//add the empty suffix last (most general)
			result.add(EXTENSION);
			nlSuffixes = (String[]) result.toArray(new String[result.size()]);
		}
		root = root.replace('.', '/');
		String[] variants = new String[nlSuffixes.length];
		for (int i = 0; i < variants.length; i++)
			variants[i] = root + nlSuffixes[i];
		return variants;
	}

	/*
	 * Change the accessibility of the specified field so we can set its value
	 * to be the appropriate message string.
	 */
	static void makeAccessible(final Field field) {
		if (System.getSecurityManager() == null) {
			field.setAccessible(true);
		} else {
			AccessController.doPrivileged(new PrivilegedAction() {
				public Object run() {
					field.setAccessible(true);
					return null;
				}
			});
		}
	}

	private static void computeMissingMessages(String bundleName, Class clazz, Map fieldMap, Field[] fieldArray, boolean isAccessible) {
		// iterate over the fields in the class to make sure that there aren't any empty ones
		final int MOD_EXPECTED = Modifier.PUBLIC | Modifier.STATIC;
		final int MOD_MASK = MOD_EXPECTED | Modifier.FINAL;
		final int numFields = fieldArray.length;
		for (int i = 0; i < numFields; i++) {
			Field field = fieldArray[i];
			if ((field.getModifiers() & MOD_MASK) != MOD_EXPECTED)
				continue;
			//if the field has a a value assigned, there is nothing to do
			if (fieldMap.get(field.getName()) == ASSIGNED)
				continue;
			try {
				// Set a value for this empty field. We should never get an exception here because
				// we know we have a public static non-final field. If we do get an exception, silently
				// log it and continue. This means that the field will (most likely) be un-initialized and
				// will fail later in the code and if so then we will see both the NPE and this error.
				String value = "NLS missing message: " + field.getName() + " in: " + bundleName; //$NON-NLS-1$ //$NON-NLS-2$
				if (Debug.DEBUG_MESSAGE_BUNDLES)
					System.out.println(value);
				log(SEVERITY_WARNING, value, null);
				if (!isAccessible)
					makeAccessible(field);
				field.set(null, value);
			} catch (Exception e) {
				log(SEVERITY_ERROR, "Error setting the missing message value for: " + field.getName(), e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Load the given resource bundle using the specified class loader.
	 */
	public static void load(final String bundleName, Class clazz) {
		long start = System.currentTimeMillis();
		final Field[] fieldArray = clazz.getDeclaredFields();
		ClassLoader loader = clazz.getClassLoader();

		boolean isAccessible = (clazz.getModifiers() & Modifier.PUBLIC) != 0;

		//build a map of field names to Field objects
		final int len = fieldArray.length;
		Map fields = new HashMap(len * 2);
		for (int i = 0; i < len; i++)
			fields.put(fieldArray[i].getName(), fieldArray[i]);

		// search the variants from most specific to most general, since
		// the MessagesProperties.put method will mark assigned fields
		// to prevent them from being assigned twice
		final String[] variants = buildVariants(bundleName);
		for (int i = 0; i < variants.length; i++) {
			final InputStream input = loader.getResourceAsStream(variants[i]);
			if (input == null)
				continue;
			try {
				final MessagesProperties properties = new MessagesProperties(fields, bundleName, isAccessible);
				properties.load(input);
			} catch (IOException e) {
				log(SEVERITY_ERROR, "Error loading " + variants[i], e); //$NON-NLS-1$
			} finally {
				if (input != null)
					try {
						input.close();
					} catch (IOException e) {
						// ignore
					}
			}
		}
		computeMissingMessages(bundleName, clazz, fields, fieldArray, isAccessible);
		if (Debug.DEBUG_MESSAGE_BUNDLES)
			System.out.println("Time to load message bundle: " + bundleName + " was " + (System.currentTimeMillis() - start) + "ms."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	static void log(int severity, String msg, Exception e) {
		if (adaptor != null)
			adaptor.getFrameworkLog().log(new FrameworkLogEntry(FrameworkAdaptor.FRAMEWORK_SYMBOLICNAME + ' ' + severity + ' ' + 1, msg, 0, e, null));
		else {
			System.out.println(msg); //$NON-NLS-1$
			if (e != null)
				e.printStackTrace();
		}
	}

	static void setAdaptor(FrameworkAdaptor adaptor) {
		MessageResourceBundle.adaptor = adaptor;
	}
}
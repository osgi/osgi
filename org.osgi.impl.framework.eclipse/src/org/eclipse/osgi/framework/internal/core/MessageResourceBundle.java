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
import java.util.*;
import org.eclipse.osgi.util.NLS;

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

		private final Map fields;
		private final String bundleName;

		public MessagesProperties(Field[] fieldArray, String bundleName) {
			super();
			this.bundleName = bundleName;
			final int len = fieldArray.length;
			fields = new HashMap(len * 2);
			for (int i = 0; i < len; i++) {
				fields.put(fieldArray[i].getName(), fieldArray[i]);
			}
		}

		/* (non-Javadoc)
		 * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
		 */
		public synchronized Object put(Object key, Object value) {
			try {
				Field field = (Field) fields.get(key);
				if (field == null) {
					if (NLS.DEBUG_MESSAGE_BUNDLES)
						System.out.println("Unused message: " + key + " in: " + bundleName); //$NON-NLS-1$ //$NON-NLS-2$
					return null;
				}
				//can only set value of public static non-final fields
				if ((field.getModifiers() & MOD_MASK) != MOD_EXPECTED)
					return null;
				// Set the value into the field. We should never get an exception here because
				// we know we have a public static non-final field. If we do get an exception, silently
				// log it and continue. This means that the field will (most likely) be un-initialized and
				// will fail later in the code and if so then we will see both the NPE and this error.
				try {
					field.set(null, value);
				} catch (Exception e) {
					// TODO externalize message
					//IStatus status = new Status(IStatus.ERROR, Platform.PI_RUNTIME, Platform.PLUGIN_ERROR, "Exception setting field value.", e);
					//InternalPlatform.getDefault().log(status);
					e.printStackTrace();
				}
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

	private static final String EXTENSION = ".properties"; //$NON-NLS-1$
	private static String[] nlSuffixes;

	/*
	 * Build an array of directories to search
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

	/**
	 * Load the given resource bundle using the specified class loader.
	 */
	public static void load(final String bundleName, final ClassLoader loader, final Field[] fields) {
		final String[] variants = buildVariants(bundleName);
		// search the dirs in reverse order so the cascading defaults is set correctly
		for (int i = variants.length; --i >= 0;) {
			final InputStream input = loader.getResourceAsStream(variants[i]);
			if (input == null)
				continue;
			try {
				final MessagesProperties properties = new MessagesProperties(fields, bundleName);
				properties.load(input);
			} catch (IOException e) {
				// TODO log
			} finally {
				if (input != null)
					try {
						input.close();
					} catch (IOException e) {
						// ignore
					}
			}
		}
	}
}

/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
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
import java.security.*;
import java.util.Properties;

/**
 * Utility class to execute common privileged code.
 */
public class SecureAction {
	/**
	 * Returns a system property.  Same as calling
	 * System.getProperty(String).
	 * @param property the property key.
	 * @return the value of the property or null if it does not exist.
	 */
	public static String getProperty(final String property) {
		if (System.getSecurityManager() == null)
			return System.getProperty(property);
		else
			return (String) AccessController.doPrivileged(new PrivilegedAction() {
				public Object run() {
					return System.getProperty(property);
				}
			});
	}

	/**
	 * Returns a system property.  Same as calling
	 * System.getProperty(String,String).
	 * @param property the property key.
	 * @param def the default value if the property key does not exist.
	 * @return the value of the property or the def value if the property
	 * does not exist.
	 */
	public static String getProperty(final String property, final String def) {
		if (System.getSecurityManager() == null)
			return System.getProperty(property, def);
		else
			return (String) AccessController.doPrivileged(new PrivilegedAction() {
				public Object run() {
					return System.getProperty(property, def);
				}
			});
	}

	/**
	 * Returns the system properties.  Same as calling
	 * System.getProperties().
	 * @return the system properties.
	 */
	public static Properties getProperties() {
		if (System.getSecurityManager() == null)
			return System.getProperties();
		else
			return (Properties) AccessController.doPrivileged(new PrivilegedAction() {
				public Object run() {
					return System.getProperties();
				}
			});
	}

	/**
	 * Creates a FileInputStream from a File.  Same as calling
	 * new FileInputStream(File).
	 * @param file the File to craete a FileInputStream from.
	 * @return The FileInputStream.
	 * @throws FileNotFoundException if the File does not exist.
	 */
	public static FileInputStream getFileInputStream(final File file) throws FileNotFoundException {
		if (System.getSecurityManager() == null)
			return new FileInputStream(file);
		else
			try {
				return (FileInputStream) AccessController.doPrivileged(new PrivilegedExceptionAction() {
					public Object run() throws FileNotFoundException {
						return new FileInputStream(file);
					}
				});
			} catch (PrivilegedActionException e) {
				throw (FileNotFoundException) e.getException();
			}
	}

	/**
	 * Creates a FileInputStream from a File.  Same as calling
	 * new FileOutputStream(File,boolean).
	 * @param file the File to create a FileOutputStream from.
	 * @param append indicates if the OutputStream should append content.
	 * @return The FileOutputStream.
	 * @throws FileNotFoundException if the File does not exist.
	 */
	public static FileOutputStream getFileOutputStream(final File file, final boolean append) throws FileNotFoundException {
		if (System.getSecurityManager() == null)
			return new FileOutputStream(file.getAbsolutePath(), append);
		else
			try {
				return (FileOutputStream) AccessController.doPrivileged(new PrivilegedExceptionAction() {
					public Object run() throws FileNotFoundException {
						return new FileOutputStream(file.getAbsolutePath(), append);
					}
				});
			} catch (PrivilegedActionException e) {
				throw (FileNotFoundException) e.getException();
			}
	}

	/**
	 * Creates a new Thread from a Runnable.  Same as calling
	 * new Thread(target,name).
	 * @param target the Runnable to create the Thread from.
	 * @param name The name of the Thread.
	 * @return The new Thread
	 */
	public static Thread createThread(final Runnable target, final String name) {
		if (System.getSecurityManager() == null)
			return new Thread(target, name);
		else {
			return (Thread) AccessController.doPrivileged(new PrivilegedAction() {
				public Object run() {
					return new Thread(target, name);
				}
			});
		}
	}

}

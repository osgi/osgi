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
import java.net.*;
import java.security.*;
import java.util.Properties;
import java.util.zip.ZipFile;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * Utility class to execute common privileged code.
 */
public class SecureAction {
	// make sure we use the correct controlContext;
	private AccessControlContext controlContext;

	public SecureAction() {
		// save the control context to be used.
		this.controlContext = AccessController.getContext();
	}

	/**
	 * Returns a system property.  Same as calling
	 * System.getProperty(String).
	 * @param property the property key.
	 * @return the value of the property or null if it does not exist.
	 */
	public String getProperty(final String property) {
		if (System.getSecurityManager() == null)
			return System.getProperty(property);
		return (String) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return System.getProperty(property);
			}
		}, controlContext);
	}

	/**
	 * Returns a system property.  Same as calling
	 * System.getProperty(String,String).
	 * @param property the property key.
	 * @param def the default value if the property key does not exist.
	 * @return the value of the property or the def value if the property
	 * does not exist.
	 */
	public String getProperty(final String property, final String def) {
		if (System.getSecurityManager() == null)
			return System.getProperty(property, def);
		return (String) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return System.getProperty(property, def);
			}
		}, controlContext);
	}

	/**
	 * Returns the system properties.  Same as calling
	 * System.getProperties().
	 * @return the system properties.
	 */
	public Properties getProperties() {
		if (System.getSecurityManager() == null)
			return System.getProperties();
		return (Properties) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return System.getProperties();
			}
		}, controlContext);
	}

	/**
	 * Creates a FileInputStream from a File.  Same as calling
	 * new FileInputStream(File).
	 * @param file the File to craete a FileInputStream from.
	 * @return The FileInputStream.
	 * @throws FileNotFoundException if the File does not exist.
	 */
	public FileInputStream getFileInputStream(final File file) throws FileNotFoundException {
		if (System.getSecurityManager() == null)
			return new FileInputStream(file);
		try {
			return (FileInputStream) AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws FileNotFoundException {
					return new FileInputStream(file);
				}
			}, controlContext);
		} catch (PrivilegedActionException e) {
			if (e.getException() instanceof FileNotFoundException)
				throw (FileNotFoundException) e.getException();
			throw (RuntimeException) e.getException();
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
	public FileOutputStream getFileOutputStream(final File file, final boolean append) throws FileNotFoundException {
		if (System.getSecurityManager() == null)
			return new FileOutputStream(file.getAbsolutePath(), append);
		try {
			return (FileOutputStream) AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws FileNotFoundException {
					return new FileOutputStream(file.getAbsolutePath(), append);
				}
			}, controlContext);
		} catch (PrivilegedActionException e) {
			if (e.getException() instanceof FileNotFoundException)
				throw (FileNotFoundException) e.getException();
			throw (RuntimeException) e.getException();
		}
	}

	public long length(final File file) {
		if (System.getSecurityManager() == null)
			return file.length();
		return ((Long) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return new Long(file.length());
			}
		}, controlContext)).longValue();
	}

	public boolean exists(final File file) {
		if (System.getSecurityManager() == null)
			return file.exists();
		return ((Boolean) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return new Boolean(file.exists());
			}
		}, controlContext)).booleanValue();
	}

	public boolean isDirectory(final File file) {
		if (System.getSecurityManager() == null)
			return file.isDirectory();
		return ((Boolean) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return new Boolean(file.isDirectory());
			}
		}, controlContext)).booleanValue();
	}

	public long lastModified(final File file) {
		if (System.getSecurityManager() == null)
			return file.lastModified();
		return ((Long) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return new Long(file.lastModified());
			}
		}, controlContext)).longValue();
	}

	public String[] list(final File file) {
		if (System.getSecurityManager() == null)
			return file.list();
		return (String[]) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return file.list();
			}
		}, controlContext);
	}

	public ZipFile getZipFile(final File file) throws IOException {
		if (System.getSecurityManager() == null)
			return new ZipFile(file);
		try {
			return (ZipFile) AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws IOException {
					return new ZipFile(file);
				}
			}, controlContext);
		} catch (PrivilegedActionException e) {
			if (e.getException() instanceof IOException)
				throw (IOException) e.getException();
			throw (RuntimeException) e.getException();
		}
	}

	public URL getURL(final String protocol, final String host, final int port, final String file, final URLStreamHandler handler) throws MalformedURLException {
		if (System.getSecurityManager() == null)
			return new URL(protocol, host, port, file, handler);
		try {
			return (URL) AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws MalformedURLException {
					return new URL(protocol, host, port, file, handler);
				}
			}, controlContext);
		} catch (PrivilegedActionException e) {
			if (e.getException() instanceof MalformedURLException)
				throw (MalformedURLException) e.getException();
			throw (RuntimeException) e.getException();
		}
	}

	/**
	 * Creates a new Thread from a Runnable.  Same as calling
	 * new Thread(target,name).
	 * @param target the Runnable to create the Thread from.
	 * @param name The name of the Thread.
	 * @return The new Thread
	 */
	public Thread createThread(final Runnable target, final String name) {
		if (System.getSecurityManager() == null)
			return new Thread(target, name);
		return (Thread) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return new Thread(target, name);
			}
		}, controlContext);
	}

	public Object getService(final ServiceReference reference, final BundleContext context) {
		if (System.getSecurityManager() == null)
			return context.getService(reference);
		return AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return context.getService(reference);
			}
		}, controlContext);
	}

	public Class forName(final String name) throws ClassNotFoundException {
		if (System.getSecurityManager() == null)
			return Class.forName(name);
		try {
			return (Class) AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws Exception {
					return Class.forName(name);
				}
			});
		} catch (PrivilegedActionException e) {
			if (e.getException() instanceof ClassNotFoundException)
				throw (ClassNotFoundException) e.getException();
			throw (RuntimeException) e.getException();
		}
	}
}

/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.framework;

import java.lang.reflect.*;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Framework Utility class.
 * 
 * <p>
 * This class contains utility methods which access Framework functions that may
 * be useful to bundles.
 * 
 * @version $Revision$
 * @since 1.3
 */
public class FrameworkUtil {
	/*
	 * NOTE: A framework implementor may also choose to replace this
	 * class in their distribution with a class that directly interfaces
	 * with the framework implementation.
	 */

	/*
	 * This class will load the FrameworkUtil class in the package named by the
	 * org.osgi.vendor.framework package. For each instance of this class, an
	 * instance of the vendor FrameworkUtil class will be created and this class
	 * will delegate method calls to the vendor FrameworkUtil instance.
	 */
	private static final String	packageProperty	= "org.osgi.vendor.framework";

	/*
	 * This is the delegate method used by createFilter.
	 */
	private final static Method	createFilter;

	static {
		createFilter = (Method) AccessController
				.doPrivileged(new PrivilegedAction() {
					public Object run() {
						String packageName = System
								.getProperty(packageProperty);
						if (packageName == null) {
							throw new NoClassDefFoundError(packageProperty
									+ " property not set");
						}

						Class delegateClass;
						try {
							delegateClass = Class.forName(packageName
									+ ".FrameworkUtil");
						}
						catch (ClassNotFoundException e) {
							throw new NoClassDefFoundError(e.toString());
						}

						Method result;
						try {
							result = delegateClass.getMethod("createFilter",
									new Class[] {String.class});
						}
						catch (NoSuchMethodException e) {
							throw new NoSuchMethodError(e.toString());
						}

						if (!Modifier.isStatic(result.getModifiers())) {
							throw new NoSuchMethodError(
									"createFilter method must be static");
						}

						return result;
					}
				});
	}

	/**
	 * Creates a <code>Filter</code> object. This <code>Filter</code> object
	 * may be used to match a <code>ServiceReference</code> object or a
	 * <code>Dictionary</code> object.
	 * 
	 * <p>
	 * If the filter cannot be parsed, an {@link InvalidSyntaxException} will be
	 * thrown with a human readable message where the filter became unparsable.
	 * 
	 * @param filter The filter string.
	 * @return A <code>Filter</code> object encapsulating the filter string.
	 * @throws InvalidSyntaxException If <code>filter</code> contains an
	 *            invalid filter string that cannot be parsed.
	 * @throws NullPointerException If <code>filter</code> is null.
	 * 
	 * @see Filter
	 */
	public static Filter createFilter(String filter)
			throws InvalidSyntaxException {
		try {
			try {
				return (Filter) createFilter.invoke(null, new Object[] {filter});
			}
			catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		}
		catch (InvalidSyntaxException e) {
			throw e;
		}
		catch (Error e) {
			throw e;
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Throwable e) {
			throw new RuntimeException(e.toString());
		}
	}
}

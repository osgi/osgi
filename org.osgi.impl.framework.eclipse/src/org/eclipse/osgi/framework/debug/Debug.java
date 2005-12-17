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

package org.eclipse.osgi.framework.debug;

import java.io.PrintStream;
import java.lang.reflect.*;

/**
 * This class has debug constants which can be used by the Framework implementation 
 * and Adaptor implementations
 * @since 3.1
 */
public class Debug {
	/**
	 * DEBUG flag.  If set to false then the debug statements will be optimized out during compilation.
	 */
	public static final boolean DEBUG = true;

	/**
	 * Indicates if tracing is enabled
	 */
	public static boolean DEBUG_ENABLED = false;

	/**
	 * General debug flag.
	 */
	public static boolean DEBUG_GENERAL = false; // "debug"
	/**
	 * Bundle time debug flag.
	 */
	public static boolean DEBUG_BUNDLE_TIME = false; //"debug.bundleTime"
	/**
	 * Loader debug flag.
	 */
	public static boolean DEBUG_LOADER = false; // "debug.loader"
	/**
	 * Events debug flag.
	 */
	public static boolean DEBUG_EVENTS = false; // "debug.events"
	/**
	 * Services debug flag.
	 */
	public static boolean DEBUG_SERVICES = false; // "debug.services"
	/**
	 * Packages debug flag.
	 */
	public static boolean DEBUG_PACKAGES = false; // "debug.packages"
	/**
	 * Manifest debug flag.
	 */
	public static boolean DEBUG_MANIFEST = false; // "debug.manifest"
	/**
	 * Filter debug flag.
	 */
	public static boolean DEBUG_FILTER = false; // "debug.filter"
	/**
	 * Security debug flag.
	 */
	public static boolean DEBUG_SECURITY = false; // "debug.security"
	/**
	 * Start level debug flag.
	 */
	public static boolean DEBUG_STARTLEVEL = false; // "debug.startlevel"
	/**
	 * PackageAdmin debug flag.
	 */
	public static boolean DEBUG_PACKAGEADMIN = false; // "debug.packageadmin"
	/**
	 * PackageAdmin timing debug flag.
	 */
	public static boolean DEBUG_PACKAGEADMIN_TIMING = false; //"debug.packageadmin/timing"
	/**
	 * Message debug flag.
	 */
	public static boolean DEBUG_MESSAGE_BUNDLES = false; //"/debug/messageBundles"
	/**
	 * Monitor activation debug flag.
	 */
	public static boolean MONITOR_ACTIVATION = false; // "monitor/bundles"

	/**
	 * Base debug option key (org.eclispe.osgi).
	 */
	public static final String ECLIPSE_OSGI = "org.eclipse.osgi"; //$NON-NLS-1$
	/**
	 * General Debug option key.
	 */
	public static final String OPTION_DEBUG_GENERAL = ECLIPSE_OSGI + "/debug"; //$NON-NLS-1$
	/**
	 * Bundle time Debug option key.
	 */
	public static final String OPTION_DEBUG_BUNDLE_TIME = ECLIPSE_OSGI + "/debug/bundleTime"; //$NON-NLS-1$
	/**
	 * Loader Debug option key.
	 */
	public static final String OPTION_DEBUG_LOADER = ECLIPSE_OSGI + "/debug/loader"; //$NON-NLS-1$
	/**
	 * Events Debug option key.
	 */
	public static final String OPTION_DEBUG_EVENTS = ECLIPSE_OSGI + "/debug/events"; //$NON-NLS-1$
	/**
	 * Services Debug option key.
	 */
	public static final String OPTION_DEBUG_SERVICES = ECLIPSE_OSGI + "/debug/services"; //$NON-NLS-1$
	/**
	 * Packages Debug option key.
	 */
	public static final String OPTION_DEBUG_PACKAGES = ECLIPSE_OSGI + "/debug/packages"; //$NON-NLS-1$
	/**
	 * Manifest Debug option key.
	 */
	public static final String OPTION_DEBUG_MANIFEST = ECLIPSE_OSGI + "/debug/manifest"; //$NON-NLS-1$
	/**
	 * Filter Debug option key.
	 */
	public static final String OPTION_DEBUG_FILTER = ECLIPSE_OSGI + "/debug/filter"; //$NON-NLS-1$
	/**
	 * Security Debug option key.
	 */
	public static final String OPTION_DEBUG_SECURITY = ECLIPSE_OSGI + "/debug/security"; //$NON-NLS-1$
	/**
	 * Start level Debug option key.
	 */
	public static final String OPTION_DEBUG_STARTLEVEL = ECLIPSE_OSGI + "/debug/startlevel"; //$NON-NLS-1$
	/**
	 * PackageAdmin Debug option key.
	 */
	public static final String OPTION_DEBUG_PACKAGEADMIN = ECLIPSE_OSGI + "/debug/packageadmin"; //$NON-NLS-1$
	/**
	 * PackageAdmin timing Debug option key.
	 */
	public static final String OPTION_DEBUG_PACKAGEADMIN_TIMING = ECLIPSE_OSGI + "/debug/packageadmin/timing"; //$NON-NLS-1$
	/**
	 * Monitor activation Debug option key.
	 */
	public static final String OPTION_MONITOR_ACTIVATION = ECLIPSE_OSGI + "/monitor/activation"; //$NON-NLS-1$
	/**
	 * Message bundles Debug option key.
	 */
	public static final String OPTION_DEBUG_MESSAGE_BUNDLES = ECLIPSE_OSGI + "/debug/messageBundles"; //$NON-NLS-1$

	static {
		FrameworkDebugOptions dbgOptions = FrameworkDebugOptions.getDefault();
		if (dbgOptions != null) {
			DEBUG_ENABLED = true;
			DEBUG_GENERAL = dbgOptions.getBooleanOption(OPTION_DEBUG_GENERAL, false);
			DEBUG_BUNDLE_TIME = dbgOptions.getBooleanOption(OPTION_DEBUG_BUNDLE_TIME, false) || dbgOptions.getBooleanOption("org.eclipse.core.runtime/timing/startup", false); //$NON-NLS-1$
			DEBUG_LOADER = dbgOptions.getBooleanOption(OPTION_DEBUG_LOADER, false);
			DEBUG_EVENTS = dbgOptions.getBooleanOption(OPTION_DEBUG_EVENTS, false);
			DEBUG_SERVICES = dbgOptions.getBooleanOption(OPTION_DEBUG_SERVICES, false);
			DEBUG_PACKAGES = dbgOptions.getBooleanOption(OPTION_DEBUG_PACKAGES, false);
			DEBUG_MANIFEST = dbgOptions.getBooleanOption(OPTION_DEBUG_MANIFEST, false);
			DEBUG_FILTER = dbgOptions.getBooleanOption(OPTION_DEBUG_FILTER, false);
			DEBUG_SECURITY = dbgOptions.getBooleanOption(OPTION_DEBUG_SECURITY, false);
			DEBUG_STARTLEVEL = dbgOptions.getBooleanOption(OPTION_DEBUG_STARTLEVEL, false);
			DEBUG_PACKAGEADMIN = dbgOptions.getBooleanOption(OPTION_DEBUG_PACKAGEADMIN, false);
			DEBUG_PACKAGEADMIN_TIMING = dbgOptions.getBooleanOption(OPTION_DEBUG_PACKAGEADMIN_TIMING, false) || dbgOptions.getBooleanOption("org.eclipse.core.runtime/debug", false); //$NON-NLS-1$
			DEBUG_MESSAGE_BUNDLES = dbgOptions.getBooleanOption(OPTION_DEBUG_MESSAGE_BUNDLES, false);
			MONITOR_ACTIVATION = dbgOptions.getBooleanOption(OPTION_MONITOR_ACTIVATION, false);
		}
	}

	/**
	 * The PrintStream to print debug messages to.
	 */
	public static PrintStream out = System.out;

	/**
	 * Prints x to the PrintStream
	 * @param x
	 */
	public static void print(boolean x) {
		out.print(x);
	}

	/**
	 * Prints x to the PrintStream
	 * @param x
	 */
	public static void print(char x) {
		out.print(x);
	}

	/**
	 * Prints x to the PrintStream
	 * @param x
	 */
	public static void print(int x) {
		out.print(x);
	}

	/**
	 * Prints x to the PrintStream
	 * @param x
	 */
	public static void print(long x) {
		out.print(x);
	}

	/**
	 * Prints x to the PrintStream
	 * @param x
	 */
	public static void print(float x) {
		out.print(x);
	}

	/**
	 * Prints x to the PrintStream
	 * @param x
	 */
	public static void print(double x) {
		out.print(x);
	}

	/**
	 * Prints x to the PrintStream
	 * @param x
	 */
	public static void print(char x[]) {
		out.print(x);
	}

	/**
	 * Prints x to the PrintStream
	 * @param x
	 */
	public static void print(String x) {
		out.print(x);
	}

	/**
	 * Prints x to the PrintStream
	 * @param x
	 */
	public static void print(Object x) {
		out.print(x);
	}

	/**
	 * Prints x to the PrintStream
	 * @param x
	 */
	public static void println(boolean x) {
		out.println(x);
	}

	/**
	 * Prints x to the PrintStream
	 * @param x
	 */
	public static void println(char x) {
		out.println(x);
	}

	/**
	 * Prints x to the PrintStream
	 * @param x
	 */
	public static void println(int x) {
		out.println(x);
	}

	/**
	 * Prints x to the PrintStream
	 * @param x
	 */
	public static void println(long x) {
		out.println(x);
	}

	/**
	 * Prints x to the PrintStream
	 * @param x
	 */
	public static void println(float x) {
		out.println(x);
	}

	/**
	 * Prints x to the PrintStream
	 * @param x
	 */
	public static void println(double x) {
		out.println(x);
	}

	/**
	 * Prints x to the PrintStream
	 * @param x
	 */
	public static void println(char x[]) {
		out.println(x);
	}

	/**
	 * Prints x to the PrintStream
	 * @param x
	 */
	public static void println(String x) {
		out.println(x);
	}

	/**
	 * Prints x to the PrintStream
	 * @param x
	 */
	public static void println(Object x) {
		out.println(x);
	}

	/**
	 * Prints t to the PrintStream
	 * @param t
	 */
	public static void printStackTrace(Throwable t) {
		t.printStackTrace(out);

		Method[] methods = t.getClass().getMethods();

		int size = methods.length;
		Class throwable = Throwable.class;

		for (int i = 0; i < size; i++) {
			Method method = methods[i];

			if (Modifier.isPublic(method.getModifiers()) && method.getName().startsWith("get") && throwable.isAssignableFrom(method.getReturnType()) && (method.getParameterTypes().length == 0)) { //$NON-NLS-1$
				try {
					Throwable nested = (Throwable) method.invoke(t, null);

					if ((nested != null) && (nested != t)) {
						out.println("Nested Exception:"); //$NON-NLS-1$
						printStackTrace(nested);
					}
				} catch (IllegalAccessException e) {
				} catch (InvocationTargetException e) {
				}
			}
		}
	}

}

/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.debug;

import java.io.PrintStream;
import java.lang.reflect.*;

public class Debug {
	public static final boolean DEBUG = true;
	public static boolean DEBUG_GENERAL = false; // "debug"
	public static boolean DEBUG_BUNDLE_TIME = false; //"debug.bundleTime"
	public static boolean DEBUG_LOADER = false; // "debug.loader"
	public static boolean DEBUG_EVENTS = false; // "debug.events"
	public static boolean DEBUG_SERVICES = false; // "debug.services"
	public static boolean DEBUG_PACKAGES = false; // "debug.packages"
	public static boolean DEBUG_MANIFEST = false; // "debug.manifest"
	public static boolean DEBUG_FILTER = false; // "debug.filter"
	public static boolean DEBUG_SECURITY = false; // "debug.security"
	public static boolean DEBUG_STARTLEVEL = false; // "debug.startlevel"
	public static boolean DEBUG_PACKAGEADMIN = false; // "debug.packageadmin"
	public static boolean DEBUG_PACKAGEADMIN_TIMING = false; //"debug.packageadmin/timing"
	public static boolean MONITOR_ACTIVATION = false; // "monitor/bundles"

	public static final String ECLIPSE_OSGI = "org.eclipse.osgi"; //$NON-NLS-1$
	public static final String OPTION_DEBUG_GENERAL = ECLIPSE_OSGI + "/debug"; //$NON-NLS-1$
	public static final String OPTION_DEBUG_BUNDLE_TIME = ECLIPSE_OSGI + "/debug/bundleTime"; //$NON-NLS-1$
	public static final String OPTION_DEBUG_LOADER = ECLIPSE_OSGI + "/debug/loader"; //$NON-NLS-1$
	public static final String OPTION_DEBUG_EVENTS = ECLIPSE_OSGI + "/debug/events"; //$NON-NLS-1$
	public static final String OPTION_DEBUG_SERVICES = ECLIPSE_OSGI + "/debug/services"; //$NON-NLS-1$
	public static final String OPTION_DEBUG_PACKAGES = ECLIPSE_OSGI + "/debug/packages"; //$NON-NLS-1$
	public static final String OPTION_DEBUG_MANIFEST = ECLIPSE_OSGI + "/debug/manifest"; //$NON-NLS-1$
	public static final String OPTION_DEBUG_FILTER = ECLIPSE_OSGI + "/debug/filter"; //$NON-NLS-1$
	public static final String OPTION_DEBUG_SECURITY = ECLIPSE_OSGI + "/debug/security"; //$NON-NLS-1$
	public static final String OPTION_DEBUG_STARTLEVEL = ECLIPSE_OSGI + "/debug/startlevel"; //$NON-NLS-1$
	public static final String OPTION_DEBUG_PACKAGEADMIN = ECLIPSE_OSGI + "/debug/packageadmin"; //$NON-NLS-1$
	public static final String OPTION_DEBUG_PACKAGEADMIN_TIMING = ECLIPSE_OSGI + "/debug/packageadmin/timing"; //$NON-NLS-1$
	public static final String OPTION_MONITOR_ACTIVATION = ECLIPSE_OSGI + "/monitor/activation"; //$NON-NLS-1$

	static {
		DebugOptions dbgOptions = DebugOptions.getDefault();
		if (dbgOptions != null) {
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
			MONITOR_ACTIVATION = dbgOptions.getBooleanOption(OPTION_MONITOR_ACTIVATION, false);
		}
	}
	public static PrintStream out = System.out;

	public static void print(boolean x) {
		out.print(x);
	}

	public static void print(char x) {
		out.print(x);
	}

	public static void print(int x) {
		out.print(x);
	}

	public static void print(long x) {
		out.print(x);
	}

	public static void print(float x) {
		out.print(x);
	}

	public static void print(double x) {
		out.print(x);
	}

	public static void print(char x[]) {
		out.print(x);
	}

	public static void print(String x) {
		out.print(x);
	}

	public static void print(Object x) {
		out.print(x);
	}

	public static void println(boolean x) {
		out.println(x);
	}

	public static void println(char x) {
		out.println(x);
	}

	public static void println(int x) {
		out.println(x);
	}

	public static void println(long x) {
		out.println(x);
	}

	public static void println(float x) {
		out.println(x);
	}

	public static void println(double x) {
		out.println(x);
	}

	public static void println(char x[]) {
		out.println(x);
	}

	public static void println(String x) {
		out.println(x);
	}

	public static void println(Object x) {
		out.println(x);
	}

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
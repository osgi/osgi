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

package org.eclipse.osgi.profile;

import org.eclipse.osgi.framework.debug.DebugOptions;

/**
 * This class is a development tool that provides a simple way to log 
 * programmer defined timmings for performance evaluations. This profiling
 * allows logging of a timestamp with a corresponding message to a trace 
 * buffer. 
 */

public class Profile {
	/**
	 * Profiling is enabled and available.
	 */
	public static final boolean PROFILE = true; // enable profile compiling
	/**
	 * The logging state of <tt>STARTUP</tt> messages
	 */
	public static boolean STARTUP = false; // enable startup profiling
	/**
	 * The logging state of <tt>BENCHMARK</tt> messages
	 */
	public static boolean BENCHMARK = false; // enable all benchmarking
	/**
	 * The logging state of <tt>DEBUG</tt> messages
	 */
	public static boolean DEBUG = false; // enable general debug profiling

	private static final String OSGI_PROP = "osgi.profile."; //$NON-NLS-1$
	private static final String PROP_STARTUP = OSGI_PROP + "startup"; //$NON-NLS-1$
	private static final String PROP_BENCHMARK = OSGI_PROP + "benchmark"; //$NON-NLS-1$
	private static final String PROP_DEBUG = OSGI_PROP + "debug"; //$NON-NLS-1$
	private static final String PROP_IMPL = OSGI_PROP + "impl"; //$NON-NLS-1$

	private static final String OSGI_OPTION = "org.eclipse.osgi/profile/"; //$NON-NLS-1$
	private static final String OPTION_STARTUP = OSGI_OPTION + "startup"; //$NON-NLS-1$
	private static final String OPTION_BENCHMARK = OSGI_OPTION + "benchmark"; //$NON-NLS-1$
	private static final String OPTION_DEBUG = OSGI_OPTION + "debug"; //$NON-NLS-1$
	private static final String OPTION_IMPL = OSGI_OPTION + "impl"; //$NON-NLS-1$

	/**
	 *  The default logging flag. 
	 */
	public static final int FLAG_NONE = 0;
	/**
	 * The logging flag for <strong>method enter</strong>
	 */
	public static final int FLAG_ENTER = 1;
	/**
	 * The logging flag for <strong>method exit</strong>
	 */
	public static final int FLAG_EXIT = 2;
	/**
	 * The description for <strong>method enter</strong>
	 */
	public static final String ENTER_DESCRIPTION = "enter"; //$NON-NLS-1$
	/**
	 * The description for <strong>method exit</strong>
	 */
	public static final String EXIT_DESCRIPTION = "exit"; //$NON-NLS-1$

	private static ProfileLogger profileLogger = null;
	private static String profileLoggerClassName = null;

	static {
		initProps();
	}

	/**
	 * Initialize/update profiling properties.
	 * 
	 * If profiling properties are updated, this method is called to update
	 * the profile states.
	 */
	public static void initProps() {
		String prop;
		DebugOptions dbgOptions = null;

		// if osgi.debug is not available, don't force DebugOptions
		//  to init as this variable may be set later on where 
		//  DebugOptions will succeed.
		if (System.getProperty("osgi.debug") != null) { //$NON-NLS-1$
			dbgOptions = DebugOptions.getDefault();
			if (dbgOptions != null) {
				STARTUP = dbgOptions.getBooleanOption(OPTION_STARTUP, false);
				BENCHMARK = dbgOptions.getBooleanOption(OPTION_BENCHMARK, false);
				DEBUG = dbgOptions.getBooleanOption(OPTION_DEBUG, false);
				if (profileLogger == null)
					profileLoggerClassName = dbgOptions.getOption(OPTION_IMPL);
			}
		}

		// System properties will always override anything in .options file
		if ((prop = System.getProperty(PROP_STARTUP)) != null) {
			STARTUP = Boolean.valueOf(prop).booleanValue();
			if (dbgOptions != null)
				dbgOptions.setOption(OPTION_STARTUP, new Boolean(STARTUP).toString());
		}
		if ((prop = System.getProperty(PROP_BENCHMARK)) != null) {
			BENCHMARK = Boolean.valueOf(prop).booleanValue();
			if (dbgOptions != null)
				dbgOptions.setOption(OPTION_BENCHMARK, new Boolean(BENCHMARK).toString());
		}
		if ((prop = System.getProperty(PROP_DEBUG)) != null) {
			DEBUG = Boolean.valueOf(prop).booleanValue();
			if (dbgOptions != null)
				dbgOptions.setOption(OPTION_DEBUG, new Boolean(DEBUG).toString());
		}

		if (profileLogger == null) {
			if ((prop = System.getProperty(PROP_IMPL)) != null) {
				profileLoggerClassName = prop;
				if (dbgOptions != null)
					dbgOptions.setOption(OPTION_IMPL, profileLoggerClassName);
			}
		} else {
			profileLogger.initProps();
		}
	}

	/**
	 * Log a method enter.
	 * 
	 * @param id The method's unique identification (e.g. org.eclipse.class#name).
	 */
	public static void logEnter(String id) {
		logTime(FLAG_ENTER, id, ENTER_DESCRIPTION, null);
	}

	/**
	 * Log a method enter.
	 * 
	 * @param id The method's unique identification (e.g. org.eclipse.class#name).
	 * @param description A description of the method.
	 */
	public static void logEnter(String id, String description) {
		logTime(FLAG_ENTER, id, ENTER_DESCRIPTION, description);
	}

	/**
	 * Log a method exit.
	 * 
	 * @param id The method's unique identification (e.g. org.eclipse.class#name).
	 */
	public static void logExit(String id) {
		logTime(FLAG_EXIT, id, EXIT_DESCRIPTION, null);
	}

	/**
	 * Log a method exit.
	 * 
	 * @param id The method's unique identification (e.g. org.eclipse.class#name).
	 * @param description A description of the method.
	 */
	public static void logExit(String id, String description) {
		logTime(FLAG_EXIT, id, EXIT_DESCRIPTION, description);
	}

	/**
	 * Log a message.
	 * 
	 * @param id The method's unique identification (e.g. org.eclipse.class#name).
	 * @param msg The message.
	 */
	public static void logTime(String id, String msg) {
		logTime(FLAG_NONE, id, msg, null);
	}

	/**
	 * Log a message.
	 * 
	 * @param id The method's unique identification (e.g. org.eclipse.class#name).
	 * @param msg The message.
	 * @param description A description of the method.
	 */
	public static void logTime(String id, String msg, String description) {
		logTime(FLAG_NONE, id, msg, description);
	}

	/**
	 * Log a message.
	 * 
	 * @param flag A profile logging flag.
	 * @param id The method's unique identification (e.g. org.eclipse.class#name).
	 * @param msg The message.
	 * @param description A description of the method.
	 * 
	 * @see #FLAG_ENTER
	 * @see #FLAG_EXIT
	 * @see #FLAG_NONE
	 */
	public static void logTime(int flag, String id, String msg, String description) {
		if (profileLogger == null) {
			if (profileLoggerClassName != null) {
				Class profileImplClass = null;
				try {
					profileImplClass = Class.forName(profileLoggerClassName);
					profileLogger = (ProfileLogger) profileImplClass.newInstance();
				} catch (Exception e) {
					// could not find the class
					e.printStackTrace();
				}
			}
			if (profileLogger == null)
				profileLogger = new DefaultProfileLogger();
		}
		profileLogger.logTime(flag, id, msg, description);
	}

	/**
	 * Get the profiling log report and reset the trace buffer.
	 * 
	 * @return The profiling log report.
	 */
	public static String getProfileLog() {
		if (profileLogger != null)
			return profileLogger.getProfileLog();
		return ""; //$NON-NLS-1$
	}

}

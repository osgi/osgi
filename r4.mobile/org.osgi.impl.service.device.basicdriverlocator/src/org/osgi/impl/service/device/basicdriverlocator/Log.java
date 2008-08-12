/**
 * Copyright (c) 1999, 2000 Gatespace AB. All Rights Reserved.
 * 
 * Gatespace grants the OSGi Alliance an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */
package org.osgi.impl.service.device.basicdriverlocator;

import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Log helper class.
 * 
 * @author Gatespace AB (osgiref@gatespace.com)
 * @version $Revision$
 *  
 */
public class Log {
	final static String		logClass	= LogService.class.getName();
	static BundleContext	bc			= null;
	static ServiceTracker	st			= null;

	/**
	 * Create new logger.
	 */
	static void start(BundleContext ctxt) {
		if (bc != null) // already initialized
			return;
		bc = ctxt;
		try {
			st = new ServiceTracker(bc, logClass, null);
			st.open();
		}
		catch (Exception e) {
			st = null;
		}
		if (st == null)
			warn("Could not instantiate ServiceTracker for Log Service");
	}

	/**
	 * Unget the log service.
	 */
	synchronized static void close() {
		debug("closing debug");
		if (st != null) {
			st.close();
			st = null;
		}
		bc = null;
	}

	/**
	 * Log a message to the log if possible, otherwise to System.err.
	 * 
	 * @param msg Log message
	 * @param level Severity level
	 * @param tag Descriptive label for System.err
	 */
	synchronized static void doLog(String msg, int level, String tag) {
		Object[] lss = null;
		if (st != null)
			lss = st.getServices();
		if (lss != null)
			for (int i = 0; i < lss.length; i++)
				((LogService) lss[i]).log(level, msg);
		else
			System.err.println(tag + msg);
	}

	/**
	 * Log a debug level message
	 * 
	 * @param msg Log message
	 */
	static void debug(String msg) {
		doLog(msg, LogService.LOG_DEBUG, "debug: ");
	}

	/**
	 * Log an information level message
	 * 
	 * @param msg Log message
	 */
	static void info(String msg) {
		doLog(msg, LogService.LOG_INFO, "info: ");
	}

	/**
	 * Log a warning level message
	 * 
	 * @param msg Log message
	 */
	static void warn(String msg) {
		doLog(msg, LogService.LOG_WARNING, "warning: ");
	}

	/**
	 * Log an error level message
	 * 
	 * @param msg Log message
	 */
	static void error(String msg, Exception e) {
		doLog(msg + ": " + e, LogService.LOG_ERROR, "error: ");
	}
}

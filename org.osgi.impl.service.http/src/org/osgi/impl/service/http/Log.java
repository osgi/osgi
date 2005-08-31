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
package org.osgi.impl.service.http;

import org.osgi.framework.*;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

//  ******************** Log ********************
/**
 * * Utility wrapper for the LogService. Adds a prefix to * all printouts and is
 * typically set to the classname or * a combination of classname and instance
 * name of the class * using the Log, e.g. "HttpServiceImpl#2" *
 * <p>* Internally the class uses the an slightly simplified version of the *
 * LogRef class. The LogRef class can be found in other * implementations of
 * osgiref bundles, e.g device. * *
 * 
 * @author Gatespace AB (osgiref@gatespace.com) *
 * @version $Revision$
 */
public final class Log {
	private static LogRef	logRef	= null;
	private static boolean	debug	= false;
	private String			prefix;

	public Log(String prefix) {
		this.prefix = "[" + prefix + "] - ";
	}

	static void init(BundleContext bc) {
		logRef = new LogRef(bc);
		logRef.open();
		debug = Boolean.getBoolean("org.osgi.service.http.debug");
	}

	static void close() {
		logRef.close();
	}

	static void setDebug(boolean b) {
		debug = b;
	}

	static boolean getDebug() {
		return debug;
	}

	public void info(String s) {
		logRef.doLog(hdr(s), LogService.LOG_INFO);
	}

	public void debug(String s) {
		if (debug)
			logRef.doLog(hdr(s), LogService.LOG_DEBUG);
	}

	public void warn(String s) {
		logRef.doLog(hdr(s), LogService.LOG_WARNING);
	}

	public void warn(String s, Exception e) {
		logRef.doLog(hdr(s), LogService.LOG_WARNING);
	}

	public void error(String s) {
		logRef.doLog(hdr(s), LogService.LOG_ERROR);
	}

	public void error(String s, Throwable t) {
		logRef.doLog(hdr(s) + t, LogService.LOG_ERROR);
	}

	private String hdr(String s) {
		return (prefix != null) ? prefix + s : s;
	}
} // Log
//  ******************** LogRef ********************
/**
 * * A simplified version of the LogRef class. Is wrapped inside * the Log class
 * when used by http.
 */

final class LogRef extends ServiceTracker {
	LogRef(BundleContext bc) {
		super(bc, LogService.class.getName(), null);
	}

	/**
	 * Log a message to the log if possible, otherwise to System.err.
	 * 
	 * @param msg Log message
	 * @param level Severity level
	 * @param tag Descriptive label for System.err
	 */
	synchronized void doLog(String msg, int level) {
		LogService log = getLog();
		if (log != null)
			log.log(level, msg);
		else
			System.err.println(getHdr(level) + msg);
	}

	private LogService getLog() {
		return (LogService) getService();
	}

	private static String getHdr(int level) {
		switch (level) {
			case LogService.LOG_INFO :
				return "info: ";
			case LogService.LOG_WARNING :
				return "warning: ";
			case LogService.LOG_ERROR :
				return "error: ";
			case LogService.LOG_DEBUG :
				return "debug: ";
			default :
				return "";
		}
	}
} // LogRef

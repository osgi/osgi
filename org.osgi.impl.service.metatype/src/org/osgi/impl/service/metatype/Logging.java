/*
 * $Header$
 *
 * Copyright (c) IBM Corporation (2005)
 *
 * These materials have been contributed  to the OSGi Alliance as 
 * "MEMBER LICENSED MATERIALS" as defined in, and subject to the terms of, 
 * the OSGi Member Agreement, specifically including but not limited to, 
 * the license rights and warranty disclaimers as set forth in Sections 3.2 
 * and 12.1 thereof, and the applicable Statement of Work. 
 *
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.
 */

package org.osgi.impl.service.metatype;

import java.io.PrintStream;


/**
 * Temporary Logging class
 * 
 * @author Julian Chen
 * @version 1.0
 */
public class Logging {

	public static final int TRACE		= 0;
	public static final int DEBUG		= 1;
	public static final int WARN		= 2;
	public static final int ERROR		= 3;

	private static int 			_logging_level	= WARN;
	private static PrintStream	out				= System.out;

	/*
	 * Constructor of class Logging.
	 */
	public Logging() {
	}

	/*
	 * 
	 */
	public static void log(int type, String message) {
			log(type, null, null, message);
	}

	/*
	 * Main method to print log message
	 */
	public static void log(int type, Object obj, String method, String message) {

		if (type >= _logging_level) {
			
			switch (type) {
			case TRACE:
				out.println("[Trace log]");
				break;
			case DEBUG:
				out.println("[Debug log]");
				break;
			case WARN:
				out.println("[Warning log]");
				break;
			default:
				out.println("[Error log]");
			}

			if (obj != null) {
				out.println("\tObject:  " + obj.getClass().getName());
			}
			if (method != null) {
				out.println("\tMethod:  " + method);
			}
			out.println("\tMessage: " + message);
		}
	}

	/*
	 * 
	 */
	public static void debug(String message) {

		log(DEBUG, null, null, message);
	}
}

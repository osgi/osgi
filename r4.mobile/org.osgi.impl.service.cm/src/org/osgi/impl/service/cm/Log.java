/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2000, 2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi (OSGI)
 * Specification may be subject to third party intellectual property rights,
 * including without limitation, patent rights (such a third party may or may
 * not be a member of OSGi). OSGi is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL NOT
 * INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY LOSS OF
 * PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF BUSINESS,
 * OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL, PUNITIVE OR
 * CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS DOCUMENT OR THE
 * INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.impl.service.cm;

import org.osgi.framework.*;
import org.osgi.service.log.LogService;

/**
 * Utility class for forwarding log messages to log service and console.
 * 
 * @author OSGi Alliance
 * @version $Revision$
 */
public class Log implements ServiceListener {
	private final static String		INFO			= "INFO";
	private final static String		DEBUG			= "DEBUG";
	private final static String		WARN			= "WARNING";
	private final static String		ERROR			= "ERROR";
	//private final static String		PROP_DEBUG		= "cm.debug";
	private final static String		PROP_CONSOLE	= "cm.console";
	private static BundleContext	bc;
	private static ServiceReference	sRef;
	private static LogService		logService;
	private static long				bundleId;
	private static boolean			printOnConsole;
	//private static boolean			debug;

	/**
	 * Constructs a Log, trying to get Log Service.
	 * 
	 * @param bc necessary to get Log Service from and to listen for Service
	 *        Events.
	 */
	public Log(BundleContext bc) {
		Log.bc = bc;
		bundleId = bc.getBundle().getBundleId();
		printOnConsole = Boolean.getBoolean(PROP_CONSOLE);
		//debug = Boolean.getBoolean(PROP_DEBUG);
		sRef = bc.getServiceReference(LogService.class.getName());
		if (sRef != null) {
			logService = (LogService) bc.getService(sRef);
		}
		try {
			bc.addServiceListener(this, "(objectClass="
					+ LogService.class.getName() + ")");
		}
		catch (InvalidSyntaxException ise) {
			/* do nothing syntax is valid */
		}
	}

	/**
	 * Method is invoked by the framework, when a service changes its state.
	 * When Log Service's state changes it is got or ungot - depending on the
	 * state.
	 * 
	 * @param sEvnt ServiceEvent to examine.
	 */
	public void serviceChanged(ServiceEvent sEvnt) {
		int type = sEvnt.getType();
		if (type == ServiceEvent.REGISTERED && logService == null) {
			sRef = sEvnt.getServiceReference();
			logService = (LogService) bc.getService(sRef);
		}
		if (type == ServiceEvent.UNREGISTERING
				&& sEvnt.getServiceReference().equals(sRef)) {
			bc.ungetService(sRef);
			logService = null;
		}
	}

	/**
	 * Logs a message to LogService - if available, otherwise message is printed
	 * on console.
	 * 
	 * @param level LogEntry's severity level.
	 * @param msg description of logged event.
	 */
	protected static void log(int level, String msg) {
		log(level, msg, null);
	}

	/**
	 * Forwards a message and exception to LogService, when it is available. If
	 * LogService is gone messages are printed on console.
	 * 
	 * @param level severity of log entry.
	 * @param msg message to be logged.
	 * @param exc Throwable object, related with the log event.
	 */
	protected static synchronized void log(int level, String msg, Throwable exc) {
		if (logService == null || printOnConsole) {
			String strLevel = null;
			if (level == LogService.LOG_ERROR) {
				strLevel = ERROR;
			}
			if (level == LogService.LOG_WARNING) {
				strLevel = WARN;
			}
			if (level == LogService.LOG_DEBUG) {
				strLevel = DEBUG;
			}
			if (level == LogService.LOG_INFO) {
				strLevel = INFO;
			}
			System.out.println(strLevel + " from " + bundleId);
			System.out.println(msg);
			if (exc != null) {
				exc.printStackTrace();
			}
		}
		if (logService != null) {
			logService.log(level, msg, exc);
		}
	}

	/**
	 * Closes the Log object, by releasing all resources: ungets LogService,
	 * removes ServiceListener.
	 */
	protected void close() {
		if (logService != null) {
			bc.ungetService(sRef);
			logService = null;
		}
		bc.removeServiceListener(this);
	}
}
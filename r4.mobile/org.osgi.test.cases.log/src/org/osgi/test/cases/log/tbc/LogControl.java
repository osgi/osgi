/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 */
package org.osgi.test.cases.log.tbc;

import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.log.*;
import org.osgi.test.cases.util.*;

public class LogControl extends DefaultTestBundleControl {
	ServiceReference	logServiceReference;
	LogService			logService;
	LogReaderService	logReaderService;
	static String[]		methods	= new String[] {"testLog",
			"testMultipleListeners"};

	public String[] getMethods() {
		return methods;
	}

	public void prepare() throws Exception {
		logServiceReference = getContext().getServiceReference(
				LogService.class.getName());
		logService = (LogService) getContext().getService(logServiceReference);
		logReaderService = (LogReaderService) getService(LogReaderService.class);
	}

	public void testLog() {
		LogReader reader = new LogReader(logReaderService, logService,
				getContext().getBundle(), "reader");
		reader.start();
		RuntimeException e = new RuntimeException();
		String message = "'The message <4711>'";
		ServiceReference sr = logServiceReference;
		logService.log(LogService.LOG_ERROR, message);
		logService.log(LogService.LOG_WARNING, message);
		logService.log(LogService.LOG_INFO, message);
		logService.log(LogService.LOG_DEBUG, message);
		logService.log(LogService.LOG_ERROR, message, e);
		logService.log(LogService.LOG_WARNING, message, e);
		logService.log(LogService.LOG_INFO, message, e);
		logService.log(LogService.LOG_DEBUG, message, e);
		logService.log(sr, LogService.LOG_ERROR, message);
		logService.log(sr, LogService.LOG_WARNING, message);
		logService.log(sr, LogService.LOG_INFO, message);
		logService.log(sr, LogService.LOG_DEBUG, message);
		logService.log(sr, LogService.LOG_ERROR, message, e);
		logService.log(sr, LogService.LOG_WARNING, message, e);
		logService.log(sr, LogService.LOG_INFO, message, e);
		logService.log(sr, LogService.LOG_DEBUG, message, e);
		reader.stop();
		logLogStrings(reader.id, reader.getLog());
	}

	public void testMultipleListeners() {
		LogReader reader1 = new LogReader(logReaderService, logService,
				getContext().getBundle(), "R1");
		LogReader reader2 = new LogReader(logReaderService, logService,
				getContext().getBundle(), "R2");
		/* Logged by none */
		logService.log(LogService.LOG_INFO, "Hello0 <4711>");
		reader1.start();
		/* Logged by R1 */
		logService.log(LogService.LOG_INFO, "Hello1 <4711>");
		reader1.stop();
		/* Logged by none */
		logService.log(LogService.LOG_INFO, "Hello2 <4711>");
		reader2.start();
		/* Logged by R2 */
		logService.log(LogService.LOG_INFO, "Hello3 <4711>");
		reader2.stop();
		/* Logged by none */
		logService.log(LogService.LOG_INFO, "Hello4 <4711>");
		reader1.start();
		reader2.start();
		/* Logged by R1 + R2 */
		logService.log(LogService.LOG_INFO, "Hello5 <4711>");
		reader1.stop();
		/* Logged by R2 */
		logService.log(LogService.LOG_INFO, "Hello6 <4711>");
		reader2.stop();
		/* Logged by none */
		logService.log(LogService.LOG_INFO, "Hello7 <4711>");
		logLogStrings(reader1.id, reader1.getLog());
		logLogStrings(reader2.id, reader2.getLog());
	}

	private void logLogStrings(String testText, Vector logStrings) {
		for (int i = 0; i < logStrings.size(); i++) {
			log(testText, (String) logStrings.elementAt(i));
		}
	}
	/*
	 * String[] keys = sr.getPropertyKeys(); for(int i = 0; i < keys.length;
	 * i++) { Object property = sr.getProperty(keys[i]); if(property instanceof
	 * String[]) { String[] p = (String[]) property; System.out.println(keys[i] + "= {
	 * "); for(int j = 0; j < p.length; j++) { System.out.println(" " + p[j]); }
	 * System.out.println("}"); } else { System.out.println(keys[i] + "=" +
	 * property); } }
	 */
}
//        "testAnotherBundle"
/*
 * installSecondBundle(); logService.log(LogService.LOG_INFO, "Hello14");
 * logReadSvc.addLogListener(testLogListener2); removeSecondBundleLogListener();
 * logService.log(LogService.LOG_INFO, "Hello15");
 * logReadSvc.removeLogListener(testLogListener1);
 * logReadSvc.removeLogListener(testLogListener2);
 */
/*
 * public void testAnotherBundle() { LogReader reader = new
 * LogReader(logReaderService, logService, getContext().getBundle(), "reader");
 * reader.start();
 * 
 * ContextSharerInfo csi = installContextSharer(); System.out.println("csi == " +
 * csi); System.out.println(csi.getBundle() + ", " + csi.getServiceReference() + ", " +
 * csi.getService()); logService.log(LogService.LOG_WARNING,
 * csi.getService().getContext().getBundle().getLocation());
 * uninstallContextSharer();
 * 
 * reader.stop(); logLogStrings(reader.id, reader.getLog()); }
 */

/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 */
package org.osgi.test.cases.log;

import junit.framework.TestCase;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;

import aQute.bnd.build.Framework;
import aQute.bnd.build.Project;

public class LogTestCase extends TestCase {

	ServiceReference logServiceReference;
	LogService logService;
	LogReaderService logReaderService;
	BundleContext context;

	Project		project;
	Framework	framework;
	
	public void setUp() throws Exception {
		framework = Framework.getInstance();	
		framework.activate();
		context = framework.getBundleContext();
		
		
		logServiceReference = context.getServiceReference(LogService.class
				.getName());
		logService = (LogService) context.getService(logServiceReference);
		
		ServiceReference logReaderServiceReference = context.getServiceReference(LogReaderService.class.getName());
		logReaderService = (LogReaderService) context
				.getService(logReaderServiceReference);
	}
	
	public void tearDown() throws Exception {
		framework.deactivate();
	}

	public void testLog() {
		LogReader reader = new LogReader();
		LogReader readers[] = new LogReader[] { reader };
		logReaderService.addLogListener(reader);
		RuntimeException e = new RuntimeException();
		String message = "'The message <4711>'";
		ServiceReference sr = logServiceReference;
		Bundle b = context.getBundle();
		assertLog(readers, b, null, LogService.LOG_ERROR, message, null);
		assertLog(readers, b, null, LogService.LOG_WARNING, message, null);
		assertLog(readers, b, null, LogService.LOG_INFO, message, null);
		assertLog(readers, b, null, LogService.LOG_DEBUG, message, null);
		assertLog(readers, b, null, -12, message, null);

		assertLog(readers, b, null, LogService.LOG_ERROR, message, e);
		assertLog(readers, b, null, LogService.LOG_WARNING, message, e);
		assertLog(readers, b, null, LogService.LOG_INFO, message, e);
		assertLog(readers, b, null, LogService.LOG_DEBUG, message, e);
		assertLog(readers, b, null, -12, message, e);

		assertLog(readers, b, sr, LogService.LOG_ERROR, message, null);
		assertLog(readers, b, sr, LogService.LOG_WARNING, message, null);
		assertLog(readers, b, sr, LogService.LOG_INFO, message, null);
		assertLog(readers, b, sr, LogService.LOG_DEBUG, message, null);
		assertLog(readers, b, sr, -12, message, null);

		assertLog(readers, b, sr, LogService.LOG_ERROR, message, e);
		assertLog(readers, b, sr, LogService.LOG_WARNING, message, e);
		assertLog(readers, b, sr, LogService.LOG_INFO, message, e);
		assertLog(readers, b, sr, LogService.LOG_DEBUG, message, e);
		assertLog(readers, b, sr, -12, message, null);
		
	}

	public void testMultipleListeners() {
		LogReader reader1 = new LogReader();
		LogReader reader2 = new LogReader();
		LogReader[] readers = new LogReader[] { reader1, reader2 };
		LogReader[] onlyReader1 = new LogReader[] { reader1 };
		LogReader[] noReaders = new LogReader[] {};

		RuntimeException e = new RuntimeException();
		String message = "'The message <4711>'";
		ServiceReference sr = logServiceReference;
		Bundle b = context.getBundle();

		logReaderService.addLogListener(reader1);
		logReaderService.addLogListener(reader2);

		assertLog(readers, b, null, LogService.LOG_ERROR, message, null);
		assertLog(readers, b, null, LogService.LOG_WARNING, message, null);
		assertLog(readers, b, sr, LogService.LOG_INFO, message, null);
		assertLog(readers, b, null, LogService.LOG_DEBUG, message, e);
		assertLog(readers, b, sr, -12, message, e);

		// Now remove reader2 so that we can check if 2 is really
		// removed
		logReaderService.removeLogListener(reader2);

		assertLog(onlyReader1, b, null, LogService.LOG_ERROR, message, null);
		assertLog(onlyReader1, b, null, LogService.LOG_WARNING, message, null);
		assertLog(onlyReader1, b, sr, LogService.LOG_INFO, message, null);
		assertLog(onlyReader1, b, null, LogService.LOG_DEBUG, message, e);
		assertLog(onlyReader1, b, sr, -12, message, null);

		assertEquals(0,reader2.size());

		// Now we also removed reader2 so that we can check if
		// the listeners re really removed
		logReaderService.removeLogListener(reader1);

		assertLog(noReaders, b, null, LogService.LOG_ERROR, message, null);
		assertLog(noReaders, b, null, LogService.LOG_WARNING, message, null);
		assertLog(noReaders, b, sr, LogService.LOG_INFO, message, null);
		assertLog(noReaders, b, null, LogService.LOG_DEBUG, message, e);
		assertLog(noReaders, b, sr, -12, message, null);

		assertEquals(0,reader1.size());
		assertEquals(0,reader2.size());
	}

	private void assertLog(LogReader readers[], Bundle bundle,
			ServiceReference reference, int level, String message,
			Throwable throwable) {

		int type = 0;

		if (reference != null)
			type++;

		if (throwable != null)
			type += 2;

		long time = System.currentTimeMillis();
		switch (type) {
		case 0:
			logService.log(level, message);
			break;
		case 1:
			logService.log(reference, level, message);
			break;
		case 2:
			logService.log(level, message, throwable);
			break;
		case 3:
			logService.log(reference, level, message, throwable);
			break;
		}

		for (int i = 0; i < readers.length; i++) {
			LogEntry entry = readers[i].getEntry(10000);
			assertEquals(entry.getBundle(), bundle);
			assertEquals(entry.getServiceReference(), reference);
			assertEquals(entry.getException(), throwable);
			assertEquals(entry.getLevel(), level);
			assertEquals(entry.getMessage(), message);
			assertTrue(entry.getTime() >= time);
			assertTrue(entry.getTime() <= System.currentTimeMillis());
		}
	}

}

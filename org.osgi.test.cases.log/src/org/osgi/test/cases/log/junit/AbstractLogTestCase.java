/*
 * Copyright (c) OSGi Alliance (2016, 2020). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.test.cases.log.junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogLevel;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;
import org.osgi.service.log.admin.LoggerAdmin;
import org.osgi.service.log.admin.LoggerContext;
import org.osgi.test.support.OSGiTestCase;

public abstract class AbstractLogTestCase extends OSGiTestCase {
	static final Throwable				auditThrowable		= new Exception(
			LogLevel.AUDIT.name());
	static final Throwable				errorThrowable		= new Exception(
			LogLevel.ERROR.name());
	static final Throwable				warnThrowable		= new Exception(
			LogLevel.WARN.name());
	static final Throwable				infoThrowable		= new Exception(
			LogLevel.INFO.name());
	static final Throwable				debugThrowable		= new Exception(
			LogLevel.DEBUG.name());
	static final Throwable				traceThrowable		= new Exception(
			LogLevel.TRACE.name());
	static final Throwable				unknownThrowable	= new Exception(
			"UNKNOWN");

	static final String					forceMessage		= "<4711> forced";
	static final String					auditMessage		= "<4711> audit";
	static final String					errorMessage		= "<4711> error";
	static final String					warnMessage			= "<4711> warn";
	static final String					infoMessage			= "<4711> info";
	static final String					debugMessage		= "<4711> debug";
	static final String					traceMessage		= "<4711> trace";
	static final String					unknownMessage		= "<4711> unknown";

	static final String					FORCE_LOGGER_NAME	= "force.logger.name";

	final LogReader						reader				= new LogReader();
	final AtomicLong					logSequence			= new AtomicLong(
			-1);
	ServiceReference<LogService>		logServiceReference;
	LogService							logService;
	ServiceReference<LogReaderService>	logReaderServiceReference;
	LogReaderService					logReaderService;
	ServiceReference<LoggerAdmin>		loggerAdminReference;
	LoggerAdmin							loggerAdmin;
	ServiceReference<ConfigurationAdmin>	configAdminReference;
	ConfigurationAdmin						configAdmin;
	Set<Configuration>						testConfigurations	= new HashSet<>();

	LoggerContext						rootContext;
	LoggerContext						bsnContext;
	LoggerContext						bsnVersionContext;
	LoggerContext						bsnVersionLocationContext;

	Map<String,LogLevel>				rootLogLevels;
	Map<String,LogLevel>				bsnLogLevels;
	Map<String,LogLevel>				bsnVersionLogLevels;
	Map<String,LogLevel>				bsnVersionLocationLogLevels;

	ServiceRegistration<String>			auditRegistration;
	ServiceRegistration<String>			errorRegistration;
	ServiceRegistration<String>			warnRegistration;
	ServiceRegistration<String>			infoRegistration;
	ServiceRegistration<String>			debugRegistration;
	ServiceRegistration<String>			traceRegistration;
	ServiceRegistration<String>			unknownRegistration;

	Bundle								tb1					= null;

	long									loggerContextConfigTimeout;

	protected void setUp() throws Exception {
		loggerContextConfigTimeout = getLongProperty(
				"org.osgi.test.cases.log.config_waiting_time", 200);
		auditRegistration = registerLogLevel(LogLevel.AUDIT);
		errorRegistration = registerLogLevel(LogLevel.ERROR);
		warnRegistration = registerLogLevel(LogLevel.WARN);
		infoRegistration = registerLogLevel(LogLevel.INFO);
		debugRegistration = registerLogLevel(LogLevel.DEBUG);
		traceRegistration = registerLogLevel(LogLevel.TRACE);
		unknownRegistration = registerLogLevel(null);

		logServiceReference = getContext().getServiceReference(
				LogService.class);
		logService = getContext().getService(logServiceReference);
		
		logReaderServiceReference = getContext()
				.getServiceReference(LogReaderService.class);
		logReaderService = getContext()
				.getService(logReaderServiceReference);
		reader.clear();
		logReaderService.addLogListener(reader);

		loggerAdminReference = getContext()
				.getServiceReference(LoggerAdmin.class);
		loggerAdmin = getContext().getService(loggerAdminReference);

		configAdminReference = getContext()
				.getServiceReference(ConfigurationAdmin.class);
		configAdmin = getContext().getService(configAdminReference);

		Bundle b = getContext().getBundle();
		// save off original log levels
		rootContext = loggerAdmin.getLoggerContext(null);
		rootLogLevels = rootContext.getLogLevels();

		bsnContext = loggerAdmin.getLoggerContext(b.getSymbolicName());
		bsnLogLevels = bsnContext.getLogLevels();

		bsnVersionContext = loggerAdmin
				.getLoggerContext(b.getSymbolicName() + '|' + b.getVersion());
		bsnVersionLogLevels = bsnVersionContext.getLogLevels();

		bsnVersionLocationContext = loggerAdmin
				.getLoggerContext(b.getSymbolicName() + '|' + b.getVersion()
						+ '|' + b.getLocation());
		bsnVersionLocationLogLevels = bsnVersionLocationContext.getLogLevels();

		// enable all log levels for all loggers by default
		loggerAdmin.getLoggerContext(null).setLogLevels(Collections
				.singletonMap(Logger.ROOT_LOGGER_NAME, LogLevel.TRACE));
	}

	private ServiceRegistration<String> registerLogLevel(LogLevel level) {
		String levelName = level == null ? "UNKNOWN" : level.name();
		return getContext().registerService(String.class, levelName,
				new Hashtable<>(
						Collections.singletonMap("level", levelName)));
	}

	protected void tearDown() throws Exception {
		auditRegistration.unregister();
		errorRegistration.unregister();
		warnRegistration.unregister();
		infoRegistration.unregister();
		debugRegistration.unregister();
		traceRegistration.unregister();
		unknownRegistration.unregister();

		rootContext.setLogLevels(rootLogLevels);
		bsnContext.setLogLevels(bsnLogLevels);
		bsnVersionContext.setLogLevels(bsnVersionLogLevels);
		bsnVersionLocationContext.setLogLevels(bsnVersionLocationLogLevels);

		for (Configuration testConfig : testConfigurations) {
			try {
				testConfig.delete();
			} catch (Exception e) {
				// Just trying to clean up
			}
		}

		getContext().ungetService(logServiceReference);
		getContext().ungetService(logReaderServiceReference);
		getContext().ungetService(loggerAdminReference);
		getContext().ungetService(configAdminReference);

		if (tb1 != null) {
			try {
				tb1.uninstall();
			} catch (BundleException e) {
				// do nothing
			} catch (IllegalStateException e) {
				// testcase must have uninstsalled it
			}
		}
	}

	void loggerThenAssertLog(boolean expectEntry, Bundle b, Logger logger,
			Collection<LogReader> listeners, LogLevel level, String format,
			String message, Throwable t, ServiceReference< ? > s,
			Object... args) {
		List<Object> argList = new ArrayList<Object>();
		if (args != null) {
			argList.addAll(Arrays.asList(args));
		}
		if (t != null) {
			argList.add(t);
		}
		if (s != null) {
			argList.add(s);
		}

		String threadName = Thread.currentThread().getName();
		long time = System.currentTimeMillis();
		StackTraceElement expectedLocation = logToLogger(logger, level, format,
				argList);

		assertLog(expectEntry, listeners, logger.getName(), b, level.ordinal(),
				message, t, s, time, threadName, expectedLocation);
	}

	private StackTraceElement logToLogger(Logger logger, LogLevel level,
			String message,
			List<Object> args) {
		Object arg1 = args.size() <= 2 && args.size() > 0 ? args.get(0) : null;
		Object arg2 = args.size() == 2 ? args.get(1) : null;
		switch (level) {
			case AUDIT :
				if (arg2 != null) {
					logger.audit(message, arg1, arg2);
				} else if (arg1 != null) {
					logger.audit(message, arg1);
				} else if (!args.isEmpty()) {
					logger.audit(message, args.toArray());
				} else {
					logger.audit(message);
				}
				break;
			case ERROR :
				if (arg2 != null) {
					logger.error(message, arg1, arg2);
				} else if (arg1 != null) {
					logger.error(message, arg1);
				} else if (!args.isEmpty()) {
					logger.error(message, args.toArray());
				} else {
					logger.error(message);
				}
				break;
			case WARN :
				if (arg2 != null) {
					logger.warn(message, arg1, arg2);
				} else if (arg1 != null) {
					logger.warn(message, arg1);
				} else if (!args.isEmpty()) {
					logger.warn(message, args.toArray());
				} else {
					logger.warn(message);
				}
				break;
			case INFO :
				if (arg2 != null) {
					logger.info(message, arg1, arg2);
				} else if (arg1 != null) {
					logger.info(message, arg1);
				} else if (!args.isEmpty()) {
					logger.info(message, args.toArray());
				} else {
					logger.info(message);
				}
				break;
			case DEBUG :
				if (arg2 != null) {
					logger.debug(message, arg1, arg2);
				} else if (arg1 != null) {
					logger.debug(message, arg1);
				} else if (!args.isEmpty()) {
					logger.debug(message, args.toArray());
				} else {
					logger.debug(message);
				}
				break;
			case TRACE :
				if (arg2 != null) {
					logger.trace(message, arg1, arg2);
				} else if (arg1 != null) {
					logger.trace(message, arg1);
				} else if (!args.isEmpty()) {
					logger.trace(message, args.toArray());
				} else {
					logger.trace(message);
				}
				break;
			default :
				fail("Unknown level: " + level);
		}
		StackTraceElement expectedLocation = null;
		for (StackTraceElement element : Thread.currentThread()
				.getStackTrace()) {
			if (AbstractLogTestCase.class.getName()
					.equals(element.getClassName())) {
				expectedLocation = element;
				break;
			}
		}
		assertNotNull("The JVM is messed up, no stack trace element to test.",
				expectedLocation);
		return expectedLocation;
	}

	@SuppressWarnings("deprecation")
	void logServiceThenAssertLog(boolean expectEntry,
			Collection<LogReader> listeners, int level,
			String message, Throwable t,
			ServiceReference< ? > s) {

		long time = System.currentTimeMillis();
		if (t == null && s == null) {
			logService.log(level, message);
		} else if (s == null){
			logService.log(level, message, t);
		} else if (t == null) {
			logService.log(s, level, message);
		} else {
			logService.log(s, level, message, t);
		}

		String threadName = Thread.currentThread().getName();
		String logName = LogService.class.getSimpleName() + "."
				+ getContext().getBundle().getSymbolicName();
		StackTraceElement expectedLocation = null;
		for (StackTraceElement element : Thread.currentThread()
				.getStackTrace()) {
			if (AbstractLogTestCase.class.getName()
					.equals(element.getClassName())) {
				expectedLocation = element;
				break;
			}
		}
		assertNotNull("The JVM is messed up, no stack trace element to test.",
				expectedLocation);

		assertLog(expectEntry, listeners, logName, getContext().getBundle(),
				level, message, t, s, time, threadName, expectedLocation);
	}

	@SuppressWarnings("deprecation")
	void assertLog(boolean expectEntry, Collection<LogReader> listeners,
			String logName, Bundle b,
			int level, String message, Throwable t, ServiceReference< ? > s,
			long time, String threadName, StackTraceElement expectedLocation) {
		if (!expectEntry) {
			forceEntryLog(listeners);
			return;
		}
		long previousSequence = logSequence.get();
		for (LogReader logReader : listeners) {
			LogEntry entry = logReader.getEntry(10000, message, level);
			assertEquals(b, entry.getBundle());
			assertEquals(s, entry.getServiceReference());
			assertEquals(t, entry.getException());
			assertEquals(level, entry.getLevel());
			assertEquals(message, entry.getMessage());
			assertTrue("Unexpected time: " + entry.getTime(),
					entry.getTime() >= time);
			assertTrue("Unexpected time: " + entry.getTime(),
					entry.getTime() <= System.currentTimeMillis());
			assertTrue("Unexpected sequence",
					previousSequence < entry.getSequence());
			assertEquals("Wrong logLevel", LogReader.getLogLevel(level),
					entry.getLogLevel());
			assertTrue("Wrong log name", logName.equals(entry.getLoggerName()));
			if (threadName != null) {
				assertTrue("Unexpected thread info: " + entry.getThreadInfo(),
						entry.getThreadInfo().indexOf(threadName) >= 0);
			}
			if (expectedLocation != null) {
				StackTraceElement actualLocation = entry.getLocation();
				assertEquals("Wrong location method: ",
						expectedLocation.getMethodName(),
						actualLocation.getMethodName());
				assertEquals("Wrong location method: ",
						expectedLocation.getClassName(),
						actualLocation.getClassName());
			}
			logSequence.set(entry.getSequence());
		}
	}

	private void forceEntryLog(Collection<LogReader> listeners) {
		loggerThenAssertLog(true, getContext().getBundle(),
				logService.getLogger(FORCE_LOGGER_NAME), listeners,
				LogLevel.AUDIT,
				forceMessage, forceMessage, null, null);
	}

	String getLogMessage(LogLevel level) {
		return getLogMessage(level.ordinal());
	}

	@SuppressWarnings("deprecation")
	String getLogMessage(int level) {
		switch (level) {
			case 0 :
				return auditMessage;
			case LogService.LOG_ERROR :
				return errorMessage;
			case LogService.LOG_WARNING :
				return warnMessage;
			case LogService.LOG_INFO :
				return infoMessage;
			case LogService.LOG_DEBUG :
				return debugMessage;
			case 5 :
				return traceMessage;
			default :
				return unknownMessage;
		}
	}

	Throwable getLogThrowable(LogLevel level) {
		return getLogThrowable(level.ordinal());
	}

	@SuppressWarnings("deprecation")
	Throwable getLogThrowable(int level) {
		switch (level) {
			case 0 :
				return auditThrowable;
			case LogService.LOG_ERROR :
				return errorThrowable;
			case LogService.LOG_WARNING :
				return warnThrowable;
			case LogService.LOG_INFO :
				return infoThrowable;
			case LogService.LOG_DEBUG :
				return debugThrowable;
			case 5 :
				return traceThrowable;
			default :
				return unknownThrowable;
		}
	}

	ServiceReference<String> getLogServiceReference(LogLevel level) {
		return getLogServiceReference(level.ordinal());
	}

	@SuppressWarnings("deprecation")
	ServiceReference<String> getLogServiceReference(int level) {
		switch (level) {
			case 0 :
				return auditRegistration.getReference();
			case LogService.LOG_ERROR :
				return errorRegistration.getReference();
			case LogService.LOG_WARNING :
				return warnRegistration.getReference();
			case LogService.LOG_INFO :
				return infoRegistration.getReference();
			case LogService.LOG_DEBUG :
				return debugRegistration.getReference();
			case 5 :
				return traceRegistration.getReference();
			default :
				return unknownRegistration.getReference();
		}
	}
}

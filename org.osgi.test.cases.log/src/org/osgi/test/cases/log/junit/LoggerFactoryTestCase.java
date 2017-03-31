/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.resolver.ResolverHook;
import org.osgi.framework.hooks.resolver.ResolverHookFactory;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.namespace.service.ServiceNamespace;
import org.osgi.resource.Namespace;
import org.osgi.service.cm.Configuration;
import org.osgi.service.log.FormatterLogger;
import org.osgi.service.log.LogLevel;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;
import org.osgi.service.log.admin.LoggerAdmin;
import org.osgi.service.log.admin.LoggerContext;

public class LoggerFactoryTestCase extends AbstractLogTestCase {

	static final String TEST_LOGGER_NAME = "test.logger.name";
	// TODO remove these when there is a spec'ed constants
	static final String	PID_PREFIX_LOG_ADMIN	= "org.osgi.service.log.admin";
	static final String	NULL_VALUE				= "NULL";

	public void testServiceReferenceIsLoggerFactory()
			throws InvalidSyntaxException {
		Filter loggerFactoryFilter = getContext().createFilter(
				"(objectClass=" + LoggerFactory.class.getName() + ")");
		assertTrue(
				"LogService registration is not also a LogFactory registration.",
				loggerFactoryFilter.match(logServiceReference));
	}

	public void testLogServiceImplProvidesOsgiServiceCapability() {
		doTestForCapability(logServiceReference, LogService.class,
				LoggerFactory.class);
	}

	public void testLogReaderServiceImplProvidesOsgiServiceCapability() {
		doTestForCapability(logReaderServiceReference, LogReaderService.class);
	}

	public void testLoggerAdminImplProvidesOsgiServiceCapability() {
		doTestForCapability(loggerAdminReference, LoggerAdmin.class);
	}

	static private void doTestForCapability(ServiceReference< ? > reference,
			Class< ? >... serviceClasses) {
		Set<String> packageSet = new HashSet<>();
		for (Class< ? > clazz : serviceClasses) {
			packageSet.add(clazz.getPackage().getName());
		}
		BundleRevision serviceRevision = reference.getBundle()
				.adapt(BundleRevision.class);
		List<BundleCapability> serviceCaps = serviceRevision
				.getDeclaredCapabilities(ServiceNamespace.SERVICE_NAMESPACE);
		assertFalse("No service capabilities found.", serviceCaps.isEmpty());
		// search for one that provides the service classes
		for (BundleCapability serviceCap : serviceCaps) {
			@SuppressWarnings("unchecked")
			List<String> objectClasses = (List<String>) serviceCap
					.getAttributes()
					.get(ServiceNamespace.CAPABILITY_OBJECTCLASS_ATTRIBUTE);
			if (containsAllServiceClasses(objectClasses, serviceClasses)) {
				String usesDirective = serviceCap.getDirectives()
						.get(Namespace.CAPABILITY_USES_DIRECTIVE);
				assertNotNull("No uses directive for: " + serviceCap,
						usesDirective);
				Set<String> usesSet = new HashSet<>();
				for (String uses : usesDirective.split(",")) {
					usesSet.add(uses.trim());
				}
				if (!usesSet.containsAll(packageSet)) {
					fail("Capability does not contain the expected uses: "
							+ packageSet + " -> " + usesSet);
				}
				return; // done with the test
			}
		}
		fail("ServiceCapability not found for service Classes: "
				+ Arrays.toString(serviceClasses) + " in service capabilities: "
				+ serviceCaps);
	}

	private static boolean containsAllServiceClasses(List<String> objectClasses,
			Class< ? >... serviceClasses) {
		for (Class< ? > clazz : serviceClasses) {
			if (!objectClasses.contains(clazz.getName())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Tests both types of loggers with no formatted messages at all LogLevel
	 * types with all combinations of log entry arguments. All log levels must
	 * be effective.
	 */
	public void testLogger() {
		Collection<LogReader> readers = Collections.singletonList(reader);
		Bundle b = getContext().getBundle();

		Logger logger = logService.getLogger(TEST_LOGGER_NAME);
		assertEquals("Wrong logger name.", TEST_LOGGER_NAME, logger.getName());
		doLoggerLogging(LogLevel.TRACE, b, logger, readers);

		logger = logService.getLogger(b, TEST_LOGGER_NAME, Logger.class);
		assertEquals("Wrong logger name.", TEST_LOGGER_NAME, logger.getName());
		doLoggerLogging(LogLevel.TRACE, b, logger, readers);

		logger = logService.getLogger(TEST_LOGGER_NAME, FormatterLogger.class);
		assertEquals("Wrong logger name.", TEST_LOGGER_NAME, logger.getName());
		doLoggerLogging(LogLevel.TRACE, b, logger, readers);
	}

	public void testLoggerForBundle() throws BundleException, IOException {
		Collection<LogReader> readers = Collections.singletonList(reader);
		// prevent all resolution
		ServiceRegistration<ResolverHookFactory> preventResolution = getContext()
				.registerService(ResolverHookFactory.class,
						new ResolverHookFactory() {

							@Override
							public ResolverHook begin(
									Collection<BundleRevision> triggers) {
								return new ResolverHook() {

									@Override
									public void filterResolvable(
											Collection<BundleRevision> candidates) {
										// prevent all resolution
										candidates.clear();
									}

									@Override
									public void filterSingletonCollisions(
											BundleCapability singleton,
											Collection<BundleCapability> collisionCandidates) {
										// do nothing
									}

									@Override
									public void filterMatches(
											BundleRequirement requirement,
											Collection<BundleCapability> candidates) {
										// do nothing
									}

									@Override
									public void end() {
										// do nothing
									}

								};
							}
						}, null);
		try {
			tb1 = install("tb1.jar");
			try {
				logService.getLogger(tb1, TEST_LOGGER_NAME, Logger.class);
				fail("Expecting an IllegalStateException for unresolved bundle");
			} catch (IllegalArgumentException e) {
				// expected
			}

			// allow resolution
			preventResolution.unregister();
			preventResolution = null;

			tb1.start();

			Logger logger = logService.getLogger(tb1, TEST_LOGGER_NAME,
				Logger.class);
			assertEquals("Wrong logger name.", TEST_LOGGER_NAME,
					logger.getName());
			doLoggerLogging(LogLevel.TRACE, tb1, logger, readers);

			logger = logService.getLogger(tb1, TEST_LOGGER_NAME,
					FormatterLogger.class);
			assertEquals("Wrong logger name.", TEST_LOGGER_NAME,
					logger.getName());
			doLoggerLogging(LogLevel.TRACE, tb1, logger, readers);

			tb1.uninstall();
			try {
				logService.getLogger(tb1, TEST_LOGGER_NAME, Logger.class);
				fail("Expecting an IllegalStateException for uninstalled bundle");
			} catch (IllegalArgumentException e) {
				// expected
			}

			// expect existing logger to keep working
			doLoggerLogging(LogLevel.TRACE, tb1, logger, readers);
		} finally {
			if (preventResolution != null) {
				preventResolution.unregister();
			}
		}
	}


	private void doLoggerLogging(LogLevel effectiveLevel, Bundle b,
			Logger logger, Collection<LogReader> readers) {
		for (LogLevel level : LogLevel.values()) {
			String message = getLogMessage(level);
			loggerThenAssertLog(effectiveLevel.implies(level), b, logger,
					readers, level, message, message, null, null);
			loggerThenAssertLog(effectiveLevel.implies(level), b, logger,
					readers, level, message, message, getLogThrowable(level),
					null);
			loggerThenAssertLog(effectiveLevel.implies(level), b, logger,
					readers, level, message, message, null,
					getLogServiceReference(level));
			loggerThenAssertLog(effectiveLevel.implies(level), b, logger,
					readers, level, message, message, getLogThrowable(level),
					getLogServiceReference(level));
		}
	}

	/**
	 * Tests the slf4j style formatting using the Logger type at all LogLevel
	 * types with all combinations of log entry arguments. All log levels must
	 * be effective.
	 */
	public void testLoggerSLF4JFormat() {
		Collection<LogReader> readers = Collections.singletonList(reader);
		Bundle b = getContext().getBundle();
		Logger logger = logService.getLogger(TEST_LOGGER_NAME);
		assertEquals("Wrong logger name.", TEST_LOGGER_NAME, logger.getName());
		doLoggerSlf4jFormat(LogLevel.TRACE, b, logger, readers);

		logger = logService.getLogger(getClass());
		assertEquals("Wrong logger name.", getClass().getName(),
				logger.getName());
		doLoggerSlf4jFormat(LogLevel.TRACE, b, logger, readers);

		logger = logService.getLogger(TEST_LOGGER_NAME, Logger.class);
		assertEquals("Wrong logger name.", TEST_LOGGER_NAME, logger.getName());
		doLoggerSlf4jFormat(LogLevel.TRACE, b, logger, readers);

		logger = logService.getLogger(getClass(), Logger.class);
		assertEquals("Wrong logger name.", getClass().getName(),
				logger.getName());
		doLoggerSlf4jFormat(LogLevel.TRACE, b, logger, readers);
	}

	public void testLoggerPrintfFormat() {
		Collection<LogReader> readers = Collections.singletonList(reader);
		Bundle b = getContext().getBundle();
		Logger logger = logService.getLogger(TEST_LOGGER_NAME,
				FormatterLogger.class);
		assertEquals("Wrong logger name.", TEST_LOGGER_NAME, logger.getName());
		doLoggerPrintfFormat(LogLevel.TRACE, b, logger, readers);

		logger = logService.getLogger(getClass(), FormatterLogger.class);
		assertEquals("Wrong logger name.", getClass().getName(),
				logger.getName());
		doLoggerPrintfFormat(LogLevel.TRACE, b, logger, readers);
	}

	public void testLogAdminRootContextRootNameEffectiveLogLevel() {
		Collection<LogReader> readers = Collections.singletonList(reader);
		Bundle b = getContext().getBundle();
		Logger logger = logService.getLogger(TEST_LOGGER_NAME);

		doTestRootContextRootNameEffectiveLogLevel(null, b, logger, readers);

		doTestRootContextRootNameEffectiveLogLevel(LogLevel.AUDIT, b, logger,
				readers);
		doTestRootContextRootNameEffectiveLogLevel(LogLevel.ERROR, b, logger,
				readers);
		doTestRootContextRootNameEffectiveLogLevel(LogLevel.WARN, b, logger,
				readers);
		doTestRootContextRootNameEffectiveLogLevel(LogLevel.INFO, b, logger,
				readers);
		doTestRootContextRootNameEffectiveLogLevel(LogLevel.DEBUG, b, logger,
				readers);
		doTestRootContextRootNameEffectiveLogLevel(LogLevel.TRACE, b, logger,
				readers);
	}

	private void doTestRootContextRootNameEffectiveLogLevel(LogLevel level, Bundle b,
			Logger logger, Collection<LogReader> readers) {
		if (level == null) {
			rootContext.setLogLevels(Collections.<String, LogLevel> emptyMap());
			// no log level should default to WARN
			level = LogLevel.WARN;
		} else {
			rootContext.setLogLevels(
					Collections.singletonMap(Logger.ROOT_LOGGER_NAME, level));
		}

		assertEquals("Wrong effective level.", level,
				rootContext.getEffectiveLogLevel(TEST_LOGGER_NAME));
		assertEquals("Wrong effective level.", level,
				rootContext.getEffectiveLogLevel(getClass().getName()));
		assertEquals("Wrong effective level.", level,
				bsnContext.getEffectiveLogLevel(TEST_LOGGER_NAME));
		assertEquals("Wrong effective level.", level,
				bsnContext.getEffectiveLogLevel(getClass().getName()));
		assertEquals("Wrong effective level.", level,
				bsnVersionContext.getEffectiveLogLevel(TEST_LOGGER_NAME));
		assertEquals("Wrong effective level.", level,
				bsnVersionContext.getEffectiveLogLevel(getClass().getName()));
		assertEquals("Wrong effective level.", level, bsnVersionLocationContext
				.getEffectiveLogLevel(TEST_LOGGER_NAME));
		assertEquals("Wrong effective level.", level, bsnVersionLocationContext
				.getEffectiveLogLevel(getClass().getName()));

		switch (level) {
			case TRACE :
				assertTrue("Trace should be enabled.", logger.isTraceEnabled());
			case DEBUG :
				assertTrue("Debug should be enabled.", logger.isDebugEnabled());
			case INFO :
				assertTrue("Info should be enabled.", logger.isInfoEnabled());
			case WARN :
				assertTrue("Warn should be enabled.", logger.isWarnEnabled());
			case ERROR :
				assertTrue("Error should be enabled.", logger.isErrorEnabled());
			case AUDIT :
			default :
				break;
		}
		doLoggerLogging(level, b, logger, readers);
	}

	public void testLogAdminBsnContextRootNameEffectiveLogLevel() {
		Collection<LoggerContext> auditContexts = Arrays.asList(rootContext,
				bsnVersionContext, bsnVersionLocationContext);
		doTestAllSpecificContextRootNameEffectiveLogLevel(bsnContext,
				auditContexts);
	}

	public void testLogAdminBsnVersionContextRootNameEffectiveLogLevel() {
		Collection<LoggerContext> auditContexts = Arrays.asList(rootContext,
				bsnContext, bsnVersionLocationContext);
		doTestAllSpecificContextRootNameEffectiveLogLevel(bsnVersionContext,
				auditContexts);
	}

	public void testLogAdminBsnVersionLocationContextRootNameEffectiveLogLevel() {
		Collection<LoggerContext> auditContexts = Arrays.asList(rootContext,
				bsnContext, bsnVersionContext);
		doTestAllSpecificContextRootNameEffectiveLogLevel(
				bsnVersionLocationContext, auditContexts);
	}

	private void doTestAllSpecificContextRootNameEffectiveLogLevel(
			LoggerContext managedContext,
			Collection<LoggerContext> auditContexts) {
		// first make root context set to AUDIT
		rootContext.setLogLevels(Collections
				.singletonMap(Logger.ROOT_LOGGER_NAME, LogLevel.AUDIT));
		Collection<LogReader> readers = Collections.singletonList(reader);
		Bundle b = getContext().getBundle();
		Logger logger = logService.getLogger(TEST_LOGGER_NAME);

		doTestSpecificContextRootNameEffectiveLogLevel(null, b, logger, readers,
				managedContext, auditContexts);

		doTestSpecificContextRootNameEffectiveLogLevel(LogLevel.AUDIT, b,
				logger, readers, managedContext, auditContexts);
		doTestSpecificContextRootNameEffectiveLogLevel(LogLevel.ERROR, b,
				logger, readers, managedContext, auditContexts);
		doTestSpecificContextRootNameEffectiveLogLevel(LogLevel.WARN, b, logger,
				readers, managedContext, auditContexts);
		doTestSpecificContextRootNameEffectiveLogLevel(LogLevel.INFO, b, logger,
				readers, managedContext, auditContexts);
		doTestSpecificContextRootNameEffectiveLogLevel(LogLevel.DEBUG, b,
				logger, readers, managedContext, auditContexts);
		doTestSpecificContextRootNameEffectiveLogLevel(LogLevel.TRACE, b,
				logger, readers, managedContext, auditContexts);
	}

	private void doTestSpecificContextRootNameEffectiveLogLevel(LogLevel level,
			Bundle b, Logger logger, Collection<LogReader> readers,
			LoggerContext managedContext,
			Collection<LoggerContext> auditContexts) {
		if (level == null) {
			managedContext
					.setLogLevels(Collections.<String, LogLevel> emptyMap());
			// no log level should default to AUDIT since that is what the root
			// context is set to
			level = LogLevel.AUDIT;
		} else {
			managedContext.setLogLevels(
					Collections.singletonMap(Logger.ROOT_LOGGER_NAME, level));
		}

		// only the managedContext is effected by the change
		assertEquals("Wrong effective level.", level,
				managedContext.getEffectiveLogLevel(TEST_LOGGER_NAME));
		assertEquals("Wrong effective level.", level,
				managedContext.getEffectiveLogLevel(getClass().getName()));

		// the other contexts remain AUDIT
		for (LoggerContext auditContext : auditContexts) {
			assertEquals("Wrong effective level.", LogLevel.AUDIT,
					auditContext.getEffectiveLogLevel(TEST_LOGGER_NAME));
			assertEquals("Wrong effective level.", LogLevel.AUDIT,
					auditContext.getEffectiveLogLevel(getClass().getName()));
		}

		switch (level) {
			case TRACE :
				assertTrue("Trace should be enabled.", logger.isTraceEnabled());
			case DEBUG :
				assertTrue("Debug should be enabled.", logger.isDebugEnabled());
			case INFO :
				assertTrue("Info should be enabled.", logger.isInfoEnabled());
			case WARN :
				assertTrue("Warn should be enabled.", logger.isWarnEnabled());
			case ERROR :
				assertTrue("Error should be enabled.", logger.isErrorEnabled());
			case AUDIT :
			default :
				break;
		}
		doLoggerLogging(level, b, logger, readers);

	}

	private static final Map<String,LogLevel> testConfigLogLevels;
	static {
		Map<String,LogLevel> temp = new HashMap<>();
		temp.put("default", LogLevel.ERROR);
		temp.put("default.audit", LogLevel.AUDIT);
		temp.put("default.audit.null1.null2", null);
		temp.put("default.audit.null1", null);
		temp.put("default.audit.warnOverride", LogLevel.WARN);
		temp.put("default.audit.infoOverride", LogLevel.INFO);
		temp.put("default.audit.debugOverride", LogLevel.DEBUG);
		temp.put("default.audit.traceOverride", LogLevel.TRACE);
		testConfigLogLevels = Collections.unmodifiableMap(temp);
	}

	public void testRootLoggerConfig() throws IOException {
		doTestConfigLoggerContext(null);
	}

	public void testBsnLoggerConfig() throws IOException {
		doTestConfigLoggerContext(bsnContext.getName());
	}

	public void testBsnVersionLoggerConfig() throws IOException {
		doTestConfigLoggerContext(bsnVersionContext.getName());
	}

	public void testBsnVersionLocationLoggerConfig() throws IOException {
		doTestConfigLoggerContext(bsnVersionLocationContext.getName());
	}

	private void doTestConfigLoggerContext(String loggerContextName)
			throws IOException {
		String loggerContextPID = loggerContextName == null
				? PID_PREFIX_LOG_ADMIN
				: PID_PREFIX_LOG_ADMIN + '|' + loggerContextName;

		Map<String,LogLevel> logLevels = new HashMap<>(testConfigLogLevels);
		// First create and configure with config admin
		Configuration contextConfig = configAdmin
				.getConfiguration(loggerContextPID, null);
		testConfigurations.add(contextConfig);

		Dictionary<String,String> logLevelConfigs = getLogLevelConfigs(
				logLevels);
		contextConfig.update(logLevelConfigs);
		sleep(loggerContextConfigTimeout);
		LoggerContext loggerContext = loggerAdmin
				.getLoggerContext(loggerContextName);
		assertEquals(
				"Unexpected loglevels for context named: " + loggerContextName,
				logLevels, loggerContext.getLogLevels());

		// delete some arbitrary key in the config and update it
		logLevels.remove(logLevels.keySet().iterator().next());
		logLevelConfigs = getLogLevelConfigs(logLevels);
		contextConfig.update(logLevelConfigs);
		sleep(loggerContextConfigTimeout);
		assertEquals(
				"Unexpected loglevels for context named: " + loggerContextName,
				logLevels, loggerContext.getLogLevels());

		// Now change with LoggerAdmin API
		Map<String,LogLevel> changeWithAPI = loggerContext.getLogLevels();
		changeWithAPI.put("added.with.api", LogLevel.TRACE);
		loggerContext.setLogLevels(changeWithAPI);
		assertEquals(
				"Unexpected loglevels for context named: " + loggerContextName,
				changeWithAPI, loggerContext.getLogLevels());

		// Now delete with config admin which should make the log levels empty
		contextConfig.delete();
		sleep(loggerContextConfigTimeout);
		assertEquals(
				"Unexpected loglevels for context named: " + loggerContextName,
				Collections.emptyMap(), loggerContext.getLogLevels());
	}

	private Dictionary<String,String> getLogLevelConfigs(
			Map<String,LogLevel> logLevels) {
		Hashtable<String,String> result = new Hashtable<>();
		for (Map.Entry<String,LogLevel> entry : logLevels.entrySet()) {
			LogLevel logLevel = entry.getValue();
			String logLevelValue = logLevel == null ? NULL_VALUE
					: logLevel.toString();
			result.put(entry.getKey(), logLevelValue);
		}
		// purposefully place an value that does not map to a LogLevel enum
		result.put("something.invalid", "Not a LogLevel");
		return result;
	}

	public void testRootContextAncestorsEffectiveLogLevel() {
		Collection<LogReader> readers = Collections.singletonList(reader);
		Bundle b = getContext().getBundle();
		Logger logger = logService.getLogger(getClass());
		String ancestor = logger.getName();

		do {
			doTestRootContextAncestorsEffectiveLogLevel(ancestor,
					LogLevel.AUDIT, b, logger, readers);
			doTestRootContextAncestorsEffectiveLogLevel(ancestor,
					LogLevel.ERROR, b, logger, readers);
			doTestRootContextAncestorsEffectiveLogLevel(ancestor, LogLevel.WARN,
					b, logger, readers);
			doTestRootContextAncestorsEffectiveLogLevel(ancestor, LogLevel.INFO,
					b, logger, readers);
			doTestRootContextAncestorsEffectiveLogLevel(ancestor,
					LogLevel.DEBUG, b, logger, readers);
			doTestRootContextAncestorsEffectiveLogLevel(ancestor,
					LogLevel.TRACE, b, logger, readers);
			int lastDot = ancestor.lastIndexOf('.');
			if (lastDot >= 0) {
				ancestor = ancestor.substring(0, lastDot);
			} else {
				ancestor = "";
			}
		} while (!ancestor.isEmpty());
	}

	private void doTestRootContextAncestorsEffectiveLogLevel(String ancestor,
			LogLevel level, Bundle b, Logger logger,
			Collection<LogReader> readers) {
		rootContext.setLogLevels(Collections.singletonMap(ancestor, level));

		assertEquals("Wrong effective level.", level,
				rootContext.getEffectiveLogLevel(logger.getName()));
		assertEquals("Wrong effective level.", level,
				bsnContext.getEffectiveLogLevel(logger.getName()));
		assertEquals("Wrong effective level.", level,
				bsnVersionContext.getEffectiveLogLevel(logger.getName()));
		assertEquals("Wrong effective level.", level, bsnVersionLocationContext
				.getEffectiveLogLevel(logger.getName()));

		switch (level) {
			case TRACE :
				assertTrue("Trace should be enabled.", logger.isTraceEnabled());
			case DEBUG :
				assertTrue("Debug should be enabled.", logger.isDebugEnabled());
			case INFO :
				assertTrue("Info should be enabled.", logger.isInfoEnabled());
			case WARN :
				assertTrue("Warn should be enabled.", logger.isWarnEnabled());
			case ERROR :
				assertTrue("Error should be enabled.", logger.isErrorEnabled());
			case AUDIT :
			default :
				break;
		}
		doLoggerLogging(level, b, logger, readers);
	}

	static class FormatArguments {
		final String	format;
		final Object[]	arguments;

		FormatArguments(String format, Object... arguments) {
			this.format = format;
			this.arguments = arguments;
		}
	}

	static final Map<String,FormatArguments> slf4jMessageToFormats = new HashMap<>();
	static {
		slf4jMessageToFormats.put("A single TEST.VALUE1 substitution.",
				new FormatArguments("A single {} substitution.",
						"TEST.VALUE1"));
		slf4jMessageToFormats.put(
				"First substitution TEST.VALUE1, second substitution TEST.VALUE2.",
				new FormatArguments(
						"First substitution {}, second substitution {}.",
						"TEST.VALUE1", "TEST.VALUE2"));
		slf4jMessageToFormats.put(
				"A message with a literal {} value, and a substitution TEST.VALUE1.",
				new FormatArguments(
						"A message with a literal \\{} value, and a substitution {}.",
						"TEST.VALUE1"));
		slf4jMessageToFormats.put(
				"First substitution with slash \\TEST.VALUE1, second substitution TEST.VALUE2.",
				new FormatArguments(
						"First substitution with slash \\\\{}, second substitution {}.",
						"TEST.VALUE1", "TEST.VALUE2"));
		slf4jMessageToFormats.put(
				"First substitution TEST.VALUE1, second substitution TEST.VALUE2, unsubstituted literal {}.",
				new FormatArguments(
						"First substitution {}, second substitution {}, unsubstituted literal {}.",
						"TEST.VALUE1", "TEST.VALUE2"));
		slf4jMessageToFormats.put(
				"First substitution with slash \\TEST.VALUE1, second substitution TEST.VALUE2, unsubstituted literal \\{}.",
				new FormatArguments(
						"First substitution with slash \\\\{}, second substitution {}, unsubstituted literal \\{}.",
						"TEST.VALUE1", "TEST.VALUE2"));
	}

	private void doLoggerSlf4jFormat(LogLevel effectiveLevel, Bundle b,
			Logger logger, Collection<LogReader> readers) {
		for (LogLevel level : LogLevel.values()) {
			String baseMessage = getLogMessage(level);
			for (Map.Entry<String,FormatArguments> messageToFormat : slf4jMessageToFormats
					.entrySet()) {
				String format = baseMessage + ": "
						+ messageToFormat.getValue().format;
				String message = baseMessage + ": " + messageToFormat.getKey();
				Throwable t = getLogThrowable(level);
				ServiceReference< ? > s = getLogServiceReference(level);
				Object[] args = messageToFormat.getValue().arguments;
				loggerThenAssertLog(effectiveLevel.implies(level), b, logger,
						readers, level, format, message, null, null, args);
				loggerThenAssertLog(effectiveLevel.implies(level), b, logger,
						readers, level, format, message, t, null, args);
				loggerThenAssertLog(effectiveLevel.implies(level), b, logger,
						readers, level, format, message, null, s, args);
				loggerThenAssertLog(effectiveLevel.implies(level), b, logger,
						readers, level, format, message, t, s, args);
			}
		}
	}

	static final Map<String,FormatArguments> printfMessageToFormats = new HashMap<>();
	static {
		printfMessageToFormats.put("A single TEST.VALUE1 substitution.",
				new FormatArguments("A single %s substitution.",
						"TEST.VALUE1"));
		printfMessageToFormats.put(
				"First substitution TEST.VALUE1, second substitution TEST.VALUE2.",
				new FormatArguments(
						"First substitution %s, second substitution %s.",
						"TEST.VALUE1", "TEST.VALUE2"));
	}

	private void doLoggerPrintfFormat(LogLevel effectiveLevel, Bundle b,
			Logger logger, Collection<LogReader> readers) {
		for (LogLevel level : LogLevel.values()) {
			String baseMessage = getLogMessage(level);
			for (Map.Entry<String,FormatArguments> messageToFormat : printfMessageToFormats
					.entrySet()) {
				String format = baseMessage + ": "
						+ messageToFormat.getValue().format;
				String message = baseMessage + ": " + messageToFormat.getKey();
				Throwable t = getLogThrowable(level);
				ServiceReference< ? > s = getLogServiceReference(level);
				Object[] args = messageToFormat.getValue().arguments;
				loggerThenAssertLog(effectiveLevel.implies(level), b, logger,
						readers, level, format, message, null, null, args);
				loggerThenAssertLog(effectiveLevel.implies(level), b, logger,
						readers, level, format, message, t, null, args);
				loggerThenAssertLog(effectiveLevel.implies(level), b, logger,
						readers, level, format, message, null, s, args);
				loggerThenAssertLog(effectiveLevel.implies(level), b, logger,
						readers, level, format, message, t, s, args);
			}
		}
	}
}

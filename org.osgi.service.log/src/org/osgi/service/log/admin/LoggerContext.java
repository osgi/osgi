/*
 * Copyright (c) OSGi Alliance (2016, 2017). All Rights Reserved.
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

package org.osgi.service.log.admin;

import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.log.LogLevel;

/**
 * Logger Context for a bundle.
 * <p>
 * Any change to the configuration of this Logger Context must be effective
 * immediately for all loggers that would rely upon the configuration of this
 * Logger Context.
 * 
 * @ThreadSafe
 * @author $Id$
 */
@ProviderType
public interface LoggerContext {
	/**
	 * Logger Context PID.
	 * <p>
	 * If Configuration Admin is present, Logger Context configuration
	 * information in Configuration Admin must be used. The name of the Logger
	 * Context is mapped to a Configuration Admin targeted PID as follows:
	 * <ul>
	 * <li>The root Logger Context, which has no name, is mapped to the PID
	 * {@code org.osgi.service.log.admin}.</li>
	 * <li>A named Logger Context is mapped to a targeted PID by prefixing the
	 * Logger Context's name with {@code org.osgi.service.log.admin} followed by
	 * vertical line ({@code '|'} &#92;u007c). For example, the Logger Context
	 * named {@code com.foo.bar} is mapped to the targeted PID
	 * {@code org.osgi.service.log.admin|com.foo.bar}.</li>
	 * </ul>
	 */
	String LOGGER_CONTEXT_PID = "org.osgi.service.log.admin";

	/**
	 * Framework launching property specifying the default log level of the root
	 * Logger Context.
	 * <p>
	 * The value of this property must be the name of the one of the
	 * {@link LogLevel}s.
	 * <p>
	 * If not specified, or the specified value is not the name of the one of
	 * the {@link LogLevel}s, the default log level of the root Logger Context
	 * is {@link LogLevel#WARN}.
	 * 
	 * @see LogLevel
	 */
	String	LOGGER_CONTEXT_DEFAULT_LOGLEVEL	= "org.osgi.service.log.admin.loglevel";

	/**
	 * Returns the name for this Logger Context.
	 * 
	 * @return The name for this Logger Context. The root Logger Context has no
	 *         name and returns {@code null}.
	 */
	String getName();

	/**
	 * Returns the effective log level of the logger name in this Logger
	 * Context.
	 * <p>
	 * The effective log level for a logger name is found by the following
	 * steps:
	 * <ol>
	 * <li>If the specified logger name is configured with a log level, return
	 * the configured log level.</li>
	 * <li>For each ancestor logger name of the specified logger name, if the
	 * ancestor logger name is configured with a log level, return the
	 * configured log level.</li>
	 * <li>If this Logger Context is named, return the result of calling this
	 * method on the root Logger Context with the specified logger name.</li>
	 * <li>If this Logger Context is the root Logger Context, return the
	 * {@link #LOGGER_CONTEXT_DEFAULT_LOGLEVEL default log level of the root
	 * Logger Context}.</li>
	 * </ol>
	 * 
	 * @param name The logger name.
	 * @return The effective log level of the logger name in this Logger
	 *         Context.
	 */
	LogLevel getEffectiveLogLevel(String name);

	/**
	 * Returns the configured log levels for this Logger Context.
	 * 
	 * @return The configured log levels for this Logger Context. The keys are
	 *         the logger names and the values are the log levels. The returned
	 *         map may be empty if no logger names are configured for this
	 *         Logger Context. The returned map is the property of the caller
	 *         who can modify the map and use it as input to
	 *         {@link #setLogLevels(Map)}. The returned map must support all
	 *         optional Map operations.
	 */
	Map<String,LogLevel> getLogLevels();

	/**
	 * Configure the log levels for this Logger Context.
	 * <p>
	 * All previous log levels configured for this Logger Context are cleared
	 * and then the log levels in the specified map are configured.
	 * <p>
	 * The configured log levels for this Logger Context can be set by both this
	 * method and by configuration information in Configuration Admin, if
	 * Configuration Admin is present. The configured log levels for this Logger
	 * Context are based upon the last technique used to update the configured
	 * log levels. This method must not modify or set configuration information
	 * in Configuration Admin.
	 * 
	 * @param logLevels The log levels to configure for this Logger Context. The
	 *            keys are the logger names and the values are the log levels.
	 *            The specified map is the property of the caller and this
	 *            method must not modify or retain the specified map.
	 */
	void setLogLevels(Map<String,LogLevel> logLevels);

	/**
	 * Clear the configuration of this Logger Context.
	 * <p>
	 * The configured log levels will be cleared.
	 */
	void clear();

	/**
	 * Returns whether the configuration of this Logger Context is empty.
	 * 
	 * @return {@code true} if this Logger Context has no configuration. That
	 *         is, the configured log levels are empty. Otherwise {@code false}
	 *         is returned.
	 */
	boolean isEmpty();
}

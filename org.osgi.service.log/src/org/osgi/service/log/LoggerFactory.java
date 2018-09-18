/*
 * Copyright (c) OSGi Alliance (2016, 2018). All Rights Reserved.
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

package org.osgi.service.log;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.Bundle;

/**
 * Logger Factory service for logging information.
 * <p>
 * Provides methods for bundles to obtain named {@link Logger}s that can be used
 * to write messages to the log.
 * <p>
 * Logger names should be in the form of a fully qualified Java class names with
 * segments separated by full stop ({@code '.'} &#92;u002E). For example:
 * 
 * <pre>
 * com.foo.Bar
 * </pre>
 * 
 * Logger names exist in a hierarchy. A logger name is said to be an ancestor of
 * another logger name if the logger name followed by a full stop ({@code '.'}
 * &#92;u002E) is a prefix of the descendant logger name. The
 * {@link Logger#ROOT_LOGGER_NAME root logger name} is the top ancestor of the
 * logger name hierarchy. For example:
 * 
 * <pre>
 * com.foo.Bar
 * com.foo
 * com
 * ROOT
 * </pre>
 * 
 * @ThreadSafe
 * @since 1.4
 * @author $Id$
 */
@ProviderType
public interface LoggerFactory {

	/**
	 * Return the {@link Logger} named with the specified name.
	 * 
	 * @param name The name to use for the logger name. Must not be
	 *            {@code null}.
	 * @return The {@link Logger} named with the specified name. If the name
	 *         parameter is equal to {@link Logger#ROOT_LOGGER_NAME}, then the
	 *         root logger is returned.
	 */
	Logger getLogger(String name);

	/**
	 * Return the {@link Logger} named with the specified class.
	 * 
	 * @param clazz The class to use for the logger name. Must not be
	 *            {@code null}.
	 * @return The {@link Logger} named with the name of the specified class.
	 */
	Logger getLogger(Class< ? > clazz);

	/**
	 * Return the {@link Logger} of the specified type named with the specified
	 * name.
	 * 
	 * @param <L> The Logger type.
	 * @param name The name to use for the logger name. Must not be
	 *            {@code null}.
	 * @param loggerType The type of Logger. Can be {@link Logger} or
	 *            {@link FormatterLogger}.
	 * @return The {@link Logger} or {@link FormatterLogger} named with the
	 *         specified name. If the name parameter is equal to
	 *         {@link Logger#ROOT_LOGGER_NAME}, then the root logger is
	 *         returned.
	 * @throws IllegalArgumentException If the specified type is not a supported
	 *             Logger type.
	 */
	<L extends Logger> L getLogger(String name, Class<L> loggerType);

	/**
	 * Return the {@link Logger} of the specified type named with the specified
	 * class.
	 * 
	 * @param <L> A Logger type.
	 * @param clazz The class to use for the logger name. Must not be
	 *            {@code null}.
	 * @param loggerType The type of Logger. Can be {@link Logger} or
	 *            {@link FormatterLogger}. Must not be {@code null}.
	 * @return The {@link Logger} or {@link FormatterLogger} named with the name
	 *         of the specified class.
	 * @throws IllegalArgumentException If the specified type is not a supported
	 *             Logger type.
	 */
	<L extends Logger> L getLogger(Class< ? > clazz, Class<L> loggerType);

	/**
	 * Return the {@link Logger} of the specified type named with the specified
	 * name for the specified bundle.
	 * <p>
	 * This method is not normally used. The other {@code getLogger} methods
	 * return a {@link Logger} associated with the bundle used to obtain this
	 * Logger Factory service. This method is used to obtain a {@link Logger}
	 * for the specified bundle which may be useful to code which is logging on
	 * behalf of another bundle.
	 * 
	 * @param <L> The Logger type.
	 * @param bundle The bundle associated with the Logger. Must not be
	 *            {@code null}.
	 * @param name The name to use for the logger name. Must not be
	 *            {@code null}.
	 * @param loggerType The type of Logger. Can be {@link Logger} or
	 *            {@link FormatterLogger}. Must not be {@code null}.
	 * @return The {@link Logger} or {@link FormatterLogger} named with the
	 *         specified name for the specified bundle. If the name parameter is
	 *         equal to {@link Logger#ROOT_LOGGER_NAME}, then the root logger is
	 *         returned.
	 * @throws IllegalArgumentException If the specified type is not a supported
	 *             Logger type or the specified Bundle is not a resolved bundle.
	 */
	<L extends Logger> L getLogger(Bundle bundle, String name,
			Class<L> loggerType);
}

/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
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

/**
 * Logger Factory service for logging information.
 * <p>
 * Provides methods for bundles to obtain named {@link Logger}s that can be used
 * to write messages to the log.
 * 
 * @ThreadSafe
 * @since 1.4
 * @author $Id$
 */
@ProviderType
public interface LoggerFactory {

	/**
	 * Return a {@link Logger} named with the specified name.
	 * 
	 * @param name The name to use for the logger name.
	 * @return A {@link Logger} named with the specified name.
	 */
	Logger getLogger(String name);

	/**
	 * Return a {@link Logger} named with the specified class.
	 * 
	 * @param clazz The class to use for the logger name.
	 * @return A {@link Logger} named with the specified class.
	 */
	Logger getLogger(Class< ? > clazz);

	/**
	 * Return a {@link Logger} of the specified type named with the specified
	 * name.
	 * 
	 * @param <L> A Logger type.
	 * @param name The name to use for the logger name.
	 * @param loggerType The type of Logger. Can be {@link Logger} or
	 *            {@link FormatterLogger}.
	 * @return A {@link Logger} named with the specified name.
	 * @throws IllegalArgumentException If the specified type is not a supported
	 *             Logger type.
	 */
	<L extends Logger> L getLogger(String name, Class<L> loggerType);

	/**
	 * Return a {@link Logger} of the specified type named with the specified
	 * class.
	 * 
	 * @param <L> A Logger type.
	 * @param clazz The class to use for the logger name.
	 * @param loggerType The type of Logger. Can be {@link Logger} or
	 *            {@link FormatterLogger}.
	 * @return A {@link Logger} named with the specified class.
	 * @throws IllegalArgumentException If the specified type is not a supported
	 *             Logger type.
	 */
	<L extends Logger> L getLogger(Class< ? > clazz, Class<L> loggerType);
}

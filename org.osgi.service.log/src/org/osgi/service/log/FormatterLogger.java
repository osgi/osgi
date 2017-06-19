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

package org.osgi.service.log;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides methods for bundles to write messages to the log using printf-style
 * format strings.
 * <p>
 * Messages can be formatted by the Logger once the Logger determines the log
 * level is enabled. Uses printf-style format strings as described in
 * {@link java.util.Formatter}.
 * <p>
 * You can also add a {@code Throwable} and/or {@code ServiceReference} to the
 * generated {@link LogEntry} by passing them to the logging methods as
 * additional arguments. If the last argument is a {@code Throwable} or
 * {@code ServiceReference}, it is added to the generated {@link LogEntry} and
 * then if the next to last argument is a {@code ServiceReference} or
 * {@code Throwable} and not the same type as the last argument, it is also
 * added to the generated {@link LogEntry}. These arguments will not be used as
 * message arguments. For example:
 * 
 * <pre>
 * logger.info("Found service %s.", serviceReference, serviceReference);
 * logger.warn("Something named %s happened.", name, serviceReference,
 * 		throwable);
 * logger.error("Failed.", exception);
 * </pre>
 * <p>
 * If an exception occurs formatting the message, the logged message will
 * indicate the formatting failure including the format string and the
 * arguments.
 * 
 * @ThreadSafe
 * @author $Id$
 * @since 1.4
 */
@ProviderType
public interface FormatterLogger extends Logger {
	// no additional methods
}

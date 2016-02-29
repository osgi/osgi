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

package org.osgi.service.log.admin;

import org.osgi.annotation.versioning.ProviderType;

/**
 * LoggerAdmin service for configuring loggers.
 * <p>
 * Each bundle may have its own named {@link LoggerContext} based upon its
 * bundle symbolic name, bundle version, and bundle location. There is also a
 * root Logger Context from which each named Logger Context inherits. The root
 * Logger Context has no name.
 * <p>
 * When a bundle logs, the logger implementation must locate the Logger Context
 * for the bundle to determine the
 * {@link LoggerContext#getEffectiveLogLevel(String) effective log level} of the
 * logger. The <i>best matching name</i> for the Logger Context is the longest
 * name, which has a non-empty Logger Context, according to this syntax:
 * 
 * <pre>
 * name ::= symbolic-name ( ’|’ version ( ’|’ location )? )?
 * </pre>
 * 
 * The version must be formatted canonically, that is, according to the
 * {@code toString()} method of the {@code Version} class. So the Logger Context
 * for a bundle is searched for using the following names in the given order:
 * 
 * <pre>
 * &lt;symbolic-name&gt;|&lt;version&gt;|&lt;location&gt;
 * &lt;symbolic-name&gt;|&lt;version&gt;
 * &lt;symbolic-name&gt;
 * </pre>
 * 
 * If a non-empty Logger Context is not found, the Logger Context with the name
 * {@code <symbolic-name>} is used for the bundle.
 * 
 * @ThreadSafe
 * @author $Id$
 */
@ProviderType
public interface LoggerAdmin {
	/**
	 * Get the Logger Context for the specified name.
	 * <p>
	 * Configuration changes to a Logger Context must be persisted by the Logger
	 * Admin service. However, an {@link LoggerContext#isEmpty() empty} Logger
	 * Context does not need to be persisted and can be removed from persistent
	 * storage.
	 * 
	 * @param name The name of the Logger Context. Can be {@code null} to
	 *            specify the root Logger Context.
	 * @return The Logger Context for the specified name.
	 */
	LoggerContext getLoggerContext(String name);

}

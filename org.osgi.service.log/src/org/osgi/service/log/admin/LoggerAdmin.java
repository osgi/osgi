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

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.log.LoggerFactory;

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
 * logger name. The <i>best matching name</i> for the Logger Context is the
 * longest name, which has a non-empty Logger Context, according to this syntax:
 * 
 * <pre>
 * name ::= symbolic-name ( '|' version ( '|' location )? )?
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
 * The search stops at the first {@link LoggerContext#isEmpty() non-empty}
 * Logger Context. If no non-empty Logger Context is found using the above
 * search order, the Logger Context with the symbolic name of the bundle must be
 * used for the bundle.
 * 
 * @ThreadSafe
 * @author $Id$
 */
@ProviderType
public interface LoggerAdmin {
	/**
	 * Logger Admin service property to associate the Logger Admin service with
	 * a {@link LoggerFactory} service.
	 * <p>
	 * This service property is set to the {@code service.id} for the
	 * {@link LoggerFactory} service administered by this Logger Admin.
	 * <p>
	 * The value of this service property must be of type {@code Long}.
	 */
	String LOG_SERVICE_ID = "osgi.log.service.id";

	/**
	 * Get the Logger Context for the specified name.
	 * 
	 * @param name The name of the Logger Context. Can be {@code null} to
	 *            specify the root Logger Context.
	 * @return The Logger Context for the specified name. The returned Logger
	 *         Context may be {@link LoggerContext#isEmpty() empty}.
	 */
	LoggerContext getLoggerContext(String name);
}

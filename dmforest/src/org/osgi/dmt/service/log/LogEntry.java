/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.dmt.service.log;

import static org.osgi.dmt.ddf.Scope.SCOPE.A;
import java.util.Date;
import org.osgi.dmt.ddf.Opt;
import org.osgi.dmt.ddf.Scope;

/**
 * A Log Entry node is the representation of a LogEntry from the OSGi Log
 * Service.
 * 
 */
public interface LogEntry {
	/**
	 * Time of the Log Entry.
	 * 
	 * @return The Time the log entry was created.
	 */
	@Scope(A)
	Date Time();

	/**
	 * The severity level of the log entry. The value is the same as the Log
	 * Service level values:
	 * <ul>
	 * <li>LOG_ERROR 1</li>
	 * <li>LOG_WARNING 2</li>
	 * <li>LOG_INFO 3</li>
	 * <li>LOG_DEBUG 4</li>
	 * </ul>
	 * <p>
	 * Other values are possible because the Log Service allows custom levels.
	 * 
	 * @return The log entry's severity.
	 */
	@Scope(A)
	Integer Level();

	/**
	 * Textual, human-readable description of the log entry.
	 * 
	 * @return Textual, human-readable description of the log entry.
	 */
	@Scope(A)
	String Message();

	/**
	 * The location of the bundle that originated this log or an empty string.
	 * 
	 * @return The location of the bundle that originated this log entry or an
	 *         empty string.
	 */
	@Scope(A)
	String Bundle();

	/**
	 * Human readable information about an exception.
	 * 
	 * Provides the exception information if any, optionally including the stack
	 * trace.
	 * 
	 * @return The value of the Exception node
	 */
	@Scope(A)
	Opt<String> Exception();
}

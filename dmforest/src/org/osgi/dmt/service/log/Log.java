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
import org.osgi.dmt.ddf.LIST;
import org.osgi.dmt.ddf.Scope;

/**
 * Provides access to the Log Entries of the Log Service.
 */

public interface Log {

	/**
	 * A potentially long list of Log Entries. The length of this list is
	 * implementation dependent. The order of the list is most recent event at
	 * index 0 and later events with higher consecutive indexes.
	 * 
	 * No new entries must be added to the log when there is an open exclusive
	 * or atomic session.
	 * 
	 * @return LIST of Log Entry nodes.
	 */
	@Scope(A)
	LIST<LogEntry> LogEntries();
}

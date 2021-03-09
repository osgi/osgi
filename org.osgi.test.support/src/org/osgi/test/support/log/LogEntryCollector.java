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


package org.osgi.test.support.log;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;

public class LogEntryCollector implements LogListener {
	private final List<LogEntry>	entries	= new ArrayList<LogEntry>();

	@Override
	public synchronized void logged(LogEntry entry) {
		entries.add(entry);
	}

	public synchronized void clear() {
		entries.clear();
	}

	public synchronized List<LogEntry> getEntries() {
		List<LogEntry> result = new ArrayList<LogEntry>(entries);
		clear();
		return result;
	}
}

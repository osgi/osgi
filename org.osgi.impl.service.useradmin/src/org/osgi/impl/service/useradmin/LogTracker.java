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
package org.osgi.impl.service.useradmin;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class LogTracker extends ServiceTracker<LogService,LogService>
		implements LogService {
	BundleContext	bc;

	public LogTracker(BundleContext bc) {
		super(bc, LogService.class, null);
		this.bc = bc;
		open();
	}

	public void error(String msg, Exception e) {
		log(LogService.LOG_ERROR, msg, e);
	}

	public void warning(String msg) {
		log(LogService.LOG_WARNING, msg, null);
	}

	public void info(String msg) {
		log(LogService.LOG_INFO, msg, null);
	}

	// Log implementation
	@Override
	public void log(int level, String msg, Throwable e) {
		log(null, level, msg, e);
	}

	@Override
	public void log(int level, String msg) {
		log(null, level, msg, null);
	}

	@Override
	public void log(@SuppressWarnings("rawtypes") ServiceReference sr,
			int level, String msg) {
		log(sr, level, msg, null);
	}

	@Override
	public void log(@SuppressWarnings("rawtypes") ServiceReference sr,
			int level, String msg, Throwable e) {
		LogService ls = getService();
		if (ls != null) {
			if (sr == null)
				ls.log(level, msg, e);
			else
				ls.log(sr, level, msg, e);
		}
		else {
			System.err.println("[" + bc.getBundle().getLocation() + ":"
					+ ((sr == null) ? "" : (sr + ":")) + level + "] " + msg
					+ ((e == null) ? "" : e.toString()));
		}
	}
}

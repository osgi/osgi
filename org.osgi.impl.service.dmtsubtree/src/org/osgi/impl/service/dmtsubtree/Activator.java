/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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

package org.osgi.impl.service.dmtsubtree;

import info.dmtree.DmtAdmin;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

	public ServiceTracker	dmtTracker;
	public ServiceTracker	cfgTracker;
	public ServiceTracker	eaTracker;
	public BundleContext	context;

	private ServiceTracker	logTracker;
	private DmtSubtreeAdmin			dmtSubtreeAdmin;

	public void start(BundleContext context) throws Exception {
		this.context = context;
		logTracker = new ServiceTracker(context, LogService.class.getName(),
				null);
		logTracker.open();

		dmtTracker = new ServiceTracker(context, DmtAdmin.class.getName(), null);
		dmtTracker.open();
		cfgTracker = new ServiceTracker(context, ConfigurationAdmin.class
				.getName(), null);
		cfgTracker.open();
		
		eaTracker = new ServiceTracker( context, EventAdmin.class.getName(), null );
		eaTracker.open();

		dmtSubtreeAdmin = new DmtSubtreeAdmin(this);
	}

	public void stop(BundleContext context) throws Exception {
		if (dmtSubtreeAdmin != null)
			dmtSubtreeAdmin.cleanup();

		if (dmtTracker != null)
			dmtTracker.close();

		if (cfgTracker != null) {
			cfgTracker.close();
		}
		
		if (eaTracker != null )
			eaTracker.close();

		if (logTracker != null)
			logTracker.close();
	}

	private void log(String msg, int level, Exception x) {
		LogService log = (LogService) logTracker.getService();
		if (log != null) {
			log.log(level, msg, x);
		}
	}

	/**
	 * log a debug message
	 * 
	 * @param msg
	 */
	public void logDebug(String msg) {
		log(msg, LogService.LOG_DEBUG, null);
	}

	/**
	 * log an info message
	 * 
	 * @param msg
	 */
	public void logInfo(String msg) {
		log(msg, LogService.LOG_INFO, null);
	}

	/**
	 * log a warning message
	 * 
	 * @param msg
	 */
	public void logWarning(String msg) {
		log(msg, LogService.LOG_WARNING, null);
	}

	/**
	 * log an Error message
	 * 
	 * @param msg
	 * @param x
	 */
	public void logError(String msg, Exception x) {
		log(msg, LogService.LOG_ERROR, x);
	}

}

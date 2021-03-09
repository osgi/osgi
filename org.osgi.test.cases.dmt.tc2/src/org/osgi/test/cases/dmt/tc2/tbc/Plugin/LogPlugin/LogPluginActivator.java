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
package org.osgi.test.cases.dmt.tc2.tbc.Plugin.LogPlugin;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.log.LogReaderService;
import org.osgi.util.tracker.ServiceTracker;

public class LogPluginActivator implements BundleActivator {
	static final String[] PLUGIN_ROOT_PATH = 
        new String[] { ".", "OSGi", "Log" };
    static final String PLUGIN_ROOT_URI = "./OSGi/Log";

	private ServiceRegistration<DataPlugin>						servReg;
	private ServiceTracker<LogReaderService,LogReaderService>	logReaderTracker;
    private LogPlugin           logPlugin;

	@Override
	public void start(BundleContext bc) throws BundleException {
        System.out.println("Log plugin activated.");
		// setting up the needed trackers
        logReaderTracker = 
				new ServiceTracker<>(bc, LogReaderService.class, null);
        logReaderTracker.open();

		// creating the service
		logPlugin = new LogPlugin(bc, logReaderTracker);
		Dictionary<String,Object> props = new Hashtable<>();
		props.put("dataRootURIs", new String[] { PLUGIN_ROOT_URI });
		servReg = bc.registerService(DataPlugin.class, logPlugin, props);
	}

	@Override
	public void stop(BundleContext bc) throws BundleException {
        // closing the used trackers
		logReaderTracker.close();
        
		// unregistering the service
		servReg.unregister();
	}
}
